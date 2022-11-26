package polyfit;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import formula.number;

public class Investment {
	String id = "0";
	long timestamp = 0;
	String fund = "";
	String remark = "";
	double cost = 0;
	double NAV = 0;
	double inrates = 0;
	double stockshare = 0;
	double thiscount = 0;
	double share = 0;
	double balance = 0;
	double cash = 0;
	int flag = 0;
	// static String balanceDir = "D:/a/temp/balance/";
	static double amount = 20000;
	static double money = 0;

	String infoString = "";
	static String xchangeout = Framework.getPath("coin", "xchangeout", "");
	static String xchangein = Framework.getPath("coin", "xchangein", "");

	public Investment() {

	}

	public Investment(String atrade) {
		String[] info = atrade.split(",");
		id = info[0];
		fund = info[2];// + "," + info[2];
		cost = Double.valueOf(info[1]);
		remark = info[3];
	}

	public String printtodolist(String id) {
		String result = "";
		result += id + "," + cost + "," + fund + " " + verify.cutDouble(inrates, 3);
		return result;
	}

	public Investment(String id, long todaytimestamp, String fund, double cost,
			double newNAV, double inrates) {
		this.id = id;
		this.fund = fund;
		this.timestamp = todaytimestamp;
		buysome(cost, newNAV, inrates);
	}

	public Investment(String id, long timestamp, String fund, double cost,
			double NAV, double inrates, double stockshare, double share, double balance,
			double cash) {
		super();
		this.id = id;
		this.timestamp = timestamp;
		this.fund = fund;
		this.cost = cost;
		this.NAV = NAV;
		this.inrates = inrates;
		this.stockshare = stockshare;
		this.share = share;
		this.balance = balance;
		this.cash = cash;
	}

	public double getbalancebyNAV(double newNAV) {
		return balance = share * newNAV;
	}
	public double sellSharebyRate(double rate) {
		double sellShare = stockshare * rate;
		sellShare = sellShare - (stockshare - share);
		if (sellShare < 0) {
			sellShare = 0;
		}
		thiscount = sellShare;
		return thiscount; // sellshare - realsellshare;
	}

	public double getbalancebyrate(double rate) {
		balance *= rate;
		return balance;
	}

	public static Investment load(String print) {
		String[] info = print.split(",");
		Investment aInvestment = new Investment(info[0],
				Long.valueOf(info[1]), info[2], Double.valueOf(info[3]),
				Double.valueOf(info[4]), Double.valueOf(info[5]),
				Double.valueOf(info[6]), Double.valueOf(info[7]),
				Double.valueOf(info[8]), Double.valueOf(info[9]));
		return aInvestment;
	}

	public static void trade(Investment trade, String info1, String info2, int type, boolean online) {
		double number = Math.round(Double.valueOf(trade.cost) * 10) / 10.0;
		if (number >= 0) {
			info2 = info2.substring(0, info2.indexOf("%"));
			buy(trade.fund + "," + trade.id, number, Double.valueOf(info1),
					Double.valueOf(info2) / 100, type, trade.remark, online);
		} else if (number < 0) {
			sell(trade.fund, number, Double.valueOf(info1), type, online);
		}
	}

	public String print() {
		String result = "";
		result += id + "," + timestamp + "," + fund + "," + cost + "," + NAV
				+ "," + inrates + "," + stockshare + "," + (share < 0.000000000001 ? 0 : share) + "," + balance + "," + cash;
		return result;
	}

