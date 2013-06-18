/*
Copyright (c) 2013, Washington University in St.Louis.
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

package edu.wustl.xipHost.dataAccess;

import java.io.File;
import java.util.List;
import java.util.Map;
import org.nema.dicom.wg23.ObjectDescriptor;

/**
 * @author Jaroslaw Krych
 *
 */
public interface Retrieve extends Runnable {
	public void setCriteria(Map<Integer, Object> dicomCriteria, Map<String, Object> aimCriteria);
	public void setCriteria(Object criteria);
	public void setObjectDescriptors(List<ObjectDescriptor> objectDescriptors);
	public void setImportDir(File importDir);
	public void setRetrieveTarget(RetrieveTarget retrieveTarget);
	public void setDataSource(DataSource dataSource);
	public void addRetrieveListener(RetrieveListener l);
}
