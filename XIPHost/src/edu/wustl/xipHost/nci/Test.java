package edu.wustl.xipHost.nci;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		File nciLibDir = new File("./lib/nci");
		File[] filesNCI = nciLibDir.listFiles();
		HashSet<String> nciSet = new HashSet<String>();
		for(int i = 0; i < filesNCI.length; i++){			
			nciSet.add(filesNCI[i].getName());
		}		
		File osuLibDir = new File("./lib");
		File[] filesOSU = osuLibDir.listFiles();
		HashSet<String> osuSet = new HashSet<String>();
		for(int i = 0; i < filesOSU.length; i++){
			osuSet.add(filesOSU[i].getName());			
		}
		Iterator<String> nciIter = nciSet.iterator();
		while(nciIter.hasNext()){
			String fileName = nciIter.next();
			if(osuSet.contains(fileName)){				
				File file = new File("./lib/nci/" + fileName);
				file.delete();
				try {
					System.out.println(file.getCanonicalPath());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

}