	public static void sell(String aim, double share, double newNAV, int type, boolean online) {
		ArrayList<Investment> investments = loads(aim);
		Investment thisInvestment = null;
		share *= -1;
		if (share != 0.000000001) {			
			double preCost = gettotalcost(investments);
			double preCash = gettotalcash(investments);
			if (type == 1) {
				for (int i = 0; share > 0 && i < investments.size(); i++) {
					thisInvestment = investments.get(i);
					share = thisInvestment
							.sellsome(
									share,
									newNAV,
									type == 1 ? outrates((Framework.getTodayTimestamp() - thisInvestment.timestamp) / 86400000) : 0.001);
				}
			} else if (type == 0){
				for (int i = 0; i < investments.size(); i++) {
					thisInvestment = investments.get(i);
					thisInvestment
							.sellsome(
									thisInvestment.sellSharebyRate(share),
									newNAV,
									type == 1 ? outrates((Framework.getTodayTimestamp() - thisInvestment.timestamp) / 86400000) : 0.001);
				}
			}
			double nowCost = gettotalcost(investments);
			double nowCash = gettotalcash(investments);
			double deltaCost = preCost - nowCost;
			double deltaCash = nowCash - preCash;
			deltaCost = deltaCost == 0 ? 1 : deltaCost;
			String path = Framework.getPath("coin", "paint", "processInfo");
			JSONObject param = verify.loadObject(path);
			//path = Framework.getPath("coin", "paint", "listInfo");
			JSONArray coinsInfo = param.getJSONArray("coins");
			path = Framework.getPath("coin", "paint", "list");
			JSONArray coins = verify.loadArray(path);
			//param.put("coins", list);
			double bar = param.optDouble("bar");
			Double stocktotalshare = Investment.gettotalshare(investments, -1);
			double realshare = stocktotalshare; // * share; // Math.round(stocktotalshare * share * 10000000) / 10000000.0;
			if (deltaCash / deltaCost > bar && realshare > 0.00001) {
				Investment trade = new Investment();
				trade.fund = aim;
				trade.cost = -realshare;
				trade.infoString = newNAV + "";
				String actionPath = Framework.getPath("coin", "paint", aim + "_moneyRecord" + "-" + Framework.gettodaydate() + (Framework.ifback == 1 ? "-S" : "") + Framework.suffix);
				String tradeString = trade.marketOrderItem();
				if (Framework.ifback == 0) {
					String serialno = verify.saveparam(xchangeout + "/" + tradeString, "");					
				}
				String id = tradeString.split(",")[0];
				boolean goon = false;
				int cycTimes = 0;
				String[] reply = null;
				String returnString = "";
				boolean out = false;
				for (int i = 1; Framework.ifback == 0 && i <= 10; i++) {
					try {
						Thread.sleep(100);
						File dir = new File(xchangein);
						File[] files = dir.listFiles();
						for (int j = 0; j < files.length; j++) {
							returnString = files[j].getName();
							reply = returnString.split(",");
							String ID = reply[0];
							if (id.equals(ID)) {
								if ((Framework.getNowTimestamp() - new Long(ID) < 5000)) {
									if ("0".equals(reply[3])) {
										goon = true;
										newNAV = Double.valueOf(reply[2]);											
									}									
									tradeString += "," + reply[3] + "," + reply[4];
								} else {
									tradeString += "," + reply[3] + "," + reply[4] + " butexceed5s";
								}
								Thread.sleep(100);
								files[j].delete();
								out = true;
								break;
							}
						}
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						String emsg = e.getMessage();
						tradeString += ",-1" + "," + emsg.substring(0, emsg.indexOf("\n"));
						out = true;
					}
					if (!goon) {
						cycTimes = i;						
					}
					if (out) {
						break;
					}
				}
				if (goon || Framework.ifback == 1) {
					boolean fold = false;
					/*for (int i = 0; i < investments.size(); i++) {
						investments.get(i).balance = investments.get(i).share * newNAV;
					}*/
					double marketprice = gettotalmarketprice(investments, newNAV);
					if (online) {
						//JSONArray coins = param.getJSONArray("coins");
						for (int i = 0; i < coins.length(); i++) {
							JSONObject record = coins.getJSONObject(i);
							if (aim.equals(record.getString("type"))) {
								JSONObject recordInfo = null;
								for (int j = 0; j < coinsInfo.length(); j++) {
									recordInfo = coinsInfo.getJSONObject(j);
									if (aim.equals(recordInfo.getString("type"))) {
										break;
									}
								}
								fold = recordInfo.getBoolean("fold");
								int paint = record.getInt("paint");
								if (paint > 0) {
									paint -= 1;
									record.put("paint", paint);
								}
								double money = record.getDouble("cost");
								double handrate = (1 - recordInfo.getDouble("inrates"));
								double realDeltaCash = newNAV * realshare * handrate;
								money += realDeltaCash;
								double diff = realDeltaCash - deltaCash;
								if (diff > 5) {
									System.out.println("strange!");
								}
								investments.get(0).cash += diff;
								record.put("cost", money);
								record.put("NAV", newNAV);
								record.put("balance", marketprice);
								double profit = money + marketprice * handrate;
								record.put("cash", profit);
								double start = recordInfo.getDouble("start");
								double amount = record.getDouble("amount");
								double nowAmount = start + (profit > 0 ? profit / recordInfo.getDouble("flag") : 0);
								record.put("amount", amount > nowAmount ? amount : nowAmount);
								coins.put(i, record);
								verify.saveparam(path, coins.toString());
								verify.appenddata(Framework.getPath("coin", "paint", "history" + "-" + Framework.gettodaydate() + (Framework.ifback == 1 ? "-S" : "") + Framework.suffix), coins.toString() + "\n");
								System.out.println(record.toString());
								break;
							}
						}
						/*Investment.money += deltaCash;
						double marketprice = gettotalmarketprice(investments, newNAV);
						double profit = Investment.money + marketprice * 0.999;
						double nowAmount =  20000 + (profit > 0 ? profit / 40 : 0);
						Investment.amount = Investment.amount > nowAmount ? Investment.amount : nowAmount;
						System.out.println(Investment.money + " + " + marketprice + " = " + profit + " price " + newNAV + " amount " + Investment.amount);
						*/
					}
					rewrites(aim, investments, fold);
				} else {
					if (cycTimes == 10) {
						tradeString += ",-2" + "," + "timeout";
					} else {
						if (cycTimes < 10) {
							tradeString += ",-9" + "," + "request but takeout";
						}
						if (!out) {
							tradeString += ",-10" + "," + "unknow";
						}
					}
				}
				verify.appenddata(actionPath, tradeString + "\n");
			} else {
				System.out.println("deltaCash / deltaCost = " + verify.cutDouble((deltaCash / deltaCost), 6));
			}
		}
	}

