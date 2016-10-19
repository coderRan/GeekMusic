package com.zdr.geekmusic.DBUtils;

import android.content.Context;
import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.zdr.geekmusic.entity.Music;
import com.zdr.geekmusic.utils.Constants;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


/**
 * 操作Music类的Dao
 * Created by zdr on 16-9-7.
 */
public class MusicDao {

    private Dao<Music, Integer> mDao;

    private static MusicDao musicDao = null;
    List<Music> results;

    public MusicDao(Context context) {
        results = new ArrayList<>();
        try {
            mDao = DBOpenHelper.getOpenHelper(context).getDao(Music.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static MusicDao getMusicDao(Context context) {
        return musicDao == null ? new MusicDao(context) : musicDao;
    }

    public void addAllMusic(List<Music> musics) {
        try {
            mDao.create(musics);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Music findMusicById(int id) {
        results.clear();
        try {
            results = mDao.queryForEq(Constants.MusicTable.ID, id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results.get(0);
    }

    public List<Music> findAllMusic() {
        try {
            return mDao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void updateMusic(Music music){
        try {
            int result = mDao.update(music);
            Log.e("TAG", "updateMusic: "+result );
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



}

