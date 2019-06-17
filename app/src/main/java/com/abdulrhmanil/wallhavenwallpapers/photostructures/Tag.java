package com.abdulrhmanil.wallhavenwallpapers.photostructures;

/**
 * Class the represent the info that the tag hold,
 * the tag hold text (showed to user), id and the link of the tag.
 * So we use this class to represent the tag in our application.
 */
public class Tag {

    /** Template of the url that include all the photos that contain the current tag */
    private final static String tagLink = "https://alpha.wallhaven.cc/tag/%s";

    /** The id of the tag */
    private final String tagId;

    /** The text that showed to the user */
    private final String tagText;


    /**
     * Constructor to create and init instances of {@link Tag Tag} class.
     * @param tagId is the id of the tag.
     * @param tagText is the text of the tag, that showed the user.
     */
    public Tag(String tagId, String tagText) {
        this.tagId = tagId;
        this.tagText = tagText;
    }


    /**
     * Return the id of the tag.
     * @return the id of the tag.
     */
    public String getTagId() {
        return tagId;
    }


    /**
     * Return the text of the tag, the text showed to the user in the UI.
     * @return the text of the tag, the text showed to the user in the UI.
     */
    public String getTagText() {
        return tagText;
    }


    /**
     * Return the link of the tag, that contain all photos with this tag.
     * @return the link of the tag.
     */
    public String getTagLink() {
        return String.format(tagLink,tagId);
    }
}
