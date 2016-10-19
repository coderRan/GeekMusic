package com.zdr.geekmusic.utils;

import com.zdr.geekmusic.service.MusicPlayer;

/**
 * 全局变量区
 * Created by zdr on 16-9-6.
 */
public class GlobarVar {
    private static MusicPlayer musicPlayer;

    public static MusicPlayer getMusicPlayer() {
        return musicPlayer;
    }

    public static void setMusicPlayer(MusicPlayer mp) {
        musicPlayer = mp;
    }
}
