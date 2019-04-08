package application;

import TrafficSim.Simulator;

public class Main {
	public static void main(String[] args) {
		
		View theView = new View();
        Controller theController = new Controller(theView);
       
//        theView.getScenar().addItem("");
        
        try {
			theView.getScenar().setSelectedIndex(Integer.parseInt(args[0]));
		} catch (Exception e) {
			theView.getScenar().setSelectedIndex(0);
		}
        theView.setVisible(true);            
    }
 
}
