package application;

public class DataSet {
	double time;
	double value1;
	double value2;
	double value3;
	
	public DataSet(double time, double value1, double value2, double value3) {
		super();
		this.time = time;
		this.value1 = value1;
		this.value2 = value2;
		this.value3 = value3;
	}
	
	public double getTime() {
		return time;
	}

	public void setTime(double time) {
		this.time = time;
	}

	public double getValue1() {
		return value1;
	}
	
	public void setValue1(double value1) {
		this.value1 = value1;
	}

	public double getValue2() {
		return value2;
	}

	public void setValue2(double value2) {
		this.value2 = value2;
	}

	public double getValue3() {
		return value3;
	}

	public void setValue3(double value3) {
		this.value3 = value3;
	}

	@Override
	public String toString() {
		return "[" + time + " : " + value1 + ", " + value2 + ", " + value3 + "]";
	}
}
