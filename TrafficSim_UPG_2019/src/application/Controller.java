package application;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.Timer;

import TrafficSim.Simulator;

public class Controller{
	//private Model model;
	private View view;
	private Timer timer;
	private Simulator sim;
	
	public Controller(View v, Simulator sim) {
		this.view = v;
		this.sim = sim;
		innitListeners();
	}

	private JComboBox<String> cb;
	private void innitListeners() {
		int timerPeriod = 100;
		
	    long startTime = System.currentTimeMillis();
		timer = new Timer(timerPeriod, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				view.setTime((System.currentTimeMillis() - startTime) / 1000.0);
				try {
					sim.nextStep(1);
				} catch (Exception e2) {
					System.out.println((System.currentTimeMillis() - startTime) / 1000.0);
				}
				view.getDrawPanel().repaint();
			}
		});
		
		cb = this.view.getScenar();
		this.view.getItemExitButton().addActionListener(e -> System.exit(1));
		this.view.getStartButton().addActionListener(e ->  timer.start());
		this.view.getStopButton().addActionListener(e -> timer.stop());
		cb.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				sim.runScenario(sim.getScenarios()[cb.getSelectedIndex()]);
				view.getDrawPanel().repaint();
			}
		});
	}

}
