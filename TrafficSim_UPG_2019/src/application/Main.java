package application;

public class Main {
	public static void main(String[] args) {
		View theView = new View();
        CrossRoadController theController = new CrossRoadController(theView);
        
        try {
			theView.getScenar().setSelectedIndex(Integer.parseInt(args[0]));
		} catch (Exception e) {
			theView.getScenar().setSelectedIndex(0);
		}
        theView.setVisible(true);            
    }
}
