package com.zdr.geekmusic;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.zdr.geekmusic.adapter.MusicAdapter;
import com.zdr.geekmusic.entity.Music;
import com.zdr.geekmusic.sortListView.ClearEditText;
import com.zdr.geekmusic.sortListView.SideBar;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.filter_edit)
    ClearEditText filterEdit;
    @BindView(R.id.country_lvcountry)
    ListView musicList;
    @BindView(R.id.dialog)
    TextView dialog;
    @BindView(R.id.sidrbar)
    SideBar sidrbar;

    private List<Music> musics;
    private MusicAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initView();
    }

    //初始化界面
    private void initView() {
        //绑定弹出的点击显示字母
        sidrbar.setTextView(dialog);
        findMusic();
        adapter = new MusicAdapter(musics, this);
        musicList.setAdapter(adapter);

        //设置右侧字母栏的触摸监听
        sidrbar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {
            @Override
            public void onTouchingLetterChanged(String s) {

                musicList.setSelection(adapter.getFirstIndexForLetters(s));
            }
        });
        //设置item点击事件
        musicList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, PlayMusicActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("music",musics.get(position));
                intent.putExtras(bundle);

                startActivity(intent);

            }
        });
    }

    //初始化音乐
    private void findMusic() {
        musics = new ArrayList<>();

        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        ContentResolver resolver = getContentResolver();
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
}
