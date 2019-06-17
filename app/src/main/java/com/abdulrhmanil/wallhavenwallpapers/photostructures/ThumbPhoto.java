package com.abdulrhmanil.wallhavenwallpapers.photostructures;


/**
 * Class that hold the basic info of the thumb photo, hold the id,
 * resolution and the link of the the thumb photo as well full res photo.
 */
public class ThumbPhoto {

    /** Template for the thumb photo link, the template is the same for all thumb photos */
    private final static String THUMB_PHOTO_LINK =
            "https://th.wallhaven.cc/small/%s/%s.jpg";


    /** Template for the full info of the photo, the template is the same for all photos*/
    private final static String INFO_LINK = "https://wallhaven.cc/w/%s";


    /** The name as well the Id of the photo (unique for each photo) */
    private final String photoId;


    /** The resolution of the photo */
    private final String wallRes;


    /**
     * Constructor to create and init an instance of {@link ThumbPhoto ThumbPhoto} class.
     * @param photoId is the name and Id of the photos.
     * @param wallRes is the resolution of the photo.
     */
    public ThumbPhoto(String photoId, String wallRes) {
        this.photoId = photoId;
        this.wallRes = wallRes;
    }


    /**
     * Copy Constructor to copy an instance of {@link ThumbPhoto ThumbPhoto} class.
     * @param lValue is the instance you want to copy.
     */
    public ThumbPhoto(ThumbPhoto lValue) {
        this.photoId = lValue.photoId;
        this.wallRes = lValue.wallRes;
    }


    /**
     * Get the link of the thumb photo.
     * @return the link of the thumb photo.
     */
    public String getThumbPhotoLink() {
        return String.format(THUMB_PHOTO_LINK,photoId.substring(0,2),photoId);
    }


    /**
     * Return the link of the info page of the photo, to get the full details.
     * @return the link of info page (contain all the details) of the photo to get all the info,
     * include the link of the full resolution photo.
     */
    public String getInfoLink() {
        return String.format(INFO_LINK,photoId);
    }


    /**
     * Get the id/name of the thumb photo.
     * @return the id pf the photo.
     */
    public String getPhotoId() {
        return photoId;
    }


    /**
     * Return the resolution of the full photo as a string.
     * @return the wallpaper resolution as a {@code String}.
     */
    public String getWallRes() {
        return wallRes;
    }


    /**
     * Return {@code True} if this object is equal to o parameter, {@code false} otherwise.
     * @param o is the object you want to check this is equal to
     * @return {@code True} if this object is equal to o parameter, {@code false} otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ThumbPhoto)) return false;

        ThumbPhoto that = (ThumbPhoto) o;

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


    @Override
    public String toString() {
        return  photoId;
    }
}
