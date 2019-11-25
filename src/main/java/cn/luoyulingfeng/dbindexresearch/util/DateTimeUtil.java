package cn.luoyulingfeng.dbindexresearch.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTimeUtil {

    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public static String format(Date date){
        return dateFormat.format(date);
    }
}
