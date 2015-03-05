
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;


public class MainProgram {

	public static void main(String[] args) {
		long timestamp = 1425571996905L;
		long timestamp2 = 1425572012246L;
		
		System.out.println("T1");
		format(timestamp);
		System.out.println();
		System.out.println("T2");
		format(timestamp2);
	}
	
	public static void format(long timestamp)
	{
		System.out.println("Timestamp = " + timestamp);
		
		Timestamp ts = new Timestamp(timestamp);
		
		//2015-02-05T15:47:13.965+01:00
		SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
		SimpleDateFormat timeZoneFormater = new SimpleDateFormat("ZZZ");
		
		String timeZoneResult = timeZoneFormater.format(ts);
		String timeZoneFirst = timeZoneResult.substring(0, 3);
		String timeZoneSecond = timeZoneResult.substring(3, 5);
		
		System.out.println("Formatted: " + formater.format(ts) + timeZoneFirst + ":" + timeZoneSecond);
	}

}
