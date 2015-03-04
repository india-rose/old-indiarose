import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;


public class MainProgram {

	public static void main(String[] args) {
		long timestamp = System.currentTimeMillis();
		
		System.out.println("Timestamp = " + timestamp);
		
		Calendar calendar = Calendar.getInstance();
		TimeZone tz = TimeZone.getDefault();
		calendar.add(Calendar.MILLISECOND, tz.getOffset(timestamp));
		
		Date date = calendar.getTime();

		System.out.println("Calendar = " + calendar.getTime());
		
		//2015-02-05T15:47:13.965+01:00
		SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
		SimpleDateFormat timeZoneFormater = new SimpleDateFormat("ZZZ");
		
		String timeZoneResult = timeZoneFormater.format(date);
		String timeZoneFirst = timeZoneResult.substring(0, 3);
		String timeZoneSecond = timeZoneResult.substring(3, 5);
		
		System.out.println("Formatted: " + formater.format(date) + timeZoneFirst + ":" + timeZoneSecond);
	}

}
