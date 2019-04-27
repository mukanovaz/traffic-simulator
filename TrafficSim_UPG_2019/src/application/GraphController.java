package application;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Panel;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.Timer;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RectangleInsets;

import TrafficSim.Lane;
import javafx.scene.control.ComboBox;

public class GraphController {

	private GraphView frame;
	private static HashMap<Lane, List<DataSet>> dataSet;
	private Timer timer;
	private ChartPanel drawingPanel;
	private static Lane lane;

	public GraphController(Lane lane, HashMap<Lane, List<DataSet>> hashMap) {
		this.lane = lane;
		this.dataSet = hashMap;
	}

	/**
	 * Launch the application.
	 */
	public static void NewScreen() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GraphController window = new GraphController();
					window.frame.setVisible(true);
					window.frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 * @param hashMap 
	 * @param lane 
	 */
	public GraphController() {
		if (lane != null) 
			initialize(lane, dataSet);
		else initialize(dataSet);
	}


	/**
	 * Initialize the contents of the frame.
	 * @param dataSet2 
	 */
	private void initialize(Lane lane, HashMap<Lane, List<DataSet>> dataSet) {
		frame = new GraphView(lane, dataSet);
		this.frame.getGraphs().addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				frame.setGraphName(frame.getGraphs().getSelectedItem().toString());
				frame.getDrawingPanel().repaint();
				frame.setData(lane, dataSet);
				frame.setLane(lane);
			}
		});
	}
	
	private void initialize(HashMap<Lane, List<DataSet>> dataSet) {
		frame = new GraphView(dataSet);
		this.frame.getGraphs().addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				frame.setGraphName(frame.getGraphs().getSelectedItem().toString());
				frame.getDrawingPanel().repaint();
				frame.setAllData(dataSet);
			}
		});
	}

	public ChartPanel getDrawingPanel(HashMap<Lane, List<DataSet>> hashMap) {
		dataSet = hashMap;
		return drawingPanel;
	}

	public GraphView getFrame() {
		return frame;
	}

	
}
