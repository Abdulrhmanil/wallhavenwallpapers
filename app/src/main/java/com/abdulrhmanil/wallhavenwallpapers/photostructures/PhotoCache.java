package com.abdulrhmanil.wallhavenwallpapers.photostructures;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;

import java.io.File;
import java.util.List;


/**
 * PhotoCache class represent the photo cache for the {@link FullPhoto Full Photo} instance,
 * while delegate all the methods.
 * This class hold :
 * 1 - {@link FullPhoto fullPhoto} instance, that hold all the photo details.
 * 2 - {@link Drawable  drawable} instance, witch is the cached photo, to save it later.
 * 3 - {@link File file} instance, to check in runtime if the image have been downloaded.
 * In other word this class is represent the photo cache, and our cache in the application is
 * is keep thus instances in the cache (map that hold PhotoCaches).
 */
public class PhotoCache {

    /** FullPhoto instance that hold the photo details*/
    private final FullPhoto fullPhoto;

    /** Drawable instance that hold the cached photo*/
    private final Drawable drawableCache;

    /** File instance to check in runtime if the photo is saved in the default downloads folder*/
    private final File file;


    /**
     * Constructor to create and init an instance of {@link PhotoCache PhotoCache}, that hold
     * the cache og the full photo.
     * @param fullPhoto is an instance of {@link FullPhoto FullPhoto} class that hold all the
     *                  details of the photo.
     * @param drawableCache is an instance of {@link Drawable Drawable} class to hold the image
     *                      cache, so we could save it to a file later.
     * @param defaultPath is the default path to save the photos, it's the path of the downloads
     *                    folder.
     */
    public PhotoCache(@NonNull FullPhoto fullPhoto, @NonNull Drawable drawableCache,
                      @NonNull String defaultPath) {
        this.fullPhoto = fullPhoto;
        this.drawableCache = drawableCache;
        this.file = new File(defaultPath, (fullPhoto.getPhotoId()
                + fullPhoto.getFormatExtension()));
    }


    /**
     * Delegate method :
     * Return the height of the photo.
     * @return the height of the photo.
     */
    public int getHeight() {
        return fullPhoto.getHeight();
    }


    /**
     * Delegate method :
     * Return the width of the photo.
     * @return the width of the photo.
     */
    public int getWidth() {
        return fullPhoto.getWidth();
    }


    /**
     * Delegate method :
     * Return the uploader name.
     * @return the uploader name.
     */
    public String getUploader() {
        return fullPhoto.getUploader();
    }


    /**
     * Delegate method :
     * Return the category of the photo.
     * @return the category of the photo.
     */
    public String getCategory() {
        return fullPhoto.getCategory();
    }


    /**
     * Delegate method :
     * Return the size of the photo.
     * @return the size of the photo.
     */
    public String getSize() {
        return fullPhoto.getSize();
    }


    /**
     * Delegate method :
     * Return the num of views.
     * @return the num of views.
     */
    public String getViews() {
        return fullPhoto.getViews();
    }


    /**
     * Delegate method :
     * Return a list that contain the dominate colors in the photo.
     * @return a list that contain the dominate colors in the photo.
     */
    public List<PhotoColor> getColors() {
        return fullPhoto.getColors();
    }


    /**
     * Delegate method :
     * Return a list that contain the tags of the photo.
     * @return a list that contain the tags of the photo.
     */
    public List<Tag> getTags() {
        return fullPhoto.getTags();
    }


    /**
     * Delegate method :
     * Add a color to the list of the colors.
     * @param color is the color of you want to add to dominate color list.
     */
    public void addColor(PhotoColor color) {
        fullPhoto.addColor(color);
    }


    /**
     * Delegate method :
     * Add a color to the list of the colors.
     * @param color is the color of you want to add to dominate color list.
     */
    public void addColor(String color) {
        fullPhoto.addColor(color);
    }


