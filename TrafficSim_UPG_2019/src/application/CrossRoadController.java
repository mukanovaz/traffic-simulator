package application;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.AbstractButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JSlider;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;

import org.jfree.graphics2d.svg.SVGGraphics2D;

import TrafficSim.Car;
import TrafficSim.Lane;
import TrafficSim.Simulator;

public class CrossRoadController{
	//private Model model;
	private View view;
	private Timer timer;
	private Simulator sim;
	private int defaultPeriod = 50;
	private Boolean state = false;
	private JComboBox<String> cb;
	private GraphController laneGraph;
	private long startTime;
	private DragHandler panner;
	
	public CrossRoadController(View v) {
		this.view = v;
		initListeners();
	}

	/**
	 * Initialize actions on all GUI components
	 */
	private void initListeners() {
		view.getDrawPanel().setCarsImages();
		// Add mouse listeners
		panner = new DragHandler(this.view.getDrawPanel());
		this.view.getDrawPanel().addMouseListener(panner);
		this.view.getDrawPanel().addMouseMotionListener(panner);
		
		timer = new Timer(defaultPeriod, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				long now = System.currentTimeMillis();
//				double stepValue = (double) (now - startTime) / 1000;
				double stepValue = (double) defaultPeriod / 1000;
				sim.nextStep(stepValue);
				view.getDrawPanel().updateDataSet(stepValue);
				view.getDrawPanel().repaint();
			}
		});
		
		scenariosControlls();
		rightPanelControlls();
		menuControlls();
	}
	
	/**
	 * Right panel buttons actions control
	 */
	private void rightPanelControlls() {
		this.view.getB1().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				AbstractButton abstractButton = (AbstractButton)e.getSource(); 
				boolean selected = abstractButton.getModel().isSelected(); 
				if (selected) { 
                	panner.setAllowSelect(true);
                } 
                else { 
                	panner.setAllowSelect(false);
                } 
			}
		});
		
		// Show cars speed text
		this.view.getB2().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				AbstractButton abstractButton =  
		                (AbstractButton)e.getSource(); 
				boolean selected = abstractButton.getModel().isSelected(); 
				if (selected) { 
                	view.getDrawPanel().setSpeedVisible(true);
                } 
                else { 
                	view.getDrawPanel().setSpeedVisible(false);
                } 
			}
		});
		
		this.view.getB3().addActionListener(e -> {
			if (this.view.getDrawPanel().getDataSet().size() == 0) return;
			laneGraph = new GraphController(null, this.view.getDrawPanel().getDataSet());
			laneGraph.NewScreen();
		});
		
		// Panner listener
		this.view.getControlBtn().addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				AbstractButton abstractButton =  
						(AbstractButton)e.getSource(); 
				boolean selected = abstractButton.getModel().isSelected(); 
				if (selected) { 
					panner.setAllowEdit(true);
				} 
				else { 
					panner.setDefaultPos();		
					panner.setAllowEdit(false);
				} 
			}
		});
		this.view.getRoads_color_btn1().addChangeListener(e -> view.getDrawPanel().setRoadColor(true));
		this.view.getRoads_color_btn2().addChangeListener(e -> view.getDrawPanel().setRoadColor(false));
		
		this.view.getZoomP().addActionListener(e -> {
			if (panner.isAllowEdit()) {
				view.getDrawPanel().setZoomFactor(view.getDrawPanel().getZoomFactor() * 1.1);
				view.getDrawPanel().repaint();				
			}
		});
		
		this.view.getZoomM().addActionListener(e -> {
			if (panner.isAllowEdit()) {
				view.getDrawPanel().setZoomFactor(view.getDrawPanel().getZoomFactor() / 1.1);
				view.getDrawPanel().repaint();				
			}
		});
	}

	/**
	 * Top and bottom panel buttons actions control
	 */
	private void scenariosControlls() {
		this.view.getStartButton().addActionListener(e ->  {
			startTime = System.currentTimeMillis();
			state = true;
			timer.start(); 
		});
		this.view.getStopButton().addActionListener(e -> { 
			state = false;	
			timer.stop(); 
		});
		
		cb = this.view.getScenar();
		
		// Scenarios
		cb.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				sim = new Simulator();
				setSimulator(sim);
				sim.runScenario(sim.getScenarios()[cb.getSelectedIndex()]);
				view.getDrawPanel().setSim(sim);
				view.getDrawPanel().computeModelDimensions();
				view.getDrawPanel().repaint();
			}
		});
		
		this.view.getSlider().addChangeListener(e -> setTimerPeriod(e));
	}

	/**
	 * Menu buttons controls
	 */
	private void menuControlls() {
		this.view.getItemExitButton().addActionListener(e -> System.exit(1));
		
		// Save to SVG
		this.view.getItem3().addActionListener(e -> {
			JFileChooser fileChooser = new JFileChooser();
			if (fileChooser.showSaveDialog(view.getDrawPanel()) == JFileChooser.APPROVE_OPTION) {
			  File file = fileChooser.getSelectedFile();
			  
			  SVGGraphics2D g2 = new SVGGraphics2D(
					  view.getDrawPanel().getWidth(), 
					  view.getDrawPanel().getHeight());
			  view.getDrawPanel().drawComponent(g2);
			  saveSVG(file, g2);
			}
		});
		
		// Save image
		this.view.getItem2().addActionListener(e -> {
			JFileChooser fileChooser = new JFileChooser();
			if (fileChooser.showSaveDialog(view.getDrawPanel()) == JFileChooser.APPROVE_OPTION) {
			  File file = fileChooser.getSelectedFile();
			  
			  BufferedImage img = new BufferedImage(view.getDrawPanel().getWidth(), 
					  view.getDrawPanel().getHeight(), 
					  BufferedImage.TYPE_INT_RGB);
			  Graphics2D g2d = img.createGraphics();
			  view.getDrawPanel().drawComponent(g2d);
			  saveImage(img, file.getPath());
			}
		});
		
		// Print crossroad
		this.view.getItem().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				PrinterJob job = PrinterJob.getPrinterJob();
				job.setPrintable(view.getDrawPanel());
				boolean doPrint = job.printDialog();
				if(doPrint) {
					try {
						job.print();
					} catch (PrinterException ex) {
						JOptionPane.showMessageDialog(view.getDrawPanel(), "Chyba tisku");
					}
				}
			}
		});
	}


	/**
	 * Set new timer period (simulation speed)
	 * @param e event
	 */
	private void setTimerPeriod(ChangeEvent e) {
	     timer.stop();
		 JSlider source = (JSlider)e.getSource();
		 defaultPeriod = source.getValue();
	     if (state) timer.restart();
	}

	/**
	 * Set new simulation instance
	 * @param sim
	 */
	public void setSimulator (Simulator sim) {
		this.sim = sim;
	}
	
	/**
	 * Save image to selected file path
	 * @param img		created image
	 * @param filename	file name
	 */
	private static void saveImage(BufferedImage img, String filename) {
		BufferedImage imageRGB = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics2D g2 = imageRGB.createGraphics();
		g2.drawImage(img, 0, 0, null);
		try {
			ImageIO.write(imageRGB, "png", new File(filename + ".png"));
		} catch (Exception e) {
			System.out.println("Zapis se nezdaril");
		}
	}
	
	/**
	 * Save SVG file to selected file path
	 * @param file		selected file
	 * @param g2		SVG graphic context
	 */
	private void saveSVG(File file, SVGGraphics2D g2) {
		  BufferedWriter writer = null;
		  try {
			  writer = new BufferedWriter(new FileWriter(file));
			  writer.write(g2.getSVGElement());
		  } catch (IOException e1) {
			  e1.printStackTrace();
		  } finally {
			  try {
				  writer.close();
			  } catch (IOException e1) {
				  e1.printStackTrace();
			  }
		  }
	}

}

