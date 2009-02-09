package edu.wustl.xipHost.avt;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.border.Border;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import com.pixelmed.dicom.Attribute;
import com.pixelmed.dicom.AttributeList;
import com.pixelmed.dicom.AttributeTag;
import com.pixelmed.dicom.CodeStringAttribute;
import com.pixelmed.dicom.DicomDictionary;
import com.pixelmed.dicom.DicomException;
import com.pixelmed.dicom.TagFromName;
import com.pixelmed.dicom.UniqueIdentifierAttribute;
import com.siemens.scr.avt.ad.connector.api.ImageAnnotationDescriptor;
import edu.wustl.xipHost.dataModel.AIMItem;
import edu.wustl.xipHost.dataModel.ImageItem;
import edu.wustl.xipHost.dataModel.Patient;
import edu.wustl.xipHost.dataModel.SearchResult;
import edu.wustl.xipHost.dataModel.Series;
import edu.wustl.xipHost.dataModel.Study;
import edu.wustl.xipHost.dicom.DicomQuery;
import edu.wustl.xipHost.dicom.DicomRetrieve;
import edu.wustl.xipHost.dicom.DicomRetrieveEvent;
import edu.wustl.xipHost.dicom.DicomRetrieveListener;
import edu.wustl.xipHost.dicom.PacsLocation;
import edu.wustl.xipHost.dicom.SearchEvent;
import edu.wustl.xipHost.dicom.SearchListener;
import edu.wustl.xipHost.gui.SearchCriteriaPanel;
import edu.wustl.xipHost.gui.checkboxTree.SearchResultTree;
import edu.wustl.xipHost.hostControl.HostConfigurator;
import edu.wustl.xipHost.localFileSystem.FileManager;
import edu.wustl.xipHost.localFileSystem.FileManagerFactory;

public class AVTPanel extends JPanel implements ActionListener, AVTListener, SearchListener, DicomRetrieveListener, TreeSelectionListener{
	SearchCriteriaPanel criteriaPanel = new SearchCriteriaPanel();
	SearchResultTree resultTree = new SearchResultTree();
	JScrollPane treeView = new JScrollPane(resultTree);
	JPanel leftPanel = new JPanel();
	JPanel rightPanel = new JPanel();
	JProgressBar progressBar = new JProgressBar();	
	Color xipColor = new Color(51, 51, 102);
	Color xipBtn = new Color(56, 73, 150);
	Color xipLightBlue = new Color(156, 162, 189);
	Font font_1 = new Font("Tahoma", 0, 13);
	Border border = BorderFactory.createLoweredBevelBorder();		
	JButton btnRetrieve;
	PacsLocation loc = new PacsLocation("127.0.0.1", 3001, "WORKSTATION1", "XIPHost embedded database");
	
	public AVTPanel(){
		setBackground(xipColor);		
		criteriaPanel.getQueryButton().addActionListener(this);
		criteriaPanel.setQueryButtonText("Search AD");
		leftPanel.add(criteriaPanel);			    
	    resultTree.addTreeSelectionListener(this);
		treeView.setPreferredSize(new Dimension(500, HostConfigurator.adjustForResolution()));
		treeView.setBorder(border);	
		btnRetrieve = new JButton("Retrieve");
        btnRetrieve.setFont(font_1); 
        btnRetrieve.setFocusable(true);
		btnRetrieve.setEnabled(false);				
		btnRetrieve.setBackground(xipBtn);
		btnRetrieve.setForeground(Color.WHITE);
		btnRetrieve.setPreferredSize(new Dimension(115, 25));
		btnRetrieve.addActionListener(this);
		rightPanel.add(treeView);
		rightPanel.add(btnRetrieve);
		leftPanel.setBackground(xipColor);
		rightPanel.setBackground(xipColor);
		add(leftPanel);
		add(rightPanel);
		progressBar.setIndeterminate(false);
	    progressBar.setString("");	    
	    progressBar.setStringPainted(true);	    
	    progressBar.setBackground(new Color(156, 162, 189));
	    progressBar.setForeground(xipColor);
	    add(progressBar);
	    buildRightPanelLayout();
	    buildLayout();
	}
		
	AttributeList criteria;	
	void setCriteriaList(AttributeList criteria){
		this.criteria = criteria;
	}
	
