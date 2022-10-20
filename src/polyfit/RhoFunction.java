package polyfit;

public class RhoFunction {
	
	int type = 0;
	
	double a = pack.aHuber;

	public double rho(double v) {
		return Math.abs(v);
	}
	
	public double valueweight(double v, double MAD) {
		double u = v / MAD;
		double result = 1;
		switch (type) {
		case 0:
			result = weightofHuber(u);
			break;

		default:
			break;
		}
		return result;
	}
	
	public double weightofHuber(double u){
		double lul = Math.abs(u);
		if (lul > a) {
			return a / lul;
		} else {
			return 1;
		}		
	}

}
