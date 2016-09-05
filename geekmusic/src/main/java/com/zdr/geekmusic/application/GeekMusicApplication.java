package com.zdr.geekmusic.application;

import android.app.Application;

import com.zdr.geekmusic.utils.DataUtils;

/**
 * 程序在启动时初始化资源
 * Created by zdr on 16-9-3.
 */
public class GeekMusicApplication extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        DataUtils.initData(this);
    }
}
