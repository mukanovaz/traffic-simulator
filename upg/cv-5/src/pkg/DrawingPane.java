package pkg;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D.Double;

import javax.swing.JPanel;

import javafx.scene.shape.Line;


public class DrawingPane extends JPanel {

	private static final long serialVersionUID = 1L;
	
	public double diametr = 0.5; // relativni velikost. Pulka okna
	private Ellipse2D circle;
	private Color circleColor = Color.black;
	private boolean isMouseInside;
	private boolean isMouseInsideInitialized = false;
	private Graphics2D g2d;
	
	private Point start = new Point(0,0);
	private Point end = new Point(0,0); 
	
	private Line2D lin;
	
	public DrawingPane() {
		this.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent e) {
				end.setLocation(e.getX(), e.getY());
				repaint(); 
			}
			
			@Override
			public void mousePressed(MouseEvent e) {
				start.setLocation(e.getX(), e.getY()); 
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				if (circle.contains(e.getX(), e.getY())) {
					diametr *= 2;
					repaint();
				}
			}
		});
		
		this.addMouseMotionListener(new MouseMotionListener() {
			
			@Override
			public void mouseMoved(MouseEvent e) {
				if (circle != null) {
					boolean inside = circle.contains(e.getX(), e.getY());
					
					if (isMouseInsideInitialized == false) {
						isMouseInside = !inside;
						isMouseInsideInitialized = true;
					}
					
					if (inside && !isMouseInside) {
						circleColor = Color.RED;
						repaint();
						isMouseInside = true;
					} 
					if (!inside && isMouseInside)  {
						circleColor = Color.black;
						repaint();
						isMouseInside = false;
					}
				}
			}
			
			@Override
			public void mouseDragged(MouseEvent e) {
				end.setLocation(e.getX(), e.getY());
				repaint(); 
				System.out.println("w");
			}
		});
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g2d = (Graphics2D)g; 
		
		// Smazeme pozadi
		g2d.setColor(Color.WHITE);
		g2d.fillRect(0, 0, this.getWidth(), this.getHeight());
		
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		g2d.setColor(circleColor);
		double  scaleDiamert = Math.min(getWidth(), getWidth()) * diametr; // Absolutni prumer
		circle = new Ellipse2D.Double(this.getWidth()/2-scaleDiamert/2, this.getHeight()/2-scaleDiamert/2, scaleDiamert, scaleDiamert);
		g2d.fill(new Ellipse2D.Double(this.getWidth()/2-scaleDiamert/2, this.getHeight()/2-scaleDiamert/2, scaleDiamert, scaleDiamert));
		
		g2d.drawLine((int)start.getX(),(int) start.getY(),(int) end.getX(),(int) end.getY()); 
	}
	
	

}
