package applikace;

import java.awt.EventQueue;

import javax.swing.JFrame;

public class app {

	private JFrame frame;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					app window = new app();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public app() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		int min_r = 50;
		int max_r = 200;
		int min_o = 10;
		int max_o = 590;
		int d = 150;
		double vysledek = (double)(d - min_r) / (max_r-min_r) * (max_o - min_o) + min_o;
		System.out.println(vysledek);
	}

}
