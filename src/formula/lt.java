package formula;

import entity.Board;

public class lt extends Branch {

	public lt(Board board) {
		super(board);
		shape = "< ";
	}

	@Override
	public double calculate(double left, double right, double value) {
		if (left < right) {
			return 1;
		} else {
			return 0;
		}
	}

}