	public String marketOrderItem() {
		id = "" + Framework.getNowTimestamp();
		String orderString = "";
		String side = "";
		if (cost < 0) {
			side = "sell";
			cost = -cost;
		} else {
			side = "buy";
		}
		orderString += id + ",";//clOrdId 0
		orderString += fund + ",";//instId 1
		orderString += "cash,";//stdMode 2
		orderString += side + ",";//side 3
		orderString += "market,";//ordType 4
		String temporary = String.valueOf(cost);
		BigDecimal bigDecimal = new BigDecimal(temporary);
		String result = String.valueOf(bigDecimal);
		orderString += result + ",";//sz 5
		orderString += ",";//ccy 6
		orderString += infoString + ",";//tag 6
		orderString += ",";//posSide 7
		orderString += ",";//px 8
		orderString += ",";//reduceOnly 8
		orderString += ",";//tgtCcy 9
		orderString += "";//banAmend 10
		return orderString;
	}

	public static void buy(String aim, double cost, double newNAV,
			double inrates, int type, String remark, boolean online) {
		String[] info = aim.split(",");
		aim = info[0];
		ArrayList<Investment> investments = loads(aim);
		Investment thisInvestment = null;
		boolean alreadyhas = false;
		boolean fold = false;
		if (cost != 0) {
			for (int i = 0; i < investments.size(); i++) {
				thisInvestment = investments.get(i);
				if (thisInvestment.timestamp == (type == 1 ? Framework.getTodayTimestamp() : Framework.getNowTimestamp())) {
					thisInvestment.buysome(cost, newNAV, inrates);
					alreadyhas = true;
				}
			}
			if (!alreadyhas) {
				Investment aInvestment = null;
				if (Framework.ifback == 1) {
					aInvestment = new Investment(info.length > 1 ? info[1] : "0",
					(type == 1 ? Framework.getTodayTimestamp() : Framework.getNowTimestamp()), aim + " " + remark, cost, newNAV, inrates);
				}
				String path = Framework.getPath("coin", "paint", "processInfo");
				JSONObject param = verify.loadObject(path);
				JSONArray coinsInfo = param.getJSONArray("coins");
				path = Framework.getPath("coin", "paint", "list");
				JSONArray coins = verify.loadArray(path);
				//param.put("coins", list);
				double open = param.optDouble("open");
				if (cost > open) {
					Investment trade = new Investment();
					trade.fund = aim;
					trade.cost = cost;
					trade.infoString = newNAV + "";
					String actionPath = Framework.getPath("coin", "paint", aim + "_moneyRecord" + "-" + Framework.gettodaydate() + (Framework.ifback == 1 ? "-S" : "") + Framework.suffix);
					String tradeString = trade.marketOrderItem();
					String id = tradeString.split(",")[0];
					if (Framework.ifback == 0) {
						String serialno = verify.saveparam(xchangeout + "/" + tradeString, "");					
					}
					boolean goon = false;
					int cycTimes = 0;
					String[] reply = null;
					String returnString = "";
					boolean out = false;
					for (int i = 1; Framework.ifback == 0 && i <= 10; i++) {
						try {
							Thread.sleep(100);
							File dir = new File(xchangein);
							File[] files = dir.listFiles();
							for (int j = 0; j < files.length; j++) {
								returnString = files[j].getName();
								reply = returnString.split(",");
								String ID = reply[0];
								if (id.equals(ID)) {
									if ((Framework.getNowTimestamp() - new Long(ID) < 5000)) {
										if ("0".equals(reply[4])) {
											goon = true;
											if ("".equals(reply[2])) {
												System.out.println("strange!");
											}
											newNAV = Double.valueOf(reply[2]);
											double shareFill = Double.valueOf(reply[3]);
											aInvestment = new Investment(info.length > 1 ? info[1] : "0",
											(type == 1 ? Framework.getTodayTimestamp() : Framework.getNowTimestamp()), aim + " " + remark, cost, newNAV, inrates);
											aInvestment.share = shareFill;
											investments.add(aInvestment);
										}
										tradeString += "," + reply[4] + "," + reply[5];
									} else {
										tradeString += "," + reply[4] + "," + reply[5] + " butexceed5s";
									}
									Thread.sleep(200);
									files[j].delete();
									out = true;
									break;
								}
							}
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							String emsg = e.getMessage();
							tradeString += ",-1" + "," + emsg.substring(0, emsg.indexOf("\n"));
							out = true;
						}
						if (!goon) {
							cycTimes = i;						
						}
						if (out) {
							break;
						}
					}
					if (goon || Framework.ifback == 1) {
						if (Framework.ifback == 1 && aInvestment != null) {
							investments.add(aInvestment);
						}
						double marketprice = gettotalmarketprice(investments, newNAV);
						for (int i = 0; i < coins.length(); i++) {
							JSONObject record = coins.getJSONObject(i);
							if (aim.equals(record.getString("type"))) {
								JSONObject recordInfo = null;
								for (int j = 0; j < coinsInfo.length(); j++) {
									recordInfo = coinsInfo.getJSONObject(j);
									if (aim.equals(recordInfo.getString("type"))) {
										break;
									}
								}
								fold = recordInfo.getBoolean("fold");
								int paint = record.getInt("paint");
								if (paint > 0) {
									paint -= 1;
									record.put("paint", paint);
								}
								double money = record.getDouble("cost");
								money -= cost;
								record.put("cost", money);
								record.put("NAV", newNAV);
								record.put("balance", marketprice);
								double profit = money + marketprice * (1 - inrates);
								record.put("cash", profit);
								double start = recordInfo.getDouble("start");
								double amount = record.getDouble("amount");
								double nowAmount = start + (profit > 0 ? profit / recordInfo.getDouble("flag") : 0);
								record.put("amount", amount > nowAmount ? amount : nowAmount);
								coins.put(i, record);
								verify.saveparam(path, coins.toString());
								verify.appenddata(Framework.getPath("coin", "paint", "history" + "-" + Framework.gettodaydate() + (Framework.ifback == 1 ? "-S" : "") + Framework.suffix), coins.toString() + "\n");
								System.out.println(record.toString());
								break;
							}
						}
					} else {
						if (cycTimes == 10) {
							tradeString += ",-2" + "," + "timeout";
						} else {
							if (cycTimes < 10) {
								tradeString += ",-9" + "," + "success but takeout";
							}
							if (!out) {
								tradeString += ",-10" + "," + "unknow";
							}
						}
					}
					verify.appenddata(actionPath, tradeString + "\n");
						
						/*Investment.money -= cost;
						double marketprice = gettotalmarketprice(investments, newNAV);
						double profit = Investment.money + marketprice * 0.999;
						double nowAmount =  20000 + (profit > 0 ? profit / 40 : 0);
						Investment.amount = Investment.amount > nowAmount ? Investment.amount : nowAmount;
						System.out.println(Investment.money + " + " + marketprice + " = " + profit + " price " + newNAV + " amount " + Investment.amount);
						*/
				}
			}
		}
		for (int i = 0; i < investments.size(); i++) {
			investments.get(i).stockshare = investments.get(i).share;
			investments.get(i).balance = investments.get(i).share * newNAV;
		}
		rewrites(aim, investments, fold);
	}

