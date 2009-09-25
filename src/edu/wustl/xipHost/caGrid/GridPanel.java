/**
 * Copyright (c) 2009 Washington University in St. Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.caGrid;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.ListCellRenderer;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import javax.xml.namespace.QName;
import org.globus.wsrf.encoding.ObjectSerializer;
import org.globus.wsrf.encoding.SerializationException;

import com.pixelmed.dicom.Attribute;
import com.pixelmed.dicom.AttributeList;
import com.pixelmed.dicom.AttributeTag;
import com.pixelmed.dicom.CodeStringAttribute;
import com.pixelmed.dicom.DicomException;
import com.pixelmed.dicom.ShortStringAttribute;
import com.pixelmed.dicom.SpecificCharacterSet;
import com.pixelmed.dicom.TagFromName;

import edu.wustl.xipHost.caGrid.GridUtil;
import edu.wustl.xipHost.dataModel.Patient;
import edu.wustl.xipHost.dataModel.SearchResult;
import edu.wustl.xipHost.dataModel.Series;
import edu.wustl.xipHost.dataModel.Study;
import edu.wustl.xipHost.gui.SearchCriteriaPanel;
import edu.wustl.xipHost.gui.UnderDevelopmentDialog;
import edu.wustl.xipHost.gui.checkboxTree.SearchResultTree;
import edu.wustl.xipHost.localFileSystem.FileManager;
import edu.wustl.xipHost.localFileSystem.FileManagerFactory;
import gov.nih.nci.cagrid.cqlquery.CQLQuery;
import gov.nih.nci.cagrid.data.MalformedQueryException;
import gov.nih.nci.ivi.dicom.HashmapToCQLQuery;
import gov.nih.nci.ivi.dicom.modelmap.ModelMap;
import gov.nih.nci.ivi.dicom.modelmap.ModelMapException;
import gov.nih.nci.ivi.helper.AIMDataServiceHelper;


/**
 * @author Jaroslaw Krych
 *
 */
public class GridPanel extends JPanel implements ActionListener, GridSearchListener, GridRetrieveListener {	

	JPanel locationSelectionPanel = new JPanel();
	JLabel lblTitle = new JLabel("Select caGRID DICOM Service Location:");		
	ImageIcon iconGlobus = new ImageIcon("./gif/applications-internet.png");	
	JLabel lblGlobus = new JLabel(iconGlobus, JLabel.CENTER);		
    DefaultComboBoxModel comboModel;	
	JComboBox list;	
	SearchCriteriaPanel criteriaPanel = new SearchCriteriaPanel();			
	JPanel leftPanel = new JPanel();
	RightPanel rightPanel = new RightPanel();
	SearchResultTree resultTree;
	JProgressBar progressBar = new JProgressBar();	
	Font font_1 = new Font("Tahoma", 0, 13);
	Font font_2 = new Font("Tahoma", 0, 12);		
	Color xipColor = new Color(51, 51, 102);
	Color xipBtn = new Color(56, 73, 150);
	Color xipLightBlue = new Color(156, 162, 189);
	GridManager gridMgr;
	GridSearchListener l;
	
