package com.abdulrhmanil.wallhavenwallpapers.fragments.adapters.utilities;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.abdulrhmanil.wallhavenwallpapers.datasources.LocalPhotosDataSource;
import com.abdulrhmanil.wallhavenwallpapers.fragments.adapters.ThumbPhotosAdapter;
import com.abdulrhmanil.wallhavenwallpapers.photostructures.PhotoCache;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import static com.abdulrhmanil.wallhavenwallpapers.GlobalConstant.DEFAULT_PATH;
import static com.abdulrhmanil.wallhavenwallpapers.GlobalConstant.MAX_QUALITY;
import static com.abdulrhmanil.wallhavenwallpapers.activities.ShowPhotoActivity.getFullPhotosCaching;
import static com.abdulrhmanil.wallhavenwallpapers.datasources.LocalPhotosDataSource.getInstance;
import static com.abdulrhmanil.wallhavenwallpapers.datasources.PhotoCacheDataSource.OnImageFileSavedListener;
import static com.abdulrhmanil.wallhavenwallpapers.datasources.PhotoCacheDataSource.OnPhotoCacheArrivedListener;
import static com.abdulrhmanil.wallhavenwallpapers.datasources.PhotoCacheDataSource.getPhotoCache;
import static com.abdulrhmanil.wallhavenwallpapers.datasources.PhotoCacheDataSource.saveImageFile;


/**
 * It's a kind of util class, we use in the {@link ThumbPhotosAdapter ThumbPhotosAdapter} class,
 * it's responsible to download the photo externally with the inline button, and when it's done.
 * it notify witch photo (id and position) have been downloaded.
 * This class is implement {@link OnPhotoCacheArrivedListener OnPhotoCacheArrivedListener}
 * and {@link OnImageFileSavedListener OnImageFileSavedListener} listeners.
 */
