package com.zdr.geekmusic.fragment;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.zdr.geekmusic.DBUtils.KVSheepDao;
import com.zdr.geekmusic.DBUtils.MusicDao;
import com.zdr.geekmusic.DBUtils.MusicSheepDao;
import com.zdr.geekmusic.Main3Activity;
import com.zdr.geekmusic.R;
import com.zdr.geekmusic.entity.KVSheep;
import com.zdr.geekmusic.entity.Music;
import com.zdr.geekmusic.service.MusicPlayer;
import com.zdr.geekmusic.utils.Constants;
import com.zdr.geekmusic.utils.MethodUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import co.mobiwise.playerview.MusicPlayerView;

/**
 * 歌曲播放界面
 * A simple {@link Fragment} subclass.
 */
public class MusicPlayFragment extends Fragment
        implements MusicListFragment.PlayMusicListener {

    @BindView(R.id.mpv)
    MusicPlayerView mpv;
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

    public MusicPlayer musicPlayer;

    private Music currMusic;
    private List<Music> mMusicList;
    private Handler handler = new Handler();
    private int playMode;
    private SharedPreferences sp;

    private MusicSheepDao musicSheepDao;
    private KVSheepDao mKVSheepDao;
    private MusicDao mMusicDao;
    private View mView;
    private boolean isPost = false;
    public MusicPlayFragment() {
        // Required empty public constructor
    }

    private void init() {
        //设置中间的旋转动画
        mpv.setCoverDrawable(R.mipmap.music_bg_3);
        mpv.setProgressVisibility(false);
        mpv.setProgress(0);
        mpv.setMax(Integer.parseInt(currMusic.getDurationMs()) / 1000);

        //设置seekBar
        sbProgressBar.setMax(Integer.parseInt(currMusic.getDurationMs()) / 1000);
        sbProgressBar.setProgress(0);
        tvDuration.setText(MethodUtils.secondsToTime(Integer.parseInt(currMusic.getDurationMs()) / 1000));
        tvCurrTime.setText(MethodUtils.secondsToTime(0));
        sp = getActivity().getSharedPreferences(Constants.SETTINGS_FILE, Context.MODE_PRIVATE);
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

        if (currMusic.isMyLove()) {
            ivLikeMusic.setImageResource(R.mipmap.checked_start_music);
        } else {
            ivLikeMusic.setImageResource(R.mipmap.start_music);
        }

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        musicSheepDao = MusicSheepDao.getmMusicSheepDao(getActivity());
        mKVSheepDao = KVSheepDao.getKvSheepDao(getActivity());
        mMusicDao = MusicDao.getMusicDao(getActivity());
        mMusicList = MusicDao.getMusicDao(getActivity()).findAllMusic();

        sp = getActivity()
                .getSharedPreferences(Constants.SETTINGS_FILE, Context.MODE_PRIVATE);
        int lastMusicIndex = sp.getInt(Constants.MUSIC_INDEX, 0);
        currMusic = mMusicList.get(lastMusicIndex);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mView != null) {
            return mView;
        }
        mView = inflater.inflate(R.layout.fragment_music_play, container, false);
        ButterKnife.bind(this, mView);
        init();
        //seekBar滑动监听
        sbProgressBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvCurrTime.setText(MethodUtils.secondsToTime(progress));

                ((Main3Activity) getActivity()).lrcRefreash(musicPlayer.getCurrentPosition());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (!mpv.isRotating() && musicPlayer!=null && musicPlayer.isPlaying()) {
                    mpv.start();
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //进度条的单位是秒，seekTo的单位是ms
                if (musicPlayer == null){
                    sbProgressBar.setProgress(0);
                    return;
                }

                musicPlayer.seekTo(sbProgressBar.getProgress() * 1000);
                musicPlayer.play();
            }
        });

        return mView;
    }

    @OnClick({R.id.iv_last_music, R.id.iv_like_music, R.id.iv_next_music,
            R.id.iv_play_mode, R.id.iv_pause_music})
    public void onClick(View view) {
        musicPlayer = ((Main3Activity) getActivity()).getPlayer();
        switch (view.getId()) {
            case R.id.iv_last_music:
                if (musicPlayer != null) {
                    musicPlayer.lastMusic();
                }
                ivPauseMusic.setImageResource(R.mipmap.pause);
                if (!isPost) {
                    handler.post(start);
                    isPost = true;
                }
                if (!mpv.isRotating()) {
                    mpv.start();
                }
                currMusic = musicPlayer.getCurrMusic();
                init();
                ((Main3Activity) getActivity()).updata(musicPlayer.getMusicIndex());
                ((Main3Activity) getActivity()).dataChanged(musicPlayer.getMusicIndex());
                break;
            case R.id.iv_like_music:
                if (!currMusic.isMyLove()) {
                    addLike();
                    currMusic.setMyLove(true);
                    mMusicDao.updateMusic(currMusic);
                    ((ImageView) view).setImageResource(R.mipmap.checked_start_music);
                } else {
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
                        if (!isPost) {
                            handler.post(start);
                            isPost = true;
                        }
                    }
                }
                ((Main3Activity) getActivity()).dataChanged(musicPlayer.getMusicIndex());
                ((Main3Activity) getActivity()).lrcChanged(currMusic);
                break;
            case R.id.iv_next_music:
                if (musicPlayer != null)
                    musicPlayer.nextMusic();
                ivPauseMusic.setImageResource(R.mipmap.pause);
                if (!isPost) {
                    handler.post(start);
                    isPost = true;
                }

                if (!mpv.isRotating()) {
                    mpv.start();
                }
                currMusic = musicPlayer.getCurrMusic();
                init();
                ((Main3Activity) getActivity()).updata(musicPlayer.getMusicIndex());
                ((Main3Activity) getActivity()).dataChanged(musicPlayer.getMusicIndex());
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
        view.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.press_anim));
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
            if (musicPlayer == null)
                return;
            //进度条的单位是秒，getCurrentPosition的单位是ms
            sbProgressBar.setProgress(musicPlayer.getCurrentPosition() / 1000);
            handler.postDelayed(updatesb, 1000);
            //每秒钟更新一次
        }

    };

    /**
     * 将音乐加入我的收藏
     */
    private void addLike() {
        int sheepId = musicSheepDao.getSheepIdByName("我的收藏");
        int musicId = currMusic.getId();
        if (sheepId != -1) {
            mKVSheepDao.addKV(new KVSheep(musicId, sheepId));
            Toast.makeText(getActivity(), "收藏成功", Toast.LENGTH_SHORT).show();
        }

    }

    private void removeLike() {
        int sheepId = musicSheepDao.getSheepIdByName("我的收藏");
        int musicId = currMusic.getId();
        if (sheepId != -1) {
            mKVSheepDao.removeKV(new KVSheep(musicId, sheepId));
            Toast.makeText(getActivity(), "取消收藏", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void updata(int position) {

        musicPlayer = ((Main3Activity) getActivity()).getPlayer();
        currMusic = mMusicList.get(position);
        if (!mpv.isRotating()) {
            mpv.start();
        }
        if (!isPost) {
            handler.post(start);
            isPost = true;
        }
        init();
        ivPauseMusic.setImageResource(R.mipmap.pause);
        ((Main3Activity) getActivity()).dataChanged(musicPlayer.getMusicIndex());
        ((Main3Activity) getActivity()).lrcChanged(currMusic);
    }

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

    public void setMusicList(List<Music> musicList){
        mMusicList.clear();
        mMusicList.addAll(musicList);

    }
}
