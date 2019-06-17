package com.abdulrhmanil.wallhavenwallpapers.fragments.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.abdulrhmanil.wallhavenwallpapers.fragments.DownloadedFragment;
import com.abdulrhmanil.wallhavenwallpapers.fragments.LatestFragment;
import com.abdulrhmanil.wallhavenwallpapers.fragments.RandomFragment;
import com.abdulrhmanil.wallhavenwallpapers.fragments.SearchFragment;
import com.abdulrhmanil.wallhavenwallpapers.fragments.ToplistFragment;

import java.util.ArrayList;
import java.util.List;


/**
 * Fragment Pager Adapter that manage all our fragments, we have in our application 4 fragments.
 * This class is manage them.
 */
public class ListFragmentPagerAdapter extends FragmentPagerAdapter {

    /** Toplist Fragment position in the Pager Adapter */
    public static final int TOPLIST_POSITION = 0;


    /** Latest Fragment position in the Pager Adapter */
    public static final int LATEST_POSITION = 1;


    /** Random Fragment position in the Pager Adapter */
    public static final int RANDOM_POSITION = 2;


    /** Latest Fragment position in the Pager Adapter */
    public static final int SEARCH_POSITION = 3;


    /** Downloaded Fragment position in the Pager Adapter */
    public static final int DOWNLOADED_POSITION = 4;


    /** List that hold all our fragments, we have 4 fragments in the app. */
    private List<Fragment> fragments = new ArrayList<>();


    /** The current fragment that showed to the user. */
    private Fragment currentFragment;


    /**
     * Constructor that create and init the fragment pager adapter (our adapter).
     * @param fm a new {@link ListFragmentPagerAdapter ListFragmentPagerAdapter} object.
     */
    public ListFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
        fragments.add(ToplistFragment.newInstance());
        fragments.add(LatestFragment.newInstance());
        fragments.add(RandomFragment.newInstance());
        fragments.add(SearchFragment.newInstance());
        fragments.add(DownloadedFragment.newInstance());
    }


    /**
     * Return the Fragment associated with a specified position.
     * @param position is the index of the fragment you want to get
     */
    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }


    /** Return the number of views available. */
    @Override
    public int getCount() {
        return fragments.size();
    }


    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        if (getCurrentFragment() != object) {
            currentFragment = ((Fragment) object);
        }
        super.setPrimaryItem(container, position, object);
    }
    

    /** Get the current fragment */
    public Fragment getCurrentFragment() {
        return currentFragment;
    }
}
