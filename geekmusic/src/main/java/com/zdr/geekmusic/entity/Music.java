package com.zdr.geekmusic.entity;

import android.support.annotation.NonNull;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.zdr.geekmusic.utils.MethodUtils;

import java.io.Serializable;

/**
 * 音乐实体类，对应音乐的属性
 * Created by zdr on 16-9-1.
 */
@DatabaseTable
public class Music implements Comparable<Music> , Serializable {
    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField
    private String name;//名字
    @DatabaseField
    private String path;//路径
    @DatabaseField
    private String artist;//作者
    @DatabaseField
    private String durationMs;//时长，毫秒
    @DatabaseField
    private String album;//专辑
    @DatabaseField
    private int isMyLove = 0;//是否是收藏
    @DatabaseField
    private String nameLetters;//歌名首字母

    public Music() {
    }

    public int getId() {
        return id;
    }


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
        this.nameLetters = MethodUtils.getFirstSpell(this.name).toUpperCase().charAt(0)+"";
    }

    public boolean isMyLove() {
        return isMyLove!=0;
    }

    public void setMyLove(boolean myLove) {
        isMyLove = myLove ? 1 : 0;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Music music = (Music) o;

        if (id != music.id) return false;
        if (isMyLove != music.isMyLove) return false;
        if (!name.equals(music.name)) return false;
        if (!path.equals(music.path)) return false;
        if (!artist.equals(music.artist)) return false;
        if (!durationMs.equals(music.durationMs)) return false;
        if (!album.equals(music.album)) return false;
        return nameLetters.equals(music.nameLetters);

    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + name.hashCode();
        result = 31 * result + path.hashCode();
        result = 31 * result + artist.hashCode();
        result = 31 * result + durationMs.hashCode();
        result = 31 * result + album.hashCode();
        result = 31 * result + isMyLove;
        result = 31 * result + nameLetters.hashCode();
        return result;
    }
}
