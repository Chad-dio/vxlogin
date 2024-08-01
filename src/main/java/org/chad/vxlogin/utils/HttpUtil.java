package org.chad.vxlogin.utils;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;

public class HttpUtil {
    public static HttpResponse doGet(String url){
        HttpClient client = HttpClientBuilder.create().build();
        HttpGet httpGet = new HttpGet(url);
        HttpResponse httpResponse;
        try {
            httpResponse = client.execute(httpGet);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return httpResponse;
    }

    public static String mergeStr(String ...s){
        StringBuilder res = new StringBuilder(200);
        for (String t : s) {
            res.append(t);
        }
        return res.toString();
    }
}
