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
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 通义千问API服务类
 * 使用OkHttp调用通义千问API
 */
public class QwenApiService {
    private static final String TAG = "QwenApiService";
    private static final String API_URL = "https://dashscope.aliyuncs.com/api/v1/services/aigc/text-generation/generation";
    private static final String API_KEY = BuildConfig.DASHSCOPE_API_KEY;
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    
    private final OkHttpClient client;
    private final Handler mainHandler;
    private final List<ChatMessage> conversationHistory;
    
    public QwenApiService() {
        this.client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
        this.mainHandler = new Handler(Looper.getMainLooper());
        this.conversationHistory = new ArrayList<>();
    }
    
    /**
     * 聊天消息类
     */
    public static class ChatMessage {
        public final String role;
        public final String content;
        
        public ChatMessage(String role, String content) {
            this.role = role;
            this.content = content;
        }
    }
    
    /**
     * 调用通义千问API
     * @param userMessage 用户输入的消息
     * @param callback 回调接口
     */
    public void sendMessage(String userMessage, ApiCallback callback) {
        try {
            // 添加用户消息到对话历史
            conversationHistory.add(new ChatMessage("user", userMessage));
            
            JSONObject requestJson = createRequestJson();
            RequestBody body = RequestBody.create(requestJson.toString(), JSON);
            
            Request request = new Request.Builder()
                    .url(API_URL)
                    .addHeader("Authorization", "Bearer " + API_KEY)
                    .addHeader("Content-Type", "application/json")
                    .post(body)
                    .build();
            
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e(TAG, "API调用失败", e);
                    // 移除刚添加的用户消息（因为请求失败）
                    if (!conversationHistory.isEmpty()) {
                        conversationHistory.remove(conversationHistory.size() - 1);
                    }
                    mainHandler.post(() -> callback.onError("网络请求失败: " + e.getMessage()));
                }
                
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    try {
                        if (!response.isSuccessful()) {
                            String errorBody = response.body() != null ? response.body().string() : "未知错误";
                            Log.e(TAG, "API响应错误: " + response.code() + ", " + errorBody);
                            // 移除刚添加的用户消息（因为请求失败）
                            if (!conversationHistory.isEmpty()) {
                                conversationHistory.remove(conversationHistory.size() - 1);
                            }
                            mainHandler.post(() -> callback.onError("API调用失败: " + response.code()));
                            return;
                        }
                        
                        String responseBody = response.body().string();
                        Log.d(TAG, "API响应: " + responseBody);
                        
                        String aiResponse = parseResponse(responseBody);
                        // 添加AI回复到对话历史
                        conversationHistory.add(new ChatMessage("assistant", aiResponse));
                        
                        mainHandler.post(() -> callback.onSuccess(aiResponse));
                        
                    } catch (Exception e) {
                        Log.e(TAG, "解析响应失败", e);
                        // 移除刚添加的用户消息（因为解析失败）
                        if (!conversationHistory.isEmpty()) {
                            conversationHistory.remove(conversationHistory.size() - 1);
                        }
                        mainHandler.post(() -> callback.onError("解析响应失败: " + e.getMessage()));
                    }
                }
            });
            
        } catch (Exception e) {
            Log.e(TAG, "创建请求失败", e);
            // 移除刚添加的用户消息（因为创建请求失败）
            if (!conversationHistory.isEmpty()) {
                conversationHistory.remove(conversationHistory.size() - 1);
            }
            callback.onError("创建请求失败: " + e.getMessage());
        }
    }
    
    /**
     * 创建API请求JSON
     */
    private JSONObject createRequestJson() throws JSONException {
        JSONObject request = new JSONObject();
        request.put("model", "qwen-plus");
        
        // 创建输入对象
        JSONObject input = new JSONObject();
        
        // 创建消息数组
        JSONArray messages = new JSONArray();
        
        // 系统消息（始终在第一位）
        JSONObject systemMessage = new JSONObject();
        systemMessage.put("role", "system");
        systemMessage.put("content", "你是一个智能助手，专门为通信电子创新基地提供服务。你需要：\n1. 用友好、专业的语气回答问题\n2. 重点介绍基地的通信技术、电子创新、人才培养等方面\n3. 回答要简洁明了，适合在移动设备上阅读\n4. 可以使用适当的emoji让回答更生动\n5. 记住之前的对话内容，提供连贯的回答");
        messages.put(systemMessage);
        
        // 添加对话历史（限制最近20轮对话以控制token数量）
        int maxHistorySize = 20;
        int startIndex = Math.max(0, conversationHistory.size() - maxHistorySize);
        
        for (int i = startIndex; i < conversationHistory.size(); i++) {
            ChatMessage chatMessage = conversationHistory.get(i);
            JSONObject messageObj = new JSONObject();
            messageObj.put("role", chatMessage.role);
            messageObj.put("content", chatMessage.content);
            messages.put(messageObj);
        }
        
        input.put("messages", messages);
        request.put("input", input);
        
        // 设置参数
        JSONObject parameters = new JSONObject();
        parameters.put("result_format", "message");
        parameters.put("max_tokens", 1500);
        parameters.put("temperature", 0.7);
        request.put("parameters", parameters);
        
        return request;
    }
    
    /**
     * 解析API响应
     */
    private String parseResponse(String responseBody) throws JSONException {
        JSONObject response = new JSONObject(responseBody);
        
        if (response.has("output")) {
            JSONObject output = response.getJSONObject("output");
            if (output.has("choices")) {
                JSONArray choices = output.getJSONArray("choices");
                if (choices.length() > 0) {
                    JSONObject choice = choices.getJSONObject(0);
                    if (choice.has("message")) {
                        JSONObject message = choice.getJSONObject("message");
                        if (message.has("content")) {
                            return message.getString("content");
                        }
                    }
                }
            }
        }
        
        throw new JSONException("无法解析AI响应内容");
    }
    
    /**
     * 清空对话历史
     */
    public void clearConversationHistory() {
        conversationHistory.clear();
        Log.d(TAG, "对话历史已清空");
    }
    
    /**
     * 获取对话历史记录数量
     */
    public int getConversationSize() {
        return conversationHistory.size();
    }
    
    /**
     * 获取对话历史记录
     */
    public List<ChatMessage> getConversationHistory() {
        return new ArrayList<>(conversationHistory);
    }
    
    /**
     * API回调接口
     */
    public interface ApiCallback {
        void onSuccess(String response);
        void onError(String error);
    }
}