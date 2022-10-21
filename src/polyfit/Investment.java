package polyfit;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Investment {
	int id = 0;
	long timestamp = 0;
	String fund = "";
	double cost = 0;
	double NAV = 0;
	double inrates = 0;
	double share = 0;
	double balance = 0;
	double cash = 0;
	int flag = 0;
	// static String balanceDir = "D:/a/temp/balance/";
	static double amount = 450;

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
			double NAV, double inrates, double share, double balance,
			double cash) {
		super();
		this.id = id;
		this.timestamp = timestamp;
		this.fund = fund;
		this.cost = cost;
		this.NAV = NAV;
		this.inrates = inrates;
		this.share = share;
		this.balance = balance;
		this.cash = cash;
	}

	public double getbalancebyNAV(double newNAV) {
		return balance = share * newNAV;
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
				Double.valueOf(info[8]));
		return aInvestment;
	}

	public static void trade(Investment trade, String info1, String info2) {
		double number = Double.valueOf(trade.cost);
		if (number > 0) {
			info2 = info2.substring(0, info2.indexOf("%"));
			buy(trade.fund, number, Double.valueOf(info1),
					Double.valueOf(info2) / 100);
		} else if (number < 0) {
			sell(trade.fund, number, Double.valueOf(info1));
		}
	}

	public String print() {
		String result = "";
		result += id + "," + timestamp + "," + fund + "," + cost + "," + NAV
				+ "," + inrates + "," + share + "," + balance + "," + cash;
		return result;
	}

	public static void sell(String aim, double share, double newNAV) {
		ArrayList<Investment> investments = loads(aim);
		Investment thisInvestment = null;
		share *= -1;
		for (int i = 0; share > 0 && i < investments.size(); i++) {
			thisInvestment = investments.get(i);
			share = thisInvestment
					.sellsome(
							share,
							newNAV,
							outrates((Framework.getTodayTimestamp() - thisInvestment.timestamp) / 86400000));
		}
		rewrites(aim, investments);
	}

	public static void buy(String aim, double cost, double newNAV,
			double inrates) {
		ArrayList<Investment> investments = loads(aim);
		Investment thisInvestment = null;
		boolean alreadyhas = false;
		for (int i = 0; i < investments.size(); i++) {
			thisInvestment = investments.get(i);
			if (thisInvestment.timestamp == Framework.getTodayTimestamp()) {
				thisInvestment.buysome(cost, newNAV, inrates);
				alreadyhas = true;
			}
		}
		if (!alreadyhas) {
			Investment aInvestment = new Investment(0,
					Framework.getTodayTimestamp(), aim, cost, newNAV, inrates);
			investments.add(aInvestment);
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

	public static double gettotalshare(String aim) {
		double result = 0;
		ArrayList<Investment> investments = loads(aim);
		result = gettotalshare(investments);
		return result;
	}

	public static double gettotalshare(String aim, long start, long end) {
		double result = 0;
		ArrayList<Investment> investments = loads(aim);
		result = gettotalshare(investments, start, end);
		return result;
	}

	public static double gettotalshare(ArrayList<Investment> investments) {
		double result = 0;
		for (int i = 0; i < investments.size(); i++) {
			result += investments.get(i).share;
		}
		return result;
	}

	public static double gettotalshare(ArrayList<Investment> investments,
			long start, long end) {
		double result = 0;
		for (int i = 0; i < investments.size(); i++) {
			if (investments.get(i).timestamp <= end
					&& investments.get(i).timestamp > start) {
				result += investments.get(i).share;
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
