package com.abdulrhmanil.wallhavenwallpapers;

import android.os.Environment;


/**
 * GlobalConstant is a class that hold all the global constants that we use in all the application
 * This constants should be public upon all the other classes in our application.
 * This constants could be replaced with local final variables, we decide to put them in one class
 * for convenience and the accessibly.
 */
public final class GlobalConstant {
    /** EXTRA key, we use it when create intent*/
    public static final String KEY_EXTRA_PHOTO_ID = "photoId";

    /** EXTRA key, we use it when create intent*/
    public static final String KEY_EXTRA_THUMB_PHOTO_LINK = "thumbPhotoLink";

    /** EXTRA key, we use it when create intent*/
    public static final String KEY_EXTRA_FULL_PHOTO_PATH = "fullPhotoPath";

    /** EXTRA key, we use it when create intent or Bundle*/
    public static final String KEY_EXTRA_CURRENT_POSITION = "currentPosition";

    /** The default quality for saving images, 100 is the max quality, NO compressing*/
    public static final int MAX_QUALITY = 100;


    /** use it for getUriForFile method in the content provider a security mechanism.
     * Remember to change in the manifests, */
    public static final String AUTHORITY = "com.abdulrhmanil.wallhavenwallpapers.fileprovider";


    /** Remember to update in the: @xml/provider_paths.xml,
     * because we give only permission to this folder */
    public static final String DEFAULT_PATH = Environment.getExternalStorageDirectory().getPath()
            + "/Pictures/WallHaven Wallpapers";
}
