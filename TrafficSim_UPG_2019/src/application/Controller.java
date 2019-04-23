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
	private JComboBox<String> cb;
	
	public Controller(View v) {
		this.view = v;
		initListeners();
	}

	private void initListeners() {
		
		timer = new Timer(defaultPeriod, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				double stepValue = (double) defaultPeriod / 1000;
				sim.nextStep(stepValue);
				view.getDrawPanel().setSimulation_time(defaultPeriod);
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
				view.getDrawPanel().computeModelDimensions();
				view.getDrawPanel().repaint();
			}
		});
		
		this.view.getSlider().addChangeListener(e -> setTimerPeriod(e));
		this.view.getRoads_color_btn1().addChangeListener(e -> view.getDrawPanel().setRoadColor(true));
		this.view.getRoads_color_btn2().addChangeListener(e -> view.getDrawPanel().setRoadColor(false));
		this.view.getZoomP().addActionListener(e -> {
			view.getDrawPanel().setZoomFactor(view.getDrawPanel().getZoomFactor() * 1.1);
			view.getDrawPanel().repaint();
		});
		this.view.getZoomM().addActionListener(e -> {
			view.getDrawPanel().setZoomFactor(view.getDrawPanel().getZoomFactor() / 1.1);
			view.getDrawPanel().repaint();
		});
	}
	
	private void setTimerPeriod(ChangeEvent e) {
	     timer.stop();
		 JSlider source = (JSlider)e.getSource();
		 defaultPeriod = source.getValue();
	     if (state) timer.restart();
	}

	public void setSimulator (Simulator sim) {
		this.sim = sim;
	}
}


