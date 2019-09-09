package com.chinaso.demo.utils;

/**
 * 通用方法定义
 *
 * @author Administrator
 */
public class Tools {
    private static final String TAG = Tools.class.getSimpleName();


    /**
     * 判断字符串是否为空
     *
     * @param o
     * @return
     */
    public static boolean isEmptyString(String o) {
        if (o == null) {
            return true;
        }
        if (o.equals("") || o.length() <= 0) {
            return true;
        }
        return false;
    }
}