	public double buysome(double cost, double newNAV, double rates) {
		this.cost += cost;
		this.NAV = newNAV;
		this.inrates = rates;
		this.share = Math.round(this.cost / (1 + this.inrates) / this.NAV * 100000000) / 100000000.0;
		this.balance = this.share * this.NAV;
		return share;
	}

	public double sellsome(double sellshare, double newNAV, double rates) {
		double realsellshare = sellshare;
		if (share >= realsellshare) {
			share -= realsellshare;
		} else {
			realsellshare = share;
			share = 0;
		}
		cash += realsellshare * newNAV * (1 - rates);
		balance = share * newNAV;
		return sellshare - realsellshare;
	}

	public static void refresh(String aim, double newNAV) {
		// String path = Framework.getPath("balance", "balance", aim);
		ArrayList<Investment> investments = loads(aim);
		Investment thisInvestment = null;
		for (int i = 0; i < investments.size(); i++) {
			thisInvestment = investments.get(i);
			thisInvestment.getbalancebyNAV(newNAV);
		}
		rewrites(aim, investments, false);
	}

	public static Investment calculate(String aim) {
		String realPath = "path" + aim;
		Investment result = new Investment();
		ArrayList<Investment> investments = loads(realPath);
		Investment thisInvestment = null;
		for (int i = 0; i < investments.size(); i++) {
			thisInvestment = investments.get(i);
			result.add(thisInvestment);
		}
		return result;
	}

