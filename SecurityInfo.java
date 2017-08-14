import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * SecurityInfo.java
 * Class to store and process data for a single security.
 *
 *
 */
public class SecurityInfo {
	
	private String mTicker;
	private ArrayList<DayTransaction> mTransactions;

	public SecurityInfo()
	{
		mTransactions = new ArrayList<DayTransaction>(); 		
	}
	
	public String getTicker() {
		return mTicker;
	}

	public void setTicker(String ticker) {
		this.mTicker = ticker;
	}
	
	public void addDayTransaction(DayTransaction transaction) {
		mTransactions.add(transaction);
	}
	
	public void displayMonthlyOpenCloseInfos() {
		double totalOpen = 0.0;
		double totalClose = 0.0;
		int count = 0;
		int curMonth = -1;
		int curYear = 0;
		System.out.println(mTicker +":");
		for (DayTransaction transaction : mTransactions)
		{
			int transactionMonth = transaction.getDate().get(Calendar.MONTH);
			if (transactionMonth != curMonth) {
				if (curMonth != -1)
				{
					displayAverageOpenCloseInfo(curYear, curMonth, totalOpen / count, totalClose / count);	
				}
				curMonth = transactionMonth;
				curYear = transaction.getDate().get(Calendar.YEAR);
				totalOpen = 0.0;
				totalClose = 0.0;
				count = 0;
			}

			totalOpen += transaction.getOpen(); 
			totalClose += transaction.getClose();
			count++;
		}
		if (curMonth != -1)
		{
			displayAverageOpenCloseInfo(curYear, curMonth, totalOpen / count, totalClose / count);	
		}
	}
	
	public void displayMaxDailyProfit() {
		double maxProfit = Double.MIN_VALUE;
		DayTransaction maxDayTransaction = null;
		for (DayTransaction transaction : mTransactions)
		{
			double dayProfit = transaction.getHigh() - transaction.getLow();
			if (maxProfit < dayProfit) {
				maxProfit = dayProfit;
				maxDayTransaction = transaction;
			}
		}
		
		String tickerStr = String.format("%-5s", getTicker());
	    String dateStr = getDayWithFormat(maxDayTransaction.getDate().getTime());
	    System.out.println(tickerStr + " " + dateStr 
							+ " " + formatPrice(maxProfit));
	}
	
	public int getNumLosingDays() {
		int count = 0;
		for (DayTransaction transaction : mTransactions)
		{
			if (transaction.getClose() < transaction.getOpen()) {
				count++;
			}
		}
		return count;
	}
	
	private void displayAverageOpenCloseInfo(int year, int month, double averageOpen, double averageClose)
	{
		System.out.println("month: " + year +"-"+String.format("%02d", month + 1)+", "+ "avg-open: "+
				formatPrice(averageOpen)+", "+"avg-close: "+formatPrice(averageClose));
	}

	public void displayBusyDays() {
		double avgVolume = getAverageVolume();
		
		System.out.println(getTicker());
		System.out.println( "Average volume: " + formatVolume(avgVolume));
		
		System.out.println( "Busy days:" );		
		for (DayTransaction transaction : mTransactions)
		{
			double volume = transaction.getVolume();
	
			if (volume > 1.1 * avgVolume)
			{
				System.out.println( getTicker() + " " + getDayWithFormat(transaction.getDate().getTime()) 
						+ " " + formatVolume(volume));
			}
		}
	}
	
	private double getAverageVolume() {
		double totalVolume = 0.0;
		int count = 0;
		for (DayTransaction transaction : mTransactions)
		{
			totalVolume += transaction.getVolume();
			count++;
		}
		return (totalVolume / count);
	}
		
	private String getDayWithFormat(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dateStr = sdf.format( date );
        return dateStr;
	}
	
	
	private String formatPrice(double price) {
		return String.format("%.2f", price);
	}
	
	private String formatVolume(double volume) {
		return String.format("%.1f", volume);
	}

}
