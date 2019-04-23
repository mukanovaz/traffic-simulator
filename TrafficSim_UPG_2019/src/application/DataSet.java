package application;

public class DataSet {
	int time;
	double value;
	
	public DataSet(int time, double value) {
		this.time = time;
		this.value = value;
	}

	public int getTime() {
		return time;
	}

	public void setTime(int time) {
		this.time = time;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}
}
