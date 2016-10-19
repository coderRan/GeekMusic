package com.zdr.geekmusic.DBUtils;

import android.content.Context;
import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.zdr.geekmusic.entity.MusicSheep;

import java.sql.SQLException;
import java.util.List;

/**
 * 列表操作dao
 * Created by zdr on 16-9-7.
 */
public class MusicSheepDao {
    private Dao<MusicSheep, Integer> mDao;
    private static MusicSheepDao mMusicSheepDao = null;

    private MusicSheepDao(Context context) {
        try {
            mDao = DBOpenHelper.getOpenHelper(context).getDao(MusicSheep.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static MusicSheepDao getmMusicSheepDao(Context context) {
        return mMusicSheepDao == null ? new MusicSheepDao(context) : mMusicSheepDao;
    }

    public void addSheep(MusicSheep sheep) {
        try {
            mDao.create(sheep);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<MusicSheep> getAllMusicSheep(){
        try {
            return  mDao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int getSheepIdByName(String name){
        List<MusicSheep> result = null;
        try {
            result = mDao.queryForEq("name", name);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if(result!=null){
            return result.get(0).getId();
        }
        return -1;
    }
    public void deleteSheep(MusicSheep ms){
        try {
            mDao.delete(ms);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
