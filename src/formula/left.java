package formula;

import entity.Board;

public class left extends Branch {

	public left(Board board) {
		super(board);
		shape = "";
	}

	@Override
	public String print(int No) {
		if (paint == 1) {
		} else {
			print = child(No, 1).print(getNextNo(No, 1));
			paint = 1;
		}
		return print;
	}

	@Override
	public double calculate(double left, double right, double value) {
		return left;
	}

}
