import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.Timer;

public class Main {
	public static void main(String[] args) {
        JFrame frame = new JFrame();

        // Vlastni graficky obsah
        DrawingPane drawingPanel = new DrawingPane();
        drawingPanel.setPreferredSize(new Dimension(640, 480));
        frame.add(drawingPanel);

        // Standardni manipulace s oknem
        frame.setTitle("Kresleni");
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);    
        frame.setVisible(true);            
        
        Timer timer;
        int timerPeriod = 1000 / 25;
        long startTime = System.currentTimeMillis();
        timer = new Timer(timerPeriod, new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				drawingPanel.setTime((System.currentTimeMillis() - startTime) / 1000.0);
				drawingPanel.repaint();
			}
		});
        timer.start();
    }
}
