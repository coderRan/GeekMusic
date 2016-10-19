package com.zdr.geekmusic.application;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;

import com.zdr.geekmusic.DBUtils.MusicDao;
import com.zdr.geekmusic.DBUtils.MusicSheepDao;
import com.zdr.geekmusic.entity.MusicSheep;
import com.zdr.geekmusic.netUtils.VolleyManager;
import com.zdr.geekmusic.service.MusicPlayer;
import com.zdr.geekmusic.service.MusicService;
import com.zdr.geekmusic.utils.Constants;
import com.zdr.geekmusic.utils.DataUtils;
import com.zdr.geekmusic.utils.GlobarVar;

/**
 * 程序在启动时初始化资源
 * Created by zdr on 16-9-3.
 */
public class GeekMusicApplication extends Application {
    private MusicDao mMusicDao;
    private MusicSheepDao msd;

    @Override

    public void onCreate() {
        super.onCreate();

        //初始化volley
        VolleyManager.initVolleyManager(this);

        SharedPreferences sp = getSharedPreferences(Constants.SETTINGS_FILE, MODE_PRIVATE);

        boolean isFirst = sp.getBoolean(Constants.IS_FIRST, false);
        if (!isFirst) {
            DataUtils.initMusic(this);
            mMusicDao = MusicDao.getMusicDao(this);
            mMusicDao.addAllMusic(DataUtils.getMusics());

            firstInitMusicSheep();
            SharedPreferences.Editor editor = sp.edit();
            editor.putBoolean(Constants.IS_FIRST, true);
            editor.apply();

        }
        DataUtils.initData();
        //启动服务
        //bindService(this);

    }

    private void bindService(Context context) {
        ServiceConnection musicConn;
        Intent startMusic = new Intent(context, MusicService.class);
        SharedPreferences sp = getSharedPreferences(Constants.SETTINGS_FILE, MODE_PRIVATE);

        startMusic.putExtra("position", sp.getInt(Constants.MUSIC_INDEX, 0));
        Log.e("INDEX", sp.getInt(Constants.MUSIC_INDEX, 0) + "");
        musicConn = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                GlobarVar.setMusicPlayer((MusicPlayer) service);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
            }
        };
        context.bindService(startMusic, musicConn, BIND_AUTO_CREATE);
    }

    private void firstInitMusicSheep() {
        msd = MusicSheepDao.getmMusicSheepDao(this);
        msd.addSheep(new MusicSheep("我的收藏"));
    }

}
