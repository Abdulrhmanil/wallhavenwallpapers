package com.abdulrhmanil.wallhavenwallpapers.fragments.interactions;


/**
 * We defined a number of interaction with the UI:
 * Functional interface that determine the response to one interaction with the UI interface.
 * This interface is responsible on swipe down from the top of the list (neutral act to refresh).
 * We decide that the response for that interact is refresh the content if the list, in other
 * word is to request the content from the website from the beginning, clean the list and the
 * recycler view from the old content. and then add the new content...
 * `when refresh the local photos list, do exactly the same except read the content from
 * the local storage.
 */
@FunctionalInterface
public interface OnSwipeDown {

    /**
     * Just reload all the content to the list and to the recycler view.
     */
    void reLoadPhotos();
}