    /**
     * Delegate method :
     * Add a tag to tags list.
     * @param tag is the tag you want to add to tag list.
     */
    public void addTag(Tag tag) {
        fullPhoto.addTag(tag);
    }


    /**
     * Delegate method :
     * Add tag to the list of tags.
     * @param tagId is the id of the tag you want to add to the list of tags.
     * @param tagText is the written text in the tag that showed in to user in UI.
     */
    public void addTag(String tagId, String tagText) {
        fullPhoto.addTag(tagId, tagText);
    }


    /**
     * Delegate method :
     * Return the url of full resolution photo to download/cache...
     * @return the url of full resolution photo.
     */
    public String getPhotoUrl() {
        return fullPhoto.getPhotoUrl();
    }


    /**
     * Delegate method :
     * Get the format of the photo as Bitmap.CompressFormat to save the photo in the correct
     * format later.
     * @return the format of the photo.
     */
    public Bitmap.CompressFormat getFormat() {
        return fullPhoto.getFormat();
    }


    /**
     * Delegate method :
     * set the format of the image, in a matter of compress the image.
     * prefer NOT to change the format, unless you really want to compress the image.
     * be careful when use this method, this method convert the {@link FullPhoto FullPhoto}
     * object to mutable;
     * @param format is the format of the image you want to set in a matter of compress.
     */
    public void setFormat(Bitmap.CompressFormat format) {
        fullPhoto.setFormat(format);
    }


    /**
     * Delegate method :
     * Return the format of the photo as a {@link String} extension,
     * example for a photo of type JPG return -> .jpg
     * @return the extension of the photo as a {@link String}.
     */
    public String getFormatExtension() {
        return fullPhoto.getFormatExtension();
    }


    /**
     * Delegate method :
     * Get the link of the thumb photo.
     * @return the link of the thumb photo.
     */
    public String getThumbPhotoLink() {
        return fullPhoto.getThumbPhotoLink();
    }


    /**
     * Delegate method :
     * Return the link of the info page of the photo, to get the full details.
     * @return the link of info page (contain all the details) of the photo to get all the info,
     * include the link of the full resolution photo.
     */
    public String getInfoLink() {
        return fullPhoto.getInfoLink();
    }


    /**
     * Delegate method :
     * Get the id/name of the thumb photo.
     * @return the id pf the photo.
     */
    public String getPhotoId() {
        return fullPhoto.getPhotoId();
    }


    /**
     * Delegate method :
     * Return the resolution of the full photo as a string.
     * @return the wallpaper resolution as a {@code String}.
     */
    public String getWallRes() {
        return fullPhoto.getWallRes();
    }


    /**
     * Delegate method :
     * Return the number of favorites of the photo.
     * @return the number of favorites of the photo.
     */
    public int getNumOfFav() {
        return fullPhoto.getNumOfFav();
    }


    /**
     * Return a {@link Drawable Drawable} instance, that contain the cache of the full resolution
     * photo, so you can write it to an image file in the storage. or show to the user in the UI.
     * @return a Drawable instance, that contain the cached photo in full resolution.
     */
    public Drawable getDrawableCache() {
        return drawableCache;
    }


    /**
     * Return the {@code Bitmap} instance from the {@code Drawable} to save it in the storage.
     * @return the {@code Bitmap} of the cached photo, to save it to a file in the storage.
     */
    public Bitmap getBitmapCache() {
        return ((BitmapDrawable) drawableCache).getBitmap();
    }


    /**
     * Return the {@code File} of the cached photo (the file of the image, that saved in the storage)
     *  to use it for the content provider.
     * @return the file of the image (cached photo).
     */
    public File getFile() {
        return file;
    }


    /**
     * Check if the photo have been downloaded and saved in the default downloads folder,
     * return {@code True} if the file exist and the photo already saved in t
     * he default download folder, false otherwise.
     * @return true if the photo already saved in the default downloads folder, false otherwise.
     */
    public boolean fileExists() {
        return file.exists();
    }

}
