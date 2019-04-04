import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;


public class Main {
	public static void main(String[] args) {
		BufferedImage img;
		try {
			img = ImageIO.read(new File(imageFileName));
			System.out.println("OK");
		} catch (Exception e) {
			System.out.println("Nepodarilo se nacist obrazek");
			img = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
		}
				
		saveImage(drawImage(img), "img/result");
	
        JFrame frame = new JFrame();

        DrawingPane drawingPanel = new DrawingPane();
		drawingPanel.setPreferredSize(new Dimension(640, 480));
		frame.add(drawingPanel);
		 
        // Standardni manipulace s oknem
        frame.setTitle("Kresleni");
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);    
        frame.setVisible(true);        
    }
	
	private static void saveImage(BufferedImage img, String filename) {
		
		try {
			ImageIO.write(img, "png", new File(filename + ".png"));
		} catch (Exception e) {
			System.out.println("Zapis se nezdaril");
		}
	}
	
	private static String imageFileName = "img/test.jpg";

	private static BufferedImage drawImage(BufferedImage img) {
		BufferedImage imageRGB = new BufferedImage(200, 200, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2 = imageRGB.createGraphics();
		
		g2.setColor(Color.WHITE);
		g2.fillRect(0, 0, imageRGB.getWidth(), imageRGB.getHeight());
		
		
		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		
        Ellipse2D ellipse1 = new Ellipse2D.Double(0,0, 200, 200);
        Area circle = new Area(ellipse1);
		
        g2.setClip(circle);
        g2.drawImage(img, 0, 0, 200, 200, 0, 0, (int)(img.getWidth()), (int)(img.getHeight()), null);
        g2.setClip(null);	
        
        return imageRGB;
	}
	
	
//	private static void saveImage(BufferedImage img, String filename) {
//		BufferedImage imageRGB = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
//		Graphics2D g2 = imageRGB.createGraphics();
//		g2.drawImage(img, 0, 0, null);
//		try {
//			ImageIO.write(imageRGB, "jpeg", new File(filename + ".jpeg"));
//		} catch (Exception e) {
//			System.out.println("Zapis se nezdaril");
//		}
//	}
//
//	private static void drawImage(BufferedImage img) {
//		Graphics2D g2d = img.createGraphics();
//		
//		int width = img.getWidth();
//		int height = img.getHeight();
//		int size = Math.min(width, height);
//		
//		g2d.setColor(Color.WHITE);
//		g2d.fillRect(0, 0, width, height);
//		
//		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
//		Ellipse2D ellipse = new Ellipse2D.Double(-0.4*size, -0.1*size, 0.8*size, 0.2*size);
//		
//		g2d.setColor(Color.BLUE);
//		g2d.setStroke(new BasicStroke(0.5f));
//		
//		int ellipseCount = 150;
//		
//		g2d.translate(width/2, height/2);
//		AffineTransform tr = g2d.getTransform();
//		
//		for (int i = 0; i<ellipseCount; i++) {
//			g2d.setTransform(tr);
//			g2d.rotate(Math.toRadians((double) i/ellipseCount * 180));
//			g2d.draw(ellipse);
//		} 
//	}
}
