package edu.wustl.xipHost.caGrid;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

import junit.framework.TestCase;

public class LoadNCIAModelMapTest extends TestCase {
	GridUtil util;
	protected void setUp() throws Exception {
		util = new GridUtil();
	}
	
	//GridUtil 1A of the basic flow. NCIAModelMap file exists and is correct.
	public void testLoadNCIAModelMap1A() throws IOException {						
		FileInputStream fis = new FileInputStream("./src-tests/edu/wustl/xipHost/caGrid/NCIAModelMap.properties");
		Map<String, String> map = util.loadNCIAModelMap(fis);
		assertNotNull("NCIAMoelMap file exists is is valid but load failed.", map);
		assertSame("Data is valid but load did not return correct type.", java.util.HashMap.class, map.getClass());		
	}

	//GridUtil 1B of the basic flow. NCIAModelMap file does not exist.
	public void testLoadNCIAModelMap1B() throws IOException {
		try {			
			FileInputStream fis = new FileInputStream("./src-tests/edu/wustl/xipHost/caGrid/NCIAModelMapVOID.properties");
			util.loadNCIAModelMap(fis);
			fail("File does not exists but system did not catch the exception.");
		} catch (FileNotFoundException e){
			assertTrue(true);
		}
	}	
}
