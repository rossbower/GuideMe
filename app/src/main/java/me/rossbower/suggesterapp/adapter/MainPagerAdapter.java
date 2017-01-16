package me.rossbower.suggesterapp.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import me.rossbower.suggesterapp.fragments.ListFragment;
import me.rossbower.suggesterapp.fragments.MapFragment;


public class MainPagerAdapter extends FragmentPagerAdapter {
    public MainPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = new ListFragment();
                break;
            case 1:
                fragment = new MapFragment();
                break;
            default:
                fragment = new ListFragment();
                break;
        }
        return fragment;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "List";
            case 1:
                return "Map";
        }
        return "List";
    }

    @Override
    public int getCount() {
        return 2;
    }
}
