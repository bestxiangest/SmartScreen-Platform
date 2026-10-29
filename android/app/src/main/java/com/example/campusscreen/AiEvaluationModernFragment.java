package com.example.campusscreen;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.android.material.textfield.TextInputEditText;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;

import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

/**
 * 现代化AI项目评估Fragment
 * 提供现代化的UI设计和微信小程序入口功能
 */
public class AiEvaluationModernFragment extends Fragment {

    // 日志标签
    private static final String TAG = "AiEvaluationFragment";
    
    // UI组件
    private LinearLayout btnWechatEvaluation;
    private Button btnStartEvaluation;
    private Button btnSaveEvaluation;
    private Button btnShareEvaluation;
    private Button btnQuickFill;
    private Button btnClearForm;
    
    // 输入字段
    private TextInputEditText etProjectName;
    private TextInputEditText etProjectIntroduce;
    private TextInputEditText etTechField;
    private TextInputEditText etProjectDuration;
    private TextInputEditText etBudgetRange;
    private TextInputEditText etTeamSize;
    
    // 状态和进度
    private LinearLayout layoutProgressContainer;
    private ProgressBar progressEvaluation;
    
    // 评估状态
    private boolean isEvaluationRunning = false;
    private boolean hasEvaluationResult = false;
    
    // API相关
    private static final String EVALUATION_API_URL = "http://sys.sharpcaterpillar.top:7878/api/evaluate_ai_project";
    private static final String IMAGE_API_KEY = BuildConfig.DASHSCOPE_API_KEY;
    private static final String IMAGE_API_BASE_URL = "https://dashscope.aliyuncs.com/api/v1";
    private ExecutorService executorService = Executors.newCachedThreadPool();
    private Handler mainHandler = new Handler(Looper.getMainLooper());
    
    // 评估结果存储
    private JSONObject evaluationResult;
    private String generatedImageUrl;
    
    // 评估结果显示组件
    private LinearLayout layoutEvaluationDisplay;
    private TextView tvEvaluationTitle;
    private TextView tvEvaluationDetails; // 用于存储完整结果
    private ImageView ivGeneratedChart;

