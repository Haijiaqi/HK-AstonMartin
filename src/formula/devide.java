package formula;

import entity.Board;

public class devide extends Branch {

	public devide(Board board) {
		super(board);
		shape="/ ";
	}

	@Override
	public double calculate(double left, double right, double value) {
		if (right == 0) {
//			System.err.println("zero devide danger:" + print);
			return 1;
		} else {
			return left / right;			
		}
	}

}
