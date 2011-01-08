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
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
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
import org.nema.dicom.wg23.State;
import edu.wustl.xipHost.application.Application;
import edu.wustl.xipHost.application.ApplicationManager;
import edu.wustl.xipHost.application.ApplicationManagerFactory;
import edu.wustl.xipHost.iterator.IterationTarget;
import edu.wustl.xipHost.hostControl.HostConfigurator;
import edu.wustl.xipHost.localFileSystem.DicomParseEvent;
import edu.wustl.xipHost.localFileSystem.DicomParseListener;
import edu.wustl.xipHost.localFileSystem.FileManagerFactory;
import edu.wustl.xipHost.localFileSystem.FileRunner;
import edu.wustl.xipHost.wg23.WG23DataModel;


/**
 * @author Jaroslaw Krych
 *
 */
public class InputDialog extends JPanel implements ListSelectionListener, ActionListener, DicomParseListener  {	
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
	JPanel iconPanel = new JPanel();
	JScrollPane scrollPane = new JScrollPane(iconPanel);
	Cursor hourglassCursor = new Cursor(Cursor.WAIT_CURSOR);	
	Cursor normalCursor = new Cursor(Cursor.DEFAULT_CURSOR);	
	
	public InputDialog(){									
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
		
		iconPanel.setBackground(Color.BLACK);
		scrollPane.setPreferredSize(new Dimension(300, 160));
		
		add(iconPanel);
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
		//listModel.addElement(uri);			
	}		
	
	//TODO ID#919 and ID#887
	public void setItems(File[] items){
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
		FileManagerFactory.getInstance().clearParsedData();	
		tableInput.updateUI();
	}
		
	ImageIcon iconApp1 = new ImageIcon("./gif/ApplicationIcon-16x16.png");
	
	public void setApplications(List<Application> applications){	
		iconPanel.removeAll();
		for(int i = 0; i < applications.size(); i++){
			Application app = applications.get(i);			
			ImageIcon iconFile;
			if(app.getIconFile() == null){
				iconFile = null;
			}else{
				iconFile = new ImageIcon(app.getIconFile().getPath());
			}
			AppButton btn = new AppButton(app.getName(), iconFile);
			btn.setPreferredSize(new Dimension(150, 25));
			btn.setOpaque(true);			
			btn.setApplicationUUID(app.getID());
			btn.setForeground(Color.BLACK);			
			btn.addActionListener(this);
			btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			iconPanel.add(btn);
		}
		int numRows = (int) (applications.size()/6 + 1);		
		iconPanel.setLayout(new GridLayout(numRows, 6));		
	}
	
	class AppLabel extends JLabel{		
		public AppLabel(String text, Icon icon,  int horizontalAlignment){
			super(text, icon, horizontalAlignment);
		}
		
		UUID appUUID;
		void setApplicationUUID(UUID uuid){
			appUUID = uuid;
		}
		UUID getApplicationUUID(){
			return appUUID;
		}
		
	}
	
	class AppButton extends JButton{		
		public AppButton(String text, Icon icon){
			super(text, icon);
		}
		
		UUID appUUID;
		void setApplicationUUID(UUID uuid){
			appUUID = uuid;
		}
		UUID getApplicationUUID(){
			return appUUID;
		}
		
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
        
        constraints.fill = GridBagConstraints.HORIZONTAL;        
        constraints.gridx = 0;
        constraints.gridy = 1;        
        constraints.gridwidth = 2;
        constraints.insets.top = 20;
        constraints.insets.left = 20;
        constraints.insets.bottom = 20;
        constraints.insets.right = 20;       
        constraints.anchor = GridBagConstraints.CENTER;
        layout.setConstraints(iconPanel, constraints); 
        
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
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {	
		        clearData();  
		        frame.dispose();
			}
		});		
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension windowSize = frame.getPreferredSize();
        frame.setBounds((screenSize.width - windowSize.width) / 2, (screenSize.height - windowSize.height) /2,  windowSize.width, windowSize.height);
		//frame.pack();
	}
	

	public void valueChanged(ListSelectionEvent e) {
		if (e.getValueIsAdjusting() == false) {
			if (list.getSelectedIndex() != -1) {
				JList list = ((JList)e.getSource());		
				URI selectedURI = (URI)list.getSelectedValue();
				
				//setTableValues(hashmap.get(selectedURI));
				
				//TODO ID#919 and ID#887
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

	public void actionPerformed(ActionEvent e) {
		setCursor(hourglassCursor);
		AppButton btn = (AppButton)e.getSource();
		UUID uuid = btn.getApplicationUUID();
		ApplicationManager appMgr = ApplicationManagerFactory.getInstance(); 
		Application app = appMgr.getApplication(uuid);
		//Check if application to be launched is not running.
		//If yes, create new application instance
		State state = app.getState();		
		WG23DataModel data = FileManagerFactory.getInstance().getWG23DataModel();
		if(state != null && !state.equals(State.EXIT)){
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
			instanceApp.setData(data);			
			instanceApp.launch(appMgr.generateNewHostServiceURL(), appMgr.generateNewApplicationServiceURL());
		}else{
			app.setData(data);			
			app.launch(appMgr.generateNewHostServiceURL(), appMgr.generateNewApplicationServiceURL());			
		}					
		setCursor(normalCursor);
		frame.setVisible(false);
		frame.requestFocus();
		frame.dispose();
		clearData();		
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

	//TODO ID#919 and ID#887
	//dicomAvailbale() or nondicomAvailable() is used to parse single items on demand (upon selection of the item) 
	@Override
	public void dicomAvailable(DicomParseEvent e) {
		setCursor(hourglassCursor);
		FileRunner source = (FileRunner)e.getSource();
		File file = source.getItem();
		URI selectedURI = file.toURI();
		String[][] result = source.getParsingResult();		
		setParsingResult(file.toURI(), result);
		setTableValues(hashmap.get(selectedURI));
		updateUI();		
		setCursor(normalCursor);
	}

	@Override
	public void nondicomAvailable(DicomParseEvent e) {
		hashmap.clear();				
		values = null;
		tableInput.updateUI();			
	}
	
	
}
