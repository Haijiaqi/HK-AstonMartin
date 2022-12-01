package polyfit;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import entity.Board;

public class verify {

	public static int o = 0;

	public static void verify2() {
		String[] info = { "270008", "0", "0", "1.23", "1.23", "0.05%" , "0.05%" , "0.05%" , "0.05%" , "0.05%" , "0.05%" , "0.05%" , "0.05%" , "0.05%" , "0.05%" , "0.05%" , "0.05%" , "0.05%" , "0.05%", "0", "0", "0", "0", "0.05%" };
		Fund aFund = new Fund(info, loadpoints(
				"D:/Aproject/test/270008.txt", 0, 1000, -1)
				,2, 0);
	}

	public static void verify1() {
		// TODO Auto-generated method stub
		// Board.datapath = "D:/a/evolution/data.txt";
		// Board.sleeppath = "D:/a/evolution/sleep.txt";
		// Board.evolution(4, 0);
		// formMatrix("D:/a/prefund", "D:/a/evolution/statistic.txt");
		if (true) {
			File dir = new File("D:/a/fund");
			boolean ifstart = false;
			File[] files = dir.listFiles();
			for (int i = 3000; i < files.length && i < 5000; i++) {
				String path = files[i].getAbsolutePath();
				if (path.contains("006221")) {
					ifstart = true;
				} else {
					if (!ifstart) {
						continue;
					}
				}
				int needmorerealpointnum = pack.endprediction - 2;
				for (int pointnum = pack.recentdata + needmorerealpointnum;; pointnum += 1) {
					ArrayList<pack> outpoints = loadpoints(path, 0, pointnum, -1);
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
					Fund aFund = new Fund(null, basepoints,2, 0);
					appenddata(
							"D:/a/prefund/" + files[i].getName(),
							String.valueOf(String.format("%.2f",
									aFund.polynomial_all.evaltopratelast()))
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
									+ ";"
									+ aFund.risklevel
									+ ";"
									+ aFund.mediumrisklevel + ";\n");
				}
			}
		}
	}

	public static void commentmainfold() {

		// double[][] b = {{0,0,0,1},{1,1,1,1},{8,4,2,1},{27,9,3,1}};
		// double[][] L = {{1},{2},{5},{10}};
		// Matrix B = new Matrix(b);
		// Matrix l = new Matrix(L);
		// double[] params;
		// Matrix A;
		// Polynomial p = new Polynomial();
		// A = p.process(points, pack.maxorder);
		// A = p.robustestimition(B, l).getMatrix();

		// Polynomial pi = p.d();
		// pi.x.print(10, 8);
		// params = pi.findzeropoint();
		// A = p.process(points,params.length + 1);
		// A.print(10, 8);
		// System.out.println(A.getRowDimension() - 1 + "index:" + i);
		// for (double zero : params) {
		// System.err.println("zero " + zero);
		// System.err.println(pi.f(zero, 0));
		// }
		// appenddata("D:/a/" + pack.maxorder + ".txt", i + path + " "
		// + (A.getRowDimension() - 1) + "\n");

		// savedata("D:/a/polynomial_all.txt", aFund.polynomial_all,
		// points);
		// savenewdata("D:/a/all_newpoints.txt", aFund.all_newpoints);

		/*
		 * savedata("D:/a/polynomial_recent.txt", aFund.polynomial_recent,
		 * aFund.polynomial_recent.rawdata);
		 * savenewdata("D:/a/recent_newpoints.txt", aFund.recent_newpoints);
		 * savedata("D:/a/quadratic.txt", aFund.quadratic,
		 * aFund.quadratic_newpoints);
		 * savenewdata("D:/a/quadratic_newpoints.txt",
		 * aFund.quadratic_newpoints); saveparam("D:/a/pythonparam.txt", path
		 * + ";" + pointnum + ";" + aFund.risklevel + ";" + pack.recentdata);
		 * 
		 * try { Thread.sleep(60); Process pr = Runtime.getRuntime().exec(
		 * "python D:/a/testanytime1.py"); // p.analysis(points, true); }
		 * catch (InterruptedException | IOException e) { e.printStackTrace(); }
		 */

	}

