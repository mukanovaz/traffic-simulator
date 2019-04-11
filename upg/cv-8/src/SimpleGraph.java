import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Ellipse2D.Double;
import java.util.Arrays;


public class SimpleGraph {
	private double[] xCoord;
	private double[] yCoord;
	private Graphics2D g2;
	private double graphW;
	private double graphH;
	private double xMin, xMax, yMin, yMax;
	private double xRange, yRange;
	private double dotDiam = 6;
	private Color axesColor = Color.BLACK;
	private Color dataColor;
	private Color gridColor = new Color(200, 200, 200);
	
	private double[] xTicks;
	private double[] yTicks;
	private double tickLenght = 5;
	
	
	
	public SimpleGraph(double[] x, double[] y) {
		 // Zkontrolovat spravnost vstupu
        if (x == null || x.length == 0) {
            throw new NullPointerException("The x coordinate must not be empty");
        }
        if (y == null || y.length == 0) {
            throw new NullPointerException("The y coordinate must not be empty");
        }
        if (x.length != y.length) {
            throw new IllegalArgumentException("Both x and y coordinates must be the same length");
        }
        
		this.xCoord = x;
		this.yCoord = y;
		
		  // Nastavit rozsah dat
        xMin = Arrays.stream(xCoord).min().getAsDouble();
        xMax = Arrays.stream(xCoord).max().getAsDouble();
        yMin = Arrays.stream(yCoord).min().getAsDouble();
        yMax = Arrays.stream(yCoord).max().getAsDouble();
        
        if (xMin == xMax) {
            xMin = xCoord[0] - 0.5;
            xMax = xCoord[0] + 0.5;
        }
        if (yMin == yMax) {
            yMin = yCoord[0] - 0.5;
            yMax = yCoord[0] + 0.5;
        }
        
        // Sirka dat
        xRange = xMax - xMin;
        yRange = yMax - yMin; 
        
        xTicks = calculateTicks(xMin, xMax, 5);
        yTicks = calculateTicks(yMin, yMax, 5);
	}
	
	private double[] calculateTicks(double minVal, double maxVal, int tickCount) {
        double[] ticks = new double[tickCount];
        for (int i = 0; i < tickCount; i++) {
            ticks[i] = (double)i/(tickCount-1) * (maxVal - minVal) + minVal;
        }
        return ticks;
    }
	
	public void draw(Graphics2D g, double graphW, double graphH) {
		this.g2 = g;
		this.graphW = graphW;
		this.graphH = graphH;
		this.dataColor = g2.getColor(); 
		
		drawGrid();
		drawAxes();
		drawXTicks();
		drawYTicks();
		g2.setColor(dataColor); 
		drawData();
	}
	
	private void drawGrid() {
		double x, x1, x2, y, y1, y2;
        g2.setColor(gridColor);

        // Svisle cary
        y1 = 0;
        y2 = graphH;
        for (int i = 0; i < xTicks.length; i++) {
            x = gConvX(xTicks[i]);
            g2.draw(new Line2D.Double(x, y1, x, y2));
        }

        // Vodorovne cary
        x1 = 0;
        x2 = graphW;
        for (int i = 0; i < yTicks.length; i++) {
            y = gConvY(yTicks[i]);
            g2.draw(new Line2D.Double(x1, y, x2, y));
        }
	}
	
	private void drawAxes() {
		g2.setColor(axesColor);
		g2.drawRect(0, 0, (int) graphW, (int) graphH);
	}
	
	private void drawXTicks() {
		g2.setColor(axesColor);
		double y1 = graphH;
		double y2 = graphH - tickLenght;
		
		FontMetrics metrics = g2.getFontMetrics(g2.getFont());
		double yF = y1 + metrics.getAscent();
		
		for (int i = 0; i < xTicks.length; i++) {
			double x = gConvX(xTicks[i]);
			g2.draw(new Line2D.Double(x, y1, x, y2));
			String text = String.format("%.3g", xTicks[i]);
			double xF = x - metrics.stringWidth(text)/2;
			g2.drawString(text, (float) xF, (float) yF);
		}
	}
	
	private void drawYTicks() {
		g2.setColor(axesColor);
		double x1 = 0;
		double x2 = tickLenght;
		
		FontMetrics metrics = g2.getFontMetrics(g2.getFont());
		
		for (int i = 0; i < yTicks.length; i++) {
			double y = gConvX(yTicks[i]);
			g2.draw(new Line2D.Double(x1, y, x2, y));			
			String text = String.format("%.3g", yTicks[i]);
			double xF = x1 - metrics.stringWidth(text) - tickLenght;
			double yF = y + metrics.getAscent()/3.0;
			g2.drawString(text, (float) xF, (float) yF);
		}
	}
	
	private void drawData() {
		dataColor = Color.BLUE;
		g2.setColor(dataColor);
		AffineTransform t;
        for (int i = 0; i < xCoord.length; i++) {
            t = g2.getTransform();
            g2.translate(gConvX(xCoord[i]), gConvY(yCoord[i]));
            g2.fill(new Ellipse2D.Double(-dotDiam/2, -dotDiam/2, dotDiam, dotDiam));
            g2.setTransform(t);
        }
	}
	
	private double gConvX(double x) { 
		double scale = graphW/xRange;
		return (x-xMin) * scale;
	}
	
	private double gConvY(double y) {
		double scale = graphH/yRange;
		return graphH - (y-yMin) * scale;
	}

}
