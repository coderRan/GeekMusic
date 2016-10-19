package com.zdr.geekmusic.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * 音乐id与列表id的对照表
 * Created by zdr on 16-9-7.
 */
@DatabaseTable
public class KVSheep {
    @DatabaseField(generatedId = true)
    private int kvId;
    @DatabaseField
    private int musicId;
    @DatabaseField
    private int sheepId;

    public KVSheep() {
    }

    public KVSheep(int musicId, int sheepId) {
        this.musicId = musicId;
        this.sheepId = sheepId;
    }


    public int getMusicId() {
        return musicId;
    }

    public int getSheepId() {
        return sheepId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        KVSheep sheep = (KVSheep) o;

        if (musicId != sheep.musicId) return false;
        return sheepId == sheep.sheepId;

    }

    @Override
    public int hashCode() {
        int result = musicId;
        result = 31 * result + sheepId;
        return result;
    }
}
