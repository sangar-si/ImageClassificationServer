import java.io.*;
import java.util.*;

public class RunPy{
		public static void main(String args[]) throws Exception{
				String[] filenames = args[0].split(",");
				String s;
				try{
						Process p = Runtime.getRuntime().exec("python3 /home/s2sivash/ece751/git/ece751/newProj/model.py "+args[0]);
						BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
						while((s = in.readLine()) != null){
							System.out.println(s);
						}
				}
				catch (Exception e){
						e.printStackTrace();
				}	
		}
}

