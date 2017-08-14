import java.util.ArrayList;
import java.net.*;
import java.io.*;


/**
 * @author Vijaya
 * 
 * Main class StockPriceAnalyzer.java
 * 	
 * 	This class obtains historical stock price data from www.quandl.com and instantiates the 
 * 	the SecurityInfo and DayTransaction objects to store that data. 
 * 	It can perform the following kinds of analysis on the data and display the results:
 * 		Monthly average open and close prices for each security.
 * 		Day on which maximum profit could be had for each security and the profit amount.
 * 		Biggest loser: Of all securities, find the security which had the maximum number of 
 * 			days on which the closing price was lower than the open price.
 * 		Busy days: For each security, find the days on which the trading volume was 10% higher
 * 			the average volume.
 * 
 * 	The current implementation deals with the following three securities: "COF", "GOOGL", "MSFT". 
 * 	The time interval is taken to be from Currently 2017-01-01 to 2017-06-30. These can be modified.
 *
 */

public class StockPriceAnalyzer {
	static final String DATA_URL_STRING = "https://www.quandl.com/api/v3/datatables/WIKI/PRICES.csv?";
	static final String DATA_COLUMNS = "ticker,date,open,high,low,close,volume";
	static final String API_KEY = "s-GMZ_xkw6CrkGYUWs1p";

	//Jan - June of 2017
	static final String FROM_DATE = "20170101";
	static final String TO_DATE = "20170630";
	
	String[] mTickers = {"COF", "GOOGL", "MSFT"};
	String mStartDate = "20170101";
	String mEndDate = "20170630";
	ArrayList<SecurityInfo> mSecurityInfos;
	
	/**
	 * Create SecurityInfo object for each security, and initialize that object by reading the
	 * stock price data for it.  
	 */
	public void readSecurityInfos() {
		mSecurityInfos = new ArrayList<SecurityInfo>();
		for (String ticker: mTickers) {
			SecurityInfo securityInfo = new SecurityInfo();
			securityInfo.setTicker(ticker);
			readTransactionsForSecurity(securityInfo, mStartDate, mEndDate);
			mSecurityInfos.add(securityInfo);
		}
	}
	
	/**
	 * For each security, displays the average monthly open and close prices.
	 */
	public void displayMonthlyOpenCloseInfos() {
		displayQuerySeparator();
		System.out.println("Monthly average open and close prices");
		System.out.println("");
		for (SecurityInfo securityInfo: mSecurityInfos) {
			securityInfo.displayMonthlyOpenCloseInfos();
			System.out.println("");
		}
		displayQuerySeparator();
	}

	/**
	 * For each security, displays the maximum daily profit, i.e., the day on which maximum
	 * profit could be made assuming one purchased at the day's low and sold at the day’s high.
	 * Also displays the profit amount for that day.
	 */
	public void displayMaxDailyProfit() {
		displayQuerySeparator();
		System.out.println("Maximum daily profit");
		System.out.println("");
		for (SecurityInfo securityInfo: mSecurityInfos) {
			securityInfo.displayMaxDailyProfit();
		}
		displayQuerySeparator();
	}
	
	/**
	 * Out of all the securities we have the data for, finds and displays the "biggest loser", i.e.,
	 * the security which had the highest number of days on which the closing price was lower than
	 * the opening price.
	 */
	public void displayBiggestLoser() {
		displayQuerySeparator();
		System.out.println("Biggest loser");
		System.out.println("");
		SecurityInfo biggestLoser = null;
		int maxLosingDays = 0;
		for (SecurityInfo securityInfo : mSecurityInfos) {
			int numLosingDays = securityInfo.getNumLosingDays();
			if (numLosingDays > maxLosingDays)
			{
				maxLosingDays = numLosingDays;
				biggestLoser = securityInfo;
			}
		}
		if (biggestLoser != null)
		{
			System.out.println(biggestLoser.getTicker() + " " + "Number of losing days: " + maxLosingDays);
		}

		displayQuerySeparator();
	}
	
