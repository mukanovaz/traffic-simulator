import java.awt.geom.Point2D;

import TrafficSim.Car;
import TrafficSim.Direction;
import TrafficSim.Simulator;
import TrafficSim.TimeTable;
import TrafficSim.Scenarios.Scenario;

public class L03_VytvoreniScenare {

	public static void main(String[] args) {		
		Simulator sim = new Simulator();

		// vytvoreni instance scenare
		Scenario scenar = new VlastniScenar(sim);

		// pridani scenare do simulatoru
		sim.addScenario(scenar);

		// vytvoreni a spusteni scenare 
		sim.runScenario(scenar.getId());

		// smycka po 1 sekunde 30 sekund scenare
		for(int i=0; i<30; i++) {
			// vypisani stavu semaforu z vedlejsi vpravo na hlavni
			System.out.println(sim.getCrossroads()[0].getTrafficLights(Direction.Right)[Direction.Right.ordinal()].getCurrentState());
			sim.nextStep(1);
		}
		String[] d = sim.getScenarios();
	}

}

class VlastniScenar extends TrafficSim.Scenarios.Scenario {

	/**
	 * V konstruktoru je nutne priradit identifikator (idealne jedinecny, nikdo to nehlida, v pripade duplicity neni 
	 * definovane chovani)
	 * @param simulator instance simulatoru
	 */
	public VlastniScenar(Simulator simulator) {
		super(simulator);
	}

	@Override
	public String getId() {
		// Jednoznacny identifikator scenare
		return "Vlastni scenar";
	}
	
	@Override
	public void create() {
		// Vytvoreni silnic
		String hlavni1 = simulator.addStraightRoad("Hlavni silnice", new Point2D.Double(0,0), new Point2D.Double(100,0), 2, 1, 2);	
		String hlavni2 = simulator.addStraightRoad("Hlavni silnice", new Point2D.Double(115,0), new Point2D.Double(200,0), 1, 1, 2);
		String vedlejsi1 = simulator.addStraightRoad("Vedlejsi silnice",  new Point2D.Double(105,-10), new Point2D.Double(105,-100), 1, 1, 0);

		// Vytvoreni krizovatky
		String krizovatka = simulator.addCrossRoad(hlavni1, vedlejsi1, hlavni2, null);

		// Propojeni silnic
		simulator.addCrossRoadConnection(krizovatka, Direction.Entry, 1, Direction.Opposite, 1); // primy smer
		simulator.addCrossRoadConnection(krizovatka, Direction.Entry, 2, Direction.Right, 1); // odboceni vpravo		
		simulator.addCrossRoadConnection(krizovatka, Direction.Right, -1, Direction.Right, 1); // napojeni z vedlejsi na hlavni vpravo
		simulator.addCrossRoadConnection(krizovatka, Direction.Opposite, -1, Direction.Opposite, -1); // primy smer (v opacnem smeru)

		// Pridani semaforu pro rizeni napojeni vedljesi-hlavni 
//		simulator.addCrossRoadTrafficLight(krizovatka, Direction.Entry, Direction.Opposite, new TimeTable(30,2,2).addInterval(2, 15)); // semafor na hlavni silnici rovne
//		simulator.addCrossRoadTrafficLight(krizovatka, Direction.Right, Direction.Right, new TimeTable(30,2,2).addInterval(17, 30)); // semafor na vedlejsi vpravo (jinam to ani nejde)

		// Pridani aut
		for(int i=0; i<10; i++)
			simulator.addCar(new Car());
	}

}