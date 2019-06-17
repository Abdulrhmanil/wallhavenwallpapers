package com.abdulrhmanil.wallhavenwallpapers.activities;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import com.abdulrhmanil.wallhavenwallpapers.R;
import com.abdulrhmanil.wallhavenwallpapers.fragments.DownloadedFragment;
import com.abdulrhmanil.wallhavenwallpapers.fragments.ScrollZoomedPhotosFragment;
import com.abdulrhmanil.wallhavenwallpapers.fragments.ScrollZoomedPhotosFragment.OnScrollZoomedPhotosListener;
import com.abdulrhmanil.wallhavenwallpapers.fragments.adapters.ListFragmentPagerAdapter;
import com.abdulrhmanil.wallhavenwallpapers.fragments.interactions.OnPressSelectedTap;
import com.abdulrhmanil.wallhavenwallpapers.fragments.interactions.OnSelectPage;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigation.OnTabSelectedListener;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationAdapter;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationViewPager;

/**
 * {@link MainActivity} is the start point of the app, here we build our fragments (4 fragments),
 * and {@link ListFragmentPagerAdapter fragment pager adapter} with
 * {@link AHBottomNavigation bottom navagtion} and {@link AHBottomNavigationViewPager view pager}
 * that let us navigate between pages.
 * Here we also test if we connect to internet and if NOT we notify the user with a dialog. here
 * also we request permission to read/write to the storage.
 */
public class MainActivity extends AppCompatActivity
        implements ScrollZoomedPhotosFragment.OnScrollZoomedPhotosListener {



    /** The default page and the default bottom navigation */
    private static final int DEFAULT_PAGE = 0;


    /** Code of the WRITE_EXTERNAL_STORAGE permission */
    private final static int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 100;


    /** Our Fragment pager adapter, Here we create and add all the fragments*/
    private ListFragmentPagerAdapter pagerAdapter =
            new ListFragmentPagerAdapter(getSupportFragmentManager());



    /** Customized bottom navigation, let us change colors,background very easily */
    private AHBottomNavigation bottomNavigation;


    /** Bottom navigation view pager let us swipe between pages, work nicely with
     * {@link AHBottomNavigation bottom navigation}*/
    private AHBottomNavigationViewPager viewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUI();
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (viewPager.getCurrentItem() == DEFAULT_PAGE) {
            pageChangeListener.onPageSelected(DEFAULT_PAGE);
        }
    }


    /**
     * Here we set everything, find views set navigation, pages, listeners, and
     * request storage Permission. we call this method in {@link #onCreate(Bundle) onCreate method}.
     */
    private void setUI() {
        //order is matter
        findViewsById();
        setBottomNavigation();
        setViewPager();
        setListeners();
        requestStoragePermission();
    }

    /**
     * Here we finds all views by id, define them in into fields.
     */
    private void findViewsById() {
        viewPager = findViewById(R.id.viewPager);
        bottomNavigation = findViewById(R.id.bottom_navigation);
    }


    /**
     * Set the settings of bottom navigation, colors, state, primary color...
     */
    private void setBottomNavigation() {
        AHBottomNavigationAdapter navigationAdapter = new AHBottomNavigationAdapter(this, R.menu.navigation);
        navigationAdapter.setupWithBottomNavigation(bottomNavigation, null);
        bottomNavigation.setTitleState(AHBottomNavigation.TitleState.ALWAYS_SHOW);
        bottomNavigation.setAccentColor(getResources().getColor(R.color.colorPrimary));
    }


    /**
     * Set the settings of view pager, default page, enable paging (swipe left and right).
     */
    private void setViewPager() {
        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(DEFAULT_PAGE);
        viewPager.setPagingEnabled(true);
    }


    /**
     * Set listeners of the {@link #bottomNavigation bottomNavigation} and
     * {@link #viewPager viewPager}.
     */
    private void setListeners() {
        viewPager.addOnPageChangeListener(pageChangeListener);
        bottomNavigation.setOnTabSelectedListener(tabSelectedListener);
    }


    /**
     * request a permission from the user to read/write to the storage.
     */
    private void requestStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //requestStoragePermission
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
            }
        }
    }


    /**
     * Method that test if we connected to the internet, return true if we are connected or connecting
     * to internet right now, false otherwise.
     * @return true if we are connected or connecting to internet right now, false otherwise.
     */
    private boolean isOnline() {
        final ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo netInfo = cm != null ? cm.getActiveNetworkInfo() : null;
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }


    /**
     * Method that show NO internet connection alter, that user can understand that NO internet,
     * user can dismiss this alter.
     */
    private void showAlertDialog() {
        final AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle("Connect to the Internet")
                .setMessage("You're offline. Check your connection")
                .setNegativeButton("dismiss", (dialog, which) -> {})
                .setIcon(android.R.drawable.ic_dialog_alert).create();
        alertDialog.show();
    }


    /**
     * Method run {@link #isOnline() isOnline} method to check if we are connected or connecting
     * to internet, and if we are NOT then run  {@link #showAlertDialog() showAlertDialog} to
     * notify the user with dialog that there are NO connection to internet.
     */
    private void alterIfNotConnected() {
        if (! isDestroyed()) {
            if (!isOnline()) {
                showAlertDialog();
            }
        }
    }


    /**
     * Listener that define the behavior when interact with the bottom navigation.
     */
    private final OnTabSelectedListener tabSelectedListener = new OnTabSelectedListener() {

        @Override
        public boolean onTabSelected(int position, boolean wasSelected) {
            if (wasSelected) {
                ((OnPressSelectedTap)pagerAdapter.getCurrentFragment()).scrollBackToTop();
                return true;
            }
            viewPager.setCurrentItem(position, true);
            return true;
        }
    };


    /**
     * Listener that define the behavior when interact with the view pager.
     */
    private final OnPageChangeListener pageChangeListener = new OnPageChangeListener() {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

        @Override
        public void onPageSelected(int position) {
            bottomNavigation.setCurrentItem(position,false);
            ((OnSelectPage)pagerAdapter.getItem(position)).notifyStatusChanges();
            alterIfNotConnected();
        }

        @Override
        public void onPageScrollStateChanged(int state) {}

    };


    /**
     * Call this method when the scroll position change
     * (when the viewed photo is change), to notify the
     * listeners that the position of the current showed
     * photo is changed so the listeners can keep their
     * recyclers views synced with the current scrolled
     * recycler view.
     *
     * This method called to notify other listeners, it allow
     * ScrollZoomedPhotosFragment to communicate with other
     * fragments (DownloadedFragment) to keep their scrolling
     * synced.
     * @param position is the new index we reach in the
     *                 scroll of the fullscreen photos.
     */
    @Override
    public void onScrollPositionChanged(int position) {

        Fragment currentFragment = pagerAdapter.getCurrentFragment();
        if (currentFragment != null
                && currentFragment instanceof OnScrollZoomedPhotosListener
                && currentFragment instanceof DownloadedFragment) {
            ((OnScrollZoomedPhotosListener)currentFragment).onScrollPositionChanged(position);
        }
    }
}