	/**
	 * Displays the "busy days" for each security. "Busy days" are taken to be the days on
	 * which the trading volume was at least 10% higher than the average volume. 
	 */
	public void displayBusyDays() {
		displayQuerySeparator();
		System.out.println("Busy days");
		System.out.println("");
		for (SecurityInfo securityInfo: mSecurityInfos) {
			securityInfo.displayBusyDays();
			System.out.println("");			
		}
		displayQuerySeparator();
	}
	
	/**
	 * Reads the historical stock price data for the specified security for the specified time interval.
	 * 
	 * @param securityInfo 	The object representing the security for which the data should be read.
	 * @param startDate 	The starting date of interval of interest.	
	 * @param endDate 		The ending date of interval of interest
	 */
	private void readTransactionsForSecurity(SecurityInfo securityInfo, String startDate, String endDate) {
		HttpURLConnection connection = null;
		try {
			String urlString = getURLString(securityInfo.getTicker(), startDate, endDate);
			URL dataUrl = new URL(urlString);
			connection = (HttpURLConnection) dataUrl.openConnection();
	        connection.setRequestMethod("GET");
	        BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
	        String line;
	        br.readLine(); // skip first line
	        while ((line = br.readLine()) != null) {
	        	DayTransaction transaction = new DayTransaction();
	        	if (transaction.initFromString(line))
	        	{
	        		securityInfo.addDayTransaction(transaction);
	        	}
	        }
	        br.close();
		} catch (IOException e) {
			System.out.println("Failed to get data for securities " + e.getMessage());
		}
		finally {
			if (connection != null) {
				connection.disconnect();
				connection = null;
			}
		}
	}
	
	
	/**
	 * Returns the URL string to fetch the data corresponding to the specified security for the 
	 * specified time interval.
	 * 
	 * @param ticker		Ticker symbol of the security of interest.
	 * @param startDate		Starting day of time interval of interest.
	 * @param endDate		Ending day of time interval of interest.
	 * @return	String 		URL string required to fetch the data for the specified security for the 
	 * 							specified time interval.
	 */
	private String getURLString(String ticker, String startDate, String endDate)
	{
		StringBuffer sb = new StringBuffer(DATA_URL_STRING);
		sb.append("date.gte=" + startDate);
		sb.append("&date.lte=" + endDate);
		sb.append("&ticker=" + ticker);
		sb.append("&qopts.columns=" + DATA_COLUMNS);
		sb.append("&api_key=" + API_KEY);
		return sb.toString();
	}
	
	private void displayQuerySeparator() {
		System.out.println("****************************************");
	}
	
	/**
	 * The main entry function. It first reads the historical stock price data for the securities
	 * and creates the objects for storing that data. It also computes and displays the average monthly
	 * open and close prices for those securities.
	 * 
	 * If any of the following command line arguments are specified, it also performs the corresponding 
	 * analysis and displays the results.
	 * 
	 * @param args			
	 * --max-daily-profit 	For each security, displays the maximum possible daily profit.
	 * --busy-day 			For each security, displays the days on which its trading volume
	 * 							was at least 10% higher than its average trading volume.
	 * --biggest-loser		Among all securities, displays the security which had the most days
	 * 							where the closing price was lower than the opening price.
	 */
	public static void main(String[] args) {
		StockPriceAnalyzer analyzer = new StockPriceAnalyzer();
		analyzer.readSecurityInfos();
		analyzer.displayMonthlyOpenCloseInfos();
		if (args != null && args.length > 0) {
			for (String arg : args) {
				if (arg.equals("--max-daily-profit")) {
					analyzer.displayMaxDailyProfit();
				}
				if (arg.equals("--busy-day")) {
					analyzer.displayBusyDays();
				}
				if (arg.equals("--biggest-loser")) {
					analyzer.displayBiggestLoser();
				}
			}
		}
	}
	
}
