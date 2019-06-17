package com.abdulrhmanil.wallhavenwallpapers.photostructures;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.List;

/**
 * Class that represent full resolution photo and hold all the info of the full resolution photo,
 * hold the: url, height, width, favorites, uploader, category, size, views and others.
 */
public class FullPhoto extends ThumbPhoto {

    /** jpg format */
    private static final String JPG=".jpg";

    /** png format */
    private static final String PNG=".png";

    /** webp format */
    private static final String WEBP=".webp";

    /** The default count of the tags */
    private static final int defTagsSize=16;

    /** The default count of colors */
    private static final int defColorsSize=5;

    /** The url of the full resolution photo...for caching/downloading */
    private final String photoUrl;

    /** The height of the photo*/
    private final int height;

    /** The width of the photo*/
    private final int width;

    /** The num of favorites of the photo*/
    private final int numOfFav;

    /** Uploader name */
    private final String uploader;

    /** The category of the photo */
    private final String category;

    /** The size of the photo */
    private final String size;

    /** The num of views the photo */
    private final String views;

    /** The format of the photo */
    private Bitmap.CompressFormat format;

    /** list that hold the tags of the photo*/
    private final List<Tag> tags;

    /** list that hold the colors of the photo*/
    private final List<PhotoColor> colors;



    /**
     * Constructor to create and init an instance of {@link FullPhoto FullPhoto} class.
     * @param photoId is the id of the photo.
     * @param wallRes is the resolution of the wallpaper.
     * @param numOfFav is the num of the favorites of the photo.
     * @param photoUrl is the url of the full resolution photo.
     * @param height is the height of the photo.
     * @param width is the width of the photo.
     * @param uploader is the uploader name of the photo.
     * @param category is the category of the photo.
     * @param size is the size of the photo.
     * @param views is num of the views.
     */
    public FullPhoto(String photoId, String wallRes, int numOfFav, String photoUrl, int height
            , int width, String uploader, String category, String size, String views) {
        this(photoId,wallRes,numOfFav,photoUrl,height,width,uploader,category,size,views,
                new ArrayList<>(defTagsSize),new ArrayList<>(defColorsSize));
    }


    /**
     * Constructor to create and init an instance of {@link FullPhoto FullPhoto} class.
     * @param photoId is the id of the photo.
     * @param wallRes is the resolution of the wallpaper.
     * @param numOfFav is the num of the favorites of the photo.
     * @param photoUrl is the url of the full resolution photo.
     * @param height is the height of the photo.
     * @param width is the width of the photo.
     * @param uploader is the uploader name of the photo.
     * @param category is the category of the photo.
     * @param size is the size of the photo.
     * @param views is num of the views.
     * @param tags is a list that hold the tags of the photo.
     * @param colors is a list that hold the dominant colors of the photo.
     */
    public FullPhoto(String photoId, String wallRes, int numOfFav, String photoUrl, int height,
                     int width, String uploader, String category, String size, String views,
                     List<Tag> tags, List<PhotoColor> colors) {
        super(photoId, wallRes);
        this.numOfFav=numOfFav;
        this.photoUrl=photoUrl;
        this.height = height;
        this.width = width;
        this.uploader = uploader;
        this.category = category;
        this.size = size;
        this.views = views;
        format=getPhotoFormatFromUrl(photoUrl,photoId);
        this.tags = tags;
        this.colors = colors;
    }


    /**
     * Copy constructor to copy an instance of {@link FullPhoto FullPhoto} class.
     * @param lValue is the instance you want to copy.
     */
    public FullPhoto(FullPhoto lValue) {
        super(lValue);
        this.numOfFav=lValue.numOfFav;
        this.photoUrl=lValue.photoUrl;
        this.height = lValue.height;
        this.width = lValue.width;
        this.uploader = lValue.uploader;
        this.category = lValue.category;
        this.size = lValue.size;
        this.views = lValue.views;
        this.format=lValue.format;
        this.colors = new ArrayList<>(lValue.colors);
        this.tags = new ArrayList<>(lValue.tags);
    }


    /**
     * Return the number of favorites of the photo.
     * @return the number of favorites of the photo.
     */
    public int getNumOfFav() {
        return numOfFav;
    }


