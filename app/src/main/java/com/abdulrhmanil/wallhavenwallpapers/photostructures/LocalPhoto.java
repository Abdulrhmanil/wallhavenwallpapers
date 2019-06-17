package com.abdulrhmanil.wallhavenwallpapers.photostructures;

import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;

import java.io.File;


/**
 * Class that represent the local downloaded photos in the storage,
 * a class that hold a basis info of the photo :
 * 1 - name of the photo (unique id).
 * 2 - File instance that hold path of the file.
 * 3 - the width of the photo.
 * 4 - the height of the photo.
 * We use this class to show the local (downloaded) photos to the user in the UI,
 * and provide him a some functionally like: deleting, sharing, set as wallpaper, sorting...
 */
public class LocalPhoto implements Comparable<LocalPhoto> {

    /** The name of the photo (unique id) */
    private final String photoId;

    /** The file of the downloaded image */
    private final File imageFile;

    /** The width of the local photo */
    private final int width;

    /** The height of the local photo */
    private final int height;

    /** The dimension of the local photo as a {@code String} */
    private final String wallRes;


    /**
     * Constructor to create and init an instance of {@link LocalPhoto LocalPhoto}, to represent
     * a local photo.
     * @param photoId is the name (unique id) of the local photo.
     * @param imageFile is the {@link File file} of the local photo, contain the path of the photo.
     */
    public LocalPhoto(String photoId, File imageFile) {
        this.photoId = photoId;
        this.imageFile = imageFile;
        BitmapFactory.Options options = options(imageFile);
        this.width = options.outWidth;
        this.height = options.outHeight;
        this.wallRes = String.valueOf(width) + " x " + String.valueOf(height);
    }


    /**
     * Util method to extract the width and height of the photo.
     * We use to extract the dimensions of the photo.
     * @param imageFile is the file of the local photo you want to extract his dimensions.
     * @return a BitmapFactory.Options instance that hold the dimensions of the local photo.
     */
    private BitmapFactory.Options options(File imageFile) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imageFile.toString(), options);
        return options;
    }


    /**
     * Get the id/name of the local photo.
     * @return the id of the local photo.
     */
    public String getPhotoId() {
        return photoId;
    }


    /**
     * Return the image file of the {@link LocalPhoto LocalPhoto}, we use this file to provide the
     * a functionally, we use it to show, delete, share, set as wallpaper for the user in the UI.
     * @return the {@link File file} of the local (downloaded) photo.
     */
    public File getImageFile() {
        return imageFile;
    }


    /**
     * Return the width of the local photo.
     * @return the width of the local photo.
     */
    public int getWidth() {
        return width;
    }


    /**
     * Return the height of the local photo.
     * @return the height of the local photo.
     */
    public int getHeight() {
        return height;
    }


    /**
     * Return the resolution of the local photo as a string.
     * @return the wallpaper resolution as a {@code String}.
     */
    public String getWallRes() {
        return wallRes;
    }


    /**
     * We provide sorting for the local photos, and the natural order is according to
     * the natural order of the letters "a-z".
     * So we provide a compareTo method, so sort method can sort
     * the list of {@link LocalPhoto LocalPhotos} objects.
     * @param rValue is the object you want to compare to.
     * @return the value {@code 0} if the argument rValue is equal to
     *          this local photo; a value less than {@code 0} if this LocalPhoto id
     *          is lexicographically less than the LocalPhoto id argument; and a
     *          value greater than {@code 0} if this this LocalPhoto id is
     *          lexicographically greater than the LocalPhoto id argument.
     */
    @Override
    public int compareTo(@NonNull LocalPhoto rValue) {
        return this.photoId.compareTo(rValue.photoId);
    }


    /**
     * Return {@code True} if this object is equal to o parameter, {@code false} otherwise.
     * @param o is the object you want to check this is equal to
     * @return {@code True} if this object is equal to o parameter, {@code false} otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LocalPhoto)) return false;
        LocalPhoto that = (LocalPhoto) o;
        return getPhotoId().equals(that.getPhotoId());
    }


    /**
     * Returns the hash code of the Id {@code String}, because the Id is unique,
     * we choose the Id hash code as the object hash code.
     * @return that hash code of this object.
     */
    @Override
    public int hashCode() {
        return getPhotoId().hashCode();
    }
}
