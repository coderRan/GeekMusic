package com.zdr.geekmusic.fragment;



import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.zdr.geekmusic.DBUtils.MusicDao;
import com.zdr.geekmusic.Main3Activity;
import com.zdr.geekmusic.R;
import com.zdr.geekmusic.adapter.MusicAdapter;
import com.zdr.geekmusic.entity.Music;
import com.zdr.geekmusic.service.MusicPlayer;
import com.zdr.geekmusic.sortListView.SideBar;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 歌曲列表
 * A simple {@link Fragment} subclass.
 */
public class MusicListFragment extends Fragment {
    @BindView(R.id.lv_music_list)
    ListView mMusicListView;
    @BindView(R.id.dialog)
    TextView mDialog;
    @BindView(R.id.sidrbar)
    SideBar mSidrbar;

    private View mView;
    private List<Music> musics;
    private MusicAdapter mMusicAdapter;
    private MusicPlayer mPlayer;

    private PlayMusicListener mListener;
    public MusicListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mView != null)
            return mView;
        mView = inflater.inflate(R.layout.fragment_music_list, container, false);
        ButterKnife.bind(this, mView);
        initView();
        return mView;
    } private void initView() {
        //绑定弹出的点击显示字母
        musics = MusicDao.getMusicDao(getActivity()).findAllMusic();

        mSidrbar.setTextView(mDialog);
        mMusicAdapter = new MusicAdapter(musics, getActivity());
        mMusicListView.setAdapter(mMusicAdapter);

        //设置右侧字母栏的触摸监听
        mSidrbar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {
            @Override
            public void onTouchingLetterChanged(String s) {
                mMusicListView.setSelection(mMusicAdapter.getFirstIndexForLetters(s));
            }
        });
        //设置item点击事件
        mMusicListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mPlayer = ((Main3Activity) getActivity()).getPlayer();
                mPlayer.musicSeekTo(position);
                ((Main3Activity) getActivity()).updata(position);
                mPlayer.play();
                if(mListener!=null){
                    mListener.updata(position);
                }
                notifyDataChanged(position);

            }
        });
    }

    public interface PlayMusicListener{
        void updata(int position);
    }

    public void setListener(PlayMusicListener listener) {
        mListener = listener;
    }

    public void setMusicList(List<Music> musicList) {
        musics.clear();
        musics.addAll(musicList);
        mMusicAdapter.notifyDataSetChanged();
    }


    public void notifyDataChanged(int position){
        mMusicAdapter.setPlaying(musics.get(position));
        mMusicAdapter.notifyDataSetChanged();
    }

}
