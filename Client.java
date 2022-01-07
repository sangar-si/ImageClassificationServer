import java.util.List;
import java.util.ArrayList;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TTransportFactory;

public class Client {
    public static void main(String [] args) {
        if (args.length < 3) {
            System.err.println("Usage: java Client FE_host FE_port filenames");
            System.exit(-1);
        }

        try {
            TSocket sock = new TSocket(args[0], Integer.parseInt(args[1]));
            TTransport transport = new TFramedTransport(sock);
            TProtocol protocol = new TBinaryProtocol(transport);
            ImageQueryService.Client client = new ImageQueryService.Client(protocol);
            transport.open();
			String filenames = args[2];
            System.out.println(filenames);
            List<Integer> result = client.imageQuery(filenames);
            transport.close();
			for (int i : result){
					System.out.println(i);
			}
        } catch (TException x) {
            x.printStackTrace();
        }
    }
}
