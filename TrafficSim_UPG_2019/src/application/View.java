package application;

import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSlider;

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
import java.util.Hashtable;

import javax.swing.JTextPane;
import javax.swing.JToggleButton;
import javax.swing.SpringLayout;
import javax.swing.Timer;
import javax.swing.border.TitledBorder;

import TrafficSim.Simulator;

import javax.swing.JTable;
import java.awt.FlowLayout;
import java.awt.Component;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;

public class View extends JFrame{

	private static final long serialVersionUID = 1L;
	  
	private JMenuBar menuBar = new JMenuBar();
	private JMenu menuFile = new JMenu("File");
	private JMenu menuHelp = new JMenu("Help");
	private JMenuItem item = new JMenuItem("Print map and graph");
	private JMenuItem item2 = new JMenuItem("Export to bitmap");
	private JMenuItem item3 = new JMenuItem("Export to SVG");
	
	private JMenuItem itemExit = new JMenuItem("Exit");
	private JMenuItem itemAbout = new JMenuItem("About");
	private final Panel top_panel = new Panel();
	private final Panel right_panel = new Panel();
	private final Panel buttom_panel = new Panel();
	private final JButton btnStart = new JButton("Start");
	private final JButton btnStop = new JButton("Stop");
	private final JLabel lblTime = new JLabel("Time:");
	private final JLabel timer_label = new JLabel("0.0");
	private JComboBox<String> scenar = new JComboBox<String>();
	private DrawPanel panel;
	private JSlider slider = new JSlider(0, 200);	
	private JToggleButton roads_color_btn1 = new JToggleButton("Speed Average");
	private JToggleButton roads_color_btn2 = new JToggleButton("Number of cars");
	private ButtonGroup roads_colors_group = new ButtonGroup();
	private JButton zoomP = new JButton();
	private JButton zoomM = new JButton();
	private JToggleButton controlBtn = new JToggleButton();
	private JToggleButton b1 = new JToggleButton("Select road");
	private JToggleButton b2 = new JToggleButton("Show cars speed");
	private JButton b3 = new JButton("Show graph"); 
	
//	private JToggleButton roads_color_btn1 = new JToggleButton("Speed Average");
//	private JToggleButton roads_color_btn2 = new JToggleButton("Number of cars");
//	private ButtonGroup roads_colors_group = new ButtonGroup();
//	private JButton zoomP = new JButton("+");
//	private JButton zoomM = new JButton("-");
//	private JToggleButton controlBtn = new JToggleButton("Allow control");
//	private JToggleButton b1 = new JToggleButton("Select road");
//	private JToggleButton b2 = new JToggleButton("Show cars speed");
//	private JButton b3 = new JButton("Show graph"); 
	
	private void seButtons () {
		controlBtn.setBorder(null);
        controlBtn.setIcon(new ImageIcon("img/edit.png"));
        zoomP.setBorder(null);
        zoomP.setIcon(new ImageIcon("img/zoom-in.png"));
        zoomM.setBorder(null);
        zoomM.setIcon(new ImageIcon("img/zoom-out.png"));
	}
	public View() {
		panel = new DrawPanel();
		this.setTitle("Crossroad - Mukanova Zhanel");
		this.setSize(new Dimension(640, 480));
		this.setExtendedState(JFrame.MAXIMIZED_BOTH); 
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLocationRelativeTo(null);    
	        
		// Menu
        menuBar.add(menuFile);
        menuBar.add(menuHelp);
        item.setSelected(true);
        menuFile.add(item);
        menuFile.add(item2);
        menuFile.add(item3);
        menuFile.addSeparator();
        menuFile.add(itemExit);
        menuHelp.add(itemAbout);
        this.setJMenuBar(menuBar);
        
        this.getContentPane().setLayout(new BorderLayout(0, 0));
        
        Hashtable<Integer, Component> labelTable = new Hashtable<Integer, Component>();
        labelTable.put(0, new JLabel("0"));
        labelTable.put(50, new JLabel("50"));
        labelTable.put(100, new JLabel("100"));
        labelTable.put(150, new JLabel("150"));
        labelTable.put(200, new JLabel("200"));
        
        slider.setLabelTable(labelTable);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        slider.setSnapToTicks(false);
        slider.setMajorTickSpacing(5);
        slider.setPaintTicks(true);
        slider.setBackground(SystemColor.inactiveCaption);
        slider.setValue(100);
        
        // Top panel
        top_panel.setBackground(SystemColor.inactiveCaption);
        this.getContentPane().add(top_panel, BorderLayout.NORTH);
        top_panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        top_panel.add(scenar);
        
        // Drawing panel
        this.getContentPane().add(panel, BorderLayout.CENTER);
        
        // Button panel
        buttom_panel.setBackground(SystemColor.inactiveCaption);
        this.getContentPane().add(buttom_panel, BorderLayout.SOUTH);
        buttom_panel.add(btnStart);
        buttom_panel.add(slider);
        buttom_panel.add(btnStop);
        
        // Right panel
        this.getContentPane().add(right_panel, BorderLayout.EAST);
        right_panel.setLayout(new BoxLayout(right_panel, BoxLayout.Y_AXIS));
        
        roads_color_btn1.setMaximumSize(new Dimension(145,25));
        roads_color_btn2.setMaximumSize(new Dimension(145,25));
        roads_colors_group.add(roads_color_btn1);
        roads_colors_group.add(roads_color_btn2);
       
        right_panel.add(addColorsPanel());
        right_panel.add(addOtherPanel());
        right_panel.add(addZoomPanel());
        
        roads_color_btn1.setSelected(true);
        
        Simulator sim = new Simulator();
        String[] scenarios = sim.getScenarios();
        
        for (String s : scenarios) {
			getScenar().addItem(s);
		}
        sim = null;
        
        seButtons();
    }
	
