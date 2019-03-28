package gedault;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class Main {
	public static void main(String[] args) {
        JFrame frame = new JFrame();

        DrawingPane drawingPanel = new DrawingPane();
		drawingPanel.setPreferredSize(new Dimension(640, 480));
		
		frame.setLayout(new BorderLayout());
		frame.add(drawingPanel, BorderLayout.CENTER);
		
		JButton buttonPrint = new JButton("Print");
		frame.add(buttonPrint, BorderLayout.PAGE_END);
		
		buttonPrint.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				PrinterJob job = PrinterJob.getPrinterJob();
				job.setPrintable(drawingPanel);
				boolean doPrint = job.printDialog();
				if( doPrint) {
					try {
						job.print();
					} catch (PrinterException ex) {
						JOptionPane.showMessageDialog(frame, "Chyba tisku");
					}
				}
			}
		});
		
		frame.add(drawingPanel);
        // Standardni manipulace s oknem
        frame.setTitle("Kresleni");
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);    
        frame.setVisible(true);        
    }
	
	
}
