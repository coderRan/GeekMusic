package com.zdr.geekmusic.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * 歌曲列表属性
 * Created by zdr on 16-9-7.
 */
@DatabaseTable
public class MusicSheep {
    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField
    private String name;

    public MusicSheep() {
    }

    public MusicSheep(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MusicSheep sheep = (MusicSheep) o;

        if (id != sheep.id) return false;
        return name.equals(sheep.name);

    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + name.hashCode();
        return result;
    }
}
