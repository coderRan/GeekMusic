package com.zdr.geekmusic.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.RemoteViews;

import com.zdr.geekmusic.DBUtils.MusicDao;
import com.zdr.geekmusic.Main3Activity;
import com.zdr.geekmusic.PlayMusicActivity;
import com.zdr.geekmusic.R;
import com.zdr.geekmusic.entity.Music;
import com.zdr.geekmusic.utils.Constants;
import com.zdr.geekmusic.utils.DataUtils;

import java.io.IOException;
import java.util.List;


/**
 * 后台播放音乐的服务
 */
public class MusicService extends Service {
    public static final String NOTIFICATION_LAST = "last";
    public static final String NOTIFICATION_PAUSE = "pause";
    public static final String NOTIFICATION_NEXT = "next";

    public static class FocuseState {
        public static final int NO_FOCUSE_NO_DUCK = 0;//失去焦点
        public static final int NO_FOCUSE_CAN_DUCK = 1;//规避焦点
        public static final int FOCUSED = 2;//获取焦点
    }

    private int mFocuseState;
    private MediaPlayer player;
    protected Music music;
    private int musicIndex;
    private UIListener mUIListener;
    private int playMode;
    private NotificationManager nm;
    private List<Music> mMusicList;
    private AudioManager am = null;


