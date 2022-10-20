package formula;

import entity.Board;

public class pow extends Branch {

	public pow(Board board) {
		super(board);
		shape = "^ ";
	}

	@Override
	public double calculate(double left, double right, double value) {
		if (right == 0) {
			if (left == 0) {
//				System.err.println("zero zero pow:" + print);
			}
		} else if (left < 0 && (1 / right) % 2 == 0) {
//			System.err.println("negative square:" + print);
		} else {
			return Math.pow(left, right);
		}
		return 1;
	}

}
