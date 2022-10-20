package entity;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import javax.naming.spi.DirStateFactory.Result;

import formula.Branch;

public class chromosome {

	public double gene[];
	public int link[];
	public double cons[];
	public double score[];
	Board myBoard;
	public Random rand = new Random();
	int formNum;
	public int paramNum;
	int length;
	int layers;
	public int genelength;
	public int innerparams;
	public double fitness;

	public chromosome(Board myBoard) {
		// this.myBoard = myBoard;
		// formNum = myBoard.formNum;
		// paramNum = myBoard.paramNum;
		// length = myBoard.length;
		// layers = myBoard.layers;
		// gene = new int[length];
		// cons = new double[length + paramNum];
		// link = new int[2 * length - 1];
		// produceMeRandomly();
		this.myBoard = myBoard;
		formNum = myBoard.formNum;
		paramNum = myBoard.paramNum;
		length = myBoard.length;
		layers = myBoard.layers;
		genelength = myBoard.genelength;
		gene = new double[length];
		score = new double[length];
		cons = new double[length / 2 + 1];
		link = new int[length - 1];
		innerparams = cons.length - paramNum - 1;
		produceMeRandomly();
	}

	public int getgene(int index) {
		return (int)gene[index];
	}
	
	public void produceMeRandomly() {
		// gene[0] = 0;
		// cons[0] = 0.6;// Math.abs(rand.nextDouble());
		// for (int i = 1; i < gene.length; i++) {
		// gene[i] = getRangeRandom(0, formNum);
		// if (i >= gene.length / 2 && i != gene.length - 1) {
		// setPtrsRandomly(i);
		// } else {
		// link[2 * i - 1] = 2 * i;
		// link[2 * i] = 2 * i + 1;
		// }
		// cons[i] = getRangeRandom(-1024, 1024);
		// }
		gene[0] = 0;
		cons[0] = 1;// Math.abs(rand.nextDouble());
		int i = 1;
		for (; i <= genelength; i++) {
			gene[i] = getRangeRandom(1, formNum);
			link[2 * i - 1] = 2 * i;
			link[2 * i] = 2 * i + 1;
			cons[i] = producecons();
		}
		for (; i < gene.length; i++) {
			gene[i] = getRangeRandom(1, formNum);
		}
	}

	public void setPtrsRandomly(int i) {
		link[2 * i - 1] = getRangeRandom(i + 1, gene.length - 1);
		link[2 * i] = getRangeRandom(i + 1, gene.length - 1);
	}

	public double producecons() {
		double result = 0;
		double base = 1024;
		result = rand.nextGaussian() * Math.pow(base, rand.nextGaussian());
		return result;
	}

	public int value(Trainingset[] set, int generation) {
		myBoard.process(this);
		double temp = 0;//gene[0];
		double termscore = 0;
		for (int i = 0; i < set.length; i++) {
			score[i] = Math.abs(set[i].out - parse(set[i].in));
		}
//		for (Trainingset sample : set) {
//			termscore += Math.abs(sample.out - parse(sample.in));
//		}
//		termscore /= set.length;
//		temp = temp * (generation - 1);
//		temp += termscore + getDeepth();
//		temp = temp / generation;
//		gene[0] = (int)temp;
		Arrays.sort(score);
		gene[0] = (int)score[length / 2] + getDeepth();
		return getgene(0);
	}

	public double parse(double[] var) {
		// myBoard.process(this);
		// clear();
		// loadDouble(cons, length, var);
		// double result = myBoard.branchs[gene[1]][1].ret(1);
		// // System.err.println(result + " " + print());
		// return result;
		myBoard.process(this);
		clear();
		loadDouble(cons, cons.length - var.length, var);
		double result = myBoard.branchs[getgene(1)][1].ret(1, gene[1]);
		// System.err.println(result + " " + print());
		return result;
	}

	public chromosome fertilize(chromosome father, double rate) {
		cons[0] = father.cons[0];
		link[0] = father.link[0];
		gene[0] = father.gene[0];
		double changerate = cons[0] * rate;
		int No = getRangeRandom(1, genelength);
		selectbranch(No, father, changerate);
		return this;
	}

	public chromosome continuitychange(chromosome flag, double rate) {
		cons[0] = flag.cons[0];
		link[0] = flag.link[0];
		gene[0] = flag.gene[0];
		double changerate = cons[0] * rate;
		for (int i = 1; i < flag.gene.length; i++) {
			if (rand.nextDouble() < changerate) {
				closeto(gene, flag.gene, i);
			}
		}
//		for (int i = 0; i < flag.link.length; i++) {
//			if (rand.nextDouble() < cons[0] * rate) {
//				closeto(link, flag.link, i);
//			}
//		}
		return this;
	}

