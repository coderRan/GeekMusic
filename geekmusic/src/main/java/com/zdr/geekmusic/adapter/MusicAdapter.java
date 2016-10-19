package com.zdr.geekmusic.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.zdr.geekmusic.R;
import com.zdr.geekmusic.entity.Music;

import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Music 适配器
 * Created by zdr on 16-9-1.
 */
public class MusicAdapter extends BaseAdapter {
    private List<Music> mData;
    private Context mContext;
    private MusicOperation operation;
    private Music playing = null;

    public MusicAdapter(List<Music> mData, Context mContext) {
        this.mData = mData;
        this.mContext = mContext;

        Collections.sort(mData);
    }

    @Override
    public int getCount() {
        return this.mData.size();
    }

    @Override
    public Object getItem(int position) {
        return this.mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {

            convertView = LayoutInflater.from(mContext)
                    .inflate(R.layout.music_listview_item, null, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tvMusicName.setText(mData.get(position).getName());
        holder.tvMusicArtist.setText(mData.get(position).getArtist());
        holder.ivMusicOperation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (operation != null) {
                    operation.operation(mData.get(position).getName());
                }
            }
        });
        if(mData.get(position).equals(playing)){
            holder.ivListPlay.setVisibility(View.VISIBLE);
        }else {
            holder.ivListPlay.setVisibility(View.GONE);
        }
        return convertView;
    }

    static class ViewHolder {
        @BindView(R.id.tv_music_name)
        TextView tvMusicName;
        @BindView(R.id.tv_music_artist)
        TextView tvMusicArtist;
        @BindView(R.id.iv_music_operation)
        ImageView ivMusicOperation;
        @BindView(R.id.iv_list_play)
        ImageView ivListPlay;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    //音乐操作接口
    public interface MusicOperation {
        void operation(String name);
    }

    public void setOperation(MusicOperation operation) {
        this.operation = operation;
    }

    public int getFirstIndexForLetters(String posiStr) {

        //获得position应该属于哪个分组，即首字母第一次出现的位置
        for (int i = 0; i < getCount(); i++) {
            String str = mData.get(i).getNameLetters();
            if (posiStr.equals(str)) {
                return i;
            }
        }
        return -1;
    }

    public void setPlaying(Music playing) {
        this.playing = playing;
    }
}
