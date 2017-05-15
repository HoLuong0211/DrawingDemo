package ominext.com.drawingapplication.util;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by LuongHH on 5/12/2017.
 */

public class Utils {

    public static String getDateTimeString() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.YEAR) + calendar.get(Calendar.MONTH) + calendar.get(Calendar.DAY_OF_MONTH) +
               "_" + calendar.get(Calendar.HOUR_OF_DAY) + calendar.get(Calendar.MINUTE) + calendar.get(Calendar.SECOND);
    }
}
