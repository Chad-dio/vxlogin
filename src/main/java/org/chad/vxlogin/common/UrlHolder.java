package org.chad.vxlogin.common;

public class UrlHolder {
    private static final ThreadLocal<String> tl = new ThreadLocal<>();

    public static void save(String redirectUrl){
        tl.set(redirectUrl);
    }

    public static String get(){
        return tl.get();
    }

    public static void remove(){
        tl.remove();
    }
}
