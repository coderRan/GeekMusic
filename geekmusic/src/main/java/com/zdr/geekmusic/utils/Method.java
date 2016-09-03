package com.zdr.geekmusic.utils;

/**
 * 方法类
 * Created by zdr on 16-9-2.
 */
public class Method {

    /**
     * 返回一个时间格式 04：35
     * @param seconds 毫秒值
     * @return 格式化后的时间
     */
    public static String secondsToTime(int seconds) {
        String time;
        String minutesText = String.valueOf(seconds / 60);
        if (minutesText.length() == 1) minutesText = "0" + minutesText;

        String secondsText = String.valueOf(seconds % 60);
        if (secondsText.length() == 1) secondsText = "0" + secondsText;

        time = minutesText + ":" + secondsText;

        return time;
    }
}
