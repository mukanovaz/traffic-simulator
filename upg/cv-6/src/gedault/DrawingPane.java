package gedault;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.Point;
import java.awt.RadialGradientPaint;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Double;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;

import javax.swing.JPanel;

import javafx.scene.shape.Line;


public class DrawingPane extends JPanel implements Printable {

	private static final long serialVersionUID = 1L;
	
	private Graphics2D g2d;
	
	public DrawingPane() {
		
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g2d = (Graphics2D)g; 
		
		// Smazeme pozadi
		g2d.setColor(Color.WHITE);
		g2d.fillRect(0, 0, this.getWidth(), this.getHeight());
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		draw(g2d);
		//drawComponent(g2d);
	}
	

	private void draw(Graphics2D g) {
		AffineTransform def = g.getTransform();
		g.translate(50, 50);
		
		Rectangle2D shape = new Rectangle2D.Double(0,0,200,400);
		Ellipse2D shape2 = new Ellipse2D.Double(0,-25,200,50);
		Ellipse2D shape3 = new Ellipse2D.Double(0,150,200,50);
		Ellipse2D shape4 = new Ellipse2D.Double(0,372,200,50);
		
		Area shapeA = new Area(shape);
		Area shape2A = new Area(shape2);
		Area shape3A = new Area(shape3);
		Area shape4A = new Area(shape4);
		
		shape2A.intersect(shapeA);
		
		shapeA.subtract(shape2A);
		shapeA.add(shape4A);
		shape3A.intersect(shapeA);
		shape3A.subtract(shape4A);
		
		
		g.setColor(Color.BLACK);
		g.draw(shape2);
		g.draw(shape);
		g.draw(shape3);
		g.draw(shape4);
		
		g.setColor(Color.WHITE);
		g.fill(shape2A);
		
		g.setPaint(new LinearGradientPaint(new Point2D.Double(0,0), 
				new Point2D.Double(150,0), 
				new float[] {0f, 0.3f, 1.0f }, 
				new Color[] {new Color(0, 0, 0, 60), new Color(0, 0, 0, 10), new Color(0, 0, 0, 100) }));
		g.fill(shapeA);
		
		g.setPaint(new LinearGradientPaint(new Point2D.Double(0,0), 
				new Point2D.Double(150,0), 
				new float[] {0f, 0.3f, 1.0f }, 
				new Color[] {new Color(0, 102, 255, 60), new Color(0, 0, 0, 10), new Color(0, 102, 255, 100) }));
		g.fill(shape3A);
		g.fill(shape4A);
		

		
		
	}

	public void drawComponent(Graphics2D g) {
		AffineTransform def = g.getTransform();
		
		GeneralPath path = new GeneralPath();
		path.moveTo(0, 0);
		path.lineTo(100, 0);
		path.lineTo(100, 100);
		path.lineTo(0, 100);
		path.closePath();
		
		g.setColor(Color.BLACK);
		g.draw(path);
		
		// Странная фигура
		GeneralPath pathHole = new GeneralPath();
		pathHole.moveTo(0, 0);
		pathHole.lineTo(50, 0);
		pathHole.curveTo(100, 0, 100, 0, 100, 50);
		pathHole.lineTo(100, 100);
		pathHole.lineTo(50, 100);
		pathHole.curveTo(0, 100, 0, 100, 0, 50);
		pathHole.closePath();
		
		pathHole.moveTo(30, 30);
		pathHole.lineTo(30, 70);
		pathHole.lineTo(70, 70);
		pathHole.lineTo(70, 30);
		pathHole.closePath();
		
		g.translate(110, 10);
		g.setColor(Color.RED);
		g.fill(pathHole);
		g.draw(pathHole);
		
		// Странный пруник
		g.setTransform(def);
		
		Ellipse2D shape = new Ellipse2D.Double(0,0,100,100);
		Rectangle2D shape2 = new Rectangle2D.Double(50,50,100,100);
		Area op = new Area(shape);
		Area tmp = new Area(shape2);
		
		op.intersect(tmp);
		
		g.translate(50, 120);
		g.draw(shape);
		g.draw(shape2);
		g.fill(op);
		g.setTransform(def);
		
		//
		
		g.translate(220, 320);
		Ellipse2D customClipPath = new Ellipse2D.Double(0,0,200,200);
		Shape originalClipPath = g.getClip();
		
		Area cp = new Area(originalClipPath);
		cp.intersect(new Area(customClipPath));
		
		g.setClip(cp);
		
		for (double y = 0; y < 200; y += 5) {
			g.draw(new Line2D.Double(0, y, 200, y));
		}
		
		g.setClip(originalClipPath);
		g.draw(customClipPath);
		g.setTransform(def);
		
		// Gradient

		g.setPaint(new RadialGradientPaint(new Point2D.Double(50,50), 
											200,
											new float[] {0f, 0.3f, 1f}, 
											new Color[] {new Color(255,255,255), new Color(255,100,0), new Color(0,0,0)}));
		g.fill(new Ellipse2D.Double(0, 0, 150, 150));
		
		// Gradient 2
		g.setPaint(new LinearGradientPaint(new Point2D.Double(0, 0), 
				new Point2D.Double(0, 150), 
				new float[] {0f, 0.3f, 1.0f }, 
				new Color[] {new Color(0, 0, 0, 0), new Color(0, 0, 0, 10), new Color(0, 0, 0, 100) }));
		g.fill(new Ellipse2D.Double(0, 0, 150, 150));
	}

	@Override
	public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
		
		if (pageIndex > 0) {
			return NO_SUCH_PAGE;
		}
		double ptInMM = 25.4 / 72;
		double pxInMM = 0.25;
		double scale = pxInMM / ptInMM;
		
		Graphics2D g2 = (Graphics2D) graphics;
		g2.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
		//g2.scale(scale, scale);
		double gScale = Math.min(pageFormat.getImageableWidth() / this.getWidth(), 
				pageFormat.getImageableHeight() / this.getHeight());
		g2.scale(gScale, gScale);
		g2.setColor(Color.black);
		g2.draw(new Rectangle2D.Double(0,0,200,200));
		drawComponent(g2);
		
		return PAGE_EXISTS;
	}

}