	Thread threadQueryDicom;
	Thread threadRetrieveDicom;
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == criteriaPanel.getQueryButton()){			
			btnRetrieve.setEnabled(false);			
			btnRetrieve.setBackground(Color.GRAY);			
			resultTree.rootNode.removeAllChildren();
			progressBar.setString("Processing search request ...");
			progressBar.setIndeterminate(true);			
			progressBar.updateUI();
			setCriteriaList(criteriaPanel.getFilterList());	
			Boolean bln = criteriaPanel.verifyCriteria(criteria);			
			if(bln){
				DicomQuery dicomQuery = new DicomQuery(criteria, loc);
				dicomQuery.addSearchListener(this);
				threadQueryDicom = new Thread(dicomQuery);
				threadQueryDicom.start();
				String studyUID = criteriaPanel.getFilterList().get(TagFromName.StudyInstanceUID).getDelimitedStringValuesOrEmptyString();
				String seriesUID = criteriaPanel.getFilterList().get(TagFromName.SeriesInstanceUID).getDelimitedStringValuesOrEmptyString();
				AVTQuery avtQuery = new AVTQuery(studyUID, seriesUID);
				avtQuery.addAVTListener(this);
				Thread t = new Thread(avtQuery);
				t.start();			
			}else{
				progressBar.setString("");
				progressBar.setIndeterminate(false);
			}																	
		}else if (e.getSource() == btnRetrieve){
			Boolean bln = criteriaPanel.verifyCriteria(retrieveCriteria);
			File importDir = HostConfigurator.getHostConfigurator().getHostTmpDir();
			numOfLocs = 0;
			if(bln && singleAimUID == null){
				progressBar.setString("Processing retrieve request ...");
				progressBar.setIndeterminate(true);
				progressBar.updateUI();	
				criteriaPanel.getQueryButton().setBackground(Color.GRAY);
				criteriaPanel.getQueryButton().setEnabled(false);
				btnRetrieve.setBackground(Color.GRAY);
				btnRetrieve.setEnabled(false);
				totalNumLocs = 2;
				DicomRetrieve dicomRetrieve = new DicomRetrieve(retrieveCriteria, loc, loc);
				dicomRetrieve.addDicomRetrieveListener(this);
				threadRetrieveDicom = new Thread(dicomRetrieve);
				threadRetrieveDicom.start();								
				AVTRetrieve avtRetrieve;
				try {
					avtRetrieve = new AVTRetrieve(selectedStudyInstanceUID, selectedSeriesInstanceUID, importDir);
					avtRetrieve.addAVTListener(this);					
					Thread t2 = new Thread(avtRetrieve);
					t2.start();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}				
			}else if(singleAimUID != null){
				progressBar.setString("Processing retrieve request ...");
				progressBar.setIndeterminate(true);
				progressBar.updateUI();	
				criteriaPanel.getQueryButton().setBackground(Color.GRAY);
				criteriaPanel.getQueryButton().setEnabled(false);
				btnRetrieve.setBackground(Color.GRAY);
				btnRetrieve.setEnabled(false);
				totalNumLocs = 1;
				AVTRetrieve avtRetrieve;
				try {
					avtRetrieve = new AVTRetrieve(singleAimUID, importDir);
					avtRetrieve.addAVTListener(this);
					Thread t = new Thread(avtRetrieve);
					t.start();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}				
			}
		}		
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
        constraints.insets.right = 15;
        constraints.insets.bottom = 5;        
        constraints.anchor = GridBagConstraints.CENTER;
        layout.setConstraints(leftPanel, constraints);        
        
        constraints.fill = GridBagConstraints.NONE;        
        constraints.gridx = 1;
        constraints.gridy = 0;        
        constraints.insets.top = 10;
        constraints.insets.left = 15;
        constraints.insets.right = 20;
        constraints.insets.bottom = 5;        
        constraints.anchor = GridBagConstraints.WEST;
        layout.setConstraints(rightPanel, constraints);
        
        constraints.fill = GridBagConstraints.HORIZONTAL;        
        constraints.gridx = 0;
        constraints.gridy = 1; 
        constraints.gridwidth = 2;
        constraints.insets.top = 10;
        constraints.insets.left = 0;
        constraints.insets.right = 0;
        constraints.insets.bottom = 0;        
        constraints.anchor = GridBagConstraints.CENTER;
        layout.setConstraints(progressBar, constraints);
	}
	
	void buildRightPanelLayout(){
		GridBagLayout layout = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        rightPanel.setLayout(layout);         
                       
        constraints.fill = GridBagConstraints.NONE;        
        constraints.gridx = 0;
        constraints.gridy = 0;        
        constraints.insets.top = 0;
        constraints.insets.left = 0;
        constraints.insets.right = 20;
        constraints.insets.bottom = 5;        
        constraints.anchor = GridBagConstraints.CENTER;
        layout.setConstraints(treeView, constraints);               
        
        constraints.fill = GridBagConstraints.NONE;        
        constraints.gridx = 0;
        constraints.gridy = 1;        
        constraints.insets.top = 10;
        constraints.insets.left = 5;
        constraints.insets.right = 20;
        constraints.insets.bottom = 5;        
        constraints.anchor = GridBagConstraints.CENTER;
        layout.setConstraints(btnRetrieve, constraints);
	}
	
	public static void main(String[] args){
		JFrame frame = new JFrame();
		AVTPanel panel = new AVTPanel();
		frame.add(panel);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);		
	}

	ImageAnnotationDescriptor[] aimDescs;
	public void searchResultsAvailable(AVTSearchEvent e) {
		try {
			//wait until dicom results arrive
			if(threadQueryDicom != null)threadQueryDicom.join();
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		aimDescs = (ImageAnnotationDescriptor[])e.getSource();
		if(aimDescs.length >= 1 && aimDescs[0] != null){
			for(int i = 0; i < aimDescs.length; i++){
				/*System.out.println(i + " " + aimDescs[i].getImageAnnotationType() + " " 
						+ aimDescs[i].getDateTime() + " Rater " + aimDescs[i].getAuthorName());*/
				AIMItem aim = new AIMItem(aimDescs[i].getImageAnnotationType(), aimDescs[i].getDateTime(), 
						aimDescs[i].getAuthorName(), aimDescs[i].getUID());
				String studyInstanceUID = aimDescs[i].getStudyInstanceUID();
				String seriesInstanceUID = aimDescs[i].getSeriesInstanceUID();
				for(int j = 0; j < result.getPatients().size(); j++){
					Patient patient = result.getPatients().get(j);
					List<Study> studies = patient.getStudies();
					for(int k = 0; k < studies.size(); k++){
						if(studies.get(k).getStudyInstanceUID().equalsIgnoreCase(studyInstanceUID)){
							List<Series> series = studies.get(k).getSeries();
							for(int m =0; m < series.size(); m++){
								if(series.get(m).getSeriesInstanceUID().equalsIgnoreCase(seriesInstanceUID)){
									Series s = series.get(m);
									s.addItem(aim);
								}
							}
						}
					}
				}				
			}	
		}				
		if(result == null){			
			resultTree.updateNodes(result);
		}else{
			resultTree.updateNodes(result);			
		}									
		progressBar.setString("ADSearch finished");
		progressBar.setIndeterminate(false);				
	}

	List<File> aimFiles;
	Integer totalNumLocs = 0;
	int numOfLocs = 0;
	@SuppressWarnings("unchecked")
	public void retriveResultsAvailable(AVTRetrieveEvent e) {
		aimFiles = (List<File>) e.getSource();				
		finalizeRetrieve();
	}

	SearchResult result;
	public void searchResultAvailable(SearchEvent e) {
		DicomQuery dicomQuery = (DicomQuery)e.getSource();
		result = dicomQuery.getSearchResult();		        				
	}
	
	List<File> dicomFiles;
	public void retrieveResultAvailable(DicomRetrieveEvent e) {
		DicomRetrieve dicomRetrieve = (DicomRetrieve)e.getSource();
		dicomFiles = dicomRetrieve.getRetrievedFiles();							
		finalizeRetrieve();			
	}
	
	
	synchronized void finalizeRetrieve(){
		numOfLocs++;
		if(numOfLocs == totalNumLocs){
			progressBar.setString("AD Retrieve finished");
			progressBar.setIndeterminate(false);		
			int numberOfDicom = 0;
			int numberOfAim = 0;
			if(dicomFiles != null) numberOfDicom = dicomFiles.size();
			if(aimFiles != null) numberOfAim = aimFiles.size();
			File[] files = new File[numberOfAim + numberOfDicom];
			List<File> allFiles = new ArrayList<File>();
			if(aimFiles != null) allFiles.addAll(aimFiles);
			if(dicomFiles != null) allFiles.addAll(dicomFiles); 
			allFiles.toArray(files);		
			FileManager fileMgr = FileManagerFactory.getInstance();						
	        fileMgr.run(files);	
	        criteriaPanel.getQueryButton().setBackground(xipBtn);
			criteriaPanel.getQueryButton().setEnabled(true);		
			btnRetrieve.setEnabled(true);
			btnRetrieve.setBackground(xipBtn);	
		}		
	}

	String selectedSeriesInstanceUID;
	String selectedStudyInstanceUID;
	String singleAimUID;	
	AttributeList retrieveCriteria;
	public void valueChanged(TreeSelectionEvent e) {				
		DefaultMutableTreeNode node = (DefaultMutableTreeNode)resultTree.getLastSelectedPathComponent();		
		if (node == null) return;				
		if (!node.isRoot()) {																	
			Object selectedNode = node.getUserObject();
			if(selectedNode instanceof Study){
				selectedSeriesInstanceUID = "";			
				selectedStudyInstanceUID = ((Study)node.getUserObject()).getStudyInstanceUID();
				singleAimUID = null;
				btnRetrieve.setEnabled(false);
			}else if(selectedNode instanceof Series){				
				selectedSeriesInstanceUID = ((Series)node.getUserObject()).getSeriesInstanceUID();				 				
				DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) node.getParent();				
				if(parentNode.getUserObject() instanceof Study){
					selectedStudyInstanceUID = ((Study)parentNode.getUserObject()).getStudyInstanceUID();
				}else{
					
				}
				singleAimUID = null;
				btnRetrieve.setBackground(xipBtn);
				btnRetrieve.setForeground(Color.WHITE);
				btnRetrieve.setEnabled(true);
			}else if(selectedNode instanceof ImageItem){				
				selectedSeriesInstanceUID = "";			
				selectedStudyInstanceUID = "";
				singleAimUID = null;
				btnRetrieve.setEnabled(false);
			}else if(selectedNode instanceof AIMItem){				
				selectedSeriesInstanceUID = "";			
				selectedStudyInstanceUID = "";
				singleAimUID = ((AIMItem)selectedNode).getItemID();				
				btnRetrieve.setBackground(xipBtn);
				btnRetrieve.setForeground(Color.WHITE);
				btnRetrieve.setEnabled(true);
			}else{
				btnRetrieve.setEnabled(false);
			}
			retrieveCriteria = new AttributeList();			
			try{				
				if(!selectedStudyInstanceUID.equalsIgnoreCase("")){
					{ AttributeTag t = TagFromName.StudyInstanceUID; Attribute a = new UniqueIdentifierAttribute(t); a.addValue(selectedStudyInstanceUID); retrieveCriteria.put(t,a); }
				}							
				if(!selectedSeriesInstanceUID.equalsIgnoreCase("")){ 
					AttributeTag t = TagFromName.SeriesInstanceUID; Attribute a = new UniqueIdentifierAttribute(t); a.addValue(selectedSeriesInstanceUID); retrieveCriteria.put(t,a); }
				{ AttributeTag t = TagFromName.SOPInstanceUID; Attribute a = new UniqueIdentifierAttribute(t); a.addValue("*"); retrieveCriteria.put(t,a); }
				{ AttributeTag t = TagFromName.QueryRetrieveLevel; Attribute a = new CodeStringAttribute(t); a.addValue("IMAGE"); retrieveCriteria.put(t,a); }
			} catch (DicomException excep){
				
			}
			DicomDictionary dictionary = AttributeList.getDictionary();
		    Iterator iter = dictionary.getTagIterator();        
		    String strAtt = null;
		    String attValue = null;
		    while(iter.hasNext()){
		    	AttributeTag attTag  = (AttributeTag)iter.next();
				strAtt = attTag.toString();									
				attValue = Attribute.getSingleStringValueOrEmptyString(retrieveCriteria, attTag);
				if(!attValue.isEmpty()){
					//System.out.println(strAtt + " " + attValue);				
				}
		    }
			//TODO enable/diable buttons																				
		} else {
			btnRetrieve.setEnabled(false);			
		}					
	}		
}
