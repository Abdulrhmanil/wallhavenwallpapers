package com.abdulrhmanil.wallhavenwallpapers.fragments.interactions;


/**
 * We defined a number of interaction with the UI:
 * Functional interface that determine response to one interaction with the UI interface.
 * This interface is responsible on the pressing on NOT pressed (selected) tap on the bottom
 * navigation.
 * We decide that the response for that interact is to notify the fragment that may the data
 * is changed and should refresh the recycler view, specially in the downloads tab.
 */
@FunctionalInterface
public interface OnSelectPage {

    /**
     * When press a new tap, NOT selected one,
     * notify that the data may changed and notify
     * the recycler view to refresh his content.
     */
    void notifyStatusChanges();
}
