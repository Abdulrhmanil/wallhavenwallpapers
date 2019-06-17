package com.abdulrhmanil.wallhavenwallpapers.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.abdulrhmanil.wallhavenwallpapers.R;
import com.abdulrhmanil.wallhavenwallpapers.datasources.LocalPhotosDataSource;
import com.abdulrhmanil.wallhavenwallpapers.fragments.adapters.ZoomedPhotosAdapter;
import com.gw.swipeback.SwipeBackLayout;

import static com.abdulrhmanil.wallhavenwallpapers.GlobalConstant.KEY_EXTRA_CURRENT_POSITION;


/**
 * Responsible to show the local downloaded photos in fullscreen mode
 * with zoom option in a recycler view, this fragment is hold listener
 * object and notify him when the the user scroll the photos in fullscreen
 * mode, the listener is attached after the creation, in other words,
 * the activity that maintain this fragment must implement this interface,
 * the interface is {@link OnScrollZoomedPhotosListener }, if NOT we throw
 * {@link RuntimeException}.
 * This class is take the list of the local photos from
 * {@link LocalPhotosDataSource LocalPhotosDataSource}
 * class, so the list is synchronized and up to date.
 */
public class ScrollZoomedPhotosFragment extends Fragment {

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that activity.
     *
     * In our case this listener communicate with Main Activity and call his method
     * when the user scroll the fullscreen photos, and when the showed photo change
     * (the scroll position changed) so the method notify that the position of the
     * showed photo is changed, and the Main Activity notify Downloaded Fragment,
     * also call onScrollPositionChanged method in Downloaded Fragment, so the
     * Downloaded Fragment can keep the scroll position synced.
     * When the user back to Downloaded Fragment, he will find the point where he
     * stop scrolling in fullscreen mode, to make the application friendlily.
     */
    public interface OnScrollZoomedPhotosListener {


        /**
         * Call this method when the scroll position change
         * (when the viewed photo is change), to notify the
         * listeners that the position of the current showed
         * photo is changed so the listeners can keep their
         * recyclers views synced with the current scrolled
         * recycler view.
         *
         * @param position is the new index we reach in the
         *                 scroll of the fullscreen photos.
         */
        void onScrollPositionChanged(int position);
    }


    /** A context*/
    protected Context context;


    /** Recycler view to the local photos in fullscreen mode with zoom option*/
    protected RecyclerView recyclerView;


    /** Zoomed photo in fullscreen mode adapter*/
    protected ZoomedPhotosAdapter adapter;


    /** Linear layout manager*/
    protected LinearLayoutManager layoutManager;


    /**
     * Layout for swipe to back, we need it to
     * set a listener when swipe to back to the
     * previous fragment and NOT to finish the
     * activity. Need it just to set a listener.
     */
    protected SwipeBackLayout swipeBackLayout;


    /**
     * The position of the start photo we show the user, when create
     * the fragment. in other words it's the index of the photo we
     * show in fullscreen mode, the position (index) we receive from
     * fragment caller in the factory method.
     */
    protected int startPosition;


    /**
     * A listener that listen to the position (index) of the current
     * showed photo in fullscreen mode, in other words it listen to
     * the scroll of the recycler view, and when the current photo
     * is changed we notify the Main activity that the scroll position
     * is changed, so in her turn can notify other fragments that the
     * scroll position is changed, so they can keep their scrolling
     * synced for the user, when back to the other fragments.
     */
    private OnScrollZoomedPhotosListener interactionListener;


    /** Must default constructor*/
    public ScrollZoomedPhotosFragment(){}