	private Component addOtherPanel() {
		JPanel p1 = new JPanel();
		p1.setAlignmentX(Component.RIGHT_ALIGNMENT);
		p1.setLayout(new BoxLayout(p1, BoxLayout.PAGE_AXIS));
		p1.setBorder(new TitledBorder("Other"));
		b1.setMaximumSize(new Dimension(145,25));
		b2.setMaximumSize(new Dimension(145,25));
		b3.setMaximumSize(new Dimension(145,25));
		p1.add(b1);
		p1.add(Box.createRigidArea(new Dimension(5,5)));
		p1.add(b2);
		p1.add(Box.createRigidArea(new Dimension(5,5)));
		p1.add(b3);
		p1.add(Box.createRigidArea(new Dimension(5,5)));
		return p1;
	}

	private Component addZoomPanel() {
		JPanel p2 = new JPanel();
		p2.setAlignmentX(Component.RIGHT_ALIGNMENT);
        p2.setLayout(new BoxLayout(p2, BoxLayout.X_AXIS));
        p2.setBorder(new TitledBorder("Zoom"));
        p2.add(controlBtn);
        p2.add(Box.createRigidArea(new Dimension(5,5)));
        p2.add(zoomP);
        p2.add(Box.createRigidArea(new Dimension(5,5)));
        p2.add(zoomM);
		return p2;
	}

	private Component addColorsPanel() {
		JPanel p1 = new JPanel();
		p1.setAlignmentX(Component.RIGHT_ALIGNMENT);
		p1.setLayout(new BoxLayout(p1, BoxLayout.PAGE_AXIS));
		p1.setBorder(new TitledBorder("Colors"));
		p1.add(roads_color_btn1);
		p1.add(Box.createRigidArea(new Dimension(5,5)));
		p1.add(roads_color_btn2);
		p1.add(Box.createRigidArea(new Dimension(5,5)));
		return p1;
	}

	public JSlider getSlider() {
		return slider;
	}

	public void setSlider(JSlider slider) {
		this.slider = slider;
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

	public JToggleButton getRoads_color_btn1() {
		return roads_color_btn1;
	}

	public void setRoads_color_btn1(JToggleButton roads_color_btn1) {
		this.roads_color_btn1 = roads_color_btn1;
	}

	public JToggleButton getRoads_color_btn2() {
		return roads_color_btn2;
	}

	public void setRoads_color_btn2(JToggleButton roads_color_btn2) {
		this.roads_color_btn2 = roads_color_btn2;
	}

	public JButton getZoomP() {
		return zoomP;
	}

	public JButton getZoomM() {
		return zoomM;
	}

	public JToggleButton getSelectRoad() {
		return b1;
	}

	public JMenuItem getItem() {
		return item;
	}

	public JMenuItem getItem2() {
		return item2;
	}

	public JMenuItem getItem3() {
		return item3;
	}

	public JToggleButton getB1() {
		return b1;
	}

	public JToggleButton getB2() {
		return b2;
	}

	public JButton getB3() {
		return b3;
	}
	public JToggleButton getControlBtn() {
		return controlBtn;
	}
	
	
}
