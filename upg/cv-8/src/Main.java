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

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;


public class Main {
	public static void main(String[] args) {
        JFrame frame = new JFrame();

        ChartPanel drawingPanel;
        JFreeChart chart = makeBarChart();
        
        drawingPanel = new ChartPanel(chart);
        //DrawingPane drawingPanel = new DrawingPane();
//		SimpleGraph graph = new SimpleGraph(x, y);
//		drawingPanel.setGraph(graph);

        drawingPanel.setPreferredSize(new Dimension(640, 480));
		frame.add(drawingPanel);
		
		double[] x = new double[] {1.2, 1.6, 3.5, 4, 5};
		double[] y = new double[] {1.4,1.1,1,3.2,2.5};
		 
        // Standardni manipulace s oknem
        frame.setTitle("Kresleni");
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);    
        frame.setVisible(true);        
    }
	
	public static JFreeChart makePieChart() {
		   DefaultPieDataset data = new DefaultPieDataset();
	        data.setValue("ANO", 78);
	        data.setValue("ODS", 25);
	        data.setValue("Piráti", 22);
	        data.setValue("SPD", 22);
	        data.setValue("KSÈM", 15);
	        data.setValue("ÈSSD", 15);
	        data.setValue("KDU-ÈSL", 10);
	        data.setValue("TOP 09", 7);
	        data.setValue("STAN", 6);
	        
	        // Vytvorime graf
	        JFreeChart chart = ChartFactory.createPieChart("Poèty mandátù - parlament (2017)", (PieDataset)data);
	        
	        return chart;
	}
	
	 public static JFreeChart makeLineChart() {
	        // Vygenerujeme data k zobrazeni
	        XYSeries data1 = new XYSeries("sin(x)");
	        for (double x = 0; x < 10; x += 0.1) {
	            data1.add(x, Math.sin(x));
	        }        
	        
	        XYSeries data2 = new XYSeries("cos(x)");
	        for (double x = 0; x < 10; x += 0.1) {
	            data2.add(x, Math.cos(x));
	        }        

	        // Data vlozime do spolecneho kontejneru
	        XYSeriesCollection dataSet = new XYSeriesCollection();
	        dataSet.addSeries(data1);
	        dataSet.addSeries(data2);
	        
	        // Vytvorime graf
	        JFreeChart chart = ChartFactory.createXYLineChart("Goniometricke funkce",
	                "x [radiany]", "y",    dataSet);
	        return chart;
	    }
	 
	 public static JFreeChart makeBarChart() {
	        // Pripravime data - dochazka na cviceni UPG a bodovani bonusovych uloh
	        DefaultCategoryDataset data = new DefaultCategoryDataset();
	        String dochazka = "docházka";
	        String body = "body";
	        String cviceniPrefix = "cv. ", cviceni;
	        int i = 1;
	        
	        cviceni = cviceniPrefix + i;
	        data.addValue(17, dochazka, cviceni);
	        data.addValue( 6, body,     cviceni);
	        
	        i++; cviceni = cviceniPrefix + i;
	        data.addValue(17, dochazka, cviceni);
	        data.addValue(12, body,     cviceni);
	        
	        i++; cviceni = cviceniPrefix + i;
	        data.addValue(16, dochazka, cviceni);
	        data.addValue( 6, body,     cviceni);
	        
	        i++; cviceni = cviceniPrefix + i;
	        data.addValue(15, dochazka, cviceni);
	        data.addValue(16, body,     cviceni);
	        
	        // Vytvorime graf
	        JFreeChart chart = ChartFactory.createBarChart("Cvièení UPG",
	                "Èíslo cvièení", "Docházka a body", data);
	        
	        //
	        // Nastaveni vizualnich vlastnosti grafu:
	        //
	        CategoryPlot plot = chart.getCategoryPlot();
	        
	        // Pozadi grafu
	        plot.setBackgroundPaint(Color.WHITE);
	        
	        // Barva mrizky
	        plot.setRangeGridlinePaint(Color.GRAY);
	        
	        // Viditelnost mrizky
	        plot.setRangeGridlinesVisible(true); // tato je standardne true
	        
	        // Nastaveni vzhledu jednotlivych vykreslovanych rad (my mame dve)
	        BarRenderer renderer = (BarRenderer) plot.getRenderer();
	        renderer.setBarPainter(new StandardBarPainter());
	        renderer.setSeriesPaint(0, Color.getHSBColor(0.6f, 0.5f, 0.9f));
	        renderer.setSeriesPaint(1, Color.getHSBColor(0f, 0.3f, 0.9f));
	        renderer.setItemMargin(0);
	        
	        // Zobrazeni hodnoty u kazdeho sloupce
	        renderer.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator());
	        renderer.setBaseItemLabelsVisible(true);

	        return chart;
	    }
}
