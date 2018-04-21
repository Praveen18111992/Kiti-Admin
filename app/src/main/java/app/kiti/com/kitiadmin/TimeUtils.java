package app.kiti.com.kitiadmin;

import android.text.format.DateUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;


/**
 * Created by Ankit on 4/18/2018.
 */

public class TimeUtils {

    public static String getTime() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+05:30"));
        return dateFormat.format(calendar.getTime());
    }

    public static String getRelativeTime(String time) {

        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+05:30"));
            long _time = 0;
            _time = dateFormat.parse(time).getTime();
            long now = System.currentTimeMillis();
            return DateUtils.getRelativeTimeSpanString(_time, now, DateUtils.SECOND_IN_MILLIS).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static boolean isDateTimePast(String time) {

        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+05:30"));
            long _time = 0;
            _time = dateFormat.parse(time).getTime();
            long now = System.currentTimeMillis();
            return now > _time;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;

    }

    public static long getMillisFrom(String time) {

        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+05:30"));
            long _time = 0;
            _time = dateFormat.parse(time).getTime();
            return _time;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;

    }

    private static String getTimeFromMillis(long millisTime) {

        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millisTime);
        return  formatter.format(calendar.getTime());

    }

    public static String getDateFrom(long millis) {

        DateFormat formatter = new SimpleDateFormat("dd");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);
        return formatter.format(calendar.getTime());

    }

    public static String getMonthFrom(long millis){

        DateFormat formatter = new SimpleDateFormat("MMMM");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);
        return formatter.format(calendar.getTime());

    }

    public static String getYearFrom(long millis){

        DateFormat formatter = new SimpleDateFormat("yyyy");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);
        return formatter.format(calendar.getTime());

    }

}
