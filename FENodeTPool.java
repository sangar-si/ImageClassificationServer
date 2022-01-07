import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import org.apache.thrift.TProcessorFactory;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.server.*;
import org.apache.thrift.transport.*;
import org.apache.thrift.transport.TFramedTransport;


public class FENodeTPool {
    static Logger log;

    public static void main(String [] args) throws Exception {
        if (args.length != 1) {
            System.err.println("Usage: java FENode FE_port");
            System.exit(-1);
        }
		try{
				BasicConfigurator.configure();
				log = Logger.getLogger(FENode.class.getName());

				int portFE = Integer.parseInt(args[0]);
				log.info("Launching FE node on port "+ portFE);

				ImageQueryService.Processor processor = new ImageQueryService.Processor<ImageQueryService.Iface>(new FEManager());
				TServerSocket socket = new TServerSocket(portFE);
				TThreadPoolServer.Args sargs = new TThreadPoolServer.Args(socket);
				sargs.protocolFactory(new TBinaryProtocol.Factory());
				sargs.transportFactory(new TFramedTransport.Factory());
				sargs.processorFactory(new TProcessorFactory(processor));
				sargs.maxWorkerThreads(64);
				TServer server = new TThreadPoolServer(sargs);
				log.info("Launching FENode ThreadPool Server");
				new Thread(new Runnable() {
						public void run(){
								server.serve();
						}
				}).start();
		}
		catch(Exception e){
			System.out.println(e.getMessage());
		}
	}
}


									

