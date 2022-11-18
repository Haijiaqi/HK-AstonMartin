package polyfit;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONObject;

public class Framework {

	public static long todaytimestamp = 0;

	public static String todate = "2020-08-21";
	public static String todaydate = "2020-08-21";

	public static String yesterdate = "2020-08-20";

	public static String lastdate = "2020-08-19";

	public static String newdate = "2020-08-19";

	public static String basepath = new File("").getAbsolutePath(); // "C:/b"; // 

	public static String workpath = basepath + "/work/date/record/list.txt";

	public static String fundDir = basepath + "/fund";

	public static String balanceDir = basepath + "/balance";

	public static int listinfolength = 19;

	public static int safeday = 6;
	public static int startTimes = 0;
	public static int ifback = 0;
	public static String suffix = "";
	public static String stData = basepath + "/fund/seconds";
	public static String ndData = basepath + "/fund/minutes";
	public static String stPtr = "";
	public static int ndPtr = 0;
	public static Long systime = new Long(0);
	
	
	public static String logpath = "";
	
	// public static Logger log = Logger.getLogger("WORK");
	
	public static ArrayList<Investment> tradeList = new ArrayList<Investment>();
	
	public static String certifysuffix (String rawPath, boolean notmore) {
		String[] s = rawPath.split("/");
		String dirPath = "";
		for (int i = 1; i < s.length - 1; i++) {
			dirPath += "/" + s[i];
		}
		File dir = new File(dirPath);
		File[] files = dir.listFiles();
		String name = s[s.length - 1];
		int indexdot = name.indexOf(".");
		String filenamepart = name.substring(0, indexdot);
		String dryname = filenamepart;
		String filename = "";
		String patternName = "";
		String result = "ok";
		String ends = "";
		String preends = "";
		int findj = -1;
		int findi = 0;
		for (int i = 0; i < 50; i++) {
			findj = -1;
			preends = ends;
			ends = (i == 0 ? "" : "-" + i);
			patternName = "";
			patternName += dryname + (i == 0 ? "" : "-" + i);
			for (int j = 0; j < files.length; j++) {
				filename = files[j].getName();
				if (filename.indexOf(patternName) != -1) {
					findj = j;
					break;
				}
			}
			if (findj != -1) {
				continue;
			} else {
				break;
			}
		}
		if (notmore) {
			result = preends;
		} else {
			result = ends;
		}
		return result;
	}
	public static boolean initCoin(String todaystamp) {
		String configPath = Framework.getPath("coin", "paint", "processInfo");
		JSONObject params = verify.loadObject(configPath);
		JSONArray coinsInfo = params.getJSONArray("coins");
		String listPath = Framework.getPath("coin", "paint", "list");
		JSONArray coins = verify.loadArray(listPath);
		int st = params.optInt("st");
		refreshtodaydate();
		if (!"start".equals(todaystamp)) {
			suffix = certifysuffix(Framework.getPath("coin", "paint", "history" + "-" + gettodaydate() + (ifback == 1 ? "-S" : "")), false);
		}
		if ("start".equals(todaystamp)) {
			Scanner in = new Scanner(System.in);
			System.out.println("restore data?[yes]");
			String s = in.nextLine();
			in.close();
			if ("yes".equals(s)) {
				for (int i = 0; i < coins.length(); i++) {
					JSONObject record = coins.getJSONObject(i);
					String aim = record.getString("type");
					JSONObject recordInfo = null;
					for (int j = 0; j < coinsInfo.length(); j++) {
						recordInfo = coinsInfo.getJSONObject(j);
						if (aim.equals(recordInfo.getString("type"))) {
							break;
						}
					}
					recordInfo.put("struct", 0);
					record.put("amount", 40);
					record.put("cost", 0);
					record.put("balance", 0);
					record.put("cash", 0);
					coins.put(i, record);
					System.out.println("restore config data " + aim);
				}
				verify.saveparam(listPath, coins.toString());
				verify.saveparam(configPath, params.toString());
				suffix = certifysuffix(Framework.getPath("coin", "paint", "history" + "-" + gettodaydate() + (ifback == 1 ? "-S" : "")), false);
			} else {
				suffix = certifysuffix(Framework.getPath("coin", "paint", "history" + "-" + gettodaydate() + (ifback == 1 ? "-S" : "")), true);

			}
		}
		for (int i = 0; "start".equals(todaystamp) && i < coins.length(); i++) {
			JSONObject item = (JSONObject)coins.get(i);
			String type = item.getString("type");
			String path = Framework.getPath("seconds", "fund", type);
			ArrayList<String> lines = new ArrayList<>();
			lines = verify.loadlines(path, 119, 120);
		//in.close();
			if (lines.size() == 0
			 || (Double.valueOf(Framework.getNowTimestamp()) - Double.valueOf(lines.get(lines.size() - 1).split(",")[0]) > (st + 1) * 1000)
			 || (Double.valueOf(Framework.getNowTimestamp()) - Double.valueOf(lines.get(lines.size() - 1).split(",")[0]) < 0)) {
				if (ifback == 0) {
					System.out.println("restore seconds data " + type);
					String path1 = "";
					String path2 = "";
					path1 = Framework.getPath("seconds", "fund", type + "_keep");
					path2 = Framework.getPath("seconds", "fund", type + "");
					verify.copylines(path1, path2);
					path1 = Framework.getPath("seconds", "fund", type + "_keep");
					path2 = Framework.getPath("seconds", "fund", type + "_data");
					//verify.copylines(path1, path2);
					path1 = Framework.getPath("minutes", "fund", type + "_keep");
					path2 = Framework.getPath("minutes", "fund", type + "");
					verify.copylines(path1, path2);
					path1 = Framework.getPath("minutes", "fund", type + "_keep");
					path2 = Framework.getPath("minutes", "fund", type + "_data");
					//verify.copylines(path1, path2);
				} else {
					String path2 = "";
					path2 = Framework.getPath("seconds", "fund", type + "");
					verify.saveparam(path2, "");
					path2 = Framework.getPath("minutes", "fund", type + "");
					verify.saveparam(path2, "");
					path2 = Framework.getPath("balance", "balance", type);
					verify.saveparam(path2, "");
				}
			}/* else {
				String path1 = "";
				String path2 = "";
				path1 = Framework.getPath("seconds", "fund", type + "_data");
				path2 = Framework.getPath("seconds", "fund", type + "");
				verify.copylines(path1, path2, 0, 120);
				path1 = Framework.getPath("minutes", "fund", type + "_data");
				path2 = Framework.getPath("minutes", "fund", type + "");
				verify.copylines(path1, path2, 0, 120);
			}*/
		}
		return true;
	}
	public static boolean intime(Long time, int inter) {
		Long delta = Math.abs(time - systime);
		return delta < inter;
	}
	public static boolean intime(String time, int inter) {
		Long timeLong = new Long(time);
		Long delta = Math.abs(timeLong - systime);
		return delta < inter;
	}
	public static boolean init(String todaystamp) {
		File fund = new File(fundDir);
		if (!fund.exists()) {
			fund.mkdirs();
		}
		File balance = new File(balanceDir);
		if (!balance.exists()) {
			balance.mkdirs();
		}
		long today = 0;
		if (todaystamp != null && !"".equals(todaystamp)) {
			today = new Long(todaystamp) - 86400 * 1000;
		} else {
			today = getNowTimestamp() - 86400 * 1000;
		}
		todate = stampToDate(String.valueOf(today));
		todaytimestamp = dateToStamp(todate, "yyyy-MM-dd");
		long yesterday = today - 86400 * 1000;
		yesterdate = stampToDate(String.valueOf(yesterday));
		lastdate = getlastday();
		if (new File(fundDir + "/" + todate).listFiles().length > 3000) {
			newdate = todate;
		} else if (new File(fundDir + "/" + lastdate).listFiles().length > 3000) {
			newdate = lastdate;
		}
		logpath = getPath("today", "out", "log");
		// log = Logger.getLogger("WORK");
		// FileHandler fileHandler = null;
		// try {
		// fileHandler = new FileHandler(ensureFile(getPath("today", "out",
		// "log")), 1000000, 1, true);
		// } catch (SecurityException | IOException e1) {
		// // TODO Auto-generated catch block
		// e1.printStackTrace();
		// }
		// fileHandler.setFormatter(new FileLogFormatter());
		// fileHandler.setLevel(Level.ALL);
		// log.addHandler(fileHandler);
		boolean startornot = new File(getPath("today", "fund", "000000"))
				.exists()
				&& new File(fundDir + "/" + gettodate()).listFiles().length > 3000;
		return startornot;
	}
	public static boolean immediateGo() {
		String url = "http://fundgz.1234567.com.cn/js/" + "000001" + ".js?";
		Framework.waiting(1, "000001" + " is getting from internet!");
		url = Framework.getHttp(url);
		long testTime = Framework.dateToStamp(Framework.getInfoFromJson(url, "gztime", "\""), "yyyy-MM-dd HH:mm");
		boolean startornot = (48800000 - 16400000) > (getNowTimestamp() - testTime);
		return startornot;
	}

