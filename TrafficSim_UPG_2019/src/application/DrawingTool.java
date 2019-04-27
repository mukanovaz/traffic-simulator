package application;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

public class DrawingTool {

	public DrawingTool() {
	}
	
	public void drawTrafficLight (Point2D pos, double scale, Graphics2D g) {
		g.setColor(Color.DARK_GRAY);
		Line2D lane = new Line2D.Double(pos, (new Point2D.Double(pos.getX(), pos.getY() - 10 * scale)));
		Line2D lane2 = new Line2D.Double(pos, (new Point2D.Double(pos.getX(), pos.getY() + 7 * scale)));
		g.setStroke(new BasicStroke((float)scale, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER));	
		g.draw(lane2);
		g.setStroke(new BasicStroke((float)scale * 6, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER));	
		g.draw(lane);
		
		int r = (int) scale * 3;
		drawCircle(pos, r, 2, Color.gray, g);
		drawCircle(pos, r, r * scale, Color.gray, g);
		drawCircle(pos, r, r * scale * 2 , Color.gray, g);
	}
	
	private void drawCircle(Point2D center, int radius, double offset, Color c, Graphics2D g) {
		int diameter = radius * 2;
		Ellipse2D light = new Ellipse2D.Double(center.getX() - radius, center.getY() - offset - radius, diameter, diameter);
		g.setStroke(new BasicStroke());	
		g.setColor(c);
		g.fill(light);
	}

	public void drawLargeCar (Point2D pos, double orientation, int lenght, int width, double speed, Graphics2D g) {
		AffineTransform defaultTrsnsform = g.getTransform();
		g.translate(pos.getX(), pos.getY());
		g.rotate(-(orientation + Math.PI / 2));

		g.setColor(Color.BLACK);
		Line2D car = new Line2D.Double(0, 0, 0, lenght-1);
		g.setStroke(new BasicStroke((float) width, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_ROUND));
//		if (java.lang.Double.toString(orientation) != "NaN") 
			g.draw(car);
		g.fill(car);
		
//		g.setColor(Color.RED);
//		Line2D cargo = new Line2D.Double(0, 0, 0, lenght / 4);
//		g.setStroke(new BasicStroke((float) width, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_ROUND));
//		if (java.lang.Double.toString(orientation) != "NaN") 
//			g.draw(cargo);

//		g.fill(cargo);
		g.setTransform(defaultTrsnsform);
	}

}
