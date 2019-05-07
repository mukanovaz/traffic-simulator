package application;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FontMetrics;
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
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.io.File;
import java.io.IOException;
import java.awt.geom.Point2D.Double;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.JLabel;
import javax.swing.JPanel;


import TrafficSim.Car;
import TrafficSim.CrossRoad;
import TrafficSim.Direction;
import TrafficSim.EndPoint;
import TrafficSim.RoadSegment;
import TrafficSim.Simulator;
import TrafficSim.TrafficLight;
import TrafficSim.Lane;

public class DrawPanel extends JPanel  implements Printable{
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

	// Offset between lanes
	private double OFFSET;
	private AffineTransform defaultTrsnsform;
	private double scale;
	// List of lanes to connect roads
	private List<Road> connectionList;
	// List of roads
	private List<MyLane> laneList;
	// Array to compute max and min points
	private List<Point2D> points_array;
	// Array of lanes to MouseClieck action
	private HashMap<Shape, Lane> roads;
	// Statistic data
	private HashMap<Lane, List<DataSet>> dataSet = new HashMap<Lane, List<DataSet>>();
	private float laneSize;
	
	// Change lanes color type
	private boolean roadColor = true;
	// Show car speed labels 
	private boolean speedVisible = false;
	
	// Zoomer parameters
	private double zoomFactor = 1;
    private double prevZoomFactor = 1;
    private boolean zoomer;
    private double xOffset = 0;
    private double yOffset = 0;

    // Panner parameters
    private AffineTransform saveTransform;
    private AffineTransform at;   // the current pan and zoom transform
    private double translateX;
    private double translateY;
	
    // Cars images
	private BufferedImage car1;
	private BufferedImage car2;
	private BufferedImage car3;
    
