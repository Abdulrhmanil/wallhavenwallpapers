package com.abdulrhmanil.wallhavenwallpapers.queriesconstants;

/**
 * Created by Abdulrhmanil on 04/03/2018.
 * NOT final, will add docs later
 */

public enum sorting {

    Toplist("toplist"),
    Random("random"),
    Relevance("relevance"),
    DateAdded("date_added"),
    Views("views"),
    Favorites("favorites");

    private final String sortingMethodology;

    sorting(String sortingMethodology) {
        this.sortingMethodology=sortingMethodology;
    }

    @Override
    public String toString() {
        return sortingMethodology;
    }

    public String asQuery() {
        return asQuery(this);
    }

    public static String asQuery(sorting methodology) {
        return "sorting="+methodology;
    }
}
