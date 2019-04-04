import TrafficSim.Car;
import TrafficSim.CrossRoad;
import TrafficSim.Direction;
import TrafficSim.EndPoint;
import TrafficSim.Lane;
import TrafficSim.RoadSegment;
import TrafficSim.Simulator;
import TrafficSim.TrafficLight;
import TrafficSim.TrafficLightState;

public class L04_PlanAuta {

	public static void main(String[] args) {
		Simulator sim = new Simulator();
		String[] scenarios = sim.getScenarios();
		for(int i=0; i<scenarios.length; i++) {
			System.out.println(scenarios[i]);
		}

		sim.runScenario(scenarios[0]);

		Car car = sim.getCars()[1];

		// zjištění plánu auta
		int p = 0;
		while(car.nextLanePlan(p)!=null) {
			System.out.println(car.nextLanePlan(p++));
		}
		
		
	}

}
