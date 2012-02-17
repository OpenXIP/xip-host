/**
 * Copyright (c) 2008 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.caGrid;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * @author Jaroslaw Krych
 *
 */
//INFO: dataset must be preloaded prior to running this JUnit tests. Use PreloadDICOM first to prelaod all DICOM files 
//and then PreloadAIM to preload all AIM XML files from AD_Preload_JUnit_Tests.
@RunWith(Suite.class)
@SuiteClasses({QueryNBIATest.class, QueryNBIAwithGridQueryTest.class})
public class TestcaGridPkgeAllJUnit4 {
	
	public class JUnit4Suite {
	
	}
}
