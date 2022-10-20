package formula;

import entity.Board;

public class multiply extends Branch {

	public multiply(Board board) {
		super(board);
		shape = "* ";
	}

	@Override
	public double calculate(double left, double right, double value) {
		return left * right;
	}

}
