package com.zdr.geekmusic.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

/**
 * 主界面ViewPage的适配器
 * Created by zdr on 16-9-12.
 */
public class MainVPAdapter extends FragmentStatePagerAdapter {
    private List<Fragment> mData;
    private List<String> tabData;
    public MainVPAdapter(FragmentManager fm, List<Fragment> data,List<String> tabData) {
        super(fm);
        mData = data;
        this.tabData = tabData;
    }



    @Override
    public Fragment getItem(int position) {
        return mData.get(position);
    }

    @Override
    public int getCount() {
        return mData.size();
    }


    //将viewpage与tablayout关联起来，设置tab的title需要重写下面两个方法
    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public CharSequence getPageTitle(int position) {

        return this.tabData.get(position);
    }


}
