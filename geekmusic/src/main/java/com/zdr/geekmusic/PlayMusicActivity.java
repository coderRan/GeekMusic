package com.zdr.geekmusic;


import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

import com.zdr.geekmusic.entity.Music;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PlayMusicActivity extends AppCompatActivity {

    @BindView(R.id.btn_start)
    Button btnStart;
    @BindView(R.id.btn_stop)
    Button btnStop;
    @BindView(R.id.btn_pause)
    Button btnPause;
    @BindView(R.id.sb_progress)
    SeekBar sbProgress;

    private Music music;
    private MediaPlayer player;

    private Handler playHandle = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            sbProgress.setProgress(msg.what);
        }
    };
    private Thread seekbarThread;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_music);
        ButterKnife.bind(this);
        music = (Music) (getIntent().getExtras().get("music"));
        player = new MediaPlayer();

        sbProgress.setMax(Integer.parseInt(music.getDurationMs()));
        seekbarThread = new Thread(new Runnable() {
            @Override
            public void run() {
                long time = Long.parseLong(music.getDurationMs());
                int  currTime = 0;
                try {
                    while (currTime<time){
                        Thread.sleep(100);
                        currTime += 100;
                        Log.e("currTime", currTime + "");
                        playHandle.sendEmptyMessage(currTime);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        sbProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

    @OnClick({R.id.btn_start, R.id.btn_stop, R.id.btn_pause})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_start:
                try {
                    player.reset();
                    player.setDataSource(music.getPath());
                    player.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                seekbarThread.start();
                player.start();
                break;
            case R.id.btn_stop:
                if (player.isPlaying())
                    player.stop();
                break;
            case R.id.btn_pause:
                player.pause();
                try {
                    seekbarThread.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

}
