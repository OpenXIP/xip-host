/**
 * Copyright (c) 2008 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.net.URI;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import org.apache.log4j.Logger;
import org.dcm4che2.data.Tag;
import org.nema.dicom.wg23.Modality;
import org.nema.dicom.wg23.ObjectDescriptor;
import org.nema.dicom.wg23.ObjectLocator;
import org.nema.dicom.wg23.State;
import org.nema.dicom.wg23.Uid;
import org.nema.dicom.wg23.Uuid;
import edu.wustl.xipHost.application.AppButton;
import edu.wustl.xipHost.application.Application;
import edu.wustl.xipHost.application.ApplicationEvent;
import edu.wustl.xipHost.application.ApplicationListener;
import edu.wustl.xipHost.application.ApplicationManager;
import edu.wustl.xipHost.application.ApplicationManagerFactory;
import edu.wustl.xipHost.dataAccess.Query;
import edu.wustl.xipHost.dataAccess.Retrieve;
import edu.wustl.xipHost.dataModel.ImageItem;
import edu.wustl.xipHost.dataModel.Item;
import edu.wustl.xipHost.dataModel.Patient;
import edu.wustl.xipHost.dataModel.SearchResult;
import edu.wustl.xipHost.dataModel.Series;
import edu.wustl.xipHost.dataModel.Study;
import edu.wustl.xipHost.iterator.Criteria;
import edu.wustl.xipHost.iterator.IterationTarget;
import edu.wustl.xipHost.hostControl.HostConfigurator;
import edu.wustl.xipHost.localFileSystem.DicomParseEvent;
import edu.wustl.xipHost.localFileSystem.DicomParseListener;
import edu.wustl.xipHost.localFileSystem.FileRunner;
import edu.wustl.xipHost.localFileSystem.LocalFileSystemQuery;
import edu.wustl.xipHost.localFileSystem.LocalFileSystemRetrieve;


/**
 * @author Jaroslaw Krych
 *
 */
public class InputDialog extends JPanel implements ListSelectionListener, DicomParseListener, ApplicationListener  {	
	final static Logger logger = Logger.getLogger(InputDialog.class);
	DicomTableModel dicomTableModel = new DicomTableModel();
	JList list;
	JScrollPane listScroller;
	DefaultListModel listModel;
	JTable tableInput;
	JScrollPane jsp;	
	Font font = new Font("Tahoma", 0, 12);
	Color xipColor = new Color(51, 51, 102);
	Color xipBtn = new Color(56, 73, 150);
	Color xipLightBlue = new Color(156, 162, 189);
	String[][] values;		
	Cursor hourglassCursor = new Cursor(Cursor.WAIT_CURSOR);	
	Cursor normalCursor = new Cursor(Cursor.DEFAULT_CURSOR);	
	int numThreads = 3;
	ExecutorService exeService = Executors.newFixedThreadPool(numThreads);			
	
