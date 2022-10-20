package formula;

import entity.Board;

public class max extends Branch {

	public max(Board board) {
		super(board);
		shape = "mx";
	}

	@Override
	public String print(int No) {
		if (paint == 1) {
		} else {
			print = shape + "(" + child(No, 1).print(getNextNo(No, 1)) + ","
					+ child(No, 0).print(getNextNo(No, 0)) + ")";
			paint = 1;
		}
		return print;
	}

	@Override
	public double calculate(double left, double right, double value) {
		return Math.max(left, right);
	}

}
