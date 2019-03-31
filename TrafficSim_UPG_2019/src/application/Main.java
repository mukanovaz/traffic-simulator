package application;

import TrafficSim.Simulator;

public class Main {
	public static void main(String[] args) {
		
		Simulator sim = new Simulator();
				
		View theView = new View(sim);
        Controller theController = new Controller(theView, sim);
        String[] scenarios = sim.getScenarios();
        for (String s : scenarios) {
			theView.getScenar().addItem(s);
		}
        
        try {
			theView.getScenar().setSelectedIndex(Integer.parseInt(args[0]));
		} catch (Exception e) {
			theView.getScenar().setSelectedIndex(0);
		}
        theView.setVisible(true);            
    }
 
}