	public InputDialog(){									
		HostMainWindow.getHostIconBar().getApplicationBar().addApplicationListener(this);
		setBackground(Color.BLACK);			
		listModel = new DefaultListModel();
		list = new JList(listModel);
		list.addListSelectionListener(this);		
		list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);		
		list.setVisibleRowCount(-1);		
		listScroller = new JScrollPane(list);
		listScroller.setPreferredSize(new Dimension(400, 180));
		list.setBackground(xipColor);
	    list.setForeground(Color.WHITE);
		add(listScroller);		
		tableInput = new JTable(dicomTableModel);
		tableInput.setShowGrid(false);
		TableColumn col = tableInput.getColumnModel().getColumn(0);	    
	    col.setPreferredWidth(dicomTableModel.getColumnWidth(0));
	    col = tableInput.getColumnModel().getColumn(1);
	    col.setPreferredWidth(dicomTableModel.getColumnWidth(1));
	    tableInput.setBackground(xipColor);
	    tableInput.setForeground(Color.WHITE);
		jsp = new JScrollPane(tableInput);
		jsp.setPreferredSize(new Dimension(450, 180));
		add(jsp);
		buildLayout();
	}
	
	class DicomTableModel extends AbstractTableModel {
		String[] strArrayColumnNames = {
			"Attribute name",
			"Attribute value"			            
        };    
		
		public int getColumnCount() {			
			return strArrayColumnNames.length;
		}

		public int getRowCount() {			
			if(values == null){return 0;}
			return values.length;
		}

		public Object getValueAt(int rowIndex, int columnIndex) {
			switch( columnIndex ) {               
				case 0:                	
					return values[rowIndex][0];
				case 1:
                	if(values == null){return null;}
					return values[rowIndex][1];                	                
                default:
                    return null;
            }           			
		}
				
		public String getColumnName( int col ) {
            return strArrayColumnNames[col];
        }
		
		public int getColumnWidth( int nCol ) {
            switch( nCol ) {
                case 0:
                    return 150;
                case 1:
                    return 300;                
                default:
                    return 225;
            }
        }
	}
	
	Map<URI, String[][]> hashmap = new HashMap<URI, String[][]>();
	public void setParsingResult(URI uri, String[][] parsingResult){
		hashmap.put(uri, parsingResult);	
	}		
	
	File[] selectedFiles;
	public void setItems(File[] items){
		selectedFiles = items;
		for(File file : items){
			URI uri = file.toURI();
			hashmap.put(file.toURI(), null);
			listModel.addElement(uri);	
		}
	}
	
	void clearData(){
		hashmap.clear();		
		listModel.clear();
		values = null;
		//FileManagerFactory.getInstance().clearParsedData();	
		tableInput.updateUI();
	}
	
	void buildLayout(){				
		GridBagLayout layout = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        setLayout(layout);         
                       
        constraints.fill = GridBagConstraints.HORIZONTAL;        
        constraints.gridx = 0;
        constraints.gridy = 0;        
        constraints.insets.top = 30;
        constraints.insets.left = 20;
        constraints.insets.right = 10;       
        constraints.anchor = GridBagConstraints.CENTER;
        layout.setConstraints(listScroller, constraints);
        
        constraints.fill = GridBagConstraints.HORIZONTAL;        
        constraints.gridx = 1;
        constraints.gridy = 0;        
        constraints.insets.top = 30;
        constraints.insets.left = 0;
        constraints.insets.right = 20;       
        constraints.anchor = GridBagConstraints.CENTER;
        layout.setConstraints(jsp, constraints); 
	}
	
	class ComboBoxRenderer extends JLabel implements ListCellRenderer {
		DefaultListCellRenderer defaultRenderer = new DefaultListCellRenderer();
		Dimension preferredSize = new Dimension(600, 15);
		public Component getListCellRendererComponent(JList list, Object value, int index,
			      boolean isSelected, boolean cellHasFocus) {
			    JLabel renderer = (JLabel) defaultRenderer.getListCellRendererComponent(list, value, index,
			        isSelected, cellHasFocus);
			    if (value instanceof Application) {
			    	renderer.setText(((Application)value).getName());
			    	renderer.setBackground(Color.WHITE);
			    }
			    if(cellHasFocus || isSelected){
			    	renderer.setBackground(xipLightBlue);
			    	renderer.setForeground(Color.WHITE);
			    	renderer.setBorder(new LineBorder(Color.DARK_GRAY));
			    }else{
			    	renderer.setBorder(null);
			    }			    
			    renderer.setPreferredSize(preferredSize);
			    return renderer;
			  }		
	}
	
	JDialog frame;	
	public void display(){			
		updateUI();
		JFrame owner = HostConfigurator.getHostConfigurator().getMainWindow();
		frame =  new JDialog(owner, "Input dataset description", false);
		JRootPane rootPane = frame.getRootPane();	
		registerEscapeKey(rootPane);
		//frame.setUndecorated(true);
		JFrame.setDefaultLookAndFeelDecorated(true);
		frame.getContentPane().add(this);
		frame.setVisible(true);
		HostConfigurator.getHostConfigurator().getMainWindow().setInputDialog(this);
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {	
		        clearData();  
		        frame.dispose();
			}
		});		
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension windowSize = frame.getPreferredSize();
        frame.setBounds((screenSize.width - windowSize.width) / 2, (screenSize.height - windowSize.height) /2,  windowSize.width, windowSize.height);
	}
	

	public void valueChanged(ListSelectionEvent e) {
		if (e.getValueIsAdjusting() == false) {
			if (list.getSelectedIndex() != -1) {
				JList list = ((JList)e.getSource());		
				URI selectedURI = (URI)list.getSelectedValue();
				FileRunner runner = new FileRunner(new File(selectedURI));
				runner.addDicomParseListener(this);
				runner.run();
			}
		}		
	}
	
	void setTableValues(String[][] values){
		this.values = values;
		tableInput.updateUI();
	}
	
	void registerEscapeKey (JRootPane rootPane) {
		KeyStroke escapeKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false);
		Action escapeAction = new AbstractAction(){
			public void actionPerformed(ActionEvent e){
				 frame.dispose();
				 clearData();
			}
		};		 
		rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escapeKeyStroke, "ESCAPE");
		rootPane.getActionMap().put("ESCAPE", escapeAction);
	}

	//dicomAvailbale() or nondicomAvailable() is used to parse single items on demand (upon selection of the item) 
	@Override
	public synchronized void dicomAvailable(DicomParseEvent e) {
		setCursor(hourglassCursor);
		FileRunner source = (FileRunner)e.getSource();
		File file = source.getItem();
		URI selectedURI = file.toURI();
		String[][] result = source.getParsingResult();		
		setParsingResult(file.toURI(), result);
		setTableValues(hashmap.get(selectedURI));
		updateSearchResult(result);		
		updateUI();		
		setCursor(normalCursor);
		numOfParsingRequestsRecieved++;
		this.notify();
	}

	void updateSearchResult(String[][] result){
		Timestamp lastUpdated = new Timestamp(Calendar.getInstance().getTime().getTime());
		String patientName = result[0][1];
		String patientID = result[1][1];
		String patientBirthDate = result[2][1];
		Patient patient = new Patient(patientName, patientID, patientBirthDate);		
		if(!selectedDataSearchResult.contains(patientID)){
			selectedDataSearchResult.addPatient(patient);
			Map<Integer, Object> dicomCriteria = new HashMap<Integer, Object>();
			Map<String, Object> aimCriteria = new HashMap<String, Object>();
			dicomCriteria.put(Tag.PatientName, patientName);
			dicomCriteria.put(Tag.PatientID, patientID);
			Criteria originalCriteria = new Criteria(dicomCriteria, aimCriteria);
			selectedDataSearchResult.setOriginalCriteria(originalCriteria);
		} else {
			patient = selectedDataSearchResult.getPatient(patientID);
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
		if(!series.containsItem(sopInstanceUID)){
			ObjectDescriptor objDesc = new ObjectDescriptor();
			Uuid objDescUUID = new Uuid();
			objDescUUID.setUuid(UUID.randomUUID().toString());
			objDesc.setUuid(objDescUUID);													
			String mimeType = null;
			objDesc.setMimeType(mimeType);			
			Uid uid = new Uid();
			String sopClassUID = result[14][1];;
			uid.setUid(sopClassUID);
			objDesc.setClassUID(uid);				
			Modality mod = new Modality();
			mod.setModality(modality);
			objDesc.setModality(mod);
			item.setObjectDescriptor(objDesc);
			ObjectLocator objLoc = new ObjectLocator();				
			objLoc.setUuid(objDescUUID);				
			objLoc.setUri(fileLocation); 
			item.setObjectLocator(objLoc);
			series.addItem(item);
			series.setLastUpdated(lastUpdated);
		} 
	}
	
	@Override
	public synchronized void nondicomAvailable(DicomParseEvent e) {
		hashmap.clear();				
		values = null;
		tableInput.updateUI();			
	}

	Application targetApp = null;
	int numOfParsingRequestsSent;
	int numOfParsingRequestsRecieved;
	SearchResult selectedDataSearchResult;
	@Override
	public void launchApplication(ApplicationEvent event) {
		logger.debug("Current data source tab: " + this.getClass().getName());
		//Create selectedDataSearchResult from the selected files
		numOfParsingRequestsSent = 0;
		numOfParsingRequestsRecieved = 0;
		selectedDataSearchResult = new SearchResult("Local File System");
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
				} catch (InterruptedException e) {
					logger.error(e, e);
				}
			}
		}		
		if(logger.isDebugEnabled()){
			List<Patient> patients = selectedDataSearchResult.getPatients();
			logger.debug("Value of selectedDataSearchresult: ");
			for(Patient logPatient : patients){
				logger.debug(logPatient.toString());
				List<Study> studies = logPatient.getStudies();
				for(Study logStudy : studies){
					logger.debug("   " + logStudy.toString());
					List<Series> series = logStudy.getSeries();
					for(Series logSeries : series){
						logger.debug("      " + logSeries.toString());
					}
				}
			}
		}
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
		Retrieve retrieve = new LocalFileSystemRetrieve(selectedDataSearchResult);
		if(state != null && !state.equals(State.EXIT)){
			Application instanceApp = new Application(instanceName, instanceExePath, instanceVendor,
					instanceVersion, instanceIconFile, type, requiresGUI, wg23DataModelType, concurrentInstances, iterationTarget);
			instanceApp.setSelectedDataSearchResult(selectedDataSearchResult);
			instanceApp.setQueryDataSource(query);
			instanceApp.setRetrieveDataSource(retrieve);
			instanceApp.setDoSave(false);
			instanceApp.setApplicationTmpDir(tmpDir);
			appMgr.addApplication(instanceApp);		
			instanceApp.launch(appMgr.generateNewHostServiceURL(), appMgr.generateNewApplicationServiceURL());
			targetApp = instanceApp;
		}else{
			app.setSelectedDataSearchResult(selectedDataSearchResult);
			app.setQueryDataSource(query);
			app.setRetrieveDataSource(retrieve);
			app.setApplicationTmpDir(tmpDir);
			app.launch(appMgr.generateNewHostServiceURL(), appMgr.generateNewApplicationServiceURL());
			targetApp = app;
		}
		//Shut InputDialog
		HostConfigurator.getHostConfigurator().getMainWindow().setInputDialog(null);
		clearData();  
        frame.dispose();
	}
	
	
}
