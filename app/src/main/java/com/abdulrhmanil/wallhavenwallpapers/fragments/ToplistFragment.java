package com.abdulrhmanil.wallhavenwallpapers.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.abdulrhmanil.wallhavenwallpapers.R;

import static com.abdulrhmanil.wallhavenwallpapers.datasources.ThumbPhotoDataSource.getToplistPhotos;

/**
 * This fragment is attends only with thumb photos from "Toplist" page.
 * Fragment that responsible to show and manage thumb photos of Toplist category, when
 * start this fragment we load the thumb photos from the website, and create the UI.
 * This fragment provide the response behavior when reach the bottom of the list,
 * this class add new thumb photos to the list and refresh the content, as well
 * provide the behavior when swipe down to refresh, it's clear the list content
 * and request thumb photos from the website.
 * This fragment is active when select "Toplist" in the bottom navigation.
 */
public class ToplistFragment extends AbstractThumbListFragment {

    /**
     * Load photos form the internet when create the fragment, every fragment provide
     * his own implementation, and determine to load or NOT to load photos from internet.
     */
    @Override
    protected void loadPhotosOnCreate() {
        reLoadPhotos();
    }


    /**
     * When reach the bottom of the list (the end of the list),
     * request the next thumb photos of "Toplist" category from
     * the website. And add them to the list and refresh recycler
     * view content.
     */
    @Override
    public void addNextPhotos() {
        getToplistPhotos(pageNum, super.addNextListener);
        pageNum++;
    }


    /**
     * Reload the thumb photos from the website, then clear
     * the content of the list and recycler view, reset the
     * page number and stop the rolling of the refresh progress
     * bar. Then load the new thumb photos into the list and
     * recycler view. The loading from "Toplist" page.
     */
    @Override
    public void reLoadPhotos() {
        pageNum = 1;
        getToplistPhotos(pageNum, super.reLoadListener);
        pageNum++;
        addNextPhotos();
    }


    /** Create the view and setup the UI */
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_toplist, container, false);
        // Init the UI
        super.initUI(view);
        return view;
    }


    /** Default constructor, must call super*/
    public ToplistFragment() {
        super();
    }


    /**
     * Refactoring method to create new instance of the ToplistFragment.
     * @return a new instance of ToplistFragment.
     */
    public static ToplistFragment newInstance() {
        return new ToplistFragment();
    }
}
