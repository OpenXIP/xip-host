/**
 * Copyright (c) 2008 Washington University in Saint Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.dicom;

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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import com.pixelmed.dicom.Attribute;
import com.pixelmed.dicom.AttributeList;
import com.pixelmed.dicom.AttributeTag;
import com.pixelmed.dicom.CodeStringAttribute;
import com.pixelmed.dicom.DicomException;
import com.pixelmed.dicom.TagFromName;
import com.pixelmed.dicom.UniqueIdentifierAttribute;
import edu.wustl.xipHost.dataModel.SearchResult;
import edu.wustl.xipHost.dataModel.Series;
import edu.wustl.xipHost.dataModel.Study;
import edu.wustl.xipHost.gui.SearchCriteriaPanel;
import edu.wustl.xipHost.gui.UnderDevelopmentDialog;
import edu.wustl.xipHost.gui.checkboxTree.SearchResultTree;
import edu.wustl.xipHost.hostControl.HostConfigurator;
import edu.wustl.xipHost.localFileSystem.FileManager;
import edu.wustl.xipHost.localFileSystem.FileManagerFactory;

/**
 * @author Jaroslaw Krych
 *
 */
public class DicomPanel extends JPanel implements ActionListener, SearchListener, DicomRetrieveListener {
	JPanel calledLocationSelectionPanel = new JPanel();
	JLabel lblTitle = new JLabel("Select Called DICOM Service Location:");		
	ImageIcon iconGlobus = new ImageIcon("./gif/applications-internet.png");	
	JLabel lblGlobus = new JLabel(iconGlobus, JLabel.CENTER);		
	JPanel leftPanel = new JPanel();
	JPanel rightPanel = new JPanel();
	JComboBox listCalledLocations;	
	DefaultComboBoxModel comboModel;
	JPanel callingLocationSelectionPanel = new JPanel();		
	JLabel lblTitle2 = new JLabel("Retrieve to / Calling DICOM Location:");
	JLabel lblGlobus2 = new JLabel(iconGlobus, JLabel.CENTER);	
	JComboBox listCallingLocations;
	DefaultComboBoxModel comboModelCalling;
	SearchCriteriaPanel criteriaPanel = new SearchCriteriaPanel();
	SearchResultTree resultTree = new SearchResultTree();
	JScrollPane treeView = new JScrollPane(resultTree);   	
	JButton btnRetrieve;
	
	JProgressBar progressBar = new JProgressBar();
	
	Font font_1 = new Font("Tahoma", 0, 13);
	Font font_2 = new Font("Tahoma", 0, 12);		
	Color xipColor = new Color(51, 51, 102);
	Color xipBtn = new Color(56, 73, 150);
	Color xipLightBlue = new Color(156, 162, 189);
	Border border = BorderFactory.createLoweredBevelBorder();
	
	DicomManager dicomMgr;
	
