package formula;

import entity.Board;

public class gte extends Branch {

	public gte(Board board) {
		super(board);
		shape = ">=";
	}

	@Override
	public double calculate(double left, double right, double value) {
		if (left >= right) {
			return 1;
		} else {
			return 2;
		}
	}

}
