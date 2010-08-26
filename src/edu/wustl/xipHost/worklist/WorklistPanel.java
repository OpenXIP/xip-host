/*
 * Copyright (c) 2008 Washington University in Saint Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.worklist;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.Border;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import org.nema.dicom.wg23.State;
import edu.wustl.xipHost.application.Application;
import edu.wustl.xipHost.application.ApplicationManager;
import edu.wustl.xipHost.application.ApplicationManagerFactory;
import edu.wustl.xipHost.avt2ext.iterator.IterationTarget;
import edu.wustl.xipHost.gui.ExceptionDialog;

/**
 * <font  face="Tahoma" size="2">
 * GUI component displaying the content of the worklist.<br></br>
 * @version	August 2008
 * @author Jaroslaw Krych
 * </font>
 */
public class WorklistPanel extends JPanel implements WorklistEntryListener{			
	JPanel panel = new JPanel();			
	WorkListTableModel tableModel;
	JTable worklistTable;
	WorkListTableRowMouseAdapter workListMouseAdapter = new WorkListTableRowMouseAdapter();	 	
	JProgressBar progressBar = new JProgressBar();	
	Font font_1 = new Font("Tahoma", 1, 13);
	Font font_2 = new Font("Tahoma", 0, 12);
	Border border = BorderFactory.createLineBorder(Color.black);
	Color xipColor = new Color(51, 51, 102);
	JLabel infoLabel = new JLabel("UNDER   DEVELOPMENT");
		
	Worklist worklist;
	
	public WorklistPanel(){				
		add(panel);	
		tableModel = new WorkListTableModel();
		worklistTable = new JTable(tableModel);	
		worklistTable.setFont(font_2);				
		Border border1 = BorderFactory.createLoweredBevelBorder();
		panel.setBorder(border1);
		panel.setBackground(new Color(51, 51, 102));
		worklistTable.addMouseListener(workListMouseAdapter);
		worklistTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);		
		worklistTable.getTableHeader().setAlignmentX(SwingConstants.CENTER);
		TableColumn column = null;
		for (int i = 0; i < tableModel.getColumnCount(); i++) {            
            column = worklistTable.getColumnModel().getColumn( i );            
            switch(i) {
                case 0:                                    	
                	//column.setCellRenderer(new ColumnRenderer());            
                case 1:
                	
                case 2:
                	
                case 3:                	
                	
                case 4:                	                         
                	
            }                                                
            column.setPreferredWidth( tableModel.getColumnWidth( i ) );            
        }		
		worklist = WorklistFactory.getInstance();		
		tableModel.fireTableDataChanged();		
		
