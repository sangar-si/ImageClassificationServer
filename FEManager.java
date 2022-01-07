import java.net.ConnectException;
import java.util.*;
import java.util.concurrent.*;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.*;
import org.apache.thrift.transport.*;
import java.lang.reflect.Constructor;
import java.io.*;

public class FEManager implements ImageQueryService.Iface {
		class BEHost{
				public String beNode = "";
				public int bePort = 0;
				public TSocket sock;
				public TTransport transport;
				public TProtocol protocol;
				public BEHost(String beNode, int bePort){
						this.beNode = beNode;
						this.bePort = bePort;
					    try{
							TSocket sock = new TSocket(beNode, bePort);
							TTransport transport = new TFramedTransport(sock);
							TProtocol protocol = new TBinaryProtocol(transport);
							this.sock = sock;
							this.transport = transport;
							this.protocol = protocol;
						}catch (Exception e){
							this.sock = null;
							this.transport = null;
							this.protocol = null;
						}
				}
		}
		
		public static class WorkerThread implements Runnable{
			   private volatile List<Integer> result = new ArrayList<>();
			   final private String filenames;
			   BEHost selectedWorkerNode;
			   public WorkerThread(String filenames, BEHost selectedWorkerNode){
					  this.filenames = filenames;
					  this.selectedWorkerNode = selectedWorkerNode;
			   }
			   @Override
			   public void run(){
					  result =  queryInBE(filenames, selectedWorkerNode);
			   }
			   public List<Integer> getValue(){
					   return result;
			   }
		}



		public static int incFacCalc(int size, int splitFac){
	    	int incFac = size/splitFac;
	    	if (size%splitFac > 0){
	       		incFac++;
	    	}
	    	return incFac;
		}
	    public static List<String> evenSplitFilenames(String filenames, int splitFactor){
        	int startIdx = 0, endIdx = 0;
        	List<String> strList = Arrays.asList(filenames.split(","));
        	List<String> result = new ArrayList<>();
        	List<String> subResult = new ArrayList<>();
        	int incFactor = incFacCalc(strList.size(),splitFactor);
        	endIdx = incFactor;
       		while(startIdx < strList.size()){
            	subResult = strList.subList(startIdx, endIdx);
           	 	result.add(String.join(",",subResult));
           		startIdx = endIdx;
        	    endIdx = ((endIdx+incFactor)>strList.size())?strList.size():(endIdx+incFactor);
        	}
        	return result;
    	}
		
		public List<Integer> loadBalanceQuery(List<String> filenameSplit){
				List<WorkerThread> workerList = new ArrayList<WorkerThread>();
				List<Thread> threadList = new ArrayList<Thread>();
				boolean includeFE = false;
				int i=0;
				List<Integer>result = new ArrayList<Integer>();
				if (filenameSplit.size() > beAvailable.size()){
						includeFE = true;
				}
				for (String filename : filenameSplit){
						WorkerThread thisWorkerThread = new WorkerThread(filename, beAvailable.get(i));
					    Thread thisThread = new Thread(thisWorkerThread);
						thisThread.start();
						workerList.add(thisWorkerThread);
						threadList.add(thisThread);
						i++;
				}
				
				for (i = 0 ; i < threadList.size() ; i++){
						try{
							threadList.get(i).join();
							WorkerThread thisWorkerThread = workerList.get(i);
							result.addAll(thisWorkerThread.getValue());
						}catch ( Exception e){
								System.out.println("Thread Error");
						}
				}
				return result;
		}



		public void registerBEinFE(String beNode, int bePort){
				try{
						sem_lock.acquire();
						beAvailable.add(new BEHost(beNode, bePort));
						System.out.println("Added "+beNode+" with port "+bePort+" to the list");
						sem_lock.release();
				}catch (Exception e){
						System.out.println("Error adding BE Node: "+beNode+"("+bePort+") to the list");
				}
		}

		static Semaphore sem_lock = new Semaphore(100);
		public List<BEHost> beAvailable = new CopyOnWriteArrayList<>();
		public int refCount = 0;
		
		@Override
		public List<Integer> imageQuery(String filenames) throws IllegalArgument, org.apache.thrift.TException
        {
				List<Integer> result = new ArrayList<>();
				try{

					if ((filenames.length() == 0) || (filenames == "")){
							throw new IllegalArgument("filename cannot be blank");
					}
					if ((beAvailable.size() == 0)){
							result = queryImageInFE(filenames);
					}
					else{
					//		BEHost selectedBENode = beAvailable.get(0);
					//		result =  queryInBE(filenames, selectedBENode);
							List<String> filenameSplit = new ArrayList<>();
							filenameSplit = evenSplitFilenames(filenames, beAvailable.size());
							result = loadBalanceQuery(filenameSplit);
					}
					return result;
				}catch(Exception e){
						System.out.println(e.getMessage());
				}
				return result;
		}
		public List<Integer> queryImageInFE(String filenames) throws IllegalArgument, org.apache.thrift.TException
        {
                String s;
                List<Integer> result =  new ArrayList<>();
                try{
                        if ((filenames.length() == 0)|| (filenames == ""))
                                throw new IllegalArgument("filename cannot be blank");
                }
                catch (Exception e){
                        e.printStackTrace();
                }
                try{
                        Process p = Runtime.getRuntime().exec("python3 /home/s2sivash/ece751/git/ece751/newProj/model.py "+filenames);
                        BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
                        while((s = in.readLine()) != null){
                            System.out.println(s);
                            result.add(Integer.valueOf(s));
                        }
                        return result;
                }
                catch (Exception e){
                        e.printStackTrace();
                }
				return null;
		}

		static List<Integer> queryInBE(String filenames, BEHost selectedBENode){
				List<Integer> result = new ArrayList<Integer>();
				try{
						ImageQueryService.Client client = new ImageQueryService.Client(selectedBENode.protocol);
						selectedBENode.transport.open();
						result = client.imageQuery(filenames);
						selectedBENode.transport.close();
				}catch (Exception e){
						System.out.println("Connection to BE Node "+selectedBENode.beNode+" "+selectedBENode.bePort+" failed.");
						result = null;
				}
				return result;
		}
}





		

