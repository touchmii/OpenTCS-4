package de.re.easymodbus.modbusclient;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Stores Log Data is a File. The Data will be stored in the File logDataYYYYMMDD.txt
 * Example: logData20170403.txt
 * 
 * @author Stefan Roﬂmann
 */

public class StoreLogData 
{
	
	FileWriter fileWriter = null;
	private String filename = null;
	
	private static StoreLogData instance;

    /**
    * Default constructor
    * @throws IOException
    */  	
	private StoreLogData ()
	{

	}		
	
    /**
    * Store into LogData into File
    * @param message 	Message to write into LogFile
    */  	
	public synchronized void Store(String message)
	{ 
		try {
			fileWriter = new FileWriter(filename,true);
			while (!new File(filename).canWrite());
				fileWriter.append(DateTime.getDateTimeStringMilliseconds() + " " +message+System.lineSeparator());
			
		} catch (IOException e) 
		{
			
			e.printStackTrace();
		}
		finally
		{
			
			if (fileWriter != null)
				try {
					fileWriter.close();;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}		
	}
	
	
	/**
	* Store into Exception into File
	* @param message 	Message to write into LogFile
	* @param e Stack Trace of Exception will be stored
	*/  	
	public void Store(String message, Exception e)
	{
		e.printStackTrace();
		StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw));
		String exceptionAsString = sw.toString();
		FileWriter fileWriter = null;
		try {
			fileWriter = new FileWriter(filename,true);
			fileWriter.append(DateTime.getDateTimeStringMilliseconds() + " "+ message+" " +exceptionAsString+System.lineSeparator());
				
		} catch (IOException exc) 
		{		
			exc.printStackTrace();
		}
		finally
		{
			if (fileWriter != null)
				try {
					fileWriter.close();
					} catch (IOException exc) {
						// TODO Auto-generated catch block
						exc.printStackTrace();
					}
		}	
	}
	
	public void setFilename(String filename)
	{
		this.filename = filename;
	}
	

    /**
    * Returns the instance of the class (Singleton)
    */  
	public static synchronized StoreLogData getInstance ()
	{
		if (StoreLogData.instance == null) 
		{
			StoreLogData.instance = new StoreLogData ();
			
		}
		return StoreLogData.instance;
	}

	

}
