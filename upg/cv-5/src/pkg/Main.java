package pkg;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Main {
	public static void main(String[] args) {
        JFrame frame = new JFrame();

        makeGui(frame);

        // Standardni manipulace s oknem
        frame.setTitle("Kresleni");
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);    
        frame.setVisible(true);        
    }
	
	public static void makeGui(JFrame frame) {
		DrawingPane drawingPanel = new DrawingPane();
		drawingPanel.setPreferredSize(new Dimension(640, 480));
		
		JPanel contentPanel = new JPanel();
		frame.add(contentPanel);
		
		contentPanel.setLayout(new BorderLayout());
		contentPanel.add(drawingPanel, BorderLayout.CENTER);
		
		JPanel buttonPanel = new JPanel();
		contentPanel.add(buttonPanel, BorderLayout.PAGE_END);
		JButton smallBtn = new JButton("Smaller");
		JButton exitBtn = new JButton("Konec");
		buttonPanel.add(smallBtn);
		buttonPanel.add(exitBtn);
		
		exitBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				frame.dispose();
			}
		});
		
		smallBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				drawingPanel.diametr *= 0.5;
				drawingPanel.repaint();
			}
		});
	}
}
