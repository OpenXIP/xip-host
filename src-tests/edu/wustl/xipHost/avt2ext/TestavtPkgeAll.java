/**
 * Copyright (c) 2008 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.avt2ext;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * @author Jaroslaw Krych
 *
 */
@RunWith(Suite.class)
@SuiteClasses({QueryADTest.class, CreateIteratorTest.class, RetrieveAIMTest.class, RetrieveDicomSegTest.class})
public class TestavtPkgeAll {
	
	public class JUnit4Suite {
	
	}
}
