package polyfit;

import java.io.BufferedReader;
import java.io.FileReader;

import org.json.JSONArray;
import org.json.JSONObject;

import Jama.Matrix;

public class pack {

	static int maxorder = 11;

	static double maxVTVtimes = 19;

	static double maxVTPVtimes = 2.25;

	static double aHuber = 3; // 10000;

	static double steprate = 40;

	static double accuracy = 0.0001;

	static int recentdata = 21;

	static int endprediction = 8;

	static double interval = 0.01;

	static double totalInterval = 3.65;

	static double discountRate = 0.5;

	static double[] stagesweight = { 0.05, 0.9, 0.05 };

	Matrix matrix;

	int id = 0;
	double val = 0;

	double number = 1000000;

	double value = 1000000;

	public double r = 1000000;

	double x;

	double y;

	public pack() {

	}

	public pack minus(pack otherPack) {
		this.x -= otherPack.getX();
		int divide = 864000 * 10;
		this.x = this.getX() / divide;
		this.y -= otherPack.getY();
		return this;
	}

	public pack minus(pack otherPack, int divide, int type) {
		this.x -= otherPack.getX();
		this.x = this.getX() / divide;
		if (type == 0) {
			this.y -= otherPack.getY();
		} else if (type == -1) {
			this.y -= otherPack.getY();
		} else if (type == -2) {
			this.y -= otherPack.getY();
			this.y = Math.signum(this.y) * Math.sqrt(Math.abs(this.y));
		} else if (type == 2) {
			this.y = Math.signum(this.y) * Math.sqrt(Math.abs(this.y));
		} else if (type == -3) {
			this.y -= otherPack.getY();
			if (this.y == 0) {
				this.y = 1;
			}
			this.y = Math.signum(this.y) * Math.log(Math.abs(this.y));
		} else if (type == 3) {
			if (this.y == 0) {
				this.y = 1;
			}
			this.y = Math.signum(this.y) * Math.log(Math.abs(this.y));
		} else if (type == -10) {
			this.y -= otherPack.getY();
			if (this.y == 0) {
				this.y = 1;
			}
			this.y = Math.signum(this.y) * (Math.log(Math.abs(this.y)) / (Math.log(10)));
		} else if (type == 10) {
			if (this.y == 0) {
				this.y = 1;
			}
			this.y = Math.signum(this.y) * (Math.log(Math.abs(this.y)) / (Math.log(10)));
		} else if (type > 1000) {
			this.y -= otherPack.getY();
			if (this.y == 0) {
				this.y = 1;
			}
			this.y = Math.signum(this.y) * (Math.log(Math.abs(this.y)) / (Math.log(type / 1000.0)));
		}
		return this;
	}

	public pack(double x, double y) {
		super();
		this.id = (int) x;
		this.x = x;
		this.y = y;
		this.val = y;
	}
	public pack(JSONObject point) {
		super();
		int x = Integer.valueOf(point.getString("ts"));
		this.id = (int) x;
		this.x = x;
		this.y = Double.valueOf(point.getString("idxPx"));
		this.val = y;
	}
	public pack(JSONArray pointArray) {
		super();
		double x = pointArray.optDouble(0);
		double o = pointArray.optDouble(1);
		double h = pointArray.optDouble(2);
		double l = pointArray.optDouble(3);
		double c = pointArray.optDouble(4);
		this.id = (int) x;
		this.x = x;
		this.y = ((o + h + l + c) / 4);
		this.val = y;
	}
	public pack(JSONArray pointArray, boolean BN) {
		super();
		double x = pointArray.optDouble(0);
		double o = pointArray.optDouble(1);
		double h = pointArray.optDouble(2);
		double l = pointArray.optDouble(3);
		double c = pointArray.optDouble(4);
		this.id = (int) x;
		this.x = x;
		this.y = ((o + h + l + c) / 4);
		this.val = y;
	}

	public Matrix getMatrix() {
		return matrix;
	}

	public void setMatrix(Matrix matrix) {
		this.matrix = matrix;
	}

	public double getNumber() {
		return number;
	}

	public void setNumber(double number) {
		this.number = number;
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}
	public String toString() {
		return this.x + ", " + this.y;
	}
	public String loadparam(String field) {
		String path = "";
		String result = "";
		try {
			BufferedReader br = new BufferedReader(new FileReader(path));
			String aline = null;
			for (int i = 0; (aline = br.readLine()) != null; i++) {
				result =  Framework.getInfoFromJson(aline, field, ":");
			}
			br.close();
			Thread.sleep(5);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

}
