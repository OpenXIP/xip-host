/*
Copyright (c) 2013, Washington University in St.Louis
All rights reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package edu.wustl.xipHost.avt2ext;

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
@SuiteClasses({QueryADTest.class, CreateIteratorTest.class, RetrieveAIMTest.class, RetrieveDicomSegTest.class, RetrieveDICOMwithAVTRetrieve.class})
public class TestavtPkgeAllJUnit4 {
	
	public class JUnit4Suite {
	
	}
}
