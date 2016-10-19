package com.zdr.geekmusic.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.zdr.geekmusic.entity.Music;
import com.zdr.geekmusic.service.MusicService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * 全局数据接口，将通过该类的方法获得全局数据
 * Created by zdr on 16-9-3.
 */
public class DataUtils {
    private static List<Music> musics;
    private static Random random;
    public static void initData(){
        random = new Random();

    }
    public static void initMusic(Context context){

        musics = new ArrayList<>();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(uri,
                new String[]{
                        MediaStore.Audio.Media.TITLE,
                        MediaStore.Audio.Media.DATA,
                        MediaStore.Audio.Media.ARTIST,
                        MediaStore.Audio.Media.DURATION,
                        MediaStore.Audio.Media.ALBUM}
                , MediaStore.Audio.Media.DURATION + ">60000", null, null);

        if (cursor == null)
            return;
        while (cursor.moveToNext()) {
            Music music = new Music();
            music.setName(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)));
            music.setPath(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA)));
            music.setArtist(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)));
            music.setDurationMs(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION)));
            music.setAlbum(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM)));
            musics.add(music);
        }
        cursor.close();
    }
    public static List<Music> getMusics(){
        Collections.sort(musics);
        return musics;
    }

    /**
     * @param max 最大值
     * @return 一个从[0,max)的随机数
     */
    public static int getRandonInt(int max) {
        return random.nextInt(max);
    }



}
