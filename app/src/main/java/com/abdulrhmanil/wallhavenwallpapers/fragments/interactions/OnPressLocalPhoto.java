package com.abdulrhmanil.wallhavenwallpapers.fragments.interactions;


/**
 * Notice: this interface interaction is very important because it's show a
 * ScrollZoomedPhotosFragment fragment, that show the photo in fullscreen with zooming option
 * while communicate with DownloadedFragment fragment (via MainActivity) to sync the position
 * of the recycler view when scroll the fullscreen photos.
 *
 * We defined a number of interaction with the UI:
 * Functional interface that determine response to one interaction with the UI interface.
 * This interface is responsible on pressing local photo (the image it's self), the response
 * for that interact is show the local photo in fullscreen mode.
 * While we show the fullscreen mode we give the option to scroll between photos and zoom them.
 */
@FunctionalInterface
public interface OnPressLocalPhoto {


    /**
     * When press a local photo, we show that photo in fullscreen mode with zoom option,
     * and you can scroll between the zoomed photos while keep the scrolling synced between
     * the to lists (recyclers view).
     * <p>
     * The operation done in steps, after pressing on the local photo :
     * 1 - firstly we show the scroll zoomed photos fragment to show the photos in fullscreen mode
     * 2 - secondly we move the recycler view to position of the pressed photo, so show the pressed
     * photo in the fullscreen mode.
     * 3 - while the user scroll between the fullscreen photos in
     * {@link com.abdulrhmanil.wallhavenwallpapers.fragments.ScrollZoomedPhotosFragment
     * ScrollZoomedPhotosFragment} you notify listener that the scroll position changed,
     * in his turn communicate with the
     * {@link com.abdulrhmanil.wallhavenwallpapers.activities.MainActivity Main Activity} that
     * the scroll position changed, so the Main Activity communicate with
     * {@link com.abdulrhmanil.wallhavenwallpapers.fragments.DownloadedFragment DownloadedFragment}
     * so the DownloadedFragment can sync the scroll position, so when the user back to the
     * DownloadedFragment, will see where he stop. To keep the UI so friendly.
     *
     * @param startPosition is the position of the pressed local photo, we need it to move the
     *                      recycler view to that position, to show the user the same photo he
     *                      pressed in fullscreen mode. this position is the initialization index.
     */
    void startScrollZoomedPhotosFragment(int startPosition);
}
