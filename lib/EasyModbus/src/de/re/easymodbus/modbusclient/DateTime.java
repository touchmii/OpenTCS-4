package de.re.easymodbus.modbusclient;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
/**
* Returns the Current Date and Time in different Formats.
*
* @author Stefan Rossmann
*/
public class DateTime 
{
    /**
    * Returns the current DateTime in Ticks (one ms = 10000ticks)
    * @return Current Date and Time in Ticks
    */    
	public static long getDateTimeTicks()
	{
		long TICKS_AT_EPOCH = 621355968000000000L; 
		long tick = System.currentTimeMillis()*10000 + TICKS_AT_EPOCH;
		return tick;
	}
	
    /**
    * Returns the current DateTme in String Format yyyy/MM/dd HH:mm:ss
    * @return current DateTme in String Format yyyy/MM/dd HH:mm:ss
    */    
	public static String getDateTimeString()
	{
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		return(dateFormat.format(cal.getTime()));
	}
	
    /**
    * Returns the current DateTme in String Format yyyy/MM/dd HH:mm:ss
    * @return current DateTme in String Format yyyy/MM/dd HH:mm:ss
    */    
	public static String getDateTimeStringMilliseconds()
	{
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
		Calendar cal = Calendar.getInstance();
		return(dateFormat.format(cal.getTime()));
	}
	
    /**
    * Returns the current Date in String Format yyyyMMdd
    * @return current DateTme in String Format yyyyMMdd
    */    
	public static String getDateString()
	{
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		Calendar cal = Calendar.getInstance();
		return(dateFormat.format(cal.getTime()));
	}
	
}
