package com.zdr.geekmusic.service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.util.Log;

import com.zdr.geekmusic.entity.Music;
import com.zdr.geekmusic.utils.Constants;
import com.zdr.geekmusic.utils.DataUtils;

import java.io.IOException;


/**
 * 后台播放音乐的服务
 */
public class MusicService extends Service{
    private MediaPlayer player;
    protected Music music;
    private int musicIndex;
    private Updata updata;
    private int playMode;
    private SharedPreferences sp;
    private void initMeidaPlayer(){

        if(player == null){
            player = new MediaPlayer();
            try {
                player.setDataSource(this,Uri.parse(music.getPath()));
                player.prepare();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }else {
            player.release();
            player.reset();
            try {
                player.setDataSource(this,Uri.parse(music.getPath()));
                player.prepare();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                moveToNext();
            }
        });
        player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                //player.start();
            }
        });
        player.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
            @Override
            public void onSeekComplete(MediaPlayer mp) {

            }
        });
        player.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                return false;
            }
        });
    }

    @Override
    public void onCreate() {
        Log.e("onCreate",  "onCreat 执行");
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("onStartCommand",  "onStartCommand 执行");
        return super.onStartCommand(intent, flags, startId);
    }

    @NonNull
    @Override
    public IBinder onBind(Intent intent) {
        Log.e("onBind", intent.getExtras().getInt("position") + "");
        //启动播放音乐
        musicIndex = intent.getExtras().getInt("position");
        music = DataUtils.getMusics().get(musicIndex);
        sp = getSharedPreferences(Constants.SETTINGS_FILE, MODE_PRIVATE);
        playMode = sp.getInt(Constants.PLAY_MODE, Constants.PLAY_MODE_ALL_REPEAT);
        initMeidaPlayer();
        //TODO 实现播放下一曲功能
        //player.setNextMediaPlayer();
        //nextPlayer = createNextPlayer(intent.getExtras().getInt("playMode"));
        //player.setNextMediaPlayer(nextPlayer);

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
            if(player!=null)
                player.start();
        }
        @Override
        public void pause() {
            if(player!=null)
                player.pause();
        }

        @Override
        public void seekTo(int position) {
            if(player!=null)
                player.seekTo(position);
        }

        @Override
        public int getCurrentPosition() {
            Log.e("CurrentPosition", player.getCurrentPosition() + "");
            if(player!=null)
                return player.getCurrentPosition();
            return -1;
        }

        @Override
        public void setUpdataListener(Updata updata) {
            setUIListener(updata);
        }

        @Override
        public void setPlayMode(int mode) {
            Log.e("MODE", mode + "");
            playMode = mode;
        }

        @Override
        public void nextMusic() {
            moveToNext();
        }

        @Override
        public void lastMusic() {
            moveToLast();
        }

    }

    private String nextMusic(int mode) {
        switch (mode) {
            case Constants.PLAY_MODE_ALL_REPEAT:
                musicIndex++;
                if (musicIndex == DataUtils.getMusics().size())
                    musicIndex = 0;
                break;
            case Constants.PLAY_MODE_SINGLE:
                break;
            case Constants.PLAY_MODE_SHUFFLE:
                musicIndex = DataUtils.getRandonInt(DataUtils.getMusics().size());
                break;
        }
        return DataUtils.getMusics().get(musicIndex).getPath();
    }

    private String lastMusic(int mode) {
        switch (mode) {
            case Constants.PLAY_MODE_ALL_REPEAT:
                musicIndex--;
                if (musicIndex == -1)
                    musicIndex = DataUtils.getMusics().size()-1;
                break;
            case Constants.PLAY_MODE_SINGLE:
                break;
            case Constants.PLAY_MODE_SHUFFLE:
                musicIndex = DataUtils.getRandonInt(DataUtils.getMusics().size());
                break;
        }
        return DataUtils.getMusics().get(musicIndex).getPath();
    }


    /**
     * 对外暴露的接口，当播放下一曲时，更新UI界面
     */
    public interface Updata {
        /**
         * @param nextIndex 下一曲的index
         */
        void updata(int nextIndex);
    }

    public void setUIListener(Updata updata) {
        this.updata = updata;
    }

    /**
     * 下一曲
     */
    private void moveToNext(){
        try {
            player.reset();
            String nextUri = nextMusic(playMode);
            if (nextUri != null) {
                player.setDataSource(nextUri);
            }
            player.prepare();
            player.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //更新UI
        if (updata != null)
            updata.updata(musicIndex);
    }

    private void moveToLast(){
        try {
            player.reset();
            String nextUri = lastMusic(playMode);
            if (nextUri != null) {
                player.setDataSource(nextUri);
            }
            player.prepare();
            player.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //更新UI
        if (updata != null)
            updata.updata(musicIndex);
    }



}
