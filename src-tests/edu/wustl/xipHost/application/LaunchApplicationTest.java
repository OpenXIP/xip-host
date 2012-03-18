/**
 * Copyright (c) 2011 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.application;

import java.io.IOException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Jaroslaw Krych
 *
 */
public class LaunchApplicationTest {

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		try {
			String[] cmdarray = {"open", "file:/Users/krych/Documents/workspace2/XIPHost/src-tests/edu/wustl/xipHost/application/test.sh"};
			//String[] cmdarray = {"open", "/Users/krych/Documents/workspace2/XIPApp/src/edu/wustl/xipApplication/samples/XIPApplication_WashU_3.sh"};
			Runtime.getRuntime().exec(cmdarray) ;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
