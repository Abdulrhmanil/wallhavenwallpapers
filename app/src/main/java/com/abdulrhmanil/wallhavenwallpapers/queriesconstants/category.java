package com.abdulrhmanil.wallhavenwallpapers.queriesconstants;

/**
 * Created by Abdulrhmanil on 04/03/2018.
 * NOT final, will add docs later
 */

public enum category {

    General(100), // 100 is General option
    Anime(010), // 010 is Anime option
    People(001); //001 is People option


    private final int option;

    category(int option) {
        this.option = option;
    }


    private static String categoryOptionToString(int option) {
        if (option >=0 && option <10)
            return "00"+String.valueOf(option);

        else if (option >=10 && option <100)
            return "0"+ String.valueOf(option);

        else
            return String.valueOf(option);
    }


    @Override
    public String toString() {
        return categoryOptionToString(this.option);
    }


    public static String combine(category... categories) {
        if (categories.length > 0) {
            int option = categories[0].option;

            for (category category : categories) {
                option |= category.option;
            }
            return categoryOptionToString(option);
        }
        else {
            throw new RuntimeException("unsupported");
        }
    }

    public static String asQuery(category... categories) {
        return "categories="+combine(categories);
    }

}