	// 初筛
	public static void preliminaryProcess(int paint) {
		try {
			// 获取当天目录列表
			String pathl = getPath("today", "record", "list");
			// 获取当天全列表的指数
			String allpath = getPath("today", "index", "all");
			clearFile(allpath);
			BufferedReader brl = new BufferedReader(new InputStreamReader(
					new FileInputStream(new File(pathl).getAbsolutePath()),
					"GB2312"));
			String lline = null;
			String[] rawinfo = null;
			// 对当天目录列表循环
			for (int i = 0; (lline = brl.readLine()) != null; i++) {
				rawinfo = lline.split(",");
				// 加载当前项目的点数据
				String thispath = getPath("today", "fund", rawinfo[0]);
				if ("008506".equals(rawinfo[0])) {
					System.out.println("008504");
				}
				// 还没数据就循环两下等等
				for (int j = 0; j < 0; j++) {
					if (!new File(thispath).exists()) {
						waiting(1, thispath + " is not exists!");
					} else {
						break;
					}
				}
				ArrayList<pack> outpoints = verify.loadpoints(thispath, 0, 1000, -1);
				// Investment.balanceDir = balanceDir;
				// 没加载到点就下一项
				if (outpoints.size() <= pack.maxorder * 5) {
					continue;
				}
				if (paint > 0) {
					verify.saveparam(basepath + "/pythonparam.txt", thispath + ";");					
				}
				// 计策修改时关注这里以下，确保能够被分配到对应的风险级中
				// 原始数据给予解析
				Fund aFund = new Fund(rawinfo, outpoints, 2, paint);
				// 无增减就不处理
				if (aFund.Erate == 1 || aFund.risklevel > pack.maxorder) {
					continue;
				}
				// 无论如何都记录入总表
				verify.appenddata(allpath
				// replacepath(indexPath, "date", gettodate())
						, aFund.outString + "\n");
				log(i + ", " + aFund.code);
			}
			brl.close();
		} catch (Exception e) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			e.printStackTrace();
			e.printStackTrace(new PrintStream(baos));
			log(baos.toString());
		}
	}
	
	// 中间步骤，梳理保存信息
	public static void middleStep(/* String indexDir, String tradeListPath */) {
		ArrayList<Investment> data = new ArrayList<Investment>();
		ArrayList<Investment> tempfinaldata = new ArrayList<Investment>();
		ArrayList<Investment> finaldata = new ArrayList<Investment>();
		int maxValueIndex = 0;
		double maxValue = 0;
		// 清除之前各风险级的指数结果
		for (int i = 1; i <= pack.maxorder; i++) {
			// 加载各风险级列表为对象列表，并进行排序
			data = sortLevelIndex(/* indexDir, */String.valueOf(i));
			clearFile(getPath("today", "index", String.valueOf(i)));
			for (int j = 1; j <= data.size(); j++) {
				verify.appenddata(getPath("today", "index", String.valueOf(i)), data.get(j).infoString + "\n");
			}
		}
	}

	public static void tailProcess(int paint) {
		// 获取当天全列表的指数
		String allpath = getPath("newday", "index", "all");
		// clearFile(allpath);
		// 获取当天持仓
		String stockpath = getPath("today", "index", "stock");
		clearFile(stockpath);
		// 清除之前各风险级的指数结果
		for (int i = 1; i <= pack.maxorder; i++) {
			clearFile(getPath("today", "index", String.valueOf(i)));
		}
		try {
			if (new File(allpath).exists()) {
				BufferedReader bri = new BufferedReader(new InputStreamReader(
						new FileInputStream(new File(allpath).getAbsolutePath()),
						"GB2312"));
				String iline = null;
				for (int k = 0; (iline = bri.readLine()) != null; k++) {
					try {
						Fund aFund = new Fund(iline, paint);
						// 无增减就不处理
						// if (aFund.Erate == 1) {
						// 	continue;
						// }
						if (aFund.totalshare > 0 && aFund.Erate < 1) {
							// 持有份额且有减倾向的进入持仓待减表
							verify.appenddata(stockpath
							// replacepath(
							// replacepath(indexPath, "date", gettodate()),
							// "all", "stock")
									, aFund.outString + "\n");
						}
						if (aFund.Erate > 1) {
							// 持有份额且有增倾向的进入对应风险级指数的表
							verify.appenddata(
									getPath("today", "index",
											String.valueOf(aFund.risklevel))
									// replacepath(
									// replacepath(indexPath, "date", gettodate()),
									// "all", String.valueOf(aFund.risklevel))
									, aFund.outString + "\n");
						}
						// 无论如何都记录入总表
						// verify.appenddata(allpath, aFund.outString + "\n");
						log(k + ", " + aFund.code);
					} catch (Exception e) {
						ByteArrayOutputStream baos = new ByteArrayOutputStream();
						e.printStackTrace();
						e.printStackTrace(new PrintStream(baos));
						log(baos.toString());
					}
				}
				bri.close();
			}
		} catch (Exception e) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			e.printStackTrace();
			e.printStackTrace(new PrintStream(baos));
			log(baos.toString());
		}
	}
	
	public static void stockMarket() {
		String thispath = getPath("today", "fund", "000000");
		ArrayList<pack> outpoints = verify.loadpoints(thispath, 0, 1000, -1);
		String[] rawinfo = {"000000", "上证指数", "0", "0", "0"};
		Fund aFund = new Fund(rawinfo, outpoints, 1, 0);
		double coefficient = aFund.Erate > 1 ? aFund.Erate : 1;
		Investment.amount = (coefficient - 1) * 20000;
	}

	public static void runhours(JSONArray coins, JSONObject param, int seconds) {
		boolean ifsave = false;
		for (int j = 0; j < coins.length(); j++) {
			JSONObject obj = coins.getJSONObject(j);
			String[] info = { obj.getString("type"), obj.getString("name") };
			ArrayList<pack> points = null;
			points = verify.getData(seconds < 60, info[0], seconds, 120, -1, "hours");	
			if (points == null) {
				System.out.println(info[0] + " loading from net!");
				continue;
			}
			if (!ifsave) {
				ifsave = true;
			}
			JSONObject recordInfo = null;
			JSONArray coinsInfo = param.getJSONArray("coins");
			for (int i = 0; i < coinsInfo.length(); i++) {
				recordInfo = coinsInfo.getJSONObject(i);
				if (info[0].equals(recordInfo.getString("type"))) {
					break;
				}
			}
			double outrate = 0;
			outrate = recordInfo.optDouble("struct");
			double Erate = 0;
			int paint = recordInfo.optInt("paint");
			Fund aFund = new Fund(info, points, paint);
			//aFund.polynomial_all.process(points, aFund.risklevel, 1, 0);
			double toprate = aFund.lastpointtoprate;// polynomial_all.evaltoprate(points.get(points.size() - 1).getY());
			Erate = aFund.getdiscount(1, toprate, pack.discountRate);
			int rd = param.optInt("rd");
			int nd = param.optInt("nd");
			int st = param.optInt("st");
			double topratecut = verify.cutDouble(toprate, 6);
			if (toprate > 0.99) {
				Erate = 2 + 12 / (rd / nd);
				System.out.println("                               TOP!!!: " + topratecut + "\tErate: " + verify.cutDouble(Erate, 6));
			} else {
				if (outrate > 2) {
					Erate = outrate - 1;
					System.out.println("                               NO BUYING!!!: " + topratecut + "\tTIMES: " + (verify.cutDouble(Erate, 6) - 2));
				} else {
					System.out.println("toprate: " + topratecut + "\tErate: " + verify.cutDouble(Erate, 6));
				}
			}
			recordInfo.put("struct", Erate);
		}
		if (ifsave) {
			System.out.println("new co save!");
			String path = Framework.getPath("coin", "paint", "processInfo");
			verify.saveparam(path, param.toString());
		}
	}
	public static void runseconds(JSONArray coins, JSONObject param, int seconds) {
		for (int j = 0; j < coins.length(); j++) {
			JSONObject obj = coins.getJSONObject(j);
			/*ArrayList<pack> structs = Fund.parsePoints(obj.getString("struct"));
			Polynomial p = new Polynomial();
			p.processLS(structs, 2);
			Polynomial start = new Polynomial(2);
			start.assemble(p, 1);
			Polynomial zero = new Polynomial(structs.get(structs.size() - 1).x, 0);
			start.assemble(zero, 1);*/
			//start.display(1000);
			String[] info = { obj.getString("type"), obj.getString("name") };
			JSONObject recordInfo = null;
			JSONArray coinsInfo = param.getJSONArray("coins");
			for (int i = 0; i < coinsInfo.length(); i++) {
				recordInfo = coinsInfo.getJSONObject(i);
				if (info[0].equals(recordInfo.getString("type"))) {
					break;
				}
			}
			double order = recordInfo.optDouble("order");
			double outrate = recordInfo.optDouble("struct");
			double start = recordInfo.optDouble("start");
			outrate = outrate > 2 ? 0 : outrate;
			int extrapolation = recordInfo.optInt("extrapolation");
			double amount = obj.optDouble("amount");
			int paint = obj.optInt("paint");
			String infomation = processAfund(info, seconds, 120, order, extrapolation, outrate, 0, amount > start ? amount : start, "", paint);
			if (!"".equals(infomation)) {
				String newNAVstring = Framework.getInfoFromJson(infomation, "newNAV", ":");
				String trade = Framework.produceTradeItem(infomation);
				executeOnline(trade, newNAVstring);				
			}
		}
	}
	public static void runminutes(JSONArray coins, JSONObject param, int seconds) {
		boolean ifsave = false;
		for (int j = 0; j < coins.length(); j++) {
			JSONObject obj = coins.getJSONObject(j);
			String[] info = { obj.getString("type"), obj.getString("name") };
			ArrayList<pack> points = null;
			points = verify.getData(seconds < 60, info[0], seconds, 120, -1, "minutes");	
			if (points == null) {
				System.out.println(info[0] + " loading from net!");
				continue;
			}
			if (!ifsave) {
				ifsave = true;
			}
			JSONObject recordInfo = null;
			JSONArray coinsInfo = param.getJSONArray("coins");
			for (int i = 0; i < coinsInfo.length(); i++) {
				recordInfo = coinsInfo.getJSONObject(i);
				if (info[0].equals(recordInfo.getString("type"))) {
					break;
				}
			}
			double outrate = 0;
			outrate = recordInfo.optDouble("struct");
			double Erate = 0;
			int paint = recordInfo.optInt("paint");
			Fund aFund = new Fund(info, points, paint);
			//aFund.polynomial_all.process(points, aFund.risklevel, 1, 0);
			double toprate = aFund.lastpointtoprate;// polynomial_all.evaltoprate(points.get(points.size() - 1).getY());
			Erate = aFund.getdiscount(1, toprate, pack.discountRate);
			int rd = param.optInt("rd");
			int nd = param.optInt("nd");
			int st = param.optInt("st");
			double topratecut = verify.cutDouble(toprate, 6);
			if (toprate > 0.99 || toprate < 0.01) {
				if (toprate > 0.99) {
					Erate = 2 + 12;// / (nd / st);
				} else if (toprate < 0.01) {
					//Erate = 2 + 1;// / (nd / st);}
				}
				System.out.println("                               TOP!!!: " + topratecut + "\tErate: " + verify.cutDouble(Erate, 6));
			} else {
				if (outrate > 2) {
					Erate = outrate - 1;
					System.out.println("                               NO BUYING!!!: " + topratecut + "\tTIMES: " + (Erate - 2));
				} else {
					System.out.println("toprate: " + topratecut + "\tErate: " + verify.cutDouble(Erate, 6));
				}
			}
			recordInfo.put("struct", Erate);
		}
		if (ifsave) {
			System.out.println("new co save!");
			String path = Framework.getPath("coin", "paint", "processInfo");
			verify.saveparam(path, param.toString());
		}
	}
	public static String processAfund(String[] type, int seconds, int num, double order, int extrapolation, double f, int gate, double amount, String outpath, int paint) {
		ArrayList<pack> points = null;
		points = verify.getData(seconds < 60, type[0], seconds, num, -1, seconds <= 60 ? "seconds" : "minutes");	
		if (points == null) {
			return "";
		}
		//points = verify.loadpoints(Framework.getPath("coin", "paint", "rawdata"), 0, 150, -1);	.f(points.get(points.size() - 1).val, 0)
		Fund afund = new Fund(type, points, order, paint, extrapolation, f, amount, gate);
		if (!"".equals(outpath)) {
			verify.saveparam(outpath, afund.outString + "\n");
		}
		return afund.outString;
	}
	// 产出指标数
	public static void produceFundIndex(/*
										 * String listPath, String fundDir, //
										 * String balanceDir, String indexPath
										 */) {
		try {
			// 获取当天目录列表
			String pathl = getPath("today", "record", "list");// replacepath(listPath,
																// "date",
																// gettodate());
			// 获取前一天全列表的指数
			String pathi = getPath("lastday", "index", "all");// replacepath(indexPath,
																// "date",
																// getyesterdate());
			// 获取当天全列表的指数
			String allpath = getPath("today", "index", "all");
			clearFile(allpath);
			// 获取当天持仓
			String stockpath = getPath("today", "index", "stock");
			clearFile(stockpath);
			// 清除之前各风险级的指数结果
			for (int i = 1; i <= pack.maxorder; i++) {
				clearFile(getPath("today", "index", String.valueOf(i)));
			}
			BufferedReader brl = new BufferedReader(new InputStreamReader(
					new FileInputStream(new File(pathl).getAbsolutePath()),
					"GB2312"));
			String lline = null;
			BufferedReader bri = new BufferedReader(new InputStreamReader(
					new FileInputStream(
							new File(ensureFile(pathi)).getAbsolutePath()),
					"GB2312"));
			String iline = null;
			String[] rawinfo = null;
			String[] preinfo = null;
			String[] initmissline = { "0" };
			String[] missline = initmissline;
			// 对当天目录列表循环
			int total = 0;
			for (int i = 0; (lline = brl.readLine()) != null; i++) {
				rawinfo = lline.split(",");
				int fundId = Integer.valueOf(safeget(rawinfo, 0));
				if (fundId == Integer.valueOf(safeget(missline, 0))) {
					// 如果匹配就合并信息
					rawinfo = mergeInfo(rawinfo, missline);
					missline = initmissline;
				} else {
					// 不匹配就往下读全列表指数
					for (int j = 0; "0".equals(missline[0])
							&& (iline = bri.readLine()) != null; j++) {
						// 无暂存指数信息就循环
						preinfo = iline.split(",");
						if (fundId == Integer.valueOf(safeget(preinfo, 0))) {
							// 如果匹配就合并信息
							rawinfo = mergeInfo(rawinfo, preinfo);
							missline = initmissline;
							break;
						}
						if ("0".equals(missline[0])
								&& fundId < Integer
										.valueOf(safeget(preinfo, 0))) {
							// 不匹配且还没到就暂存
							missline = preinfo;
							break;
						}
					}
				}
				// 加载当前项目的点数据
				String thispath = getPath("today", "fund", rawinfo[0]);
				// 还没数据就循环两下等等
				for (int j = 0; j < 2; j++) {
					if (!new File(thispath).exists()) {
						waiting(0, thispath + " is not exists!");
						// try {
						// Thread.sleep(1000);
						// } catch (InterruptedException e) {
						// e.printStackTrace();
						// }
					} else {
						break;
					}
				}
				ArrayList<pack> outpoints = verify.loadpoints(thispath// fundDir
																		// +
																		// rawinfo[0]
																		// +
																		// ".txt"
						, 0, 1000, -1);
				// Investment.balanceDir = balanceDir;
				// 没加载到点就下一项
				if (outpoints.size() <= 0) {
					continue;
				}
				// 计策修改时关注这里以下，确保能够被分配到对应的风险级中
				// 原始数据给予解析
				Fund aFund = new Fund(rawinfo, outpoints, 2, 0);
				// 无增减就不处理
				if (aFund.Erate == 1) {
					continue;
				}
				if (aFund.totalshare > 0 && aFund.Erate < 1) {
					// 持有份额且有减倾向的进入持仓待减表
					verify.appenddata(stockpath
					// replacepath(
					// replacepath(indexPath, "date", gettodate()),
					// "all", "stock")
							, aFund.outString + "\n");
				}
				if (aFund.Erate > 1) {
					// 持有份额且有增倾向的进入对应风险级指数的表
					verify.appenddata(
							getPath("today", "index",
									String.valueOf(aFund.risklevel))
							// replacepath(
							// replacepath(indexPath, "date", gettodate()),
							// "all", String.valueOf(aFund.risklevel))
							, aFund.outString + "\n");
				}
				// 无论如何都记录入总表
				verify.appenddata(allpath
				// replacepath(indexPath, "date", gettodate())
						, aFund.outString + "\n");
				log(i + ", " + aFund.code);
			}
			bri.close();
			brl.close();
		} catch (Exception e) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			e.printStackTrace();
			e.printStackTrace(new PrintStream(baos));
			log(baos.toString());
		}
	}

	public static void produceBuyList(/* String indexDir, String tradeListPath */) {
		ArrayList<Investment> data = new ArrayList<Investment>();
		ArrayList<Investment> tempfinaldata = new ArrayList<Investment>();
		ArrayList<Investment> finaldata = new ArrayList<Investment>();
		int maxValueIndex = 0;
		double maxValue = 0;
		for (int i = 2; i < pack.maxorder; i++) {
			// 加载各风险级列表为对象列表，并进行排序
			data = sortLevelIndex(/* indexDir, */String.valueOf(i));
			if (data.size() > 0) {
				// 找到占比最大的
				// if (maxValue < data.get(0).inrates) {
				// 	maxValue = data.get(0).inrates;
				// 	maxValueIndex = tempfinaldata.size();
				// }
				// 只取各风险级为首的项目加入最终处理，风险级最高的在前面
				if (i < 7) {
					finaldata.add(data.get(0));
				}
				if (i < 7) {
					// if (data.get(1).inrates < 100) {
						// tempfinaldata.add(data.get(1));
					// }
				}
			}
		}
		//重装载，把最大的放最后，便于放弃
		// for (int i = 0; i < tempfinaldata.size(); i++) {
		// 	if (i != maxValueIndex) {
		// 		finaldata.add(tempfinaldata.get(i));
		// 	}
		// }
		// finaldata.add(tempfinaldata.get(maxValueIndex));
		// finaldata = sortIndexdesc(tempfinaldata);
		finaldata.addAll(tempfinaldata);
		if (finaldata.size() > 0) {
			// 对待处理集进行额度分配
			finaldata = distribute(finaldata);
		}
		// for (int i = 1; i <= pack.maxorder; i++) {
		// data = sortLevelIndex(/* indexDir, */String.valueOf(i));
		// if (data.size() > 0) {
		// data = distribute(data);
		// finaldata.addAll(data);
		// }
		// }
		// 打印为极简打印
		for (int i = 0; i < finaldata.size(); i++) {
			verify.appenddata(getPath("today", "trade", "tradelist")
			// replacepath(tradeListPath, "date", gettodate())
					, finaldata.get(i).printtodolist((i + 1) + "") + "\n");
		}
	}

	public static void produceTradeItem(/* String indexDir, String tradeListPath */) {
		ArrayList<Investment> data = new ArrayList<Investment>();
		ArrayList<Investment> tempfinaldata = new ArrayList<Investment>();
		ArrayList<Investment> finaldata = new ArrayList<Investment>();
		int maxValueIndex = 0;
		double maxValue = 0;
		BufferedReader bri;
		ArrayList<Investment> datas = new ArrayList<Investment>();
		// ArrayList<String[]> data = new ArrayList<String[]>();
		String[] aindex;
		Investment aInvestment;
		String path = Framework.basepath + "/processInfo.txt";
		try {
			if (new File(path).exists()) {
				bri = new BufferedReader(new InputStreamReader(
						new FileInputStream(new File(path).getAbsolutePath()),
						"GB2312"));
				String iline = null;
				for (int k = 0; (iline = bri.readLine()) != null; k++) {
					aindex = iline.split(",");
					aInvestment = new Investment();
					aInvestment.infoString = iline;
					// aInvestment.fund = aindex[0] + "," + aindex[1];
					aInvestment.fund = Framework.getInfoFromJson(iline, "code", ":") + " " + Framework.getInfoFromJson(iline, "name", ":");
					// aInvestment.share = Double.valueOf(safeget(aindex, 2));
					aInvestment.stockshare = Double.valueOf(Framework.getInfoFromJson(iline, "totalshare", ":"));
					aInvestment.share = Double.valueOf(Framework.getInfoFromJson(iline, "realshare", ":"));
					// aInvestment.inrates = Double.valueOf(safeget(aindex, 6));
					aInvestment.inrates = Double.valueOf(Framework.getInfoFromJson(iline, "Erate", ":")) - 1;
					// data.add(aindex);
					if (aInvestment.inrates < 0) {
					// 导出的风险指数小于1且总额大于1
						if (aInvestment.inrates > -1) {
							/*aInvestment.cost = aInvestment.stockshare * aInvestment.inrates * -1;
							aInvestment.cost = aInvestment.cost - (aInvestment.stockshare - aInvestment.share);
							if (aInvestment.cost < 0) {
								aInvestment.cost = 0.000000001;
							}
							aInvestment.cost *= -1;*/
							aInvestment.cost = aInvestment.inrates;
						}
					} else if (aInvestment.inrates > 0) {
						if (aInvestment.inrates > 0.99) {
							System.out.println("");
						}
						aInvestment.cost = Investment.amount * aInvestment.inrates;
						aInvestment.cost = verify.cutDouble(aInvestment.cost, 2);
					}
					verify.saveparam(basepath + "/tradeList.txt", "");
					verify.appenddata(basepath + "/tradeList.txt"
					// replacepath(tradeListPath, "date", gettodate())
							, aInvestment.printtodolist("0") + "\n");
				}
				bri.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			e.printStackTrace();
			e.printStackTrace(new PrintStream(baos));
			log(baos.toString());
		}
	}
	public static String produceTradeItem(String iline) {
		Investment aInvestment;
		aInvestment = new Investment();
		aInvestment.infoString = iline;
		aInvestment.fund = Framework.getInfoFromJson(iline, "code", ":") + "," + Framework.getInfoFromJson(iline, "name", ":");
		aInvestment.stockshare = Double.valueOf(Framework.getInfoFromJson(iline, "totalshare", ":"));
		aInvestment.share = Double.valueOf(Framework.getInfoFromJson(iline, "realshare", ":"));
		aInvestment.inrates = Double.valueOf(Framework.getInfoFromJson(iline, "Erate", ":")) - 1;
		aInvestment.cash = Double.valueOf(Framework.getInfoFromJson(iline, "amount", ":"));
		if (aInvestment.inrates < 0) {
		// 导出的风险指数小于1且总额大于1
			if (aInvestment.inrates > -1) {
				/*aInvestment.cost = aInvestment.stockshare * aInvestment.inrates * -1;
				aInvestment.cost = aInvestment.cost - (aInvestment.stockshare - aInvestment.share);
				if (aInvestment.cost < 0) {
					aInvestment.cost = 0.000000001;
				}
				aInvestment.cost *= -1;*/
				aInvestment.cost = aInvestment.inrates;
			}
		} else if (aInvestment.inrates > 0) {
			if (aInvestment.inrates > 0.99) {
				System.out.println("");
			}
			aInvestment.cost = aInvestment.cash * aInvestment.inrates;
			aInvestment.cost = verify.cutDouble(aInvestment.cost, 2);
		}
		return aInvestment.printtodolist(Framework.getNowTimestamp() + "");
	}

	public static void executeOnline(String line, String price) {
		Investment item = new Investment(line);
		Investment.trade(item, price, "0.1%", 0, true);
	}

	public static ArrayList<Investment> distribute(ArrayList<Investment> data) {
		double totalrate = 0;
		// ArrayList<Investment> buyList = data;
		// ArrayList<Investment> tempList = new ArrayList<Investment>();
		// 求左右指标总合，用于后续按权分配
		for (int i = 0; i < data.size(); i++) {
			totalrate += data.get(i).inrates;
		}
		// 未舍弃列表的末端
		int maxIndex = data.size();
		// 当前分配的最小额。首先赋予上限值。Investment.amount有意义，是基础（最大）投入额
		double minamount = Investment.amount;
		for (int i = 0; i < data.size(); i++) {
			// 在未舍弃列表中每一项按权求取分配值
			for (int j = 0; j < maxIndex; j++) {
				data.get(j).cost = Investment.amount * data.get(j).inrates
						/ totalrate;
				data.get(j).cost = verify.cutDouble(data.get(j).cost, 2);
				if (minamount > data.get(j).cost) {
					// 找到当前分配的最小额，遇到小的就记录下来
					minamount = data.get(j).cost;
				}
			}
			if (minamount < 10) {
				// 发现最小额小于10
				minamount = Investment.amount;
				// 就需要从末端开始舍弃参与分配的项
				maxIndex--;
				// 消除刚才为末端分配的额度
				data.get(maxIndex).cost = 0;
				// 重新计算总额
				totalrate = 0;
				for (int j = 0; j < maxIndex; j++) {
					totalrate += data.get(j).inrates;
				}
				continue;
			}
			break;
		}
		return data;
	}

	public static ArrayList<Investment> distribute1(ArrayList<Investment> data) {
		double totalrate = 0;
		ArrayList<Investment> buyList = data;
		ArrayList<Investment> tempList = new ArrayList<Investment>();
		for (int i = 0; i < buyList.size(); i++) {
			totalrate += buyList.get(i).inrates;
		}
		for (int j = 0; buyList.size() > 0; j++) {
			double minamount = Investment.amount;
			for (int i = 0; i < buyList.size(); i++) {
				double amount = Investment.amount * buyList.get(i).inrates
						/ totalrate;
				if (minamount > amount) {
					minamount = amount;
				}
			}
			if (minamount < 10) {
				tempList = new ArrayList<Investment>();
				totalrate = 0;
				for (int i = 0; i < buyList.size() - 1; i++) {
					tempList.add(buyList.get(i));
					totalrate += buyList.get(i).inrates;
				}
				buyList = tempList;
			} else {
				for (int i = 0; i < buyList.size(); i++) {
					buyList.get(i).cost = Investment.amount
							* buyList.get(i).inrates / totalrate;
				}
				break;
			}
		}
		return buyList;
	}

	public static void produceSellList(/* String indexDir, String tradeListPath */) {
		ArrayList<Investment> data = new ArrayList<Investment>();
		// 加载持仓待减列表为对象列表，并进行排序
		data = sortLevelIndex(/* indexDir, */"stock");
		// 对持仓待减对象列表进行预处理，主要使卖出计划值合理（不要小于最小操作）
		data = prepare(data);
		// 打印为极简打印
		for (int i = 0; i < data.size(); i++) {
			verify.appenddata(getPath("today", "trade", "tradelist")
			// replacepath(tradeListPath, "date", gettodate())
					, data.get(i).printtodolist((i + 1) + "") + "\n");
		}
	}

	public static ArrayList<Investment> prepare(ArrayList<Investment> data) {
		ArrayList<Investment> sellList = new ArrayList<Investment>();
		double minOutShare = 1;
		// ArrayList<Investment> tempList = new ArrayList<Investment>();
		for (int i = 0; i < data.size(); i++) {
			if (data.get(i).inrates < 1 && data.get(i).share > minOutShare) {
				// 导出的风险指数小于1且总额大于1
				data.get(i).cost = data.get(i).stockshare * data.get(i).inrates;
				data.get(i).cost = data.get(i).cost - (data.get(i).stockshare - data.get(i).share);
				// 按风险额计算待减额
				if (data.get(i).cost < minOutShare) {
					// 过于小的，按最小卖出值看待
					data.get(i).cost = minOutShare;
				}
			} else {
				// 风险额大于1且总额小于1的就按最小卖出值全部卖出
				data.get(i).cost = data.get(i).share;
			}
			// 提取非冻结（安全）天数之前的总额
			double freeshare = Investment.gettotalshare(
					data.get(i).fund.split(",")[0], new Long("0"),
					Framework.getTodayTimestamp() - safeday
							* new Long("86400000"));
			// 自由总额比计划卖出额小的，且风险指数小于0.9（？）的，卖出全部自由额
			if (freeshare < data.get(i).cost) {// && data.get(i).inrates < 0.9) {
				data.get(i).cost = freeshare;
			}
			// 记录时，卖出的符号取负
			data.get(i).cost *= -1;
			sellList.add(data.get(i));
		}
		return sellList;
	}

	// 产出买卖计划清单
	public static void produceTradeList() {
		// 到交易列表
		String path = getPath("today", "trade", "tradelist");
		clearFile(path);
		// 先处理卖出再处理买入
		produceSellList();
		produceBuyList();
	}

	public static void executesettle(/* String listPath, String tradeListPath */) {
		String tradepath = gettradepath();
		tradeList.clear();
		if (!"".equals(tradepath)) {
			try {
				BufferedReader bt = new BufferedReader(
						new FileReader(tradepath));
				String line = null;
				Investment item;
				for (int i = 0; (line = bt.readLine()) != null; i++) {
					item = new Investment(line);
					tradeList.add(item);
				}
				bt.close();
				String path = getPath("today", "record", "list");// replacepath(listPath,
																	// "date",
																	// gettodate());
				BufferedReader br = new BufferedReader(new InputStreamReader(
						new FileInputStream(new File(path).getAbsolutePath()),
						"GB2312"));
				String aline = null;
				String[] meta;
				for (int i = 0; (aline = br.readLine()) != null; i++) {
					meta = parseMetaInfo(aline);
					ArrayList<Investment> trades = getfundtrade(meta[0]);
					for (int j = 0; j < trades.size(); j++) {
						Investment.trade(trades.get(j), meta[4],
								meta[listinfolength - 1], 1, false);
					}
				}
				br.close();
			} catch (Exception e) {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				e.printStackTrace();
				e.printStackTrace(new PrintStream(baos));
				log(baos.toString());
			}
		}
	}
	public static void execute(String price) {
		String tradepath = basepath + "/tradeList.txt";
		tradeList.clear();
		if (!"".equals(tradepath)) {
			try {
				BufferedReader bt = new BufferedReader(
						new FileReader(tradepath));
				String line = null;
				Investment item;
				for (int i = 0; (line = bt.readLine()) != null; i++) {
					item = new Investment(line);
					tradeList.add(item);
				}
				bt.close();
				ArrayList<Investment> trades = getfundtrade("BTC");
				for (int j = 0; j < trades.size(); j++) {
					Investment.trade(trades.get(j), price,
							"0.1%", 0, false);
				}
			} catch (Exception e) {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				e.printStackTrace();
				e.printStackTrace(new PrintStream(baos));
				log(baos.toString());
			}
		}
	}

	public static ArrayList<Investment> sortLevelIndex(/* String indexDir, */
	String name) {
		BufferedReader bri;
		ArrayList<Investment> datas = new ArrayList<Investment>();
		// ArrayList<String[]> data = new ArrayList<String[]>();
		String[] aindex;
		Investment aInvestment;
		String path = getPath("today", "index", name);// replacepath(replacepath(indexDir,
														// "date", gettodate()),
														// "all", name);
		try {
			if (new File(path).exists()) {
				bri = new BufferedReader(new InputStreamReader(
						new FileInputStream(new File(path).getAbsolutePath()),
						"GB2312"));
				String iline = null;
				for (int k = 0; (iline = bri.readLine()) != null; k++) {
					aindex = iline.split(",");
					aInvestment = new Investment();
					aInvestment.infoString = iline;
					// aInvestment.fund = aindex[0] + "," + aindex[1];
					aInvestment.fund = Framework.getInfoFromJson(iline, "code", ":") + "," + Framework.getInfoFromJson(iline, "name", ":");
					// aInvestment.share = Double.valueOf(safeget(aindex, 2));
					aInvestment.stockshare = Double.valueOf(Framework.getInfoFromJson(iline, "totalshare", ":"));
					aInvestment.share = Double.valueOf(Framework.getInfoFromJson(iline, "realshare", ":"));
					// aInvestment.inrates = Double.valueOf(safeget(aindex, 6));
					aInvestment.inrates = Double.valueOf(Framework.getInfoFromJson(iline, "conclusion", ":"));
					// data.add(aindex);
					datas.add(aInvestment);
				}
				bri.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			e.printStackTrace();
			e.printStackTrace(new PrintStream(baos));
			log(baos.toString());
		}
		return datas = sortIndexdesc(datas);
	}

	public static ArrayList<Investment> sortIndexdesc(ArrayList<Investment> data) {
		Collections.sort(data, new Comparator<Object>() {
			@Override
			public int compare(Object o1, Object o2) {
				Investment l1 = (Investment) o1;
				Investment l2 = (Investment) o2;
				double r1 = l1.inrates;
				double r2 = l2.inrates;
				// double e1 = l1.NAV;
				// double e2 = l2.NAV;
				// double n1 = 0.925 * r1 + 0.075 * e1;
				// double n2 = 0.925 * r2 + 0.075 * e2;
				if (r1 > r2) {
					return -1;
				} else if (r1 == r2) {
					return 0;
				} else {
					return 1;
				}
			}
		});
		return data;
	}

	public static void freshNAV(/* String listPath, String tradeListPath */) {
		try {
			String listPath = getPath("today", "record", "list");
			BufferedReader br = new BufferedReader(new InputStreamReader(
					new FileInputStream(new File(listPath).getAbsolutePath()),
					"GB2312"));
			String aline = null;
			String[] meta;
			for (int i = 0; (aline = br.readLine()) != null; i++) {
				meta = parseMetaInfo(aline);
				Investment.refresh(meta[0], Double.valueOf(safeget(meta, 4)));
			}
			br.close();
		} catch (Exception e) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			e.printStackTrace();
			e.printStackTrace(new PrintStream(baos));
			log(baos.toString());
		}
	}

	public static Investment statistic(/* String balanceBasePath */) {
		File dir = new File(balanceDir);
		File[] files = dir.listFiles();
		Investment result = new Investment();
		for (int i = 0; i < files.length; i++) {
			String path = files[i].getAbsolutePath();
			result.add(Investment.calculate(path));
		}
		return result;
	}

	public static void sendMessage() {
		try {
			Process pr = Runtime.getRuntime().exec(
					"python D:/Aproject/sendmessage.py");
			System.err.println("has called python to send message!");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			e.printStackTrace();
			e.printStackTrace(new PrintStream(baos));
			log(baos.toString());
		}
	}

	public static String gettradepath() {
		File verifiedtrade = new File(getPath("lastday", "trade", "tradelist "
				+ gettodate()));
		File rawtrade = new File(getPath("lastday", "trade", "tradelist"));
		if (verifiedtrade.exists()) {
			return verifiedtrade.getAbsolutePath();
		} else if (rawtrade.exists()) {
			return rawtrade.getAbsolutePath();
		} else {
			return "";
		}
	}

	public static String getlastday() {
		String current = "";
		String before = "";
		File dir = new File(fundDir);
		File[] files = dir.listFiles();
		if (files != null && files.length > 0) {
			String path0 = files[0].getAbsolutePath() + "/" + "000000.txt";
			if (new File(path0).exists()) {
				before = files[0].getName();
				for (int i = 1; i < files.length; i++) {
					current = files[i].getName();
					String path = files[i].getAbsolutePath() + "/"
							+ "000000.txt";
					if (gettodate().equals(current)) {
						break;
					} else if (new File(path).exists()
							&& files[i].listFiles().length > 3000) {
						before = current;
					}
				}
			}
		}
		if ("".equals(before)) {
			before = getyesterdate();
		}
		return before;
	}

	public static String getlastday1() {
		String result = "";
		File dir = new File(fundDir);
		File[] files = dir.listFiles();
		if (files != null) {
			for (int i = files.length - 2; i >= 0; i--) {
				String path = files[i].getAbsolutePath() + "/" + "000000.txt";
				result = files[i].getName();
				if (new File(path).exists()) {
					return result;
				}
			}
		}
		if ("".equals(result)) {
			result = getPath("today", "index", "all");
			try {
				File file = new File(result);
				if (!file.exists()) {
					file.createNewFile();
				}
				return gettodate();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				e.printStackTrace();
				e.printStackTrace(new PrintStream(baos));
				log(baos.toString());
			}
		}
		return result;
	}

	public static ArrayList<Investment> getfundtrade(String code) {
		ArrayList<Investment> result = new ArrayList<Investment>();
		for (int i = 0; i < tradeList.size(); i++) {
			if (code.equals(tradeList.get(i).fund)) {
				result.add(tradeList.get(i));
			} else {
				continue;
			}
		}
		return result;
	}

	public static String safeget(String[] infomation, int index) {
		if (infomation.length > index) {
			if (!"".equals(infomation[index])) {
				return infomation[index];
			} else {
				return "0";
			}
		} else {
			return "0";
		}
	}

	public static String[] mergeInfo(String[] list, String[] yesterIndex) {
		String[] result = new String[list.length + yesterIndex.length];
		int i = 0;
		for (; i < list.length; i++) {
			result[i] = list[i];
		}
		for (; i < result.length; i++) {
			result[i] = yesterIndex[i - list.length];
		}
		return result;
	}

	// public static String replacepath(String path, String ori, String now) {
	// String result = "";
	// result = path.replace(ori, now);
	// return result;
	// }

	// public static String linetowrite(String[] info) {
	// String result = "";
	// for (int i = 0; i < info.length; i++) {
	// result += info[i] + ",";
	// }
	// return result;
	// }

	public static boolean clock(int hour, int minute, boolean ifprint) {
		for (int j = 0; true; j++) {
			String nowTime = Framework.stampToDate(
					Framework.getNowTimestamp(), "HH:mm");
			String[] HHmm = nowTime.split(":");
			if (hour == Integer.valueOf(HHmm[0])
					&& minute == Integer.valueOf(HHmm[1])) {
				break;
			} else {
				if (ifprint) {
					Framework.waiting(590, nowTime + ", waiting...");
				}
				// try {
				// System.out.println(nowTime + ", waiting...");
				// Thread.sleep(59 * 1000);
				// } catch (InterruptedException e) {
				// e.printStackTrace();
				// }
			}
		}
		return true;
	}

	public static String getHttp(String url) {
		String result = "{}";
		try {
			URL realUrl;
			realUrl = new URL(url);
			HttpURLConnection connection = (HttpURLConnection) realUrl.openConnection();
			connection.setRequestProperty("accept", "*/*");
			connection.setRequestProperty("connection", "Keep-Alive");
			connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			// 建立实际的连接
			connection.connect();
			//请求成功
			if (connection.getResponseCode() == 200) {
				InputStream is = connection.getInputStream();
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				//10MB的缓存
				byte[] buffer = new byte[1024];
				int len = 0;
				while ((len = is.read(buffer)) != -1) {
					baos.write(buffer, 0, len);
				}
				result = baos.toString();
				baos.close();
				is.close();
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		return result;
    }

	public static String getInfoFromJson(String json, String fieldname, String split) {
		String result = "";
		String[] info = json.split(",");
		String[] infoInner = {};
		for (int i = 0; i < info.length; i++) {
			if (info[i].indexOf(fieldname) != -1) {
				infoInner = info[i].split(split);
			}
		}
		if (infoInner.length > 1) {
			if (infoInner[infoInner.length - 1].indexOf("}") != -1) {
				result = infoInner[infoInner.length - 2];
			} else {
				result = infoInner[infoInner.length - 1];
			}
		}
		return result;
	}

	public static String getFieldPart(String fieldname, String value) {
		return fieldname + ":" + value + ",";
	}

	public static String getFieldPart(String fieldname, Double value) {
		return fieldname + ":" + value + ",";
	}

	public static String getFieldPart(String fieldname, int value) {
		return fieldname + ":" + value + ",";
	}

	public static String[] parseMetaInfo(String aline) {
		String[] result = aline.split(",");
		return result;
	}

	public static long getNowTimestamp() {
		if (ifback == 0) {
			return System.currentTimeMillis();
		} else {
			return systime;
		}
	}

	public static long timeToGo(int seconds) {
		long inter = seconds * 1000;
		for (int i = 0; true; i++) {
			Long time = Framework.getNowTimestamp();
			if ((time - new Long(1667577600) * 1000) % inter < 23) {
				return time;
			} else {
				try {
					Thread.sleep(23);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	public static boolean ifNewTime(int seconds) {
		long inter = seconds * 1000;
		Long time = Framework.getNowTimestamp();
		if ((time - new Long(1667577600) * 1000) % inter < 1000) {
			return true;
		} else {
			return false;
		}
	}

	public static long getTodayTimestamp() {
		return todaytimestamp;
	}

	public static String gettodate() {
		return todate;
	}
	public static String gettodaydate() {
		return todaydate;
	}
	public static String refreshtodaydate() {
		Long today = getNowTimestamp();// - 86400 * 1000;
		todaydate = stampToDate(String.valueOf(today));
		return todaydate;
	}

	public static String getyesterdate() {
		return yesterdate;
	}

	public static String getlastdate() {
		return lastdate;
	}

	public static String getnewdate() {
		return newdate;
	}

	public static String getPath(String date, String type, String name) {
		if ("balance".equals(type)) {
			return balanceDir + "/" + name + (Framework.ifback == 1 ? "-S" : "") + ".txt";
		}
		switch (date) {
		case "today":
			date = gettodate();
			break;
		case "yesterday":
			date = getyesterdate();
			break;
		case "lastday":
			date = getlastdate();
			break;
		case "newday":
			date = getnewdate();
			break;
		default:
			break;
		}
		if ("fund".equals(type)) {
			return fundDir + "/" + date + "/" + name + ".txt";// .replace("000001",
			// name);
		}
		switch (type) {
		case "trade":
			return workpath.replace("date", date).replace("record", type)
					.replace("list", "tradelist");
		case "index":
			return workpath.replace("date", date).replace("record", type)
					.replace("list", name);
		case "out":
			return workpath.replace("date", date).replace("record", type)
					.replace("list", "log");
		case "record":
			return workpath.replace("date", date);
		default:
			break;
		}
		if ("".equals(name)) {
			return workpath.replace("date", date).replace("record", type)
		.replace("/list.txt", name);
		} else {
			return workpath.replace("date", date).replace("record", type)
					.replace("list", name);	
		}
	}

	public static void clearFile(String path) {
		try {
			File file = new File(path);
			if (file.exists()) {
				BufferedWriter bw = new BufferedWriter(new FileWriter(
						file.getAbsoluteFile()));
				bw.close();
			}
		} catch (IOException e) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			e.printStackTrace();
			e.printStackTrace(new PrintStream(baos));
			log(baos.toString());
		}
	}

	public static long dateToStamp(String today, String Format) {
		String res;
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Format);
		Date date = null;
		if (!"".equals(today)) {
			try {
				date = simpleDateFormat.parse(today);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				e.printStackTrace();
				e.printStackTrace(new PrintStream(baos));
				log(baos.toString());
			}
			long ts = date.getTime();
			res = String.valueOf(ts);
			return ts;
		} else {
			return 0;
		}
	}

	public static String stampToDate(String s) {
		String res;
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		long lt = new Long(s);
		Date date = new Date(lt);
		res = simpleDateFormat.format(date);
		return res;
	}

	public static String stampToDate(long s, String fomat) {
		String res;
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(fomat);
		// long lt = new Long(s);
		Date date = new Date(s);
		res = simpleDateFormat.format(date);
		return res;
	}

	public static String log(String info) {
		String time = stampToDate(getNowTimestamp(), "yyyy-MM-dd HH:mm:ss")
				+ " ";
		info = time + info;
		System.out.println(info);
		return verify.appenddata(logpath, info + "\n");
	}

	public static String ensureFile(String path) {
		File dir = new File(path);
		dir = new File(dir.getParent());
		if (!dir.exists()) {
			dir.mkdirs();
		}
		File file = new File(path);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				e.printStackTrace();
				e.printStackTrace(new PrintStream(baos));
				log(baos.toString());
				return "";
			}
		}
		return path;
	}

	public static int waiting(int seconds, String info) {
		try {
			System.out.println(info);
			Thread.sleep(seconds * 100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return seconds;
	}

	public static void sellIndex(String[] balance, String fundDir,
			String indexDir, ArrayList<String[]> stock) {
		String fundIndex = "";
		ArrayList<pack> outpoints = verify.loadpoints(fundDir + balance[1]
				+ ".txt", 0, 1000, -1);
		for (int i = 0; i < outpoints.size(); i++) {
			if (Integer.valueOf(safeget(balance, 0)) == outpoints.get(i).id) {
				fundIndex += outpoints.get(i).getY() + ",";
				fundIndex += outpoints.get(outpoints.size() - 1).getY() + ",";
				break;
			}
		}
		for (int i = 0; i < stock.size(); i++) {
			if (Integer.valueOf(safeget(balance, 0)) == Integer
					.valueOf(safeget(stock.get(i), 1))) {
				fundIndex += stock.get(i);
				break;
			}
		}
		verify.appenddata(getPath("today", "index", "sell")
		// replacepath(replacepath(indexDir, "date", gettodate()), "all",
		// "sell")
				, fundIndex + "\n");
	}
}