package polyfit;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.TreeMap;

import Jama.Matrix;

public class Polynomial {

	RhoFunction r;

	ArrayList<Matrix> x = new ArrayList<Matrix>();

	double start = 0;

	double end = 0;

	double max = 0;

	double min = 0;

	double averageU = 0;
	double averageD = 0;

	double[] zero = {};

	Map<Integer, Double> probabilitydensityfunction;

	public ArrayList<pack> rawdata = new ArrayList<pack>();

	ArrayList<double[]> domain = new ArrayList<double[]>();

	pack worse = new pack();
	pack imum;

	public Polynomial() {
		r = new RhoFunction();
		if (this.x.size() < 1) {
			double[][] intro = { { 0 } };
			Matrix startStone = new Matrix(intro);
			this.x.add(startStone);
			double[] init = { -Double.MAX_VALUE, Double.MAX_VALUE };
			this.domain.add(init);
		}
	}

	public Map<Integer, Integer> analysis(ArrayList<pack> points,
			boolean ifprint) {
		TreeMap<Integer, Integer> statistic = null;
		try {
			statistic = new TreeMap<Integer, Integer>();
			int sample = 0;
			start = points.get(0).getX();
			end = start;
			max = points.get(0).getY();
			min = max;
			double x = 0;
			double y = 0;
			double averageU = 0;
			double averageD = 0;
			int numU = 0;
			int numD = 0;
			for (int i = 0; i < points.size(); ++i) {// pack pack : points) {
				x = points.get(i).getX();
				if (x < start) {
					start = x;
				}
				if (x > end) {
					end = x;
				}
				y = points.get(i).getY();
				if (y < min) {
					min = y;
				}
				if (y > max) {
					max = y;
				}
				sample = (int) Math.round(points.get(i).getY());
				if (statistic.containsKey(sample)) {
					statistic.put(sample, statistic.get(sample) + 1);
				} else {
					statistic.put(sample, 1);
				}
				if (i > 0) {
					if (points.get(i).getY() > points.get(i - 1).getY()) {
						averageU += points.get(i).getY()
								- points.get(i - 1).getY();
						numU++;
					}
					if (points.get(i).getY() < points.get(i - 1).getY()) {
						averageD += points.get(i).getY()
								- points.get(i - 1).getY();
						numD++;
					}
				}
			}
			if (numU > 0) {
				this.averageU = averageU / numU;
			} else {
				this.averageU = 0;
			}
			if (numD > 0) {
				this.averageD = averageD / numD;
			} else {
				this.averageD = 0;
			}
			probabilitydensityfunction = new TreeMap<Integer, Double>();
			for (int key : statistic.keySet()) {
				probabilitydensityfunction.put(key, (double) statistic.get(key)
						/ points.size());
			}
			int mapKey = 0;
			int mapValue = 0;
			String shape = "";
			if (ifprint) {
				for (Entry<Integer, Integer> entry : statistic.entrySet()) {
					mapKey = entry.getKey();
					mapValue = entry.getValue();
					shape = "";
					for (int i = 1; i < mapValue; i++) {
						shape += "=";
					}
					System.out.println(String.format("%3d", mapKey) + "="
							+ shape + mapValue);
				}
			}
			if (this.x.size() < 2) {
				// addAnalyticFormula(null, start, end);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return statistic;
	}

	// 计算回到同样高水平所需的最长时间间隔及相关信息
	public pack worseAnalysis(ArrayList<pack> points, double stablity) {
		// 结果包
		// pack result = new pack(0, 0);
		// 最大间隔
		worse.value = 0;
		try {
			if (stablity == 0) {
				// 起始标记点
				pack start = null;
				// 向后找临近点，后者比前者低的
				for (int i = 0; i + 1 < points.size(); i++) {
					start = points.get(i);
					if (points.get(i + 1).getY() < start.getY()) {
						// 找到后者比前者低
						pack next = null;
						// 向后找
						for (int j = i + 1; j < points.size(); j++) {
							next = points.get(j);
							// 直到找到一个回到原水平的
							boolean surpass = next.getY() - start.getY() > -0.05;
							// 记录下相关信息，如果最后也没找到，无论如何使用最后一个点做结束
							if (surpass || j == points.size() - 1) {
								// 只留下最长的记录
								if (next.getX() - start.getX() > worse.value) {
									worse.value = next.getX() - start.getX();
									worse.setX(start.getX());
									worse.setY(next.getX());
								}
								// 将结尾点再记为起始点（上面会自加一）
								i = j - 1;
								break;
							}
						}
					}
				}
				double v = (points.get(points.size() - 1).getX() - points.get(0).getX()) / pack.totalInterval; //
				if (v < 0.8) {
					worse.value = 0;
				}
			} else {
				worse.value = stablity;
			}
			// 计算跨度占整个长度的比例，也作为一个指标，接近1就要注意了
			worse.r = worse.value / pack.totalInterval; // points.get(points.size() - 1).getX() - points.get(0).getX(); //
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return worse;
	}

	public ArrayList<Matrix> addAnalyticFormulaInOrder(Matrix x,
			double startPoint) {
		if (startPoint > this.domain.get(this.domain.size() - 1)[0]) {
			if (startPoint < this.domain.get(this.domain.size() - 1)[1]) {
				this.x.add(x);
				this.domain.get(this.domain.size() - 1)[1] = startPoint;
				double[] domain = { startPoint, Double.MAX_VALUE };
				this.domain.add(domain);
			} else {
				System.err.println("新添加定义域游离，仅有开端，无末端。");
			}
		}
		return this.x;
	}

	public ArrayList<Matrix> addAnalyticFormula(Matrix x, double startPoint,
			double endPoint) {
		if (startPoint < endPoint) {
			int brokenIntervalIndex = findInterval(startPoint);
			if (this.domain.get(brokenIntervalIndex)[1] <= endPoint) {
				if (this.domain.get(brokenIntervalIndex)[1] < endPoint) {
					if (endPoint < this.domain.get(findInterval(endPoint))[1]) {
						this.domain.get(findInterval(endPoint))[0] = endPoint;
					} else {
						endPoint = this.domain.get(brokenIntervalIndex)[1];
					}
					this.domain.get(brokenIntervalIndex)[1] = startPoint;
				} else {
					this.domain.get(brokenIntervalIndex)[1] = startPoint;
				}
			} else {
				double[] newDomain = { endPoint,
						this.domain.get(brokenIntervalIndex)[1] };
				this.domain.get(findInterval(startPoint))[1] = startPoint;
				this.domain.add(newDomain);
				this.x.add(this.x.get(brokenIntervalIndex));
			}
			this.x.add(x);
			double[] domain = { startPoint, endPoint };
			this.domain.add(domain);
		}
		return this.x;
	}

	public double evaltoprate(double in) {
		double probability = 0;
		try {
			Integer inint = (int) Math.round(in);
			probability = 0;
			for (int key : probabilitydensityfunction.keySet()) {
				if (inint >= key) {// 比百分之多少大
					probability += probabilitydensityfunction.get(key);
				} else {
					break;
				}
			}
			// if (in > min) {
			// probability = (in - min) / (max - min);
			// }
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return probability;
	}

	public Matrix process(ArrayList<pack> points, int order, int type, int extrapolations) {
		try {
			analysis(points, false);
			if (extrapolations > 0) {
				rawdata = new ArrayList<>();
				for (int i = 0; i < points.size(); i++) {
					rawdata.add(points.get(i));
				}
				this.extrapolations(pack.interval, extrapolations);
			} else {
				rawdata = points;
			}
			double[] x = new double[rawdata.size()];
			double[] y = new double[rawdata.size()];
			for (int i = 0; i < x.length; i++) {
				x[i] = rawdata.get(i).getX();
				y[i] = rawdata.get(i).getY();
			}
			Matrix l = organizel(y);
			pack former = new pack();
			pack later = new pack();
			if (order < 1) {
				for (int i = 1; i <= pack.maxorder; i++) {
					Matrix B = organizeB(x, i);
					later = robustestimition(B, l);
					this.x.set(1, later.getMatrix());
					System.out.println(i + "num:" + Math.round(later.number)
							+ " val:" + Math.round(later.value));
					if (ifstop(former, later, B)) {
						break;
					}
					former = later;
				}
			} else {
				double realy;
				double estimatey;
				for (int i = order; i > 0; --i) {
					Matrix B = organizeB(x, i);
					// Matrix tempx = leastsquare(B, l).getMatrix();
					if (type == 0) {
						addAnalyticFormulaInOrder(leastsquare(B, l).getMatrix(),
								start);						
					} else {
						addAnalyticFormulaInOrder(robustestimition(B, l)
						.getMatrix(), start);
					}
					realy = rawdata.get(rawdata.size() - 1).getY();
					estimatey = this.f(rawdata.get(rawdata.size() - 1).getX(), 0);
					if (Math.abs(estimatey - realy) < 2 * (max - min)) {
						Polynomial pi = this.d(1);
						// if (pi.x.size() == 1) {
						// 	pi = this.d(1);
						// }
						pi.findzeropoint();
						if (pi.zero.length + 1 == i) {
							break;
						} else {
							B = organizeB(x, pi.zero.length + 1);
							if (type == 0) {
								this.x.set(this.x.size() - 1, leastsquare(B, l)
										.getMatrix());
							} else {
								this.x.set(this.x.size() - 1, robustestimition(B, l)
										.getMatrix());
							}
							break;
						}
					} else {
						// System.out.println("bigger!" + i);
						continue;
					}
				}
			}
			if (extrapolations > 0) {
				rawdata = points;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return this.x.get(this.x.size() - 1);
	}
	public Matrix processLS(ArrayList<pack> points, int order) {
		try {
			analysis(points, false);
			if (points.size() > order) {
				rawdata = points;
				double[] x = new double[points.size()];
				double[] y = new double[points.size()];
				for (int i = 0; i < x.length; i++) {
					x[i] = points.get(i).getX();
					y[i] = points.get(i).getY();
				}
				Matrix l = organizel(y);
				Matrix B = organizeB(x, order);
				addAnalyticFormulaInOrder(leastsquare(B, l).matrix, start);
				if (order == 2) {
					imum = getimum(this.x.size() - 1);
				} else {
					System.err.println("需要拟合" + order);
				}
			} else {
				System.err.println("需要拟合" + order + "次函数，但你只给了" + points.size()
						+ "个点。");
				// return null;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return this.x.get(this.x.size() - 1);
	}

	public Matrix processR(ArrayList<pack> points, int order, int extrapolations) {
		try {
			analysis(points, false);
			if (points.size() > order) {
				if (extrapolations > 0) {
					rawdata = new ArrayList<>();
					for (int i = 0; i < points.size(); i++) {
						rawdata.add(points.get(i));
					}
					this.extrapolations(pack.interval, extrapolations);
				} else {
					rawdata = points;
				}
				double[] x = new double[rawdata.size()];
				double[] y = new double[rawdata.size()];
				for (int i = 0; i < x.length; i++) {
					x[i] = rawdata.get(i).getX();
					y[i] = rawdata.get(i).getY();
				}
				Matrix l = organizel(y);
				Matrix B = organizeB(x, order);
				addAnalyticFormulaInOrder(robustestimition(B, l).matrix, start);
				if (order == 2) {
					imum = getimum(this.x.size() - 1);
				} else {
					System.err.println("需要拟合" + order);
				}
				if (extrapolations > 0) {
					rawdata = points;
				}
			} else {
				System.err.println("需要拟合" + order + "次函数，但你只给了" + points.size()
						+ "个点。");
				return null;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return this.x.get(this.x.size() - 1);
	}

	public boolean ifstop(pack former, pack later, Matrix B) {
		try {
			double rate1 = former.number / later.number;
			double rate2 = former.value / later.value;
			if (1.03 > rate1 && 0.89 < rate1 && 1.03 > rate2 && 0.89 < rate2) {
				/*
				 * if ((later.value < 1735 || later.number < 310) &&
				 * Math.abs(later.getMatrix().get(0, 0)) < 0.000005) if ((1.03 >
				 * rate1 && 0.89 < rate1 && 1.04 > rate2 && former.number <
				 * B.getRowDimension() * pack.maxVTPVtimes && former.value < B
				 * .getRowDimension() * pack.maxVTVtimes) || former.number <
				 * 0.0001 || Math.abs(later.getMatrix().get(0, 0)) < 0.0001) {
				 */
				return true;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	public pack leastsquare(Matrix B, Matrix l) {
		pack result = null;
		try {
			Matrix x = new Matrix(B.getColumnDimension(), 1, 0);
			x = getLSx(B, l);
			result = new pack();
			result.matrix = x;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	public pack robustestimition(Matrix B, Matrix l) {
		// Matrix X = new Matrix(B.getRowDimension(), 1);
		// for (int i = 0; i < B.getRowDimension(); i++) {
		// X.set(i, 0, B.get(i, B.getColumnDimension() - 1));
		// }
		pack result = null;
		try {
			double stopcondition = 0.01;
			Matrix P = new Matrix(B.getRowDimension(), l.getRowDimension());
			for (int i = 0; i < P.getRowDimension(); i++) {
				P.set(i, i, 1);
			}
			Matrix previousP = P.copy();
			Matrix x = new Matrix(B.getColumnDimension(), 1, 0);
			Matrix V;
			for (int i = 0;; i++) {
				x = getx(B, P, l);
				V = getV(B, x, l);
				P = processP(P, V);
				if (stopcondition >= getdeltaSquare(previousP, P)) {
					break;
				}
				previousP = P.copy();
			}
			result = new pack();
			result.matrix = x;
			result.number = min(V, P);
			result.value = min(V);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return leastsquare(B, l);
		}
		// result.r = co(V, X);
		return result;
	}

	public pack extrapolationOne(double interval, boolean upORdown) {
		pack newPoint = new pack();
		try {
			double lastx = rawdata.get(rawdata.size() - 1).getX();
			double lasty = rawdata.get(rawdata.size() - 1).getY();
			double ey = lasty;
			if (upORdown) {
				ey += averageU;
			} else {
				ey += averageD;
			}
			newPoint.setX(lastx + interval);
			newPoint.setY(ey);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return newPoint;
	}
	public ArrayList<pack> extrapolations(double interval, int n) {
		try {
			double lastx = rawdata.get(rawdata.size() - 1).getX();
			double lasty = rawdata.get(rawdata.size() - 1).getY();
			for (int i = 0; i < n; i++) {
				rawdata.add(new pack(lastx + (i + 1) * interval, lasty));
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return rawdata;
	}

	public pack extrapolationOne(double interval, Double Y) {
		pack newPoint = new pack();
		try {
			double lastx = rawdata.get(rawdata.size() - 1).getX();
			double lasty = rawdata.get(rawdata.size() - 1).getY();
			newPoint.setX(lastx + interval);
			newPoint.setY(((1 + (lasty / 100)) * (1 + Y) - 1) * 100);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return newPoint;
	}

	public ArrayList<pack> extrapolation(double interval, int start, int end) {
		ArrayList<pack> xs = null;
		try {
			xs = producefuturex(rawdata, interval, start, end);
			for (int i = 0; i < xs.size(); i++) {
				xs.get(i).setY(f(xs.get(i).getX(), 0));
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return xs;
	}

	public ArrayList<pack> producefuturex(ArrayList<pack> points,
			double interval, int start, int end) {
		ArrayList<pack> newpoints = null;
		try {
			newpoints = new ArrayList<pack>();
			double lastx = points.get(points.size() - 1).getX();
			pack newpoint;
			for (int i = start; i <= end; i++) {
				newpoint = new pack();
				newpoint.setX(i * interval + lastx);
				newpoints.add(newpoint);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return newpoints;
	}

	public int findInterval(double x) {
		// x += Double.MIN_VALUE;
		int result = -1;
		for (int i = 0; i < domain.size(); i++) {
			if (x >= domain.get(i)[0] && x < domain.get(i)[1]) {
				result = i;
				break;
			}
		}
		return result;
	}

	public double f(double x, double noise) {
		double result = 0;
		try {
			result = 0;
			result = f(this.x.get(findInterval(x)).getArray(), x);
			if (noise != 0) {
				result += new Random().nextGaussian() * noise;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	public double f(double[][] x, double var) {
		double result = 0;
		try {
			int order = x.length - 1;
			result = 0;
			for (int i = 0; i <= order; i++) {
				result += x[i][0] * Math.pow(var, order - i);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	public Polynomial d(int order) {
		if (order > 0) {
			Polynomial fi = null;
			try {
				fi = new Polynomial();
				for (int j = 1; j < x.size(); j++) {
					double[][] params = new double[x.get(j).getRowDimension() - 1][1];
					for (int i = 0; i < x.get(j).getRowDimension() - 1; i++) {
						params[i][0] = x.get(j).get(i, 0)
								* (x.get(j).getRowDimension() - i - 1);
					}
					if (params.length != 0) {
						fi.addAnalyticFormulaInOrder(new Matrix(params),
								this.domain.get(j)[0]);
					} else {
						double[][] param = { { Double.MIN_VALUE } };
						fi.addAnalyticFormulaInOrder(new Matrix(param),
								this.domain.get(j)[0]);
					}
				}
				fi.start = this.start;
				fi.end = this.end;
				--order;
				if (order > 0) {
					fi = fi.d(order);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return fi;
		} else {
			return this;
		}
	}

	public Polynomial quadraticTranslation(double distanceX, double distanceY,
			Boolean withDomain) {
		Polynomial fi = null;
		try {
			fi = new Polynomial();
			boolean ifFirstDomainPassBy = false;
			for (int j = 1; j < x.size(); j++) {
				if (x.get(j).getRowDimension() == 2) {
					double[][] params = new double[x.get(j).getRowDimension()][1];
					double a = x.get(j).get(0, 0);
					double b = x.get(j).get(1, 0);
					double c = x.get(j).get(2, 0);
					params[0][0] = a;
					params[1][0] = 2 * a * distanceX + b;
					params[2][0] = distanceX * distanceX + distanceX * b + c
							+ distanceY;
					double startPoint = withDomain ? this.domain.get(j)[0]
							- distanceX : this.domain.get(j)[0];
					double endPoint = withDomain ? this.domain.get(j)[1]
							- distanceX : this.domain.get(j)[1];
					fi.addAnalyticFormula(new Matrix(params), startPoint,
							endPoint);
					if (!ifFirstDomainPassBy) {
						fi.start = startPoint;
						ifFirstDomainPassBy = true;
					}
					fi.end = endPoint;
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return fi;
	}

	public Polynomial s(int order) {
		if (order > 0) {
			Polynomial Fi = null;
			try {
				Fi = new Polynomial();
				for (int j = 1; j < x.size(); j++) {
					double[][] params = new double[x.get(j).getRowDimension() + 1][1];
					for (int i = 0; i < x.get(j).getRowDimension(); i++) {
						params[i][0] = x.get(j).get(i, 0)
								/ (x.get(j).getRowDimension() - i);
					}
					params[x.get(j).getRowDimension()][0] = Fi.f(Fi.x
							.get(j - 1).getArray(), domain.get(j - 1)[1])
							- Fi.f(params, domain.get(j)[0]);
					Fi.addAnalyticFormulaInOrder(new Matrix(params),
							this.domain.get(j)[0]);
				}
				Fi.start = this.start;
				Fi.end = this.end;
				--order;
				if (order > 0) {
					Fi = Fi.s(order);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return Fi;
		} else {
			return this;
		}
	}

	// 获取二次函数线上各点小区间上的积分值，除以总积分计算权重
	// double x积分末点,
	// double start积分起点,
	// int order特征线次数,
	// double total总额,
	// int xIndex指定解析式索引
	public pack getStringWeight(double x, double start, double order,
			double total, int xIndex, double radix) {
		pack result = new pack();
		if (this.x.get(xIndex).getRowDimension() - 1 == 2
				&& this.x.get(xIndex).get(0, 0) != 0) {
			double rightY = f(rawdata.get(rawdata.size() - 1).getX(), 0);
			double leftY = f(rawdata.get(0).getX(), 0);
			// 获取二次线对称轴上的点（极值点）
			pack rawimum = getimum(xIndex);
			pack imum = getimum(xIndex);
			// 要使左短右长、只有右的线提前结束，从而留下有积分意义的线
			if (this.x.get(xIndex).get(0, 0) * (rightY - leftY) >= 0) {
				result.setX(0);
				result.setY(0);
				return result;
			}
			// 获取左端点
			pack known = new pack(rawdata.get(0).getX(), leftY);
			// 获取左右高，开口朝上为正，开口朝下为负
			double height = known.getY() - imum.getY();
			// 获取左右距离，有左为正，无左为负
			double width = imum.getX() - known.getX();
			// 将整个图像移到x轴上
			known.setY(height);
			double imumHeight = imum.getY();
			imum.setY(0);
			Polynomial function0 = new Polynomial();
			if (order == 0) {
				// 如果指定零次，积分线是平行于x轴的线
				pack mirror = new pack(imum.getX() + width, known.getY());
				ArrayList<pack> points = new ArrayList<pack>();
				points.add(known);
				points.add(mirror);
				function0.analysis(points, false);
				double[][] params = new double[1][1];
				params[0][0] = height;
				function0.x.add(new Matrix(params));
			} else if (order > 0 && order <= 2) {
				// 如果高于零次，需要分左右两边来拟合特征线
				double forExchangeX = imum.getX();
				imum.setX(known.getX());
				known.setX(forExchangeX);
				pack mirror = new pack(known.getX() + width, imum.getY());
				// 组装左右的各两个拟合点
				Polynomial leftPolynomial = new Polynomial();
				Polynomial rightPolynomial = new Polynomial();
				ArrayList<pack> leftPoints = new ArrayList<pack>();
				ArrayList<pack> rightPoints = new ArrayList<pack>();
				leftPoints.add(known);
				leftPoints.add(imum);
				rightPoints.add(known);
				rightPoints.add(mirror);
				if (order == 2) {
					// 如果指定次数为2，至少需要三个点
					// 采取右边线中段点函数值作为左侧补充点，另一边同理；为了不涉及定义域问题，直接使用表达式求Y值，并减去对称点高度，归化到x轴上
					double midx = (known.getX() + mirror.getX()) / 2;
					pack midLeft = new pack(midx - width, this.f(this.x.get(1)
							.getArray(), midx)
							- imumHeight);
					midx = (known.getX() + imum.getX()) / 2;
					pack midRight = new pack(midx + width, this.f(this.x.get(1)
							.getArray(), midx)
							- imumHeight);
					leftPoints.add(midLeft);
					rightPoints.add(midRight);
				}
				if (order == 0.5) {
					double midx = (known.getX() + mirror.getX()) / 2;
					pack midLeft = new pack(midx - width, height - (this.f(this.x.get(1).getArray(), midx) - imumHeight));
					midx = (known.getX() + imum.getX()) / 2;
					pack midRight = new pack(midx + width, height - (this.f(this.x.get(1).getArray(), midx) - imumHeight));
					leftPoints.add(midLeft);
					rightPoints.add(midRight);
				}
				leftPolynomial.processLS(leftPoints, order == 0.5 ? 2 : (int)order);
				rightPolynomial.processLS(rightPoints, order == 0.5 ? 2 : (int)order);
				if (leftPolynomial.start < rightPolynomial.start) {
					function0.assemble(leftPolynomial, 1);
					function0.assemble(rightPolynomial, 1);
				} else {
					function0.assemble(rightPolynomial, 1);
					function0.assemble(leftPolynomial, 1);
				}
			}
			// 积分
			Polynomial function1 = function0.s(1);
			//function0.display(5000);
			//function1.display(5000);
			// 对积分函数求拟合域（拟合原函数所用的点的区域）上的总积分值
			double integral = function1.f(function1.end, 0)
					- function1.f(function1.start, 0);
			// 使用总额求感兴趣点对应的小段部位的部分小额
			double realinter = 0;
			double realhalf = 0;
			double rate = 0;
			double needinter = 0;
			if (radix != 0) {
				realhalf = rawimum.getX() - rawdata.get(0).getX();
				rate = realhalf / (radix / 2);
				realinter = x - start;
				needinter = rate * realinter;
				x = start + needinter;
			}
			double zero = rawdata.get(0).getX();
			double ready = (function1.f(x, 0) - function1.f(zero, 0))/ (Math.abs(integral) / 1.33333333);
			ready = Math.abs(ready) > 1 ? Math.signum(ready) : ready;
			double weight = total * (function1.f(x, 0) - function1.f(start, 0))/ Math.abs(integral);
			result.setX(weight);
			result.setY(ready);
		}
		return result;
	}

	public Polynomial assemble(Polynomial outerPolynomial, int formulaIndex) {
		this.addAnalyticFormula(outerPolynomial.x.get(formulaIndex),
				outerPolynomial.domain.get(formulaIndex)[0],
				outerPolynomial.domain.get(formulaIndex)[1]);
		this.rawdata.addAll(outerPolynomial.rawdata);
		this.analysis(rawdata, false);
		return this;
	}

	public Polynomial continuation(Polynomial newOne, int subIndex) {
		this.addAnalyticFormulaInOrder(newOne.x.get(subIndex),
				newOne.domain.get(subIndex)[0]);
		this.rawdata.addAll(newOne.rawdata);
		this.analysis(rawdata, false);
		return this;
	}

	public double[] findzeropoint() {
		double[] result = null;
		try {
			ArrayList<Double> x = new ArrayList<Double>();
			double step = (end - start) / pack.steprate;
			double previouspoint = f(start, 0);
			double thispoint;
			double midvalue;
			double mid = 0;
			double right = 0;
			for (double i = start; i <= end; i += step) {
				right = i + step;
				thispoint = f(right, 0);
				if (previouspoint * thispoint > 0) {
				} else {
					for (int j = 0; right - i > pack.accuracy; j++) {
						mid = (right + i) / 2;
						midvalue = f(mid, 0);
						if (midvalue * previouspoint > 0) {
							i = mid;
							previouspoint = midvalue;
						} else {
							right = mid;
							thispoint = midvalue;
						}
					}
					x.add(i);
					i = right;
				}
				previouspoint = thispoint;
			}
			result = new double[x.size()];
			for (int i = 0; i < result.length; i++) {
				result[i] = x.get(i);
			}
			zero = result;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	public Matrix organizeB(double[] x, int order) {
		Matrix B = null;
		try {
			int n = order + 1;
			double[][] b = new double[x.length][n];
			for (int i = 0; i < b.length; i++) {
				for (int j = 0; j < n; j++) {
					b[i][j] = Math.pow(x[i], order - j);
				}
			}
			B = new Matrix(b);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return B;
	}

	public Matrix organizel(double[] y) {
		Matrix l = null;
		try {
			double[][] L = new double[y.length][1];
			for (int i = 0; i < L.length; i++) {
				L[i][0] = y[i];
			}
			l = new Matrix(L);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return l;
	}

	public ArrayList<pack> pickdata(double start) {
		ArrayList<pack> result = new ArrayList<pack>();
		ArrayList<pack> continution = new ArrayList<pack>();
		try {
			int startIndex = 0;
			double dXminPoint = 100000;
			for (int i = 0; i < rawdata.size(); i++) {
				double dX = start - rawdata.get(i).getX();
				// double dX = Math.abs(start - rawdata.get(i).getX());
				// if (dX <= dXminPoint) {
				if (dX <= dXminPoint && dX > 0) {
					startIndex = i;
					dXminPoint = dX;
				}
			}
			for (int i = startIndex; i < rawdata.size() - 1; i++) {
				continution.add(rawdata.get(i));
				if (rawdata.get(i + 1).getX() - rawdata.get(i).getX() > pack.interval + 0.005) {
					ArrayList<pack> interPoints = interpolation(rawdata.get(i), rawdata.get(i + 1));
					continution.addAll(interPoints);
				}
			}
			continution.add(rawdata.get(rawdata.size() - 1));
			
			startIndex = 0;
			dXminPoint = 100000;
			for (int i = 0; i < continution.size(); i++) {
				// double dX = start - continution.get(i).getX();
				double dX = Math.abs(start - continution.get(i).getX());
				if (dX <= dXminPoint) {
				// if (dX <= dXminPoint && dX > 0) {
					startIndex = i;
					dXminPoint = dX;
				}
			}
			for (int i = startIndex; i < continution.size(); i++) {
				result.add(continution.get(i));
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	public static ArrayList<pack> interpolation(pack pointStart, pack pointEnd) {
		ArrayList<pack> points = new ArrayList<pack>();
		double gradient = (pointEnd.getY() - pointStart.getY()) / (pointEnd.getX() - pointStart.getX());
		for (int i = 1;; i++) {
			double top = pointStart.getX() + i * pack.interval;
			if (top < pointEnd.getX()) {
				BigDecimal bgx = new BigDecimal(pointStart.getX() + i * pack.interval);
				double x = bgx.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
				BigDecimal bgy = new BigDecimal(pointStart.getY() + i * pack.interval * gradient);
				double y = bgy.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
				pack newPack = new pack(x,  y);
				points.add(newPack);
			} else {
				break;
			}
		}
		return points;
	}

	public ArrayList<pack> pickdata(double start, double end) {
		ArrayList<pack> result = new ArrayList<pack>();
		try {
			for (int i = 0; i < rawdata.size(); i++) {
				if (start <= rawdata.get(i).getX()
						&& end >= rawdata.get(i).getX()) {
					result.add(rawdata.get(i));
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	public pack getimum(int xIndex) {
		pack point = new pack();
		if (x.get(xIndex).getRowDimension() - 1 == 2
				&& x.get(xIndex).get(0, 0) != 0) {
			point.setX(-1 * x.get(xIndex).get(1, 0) / 2
					/ x.get(xIndex).get(0, 0));
			point.setY(f(point.getX(), 0));
		}
		return point;
	}

	public double quadraticshape(double x0) {
		double symmetry = -1 * x.get(0).get(0, 1) / 2 / x.get(0).get(0, 0);
		double a = x.get(0).get(0, 0);
		return 1;
	}

	public double getdelta(Matrix M, Matrix N) {
		Matrix delta = M.minus(N);
		return delta.transpose().times(delta).get(0, 0);
	}

	public double getdeltaSquare(Matrix M, Matrix N) {
		try {
			if (M.getColumnDimension() == M.getRowDimension()
					&& M.getRowDimension() == N.getColumnDimension()
					&& N.getColumnDimension() == N.getRowDimension()) {
				Matrix delta = M.minus(N);
				return delta.times(delta).trace();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}

	public double min(Matrix V, Matrix P) {
		return V.transpose().times(P).times(V).get(0, 0);
	}

	public double min(Matrix V) {
		return V.transpose().times(V).get(0, 0);
	}

	public Matrix getV(Matrix B, Matrix x, Matrix l) {
		Matrix V = null;
		try {
			V = B.times(x).minus(l);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return V;
	}

	public Matrix getx(Matrix B, Matrix P, Matrix l) {
		Matrix x = null;
		Matrix BTPB = B.transpose().times(P).times(B);
		Matrix BTPl = B.transpose().times(P).times(l);
		try {
			x = BTPB.inverse().times(BTPl);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			x = new Matrix(BTPB.getRowDimension(), BTPl.getColumnDimension(),
					-1);
		}
		return x;
	}

	public Matrix getLSx(Matrix B, Matrix l) {
		Matrix x = null;
		Matrix BTB = B.transpose().times(B);
		Matrix BTl = B.transpose().times(l);
		try {
			x = BTB.inverse().times(BTl);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			// x = new Matrix(BTB.getRowDimension(), BTl.getColumnDimension(),
			// -1);
		}
		return x;
	}

	public Matrix processP(Matrix previousP, Matrix V) {
		Matrix P = previousP.copy();
		double[] v = V.getRowPackedCopy();
		for (int i = 0; i < v.length; i++) {
			v[i] = Math.abs(v[i]);
			if (v[i] < 0.0001) {
				v[i] = 0;
			}
		}
		Arrays.sort(v);
		double MAD;
		if (v.length % 2 == 0) {
			MAD = (v[v.length / 2] + v[v.length / 2 - 1]) / 2;
		} else {
			MAD = v[v.length / 2];
		}
		for (int i = 0; i < V.getRowDimension() && MAD != 0; i++) {
			P.set(i, i, P.get(i, i) * r.valueweight(V.get(i, 0), MAD));
		}
		return P;
	}

	public int paintCurve(String path, double interval) {
		String result = "";
		if (interval <= 0) {
			return 1;
		}
		if ((end - start) / interval > 1000 * 1000 * 10) {
			return 2;
		}
		for (double i = start; i <= end; i += interval) {
			result += String.format("%.3f", i) + ","
					+ String.format("%.3f", f(i, 0)) + "\n";
		}
		verify.saveparam(path, result);
		return 0;
	}
	public int display(int paint) {
		if (paint > 0) {
			try {
				paintCurve(Framework.basepath + "/function.txt", pack.interval);
				String command = "python3 " + Framework.basepath + "/fastpaint.py";
				System.out.println(command);
				Process pr = Runtime.getRuntime().exec(command);
				Thread.sleep(paint);
			} catch (InterruptedException | IOException e) {
				((Throwable) e).printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return paint;
	}

	// public pack robustestimitionoptimization(Matrix B, Matrix l) {
	// double stopcondition = 0.01;
	// Matrix P = new Matrix(B.getRowDimension(), l.getRowDimension());
	// for (int i = 0; i < P.getRowDimension(); i++) {
	// P.set(i, i, 1);
	// }
	// Matrix previousP = P.copy();
	// Matrix x = new Matrix(B.getColumnDimension(), 1, 0);
	// Matrix V;
	// for (int i = 0;; i++) {
	// x = getx(B, P, l, loss);
	// V = getV(B, x, l);
	// P = processP(P, V);
	// if (stopcondition >= getdeltaSquare(previousP, P)) {
	// break;
	// }
	// previousP = P.copy();
	// }
	// pack result = new pack();
	// result.matrix = x;
	// result.number = min(V, P);
	// return result;
	// }

	public static String pointswrite(ArrayList<pack> points) {
		String result = "";
		for (int i = 0; i < points.size(); i++) {
			result += String.format("%.2f", points.get(i).getY()) + ",";
		}
		return result;
	}

	public double evaltopratelast() {
		return evaltoprate(rawdata.get(rawdata.size() - 1).getY());
	}

	public double co(Matrix X, Matrix Y) {
		Matrix dispersionX = dispersion(X);
		Matrix dispersionXT = dispersionX.transpose();
		Matrix dispersionY = dispersion(Y);
		Matrix dispersionYT = dispersionY.transpose();
		double cov = dispersionXT.times(dispersionY).get(0, 0);
		double deltX = dispersionXT.times(dispersionX).get(0, 0);
		double deltY = dispersionYT.times(dispersionY).get(0, 0);
		double result = cov / Math.sqrt(deltX * deltY);
		return result;
	}

	public Matrix dispersion(Matrix X) {
		double average = 0;
		for (int i = 0; i < X.getRowDimension(); i++) {
			average += X.get(i, 0);
		}
		average /= X.getRowDimension();
		Matrix X_ = new Matrix(X.getRowDimension(), X.getColumnDimension());
		for (int i = 0; i < X.getRowDimension(); i++) {
			X_.set(i, 0, average);
		}
		return X.minus(X_);
	}

	public ArrayList<pack> smooth(ArrayList<pack> points, double permitcos) {
		pack vector1;
		pack vector2;
		pack point1 = points.get(0);
		pack point2 = points.get(1);
		pack point3 = points.get(2);
		double cos;
		pack[] newpoints = new pack[points.size()];
		newpoints[0] = points.get(0);
		int ptr = 1;
		for (int i = 2; i < points.size() - 1; i++) {
			vector1 = vector(point1, point2);
			vector2 = vector(point3, point2);
			cos = cos(vector1, vector2);
			if (cos < permitcos) {
				newpoints[ptr] = point2;
				++ptr;
				point1 = newpoints[ptr - 1];
				point2 = points.get(i);
				point3 = points.get(i + 1);
			} else {
				if (ptr >= 2) {
					--ptr;
					--i;
					point1 = newpoints[ptr - 1];
					point2 = newpoints[ptr];
				} else {
					return null;
				}
			}
		}
		newpoints[ptr] = point2;
		newpoints[++ptr] = point3;
		ArrayList<pack> resultList = new ArrayList<pack>();
		for (int i = 0; newpoints[i] != null; ++i) {
			resultList.add(newpoints[i]);
		}
		return resultList;
	}

	public pack vector(pack a, pack b) {
		pack vector = new pack();
		vector.x = a.x - b.x;
		vector.y = a.y - b.y;
		return vector;
	}

	public double cos(pack a, pack b) {
		double up = a.x * b.x + a.y * b.y;
		double down = Math.sqrt((a.x * a.x + a.y * a.y)
				* (b.x * b.x + b.y * b.y));
		if (down != 0) {
			return up / down;
		} else {
			return 1;
		}
	}
}
