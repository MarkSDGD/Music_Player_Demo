package com.haikan.sport.musicplayer.utils;

/**
 * Created by admin on 2019/4/17.
 * <p>Copyright 2019 SDGD.</p>
 */
public class DateUtils {
    // 转换歌曲时间的格式


    public static String formatTime(int time) {
        if (time / 1000 % 60 < 10) {
            String tt = time / 1000 / 60 + ":0" + time / 1000 % 60;
            return tt;
        } else {
            String tt = time / 1000 / 60 + ":" + time / 1000 % 60;
            return tt;
        }
    }

}
