package com.github.kingschan1204.easycrawl.helper.validation;

/**
 * @author kingschan
 * 2023-3-30
 */
public class Assert {
    /**
     * 是否为真
     * @param exp
     * @param message
     */
    public static void isTrue(boolean exp, String message) {
        if (!exp) {
            throw new RuntimeException(message);
        }
    }

    public static void notNull(Object val, String message) {
        if(null == val){
            throw new RuntimeException(message);
        }
    }



}
