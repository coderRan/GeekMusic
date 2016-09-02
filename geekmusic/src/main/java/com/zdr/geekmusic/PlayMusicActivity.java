package com.zdr.geekmusic;

import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.zdr.geekmusic.entity.Music;
import com.zdr.geekmusic.utils.Method;

import java.io.IOException;

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
    private MediaPlayer player;
    private Handler handler = new Handler();

    private void init() {
        music = (Music) (getIntent().getExtras().get("music"));
        mpv.setCoverDrawable(R.mipmap.mycover);
        mpv.setProgressVisibility(false);
        tvMusicName.setText(music.getName());
        tvMusicArtist.setText(music.getArtist());
        player = MediaPlayer.create(this, Uri.parse(music.getPath()));
        //设置seekBar
        sbProgressBar.setDrawingCacheBackgroundColor(Color.argb(100,100,100,100));
        sbProgressBar.setMax(player.getDuration());
        tvDuration.setText(Method.secondsToTime(player.getDuration()/1000));
        tvCurrTime.setText(Method.secondsToTime(0));
        //加载音乐
        player.reset();
        try {
            player.setDataSource(music.getPath());
            player.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_music);
        ButterKnife.bind(this);
        init();

        //中间点击暂停的按钮监听
        mpv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mpv.isRotating()) {
                    mpv.stop();
                    player.pause();
                } else {
                    mpv.start();
                    handler.post(start);
                }
            }
        });

        sbProgressBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvCurrTime.setText(Method.secondsToTime(progress/1000));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                player.pause();
                if (mpv.isRotating())
                    mpv.stop();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                player.seekTo(sbProgressBar.getProgress());
                player.start();

                if (!mpv.isRotating())
                    mpv.start();
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
            player.start();
            handler.post(updatesb);
            //用一个handler更新SeekBar
        }

    };
    Runnable updatesb = new Runnable() {
        @Override
        public void run() {
            sbProgressBar.setProgress(player.getCurrentPosition());
            handler.postDelayed(updatesb, 1000);
            //每秒钟更新一次
        }

    };

    @Override
    public void finish() {
        player.release();
        super.finish();

    }
}
/**
 * 1. 打开播放界面后，直接滑动进度条，不播放音乐。
 * 2. 在播放音乐时点击进度条，中间的图标不变
 * */