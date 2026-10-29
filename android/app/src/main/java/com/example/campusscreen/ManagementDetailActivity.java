package com.example.campusscreen;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ManagementDetailActivity extends AppCompatActivity {
    
    public static final String EXTRA_DOCUMENT_TITLE = "document_title";
    public static final String FUND_MANAGEMENT = "基金使用暂行管理办法";
    public static final String MENTOR_MANAGEMENT = "校外导师暂行管理办法";
    
    private ImageButton btnBack;
    private TextView tvTitle;
    private TextView tvContent;
    private TextView tvPoints;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_management_detail);
        
        initViews();
        setupContent();
        setupBackButton();
    }
    
    private void initViews() {
        btnBack = findViewById(R.id.btn_back);
        tvTitle = findViewById(R.id.tv_title);
        tvContent = findViewById(R.id.tv_content);
        tvPoints = findViewById(R.id.tv_points);
    }
    
    private void setupContent() {
        String documentTitle = getIntent().getStringExtra(EXTRA_DOCUMENT_TITLE);
        
        if (FUND_MANAGEMENT.equals(documentTitle)) {
            tvTitle.setText(getString(R.string.management_fund_usage_title));
            tvContent.setText(getString(R.string.management_fund_usage_content));
            tvPoints.setText(getString(R.string.management_fund_usage_points));
        } else if (MENTOR_MANAGEMENT.equals(documentTitle)) {
            tvTitle.setText(getString(R.string.management_external_mentor_title));
            tvContent.setText(getString(R.string.management_external_mentor_content));
            tvPoints.setText(getString(R.string.management_external_mentor_points));
        } else {
            // 默认显示基金管理办法
            tvTitle.setText(getString(R.string.management_fund_usage_title));
            tvContent.setText(getString(R.string.management_fund_usage_content));
            tvPoints.setText(getString(R.string.management_fund_usage_points));
        }
    }
    
    private void setupBackButton() {
        btnBack.setOnClickListener(v -> finish());
    }
}