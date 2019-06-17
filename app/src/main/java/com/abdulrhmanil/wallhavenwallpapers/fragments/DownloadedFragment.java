package com.abdulrhmanil.wallhavenwallpapers.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.abdulrhmanil.wallhavenwallpapers.DisplayUtil;
import com.abdulrhmanil.wallhavenwallpapers.R;
import com.abdulrhmanil.wallhavenwallpapers.activities.MainActivity;
import com.abdulrhmanil.wallhavenwallpapers.datasources.LocalPhotosDataSource;
import com.abdulrhmanil.wallhavenwallpapers.datasources.LocalPhotosDataSource.OnReloadLocalPhotosListener;
import com.abdulrhmanil.wallhavenwallpapers.fragments.adapters.LocalPhotosAdapter;
import com.abdulrhmanil.wallhavenwallpapers.fragments.adapters.ThumbPhotosAdapter;
import com.abdulrhmanil.wallhavenwallpapers.fragments.interactions.OnPressLocalPhoto;
import com.abdulrhmanil.wallhavenwallpapers.fragments.interactions.OnPressSelectedTap;
import com.abdulrhmanil.wallhavenwallpapers.fragments.interactions.OnSelectPage;
import com.abdulrhmanil.wallhavenwallpapers.fragments.interactions.OnSwipeDown;

/**
 * Responsible to show the local downloaded photos in a recycler view.
 * And provide a functionally to manage thus downloaded photos like :
 * show, delete, share, sat as wallpaper. This class is take the list
 * of the local photos from {@link LocalPhotosDataSource LocalPhotosDataSource}
 * class, so the list is synchronized and up to date.
 */
