import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Ellipse2D.Double;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class DrawingPane extends JPanel{
	
	
	private static final long serialVersionUID = 1L;
	private String imageFileName = "img/test.jpg";
	BufferedImage img;
	
	public DrawingPane () {
		try {
			img = ImageIO.read(new File(imageFileName));
			System.out.println("OK");
		} catch (Exception e) {
			System.out.println("Nepodarilo se nacist obrazek");
			img = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
		}
	}
	
	public DrawingPane (BufferedImage img) {
		this.img = img;
	}
	

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D)g; 		
		//g2d.drawImage(img, 0, 0, this.getWidth(), this.getHeight(), null);
		
		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		
        Ellipse2D ellipse1 = new Ellipse2D.Double(0,0, 200, 200);
        Area circle = new Area(ellipse1);
		
        Graphics2D g2 = img.createGraphics();
        g.setClip(circle);
        g2d.drawImage(img, 0, 0, 200, 200, 0, 0, (int)(img.getWidth()), (int)(img.getHeight()), null);
        g.setClip(null);
		
	}

	private void paintImages(Graphics2D g2d) {
//		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//		g2d.drawImage(img, 10, 10, null);
//		g2d.drawImage(img, 250, 10, (int)(img.getWidth() * 1.5), (int)(img.getHeight() * 1.5), null);
		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		
		double scale = Math.min((double) getWidth()/ img.getWidth(), 
				(double) getHeight()/ img.getHeight());
				
		g2d.drawImage(img, 0, 0, (int)(img.getWidth() * scale), (int)(img.getHeight() * scale), null);
	}
	
	private void drawEllipse (Graphics2D g2d) {
		int width = this.getWidth();
		int height = this.getHeight();
		int size = Math.min(width, height);
		
		g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
		Ellipse2D ellipse = new Ellipse2D.Double(-0.4*size, -0.1*size, 0.8*size, 0.2*size);
		
		g2d.setColor(Color.BLUE);
		g2d.setStroke(new BasicStroke(0.5f));
		
		int ellipseCount = 150;
		
		g2d.translate(width/2, height/2);
		AffineTransform tr = g2d.getTransform();
		
		for (int i = 0; i<ellipseCount; i++) {
			g2d.setTransform(tr);
			g2d.rotate(Math.toRadians((double) i/ellipseCount * 180));
			g2d.draw(ellipse);
		} 
	}
}
