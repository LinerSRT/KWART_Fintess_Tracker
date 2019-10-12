package com.kwart.tracking.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class FragmentPageAdapter extends FragmentPagerAdapter {
    private Context context;
    private ArrayList<Class<? extends Fragment>> pages;

    public FragmentPageAdapter(FragmentManager fragmentManager, Context context){
        super(fragmentManager);
        this.context = context;
        pages = new ArrayList<>();
    }

    public void addPage(Class<?extends Fragment> page){
        pages.add(page);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return Fragment.instantiate(context, pages.get(position).getName());
    }

    @Override
    public int getCount() {
        return pages.size();
    }
}
