package com.example.campusscreen;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AnnouncementApiService {
    private static final String TAG = "AnnouncementApiService";
    // 使用实际的服务器地址
    private static final String BASE_URL = "http://sys.sharpcaterpillar.top:5000/api/v1";
    
    private final OkHttpClient client;
    private final Handler mainHandler;
    
    public AnnouncementApiService() {
        this.client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
        this.mainHandler = new Handler(Looper.getMainLooper());
    }
    
    /**
     * 获取通知公告列表
     * @param page 页码
     * @param limit 每页数量
     * @param type 公告类型（可选）
     * @param isImportant 是否重要通知（可选）
     * @param callback 回调接口
     */
    public void getAnnouncements(int page, int limit, String type, Boolean isImportant, AnnouncementCallback callback) {
        StringBuilder urlBuilder = new StringBuilder(BASE_URL + "/announcements");
        urlBuilder.append("?page=").append(page);
        urlBuilder.append("&limit=").append(limit);
        
        if (type != null && !type.isEmpty()) {
            urlBuilder.append("&type=").append(type);
        }
        
        if (isImportant != null) {
            urlBuilder.append("&is_important=").append(isImportant);
        }
        
        String finalUrl = urlBuilder.toString();
        Log.d(TAG, "=== 开始API调用 ===");
        Log.d(TAG, "请求URL: " + finalUrl);
        Log.d(TAG, "BASE_URL: " + BASE_URL);
        
        Request request = new Request.Builder()
                .url(finalUrl)
                .get()
                .build();
        
        Log.d(TAG, "发送HTTP请求...");
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "获取通知公告失败", e);
                mainHandler.post(() -> callback.onError("网络请求失败: " + e.getMessage()));
            }
            
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    String responseBody = response.body().string();
                    Log.d(TAG, "API响应: " + responseBody);
                    
                    if (response.isSuccessful()) {
                        List<Announcement> announcements = parseAnnouncementList(responseBody);
                        mainHandler.post(() -> callback.onSuccess(announcements));
                    } else {
                        mainHandler.post(() -> callback.onError("服务器错误: " + response.code()));
                    }
                } catch (Exception e) {
                    Log.e(TAG, "解析响应失败", e);
                    mainHandler.post(() -> callback.onError("数据解析失败: " + e.getMessage()));
                }
            }
        });
    }
    
    /**
     * 获取指定公告详情
     * @param announcementId 公告ID
     * @param callback 回调接口
     */
    public void getAnnouncementDetail(int announcementId, AnnouncementDetailCallback callback) {
        String url = BASE_URL + "/announcements/" + announcementId;
        
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "获取公告详情失败", e);
                mainHandler.post(() -> callback.onError("网络请求失败: " + e.getMessage()));
            }
            
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    String responseBody = response.body().string();
                    Log.d(TAG, "公告详情响应: " + responseBody);
                    
                    if (response.isSuccessful()) {
                        Announcement announcement = parseAnnouncementDetail(responseBody);
                        mainHandler.post(() -> callback.onSuccess(announcement));
                    } else {
                        mainHandler.post(() -> callback.onError("服务器错误: " + response.code()));
                    }
                } catch (Exception e) {
                    Log.e(TAG, "解析公告详情失败", e);
                    mainHandler.post(() -> callback.onError("数据解析失败: " + e.getMessage()));
                }
            }
        });
    }
    
    /**
     * 解析通知公告列表响应
     */
    private List<Announcement> parseAnnouncementList(String responseBody) throws JSONException {
        List<Announcement> announcements = new ArrayList<>();
        
        JSONObject response = new JSONObject(responseBody);
        if (response.getBoolean("success")) {
            JSONObject data = response.getJSONObject("data");
            JSONArray items = data.getJSONArray("items");
            
            for (int i = 0; i < items.length(); i++) {
                JSONObject item = items.getJSONObject(i);
                Announcement announcement = new Announcement();
                
                announcement.setId(item.getInt("id"));
                announcement.setTitle(item.getString("title"));
                announcement.setContent(item.getString("content"));
                announcement.setAuthorName(item.optString("author_name", ""));
                announcement.setType(item.getString("type"));
                boolean isImportant = item.getBoolean("is_important");
                announcement.setImportant(isImportant);
                announcement.setCreatedAt(item.getString("created_at"));
                
                Log.d(TAG, "解析消息 " + (i+1) + ": 标题=" + announcement.getTitle() + ", is_important=" + isImportant + ", 创建时间=" + announcement.getCreatedAt());
                
                announcements.add(announcement);
            }
        }
        
        return announcements;
    }
    
    /**
     * 解析公告详情响应
     */
    private Announcement parseAnnouncementDetail(String responseBody) throws JSONException {
        JSONObject response = new JSONObject(responseBody);
        if (response.getBoolean("success")) {
            JSONObject data = response.getJSONObject("data");
            
            Announcement announcement = new Announcement();
            announcement.setId(data.getInt("id"));
            announcement.setTitle(data.getString("title"));
            announcement.setContent(data.getString("content"));
            announcement.setAuthorName(data.optString("author_name", ""));
            announcement.setType(data.getString("type"));
            announcement.setImportant(data.getBoolean("is_important"));
            announcement.setCreatedAt(data.getString("created_at"));
            
            return announcement;
        }
        
        throw new JSONException("响应格式错误");
    }
    
    /**
     * 通知公告列表回调接口
     */
    public interface AnnouncementCallback {
        void onSuccess(List<Announcement> announcements);
        void onError(String error);
    }
    
    /**
     * 公告详情回调接口
     */
    public interface AnnouncementDetailCallback {
        void onSuccess(Announcement announcement);
        void onError(String error);
    }
}