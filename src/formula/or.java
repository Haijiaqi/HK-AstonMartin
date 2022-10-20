package formula;

import entity.Board;

public class or extends Branch {

	public or(Board board) {
		super(board);
		shape = "||";
	}

	@Override
	public double calculate(double left, double right, double value) {
		if (left != 0 || right != 0) {
			return 1;
		} else {
			return 0;
		}
	}

}
