package com.abdulrhmanil.wallhavenwallpapers.queriesconstants;

// NOT final, will add docs later
public enum order {
    Desc("desc"),
    Asc("asc");

    private final String order;

    order(String order) {
        this.order = order;
    }

    @Override
    public String toString() {
       return order;
    }
    public String asQuery() {
        return asQuery(this);
    }

    public static String asQuery(order order) {
        return "order="+order;
    }
}
