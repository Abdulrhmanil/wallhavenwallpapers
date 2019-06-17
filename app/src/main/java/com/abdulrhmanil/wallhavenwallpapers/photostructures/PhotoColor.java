package com.abdulrhmanil.wallhavenwallpapers.photostructures;

import java.util.HashSet;
import java.util.Set;


/**
 * Class that represent the dominates colors in the photos.
 * We use it to provide searching photos with colors.
 * so we also hold the dominates colors in the photo.
 */
public class PhotoColor {

    /** The count of the colors we support in our application*/
    private static final int colorsAmount=29;

    /** The set of the colors we support in our application*/
    private static final Set<String> colorsSet=new HashSet<>(colorsAmount);

    /** Template of the link contain all photos that contain the current dominate color*/
    protected static final String colorLink="https://alpha.wallhaven.cc/search?colors=%s";

    /** The value of the color in hex*/
    private final String color;


    static {
        //init and add supported dominates colors
        addColorsToSet();
    }


    /**
     * Constructor to create and init an instance of a dominate color
     * @param color is the value of the color
     */
    public PhotoColor(String color) {
        this.color = color;
    }


    /**
     * Return the color value in hex.
     * @return the holor value in hex.
     */
    public String getColor() {
        return color;
    }


    /**
     * Return the color number without "#"
     * @return the color number without "#"
     */
    public String getColorNumber() {
        return color.substring(1);
    }


    /**
     * Return the link of the color that contain all photos that contain current dominate color.
     * @return the link of color.
     */
    public String getColorLink() {
        return String.format(colorLink,getColorNumber());
    }


    /**
     * Return the color in query format to use in the search method.
     * @return the color as query in format colors=#colornum .. to use it search.
     */
    public String asQuery() {
        return asQuery(this);
    }


    /**
     * Util method to create the query.
     * @param color is the color number you want to create a query for it.
     * @return a {@code String} that represent a query for color searching.
     */
    public static String asQuery(PhotoColor color) {
        return "colors="+color.getColorNumber();
    }


    /**
     * Fill the colors set of the colors we support in our application,
     * for now we support only 29 color, could be change later.
     */
    private static void addColorsToSet() {
        /* The search colors we support in our application : */
        colorsSet.add("#660000");
        colorsSet.add("#990000");
        colorsSet.add("#cc0000");
        colorsSet.add("#cc3333");
        colorsSet.add("#ea4c88");
        colorsSet.add("#993399");
        //
        colorsSet.add("#663399");
        colorsSet.add("#333399");
        colorsSet.add("#0066cc");
        colorsSet.add("#0099cc");
        colorsSet.add("#66cccc");
        colorsSet.add("#77cc33");
        //
        colorsSet.add("#669900");
        colorsSet.add("#336600");
        colorsSet.add("#666600");
        colorsSet.add("#999900");
        colorsSet.add("#cccc33");
        colorsSet.add("#ffff00");
        //
        colorsSet.add("#ffcc33");
        colorsSet.add("#ff9900");
        colorsSet.add("#ff6600");
        colorsSet.add("#cc6633");
        colorsSet.add("#996633");
        colorsSet.add("#663300");
        //
        colorsSet.add("#000000");
        colorsSet.add("#999999");
        colorsSet.add("#cccccc");
        colorsSet.add("#ffffff");
        colorsSet.add("#424153");
    }
}

