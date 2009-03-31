/**
 * Copyright (c) 2008 Washington University in Saint Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.avt;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import junit.framework.TestCase;

/**
 * @author Jaroslaw Krych
 *
 */
public class StoreAIMToADTest extends TestCase {
	AVTStore avtStore;
	int numThreads = 3;
	ExecutorService exeService = Executors.newFixedThreadPool(numThreads);
	
	protected void setUp() throws Exception {
		super.setUp();		
	}
	
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	//AVTStore 1A - basic flow. Perfect condistions. AIM objects to store are valid XML strings.
	//Expected result: boolean true
	public void testStoreAimToAD_1A(){
		File[] aims = new File[3];		
		File aim1 = new File("./test-content/AIM_rv12_Test/0022BaselineA.xml");		
		File aim2 = new File("./test-content/AIM_rv12_Test/0022FollowupA.xml");		
		File aim3 = new File("./test-content/AIM_rv12_Test/0022FollowupB.xml");
		aims[0] = aim1;
		aims[1] = aim2;
		aims[2] = aim3;				
		avtStore = new AVTStore(aims);
		avtStore.run();
		assertTrue(avtStore.getStoreResult());
	}
}