	public DicomPanel(){
		setBackground(xipColor);			
		comboModel = new DefaultComboBoxModel();
		listCalledLocations = new JComboBox(comboModel);
		dicomMgr = DicomManagerFactory.getInstance();
		List<PacsLocation> pacsLocs = dicomMgr.getPacsLocations();
		for(int i = 0; i < pacsLocs.size(); i++){
			comboModel.addElement(pacsLocs.get(i));
		}
		ComboBoxRenderer renderer = new ComboBoxRenderer();		
		listCalledLocations.setRenderer(renderer);
		listCalledLocations.setMaximumRowCount(10);
		listCalledLocations.setSelectedIndex(-1);
		listCalledLocations.setPreferredSize(new Dimension(465, 25));
		listCalledLocations.setFont(font_2);
		listCalledLocations.setEditable(false);		
		listCalledLocations.addActionListener(this);
		
		comboModelCalling = new DefaultComboBoxModel();
		listCallingLocations = new JComboBox(comboModelCalling);
		for(int i = 0; i < pacsLocs.size(); i++){
			comboModelCalling.addElement(pacsLocs.get(i));
		}
		listCallingLocations.setRenderer(renderer);
		listCallingLocations.setMaximumRowCount(10);
		listCallingLocations.setSelectedIndex(-1);
		listCallingLocations.setPreferredSize(new Dimension(465, 25));
		listCallingLocations.setFont(font_2);
		listCallingLocations.setEditable(false);		
		listCallingLocations.addActionListener(this);
		
		lblTitle.setForeground(Color.WHITE);
		lblTitle2.setForeground(Color.WHITE);
		calledLocationSelectionPanel.add(lblTitle);		
		calledLocationSelectionPanel.add(listCalledLocations);
		callingLocationSelectionPanel.add(lblTitle2);
		callingLocationSelectionPanel.add(listCallingLocations);
		lblGlobus.setToolTipText("DICOM service locations");
		lblGlobus.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		lblGlobus.addMouseListener(
			new MouseAdapter(){
				public void mouseClicked(MouseEvent e){																
					//System.out.println("Display locations");
					//displayLocations();
					new UnderDevelopmentDialog(lblGlobus.getLocationOnScreen());
				}
			}
		);
		calledLocationSelectionPanel.add(lblGlobus);		
		calledLocationSelectionPanel.setBackground(xipColor);
		callingLocationSelectionPanel.add(lblGlobus2);
		callingLocationSelectionPanel.setBackground(xipColor);
		buildLayoutLocationSelectionPanel();
		lblGlobus2.setToolTipText("DICOM service locations");
		lblGlobus2.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		lblGlobus2.addMouseListener(
			new MouseAdapter(){
				public void mouseClicked(MouseEvent e){																
					/*System.out.println("Display calling locations");
					dicomMgr.submit(null, null);*/
					new UnderDevelopmentDialog(lblGlobus2.getLocationOnScreen());
				}
			}
		);
		criteriaPanel.getQueryButton().addActionListener(this);			
		criteriaPanel.setQueryButtonText("Query");		
		leftPanel.add(calledLocationSelectionPanel);
		leftPanel.add(criteriaPanel);							    
	    //resultTree.addTreeSelectionListener(this);
	    resultTree.addMouseListener(ml);
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
        buildLayoutCallingLocationSelectionPanel();
        rightPanel.add(callingLocationSelectionPanel);        
        rightPanel.add(btnRetrieve);
        leftPanel.setBackground(xipColor);
        rightPanel.setBackground(xipColor);
        buildLeftPanelLayout();
        buildRightPanelLayout();
				
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
        constraints.insets.right = 10;
        constraints.insets.bottom = 5;        
        constraints.anchor = GridBagConstraints.CENTER;
        layout.setConstraints(leftPanel, constraints);
        
        constraints.fill = GridBagConstraints.HORIZONTAL;        
        constraints.gridx = 1;
        constraints.gridy = 0;         
        constraints.insets.top = 10;
        constraints.insets.left = 0;
        constraints.insets.right = 20;
        constraints.insets.bottom = 5;        
        constraints.anchor = GridBagConstraints.CENTER;
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
	
	void buildLeftPanelLayout(){
		GridBagLayout layout = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        leftPanel.setLayout(layout);         
                       
        constraints.fill = GridBagConstraints.NONE;        
        constraints.gridx = 0;
        constraints.gridy = 0;        
        constraints.insets.top = 10;
        constraints.insets.left = 5;
        constraints.insets.right = 10;
        constraints.insets.bottom = 5;        
        constraints.anchor = GridBagConstraints.CENTER;
        layout.setConstraints(calledLocationSelectionPanel, constraints);
        
        constraints.fill = GridBagConstraints.NONE;        
        constraints.gridx = 0;
        constraints.gridy = 1;        
        constraints.insets.top = 10;
        constraints.insets.left = 5;
        constraints.insets.right = 10;
        constraints.insets.bottom = 5;        
        constraints.anchor = GridBagConstraints.CENTER;
        layout.setConstraints(criteriaPanel, constraints); 
	}
	
	
	void buildRightPanelLayout(){
		GridBagLayout layout = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        rightPanel.setLayout(layout);         
                       
        constraints.fill = GridBagConstraints.NONE;        
        constraints.gridx = 0;
        constraints.gridy = 0;        
        constraints.insets.top = 30;
        constraints.insets.left = 0;
        constraints.insets.right = 20;
        constraints.insets.bottom = 5;        
        constraints.anchor = GridBagConstraints.CENTER;
        layout.setConstraints(treeView, constraints);
        
        constraints.fill = GridBagConstraints.NONE;        
        constraints.gridx = 0;
        constraints.gridy = 1;        
        constraints.insets.top = 5;
        constraints.insets.left = 0;
        constraints.insets.right = 20;
        constraints.insets.bottom = 5;        
        constraints.anchor = GridBagConstraints.CENTER;
        layout.setConstraints(callingLocationSelectionPanel, constraints);   
        
        constraints.fill = GridBagConstraints.NONE;        
        constraints.gridx = 0;
        constraints.gridy = 2;        
        constraints.insets.top = 5;
        constraints.insets.left = 5;
        constraints.insets.right = 20;
        constraints.insets.bottom = 5;        
        constraints.anchor = GridBagConstraints.CENTER;
        layout.setConstraints(btnRetrieve, constraints);
	}
	
	void buildLayoutLocationSelectionPanel(){
		GridBagLayout layout = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        calledLocationSelectionPanel.setLayout(layout);         
                       
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
        layout.setConstraints(listCalledLocations, constraints);
        
        constraints.fill = GridBagConstraints.NONE;        
        constraints.gridx = 1;
        constraints.gridy = 1;        
        constraints.insets.top = 10;
        constraints.insets.left = 20;        
        constraints.insets.bottom = 5;        
        constraints.anchor = GridBagConstraints.WEST;
        layout.setConstraints(lblGlobus, constraints);                
	}
	
	void buildLayoutCallingLocationSelectionPanel(){
		GridBagLayout layout = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        callingLocationSelectionPanel.setLayout(layout);         
                       
        constraints.fill = GridBagConstraints.NONE;        
        constraints.gridx = 0;
        constraints.gridy = 0;        
        constraints.insets.top = 10;
        constraints.insets.left = 20;        
        constraints.insets.bottom = 5;        
        constraints.anchor = GridBagConstraints.WEST;
        layout.setConstraints(lblTitle2, constraints);
        
        constraints.fill = GridBagConstraints.NONE;        
        constraints.gridx = 0;
        constraints.gridy = 1;        
        constraints.insets.top = 10;
        constraints.insets.left = 20;        
        constraints.insets.bottom = 5;        
        constraints.anchor = GridBagConstraints.WEST;
        layout.setConstraints(listCallingLocations, constraints);
        
        constraints.fill = GridBagConstraints.NONE;        
        constraints.gridx = 1;
        constraints.gridy = 1;        
        constraints.insets.top = 10;
        constraints.insets.left = 20;        
        constraints.insets.bottom = 5;        
        constraints.anchor = GridBagConstraints.WEST;
        layout.setConstraints(lblGlobus2, constraints);                
	}	
	
	
	public static void main(String[] args) {
		JFrame frame = new JFrame();
		DicomPanel panel = new DicomPanel();
		frame.getContentPane().add(panel);
		frame.setVisible(true);
		frame.pack();

	}

	PacsLocation calledPacsLocation;
	PacsLocation callingPacsLocation;
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == listCalledLocations){
			Object item = ((JComboBox)e.getSource()).getSelectedItem();
			calledPacsLocation = (PacsLocation)item;			
		}else if(e.getSource() == listCallingLocations){
			Object item = ((JComboBox)e.getSource()).getSelectedItem();
			callingPacsLocation = (PacsLocation)item;
		}else if(e.getSource() == criteriaPanel.getQueryButton()){
			resultTree.rootNode.removeAllChildren();
			progressBar.setString("Processing search request ...");
			progressBar.setIndeterminate(true);
			progressBar.updateUI();	
			setCriteriaList(criteriaPanel.getFilterList());				
			Boolean bln = criteriaPanel.verifyCriteria(criteria);
			if(bln && calledPacsLocation != null){
				DicomQuery dicomQuery = new DicomQuery(criteria, calledPacsLocation);
				dicomQuery.addSearchListener(this);
				Thread t = new Thread(dicomQuery);				
				t.start();				
			}else{
				progressBar.setString("");
				progressBar.setIndeterminate(false);
			}
		}else if(e.getSource() == btnRetrieve){			
			allRetrivedFiles = new ArrayList<File>();
			numRetrieveThreadsStarted = 0;
			numRetrieveThreadsReturned = 0;
			List<AttributeList> criterias = getRetrieveCriteria();
			if(criterias.size() > 0 && calledPacsLocation != null && callingPacsLocation != null){
				progressBar.setString("Processing retrieve request ...");
				progressBar.setIndeterminate(true);
				progressBar.updateUI();	
				criteriaPanel.getQueryButton().setBackground(Color.GRAY);
				criteriaPanel.getQueryButton().setEnabled(false);
				btnRetrieve.setBackground(Color.GRAY);
				btnRetrieve.setEnabled(false);												
				for(int i = 0; i < criterias.size(); i++){
					DicomRetrieve dicomRetrieve = new DicomRetrieve(criterias.get(i), calledPacsLocation, callingPacsLocation);
					dicomRetrieve.addDicomRetrieveListener(this);
					Thread t = new Thread(dicomRetrieve);
					t.start();
					numRetrieveThreadsStarted++;
				}				
			}			
		}		
	}

	AttributeList criteria;	
	void setCriteriaList(AttributeList criteria){
		this.criteria = criteria;
	}
	
	SearchResult result;
	public void searchResultAvailable(SearchEvent e) {		
		DicomQuery dicomQuery = (DicomQuery)e.getSource();
		result = dicomQuery.getSearchResult();		        
		if(result == null){			
			resultTree.updateNodes(result);
		}else{
			resultTree.updateNodes(result);			
		}							
		progressBar.setString("DicomSearch finished");
		progressBar.setIndeterminate(false);		
	}
	

	List<File> allRetrivedFiles;
	int numRetrieveThreadsStarted;
	int numRetrieveThreadsReturned;
	public void retrieveResultAvailable(DicomRetrieveEvent e) {
		//check if all retrieve calls returned
		DicomRetrieve dicomRetrieve = (DicomRetrieve)e.getSource();
		List<File> result = dicomRetrieve.getRetrievedFiles();
		allRetrivedFiles.addAll(result);
		numRetrieveThreadsReturned++;
		if(numRetrieveThreadsStarted == numRetrieveThreadsReturned){
			progressBar.setString("DicomRetrieve finished");
			progressBar.setIndeterminate(false);
			criteriaPanel.getQueryButton().setBackground(xipBtn);
			criteriaPanel.getQueryButton().setEnabled(true);		
			btnRetrieve.setEnabled(true);
			btnRetrieve.setBackground(xipBtn);
			File[] files = new File[allRetrivedFiles.size()];
			allRetrivedFiles.toArray(files);		
			FileManager fileMgr = FileManagerFactory.getInstance();						
	        fileMgr.run(files);
		}		       	
	}
	
	class ComboBoxRenderer extends JLabel implements ListCellRenderer {
		DefaultListCellRenderer defaultRenderer = new DefaultListCellRenderer();
		Dimension preferredSize = new Dimension(440, 15);
		public Component getListCellRendererComponent(JList list, Object value, int index,
			      boolean isSelected, boolean cellHasFocus) {
			    JLabel renderer = (JLabel) defaultRenderer.getListCellRendererComponent(list, value, index,
			        isSelected, cellHasFocus);
			    if (value instanceof PacsLocation) {
			    	renderer.setText(((PacsLocation)value).getShortName());
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
	
	List<AttributeList> getRetrieveCriteria() {
		List<AttributeList> retrieveCriterias = new ArrayList<AttributeList>();
		Map<Series, Study> map = resultTree.getSelectedSeries();
		Set<Series> seriesSet = map.keySet();
		Iterator<Series> iter = seriesSet.iterator();
		while (iter.hasNext()){
			Series series = iter.next();
			String selectedSeriesInstanceUID = series.getSeriesInstanceUID();			
			String selectedStudyInstanceUID = ((Study)map.get(series)).getStudyInstanceUID();
			AttributeList retrieveCriteria = new AttributeList();
			try{				
				if(!selectedStudyInstanceUID.equalsIgnoreCase("")){
					{ AttributeTag t = TagFromName.StudyInstanceUID; Attribute a = new UniqueIdentifierAttribute(t); a.addValue(selectedStudyInstanceUID); retrieveCriteria.put(t,a); }
				}							
				if(!selectedSeriesInstanceUID.equalsIgnoreCase("")){ 
				  AttributeTag t = TagFromName.SeriesInstanceUID; Attribute a = new UniqueIdentifierAttribute(t); a.addValue(selectedSeriesInstanceUID); retrieveCriteria.put(t,a); }
				{ AttributeTag t = TagFromName.SOPInstanceUID; Attribute a = new UniqueIdentifierAttribute(t); a.addValue("*"); retrieveCriteria.put(t,a); }
				{ AttributeTag t = TagFromName.QueryRetrieveLevel; Attribute a = new CodeStringAttribute(t); a.addValue("IMAGE"); retrieveCriteria.put(t,a); }
				/*DicomDictionary dictionary = AttributeList.getDictionary();
			    Iterator iter = dictionary.getTagIterator();        
			    String strAtt = null;
			    String attValue = null;
			    while(iter.hasNext()){
			    	AttributeTag attTag  = (AttributeTag)iter.next();
					strAtt = attTag.toString();									
					attValue = Attribute.getSingleStringValueOrEmptyString(retrieveCriteria, attTag);
					if(!attValue.isEmpty()){
						System.out.println(strAtt + " " + attValue);				
					}
			    }*/		
				if(criteriaPanel.verifyCriteria(retrieveCriteria)){
					retrieveCriterias.add(retrieveCriteria);
				}				
			} catch (DicomException excep){
				
			}			
		}
		return retrieveCriterias;																													
	}
	
	MouseListener ml = new MouseAdapter() {
	     public void mousePressed(MouseEvent e) {
	    	 if(resultTree.getSelectedSeries().size() > 0){
	    		 btnRetrieve.setEnabled(true);
	    	 }else{
	    		 btnRetrieve.setEnabled(false);
	    	 }
	     }
	};
}
