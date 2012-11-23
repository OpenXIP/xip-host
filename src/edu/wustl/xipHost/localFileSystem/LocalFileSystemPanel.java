/**
 * Copyright (c) 2011 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.localFileSystem;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.net.URI;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.Timer;
import javax.swing.border.Border;
import javax.swing.tree.DefaultMutableTreeNode;

import org.apache.log4j.Logger;
import org.dcm4che2.data.Tag;
import org.nema.dicom.PS3_19.MimeType;
import org.nema.dicom.PS3_19.Modality;
import org.nema.dicom.PS3_19.ObjectDescriptor;
import org.nema.dicom.PS3_19.ObjectLocator;
import org.nema.dicom.PS3_19.State;
import org.nema.dicom.PS3_19.UID;
import org.nema.dicom.PS3_19.UUID;
import edu.wustl.xipHost.application.AppButton;
import edu.wustl.xipHost.application.Application;
import edu.wustl.xipHost.application.ApplicationEvent;
import edu.wustl.xipHost.application.ApplicationListener;
import edu.wustl.xipHost.application.ApplicationManager;
import edu.wustl.xipHost.application.ApplicationManagerFactory;
import edu.wustl.xipHost.dataAccess.Query;
import edu.wustl.xipHost.dataModel.ImageItem;
import edu.wustl.xipHost.dataModel.Item;
import edu.wustl.xipHost.dataModel.Patient;
import edu.wustl.xipHost.dataModel.SearchResult;
import edu.wustl.xipHost.dataModel.Series;
import edu.wustl.xipHost.dataModel.Study;
import edu.wustl.xipHost.gui.HostMainWindow;
import edu.wustl.xipHost.gui.checkboxTree.DataSelectionEvent;
import edu.wustl.xipHost.gui.checkboxTree.DataSelectionListener;
import edu.wustl.xipHost.gui.checkboxTree.DataSelectionValidator;
import edu.wustl.xipHost.gui.checkboxTree.NodeSelectionListener;
import edu.wustl.xipHost.gui.checkboxTree.SearchResultTree;
import edu.wustl.xipHost.hostControl.HostConfigurator;
import edu.wustl.xipHost.iterator.Criteria;
import edu.wustl.xipHost.iterator.IterationTarget;


/**
 * @author Jaroslaw Krych
 *
 */
public class LocalFileSystemPanel extends JPanel implements ApplicationListener, ActionListener, DicomParseListener, DataSelectionListener {
	final static Logger logger = Logger.getLogger(LocalFileSystemPanel.class);
	JPanel leftPanel = new JPanel();
	JPanel rightPanel = new JPanel();
	SearchResultTree resultTree = new SearchResultTree();
	JScrollPane treeView = new JScrollPane(resultTree);
	NodeSelectionListener nodeSelectionListener = new NodeSelectionListener();
	Color xipColor = new Color(51, 51, 102);
	Border border = BorderFactory.createLoweredBevelBorder();
	HostFileChooser fileChooser;
	File [] selectedFiles;
	int numThreads = 3;
	ExecutorService exeService = Executors.newFixedThreadPool(numThreads);	
	
	public LocalFileSystemPanel() {
		searchResult = new SearchResult("Local File System");
		resultTree.addMouseListener(ml);
		nodeSelectionListener.addDataSelectionListener(this);
		setBackground(xipColor);
		HostMainWindow.getHostIconBar().getApplicationBar().addApplicationListener(this);
	    treeView.setPreferredSize(new Dimension(500, HostConfigurator.adjustForResolution() + 10));
          
        //JFileChooser fileChooser = new JFileChooser();
        fileChooser = new HostFileChooser(true, new File("./dicom-dataset-demo"));
        fileChooser.setPreferredSize(new Dimension(500, HostConfigurator.adjustForResolution()));
        fileChooser.addActionListener(this);
		//leftPanel.setBackground(xipColor);
		leftPanel.add(fileChooser);
        add(leftPanel);
		add(rightPanel);
		rightPanel.add(treeView); 
		rightPanel.setBackground(xipColor);
		buildLayout();
	}

