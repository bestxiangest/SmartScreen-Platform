package com.example.campusscreen;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ProjectDetailActivity extends AppCompatActivity {
    
    // 项目详细信息数据结构
    public static class ProjectData {
        public String title;
        public String description;
        public String technicalFeatures;
        public String applicationValue;
        public String awardLevel;
        public String category;
        public String leader;
        public String teamSize;
        public String completionDate;
        public String supervisor;
        
        public ProjectData(String title, String description, String technicalFeatures, 
                          String applicationValue, String awardLevel, String category,
                          String leader, String teamSize, String completionDate, String supervisor) {
            this.title = title;
            this.description = description;
            this.technicalFeatures = technicalFeatures;
            this.applicationValue = applicationValue;
            this.awardLevel = awardLevel;
            this.category = category;
            this.leader = leader;
            this.teamSize = teamSize;
            this.completionDate = completionDate;
            this.supervisor = supervisor;
        }
    }
    
    // 预定义的项目数据
    private ProjectData[] PROJECT_DATA;
    
    private void initProjectData() {
        PROJECT_DATA = new ProjectData[]{
            new ProjectData(
                getString(R.string.project_1_title),
                getString(R.string.project_1_description),
                getString(R.string.project_1_technical_features),
                getString(R.string.project_1_application_value),
                getString(R.string.project_1_award_level),
                getString(R.string.project_1_category),
                getString(R.string.project_1_leader),
                getString(R.string.project_1_team_size),
                getString(R.string.project_1_completion_date),
                getString(R.string.project_1_supervisor)
            ),
            new ProjectData(
                getString(R.string.project_2_title),
                getString(R.string.project_2_description),
                getString(R.string.project_2_technical_features),
                getString(R.string.project_2_application_value),
                getString(R.string.project_2_award_level),
                getString(R.string.project_2_category),
                getString(R.string.project_2_leader),
                getString(R.string.project_2_team_size),
                getString(R.string.project_2_completion_date),
                getString(R.string.project_2_supervisor)
            ),
            new ProjectData(
                getString(R.string.project_3_title),
                getString(R.string.project_3_description),
                getString(R.string.project_3_technical_features),
                getString(R.string.project_3_application_value),
                getString(R.string.project_3_award_level),
                getString(R.string.project_3_category),
                getString(R.string.project_3_leader),
                getString(R.string.project_3_team_size),
                getString(R.string.project_3_completion_date),
                getString(R.string.project_3_supervisor)
            ),
            new ProjectData(
                getString(R.string.project_4_title),
                getString(R.string.project_4_description),
                getString(R.string.project_4_technical_features),
                getString(R.string.project_4_application_value),
                getString(R.string.project_4_award_level),
                getString(R.string.project_4_category),
                getString(R.string.project_4_leader),
                getString(R.string.project_4_team_size),
                getString(R.string.project_4_completion_date),
                getString(R.string.project_4_supervisor)
            ),
            new ProjectData(
                getString(R.string.project_5_title),
                getString(R.string.project_5_description),
                getString(R.string.project_5_technical_features),
                getString(R.string.project_5_application_value),
                getString(R.string.project_5_award_level),
                getString(R.string.project_5_category),
                getString(R.string.project_5_leader),
                getString(R.string.project_5_team_size),
                getString(R.string.project_5_completion_date),
                getString(R.string.project_5_supervisor)
            ),
            new ProjectData(
                getString(R.string.project_6_title),
                getString(R.string.project_6_description),
                getString(R.string.project_6_technical_features),
                getString(R.string.project_6_application_value),
                getString(R.string.project_6_award_level),
                getString(R.string.project_6_category),
                getString(R.string.project_6_leader),
                getString(R.string.project_6_team_size),
                getString(R.string.project_6_completion_date),
                getString(R.string.project_6_supervisor)
            )
        };
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_detail);
        
        // 获取传递的项目索引
        int projectIndex = getIntent().getIntExtra("project_index", 0);
        
        // 初始化项目数据
        initProjectData();
        
        // 初始化视图
        initViews();
        
        // 加载项目数据
        loadProjectData(projectIndex);
        
        // 设置点击事件
        setupClickListeners();
    }
    
    private void initViews() {
        // 初始化所有视图组件
    }
    
    private void loadProjectData(int projectIndex) {
        if (projectIndex >= 0 && projectIndex < PROJECT_DATA.length) {
            ProjectData project = PROJECT_DATA[projectIndex];
            
            // 设置项目标题
            TextView title = findViewById(R.id.tv_project_title);
            if (title != null) title.setText(project.title);
            
            // 设置获奖信息
            TextView awardLevel = findViewById(R.id.tv_award_level);
            TextView category = findViewById(R.id.tv_project_category);
            if (awardLevel != null) awardLevel.setText(project.awardLevel);
            if (category != null) category.setText(project.category);
            
            // 设置项目信息
            TextView leader = findViewById(R.id.tv_project_leader);
            TextView teamSize = findViewById(R.id.tv_team_size);
            TextView completionDate = findViewById(R.id.tv_completion_date);
            TextView supervisor = findViewById(R.id.tv_supervisor);
            
            if (leader != null) leader.setText(project.leader);
            if (teamSize != null) teamSize.setText(project.teamSize);
            if (completionDate != null) completionDate.setText(project.completionDate);
            if (supervisor != null) supervisor.setText(project.supervisor);
            
            // 设置项目内容
            TextView description = findViewById(R.id.tv_project_description);
            TextView technicalFeatures = findViewById(R.id.tv_technical_features);
            TextView applicationValue = findViewById(R.id.tv_application_value);
            
            if (description != null) description.setText(project.description);
            if (technicalFeatures != null) technicalFeatures.setText(project.technicalFeatures);
            if (applicationValue != null) applicationValue.setText(project.applicationValue);
            
            // 项目图片已简化为纯色背景，无需设置图片
        }
    }
    

    
    private void setupClickListeners() {
        // 返回按钮
        ImageButton backButton = findViewById(R.id.btn_back);
        if (backButton != null) {
            backButton.setOnClickListener(v -> finish());
        }
        
        // 分享按钮
        ImageButton shareButton = findViewById(R.id.btn_share);
        if (shareButton != null) {
            shareButton.setOnClickListener(v -> shareProject());
        }
    }
    
    private void shareProject() {
        // 获取当前项目信息
        TextView titleView = findViewById(R.id.tv_project_title);
        TextView awardView = findViewById(R.id.tv_award_level);
        
        String title = titleView != null ? titleView.getText().toString() : getString(R.string.default_project_title);
        String award = awardView != null ? awardView.getText().toString() : "";
        
        String shareText = getString(R.string.share_project_prefix) + title;
        if (!award.isEmpty()) {
            shareText += getString(R.string.share_award_prefix) + award;
        }
        shareText += getString(R.string.share_suffix);
        
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share_subject));
        
        startActivity(Intent.createChooser(shareIntent, getString(R.string.share_chooser_title)));
    }
}