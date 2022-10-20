package entity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

import Jama.test.TestMatrix;
import formula.Branch;
import formula.add;
import formula.and;
import formula.devide;
import formula.equal;
import formula.gt;
import formula.gte;
import formula.log;
import formula.lt;
import formula.lte;
import formula.max;
import formula.min;
import formula.multiply;
import formula.number;
import formula.or;
import formula.pow;
import formula.subtract;
import formula.xor;

public class Board {
	int mode = 0;
	public Branch[][] branchs;
	int formNum;
	double formmidno;
	int paramNum;
	public int length;
	int layers;
	int genelength;
	// public double[] params;
	public Branch[] cons;
	public chromosome chromosomeOn;
	chromosome[] population = new chromosome[512];
	static Random rand = new Random();
	public static String sleeppath = "";
	public static String datapath = "";

	public Board(int firstfloor, int realparamsnum, int formNum, int mode) {
		// this.formNum = formNum;
		// this.paramNum = realparamsnum;
		// length = 1;
		// for (layers = 1; length < firstfloor; ++layers) {
		// length = 2 * length;
		// }
		// length = 2 * length;
		// params = new double[length + paramNum];
		// cons = new Branch[paramNum + 1];
		// branchs = new Branch[formNum + 1][length];
		// loadParam(cons, length);
		// loadBoard();
		this.mode = mode;
		this.formNum = formNum;
		this.formmidno = formNum / 2;
		this.paramNum = realparamsnum;
		length = 1;
		for (layers = 1; length < firstfloor; ++layers) {
			length = 2 * length;
		}
		firstfloor = length;
		genelength = firstfloor - 1;
		length = 2 * length;
		if (mode == 1) {
			genelength = length - 1;
		}
		// params = new double[length + paramNum];
		// cons = new Branch[firstfloor + 1];
		branchs = new Branch[formNum + 1][length];
		// loadParam(cons, 1);
		loadBoard();
	}

	public static void evolution(int firstfloornum, int mode) {
		ArrayList<Trainingset> set = loaddata();
		Board board = new Board(firstfloornum, set.get(0).in.length, 6, mode);
		for (int i = 0; i < board.population.length; i++) {
			board.population[i] = new chromosome(board);
		}
		load(board.population);
		chromosome mankindone = new chromosome(board);
		chromosome clone = new chromosome(board);
		double[] a = { 0, 5, 4, 5, 3, 3, 3, 3 };
//		mankindone.gene = a;
		int[] b = { 0, 2, 4, 3, 7, 6, 6 };
		mankindone.link = b;
		double[] c = { 1, 1000, 4, 0, 0 };
		mankindone.cons = c;
//		mankindone.producedata("D:\\a\\evolution\\data.txt", 100);
//		clone.fertilize(mankindone, 0.9);
//		mankindone.sentlink(clone, 0.1);
		chromosome pre = board.population[0].copy();
		board.population[0].cons[1] += 24;
		for (int i = 1;; i++) {
			Trainingset[] thisset = produceset(set,
					board.population[0].length);
			for (chromosome individual : board.population) {
				individual.value(thisset, i);
			}
			Arrays.sort(board.population, new Comparator<chromosome>() {
				@Override
				public int compare(chromosome individual1,
						chromosome individual2) {
					return individual1.getgene(0) - individual2.getgene(0);
				}
			});
			if (!board.population[0].looklike(pre)) {
				// System.out.println(pre.print());
				System.out.println(String.valueOf(i) + " "
						+ board.population[0].getgene(0) + " "
						+ board.statistic(board.population)
						// + String.format("%.1f", board.population[0].cons[0])
						+ " " + board.population[0].print());
			}
			// System.err.println(board.population[0].print());
			// System.out.println(board.population[0].writedown());
			pre = board.population[0].copy();
			// System.out.println(pre.consanguinity(board.population[0]) +
			// pre.print());
			board.population = reproduce(board.population);
			if (/*i % 100 == 0 || */board.population[0].getgene(0) <= 20) {
				System.err.println(String.valueOf(i) + " "
						+ board.population[0].getgene(0) + " "
						+ board.statistic(board.population)
						// + String.format("%.1f", board.population[0].cons[0])
						+ " " + board.population[0].print());
				save(board.population);				
				int s = 0;
			}
		}
	}

	public static chromosome[] reproduce(chromosome[] population) {
		int half = population.length / 2;
		int top = population.length / 1 ;
		int topsegment = population.length / 3;
		int startone = 1;
		for (int i = 0; i < top; i++) {
// 7,5,4,5,3,3,3,3,;0,2,4,3,7,6,6,;0.3,1000,3351.4657273618204,1.0,2.0,;
//			population[half + i].continuitychange(population[population.length - 1], 0.01);
			population[i].afterprocess(i);
//			population[population.length - i - 1].inject(population[i]);
//			population[population.length - i - 1].fertilize(population[i], 0.5);
//			population[population.length - i - 1].sentlink(population[i], 1);
			population[i].selfchange(1);
//			population[i].sentlink(population[population.length - i - 1], 0.005);
//			population[i].selfchange(0.2);
		}
		// for (int i = 0; ; i++) {
		// startone = getnearrandindex(startone, population.length - 1) + 2;
		// if (startone < population.length - 1) {
		// population[startone].fertilize(population[i], 1);
		// population[startone].selfchange(0.1);
		// } else {
		// break;
		// }
		// // population[topsegment - i - 1].fertilize(population[i]);
		// // population[topsegment - i - 1].selfchange();
		// }
		int edge = (int) (population.length / population[0].cons[0]);
		int whichone = 0;
		int times = rand.nextInt(population.length);
		// for (int i = 0; i < times; i++) {
		// whichone = getRangeRandom(1, edge);
		// if (whichone < population.length) {
		// population[population.length - i - 1]
		// .continuitychange(population[i]);
		// population[population.length - i - 1].sentlink(population[i]);
		// }
		// }
		return population;
	}

