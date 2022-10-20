package formula;

import entity.Board;
import entity.chromosome;

public class Branch {
	public Board myBoard;
	int id;
	double buffer;
	int flag = 0;
	int paint = 0;
	String shape;
	String print;
	public int type = 2;
	public int deep;
	// chromosome on;
	// int totalForm = 64;
	// int top = 8;
	// int isolation;
	// int codes[] = new int[2];
	// Branch[] childs = new Branch[2];
	int warnCount = 10;

	public Branch(Board myBoard) {
		this.myBoard = myBoard;
	}

	public int getNextNo(int No, int dir) {
		chromosome on = myBoard.chromosomeOn;
		return on.link[2 * No - dir];
	}

	public Branch child(int No, int dir) {
		chromosome on = myBoard.chromosomeOn;
		return myBoard.branchs[on.getgene(getNextNo(No, dir))][getNextNo(No, dir)];
	}

	public double ret(int No, double value) {
		double left = 0;
		double right = 0;
		if (flag == 0) {
			if (type > 0) {
				int leftindex = getNextNo(No, 1);
				left = child(No, 1).ret(leftindex, myBoard.chromosomeOn.gene[leftindex]);
				if (type > 1) {
					int rightindex = getNextNo(No, 0);
					right = child(No, 0).ret(rightindex, myBoard.chromosomeOn.gene[rightindex]);
				}
			}
			buffer = calculate(left, right, value);
		} else {
			if (flag % warnCount == 0) {
				System.err.println("called" + flag + "times:" + this.print(No));
			}
		}
		++flag;
		return illegalprocess(buffer);
	}

	public double calculate(double left, double right, double value) {
		return 0;
	}

	public String print(int No) {
		if (paint != 0) {
		} else {
			print = "(" + child(No, 1).print(getNextNo(No, 1)) + shape
					+ child(No, 0).print(getNextNo(No, 0)) + ")";
			paint++;
		}
		return print;
	}

	public void refresh(int No) {
		paint = 0;
		flag = 0;
		deep = 0;
		if (type > 0) {
			child(No, 1).refresh(getNextNo(No, 1));
			if (type > 1) {
				child(No, 0).refresh(getNextNo(No, 0));
			}
		}
	}

	public int getDeepth(int No, int deep) {
		if (deep >= this.deep) {
			this.deep = ++deep;
			if (type > 0) {
				child(No, 1).getDeepth(getNextNo(No, 1), this.deep);
				if (type > 1) {
					child(No, 0).getDeepth(getNextNo(No, 0), this.deep);
				}
			}
		}
//		if (myBoard.chromosomeOn.gene[0] < deep) {
//			myBoard.chromosomeOn.gene[0] = deep;
//		}
		return this.deep;
	}

	@Override
	public String toString() {
		if (shape == null) {
			return "n ";
		}
		return shape;
	}

	public double illegalprocess(double in) {
		if (Double.isInfinite(in)) {
			return Double.MAX_VALUE;
		} else if (Double.isNaN(in)) {
			return 1;
		}
		return in;
	}
}
