package com.zdr.geekmusic;


import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.zdr.geekmusic.DBUtils.KVSheepDao;
import com.zdr.geekmusic.DBUtils.MusicDao;
import com.zdr.geekmusic.DBUtils.MusicSheepDao;
import com.zdr.geekmusic.entity.KVSheep;
import com.zdr.geekmusic.entity.Music;
import com.zdr.geekmusic.service.MusicPlayer;
import com.zdr.geekmusic.service.MusicService;
import com.zdr.geekmusic.utils.Constants;
import com.zdr.geekmusic.utils.GlobarVar;
import com.zdr.geekmusic.utils.MethodUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import co.mobiwise.playerview.MusicPlayerView;

public class PlayMusicActivity extends AppCompatActivity implements MusicService.UIListener {
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
    @BindView(R.id.iv_pause_music)
    ImageView ivPauseMusic;

    private Music currMusic;
    private List<Music> mMusicList;
    private Handler handler = new Handler();
    private MusicPlayer musicPlayer;
    private boolean isPost = false;
    private int playMode;
    private SharedPreferences sp;

    private MusicSheepDao musicSheepDao;
    private KVSheepDao mKVSheepDao;
    private MusicDao mMusicDao;

    private void init() {

        mpv.setCoverDrawable(R.mipmap.music_bg_3);
        mpv.setProgressVisibility(false);
        mpv.setProgress(0);
        mpv.setMax(Integer.parseInt(currMusic.getDurationMs()) / 1000);
        mpv.stop();
        if(!mpv.isRotating()){
            mpv.start();
        }

        //设置seekBar
        sbProgressBar.setMax(Integer.parseInt(currMusic.getDurationMs()) / 1000);
        sbProgressBar.setProgress(0);

        tvDuration.setText(MethodUtils.secondsToTime(Integer.parseInt(currMusic.getDurationMs()) / 1000));
        tvCurrTime.setText(MethodUtils.secondsToTime(0));
        tvMusicName.setText(currMusic.getName());
        tvMusicArtist.setText(currMusic.getArtist());
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

        if(currMusic.isMyLove()){
            ivLikeMusic.setImageResource(R.mipmap.checked_start_music);
        }else {
            ivLikeMusic.setImageResource(R.mipmap.start_music);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_music);
        ButterKnife.bind(this);
        mMusicList = MusicDao.getMusicDao(this).findAllMusic();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

        currMusic = mMusicList.get(getIntent().getExtras().getInt("position"));
        musicPlayer = GlobarVar.getMusicPlayer();
        init();
        musicPlayer.setUpdataListener(this);
        if (!mpv.isRotating()) {
            mpv.start();
        }

        if (!isPost) {
            handler.post(start);
            isPost = true;
        }
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

        musicSheepDao = MusicSheepDao.getmMusicSheepDao(this);
        mKVSheepDao = KVSheepDao.getKvSheepDao(this);
        mMusicDao = MusicDao.getMusicDao(this);
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
    public void refresh(int nextIndex) {
        currMusic = mMusicList.get(nextIndex);

        init();
    }

    @Override
    public void notificationRefresh() {
        if (musicPlayer.isPlaying()) {
            mpv.stop();
            musicPlayer.pause();
            ivPauseMusic.setImageResource(R.mipmap.play);
        } else {
            mpv.start();
            musicPlayer.play();
            ivPauseMusic.setImageResource(R.mipmap.pause);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @OnClick({R.id.iv_last_music, R.id.iv_like_music, R.id.iv_next_music,
            R.id.iv_play_mode,R.id.iv_pause_music})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_last_music:
                if (musicPlayer != null) {
                    musicPlayer.lastMusic();
                }
                if (!mpv.isRotating()) {
                    mpv.start();
                }
                break;
            case R.id.iv_like_music:
                if(!currMusic.isMyLove()){
                    addLike();
                    currMusic.setMyLove(true);
                    mMusicDao.updateMusic(currMusic);
                    ((ImageView) view).setImageResource(R.mipmap.checked_start_music);
                }else {
                    removeLike();
                    currMusic.setMyLove(false);
                    mMusicDao.updateMusic(currMusic);
                    ((ImageView) view).setImageResource(R.mipmap.start_music);
                }
                break;
            case R.id.iv_pause_music:
                if (musicPlayer != null) {
                    if (musicPlayer.isPlaying()) {
                        mpv.stop();
                        musicPlayer.pause();
                        ((ImageView) view).setImageResource(R.mipmap.play);
                    } else {
                        mpv.start();
                        musicPlayer.play();
                        ((ImageView) view).setImageResource(R.mipmap.pause);
                    }
                }
                break;
            case R.id.iv_next_music:
                if (musicPlayer != null)
                    musicPlayer.nextMusic();
                if (!mpv.isRotating()) {
                    mpv.start();
                }
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
                SharedPreferences.Editor editor = sp.edit();
                editor.putInt(Constants.PLAY_MODE, playMode);
                editor.apply();
                musicPlayer.setPlayMode(playMode);
                break;

        }
        view.startAnimation(AnimationUtils.loadAnimation(this, R.anim.press_anim));
    }

    /**
     * 将音乐加入我的收藏
     */
    private void addLike(){
        int sheepId = musicSheepDao.getSheepIdByName("我的收藏");
        int musicId = currMusic.getId();
        if(sheepId!=-1){
            mKVSheepDao.addKV(new KVSheep(musicId, sheepId));
            Toast.makeText(PlayMusicActivity.this, "收藏成功", Toast.LENGTH_SHORT).show();
        }

    }
    private void removeLike(){
        int sheepId = musicSheepDao.getSheepIdByName("我的收藏");
        int musicId = currMusic.getId();
        if(sheepId!=-1){
            mKVSheepDao.removeKV(new KVSheep(musicId, sheepId));
            Toast.makeText(PlayMusicActivity.this, "取消收藏", Toast.LENGTH_SHORT).show();
        }
    }


}