	void buildLayout(){				
		GridBagLayout layout = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        setLayout(layout);                 
        
        constraints.fill = GridBagConstraints.NONE;        
        constraints.gridx = 0;
        constraints.gridy = 0;        
        constraints.insets.top = 10;
        constraints.insets.left = 20;
        constraints.insets.right = 20;
        constraints.insets.bottom = 5;        
        constraints.anchor = GridBagConstraints.CENTER;
        layout.setConstraints(leftPanel, constraints);
        
        constraints.fill = GridBagConstraints.HORIZONTAL;        
        constraints.gridx = 1;
        constraints.gridy = 0;         
        constraints.insets.top = 10;
        constraints.insets.left = 20;
        constraints.insets.right = 20;
        constraints.insets.bottom = 5;        
        constraints.anchor = GridBagConstraints.CENTER;
        layout.setConstraints(rightPanel, constraints);
	}
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	Application targetApp = null;
	@Override
	public void launchApplication(ApplicationEvent event) {
		logger.debug("Current data source tab: " + this.getClass().getName());
		DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode)resultTree.getRootNode();
		boolean isDataSelected = DataSelectionValidator.isDataSelected(rootNode);
		if(isDataSelected){
			// Determine which application button the user clicked, and retrieve info about the application
			AppButton btn = (AppButton)event.getSource();
			ApplicationManager appMgr = ApplicationManagerFactory.getInstance(); 
			Application app = appMgr.getApplication(btn.getApplicationUUID());
			String appID = app.getID().toString();
			logger.debug("Application internal id: " + appID);
			String instanceName = app.getName();
			logger.debug("Application name: " + instanceName);
			File instanceExePath = app.getExePath();
			logger.debug("Exe path: " + instanceExePath);
			String instanceVendor = app.getVendor();
			logger.debug("Vendor: " + instanceVendor);
			String instanceVersion = app.getVersion();
			logger.debug("Version: " + instanceVersion);
			File instanceIconFile = app.getIconFile();
			String type = app.getType();
			logger.debug("Type: " + type);
			boolean requiresGUI = app.requiresGUI();
			logger.debug("Requires GUI: " + requiresGUI);
			String wg23DataModelType = app.getWG23DataModelType();
			logger.debug("WG23 data model type: " + wg23DataModelType);
			int concurrentInstances = app.getConcurrentInstances();
			logger.debug("Number of allowable concurrent instances: " + concurrentInstances);
			IterationTarget iterationTarget = app.getIterationTarget();
			logger.debug("IterationTarget: " + iterationTarget.toString());
			//Check if application to be launched is not running.
			//If yes, create new application instance
			State state = app.getState();
			Query query = new LocalFileSystemQuery(selectedDataSearchResult);
			File tmpDir = ApplicationManagerFactory.getInstance().getTmpDir();
			//Retrieve retrieve = new LocalFileSystemRetrieve(selectedDataSearchResult);
			if(state != null && !state.equals(State.EXIT)){
				Application instanceApp = new Application(instanceName, instanceExePath, instanceVendor,
						instanceVersion, instanceIconFile, type, requiresGUI, wg23DataModelType, concurrentInstances, iterationTarget);
				instanceApp.setSelectedDataSearchResult(selectedDataSearchResult);
				instanceApp.setQueryDataSource(query);
				//instanceApp.setRetrieveDataSource(retrieve);
				instanceApp.setDataSourceDomainName("edu.wustl.xipHost.localFileSystem.LocalFileSystemRetrieve");
				instanceApp.setDoSave(false);
				instanceApp.setApplicationTmpDir(tmpDir);
				appMgr.addApplication(instanceApp);		
				instanceApp.launch(appMgr.generateNewHostServiceURL(), appMgr.generateNewApplicationServiceURL());
				targetApp = instanceApp;
			}else{
				app.setSelectedDataSearchResult(selectedDataSearchResult);
				app.setQueryDataSource(query);
				//app.setRetrieveDataSource(retrieve);
				app.setDataSourceDomainName("edu.wustl.xipHost.localFileSystem.LocalFileSystemRetrieve");
				app.setApplicationTmpDir(tmpDir);
				app.launch(appMgr.generateNewHostServiceURL(), appMgr.generateNewApplicationServiceURL());
				targetApp = app;
			}
		}
	}
	
	int numOfParsingRequestsSent;
	int numOfParsingRequestsRecieved;
	SearchResult searchResult;
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("ApproveSelection")){
			resultTree.getRootNode().removeAllChildren();
			searchResult = new SearchResult("Local File System");		    		
			//Create selectedDataSearchResult from the selected files
			numOfParsingRequestsSent = 0;
			numOfParsingRequestsRecieved = 0;
			selectedFiles = fileChooser.getSelectedFiles();
			File selectedFile = null;
			for(int i = 0; i < selectedFiles.length; i++){
				selectedFile = selectedFiles[i];
				URI uri = selectedFile.toURI();
				FileRunner runner = new FileRunner(new File(uri));
				runner.addDicomParseListener(this);
				exeService.execute(runner);
				numOfParsingRequestsSent++;
			}
			synchronized(this){
				while(numOfParsingRequestsSent != numOfParsingRequestsRecieved){
					try {
						this.wait();
					} catch (InterruptedException e1) {
						logger.error(e1, e1);
					}
				}
			}		
			if(logger.isDebugEnabled()){
				List<Patient> patients = searchResult.getPatients();
				logger.debug("Value of selectedDataSearchresult: ");
				for(Patient logPatient : patients){
					logger.debug(logPatient.toString());
					List<Study> studies = logPatient.getStudies();
					for(Study logStudy : studies){
						logger.debug("   " + logStudy.toString());
						List<Series> series = logStudy.getSeries();
						for(Series logSeries : series){
							logger.debug("      " + logSeries.toString());
							List<Item> items = logSeries.getItems();
							for(Item logItem : items){
								logger.debug("         " + logItem.toString());
							}
						}
					}
				}
			}
			resultTree.updateNodes(searchResult);
	    } else if (e.getActionCommand().equals("CancelSelection")) {
		        
	    }
	}

	@Override
	public synchronized void dicomAvailable(DicomParseEvent e) {
		FileRunner source = (FileRunner)e.getSource();
		String[][] result = source.getParsingResult();		
		updateSearchResult(result);	
		updateUI();		
		numOfParsingRequestsRecieved++;
		this.notify();
	}

	void updateSearchResult(String[][] result){
		Timestamp lastUpdated = new Timestamp(Calendar.getInstance().getTime().getTime());
		String patientName = result[0][1];
		String patientID = result[1][1];
		String patientBirthDate = result[2][1];
		Patient patient = new Patient(patientName, patientID, patientBirthDate);		
		/*if(selectedDataSearchResult == null){
			selectedDataSearchResult = new SearchResult("Local File System");
		}*/
		if(!searchResult.contains(patientID)){
			searchResult.addPatient(patient);
			Map<Integer, Object> dicomCriteria = new HashMap<Integer, Object>();
			Map<String, Object> aimCriteria = new HashMap<String, Object>();
			dicomCriteria.put(Tag.PatientName, patientName);
			dicomCriteria.put(Tag.PatientID, patientID);
			Criteria originalCriteria = new Criteria(dicomCriteria, aimCriteria);
			searchResult.setOriginalCriteria(originalCriteria);
		} else {
			patient = searchResult.getPatient(patientID);
		}
		String studyDate = result[3][1];
		String studyID = result[4][1];
		String studyDesc = result[5][1];
		String studyInstanceUID = result[10][1];
		Study study = new Study(studyDate, studyID, studyDesc, studyInstanceUID);
		if(!patient.contains(studyInstanceUID)){
			patient.addStudy(study);
			patient.setLastUpdated(lastUpdated);
		} else {
			study = patient.getStudy(studyInstanceUID);
		}		
		String seriesNumber = result[6][1];
		String modality = result[7][1];
		String seriesDesc = result[8][1];
		String seriesInstanceUID = result[11][1];
		Series series = new Series(seriesNumber, modality, seriesDesc, seriesInstanceUID);
		if(!study.contains(seriesInstanceUID)){
			study.addSeries(series);
			study.setLastUpdated(lastUpdated);
		} else {
			series = study.getSeries(seriesInstanceUID);
		}		
		String sopInstanceUID = result[13][1];
		String fileLocation = result[9][1];
		Item item = new ImageItem(sopInstanceUID);
		if(!series.contains(sopInstanceUID)){
			ObjectDescriptor objDesc = new ObjectDescriptor();
			UUID objDescUUID = new UUID();
			objDescUUID.setUuid(java.util.UUID.randomUUID().toString());
			objDesc.setDescriptorUuid(objDescUUID);													
			String mimeTypeString = null;
			MimeType mimeType = new MimeType();
			mimeType.setType(mimeTypeString);
			objDesc.setMimeType(mimeType);			
			UID uid = new UID();
			String sopClassUID = result[14][1];;
			uid.setUid(sopClassUID);
			objDesc.setClassUID(uid);				
			Modality mod = new Modality();
			mod.setModality(modality);
			objDesc.setModality(mod);
			item.setObjectDescriptor(objDesc);
			ObjectLocator objLoc = new ObjectLocator();				
			objLoc.setSource(objDescUUID);
			objLoc.setLocator(objDescUUID);
			//TODO: Add TS, etc.
			objLoc.setURI(fileLocation); 
			item.setObjectLocator(objLoc);
			series.addItem(item);
			series.setLastUpdated(lastUpdated);
			series.setContainsSubsetOfItems(true);
		} 
	}
	
	@Override
	public synchronized void nondicomAvailable(DicomParseEvent e) {
					
	}
	
	boolean wasDoubleClick = false;
	MouseListener ml = new MouseAdapter(){  
		public void mouseClicked(final MouseEvent e) {
			int x = e.getX();
	     	int y = e.getY();
	     	nodeSelectionListener.setSearchResultTree(resultTree);
	     	nodeSelectionListener.setSelectionCoordinates(x, y);
	     	nodeSelectionListener.setSearchResult(searchResult);
	    	if (e.getClickCount() == 2){
	    		wasDoubleClick = true;
	    		nodeSelectionListener.setWasDoubleClick(wasDoubleClick);
	        } else {
	        	Timer timer = new Timer(300, nodeSelectionListener);
	        	timer.setRepeats(false);
	        	timer.start();
	        }
	    }
	};

	SearchResult selectedDataSearchResult;
	@Override
	public void dataSelectionChanged(DataSelectionEvent event) {
		selectedDataSearchResult = (SearchResult)event.getSource();
	}
}