public class DownloadedFragment extends Fragment
        implements OnPressSelectedTap, OnSelectPage, OnSwipeDown,
        OnPressLocalPhoto, ScrollZoomedPhotosFragment.OnScrollZoomedPhotosListener {


    /** Singleton that hold the local photos list, and keep the list synchronized*/
    private final LocalPhotosDataSource localPhotosUtil = LocalPhotosDataSource.getInstance();

    /** Recycler view to show thumb photos*/
    protected RecyclerView recyclerView;

    /** Thumb photos adapter*/
    protected LocalPhotosAdapter adapter;

    /** Grid layout manager*/
    protected GridLayoutManager layoutManager;

    /** A context*/
    protected Context context;

    /** ThumbRes object that hold the thumb photo dimension, and num of photos in one row*/
    protected DisplayUtil.ThumbRes thumbRes;

    /** Swipe to refresh layout*/
    protected SwipeRefreshLayout swipeRefreshLayout;


    /** Default constructor, turn on the flag to retain fragment status, to prevent recreation*/
    public DownloadedFragment() {
        setRetainInstance(true);
    }


    /**
     * Refactoring method to create new instance of the DownloadedFragment.
     * @return a new instance of DownloadedFragment.
     */
    public static DownloadedFragment newInstance() {
        return new DownloadedFragment();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_downloaded, container, false);
        // Init the UI.
        this.initUI(view);
        return view;
    }


    /**
     * Setup the recycler view adapter {@link ThumbPhotosAdapter ThumbPhotosAdapter},
     * to show the thumb photos. Here we setup the UI.
     * @param view is the Inflated layout from xml file
     */
    protected void initUI(View view) {

        recyclerView = view.findViewById(R.id.rvLocalPhotos);
        context = view.getContext();
        thumbRes = DisplayUtil.getThumbRes(context);
        final int spanCount = thumbRes.numOfCols;
        layoutManager = new GridLayoutManager(context, spanCount);
        adapter = new LocalPhotosAdapter(context, localPhotosUtil.getLocalPhotosList(),this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this::reLoadPhotos);
    }


    /**
     * Test if the fragment is safe and NOT destroyed and
     * we able to make changes on the fragment UI.
     * @return {@code True} if the fragment is safe
     * to make UI changes, {@code False} otherwise.
     */
    public boolean isSafe() {
        return !(this.isRemoving() ||
                this.getActivity() == null ||
                this.isDetached() ||
                !this.isAdded() ||
                this.getView() == null);
    }


    /**
     * When press a new tap, NOT selected one,
     * notify that the data may changed and notify
     * the recycler view to refresh his content.
     */
    @Override
    public void notifyStatusChanges() {
        if (isSafe()) {
            adapter.notifyDataSetChanged();
        }
    }


    /**
     * When press a selected tap, back to the top of the list, in the recycler view.
     */
    @Override
    public void scrollBackToTop() {
        if (isSafe()) {
            adapter.notifyDataSetChanged();
            if (localPhotosUtil.getLocalPhotosList().size() > 0 && recyclerView != null) {
                recyclerView.smoothScrollToPosition(0);
            }
        }
    }


    /**
     * Reload the local (downloaded) photos from the storage.
     * Method that load all the local downloaded photos from
     * the default downloads folder then  clear the list and
     * recycler view and load the new photos from the default
     * downloads folder.
     */
    @Override
    public void reLoadPhotos() {
        localPhotosUtil.reLoad(afterLoadingListener);
    }


    /**
     * Listener as field of anonymous inner class,
     * that hold the behavior of the fragment after
     * reload all the photos from the default downloads folder.
     * In this listener we add the photos to the recycler view,
     * notifyDataSetChanged() method. then stop and hide the
     * refresh progress bar.
     */
    OnReloadLocalPhotosListener afterLoadingListener = () -> {
        if (isSafe()) {
            adapter.notifyDataSetChanged();
            DownloadedFragment.this.swipeRefreshLayout.setRefreshing(false);
        }
    };



    /*
    Implementation of OnPressLocalPhoto and OnScrollZoomedPhotosListener to
    communicate with ScrollZoomedPhotosFragment fragment, and allow him to
    notify me.
    */

    /**
     * When press a local photo in ({@link LocalPhotosAdapter LocalPhotosAdapter}),
     * we show that photo in fullscreen mode with zoom option,
     * and you can scroll between the zoomed photos while keep the scrolling synced between
     * the to lists (recyclers view).
     * <p>
     * The operation done in steps, after pressing on the local photo :
     * 1 - firstly we show (create) the scroll zoomed photos fragment to show the photos in
     * fullscreen mode.
     * 2 - secondly we move the recycler view to position of the pressed photo, so show the pressed
     * photo in the fullscreen mode.
     * 3 - while the user scroll between the fullscreen photos in
     * {@link ScrollZoomedPhotosFragment
     * ScrollZoomedPhotosFragment} you notify listener that the scroll position changed,
     * in his turn communicate with the
     * {@link MainActivity Main Activity} that
     * the scroll position changed, so the Main Activity communicate with
     * {@link DownloadedFragment DownloadedFragment}
     * so the DownloadedFragment can sync the scroll position, so when the user back to the
     * DownloadedFragment, will see where he stop. To keep the UI so friendly.
     *
     * @param startPosition is the position of the pressed local photo, we need it to move the
     *                      recycler view to that position, to show the user the same photo he
     *                      pressed in fullscreen mode. this position is the initialization index.
     */
    @Override
    public void startScrollZoomedPhotosFragment(int startPosition) {
        FragmentManager fragmentMgr = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentMgr.beginTransaction();

        ScrollZoomedPhotosFragment fragment = ScrollZoomedPhotosFragment.newInstance(startPosition);
        String name = fragment.getClass().getName();

        fragmentTransaction.replace(R.id.scroll_zoomed_photos_fragment_container, fragment);
        fragmentTransaction.addToBackStack(name);
        fragmentTransaction.commit();
    }


    /**
     * Method that change the scroll position of the
     * recycler view, it's scroll the the recycler view
     * to the specified position. we need this method
     * to keep the recycler position synced with other.
     * @param position is the new index (position) to scroll to them.
     */
    protected void syncScrollPosition(int position) {
        if (isSafe()) {
            recyclerView.scrollToPosition(position);
        }
    }


    /**
     * Call this method when the scroll position change
     * (when the viewed photo is change), to notify the
     * listeners that the position of the current showed
     * photo is changed so the listeners can keep their
     * recyclers views synced with the current scrolled
     * recycler view.
     *
     * This method allow to ScrollZoomedPhotosFragment to
     * communicate with this class, to keep the scroll
     * position synced. The communication done via the
     * MainActivity. This method called when the user
     * interact with the interface in
     * ScrollZoomedPhotosFragment and scroll the photos,
     * so this method called to keep the scrolling synced.
     *
     * This method call syncScrollPosition that changed
     * the recycler view position.
     *
     * @param position is the new index we reach in the
     *                 scroll of the fullscreen photos.
     */
    @Override
    public void onScrollPositionChanged(int position) {
        syncScrollPosition(position);
    }
}
