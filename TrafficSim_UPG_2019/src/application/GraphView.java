package application;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Panel;
import java.awt.SystemColor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

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

public class GraphView  extends JFrame{
	
	private ChartPanel drawingPanel;
	private Panel top_panel = new Panel();
	private JComboBox<String> graphs;
	private JFreeChart chart;
	private String graphName;
	private XYSeries data1 = null;
	private XYSeries data2 = null;
	private XYSeries data3 = null;
	private List<XYSeries> dataAll1 = new ArrayList<XYSeries>();
	private List<XYSeries> dataAll2 = new ArrayList<XYSeries>();
	private List<XYSeries> dataAll3 = new ArrayList<XYSeries>();
	private JTabbedPane tabs;
	private String[] items = {
			"Number of cars current [num]",
			"Number of cars total [num]",
			"Speed average [km/h]"
	};
	private Lane lane;
	
	/**	
	 * Draw graph of selected lane
	 * @param lane 
	 * @param dataSet dataset of lane
	 */
	public GraphView(Lane lane,HashMap<Lane, List<DataSet>> dataSet) {
		setData(lane, dataSet);
		this.getContentPane().setLayout(new BorderLayout(0, 0));
		graphs = new JComboBox<String>(items);
		
		// Center
		tabs = new JTabbedPane();
		this.getContentPane().add(tabs, BorderLayout.CENTER);
		tabs.add(items[0], new ChartPanel(makeLineChart(data1)));
		tabs.add(items[1], new ChartPanel(makeLineChart(data2)));
		tabs.add(items[2], new ChartPanel(makeLineChart(data3)));
		top_panel.setBackground(SystemColor.inactiveCaption);
		
		this.setTitle(graphName);
		this.setSize(new Dimension(640, 480));
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	/**
	 * Draw graph osf all lanes 
	 * @param dataSet
	 */
	public GraphView(HashMap<Lane, List<DataSet>> dataSet) {
		setAllData(dataSet);
		this.getContentPane().setLayout(new BorderLayout(0, 0));
		graphs = new JComboBox<String>(items);
		
		// Center
		tabs = new JTabbedPane();
		this.getContentPane().add(tabs, BorderLayout.CENTER);
		tabs.add(items[0], new ChartPanel(makeAllLineChart(dataAll1)));
		tabs.add(items[1], new ChartPanel(makeAllLineChart(dataAll2)));
		tabs.add(items[2], new ChartPanel(makeAllLineChart(dataAll3)));
		
		top_panel.setBackground(SystemColor.inactiveCaption);
		
		this.setTitle(graphName);
		this.setSize(new Dimension(640, 480));
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	
	private JFreeChart makeLineChart(XYSeries d) {
		// Data vlozime do spolecneho kontejneru
        XYSeriesCollection data = new XYSeriesCollection();
        data.addSeries(d);
        
        // Vytvorime graf
        chart = ChartFactory.createXYLineChart(
        		graphName, "Time [s]", "Value", data,
                PlotOrientation.VERTICAL, true, false, false);
        
        chart.setBackgroundPaint(Color.white);

        final XYPlot plot = chart.getXYPlot();
        plot.setBackgroundPaint(new Color(232, 232, 232));

        plot.setDomainGridlinePaint(Color.gray);
        plot.setRangeGridlinePaint (Color.gray);

        // Определение отступа меток делений
        plot.setAxisOffset(new RectangleInsets (1.0, 1.0, 1.0, 1.0));

        // Скрытие осевых линий
        ValueAxis axis = plot.getDomainAxis();
        axis.setAxisLineVisible (false);

        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        // Удаление меток Series 2
        renderer.setSeriesShapesVisible(0, false);

        // Настройка графика (цвет, ширина линии) Series 3
        renderer.setSeriesPaint  (0, Color.orange);
        renderer.setSeriesStroke (0, new BasicStroke(2.5f));
        plot.setRenderer(renderer);
        
        return chart;
	}

	public void setData(Lane lane, HashMap<Lane, List<DataSet>> dataSet) {
		List<DataSet> d = (List<DataSet>) dataSet.get(lane);
		graphName = lane.getId(); 
		data1 = new XYSeries("Number of cars current");
		data2 = new XYSeries("Number of cars total");
		data3 = new XYSeries("Speed average");
		int i = 0;
		for (DataSet dd : d) {
			data1.addOrUpdate(i, dd.getValue1());
			data2.addOrUpdate(i, dd.getValue2());
			data3.addOrUpdate(i, dd.getValue3());
			i++;
		}
	}
	
	public void setAllData(HashMap<Lane, List<DataSet>> dataSet) {
		
		for(Map.Entry m:dataSet.entrySet()){    
			List<DataSet> d = (List<DataSet>) m.getValue();
			Lane lane = (Lane) m.getKey();
			graphName = lane.getId(); 
			
			XYSeries data1 = new XYSeries(lane.getId());
			int i = 0;
			for (DataSet dd : d) {
				data1.addOrUpdate(i, dd.getValue1());
				i++;
			}
			dataAll1.add(data1);
			
			XYSeries data2 = new XYSeries(lane.getId());
			i = 0;
			for (DataSet dd : d) {
				data2.addOrUpdate(i, dd.getValue2());
				i++;
			}
			dataAll2.add(data2);
			
			XYSeries data3 = new XYSeries(lane.getId());
			i = 0;
			for (DataSet dd : d) {
				data3.addOrUpdate(i, dd.getValue3());
				i++;
			}
			dataAll3.add(data3);
		}  
	}
	
	private JFreeChart makeAllLineChart(List<XYSeries> data) {
		// Data vlozime do spolecneho kontejneru
        XYSeriesCollection dataSet = new XYSeriesCollection();
        
        for (XYSeries xySeries : data) {
        	dataSet.addSeries(xySeries);
		}
        
        // Vytvorime graf
        chart = ChartFactory.createXYLineChart(
        		graphName, "Time [s]", "Value", dataSet,
                PlotOrientation.VERTICAL, true, false, false);
        
        chart.setBackgroundPaint(Color.white);

        final XYPlot plot = chart.getXYPlot();
        plot.setBackgroundPaint(new Color(232, 232, 232));

        plot.setDomainGridlinePaint(Color.gray);
        plot.setRangeGridlinePaint (Color.gray);

        // Определение отступа меток делений
        plot.setAxisOffset(new RectangleInsets (1.0, 1.0, 1.0, 1.0));

        // Скрытие осевых линий
        ValueAxis axis = plot.getDomainAxis();
        axis.setAxisLineVisible (false);

        return chart;
	}

	public ChartPanel getDrawingPanel() {
		return drawingPanel;
	}

	public Panel getTop_panel() {
		return top_panel;
	}

	public JComboBox<String> getGraphs() {
		return graphs;
	}

	public JFreeChart getChart() {
		return chart;
	}

	public void setChart(JFreeChart chart) {
		this.chart = chart;
	}

	public void setGraphName(String graphName) {
		this.graphName = graphName;
	}

	public XYSeries getData1() {
		return data1;
	}


	public void setLane(Lane lane) {
		this.lane = lane;
	}
}
