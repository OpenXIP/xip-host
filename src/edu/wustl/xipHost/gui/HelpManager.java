/*
 * Copyright (c) 2007 Washington University in Saint Louis. All Rights Reserved.
 */
package edu.wustl.xipHost.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class HelpManager extends JDialog {	
	JLabel lblTitle = new JLabel("XIPHost Help Contents");
	JTextArea contents = new JTextArea(50, 80);
	JScrollPane scrollPane = new JScrollPane(contents);
	JPanel panel = new JPanel();
	Font font_1 = new Font("Tahoma", 0, 13);
	
	public HelpManager(Frame owner){
		super(owner, "Help Contents", true);		
		panel.setLayout(new BorderLayout());		
		lblTitle.setHorizontalAlignment(0);
		lblTitle.setFont(font_1);
		readFile();
		contents.setEditable(false);
		contents.setFont(font_1);
		contents.setBackground(Color.BLACK);
		contents.setForeground(Color.WHITE);
		contents.setToolTipText("XIPHost Help Contents");
		contents.setLineWrap(true);
		contents.setWrapStyleWord(true);
		contents.setCaretPosition(0);
		panel.add(lblTitle, BorderLayout.NORTH);
		panel.add(scrollPane, BorderLayout.SOUTH);
		add(panel);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension windowSize = this.getPreferredSize();                        
        setBounds((screenSize.width - windowSize.width) / 2, (screenSize.height - windowSize.height) /4,  windowSize.width, windowSize.height);
        pack();        
        setVisible(true);
        addWindowListener(
            new WindowAdapter(){
                public void windowClosing(WindowEvent e){
                    dispose();
                }
            }
        );
    }        
	
	BufferedReader in;
	public boolean readFile(){		
		try {
			in = new BufferedReader(new FileReader("./read.txt"));
		} catch (FileNotFoundException e) {			
			e.printStackTrace();
		}
        String str = new String();                
        try {
			while ((str = in.readLine()) != null){                   
				contents.append(str + "\r\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
        try {
			in.close();
		} catch (IOException e) {			
			e.printStackTrace();
		}
        return new Boolean (true);
	}
	
	public static void main(String[] args){
		new HelpManager(new JFrame());

	}

}