    private void initMediaPlayer() {

        player = new MediaPlayer();
        try {
            player.setDataSource(this, Uri.parse(music.getPath()));
            player.prepare();

        } catch (IOException e) {
            e.printStackTrace();
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
        Log.e("onCreate", "onCreat 执行");
        //super.onCreate();
        nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        am = (AudioManager) getSystemService(AUDIO_SERVICE);

        mMusicList = MusicDao.getMusicDao(this).findAllMusic();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();
        if (NOTIFICATION_LAST.equals(action)) {
            moveToLast();

        }
        if (NOTIFICATION_PAUSE.equals(action)) {
            if (mUIListener != null)
                mUIListener.notificationRefresh();
            //暂停
            createNotification();
        }
        if (NOTIFICATION_NEXT.equals(action)) {
            moveToNext();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @NonNull
    @Override
    public IBinder onBind(Intent intent) {

        Log.e("onBind", intent.getExtras().getInt("position") + "");
        //启动播放音乐
        musicIndex = intent.getExtras().getInt("position");
        music = mMusicList.get(musicIndex);
        SharedPreferences sp = getSharedPreferences(Constants.SETTINGS_FILE, MODE_PRIVATE);
        playMode = sp.getInt(Constants.PLAY_MODE, Constants.PLAY_MODE_ALL_REPEAT);
        initMediaPlayer();

        return new MusicServiceBind();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        //当启动该服务的activity被销毁时，会调用onUnbind

        am.abandonAudioFocus(mListener);
        mUIListener = null;
        music = null;
        mMusicList = null;
        nm.cancel(Constants.NOTIFICTION_CODE);
        nm = null;
        am = null;
        this.stopSelf();
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        //释放音乐资源
        writeSp(musicIndex);
        if (player != null) {
            //手动释放资源
            player.reset();
            player.release();
            player = null;
        }
        super.onDestroy();
    }

    private class MusicServiceBind extends Binder implements MusicPlayer {
        @Override
        public void play() {
            if (player != null){
                requestFocuse();
                playSong();
            }

        }

        @Override
        public void pause() {
            if (player != null)
                player.pause();
        }

        @Override
        public void seekTo(int position) {
            if (player != null)
                player.seekTo(position);
        }

        @Override
        public int getCurrentPosition() {
            if (player != null)
                return player.getCurrentPosition();
            return -1;
        }

        @Override
        public void setUpdataListener(UIListener UIListener) {
            setUIListener(UIListener);
        }

        @Override
        public void setPlayMode(int mode) {
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

        @Override
        public void musicSeekTo(int index) {
            moveSeetTo(index);
        }

        @Override
        public boolean isPlaying() {
            return player.isPlaying();
        }

        @Override
        public Music getCurrMusic() {
            return music;
        }

        @Override
        public int getMusicIndex() {
            return musicIndex;
        }

        @Override
        public void chooseList(List<Music> musics) {
            mMusicList.clear();
            mMusicList.addAll(musics);
        }
    }

    private String nextMusic(int mode) {
        switch (mode) {
            case Constants.PLAY_MODE_ALL_REPEAT:
                musicIndex++;
                if (musicIndex == mMusicList.size())
                    musicIndex = 0;
                break;
            case Constants.PLAY_MODE_SINGLE:
                break;
            case Constants.PLAY_MODE_SHUFFLE:
                musicIndex = DataUtils.getRandonInt(mMusicList.size());
                break;
        }
        writeSp(musicIndex);
        return mMusicList.get(musicIndex).getPath();
    }

    private String lastMusic(int mode) {
        switch (mode) {
            case Constants.PLAY_MODE_ALL_REPEAT:
                musicIndex--;
                if (musicIndex == -1)
                    musicIndex = mMusicList.size() - 1;
                break;
            case Constants.PLAY_MODE_SINGLE:
                break;
            case Constants.PLAY_MODE_SHUFFLE:
                musicIndex = DataUtils.getRandonInt(mMusicList.size());
                break;
        }
        writeSp(musicIndex);
        return mMusicList.get(musicIndex).getPath();
    }

    /**
     * 对外暴露的接口
     */
    public interface UIListener {
        /**
         * 当播放下一曲时，更新UI界面
         *
         * @param nextIndex 下一曲的index
         */
        void refresh(int nextIndex);

        /**
         * 当点击通知栏上的按钮时对播放界面的刷新
         */
        void notificationRefresh();
    }

    public void setUIListener(UIListener UIListener) {
        this.mUIListener = UIListener;
    }

    /**
     * 下一曲
     */
    private void moveToNext() {
        try {
                player.reset();
            String nextUri = nextMusic(playMode);
            if (nextUri != null) {
                player.setDataSource(nextUri);
            }
            player.prepare();

        } catch (IOException e) {
            e.printStackTrace();
        }
        //更新UI
        if (mUIListener != null)
            mUIListener.refresh(musicIndex);
        requestFocuse();
        playSong();
        createNotification();


    }

    /**
     * 上一曲
     */
    private void moveToLast() {
        try {
            player.reset();
            String nextUri = lastMusic(playMode);
            if (nextUri != null) {
                player.setDataSource(nextUri);
            }
            player.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //更新UI
        if (mUIListener != null)
            mUIListener.refresh(musicIndex);
        requestFocuse();
        playSong();
        createNotification();
    }

    private void moveSeetTo(int index) {
        musicIndex = index;
        try {
            player.reset();
            String nextUri = mMusicList.get(musicIndex).getPath();
            if (nextUri != null) {
                player.setDataSource(nextUri);
            }
            player.prepare();

        } catch (IOException e) {
            e.printStackTrace();
        }
        //更新UI
        if (mUIListener != null)
            mUIListener.refresh(musicIndex);
        requestFocuse();
        playSong();
        createNotification();
        writeSp(musicIndex);
    }

    private void createNotification() {
        music = mMusicList.get(musicIndex);
        Notification.Builder builder =
                new Notification.Builder(this);
        builder.setSmallIcon(R.mipmap.app_logo);
        RemoteViews remoteViews
                = new RemoteViews(getPackageName(), R.layout.play_mini_layout);
        remoteViews.setTextViewText(R.id.tv_mini_name, music.getName());
        remoteViews.setTextViewText(R.id.tv_mini_artist, music.getArtist());
        //根据音乐的状态选择不同的播放图标
        if (player.isPlaying())
            remoteViews.setImageViewResource(R.id.iv_play_mini, R.mipmap.pause);
        else
            remoteViews.setImageViewResource(R.id.iv_play_mini, R.mipmap.play);

        //上一首
        Intent last = new Intent(this, this.getClass());
        last.setAction(NOTIFICATION_LAST);
        PendingIntent pendingLast = PendingIntent.getService(
                this,
                Constants.NOTIFICTION_CODE,
                last,
                PendingIntent.FLAG_CANCEL_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.iv_last_mini, pendingLast);
        //暂停
        Intent pause = new Intent(this, this.getClass());
        pause.setAction(NOTIFICATION_PAUSE);
        PendingIntent pendingPause = PendingIntent.getService(
                this,
                Constants.NOTIFICTION_CODE,
                pause,
                PendingIntent.FLAG_CANCEL_CURRENT
        );

        remoteViews.setOnClickPendingIntent(R.id.iv_play_mini, pendingPause);
        //下一首
        Intent next = new Intent(this, this.getClass());
        next.setAction(NOTIFICATION_NEXT);
        PendingIntent pendingNext = PendingIntent.getService(
                this,
                Constants.NOTIFICTION_CODE,
                next,
                PendingIntent.FLAG_CANCEL_CURRENT
        );


        remoteViews.setOnClickPendingIntent(R.id.iv_next_mini, pendingNext);
        builder.setContent(remoteViews);
        //点击进入播放界面
        Intent play = new Intent(this, Main3Activity.class);
        PendingIntent pendingPlay = PendingIntent.getActivity(this,
                Constants.NOTIFICTION_CODE,
                play,
                PendingIntent.FLAG_CANCEL_CURRENT);

        remoteViews.setOnClickPendingIntent(R.id.rl_mini_music, pendingPlay);
        nm.notify(Constants.NOTIFICTION_CODE, builder.build());
    }

    private void requestFocuse() {
        if (mFocuseState == FocuseState.FOCUSED)
            return;
        int request = am.requestAudioFocus(mListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        if (request == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            //获取到焦点
            mFocuseState = FocuseState.FOCUSED;
        }
    }

    private AudioManager.OnAudioFocusChangeListener mListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            switch (focusChange) {
                case AudioManager.AUDIOFOCUS_GAIN:
                    mFocuseState = FocuseState.FOCUSED;
                    break;
                case AudioManager.AUDIOFOCUS_LOSS:
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                    mFocuseState = FocuseState.NO_FOCUSE_NO_DUCK;
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                    mFocuseState = FocuseState.NO_FOCUSE_CAN_DUCK;
                    break;
            }
        }
    };

    /**
     * 获取音频焦点后播放
     */
    private void playSong() {
        if (mFocuseState == FocuseState.NO_FOCUSE_NO_DUCK) {
            if (player.isPlaying())
                player.pause();
            return;
        }else {
            if(mFocuseState == FocuseState.NO_FOCUSE_CAN_DUCK){
                player.setVolume(0.1f, 0.1f);
            }else {
                player.setVolume(1.0f,1.0f);
            }
            if(!player.isPlaying()){
                player.start();
            }
        }
    }

    private void writeSp(int position){
        SharedPreferences sp = getApplicationContext().getSharedPreferences(Constants.SETTINGS_FILE, MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(Constants.MUSIC_INDEX, position);
        editor.apply();

    }
}
