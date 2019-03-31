package application;

import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

import java.awt.BorderLayout;
import java.awt.Panel;
import java.awt.Canvas;
import java.awt.Color;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;

import javax.swing.JTextPane;
import javax.swing.Timer;

import TrafficSim.Simulator;

import javax.swing.JTable;
import java.awt.FlowLayout;
import java.awt.Component;
import javax.swing.Box;

public class View extends JFrame{

	private static final long serialVersionUID = 1L;
	  
	private JMenuBar menuBar = new JMenuBar();
	private JMenu menuFile = new JMenu("File");
	private JMenu menuHelp = new JMenu("Help");
	private JMenuItem item = new JMenuItem("Open");
	private JMenuItem itemExit = new JMenuItem("Exit");
	private JMenuItem itemAbout = new JMenuItem("About");
	private final Panel top_panel = new Panel();
	private final JButton btnStart = new JButton("Start");
	private final JButton btnStop = new JButton("Stop");
	private final Panel buttom_panel = new Panel();
	private final JLabel lblTime = new JLabel("Time:");
	private final JLabel timer_label = new JLabel("0.0");
	private JComboBox<String> scenar = new JComboBox<String>();
	private DrawPanel panel;
	
	public View(Simulator sim) {
		panel = new DrawPanel(sim);
		this.setTitle("Crossroad - Mukanova Zhanel");
//		this.setSize(new Dimension(640, 480));
		this.setExtendedState(JFrame.MAXIMIZED_BOTH); 
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLocationRelativeTo(null);    
	        
		// Menu
        menuBar.add(menuFile);
        menuBar.add(menuHelp);
        item.setSelected(true);
        menuFile.add(item);
        menuFile.addSeparator();
        menuFile.add(itemExit);
        menuHelp.add(itemAbout);
        this.setJMenuBar(menuBar);
        
        this.getContentPane().setLayout(new BorderLayout(0, 0));
        
        // Top panel
        top_panel.setBackground(SystemColor.inactiveCaption);
        this.getContentPane().add(top_panel, BorderLayout.NORTH);
        top_panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        top_panel.add(btnStart);
        top_panel.add(btnStop);
        top_panel.add(scenar);
        
        // Drawing panel
        this.getContentPane().add(panel, BorderLayout.CENTER);
        
        // Button panel
        buttom_panel.setBackground(SystemColor.inactiveCaption);
        this.getContentPane().add(buttom_panel, BorderLayout.SOUTH);
        buttom_panel.add(lblTime);
        buttom_panel.add(timer_label);
    }
	
	public JComboBox<String> getScenar() {
		return scenar;
	}
	
	protected void setTime(double d) {
		timer_label.setText(String.format("%.2f s", d));
	}

	public JMenuItem getItemExitButton () {
		return itemExit;
	}
	
	public JButton getStartButton () {
		return btnStart;
	}
	
	public JButton getStopButton () {
		return btnStop;
	}
	
	public DrawPanel getDrawPanel () {
		return panel;
	}
}
