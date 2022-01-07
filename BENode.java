import java.net.InetAddress;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import org.apache.thrift.TProcessorFactory;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.protocol.TProtocol;
import java.util.concurrent.CountDownLatch;
import org.apache.thrift.*;
import org.apache.thrift.async.*;
import org.apache.thrift.transport.*;
import org.apache.thrift.protocol.*;
import org.apache.thrift.server.*;
import org.apache.thrift.server.TServer.*;

public class BENode {
    static Logger log;

	public static void main(String [] args) throws Exception {
        if (args.length != 3) {
            System.err.println("Usage: java BENode FE_host FE_port BE_port");
            System.exit(-1);
        }
		
		try{
			BasicConfigurator.configure();
			log = Logger.getLogger(BENode.class.getName());
			String hostFE = args[0];
			int portFE = Integer.parseInt(args[1]);
			int portBE = Integer.parseInt(args[2]);
			log.info("Launching BE Node on port " + portBE + " at host " + getHostName());
			ImageQueryService.Processor processor = new ImageQueryService.Processor<ImageQueryService.Iface>(new ImageQueryServiceHandler());
			TNonblockingServerSocket socket = new TNonblockingServerSocket(portBE);
			THsHaServer.Args sargs = new THsHaServer.Args(socket);
			sargs.protocolFactory(new TBinaryProtocol.Factory());
			sargs.transportFactory(new TFramedTransport.Factory());
			sargs.processorFactory(new TProcessorFactory(processor));
			sargs.maxWorkerThreads(64);
			THsHaServer server = new THsHaServer(sargs);
			pingFE(getHostName(), portBE, hostFE, portFE);
			System.out.println("Sent ping to FE Node");
			server.serve();
			System.out.println("BE is in service...");
		}	

		catch (Exception e){
			System.out.println("Exception Occurred in BE...");
			e.printStackTrace();
		}
	}

	static void pingFE(String beNode, int bePort, String feNode, int fePort){
			boolean success = false;
			while(!success){
					try{
							System.out.println("Pinging to FE");
							TSocket sock = new TSocket(feNode, fePort);
							TTransport transport = new TFramedTransport(sock);
							TProtocol protocol = new TBinaryProtocol(transport);
							ImageQueryService.Client client = new ImageQueryService.Client(protocol);
							transport.open();
							client.registerBEinFE(beNode, bePort);
							transport.close();
							success = true;
					}
					catch (Exception e){
							System.out.println(e.getMessage());
					}
			}
	}

	static String getHostName(){
			try{
					return InetAddress.getLocalHost().getHostName();
			}
			catch (Exception e){
					return "localhost";
			}
	}
}


