package com.abdulrhmanil.wallhavenwallpapers.queriesconstants;

/**
 * Created by Abdulrhmanil on 04/03/2018.
 * NOT final, will add docs later
 */

public enum purity {
    SFW(100), // 100 is SFW option
    Sketchy(010); // 010 is Sketchy option

    private final int option;
    purity(int option) {
        this.option = option;
    }


    private static String purityOptionToString(int option) {
        if (option >=0 && option <10)
            return "00"+String.valueOf(option);

        else if (option >=10 && option <100)
            return "0"+ String.valueOf(option);

        else
            return String.valueOf(option);
    }
    public String toString() {
        return purityOptionToString(this.option);
    }

    public static String combine(purity... purities) {
        if (purities.length > 0) {
            int option = purities[0].option;

            for (purity purity : purities) {
                option |= purity.option;
            }
            return purityOptionToString(option);
        }
        else {
            throw new RuntimeException("unsupported");
        }
    }
    public static String asQuery(purity... purities) {
        return "purity="+combine(purities);
    }

}
