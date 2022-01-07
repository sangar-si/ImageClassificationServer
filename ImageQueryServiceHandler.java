import java.io.*;
import java.util.*;

public class ImageQueryServiceHandler implements ImageQueryService.Iface{
		public List<Integer> imageQuery(String filenames) throws IllegalArgument, org.apache.thrift.TException
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

		@Override
    	public void registerBEinFE(String beNode, int bePort)
		{
        		System.out.println("BE Called BCrypt for ping. Unsupported call");
       			throw new UnsupportedOperationException();
    	}
}