		JScrollPane jsp = new JScrollPane(worklistTable);
		jsp.setPreferredSize(new Dimension(900, 300));				
		panel.add(jsp);
		progressBar.setIndeterminate(false);
	    progressBar.setString("");	    
	    progressBar.setStringPainted(true);	    
	    progressBar.setBackground(new Color(156, 162, 189));
	    progressBar.setForeground(xipColor);
	    infoLabel.setForeground(Color.ORANGE);
		add(infoLabel);
	    add(progressBar);
		setBackground(xipColor);
		buildLayout();         		
	}
	
	class WorkListTableModel extends AbstractTableModel {
		String[] strArrayColumnNames = {
			"Subject name",
			"Subject ID",
			"Study Date",
			"Task Desc", 
			"Application",                       
        };    
		
		public int getColumnCount() {			
			return strArrayColumnNames.length;
		}

		public int getRowCount() {			
			return worklist.getNumberOfWorklistEntries();
		}

		public Object getValueAt(int rowIndex, int columnIndex) {
			switch( columnIndex ) {
                //Subject name
				case 0:
                	return worklist.getWorklistEntry(rowIndex).getSubjectName();
				//Subject ID
				case 1:
                	return worklist.getWorklistEntry(rowIndex).getSubjectID();                	
                //Study date
				case 2:
                    return worklist.getWorklistEntry(rowIndex).getStudyDate();
                //Task desc
				case 3:
                    return worklist.getWorklistEntry(rowIndex).getTaskDescription();
                //Application
				case 4:
					return worklist.getWorklistEntry(rowIndex).getApplicationName();				
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
                    return 100;
                case 2:
                    return 150;
                case 3:
                    return 300;
                case 4:
                    return 200;                
                default:
                    return 150;
            }
        }		
	}	
	
	public void buildLayout(){		
        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        setLayout(layout);
        
        constraints.fill = GridBagConstraints.NONE;        
        constraints.gridx = 0;
        constraints.gridy = 0;                     
        constraints.insets.top = 10;
        constraints.insets.bottom = 10;
        constraints.anchor = GridBagConstraints.CENTER;
        layout.setConstraints(infoLabel, constraints);      
        
        constraints.fill = GridBagConstraints.HORIZONTAL;        
        constraints.gridx = 0;
        constraints.gridy = 1;                     
        constraints.insets.top = 10;
        constraints.insets.bottom = 10;
        layout.setConstraints(panel, constraints);        
        
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 0;
        constraints.gridy = 2;                     
        constraints.insets.top = 10;
        constraints.insets.bottom = 10;
        layout.setConstraints(progressBar, constraints);
	}
	
	public class ColumnRenderer extends DefaultTableCellRenderer {        
        public ColumnRenderer(){            
            setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
            this.setBackground(Color.BLUE);
        }        
    }            	
	
	WorklistEntryListener entryListener = this;	
	class WorkListTableRowMouseAdapter extends MouseAdapter{
		int row;					
		public void mouseClicked(MouseEvent e) {
			if(e.getButton() == 1){
				JTable source = (JTable)e.getSource();
				row = source.rowAtPoint( e.getPoint() );
				worklistTable.setRowSelectionInterval(row, row) ;
				WorklistEntry entry = worklist.getWorklistEntry(row);				
				entry.addWorklistEntryListener(entryListener);				
				if(e.getClickCount() == 2){					
					//Check is applications assigned to teh worklist entry is added to teh host.
					String appName = entry.getApplicationName();
					ApplicationManager appMgr = ApplicationManagerFactory.getInstance(); 
					Application app = appMgr.getApplication(appName);
					if(app == null){
						new ExceptionDialog("Unable to launch assigned application.", 
								"Application must be added to the list of registered applications.",
								"Application Manager");
						return;
					}
					progressBar.setString("Executing worklist entry ...");
					progressBar.setIndeterminate(true);
					progressBar.updateUI();	
					entry.setImportLocation(importLocation);					
					Thread t = new Thread(entry);
					t.start();
					//entry.execute(importLocation);					
				}
			}				
		}
	}	
		
	File importLocation;
	public void setImportLocation(File importLocation){
		this.importLocation = importLocation;
	}
	
	public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    	JFrame app = new JFrame();
		WorklistPanel panel = new WorklistPanel();
		
		Worklist worklist = new XMLWorklistImpl();
		String path = "./config/worklist.xml";
		File xmlWorklistFile = new File(path);	
		worklist.loadWorklist(xmlWorklistFile);		
        app.add(panel);
		//app.setSize(new Dimension(888, 731));
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension windowSize = app.getPreferredSize();
        app.setBounds((screenSize.width - windowSize.width) / 2, (screenSize.height - windowSize.height) /2,  windowSize.width, windowSize.height);        
        app.pack();
        app.setVisible(true);
        //app.setExtendedState (JFrame.MAXIMIZED_BOTH);
        app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public void worklistEntryDataAvailable(WorklistEntryEvent e) {
		//TODO use multiple progress bars for each row in the table 
		//Worklist entries can be executed in parallel
		progressBar.setString("Retrieve data completed. Launching assigned application ...");				
		WorklistEntry source = (WorklistEntry) e.getSource();
		String appName = source.getApplicationName();
		ApplicationManager appMgr = ApplicationManagerFactory.getInstance(); 
		Application app = appMgr.getApplication(appName);		
		//Check if application assigned to the worklist entry is not running. If it is create new instance 
		if(app.getState() != null && !app.getState().equals(State.EXIT)){
			String instanceName = app.getName();
			File instanceExePath = app.getExePath();
			String instanceVendor = app.getVendor();
			String instanceVersion = app.getVersion();
			File instanceIconFile = app.getIconFile();
			String type = app.getType();
			boolean requiresGUI = app.requiresGUI();
			String wg23DataModelType = app.getWG23DataModelType();
			int concurrentInstances = app.getConcurrentInstances();
			IterationTarget iterationTarget = app.getIterationTarget();
			Application instanceApp = new Application(instanceName, instanceExePath, instanceVendor,
					instanceVersion, instanceIconFile, type, requiresGUI, wg23DataModelType, concurrentInstances, iterationTarget);
			instanceApp.setDoSave(false);
			appMgr.addApplication(instanceApp);
			instanceApp.setData(source.getDataModel());			
			instanceApp.launch(appMgr.generateNewHostServiceURL(), appMgr.generateNewApplicationServiceURL());
		}else{
			app.setData(source.getDataModel());				
			//app.launch(new URL("http://localhost:8090/HostService"+"?wsdl"), new URL("http://localhost:8091/ApplicationService"+"?wsdl"));			
			app.launch(appMgr.generateNewHostServiceURL(), appMgr.generateNewApplicationServiceURL());
		}
		progressBar.setString("");
		progressBar.setIndeterminate(false);
		progressBar.updateUI();
	}	
}
