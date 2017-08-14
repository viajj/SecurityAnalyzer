import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * @author Vijaya
 *
 * DayTransaction.java
 * Class to represent one day's stock transaction information for a security.
 */
public class DayTransaction {
	
	private GregorianCalendar mDate = null;
	private double mOpen = 0.0;
	private double mHigh = 0.0;
	private double mLow = 0.0;
	private double mClose = 0.0;
	private double mVolume = 0.0;
	
	public GregorianCalendar getDate() {
		return mDate;
	}
	public void setDate(GregorianCalendar date) {
		this.mDate = date;
	}
	public double getOpen() {
		return mOpen;
	}
	public void setOpen(double open) {
		this.mOpen = open;
	}
	public double getHigh() {
		return mHigh;
	}
	public void setHigh(double high) {
		this.mHigh = high;
	}
	public double getLow() {
		return mLow;
	}
	public void setLow(double low) {
		this.mLow = low;
	}
	public double getClose() {
		return mClose;
	}
	public void setClose(double close) {
		this.mClose = close;
	}
	public double getVolume() {
		return mVolume;
	}
	public void setVolume(double volume) {
		this.mVolume = volume;
	}
	
	/**
	 * Initializes the members of the class from a string representing the
	 * stock transaction information for a day.
	 * 
	 * @param data 	String representing the stock transaction information for a day. This
	 * 				is expected to be of the form "ticker,year-month-date,open,high,low,close,volume"	
	 * @return		"true" if initialization is successful, "false" otherwise.
	 */
	public boolean initFromString(String data) {
		String[] dayValues = data.split(",");
		if (dayValues.length != 7)
		{
			System.out.println("String corresponding to DayTransaction is of invalid format");
			return false;
		}
		try {
			setDate(createCalendar(dayValues[1]));
			setOpen(Double.parseDouble(dayValues[2]));
			setHigh(Double.parseDouble(dayValues[3]));
			setLow(Double.parseDouble(dayValues[4]));
			setClose(Double.parseDouble(dayValues[5]));
			setVolume(Double.parseDouble(dayValues[6]));
		}
		catch(Exception e)
		{
			System.out.println("Could not initialize DayTransaction due to error: " + e.getMessage());
			return false;
		}
		return true;
	}
	
	/**
	 * Creates the GregorianCalendar object representing a date given the string 
	 * representing a date.
	 * 
	 * @param dateStr	String representing the data. The format is expected to be "yyyy-MM-dd".
	 * @return			The GregorianCalendarObject created from the string if 
	 */
	private GregorianCalendar createCalendar(String dateStr) {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Date date = null;
		try {
			date = df.parse(dateStr);
		} catch (ParseException e) {
			e.printStackTrace();
			System.out.println("Could not create calendar object. Error: " + e.getMessage());
			return null;
		}
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		return calendar;
	}
}
