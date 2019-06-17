package com.abdulrhmanil.wallhavenwallpapers.queriesconstants;

/**
 * Created by Abdulrhmanil on 05/03/2018.
 * NOT final, will add docs later
 */

public enum topRange {

    LastDay("1d"),
    LastThreeDays("3d"),
    LastWeek("1w"),
    LastMonth("1M"),
    LastThreeMonths("3M"),
    LastSixMonths("6M"),
    LastYear("1y");

    private final String range;

    topRange(String range) {
        this.range = range;
    }

    @Override
    public String toString() {
        return range;
    }

    public String asQuery() {
        return asQuery(this);
    }

    public static String asQuery(topRange range) {
        return "topRange="+range;
    }

}
