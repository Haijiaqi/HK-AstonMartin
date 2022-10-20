package formula;

import entity.Board;

public class subtract extends Branch {

	public subtract(Board board) {
		super(board);
		shape = "- ";
	}

	@Override
	public double calculate(double left, double right, double value) {
		return left - right;
	}

}
