import TrafficSim.CrossRoad;
import TrafficSim.Direction;
import TrafficSim.EndPoint;
import TrafficSim.Lane;
import TrafficSim.RoadSegment;
import TrafficSim.Simulator;
import TrafficSim.TrafficLight;
import TrafficSim.TrafficLightState;

public class L02_TopologieSite {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		// Vytvoreni simulatoru
		Simulator sim = new Simulator();
		
		// Vypis existujicich scenaru
		String[] scenarios = sim.getScenarios();
		for(int i=0; i<scenarios.length; i++) {
			System.out.println(scenarios[i]);
		}

		sim.runScenario(scenarios[0]);
		
		// Silnice
		// =======
		RoadSegment[] roads = sim.getRoadSegments();
		System.out.println("Pocet silnic: " + roads.length);
		for (int i = 0; i < roads.length; i++) {
			System.out.println(i + ". silnice: " + roads[i]);
			System.out.println("\tPocet pruhu v jednom smeru: " + roads[i].getForwardLanesCount());
			System.out.println("\tPocet pruhu v druhem smeru: " + roads[i].getForwardLanesCount());
			System.out.println("\tSirka jizdnich pruhu: " + roads[i].getLaneWidth());
			System.out.println("\tSirka deliciho pruhu: " + roads[i].getLaneSeparatorWidth()*i);
			System.out.println("\tSouradnice zacatku a konce: " + roads[i].getStartPosition() + "; " + roads[i].getEndPosition());
			System.out.println("\tMaximalni povolena rychlost v levem jizdnim pruhu ve smeru od zacatku ke konci: "+roads[i].getLane(1).getSpeedLimit());
		}
		
		// Krizovatky
		// ==========
		CrossRoad[] cross = sim.getCrossroads();
		System.out.println("Pocet krizovatek: " + cross.length);
		System.out.println("1. krizovatka: " + cross[0]);
		
		// Silnice vstupujici do krizovatky
		// --------------------------------
		roads = cross[0].getRoads();
		EndPoint[] ends = cross[0].getEndPoints();
		System.out.println("\tPripojene silnice: ");
		for(int i=0; i<roads.length; i++) {
			if(roads[i]==null) continue;
			if(ends[i]==EndPoint.START) 
				System.out.print("\t\tzacatek silnice: ");
			else 
				System.out.print("\t\tkonec silnice: ");
			System.out.println(roads[i].getId() + " souradnice: "+ roads[i].getEndPointPosition(ends[i]));
		}

		// Propojky v krizovatce
		// ---------------------
		for (int j = 0; j < cross.length; j++) {
			Lane[] lanes = cross[j].getLanes();
			for (int i = 0; i < lanes.length; i++) {
				System.out.println("\t"+ i + ". propojeni: " + cross[0]);		
				System.out.println("\t\t ze silnice: "+lanes[i].getStartRoad());
				System.out.println("\t\t z pruhu: "+lanes[i].getStartLaneNumber());
				System.out.println("\t\t na silnici: "+lanes[i].getEndRoad());
				System.out.println("\t\t na pruh: "+lanes[i].getEndLaneNumber());
				System.out.println("\t\t projelo aut: "+lanes[i].getNumberOfCarsTotal());
				System.out.println("\t\t prumerna rychlost: "+lanes[i].getSpeedAverage());
				System.out.println("\t\t povolena rychlost: "+lanes[i].getSpeedLimit());
			}
		}
		
		// Semafory
		// --------
		System.out.println("\tsemafory ze silnice: " + roads[Direction.Entry.ordinal()]);		
		TrafficLight[] lights = cross[0].getTrafficLights(Direction.Entry);
		
		for(int i=1 /* od 1, 0 by byl semafor zpet... */; i<lights.length; i++) {
			if(lights[i]==null) continue;
			System.out.println("\t\tdo smeru: "+Direction.values()[i]+" stav: " + lights[i].getCurrentState());
		}
		
//		System.out.println("\t\trozvrh semaforu (po sekundach): " + Direction.Entry + "->" + Direction.Left);
//		TrafficLightState[] states = lights[Direction.Left.ordinal()].getTimeTable();
//		System.out.print("\t\t\t");
//		for(int i=0; i<states.length; i++) {
//			System.out.print(" "+ states[i]);
//		}
		
	}

}
