package application;

import java.awt.geom.Point2D;

import TrafficSim.Lane;

public class MyLane {
	private Point2D start;
	private Point2D end;
	private double size;
	private Lane line;
	
	public MyLane(Point2D start, Point2D end, double size, Lane line) {
		this.start = start;
		this.end = end;
		this.size = size;
		this.line = line;
	}

	public Point2D getStart() {
		return start;
	}

	public Point2D getEnd() {
		return end;
	}

	public double getSize() {
		return size;
	}

	public Lane getLine() {
		return line;
	}
	
	
}
