package com.zdr.geekmusic;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zdr.geekmusic.DBUtils.KVSheepDao;
import com.zdr.geekmusic.DBUtils.MusicDao;
import com.zdr.geekmusic.DBUtils.MusicSheepDao;
import com.zdr.geekmusic.adapter.MainVPAdapter;
import com.zdr.geekmusic.adapter.MusicSheepAdapter;
import com.zdr.geekmusic.entity.Music;
import com.zdr.geekmusic.entity.MusicSheep;
import com.zdr.geekmusic.fragment.MusicListFragment;
import com.zdr.geekmusic.fragment.MusicLrcFragment;
import com.zdr.geekmusic.fragment.MusicPlayFragment;
import com.zdr.geekmusic.service.MusicPlayer;
import com.zdr.geekmusic.service.MusicService;
import com.zdr.geekmusic.utils.Blur;
import com.zdr.geekmusic.utils.Constants;
import com.zdr.geekmusic.utils.ImageUtils;
import com.zdr.geekmusic.view.PagerEnabledSlidingPaneLayout;
import com.zdr.geekmusic.view.ScrollableImageView;
import com.zdr.geekmusic.view.TopCenterImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class Main3Activity extends AppCompatActivity implements MusicSheepAdapter.SheepOperation{

    RelativeLayout mRlHeaderTop;
    @BindView(R.id.lv_music_sheep)
    ListView mLvMusicSheep;
    @BindView(R.id.blurred_image)
    TopCenterImageView mBlurredImage;
    @BindView(R.id.blurred_image_header)
    ScrollableImageView mBlurredImageHeader;
    @BindView(R.id.vp_main)
    ViewPager mVpMain;
    @BindView(R.id.tv_local_music)
    TextView mTvLocalMusic;
    @BindView(R.id.drawable)
    PagerEnabledSlidingPaneLayout mDrawable;
    @BindView(R.id.tb_top)
    TabLayout mTbTop;
    @BindView(R.id.tv_add_sheep)
    TextView mTvAddSheep;
    private MusicSheepDao msd;
    private MusicDao mmd;
    private KVSheepDao kvd;
    private List<Music> mMusicList;
    private SharedPreferences sp;
    public Music mMusic;


    private MusicPlayer mPlayer;
    private MusicPlayFragment mPlayFragment;
    private MusicListFragment mListFragment;
    private MusicLrcFragment mLrcFragment;
    private ServiceConnection musicConn;
    private static boolean exitFlag = false;
    private NotificationManager nm;

    private MusicSheepAdapter mSheepAdapter;
    //高斯模糊
    private static final String BLURRED_IMG_PATH = "blurred_image.png";
    private static final int EXIT_MSG = 1;
    private static Handler handle = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case EXIT_MSG:
                    exitFlag = false;
                    break;
            }
            super.handleMessage(msg);
        }
    };
    private Dialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_main3);
        ButterKnife.bind(this);

        sp = getSharedPreferences(Constants.SETTINGS_FILE, Context.MODE_PRIVATE);

        msd = MusicSheepDao.getmMusicSheepDao(this);
        mmd = MusicDao.getMusicDao(this);
        kvd = KVSheepDao.getKvSheepDao(this);

        mMusicList = mmd.findAllMusic();
        startService(new Intent(this, MusicService.class));
        bindService(this);

        mSheepAdapter = new MusicSheepAdapter(msd.getAllMusicSheep(), this);
        List<Fragment> fragmentList = new ArrayList<>();
        mLvMusicSheep.setAdapter(mSheepAdapter);

        mMusic = mMusicList.get(sp.getInt(Constants.MUSIC_INDEX, 0));
        blurred();

        mTvLocalMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMusicList.clear();
                mMusicList.addAll(mmd.findAllMusic());
                updataList(mMusicList);
                mDrawable.closePane();
            }
        });

        mPlayFragment = new MusicPlayFragment();
        mListFragment = new MusicListFragment();
        mListFragment.setListener(mPlayFragment);
        mLrcFragment = new MusicLrcFragment();
        //mLrcFragment.setCurrMusic(mMusic);
        fragmentList.add(mListFragment);
        fragmentList.add(mPlayFragment);
        fragmentList.add(mLrcFragment);
        List<String> tabData = new ArrayList<>();
        tabData.add("列表");
        tabData.add("播放");
        tabData.add("歌词");
        MainVPAdapter vpAdapter = new MainVPAdapter(getSupportFragmentManager(),
                fragmentList, tabData);
        mVpMain.setAdapter(vpAdapter);
        mVpMain.setOffscreenPageLimit(3);

        mTbTop.setTabMode(TabLayout.MODE_FIXED);
        mTbTop.setTabTextColors(Color.WHITE, Color.RED);
        mTbTop.setupWithViewPager(mVpMain);
        chooseSheep();
        nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        mSheepAdapter.setListener(this);
    }

    public void updata(int position) {
        mMusic = mMusicList.get(position);

    }

    private void bindService(Context context) {

        Intent startMusic = new Intent(context, MusicService.class);
        startMusic.putExtra("position", sp.getInt(Constants.MUSIC_INDEX, 0));
        musicConn = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mPlayer = (MusicPlayer) service;
                mPlayer.setUpdataListener(new MusicService.UIListener() {
                    @Override
                    public void refresh(int nextIndex) {
                        mPlayFragment.updata(nextIndex);
                        updata(nextIndex);
                    }

                    @Override
                    public void notificationRefresh() {
                        mPlayFragment.notificationRefresh();
                    }
                });
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
            }
        };
        context.bindService(startMusic, musicConn, Context.BIND_AUTO_CREATE);

    }

    public MusicPlayer getPlayer() {
        return mPlayer;
    }

    /**
     * 背景模糊
     */
    private void blurred() {
        final int screenWidth = ImageUtils.getScreenWidth(this);

        mBlurredImage = (TopCenterImageView) findViewById(R.id.blurred_image);
        mBlurredImageHeader = (ScrollableImageView) findViewById(R.id.blurred_image_header);

        assert mBlurredImageHeader != null;
        mBlurredImageHeader.setScreenWidth(screenWidth);
        mBlurredImage.setAlpha(0.9f);

        final File blurredImage = new File(getFilesDir() + BLURRED_IMG_PATH);
        if (!blurredImage.exists()) {
            setProgressBarIndeterminateVisibility(true);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 2;
                    Bitmap image = BitmapFactory.decodeResource(getResources(), R.mipmap.image, options);
                    Bitmap newImg = Blur.fastblur(Main3Activity.this, image, 12);
                    ImageUtils.storeImage(newImg, blurredImage);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            updateView(screenWidth);
                            setProgressBarIndeterminateVisibility(false);
                        }
                    });
                }
            }).start();
        } else {
            updateView(screenWidth);
        }
    }

    private void updateView(final int screenWidth) {
        Bitmap bmpBlurred = BitmapFactory.decodeFile(getFilesDir() + BLURRED_IMG_PATH);
        bmpBlurred = Bitmap.createScaledBitmap(bmpBlurred, screenWidth, (int) (bmpBlurred.getHeight()
                * ((float) screenWidth) / (float) bmpBlurred.getWidth()), false);
        mBlurredImage.setImageBitmap(bmpBlurred);
        mBlurredImageHeader.setoriginalImage(bmpBlurred);
    }

    /**
     * 设置显示的列表
     */
    private void chooseSheep() {
        mLvMusicSheep.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mMusicList = kvd.getMusicListBySheepId(
                        msd.getSheepIdByName(
                                msd.getAllMusicSheep().get(position).getName()));
                updataList(mMusicList);
                mDrawable.closePane();
            }
        });
    }


    /**
     * 设置按两次退出程序
     */
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (!exitFlag) {
                Toast.makeText(Main3Activity.this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitFlag = true;
                handle.sendEmptyMessageDelayed(EXIT_MSG, 3000);
                return true;
            }
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    protected void onDestroy() {

        unbindService(musicConn);
        nm.cancel(Constants.NOTIFICTION_CODE);
        super.onDestroy();
    }

    public void updataList(List<Music> musics) {

        mListFragment.setMusicList(musics);
        mPlayFragment.setMusicList(musics);
        if (mPlayer != null) {
            mPlayer.chooseList(musics);
        }
    }

    public void dataChanged(int position) {
        mListFragment.notifyDataChanged(position);
    }

    public void lrcChanged(Music music) {
        mLrcFragment.setCurrMusic(music);
    }

    public void lrcRefreash(int progress) {
        mLrcFragment.lrcRefreash(progress);
    }

    @OnClick(R.id.tv_add_sheep)
    public void addSheep() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_layout, null, false);


        final EditText etName = (EditText) view.findViewById(R.id.et_sheep_name);
        etName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        Button ok = (Button) view.findViewById(R.id.bt_ok);
        Button cancel = (Button) view.findViewById(R.id.bt_cancel);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = etName.getText().toString();
                if (TextUtils.isEmpty(name)) {
                    return;
                }
                msd.addSheep(new MusicSheep(name));

                mSheepAdapter.setData(msd.getAllMusicSheep());
                mSheepAdapter.notifyDataSetChanged();
                dialog.dismiss();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

            }
        });
        dialog =  builder.setView(view).create();
        dialog.show();
    }

    @Override
    public void delete(MusicSheep ms) {
        msd.deleteSheep(ms);
        mSheepAdapter.setData(msd.getAllMusicSheep());
        mSheepAdapter.notifyDataSetChanged();
    }
}
