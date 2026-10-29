package com.example.campusscreen;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ScanCheckinActivity extends AppCompatActivity {
    
    private Button btnStartScan;
    private ImageButton btnBack;
    private TextView tvCheckinStatus;
    private TextView tvCheckinTime;
    private TextView tvCheckinLocation;
    
    private boolean isCheckedIn = false;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_checkin);
        
        initViews();
        setupClickListeners();
        updateCheckinStatus();
    }
    
    private void initViews() {
        btnStartScan = findViewById(R.id.btn_start_scan);
        btnBack = findViewById(R.id.btn_back);
        tvCheckinStatus = findViewById(R.id.tv_checkin_status);
        tvCheckinTime = findViewById(R.id.tv_checkin_time);
        tvCheckinLocation = findViewById(R.id.tv_checkin_location);
    }
    
    private void setupClickListeners() {
        // 返回按钮点击事件
        btnBack.setOnClickListener(v -> finish());
        
        // 开始扫码按钮点击事件
        btnStartScan.setOnClickListener(v -> startScanProcess());
    }
    
    private void startScanProcess() {
        if (isCheckedIn) {
            Toast.makeText(this, "今日已完成签到", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // 显示扫码模拟对话框
        showScanDialog();
    }
    
    private void showScanDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("扫码签到")
               .setMessage("正在启动摄像头...\n\n请将设备摄像头对准二维码进行扫描")
               .setPositiveButton("模拟扫码成功", (dialog, which) -> {
                   // 模拟扫码成功
                   performCheckin();
               })
               .setNegativeButton("取消", (dialog, which) -> {
                   dialog.dismiss();
               })
               .setNeutralButton("扫码失败", (dialog, which) -> {
                   Toast.makeText(ScanCheckinActivity.this, "扫码失败，请重试", Toast.LENGTH_SHORT).show();
               })
               .setCancelable(true)
               .show();
    }
    
    private void performCheckin() {
        // 模拟签到成功
        isCheckedIn = true;
        
        // 获取当前时间
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        String currentTime = sdf.format(new Date());
        
        // 更新UI
        tvCheckinStatus.setText("已签到");
        tvCheckinStatus.setTextColor(getResources().getColor(R.color.success));
        tvCheckinTime.setText(currentTime);
        tvCheckinTime.setTextColor(getResources().getColor(R.color.text_primary));
        
        // 更新按钮状态
        btnStartScan.setText("已完成签到");
        btnStartScan.setEnabled(false);
        btnStartScan.setAlpha(0.6f);
        
        // 显示成功提示
        showSuccessDialog(currentTime);
    }
    
    private void showSuccessDialog(String time) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("签到成功")
               .setMessage("恭喜您！签到成功\n\n签到时间：" + time + "\n签到地点：通信电子创新基地\n\n请遵守基地相关规定，祝您学习愉快！")
               .setPositiveButton("确定", (dialog, which) -> {
                   dialog.dismiss();
               })
               .setCancelable(false)
               .show();
    }
    
    private void updateCheckinStatus() {
        if (isCheckedIn) {
            tvCheckinStatus.setText("已签到");
            tvCheckinStatus.setTextColor(getResources().getColor(R.color.success));
            btnStartScan.setText("已完成签到");
            btnStartScan.setEnabled(false);
            btnStartScan.setAlpha(0.6f);
        } else {
            tvCheckinStatus.setText("未签到");
            tvCheckinStatus.setTextColor(getResources().getColor(R.color.warning));
            btnStartScan.setText("开始扫码");
            btnStartScan.setEnabled(true);
            btnStartScan.setAlpha(1.0f);
        }
    }
    
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}