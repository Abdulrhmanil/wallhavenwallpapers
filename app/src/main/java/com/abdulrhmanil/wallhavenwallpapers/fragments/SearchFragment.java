package com.abdulrhmanil.wallhavenwallpapers.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.SearchView;

import com.abdulrhmanil.wallhavenwallpapers.R;
import com.abdulrhmanil.wallhavenwallpapers.photostructures.ThumbPhoto;

import java.util.List;

import static android.widget.SearchView.OnQueryTextListener;
import static com.abdulrhmanil.wallhavenwallpapers.datasources.ThumbPhotoDataSource.OnThumbPhotoArrivedListener;
import static com.abdulrhmanil.wallhavenwallpapers.datasources.ThumbPhotoDataSource.getSearchedPhotos;


public class SearchFragment extends AbstractThumbListFragment {

    /** To search photos and show them*/
    SearchView searchView;

    /** To show No results message if there NO results after search*/
    CardView cardView;


    /**
     * Load photos form the internet when create the fragment, every fragment provide
     * his own implementation, and determine to load or NOT to load photos from internet.
     * In Search fragment we will NOT load photos, because we do search, and wait to user input.
     */
    @Override
    protected void loadPhotosOnCreate() {}


    /**
     * When reach the bottom of the list (the end of the list),
     * request the next thumb photos from the website.
     * And add them to the recycler view.
     */
    @Override
    public void addNextPhotos() {
        final String word = searchView.getQuery().toString();
        getSearchedPhotos(pageNum, word, super.addNextListener);
        pageNum++;
    }


    /**
     * Just reload all the content to the list and to the recycler view.
     */
    @Override
    public void reLoadPhotos() {
        pageNum = 1;
        final String word = searchView.getQuery().toString();
        getSearchedPhotos(pageNum, word, this.reLoadListener);
        pageNum++;
        addNextPhotos();
    }


    /** Create the view and setup the UI */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_search, container, false);
        // Init the UI
        this.initUI(view);
        return view;
    }


    /** Setup the search bar in the UI */
    protected void initUI(View view) {
        super.initUI(view);
        swipeRefreshLayout.setRefreshing(false);
        searchView = view.findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(searchListener);
        cardView = view.findViewById(R.id.noResults);
    }


    /**
     * Show the message "No Results found",
     * if there no results found, after applying search.
     * Or applying search and moved to another fragment.
     */
    private void showNoResultsCardIfNeeded(){
        if (isSafe()) {
            final String word = searchView.getQuery().toString();
            if ((!word.equals("")) && list.size() == 0) {
                cardView.setVisibility(View.VISIBLE);
            }
        }
    }


    /**
     * Hide the message "No Results found" after clear
     * the searched text, to apply new search or just
     * to clear the old search and to clear the message
     * "No results found".
     */
    private void hideNoResultsCardIfNeeded() {
        if (isSafe()) {
            final String word = searchView.getQuery().toString();
            if (word.equals("") && list.size() == 0) {
                cardView.setVisibility(View.GONE);
            }
        }
    }


    /** Force hiding "No Results found", use it after press search button*/
    private void hideNoResultsCard(){
        if (isSafe()) {
            cardView.setVisibility(View.GONE);
        }
    }


    /** Default constructor, must call super*/
    public SearchFragment() {
        super();
    }



    @Override
    public void onResume() {
        super.onResume();
        // If apply search early, and NOT found results, then show "No Results found" message.
        showNoResultsCardIfNeeded();
    }


    /**
     * Refactoring method to create new instance of the SearchFragment.
     * @return a new instance of SearchFragment.
     */
    public static SearchFragment newInstance() {
        return new SearchFragment();
    }


    /** Method to hide the keyboard after pressing search (enter button)*/
    private void hideKeyboard() {
        if (isSafe()) {
            InputMethodManager imm = (InputMethodManager) context
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
            }
        }
    }


    /**
     * Here we apply the searching, and get the results from the website,
     * and show them to user. Firstly clear the old results, load searched
     * photos from the website, while show the loading bar, and hide keyboard.
     * And when the results show up, we show them to user, and hide the loading bar
     * (in the listener).
     */
    private void applySearch() {
        hideNoResultsCard();
        list.clear();
        SearchFragment.this.adapter.notifyDataSetChanged();
        swipeRefreshLayout.setRefreshing(true);
        reLoadPhotos();
        searchView.clearFocus();
        hideKeyboard();
    }


    /**
     * The listener of interaction with {@link SearchView searchView},
     * when press enter we apply searching and call applySearch method.
     */
    private final OnQueryTextListener searchListener = new OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {
            applySearch();
            return true;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            hideNoResultsCardIfNeeded();
            //hideNoResultsCard();
            return true;
        }
    };


    /**
     * Listener as field of anonymous inner class,
     * that hold the behavior of the fragment after
     * we reload all the thumb photos of the fragment.
     * In this listener firstly we clear the content of
     * list then add the photos to the list and the recycler
     * view and then we refresh the content.
     * Behave exactly like super (and other fragments),
     * just in the end check if there NO results found,
     * if there NO results found show appropriate message.
     */
    protected final OnThumbPhotoArrivedListener reLoadListener = new OnThumbPhotoArrivedListener() {
        @Override
        public void onResult(@NonNull List<ThumbPhoto> photos) {
            SearchFragment.super.reLoadListener.onResult(photos);
            showNoResultsCardIfNeeded();
        }

        @Override
        public void onError(@NonNull Exception e) {
            SearchFragment.super.reLoadListener.onError(e);
        }
    };

}
