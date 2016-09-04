package com.zdr.geekmusic;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.TextView;

import com.zdr.geekmusic.entity.Music;
import com.zdr.geekmusic.service.IMusicPlayer;
import com.zdr.geekmusic.service.MusicService;
import com.zdr.geekmusic.utils.Constants;
import com.zdr.geekmusic.utils.DataUtils;
import com.zdr.geekmusic.utils.MethodUtils;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import co.mobiwise.playerview.MusicPlayerView;

public class PlayMusicActivity extends AppCompatActivity implements MusicService.IUpdataUI{
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
    private Handler handler = new Handler();
    private ServiceConnection musicConn;
    private IMusicPlayer musicPlayer;
    private Intent startMusic;
    private boolean isPost = false;
    private void init() {
        mpv.setCoverDrawable(R.mipmap.mycover);
        mpv.setProgressVisibility(false);
        mpv.setMax(Integer.parseInt(music.getDurationMs())/1000);
        //设置seekBar
        sbProgressBar.setMax(Integer.parseInt(music.getDurationMs()) / 1000);
        sbProgressBar.setProgress(0);

        tvDuration.setText(MethodUtils.secondsToTime(Integer.parseInt(music.getDurationMs()) / 1000));
        tvCurrTime.setText(MethodUtils.secondsToTime(0));
        tvMusicName.setText(music.getName());
        tvMusicArtist.setText(music.getArtist());


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_music);
        ButterKnife.bind(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

        music = DataUtils.getMusics().get(getIntent().getExtras().getInt("position"));
        init();
        //启动服务
        startMusic = new Intent(this, MusicService.class);
        startMusic.putExtra("position",getIntent().getExtras().getInt("position"));
        startMusic.putExtra("playMode", Constants.PLAY_MODE_ALL_REPEAT);
        musicConn = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                musicPlayer = (IMusicPlayer) service;
                musicPlayer.setUpdataUI(PlayMusicActivity.this);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
            }
        };
        startService(startMusic);
        boolean flag = bindService(startMusic, musicConn, BIND_ABOVE_CLIENT);
        Log.i("bindService", flag + "");

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
                    isPost = true;

                }
            }
        });
        sbProgressBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.e("PRO",progress+"");
                tvCurrTime.setText(MethodUtils.secondsToTime(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if(!mpv.isRotating()){
                    mpv.start();
                }
                if(!isPost){
                    handler.post(start);
                }
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
            Log.e("updatesb",sbProgressBar.getProgress()+"");
            //每秒钟更新一次
        }

    };

    @Override
    protected void onDestroy() {
        unbindService(musicConn);
        stopService(startMusic);
        super.onDestroy();
    }

//    @Override
//    public void onWindowFocusChanged(boolean hasFocus) {
//        super.onWindowFocusChanged(hasFocus);
//        View decorView = this.getWindow().getDecorView();
//        if (hasFocus) {
//            decorView.setSystemUiVisibility(
//                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                            | View.SYSTEM_UI_FLAG_FULLSCREEN
//                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);}
//    }

    @Override
    public void updata(int nextIndex) {
        music = DataUtils.getMusics().get(nextIndex);
        init();


    }
}
/**
 * 1. 打开播放界面后，直接滑动进度条，不播放音乐。
 * 2. 在播放音乐时点击进度条，中间的图标不变
 * 3. 启动服务时无法调用onBind（）方法
 */