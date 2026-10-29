package com.example.campusscreen;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AiCultivationModernFragment extends Fragment {
    
    private static final String TAG = "AiCultivationModern";
    
    // API配置
    private static final String STUDY_PLAN_API = "http://sys.sharpcaterpillar.top:7878/api/study_plan";
    private static final String IMAGE_GENERATION_KEY = BuildConfig.DASHSCOPE_API_KEY;
    private static final String IMAGE_API_BASE_URL = "https://dashscope.aliyuncs.com/api/v1";
    
    // 现代化UI组件
    private TextInputEditText etNameModern, etAgeModern, etEducationModern, etMajorModern;
    private TextInputEditText etInterestModern, etGoalModern;
    private LinearLayout btnWechatMiniprogram;
    private Button btnGeneratePlanModern, btnQuickFill, btnClearForm;
    private LinearLayout layoutPlanDisplayModern;
    private TextView tvPlanContentModern;
    private ProgressBar progressGeneration;
    private LinearLayout layoutProgressContainer;
    private ImageView ivGeneratedChart;
    
    // 网络请求相关
    private OkHttpClient httpClient;
    private ExecutorService executorService;
    private Handler mainHandler;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ai_cultivation_modern, container, false);
        
        // 初始化网络请求组件，增加超时时间
        httpClient = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .build();
        executorService = Executors.newFixedThreadPool(3);
        mainHandler = new Handler(Looper.getMainLooper());
        
        initModernViews(view);
        setupModernInteractions();
        
        return view;
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (executorService != null) {
            executorService.shutdown();
        }
    }
    
    private void initModernViews(View view) {
        // 初始化现代化输入组件
        etNameModern = view.findViewById(R.id.et_name_modern);
        etAgeModern = view.findViewById(R.id.et_age_modern);
        etEducationModern = view.findViewById(R.id.et_education_modern);
        etMajorModern = view.findViewById(R.id.et_major_modern);
        etInterestModern = view.findViewById(R.id.et_interest_modern);
        etGoalModern = view.findViewById(R.id.et_goal_modern);
        
        // 初始化现代化按钮组件
        btnWechatMiniprogram = view.findViewById(R.id.btn_wechat_miniprogram);
        btnGeneratePlanModern = view.findViewById(R.id.btn_generate_plan_modern);
        btnQuickFill = view.findViewById(R.id.btn_quick_fill);
        btnClearForm = view.findViewById(R.id.btn_clear_form);
        
        // 初始化显示组件
        layoutPlanDisplayModern = view.findViewById(R.id.layout_plan_display_modern);
        tvPlanContentModern = view.findViewById(R.id.tv_plan_content_modern); // 用于保存完整文本
        
        // 初始化进度条和图片显示组件
        layoutProgressContainer = view.findViewById(R.id.layout_progress_container);
        ivGeneratedChart = view.findViewById(R.id.iv_generated_chart);
        
    }
    
    private void setupModernInteractions() {
        // 核心功能：微信小程序按钮
        btnWechatMiniprogram.setOnClickListener(v -> {
            openWechatMiniprogram();
        });
        
        // AI方案生成按钮
        btnGeneratePlanModern.setOnClickListener(v -> {
            generateModernAiPlan();
        });
        
        // 快速填写按钮
        btnQuickFill.setOnClickListener(v -> {
            quickFillForm();
        });
        
        // 清空表单按钮
        btnClearForm.setOnClickListener(v -> {
            clearForm();
        });
        
        // 保存方案按钮
        
        // 分享方案按钮
        
        // 导出PDF按钮
    }
    
    /**
     * 核心功能：打开微信小程序
     * 这是UI改进的关键功能点
     */
    private void openWechatMiniprogram() {
        try {
            // 跳转到微信小程序界面
            Intent intent = new Intent(getActivity(), WechatMiniprogramActivity.class);
            startActivity(intent);
            Toast.makeText(getContext(), "正在进入微信小程序界面...", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(getContext(), "界面跳转失败，请重试", Toast.LENGTH_SHORT).show();
        }
    }
    
    /**
     * 微信未安装时的处理
     */
    private void showWechatNotInstalledDialog() {
        Toast.makeText(getContext(), 
            "未检测到微信应用\n请安装微信后使用小程序功能\n或使用下方表单直接填写", 
            Toast.LENGTH_LONG).show();
    }
    
    /**
     * 生成现代化AI培养方案
     */
    private void generateModernAiPlan() {
        String name = etNameModern.getText().toString().trim();
        String age = etAgeModern.getText().toString().trim();
        String education = etEducationModern.getText().toString().trim();
        String major = etMajorModern.getText().toString().trim();
        String interest = etInterestModern.getText().toString().trim();
        String goal = etGoalModern.getText().toString().trim();
        
        // 表单验证
        if (name.isEmpty()) {
            Toast.makeText(getContext(), "请输入姓名", Toast.LENGTH_SHORT).show();
            return;
        }
        if (age.isEmpty()) {
            Toast.makeText(getContext(), "请输入年龄", Toast.LENGTH_SHORT).show();
            return;
        }
        if (education.isEmpty()) {
            Toast.makeText(getContext(), "请输入学历", Toast.LENGTH_SHORT).show();
            return;
        }
        if (major.isEmpty()) {
            Toast.makeText(getContext(), "请输入专业", Toast.LENGTH_SHORT).show();
            return;
        }
        if (interest.isEmpty()) {
            Toast.makeText(getContext(), "请输入兴趣方向", Toast.LENGTH_SHORT).show();
            return;
        }
        if (goal.isEmpty()) {
            Toast.makeText(getContext(), "请输入学习目标", Toast.LENGTH_SHORT).show();
            return;
        }
        
        Log.d(TAG, "开始生成AI培养方案");
        
        // 显示进度条
        if (layoutProgressContainer != null) {
            layoutProgressContainer.setVisibility(View.VISIBLE);
            Log.i(TAG, "显示等待动画");
        }
        
        // 调用培养方案API
        callStudyPlanApi(name, age, education, major, interest, goal);
    }
    
    /**
     * 调用学习计划API
     */
    private void callStudyPlanApi(String name, String age, String education, String major, String interest, String goal) {
        Log.i(TAG, "=== 开始调用学习计划API ===");
        Log.i(TAG, "API地址: " + STUDY_PLAN_API);
        
        try {
            JSONObject requestData = new JSONObject();
            requestData.put("name", name);
            requestData.put("age", age);
            requestData.put("education", education);
            requestData.put("major", major);
            requestData.put("interests", interest);
            requestData.put("study_objective", goal);
            
            Log.d(TAG, "构建请求数据成功");
            Log.d(TAG, "请求数据: " + requestData.toString());
            
            RequestBody requestBody = RequestBody.create(
                requestData.toString(),
                MediaType.parse("application/json")
            );
            
            Request request = new Request.Builder()
                .url(STUDY_PLAN_API)
                .post(requestBody)
                .addHeader("Content-Type", "application/json")
                .build();
            
            Log.i(TAG, "发送HTTP请求到: " + request.url());
            Log.i(TAG, "请求方法: " + request.method());
            Log.i(TAG, "请求头: " + request.headers());
            
            httpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e(TAG, "=== API调用失败 ===");
                    Log.e(TAG, "失败原因: " + e.getClass().getSimpleName());
                    Log.e(TAG, "错误信息: " + e.getMessage());
                    Log.e(TAG, "请求URL: " + call.request().url());
                    e.printStackTrace();
                    
                    mainHandler.post(() -> {
                        if (layoutProgressContainer != null) {
                            layoutProgressContainer.setVisibility(View.GONE);
                            Log.i(TAG, "隐藏等待动画 - 网络请求失败");
                        }
                        Toast.makeText(getContext(), "网络请求失败: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
                }
                
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    Log.i(TAG, "=== 收到API响应 ===");
                    Log.i(TAG, "响应状态码: " + response.code());
                    Log.i(TAG, "响应消息: " + response.message());
                    Log.i(TAG, "响应头: " + response.headers());
                    
                    String responseBody = response.body().string();
                    Log.d(TAG, "响应体长度: " + responseBody.length() + " 字符");
                    Log.d(TAG, "完整API响应: " + responseBody);
                    
                    if (response.isSuccessful()) {
                        try {
                            JSONObject jsonResponse = new JSONObject(responseBody);
                            if (jsonResponse.getBoolean("success")) {
                                // 解析培养方案数据
                                JSONObject developmentPlan = jsonResponse.getJSONObject("development_plan");
                                
                                // 生成图片提示词
                                String chartPrompt = createChartPrompt(developmentPlan, name);
                                Log.d(TAG, "图片提示词: " + chartPrompt);
                                
                                // 调用图片生成API
                                generateImage(chartPrompt, developmentPlan);
                            } else {
                                String errorMsg = jsonResponse.optString("message", "未知错误");
                                mainHandler.post(() -> {
                                    if (layoutProgressContainer != null) {
                                        layoutProgressContainer.setVisibility(View.GONE);
                                        Log.i(TAG, "隐藏等待动画 - 培养方案生成失败");
                                    }
                                    Toast.makeText(getContext(), "培养方案生成失败: " + errorMsg, Toast.LENGTH_LONG).show();
                                });
                            }
                        } catch (JSONException e) {
                            Log.e(TAG, "JSON解析错误: " + e.getMessage());
                            mainHandler.post(() -> {
                                if (layoutProgressContainer != null) {
                                    layoutProgressContainer.setVisibility(View.GONE);
                                    Log.i(TAG, "隐藏等待动画 - JSON解析错误");
                                }
                                Toast.makeText(getContext(), "数据解析失败", Toast.LENGTH_SHORT).show();
                            });
                        }
                    } else {
                        mainHandler.post(() -> {
                            if (layoutProgressContainer != null) {
                                layoutProgressContainer.setVisibility(View.GONE);
                                Log.i(TAG, "隐藏等待动画 - 服务器错误: " + response.code());
                            }
                            Toast.makeText(getContext(), "服务器错误: " + response.code(), Toast.LENGTH_SHORT).show();
                        });
                    }
                }
            });
            
        } catch (JSONException e) {
            Log.e(TAG, "构建请求数据失败: " + e.getMessage());
            if (layoutProgressContainer != null) {
                layoutProgressContainer.setVisibility(View.GONE);
                Log.i(TAG, "隐藏等待动画 - 构建请求数据失败");
            }
            Toast.makeText(getContext(), "请求数据构建失败", Toast.LENGTH_SHORT).show();
        }
    }
     
     /**
      * 根据培养方案数据创建图片提示词
      */
     private String createChartPrompt(JSONObject developmentPlan, String name) {
         try {
             StringBuilder prompt = new StringBuilder();
             prompt.append("请创建一个关于").append(name).append("的AI人才培养方案信息图表，包含以下内容：\n\n");
             
             // 学生信息
             if (developmentPlan.has("studentInfo")) {
                 JSONObject studentInfo = developmentPlan.getJSONObject("studentInfo");
                 prompt.append("# 学生信息\n");
                 prompt.append("- 姓名: ").append(studentInfo.optString("name", name)).append("\n");
                 prompt.append("- 年龄: ").append(studentInfo.optString("age", "")).append("\n");
                 prompt.append("- 兴趣方向: ").append(studentInfo.optString("interests", "")).append("\n");
                 prompt.append("- 学习阶段: ").append(studentInfo.optString("learningStage", "")).append("\n\n");
             }
             
             // 发展计划
             if (developmentPlan.has("developmentPlan")) {
                 JSONObject devPlan = developmentPlan.getJSONObject("developmentPlan");
                 prompt.append("# 发展计划\n");
                 
                 if (devPlan.has("recommendedMajors")) {
                     prompt.append("推荐专业: ").append(devPlan.getJSONArray("recommendedMajors").join(", ")).append("\n");
                 }
                 
                 if (devPlan.has("coreCompetencies")) {
                     prompt.append("核心能力: ").append(devPlan.getJSONArray("coreCompetencies").join(", ")).append("\n\n");
                 }
             }
             
             // 时间线目标
             if (developmentPlan.has("timelineGoals")) {
                 JSONObject timeline = developmentPlan.getJSONObject("timelineGoals");
                 prompt.append("# 时间线目标\n");
                 
                 if (timeline.has("shortTerm")) {
                     JSONObject shortTerm = timeline.getJSONObject("shortTerm");
                     prompt.append("短期目标(").append(shortTerm.optString("period", "")).append("):\n");
                     if (shortTerm.has("goals")) {
                         prompt.append(shortTerm.getJSONArray("goals").join(", ")).append("\n");
                     }
                 }
                 
                 if (timeline.has("longTerm")) {
                     JSONObject longTerm = timeline.getJSONObject("longTerm");
                     prompt.append("长期目标(").append(longTerm.optString("period", "")).append("):\n");
                     if (longTerm.has("goals")) {
                         prompt.append(longTerm.getJSONArray("goals").join(", ")).append("\n\n");
                     }
                 }
             }
             
             prompt.append("请将这些信息制作成一个美观的信息图表，使用现代化的设计风格，包含图标和色彩搭配。");
             
             return prompt.toString();
         } catch (JSONException e) {
             Log.e(TAG, "创建图片提示词失败: " + e.getMessage());
             return "请创建一个AI人才培养方案的信息图表，包含学习路径、技能发展和时间规划等内容。";
         }
     }
     
     /**
      * 生成图片
      */
     private void generateImage(String prompt, JSONObject developmentPlan) {
         try {
             JSONObject requestData = new JSONObject();
             requestData.put("model", "wan2.2-t2i-flash");
             
             JSONObject input = new JSONObject();
             input.put("prompt", prompt);
             requestData.put("input", input);
             
             JSONObject parameters = new JSONObject();
             parameters.put("n", 1);
             requestData.put("parameters", parameters);
             
             Log.d(TAG, "图片生成请求: " + requestData.toString());
             
             RequestBody requestBody = RequestBody.create(
                 requestData.toString(),
                 MediaType.parse("application/json")
             );
             
             Request request = new Request.Builder()
                 .url(IMAGE_API_BASE_URL + "/services/aigc/text2image/image-synthesis")
                 .post(requestBody)
                 .addHeader("X-DashScope-Async", "enable")
                 .addHeader("Authorization", "Bearer " + IMAGE_GENERATION_KEY)
                 .addHeader("Content-Type", "application/json")
                 .build();
             
             httpClient.newCall(request).enqueue(new Callback() {
                 @Override
                 public void onFailure(Call call, IOException e) {
                     Log.e(TAG, "图片生成请求失败: " + e.getMessage());
                     mainHandler.post(() -> {
                         // 即使图片生成失败，也显示培养方案内容
                         displayCultivationPlan(developmentPlan, null);
                     });
                 }
                 
                 @Override
                 public void onResponse(Call call, Response response) throws IOException {
                     String responseBody = response.body().string();
                     Log.d(TAG, "图片生成响应: " + responseBody);
                     
                     if (response.isSuccessful()) {
                         try {
                             JSONObject jsonResponse = new JSONObject(responseBody);
                             if (jsonResponse.has("output")) {
                                 JSONObject output = jsonResponse.getJSONObject("output");
                                 String taskId = output.optString("task_id", "");
                                 
                                 if (!taskId.isEmpty()) {
                                     Log.d(TAG, "图片生成任务ID: " + taskId);
                                     // 查询图片生成结果
                                     checkImageGenerationResult(taskId, developmentPlan, 0);
                                 } else {
                                     mainHandler.post(() -> displayCultivationPlan(developmentPlan, null));
                                 }
                             } else {
                                 mainHandler.post(() -> displayCultivationPlan(developmentPlan, null));
                             }
                         } catch (JSONException e) {
                             Log.e(TAG, "图片生成响应解析失败: " + e.getMessage());
                             mainHandler.post(() -> displayCultivationPlan(developmentPlan, null));
                         }
                     } else {
                         Log.e(TAG, "图片生成请求失败，状态码: " + response.code());
                         mainHandler.post(() -> displayCultivationPlan(developmentPlan, null));
                     }
                 }
             });
             
         } catch (JSONException e) {
             Log.e(TAG, "构建图片生成请求失败: " + e.getMessage());
             displayCultivationPlan(developmentPlan, null);
         }
     }
     
     /**
      * 查询图片生成结果
      */
     private void checkImageGenerationResult(String taskId, JSONObject developmentPlan, int attempt) {
         if (attempt >= 30) { // 最大尝试30次（约2.5分钟）
             Log.w(TAG, "图片生成超时");
             mainHandler.post(() -> displayCultivationPlan(developmentPlan, null));
             return;
         }
         
         // 延迟5秒后查询
         executorService.execute(() -> {
             try {
                 Thread.sleep(5000);
                 
                 Request request = new Request.Builder()
                     .url(IMAGE_API_BASE_URL + "/tasks/" + taskId)
                     .get()
                     .addHeader("Authorization", "Bearer " + IMAGE_GENERATION_KEY)
                     .build();
                 
                 httpClient.newCall(request).enqueue(new Callback() {
                     @Override
                     public void onFailure(Call call, IOException e) {
                         Log.e(TAG, "查询图片生成结果失败: " + e.getMessage());
                         mainHandler.post(() -> displayCultivationPlan(developmentPlan, null));
                     }
                     
                     @Override
                     public void onResponse(Call call, Response response) throws IOException {
                         String responseBody = response.body().string();
                         Log.d(TAG, "图片生成结果查询响应: " + responseBody);
                         
                         if (response.isSuccessful()) {
                             try {
                                 JSONObject jsonResponse = new JSONObject(responseBody);
                                 if (jsonResponse.has("output")) {
                                     JSONObject output = jsonResponse.getJSONObject("output");
                                     String status = output.optString("task_status", "");
                                     
                                     if ("SUCCEEDED".equals(status)) {
                                         if (output.has("results")) {
                                             String imageUrl = output.getJSONArray("results")
                                                 .getJSONObject(0)
                                                 .optString("url", "");
                                             
                                             Log.d(TAG, "图片生成成功，URL: " + imageUrl);
                                             mainHandler.post(() -> displayCultivationPlan(developmentPlan, imageUrl));
                                         } else {
                                             mainHandler.post(() -> displayCultivationPlan(developmentPlan, null));
                                         }
                                     } else if ("FAILED".equals(status)) {
                                         Log.e(TAG, "图片生成失败");
                                         mainHandler.post(() -> displayCultivationPlan(developmentPlan, null));
                                     } else {
                                         // 继续查询
                                         checkImageGenerationResult(taskId, developmentPlan, attempt + 1);
                                     }
                                 } else {
                                     mainHandler.post(() -> displayCultivationPlan(developmentPlan, null));
                                 }
                             } catch (JSONException e) {
                                 Log.e(TAG, "解析图片生成结果失败: " + e.getMessage());
                                 mainHandler.post(() -> displayCultivationPlan(developmentPlan, null));
                             }
                         } else {
                             Log.e(TAG, "查询图片生成结果失败，状态码: " + response.code());
                             mainHandler.post(() -> displayCultivationPlan(developmentPlan, null));
                         }
                     }
                 });
                 
             } catch (InterruptedException e) {
                 Log.e(TAG, "查询图片生成结果被中断: " + e.getMessage());
                 mainHandler.post(() -> displayCultivationPlan(developmentPlan, null));
             }
         });
     }
     
     /**
      * 显示培养方案内容和图片
      */
     private void displayCultivationPlan(JSONObject developmentPlan, String imageUrl) {
         mainHandler.post(() -> {
             try {
                 Log.i(TAG, "主线程：开始更新UI以显示培养方案");
                 // 隐藏进度条
                 if (layoutProgressContainer != null) {
                     layoutProgressContainer.setVisibility(View.GONE);
                     Log.d(TAG, "UI更新：隐藏等待动画");
                 }
                 
                 // 格式化并分发内容到UI
                 distributeApiContentToViews(developmentPlan);
                 Log.d(TAG, "UI更新：已将文本内容分发到各个TextView");

                 // 显示图片
                 if (imageUrl != null && !imageUrl.isEmpty() && ivGeneratedChart != null) {
                     ivGeneratedChart.setVisibility(View.VISIBLE);
                     Log.i(TAG, "UI更新：开始加载图片，URL: " + imageUrl);
                     Glide.with(this)
                         .load(imageUrl)
                         .placeholder(R.drawable.bg_chart_placeholder) // 占位图
                         .error(R.drawable.bg_chart_placeholder) // 错误图
                         .fitCenter() // 确保图片完全显示
                         .into(ivGeneratedChart);
                 } else {
                     ivGeneratedChart.setVisibility(View.GONE);
                     Log.w(TAG, "UI更新：图片URL为空或ImageView不存在，隐藏图片视图");
                 }
                 
                 // 显示整个方案区域
                 if (layoutPlanDisplayModern != null) {
                     layoutPlanDisplayModern.setVisibility(View.VISIBLE);
                     Log.d(TAG, "UI更新：显示培养方案结果区域");
                 }
                 
                 Toast.makeText(getContext(), "✨ AI培养方案生成成功！", Toast.LENGTH_SHORT).show();
                 Log.i(TAG, "主线程：UI更新完成");

             } catch (Exception e) {
                 Log.e(TAG, "在主线程显示培养方案失败", e);
                 Toast.makeText(getContext(), "显示培养方案时出错，请稍后重试", Toast.LENGTH_SHORT).show();
             }
         });
     }
     
     /**
      * 格式化培养方案内容
      */
     private String formatCultivationPlan(JSONObject developmentPlan) {
         StringBuilder plan = new StringBuilder();
         
         try {
             // 学生信息
             if (developmentPlan.has("studentInfo")) {
                 JSONObject studentInfo = developmentPlan.getJSONObject("studentInfo");
                 plan.append("🎯 ").append(studentInfo.optString("name", "")).append("的AI人才培养方案\n\n");
                 plan.append("📊 个人画像分析\n");
                 plan.append("━━━━━━━━━━━━━━━━━━━━\n");
                 plan.append("• 年龄: ").append(studentInfo.optString("age", "")).append("\n");
                 plan.append("• 兴趣方向: ").append(studentInfo.optString("interests", "")).append("\n");
                 plan.append("• 学习阶段: ").append(studentInfo.optString("learningStage", "")).append("\n\n");
             }
             
             // 发展计划
             if (developmentPlan.has("developmentPlan")) {
                 JSONObject devPlan = developmentPlan.getJSONObject("developmentPlan");
                 plan.append("🚀 培养路径规划\n");
                 plan.append("━━━━━━━━━━━━━━━━━━━━\n");
                 
                 if (devPlan.has("recommendedMajors")) {
                     plan.append("推荐专业方向:\n");
                     for (int i = 0; i < devPlan.getJSONArray("recommendedMajors").length(); i++) {
                         plan.append("• ").append(devPlan.getJSONArray("recommendedMajors").getString(i)).append("\n");
                     }
                     plan.append("\n");
                 }
                 
                 if (devPlan.has("coreCompetencies")) {
                     plan.append("核心能力培养:\n");
                     for (int i = 0; i < devPlan.getJSONArray("coreCompetencies").length(); i++) {
                         plan.append("• ").append(devPlan.getJSONArray("coreCompetencies").getString(i)).append("\n");
                     }
                     plan.append("\n");
                 }
             }
             
             // 个性化建议
             if (developmentPlan.has("personalizedAdvice")) {
                 JSONObject advice = developmentPlan.getJSONObject("personalizedAdvice");
                 plan.append("🎯 个性化建议\n");
                 plan.append("━━━━━━━━━━━━━━━━━━━━\n");
                 plan.append("• 优势: ").append(advice.optString("strengths", "")).append("\n");
                 plan.append("• 挑战: ").append(advice.optString("challenges", "")).append("\n");
                 if (advice.has("strategies")) {
                     plan.append("• 策略建议:\n");
                     for (int i = 0; i < advice.getJSONArray("strategies").length(); i++) {
                         plan.append("  - ").append(advice.getJSONArray("strategies").getString(i)).append("\n");
                     }
                 }
                 plan.append("\n");
             }
             
             // 时间线目标
             if (developmentPlan.has("timelineGoals")) {
                 JSONObject timeline = developmentPlan.getJSONObject("timelineGoals");
                 plan.append("💡 下一步行动\n");
                 plan.append("━━━━━━━━━━━━━━━━━━━━\n");
                 
                 if (timeline.has("shortTerm")) {
                     JSONObject shortTerm = timeline.getJSONObject("shortTerm");
                     plan.append("短期目标(").append(shortTerm.optString("period", "")).append("):\n");
                     if (shortTerm.has("goals")) {
                         for (int i = 0; i < shortTerm.getJSONArray("goals").length(); i++) {
                             plan.append("• ").append(shortTerm.getJSONArray("goals").getString(i)).append("\n");
                         }
                     }
                     plan.append("\n");
                 }
                 
                 if (timeline.has("longTerm")) {
                     JSONObject longTerm = timeline.getJSONObject("longTerm");
                     plan.append("长期目标(").append(longTerm.optString("period", "")).append("):\n");
                     if (longTerm.has("goals")) {
                         for (int i = 0; i < longTerm.getJSONArray("goals").length(); i++) {
                             plan.append("• ").append(longTerm.getJSONArray("goals").getString(i)).append("\n");
                         }
                     }
                     plan.append("\n");
                 }
             }
             
             // 预期成果
             plan.append("✨ 预期成果\n");
             plan.append("━━━━━━━━━━━━━━━━━━━━\n");
             plan.append("🏆 掌握AI核心技术栈\n");
             plan.append("🚀 具备独立项目开发能力\n");
             plan.append("💼 获得行业认可的专业技能\n");
             plan.append("🌟 成为AI领域的专业人才\n");
             
         } catch (JSONException e) {
             Log.e(TAG, "格式化培养方案失败: " + e.getMessage());
             plan.append("培养方案生成成功，但格式化时出现问题。");
         }
         
         return plan.toString();
     }
     
     /**
      * 将API返回的培养方案内容分发到两列布局
      */
     private void distributeApiContentToViews(JSONObject plan) throws JSONException {
         Log.d(TAG, "开始将JSON内容分发到UI视图");

         // 设置主标题
         String name = plan.getJSONObject("studentInfo").optString("name", "同学");
         TextView tvMainTitle = layoutPlanDisplayModern.findViewById(R.id.tv_plan_title);
         setTextViewContent(tvMainTitle, "为" + name + "定制的培养方案");

         // 1. 发展计划
         View cardDevPlan = layoutPlanDisplayModern.findViewById(R.id.card_development_plan);
         setCardContent(cardDevPlan, "发展计划", formatDevelopmentPlan(plan.optJSONObject("developmentPlan")));
         
         // 2. 时间目标规划
         View cardTimeline = layoutPlanDisplayModern.findViewById(R.id.card_timeline_goals);
         setCardContent(cardTimeline, "时间目标规划", formatTimelineGoals(plan.optJSONObject("timelineGoals")));

         // 3. 学习资源推荐
         View cardResources = layoutPlanDisplayModern.findViewById(R.id.card_learning_resources);
         setCardContent(cardResources, "学习资源推荐", formatLearningResources(plan.optJSONObject("learningResources")));

         // 4. 技能发展
         View cardSkills = layoutPlanDisplayModern.findViewById(R.id.card_skill_development);
         setCardContent(cardSkills, "技能发展", formatSkillDevelopment(plan.optJSONObject("skillDevelopment")));

         // 5. 职业发展指导
         View cardCareer = layoutPlanDisplayModern.findViewById(R.id.card_career_guidance);
         setCardContent(cardCareer, "职业发展指导", formatCareerGuidance(plan.optJSONObject("careerGuidance")));

         // 6. 个性化建议
         View cardSuggestions = layoutPlanDisplayModern.findViewById(R.id.card_personalized_suggestions);
         setCardContent(cardSuggestions, "个性化建议", formatPersonalizedSuggestions(plan.optJSONObject("personalizedAdvice")));
         
         Log.d(TAG, "JSON内容分发完成");
     }

     private String formatDevelopmentPlan(JSONObject obj) throws JSONException {
         if (obj == null) return "暂无内容";
         StringBuilder sb = new StringBuilder();
         sb.append("推荐专业方向:\n").append(formatJsonArray(obj.optJSONArray("recommendedMajors")));
         sb.append("\n核心能力培养:\n").append(formatJsonArray(obj.optJSONArray("coreCompetencies")));
         return sb.toString();
     }

     private String formatTimelineGoals(JSONObject obj) throws JSONException {
         if (obj == null) return "暂无内容";
         StringBuilder sb = new StringBuilder();
         JSONObject shortTerm = obj.optJSONObject("shortTerm");
         if (shortTerm != null) {
             sb.append("短期目标 (").append(shortTerm.optString("period", "6个月内")).append("):\n");
             sb.append(formatJsonArray(shortTerm.optJSONArray("goals")));
         }
         JSONObject mediumTerm = obj.optJSONObject("mediumTerm");
         if (mediumTerm != null) {
             sb.append("\n中期目标 (").append(mediumTerm.optString("period", "1-2年内")).append("):\n");
             sb.append(formatJsonArray(mediumTerm.optJSONArray("goals")));
         }
         JSONObject longTerm = obj.optJSONObject("longTerm");
         if (longTerm != null) {
             sb.append("\n长期目标 (").append(longTerm.optString("period", "3-5年内")).append("):\n");
             sb.append(formatJsonArray(longTerm.optJSONArray("goals")));
         }
         return sb.toString();
     }

     private String formatLearningResources(JSONObject obj) throws JSONException {
         if (obj == null) return "暂无内容";
         StringBuilder sb = new StringBuilder();
         sb.append("在线平台:\n").append(formatJsonArray(obj.optJSONArray("onlinePlatforms")));
         sb.append("\n推荐书籍:\n").append(formatJsonArray(obj.optJSONArray("books")));
         sb.append("\n实践项目:\n").append(formatJsonArray(obj.optJSONArray("practicalProjects")));
         return sb.toString();
     }

     private String formatSkillDevelopment(JSONObject obj) throws JSONException {
         if (obj == null) return "暂无内容";
         StringBuilder sb = new StringBuilder();
         sb.append("技术技能:\n").append(formatJsonArray(obj.optJSONArray("technicalSkills")));
         sb.append("\n软技能:\n").append(formatJsonArray(obj.optJSONArray("softSkills")));
         sb.append("\n认证证书:\n").append(formatJsonArray(obj.optJSONArray("certifications")));
         return sb.toString();
     }

     private String formatCareerGuidance(JSONObject obj) throws JSONException {
         if (obj == null) return "暂无内容";
         StringBuilder sb = new StringBuilder();
         sb.append("行业趋势:\n").append(obj.optString("industryTrends", "暂无")).append("\n");
         sb.append("\n职业路径:\n").append(formatJsonArray(obj.optJSONArray("careerPaths")));
         return sb.toString();
     }

     private String formatPersonalizedSuggestions(JSONObject obj) throws JSONException {
         if (obj == null) return "暂无内容";
         StringBuilder sb = new StringBuilder();
         sb.append("优势:\n").append(obj.optString("strengths", "暂无")).append("\n");
         sb.append("\n挑战:\n").append(obj.optString("challenges", "暂无")).append("\n");
         sb.append("\n准备建议:\n").append(formatJsonArray(obj.optJSONArray("strategies")));
         return sb.toString();
     }

     private String formatJsonArray(org.json.JSONArray jsonArray) throws JSONException {
         if (jsonArray == null || jsonArray.length() == 0) return "  暂无\n";
         StringBuilder builder = new StringBuilder();
         for (int i = 0; i < jsonArray.length(); i++) {
             builder.append("  • ").append(jsonArray.getString(i)).append("\n");
         }
         return builder.toString();
     }

     private void setCardContent(View cardView, String title, String content) {
         if (cardView == null) return;
         TextView tvTitle = cardView.findViewById(R.id.tv_card_title);
         TextView tvContent = cardView.findViewById(R.id.tv_card_content);
         
         if (tvTitle != null) {
             tvTitle.setText(title);
         }
         if (tvContent != null) {
             if (content != null && !content.trim().isEmpty()) {
                 tvContent.setText(content.trim());
             } else {
                 tvContent.setText("暂无内容");
             }
         }
     }

     private void setTextViewContent(TextView textView, String content) {
         if (textView != null) {
             if (content != null && !content.trim().isEmpty()) {
                 textView.setText(content.trim());
             } else {
                 textView.setText("暂无内容");
             }
         }
     }
     
    
    /**
     * 现代化AI培养方案生成逻辑
     */
    private String generateModernAiCultivationPlan(String name, String age, String education, String major, 
                                                   String experience, String skillLevel, String interest, String goal) {
        StringBuilder plan = new StringBuilder();
        
        plan.append("🎯 ").append(name).append("的AI人才培养方案\n\n");
        
        plan.append("📊 个人画像分析\n");
        plan.append("━━━━━━━━━━━━━━━━━━━━\n");
        plan.append("👤 姓名：").append(name).append("\n");
        if (!age.isEmpty()) plan.append("🎂 年龄：").append(age).append("岁\n");
        plan.append("🎓 学历：").append(education).append("\n");
        plan.append("📚 专业：").append(major).append("\n");
        if (!experience.isEmpty()) plan.append("💼 经验：").append(experience).append("年\n");
        if (!skillLevel.isEmpty()) plan.append("⭐ 技能：").append(skillLevel).append("\n");
        plan.append("\n");
        
        plan.append("🚀 培养路径规划\n");
        plan.append("━━━━━━━━━━━━━━━━━━━━\n");
        plan.append("📍 第一阶段：基础能力建设（1-3个月）\n");
        plan.append("   • Python编程基础\n");
        plan.append("   • 数据结构与算法\n");
        plan.append("   • 机器学习概论\n\n");
        
        plan.append("📍 第二阶段：核心技能提升（4-8个月）\n");
        plan.append("   • 深度学习框架（TensorFlow/PyTorch）\n");
        plan.append("   • 计算机视觉/自然语言处理\n");
        plan.append("   • 项目实战训练\n\n");
        
        plan.append("📍 第三阶段：专业方向深化（9-12个月）\n");
        if (!interest.isEmpty()) {
            plan.append("   • ").append(interest).append("专项深化\n");
        }
        plan.append("   • 行业应用实践\n");
        plan.append("   • 团队协作项目\n\n");
        
        plan.append("🎯 个性化建议\n");
        plan.append("━━━━━━━━━━━━━━━━━━━━\n");
        if (!goal.isEmpty()) {
            plan.append("🎪 目标导向：基于您的'").append(goal).append("'目标\n");
        }
        plan.append("📱 移动学习：推荐使用微信小程序进行碎片化学习\n");
        plan.append("🤝 社群支持：加入AI学习社群，获得持续指导\n");
        plan.append("📈 进度跟踪：定期评估学习效果，调整培养方案\n\n");
        
        plan.append("💡 下一步行动\n");
        plan.append("━━━━━━━━━━━━━━━━━━━━\n");
        plan.append("1️⃣ 点击'进入微信小程序'获取详细课程\n");
        plan.append("2️⃣ 制定个人学习计划和时间安排\n");
        plan.append("3️⃣ 开始第一阶段的基础学习\n");
        plan.append("4️⃣ 定期回顾和调整培养方案\n\n");
        
        plan.append("✨ 预期成果\n");
        plan.append("━━━━━━━━━━━━━━━━━━━━\n");
        plan.append("🏆 掌握AI核心技术栈\n");
        plan.append("🚀 具备独立项目开发能力\n");
        plan.append("💼 获得行业认可的专业技能\n");
        plan.append("🌟 成为AI领域的专业人才\n");
        
        return plan.toString();
    }
    
    /**
     * 快速填写示例数据
     */
    private void quickFillForm() {
        etNameModern.setText("张同学");
        etAgeModern.setText("22");
        etEducationModern.setText("本科");
        etMajorModern.setText("计算机科学与技术");
        etInterestModern.setText("机器学习、深度学习");
        etGoalModern.setText("成为AI工程师");
        
        Toast.makeText(getContext(), "已快速填写示例数据", Toast.LENGTH_SHORT).show();
    }
    
    /**
     * 清空表单
     */
    private void clearForm() {
        etNameModern.setText("");
        etAgeModern.setText("");
        etEducationModern.setText("");
        etMajorModern.setText("");
        etInterestModern.setText("");
        etGoalModern.setText("");
        
        layoutPlanDisplayModern.setVisibility(View.GONE);
        
        Toast.makeText(getContext(), "表单已清空", Toast.LENGTH_SHORT).show();
    }
    
}