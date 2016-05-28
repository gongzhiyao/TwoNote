package com.example.gongzhiyao.twonote03.Recordlist_fragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.gongzhiyao.twonote03.recordsoundActivity.Record_Sound_list;

import java.util.List;

/**
 * Created by 宫智耀 on 2016/4/18.
 */
public class ViewPager_adapter extends FragmentPagerAdapter {
    private List<Fragment> mFragments;

    public ViewPager_adapter(FragmentManager fm, List<Fragment> fragments) {//重写构造方法
        super(fm);
        mFragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return Record_Sound_list.titleList.get(position);
    }
}
