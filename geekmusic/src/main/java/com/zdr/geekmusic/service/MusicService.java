package com.zdr.geekmusic.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.util.Log;

import com.zdr.geekmusic.entity.Music;


/**
 * 后台播放音乐的服务
 */
public class MusicService extends Service {
    private MediaPlayer player;
    private Music music;
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return super.onStartCommand(intent, flags, startId);

    }
    @NonNull
    @Override
    public IBinder onBind(Intent intent) {
        Log.e("onBind", intent.getStringExtra("path"));
        //启动播放音乐
        //music = (Music) (intent.getExtras().get("music"));
        player = MediaPlayer.create(this, Uri.parse(intent.getStringExtra("path")));
        //TODO 实现播放下一曲功能
        //player.setNextMediaPlayer();
        player.setLooping(true);
        return new MusicServiceBind();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        //当启动该服务的activity被销毁时，会调用onUnbind
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        //释放音乐资源
        //player.release();
        super.onDestroy();
    }

    private class MusicServiceBind extends Binder implements MusicPlayer {
        @Override
        public void play() {
            player.start();
        }

        @Override
        public void pause() {
            player.pause();
        }

        @Override
        public void seekTo(int position) {
            player.seekTo(position);
        }

        @Override
        public int getCurrentPosition() {
            return player.getCurrentPosition();
        }
    }

}