	public GridPanel(){
		l = this;
		setBackground(xipColor);							
		comboModel = new DefaultComboBoxModel();
		list = new JComboBox(comboModel);
		gridMgr = GridManagerFactory.getInstance();
		List<GridLocation> gridTypeDicomLocations = gridMgr.getGridTypeDicomLocations();
		for(int i = 0; i < gridTypeDicomLocations.size(); i++){
			comboModel.addElement(gridTypeDicomLocations.get(i));
		}
		List<GridLocation> gridTypeAimLocations = gridMgr.getGridTypeAimLocations();
		for(int i = 0; i < gridTypeAimLocations.size(); i++){
			rightPanel.comboModel.addElement(gridTypeAimLocations.get(i));
		}		
		ComboBoxRenderer renderer = new ComboBoxRenderer();		
		list.setRenderer(renderer);
		list.setMaximumRowCount(10);
		list.setSelectedIndex(-1);
		list.setPreferredSize(new Dimension(465, 25));
		list.setFont(font_2);
		list.setEditable(false);		
		list.addActionListener(this);
		rightPanel.list.setSelectedIndex(-1);
		rightPanel.list.addActionListener(this);
		rightPanel.btnRetrieve.addActionListener(this);
		resultTree = rightPanel.getGridJTreePanel(); 
		//resultTree.addTreeSelectionListener(this);		
		resultTree.addMouseListener(ml);
		rightPanel.lblGlobus.addMouseListener(
			new MouseAdapter(){
				public void mouseClicked(MouseEvent e){																
					//TODO display and manage LocationsDilog
					/*new LocationsDialog(HostConfigurator.getHostConfigurator().getMainWindow(), gridMgr.getGridTypeAimLocations());					
					comboModel.removeAllElements();
					List<GridLocation> gridTypeDicomLocations = gridMgr.getGridTypeDicomLocations();
					for(int i = 0; i < gridTypeDicomLocations.size(); i++){
						comboModel.addElement(gridTypeDicomLocations.get(i));
					}
					rightPanel.comboModel.removeAllElements();
					List<GridLocation> gridTypeAimLocations = gridMgr.getGridTypeAimLocations();
					for(int i = 0; i < gridTypeAimLocations.size(); i++){
						rightPanel.comboModel.addElement(gridTypeAimLocations.get(i));
					}
					list.update(list.getGraphics());*/
					new UnderDevelopmentDialog(rightPanel.lblGlobus.getLocationOnScreen());
				}
			}
		);
		//rightPanel.cbxAnnot.addItemListener(this);
		Border border1 = BorderFactory.createLoweredBevelBorder();
		list.setBorder(border1);		
		lblTitle.setForeground(Color.WHITE);
		locationSelectionPanel.add(lblTitle);		
		locationSelectionPanel.add(list);
		lblGlobus.setToolTipText("DICOM service locations");
		lblGlobus.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		lblGlobus.addMouseListener(
			new MouseAdapter(){
				public void mouseClicked(MouseEvent e){																					
					//TODO display and manage LocationsDilog
					/*new LocationsDialog(HostConfigurator.getHostConfigurator().getMainWindow(), gridMgr.getGridTypeDicomLocations());					
					comboModel.removeAllElements();
					List<GridLocation> gridTypeDicomLocations = gridMgr.getGridTypeDicomLocations();
					for(int i = 0; i < gridTypeDicomLocations.size(); i++){
						comboModel.addElement(gridTypeDicomLocations.get(i));
					}
					rightPanel.comboModel.removeAllElements();
					List<GridLocation> gridTypeAimLocations = gridMgr.getGridTypeAimLocations();
					for(int i = 0; i < gridTypeAimLocations.size(); i++){
						rightPanel.comboModel.addElement(gridTypeAimLocations.get(i));
					}
					list.update(list.getGraphics());*/
					new UnderDevelopmentDialog(lblGlobus.getLocationOnScreen());
				}
			}
		);
		locationSelectionPanel.add(lblGlobus);		
		locationSelectionPanel.setBackground(xipColor);		
		buildLayoutLocationSelectionPanel();
		
		criteriaPanel.getQueryButton().addActionListener(this);
		criteriaPanel.setQueryButtonText("Search");		
		leftPanel.add(criteriaPanel);
		leftPanel.add(locationSelectionPanel);
		leftPanel.setBackground(xipColor);
		buildLeftPanelLayout();
		add(leftPanel);
		add(rightPanel);
				
		progressBar.setIndeterminate(false);
	    progressBar.setString("");	    
	    progressBar.setStringPainted(true);	    
	    progressBar.setBackground(new Color(156, 162, 189));
	    progressBar.setForeground(xipColor);
	    add(progressBar);
	    	    
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
        constraints.insets.bottom = 5;        
        constraints.anchor = GridBagConstraints.CENTER;
        layout.setConstraints(leftPanel, constraints);        
        
        constraints.fill = GridBagConstraints.NONE;        
        constraints.gridx = 1;
        constraints.gridy = 0;        
        constraints.insets.top = 10;
        constraints.insets.left = 5;
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
	
	void buildLayoutLocationSelectionPanel(){
		GridBagLayout layout = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        locationSelectionPanel.setLayout(layout);         
                       
        constraints.fill = GridBagConstraints.NONE;        
        constraints.gridx = 0;
        constraints.gridy = 0;        
        constraints.insets.top = 10;
        constraints.insets.left = 20;        
        constraints.insets.bottom = 5;        
        constraints.anchor = GridBagConstraints.WEST;
        layout.setConstraints(lblTitle, constraints);
        
        constraints.fill = GridBagConstraints.NONE;        
        constraints.gridx = 0;
        constraints.gridy = 1;        
        constraints.insets.top = 10;
        constraints.insets.left = 20;        
        constraints.insets.bottom = 5;        
        constraints.anchor = GridBagConstraints.WEST;
        layout.setConstraints(list, constraints);
        
        constraints.fill = GridBagConstraints.NONE;        
        constraints.gridx = 1;
        constraints.gridy = 1;        
        constraints.insets.top = 10;
        constraints.insets.left = 20;        
        constraints.insets.bottom = 5;        
        constraints.anchor = GridBagConstraints.WEST;
        layout.setConstraints(lblGlobus, constraints);                
	}	
	
	void buildLeftPanelLayout(){
		GridBagLayout layout = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        leftPanel.setLayout(layout);         
                       
        constraints.fill = GridBagConstraints.NONE;        
        constraints.gridx = 0;
        constraints.gridy = 0;        
        constraints.insets.top = 10;
        constraints.insets.left = 20;
        constraints.insets.right = 10;
        constraints.insets.bottom = 5;        
        constraints.anchor = GridBagConstraints.CENTER;
        layout.setConstraints(locationSelectionPanel, constraints);
        
        constraints.fill = GridBagConstraints.NONE;        
        constraints.gridx = 0;
        constraints.gridy = 1;        
        constraints.insets.top = 10;
        constraints.insets.left = 20;
        constraints.insets.right = 10;
        constraints.insets.bottom = 5;        
        constraints.anchor = GridBagConstraints.CENTER;
        layout.setConstraints(criteriaPanel, constraints); 
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		JFrame frame = new JFrame();
		GridPanel panel = new GridPanel();
		frame.getContentPane().add(panel);
		frame.setVisible(true);
		frame.pack();
	}	

	
	GridLocation selectedGridTypeDicomService;
	GridLocation selectedGridTypeAimService;	
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == list){
			Object item = ((JComboBox)e.getSource()).getSelectedItem();
			selectedGridTypeDicomService = (GridLocation)item;			
		}else if(e.getSource() == rightPanel.list){
			Object item = ((JComboBox)e.getSource()).getSelectedItem();
			selectedGridTypeAimService = (GridLocation)item;			
		}else if(e.getSource() == criteriaPanel.getQueryButton()){
			rightPanel.btnRetrieve.setEnabled(false);
			rightPanel.cbxAnnot.setEnabled(false);
			rightPanel.btnRetrieve.setBackground(Color.GRAY);
			progressBar.setString("Processing search request ...");
			progressBar.setIndeterminate(true);
			progressBar.updateUI();	
			setCriteriaList(criteriaPanel.getFilterList());				
			Boolean bln = criteriaPanel.verifyCriteria(criteria);
			if(bln && selectedGridTypeDicomService != null){											
				GridUtil gridUtil = gridMgr.getGridUtil();
				CQLQuery cql = gridUtil.convertToCQLStatement(criteria, CQLTargetName.PATIENT);	
				//CQLQuery cql = GridUtil.convertToCQLWithNCIAHelper(criteria);				
				GridQuery gridQuery = new GridQuery(cql, selectedGridTypeDicomService);				
				gridQuery.addGridSearchListener(this);
				Thread t = new Thread(gridQuery); 					
				t.start();									
			}else{
				progressBar.setString("");
				progressBar.setIndeterminate(false);
			}
		}else if(e.getSource() == rightPanel.btnRetrieve){
			allRetrivedFiles = new ArrayList<File>();
			numRetrieveThreadsStarted = 0;
			numRetrieveThreadsReturned = 0;
			List<CQLQuery> criteriasDicom = getDicomRetrieveCriteria2();
			List<CQLQuery> criteriasAim = getAimRetrieveCriteria();	
			//number of criteriaDicom and criteriaAim should be equal 
			int numOfCriterias = criteriasDicom.size();
			if( numOfCriterias > 0){
				progressBar.setString("Processing retrieve request ...");
				progressBar.setIndeterminate(true);
				progressBar.updateUI();	
				criteriaPanel.getQueryButton().setBackground(Color.GRAY);
				criteriaPanel.getQueryButton().setEnabled(false);
				rightPanel.btnRetrieve.setBackground(Color.GRAY);
				rightPanel.btnRetrieve.setEnabled(false);
				rightPanel.cbxAnnot.setEnabled(false);												
				for(int i = 0; i < criteriasDicom.size(); i++){
					CQLQuery cqlQuery = criteriasDicom.get(i);
					CQLQuery aimCQL = criteriasAim.get(i);	
					try {
						//Retrieve Dicom
						GridRetrieve gridRetrieve = new GridRetrieve(cqlQuery, selectedGridTypeDicomService, gridMgr.getImportDirectory());
						gridRetrieve.addGridRetrieveListener(this);						
						Thread t1 = new Thread(gridRetrieve);
						t1.start();
						numRetrieveThreadsStarted++;
						//Retrieve AIM
						if(rightPanel.cbxAnnot.isSelected() && selectedGridTypeAimService != null){							
								AimRetrieve aimRetrieve = new AimRetrieve(aimCQL, selectedGridTypeAimService, gridMgr.getImportDirectory());
								aimRetrieve.addGridRetrieveListener(this);
								Thread t2 = new Thread(aimRetrieve);
								t2.start();
								numRetrieveThreadsStarted++;
						} 
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
						//TODO
						//Set to mumber of threads to terminate retrieve
					}																
				}				
			}						
		}
	}	
	
	AttributeList criteria;	
	void setCriteriaList(AttributeList criteria){
		this.criteria = criteria;
	}
	//getCriteria, map criteria to NCIA Model Map, verify criteria	
	
	
	class ComboBoxRenderer extends JLabel implements ListCellRenderer {
		DefaultListCellRenderer defaultRenderer = new DefaultListCellRenderer();
		Dimension preferredSize = new Dimension(440, 15);
		public Component getListCellRendererComponent(JList list, Object value, int index,
			      boolean isSelected, boolean cellHasFocus) {
			    JLabel renderer = (JLabel) defaultRenderer.getListCellRendererComponent(list, value, index,
			        isSelected, cellHasFocus);
			    if (value instanceof GridLocation) {
			    	renderer.setText(((GridLocation)value).getShortName());
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
	
	SearchResult result;
	public void searchResultAvailable(GridSearchEvent e) {
		progressBar.setString("GridSearch finished");
		progressBar.setIndeterminate(false);
		if(e.getSource().getClass() == GridQuery.class){
			GridQuery gridQuery = (GridQuery)e.getSource();
			result = gridQuery.getSearchResult();			
		}else if(e.getSource().getClass() == GridQueryNBIA.class){
			GridQueryNBIA gridQuery = (GridQueryNBIA)e.getSource();
			result = gridQuery.getSearchResult();			
		}			
		if(selectedNode instanceof Patient){
			rightPanel.getGridJTreePanel().updateNodes(result);
		}else{
			rightPanel.getGridJTreePanel().updateNodes(result);
		}
	}
	
	
	List<CQLQuery> getDicomRetrieveCriteria2() {
		List<CQLQuery> retrieveCriterias = new ArrayList<CQLQuery>();
		Map<Series, Study> map = resultTree.getSelectedSeries();
		Set<Series> seriesSet = map.keySet();
		Iterator<Series> iter = seriesSet.iterator();
		while (iter.hasNext()){
			Series series = iter.next();
			String selectedSeriesInstanceUID = series.getSeriesInstanceUID();			
			String selectedStudyInstanceUID = ((Study)map.get(series)).getStudyInstanceUID();
			CQLQuery cqlQuery = null;	
			GridUtil gridUtil = gridMgr.getGridUtil();
			AttributeList attList = new AttributeList();
			try {
				String[] characterSets = { "ISO_IR 100" };
				SpecificCharacterSet specificCharacterSet = new SpecificCharacterSet(characterSets);			
				{ AttributeTag t = TagFromName.StudyInstanceUID; Attribute a = new ShortStringAttribute(t,specificCharacterSet); a.addValue(selectedStudyInstanceUID); attList.put(t,a); }
				{ AttributeTag t = TagFromName.SeriesInstanceUID; Attribute a = new ShortStringAttribute(t,specificCharacterSet); a.addValue(selectedSeriesInstanceUID); attList.put(t,a); }
				{ AttributeTag t = TagFromName.SpecificCharacterSet; Attribute a = new CodeStringAttribute(t); a.addValue(characterSets[0]); attList.put(t,a); }			
			}
			catch (Exception e) {
				e.printStackTrace(System.err);			
			}
			cqlQuery = gridUtil.convertToCQLStatement(attList, CQLTargetName.SERIES);
			retrieveCriterias.add(cqlQuery);
		}
		return retrieveCriterias;
	}
	
	
	List<CQLQuery> getDicomRetrieveCriteria() {
		List<CQLQuery> retrieveCriterias = new ArrayList<CQLQuery>();
		Map<Series, Study> map = resultTree.getSelectedSeries();
		Set<Series> seriesSet = map.keySet();
		Iterator<Series> iter = seriesSet.iterator();
		while (iter.hasNext()){
			Series series = iter.next();
			String selectedSeriesInstanceUID = series.getSeriesInstanceUID();			
			String selectedStudyInstanceUID = ((Study)map.get(series)).getStudyInstanceUID();
			//Create CQL statement for retrieve
			HashMap<String, String> query = new HashMap<String, String>();						
			if(selectedStudyInstanceUID != null && selectedSeriesInstanceUID != null){
				query.put(HashmapToCQLQuery.TARGET_NAME_KEY, gov.nih.nci.ncia.domain.Series.class.getCanonicalName());
				query.put("gov.nih.nci.ncia.domain.Study.studyInstanceUID", selectedStudyInstanceUID);
				query.put("gov.nih.nci.ncia.domain.Series.seriesInstanceUID", selectedSeriesInstanceUID);
			}else if(selectedStudyInstanceUID != null && selectedSeriesInstanceUID == null){				
				query.put(HashmapToCQLQuery.TARGET_NAME_KEY, gov.nih.nci.ncia.domain.Study.class.getCanonicalName());
				query.put("gov.nih.nci.ncia.domain.Study.studyInstanceUID", selectedStudyInstanceUID);
			}else{
				
			}
			/* Convert hash map to SQL */
			HashmapToCQLQuery h2cql;
			CQLQuery cqlQuery = null;	
			try {
				h2cql = new HashmapToCQLQuery(new ModelMap());							
				cqlQuery = h2cql.makeCQLQuery(query);
				/*System.err.println(ObjectSerializer.toString(cqlQuery, 
						new QName("http://CQL.caBIG/1/gov.nih.nci.cagrid.CQLQuery", "CQLQuery")));*/
				retrieveCriterias.add(cqlQuery);
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (ModelMapException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (ClassNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (MalformedQueryException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}																				
		}
		return retrieveCriterias;																													
	}
	
	List<CQLQuery> getAimRetrieveCriteria() {
		List<CQLQuery> retrieveCriterias = new ArrayList<CQLQuery>();
		Map<Series, Study> map = resultTree.getSelectedSeries();
		Set<Series> seriesSet = map.keySet();
		Iterator<Series> iter = seriesSet.iterator();
		while (iter.hasNext()){
			Series series = iter.next();
			String selectedSeriesInstanceUID = series.getSeriesInstanceUID();			
			String selectedStudyInstanceUID = ((Study)map.get(series)).getStudyInstanceUID();			
			CQLQuery aimCQL = AIMDataServiceHelper.generateImageAnnotationQuery(selectedStudyInstanceUID, selectedSeriesInstanceUID, null);;
			retrieveCriterias.add(aimCQL);
		}
		return retrieveCriterias;
	}
	
	Object selectedNode;
	MouseListener ml = new MouseAdapter(){
	     public void mousePressed(MouseEvent e) {
	    	 if(resultTree.getSelectedSeries().size() > 0){
	    		rightPanel.btnRetrieve.setEnabled(true);	    		 	    			
	 			rightPanel.btnRetrieve.setBackground(xipBtn);
	 			//rightPanel.btnRetrieve.setForeground(Color.WHITE);
	 			rightPanel.btnRetrieve.setForeground(Color.BLACK);
	 			rightPanel.cbxAnnot.setEnabled(true);
	    	 }else{
	    		rightPanel.btnRetrieve.setEnabled(false);	    		 	    		 
	 			rightPanel.cbxAnnot.setEnabled(false);
	 			rightPanel.btnRetrieve.setBackground(Color.GRAY);
	    	 }
	     }
	     
	     public void mouseClicked(MouseEvent e) {	        
		        if (e.getClickCount() == 2) {
		        	int x = e.getX();
			     	int y = e.getY();
			     	int row = resultTree.getRowForLocation(x, y);
			     	TreePath  path = resultTree.getPathForRow(row);    	
			     	if (path != null) {    		
			     		DefaultMutableTreeNode node = (DefaultMutableTreeNode)resultTree.getLastSelectedPathComponent();							
			     		//System.out.println(this.getRowForPath(new TreePath(node.getPath())));
			     		//System.out.println("Checking set changed, leading path: " + e.getPath().toString());			    
			     		if (node == null) return;		 
			     		if (!node.isRoot()) {																	
			     			selectedNode = node.getUserObject();
			     			if(selectedNode instanceof Patient){
			     				Patient selectedPatient = Patient.class.cast(selectedNode);
			     				//System.out.println(selectedNode.toString());
			     				//Retrieve studies for selected patient
			     				rightPanel.btnRetrieve.setEnabled(false);
			     				rightPanel.cbxAnnot.setEnabled(false);
			     				rightPanel.btnRetrieve.setBackground(Color.GRAY);
			     				progressBar.setString("Processing search request ...");
			     				progressBar.setIndeterminate(true);
			     				progressBar.updateUI();	
			     				AttributeList initialCriteria = criteriaPanel.getFilterList();
			     				AttributeList updatedCriteria = initialCriteria;
			     				String[] characterSets = { "ISO_IR 100" };
			     				SpecificCharacterSet specificCharacterSet = new SpecificCharacterSet(characterSets);
			     				{ AttributeTag t = TagFromName.PatientID; Attribute a = new ShortStringAttribute(t,specificCharacterSet); 
			     				try {
									a.addValue(selectedPatient.getPatientID());
								} catch (DicomException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								} updatedCriteria.put(t,a); }
			     				setCriteriaList(updatedCriteria);				
			     				Boolean bln = criteriaPanel.verifyCriteria(updatedCriteria);
			     				if(bln && selectedGridTypeDicomService != null){											
			     					GridUtil gridUtil = gridMgr.getGridUtil();
			     					CQLQuery cql = gridUtil.convertToCQLStatement(updatedCriteria, CQLTargetName.STUDY);	
			     					//CQLQuery cql = GridUtil.convertToCQLWithNCIAHelper(criteria);				
			     					GridQueryNBIA gridQuery = new GridQueryNBIA(cql, selectedGridTypeDicomService, result, selectedNode);
			     					gridQuery.addGridSearchListener(l);
			     					Thread t = new Thread(gridQuery); 					
			     					t.start();									
			     				}else{
			     					progressBar.setString("");
			     					progressBar.setIndeterminate(false);
			     				}			     				
			     				repaint();
			     			}else if(selectedNode instanceof Study){
			     				Study selectedStudy = Study.class.cast(selectedNode);
			     				//System.out.println(selectedNode.toString());
			     				//Retrieve studies for selected patient
			     				rightPanel.btnRetrieve.setEnabled(false);
			     				rightPanel.cbxAnnot.setEnabled(false);
			     				rightPanel.btnRetrieve.setBackground(Color.GRAY);
			     				progressBar.setString("Processing search request ...");
			     				progressBar.setIndeterminate(true);
			     				progressBar.updateUI();	
			     				AttributeList initialCriteria = criteriaPanel.getFilterList();
			     				AttributeList updatedCriteria = initialCriteria;
			     				String[] characterSets = { "ISO_IR 100" };
			     				SpecificCharacterSet specificCharacterSet = new SpecificCharacterSet(characterSets);
			     				{ AttributeTag t = TagFromName.StudyInstanceUID; Attribute a = new ShortStringAttribute(t,specificCharacterSet); 
			     				try {
									a.addValue(selectedStudy.getStudyInstanceUID());
								} catch (DicomException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								} updatedCriteria.put(t,a); }
			     				setCriteriaList(updatedCriteria);				
			     				Boolean bln = criteriaPanel.verifyCriteria(updatedCriteria);
			     				if(bln && selectedGridTypeDicomService != null){											
			     					GridUtil gridUtil = gridMgr.getGridUtil();
			     					CQLQuery cql = gridUtil.convertToCQLStatement(updatedCriteria, CQLTargetName.SERIES);	
			     					//CQLQuery cql = GridUtil.convertToCQLWithNCIAHelper(criteria);				
			     					GridQueryNBIA gridQuery = new GridQueryNBIA(cql, selectedGridTypeDicomService, result, selectedNode);
			     					gridQuery.addGridSearchListener(l);
			     					Thread t = new Thread(gridQuery); 					
			     					t.start();									
			     				}else{
			     					progressBar.setString("");
			     					progressBar.setIndeterminate(false);
			     				}			     				
			     				repaint();
			     			}
			     		}
			     	}
		        } 
		    }
	};	
	
	//TODO check if allRetrieveFiles must be synchronized
	List<File> allRetrivedFiles;
	int numRetrieveThreadsStarted;
	int numRetrieveThreadsReturned;
	public void importedFilesAvailable(GridRetrieveEvent e) {
		if(e.getSource() instanceof GridRetrieve){
			GridRetrieve dicomRetrieve = (GridRetrieve)e.getSource();
			List<File> result = dicomRetrieve.getRetrievedFiles();
			allRetrivedFiles.addAll(result);
		}else if(e.getSource() instanceof AimRetrieve){
			AimRetrieve aimRetrieve = (AimRetrieve)e.getSource();
			List<File> result = aimRetrieve.getRetrievedFiles();
			if(result != null){
				allRetrivedFiles.addAll(result);
			}
		}		
		numRetrieveThreadsReturned++;
		if(numRetrieveThreadsStarted == numRetrieveThreadsReturned){
			progressBar.setString("GridRetrieve finished");
			progressBar.setIndeterminate(false);			
			criteriaPanel.getQueryButton().setBackground(xipBtn);
			criteriaPanel.getQueryButton().setEnabled(true);
			rightPanel.btnRetrieve.setEnabled(true);
			rightPanel.btnRetrieve.setBackground(xipBtn);						
			File[] files = new File[allRetrivedFiles.size()];
			allRetrivedFiles.toArray(files);		
			FileManager fileMgr = FileManagerFactory.getInstance();						
	        fileMgr.run(files);
		}			
	}	
}

