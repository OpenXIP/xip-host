/**
 * Copyright (c) 2008 Washington University in Saint Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.worklist;

import java.io.File;
import java.util.List;

/**
 * @author Jaroslaw Krych
 *
 */
public interface Worklist {
	public boolean loadWorklist(File xmlWorklistFile);
	public boolean addWorklistEntry(WorklistEntry entry); //addWorklistEntry(WorklistEntry entry)
	public boolean modifyWorklistEntry(WorklistEntry entry); //modifyWorklistEntry(WorklistEntry entry)
	public boolean deleteWorkListEntry(WorklistEntry entry); //deleteWorklistEntry(WorklistEntry entry)	
	public List getWorklistEntries();
	public int getNumberOfWorklistEntries();
	public WorklistEntry getWorklistEntry(int i);	
}