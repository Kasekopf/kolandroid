package com.github.kolandroid.kol.util;

public class StringUtils {
    public static String htmlDecode(String str) {
        return str.replace("&lt;", "<").replace("&gt;", ">").replace("&quot;", "\"").replace("&nbsp", " ");
    }
}
