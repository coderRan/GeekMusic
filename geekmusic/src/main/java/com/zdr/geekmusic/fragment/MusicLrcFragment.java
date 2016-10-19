package com.zdr.geekmusic.fragment;


import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.google.gson.Gson;
import com.zdr.geekmusic.Main3Activity;
import com.zdr.geekmusic.R;
import com.zdr.geekmusic.entity.Lrc;
import com.zdr.geekmusic.entity.Music;
import com.zdr.geekmusic.entity.MusicLrc;
import com.zdr.geekmusic.entity.MusicQuery;
import com.zdr.geekmusic.netUtils.Callback;
import com.zdr.geekmusic.netUtils.VolleyManager;
import com.zdr.geekmusic.utils.Constants;
import com.zdr.geekmusic.view.LrcTextView;

import java.io.File;

/**
 * A simple {@link Fragment} subclass.
 */
public class MusicLrcFragment extends Fragment {

    private View mView = null;
    private Music currMusic;
    private Gson gson = new Gson();
    private LrcTextView tv;
    private Lrc currLrc;
    public MusicLrcFragment() {
        // Required empty public constructor

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mView != null)
            return mView;

        mView = inflater.inflate(R.layout.fragment_music_lrc, container, false);
        tv = (LrcTextView) mView.findViewById(R.id.tv_music_lrc);
        if(currMusic == null){
            currMusic = ((Main3Activity) getActivity()).mMusic;
        }


        return mView;
    }

    private void musicQuery(String url) {

        VolleyManager
                .get(url)
                .setMethod(Request.Method.GET)
                .setCallbacl(new Callback() {
                    @Override
                    public void onSuccess(String respone) {
                        MusicQuery mq;

                        try{
                            mq = gson.fromJson(respone, MusicQuery.class);
                            for (MusicQuery.DataBean.MusicItem mi : mq.getData().getItem()) {
                                if (mi.getFilename().contains(currMusic.getName())
                                        && mi.getSingername().equals(currMusic.getArtist())
                                        && mi.getDuration() == Integer.parseInt(currMusic.getDurationMs())/1000) {
                                    musicLrcRequest(mi);
                                    break;
                                }
                            }

                        }catch (Exception ex){
                            Log.e("musicQuery", "onSuccess: "+ex.toString());
                        }
                    }

                    @Override
                    public void onError(String err) {
                        Log.e("Volley", "onError: " + err);
                    }
                }).buile().addHeader("apikey", Constants.API_KEY).start();
    }

    private void musicLrcRequest(MusicQuery.DataBean.MusicItem currMi) {
        String url = "http://apis.baidu.com/geekery/music/krc";
        String httpArg = "name=" + Uri.encode(currMi.getFilename())
                + "&hash=" + currMi.getHash()
                + "&time=" + currMi.getDuration();
        VolleyManager
                .get(url + "?" + httpArg)
                .setMethod(Request.Method.GET)
                .setCallbacl(new Callback() {
                    @Override
                    public void onSuccess(String respone) {
                        MusicLrc lrc;
                        try {
                            lrc = gson.fromJson(respone, MusicLrc.class);

                            currLrc = new Lrc(lrc.getLrc().getContent());
                            tv.setLrc(currLrc);
                            tv.setIndex(0);
                            tv.invalidate();

                        }catch (Exception ex){
                            tv.setText("歌词加载失败");
                        }
                    }

                    @Override
                    public void onError(String err) {
                        Log.e("Volley", "onError: " + err);
                    }
                }).buile().addHeader("apikey", Constants.API_KEY).start();
    }

    public void setCurrMusic(Music m) {
        this.currMusic = m;
        File lrc = new File(currMusic.getPath().replace("mp3", "lrc"));
        if(!lrc.exists()){
            currLrc = null;
            tv.setLrc(null);
            String url = "http://apis.baidu.com/geekery/music/query";
            String httpArg = "s=" + Uri.encode(currMusic.getName()) + "&size=20&page=1";
            musicQuery(url + "?" + httpArg);
            return;
        }
        currLrc = new Lrc(lrc);
        tv.setLrc(currLrc);
        tv.setIndex(0);
        tv.invalidate();
    }

    public void lrcRefreash(int progress){
        if (currLrc==null){
            return;
        }
        for (int i = 0; i <currLrc.getLrcItems().size()-1 ; i++) {
            if (currLrc.getLrcItems().get(i).getStart() <= progress &&
                    currLrc.getLrcItems().get(i + 1).getStart() >= progress) {
                tv.setIndex(i);
                tv.invalidate();
                break;
            }
        }

    }
}