	public static void formMatrix(String readPath, String writePath) {
		File dir = new File(readPath);// "D:/a/prefund");
		File[] files = dir.listFiles();
		int[][] map = new int[101][81];
		for (int f = 0; f < files.length && f < 1000; f++) {
			String path = files[f].getAbsolutePath();
			try {
				BufferedReader br = new BufferedReader(new FileReader(path));
				String aline = null;
				for (int i = 0; (aline = br.readLine()) != null; i++) {
					parseprint(aline, map);
				}
				br.close();
				Thread.sleep(5);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		printMatrix(writePath, map);
	}

	public static String parseprint(String line, int[][] map) {
		ArrayList<String> infoteam = Board.cutString(line, ";");
		double rate = Double.valueOf(infoteam.get(0));
		double[] all_newpoint = parsedata(infoteam.get(1), ",");
		int all = delta(all_newpoint);
		double[] recent_newpoint = parsedata(infoteam.get(2), ",");
		int recent = delta(recent_newpoint);
		double[] quadratic_newpoint = parsedata(infoteam.get(3), ",");
		int quadratic = delta(quadratic_newpoint);
		double[] real_newpoint = parsedata(infoteam.get(4), ",");
		int real = delta(real_newpoint);
		map[(int) (rate * 100)][all * 27 + recent * 9 + quadratic * 3 + real]++;
		String result = "" + infoteam.get(0) + "," + all + "," + recent + ","
				+ quadratic + "," + real + ",\n";
		return result;
	}

	public static double[] parsedata(String line, String splitor) {
		ArrayList<String> data = Board.cutString(line, splitor);
		double[] outdata = new double[data.size()];
		for (int i = 0; i < data.size(); i++) {
			outdata[i] = Double.valueOf(data.get(i));
		}
		return outdata;
	}

	public static int delta(double[] arr) {
		int result = 0;
		if (arr[0] > arr[arr.length - 1]) {
			result = 0;
		} else if (arr[0] == arr[arr.length - 1]) {
			result = 1;
		} else if (arr[0] < arr[arr.length - 1]) {
			result = 2;
		}
		return result;
	}

	public static void printMatrix(String path, int[][] map) {
		String arow = "";
		for (int i = 0; i < map.length; i++) {
			for (int j = 0; j < map[i].length; j++) {
				arow += map[i][j] + ",";
			}
			arow += "\n";
			appenddata(path, arow);
			arow = "";
		}
	}

	public static ArrayList<pack> loadpoints(String path, int start, int end, int type) {
		ArrayList<pack> points = new ArrayList<pack>();
		try {
			File pointfile = new File(path);
			if (!pointfile.exists()) {
				return points;
			}
			BufferedReader bt = new BufferedReader(new FileReader(path));
			String alinet = null;
			pack pointt = null;
			ArrayList<pack> pointst = new ArrayList<pack>();
			for (int i = 0; i < 5 && (alinet = bt.readLine()) != null; i++) {
				if (i == 0) {
					String[] xy = alinet.split(",");
					pointt = new pack(Double.valueOf(xy[0]), 999999999);
				}
				if (i > 0) {
					String[] xy = alinet.split(",");
					pointt = new pack(Double.valueOf(xy[0]), Double.valueOf(xy[0]) - pointst.get(i - 1).getX());
				}
				pointst.add(pointt);
			}
			double min = 999999999;
			for (int i = 0; i < pointst.size(); i++) {
				if (min > pointst.get(i).getY()) {
					min = pointst.get(i).getY();
				}
			}
			int divide = (int)(min * 100 + 0.000001);
			bt.close();
			Thread.sleep(5);
			BufferedReader br = new BufferedReader(new FileReader(path));
			String aline = null;
			pack firstpoint = null;
			for (int i = 0; i < end && (aline = br.readLine()) != null; i++) {
				if (i == start) {
					String[] xy = aline.split(",");
					firstpoint = new pack(Double.valueOf(xy[0]),
							"None".equals(xy[1]) ? 0 : Double.valueOf(xy[1]));
				}
				if (i >= start) {
					String[] xy = aline.split(",");
					pack point = new pack(Double.valueOf(xy[0]),
							Double.valueOf("None".equals(xy[1]) ? 0 : Double.valueOf(xy[1])));
					point.minus(firstpoint, divide, type);
					points.add(point);
				}
			}
			br.close();
			Thread.sleep(5);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return points;
	}
	public static ArrayList<String> loadlines(String path, int start, int end) {
		ArrayList<String> points = new ArrayList<>();
		try {
			File pointfile = new File(path);
			if (!pointfile.exists()) {
				return points;
			}
			BufferedReader br = new BufferedReader(new FileReader(path));
			String aline = null;
			String preline = null;
			int record = (int)Math.abs(start);
			if (start >= 0) {				
				for (int i = 0; i < end && (aline = br.readLine()) != null; i++) {
					if (i >= start) {
						points.add(aline);
					}
				}
			} else {			
				String[] lastarray = new String[record];
				for (int i = 0; (aline = br.readLine()) != null; i++) {
					for (int j = 0; j < record; j++) {
						lastarray[j] = lastarray[j + 1];
					}
					lastarray[record - 1] = aline;
				}
				points.add(lastarray[0]);
			}
			br.close();
			Thread.sleep(5);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return points;
	}
	public static String loadline(String path, Long key) {
		String result = "";
		String preline = "";
		String pretime = "0";
		try {
			File pointfile = new File(path);
			if (!pointfile.exists()) {
				return "";
			}
			BufferedReader br = new BufferedReader(new FileReader(path));
			String aline = "";
			for (int i = 0; preline != null; i++) {
				aline = br.readLine();
				String time = aline == null ? "" : aline.split(",")[0];
				int preflag = intime(key, pretime, 2250);
				if (preflag > 0) {
					break;
				}
				int nowflag = intime(key, time, 2250);
				if (nowflag != 0 && preflag == 0) {
					result = preline;
					break;
				}
				pretime = time;
				preline = aline;
			}
			br.close();
			Thread.sleep(5);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (preline != null) {
			return result;			
		} else {
			return null;
		}
	}
	public static int intime(Long a, String time, int inter) {
		double timeLong = "".equals(time) ? 0 : Double.valueOf(time);
		double realdelta = timeLong - a;
		double delta = Math.abs(realdelta);
		if (delta < inter) {
			return 0;
		} else if (realdelta < 0) {
			return -1;
		} else {
			return 1;
		}
	}
	public static ArrayList<pack> loadpoints(ArrayList<String> path, int start, int end, int type) {
		ArrayList<pack> points = new ArrayList<pack>();
		try {
			String alinet = null;
			pack pointt = null;
			ArrayList<pack> pointst = new ArrayList<pack>();
			int first = path.size();
			double avg = 0;
			int divide = 100000;
			int teststart = 0;
			if (first >= 3) {
				for (int i = 0; i < path.size() - 3; i++) {
					String[] xy1 = path.get(i).split(",");
					String[] xy2 = path.get(i + 1).split(",");
					String[] xy3 = path.get(i + 2).split(",");
					double x1 = Double.valueOf(xy1[0]);
					double x2 = Double.valueOf(xy2[0]);
					double x3 = Double.valueOf(xy3[0]);
					if (x1 != x2 && x1 != x3 && x2 != x3) {
						teststart += i;
						break;
					}
				}
				first = (first > teststart + 5 ? (teststart + 5) : first);
				for (int i = teststart, j = 0; i < first; i++, j++) {
					alinet = path.get(i);
					if (i == teststart) {
						String[] xy = alinet.split(",");
						pointt = new pack(Double.valueOf(xy[0]), 999999999);
					}
					if (i > teststart) {
						String[] xy = alinet.split(",");
						pointt = new pack(Double.valueOf(xy[0]), Double.valueOf(xy[0]) - pointst.get(j - 1).getX());
					}
					pointst.add(pointt);
				}
				for (int i = 1; i < pointst.size(); i++) {
					//if (min > pointst.get(i).getY()) {
						avg += pointst.get(i).getY();
					//}
				}
				avg /= (pointst.size() - 1);
			}
			divide = (int)(avg * 100 + 0.000001);
			if (divide != 0) {
				String aline = null;
				pack firstpoint = null;
				for (int i = 0; i < end; i++) {
					aline = path.get(i);
					if (i == start) {
						String[] xy = aline.split(",");
						firstpoint = new pack(Double.valueOf(xy[0]),
								"None".equals(xy[1]) ? 0 : Double.valueOf(xy[1]));
					}
					if (i >= start) {
						String[] xy = aline.split(",");
						pack point = new pack(Double.valueOf(xy[0]),
								Double.valueOf("None".equals(xy[1]) ? 0 : Double.valueOf(xy[1])));
						point.minus(firstpoint, divide, type);
						points.add(point);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return points;
	}
	public static JSONObject getInfoFromNet(String url) {
		pack result = new pack();
		// String url = "http://fundgz.1234567.com.cn/js/" + this.code + ".js?rt=" + Framework.getNowTimestamp();
		url = Framework.getHttp(url);
		JSONObject json = new JSONObject(url);
		return json;
	}
	public static JSONArray getArrayFromNet(String url) {
		pack result = new pack();
		// String url = "http://fundgz.1234567.com.cn/js/" + this.code + ".js?rt=" + Framework.getNowTimestamp();
		url = Framework.getHttp(url);
		JSONArray json = new JSONArray(url);
		return json;
	}
	public static ArrayList<pack> getPointsFromArray(JSONArray arr, int linenum) {
		ArrayList<pack> points = new ArrayList<pack>();
		int start = arr.length() - linenum;
		if (start < 0) {
			start = 0;
		}
		pack firstpoint = null;
		for (int i = start; i < arr.length(); i++) {
			JSONArray pointArray = (JSONArray)arr.get(i);
			if (i == start) {
				firstpoint = new pack(pointArray);
			}
			if (i >= start) {
				pack point = new pack(pointArray);
				point.minus(firstpoint, 1 * 1000 * 100, -1);
				points.add(point);
			}
		}
		return points;
	}
	public static String netInter(int seconds) {
		String result = "";
		if (seconds < 60) {
			result = seconds + "s";
		} else if (seconds / 60 < 20) {
			result = seconds / 60 + "m";
		} else {
			result = seconds / 3600 + "H";
		}
		return result;
	}
	public static int keeplines(String path, int linenum, String line) {
		try {
			File pointfile = new File(path);
			if (!pointfile.exists()) {
				verify.saveparam(path, "");
			}
			BufferedReader bt = new BufferedReader(new FileReader(path));
			String alinet = null;
			pack pointt = null;
			ArrayList<String> lines = new ArrayList<String>();
			for (int i = 0; (alinet = bt.readLine()) != null; i++) {
				lines.add(alinet);
			}
			bt.close();
			Thread.sleep(5);
			int length = lines.size();
			int start = length - linenum + 1;
			if (start < 0) {
				start = 0;
			}
			BufferedWriter bw = new BufferedWriter(new FileWriter(
					path));
			for (int j = start; j < lines.size(); j++) {
				bw.write(lines.get(j) + "\n");
			}
			bw.write(line);
			bw.close();
			Thread.sleep(5);
			return lines.size() + 1;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
	public static int copylines(String path1, String path2) {
		try {
			verify.saveparam(path2, "");
			BufferedReader bt = new BufferedReader(new FileReader(path1));
			String alinet = null;
			ArrayList<String> lines = new ArrayList<String>();
			for (int i = 0; (alinet = bt.readLine()) != null; i++) {
				lines.add(alinet);
			}
			bt.close();
			Thread.sleep(5);
			BufferedWriter bw = new BufferedWriter(new FileWriter(
					path2));
			for (int j = 0; j < lines.size(); j++) {
				bw.write(lines.get(j) + "\n");
			}
			bw.close();
			Thread.sleep(5);
			return lines.size();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
	public static int copylines(String path1, String path2, int start, int end) {
		try {
			verify.saveparam(path2, "");
			BufferedReader bt = new BufferedReader(new FileReader(path1));
			String alinet = null;
			ArrayList<String> lines = new ArrayList<String>();
			for (int i = 0; (alinet = bt.readLine()) != null; i++) {
				if (i >= start) {
					lines.add(alinet);					
				}
				if (i >= end) {
					break;
				}
			}
			bt.close();
			Thread.sleep(5);
			BufferedWriter bw = new BufferedWriter(new FileWriter(
					path2));
			for (int j = 0; j < lines.size(); j++) {
				bw.write(lines.get(j) + "\n");
			}
			bw.close();
			Thread.sleep(5);
			return lines.size();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
	public static ArrayList<pack> keeplinesIn(ArrayList<pack> pointst, int linenum, JSONObject json) {
		ArrayList<pack> newpointst = new ArrayList<pack>();
		int length = pointst.size();
		int start = length - linenum + 1;
		if (start < 0) {
			start = 0;
		}
		for (int j = start; j < pointst.size(); j++) {
			newpointst.add(pointst.get(j));
		}
		newpointst.add(new pack(json));
		return newpointst;
	}
	public static ArrayList<pack> getData(boolean addORall, String type, int seconds, int num, int datatype, String temppath) {
		ArrayList<pack> points = new ArrayList<pack>();
		String instId = "instId=" + type;
		String url = "";
		JSONObject json = null;
		JSONArray jsonArray = null;
		double channel = seconds / 60.0;
		if ((int)channel < channel || seconds < 60) {
			String path = Framework.getPath(temppath, "fund", type);
			ArrayList<String> lines = new ArrayList<>();
			lines = loadlines(path, 1, num + 1);
			String line = "";
			String value = "";
			String x = "";
			if (Framework.ifback == 0) {
				url = "https://www.okx.com/api/v5/market/index-tickers?instId=BTC-USD".replace("instId=BTC-USD", instId);
				//url = ("https://www.binance.com/api/v3/klines?symbol=BTCUSDT&interval=1s&limit=120").replace("BTCUSDT", type.replace("-", ""));
				try {
					json = getInfoFromNet(url);
					jsonArray = json.getJSONArray("data");
					json = (JSONObject)jsonArray.get(0);
					value = json.getString("idxPx");
					line = json.getString("ts") + "," + value;
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
					value = "";
				}
			} else {
				line = verify.loadline(Framework.getPath(temppath, "fund", type + "_data"), Framework.systime);
				if (line == null || "".equals(line)) {//.indexOf(",") != -1) {
					value = line;					
				} else {//.indexOf(",") != -1) {
					value = line.split(",")[1];					
				}
			}
			int linenum = lines.size();
			if (value != null) {
				if ("".equals(value) || linenum != 0 && line.equals(lines.get(lines.size() - 1))) {
					System.out.println("add a bad line!!! " + line);
					String[] xy = lines.get(lines.size() - 1).split(",");
					line = (new Long(xy[0]) + seconds * 1000) + "," + ("".equals(value) ? xy[1] : value);
				}
			} else {
				Framework.systime = new Long(0);
				return null;
			}
			if (Framework.ifback == 0) {
				verify.appenddata(Framework.getPath(temppath, "fund", type + "_data"), line + "\n");				
			}
			if (linenum < num - 1) {
				verify.appenddata(path, line + "\n");
				return null;
			}
			lines.add(line);
			points = loadpoints(lines, 0, num, datatype);
			if (points == null || points.size() == 0) {
				return null;
			}
			keeplines(path, num, line + "\n");// points.size();// 
			/*jsonArray = getArrayFromNet(url);
			points = getPointsFromArray(jsonArray, num);*/
			//points = keeplinesIn(points, num, json);	
			//points = verify.loadpoints(path, 0, num, datatype);
		} else {
			int end = num % 100;
			int loop = num / 100 + end == 0 ? 0 : 1;
			long before = 0;
			long after = 0;
			String bar = "bar=" + netInter(seconds);
			url = "https://www.okx.com/api/v5/market/index-candles?instId=BTC-USD&bar=15m".replace("instId=BTC-USD", instId).replace("bar=15m", bar);
			
			json = getInfoFromNet(url);
			jsonArray = json.getJSONArray("data");
			url = url.replace("index-candles", "history-index-candles");
			String urlHistory = url;
			for (int i = 1; i <= loop; i++) {
				long top = new Long((i + 1) * seconds);
				long tail = new Long((i) * seconds);
				Long time = Framework.getNowTimestamp();
				before = time - 101 * top * 1000;
				after = time - 100 * tail * 1000;
				url = urlHistory + "&before=" + before + "&after=" + after;
				json = getInfoFromNet(url);
				jsonArray.putAll(json.getJSONArray("data"));
			}
			pack firstpoint = null;
			for (int i = num - 1; i >= 0; i--) {
				JSONArray pointArray = (JSONArray)jsonArray.get(i);
				if (i == (num - 1)) {
					firstpoint = new pack(pointArray);
				}
				if (i <= (num - 1)) {
					pack point = new pack(pointArray);
					point.minus(firstpoint, seconds * 1000 * 100, datatype);
					points.add(point);
				}
			}
		}
		if (points.get(points.size() - 1).getY() == 0) {
			System.out.println("may all zero!!!");
		}
		return points;
	}
	public static ArrayList<pack> loadpointsend(String path, int start, int end, int type) {
		ArrayList<pack> points = new ArrayList<pack>();
		try {
			File pointfile = new File(path);
			if (!pointfile.exists()) {
				return points;
			}
			BufferedReader bt = new BufferedReader(new FileReader(path));
			String alinet = null;
			pack pointt = null;
			ArrayList<pack> pointst = new ArrayList<pack>();
			for (int i = 0; i < 5 && (alinet = bt.readLine()) != null; i++) {
				if (i == 0) {
					String[] xy = alinet.split(",");
					pointt = new pack(Double.valueOf(xy[0]), 999999999);
				}
				if (i > 0) {
					String[] xy = alinet.split(",");
					pointt = new pack(Double.valueOf(xy[0]), Double.valueOf(xy[0]) - pointst.get(i - 1).getX());
				}
				pointst.add(pointt);
			}
			double min = 999999999;
			for (int i = 0; i < pointst.size(); i++) {
				if (min > pointst.get(i).getY()) {
					min = pointst.get(i).getY();
				}
			}
			int divide = (int)min * 100;
			bt.close();
			Thread.sleep(5);
			BufferedReader br = new BufferedReader(new FileReader(path));
			String aline = null;
			pack firstpoint = null;
			for (int i = 0; i < end && (aline = br.readLine()) != null; i++) {
				if (i == start) {
					String[] xy = aline.split(",");
					firstpoint = new pack(Double.valueOf(xy[0]),
							"None".equals(xy[1]) ? 0 : Double.valueOf(xy[1]));
				}
				if (i >= start) {
					String[] xy = aline.split(",");
					pack point = new pack(Double.valueOf(xy[0]),
							Double.valueOf("None".equals(xy[1]) ? 0 : Double.valueOf(xy[1])));
					point.minus(firstpoint, divide, type);
					points.add(point);
				}
			}
			br.close();
			Thread.sleep(5);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return points;
	}
	public static ArrayList<pack> loadpointsraw(String path, int start, int end) {
		ArrayList<pack> points = new ArrayList<pack>();
		try {
			File pointfile = new File(path);
			if (!pointfile.exists()) {
				return points;
			}
			BufferedReader br = new BufferedReader(new FileReader(path));
			String aline = null;
			for (int i = 0; i < end && (aline = br.readLine()) != null; i++) {
				String[] xy = aline.split(",");
				pack point = new pack(Double.valueOf(xy[0]),
						Double.valueOf(xy[1]));
				points.add(point);
			}
			br.close();
			Thread.sleep(5);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return points;
	}
	public static String loadparam(String path, String field) {
		String result = "";
		try {
			File pointfile = new File(path);
			if (!pointfile.exists()) {
				return "";
			}
			BufferedReader br = new BufferedReader(new FileReader(path));
			String aline = null;
			for (int i = 0; (aline = br.readLine()) != null; i++) {
				result += aline;
			}
			br.close();
			Thread.sleep(5);
			JSONObject jsonObject = new JSONObject(result);
			result = jsonObject.getString(field);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	public static String loadparam(String path) {
		String result = "";
		try {
			File pointfile = new File(path);
			if (!pointfile.exists()) {
				return "";
			}
			BufferedReader br = new BufferedReader(new FileReader(path));
			String aline = null;
			for (int i = 0; (aline = br.readLine()) != null; i++) {
				result += aline;
			}
			br.close();
			Thread.sleep(5);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	public static JSONObject loadObject(String path) {
		String result = "";
		JSONObject jsonObject = null;
		try {
			File pointfile = new File(path);
			if (!pointfile.exists()) {
				return jsonObject;
			}
			BufferedReader br = new BufferedReader(new FileReader(path));
			String aline = null;
			for (int i = 0; (aline = br.readLine()) != null; i++) {
				result += aline;
			}
			br.close();
			Thread.sleep(5);
			jsonObject = new JSONObject(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return jsonObject;
	}
	public static JSONArray loadArray(String path) {
		String result = "";
		JSONArray jsonObject = null;
		try {
			File pointfile = new File(path);
			if (!pointfile.exists()) {
				return jsonObject;
			}
			BufferedReader br = new BufferedReader(new FileReader(path));
			String aline = null;
			for (int i = 0; (aline = br.readLine()) != null; i++) {
				result += aline;
			}
			br.close();
			Thread.sleep(5);
			jsonObject = new JSONArray(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return jsonObject;
	}

	public static String saveparam(String path, String param) {
		File pythonparam = new File(path);
		BufferedWriter python;
		try {
			python = new BufferedWriter(new FileWriter(
					pythonparam.getAbsoluteFile()));
			python.write(param);
			python.close();
			Thread.sleep(5);
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return path;
	}

	public static void savedata(String path, Polynomial p,
			ArrayList<pack> points) {
		try {
			String content = "";
			File file = new File(path);
			if (!file.exists()) {
				file.createNewFile();
			}
			BufferedWriter bw = new BufferedWriter(new FileWriter(
					file.getAbsoluteFile()));
			for (pack pack : points) {
				content = String.valueOf(pack.getX()) + ","
						+ String.valueOf(p.f(pack.getX(), 0)) + "\n";
				bw.write(content);
			}
			bw.close();
			Thread.sleep(5);
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static void savenewdata(String path, ArrayList<pack> points) {
		try {
			String content = "";
			File file = new File(path);
			if (!file.exists()) {
				file.createNewFile();
			}
			BufferedWriter bw = new BufferedWriter(new FileWriter(
					file.getAbsoluteFile()));
			for (pack pack : points) {
				content = String.valueOf(pack.getX()) + ","
						+ String.valueOf(pack.getY()) + "\n";
				bw.write(content);
			}
			bw.close();
			Thread.sleep(5);
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static String appenddata(String path, String content) {
		try {
			File file = new File(Framework.ensureFile(path));
			BufferedWriter bw = new BufferedWriter(new FileWriter(
					file.getAbsoluteFile(), true));
			bw.append(content);
			bw.close();
			Thread.sleep(5);
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
		return content;
	}
	public static double cutDouble(double d, int tailBit) {
		if (tailBit == 2) {
			return Math.round(d * 100) / 100.0;
		} else if (tailBit == 3) {
			return Math.round(d * 1000) / 1000.0;
		} else {
			return Math.round(d * 10000) / 10000.0;
		}
	}
}