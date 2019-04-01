import TrafficSim.Car;
import TrafficSim.Simulator;

public class L01_SpusteniSimulatoru {

	public static void main(String[] args) {
		// Vytvoreni simulatoru
		Simulator sim = new Simulator();
		
		// Vypis existujicich scenaru
		String[] scenarios = sim.getScenarios();
		for(int i=0; i<scenarios.length; i++) {
			System.out.println(scenarios[i]);
		}
		
		// Nahrani a spusteni prvniho scenare
		sim.runScenario(scenarios[3]);

		// Ziskani prvniho auta ze seznamu aut
		Car car = sim.getCars()[0];

		// Spusteni simulace, v kazdem kroku cyklu se posune o 1 sekundu. Obecne by ale melo byt provazano na realny cas 
		// (cas, ktery uplynul od posledni aktualizace).
		// Kdyz auto vyjede z mapy, automaticky se nasadi na dalsi pocatecni usek. Nekdy k tomu dojde 
		// v ramci jednoho simulacniho kroku, jindy muze byt auto nekolik kroku mimo. 
		for(int i=0; i<100; i++) {
			sim.nextStep(1);
			System.out.format("%d km/h, souradnice X: %.2f; Y: %.2f\n",(int)car.getCurrentSpeed(), car.getPosition().getX(), car.getPosition().getY());
			System.out.format("%d Orientation, souradnice X: %.2f; Y: %.2f\n",(int)car.getOrientation(), car.getPosition().getX(), car.getPosition().getY());
		}

	}

}
