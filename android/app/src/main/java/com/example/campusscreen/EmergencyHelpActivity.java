package com.example.campusscreen;

import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class EmergencyHelpActivity extends AppCompatActivity {
    
    private ImageButton btnBack;
    private LinearLayout btnEmergencyCenter;
    private ProgressBar progressCircle;
    private TextView tvEmergencyText;
    private TextView tvCountdown;
    
    private CountDownTimer countDownTimer;
    private ObjectAnimator progressAnimator;
    private boolean isLongPressing = false;
    private boolean hasTriggered = false;
    
    // 紧急联系人电话号码
    private static final String EMERGENCY_PHONE = "13888888888";
    private static final String SAFETY_PHONE = "13999999999";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_help);
        
        initViews();
        setupClickListeners();
    }
    
    private void initViews() {
        btnBack = findViewById(R.id.btn_back);
        btnEmergencyCenter = findViewById(R.id.btn_emergency_center);
        progressCircle = findViewById(R.id.progress_circle);
        tvEmergencyText = findViewById(R.id.tv_emergency_text);
        tvCountdown = findViewById(R.id.tv_countdown);
    }
    
    private void setupClickListeners() {
        // 返回按钮
        btnBack.setOnClickListener(v -> finish());
        
        // 设置快速拨号点击事件
        setupQuickDialButtons();
        
        // 紧急求助按钮长按事件
        btnEmergencyCenter.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (!hasTriggered) {
                            startLongPress();
                        }
                        return true;
                        
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        if (!hasTriggered) {
                            stopLongPress();
                        }
                        return true;
                }
                return false;
            }
        });
    }
    
    private void startLongPress() {
        isLongPressing = true;
        
        // 显示进度条
        progressCircle.setVisibility(View.VISIBLE);
        progressCircle.setProgress(0);
        
        // 更新按钮文字
        tvEmergencyText.setText("正在求助...");
        tvCountdown.setVisibility(View.VISIBLE);
        
        // 开始进度动画
        progressAnimator = ObjectAnimator.ofInt(progressCircle, "progress", 0, 100);
        progressAnimator.setDuration(5000); // 5秒
        progressAnimator.start();
        
        // 倒计时
        countDownTimer = new CountDownTimer(5000, 100) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (isLongPressing) {
                    int secondsLeft = (int) (millisUntilFinished / 1000) + 1;
                    tvCountdown.setText(secondsLeft + "秒");
                } else {
                    cancel();
                }
            }
            
            @Override
            public void onFinish() {
                if (isLongPressing) {
                    triggerEmergencyHelp();
                }
            }
        };
        countDownTimer.start();
    }
    
    private void stopLongPress() {
        isLongPressing = false;
        
        // 停止动画和计时器
        if (progressAnimator != null) {
            progressAnimator.cancel();
        }
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        
        // 重置UI
        resetUI();
    }
    
    private void resetUI() {
        progressCircle.setVisibility(View.INVISIBLE);
        progressCircle.setProgress(0);
        tvEmergencyText.setText("长按求助");
        tvCountdown.setVisibility(View.GONE);
        tvCountdown.setText("");
    }
    
    private void triggerEmergencyHelp() {
        hasTriggered = true;
        isLongPressing = false;
        
        // 更新UI显示成功状态
        tvEmergencyText.setText("求助成功!");
        tvCountdown.setText("已发送");
        tvCountdown.setVisibility(View.VISIBLE);
        
        // 发送求助信息
        sendEmergencyMessage();
        
        // 显示成功对话框
        showSuccessDialog();
        
        // 3秒后重置状态
        new Handler().postDelayed(() -> {
            hasTriggered = false;
            resetUI();
        }, 3000);
    }
    
    private void sendEmergencyMessage() {
        try {
            // 构建求助短信内容
            String message = "【紧急求助】\n" +
                    "发送人：校园基地用户\n" +
                    "时间：" + getCurrentTime() + "\n" +
                    "地点：校园创新基地\n" +
                    "情况：用户触发紧急求助功能，请立即联系确认情况！";
            
            // 发送短信给管理员
            sendSMS(EMERGENCY_PHONE, message);
            
            // 也可以发送给安全负责人
            sendSMS(SAFETY_PHONE, message);
            
            Toast.makeText(this, "求助信息已发送给管理员", Toast.LENGTH_LONG).show();
            
        } catch (Exception e) {
            Toast.makeText(this, "发送失败，请直接拨打紧急电话", Toast.LENGTH_LONG).show();
        }
    }
    
    private void sendSMS(String phoneNumber, String message) {
        try {
            Intent smsIntent = new Intent(Intent.ACTION_SENDTO);
            smsIntent.setData(Uri.parse("smsto:" + phoneNumber));
            smsIntent.putExtra("sms_body", message);
            
            if (smsIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(smsIntent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private String getCurrentTime() {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault());
        return sdf.format(new java.util.Date());
    }
    
    private void showSuccessDialog() {
        new AlertDialog.Builder(this)
                .setTitle("求助成功")
                .setMessage("紧急求助信息已发送给管理员！\n\n如果情况紧急，请直接拨打：\n" +
                        "基地管理员：" + EMERGENCY_PHONE + "\n" +
                        "安全负责人：" + SAFETY_PHONE + "\n\n" +
                        "火警：119 | 急救：120")
                .setPositiveButton("知道了", null)
                .setNegativeButton("拨打电话", (dialog, which) -> {
                    showCallDialog();
                })
                .show();
    }
    
    private void showCallDialog() {
        String[] options = {"基地管理员 (" + EMERGENCY_PHONE + ")", "安全负责人 (" + SAFETY_PHONE + ")", "火警 (119)", "急救 (120)"};
        String[] phones = {EMERGENCY_PHONE, SAFETY_PHONE, "119", "120"};
        
        new AlertDialog.Builder(this)
                .setTitle("选择拨打电话")
                .setItems(options, (dialog, which) -> {
                    makePhoneCall(phones[which]);
                })
                .setNegativeButton("取消", null)
                .show();
    }
    
    private void setupQuickDialButtons() {
        // 基地管理员卡片点击事件
        findViewById(R.id.card_manager).setOnClickListener(v -> {
            makePhoneCall(EMERGENCY_PHONE);
        });
        
        // 安全负责人卡片点击事件
        findViewById(R.id.card_safety).setOnClickListener(v -> {
            makePhoneCall(SAFETY_PHONE);
        });
        
        // 火警119卡片点击事件
        findViewById(R.id.card_fire).setOnClickListener(v -> {
            makePhoneCall("119");
        });
        
        // 急救120卡片点击事件
        findViewById(R.id.card_medical).setOnClickListener(v -> {
            makePhoneCall("120");
        });
    }
    
    private void makePhoneCall(String phoneNumber) {
        try {
            Intent callIntent = new Intent(Intent.ACTION_DIAL);
            callIntent.setData(Uri.parse("tel:" + phoneNumber));
            startActivity(callIntent);
        } catch (Exception e) {
            Toast.makeText(this, "无法拨打电话", Toast.LENGTH_SHORT).show();
        }
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 清理资源
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        if (progressAnimator != null) {
            progressAnimator.cancel();
        }
    }
}