	public static ArrayList<Trainingset> loaddata() {
		ArrayList<Trainingset> set = new ArrayList<Trainingset>();
		ArrayList<String> samplestring;
		ArrayList<String> instring;
		try {
			BufferedReader br = new BufferedReader(new FileReader(datapath));
			String aline = null;
			for (int i = 0; (aline = br.readLine()) != null; i++) {
				samplestring = cutString(aline, ";");
				instring = cutString(samplestring.get(1), ",");
				double[] in = new double[instring.size()];
				for (int j = 0; j < instring.size(); j++) {
					in[j] = Double.valueOf(instring.get(j));
				}
				set.add(new Trainingset(Double.valueOf(samplestring.get(0)), in));
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return set;
	}

	public static int load(chromosome[] population) {
		int total = 0;
		try {
			BufferedReader br = new BufferedReader(new FileReader(sleeppath));
			String aline = null;
			for (int i = 0; (aline = br.readLine()) != null; i++) {
				if (i < 1) {// /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
					population[i].loadalive(aline);
				} else {
					total = i;
					break;
				}
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return total;
	}

	public static void save(chromosome[] population) {
		try {
			File file = new File(sleeppath);
			if (!file.exists()) {
				file.createNewFile();
			}
			BufferedWriter bw = new BufferedWriter(new FileWriter(
					file.getAbsoluteFile()));
			for (chromosome one : population) {
				bw.write(one.writedown());
			}
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static Trainingset[] produceset(ArrayList<Trainingset> set,
			int length) {
		Trainingset[] result = new Trainingset[length];
		for (int i = 0; i < result.length; i++) {
			result[i] = set.get(getRangeRandom(0, set.size() - 1));
		}
		return result;
	}

	public String statistic(chromosome[] population) {
		String result = "";
		int i = 0;
		for (; population[i].getgene(0) <= population[0].getgene(0) * 2
				&& i < population.length - 1; i++) {

		}
//		int j = 0;
//		for (; population[i].looklike(population[0]) && j < population.length; j++) {
//
//		}
		return result + i;
	}

	public static int getnearrandindex(int startindex, int endindex) {
		double randnum = Math.abs(rand.nextGaussian()) * 0.1;
		int delta = (int) (randnum * (endindex - startindex));
		int index = delta + startindex;
		if (index <= endindex) {
			return index;
		} else {
			return getnearrandindex(startindex, endindex);
		}
	}

	public void process(chromosome one) {
		chromosomeOn = one;
	}

	public Branch getBranch(int id, int index, int dragscale, Board board, int mode) {
		Branch result = new Branch(board);
		switch (id) {
		case 0:
			return new number(index, dragscale, board, mode);
		case 1:
			return new log(board);
		case 2:
			return new devide(board);
		case 3:
			return new subtract(board);
		case 4:
			return new add(board);
		case 5:
			return new multiply(board);
		case 6:
			return new pow(board);
		case 7:
			return new max(board);
		case 8:
			return new min(board);
		case 9:
			return new gte(board);
		case 10:
			return new gt(board);
		case 11:
			return new lt(board);
		case 12:
			return new lte(board);
		case 13:
			return new and(board);
		case 14:
			return new or(board);
		case 15:
			return new xor(board);
		case 16:
			return new equal(board);
		default:
			break;
		}
		return result;
	}

	public void loadBoard() {
		// for (int i = 1; i < branchs.length; i++) {
		// int forms = branchs[i].length - (cons.length - 1);
		// int j = 1;
		// for (; j < forms; j++) {
		// branchs[i][j] = getBranch(i, j, this);
		// }
		// for (int k = 1; j < branchs[i].length; j++, k++) {
		// branchs[i][j] = cons[k];
		// }
		// }
		// for (int i = 1; i < branchs[0].length; i++) {
		// branchs[0][i] = getBranch(0, i, this);
		// }
		for (int i = 1; i < branchs.length; i++) {
			int forms = branchs[i].length / 2;
			int j = 1;
			for (; j < forms; j++) {
				branchs[i][j] = getBranch(i, j, getdragscale(i), this, mode);
			}
			for (int k = 1; j < branchs[i].length; j++, k++) {
				branchs[i][j] = getBranch(0, k, getdragscale(i), this, mode);
			}
		}
	}

	int getdragscale(int genevalue) {
		double delta = genevalue - formmidno;
		if (delta < -0.75 || delta > 0.75) {
			return (int) delta;
		}
		return 0;
	}

	public String print() {
		String result = "";
		for (int i = 0; i < branchs.length; i++) {
			result = result + "\n";
			for (int j = 1; j < branchs[i].length; j++) {
				result = result + branchs[i][j].toString() + " ";
			}
		}
		return result;
	}

	// public void loadParam(Branch[] col, int startIndex) {
	// for (int i = 1; i < col.length; i++, startIndex++) {
	// col[i] = getBranch(0, startIndex, this);
	// }
	// }

	public static ArrayList<String> cutString(String line, String split) {
		ArrayList<String> result = new ArrayList<String>();
		// line += split;
		int splength = split.length();
		for (;;) {
			if (line.indexOf(split) != -1) {
				result.add(line.substring(0, line.indexOf(split)));
				line = line.substring(line.indexOf(split) + splength,
						line.length());
			} else {
				break;
			}
		}
		return result;
	}

	public static int getRangeRandom(int from, int to) {
		return rand.nextInt(to - from + 1) + from;
	}
}
