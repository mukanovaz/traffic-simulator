package application;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Point2D.Double;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import javax.swing.JPanel;

import TrafficSim.Car;
import TrafficSim.CrossRoad;
import TrafficSim.EndPoint;
import TrafficSim.RoadSegment;
import TrafficSim.Simulator;
import TrafficSim.Lane;

public class DrawPanel extends JPanel implements MouseWheelListener, MouseListener, MouseMotionListener {
	private static final long serialVersionUID = 1L;
	private final int MARGIN = 10;
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

	// Offset between lanes
	private double OFFSET;
	AffineTransform defaultTrsnsform;
	private double scale;
	// List of lanes to connect roads
	private List<Road> connectionList;
	// List of roads
	private List<MyLane> laneList;
	// Array to compute max and min points
	private List<Point2D> points_array;
	private List<Shape> shapes;
	private int simulation_time;
	
	private double zoomFactor = 1;
    private double prevZoomFactor = 1;
    private boolean zoomer;
    private boolean dragger;
    private boolean released;
    private double xOffset = 0;
    private double yOffset = 0;
    private int xDiff;
    private int yDiff;
    private DrawingTool dr = new DrawingTool();
    
    AffineTransform saveTransform;
    AffineTransform at;   // the current pan and zoom transform
    double translateX;
	double translateY;
    
	public DrawPanel() {
		initComponent();
	}
	
	private void initComponent() {
		addMouseWheelListener(this);
		addMouseMotionListener(this);
		addMouseListener(this);
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		shapes = new ArrayList<Shape>();
		
		Graphics2D g2 = (Graphics2D)g; 
		// Set background color
		g2.setColor(new Color(230, 255, 204));
		g2.fillRect(0, 0, this.getWidth(), this.getHeight());
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		computeModel2WindowTransformation(getWidth(), getHeight());
		setZoomer(g2);
		setDragger(g2);
        
		// Draw crossroads
		g2.translate(MARGIN, MARGIN);
		
		drawTrafficState(sim, g2);	
		
		
	}
	
	private void setDragger(Graphics2D g2) {
		saveTransform = g2.getTransform();
		at = new AffineTransform(saveTransform);
		at.translate(translateX, translateY);
		g2.setTransform(at);
	}

	private void setZoomer(Graphics2D g2) {
		if (zoomer) {
            
			AffineTransform at = new AffineTransform();
            double xRel = MouseInfo.getPointerInfo().getLocation().getX() - getLocationOnScreen().getX();
            double yRel = MouseInfo.getPointerInfo().getLocation().getY() - getLocationOnScreen().getY();
            double zoomDiv = zoomFactor / prevZoomFactor;

            xOffset = (zoomDiv) * (xOffset) + (1 - zoomDiv) * xRel;
            yOffset = (zoomDiv) * (yOffset) + (1 - zoomDiv) * yRel;

            at.translate(xOffset, yOffset);
            at.scale(zoomFactor, zoomFactor);
            prevZoomFactor = zoomFactor;
            g2.transform(at);
            zoomer = false;
        } else {
        	AffineTransform at = new AffineTransform();
        	at.translate(xOffset, yOffset);
            at.scale(zoomFactor, zoomFactor);
            g2.transform(at);
        }
	}

	public void computeModelDimensions() {
		points_array = new ArrayList<>();
		connectionList = new ArrayList<Road>();
		laneList = new ArrayList<MyLane>();
		
		Xmax_X_in_m = 0;
		Xmax_Y_in_m = 0;
		Xmin_X_in_m = 0;
		Xmin_Y_in_m = 0;
		
		// fill points_array, laneList and connectionList
		getRoadsDimensions();
		
		Xmin_X_in_m = points_array
			      .stream()
			      .min(Comparator.comparing(Point2D::getX))
			      .get().getX(); 
		Xmax_X_in_m = points_array
			      .stream()
			      .max(Comparator.comparing(Point2D::getX))
			      .get().getX(); 
		Xmax_Y_in_m = points_array
			      .stream()
			      .max(Comparator.comparing(Point2D::getY))
			      .get().getY(); 
		Xmin_Y_in_m = points_array
			      .stream()
			      .min(Comparator.comparing(Point2D::getY))
			      .get().getY(); 
		
	}

