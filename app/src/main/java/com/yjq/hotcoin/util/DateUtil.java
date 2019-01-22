package com.yjq.hotcoin.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by yjq
 * 2018/5/24.
 */

public class DateUtil {


    /**
     * 毫秒转时分秒
     * @param time
     * @return
     */
    public static String millsToDate(long time){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        Date date = new Date();
        date.setTime(time);
        return simpleDateFormat.format(date);
    }
}
