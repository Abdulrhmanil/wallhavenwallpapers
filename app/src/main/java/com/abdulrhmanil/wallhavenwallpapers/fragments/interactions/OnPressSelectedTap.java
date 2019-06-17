package com.abdulrhmanil.wallhavenwallpapers.fragments.interactions;


/**
 * We defined a number of interaction with the UI:
 * Functional interface that determine response to one interaction with the UI interface.
 * This interface is responsible on the pressing on a pressed (selected) tap on the bottom
 * navigation.
 * We decide that the response for that interact is to back to top of the list.
 */
@FunctionalInterface
public interface OnPressSelectedTap {

    /**
     * When press a selected tap, back to the top of the list, in the recycler view.
     */
    void scrollBackToTop();
}
