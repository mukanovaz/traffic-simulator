import java.awt.Dimension;
import java.awt.EventQueue;

import javax.swing.JFrame;

public class DrawingPane {
	public static void main(String[] args) {
        JFrame frame = new JFrame();

        // Vlastni graficky obsah
        Main drawingPanel = new Main();
        drawingPanel.setPreferredSize(new Dimension(640, 480));
        frame.add(drawingPanel);

        // Standardni manipulace s oknem
        frame.setTitle("Kresleni");
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);    
        frame.setVisible(true);             
    }
}
