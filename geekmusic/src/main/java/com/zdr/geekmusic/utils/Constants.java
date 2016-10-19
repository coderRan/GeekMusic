package com.zdr.geekmusic.utils;

/**
 * 常量字段类
 * Created by zdr on 16-9-3.
 */
public class Constants {
    public static final String IS_FIRST = "isFirst";
    public static final int NOTIFICTION_CODE = 1;
    //单曲循环
    public static final int PLAY_MODE_SINGLE = 1;
    //列表循环
    public static final int PLAY_MODE_ALL_REPEAT = 2;
    //随机播放
    public static final int PLAY_MODE_SHUFFLE = 3;
    //sp文件
    public static final String SETTINGS_FILE = "settings";
    public static final String PLAY_MODE = "playMode";
    public static final String MUSIC_INDEX = "musicIndex";

    public static String API_KEY = "6b68bca2d6662442fb1103b2427b4fed";

    //数据库字段
    public static final String DB_NAME = "geekMusic.db";
    public static final int DB_VERSION = 1;

    public static class MusicTable{
        public static final String ID = "id";

    }

    public static class KVSheepTable{
        public static final String SHEEP_ID = "sheepId";
        public static final String MUSIC_ID = "musicId";

    }
}
