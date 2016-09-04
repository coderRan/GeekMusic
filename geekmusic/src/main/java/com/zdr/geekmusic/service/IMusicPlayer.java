package com.zdr.geekmusic.service;

/**
 * 音乐播放接口，提供音乐播放的相关方法
 * Created by zdr on 16-9-3.
 */
public interface IMusicPlayer {
    void play();

    void pause();

    void seekTo(int position);

    int getCurrentPosition();

    void setUpdataUI(MusicService.IUpdataUI updataUI);
}
