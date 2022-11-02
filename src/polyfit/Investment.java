package polyfit;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import formula.number;

public class Investment {
	int id = 0;
	long timestamp = 0;
	String fund = "";
	double cost = 0;
	double NAV = 0;
	double inrates = 0;
	double stockshare = 0;
	double share = 0;
	double balance = 0;
	double cash = 0;
	int flag = 0;
	// static String balanceDir = "D:/a/temp/balance/";
	static double amount = 20000;
	static double money = 0;

	String infoString = "";

	public Investment() {

	}

	public Investment(String atrade) {
		String[] info = atrade.split(",");
		id = Integer.valueOf(info[0]);
		fund = info[2];// + "," + info[2];
		cost = Double.valueOf(info[1]);
	}

	public String printtodolist(int id) {
		String result = "";
		result += id + "," + cost + "," + fund + "(" + verify.cutDouble(inrates, 2) + ")";
		return result;
	}

	public Investment(int id, long todaytimestamp, String fund, double cost,
			double newNAV, double inrates) {
		this.id = id;
		this.fund = fund;
		this.timestamp = todaytimestamp;
		buysome(cost, newNAV, inrates);
	}

	public Investment(int id, long timestamp, String fund, double cost,
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
		return sellShare;
	}

	public double getbalancebyrate(double rate) {
		balance *= rate;
		return balance;
	}

	public static Investment load(String print) {
		String[] info = print.split(",");
		Investment aInvestment = new Investment(Integer.valueOf(info[0]),
				Long.valueOf(info[1]), info[2], Double.valueOf(info[3]),
				Double.valueOf(info[4]), Double.valueOf(info[5]),
				Double.valueOf(info[6]), Double.valueOf(info[7]),
				Double.valueOf(info[8]), Double.valueOf(info[9]));
		return aInvestment;
	}

	public static void trade(Investment trade, String info1, String info2, int type) {
		double number = Double.valueOf(trade.cost);
		if (number >= 0) {
			info2 = info2.substring(0, info2.indexOf("%"));
			buy(trade.fund, number, Double.valueOf(info1),
					Double.valueOf(info2) / 100, type);
		} else if (number < 0) {
			sell(trade.fund, number, Double.valueOf(info1), type);
		}
	}

	public String print() {
		String result = "";
		result += id + "," + timestamp + "," + fund + "," + cost + "," + NAV
				+ "," + inrates + "," + stockshare + "," + share + "," + balance + "," + cash;
		return result;
	}

	public static void sell(String aim, double share, double newNAV, int type) {
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
			if (deltaCash / deltaCost > 1.01) {
				for (int i = 0; i < investments.size(); i++) {
					investments.get(i).balance = investments.get(i).share * newNAV;
				}
				Investment.money += deltaCash;
				double marketprice = gettotalmarketprice(investments, newNAV);
				double profit = Investment.money + marketprice * 0.999;
				Investment.amount = 20000 + (profit > 0 ? profit : 0);
				System.out.println(Investment.money + " + " + marketprice + " = " + profit + " price " + newNAV + " amount " + Investment.amount);
				rewrites(aim, investments);
			}
		}
	}

	public static void buy(String aim, double cost, double newNAV,
			double inrates, int type) {
		ArrayList<Investment> investments = loads(aim);
		Investment thisInvestment = null;
		boolean alreadyhas = false;
		if (cost != 0) {
			for (int i = 0; i < investments.size(); i++) {
				thisInvestment = investments.get(i);
				if (thisInvestment.timestamp == (type == 1 ? Framework.getTodayTimestamp() : Framework.getNowTimestamp())) {
					thisInvestment.buysome(cost, newNAV, inrates);
					alreadyhas = true;
				}
			}
			if (!alreadyhas) {
				Investment aInvestment = new Investment(0,
				(type == 1 ? Framework.getTodayTimestamp() : Framework.getNowTimestamp()), aim, cost, newNAV, inrates);
				if (cost / Investment.amount > 0.001) {
					investments.add(aInvestment);
					Investment.money -= cost;
					double marketprice = gettotalmarketprice(investments, newNAV);
					double profit = Investment.money + marketprice * 0.999;
					Investment.amount = 20000 + (profit > 0 ? profit : 0);
					System.out.println(Investment.money + " + " + marketprice + " = " + profit + " price " + newNAV + " amount " + Investment.amount);
				}
			}
		}
		for (int i = 0; i < investments.size(); i++) {
			investments.get(i).stockshare = investments.get(i).share;
			investments.get(i).balance = investments.get(i).share * newNAV;
		}
		rewrites(aim, investments);
	}

	public double buysome(double cost, double newNAV, double rates) {
		this.cost += cost;
		this.NAV = newNAV;
		this.inrates = rates;
		this.share = this.cost / (1 + this.inrates) / this.NAV;
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
		rewrites(aim, investments);
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
			result += investments.get(i).share * newNAV;
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
			} else {
				result += investments.get(i).share;
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
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return investments;
	}

	public static void rewrites(String code, ArrayList<Investment> investments) {
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
				for (int i = 0; i < investments.size(); i++) {
					bw.write(investments.get(i).print() + "\n");
				}
				bw.close();
			}
		} catch (IOException e) {
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
