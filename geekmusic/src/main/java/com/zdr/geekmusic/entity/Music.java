package com.zdr.geekmusic.entity;

import android.support.annotation.NonNull;

import com.zdr.geekmusic.utils.PinYin;

import java.io.Serializable;

/**
 * 音乐实体类，对应音乐的属性
 * Created by zdr on 16-9-1.
 */
public class Music implements Comparable<Music> , Serializable {
    private String name;//名字
    private String path;//路径
    private String artist;//作者
    private String durationMs;//时长，毫秒
    private String album;//专辑
    private boolean isMyLove = false;//是否是收藏
    private String nameLetters;//歌名首字母


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        setNameLetters();
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getDurationMs() {
        return durationMs;
    }

    public void setDurationMs(String durationMs) {
        this.durationMs = durationMs;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getNameLetters() {
        return nameLetters;
    }
    private void setNameLetters() {
        this.nameLetters = PinYin.getFirstSpell(this.name).toUpperCase().charAt(0)+"";
    }

    @Override
    public int compareTo(@NonNull Music music) {
        return this.nameLetters.compareTo(music.getNameLetters());
    }

    @Override
    public String toString() {
        return "Music{" +
                "name='" + name + '\'' +
                ", path='" + path + '\'' +
                ", artist='" + artist + '\'' +
                ", durationMs='" + durationMs + '\'' +
                ", album='" + album + '\'' +
                ", nameLetters='" + nameLetters + '\'' +
                '}';
    }
}