final public class InlineDownloader
        implements OnPhotoCacheArrivedListener, OnImageFileSavedListener {



    /**
     * Who want to use this util class, must provide an
     * {@link OnFinishDownloadingListener OnFinishDownloadingListener} listener,
     * so we can notify him witch photo have been downloaded, with her id and position.
     */
    public interface OnFinishDownloadingListener {


        /**
         * After we download the photo, we call this method in {@link OnFinishDownloadingListener},
         * to notify the the listener witch photo have been downloaded with her Id and position.
         * So he can refresh the recycler view.
         * @param photoId is the Id if the photo that we downloaded.
         * @param position is the current position of the photo in the list/recycler view.
         */
        void notifyItemDownloaded(final String photoId, final int position);



        /**
         * If Unexpected event happen we notify the listener to refresh the whole list/recycler view.
         */
        void notifyUnexpected();
    }


    /** The map that hold all the cached photo (OptimizedMap) */
    final private static Map<String, PhotoCache> fullPhotosCaching = getFullPhotosCaching();


    /** A singleton that hold the downloaded photos names. */
    final private static LocalPhotosDataSource localPhotosUtil = getInstance();


    /** A set that hold the photos id's that in the progress of downloading and saving right now */
    final private static Set<String> inDownloadingProgress =
            Collections.synchronizedSet(new HashSet<String>());


    /** The id of the of the photo you want to download*/
    private final String photoId;


    /** The position of the photo you ant to download in the list/recycler view */
    private final int position;


    /** A context of the fragment that use the util class */
    private final Context context;


    /** A listener so we can notify when the mission done/fail */
    private final OnFinishDownloadingListener onFinishDownloadingListener;


    /***
     * A constructor to create and init instance of {@link InlineDownloader InlineDownloader},
     * that can manage the downloading of the photo and notify when the mission done and succeed to
     * download the photo, and when fail notify with other method.
     * @param photoId is the Id of the photo, unique value.
     * @param position the position of the photo in the list, recycler view.
     * @param context a context.
     * @param onFinishDownloadingListener a listener so we can notify when the mission done and
     *                                    succeed to download the photo (call notifyItemDownloaded),
     *                                    or unexpected event accrue we notify with other method,
     *                                    (call notifyUnexpected).
     */
    public InlineDownloader(@NonNull final String photoId,
                     final int position,
                     @NonNull final Context context,
                     @NonNull final OnFinishDownloadingListener onFinishDownloadingListener) {

        this.photoId = photoId;
        this.position = position;
        this.context = context;
        this.onFinishDownloadingListener = onFinishDownloadingListener;
    }



    /**
     * Check if photo with photoId is in the downloading progress right now.
     * @param photoId is the id of the photo you want to check if it downloading right now.
     * @return true if the photo with photoId is downloading/saving right now, false otherwise.
     */
    public static boolean isDownloadingNow(final String photoId) {
        return inDownloadingProgress.contains(photoId);
    }



    /** Method that start the downloading process */
    public void startInlineDownloading() {
        inDownloadingProgress.add(photoId);
        if (fullPhotosCaching.containsKey(photoId)) {
            final PhotoCache photoCache = fullPhotosCaching.get(photoId);
            saveImageFile(photoCache, DEFAULT_PATH, MAX_QUALITY, this);
        }
        else {
            getPhotoCache(photoId, DEFAULT_PATH, context, this);
        }
    }



    /**
     * Save the image file after we succeed to cache it.
     * @param photoCache is an instance that hold the photo info include bitmap.
     */
    @Override
    public void onResult(@NonNull PhotoCache photoCache) {
        saveImageFile(photoCache, DEFAULT_PATH, MAX_QUALITY, this);
        fullPhotosCaching.put(photoCache.getPhotoId(), photoCache);
    }



    /**
     * When fail to cache the photo, and an exception is thrown from any reason.
     * we just notify the user (UI message).
     * @param e is the the exception that thrown while we download and cache the photo.
     */
    @Override
    public void onError(@NonNull Exception e) {
        if (e instanceof InterruptedException || e instanceof ExecutionException) {
            Toast.makeText(context, "Something wrong while caching the photo",
                    Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        notifyAfterDownloadingProgress();
    }



    /**
     * When successfully saved the image file, we add the the photo to the singleton that hold
     * all the Id's of the downloaded photos and then notify the user (UI message).
     * @param photoCache is an instance that hold the photo info.
     */
    @Override
    public void onSaved(@NonNull PhotoCache photoCache) {

        boolean addStatus = localPhotosUtil.add(photoCache.getPhotoId(),
                photoCache.getFormatExtension());
        if (addStatus) {
            Toast.makeText(context, "Successfully Saved", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(context, "Something wrong while adding to Downloads",
                    Toast.LENGTH_SHORT).show();
        }
        notifyAfterDownloadingProgress();
    }



    /**
     * When the photo already saved or another photo with same Id (should never happen), exist in
     * the default downloads folder, we just notify the user with a message (UI message).
     * @param photoCache is an instance that hold the photo info.
     */
    @Override
    public void onAlreadySaved(@NonNull PhotoCache photoCache) {
        Toast.makeText(context, "The file already exist in:\n" +
                DEFAULT_PATH, Toast.LENGTH_SHORT).show();
        notifyAfterDownloadingProgress();
    }



    /**
     * When we fail to save the image file, from any reason, we notify the user with a message.
     * @param e  is the the exception that thrown while writhing the image file.
     */
    @Override
    public void onSavingFailed(@NonNull IOException e) {
        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
        notifyAfterDownloadingProgress();
    }



    /**
     * After downloading or fail to download the photo, we remove the photoId from the set that
     * hold the current downloading photos (photos in the downloading progress), and then notify
     * that the progress done/fail.
     */
    private void notifyAfterDownloadingProgress() {
        if (inDownloadingProgress.contains(photoId)) {
            inDownloadingProgress.remove(photoId);
            onFinishDownloadingListener.notifyItemDownloaded(photoId, position);
        }
        else {
            onFinishDownloadingListener.notifyUnexpected();
        }
    }
}