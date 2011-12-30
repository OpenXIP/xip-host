package edu.wustl.xipHost.avt2ext;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class TestavtPkgeAllJUnit3_8 extends TestCase {

	public static Test suite() {
		TestSuite suite = new TestSuite(TestavtPkgeAllJUnit3_8.class.getName());
		//$JUnit-BEGIN$
		suite.addTestSuite(StoreADTestPatientLevel.class);
		suite.addTestSuite(StoreADTestSeriesLevel.class);
		suite.addTestSuite(StoreADTestStudyLevel.class);
		suite.addTestSuite(StoreADTestTopLevel.class);
		suite.addTestSuite(StoreAIMToADTest.class);
		suite.addTestSuite(StoreDicomSegToADTest.class);
		//$JUnit-END$
		return suite;
	}

}
