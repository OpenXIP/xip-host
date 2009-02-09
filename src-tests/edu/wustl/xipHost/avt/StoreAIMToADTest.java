/**
 * Copyright (c) 2008 Washington University in Saint Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.avt;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import edu.wustl.xipApplication.aim.AimParseEvent;
import edu.wustl.xipApplication.aim.AimParseListener;
import edu.wustl.xipApplication.aim.AimParser;
import junit.framework.TestCase;

/**
 * @author Jaroslaw Krych
 *
 */
public class StoreAIMToADTest extends TestCase implements AimParseListener {
	AVTStore avtStore;
	List<AimParser> aimResults;
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
	public void testStoreAimToAD_1A() {
		String[] aims = new String[4];
		List<File> files = new ArrayList<File>();
		File aim1 = new File("./src-tests/edu/wustl/xipHost/avt/0022BaselineA.xml");
		File aim2 = new File("./src-tests/edu/wustl/xipHost/avt/0022BaselineB.xml");
		File aim3 = new File("./src-tests/edu/wustl/xipHost/avt/0022FollowupA.xml");
		File aim4 = new File("./src-tests/edu/wustl/xipHost/avt/0022FollowupB.xml");
		files.add(aim1);
		files.add(aim2);
		files.add(aim3);
		files.add(aim4);
		aimResults = new ArrayList<AimParser>();
		parse(files);
		while(aimResults.size() != 4){
			
		}
		for(int i = 0; i < aimResults.size(); i++){
			aims[i] = aimResults.get(i).getXMLString();
			System.out.println(aims[i]);
			System.out.println("-----------------------------------------------------------------");
		}		
		avtStore = new AVTStore(aims);
		avtStore.run();
		assertTrue(avtStore.getStoreResult());
	}
	
	void parse(List<File> items){
		for(int i = 0; i < items.size(); i++){									
			AimParser parser = new AimParser(items.get(i));
			parser.addAimParseListener(this);						
			exeService.execute(parser);			
		}	
		//exeService.shutdown();
	}

	public void parsedAimAvailable(AimParseEvent e) {
		AimParser aimParser = (AimParser)e.getSource();
		aimResults.add(aimParser);		
	}	
}