    // 卡片视图
    private View cardTechFeasibility;
    private View cardBusinessValue;
    private View cardInnovation;
    private View cardRiskAssessment;
    private View cardMarketProspects;
    private View cardOverallSuggestion;
    private View cardTechnicalArchitecture;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ai_evaluation_modern, container, false);
        
        initViews(view);
        setupInteractions();
        
        return view;
    }

    /**
     * 初始化UI组件
     */
    private void initViews(View view) {
        // 按钮组件
        // 按钮组件
        btnWechatEvaluation = view.findViewById(R.id.btn_wechat_miniprogram);
        btnStartEvaluation = view.findViewById(R.id.btn_start_evaluation);
        btnSaveEvaluation = view.findViewById(R.id.btn_save_evaluation);
        btnShareEvaluation = view.findViewById(R.id.btn_share_evaluation);
        btnQuickFill = view.findViewById(R.id.btn_quick_fill);
        btnClearForm = view.findViewById(R.id.btn_clear_form);

        // 输入字段
        etProjectName = view.findViewById(R.id.et_project_name);
        etProjectIntroduce = view.findViewById(R.id.et_project_introduce);
        etTechField = view.findViewById(R.id.et_tech_field);
        etProjectDuration = view.findViewById(R.id.et_project_duration);
        etBudgetRange = view.findViewById(R.id.et_budget_range);
        etTeamSize = view.findViewById(R.id.et_team_size);

        // 状态和进度
        layoutProgressContainer = view.findViewById(R.id.layout_progress_container);
        progressEvaluation = view.findViewById(R.id.progress_evaluation);

        // 评估结果显示组件
        layoutEvaluationDisplay = view.findViewById(R.id.layout_evaluation_display);
        tvEvaluationTitle = view.findViewById(R.id.tv_evaluation_title);
        tvEvaluationDetails = view.findViewById(R.id.tv_evaluation_details);
        ivGeneratedChart = view.findViewById(R.id.iv_generated_chart);

        // 卡片视图
        cardTechFeasibility = view.findViewById(R.id.card_tech_feasibility);
        cardBusinessValue = view.findViewById(R.id.card_business_value);
        cardInnovation = view.findViewById(R.id.card_innovation);
        cardRiskAssessment = view.findViewById(R.id.card_risk_assessment);
        cardMarketProspects = view.findViewById(R.id.card_market_prospects);
        cardOverallSuggestion = view.findViewById(R.id.card_overall_suggestion);
        cardTechnicalArchitecture = view.findViewById(R.id.card_technical_architecture);
    }

    /**
     * 设置交互逻辑
     */
    private void setupInteractions() {
        // 微信小程序入口按钮
        btnWechatEvaluation.setOnClickListener(v -> openWechatMiniProgram());
        
        // 启动评估按钮
        btnStartEvaluation.setOnClickListener(v -> startAiEvaluation());
        
        // 保存按钮
        btnSaveEvaluation.setOnClickListener(v -> saveEvaluation());
        
        // 分享按钮
        btnShareEvaluation.setOnClickListener(v -> shareEvaluation());
        
        // 导出PDF按钮
        
        // 快速填写按钮
        btnQuickFill.setOnClickListener(v -> quickFillForm());
        
        // 清空表单按钮
        btnClearForm.setOnClickListener(v -> clearForm());
    }

    /**
     * 打开微信小程序
     */
    private void openWechatMiniProgram() {
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
     * 启动AI评估
     */
    private void startAiEvaluation() {
        Log.d(TAG, "开始AI评估流程");
        
        if (!validateForm()) {
            Log.w(TAG, "表单验证失败");
            Toast.makeText(getContext(), "请填写完整的项目信息", Toast.LENGTH_SHORT).show();
            return;
        }
        
        if (isEvaluationRunning) {
            Log.w(TAG, "评估已在进行中，忽略重复请求");
            Toast.makeText(getContext(), "评估正在进行中，请稍候...", Toast.LENGTH_SHORT).show();
            return;
        }
        
        Log.d(TAG, "表单验证通过，开始评估");
        // 开始评估
        isEvaluationRunning = true;
        // 更新UI为加载状态
        layoutProgressContainer.setVisibility(View.VISIBLE);
        btnStartEvaluation.setText("评估中...");
        btnStartEvaluation.setEnabled(false);
        layoutEvaluationDisplay.setVisibility(View.GONE);
        
        // 调用API进行评估
        executorService.execute(() -> {
            try {
                Log.d(TAG, "开始调用项目评估API");
                // 1. 调用项目评估API
                JSONObject projectData = createProjectData();
                Log.d(TAG, "项目数据构建完成: " + projectData.toString());
                
                JSONObject apiResponse = callEvaluationAPI(projectData);
                
                if (apiResponse != null && apiResponse.optBoolean("success", false)) {
                    Log.d(TAG, "评估API调用成功，开始生成图表");
                    evaluationResult = apiResponse.getJSONObject("evaluation_result");
                    
                    
                    // 2. 生成图表提示词并调用图像生成API
                    String chartPrompt = createChartPrompt(evaluationResult);
                    Log.d(TAG, "图表提示词生成完成: " + chartPrompt);
                    
                    String taskId = generateImage(chartPrompt);
                    Log.d(TAG, "图像生成任务ID: " + taskId);
                    
                    if (taskId != null) {
                        Log.d(TAG, "开始等待图片生成完成");
                        // 3. 轮询获取图片结果
                        generatedImageUrl = waitForImageGeneration(taskId);
                        Log.d(TAG, "图片生成完成，URL: " + generatedImageUrl);
                    } else {
                        Log.w(TAG, "图像生成任务创建失败");
                    }
                    
                    // 4. 更新UI显示结果
                    mainHandler.post(() -> {
                        Log.d(TAG, "更新UI显示评估结果");
                        isEvaluationRunning = false;
                        hasEvaluationResult = true;
                        layoutProgressContainer.setVisibility(View.GONE);
                        btnStartEvaluation.setText("重新评估");
                        btnStartEvaluation.setEnabled(true);

                        // 显示评估结果
                        displayEvaluationResults();

                        Toast.makeText(getContext(), "AI评估完成！", Toast.LENGTH_SHORT).show();
                    });
                } else {
                    Log.e(TAG, "评估API调用失败");
                    String errorMsg = apiResponse != null ? apiResponse.optString("message", "评估失败") : "网络连接失败";
                    mainHandler.post(() -> {
                        isEvaluationRunning = false;
                        layoutProgressContainer.setVisibility(View.GONE);
                        btnStartEvaluation.setText("重新评估");
                        btnStartEvaluation.setEnabled(true);
                        Toast.makeText(getContext(), "评估失败: " + errorMsg, Toast.LENGTH_LONG).show();
                    });
                }
            } catch (Exception e) {
                Log.e(TAG, "评估过程发生异常", e);
                e.printStackTrace();
                mainHandler.post(() -> {
                    isEvaluationRunning = false;
                    layoutProgressContainer.setVisibility(View.GONE);
                    btnStartEvaluation.setText("重新评估");
                    btnStartEvaluation.setEnabled(true);
                    Toast.makeText(getContext(), "评估过程中发生错误", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    /**
     * 验证表单
     */
    private boolean validateForm() {
        return !etProjectName.getText().toString().trim().isEmpty() &&
                !etProjectIntroduce.getText().toString().trim().isEmpty() &&
               !etTechField.getText().toString().trim().isEmpty() &&
               !etProjectDuration.getText().toString().trim().isEmpty() &&
               !etBudgetRange.getText().toString().trim().isEmpty() &&
               !etTeamSize.getText().toString().trim().isEmpty();
    }

    /**
     * 保存评估
     */
    private void saveEvaluation() {
        if (!hasEvaluationResult) {
            Toast.makeText(getContext(), "请先完成评估", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // 模拟保存过程
        Toast.makeText(getContext(), "评估结果已保存", Toast.LENGTH_SHORT).show();
    }

    /**
     * 分享评估
     */
    private void shareEvaluation() {
        if (!hasEvaluationResult) {
            Toast.makeText(getContext(), "请先完成评估", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // 创建分享内容
        String shareContent = generateShareContent();
        
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareContent);
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "AI项目评估报告");
        
        startActivity(Intent.createChooser(shareIntent, "分享评估报告"));
    }

    /**
     * 生成分享内容
     */
    private String generateShareContent() {
        StringBuilder content = new StringBuilder();
        content.append("📊 AI项目评估报告\n\n");
        content.append("项目名称：").append(etProjectName.getText().toString()).append("\n");
        content.append("技术领域：").append(etTechField.getText().toString()).append("\n");
        content.append("项目周期：").append(etProjectDuration.getText().toString()).append("个月\n");
        content.append("预算范围：").append(etBudgetRange.getText().toString()).append("万元\n");
        content.append("团队规模：").append(etTeamSize.getText().toString()).append("人\n\n");
        content.append("🤖 通过AI智能评估系统生成");
        
        return content.toString();
    }

    /**
     * 导出PDF
     */
    private void exportToPdf() {
        if (!hasEvaluationResult) {
            Toast.makeText(getContext(), "请先完成评估", Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(getContext(), "PDF报告导出功能开发中...", Toast.LENGTH_SHORT).show();
    }

    private void exportToExcel() {
        if (!hasEvaluationResult) {
            Toast.makeText(getContext(), "请先完成评估", Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(getContext(), "Excel数据导出功能开发中...", Toast.LENGTH_SHORT).show();
    }

    /**
     * 快速填写表单
     */
    private void quickFillForm() {
        etProjectName.setText("智能校园AI助手");
        etProjectIntroduce.setText("一个基于大语言模型的智能问答和校园服务助手，旨在提高学生和教职工的信息获取效率。");
        etTechField.setText("人工智能, 自然语言处理");
        etProjectDuration.setText("6");
        etBudgetRange.setText("20");
        etTeamSize.setText("5");
        
        Toast.makeText(getContext(), "表单已快速填写", Toast.LENGTH_SHORT).show();
    }

    /**
     * 清空表单
     */
    private void clearForm() {
        etProjectName.setText("");
        etProjectIntroduce.setText("");
        etTechField.setText("");
        etProjectDuration.setText("");
        etBudgetRange.setText("");
        etTeamSize.setText("");

        // 重置状态
        isEvaluationRunning = false;
        hasEvaluationResult = false;
        evaluationResult = null;
        generatedImageUrl = null;
        btnStartEvaluation.setText("启动AI评估");
        btnStartEvaluation.setEnabled(true);

        // 重置UI显示
        layoutEvaluationDisplay.setVisibility(View.GONE);
        layoutProgressContainer.setVisibility(View.GONE);
        if (ivGeneratedChart != null) {
            ivGeneratedChart.setVisibility(View.GONE);
            ivGeneratedChart.setImageDrawable(null);
        }
         
         Toast.makeText(getContext(), "表单已清空", Toast.LENGTH_SHORT).show();
    }
    
    /**
     * 创建项目数据JSON
     */
    private JSONObject createProjectData() throws JSONException {
        JSONObject projectData = new JSONObject();
        projectData.put("project_name", etProjectName.getText().toString().trim());
        projectData.put("project_description", etProjectIntroduce.getText().toString().trim());
        projectData.put("tech_area", etTechField.getText().toString().trim());
        projectData.put("project_duration", etProjectDuration.getText().toString().trim());
        projectData.put("team_size", etTeamSize.getText().toString().trim());
        return projectData;
    }
    
    /**
     * 调用项目评估API
     */
    private JSONObject callEvaluationAPI(JSONObject projectData) {
        try {
            URL url = new URL(EVALUATION_API_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);
            connection.setConnectTimeout(30000);
            connection.setReadTimeout(30000);
            
            // 发送请求数据
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = projectData.toString().getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }
            
            // 读取响应
            int responseCode = connection.getResponseCode();
            BufferedReader reader;
            if (responseCode == HttpURLConnection.HTTP_OK) {
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
            } else {
                reader = new BufferedReader(new InputStreamReader(connection.getErrorStream(), StandardCharsets.UTF_8));
            }
            
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
            
            return new JSONObject(response.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * 创建图表提示词
     */
    private String createChartPrompt(JSONObject evaluationData) {
        try {
            JSONObject projectInfo = evaluationData.getJSONObject("projectInfo");
            JSONObject phases = evaluationData.getJSONObject("implementationPlan");
            JSONObject team = evaluationData.getJSONObject("teamStructure");
            
            StringBuilder prompt = new StringBuilder();
            prompt.append("请创建包含以下多个维度的项目评估信息图表：\n\n");
            prompt.append("# 项目概况 ('").append(projectInfo.getString("name")).append("')\n");
            prompt.append("- 可行性评分: ").append(projectInfo.getString("feasibilityScore")).append("/10\n");
            prompt.append("- 复杂程度: ").append(projectInfo.getString("complexityLevel")).append("\n");
            prompt.append("- 技术领域: ").append(projectInfo.getString("techArea")).append("\n");
            prompt.append("- 团队规模: ").append(projectInfo.getString("teamSize")).append("\n\n");
            
            prompt.append("# 实施计划\n");
            prompt.append("| 阶段 | 时长 | 主要目标 |\n");
            prompt.append("|------|------|-----------|\n");
            
            JSONObject phase1 = phases.getJSONObject("phase1");
            JSONObject phase2 = phases.getJSONObject("phase2");
            JSONObject phase3 = phases.getJSONObject("phase3");
            
            prompt.append("| ").append(phase1.getString("name")).append(" | ")
                  .append(phase1.getString("duration")).append(" | ")
                  .append(phase1.getJSONArray("objectives").getString(0)).append(" |\n");
            prompt.append("| ").append(phase2.getString("name")).append(" | ")
                  .append(phase2.getString("duration")).append(" | ")
                  .append(phase2.getJSONArray("objectives").getString(0)).append(" |\n");
            prompt.append("| ").append(phase3.getString("name")).append(" | ")
                  .append(phase3.getString("duration")).append(" | ")
                  .append(phase3.getJSONArray("objectives").getString(0)).append(" |\n\n");
            
            return prompt.toString();
        } catch (JSONException e) {
            e.printStackTrace();
            return "请创建一个AI项目评估图表，包含项目概况、实施计划、团队结构等信息";
        }
    }
    
    /**
     * 生成图片
     */
    private String generateImage(String promptText) {
        try {
            URL url = new URL(IMAGE_API_BASE_URL + "/services/aigc/text2image/image-synthesis");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("X-DashScope-Async", "enable");
            connection.setRequestProperty("Authorization", "Bearer " + IMAGE_API_KEY);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);
            connection.setConnectTimeout(30000);
            connection.setReadTimeout(30000);
            
            // 构建请求数据
            JSONObject payload = new JSONObject();
            payload.put("model", "wan2.2-t2i-flash");
            
            JSONObject input = new JSONObject();
            input.put("prompt", promptText);
            payload.put("input", input);
            
            JSONObject parameters = new JSONObject();
            parameters.put("n", 1);
            payload.put("parameters", parameters);
            
            // 发送请求
            try (OutputStream os = connection.getOutputStream()) {
                byte[] inputBytes = payload.toString().getBytes(StandardCharsets.UTF_8);
                os.write(inputBytes, 0, inputBytes.length);
            }
            
            // 读取响应
            int responseCode = connection.getResponseCode();
            BufferedReader reader;
            if (responseCode == HttpURLConnection.HTTP_OK) {
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
            } else {
                reader = new BufferedReader(new InputStreamReader(connection.getErrorStream(), StandardCharsets.UTF_8));
            }
            
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
            
            JSONObject responseJson = new JSONObject(response.toString());
            if (responseJson.has("output")) {
                return responseJson.getJSONObject("output").getString("task_id");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * 等待图片生成完成
     */
    private String waitForImageGeneration(String taskId) {
        try {
            int maxAttempts = 30; // 最大尝试次数
            int attempt = 0;
            
            while (attempt < maxAttempts) {
                Thread.sleep(5000); // 等待5秒
                
                URL url = new URL(IMAGE_API_BASE_URL + "/tasks/" + taskId);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Authorization", "Bearer " + IMAGE_API_KEY);
                connection.setConnectTimeout(15000);
                connection.setReadTimeout(15000);
                
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                
                JSONObject responseJson = new JSONObject(response.toString());
                if (responseJson.has("output")) {
                    JSONObject output = responseJson.getJSONObject("output");
                    String status = output.getString("task_status");
                    
                    if ("SUCCEEDED".equals(status)) {
                        return output.getJSONArray("results").getJSONObject(0).getString("url");
                    } else if ("FAILED".equals(status)) {
                        break;
                    }
                }
                
                attempt++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * 显示评估结果
     */
    private void displayEvaluationResults() {
        if (evaluationResult == null) return;

        layoutEvaluationDisplay.setVisibility(View.VISIBLE);
        tvEvaluationTitle.setText(etProjectName.getText().toString() + " - 评估报告");

        // 隐藏按钮
        btnSaveEvaluation.setVisibility(View.GONE);
        btnShareEvaluation.setVisibility(View.GONE);

        try {
            // 1. 项目基本信息
            populateBasicInfoCard(evaluationResult.optJSONObject("projectInfo"));

            // 2. 团队结构
            populateTeamStructureCard(evaluationResult.optJSONObject("teamStructure"));

            // 3. 风险评估
            populateRiskAssessmentCard(evaluationResult.optJSONObject("riskAssessment"));

            // 4. 资源需求
            populateResourceRequirementsCard(evaluationResult.optJSONObject("resourceRequirements"));

            // 5. 实施计划
            populateImplementationPlanCard(evaluationResult.optJSONObject("implementationPlan"));

            // 6. 质量保证 & 成功标准
            populateQaAndSuccessCard(
                evaluationResult.optJSONObject("qualityAssurance"),
                evaluationResult.optJSONObject("successCriteria")
            );

            // 7. 技术架构
            populateTechnicalArchitectureCard(evaluationResult.optJSONObject("technicalArchitecture"));

            // 隐藏的TextView保存完整结果
            tvEvaluationDetails.setText(evaluationResult.toString(2));

            // 加载图表
            if (generatedImageUrl != null && !generatedImageUrl.isEmpty()) {
                ivGeneratedChart.setVisibility(View.VISIBLE);
                loadImageFromUrl(generatedImageUrl);
            } else {
                ivGeneratedChart.setVisibility(View.GONE);
            }
        } catch (JSONException e) {
            Log.e(TAG, "解析评估结果失败", e);
            Toast.makeText(getContext(), "解析评估结果失败: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void populateCard(View cardView, String title, String content) {
        TextView tvCardTitle = cardView.findViewById(R.id.tv_card_title);
        TextView tvCardContent = cardView.findViewById(R.id.tv_card_content);
        tvCardTitle.setText(title);
        tvCardContent.setText(content);
    }

    private void populateBasicInfoCard(JSONObject projectInfo) {
        // Mapped to cardTechFeasibility
        if (projectInfo == null) {
            populateCard(cardTechFeasibility, "项目基本信息", "无相关数据");
            return;
        }
        StringBuilder content = new StringBuilder();
        content.append("项目名称: ").append(projectInfo.optString("name", "N/A")).append("\n\n");
        content.append("描述: ").append(projectInfo.optString("description", "N/A")).append("\n\n");
        content.append("技术领域: ").append(projectInfo.optString("techArea", "N/A")).append("\n");
        content.append("项目周期: ").append(projectInfo.optInt("duration")).append("个月\n");
        content.append("团队规模: ").append(projectInfo.optInt("teamSize")).append("人\n");
        content.append("复杂程度: ").append(projectInfo.optString("complexityLevel", "N/A")).append("\n");
        content.append("可行性评分: ").append(projectInfo.optInt("feasibilityScore"));
        populateCard(cardTechFeasibility, "项目基本信息", content.toString());
    }

    private void populateTeamStructureCard(JSONObject teamStructure) {
        // Mapped to cardBusinessValue
        if (teamStructure == null) {
            populateCard(cardBusinessValue, "团队结构", "无相关数据");
            return;
        }
        StringBuilder content = new StringBuilder();
        org.json.JSONArray roles = teamStructure.optJSONArray("roles");
        if (roles != null) {
            for (int i = 0; i < roles.length(); i++) {
                JSONObject role = roles.optJSONObject(i);
                if (role != null) {
                    content.append(role.optString("position", "未知职位")).append(": ")
                           .append(role.optString("count", "N/A")).append("人\n");

                    org.json.JSONArray responsibilities = role.optJSONArray("responsibilities");
                    if (responsibilities != null) {
                        for (int j = 0; j < responsibilities.length(); j++) {
                            content.append("  - ").append(responsibilities.optString(j)).append("\n");
                        }
                    }
                    if (i < roles.length() - 1) {
                        content.append("\n");
                    }
                }
            }
        }
        populateCard(cardBusinessValue, "团队结构", content.toString().trim());
    }

    private void populateRiskAssessmentCard(JSONObject riskAssessment) {
        // Mapped to cardInnovation
        if (riskAssessment == null) {
            populateCard(cardInnovation, "风险评估", "无相关数据");
            return;
        }
        StringBuilder content = new StringBuilder();
        org.json.JSONArray techRisks = riskAssessment.optJSONArray("technicalRisks");
        if (techRisks != null && techRisks.length() > 0) {
            content.append("技术风险:\n");
            for (int i = 0; i < techRisks.length(); i++) {
                JSONObject risk = techRisks.optJSONObject(i);
                content.append("  - ").append(risk.optString("risk", "N/A"))
                       .append(" (概率: ").append(risk.optString("probability", "N/A"))
                       .append(", 影响: ").append(risk.optString("impact", "N/A")).append(")\n");
            }
        }
        org.json.JSONArray projRisks = riskAssessment.optJSONArray("projectRisks");
        if (projRisks != null && projRisks.length() > 0) {
            content.append("\n项目风险:\n");
            for (int i = 0; i < projRisks.length(); i++) {
                JSONObject risk = projRisks.optJSONObject(i);
                content.append("  - ").append(risk.optString("risk", "N/A"))
                       .append(" (概率: ").append(risk.optString("probability", "N/A"))
                       .append(", 影响: ").append(risk.optString("impact", "N/A")).append(")\n");
            }
        }
        populateCard(cardInnovation, "风险评估", content.toString().trim());
    }

    private void populateResourceRequirementsCard(JSONObject requirements) {
        // Mapped to cardRiskAssessment
        if (requirements == null) {
            populateCard(cardRiskAssessment, "资源需求", "无相关数据");
            return;
        }
        StringBuilder content = new StringBuilder();
        content.append("预估预算: ").append(requirements.optString("estimatedBudget", "N/A")).append("\n\n");
        content.append("硬件: ").append(joinArray(requirements.optJSONArray("hardware"))).append("\n\n");
        content.append("软件: ").append(joinArray(requirements.optJSONArray("software"))).append("\n\n");
        content.append("外部服务: ").append(joinArray(requirements.optJSONArray("externalServices")));
        populateCard(cardRiskAssessment, "资源需求", content.toString().trim());
    }

    private void populateImplementationPlanCard(JSONObject plan) {
        // Mapped to cardMarketProspects
        if (plan == null) {
            populateCard(cardMarketProspects, "实施计划与时间线", "无相关数据");
            return;
        }
        StringBuilder content = new StringBuilder();

        // 解析阶段
        JSONObject phase1 = plan.optJSONObject("phase1");
        if (phase1 != null) {
            content.append(phase1.optString("name", "阶段一")).append(" (").append(phase1.optString("duration", "N/A")).append(")\n");
            content.append("  ").append(joinArray(phase1.optJSONArray("objectives"))).append("\n\n");
        }
        JSONObject phase2 = plan.optJSONObject("phase2");
        if (phase2 != null) {
            content.append(phase2.optString("name", "阶段二")).append(" (").append(phase2.optString("duration", "N/A")).append(")\n");
            content.append("  ").append(joinArray(phase2.optJSONArray("objectives"))).append("\n\n");
        }
        JSONObject phase3 = plan.optJSONObject("phase3");
        if (phase3 != null) {
            content.append(phase3.optString("name", "阶段三")).append(" (").append(phase3.optString("duration", "N/A")).append(")\n");
            content.append("  ").append(joinArray(phase3.optJSONArray("objectives"))).append("\n\n");
        }

        // 解析时间线
        JSONObject timeline = plan.optJSONObject("timeline");
        if (timeline != null) {
            content.append("时间线:\n");
            content.append("  - 关键路径: ").append(joinArray(timeline.optJSONArray("criticalPath"))).append("\n");

            org.json.JSONArray milestones = timeline.optJSONArray("milestones");
            if (milestones != null) {
                content.append("  - 里程碑:\n");
                for (int i = 0; i < milestones.length(); i++) {
                    JSONObject milestone = milestones.optJSONObject(i);
                    if (milestone != null) {
                        content.append("    • ").append(milestone.optString("name"))
                               .append(" (").append(milestone.optString("deadline")).append("): ")
                               .append(milestone.optString("description")).append("\n");
                    }
                }
            }
        }
        populateCard(cardMarketProspects, "实施计划与时间线", content.toString().trim());
    }

    private void populateQaAndSuccessCard(JSONObject qa, JSONObject success) {
        // Mapped to cardOverallSuggestion
        StringBuilder content = new StringBuilder();
        if (qa != null) {
            content.append("质量保证\n");
            content.append("  - 质量指标: ").append(joinArray(qa.optJSONArray("qualityMetrics"))).append("\n");
            content.append("  - 审查流程: ").append(qa.optString("reviewProcess", "N/A")).append("\n");
            content.append("  - 测试策略: ").append(qa.optString("testingStrategy", "N/A")).append("\n\n");
        }
        if (success != null) {
            content.append("成功标准\n");
            content.append("  - 业务目标: ").append(joinArray(success.optJSONArray("businessGoals"))).append("\n");
            content.append("  - 性能指标: ").append(joinArray(success.optJSONArray("performanceMetrics"))).append("\n");
            content.append("  - 技术目标: ").append(joinArray(success.optJSONArray("technicalGoals")));
        }
        if (content.length() == 0) {
            populateCard(cardOverallSuggestion, "质量与成功标准", "无相关数据");
        } else {
            populateCard(cardOverallSuggestion, "质量与成功标准", content.toString().trim());
        }
    }

    private void populateTechnicalArchitectureCard(JSONObject architecture) {
        if (architecture == null) {
            populateCard(cardTechnicalArchitecture, "技术架构", "无相关数据");
            return;
        }
        StringBuilder content = new StringBuilder();
        content.append("核心框架: ").append(joinArray(architecture.optJSONArray("coreFrameworks"))).append("\n\n");

        JSONObject stack = architecture.optJSONObject("developmentStack");
        if (stack != null) {
            content.append("开发栈:\n");
            content.append("  - 前端: ").append(stack.optString("frontend", "N/A")).append("\n");
            content.append("  - 后端: ").append(stack.optString("backend", "N/A")).append("\n");
            content.append("  - 数据库: ").append(stack.optString("database", "N/A")).append("\n");
            content.append("  - AI模型: ").append(stack.optString("aiModels", "N/A")).append("\n\n");
        }

        content.append("基础设施要求: ").append(joinArray(architecture.optJSONArray("infrastructureRequirements")));
        populateCard(cardTechnicalArchitecture, "技术架构", content.toString().trim());
    }

    private String joinArray(org.json.JSONArray jsonArray) {
        if (jsonArray == null) return "N/A";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < jsonArray.length(); i++) {
            sb.append(jsonArray.optString(i));
            if (i < jsonArray.length() - 1) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }
    
    /**
      * 从URL加载图片
      */
     private void loadImageFromUrl(String imageUrl) {
         if (getContext() != null && ivGeneratedChart != null) {
             Glide.with(this)
                     .load(imageUrl)
                     .diskCacheStrategy(DiskCacheStrategy.ALL)
                     .placeholder(android.R.drawable.ic_menu_gallery)
                     .error(android.R.drawable.ic_menu_close_clear_cancel)
                     .into(ivGeneratedChart);
             
             Toast.makeText(getContext(), "正在加载评估图表...", Toast.LENGTH_SHORT).show();
         }
     }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}