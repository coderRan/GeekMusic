package com.zdr.geekmusic.DBUtils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.zdr.geekmusic.entity.KVSheep;
import com.zdr.geekmusic.entity.Music;
import com.zdr.geekmusic.entity.MusicSheep;
import com.zdr.geekmusic.utils.Constants;

import java.sql.SQLException;


/**
 *
 * Created by zdr on 16-9-7.
 */
public class DBOpenHelper extends OrmLiteSqliteOpenHelper{
    private static DBOpenHelper mDBOpenHelper = null;
    private DBOpenHelper(Context context) {
        super(context, Constants.DB_NAME, null, Constants.DB_VERSION);
    }

    public static DBOpenHelper getOpenHelper(Context context) {
        return mDBOpenHelper == null ? new DBOpenHelper(context) : mDBOpenHelper;
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource source) {
        try {
            TableUtils.createTable(source, Music.class);
            TableUtils.createTable(source, KVSheep.class);
            TableUtils.createTable(source, MusicSheep.class);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource source, int i, int i1) {

    }
}