	private void computeModel2WindowTransformation(int width, int height) {
		min_X_px = MARGIN;
		max_X_px = width - MARGIN;
		
		min_Y_px = MARGIN;
		max_Y_px = height - MARGIN;
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
			drawCar(cars[i].getPosition(), cars[i].getOrientation(), (int) cars[i].getLength(), (int) laneSize, cars[i].getCurrentSpeed(), g);
		}
	}
  
	private void drawCrossRoad(Graphics2D g2d) {
		drawRoadSegment(g2d);
		
		CrossRoad[] cross = sim.getCrossroads();	
		for (CrossRoad crossRoad : cross) {
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
		
		startRoad =  connectionList.stream()
			.filter(c -> c.getId().contains(s.getId()) 
					  && c.getNumber() == lane.getStartLaneNumber())
			.findAny()
			.orElse(null);	
		
		endRoad =  connectionList.stream()
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
		g.setStroke(new BasicStroke((float) width, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
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
		g.rotate(-(orientation + Math.PI / 2));
		
		if (java.lang.Double.toString(orientation) != "NaN") 
			g.draw(car);
		g.fill(car);
		g.setTransform(defaultTrsnsform);
	}
	
	private float laneSize;
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
		
		Point2D s = model2window(start);
		Point2D e = model2window(end);
		
		  GeneralPath path = new GeneralPath();
	        path.moveTo(s.getX(), s.getY());
	        path.lineTo(e.getX(), e.getY());
	        path.closePath();

		shapes.add(lane);
		laneSize = (float)size * (float) scale;
		g.setStroke(new BasicStroke((float)size * (float) scale, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER));	
		g.draw(lane);
	}
	
	private void drawRoadSegment(Graphics2D g) {
		for (MyLane myLane : laneList) {
			drawLane(myLane.getStart(), myLane.getEnd(), (int) myLane.getSize(), g, myLane.getLine());
//			lanesDataSet.put(myLane.getLine(), new DataSet(simulation_time, myLane.getLine().getNumberOfCarsTotal()));
		}
	}
	
	private void getRoadsDimensions() {
		RoadSegment[] roads = sim.getRoadSegments();
		for (RoadSegment road : roads) {
			points_array.add(road.getEndPointPosition(EndPoint.START));
			points_array.add(road.getEndPointPosition(EndPoint.END));
			laneList.add(new MyLane(road.getEndPointPosition(EndPoint.START), road.getEndPointPosition(EndPoint.END), (int) road.getLaneWidth(), road.getLane(1)));
			connectionList.add(new Road(1, road.getId(), road.getStartPosition(), road.getEndPosition()));
			
			double x1 = road.getStartPosition().getX();
			double y1 = road.getStartPosition().getY();
			double x2 = road.getEndPosition().getX();
			double y2 = road.getEndPosition().getY();
			
			if (road.getBackwardLanesCount() != 0 && road.getForwardLanesCount() != 0) {
				drawForwardLanes(x1, y1, x2, y2, road, road.getForwardLanesCount());
				x1 = road.getStartPosition().getX();
				y1 = road.getStartPosition().getY();
				x2 = road.getEndPosition().getX();
				y2 = road.getEndPosition().getY();
				drawBackwardLanes(x1, y1, x2, y2, road, road.getBackwardLanesCount());
			} 
			
			if (road.getForwardLanesCount() == 0) {
				drawBackwardLanes(x1, y1, x2, y2, road, road.getBackwardLanesCount());
			}
			
			if (road.getBackwardLanesCount() == 0) {
				drawForwardLanes(x1, y1, x2, y2, road, road.getForwardLanesCount());
			}
		}
	}
	
	private void drawForwardLanes(double x1, double y1, double x2, double y2, RoadSegment road, int j) {
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
			
			points_array.add(x);
			points_array.add(y);

			connectionList.add(new Road(i + 1, road.getId(), x, y));
			laneList.add(new MyLane(x, y, (int) road.getLaneWidth(), road.getLane(i)));
		
			x1 = x1p;
			x2 = x2p;
			y1 = y1p;
			y2 = y2p;
		}
	}

	private void drawBackwardLanes(double x1, double y1, double x2, double y2, RoadSegment road, int j) {
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
			
			points_array.add(x);
			points_array.add(y);
			
			laneList.add(new MyLane(x, y, (int) road.getLaneWidth(), road.getLane(i - 1)));
			connectionList.add(new Road(i-1, road.getId(), x, y));
			
			x1 = x1p;
			x2 = x2p;
			y1 = y1p;
			y2 = y2p;
		}
		
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
	
	private int xPos,yPos;
	private Point2D startPoint;
	private double referenceX;
	private double referenceY;
	// saves the initial transform at the beginning of the pan interaction
	AffineTransform initialTransform;
		
	@Override
	public void mouseDragged(MouseEvent e) {
		 // first transform the mouse point to the pan and zoom
	    // coordinates. We must take care to transform by the
	    // initial tranform, not the updated transform, so that
	    // both the initial reference point and all subsequent
	    // reference points are measured against the same origin.
	    try {
	    	startPoint = initialTransform.inverseTransform(e.getPoint(), null);
	    }
	    catch (NoninvertibleTransformException te) {
	    	System.out.println(te);
	    }

	    // the size of the pan translations 
	    // are defined by the current mouse location subtracted
	    // from the reference location
	    double deltaX = startPoint.getX() - referenceX;
	    double deltaY = startPoint.getY() - referenceY;

	    // make the reference point be the new mouse point. 
	    referenceX = startPoint.getX();
	    referenceY = startPoint.getY();
	    
	    translateX += deltaX;
	    translateY += deltaY;
 
	    // schedule a repaint.
	    repaint();
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		for (int i = 0; i < shapes.size(); i++) {
            Shape shape = shapes.get(i);
            
            Rectangle2D range = new Rectangle2D.Double(e.getX() - laneSize, e.getY() - laneSize, laneSize, laneSize);
            
            if (shape.intersects(range)) {
            	System.out.println(i);
//                LaneGraph w = new LaneGraph();
//                w.NewScreen();
            }
        }
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		try {
			startPoint = at.inverseTransform(e.getPoint(), null);
		}
		catch (NoninvertibleTransformException te) {
			System.out.println(te);
		}
		
		// save the transformed starting point and the initial
	    // transform
	    referenceX = startPoint.getX();
	    referenceY = startPoint.getY();
	    initialTransform = at;
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
	}

	public void setZoomFactor(double zoomFactor) {
		this.zoomFactor = zoomFactor;
	}
	
	public double getZoomFactor() {
		return this.zoomFactor;
	}

	public void setxDiff(int xDiff) {
		this.xDiff = xDiff;
	}

	public void setyDiff(int yDiff) {
		this.yDiff = yDiff;
	}

	public void setSimulation_time(int simulation_time) {
		this.simulation_time = simulation_time;
	}
}

