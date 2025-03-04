import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JPanel;


public class Main extends JPanel {

	private static final long serialVersionUID = 1L;
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		Graphics2D g2d = (Graphics2D)g; 
		g2d.draw(drawRec(getWidth(), getHeight()));

		// Arrow
		drawArrow2(g2d);
	}
	
	public static Rectangle2D drawRec (int width, int height) {
		int margin = 10;
		int r = Math.min(width, height);
		
		int min_rx = 10; // road
		int max_rx = 50;
		int min_ox = margin;
		int max_ox = r - margin;
		int dx1 = 20; // Min()
		int dx2 = 40;
		double x1 = (double)(dx1 - min_rx) / (max_rx - min_rx) * (max_ox - min_ox) + min_ox;
		double x2 = (double)(dx2 - min_rx) / (max_rx - min_rx) * (max_ox - min_ox) + min_ox;

		int min_ry = 60;
		int max_ry = 60;
		int min_oy = margin;
		int max_oy = r - margin;
		int dy1 = 30;
		int dy2 = 60; // 50
		double y1 = (double)(dy1 - min_ry) / (max_ry - min_ry) * (max_oy - min_oy) + min_oy;
		double y2 = (double)(dy2 - min_ry) / (max_ry - min_ry) * (max_oy - min_oy) + min_oy;
		
		return new Rectangle2D.Double(x1,  y1, x2 - x1, y2 - y1);
	}
	
	public static void drawArrow (Graphics2D g2d) {
		int ax = 10;
		int ay = 50;
		int bx = 100;
		int by = 20;
		
		int l = 10; // delka sipky
		int k = 5; // sirka sipky
		
		int ux = ax - bx; // smerovy vektor
		int uy = ay - by; // smerovy vektor
		
		double d = Math.hypot(ux, uy); // velikost vektoru
		
		double ux1 = ux / d; // jednotkovy vektor
		double uy1 = uy / d; // jednotkovy vektor
		
		double cx = bx + (ux1 * l); // bod c
		double cy = by + (uy1 * l);
		
		double nx1 = uy1; 	// normalovy vektor k jednotkovemu
		double ny1 = - ux1;
		
		double dx1 = cx + k * nx1; // pocatek sipky 1
		double dy1 = cy + k * ny1; // pocatek sipky 1
		
		double dx2 = cx - k * nx1; // pocatek sipky 2
		double dy2 = cy - k * ny1; // pocatek sipky 2
		
		g2d.draw(new Line2D.Double(ax, ay, bx, by));
		g2d.draw(new Line2D.Double(dx1, dy1, bx, by));
		g2d.draw(new Line2D.Double(dx2, dy2, bx, by));
	}
	
	public static void drawArrow2 (Graphics2D g2d) {
		
		int l1 = 20;
		
		int ax = 10;
		int ay = 50;
		int bx = 100;
		int by = 20;
		
		int l = 10; // delka sipky
		int k = 5; // sirka sipky
		
		int ux = ax - bx; // smerovy vektor
		int uy = ay - by; // smerovy vektor
		
		double d = Math.hypot(ux, uy); // velikost vektoru
		
		double ux1 = ux / d; // jednotkovy vektor
		double uy1 = uy / d; // jednotkovy vektor
		
		double cx = bx + (ux1 * l); // bod c
		double cy = by + (uy1 * l);
		
		double nx1 = uy1; 	// normalovy vektor k jednotkovemu
		double ny1 = - ux1;
		
		double dx1 = cx + k * nx1; // pocatek sipky 1
		double dy1 = cy + k * ny1; // pocatek sipky 1
		
		double dx2 = cx - k * nx1; // pocatek sipky 2
		double dy2 = cy - k * ny1; // pocatek sipky 2
		
		// 2
		int uux = bx - ax; // smerovy vektor
		int uuy = by - ay; // smerovy vektor
		
		double uux1 = uux / Math.hypot(uux, uuy); // jednotkovy vektor
		double uuy1 = uuy / Math.hypot(uux, uuy); // jednotkovy vektor
		
		double dx = ax + (uux1 * l1); // bod 
		double dy = ay + (uuy1 * l1);
		
		// Draw
		g2d.draw(new Line2D.Double(ax, ay, bx, by));
		g2d.draw(new Line2D.Double(dx1, dy1, bx, by));
		g2d.draw(new Line2D.Double(dx2, dy2, bx, by));
	}
}
