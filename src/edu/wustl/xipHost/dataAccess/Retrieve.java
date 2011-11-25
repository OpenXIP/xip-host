/**
 * Copyright (c) 2010 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.dataAccess;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.nema.dicom.wg23.ObjectDescriptor;
import org.nema.dicom.wg23.ObjectLocator;

import edu.wustl.xipHost.iterator.TargetElement;

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
