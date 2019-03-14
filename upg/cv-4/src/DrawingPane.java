import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D.Double;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.swing.JPanel;


public class DrawingPane extends JPanel {

	private static final long serialVersionUID = 1L;
	private double time;
	private int[] x=new int[2];
	private int[] y=new int[2];
	AffineTransform defTr;
	
	public double getTime () {
		return this.time;
	}
	
	public void setTime (double time) {
		this.time = time;
	}
	
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D)g; 
		
		// Smazeme pozadi
		g2d.setColor(Color.WHITE);
		g2d.fillRect(0, 0, this.getWidth(), this.getHeight());
		
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		g2d.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 20));
		g2d.setColor(Color.BLACK);
		g2d.drawString(String.format("time = %.2f s", time), 20, 20);
		
		AffineTransform tr = g2d.getTransform();
		
		drawClock(g2d);
		g2d.setTransform(tr);
		drawSolarSysetem(g2d, time);
	}
	
	private void drawClock(Graphics2D g) {
		g.translate(20, 50);
		g.setColor(Color.BLACK);
		g.draw(new Ellipse2D.Double(0, 0, 100, 100));
		
		g.translate(50, 50);
		AffineTransform center = g.getTransform();
		double perioda1 = 60.0;
		double omega1 = 2 * Math.PI / perioda1;
		g.rotate(omega1 * time);
		g.draw(new Line2D.Double(0.0, 0.0, 0.0, -30));
		
	}

	private void drawSolarSysetem (Graphics2D g, double time) {
		double diametrSystem = 2 * 4.5;
		
		double scale = Math.min(this.getWidth() , this.getHeight()) / diametrSystem;
		g.translate(this.getWidth() / 2, this.getHeight() / 2);
		g.scale(scale, scale);
		
		AffineTransform trSun = g.getTransform();
		
		// Slunce
		double rSun = 0.3;
		g.setColor(Color.orange);
		g.fill(new Ellipse2D.Double(-rSun, -rSun, 2*rSun, 2*rSun));
		
		// Planeta 1
		double perioda1 = 10.0;
		double rPath1 = 1.0; // от солнца до середины планеты
		double rPlanet1 = 0.1;
		double omega1 = 2 * Math.PI / perioda1;
		
		g.rotate(omega1 * time);
		g.translate(rPath1, 0);
		g.setColor(Color.BLACK);
		
		g.fill(new Ellipse2D.Double(-rPlanet1, -rPlanet1, 2 * rPlanet1, 2 * rPlanet1));
	
		// Planeta 2
		g.setTransform(trSun);
		double perioda2 = 18.0;
		double rPath2 = 3.5; // от солнца до середины планеты
		double rPlanet2 = 0.125;
		double omega2 = 2 * Math.PI / perioda2;
				
		g.rotate(omega2 * time);
		g.translate(rPath2, 0);
		g.setColor(Color.BLACK);
				
		g.fill(new Ellipse2D.Double(-rPlanet2, -rPlanet2, 2 * rPlanet2, 2 * rPlanet2));
		
		AffineTransform trPlanet1 = g.getTransform();
		
		// Mesic 1
		double period2A = 1.0;
		double rPath2A = 0.4;
		double rMoon2A = 0.05;
		double omega2A = 2 * Math.PI / period2A; // Rychlost
		
		g.rotate(omega2A * time);
		g.translate(rPath2A, 0);
		g.setColor(Color.BLACK);
				
		g.fill(new Ellipse2D.Double(-rMoon2A, -rMoon2A, 2 * rMoon2A, 2 * rMoon2A));
		
		// Mesic 2
		g.setTransform(trPlanet1);
		
		double period2B = 1.5;
		double rPath2B = 0.6;
		double rMoon2B = 0.05;
		double omega2B = 2 * Math.PI / period2B; // Rychlost
		
		g.rotate(omega2B * time);
		g.translate(rPath2B, 0);
		g.setColor(Color.BLACK);
				
		g.fill(new Ellipse2D.Double(-rMoon2B, -rMoon2B, 2 * rMoon2B, 2 * rMoon2B));
		
	}
	
	

}














//g2d.setColor(Color.WHITE);
//g2d.fillRect(0, 0, this.getWidth(), this.getHeight());
//
//g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//
//Rectangle2D square = new Rectangle2D.Double(-50, -50, 100, 100);
//g2d.setColor(Color.BLACK);
//
//// Save default transform
//AffineTransform defTransform = g2d.getTransform();
//g2d.translate(300, 250);
//
//AffineTransform tr = g2d.getTransform();
//g2d.draw(square);
//
//g2d.translate(70, 0);
//g2d.scale(0.25, 0.25);	// масштаб
//g2d.rotate(Math.toRadians(10));
//g2d.draw(square);
//
//g2d.setTransform(tr);
//Shape smallSquare;
//
//AffineTransform tr2 = AffineTransform.getTranslateInstance(0, 70);
//tr2.concatenate(AffineTransform.getScaleInstance(0.25, 0.25));
//tr2.concatenate(AffineTransform.getRotateInstance(Math.toRadians(10)));
//smallSquare = tr2.createTransformedShape(square);
//g2d.draw(smallSquare);
//
//tr2 = AffineTransform.getTranslateInstance(-70, 0);
//tr2.concatenate(AffineTransform.getScaleInstance(0.25, 0.25));
//tr2.concatenate(AffineTransform.getRotateInstance(Math.toRadians(10)));
//smallSquare = tr2.createTransformedShape(square);
//g2d.draw(smallSquare);
//
//tr2 = AffineTransform.getTranslateInstance(0, -70);
//tr2.concatenate(AffineTransform.getScaleInstance(0.25, 0.25));
//tr2.concatenate(AffineTransform.getRotateInstance(Math.toRadians(10)));
//smallSquare = tr2.createTransformedShape(square);
//g2d.draw(smallSquare);
//Rectangle2D square;






/*
g2d.translate(200, 20);
g2d.fill(square);

g2d.setTransform(defTransform);
g2d.translate(0, 150);
g2d.fill(square);

g2d.setTransform(defTransform);
g2d.translate(200, 150);
g2d.rotate(Math.toRadians(15));
g2d.draw(square);

g2d.setTransform(defTransform);
g2d.rotate(Math.toRadians(15));
g2d.translate(200, 150);
g2d.draw(square); */