	public chromosome sentlink(chromosome other, double rate) {
		cons[0] = other.cons[0];
		link[0] = other.link[0];
		gene[0] = other.gene[0];
		double changerate = cons[0] * rate;
		int edge = (int) (other.link.length / changerate);
		int linkposition = 0;
		int times = rand.nextInt(link.length);
		for (int i = 0; i < times; i++) {
			linkposition = getRangeRandom(1, edge);
			if (linkposition < other.link.length) {
				transalink(other.link, linkposition);
			}
		}
		return this;
	}

	public chromosome selfchange(double rate) {
		// cons[0] = Math.abs(rand.nextDouble());
		double changerate = cons[0] * rate;
		changeGene(changerate);
		changePtr(changerate);
		changeCon(changerate);
		return this;
	}

	public chromosome inject(chromosome other) {
		inject(other.gene, other.link, other.cons);
		return this;
	}

	public chromosome copy() {
		chromosome newOne = new chromosome(myBoard);
		loadDouble(newOne.gene, 0, gene);
		loadInt(newOne.link, 0, link);
		loadDouble(newOne.cons, 0, cons);
		return newOne;
	}

	public void afterprocess(int i) {
		cons[0] = i / (myBoard.population.length * 1.0);
	}
	
	public void changeGene(double changeRate) {
		changearray(gene, 1, genelength + innerparams, formNum, changeRate);
		/*
		 * if (Math.random() < changeRate) { gene[getRangeRandom(1, gene.length
		 * - paramNum - 1)] = rand .nextInt(formNum); }
		 */
	}

	public void changePtr(double changeRate) {
		double randnum = 0;
		// Sint i = getRangeRandom(1, gene.length - 2);
		for (int i = 1; i <= genelength; i++) {
			randnum = rand.nextDouble();
			if (randnum < 0.25 * changeRate) {
				link[2 * i - 1] = getRangeRandom(i + 1, gene.length - 1);
			} else if (randnum < 0.5 * changeRate) {
				link[2 * i] = getRangeRandom(i + 1, gene.length - 1);
			} else if (randnum < changeRate) {
				setPtrsRandomly(i);
			}
		}
	}

	public void changeCon(double changeRate) {
		int times = rand.nextInt(innerparams + 1);
		double randnum = 0;
		for (int i = 0; i < times; i++) {
			randnum = rand.nextDouble();
			if (randnum < changeRate) {
				cons[getRangeRandom(1, innerparams)] = producecons();
			}
		}
	}

	public double[] changearray(double[] array, int start, int end, int range,
			double changerate) {
		int edge = (int) (2 * range / changerate);
		int newcode = 0;
		for (int i = start; i <= end; i++) {
			newcode = getRangeRandom(1, edge);
			if (newcode <= range) {
				array[i] = newcode;
			}
		}
		return array;
	}

	public void closeto(double[] thisarray, double[] otherarray, int index) {
		thisarray[index] += (int) Math.signum(otherarray[index] - thisarray[index]);
	}

	public void awayfrom(int[] thisarray, int[] otherarray, int index) {
		thisarray[index] -= normalize((int) Math.signum(otherarray[index] - thisarray[index]));
	}
	
	public int normalize(int in) {
		in -= 1;
		in = Math.floorMod(in, formNum);
		in += 1;
		return in;
	}

	public int getRangeRandom(int from, int to) {
		return rand.nextInt(to - from + 1) + from;
	}

	public double getRangeRandomDouble(double from, double to) {
		return rand.nextDouble() * (to - from) + from;
	}

	public void loadInt(int[] col, int startIndex, int endIndex, int[] number) {
		if (0 <= startIndex && startIndex <= endIndex
				&& endIndex < number.length && number.length <= col.length) {
			for (int i = startIndex; i <= endIndex; i++) {
				col[i] = number[i];
			}
		} else {
			System.err.println("cant lod nubr!");
		}
	}

	public void loadInt(int[] col, int startIndex, int[] number) {
		if (col.length - startIndex >= number.length) {
			for (int i = 1; i < number.length; i++) {
				col[i + startIndex] = number[i];
			}
		} else {
			System.err.println("cant lod nubr!");
		}
	}

	public void loadDouble(double[] col, int startIndex, double[] number) {
		if (col.length - startIndex >= number.length) {
			for (int i = 0; i < number.length; i++) {
				col[i + startIndex] = number[i];
			}
		} else {
			System.err.println("cant lod nubr!");
		}
	}

	public void inject(double[] gene, int[] link, double[] cons) {
		loadDouble(this.gene, 0, gene);
		loadInt(this.link, 0, link);
		loadDouble(this.cons, 0, cons);
	}

