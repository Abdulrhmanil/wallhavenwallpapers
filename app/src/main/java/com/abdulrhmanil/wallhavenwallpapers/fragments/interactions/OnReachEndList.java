package com.abdulrhmanil.wallhavenwallpapers.fragments.interactions;


/**
 * We defined a number of interaction with the UI:
 * Functional interface that determine the response to one interaction with the UI interface.
 * This interface is responsible on reach the bottom of the list, in other words when reach the
 * end of the list.
 * We decide that the response for that interact is to request a new thumb photos from
 * the website, to request the next 24 thumb photos to show them to the user.
 * The response is to add new photos.
 *
 */
@FunctionalInterface
public interface OnReachEndList {

    /**
     * When reach the bottom of the list (the end of the list),
     * request the next thumb photos from the website.
     * And add them to the recycler view.
     */
    void addNextPhotos();


    /**
     * A default method that call {@link #addNextPhotos() addNextPhotos} N times.
     * in case you want apply multiple request to the website... and add more than the next 24
     * thumb photos.
     * @param NTimes is the number of the times you want to call addNextPhotos method.
     */
    default void addNTimesNextPhotos(final int NTimes) {
        for (int i = 0; i < NTimes; i++) {
            addNextPhotos();
        }
    }
}
