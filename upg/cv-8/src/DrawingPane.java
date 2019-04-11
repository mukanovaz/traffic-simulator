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
	private SimpleGraph graph;
	
	public void setGraph(SimpleGraph graph) {
		this.graph = graph;
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D)g; 		
		
		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		if (graph != null) {
			g2d.setColor(Color.BLUE);
			g2d.translate(50, 50);
			graph.draw(g2d, this.getWidth() - 100, this.getHeight() - 100);
			
		}
		
	}
}
