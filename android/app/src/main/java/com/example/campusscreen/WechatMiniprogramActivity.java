package com.example.campusscreen;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class WechatMiniprogramActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wechat_miniprogram);
        
        // 设置返回按钮
        ImageButton btnBack = findViewById(R.id.btn_back);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }
        
        // 设置标题
        TextView tvTitle = findViewById(R.id.tv_title);
        if (tvTitle != null) {
            tvTitle.setText("智慧电子班牌——微信小程序");
        }
        
        // 设置三个图片：xcx_code1.jpg, xcx_search.png, xcx_code2.jpg
        ImageView ivCode1 = findViewById(R.id.iv_xcx_code1);
        ImageView ivXcxSearch = findViewById(R.id.iv_xcx_search);
        ImageView ivCode2 = findViewById(R.id.iv_xcx_code2);
        
        if (ivCode1 != null) {
            ivCode1.setImageResource(R.drawable.xcx_code1);
        }
        
        if (ivXcxSearch != null) {
            ivXcxSearch.setImageResource(R.drawable.xcx_search);
        }
        
        if (ivCode2 != null) {
            ivCode2.setImageResource(R.drawable.xcx_code2);
        }

        
        // 设置功能和作用文字（这些文字已经在布局文件中硬编码了）
        TextView tvFunctions = findViewById(R.id.tv_functions);
        TextView tvBenefits = findViewById(R.id.tv_benefits);
        
        // 功能和作用的文字内容已经在布局文件中设置，这里可以根据需要动态修改
        if (tvFunctions != null) {
            // 功能文字已在布局中设置，如需动态修改可在此处添加代码
        }
        
        if (tvBenefits != null) {
            // 作用文字已在布局中设置，如需动态修改可在此处添加代码
        }
    }
}