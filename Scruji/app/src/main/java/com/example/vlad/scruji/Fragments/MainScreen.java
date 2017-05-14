package com.example.vlad.scruji.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.vlad.scruji.Constants.Constants;
import com.example.vlad.scruji.R;


public class MainScreen extends android.support.v4.app.Fragment {
    ViewPager viewPager;
    TabLayout tabLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final int[] icons = {R.drawable.profile,R.drawable.settings2, R.drawable.chat2};
        View view = inflater.inflate(R.layout.fragment_main_screen_with_tabs,container,false);
        tabLayout = (TabLayout) view.findViewById(R.id.tab_layout);
        viewPager = (ViewPager) view.findViewById(R.id.main_tab_content);
        viewPager.setAdapter(new ViewPagerAdapter(getChildFragmentManager(),getActivity()));
        tabLayout.setupWithViewPager(viewPager);

        for (int i = 0; i < icons.length; i++) {
            tabLayout.getTabAt(i).setIcon(icons[i]);
        }
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                viewPager.getAdapter().notifyDataSetChanged();
                for (int i = 0; i < icons.length; i++) {
                    tabLayout.getTabAt(i).setIcon(icons[i]);
                }
            }
            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        return view;
    }

    class ViewPagerAdapter extends FragmentStatePagerAdapter {
        Context context;
        public ViewPagerAdapter(FragmentManager fm,Context context) {
            super(fm);
            this.context =context;
        }
        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            android.support.v4.app.Fragment fragment;
            switch (position) {
                case 0:
                    fragment = new Home();
                    return fragment;
                case 1: fragment = new Settings();
                    return fragment;
                case 2: fragment = new Dialogs();
                    return fragment;
                default:
                    return null;
            }
        }
        @Override
        public int getCount() {
            return Constants.TABS_COUNT;
        }
        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }
    }
}
