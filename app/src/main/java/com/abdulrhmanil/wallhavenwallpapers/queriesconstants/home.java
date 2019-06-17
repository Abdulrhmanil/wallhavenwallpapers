package com.abdulrhmanil.wallhavenwallpapers.queriesconstants;

// NOT final, will add docs later
public enum home {
    Latest("latest"),
    TopList("toplist"),
    Random("random");

    private final String homePage;

    home(String homePage) {

        this.homePage = homePage;
    }
    @Override
    public String toString() {
        return homePage;
    }
}
