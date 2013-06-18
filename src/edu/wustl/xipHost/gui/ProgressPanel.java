/*
Copyright (c) 2013, Washington University in St.Louis.
All rights reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package edu.wustl.xipHost.gui;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public class ProgressPanel extends JPanel implements Runnable 
{			
	ProgressPanel _this;		
	String strMessage = new String();
	JProgressBar progressBar = new JProgressBar();    
	Font font_2 = new Font("Tahoma", 0, 12);
	
	public ProgressPanel(){
		_this = this;		
	}
	
	public void createAndShowGUI(){
		//super("Progress Bar");
				 
	    Border border = BorderFactory.createLineBorder(Color.black);	    
		progressBar.setIndeterminate(false);
	    progressBar.setString(strMessage);
	    progressBar.setFont(font_2);
	    progressBar.setStringPainted(true);
	    //progressBar.setPreferredSize(new Dimension(350, 20));	    	   
	    	    	    
		add(progressBar);	
		setBorder(border);
		setPreferredSize(new Dimension(400, 34));
		buildLayout();
		setVisible(true);
		//UIManager.put("ProgressBar.repaintInterval", new Integer(100));
		//UIManager.put("ProgressBar.cycleTime", new Integer(500));
	}
	
    public void stopProgress(){        
    	progressBar.setIndeterminate(false);    	
    }
        
    public JProgressBar getProgressBar(){
    	return progressBar;
    }
    public void buildLayout(){
//    	GridBagLayout dimensions: 1 rows x 1 column
        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        setLayout(layout);
        constraints.fill = GridBagConstraints.BOTH;        
        constraints.gridx = 0;
        constraints.gridy = 0;
        //Insets(top, left, bottom, right) 
        constraints.ipadx = 40;
        //constraints.ipady = 10;
        //constraints.insets = new Insets(10, 20, 10, 20);
        layout.setConstraints(this, constraints);  
    }
   
    public static void main(String[] args) {
        JFrame frame = new JFrame();
        ProgressPanel panel = new ProgressPanel();
        frame.add(panel);
    	frame.setVisible(true);
        Thread t = new Thread(panel);
        t.start();
    }

    public boolean isDone = false;
    boolean shouldStop = false;
	public void run() {		
		
		while(isDone == false){
			if(shouldStop == false){
				//_this.createAndShowGUI();
				progressBar.setIndeterminate(true);
				shouldStop = true;				
			}else{
				//isDone = true;				
				return;
			}			
		}										
	}	
}
