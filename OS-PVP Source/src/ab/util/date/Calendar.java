package ab.util.date;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import ab.Server;
/**
 * The purpose of this class is to represent a current date in time, and request
 * for it to be updated every certain amount of cycles. 
 * 
 * @author Jason MacKeigan
 * @date Oct 26, 2014, 10:57:39 PM
 */
public class Calendar {
	
	/**
	 * The current date, represented with the use of the DateFormat class 
	 */
	private java.util.Calendar date = java.util.Calendar.getInstance();

	/**
	 * Representation of the current date
	 */
	DateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	
	/**
	 * The time zone of the calendar
	 */
	TimeZone timeZone = TimeZone.getTimeZone("GMT-4:00");
	
	/**
	 * Constructs a new calendar and tells it to automatically update however
	 * often we need it to.
	 * @param cycles the number of cycles
	 */
	public Calendar(SimpleDateFormat formatter) {
		this.formatter = formatter;
		this.date.setTimeZone(timeZone);
		this.formatter.setTimeZone(timeZone);
	}
	
	/**
	 * Requests that the calender be updated immedietly
	 */
	public void requestUpdate() {
		date = getInstance();
	}
	
	public static java.util.Calendar getFutureDate(int dayOfWeek, int hourOfDay, int minute) {
		java.util.Calendar tempDate = Server.getCalendar().getInstance();
		tempDate.set(java.util.Calendar.DAY_OF_WEEK, dayOfWeek);
		tempDate.set(java.util.Calendar.HOUR_OF_DAY, hourOfDay);
		tempDate.set(java.util.Calendar.MINUTE, minute);
		return tempDate;
	}
	
	/**
	 * Returns the date representation in years, months, and days
	 * @return the date in years, months, and days
	 */
	public String getYMD() {
		date = getInstance();
		return reformat(new SimpleDateFormat("yyyy/MM/dd"));
	}
	
	/**
	 * Returns the date representation in hours, minutes, and seconds
	 * @return the hour, minute and second of the day
	 */
	public String getHMS() {
		date = getInstance();
		return reformat(new SimpleDateFormat("HH:mm:ss"));
	}
	
	/**
	 * Returns the day of the month in the form of a string
	 * @return	the day
	 */
	public String getDay() {
		return Integer.toString(date.get(java.util.Calendar.DAY_OF_MONTH));
	}
	
	/**
	 * Returns a string representation of the current date and time
	 * @return the formatted date and time
	 */
	@Override
	public String toString() {
		date = getInstance();
		return formatter.format(date.getTime());
	}
	
	/**
	 * Returns a new unfamiliar representation of the date
	 * @param formatter the format of the date
	 * @return the date reformatted
	 */
	public String reformat(SimpleDateFormat formatter) {
		date = getInstance();
		return formatter.format(date.getTime());
	}
	
	/**
	 * Returns the date object for this calendar
	 * @return the date
	 */
	public java.util.Calendar getInstance() {
		date = java.util.Calendar.getInstance(timeZone);
		return date;
	}
	
	/**
	 * Returns the time zone that the server abides by
	 * @return
	 */
	public TimeZone getTimeZone() {
		return timeZone;
	}

}
