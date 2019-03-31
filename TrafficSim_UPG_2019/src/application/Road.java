package application;

import java.awt.geom.Point2D;

public class Road {

	private int number;
	private String id;
	private Point2D startPos;
	private Point2D endPos;
	
	public Road(int number, String id, Point2D x, Point2D y) {
		super();
		this.number = number;
		this.id = id;
		this.startPos = x;
		this.endPos = y;
	}

	public Point2D getStartPos() {
		return startPos;
	}

	public void setX(Point2D x) {
		this.startPos = x;
	}

	public Point2D getEndPos() {
		return endPos;
	}

	public void setY(Point2D y) {
		this.endPos = y;
	}


	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	@Override
	public String toString() {
		return id;
	}

}
