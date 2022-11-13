package polyfit;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Function;

import org.json.JSONArray;
import org.json.JSONObject;

public class Fund {
	String code = "";
	String name = "";
	String outString = "";
	int risklevel = pack.maxorder;
	int mediumrisklevel = risklevel;
	double lastpointtoprate = 1;
	Polynomial polynomial_all = new Polynomial();
	ArrayList<pack> all_newpoints;
	Polynomial polynomial_recent = new Polynomial();
	ArrayList<pack> recent_newpoints;
	Polynomial quadratic = new Polynomial();
	ArrayList<pack> quadratic_newpoints;
	double immediaterisk = 0;
	int predictBefore = 0;
	double Erate = 1;
	double totalshare = 0;
	double realshare = 0;
	ArrayList<pack> quadraticpoints;
	// double longEY = 0;
	// double mediumEY = 0;
	// double shortEY = 0;
	String[] information = {};
	double[] zero = {};
	double[] extreme = {};
	double[] inflection = {};
	double[] keypoints = {};
	double tailstart = 0;
	pack lastpoint;

	String rateString = "";
	double reliability = 1;
	double conclusion = 1;
	String newNAVstring = "";
	double fee = 0;
	int gate = 0;
	ArrayList<pack> points;
	ArrayList<Investment> investments;

	double tailstartcubic = 0;
	ArrayList<pack> cubicpoints = new ArrayList<pack>();
	ArrayList<pack> quadraticubicpoints = new ArrayList<pack>();
	ArrayList<pack> evalpoints = new ArrayList<pack>();
	ArrayList<pack> peakpoints = new ArrayList<pack>();

