package com.ld.chatgptcopilot.translate.edge;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.hutool.json.JSONUtil;
import com.ld.chatgptcopilot.translate.edge.model.EdgeResult;
import com.ld.chatgptcopilot.translate.edge.model.EdgeText;
import com.ld.chatgptcopilot.translate.translator;
import com.ld.chatgptcopilot.util.ChatGPTCopilotCommonUtil;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class EdgeTranslator implements translator {
    @Override
    public Result translate(Param param) {
        return executeRequest(param);
    }

    @Override
    public String translate(String text, String from, String to) {
        Param param = new Param();
        param.setTexts(Collections.singletonList(text));
        param.setFrom(from);
        param.setTo(to);
        Result result = executeRequest(param);
        if (result.getTexts().isEmpty()) {
            return null;
        }
        return result.getTexts().get(0);
    }

    private static Result executeRequest(Param param) {
        Result result = new Result();
        String token = getToken();
        if (token == null) {
            result.setTexts(Collections.singletonList("获取token失败"));
            return result;
        }

        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("application/json");
        List<EdgeText> edgeTexts = new ArrayList<>();
        for (String text : param.getTexts()) {
            edgeTexts.add(new EdgeText(text));
        }
        String content = JSONUtil.toJsonStr(edgeTexts);
        RequestBody body = RequestBody.create(mediaType, content);
        String from = param.from;
        String to = param.to;
        Request request = new Request.Builder()
                .url("https://api-edge.cognitive.microsofttranslator.com/translate?from=" + from + "&to=" + to + "&api-version=3.0&includeSentenceLength=true")
                .method("POST", body)
                .addHeader("authority", "api-edge.cognitive.microsofttranslator.com")
                .addHeader("accept", "*/*")
                .addHeader("accept-language", "zh-CN,zh;q=0.9,en;q=0.8,en-GB;q=0.7,en-US;q=0.6")
                .addHeader("authorization", "Bearer " + token)
                .addHeader("cache-control", "no-cache")
                .addHeader("content-type", "application/json")
                .addHeader("origin", "https://github.com")
                .addHeader("pragma", "no-cache")
                .addHeader("referer", "https://github.com/")
                .addHeader("sec-ch-ua", "\"Microsoft Edge\";v=\"113\", \"Chromium\";v=\"113\", \"Not-A.Brand\";v=\"24\"")
                .addHeader("sec-ch-ua-mobile", "?0")
                .addHeader("sec-ch-ua-platform", "\"Windows\"")
                .addHeader("sec-fetch-dest", "empty")
                .addHeader("sec-fetch-mode", "cors")
                .addHeader("sec-fetch-site", "cross-site")
                .addHeader("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/113.0.0.0 Safari/537.36 Edg/113.0.1774.50")
                .build();
        try {
            Response response = client.newCall(request).execute();
            ResponseBody responseBody = response.body();
            if (responseBody == null) {
                result.setTexts(Collections.singletonList(response.code() + " " + response.message()));
                return result;
            }
            if (!response.isSuccessful()) {
                result.setTexts(Collections.singletonList(response.code() + " " + responseBody.string() + content));
                return result;
            }
            String string = responseBody.string();
            List<EdgeResult> edgeResults = JSONUtil.toList(JSONUtil.parseArray(string), EdgeResult.class);
            List<String> texts = new ArrayList<>();
            for (EdgeResult edgeResult : edgeResults) {
                texts.add(edgeResult.getTranslations().get(0).getText());
            }
            result.setTexts(texts);
        } catch (IOException e) {
            e.printStackTrace();
            result.setTexts(Collections.singletonList(e.getMessage()));
        }
        return result;
    }


    public static void main(String[] args) {
        String token = getToken();
    }

    private static String getToken() {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        Request request = new Request.Builder()
                .url("https://edge.microsoft.com/translate/auth")
                .method("GET", null)
                .addHeader("authority", "edge.microsoft.com")
                .addHeader("accept", "*/*")
                .addHeader("accept-language", "zh-CN,zh;q=0.9,en;q=0.8,en-GB;q=0.7,en-US;q=0.6")
                .addHeader("cache-control", "no-cache")
                .addHeader("origin", "https://github.com")
                .addHeader("pragma", "no-cache")
                .addHeader("referer", "https://github.com/")
                .addHeader("sec-ch-ua", "\"Microsoft Edge\";v=\"113\", \"Chromium\";v=\"113\", \"Not-A.Brand\";v=\"24\"")
                .addHeader("sec-ch-ua-mobile", "?0")
                .addHeader("sec-ch-ua-platform", "\"Windows\"")
                .addHeader("sec-fetch-dest", "empty")
                .addHeader("sec-fetch-mode", "cors")
                .addHeader("sec-fetch-site", "cross-site")
                .addHeader("sec-mesh-client-arch", "x86_64")
                .addHeader("sec-mesh-client-edge-channel", "stable")
                .addHeader("sec-mesh-client-edge-version", "113.0.1774.50")
                .addHeader("sec-mesh-client-os", "Windows")
                .addHeader("sec-mesh-client-os-version", "10.0.19045")
                .addHeader("sec-mesh-client-webview", "0")
                .addHeader("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/113.0.0.0 Safari/537.36 Edg/113.0.1774.50")
                .build();
        try {
            Response response = client.newCall(request).execute();
            if (response.body() == null) {
                ChatGPTCopilotCommonUtil.showFailedNotification(response.code() + "");
                return null;
            }
            String body = response.body().string();
            if (!response.isSuccessful()) {
                ChatGPTCopilotCommonUtil.showFailedNotification(body);
                return null;
            }
            if (response.body() != null) {
                return body;
            }
        } catch (Exception e) {
            e.printStackTrace();
            ChatGPTCopilotCommonUtil.showFailedNotification(e.getMessage());
            return null;
        }
        return null;
    }

}
