package com.abdulrhmanil.wallhavenwallpapers;

import android.content.Context;
import android.util.DisplayMetrics;


/**
 * Display utility class that help us to calculate the dimension of thumb photos,
 * and the number of the thumb photos that should be in one row/line.
 * This class provide {@link #getThumbRes(Context) getThumbRes},
 * {@link #getThumbRes(Context, int, int, int) getThumbRes} methods.
 * that return an instance of type {@link ThumbRes ThumbRes} that contain 3 variables.
 * thus 3 variables is result of the calculation.
 * This class is have 3 constant values :
 * witch it the minimum width, height of the photo, and the side margin of the thumb photo.
 * Thus constants could change ...
 */
public class DisplayUtil {

    /* Remember to change in /res/layout/thumb_photo_item.xml
     * it's the CardView margin : */

    /** The margin of the thumb card in the width from both sides*/
    private static final int thumbMarginWidth = 8;

    /** The default (minimum) width of thumb photo*/
    private static final int thumbWidth = 300;

    /** The default (minimum) height of thumb photo*/
    private static final int thumbHeight = 200;


    /** Should NOT make instances, this class used only as utility methods */
    private DisplayUtil() {}


    /**
     * Return {@link ThumbRes ThumbRes} instance that hold 3 final variables
     * 1 - the supposed width of the thump photo.
     * 2 - the supposed height of the thump photo.
     * 3 - the number of thumb photos in on row or in one line.
     * Thous final variables calculated, depends on the height, width
     * and the resolution of the screen.
     * The methodology is we trying to put max number of thumb photos in one line,
     * when the minimum width x height of the thumb photo is 300x200 with padding 8 DP.
     * and remaining space we scale the thumb photos in that space.
     * @param context a context.
     * @return Return {@link ThumbRes ThumbRes} instance, that contain number of thumb photos in one
     * line, the supposed width of the thump photo, the supposed height of the thump photo.
     */
    public static ThumbRes getThumbRes(final Context context) {
        return getThumbRes(context, thumbWidth, thumbMarginWidth, thumbHeight);
    }


    /**
     * Return {@link ThumbRes ThumbRes} instance that hold 3 final variables
     * 1 - the supposed width of the thump photo.
     * 2 - the supposed height of the thump photo.
     * 3 - the number of thumb photos in on row or in one line.
     * Thous final variables calculated, depends on the height, width
     * and the resolution of the screen.
     * The methodology is we trying to put max number of thumb photos in one line,
     * when the minimum resolution is :  thumbWidthDp x thumbHeightDp
     * and sides margin of thumbMarginWidthDp.
     * and remaining space we divide it between photos and scale them in that space.
     * @param context a context.
     * @param thumbWidthDp is minimum width allowed for each thumb photo.
     * @param thumbMarginWidthDp is the margin of each thumb photo from each side.
     * @param thumbHeightDp is minimum height allowed for each thumb photo.
     * @return Return {@link ThumbRes ThumbRes} instance, that contain number of thumb photos in one
     * line, the supposed width of the thump photo, the supposed height of the thump photo.
     */
    public static ThumbRes getThumbRes(final Context context,
                                       final int thumbWidthDp,
                                       final int thumbMarginWidthDp,
                                       final int thumbHeightDp) {
        return new ThumbRes(context,
                thumbWidthDp,
                thumbMarginWidthDp,
                thumbHeightDp);
    }


    /**
     * Class that able to calculate and determine :
     * 1 - the number of thumb photos in one line.
     * 2 - the width of the thumb photo.
     * 3 - the height of the thumb photo.
     * With keeping the photo scaled with her the original dimensions,
     * this class do the calculation and keep the needed numbers in 3 public Fields :
     * 1 - widthPx, 2 - heightPx, 3 - numOfCols .
     */
    public static class ThumbRes {
        public final int widthPx;
        public final int heightPx;
        public final int numOfCols;

        private ThumbRes(final Context context,
                         final int thumbWidthDp,
                         final int thumbMarginWidthDp,
                         final int thumbHeightDp) {

            // Do the calculation :
            final DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
            final float density = displayMetrics.density;
            final int displayWidthPx = displayMetrics.widthPixels;
            final float displayWidthDp = displayWidthPx / density;

            final int totalThumbWidthDp = thumbWidthDp + thumbMarginWidthDp;
            final float scale = displayWidthDp / totalThumbWidthDp;

            //final int numOfCols = (int) scale;
            final int numOfCols = (int) (displayWidthDp / thumbWidthDp);
            final int widthPx = (int) ((thumbWidthDp * density * scale) / numOfCols);
            final int heightPx = (int) ((thumbHeightDp * density * scale) / numOfCols);
            this.widthPx = widthPx;
            this.heightPx = heightPx;
            this.numOfCols = numOfCols;
        }
    }
}
