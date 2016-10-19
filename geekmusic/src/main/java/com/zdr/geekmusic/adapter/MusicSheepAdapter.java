package com.zdr.geekmusic.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.zdr.geekmusic.R;
import com.zdr.geekmusic.entity.MusicSheep;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 歌曲列表的适配器。
 * Created by zdr on 16-9-12.
 */
public class MusicSheepAdapter extends BaseAdapter {
    private List<MusicSheep> mData;
    private Context mContext;
    private SheepOperation listener;
    public MusicSheepAdapter(List<MusicSheep> data, Context context) {
        mData = data;
        mContext = context;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
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
                    .inflate(R.layout.sheep_list_view_item, null, false);
            holder = new ViewHolder(convertView);

            convertView.setTag(holder);
        }else
        {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.mTvGroupName.setText(mData.get(position).getName());
        holder.mIvMusicOperation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener!=null){
                    listener.delete(mData.get(position));
                }
            }
        });
        return convertView;
    }

    static class ViewHolder {
        @BindView(R.id.tv_group_name)
        TextView mTvGroupName;
        @BindView(R.id.iv_music_operation)
        ImageView mIvMusicOperation;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
    public void setData(List<MusicSheep> data){
        mData.clear();
        mData.addAll(data);
    }

    public interface SheepOperation{
        void delete(MusicSheep ms);
    }

    public void setListener(SheepOperation listener) {
        this.listener = listener;
    }
}