	// public Fund(String[] information, ArrayList<pack> points) {
	// 	givedata(information, points);
	// }
	double order = 2;
	int paint = 0;
	int extrapolations = 0;
	double amount = 20000;
	public Fund() {

	}
	public Fund(String[] information, ArrayList<pack> points, double order, int paint) {
		this.points = points;
		this.information = information;
		this.points = points;
		this.order = order;
		this.paint = paint;
		code = Framework.safeget(information, 0);
		name = Framework.safeget(information, 1);
		String number = Framework.safeget(information,
				Framework.listinfolength - 1);
		if (number.indexOf("%") == -1) {
			number = "0";
		} else {
			number = number.substring(0, number.indexOf("%"));
		}
		newNAVstring = Framework.safeget(information, 4);
		double newNAV = Double.valueOf(newNAVstring);
		fee = Double.valueOf(number) / 100;
		if (points.size() == 244 || points.size() == 245) {
			risklevel = pack.maxorder;
		} else {
			risklevel = (int)((points.size() / 245.0) * pack.maxorder);
		}
		risklevel = risklevel > pack.maxorder ? pack.maxorder : risklevel;
		macroscopic();
		microcosmic();
		if (paint > 0) {
			try {
				Process pr = Runtime.getRuntime().exec("python " + Framework.basepath + "/canpaintin20210702.py");
				Thread.sleep(paint);
			} catch (InterruptedException | IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public Fund(String[] information, ArrayList<pack> points, int paint) {
		this.points = points;
		this.information = information;
		this.points = points;
		this.paint = paint;
		code = Framework.safeget(information, 0);
		name = Framework.safeget(information, 1);
		macroscopic();
		extractindex(false);
		if (paint > 0) {
			try {
				String pythonconfig = Framework.getPath("coin", "paint", "pythonparam");
				String rawdata = Framework.getPath("coin", "paint", "rawdata");
				verify.appenddata(pythonconfig, " $" + newNAVstring + ";" + rawdata);
				verify.savenewdata(rawdata, points);
				String command = "python3 " + Framework.basepath + "/canpaintin20210702.py";
				System.out.println(command);
				Process pr = Runtime.getRuntime().exec(command);
				Thread.sleep(paint);
			} catch (InterruptedException | IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public Fund(String[] information, ArrayList<pack> points, double order, int paint, int extrapolations, double recommand, double amount, int gate) {
		this.information = information;
		this.gate = gate;
		code = Framework.safeget(information, 0);
		name = Framework.safeget(information, 1);
		this.immediaterisk = (recommand > 0 ? recommand : 0);
		this.points = points;
		this.order = order;
		this.paint = paint;
		this.extrapolations = extrapolations;
		this.amount = amount;
		this.newNAVstring = points.get(points.size() - 1).val + "";
		macroscopic();
		microcosmic();
		if (paint > 0) {
			try {
				String pythonconfig = Framework.getPath("coin", "paint", "pythonparam");
				String rawdata = Framework.getPath("coin", "paint", "rawdata");
				verify.appenddata(pythonconfig, " $" + newNAVstring + ";" + rawdata);
				verify.savenewdata(rawdata, points);
				// .basepath + "/rawdata.txt", points);
				String command = "python3 " + Framework.basepath + "/canpaintin20210702.py";
				System.out.println(command);
				Process pr = Runtime.getRuntime().exec(command);
				Thread.sleep(paint);
			} catch (InterruptedException | IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public Fund(String info, int paint) {
		code = Framework.getInfoFromJson(info, "code", ":");
		name = Framework.getInfoFromJson(info, "name", ":");
		totalshare = Double.valueOf(Framework.getInfoFromJson(info, "totalshare", ":"));
		Erate = Double.valueOf(Framework.getInfoFromJson(info, "Erate", ":"));
		reliability = Double.valueOf(Framework.getInfoFromJson(info, "reliability", ":"));
		conclusion = Double.valueOf(Framework.getInfoFromJson(info, "conclusion", ":"));
		newNAVstring = Framework.getInfoFromJson(info, "newNAV", ":");
		fee = Double.valueOf(Framework.getInfoFromJson(info, "fee", ":"));
		risklevel = Integer.valueOf(Framework.getInfoFromJson(info, "risklevel", ":"));
		mediumrisklevel = Integer.valueOf(Framework.getInfoFromJson(info, "mediumrisklevel", ":"));
		lastpointtoprate = Double.valueOf(Framework.getInfoFromJson(info, "lastpointtoprate", ":"));
		tailstart = Double.valueOf(Framework.getInfoFromJson(info, "tailstart", ":"));
		polynomial_all.rawdata = parsePoints(Framework.getInfoFromJson(info, "tailpoints", ":"));
		points = polynomial_all.rawdata;
		quadraticpoints = points;
		order = 2;
		this.paint = paint;
		extrapolations = 0;
			// + Framework.getFieldPart("lastpoint", "@" + points.get(points.size() - 1).getX() + "|" + points.get(points.size() - 1).getY())
			// + Framework.getFieldPart("tailpoints", organizePoints(quadraticpoints));
		// ArrayList<pack> outpoints = verify.loadpoints(path, 0, 1000);
		// risklevel = polynomial_all.process(points, risklevel).getRowDimension() - 1;
		extractindex(true);
	}

	// 计策修改时关注这里
	public void givedata(String[] information, ArrayList<pack> points) {
		this.points = points;
		code = information[0];
		// 加载当前项目在余额表中的总份额
		if ("011000".equals(code)) {
			System.out.println("code");
		}
		investments = Investment.loads(code);
		totalshare = gettotalshare(1);
		// risklevel = polynomial_all.process(points,
		// risklevel).getRowDimension() - 1;
		risklevel = polynomial_all.processLS(points, risklevel)
				.getRowDimension() - 1;
		if (points.size() >= pack.recentdata) {
			risklevel = (int) (polynomial_all.worseAnalysis(points, produceRiskByName()).r / 0.1) + 1;
			mediumrisklevel = risklevel;
			lastpointtoprate = polynomial_all.evaltoprate(points.get(
					points.size() - 1).getY());
			// rulemining(points);
			// calcnewpoints(1, pack.endprediction);
			extractindex(information);
			// quadratic_newpoints.addAll(quadraticpoints);
		}
	}

	// 计策修改时关注这里
	public void macroscopic() {
		// 加载当前项目在余额表中的总份额
		// investments = Investment.loads(code);
		// totalshare = gettotalshare();
		// risklevel = polynomial_all.process(points,
		// risklevel).getRowDimension() - 1;
		risklevel = polynomial_all.process(points, risklevel, 1, extrapolations)
		.getRowDimension() - 1;
		risklevel = (int) (polynomial_all.worseAnalysis(points, produceRiskByName()).r / 0.1) + 1;
		if (paint > 0) {
			polynomial_all.paintCurve(Framework.getPath("coin", "paint", "polynomial"), pack.interval);
		}
		Polynomial polynomial_all1;
		polynomial_all1 = polynomial_all.d(1);
		extreme = polynomial_all1.findzeropoint();
		Polynomial polynomial_all2;
		polynomial_all2 = polynomial_all1.d(1);
		inflection = polynomial_all2.findzeropoint();
		keypoints = mergesort(extreme, inflection);
		// double[] startp = { polynomial_all.start };
		// keypoints = mergesort(startp, keypoints);
		double halflocalinterreal = Escale(keypoints, 0) / pack.interval;
		int halflocalinter = (int)Math.round(halflocalinterreal);
		double localinter = (int)Math.round(halflocalinterreal * 2);

		tailstartcubic = findTailStart(keypoints, polynomial_all, (int)localinter); // 17);
		cubicpoints = polynomial_all.pickdata(tailstartcubic);
		Polynomial polynomial_recent1;
		polynomial_recent.process(cubicpoints, 3, 1, extrapolations); // 3, 1);
		if (paint > 0) {
			polynomial_recent.paintCurve(Framework.getPath("coin", "paint", "cubic"), pack.interval);
		}
		polynomial_recent1 = polynomial_recent.d(1);
		double[] extremecubic = {};
		extremecubic = polynomial_recent1.findzeropoint();
		Polynomial polynomial_recent11;
		double[] inflectioncubic = {};
		double[] keypointscubic = {};
		double tailstartcubicquadratic = 0; // 17);
		// for (int i = points.size() - pack.recentdata; i < points.size(); i++) {
		// 	recentpoints.add(points.get(i));
		// }
		// Polynomial polynomial_recent1;
		// polynomial_recent.process(recentpoints, 3, 0);
		// polynomial_recent1 = polynomial_recent.d(1);
		// extreme = polynomial_recent1.findzeropoint();

		// risklevel *= (extremecubic.length < 1 ? 1 : extremecubic.length);
		mediumrisklevel = risklevel;
		lastpointtoprate = polynomial_all.evaltoprate(points.get(
				points.size() - 1).getY());

		// Polynomial polynomial_recent2;
		// polynomial_recent2 = polynomial_all.d(1);
		// extreme = polynomial_recent2.findzeropoint();
		// polynomial_recent2 = polynomial_recent2.d(1);
		// inflection = polynomial_recent2.findzeropoint();
		// keypoints = mergesort(extreme, inflection);
		// for (int i = keypoints.length - 1; true; i--) {
		// 	if (i >= 0) {
		// 		tailstart = keypoints[i];
		// 		quadraticpoints = polynomial_all.pickdata(tailstart);
		// 		if (quadraticpoints.size() > 7) {
		// 			break;
		// 		} else {
		// 			continue;
		// 		}
		// 	} else {
		// 		tailstart = polynomial_all.start;
		// 		quadraticpoints = polynomial_all.pickdata(tailstart);
		// 		break;
		// 	}
		// }
		ArrayList<pack> valuepoints = new ArrayList<pack>();
		if (polynomial_recent.x.get(polynomial_recent.x.size() - 1).getRowDimension() - 1 >= 2 && extremecubic.length > 0) {
			if (extremecubic.length > 1) {
				polynomial_recent11 = polynomial_recent1.d(1);
				inflectioncubic = polynomial_recent11.findzeropoint();
				keypointscubic = mergesort(extremecubic, inflectioncubic);
				tailstartcubicquadratic = findTailStart(keypointscubic, polynomial_recent, (int)halflocalinter); // 17);
			} else {
				tailstartcubicquadratic = extremecubic[0];
			}
			quadraticubicpoints = polynomial_all.pickdata(tailstartcubicquadratic);// - 0.04);
			if (quadraticubicpoints.size() > halflocalinter) {
				tailstart = tailstartcubicquadratic;
				quadraticpoints = quadraticubicpoints;
				valuepoints = quadraticubicpoints;
				// polynomial_recent = polynomial_recentrecent;
			} else {
				tailstart = tailstartcubic; // findTailStart(keypoints, polynomial_all, (int)halflocalinter); // pack.recentdata / 2);
				quadraticpoints = cubicpoints;// - 0.04);
				valuepoints = quadraticpoints;
				// polynomial_recent = polynomial_recentrecent;
			}
		} else {
			tailstart = tailstartcubic; // findTailStart(keypoints, polynomial_all, (int)halflocalinter); // pack.recentdata / 2);
			quadraticpoints = cubicpoints;// - 0.04);
			valuepoints = quadraticpoints;
		}
	}

	public void microcosmic() {
		// 加载当前项目在余额表中的总份额
		investments = Investment.loads(code);
		totalshare = gettotalshare(0);
		realshare = gettotalshare(1);
		extractindex(false);
	}
	
	public double produceRiskByName () {
		double result = 0;
		if (name.indexOf("年") != -1) {
			result = 3.65;
		} else if (name.indexOf("A") != -1) {
			result = 3.65;
		} else if (name.indexOf("月") != -1) {
			result = 1.80;
		}
		return result;
	}

	// 仅仅计算势能值，当减为负，当加为正。
	public pack onDemandInvast(double fundamental) {
		pack result = new pack();
		ArrayList<pack> quadraticpoints = new ArrayList<pack>();
		// if (risklevel < 2) {现在不需要这个判断了。因为全局风险级用LS定值为11，必有驻点。固更改该判断为死分支
		if (risklevel < 0) {
			tailstart = polynomial_all.rawdata.get(
					polynomial_all.rawdata.size() - 5).getX();
			quadraticpoints = polynomial_all.pickdata(tailstart);
		} else {
			polynomial_recent = polynomial_all.d(1);
			extreme = polynomial_recent.findzeropoint();
			polynomial_recent = polynomial_recent.d(1);
			inflection = polynomial_recent.findzeropoint();
			keypoints = mergesort(extreme, inflection);
			for (int i = keypoints.length - 1; true; i--) {
				if (i >= 0) {
					tailstart = keypoints[i];
					quadraticpoints = polynomial_all.pickdata(tailstart);
					if (quadraticpoints.size() > 5) {
						break;
					} else {
						continue;
					}
				} else {
					tailstart = polynomial_all.start;
					quadraticpoints = polynomial_all.pickdata(tailstart);
					break;
				}
			}
		}
		double x = points.get(points.size() - 1).getX();

		Polynomial quadraticU = new Polynomial();
		Polynomial quadraticD = new Polynomial();
		pack EPointU = polynomial_all.extrapolationOne(pack.interval, true);
		pack EPointD = polynomial_all.extrapolationOne(pack.interval, false);
		ArrayList<pack> pointsU = (ArrayList<pack>)quadraticpoints.clone();
		pointsU.add(EPointU);
		ArrayList<pack> pointsD = (ArrayList<pack>)quadraticpoints.clone();
		pointsD.add(EPointD);
		quadraticU.processR(pointsU, 2, 0);
		quadraticD.processR(pointsD, 2, 0);
		result.x = quadraticU.getStringWeight(x + pack.interval, x, 2,
		fundamental, 1, 0.5).getX();
		result.y = quadraticD.getStringWeight(x + pack.interval, x, 2,
		fundamental, 1, 0.5).getX();

		quadratic.processR(quadraticpoints, 2, 0);
		result.value = quadratic.getStringWeight(x + pack.interval, x, 2,
				fundamental, 1, 0.5).getX(); 
		// result.r = pack.stagesweight[0] * result.x + pack.stagesweight[1] * result.value + pack.stagesweight[2] * result.y;
		result.r = maxInthree(result.x, result.value, result.y, true);
		return result;
	}
	
	// 在线计算势能值，当减为负，当加为正。
	public pack onLineDemandInvast(double fundamental, boolean online) {
		pack result = new pack();
		pack weight = new pack();
		double x = quadraticpoints.get(quadraticpoints.size() - 1).getX();// + pack.interval;
		if (online) {
			ArrayList<pack> pointsOnline = (ArrayList<pack>)quadraticpoints.clone();
			pack onlinePoint = getEstimitValueFromNet();
			if (Framework.todaytimestamp < onlinePoint.getX()) {
				pack shellPoint = polynomial_all.extrapolationOne(pack.interval, onlinePoint.getY());
				pack rawTailPoint =  new pack();
				rawTailPoint.setX(shellPoint.getX() + pack.interval);
				rawTailPoint.setY(pointsOnline.get(pointsOnline.size() - 1).getY());
//				pointsOnline.get(pointsOnline.size() - 1);
				pointsOnline.add(shellPoint);
				pointsOnline.add(rawTailPoint);
			}

			quadratic.processR(pointsOnline, 2, extrapolations);
			weight = quadratic.getStringWeight(x + pack.interval, x, order,
					fundamental, 1, 0.5); 
			result.value = weight.getX();
			result.val = weight.getY();
			result.value /= (1 + onlinePoint.getY() * 5);
			result.r = result.value;
		} else {
			/*Polynomial quadraticU = new Polynomial();
			Polynomial quadraticD = new Polynomial();
			pack EPointU = polynomial_all.extrapolationOne(pack.interval, true);
			pack EPointD = polynomial_all.extrapolationOne(pack.interval, false);
			ArrayList<pack> pointsU = (ArrayList<pack>)quadraticpoints.clone();
			pointsU.add(EPointU);
			ArrayList<pack> pointsD = (ArrayList<pack>)quadraticpoints.clone();
			pointsD.add(EPointD);
			quadraticU.processR(pointsU, 2, extrapolations);
			quadraticD.processR(pointsD, 2, extrapolations);
			result.x = quadraticU.getStringWeight(x + pack.interval, x, order,
			fundamental, 1, 0.5).getX();
			result.y = quadraticD.getStringWeight(x + pack.interval, x, order,
			fundamental, 1, 0.5).getX();*/

			quadratic.processR(quadraticpoints, 2, extrapolations);
			weight = quadratic.getStringWeight(x + pack.interval, x, order,
					fundamental, 1, 0.5); 
			result.value = weight.getX();
			// result.r = pack.stagesweight[0] * result.x + pack.stagesweight[1] * result.value + pack.stagesweight[2] * result.y;
			result.r = maxInthree(result.x, result.value, result.y, true);
			result.val = weight.getY();
			System.out.println("raw: " + weight.toString());
		}
		double delta = quadraticpoints.get(quadraticpoints.size() - 1).getY() - quadratic.f(quadratic.end, 0);
		double base = quadraticpoints.get(quadraticpoints.size() - 1).val - delta;
		double updnRate = (delta / base ) * 10;
		if (result.r > 0) {
			result.r = (result.r * (1 - updnRate));
			if (result.r <= 0) {
				result.r = 0.000000000000001;
			}				
		} else if (result.r < 0) {
			if (result.val <= -1) {
				
			} else {
				result.val = (result.val * (1 + updnRate));
				if (result.val <= -1) {
					result.val = -0.999999999999999999999999999999;
				}				
			}
		}
		System.out.println("updn: " + result.r + ", " + result.val);
		if (paint > 0) {
			verify.saveparam(Framework.getPath("coin", "paint", "weightup"), "");
			verify.saveparam(Framework.getPath("coin", "paint", "weightdn"), "");
			if (result.r > 0) {
				verify.appenddata(Framework.getPath("coin", "paint", "weightup"),
				String.valueOf(x + 0 * pack.interval) + ","
				+ String.valueOf(result.r * 250 + points.get(points.size() - 1).getY()) + "\n");
				System.out.println("into up!");
				//verify.appenddata(Framework.basepath + "/pythonparam.txt", String.valueOf(verify.cutDouble(result.r, 4)));
			}
			if (result.r < 0) {
				if (result.val > -1) {
					verify.appenddata(Framework.getPath("coin", "paint", "weightdn"),
					String.valueOf(x + 0 * pack.interval) + ","
					+ String.valueOf(result.val * 25 + points.get(points.size() - 1).getY()) + "\n");
					System.out.println("into dn!");
					//verify.appenddata(Framework.basepath + "/pythonparam.txt", String.valueOf(verify.cutDouble(result.val, 4)));
				}
			}
			quadratic.paintCurve(Framework.getPath("coin", "paint", "quatratic"), pack.interval);
		}
		return result;
	}
	
	public static double Escale(double[] keypoints, int type) {
		if (keypoints.length > 1) {
			double[] interval = new double[keypoints.length - 1];
			for (int i = 0; i < keypoints.length - 1; i++) {
				interval[i] = keypoints[i + 1] - keypoints[i];
			}
			if (type == -1) {
				int index = 0;
				double inter = 100000;
				for (int i = 0; i < interval.length; i++) {
					if (interval[i] < inter) {
						inter = interval[i];
						index = i;
					}
				}
				return inter;
			} else if (type == 0){
				double inter = 0;
				for (int i = 0; i < interval.length; i++) {
						inter += interval[i];
				}
				inter /= ((interval.length == 1 ? 2 : interval.length) - 1);
				return inter;
			} else if (type == 1) {
				Arrays.sort(interval);
				// for (int i = 0; i < interval.length; i++) {
				// 	String stars = "";
				// 	for (int j = 0; j < interval[i] / 0.02; j++) {
				// 		stars += "*";
				// 	}
				// 	System.out.println(stars + interval[i]);
				// }
				int midIndex = interval.length / 2;
				if (midIndex * 2 == interval.length) {
					return (interval[midIndex - 1] + interval[midIndex]) / 2;
				} else {
					return interval[midIndex];
				}
			}
		} else {
			return pack.interval * 4;
		}
		return pack.interval * 4;
	}
	public static double findTailStart(double[] keypoints, Polynomial polynomial_all, int leastPointsNum) {
		double tailstart = 0;
		ArrayList<pack> quadraticpoints = new ArrayList<pack>();
		if (keypoints.length == 0) {
			double[] temp = { polynomial_all.end * 0.8 };
			keypoints = temp;
		}
		for (int i = keypoints.length - 1; true; i--) {
			if (i >= 0) {
				tailstart = keypoints[i];
				quadraticpoints = polynomial_all.pickdata(tailstart);
				if (quadraticpoints.size() > leastPointsNum) { // 7) { // 
					break;
				} else {
					continue;
				}
			} else {
				tailstart = polynomial_all.start;
				quadraticpoints = polynomial_all.pickdata(tailstart);
				break;
			}
		}
		return tailstart;
	}

	public void calcnewpoints(int start, int end) {
		all_newpoints = polynomial_all.extrapolation(pack.interval, start, end);
		recent_newpoints = polynomial_recent.extrapolation(pack.interval,
				start, end);
		quadratic_newpoints = quadratic
				.extrapolation(pack.interval, start, end);
	}

	public void rulemining(ArrayList<pack> points) {
		ArrayList<pack> recentpoints = new ArrayList<pack>();
		for (int i = points.size() - pack.recentdata; i < points.size(); i++) {
			recentpoints.add(points.get(i));
		}
		Polynomial polynomial_recent1;
		Polynomial polynomial_recent2;
		mediumrisklevel = polynomial_recent.process(recentpoints,
				mediumrisklevel, 1, 0).getRowDimension() - 1;
		polynomial_recent1 = polynomial_recent.d(1);
		extreme = polynomial_recent1.findzeropoint();
		polynomial_recent2 = polynomial_recent1.d(1);
		// if (polynomial_recent2.x == null) {
		// polynomial_recent2 = polynomial_recent1.d(1);
		// }
		inflection = polynomial_recent2.findzeropoint();
		// keypoints = mergesort(extreme, inflection);
		ArrayList<pack> quadraticpoints = new ArrayList<pack>();
		// if (points.size() == 191) {
		// System.out.println(points.size());
		// }
		for (int i = inflection.length - 1; true; i--) {
			if (i >= 0) {
				tailstart = inflection[i];
				quadraticpoints = polynomial_recent.pickdata(tailstart);
				if (quadraticpoints.size() > 2) {
					break;
				} else {
					continue;
				}
			} else {
				tailstart = polynomial_recent.start;
				quadraticpoints = polynomial_recent.pickdata(tailstart);
				break;
			}
		}
		// if (inflection.length > 0) {
		// for (int i = inflection.length - 1; i >= 0; i--) {
		// if ((inflection[i] - polynomial_recent2.start)
		// / (polynomial_recent2.end - polynomial_recent2.start) < 0.96) {
		// tailstart = inflection[i];
		// quadraticpoints = polynomial_recent.pickdata(tailstart);
		// if (quadraticpoints.size() > 2) {
		// break;
		// } else {
		// continue;
		// }
		// } else {
		// continue;
		// }
		// }
		// } else {
		// tailstart = polynomial_recent.start;
		// quadraticpoints = polynomial_recent.pickdata(tailstart);
		// }
		quadratic.processLS(quadraticpoints, 2);
	}

	public String extractindex(String[] information) {
		outString = "";
		String code = Framework.safeget(information, 0);
		String number = Framework.safeget(information,
				Framework.listinfolength - 1);
		number = number.substring(0, number.indexOf("%"));
		String newNAVstring = Framework.safeget(information, 4);
		double newNAV = Double.valueOf(newNAVstring);
		double fee = Double.valueOf(number) / 100;
		if (information.length > Framework.listinfolength) {
			predictBefore = Integer.valueOf(information[22]);
		} else {
			predictBefore = 0;
		}
		// double EYmin = minInthree(all_newpoints.get(all_newpoints.size() - 1)
		// .getY(), recent_newpoints.get(recent_newpoints.size() - 1)
		// .getY(), quadratic_newpoints
		// .get(quadratic_newpoints.size() - 1).getY());
		// double EYmax = maxInthree(all_newpoints.get(all_newpoints.size() - 1)
		// .getY(), recent_newpoints.get(recent_newpoints.size() - 1)
		// .getY(), quadratic_newpoints
		// .get(quadratic_newpoints.size() - 1).getY());
		// double EYaverage = averageInthree(
		// all_newpoints.get(all_newpoints.size() - 1).getY(),
		// recent_newpoints.get(recent_newpoints.size() - 1).getY(),
		// quadratic_newpoints.get(quadratic_newpoints.size() - 1).getY());
		// EYmin = (EYmin + 100)
		// / ((points.get(points.size() - 1).getY() + 100) * (1 + fee));
		// EYmax = (EYmax + 100)
		// / ((points.get(points.size() - 1).getY() + 100) * (1 + fee));
		// EYaverage = (EYaverage + 100)
		// / ((points.get(points.size() - 1).getY() + 100) * (1 + fee));
		// if (EYmin > 1) {
		// Erate = EYmin;
		// } else if (EYmax < 1) {
		// Erate = EYmax;
		// } else {
		// Erate = EYaverage;
		// }
		pack result = onDemandInvast(1);
		Erate = result.r;// Investment.amount);
		String rateString = "(" + verify.cutDouble(result.x, 3) + "|" + verify.cutDouble(result.value, 3) + "|" + verify.cutDouble(result.y, 3) + ")";
		// 加一是为了保持reliability函数的最小改动
		double reliability = reliability(Erate + 1);
		// double conclusion = 0.925 * reliability + 0.075 * Erate;
		double conclusion = 1;
		if (Erate > 0) {
			conclusion = reliability * Erate * 10000 / (1 + fee);
		} else {
			conclusion = reliability * Erate + 1;
		}
		Erate = reliability * Erate / (1 + fee) + 1;
		// 计策修改时关注这里，第7个值（[6]，这里是conclusion）是真正参与测评的。增持与减持列表同使用该值。该值貌似始终需要大于零（sortLevelIndex）。区别在于是否小于1，这涉及到分值叉录入、减持计算等等；TODO
		return outString = code + "," + information[1] + rateString + "," + totalshare + ","
				+ predictBefore + "," + Erate + "," + reliability + ","
				+ conclusion + ",";
	}

	public String extractindex(boolean online) {
		outString = "";
		pack result = new pack();
		result = onLineDemandInvast(1, online);
		Erate = result.r;// Investment.amount);
		rateString = "(" + verify.cutDouble(result.x, 4) + "|" + verify.cutDouble(result.value, 4) + "|" + verify.cutDouble(result.y, 4) + ")";
		// 加一是为了保持reliability函数的最小改动
		reliability = reliability(Erate + 1);
		// double conclusion = 0.925 * reliability + 0.075 * Erate;
		if (Erate >= 0) {
			Erate *= immediaterisk;
			conclusion = reliability * Erate / (1 + fee);
			System.out.println("  /|\\  ");
			System.out.println("   |    " + immediaterisk + ", " + Erate);
		} else {
			Erate = result.val;
			if (Erate > -1) {
				double r = -(immediaterisk - 2);
				Erate *= (r > 1 ? 1 : r);				
				if (Erate <= -1) {
					Erate = -0.999;
				}				
			} else if (Erate <= -1) {
				Erate = -1;
			}
			System.out.println("   |    ");
			System.out.println("  \\|/   " + (-(immediaterisk - 2)) + ", " + Erate);
			conclusion = reliability * Erate / (1 + fee);
		}
		if (Erate > -1 && Erate < 0 ) {
			Erate *= getdiscount(Erate, lastpointtoprate, pack.discountRate);
			if (Erate <= -1) {
				Erate = -0.999;
			}
		} else if (Erate <= -1){
			Erate = -1;
		} else {
			Erate *= getdiscount(Erate, lastpointtoprate, pack.discountRate);
		}
		if (paint > 0) {
			String pythonconfig = Framework.getPath("coin", "paint", "pythonparam");
			verify.saveparam(pythonconfig, "");
			verify.appenddata(pythonconfig, String.valueOf(verify.cutDouble(Erate, 4)));
		}
		if (Erate > 0) {
			Erate = gate == -1 ? 0 : Erate;
		} else if (Erate < 0) {
			Erate = gate == 1 ? 0 : Erate;
		}
		Erate += 1; //reliability * Erate / (1 + fee) + 1;
		// 计策修改时关注这里，第7个值（[6]，这里是conclusion）是真正参与测评的。增持与减持列表同使用该值。该值貌似始终需要大于零（sortLevelIndex）。区别在于是否小于1，这涉及到分值叉录入、减持计算等等；TODO
		outString = printFund();
		return outString;
		// return outString = code + "," + information[1] + rateString + "," + totalshare + ","
		// 		+ predictBefore + "," + Erate + "," + reliability + ","
		// 		+ conclusion + ",";
	}

	public String printFund() {
		return outString = Framework.getFieldPart("code", code)
		+ Framework.getFieldPart("name", name)
		+ Framework.getFieldPart("totalshare", totalshare)
		+ Framework.getFieldPart("realshare", realshare)
		+ Framework.getFieldPart("Erate", Erate)
		+ Framework.getFieldPart("amount", amount)
		+ Framework.getFieldPart("reliability", reliability)
		+ Framework.getFieldPart("conclusion", conclusion)
		+ Framework.getFieldPart("newNAV", newNAVstring)
		+ Framework.getFieldPart("fee", fee)
		+ Framework.getFieldPart("risklevel", risklevel)
		+ Framework.getFieldPart("mediumrisklevel", mediumrisklevel)
		+ Framework.getFieldPart("lastpointtoprate", lastpointtoprate)
		+ Framework.getFieldPart("tailstart", tailstart)
		// + Framework.getFieldPart("lastpoint", "@" + points.get(points.size() - 1).getX() + "|" + points.get(points.size() - 1).getY())
		+ Framework.getFieldPart("tailpoints", organizePoints(quadraticpoints));
	}

	public double reliability(double thisrate) {
		// 可信度。涨有可信度，跌也有可信度
		double result = 1;
		// 信多跌不信多涨
		//double risk = getimmediaterisk();
		// 信号量，涨大于1，跌小于1
		double sign = thisrate - 1;
		sign = Math.signum(sign);
		if (sign < 0) {
			// 如果判跌就加可信度
			// result += risk;
			// if (lastpointtoprate > 0.33) {
			// result += lastpointtoprate - 0.33;
			// }
		} else {
			// 多下分散置信
			// result /= (1 + risk);
			// if (lastpointtoprate < 0.33) {
			// result += 0.33 - lastpointtoprate;
			// }
		}
		// 短期风险率的倒数
		result -= (mediumrisklevel / 10);
		// 根据末期水位打折最终指标
		result *= getdiscount(lastpointtoprate, pack.discountRate);
		if (predictBefore * sign >= 0) {
			// 如果本次预测与之前的预测同向，本次结果置信为指数上升(连续预测可以作为记录，比真实的连续同向不值得有参考性)
			// result *= Math.pow(1.1, Math.abs(predictBefore));
			// 且继续累积预测
			predictBefore += sign;
		} else {
			// 否则重新从头累积
			predictBefore = 0;
		}
		return result;
	}

	// 偏离标准程度的量化器double sign二值信号, double lasttoprate末点水位, double standard低水位标准
	public double getdiscount(double lasttoprate, double standard) {
		// 如果判升
		if (lasttoprate > standard) {
			// 高于低水位，补段除以水位线补段
			return (1 - lasttoprate) / (1 - standard);
		} else if (lasttoprate < (1 - standard)) {
			// 低于低水位的对称线，本段除以低水位补长度
			return lasttoprate / (1 - standard);
		} else {
			// 高于低水位的对称线，置信降率为1
			return 1; // (lasttoprate) / (standard);
		}
	}

	// 偏离标准程度的量化器double sign二值信号, double lasttoprate末点水位, double standard低水位标准
	public double getdiscount(double sign, double lasttoprate, double standard) {
		double result = 0;
		double anti = standard / 10;
		double antirate = standard * anti;
		if (sign > 0) {
			// 如果判升
			//if (lasttoprate > standard) {
				// 高于低水位，补段除以水位线补段
			if (lasttoprate < anti) {
				return lasttoprate / (antirate);
			}
			return (1 - lasttoprate) / (standard);
			/*} else {
				// 低于低水位，置信升率为1
				return 1; // (standard - lasttoprate) / (standard);
			}*/
		} else {
			// 如果判降
			//if (lasttoprate < (1 - standard)) {
				// 低于低水位的对称线，本段除以低水位补长度
			if (lasttoprate > (1 - anti)) {
				return (1 - lasttoprate) / (antirate);
			}
			return lasttoprate / (standard); // (lasttoprate - standard)
														// / (1 - standard);
			/*} else {
				// 高于低水位的对称线，置信降率为1
				return 1; // (lasttoprate) / (standard);
			}*/
		}
	}

	// 信跌不信涨。爬一步加0.5，跌一步减1。连跌大于0，输出原值，连涨输出0。
	public double getimmediaterisk() {
		int size = this.points.size();
		double Ya = this.points.get(size - 4).getY();
		double Yb = this.points.get(size - 3).getY();
		double Yc = this.points.get(size - 2).getY();
		double Yd = this.points.get(size - 1).getY();
		double D1 = Yb - Ya > 0 ? 0.5 : -1;
		double D2 = Yc - Yb > 0 ? 0.5 : -1;
		double D3 = Yd - Yc > 0 ? 0.5 : -1;
		double sign = (D1 + D2 + D3) / -3;
		return this.immediaterisk = sign < 0 ? 0 : sign;
	}

	// 获取当前项的持有总额
	public double gettotalshare(int type) {
		// 简单加和
		return Investment.gettotalshare(investments, type);
	}

	public pack getEstimitValueFromNet() {
		pack result = new pack();
		// String url = "http://fundgz.1234567.com.cn/js/" + this.code + ".js?rt=" + Framework.getNowTimestamp();
		String url = "http://fundgz.1234567.com.cn/js/" + this.code + ".js?";
		Framework.waiting(1, this.code + " is getting from internet!");
		url = Framework.getHttp(url);
		result.setX(Framework.dateToStamp(Framework.getInfoFromJson(url, "gztime", "\""), "yyyy-MM-dd HH:mm"));
		String gszzl = Framework.getInfoFromJson(url, "gszzl", "\"");
		if ("".equals(gszzl)) {
			result.setX(0);
			result.setY(0);
		} else {
			result.setY(Double.valueOf(gszzl) / 100.0);
		}
		return result;
	}
	
	//
	// public double newaccumulatevalue(double accumulatevalue, double preY,
	// double currentY) {
	// double step = 0;
	// if (currentY - preY > 0) {
	// step = 0.5;
	// } else {
	// step = -1;
	// }
	// accumulatevalue += step;
	// if (accumulatevalue >= 0) {
	// accumulatevalue = 0;
	// }
	// return accumulatevalue;
	// }

	public String organizePoints(ArrayList<pack> quadraticpoints) {
		String result = "";
		for (int i = 0; i < quadraticpoints.size(); i++) {
			result += "@" + quadraticpoints.get(i).getX() + "%" + quadraticpoints.get(i).getY();
		}
		return result;
	}

	public static ArrayList<pack> parsePoints(String info) {
		ArrayList<pack> points = new ArrayList<pack>();
		info = info.substring(1, info.length());
		String[] pointsInfo = info.split("@");
		for (int i = 0; i < pointsInfo.length; i++) {
			points.add(parsePoint(pointsInfo[i]));
		}
		return points;
	}

	public static pack parsePoint(String info) {
		String[] xy = info.split("%");
		pack result = new pack(Double.valueOf(xy[0]), Double.valueOf(xy[1]));
		return result;
	}


	public double minInthree(double a, double b, double c) {
		if (a < b) {
			if (a < c) {
				return a;
			} else {
				return c;
			}
		} else {
			if (b < c) {
				return b;
			} else {
				return c;
			}
		}
	}

	public double maxInthree(double a, double b, double c, boolean abs) {
		if (abs) {
			if (Math.abs(a) > Math.abs(b)) {
				if (Math.abs(a) > Math.abs(c)) {
					return a;
				} else {
					return c;
				}
			} else {
				if (Math.abs(b) > Math.abs(c)) {
					return b;
				} else {
					return c;
				}
			}
		} else {
			if (a > b) {
				if (a > c) {
					return a;
				} else {
					return c;
				}
			} else {
				if (b > c) {
					return b;
				} else {
					return c;
				}
			}
		}
	}

	public double averageInthree(double a, double b, double c) {
		return (a + b + c) / 3;
	}

	public double[] mergesort(double[] a, double[] b) {
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
	// public double extractindex() {
	// Collections.sort(all_newpoints, new Comparator<Object>() {
	// @Override
	// public int compare(Object o1, Object o2) {
	// pack p1 = (pack) o1;
	// pack p2 = (pack) o2;
	// if (p1.getY() > p2.getY()) {
	// return 1;
	// } else if (p1.getY() == p2.getY()) {
	// return 0;
	// } else {
	// return -1;
	// }
	// }
	// });
	// longEY = all_newpoints.get(all_newpoints.size() / 2).getY();
	// Collections.sort(recent_newpoints, new Comparator<Object>() {
	// @Override
	// public int compare(Object o1, Object o2) {
	// pack p1 = (pack) o1;
	// pack p2 = (pack) o2;
	// if (p1.getY() > p2.getY()) {
	// return 1;
	// } else if (p1.getY() == p2.getY()) {
	// return 0;
	// } else {
	// return -1;
	// }
	// }
	// });
	// mediumEY = recent_newpoints.get(recent_newpoints.size() / 2).getY();
	// Collections.sort(quadratic_newpoints, new Comparator<Object>() {
	// @Override
	// public int compare(Object o1, Object o2) {
	// pack p1 = (pack) o1;
	// pack p2 = (pack) o2;
	// if (p1.getY() > p2.getY()) {
	// return 1;
	// } else if (p1.getY() == p2.getY()) {
	// return 0;
	// } else {
	// return -1;
	// }
	// }
	// });
	// shortEY = quadratic_newpoints.get(quadratic_newpoints.size() / 2)
	// .getY();
	// return pack.stagesweight[0] * longEY + pack.stagesweight[0] * mediumEY
	// + pack.stagesweight[0] * shortEY;
	//
	// }

}