    /**
     * Return the height of the photo.
     * @return the height of the photo.
     */
    public int getHeight() {
        return height;
    }


    /**
     * Return the width of the photo.
     * @return the width of the photo.
     */
    public int getWidth() {
        return width;
    }


    /**
     * Return the uploader name.
     * @return the uploader name.
     */
    public String getUploader() {
        return uploader;
    }


    /**
     * Return the category of the photo.
     * @return the category of the photo.
     */
    public String getCategory() {
        return category;
    }


    /**
     * Return the size of the photo.
     * @return the size of the photo.
     */
    public String getSize() {
        return size;
    }


    /**
     * Return the views of the photo.
     * @return the views of the photo.
     */
    public String getViews() {
        return views;
    }


    /**
     * Return the list that hold the dominant colors of the photo.
     * @return the list that hold the dominant colors of the photo.
     */
    public List<PhotoColor> getColors() {
        return colors;
    }


    /**
     * Return the list that hold the tags of the photo.
     * @return the list that hold the tags of the photo.
     */
    public List<Tag> getTags() {
        return tags;
    }


    /**
     * Add color to the list of colors.
     * @param color is the dominant color you want to add to the colors list.
     */
    public void addColor(PhotoColor color) {
        this.colors.add(color);
    }


    /**
     * Add color to the list of colors.
     * @param color is the dominant color you want to add to the colors list.
     */
    public void addColor(String color) {
        addColor(new PhotoColor(color));
    }


    /**
     * Add tag to the list of tags.
     * @param tag is the tag you want to add to the tags list.
     */
    public void addTag(Tag tag) {
        this.tags.add(tag);
    }


    /**
     * Add tag to the list of tags.
     * @param tagId is the id of the tag you want to add to the list of tags.
     * @param tagText is the written text in the tag that showed in to user in UI.
     */
    public void addTag(String tagId, String tagText) {
        this.addTag(new Tag(tagId,tagText));
    }


    /**
     * Return the url of full resolution photo to download/cache...
     * @return the url of full resolution photo.
     */
    public String getPhotoUrl() {
        return photoUrl;
    }


    /**
     * Get the format of the photo as Bitmap.CompressFormat to save the photo in the correct
     * format later.
     * @return the format of the photo.
     */
    public Bitmap.CompressFormat getFormat() {
        return format;
    }


    /**
     * Return the format of the photo as a {@link String} extension,
     * example for a photo of type JPG return -> .jpg
     * @return the extension of the photo as a {@link String}.
     */
    public String getFormatExtension() {
        if (format.equals(Bitmap.CompressFormat.JPEG))
            return JPG;
        else if (format.equals(Bitmap.CompressFormat.PNG))
            return PNG;
        else if (format.equals(Bitmap.CompressFormat.WEBP))
            return WEBP;

        throw new RuntimeException("something wrong in getFormatExtension!!!!");
        //return JPG;
    }


    /**
     * Util static method that can determine the format of the photo from the url of the photo.
     * @param url is the info url of the photo.
     * @param id is the id/name of the photo.
     * @return the format of the photo as Bitmap.CompressFormat enum,
     * to save the file correctly later.
     */
    private static Bitmap.CompressFormat getPhotoFormatFromUrl(String url,String id){
        String[] array=url.split(id);
        String photoFormat=array[1].toLowerCase();
        if (photoFormat.equals(JPG))
            return Bitmap.CompressFormat.JPEG;
        else if(photoFormat.equals(PNG))
            return Bitmap.CompressFormat.PNG;
        else if (photoFormat.equals(WEBP))
            return Bitmap.CompressFormat.WEBP;

        throw new RuntimeException("something wrong in getPhotoFormatFromUrl!!!!");
        //return Bitmap.CompressFormat.JPEG;
    }


    /**
     *  set the format of the image, in a matter of compress the image.
     *  prefer NOT to change the format, unless you really want to compress the image.
     *  be careful when use this method, this method convert the {@link FullPhoto FullPhoto}
     *  object to mutable;
     * @param format is the format of the image you want to set in a matter of compress.
     */
    public void setFormat(Bitmap.CompressFormat format) {
        this.format = format;
    }
}
