package com.ld.chatgptcopilot.translate.google;

import java.io.IOException;
import java.net.Proxy;
import java.util.Collections;

import com.ld.chatgptcopilot.translate.translator;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 不可用
 */
public class GoogleTranslator implements translator {
    @Override
    public Result translate(Param param) {
        return executeRequest(param);
    }

    @Override
    public String translate(String text, String from, String to) {
        Param param = new Param();
        param.setTexts(Collections.singletonList(text));
        Result result = executeRequest(param);
        if (result.getTexts().isEmpty()) {
            return null;
        }
        return result.getTexts().get(0);
    }

    private static Result executeRequest(Param param) {
        Proxy proxy = new Proxy(Proxy.Type.HTTP, new java.net.InetSocketAddress("127.0.0.1", 10809));

        OkHttpClient client = new OkHttpClient().newBuilder()
                //.proxy(proxy)
                .build();
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        RequestBody body = RequestBody.create(mediaType, "q=快乐星球.site&q=<a i=0>Copyright 2023</a><a i=1>•</a><a i=2>Privacy Policy</a><a i=3>•</a><a i=4>Legal</a>&q=The domain 快乐星球.site may be for sale. Click here to inquire about this domain.");
        Request request = new Request.Builder()
                .url("https://translate.googleapis.com/translate_a/t?anno=3&client=te_lib&format=html&v=1.0&key=AIzaSyBOti4mM-6x9WDnZIjIeyEU21OpBXqWBgw&logld=vTE_20230517&sl=auto&tl=zh-CN&tc=1&sr=1&tk=217741.291175")
                .method("POST", body)
                .addHeader("authority", "translate.googleapis.com")
                .addHeader("accept", "*/*")
                .addHeader("accept-language", "zh-CN,zh;q=0.9,en;q=0.8,zh-TW;q=0.7")
                .addHeader("content-type", "application/x-www-form-urlencoded")
                .addHeader("origin", "http://ww25.xn--fjqz24bfqeo5p.site")
                .addHeader("referer", "http://ww25.xn--fjqz24bfqeo5p.site/")
                .addHeader("sec-ch-ua", "\"Google Chrome\";v=\"113\", \"Chromium\";v=\"113\", \"Not-A.Brand\";v=\"24\"")
                .addHeader("sec-ch-ua-mobile", "?0")
                .addHeader("sec-ch-ua-platform", "\"Windows\"")
                .addHeader("sec-fetch-dest", "empty")
                .addHeader("sec-fetch-mode", "cors")
                .addHeader("sec-fetch-site", "cross-site")
                .addHeader("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/113.0.0.0 Safari/537.36")
                .addHeader("x-client-data", "CLK1yQEIlLbJAQiitskBCKmdygEI0aDKAQiOkssBCJOhywEIhaDNAQjAqs0BGIqnzQE=")
                .build();
        try {
            Response response = client.newCall(request).execute();
            if (response.body() != null) {
                System.out.println(response.body().string());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new Result();
    }


    public static void main(String[] args) {
        Proxy proxy = new Proxy(Proxy.Type.HTTP, new java.net.InetSocketAddress("127.0.0.1", 10809));

        OkHttpClient client = new OkHttpClient().newBuilder()
                .proxy(proxy)
                .build();
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        RequestBody body = RequestBody.create(mediaType, "q=快乐星球.site&q=<a i=0>Copyright 2023</a><a i=1>•</a><a i=2>Privacy Policy</a><a i=3>•</a><a i=4>Legal</a>&q=The domain 快乐星球.site may be for sale. Click here to inquire about this domain.");
        Request request = new Request.Builder()
                .url("https://translate.googleapis.com/translate_a/t?anno=3&client=te_lib&format=html&v=1.0&key=AIzaSyBOti4mM-6x9WDnZIjIeyEU21OpBXqWBgw&logld=vTE_20230517&sl=auto&tl=zh-CN&tc=1&sr=1&tk=217741.291175")
                .method("POST", body)
                .addHeader("authority", "translate.googleapis.com")
                .addHeader("accept", "*/*")
                .addHeader("accept-language", "zh-CN,zh;q=0.9,en;q=0.8,zh-TW;q=0.7")
                .addHeader("content-type", "application/x-www-form-urlencoded")
                .addHeader("origin", "http://ww25.xn--fjqz24bfqeo5p.site")
                .addHeader("referer", "http://ww25.xn--fjqz24bfqeo5p.site/")
                .addHeader("sec-ch-ua", "\"Google Chrome\";v=\"113\", \"Chromium\";v=\"113\", \"Not-A.Brand\";v=\"24\"")
                .addHeader("sec-ch-ua-mobile", "?0")
                .addHeader("sec-ch-ua-platform", "\"Windows\"")
                .addHeader("sec-fetch-dest", "empty")
                .addHeader("sec-fetch-mode", "cors")
                .addHeader("sec-fetch-site", "cross-site")
                .addHeader("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/113.0.0.0 Safari/537.36")
                .addHeader("x-client-data", "CLK1yQEIlLbJAQiitskBCKmdygEI0aDKAQiOkssBCJOhywEIhaDNAQjAqs0BGIqnzQE=")
                .build();
        try {
            Response response = client.newCall(request).execute();
            System.out.println(response.body().string());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
