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
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
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
	
	public CrossRoadController(View v) {
		this.view = v;
		initListeners();
	}

	private void initListeners() {
		
		DragHandler panner = new DragHandler(this.view.getDrawPanel());
		this.view.getDrawPanel().addMouseListener(panner);
		this.view.getDrawPanel().addMouseMotionListener(panner);
		
		timer = new Timer(defaultPeriod, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				long now = System.currentTimeMillis();
//				double stepValue = (double) (now - startTime) / 1000;
				double stepValue = (double) defaultPeriod / 1000;
				sim.nextStep(stepValue);
				view.getDrawPanel().setSimulation_time(defaultPeriod);
				view.getDrawPanel().updateDataSet(stepValue);
				view.getDrawPanel().repaint();
			}
		});
		
		this.view.getItemExitButton().addActionListener(e -> System.exit(1));
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
		this.view.getRoads_color_btn1().addChangeListener(e -> view.getDrawPanel().setRoadColor(true));
		this.view.getRoads_color_btn2().addChangeListener(e -> view.getDrawPanel().setRoadColor(false));
		this.view.getZoomP().addActionListener(e -> {
			view.getDrawPanel().setZoomFactor(view.getDrawPanel().getZoomFactor() * 1.1);
			view.getDrawPanel().repaint();
		});
		this.view.getZoomM().addActionListener(e -> {
			view.getDrawPanel().setZoomFactor(view.getDrawPanel().getZoomFactor() / 1.1);
			view.getDrawPanel().repaint();
		});
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
	}
	
	private void setTimerPeriod(ChangeEvent e) {
	     timer.stop();
		 JSlider source = (JSlider)e.getSource();
		 defaultPeriod = source.getValue();
	     if (state) timer.restart();
	}

	public void setSimulator (Simulator sim) {
		this.sim = sim;
	}
	
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
}

class DragHandler implements MouseListener, MouseMotionListener {

	private DrawPanel dp;
	private Point2D startPoint;
	private double referenceX;
	private double referenceY;
	// saves the initial transform at the beginning of the pan interaction
	AffineTransform initialTransform;
		
	public DragHandler(DrawPanel dp) {
		this.dp = dp;
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		 // first transform the mouse point to the pan and zoom
	    // coordinates. We must take care to transform by the
	    // initial tranform, not the updated transform, so that
	    // both the initial reference point and all subsequent
	    // reference points are measured against the same origin.
	    try {
	    	startPoint = initialTransform.inverseTransform(e.getPoint(), null);
	    }
	    catch (NoninvertibleTransformException te) {
	    	System.out.println(te);
	    }

	    // the size of the pan translations 
	    // are defined by the current mouse location subtracted
	    // from the reference location
	    double deltaX = startPoint.getX() - referenceX;
	    double deltaY = startPoint.getY() - referenceY;

	    // make the reference point be the new mouse point. 
	    referenceX = startPoint.getX();
	    referenceY = startPoint.getY();
	    
	    dp.setTranslateX(dp.getTranslateX() + deltaX);
	    dp.setTranslateY(dp.getTranslateY() + deltaY);
 
	    // schedule a repaint.
	    dp.repaint();
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void mouseClicked(MouseEvent e) {
		float laneSize = dp.getLaneSize();
		HashMap<Shape, Lane> roads = dp.getRoads();
		
		for(Map.Entry m:dp.getRoads().entrySet()){   
			 Shape shape = (Shape) m.getKey();
			 
			 Rectangle2D range = new Rectangle2D.Double(e.getX() - laneSize, e.getY() - laneSize, laneSize, laneSize);
	            
	            if (shape.intersects(range)) {
	            	Lane lane = (Lane) m.getValue();
	            	GraphController laneGraph = new GraphController(lane, dp.getDataSet());
	    			laneGraph.NewScreen();
	            }
		} 
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
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

	@Override
	public void mouseReleased(MouseEvent e) {
	}
}




