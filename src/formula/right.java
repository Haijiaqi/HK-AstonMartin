package formula;

import entity.Board;

public class right extends Branch {

	public right(Board board) {
		super(board);
		shape = "";
	}

	@Override
	public String print(int No) {
		if (paint == 1) {
		} else {
			print = child(No, 0).print(getNextNo(No, 0)) + ")";
			paint = 1;
		}
		return print;
	}

	@Override
	public double calculate(double left, double right, double value) {
		return right;
	}

}
