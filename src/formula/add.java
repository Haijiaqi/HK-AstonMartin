package formula;

import entity.Board;

public class add extends Branch {

	public add(Board board) {
		super(board);
		shape = "+ ";
	}

	@Override
	public double calculate(double left, double right, double value) {
		return left + right;
	}
}
