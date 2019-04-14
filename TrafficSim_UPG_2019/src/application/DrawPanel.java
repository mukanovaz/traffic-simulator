package application;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.Timer;

import com.sun.org.apache.xml.internal.security.Init;
import com.sun.org.apache.xpath.internal.operations.Bool;

import TrafficSim.Car;
import TrafficSim.CrossRoad;
import TrafficSim.Direction;
import TrafficSim.EndPoint;
import TrafficSim.RoadSegment;
import TrafficSim.Simulator;
import TrafficSim.TrafficLight;
import TrafficSim.TrafficLightState;
import TrafficSim.Lane;

public class DrawPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private final int MARGIN = 30;
	// Max a Min of crossroad in meters
	private double Xmax_X_in_m = 0;
	private double Xmax_Y_in_m = 0;
	private double Xmin_X_in_m = 0;
	private double Xmin_Y_in_m = 0;
	// Max a Min of crossroad in pixels
	private double min_X_px;
	private double max_X_px;
	private double min_Y_px;
	private double max_Y_px;
	// Simulator instance
	private Simulator sim;

	// List of generated lanes
	private List<Road> roadList;
	// Offset between lanes
	private double OFFSET;
	private final int ZOOM = 0;
	AffineTransform defaultTrsnsform;
	private List<Shape> shapes;
	private double scale;
	  
	public DrawPanel() {
		
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		shapes = new ArrayList<Shape>();
		
		// Initialize roads List and road Width
		roadList = new ArrayList<Road>();
		
		Graphics2D g2 = (Graphics2D)g; 
		// Set background color
		g2.setColor(new Color(230, 255, 204));
		g2.fillRect(0, 0, this.getWidth(), this.getHeight());
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.translate(MARGIN, MARGIN);
		
		computeModel2WindowTransformation(getWidth(), getHeight());
		drawTrafficState(sim, g2);	
	}
  
	private void drawCrossRoad(Graphics2D g2d) {
		
		CrossRoad[] cross = sim.getCrossroads();	  
		
		if (cross.length == 0) {
			RoadSegment[] roads = sim.getRoadSegments();
			for (RoadSegment roadSegment : roads) {
				if (roadSegment != null) drawRoadSegment(roadSegment, g2d);
			}
			return;
		}
		
		for (CrossRoad crossRoad : cross) {
			RoadSegment[] roads = crossRoad.getRoads();
			
			// Draw road segments
			for (RoadSegment roadSegment : roads) {
				if (roadSegment != null) drawRoadSegment(roadSegment, g2d);
			}
			
			// Connect roads into crossroad
			Lane[] lanes = crossRoad.getLanes();
			for (Lane lane : lanes) {
				connectLanes(lane, g2d);
			}
		}	
	}
	


	private void connectLanes(Lane lane, Graphics2D g) {
		RoadSegment s = lane.getStartRoad();
		RoadSegment e = lane.getEndRoad();
		Road startRoad = null;
		Road endRoad = null;
		
		startRoad =  roadList.stream()
			.filter(c -> c.getId().contains(s.getId()) 
					  && c.getNumber() == lane.getStartLaneNumber())
			.findAny()
			.orElse(null);	
		
		endRoad =  roadList.stream()
				.filter(c -> c.getId().contains(e.getId()) 
						&& c.getNumber() == lane.getEndLaneNumber())
				.findAny() 
				.orElse(null);	
		
		Point2D start = null;
		Point2D end = null;
		
		if (startRoad.getNumber() > 0 && endRoad.getNumber() > 0) { 	 	// + +
			start = startRoad.getEndPos();
			end = endRoad.getStartPos();		
		} 
		else if (startRoad.getNumber() < 0 && endRoad.getNumber() < 0) { 	// - -
			start = startRoad.getStartPos();
			end = endRoad.getEndPos();
		}
		else if (startRoad.getNumber() > 0 && endRoad.getNumber() < 0) { 	// + -
			start = startRoad.getEndPos();
			end = endRoad.getEndPos();
		} 
		else if (startRoad.getNumber() < 0 && endRoad.getNumber() > 0) { 	// - +
			start = startRoad.getStartPos();
			end = endRoad.getStartPos();
		}
		
		drawLane(start, end,(int) s.getLaneWidth(), g, lane);
	}
	
	private void drawCar(Point2D position, double orientation, int lenght, int width, double speed, Graphics2D g) {
		position = model2window(position);
		g.setStroke(new BasicStroke((float) width * (float) scale / 2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		
		switch (lenght) {
		case 4:
			g.setColor(new Color(242,238,215));
			break;
		case 5:
			g.setColor(new Color(242,234,184));
			break;
		case 6:
			g.setColor(new Color(243,231,156));
			break;
		case 7:
			g.setColor(new Color(233,215,140));
			break;
		default:
			g.setColor(Color.BLACK);
			break;
		}
		
		defaultTrsnsform = g.getTransform();
		g.translate(position.getX(), position.getY());

		Line2D car = new Line2D.Double(0, 0, 0, lenght-1);
		shapes.add(car);
		g.rotate(-(orientation + Math.PI / 2));
		
		if (java.lang.Double.toString(orientation) != "NaN") 
			g.draw(car);
		g.fill(car);
		g.setTransform(defaultTrsnsform);
	}
	
	private Boolean roadColor = true;
	private void drawLane(Point2D start, Point2D end, int size, Graphics2D g, Lane l) {
		double value;
		if (roadColor) {
			value = l.getSpeedAverage();
			if(value < 10) g.setColor(new Color(128,193,255)); 
			else if(10 <= value && value < 50) g.setColor(new Color(115,174,230));
			else if(50 <= value && value < 60) g.setColor(new Color(96,145,191));
			else if(60 <= value && value < 80) g.setColor(new Color(64,97,128));
			else g.setColor(new Color(79,101,128));			
		} else {
			value = l.getNumberOfCarsCurrent();
			if(value < 1) g.setColor(new Color(128,193,255)); 
			else if(1 <= value && value < 2) g.setColor(new Color(115,174,230));
			else if(2 <= value && value < 3) g.setColor(new Color(96,145,191));
			else if(3 <= value && value < 4) g.setColor(new Color(64,97,128));
			else g.setColor(new Color(79,101,128));		
		}
		
		Line2D lane = new Line2D.Double(model2window(start), model2window(end));
		System.out.println("S " + model2window(start));
		System.out.println("E " + model2window(end));
		g.setStroke(new BasicStroke((float)size * (float) scale, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER));	
		g.draw(lane);
		shapes.add(lane);
	}
	
	private void drawRoadSegment(RoadSegment road, Graphics2D g) {
		drawLane(road.getEndPointPosition(EndPoint.START), road.getEndPointPosition(EndPoint.END), (int) road.getLaneWidth(), g, road.getLane(1));
		roadList.add(new Road(1, road.getId(), road.getStartPosition(), road.getEndPosition()));
		
		double x1 = road.getStartPosition().getX();
		double y1 = road.getStartPosition().getY();
		double x2 = road.getEndPosition().getX();
		double y2 = road.getEndPosition().getY();
		
		if (road.getBackwardLanesCount() != 0 && road.getForwardLanesCount() != 0) {
			drawForwardLanes(x1, y1, x2, y2, road, g, road.getForwardLanesCount());
			x1 = road.getStartPosition().getX();
			y1 = road.getStartPosition().getY();
			x2 = road.getEndPosition().getX();
			y2 = road.getEndPosition().getY();
			drawBackwardLanes(x1, y1, x2, y2, road, g, road.getBackwardLanesCount());
		} 
		
		if (road.getForwardLanesCount() == 0) {
			drawBackwardLanes(x1, y1, x2, y2, road, g, road.getBackwardLanesCount());
		}

		if (road.getBackwardLanesCount() == 0) {
			drawForwardLanes(x1, y1, x2, y2, road, g, road.getForwardLanesCount());
		}
	}
	
	private void drawForwardLanes(double x1, double y1, double x2, double y2, RoadSegment road, Graphics2D g, int j) {
		// Draw Backward lanes 
		for (int i = 1; i < j; i++) {
			
			OFFSET = road.getLaneWidth();
			
			double ux = x1 - x2; // smerovy vektor
			double uy = y1 - y2; // smerovy vektor
			
			double L = Math.hypot(ux, uy); // velikost vektoru
			
			// This is the second line
			double x1p = x1 + OFFSET * (y2-y1) / L;
			double x2p = x2 + OFFSET * (y2-y1) / L;
			double y1p = y1 + OFFSET * (x1-x2) / L;
			double y2p = y2 + OFFSET * (x1-x2) / L;
			
			Point2D x = new Point2D.Double(x1p, y1p);
			Point2D y = new Point2D.Double(x2p, y2p);
			
			roadList.add(new Road(i + 1, road.getId(), x, y));
			drawLane(x, y, (int) road.getLaneWidth() + ZOOM, g, road.getLane(i));
			
			x1 = x1p;
			x2 = x2p;
			y1 = y1p;
			y2 = y2p;
		}
	}

	private void drawBackwardLanes(double x1, double y1, double x2, double y2, RoadSegment road, Graphics2D g, int j) {
		// Draw Forward lanes 
		for (int i = 0; i > -j; i--) {
			
			if (i == 0)
				OFFSET = (road.getLaneWidth() + road.getLaneSeparatorWidth());
			else 
				OFFSET = road.getLaneWidth();
			
			double ux = x1 - x2; // smerovy vektor
			double uy = y1 - y2; // smerovy vektor
			
			double L = Math.hypot(ux, uy); // velikost vektoru
			
			// This is the second line
			double x1p = x1 - OFFSET * (y2-y1) / L;
			double x2p = x2 - OFFSET * (y2-y1) / L;
			double y1p = y1 - OFFSET * (x1-x2) / L;
			double y2p = y2 - OFFSET * (x1-x2) / L;
			
			Point2D x = new Point2D.Double(x1p, y1p);
			Point2D y = new Point2D.Double(x2p, y2p);
			
			drawLane(x, y, (int) road.getLaneWidth() + ZOOM, g, road.getLane(i - 1));
			roadList.add(new Road(i-1, road.getId(), x, y));
			
			x1 = x1p;
			x2 = x2p;
			y1 = y1p;
			y2 = y2p;
		}
		
	}

	public void computeModelDimensions() {
		Xmax_X_in_m = 0;
		Xmax_Y_in_m = 0;
		Xmin_X_in_m = 0;
		Xmin_Y_in_m = 0;
		
		RoadSegment[] roads = sim.getRoadSegments();
		
		double[] x_array = new double[roads.length * 2];
		double[] y_array = new double[roads.length * 2];
	
		for (int i = 0; i < roads.length; i++) {
			x_array[i] = roads[i].getStartPosition().getX();
			y_array[i] = roads[i].getStartPosition().getY();
		}
		
		for (int i = roads.length, j = 0; i < roads.length * 2; i++, j++) {
			x_array[i] = roads[j].getEndPosition().getX();
			y_array[i] = roads[j].getEndPosition().getY();
		}
		
		Arrays.sort(x_array);
		Xmax_X_in_m = x_array[x_array.length - 1];
		Xmin_X_in_m = x_array[0];

		Arrays.sort(y_array);
		Xmax_Y_in_m = y_array[x_array.length - 1];
		Xmin_Y_in_m = y_array[0];
	}
	
	private void computeModel2WindowTransformation(int width, int height) {
		int r = Math.min(width, height);
		min_X_px = MARGIN;
		max_X_px = r - MARGIN;
		
		min_Y_px = MARGIN;
		max_Y_px = r - MARGIN;
	}
	
	private Point2D model2window(Point2D p) {
		double x = 0;
		double y = 0;
		
		double sx = (max_X_px - min_X_px)  / (Xmax_X_in_m - Xmin_X_in_m);
		double sy = (max_Y_px - min_Y_px)  / (Xmax_Y_in_m - Xmin_Y_in_m);
		scale = Math.min(sx,  sy);
		
		x = (p.getX() - Xmin_X_in_m) * scale;
		y = (max_Y_px - min_Y_px) - (p.getY() - Xmin_Y_in_m) * scale;
				
		return new Point2D.Double(x, y);
	}
	
	private void drawTrafficState(Simulator sim, Graphics2D g) {
		drawCrossRoad(g);
		
		Car[] cars = sim.getCars();
		
		for (int i = 0; i < cars.length; i++) {
			drawCar(cars[i].getPosition(), cars[i].getOrientation(), (int) cars[i].getLength(), 5, cars[i].getCurrentSpeed(), g);
		}
	}

	private void drawTrafficLight (Point2D start, Graphics2D g) {
		g.setColor(Color.DARK_GRAY);
		Point2D newPoint = model2window(start);
		Line2D lane = new Line2D.Double(newPoint, (new Point2D.Double(newPoint.getX(), newPoint.getY() - 25)));
		Line2D lane2 = new Line2D.Double(newPoint, (new Point2D.Double(newPoint.getX(), newPoint.getY() + 15)));
		g.setStroke(new BasicStroke((float)4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER));	
		g.draw(lane2);
		g.setStroke(new BasicStroke((float)15, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER));	
		g.draw(lane);
		drawCircle(newPoint, 5, 2, Color.gray, g);
		drawCircle(newPoint, 5, 13, Color.gray, g);
		drawCircle(newPoint, 5, 23, Color.gray, g);
	}
	
	private void drawCircle(Point2D center, int radius, double offset, Color c, Graphics2D g) {
		int diameter = radius * 2;
		Ellipse2D light = new Ellipse2D.Double(center.getX() - radius, center.getY() - offset - radius, diameter, diameter);
		g.setStroke(new BasicStroke());	
		g.setColor(c);
		g.fill(light);
	}
	
	public Simulator getSim() {
		return sim;
	}
	
	public void setSim(Simulator sim) {
		this.sim = sim;
	}

	public void setRoadColor(Boolean roadColor) {
		this.roadColor = roadColor;
	}

	
}