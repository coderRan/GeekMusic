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
import com.zdr.geekmusic.utils.Constants;
import com.zdr.geekmusic.utils.DataUtils;

import java.io.IOException;


/**
 * 后台播放音乐的服务
 */
public class MusicService extends Service{
    private MediaPlayer player;
    protected Music music;
    protected MediaPlayer nextPlayer;
    private int musicIndex;
    private IUpdataUI updataUI;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return super.onStartCommand(intent, flags, startId);

    }

    @NonNull
    @Override
    public IBinder onBind(Intent intent) {
        Log.e("onBind", intent.getExtras().getInt("position") + "");
        //启动播放音乐
        musicIndex = intent.getExtras().getInt("position");
        music = DataUtils.getMusics().get(musicIndex);

        player = MediaPlayer.create(this,Uri.parse(music.getPath()));

        //TODO 实现播放下一曲功能
        //player.setNextMediaPlayer();

        //nextPlayer = createNextPlayer(intent.getExtras().getInt("playMode"));
        //player.setNextMediaPlayer(nextPlayer);

        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                moveToNext();

            }
        });
        return new IMusicServiceBind();
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

    private class IMusicServiceBind extends Binder implements IMusicPlayer {
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
            Log.e("CurrentPosition", player.getCurrentPosition() + "");
            return player.getCurrentPosition();
        }

        @Override
        public void setUpdataUI(IUpdataUI updataUI) {
            setUIListener(updataUI);
        }


    }

    private String createNextPlayer(int mode) {
        switch (mode) {
            case Constants.PLAY_MODE_ALL_REPEAT:
                musicIndex++;
                if (musicIndex == DataUtils.getMusics().size())
                    musicIndex = 0;
                break;
            case Constants.PLAY_MODE_ORDER:
                musicIndex++;
                if (musicIndex == DataUtils.getMusics().size())
                    return null;
            case Constants.PLAY_MODE_SINGLE:
                break;
            case Constants.PLAY_MODE_SHUFFLE:
                musicIndex = DataUtils.getRandonInt(DataUtils.getMusics().size());
                break;
        }
//        return MediaPlayer.create(this,
//                Uri.parse(DataUtils.getMusics().get(musicIndex).getPath()));
        return DataUtils.getMusics().get(musicIndex).getPath();
    }

    /**
     * 对外暴露的接口，当播放下一曲时，更新UI界面
     */
    public interface IUpdataUI {
        /**
         * @param nextIndex 下一曲的index
         */
        void updata(int nextIndex);
    }

    public void setUIListener(IUpdataUI updataUI) {
        this.updataUI = updataUI;
    }

    /**
     * 下一曲
     */
    private void moveToNext(){
        try {
            player.reset();
            player.setDataSource(createNextPlayer(Constants.PLAY_MODE_ALL_REPEAT));
            player.prepare();
            player.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //更新UI
        if (updataUI != null)
            updataUI.updata(musicIndex);
    }

}
