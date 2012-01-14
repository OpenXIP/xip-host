package edu.wustl.xipHost.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.geom.AffineTransform;
import java.util.UUID;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

import edu.wustl.xipHost.hostControl.HostConfigurator;

public class VerticalTextIcon extends JComponent implements Icon, SwingConstants{ 
    Font font = new Font("Tahoma", 0, 12);
    FontMetrics fm = getFontMetrics(font);  
    String text;
    UUID uuid;
    int width, height; 
    boolean clockwize; 
 
    public VerticalTextIcon(String text, boolean clockwize){ 
        this.text = text; 
        width = SwingUtilities.computeStringWidth(fm, text);
        height = fm.getHeight(); 
        this.clockwize = clockwize;
    } 
 
    public void paintIcon(Component c, Graphics g, int x, int y) { 	
    	Graphics2D g2 = (Graphics2D)g; 
        Font oldFont = g.getFont(); 
        Color oldColor = g.getColor(); 
        AffineTransform oldTransform = g2.getTransform(); 
        g.setFont(font); 
        g.setColor(Color.BLACK); 
        if(clockwize){     	
    		g2.translate(x + getIconWidth(), y); 
    		g2.rotate(Math.PI/2);   
        } else { 
            g2.translate(x, y + getIconHeight()); 
            g2.rotate(-Math.PI/2); 
        } 
        g.drawString(text, 0, fm.getLeading() + fm.getAscent()); 
        g.setFont(oldFont); 
        g.setColor(oldColor); 
        g2.setTransform(oldTransform); 
    } 
 
    public int getIconWidth(){ 
        return height; 
    } 
 
    public int getIconHeight(){ 
        return width; 
    }
    
    public static void addTab(JTabbedPane tabPane, String text, Component comp){ 
        int tabPlacement = tabPane.getTabPlacement(); 
        switch(tabPlacement){ 
            case JTabbedPane.LEFT: 
            case JTabbedPane.RIGHT:                 
            	if(HostConfigurator.OS.contains("Mac OS X") == false){
            		tabPane.addTab(null, new VerticalTextIcon(text, tabPlacement == JTabbedPane.RIGHT), comp);  
            	} else {
            		tabPane.addTab(text, null, comp); 
            	}
            	return;
            default: 
                tabPane.addTab(text, null, comp); 
        } 
    } 
    
    public String getTabTitle(){
    	return text;
    }
    public UUID getUUIDfromTab(){
    	return uuid;
    }
    
    public static JTabbedPane createTabbedPane(int tabPlacement){ 
        switch(tabPlacement){ 
            case JTabbedPane.LEFT: 
            case JTabbedPane.RIGHT: 
                Object textIconGap = UIManager.get("TabbedPane.textIconGap"); 
                Insets tabInsets = UIManager.getInsets("TabbedPane.tabInsets"); 
                UIManager.put("TabbedPane.textIconGap", new Integer(1)); 
                UIManager.put("TabbedPane.tabInsets", new Insets(tabInsets.left, tabInsets.top, tabInsets.right, tabInsets.bottom)); 
                JTabbedPane tabPane = new JTabbedPane(tabPlacement); 
                UIManager.put("TabbedPane.textIconGap", textIconGap); 
                UIManager.put("TabbedPane.tabInsets", tabInsets);                 
                return tabPane; 
            default: 
                return new JTabbedPane(tabPlacement); 
        } 
    }
}