	public DrawPanel() {}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		Graphics2D g2 = (Graphics2D)g; 
		drawComponent(g2);
	}
	
	/** 
	 * Set current position
	 * @param g2 Graphics context
	 */
	private void setDragger(Graphics2D g2) {
		saveTransform = g2.getTransform();
		at = new AffineTransform(saveTransform);
		at.translate(translateX, translateY);
		g2.setTransform(at);
	}

	/**
	 * Set current zoom position
	 * @param g2 Graphics context
	 */
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

	/**
	 * 1. Compute model dimensions and set maximum and minimum points of cross road.
	 * 2. Fill laneList to future roads drawing
	 */
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

	/**
	 * Set cars images 
	 */
	public void setCarsImages() {
		try {
			car1 = ImageIO.read(new File("img/1.jpg"));
			car2 = ImageIO.read(new File("img/2.jpg"));
			car3 = ImageIO.read(new File("img/3.jpg"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Set window dimensions
	 * @param width		width of window
	 * @param height	height of window
	 */
	private void computeModel2WindowTransformation(int width, int height) {
		min_X_px = MARGIN;
		max_X_px = width - MARGIN;
		
		min_Y_px = MARGIN;
		max_Y_px = height - MARGIN;
	}
	
	/**
	 * Compute new position of points on window
	 * World -> Window
	 * @param p Point in "World"
	 * @return	Point in "Window"
	 */
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

	/**
	 * Draw Traffic State
	 * @param sim	Simulator instance
	 * @param g		Graphic context
	 */
	private void drawTrafficState(Simulator sim, Graphics2D g) {
		drawCrossRoad(g);
		
		Car[] cars = sim.getCars();
		
		for (int i = 0; i < cars.length; i++) {
			drawCar(cars[i], (int) laneSize, g);
		}
	}
  
	/**
	 * Draw all roads and connect it
	 * @param g2d	Graphic context
	 */
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
	
	/**
	 * Connect lines in crossroad
	 * @param lane	Lane
	 * @param g		Graphic context
	 */
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
	
	/**
	 * Draw car with specific car type
	 * @param car		Car
	 * @param length	Length
	 * @param g			Graphic context
	 */
	private void drawCar(Car car, int lenght, Graphics2D g) {
		int w = (int) car.getLength();
		int width = (int) car.getLength() * (int) scale;
		Point2D position = model2window(car.getPosition());
		
		defaultTrsnsform = g.getTransform();
		g.translate(position.getX(), position.getY());
		drawSpeedString(speedVisible, car.getCurrentSpeed(), g);

		g.rotate(-(car.getOrientation() + Math.PI / 2));
		g.setStroke(new BasicStroke(4));
		g.setColor(Color.black);

		switch (w) {
		case 4: // 8
			g.drawImage(car2, - lenght / 2, - width / 2, lenght/2, width/2, 0, 0, (int)(car2.getWidth()), (int)(car2.getHeight()), null);
			break;
		case 5: // 10
			g.drawImage(car3,  - lenght / 2, - width / 2, lenght/2, width/2, 0, 0, (int)(car3.getWidth()), (int)(car3.getHeight()), null);
			break;
		case 6: // 12
			g.drawImage(car1,  - lenght / 2, - width / 2, lenght/2, width/2, 0, 0, (int)(car1.getWidth()), (int)(car1.getHeight()), null);
			break;
		case 7: // 14
			g.drawImage(car1,  - lenght / 2, - width / 2, lenght/2, width/2, 0, 0, (int)(car1.getWidth()), (int)(car1.getHeight()), null);
			break;
		default:
			g.setColor(Color.BLACK);
			break;
		}		
		
		g.setTransform(defaultTrsnsform);
	}
	
	/**
	 * Draw speed labels near a car
	 * @param b			Is needed
	 * @param speed		Speed value
	 * @param g			Graphic context
	 */
	private void drawSpeedString(boolean b, double speed, Graphics2D g) {
		if (b) {
			FontMetrics metrics = g.getFontMetrics(g.getFont());
			
			g.setColor(Color.BLACK);
			String text = String.format("%.3g", speed);
			double xF = 0 - metrics.stringWidth(text) - 15;
			double yF = 0 + metrics.getAscent()/3.0;
			g.drawString(text, (float) xF, (float) yF);
		}
	}

	/**
	 * Draw lane
	 * @param start	Start position
	 * @param end	End position
	 * @param size	Lane size
	 * @param g		Graphic context
	 * @param l		Lane object
	 */
	private void drawLane(Point2D start, Point2D end, int size, Graphics2D g, Lane l) {		
		Line2D lane = new Line2D.Double(model2window(start), model2window(end));
		laneColor(l, g);
		laneSize = (float)size * (float) scale;
		g.setStroke(new BasicStroke((float)size * (float) scale, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER));	
				
		Point2D s = model2window(start);
		Point2D e = model2window(end);
		
		GeneralPath path = new GeneralPath();
		path.moveTo(s.getX(), s.getY());
		path.lineTo(e.getX(), e.getY());
		path.closePath();
		roads.put(path, l);
		g.draw(lane);
	}
	
	/**
	 * Color lane
	 * @param l		Lane object
	 * @param g		Graphic context
	 */
	private void laneColor(Lane l, Graphics2D g) {
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
		
	}

	/**
	 * Draw all roads
	 * @param g		Graphic context
	 */
	private void drawRoadSegment(Graphics2D g) {
		roads = new HashMap<Shape, Lane>();
		for (MyLane myLane : laneList) {
			drawLane(myLane.getStart(), myLane.getEnd(), (int) myLane.getSize(), g, myLane.getLine());
		}
	}
	
	/**
	 * Compute road dimensions (max and min points)
	 * And fill laneList to draw roads and connectionList to draw connect lines
	 */
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
			laneList.add(new MyLane(x, y, (int) road.getLaneWidth(), road.getLane(i+1)));
		
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
	
	/**
	 * Draw all crossroad
	 * @param g2d
	 */
	public void drawComponent(Graphics2D g2d) {	
		// Set background color
		g2d.setColor(new Color(230, 255, 204));
		g2d.fillRect(0, 0, this.getWidth(), this.getHeight());
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		computeModel2WindowTransformation(getWidth(), getHeight());
		setZoomer(g2d);
		setDragger(g2d);
		
		// Draw crossroads
		g2d.translate(MARGIN, MARGIN);
		drawTrafficState(sim, g2d);	
	}
	
	/**
	 * Print method
	 */
	@Override
	public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
		if (pageIndex > 0) {
			return NO_SUCH_PAGE;
		}
		
		Graphics2D g2 = (Graphics2D)graphics;
		
		g2.rotate(Math.toRadians(90));
		g2.translate(pageFormat.getImageableX(), pageFormat.getImageableY());

		// Samotne vykresleni obsahu
		drawComponent(g2);
		return PAGE_EXISTS;
	}

	/**
	 * Every timer tick will add to structure new statistic values 
	 * @param i
	 */
	public void updateDataSet(double i) {
		for (MyLane myLane : laneList) {
			Lane lane = myLane.getLine();
			if (!dataSet.containsKey(lane)) {
				List<DataSet> d = new ArrayList<DataSet>();
				d.add(new DataSet(i, lane.getNumberOfCarsCurrent(), lane.getNumberOfCarsTotal(), lane.getSpeedAverage()));
				dataSet.put(lane, d);
			} else {
				List<DataSet> d = dataSet.get(lane);
				d.add(new DataSet(i, lane.getNumberOfCarsCurrent(), lane.getNumberOfCarsTotal(), lane.getSpeedAverage()));
				dataSet.replace(lane, d);
			}
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
	
	public void setZoomFactor(double zoomFactor) {
		this.zoomFactor = zoomFactor;
	}
	
	public double getZoomFactor() {
		return this.zoomFactor;
	}

	public double getTranslateX() {
		return translateX;
	}

	public void setTranslateX(double translateX) {
		this.translateX = translateX;
	}

	public double getTranslateY() {
		return translateY;
	}

	public void setTranslateY(double translateY) {
		this.translateY = translateY;
	}

	public AffineTransform getAt() {
		return at;
	}

	public HashMap<Shape, Lane> getRoads() {
		return roads;
	}

	public float getLaneSize() {
		return laneSize;
	}

	public void setSpeedVisible(boolean b) {
		speedVisible = b;
	}

	public HashMap<Lane, List<DataSet>> getDataSet() {
		return dataSet;
	}

}

