package com.zdr.geekmusic.DBUtils;

import android.content.Context;
import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.zdr.geekmusic.entity.KVSheep;
import com.zdr.geekmusic.entity.Music;
import com.zdr.geekmusic.utils.Constants;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 初始化列表信息
 * Created by zdr on 16-9-7.
 */
public class KVSheepDao {
    private Dao<KVSheep, Integer> mDao;
    private static KVSheepDao kvSheepDao = null;
    private List<Music> mMusicList;
    private MusicDao mMusicDao;
    private KVSheepDao(Context context){
        try {
            mDao = DBOpenHelper.getOpenHelper(context).getDao(KVSheep.class);
            mMusicDao = MusicDao.getMusicDao(context);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static KVSheepDao getKvSheepDao(Context context){
        return kvSheepDao == null ? new KVSheepDao(context) : kvSheepDao;
    }

    public List<Music> getMusicListBySheepId(int id){
        mMusicList = new ArrayList<>();
        List<KVSheep> kv = findAllKVById(id);
        if(kv!=null){
            for (KVSheep aKv : kv) {
                mMusicList.add(mMusicDao.findMusicById(aKv.getMusicId()));
            }
        }
        return mMusicList;

    }

    public List<KVSheep> findAllKVById(int sheepId){
        try {
           return mDao.queryForEq(Constants.KVSheepTable.SHEEP_ID, sheepId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void addKV(KVSheep kv){
        try {
            mDao.create(kv);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void removeKV(KVSheep kv){
//        try {
//            int result = mDao.delete(kv);
//            Log.e("result", "removeKV: "+result);
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
        DeleteBuilder<KVSheep, Integer> builder = mDao.deleteBuilder();
        try {
            builder.where().eq(Constants.KVSheepTable.MUSIC_ID, kv.getMusicId())
                    .and().eq(Constants.KVSheepTable.SHEEP_ID, kv.getSheepId());

            builder.delete();

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

}
