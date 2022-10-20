package formula;

import entity.Board;

public class mod extends Branch {

	public mod(Board board) {
		super(board);
		shape = "%";
	}

	@Override
	public double calculate(double left, double right, double value) {
		return left % right;
	}

}
