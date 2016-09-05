package com.zdr.geekmusic;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.zdr.geekmusic.entity.Music;
import com.zdr.geekmusic.service.MusicPlayer;
import com.zdr.geekmusic.service.MusicService;
import com.zdr.geekmusic.utils.Constants;
import com.zdr.geekmusic.utils.DataUtils;
import com.zdr.geekmusic.utils.MethodUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import co.mobiwise.playerview.MusicPlayerView;

public class PlayMusicActivity extends AppCompatActivity implements MusicService.Updata {
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
    @BindView(R.id.iv_last_music)
    ImageView ivLastMusic;
    @BindView(R.id.iv_next_music)
    ImageView ivNextMusic;
    @BindView(R.id.iv_like_music)
    ImageView ivLikeMusic;
    @BindView(R.id.rl_time)
    RelativeLayout rlTime;
    @BindView(R.id.iv_play_mode)
    ImageView ivPlayMode;

    private Music music;
    private Handler handler = new Handler();
    private ServiceConnection musicConn;
    private MusicPlayer musicPlayer;
    private boolean isPost = false;
    private int playMode;
    private SharedPreferences sp;

    private void init() {
        mpv.setCoverDrawable(R.mipmap.music_bg);
        mpv.setProgressVisibility(false);
        mpv.setMax(Integer.parseInt(music.getDurationMs()) / 1000);
        //设置seekBar
        sbProgressBar.setMax(Integer.parseInt(music.getDurationMs()) / 1000);
        sbProgressBar.setProgress(0);

        tvDuration.setText(MethodUtils.secondsToTime(Integer.parseInt(music.getDurationMs()) / 1000));
        tvCurrTime.setText(MethodUtils.secondsToTime(0));
        tvMusicName.setText(music.getName());
        tvMusicArtist.setText(music.getArtist());
        sp = getSharedPreferences(Constants.SETTINGS_FILE, MODE_PRIVATE);
        playMode = sp.getInt(Constants.PLAY_MODE, Constants.PLAY_MODE_ALL_REPEAT);
        switch (playMode) {
            case Constants.PLAY_MODE_SINGLE:
                ivPlayMode.setImageResource(R.mipmap.single_music);
                break;
            case Constants.PLAY_MODE_ALL_REPEAT:
                ivPlayMode.setImageResource(R.mipmap.all_repeat);
                break;
            case Constants.PLAY_MODE_SHUFFLE:
                ivPlayMode.setImageResource(R.mipmap.shuffle_music);
                break;
        }
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
        Intent startMusic = new Intent(this, MusicService.class);
        startMusic.putExtra("position", getIntent().getExtras().getInt("position"));
        musicConn = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                musicPlayer = (MusicPlayer) service;
                musicPlayer.setUpdataListener(PlayMusicActivity.this);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
            }
        };
        bindService(startMusic, musicConn, BIND_AUTO_CREATE);

        //中间点击暂停的按钮监听
        mpv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mpv.isRotating()) {
                    mpv.stop();
                    if (musicPlayer != null)
                        musicPlayer.pause();
                } else {
                    mpv.start();
                    if (musicPlayer != null)
                        musicPlayer.play();
                    handler.post(start);
                    isPost = true;

                }
            }
        });
        //seekBar滑动监听
        sbProgressBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                tvCurrTime.setText(MethodUtils.secondsToTime(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (!mpv.isRotating()) {
                    mpv.start();
                }
                if (!isPost) {
                    handler.post(start);
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //进度条的单位是秒，seekTo的单位是ms
                if (musicPlayer == null)
                    return;
                musicPlayer.seekTo(sbProgressBar.getProgress() * 1000);
                musicPlayer.play();
            }
        });
    }

    Runnable start = new Runnable() {
        @Override
        public void run() {
            if (musicPlayer == null)
                return;
            musicPlayer.play();
            handler.post(updatesb);
            //用一个handler更新SeekBar
        }

    };
    Runnable updatesb = new Runnable() {
        @Override
        public void run() {
            if (musicPlayer == null)
                return;
            //进度条的单位是秒，getCurrentPosition的单位是ms
            sbProgressBar.setProgress(musicPlayer.getCurrentPosition() / 1000);
            handler.postDelayed(updatesb, 1000);
            //每秒钟更新一次
        }

    };

    @Override
    public void updata(int nextIndex) {
        music = DataUtils.getMusics().get(nextIndex);
        init();

    }

    @Override
    protected void onDestroy() {
        unbindService(musicConn);
        super.onDestroy();
    }

    @OnClick({R.id.iv_last_music, R.id.iv_like_music, R.id.iv_next_music, R.id.iv_play_mode})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_last_music:
                if(musicPlayer!=null){
                    musicPlayer.lastMusic();
                }
                view.startAnimation(AnimationUtils.loadAnimation(this, R.anim.press_anim));
                break;
            case R.id.iv_like_music:
                view.startAnimation(AnimationUtils.loadAnimation(this, R.anim.press_anim));
                break;
            case R.id.iv_next_music:
                if(musicPlayer!=null)
                    musicPlayer.nextMusic();
                view.startAnimation(AnimationUtils.loadAnimation(this, R.anim.press_anim));
                break;
            case R.id.iv_play_mode:
                switch (playMode) {
                    case Constants.PLAY_MODE_SINGLE:
                        playMode = Constants.PLAY_MODE_ALL_REPEAT;
                        ((ImageView) view).setImageResource(R.mipmap.all_repeat);
                        break;
                    case Constants.PLAY_MODE_ALL_REPEAT:
                        playMode = Constants.PLAY_MODE_SHUFFLE;
                        ((ImageView) view).setImageResource(R.mipmap.shuffle_music);
                        break;
                    case Constants.PLAY_MODE_SHUFFLE:
                        playMode = Constants.PLAY_MODE_SINGLE;
                        ((ImageView) view).setImageResource(R.mipmap.single_music);
                        break;
                }
                view.startAnimation(AnimationUtils.loadAnimation(this, R.anim.press_anim));
                SharedPreferences.Editor editor = sp.edit();
                editor.putInt(Constants.PLAY_MODE, playMode);
                editor.apply();
                musicPlayer.setPlayMode(playMode);
                break;
        }
    }
}
