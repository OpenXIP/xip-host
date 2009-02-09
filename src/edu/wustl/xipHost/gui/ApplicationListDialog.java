/**
 * Copyright (c) 2007 Washington University in Saint Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.Border;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import edu.wustl.xipHost.application.Application;

/**
 * <font  face="Tahoma" size="2">
 * UI component used to display applications registered with XIP host. <br></br>
 * @version	January 2008
 * @author Jaroslaw Krych
 * </font>
 */
public class ApplicationListDialog extends JDialog {			
	JPanel panel = new JPanel();			
	ApplicationListTableModel tableModel = new ApplicationListTableModel();
	public JTable appListTable = new JTable(tableModel);	
	Object values [][];
	int numOfRows;	
	Font font_1 = new Font("Tahoma", 0, 13);
	Font font_2 = new Font("Tahoma", 0, 12);
	Border border = BorderFactory.createLineBorder(Color.black);	
		
	
	public ApplicationListDialog(Frame owner, Object[] values){						
		super(owner, "Registered applications", true);  
		if(values != null){
			numOfRows = values.length;		
			this.values = new Object[numOfRows][4];		
			for(int i = 0; i < numOfRows; i++){
				this.values[i][0] = ((Application)values[i]).getName();
				this.values[i][1] = ((Application)values[i]).getExePath();
				this.values[i][2] = ((Application)values[i]).getVendor();
				this.values[i][3] = ((Application)values[i]).getVersion();			
			}		
		}		
		add(panel);		
		appListTable.setFont(font_2);
		Border border1 = BorderFactory.createLoweredBevelBorder();
		panel.setBorder(border1);						
		appListTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);		
		appListTable.getTableHeader().setAlignmentX(SwingConstants.CENTER);
		appListTable.setRowSelectionAllowed(true);		
		TableColumn column = null;		
		for (int i = 0; i < 4; i++) {            
            column = appListTable.getColumnModel().getColumn( i );            
            switch(i) {
                case 0:                                    	
                	column.setCellRenderer(new Renderer());                    
                case 1:
                	column.setCellRenderer(new Renderer());                                                        
                case 2:
                	column.setCellRenderer(new Renderer());
                case 3:                	                	                	
                	column.setCellRenderer(new Renderer());  
            }                                                
            column.setPreferredWidth( tableModel.getColumnWidth( i ) );            
        }
		
		//workListTable.setPreferredScrollableViewportSize( new Dimension(600, 550) );		
		JScrollPane jsp = new JScrollPane(appListTable);
		jsp.setPreferredSize(new Dimension(900, 300));
		panel.add(jsp);
		buildLayout();         		
	}
		
	class ApplicationListTableModel extends AbstractTableModel {
		String[] strArrayColumnNames = {
			"Name", 
			"Path",
            "Vendor",
            "Version",
        };    
		
		public int getColumnCount() {			
			return strArrayColumnNames.length;
		}

		public int getRowCount() {			
			return numOfRows;
		}

		public Object getValueAt(int rowIndex, int columnIndex) {
			switch( columnIndex ) {
                case 0:
                    return values[rowIndex][0];
                case 1:
                    return values[rowIndex][1];
                case 2:
                    return values[rowIndex][2];
                case 3:
                    return values[rowIndex][3];
                default:
                    return null;
            }           			
		}
				
		public String getColumnName( int col ) {
            return strArrayColumnNames[col];
        }
		
		public int getColumnWidth( int nCol ) {
            switch( nCol ) {
                //Name
            	case 0:
                    return 200;
                //Path
            	case 1:
                    return 350;
                //Vendor
            	case 2:
                    return 200;
                //Version
            	case 3:
                    return 150;                
                default:
                    return 150;
            }
        }
		
	}	
	
	public void buildLayout(){
		//GridBagLayout dimensions: 3 rows x 5 column
        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        setLayout(layout);
                
        constraints.fill = GridBagConstraints.HORIZONTAL;        
        constraints.gridx = 0;
        constraints.gridy = 0;     
        constraints.gridwidth = 3;
        constraints.gridheight = 1;
        //constraints.insets.right = 10;
        constraints.insets.bottom = 10;
        layout.setConstraints(panel, constraints);                  
	}
	
	public class Renderer extends DefaultTableCellRenderer implements TableCellRenderer{        
        public Renderer(){            
            setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        }         
    }    
		
	public Boolean display(){				
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}    		
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension windowSize = getPreferredSize();
        setBounds((screenSize.width - windowSize.width) / 2, (screenSize.height - windowSize.height) /2,  windowSize.width, windowSize.height);        
        setResizable(false);
        pack();
        setVisible(true);        
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		return new Boolean(true);
	}
	
	public void dispose(){
		dispose();
	}
	
	public static void main(String[] args){
		new ApplicationListDialog(new JFrame(), null).display();
	}
	
}	

	