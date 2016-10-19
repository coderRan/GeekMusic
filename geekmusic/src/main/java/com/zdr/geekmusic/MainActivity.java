package com.zdr.geekmusic;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.zdr.geekmusic.DBUtils.MusicDao;
import com.zdr.geekmusic.adapter.MusicAdapter;
import com.zdr.geekmusic.entity.Music;
import com.zdr.geekmusic.sortListView.ClearEditText;
import com.zdr.geekmusic.sortListView.SideBar;
import com.zdr.geekmusic.utils.GlobarVar;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.filter_edit)
    ClearEditText filterEdit;
    @BindView(R.id.lv_music_list)
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        initView();
    }

    //初始化界面
    private void initView() {
        //绑定弹出的点击显示字母
        musics = MusicDao.getMusicDao(this).findAllMusic();

        sidrbar.setTextView(dialog);
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
                intent.putExtra("position", position);
                GlobarVar.getMusicPlayer().musicSeekTo(position);

                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStop() {
        Log.e("onStop", "onStop执行");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.e("onDestroy", "onDestroy执行");
        super.onDestroy();
    }
}
