package com.zdr.geekmusic;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;

import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.zdr.geekmusic.entity.Music;
import com.zdr.geekmusic.service.MusicPlayer;
import com.zdr.geekmusic.service.MusicService;
import com.zdr.geekmusic.utils.Method;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import co.mobiwise.playerview.MusicPlayerView;

public class PlayMusicActivity extends AppCompatActivity {
    @BindView(R.id.mpv)
    MusicPlayerView mpv;
    @BindView(R.id.tv_music_name)
    TextView tvMusicName;
    @BindView(R.id.tv_music_artist)
    TextView tvMusicArtist;
    @BindView(R.id.sb_progress_bar)
    SeekBar sbProgressBar;
    @BindView(R.id.tv_currTime)
    TextView tvCurrTime;
    @BindView(R.id.tv_Duration)
    TextView tvDuration;

    private Music music;
    //private MediaPlayer player;
    private Handler handler = new Handler();
    private ServiceConnection musicConn;
    private MusicPlayer musicPlayer;
    private Intent startMusic;
    private void init() {
        music = (Music) (getIntent().getExtras().get("music"));
        mpv.setCoverDrawable(R.mipmap.mycover);
        mpv.setProgressVisibility(false);
        mpv.setMax(Integer.parseInt(music.getDurationMs())/1000);
        //设置seekBar
        sbProgressBar.setMax(Integer.parseInt(music.getDurationMs()) / 1000);

        tvDuration.setText(Method.secondsToTime(Integer.parseInt(music.getDurationMs()) / 1000));
        tvCurrTime.setText(Method.secondsToTime(0));
        tvMusicName.setText(music.getName());
        tvMusicArtist.setText(music.getArtist());


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_music);
        ButterKnife.bind(this);
        init();
        //启动服务
        startMusic = new Intent(this, MusicService.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("music",music);
        startMusic.putExtra("path", music.getPath());

        musicConn = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                musicPlayer = (MusicPlayer) service;
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
            }
        };
        startService(startMusic);
        boolean flag = bindService(startMusic, musicConn, BIND_ABOVE_CLIENT);
        Log.e("FLAG", flag + "");
        //中间点击暂停的按钮监听
        mpv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mpv.isRotating()) {
                    mpv.stop();
                    musicPlayer.pause();
                } else {
                    mpv.start();
                    musicPlayer.play();
                    handler.post(start);

                }
            }
        });

        sbProgressBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.e("PRO",progress+"");
                tvCurrTime.setText(Method.secondsToTime(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //进度条的单位是秒，seekTo的单位是ms
                musicPlayer.seekTo(sbProgressBar.getProgress()*1000);
                musicPlayer.play();
            }
        });
    }

    @OnClick({R.id.tv_music_artist, R.id.sb_progress_bar})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_music_artist:
                break;
            case R.id.sb_progress_bar:
                break;
        }
    }

    Runnable start = new Runnable() {
        @Override
        public void run() {
            musicPlayer.play();
            handler.post(updatesb);
            //用一个handler更新SeekBar
        }

    };
    Runnable updatesb = new Runnable() {
        @Override
        public void run() {
            //进度条的单位是秒，getCurrentPosition的单位是ms
            sbProgressBar.setProgress(musicPlayer.getCurrentPosition()/1000);
            handler.postDelayed(updatesb, 1000);
            //每秒钟更新一次
        }

    };

    @Override
    protected void onDestroy() {
        unbindService(musicConn);
        stopService(startMusic);
        super.onDestroy();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        View decorView = this.getWindow().getDecorView();
        if (hasFocus) {
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);}
    }
}
/**
 * 1. 打开播放界面后，直接滑动进度条，不播放音乐。
 * 2. 在播放音乐时点击进度条，中间的图标不变
 * 3. 启动服务时无法调用onBind（）方法
 */