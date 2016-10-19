package com.zdr.geekmusic;

import android.app.ExpandableListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.Toast;

import com.zdr.geekmusic.DBUtils.KVSheepDao;
import com.zdr.geekmusic.DBUtils.MusicDao;
import com.zdr.geekmusic.DBUtils.MusicSheepDao;
import com.zdr.geekmusic.entity.Music;
import com.zdr.geekmusic.entity.MusicSheep;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SongSheetActivity extends ExpandableListActivity {
    private static final String KEY = "song";

    @BindView(R.id.btn_all_music)
    Button allMusic;
    @BindView(R.id.btn_add_sheep)
    Button addShep;

    private List<Map<String, String>> groupData = new ArrayList<>();
    private List<List<Map<String, String>>> childData = new ArrayList<>();
    private SimpleExpandableListAdapter mAdapter;
    private MusicSheepDao mMusicSheepDao;
    private KVSheepDao mKVSheepDao;
    private MusicDao mMusicDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_sheet);
        ButterKnife.bind(this);

        mAdapter = new SimpleExpandableListAdapter(
                this,
                groupData,
                R.layout.sheep_list_view_item,
                new String[]{KEY},
                new int[]{R.id.tv_group_name},
                childData,
                R.layout.sheep_child_item,
                new String[]{"name", "artist"},
                new int[]{R.id.tv_music_name, R.id.tv_music_artist});
        setListAdapter(mAdapter);

        getExpandableListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ImageView iv = (ImageView) view.findViewById(R.id.iv_music_operation);
                iv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(SongSheetActivity.this, "group", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        getExpandableListView().setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                ImageView iv = (ImageView) v.findViewById(R.id.iv_music_operation);
                iv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(SongSheetActivity.this, "child", Toast.LENGTH_SHORT).show();
                    }
                });
                return true;
            }
        });
        mMusicSheepDao = MusicSheepDao.getmMusicSheepDao(this);
        mKVSheepDao = KVSheepDao.getKvSheepDao(this);
        mMusicDao = MusicDao.getMusicDao(this);
        initData();
    }

    @Override
    protected void onStart() {
        super.onStart();
        groupData.clear();
        childData.clear();
        initData();
    }

    private void initData() {
        //初始化列表
        List<MusicSheep> musicSheepList = mMusicSheepDao.getAllMusicSheep();
        for (int i = 0; i < musicSheepList.size(); i++) {
            Map<String, String> map = new HashMap<>();
            map.put(KEY, musicSheepList.get(i).getName());
            groupData.add(map);

            //初始化列表的子元素
            int musicId = musicSheepList.get(i).getId();
//            List<KVSheep> kvSheeps = mKVSheepDao.findAllKVById(musicId);

            List<Music> musicList = mKVSheepDao.getMusicListBySheepId(musicId);
            List<Map<String, String>> childList = new ArrayList<>();
            for (Music m : musicList) {
//                Music m = mMusicDao.findMusicById(kv.getMusicId());
                Map<String, String> childMap = new HashMap<>();

                childList.add(childMap);
                childMap.put("name", m.getName());
                childMap.put("artist", m.getArtist());
            }
            childData.add(childList);
        }
    }

    @OnClick({R.id.btn_add_sheep, R.id.btn_all_music})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_add_sheep:
                mMusicSheepDao.addSheep(new MusicSheep("新添加的列表"));
                Map<String, String> map = new HashMap<>();
                map.put(KEY, "新添加的列表");
                groupData.add(map);

                List<Map<String, String>> childList = new ArrayList<>();
                childData.add(childList);
                mAdapter.notifyDataSetChanged();
                break;
            case R.id.btn_all_music:
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                break;
        }
    }
}