class DragHandler implements MouseListener, MouseMotionListener {

	private DrawPanel dp;
	private Point2D startPoint;
	private double referenceX;
	private double referenceY;
	private boolean allowEdit = false;
	private boolean allowSelect = false;
	// saves the initial transform at the beginning of the pan interaction
	AffineTransform initialTransform;
		
	public DragHandler(DrawPanel dp) {
		this.dp = dp;
	}
	
	public void setDefaultPos () {
		dp.setZoomFactor(1);
		dp.setTranslateX(0);
		dp.setTranslateY(0);
		dp.repaint();
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (allowEdit) {
			try {
				startPoint = initialTransform.inverseTransform(e.getPoint(), null);
			}
			catch (NoninvertibleTransformException te) {
				System.out.println(te);
			}
			
			double deltaX = startPoint.getX() - referenceX;
			double deltaY = startPoint.getY() - referenceY;
			
			referenceX = startPoint.getX();
			referenceY = startPoint.getY();
			
			dp.setTranslateX(dp.getTranslateX() + deltaX);
			dp.setTranslateY(dp.getTranslateY() + deltaY);
			
			dp.repaint();
		}
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {}

	@SuppressWarnings("rawtypes")
	@Override
	public void mouseClicked(MouseEvent e) {
		if (allowSelect) {
			float laneSize = dp.getLaneSize();
			HashMap<Shape, Lane> roads = dp.getRoads();
			
			for(Map.Entry m:roads.entrySet()){   
				 GeneralPath shape = (GeneralPath) m.getKey();
				 double r = laneSize / 2;
				 Rectangle2D range = new Rectangle2D.Double(e.getX() - r, e.getY() - r, r, r);
				  
				 if (shape.intersects(range)) {
					 Lane lane = (Lane) m.getValue();
					 if (dp.getDataSet().size() == 0) return;
					 GraphController laneGraph = new GraphController(lane, dp.getDataSet());
					 laneGraph.NewScreen();
				 }
			} 
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (allowEdit) {
			try {
				startPoint = dp.getAt().inverseTransform(e.getPoint(), null);
			}
			catch (NoninvertibleTransformException te) {
				System.out.println(te);
			}
			
			// save the transformed starting point and the initial
			// transform
			referenceX = startPoint.getX();
			referenceY = startPoint.getY();
			initialTransform =  dp.getAt();
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}

	@Override
	public void mouseReleased(MouseEvent e) {}

	public void setAllowEdit(boolean allowEdit) {
		this.allowEdit = allowEdit;
	}

	public boolean isAllowEdit() {
		return allowEdit;
	}

	public void setAllowSelect(boolean allowSelect) {
		this.allowSelect = allowSelect;
	}
	
	
}




