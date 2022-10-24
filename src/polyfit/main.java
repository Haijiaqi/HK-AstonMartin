package polyfit;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class main {
	// private static Logger logger = LogManager.getLogger("HelloLog4j");
	@SuppressWarnings("unused")
	public static void maint(String[] args) {

		// File dir = new File("D:/Aproject/fund");
		// File[] files = dir.listFiles();
		for (int i = 0; true; i++) {
			// String date = files[i].getName();
			// String stamp = Framework.dateToStamp(date);
			// if (Long.valueOf(stamp) < (Long.valueOf("1561305600000"))) {
			// continue;
			// }
			Framework.clock(10, 35, true);
			// 放置当前毫秒时间戳，即可模拟，如162752337800016281317450001628660430667"1628740717091""1627554880000"String.valueOf(Framework.getNowTimestamp())"1628754632000"
			if (Framework.init("")) {
				Framework.freshNAV();// "listPath", "tradeListPath");
				Framework.executesettle();
				// Framework.produceFundIndex();
				Framework.preliminaryProcess(0);
			}
			Framework.clock(14, 40, true);
			if (Framework.immediateGo()) {
				Framework.tailProcess(0);
				Framework.stockMarket();
				Framework.produceTradeList();
			}
			Framework.waiting(60, "Todays' loop end...");
		}
	}

	@SuppressWarnings("unused")
	public static void main(String[] args) {
		String thispath = Framework.basepath + "/fund/bitcoin/D7points.txt";
		String path = Framework.getPath("balance", "balance", "BTC");
		verify.saveparam(path, "");
		for (int i = 0; i < 458; i++) {
			String[] infomation = {"BTC", "BTC" + i};
			ArrayList<pack> outpoints = verify.loadpoints(thispath, 0 + i, 240 + i, -1);
			// Investment.balanceDir = balanceDir;
			// 没加载到点就下一项
			if (outpoints.size() <= pack.maxorder * 5) {
			} else {
				int paint = 0;//5000;//
				if (i < 173) {
					paint = 0;
				}
				if (paint > 0) {
					verify.saveparam(Framework.basepath + "/pythonparam.txt", thispath + ";" + "rawinfo[0]");					
				}
				// 计策修改时关注这里以下，确保能够被分配到对应的风险级中
				// 原始数据给予解析
				Fund aFund = new Fund(infomation, outpoints, 0.5, paint, 12);
				verify.saveparam(Framework.basepath + "/processInfo.txt", "");
				verify.appenddata(Framework.basepath + "/processInfo.txt"
						// replacepath(
						// replacepath(indexPath, "date", gettodate()),
						// "all", String.valueOf(aFund.risklevel))
						, aFund.outString + "\n");
				Framework.produceTradeItem();
				Framework.execute(outpoints.get(outpoints.size() - 1).val + "");
			}
		}
	}

	public static void mains(String[] args) {
		String code = "009893";
		String fundPath = "D:/Free C/Program Files/eclipse32/workspace/polyfit/fund/2021-08-10/" + code + ".txt";
		verify.saveparam("D:/a/temp/20210702t/pythonparam.txt", fundPath + ";450;11;450");
		verify.saveparam("D:/a/temp/20210702t/actionpoints.txt", "");
		verify.saveparam("D:/a/temp/20210702t/actionpointss.txt", "");
		double tnum = 0;
		for (int num = 250; num <= 250; num += 1) {
			ArrayList<pack> points = verify.loadpoints(
				fundPath, 0, num, -1);
			String[] c = {"010592","南方医药创新股票A","NFYYCXGPA","1627401600.0","0.9277","0.9277","4.56","-9.24","-10.95","-7.35","","","","","","-7.23","1614614400.0","","0.15%"
			};
			// Fund aFund = new Fund(c, points);		
			Polynomial p = new Polynomial();
			int risklevel = (int) (num / 20 + 2);
			// risklevel = p.processLS(points, 2).getRowDimension() - 1;
			// risklevel = p.processR(points, risklevel > 11 ? 11 : 11)
			risklevel = p.processLS(points, 11).getRowDimension() - 1;
			p.worseAnalysis(points, 0);
			Polynomial polynomial_recent1;
			Polynomial polynomial_recent2;
			double[] zero = {};
			double[] extreme = {};
			double[] inflection = {};
			double[] keypoints = {};
			double tailstart = 0;
			Polynomial quadratic = new Polynomial();
			ArrayList<pack> quadraticpoints = new ArrayList<pack>();
			if (risklevel < 2) {
				tailstart = p.rawdata.get(p.rawdata.size() - 5).getX();
				quadraticpoints = p.pickdata(tailstart);
			} else {
				polynomial_recent1 = p.d(1);
				extreme = polynomial_recent1.findzeropoint();
				polynomial_recent2 = polynomial_recent1.d(1);
				inflection = polynomial_recent2.findzeropoint();
				keypoints = mergesort(extreme, inflection);
				// if (points.size() == 191) {
				// System.out.println(points.size());
				// }
				for (int i = keypoints.length - 1; true; i--) {
					if (i >= 0) {
						tailstart = keypoints[i] + 0 * pack.interval;
						quadraticpoints = p.pickdata(tailstart);
						if (quadraticpoints.size() > 5) {
							break;
						} else {
							continue;
						}
					} else {
						tailstart = p.start;
						quadraticpoints = p.pickdata(tailstart);
						break;
					}
				}
			}
			quadratic.processR(quadraticpoints, 2, 0);
			// quadratic.paintCurve("D:/a/temp/20210702t/quadratic0.txt",
			// 0.005);
			// ArrayList<pack> apoint = new ArrayList<pack>();
			double x = points.get(points.size() - 1).getX();
			double w = quadratic.getStringWeight(x + pack.interval, x, 2, 1, 1, 0.5).getX();
			System.out.println(w);// + "+" + risklevel + "+" +
									// p.evaltopratelast());
			double y = w * 10 + points.get(points.size() - 1).getY();
			// pack ap = new pack(points.get(points.size() - 1).getX(),
			// quadratic.getStringWeight(2, 0, 2, 10, 1)
			// + points.get(points.size() - 1).getY());
			// apoint.add(ap);
			// if (w != 0) {
			// verify.appenddata("D:/a/temp/20210702t/actionpoints.txt",
			// String.valueOf(x) + "," + String.valueOf(y) + "\n");
			// }
			if (w > 0) {
				tnum = x;
				verify.appenddata(
						"D:/a/temp/20210702t/actionpoints.txt",
						String.valueOf(x + 1 * pack.interval) + ","
								+ String.valueOf(y) + "\n");
			}
			if (w < 0 && ((x - tnum) / pack.interval) > 7) {
				// if (w < 0) {
				verify.appenddata(
						"D:/a/temp/20210702t/actionpointss.txt",
						String.valueOf(x + 1 * pack.interval) + ","
								+ String.valueOf(y) + "\n");
			}
			verify.savedata("D:/a/temp/20210702t/rawpoints.txt", p, points);
			// p.paintCurve("D:/a/temp/20210702t/rawpoints.txt", 0.05);
			quadratic.paintCurve("D:/a/temp/20210702t/quadratic.txt", 0.01);

			try {
				Framework.getHttp("http://fundgz.1234567.com.cn/js/010963.js?rt=1628759569296");
				if (num > 244 && risklevel > 1
						&& points.get(points.size() - 1).getX() >= 0.90) {
					Process pr = Runtime.getRuntime().exec(
							"python D:/a/canpaintin20210702.py");
					Thread.sleep(1500);

				}
			} catch (InterruptedException | IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static double[] mergesort(double[] a, double[] b) {
		int pa = 0;
		int pb = 0;
		double[] result = new double[a.length + b.length];
		int pr = 0;
		for (pr = 0; pr < result.length; pr++) {
			if (pa < a.length && pb < b.length) {
				if (a[pa] < b[pb]) {
					result[pr] = a[pa];
					++pa;
				} else {
					result[pr] = b[pb];
					++pb;
				}
			} else {
				if (pa < a.length) {
					result[pr] = a[pa];
					++pa;
				} else if (pb < b.length) {
					result[pr] = b[pb];
					++pb;
				}

			}
		}
		return result;
	}

	//
	public static void mainss(String[] args) {
		// TODO Auto-generated method stub
		// Board.datapath = "D:/a/evolution/data.txt";
		// Board.sleeppath = "D:/a/evolution/sleep.txt";
		// Board.evolution(4, 0);
		// formMatrix("D:/a/prefund", "D:/a/evolution/statistic.txt");
		String[] c = { "160632", "鹏华酒A", "PHJA", "1624809600.0", "1.1420",
				"2.4150", "0.88", "1.06", "-2.64", "20.97", "10.23", "92.31",
				"157.81", "216.78", "7.23", "360.47", "1430236800.0",
				"131.2762", "0.12%" };

		if (true) {
			File dir = new File("D:/a/fund");
			boolean ifstart = false;
			File[] files = dir.listFiles();
			for (int i = 2000; i < files.length && i < 5000; i++) {
				String path = files[i].getAbsolutePath();
				if (path.contains("004236")) {
					ifstart = true;
				} else {
					if (!ifstart) {
						continue;
					}
				}
				int needmorerealpointnum = pack.endprediction - 2;
				for (int pointnum = pack.recentdata + needmorerealpointnum;; pointnum += 1) {
					ArrayList<pack> outpoints = loadpoints(path, 0, pointnum);
					if (pointnum > outpoints.size()) {
						break;
					}
					ArrayList<pack> basepoints = new ArrayList<pack>();
					for (int j = 0; j < outpoints.size() - needmorerealpointnum; j++) {
						basepoints.add(outpoints.get(j));
					}
					ArrayList<pack> realnewpoints = new ArrayList<pack>();
					for (int j = outpoints.size() - needmorerealpointnum; j < outpoints
							.size(); j++) {
						realnewpoints.add(outpoints.get(j));
					}
					Fund aFund = new Fund(c, basepoints, 2, 0);
					appenddata(
							"D:/a/prefund/" + files[i].getName(),
							String.valueOf(String.format("%.2f",
									aFund.polynomial_all.evaltopratelast()))
									+ ";"
									+ aFund.risklevel
									+ ";"
									+ aFund.mediumrisklevel
									+ ";"
									+ Polynomial
											.pointswrite(aFund.all_newpoints)
									+ ";"
									+ Polynomial
											.pointswrite(aFund.recent_newpoints)
									+ ";"
									+ Polynomial
											.pointswrite(aFund.quadratic_newpoints)
									+ ";"
									+ Polynomial.pointswrite(realnewpoints)
									+ ";\n");
				}
			}
		}
	}

	// }
	// }
	// }
	// }
	//
	// public static void commentmainfold() {
	//
	// // double[][] b = {{0,0,0,1},{1,1,1,1},{8,4,2,1},{27,9,3,1}};
	// // double[][] L = {{1},{2},{5},{10}};
	// // Matrix B = new Matrix(b);
	// // Matrix l = new Matrix(L);
	// // double[] params;
	// // Matrix A;
	// // Polynomial p = new Polynomial();
	// // A = p.process(points, pack.maxorder);
	// // A = p.robustestimition(B, l).getMatrix();
	//
	// // Polynomial pi = p.d();
	// // pi.x.print(10, 8);
	// // params = pi.findzeropoint();
	// // A = p.process(points,params.length + 1);
	// // A.print(10, 8);
	// // System.out.println(A.getRowDimension() - 1 + "index:" + i);
	// // for (double zero : params) {
	// // System.err.println("zero " + zero);
	// // System.err.println(pi.f(zero, 0));
	// // }
	// // appenddata("D:/a/" + pack.maxorder + ".txt", i + path + " "
	// // + (A.getRowDimension() - 1) + "\n");
	//
	// // savedata("D:/a/polynomial_all.txt", aFund.polynomial_all,
	// // points);
	// // savenewdata("D:/a/all_newpoints.txt", aFund.all_newpoints);
	//
	// /*
	// * savedata("D:/a/polynomial_recent.txt", aFund.polynomial_recent,
	// * aFund.polynomial_recent.rawdata);
	// * savenewdata("D:/a/recent_newpoints.txt", aFund.recent_newpoints);
	// * savedata("D:/a/quadratic.txt", aFund.quadratic,
	// * aFund.quadratic_newpoints);
	// * savenewdata("D:/a/quadratic_newpoints.txt",
	// * aFund.quadratic_newpoints); saveparam("D:/a/pythonparam.txt", path
	// * + ";" + pointnum + ";" + aFund.risklevel + ";" + pack.recentdata);
	// *
	// * try { Thread.sleep(60); Process pr = Runtime.getRuntime().exec(
	// * "python D:/a/testanytime1.py"); // p.analysis(points, true); }
	// * catch (InterruptedException | IOException e) { e.printStackTrace(); }
	// */
	//
	// }
	//
	// public static void formMatrix(String readPath, String writePath) {
	// File dir = new File(readPath);// "D:/a/prefund");
	// File[] files = dir.listFiles();
	// int[][] map = new int[101][81];
	// for (int f = 0; f < files.length && f < 1000; f++) {
	// String path = files[f].getAbsolutePath();
	// try {
	// BufferedReader br = new BufferedReader(new FileReader(path));
	// String aline = null;
	// for (int i = 0; (aline = br.readLine()) != null; i++) {
	// parseprint(aline, map);
	// }
	// br.close();
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }
	// printMatrix(writePath, map);
	// }
	//
	// public static String parseprint(String line, int[][] map) {
	// ArrayList<String> infoteam = Board.cutString(line, ";");
	// double rate = Double.valueOf(infoteam.get(0));
	// double[] all_newpoint = parsedata(infoteam.get(1), ",");
	// int all = delta(all_newpoint);
	// double[] recent_newpoint = parsedata(infoteam.get(2), ",");
	// int recent = delta(recent_newpoint);
	// double[] quadratic_newpoint = parsedata(infoteam.get(3), ",");
	// int quadratic = delta(quadratic_newpoint);
	// double[] real_newpoint = parsedata(infoteam.get(4), ",");
	// int real = delta(real_newpoint);
	// map[(int) (rate * 100)][all * 27 + recent * 9 + quadratic * 3 + real]++;
	// String result = "" + infoteam.get(0) + "," + all + "," + recent + ","
	// + quadratic + "," + real + ",\n";
	// return result;
	// }
	//
	// public static double[] parsedata(String line, String splitor) {
	// ArrayList<String> data = Board.cutString(line, splitor);
	// double[] outdata = new double[data.size()];
	// for (int i = 0; i < data.size(); i++) {
	// outdata[i] = Double.valueOf(data.get(i));
	// }
	// return outdata;
	// }
	//
	// public static int delta(double[] arr) {
	// int result = 0;
	// if (arr[0] > arr[arr.length - 1]) {
	// result = 0;
	// } else if (arr[0] == arr[arr.length - 1]) {
	// result = 1;
	// } else if (arr[0] < arr[arr.length - 1]) {
	// result = 2;
	// }
	// return result;
	// }
	//
	// public static void printMatrix(String path, int[][] map) {
	// String arow = "";
	// for (int i = 0; i < map.length; i++) {
	// for (int j = 0; j < map[i].length; j++) {
	// arow += map[i][j] + ",";
	// }
	// arow += "\n";
	// appenddata(path, arow);
	// arow = "";
	// }
	// }
	//
	public static ArrayList<pack> loadpoints(String path, int start, int end) {
		ArrayList<pack> points = new ArrayList<pack>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(path));
			String aline = null;
			pack firstpoint = null;
			for (int i = 0; i < end && (aline = br.readLine()) != null; i++) {
				if (i == 0) {
					String[] xy = aline.split(",");
					firstpoint = new pack(Double.valueOf(xy[0]),
							Double.valueOf(xy[1]));
				}
				if (i >= start) {
					String[] xy = aline.split(",");
					pack point = new pack(Double.valueOf(xy[0]),
							Double.valueOf(xy[1]));
					point.minus(firstpoint);
					point.id = i;
					points.add(point);
				}
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return points;
	}

	//
	// public static void saveparam(String path, String param) {
	// File pythonparam = new File(path);
	// BufferedWriter python;
	// try {
	// python = new BufferedWriter(new FileWriter(
	// pythonparam.getAbsoluteFile()));
	// python.write(param);
	// python.close();
	// } catch (IOException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }
	//
	// public static void savedata(String path, Polynomial p,
	// ArrayList<pack> points) {
	// try {
	// String content = "";
	// File file = new File(path);
	// if (!file.exists()) {
	// file.createNewFile();
	// }
	// BufferedWriter bw = new BufferedWriter(new FileWriter(
	// file.getAbsoluteFile()));
	// for (pack pack : points) {
	// content = String.valueOf(pack.getX()) + ","
	// + String.valueOf(p.f(pack.getX(), 0)) + "\n";
	// bw.write(content);
	// }
	// bw.close();
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// }
	//
	// public static void savenewdata(String path, ArrayList<pack> points) {
	// try {
	// String content = "";
	// File file = new File(path);
	// if (!file.exists()) {
	// file.createNewFile();
	// }
	// BufferedWriter bw = new BufferedWriter(new FileWriter(
	// file.getAbsoluteFile()));
	// for (pack pack : points) {
	// content = String.valueOf(pack.getX()) + ","
	// + String.valueOf(pack.getY()) + "\n";
	// bw.write(content);
	// }
	// bw.close();
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// }
	//
	public static void appenddata(String path, String content) {
		try {
			File file = new File(path);
			if (!file.exists()) {
				file.createNewFile();
			}
			BufferedWriter bw = new BufferedWriter(new FileWriter(
					file.getAbsoluteFile(), true));
			bw.append(content);
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