    /**
     * Refactoring method to create new instance of the ScrollZoomedPhotosFragment.
     * @param startPosition is the index (position) of the clicked local photo,
     *                      in the list of the local photos.
     *                      That the user want to show in fullscreen mode.
     * @return a new instance of ScrollZoomedPhotosFragment.
     */
    public static ScrollZoomedPhotosFragment newInstance(int startPosition) {
        ScrollZoomedPhotosFragment fragment = new ScrollZoomedPhotosFragment();
        Bundle args = new Bundle();
        args.putInt(KEY_EXTRA_CURRENT_POSITION, startPosition);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            startPosition = getArguments().getInt(KEY_EXTRA_CURRENT_POSITION);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_scroll_zoomed_photos,
                container, false);
        // Init the UI.
        this.initUI(view);
        return view;
    }


    /**
     * Setup the recycler view to show the local photos,
     * setup the snap helper to allow grip scrolling,
     * setup the listener of the recycler view scrolling,
     * setup the listener of the swipe to back to previous
     * fragment. Here we setup all the UI.
     * @param view is the Inflated layout from xml file.
     */
    protected void initUI(View view) {

        context = view.getContext();
        recyclerView = view.findViewById(R.id.rvZoomedPhotos);
        swipeBackLayout = view.findViewById(R.id.swipeBackLayout);

        layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        adapter = new ZoomedPhotosAdapter(context);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);
        recyclerView.addOnScrollListener(recyclerViewOnScrollListener);
        swipeBackLayout.setSwipeBackListener(onSwipeBackListener);

        recyclerView.scrollToPosition(startPosition);
    }


    /**
     * Method that call the {@link #interactionListener interaction Listener}
     * method onScrollPositionChanged, to notify the listeners that the position
     * of the current showed photo is changed, so they can keep scroll synchronization.
     * We call this method in {@link #recyclerViewOnScrollListener recyclerViewOnScrollListener}
     * that we provide for the recycler view that show our fullscreen photos in
     * {@link RecyclerView#addOnScrollListener(RecyclerView.OnScrollListener) addOnScrollListener}
     * method, so when the user scroll the recycler view, the method
     * {@link RecyclerView#onScrolled(int, int) onScrolled} is called,
     * and IN this method we call our method notifyScrollPositionChanged to
     * notify the other listener that the user is scrolling the recycler view so they
     * update their recycler and keep synchronization.
     * @param position
     */
    public void notifyScrollPositionChanged(int position) {
        if (interactionListener != null) {
            interactionListener.onScrollPositionChanged(position);
        }
    }


    /**
     * Just attach the the activity creator as a listener of the user interaction,
     * as a listener of the scrolling of the recycler view, to notify him later.
     * @param context is the context of the activity that maintain this fragment.
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnScrollZoomedPhotosListener) {
            interactionListener = (OnScrollZoomedPhotosListener) context;
        }
        else {
            throw new RuntimeException(context.toString()
                    + " must implement OnScrollZoomedPhotosListener");
        }
    }


    /** Just detach the the maintain activity to be interaction listener*/
    @Override
    public void onDetach() {
        super.onDetach();
        interactionListener = null;
    }


    /**
     * A listener of the scrolling of the recycler view, the listener
     * is just call {@link #notifyScrollPositionChanged(int)} method
     * to notify others that the scroll position is changed, in other
     * words, the user is interact with the fragment and scroll the photos.
     */
    RecyclerView.OnScrollListener recyclerViewOnScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            int itemPosition = ((LinearLayoutManager)
                    recyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
            if (itemPosition != -1 ) {
                notifyScrollPositionChanged(itemPosition);
            }
        }
    };


    /**
     * A listener when the user swipe the photo to bottom,
     * we back to the previous page or previous fragment.
     * NOT to finish the activity that maintain this fragment.
     * To make the UI is friendlily and responsive.
     */
    SwipeBackLayout.OnSwipeBackListener onSwipeBackListener = new SwipeBackLayout.OnSwipeBackListener() {
        @Override
        public void onViewPositionChanged(View mView, float swipeBackFraction, float SWIPE_BACK_FACTOR) {
            swipeBackLayout.invalidate();
        }

        @Override
        public void onViewSwipeFinished(View mView, boolean isEnd) {
            if (isEnd) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        }
    };
}
