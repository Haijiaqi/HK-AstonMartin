package formula;

import entity.Board;

public class number extends Branch {

	int index;
	int basenumber = 7;
	int dragscale;
	int absdragscale;
	int dragdir;

	public number(int input, int dragscale, Board board, int mode) {
		super(board);
		type = 0;
		index = input;
		this.dragscale = dragscale;
		if (dragscale != 0) {
			if (dragscale < 0) {
				dragdir = -1;
			} else {
				dragdir = 1;
			}
			absdragscale = dragscale / dragdir;
		} else {
			dragdir = 0;
			absdragscale = 0;
		}
	}

	@Override
	public double calculate(double left, double right, double value) {
		if (dragscale != 0 && index <= myBoard.chromosomeOn.innerparams) {
			double locvalue = Math.abs(myBoard.chromosomeOn.cons[index]);
			double normalvalue = locvalue;
			if (locvalue < 1) {
				normalvalue = 1 / locvalue;
			}
			double pow = myBoard.chromosomeOn.getRangeRandomDouble(
					getdomainleftedge(normalvalue), absdragscale - 0.95);
			double delta;
			if (locvalue < 1) {
				delta = Math.pow(normalvalue, pow - 1 - locvalue);
			} else {
				delta = Math.pow(normalvalue, pow);
			}
			myBoard.chromosomeOn.cons[index] += dragdir * delta;
			// myBoard.chromosomeOn.getgene(myBoard.chromosomeOn.genelength +
			// index) -= dragdir;
		}
		return myBoard.chromosomeOn.cons[index];
	}

	double getcon(double x) {
		double y = 0;
		y = Math.pow(x, basenumber);
		return y;
	}

	double getdomainleftedge(double x) {
		return -1 / (x - 1) - 1;
	}

	@Override
	public String print(int No) {
		// if (paint == 1) {
		// } else {
		// int delta = index - myBoard.chromosomeOn.gene.length + 1;
		// if (delta > 0) {
		// shape = "x" + String.valueOf(delta);
		// } else {
		// shape = String.valueOf((int) myBoard.chromosomeOn.cons[index]);
		// }
		// print = shape;
		// paint = 1;
		// }
		// return print;
		if (paint == 1) {
		} else {
			int delta = index
					- (myBoard.chromosomeOn.cons.length - myBoard.chromosomeOn.paramNum)
					+ 1;
			if (delta > 0) {
				shape = "x" + String.valueOf(delta);
			} else {
				shape = String.valueOf((int) myBoard.chromosomeOn.cons[index]);
			}
			print = shape;
			paint = 1;
		}
		return print;
	}
}
