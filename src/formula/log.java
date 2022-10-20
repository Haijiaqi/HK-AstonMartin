package formula;

import entity.Board;

public class log extends Branch {

	public log(Board board) {
		super(board);
		shape = "lg";
	}

	@Override
	public String print(int No) {
		if (paint == 1) {
		} else {
			print = "(" + shape + child(No, 1).print(getNextNo(No, 1)) + "_"
					+ child(No, 0).print(getNextNo(No, 0)) + ")";
			paint = 1;
		}
		return print;
	}

	@Override
	public double calculate(double left, double right, double value) {
		if (left <= 0 || right <= 0 || left == 1) {
//			System.err.println("below zero log:" + print);
			return 1;
		} else {
			return Math.log(right) / Math.log(left);
		}
	}

}
