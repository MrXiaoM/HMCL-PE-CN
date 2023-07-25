package com.tungsten.hmclpe.sweetrice.update;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.*;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

public class ResourcePackUpdater {
    private static final String OWNER = "SweetRiceMC";
    private static final String REPO = "ResourcePack";
    private static final String REMOTE_PATH = "/resource.zip";
    private static final String LOCAL_PATH = "/resourcepacks/服务器材质包.zip";
    private static final String LOCAL_SHA1 = "/SweetRiceMC/last_sha1";
    private static final String[] MIRRORS = {
            "https://github.moeyy.cn/",
            "https://ghproxy.homeboyc.cn/",
            "https://ghps.cc/",
            "https://hub.gitmirror.com/",
            "https://gh.ddlc.top/",
            "https://ghproxy.net/",
            "https://ghproxy.com/",
            ""
    };
    private static String localSha1 = "";

    static String urlEncode(String s) {
        try {
            return URLEncoder.encode(s, "utf-8");
        } catch (Throwable t) {
            return s;
        }
    }

    public static boolean check(File mcDir) {
        File fileSha1 = new File(mcDir, LOCAL_SHA1);
        File fileLocal = new File(mcDir, LOCAL_PATH);
        if (localSha1.isEmpty()) {
            if (fileSha1.exists()) {
                localSha1 = readAsString(fileSha1);
            }
        }
        OkHttpClient client = new OkHttpClient.Builder()
                .callTimeout(Duration.ofSeconds(30))
                .connectTimeout(Duration.ofSeconds(30))
                .readTimeout(Duration.ofSeconds(30))
                .build();
        // 获取最新 commit
        Request httpGet = new Request.Builder()
                .get()
                .url("https://api.github.com/repos/" + OWNER + "/" + REPO + "/commits?path="
                        + urlEncode(REMOTE_PATH) + "&per_page=1&page=1"
                )
                .header("Content-Type", "application/json")
                .build();


        try (Response response = client.newCall(httpGet).execute()) {
            if (response.body() == null) return false;
            String result = response.body().string();

            // 解析接口返回数据
            JsonElement element = JsonParser.parseString(result);
            JsonArray array = element.isJsonArray() ? element.getAsJsonArray() : null;
            if (array == null || array.size() == 0) {
                return false;
            }
            JsonObject obj = array.get(0).getAsJsonObject();
            String remoteSha1 = obj.get("sha").getAsString();
            if (remoteSha1.equalsIgnoreCase(localSha1)) return false;
            localSha1 = remoteSha1;

            saveFromString(fileSha1, localSha1);

            // 下载更新
            if (fileLocal.exists()) FileUtils.deleteQuietly(fileLocal);
            return true;
            //String url = "https://github.com/" + OWNER + "/" + REPO + "/blob/main" + REMOTE_PATH;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String downloadLink() {
        return MIRRORS[0] + "https://github.com/" + OWNER + "/" + REPO + "/blob/main" + REMOTE_PATH;
    }

    public static String readAsString(File file) {
        StringBuilder result = new StringBuilder();
        try (
                FileInputStream input = new FileInputStream(file);
                InputStreamReader reader = new InputStreamReader(input, StandardCharsets.UTF_8);
                BufferedReader br = new BufferedReader(reader)) {
            String lineTxt;
            boolean a = false;
            // 逐行读取
            while ((lineTxt = br.readLine()) != null) {
                // 输出内容到控制台
                if (a) result.append("\n");
                else a = true;
                result.append(lineTxt);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return result.toString();
    }

    public static void saveFromString(File file, String content) {
        try {
            file.getParentFile().mkdirs();
            file.createNewFile();
        } catch (Throwable ignored) {
        }
        try (
                FileOutputStream fos = new FileOutputStream(file);
                OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8)) {
            BufferedWriter out = new BufferedWriter(osw);
            out.write(content);
            out.flush();
            out.close();

        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}