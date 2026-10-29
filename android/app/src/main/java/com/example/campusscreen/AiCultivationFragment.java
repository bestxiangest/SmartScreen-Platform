package com.example.campusscreen;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class AiCultivationFragment extends Fragment {
    
    private EditText etName, etAge, etEducation, etMajor, etExperience, etSkillLevel, etInterest, etGoal;
    private Button btnGeneratePlan, btnSavePlan, btnSharePlan;
    private LinearLayout layoutPlanDisplay;
    private TextView tvPlanContent;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ai_cultivation, container, false);
        
        initViews(view);
        setupInteractions();
        
        return view;
    }
    
    private void initViews(View view) {
        // 初始化输入框
        etName = view.findViewById(R.id.et_name);
        etAge = view.findViewById(R.id.et_age);
        etEducation = view.findViewById(R.id.et_education);
        etMajor = view.findViewById(R.id.et_major);
        etExperience = view.findViewById(R.id.et_experience);
        etSkillLevel = view.findViewById(R.id.et_skill_level);
        etInterest = view.findViewById(R.id.et_interest);
        etGoal = view.findViewById(R.id.et_goal);
        
        // 初始化按钮
        btnGeneratePlan = view.findViewById(R.id.btn_generate_plan);
        btnSavePlan = view.findViewById(R.id.btn_save_plan);
        btnSharePlan = view.findViewById(R.id.btn_share_plan);
        
        // 初始化方案显示区域
        layoutPlanDisplay = view.findViewById(R.id.layout_plan_display);
        tvPlanContent = view.findViewById(R.id.tv_plan_content);
    }
    
    private void setupInteractions() {
        // 生成方案按钮点击事件
        if (btnGeneratePlan != null) {
            btnGeneratePlan.setOnClickListener(v -> generatePlan());
        }
        
        // 保存方案按钮点击事件
        if (btnSavePlan != null) {
            btnSavePlan.setOnClickListener(v -> savePlan());
        }
        
        // 分享方案按钮点击事件
        if (btnSharePlan != null) {
            btnSharePlan.setOnClickListener(v -> sharePlan());
        }
    }
    
    private void generatePlan() {
        // 收集用户输入信息
        String name = etName != null ? etName.getText().toString().trim() : "";
        String age = etAge != null ? etAge.getText().toString().trim() : "";
        String education = etEducation != null ? etEducation.getText().toString().trim() : "";
        String major = etMajor != null ? etMajor.getText().toString().trim() : "";
        String experience = etExperience != null ? etExperience.getText().toString().trim() : "";
        String skillLevel = etSkillLevel != null ? etSkillLevel.getText().toString().trim() : "";
        String interest = etInterest != null ? etInterest.getText().toString().trim() : "";
        String goal = etGoal != null ? etGoal.getText().toString().trim() : "";
        
        // 验证必填字段
        if (name.isEmpty() || education.isEmpty() || major.isEmpty()) {
            showToast("请填写姓名、学历和专业等基本信息");
            return;
        }
        
        // 生成AI培养方案
        String plan = generateAiCultivationPlan(name, age, education, major, experience, skillLevel, interest, goal);
        
        // 显示方案
        if (tvPlanContent != null) {
            tvPlanContent.setText(plan);
            tvPlanContent.setGravity(android.view.Gravity.START);
        }
        
        if (layoutPlanDisplay != null) {
            layoutPlanDisplay.setVisibility(View.VISIBLE);
        }
        
        showToast("AI培养方案生成成功！");
    }
    
    private void savePlan() {
        showToast("方案已保存到本地");
    }
    
    private void sharePlan() {
        if (tvPlanContent != null) {
            String planText = tvPlanContent.getText().toString();
            if (!planText.isEmpty() && !planText.contains("点击上方按钮")) {
                shareAiPlan(planText);
            } else {
                showToast("请先生成培养方案");
            }
        }
    }
    
    private String generateAiCultivationPlan(String name, String age, String education, String major, 
                                            String experience, String skillLevel, String interest, String goal) {
        StringBuilder plan = new StringBuilder();
        
        plan.append("🎯 ").append(name).append("的AI人才培养方案\n\n");
        
        plan.append("📋 基本信息分析\n");
        plan.append("• 姓名：").append(name).append("\n");
        if (!age.isEmpty()) plan.append("• 年龄：").append(age).append("岁\n");
        plan.append("• 学历：").append(education).append("\n");
        plan.append("• 专业：").append(major).append("\n");
        if (!experience.isEmpty()) plan.append("• 工作经验：").append(experience).append("年\n");
        if (!skillLevel.isEmpty()) plan.append("• 技能水平：").append(skillLevel).append("\n");
        plan.append("\n");
        
        plan.append("🎯 培养目标设定\n");
        if (!goal.isEmpty()) {
            plan.append("• 个人目标：").append(goal).append("\n");
        }
        plan.append("• 核心能力：机器学习算法理解与应用\n");
        plan.append("• 技术栈：Python、TensorFlow/PyTorch、数据分析\n");
        plan.append("• 项目经验：完成2-3个AI实战项目\n\n");
        
        plan.append("📚 学习路径规划\n");
        plan.append("阶段一：基础理论学习（1-2个月）\n");
        plan.append("• 数学基础：线性代数、概率统计、微积分\n");
        plan.append("• 编程基础：Python语言深入学习\n");
        plan.append("• AI概论：人工智能发展史与基本概念\n\n");
        
        plan.append("阶段二：核心技术掌握（2-3个月）\n");
        plan.append("• 机器学习：监督学习、无监督学习、强化学习\n");
        plan.append("• 深度学习：神经网络、CNN、RNN、Transformer\n");
        plan.append("• 数据处理：数据清洗、特征工程、可视化\n\n");
        
        plan.append("阶段三：实战项目训练（2-3个月）\n");
        if (!interest.isEmpty()) {
            plan.append("• 专业方向项目：基于").append(interest).append("的应用开发\n");
        }
        plan.append("• 计算机视觉：图像识别与处理项目\n");
        plan.append("• 自然语言处理：文本分析与生成项目\n");
        plan.append("• 推荐系统：个性化推荐算法实现\n\n");
        
        plan.append("🛠️ 推荐学习资源\n");
        plan.append("• 在线课程：吴恩达机器学习课程、CS231n\n");
        plan.append("• 实践平台：Kaggle竞赛、GitHub开源项目\n");
        plan.append("• 技术社区：CSDN、知乎AI专栏、Stack Overflow\n");
        plan.append("• 论文阅读：arXiv最新AI论文跟踪\n\n");
        
        plan.append("📈 能力评估与进阶\n");
        plan.append("• 月度评估：理论知识测试 + 项目代码审查\n");
        plan.append("• 技能认证：获得相关AI技术认证证书\n");
        plan.append("• 行业实习：寻找AI相关实习或工作机会\n");
        plan.append("• 持续学习：关注AI前沿技术发展趋势\n\n");
        
        plan.append("💡 个性化建议\n");
        if (major.contains("计算机") || major.contains("软件")) {
            plan.append("• 基于您的计算机专业背景，建议重点关注算法优化和系统架构\n");
        } else if (major.contains("数学") || major.contains("统计")) {
            plan.append("• 基于您的数学背景，建议深入学习机器学习理论和算法原理\n");
        } else {
            plan.append("• 建议先补强数学和编程基础，再进入AI核心技术学习\n");
        }
        
        if (!experience.isEmpty() && !experience.equals("0")) {
            plan.append("• 利用您的工作经验，寻找AI技术在现有领域的应用场景\n");
        }
        
        plan.append("• 建议加入AI学习社群，与同行交流学习心得\n");
        plan.append("• 定期参加AI技术会议和workshop，拓展视野\n\n");
        
        plan.append("🎉 预期成果\n");
        plan.append("完成本培养方案后，您将具备：\n");
        plan.append("✅ 扎实的AI理论基础和实践能力\n");
        plan.append("✅ 独立完成AI项目的技术实力\n");
        plan.append("✅ 在AI领域的职业发展竞争力\n");
        plan.append("✅ 持续学习和技术创新的能力\n");
        
        return plan.toString();
    }
    
    private void shareAiPlan(String planText) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, planText);
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "我的AI人才培养方案");
        startActivity(Intent.createChooser(shareIntent, "分享培养方案"));
    }
    
    private void showToast(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }
}