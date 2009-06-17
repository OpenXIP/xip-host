package edu.wustl.xipHost.avt;

import java.awt.Color;
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
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.border.Border;
import com.pixelmed.dicom.AttributeList;
import edu.wustl.xipHost.dataModel.SearchResult;
import edu.wustl.xipHost.dataModel.Series;
import edu.wustl.xipHost.dataModel.Study;
import edu.wustl.xipHost.dicom.DicomUtil;
import edu.wustl.xipHost.gui.SearchCriteriaPanel;
import edu.wustl.xipHost.gui.AVTSearchCriteriaVerifier;
import edu.wustl.xipHost.gui.checkboxTree.SearchResultTree;
import edu.wustl.xipHost.hostControl.HostConfigurator;
import edu.wustl.xipHost.localFileSystem.FileManager;
import edu.wustl.xipHost.localFileSystem.FileManagerFactory;

public class AVTPanel extends JPanel implements ActionListener, AVTListener {
	SearchCriteriaPanel criteriaPanel = new SearchCriteriaPanel();
	SearchResultTree resultTree = new SearchResultTree();
	JScrollPane treeView = new JScrollPane(resultTree);
	JPanel leftPanel = new JPanel();
	JPanel rightPanel = new JPanel();
	JProgressBar progressBar = new JProgressBar();	
	//Color xipColor = new Color(51, 51, 102);
	//Color xipBtn = new Color(56, 73, 150);
	//Color xipLightBlue = new Color(156, 162, 189);
	//Font font_1 = new Font("Tahoma", 0, 13);
	Border border = BorderFactory.createLoweredBevelBorder();		
	JButton btnRetrieve;	
	
	public AVTPanel(){
		//setBackground(xipColor);		
		criteriaPanel.setSearchCriteriaVerifier( new AVTSearchCriteriaVerifier());
		criteriaPanel.getQueryButton().addActionListener(this);
		criteriaPanel.setQueryButtonText("Search AD");
		leftPanel.add(criteriaPanel);			    
	    //resultTree.addTreeSelectionListener(this);
		resultTree.addMouseListener(ml);
		treeView.setPreferredSize(new Dimension(500, HostConfigurator.adjustForResolution()));
		treeView.setBorder(border);	
		btnRetrieve = new JButton("Retrieve");
        //btnRetrieve.setFont(font_1); 
        btnRetrieve.setFocusable(true);
		btnRetrieve.setEnabled(false);				
		//btnRetrieve.setBackground(xipBtn);
//		btnRetrieve.setForeground(Color.WHITE);
		btnRetrieve.setPreferredSize(new Dimension(115, 25));
		btnRetrieve.addActionListener(this);
		rightPanel.add(treeView);
		rightPanel.add(btnRetrieve);
		//leftPanel.setBackground(xipColor);
		//rightPanel.setBackground(xipColor);
		add(leftPanel);
		add(rightPanel);
		progressBar.setIndeterminate(false);
	    progressBar.setString("");	    
	    progressBar.setStringPainted(true);	    
	    progressBar.setBackground(new Color(156, 162, 189));
	    //progressBar.setForeground(xipColor);
	    add(progressBar);
	    buildRightPanelLayout();
	    buildLayout();
	}
		
	AttributeList criteria;	
	void setCriteriaList(AttributeList criteria){
		this.criteria = criteria;
	}
	
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == criteriaPanel.getQueryButton()){												
			resultTree.rootNode.removeAllChildren();
			progressBar.setString("Processing search request ...");
			progressBar.setIndeterminate(true);			
			progressBar.updateUI();
			setCriteriaList(criteriaPanel.getFilterList());	
			Boolean bln = criteriaPanel.verifyCriteria(criteria);			
			if(bln){				
				//create AVT AD criteria map HashMap<Integer, Object>
				HashMap<Integer, Object> adCriteria = DicomUtil.convertToADDicomCriteria(criteriaPanel.getFilterList());
				//pass adCriteria to AVTQuery, modify AVTQuery accordingly
				AVTQuery avtQuery = new AVTQuery(adCriteria);
				avtQuery.addAVTListener(this);
				Thread t = new Thread(avtQuery);
				t.start();			
			}else{
				progressBar.setString("");
				progressBar.setIndeterminate(false);
			}																	
		}else if (e.getSource() == btnRetrieve){			
			allRetrivedFiles = new ArrayList<File>();
			numRetrieveThreadsStarted = 0;
			numRetrieveThreadsReturned = 0;
			File importDir = HostConfigurator.getHostConfigurator().getHostTmpDir();						
			progressBar.setString("Processing retrieve request ...");
			progressBar.setIndeterminate(true);
			progressBar.updateUI();	
			criteriaPanel.getQueryButton().setBackground(Color.GRAY);
			criteriaPanel.getQueryButton().setEnabled(false);
			btnRetrieve.setBackground(Color.GRAY);
			btnRetrieve.setEnabled(false);												
			Map<Series, Study> map = resultTree.getSelectedSeries();
			Set<Series> seriesSet = map.keySet();
			Iterator<Series> iter = seriesSet.iterator();
			while (iter.hasNext()){
				Series series = iter.next();
				String selectedSeriesInstanceUID = series.getSeriesInstanceUID();			
				String selectedStudyInstanceUID = ((Study)map.get(series)).getStudyInstanceUID();
				try {
					AVTRetrieve avtRetrieve = new AVTRetrieve(selectedStudyInstanceUID, selectedSeriesInstanceUID, importDir);
					avtRetrieve.addAVTListener(this);					
					Thread t = new Thread(avtRetrieve);
					t.start();
					numRetrieveThreadsStarted++;
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

	public void searchResultsAvailable(AVTSearchEvent e) {
		SearchResult result = (SearchResult) e.getSource();				
		if(result == null){			
			resultTree.updateNodes(result);
		}else{
			resultTree.updateNodes(result);			
		}											
		progressBar.setString("AVT AD Search finished");
		progressBar.setIndeterminate(false);				
	}

	List<File> retrivedFiles;
	List<File> allRetrivedFiles;
	int numRetrieveThreadsStarted;
	int numRetrieveThreadsReturned;
	@SuppressWarnings("unchecked")
	public void retriveResultsAvailable(AVTRetrieveEvent e) {		
		retrivedFiles = (List<File>) e.getSource();	
		synchronized(retrivedFiles){
			allRetrivedFiles.addAll(retrivedFiles);
		}		
		numRetrieveThreadsReturned++;
		if(numRetrieveThreadsStarted == numRetrieveThreadsReturned){
			finalizeRetrieve();
		}
	}	
		
	synchronized void finalizeRetrieve(){		
		progressBar.setString("AD Retrieve finished");
		progressBar.setIndeterminate(false);						
		File[] files = new File[allRetrivedFiles.size()];		 
		allRetrivedFiles.toArray(files);		
		FileManager fileMgr = FileManagerFactory.getInstance();						
        fileMgr.run(files);	
        //criteriaPanel.getQueryButton().setBackground(xipBtn);
		criteriaPanel.getQueryButton().setEnabled(true);		
		btnRetrieve.setEnabled(true);
		//btnRetrieve.setBackground(xipBtn);					
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