	public static double gettotalshare(String aim, int type) {
		double result = 0;
		ArrayList<Investment> investments = loads(aim);
		result = gettotalshare(investments, type);
		return result;
	}

	public static double gettotalshare(String aim, long start, long end) {
		double result = 0;
		ArrayList<Investment> investments = loads(aim);
		result = gettotalshare(investments, start, end);
		return result;
	}

	public static double gettotalcost(ArrayList<Investment> investments) {
		double result = 0;
		for (int i = 0; i < investments.size(); i++) {
			result += investments.get(i).share * investments.get(i).NAV * (1 + investments.get(i).inrates);
		}
		return result;
	}
	public static double gettotalmarketprice(ArrayList<Investment> investments, double newNAV) {
		double result = 0;
		for (int i = 0; i < investments.size(); i++) {
			investments.get(i).balance = investments.get(i).share * newNAV;
			result += investments.get(i).balance;
		}
		return result;
	}
	public static double gettotalcash(ArrayList<Investment> investments) {
		double result = 0;
		for (int i = 0; i < investments.size(); i++) {
			result += investments.get(i).cash;
		}
		return result;
	}
	public static double gettotalshare(ArrayList<Investment> investments, int type) {
		double result = 0;
		for (int i = 0; i < investments.size(); i++) {
			if (type == 0) {
				result += investments.get(i).stockshare;
			} else if (type == 1){
				result += investments.get(i).share;
			} else {
				result += investments.get(i).thiscount;
			}
		}
		return result;
	}