	public int transalink(int[] sourcelink, int linkposition) {
		int geneindex = sourcelink[linkposition];
		if (geneindex <= genelength) {
			return this.link[getRangeRandom(1, 2 * geneindex - 2)] = geneindex;
		} else {
			return this.link[getRangeRandom(1, this.link.length - 1)] = geneindex;
		}
	}

	public int selectbranch(int No, chromosome father, double rate) {
		gene[No] = father.gene[No];
		if (myBoard.branchs[getgene(No)][No].type != 0) {
			double dice = rand.nextDouble();
			if (dice < 0.25 * rate) {
				link[2 * No - 1] = father.link[2 * No - 1];
				selectbranch(father.link[2 * No - 1], father, rate);
			} else if (dice < 0.5 * rate) {
				link[2 * No] = father.link[2 * No];
				selectbranch(father.link[2 * No], father, rate);
			} else if (dice < 0.75 * rate) {
				link[2 * No - 1] = father.link[2 * No - 1];
				selectbranch(father.link[2 * No - 1], father, rate);
				link[2 * No] = father.link[2 * No];
				selectbranch(father.link[2 * No], father, rate);
			}
		} else {
			int conindex = No - genelength;
			cons[conindex] = father.cons[conindex];
		}
		return getgene(No);
	}

	public String print() {
		myBoard.process(this);
		clear();
		return myBoard.branchs[getgene(1)][1].print(1);
	}

	public void clear() {
		myBoard.branchs[getgene(1)][1].refresh(1);
	}

	public int getDeepth() {
		myBoard.process(this);
		clear();		
		return myBoard.branchs[getgene(1)][1].getDeepth(1, 0);
	}
	
	public chromosome loadalive(String infomation) {
		ArrayList<String> info = Board.cutString(infomation, ";");
		ArrayList<String> geneinfo = Board.cutString(info.get(0), ",");
		// gene = new int[geneinfo.size()];
		for (int i = 1; i < gene.length; i++) {
			gene[i] = Integer.valueOf(geneinfo.get(i));
		}
		ArrayList<String> linkinfo = Board.cutString(info.get(1), ",");
		// link = new int[linkinfo.size()];
		for (int i = 1; i < link.length; i++) {
			link[i] = Integer.valueOf(linkinfo.get(i));
		}
		ArrayList<String> consinfo = Board.cutString(info.get(2), ",");
		// cons = new double[consinfo.size()];
		for (int i = 1; i < cons.length; i++) {
			cons[i] = Double.valueOf(consinfo.get(i));
		}
		return this;
	}

	public String writedown() {
		String result = "";
		for (double g : gene) {
			result += String.valueOf(g) + ",";
		}
		result += ";";
		for (int l : link) {
			result += String.valueOf(l) + ",";
		}
		result += ";";
		for (double c : cons) {
			result += String.valueOf(c) + ",";
		}
		result += ";\n";
		return result;
	}
	
	public int consanguinity(chromosome other) {
		if (issame(gene, other.gene)) {
			if (issame(link, other.link)) {
				return 1;
			} else {
				return 2;
			}
		} else {
			if (issame(link, other.link)) {
				return 3;
			} else {
				return 4;
			}
		}
	}

	public boolean looklike(chromosome other) {
		if (this.print().equals(other.print())) {
			return true;
		} else {
			return false;
		}
	}

	public boolean issame(int[] a, int[] b) {
		boolean result = true;
		if (a.length == b.length) {
			for (int i = 1; i < a.length && result; i++) {
				if (a[i] == b[i]) {
				} else {
					result = false;
				}
			}
		} else {
			result = false;
		}
		return result;
	}

	public boolean issame(double[] a, double[] b) {
		boolean result = true;
		if (a.length == b.length) {
			for (int i = 1; i < a.length && result; i++) {
				if ((int)a[i] == (int)b[i]) {
				} else {
					result = false;
				}
			}
		} else {
			result = false;
		}
		return result;
	}

	public void producedata(String path, int quantity) {
		try {
			File file = new File(path);
			if (!file.exists()) {
				file.createNewFile();
			}
			BufferedWriter bw = new BufferedWriter(new FileWriter(
					file.getAbsoluteFile()));
			double[] var = new double[paramNum];
			for (int i = 0; i < quantity; i++) {
				for (int j = 0; j < var.length; j++) {
					var[j] = getRangeRandom(-100, 100);
				}
				bw.write(parseprint(var));
			}
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String parseprint(double[] var) {
		myBoard.process(this);
		clear();
		loadDouble(cons, cons.length - var.length, var);
		double result = myBoard.branchs[getgene(1)][1].ret(1, gene[1]);
		String print = String.valueOf(result) + ";";
		for (double d : var) {
			print += String.valueOf(d) + ",";
		}
		print += ";\n";
		return print;
	}

}