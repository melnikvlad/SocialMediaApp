package com.example.vlad.scruji.Main_Screen_With_Tabs.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.vlad.scruji.Constants.Constants;
import com.example.vlad.scruji.R;

import static android.support.v4.view.PagerAdapter.POSITION_NONE;


public class MainScreenWithTabsFragment extends android.support.v4.app.Fragment {
    ViewPager viewPager;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        int[] icons = {R.drawable.profile, R.drawable.search2, R.drawable.settings2, R.drawable.chat2};
        View view = inflater.inflate(R.layout.fragment_main_screen_with_tabs,container,false);

        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.tab_layout);
        viewPager = (ViewPager) view.findViewById(R.id.main_tab_content);

        viewPager.setAdapter(new ViewPagerAdapter(getChildFragmentManager(),getActivity()));
        tabLayout.setupWithViewPager(viewPager);

        for (int i = 0; i < icons.length; i++) {
            tabLayout.getTabAt(i).setIcon(icons[i]);
        }
        tabLayout.getTabAt(0).select();

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
            switch (position) {
                case 0:
                    Home home = new Home();

                    return home;
                case 1: Filter filter = new Filter();
                    return filter;
                case 2: Settings settings = new Settings();
                    return settings;
                case 3: Chats chats = new Chats();
                    return chats;
                default:
                    home = new Home();

                    return home;
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

        @Override
        public void notifyDataSetChanged() {
           viewPager.getAdapter().notifyDataSetChanged();

        }
    }

}