	public static double gettotalshare(ArrayList<Investment> investments,
			long start, long end) {
		double result = 0;
		for (int i = 0; i < investments.size(); i++) {
			if (investments.get(i).timestamp <= end
					&& investments.get(i).timestamp > start) {
				result += investments.get(i).stockshare;
			}
		}
		return result;
	}

	public static ArrayList<Investment> loads(String code) {
		ArrayList<Investment> investments = new ArrayList<Investment>();
		try {
			String path = Framework.getPath("balance", "balance", code);// balanceDir
																		// +
																		// code
																		// +
																		// ".txt";
			if (new File(path).exists()) {
				BufferedReader br = new BufferedReader(new FileReader(path));
				String aline = null;
				for (int i = 0; (aline = br.readLine()) != null; i++) {
					Investment aInvestment = Investment.load(aline);
					investments.add(aInvestment);
				}
				br.close();
				Thread.sleep(5);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return investments;
	}

	public static void rewrites(String code, ArrayList<Investment> investments, boolean fold) {
		double test = gettotalshare(investments, 1);
		System.out.println("totalshare: " + test);
		try {
			if (investments.size() > 0) {
				String path = Framework.getPath("balance", "balance", code);// balanceDir
																			// +
																			// code
																			// +
																			// ".txt";
				File file = new File(path);
				if (!file.exists()) {
					file.createNewFile();
				}
				BufferedWriter bw = new BufferedWriter(new FileWriter(
						file.getAbsoluteFile()));
				Investment assemble = null;
				if (fold) {
					assemble = new Investment();
				}
				int num = 0;
				for (int i = 0; i < investments.size(); i++) {
					if (fold) {
						if (investments.get(i).balance == 0) {
							//result += id + "," + timestamp + "," + fund + "," + cost + "," + NAV
							//		+ "," + inrates + "," + stockshare + "," + (share < 0.00000001 ? 0 : share) + "," + balance + "," + cash;
							assemble.id = investments.get(i).id;
							assemble.timestamp = investments.get(i).timestamp;
							assemble.fund = investments.get(i).fund;
							assemble.cost += investments.get(i).cost;
							assemble.NAV = investments.get(i).NAV;
							assemble.inrates = investments.get(i).inrates;
							assemble.stockshare = investments.get(i).stockshare;
							assemble.share = investments.get(i).share;
							assemble.balance = investments.get(i).balance;
							assemble.cash += investments.get(i).cash;
							num++;
						} else {
							if (i != 0) {
								bw.write(assemble.print() + "\n");
							}
							bw.write(investments.get(i).print() + "\n");
							fold = false;
						}
					} else {
						bw.write(investments.get(i).print() + "\n");
					}
				}
				if (num == investments.size()) {
					bw.write(assemble.print() + "\n");	
				}
				bw.close();
				Thread.sleep(5);
			}
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static double outrates(long days) {
		if (days < 7) {
			return 1.5 / 100;
		} else if (days < 30) {
			return 0.5 / 100;
		} else {
			return 0;
		}
	}

	public Investment add(Investment other) {
		cost += other.cost;
		share += other.share;
		balance += other.balance;
		cash += other.cash;
		return this;
	}
}
