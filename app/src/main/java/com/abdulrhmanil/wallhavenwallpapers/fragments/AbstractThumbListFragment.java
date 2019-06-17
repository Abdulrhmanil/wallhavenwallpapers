package com.abdulrhmanil.wallhavenwallpapers.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.abdulrhmanil.wallhavenwallpapers.DisplayUtil;
import com.abdulrhmanil.wallhavenwallpapers.R;
import com.abdulrhmanil.wallhavenwallpapers.datasources.ThumbPhotoDataSource.OnThumbPhotoArrivedListener;
import com.abdulrhmanil.wallhavenwallpapers.fragments.adapters.ThumbPhotosAdapter;
import com.abdulrhmanil.wallhavenwallpapers.fragments.interactions.OnPressSelectedTap;
import com.abdulrhmanil.wallhavenwallpapers.fragments.interactions.OnReachEndList;
import com.abdulrhmanil.wallhavenwallpapers.fragments.interactions.OnSelectPage;
import com.abdulrhmanil.wallhavenwallpapers.fragments.interactions.OnSwipeDown;
import com.abdulrhmanil.wallhavenwallpapers.photostructures.ThumbPhoto;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract class represent a fragment that manage a recycler view that contains bunch
 * of thumbs photos. Every fragment extend this class must manage a list of thumb photos.
 * For now: we have 3 pages "categories" that every category manage it's own thumb photos,
 * so we created 3 fragments.
 */
public abstract class AbstractThumbListFragment extends Fragment
        implements OnReachEndList, OnPressSelectedTap, OnSelectPage, OnSwipeDown {


    /**
     * Load photos form the internet when create the fragment, every fragment provide
     * his own implementation, and determine to load or NOT to load photos from internet.
     */
    abstract protected void loadPhotosOnCreate();


    /**
     * When override this method you should use {@link #initUI(View) initUI},
     * and send her as a parameter the inflated view from the specified xml resource,
     * or provide your own initialization of the UI.
     */
    abstract public View onCreateView(LayoutInflater inflater,
                                      @Nullable ViewGroup container,
                                      @Nullable Bundle savedInstanceState);


    /** The default size of the thumb list*/
    protected static final int DEF_SIZE = 100;

    /** The list of the thumb photos*/
    protected final List<ThumbPhoto> list = new ArrayList<>(DEF_SIZE);

    /** The current page number we reach*/
    protected static int pageNum = 1;

    /** Flag to determine if it's first time load the fragment*/
    protected boolean firstTimeLoaded = true;

    /** Recycler view to show thumb photos*/
    protected RecyclerView recyclerView;

    /** Thumb photos adapter*/
    protected ThumbPhotosAdapter adapter;

    /** Grid layout manager*/
    protected GridLayoutManager layoutManager;

    /** A context*/
    protected Context context;

    /** ThumbRes object that hold the thumb photo dimension, and num of photos in one row*/
    protected DisplayUtil.ThumbRes thumbRes;

    /** Swipe to refresh layout*/
    protected SwipeRefreshLayout swipeRefreshLayout;


    /** Default constructor, turn on the flag to retain fragment status, to prevent recreation*/
    public AbstractThumbListFragment() {
        setRetainInstance(true);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Start with loading the thumb photos, while rolling the refresh progress
        loadPhotosOnCreate();
    }


    /**
     * Setup the recycler view adapter {@link ThumbPhotosAdapter ThumbPhotosAdapter},
     * to show the thumb photos. Here we setup the UI.
     * @param view is the Inflated layout from xml file
     */
    protected void initUI(View view) {
        recyclerView = view.findViewById(R.id.rvThumbPhotos);
        context = view.getContext();
        thumbRes = DisplayUtil.getThumbRes(context);
        final int spanCount = thumbRes.numOfCols;
        layoutManager = new GridLayoutManager(context, spanCount);
        adapter = new ThumbPhotosAdapter(context, list,this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this::reLoadPhotos);

        if (firstTimeLoaded) {
            firstTimeLoaded = false;
            swipeRefreshLayout.setRefreshing(true);
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
        adapter.enableClick();
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
     * Test if the fragment is safe and NOT destroyed and
     * we able to make changes on the fragment UI.
     * @return {@code True} if the fragment is safe to make UI changes,
     * {@code False} otherwise.
     */
    public boolean isSafe() {
        return !(this.isRemoving() ||
                this.getActivity() == null ||
                this.isDetached() ||
                !this.isAdded() ||
                this.getView() == null);
    }


    /**
     * When press a selected tap, back to the top of the list, in the recycler view.
     */
    @Override
    public void scrollBackToTop() {
        if (isSafe()) {
            if (list.size() > 0 && recyclerView != null) {
                recyclerView.smoothScrollToPosition(0);
            }
        }
    }


    /**
     * Listener as field of anonymous inner class,
     * that hold the behavior of the fragment after
     * the thumb photos arrived from the internet.
     * In this listener we add the photos to the recycler
     * view and then we refresh the new add content.
     */
    protected final OnThumbPhotoArrivedListener addNextListener = new OnThumbPhotoArrivedListener() {

        @Override
        public void onResult(@NonNull List<ThumbPhoto> photos) {
            int positionStart = list.size()-1;
            int itemCount = photos.size();
            list.addAll(photos);
            if (isSafe()) {
                adapter.notifyItemRangeInserted(positionStart,itemCount);
            }

        }

        @Override
        public void onError(@NonNull Exception e) {
            if (isSafe()) {
                adapter.notifyDataSetChanged();
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    };


    /**
     * Listener as field of anonymous inner class,
     * that hold the behavior of the fragment after
     * we reload all the thumb photos of the fragment.
     * In this listener firstly we clear the content of
     * list then add the photos to the list and the recycler
     * view and then we refresh the content.
     */
    protected final OnThumbPhotoArrivedListener reLoadListener = new OnThumbPhotoArrivedListener() {

       @Override
       public void onResult(@NonNull List<ThumbPhoto> photos) {
           list.clear();
           addNextListener.onResult(photos);
           if (isSafe()) {
               adapter.notifyDataSetChanged();
               AbstractThumbListFragment.this.swipeRefreshLayout.setRefreshing(false);
           }
       }

       @Override
       public void onError(@NonNull Exception e) {
           addNextListener.onError(e);
           if (isSafe()) {
               AbstractThumbListFragment.this.swipeRefreshLayout.setRefreshing(false);
           }
       }
   };
}
