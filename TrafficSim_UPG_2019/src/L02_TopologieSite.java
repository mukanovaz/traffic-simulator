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

				sim.runScenario("Zakladni scenar");
//				
//				// Silnice
//				// =======
				RoadSegment[] roads = sim.getRoadSegments();
//				System.out.println("Pocet silnic: " + roads.length);
//				System.out.println("1. silnice: " + roads[0]);
//				System.out.println("\tPocet pruhu v jednom smeru: " + roads[0].getForwardLanesCount());
//				System.out.println("\tPocet pruhu v druhem smeru: " + roads[0].getForwardLanesCount());
//				System.out.println("\tSirka jizdnich pruhu: " + roads[0].getLaneWidth());
//				System.out.println("\tSirka deliciho pruhu: " + roads[0].getLaneSeparatorWidth());
//				System.out.println("\tSouradnice zacatku a konce: " + roads[0].getStartPosition() + "; " + roads[0].getEndPosition());
//				System.out.println("\tMaximalni povolena rychlost v levem jizdnim pruhu ve smeru od zacatku ke konci: "+roads[0].getLane(1).getSpeedLimit());
//				
				// Krizovatky
				// ==========
				CrossRoad[] cross = sim.getCrossroads();
//				System.out.println("Pocet krizovatek: " + cross.length);
//				System.out.println("1. krizovatka: " + cross[0]);
//				
//				// Silnice vstupujici do krizovatky
//				// --------------------------------
				roads = cross[0].getRoads();
//				EndPoint[] ends = cross[0].getEndPoints();
//				System.out.println("\tPripojene silnice: ");
//				for(int i=0; i<roads.length; i++) {
//					if(roads[i]==null) continue;
//					if(ends[i]==EndPoint.START) 
//						System.out.print("\t\tzacatek silnice: ");
//					else 
//						System.out.print("\t\tkonec silnice: ");
//					System.out.println(roads[i].getId() + " souradnice: "+ roads[i].getEndPointPosition(ends[i]));
//				}
//
//				// Propojky v krizovatce
//				// ---------------------
//				Lane[] lanes = cross[0].getLanes();
//				System.out.println("\t1. propojeni: " + cross[0]);		
//					System.out.println("\t\tze silnice: "+lanes[0].getStartRoad());
//					System.out.println("\t\tz pruhu: "+lanes[0].getStartLaneNumber());
//					System.out.println("\t\tna silnici: "+lanes[0].getEndRoad());
//					System.out.println("\t\tna pruh: "+lanes[0].getEndLaneNumber());
//					System.out.println("\t\tprojelo aut: "+lanes[0].getNumberOfCarsTotal());
//					System.out.println("\t\tprumerna rychlost: "+lanes[0].getSpeedAverage());
//					System.out.println("\t\tpovolena rychlost: "+lanes[0].getSpeedLimit());
//				
				// Semafory
				// --------
				System.out.println("\tsemafory ze silnice: " + roads[Direction.Entry.ordinal()]);		
				TrafficLight[] lights = cross[0].getTrafficLights(Direction.Entry);
				
				for(int i=1 /* od 1, 0 by byl semafor zpet... */; i<lights.length; i++) {
					if(lights[i]==null) continue;
					System.out.println("\t\tdo smeru: "+Direction.values()[i]+" stav: " + lights[i].getCurrentState());
				}
				
				if (lights[Direction.Left.ordinal()] != null) {		
					System.out.println("\t\trozvrh semaforu (po sekundach): " +Direction.Entry + "->" + Direction.Left);
					TrafficLightState[] states = lights[Direction.Left.ordinal()].getTimeTable();
					System.out.print("\t\t\t");
					for(int i=0; i<states.length; i++) {
						System.out.print(" "+ states[i]);
					}
				}

		
	}

}
