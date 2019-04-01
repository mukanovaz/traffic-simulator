package application;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.Timer;

import TrafficSim.Car;
import TrafficSim.CrossRoad;
import TrafficSim.EndPoint;
import TrafficSim.RoadSegment;
import TrafficSim.Simulator;
import TrafficSim.Lane;

public class DrawPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private final int MARGIN = 0;
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
	// Relative road width
	private double stroke;
	// List of generated lanes
	private List<Road> roadList;
	// Offset between lanes
//	private final int OFFSET = 5;
	private double OFFSET;
	private final int ZOOM = 0;
	AffineTransform defaultTrsnsform;
  
	public DrawPanel(Simulator sim) {
		this.sim = sim;
	}
  
	@Override
	protected void paintComponent(Graphics g) {
		Xmax_X_in_m = 0;
		Xmax_Y_in_m = 0;
		Xmin_X_in_m = 0;
		Xmin_Y_in_m = 0;
		
		// Initialize roads List and road Width
		roadList = new ArrayList<Road>();
		stroke = Math.min(getWidth(), getHeight()) * 0.012;
		
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D)g; 
		defaultTrsnsform = g2d.getTransform();
		
		computeModelDimensions();
		computeModel2WindowTransformation(getWidth(), getHeight());
		
		// Set background color
		g2d.setColor(new Color(230, 255, 204));
		g2d.fillRect(0, 0, this.getWidth(), this.getHeight());
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		g2d.translate(0, getHeight());
		drawTrafficState(sim, g2d);
	}
  
	private void drawCrossRoad(Graphics2D g2d) {
		
		CrossRoad[] cross = sim.getCrossroads();	    
		
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
		
		drawLane(start, end,(int) s.getLaneWidth(), g);
	}
	
	private void drawCar(Point2D position, double orientation, int lenght, int width, Graphics2D g) {
		position = model2window(position);
		g.setStroke(new BasicStroke((float) stroke));
		g.setColor(Color.BLACK);
		
		Rectangle2D car = new Rectangle2D.Double(position.getX(), position.getY(), 0.5, lenght);
//		Rectangle2D car = new Rectangle2D.Double(0, 0, 0.5, lenght);

		if (java.lang.Double.toString(orientation) != "NaN") 
			g.draw(car);
		g.fill(car);
	}
	
	private void drawLane(Point2D start, Point2D end, int size, Graphics2D g) {
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		Line2D lane = new Line2D.Double(model2window(start), model2window(end));
		
		g.setStroke(new BasicStroke((float)stroke, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER));
		g.setColor(Color.GRAY);
		g.draw(lane);
	}
	
	private void drawRoadSegment(RoadSegment road, Graphics2D g) {
		
		drawLane(road.getEndPointPosition(EndPoint.START), road.getEndPointPosition(EndPoint.END), (int) road.getLaneWidth(), g);
//		drawLane(road.getStartPosition(), road.getEndPosition(), (int) road.getLaneWidth(), g);
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
			drawLane(x, y, (int) road.getLaneWidth() + ZOOM, g);
			
			roadList.add(new Road(i + 1, road.getId(), x, y));
			
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
				OFFSET = road.getLaneWidth() + road.getLaneSeparatorWidth();
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
			drawLane(x, y, (int) road.getLaneWidth() + ZOOM, g);
			
			roadList.add(new Road(i-1, road.getId(), x, y));
			
			x1 = x1p;
			x2 = x2p;
			y1 = y1p;
			y2 = y2p;
		}
		
	}

	private void computeModelDimensions() {
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
		double scale = (max_X_px - min_X_px)  / (Xmax_X_in_m - Xmin_X_in_m);
		
		x = (p.getX() - min_X_px) * scale;	
		y = (double) - ((p.getY() - Xmin_Y_in_m) / (Xmax_Y_in_m - Xmin_Y_in_m) * (max_Y_px - min_Y_px) + min_Y_px);			
		
		return new Point2D.Double(x, y);
	}
	
	private void drawTrafficState(Simulator sim, Graphics2D g) {
		drawCrossRoad(g);
		
		Car[] cars = sim.getCars();
		
		for (int i = 0; i < cars.length; i++) {
			drawCar(cars[i].getPosition(), cars[i].getOrientation(), (int) cars[i].getLength(), 5, g);
		}
	}
}