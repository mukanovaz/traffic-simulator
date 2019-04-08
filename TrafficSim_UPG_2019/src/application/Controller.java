package application;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JSlider;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;

import TrafficSim.Simulator;

public class Controller{
	//private Model model;
	private View view;
	private Timer timer;
	private Simulator sim;
	private int defaultPeriod = 50;
	private Boolean state = false;
	private double simulator_time = 0.09;
	private JComboBox<String> cb;
	
	public Controller(View v) {
		this.view = v;
		initListeners();
	}

	private void initListeners() {
		
	    long startTime = System.currentTimeMillis();
		timer = new Timer(defaultPeriod, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				view.setTime((System.currentTimeMillis() - startTime) / 1000.0);
				sim.nextStep(simulator_time);
				view.getDrawPanel().repaint();
			}
		});
		
		cb = this.view.getScenar();
		this.view.getItemExitButton().addActionListener(e -> System.exit(1));
		this.view.getStartButton().addActionListener(e ->  { 
				state = true;
				timer.start(); 
			});
		this.view.getStopButton().addActionListener(e -> { 
				state = false;	
				timer.stop(); 
			});
		
		cb.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				sim = new Simulator();
				setSimulator(sim);
				sim.runScenario(sim.getScenarios()[cb.getSelectedIndex()]);
				view.getDrawPanel().setSim(sim);
				view.getDrawPanel().repaint();
			}
		});
		
		this.view.getSlider().addChangeListener(e -> setTimerPeriod(e));
		
	}
	
	private void setTimerPeriod(ChangeEvent e) {
	     timer.stop();
		 JSlider source = (JSlider)e.getSource();
		 int newPeriod = source.getValue();
	     simulator_time = newPeriod;
	     if (state) timer.restart();
	}

	public void setSimulator (Simulator sim) {
		this.sim = sim;
	}
}
