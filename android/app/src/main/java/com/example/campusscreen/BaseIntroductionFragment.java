package com.example.campusscreen;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import androidx.fragment.app.Fragment;

public class BaseIntroductionFragment extends Fragment {

    private LinearLayout btnCulture, btnManagement, btnIntroduction, btnCultivation, btnFundManagement;
    private MainActivity mainActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_base_introduction, container, false);
        
        mainActivity = (MainActivity) getActivity();
        
        // 初始化按钮
        btnCulture = view.findViewById(R.id.btn_base_culture);
        btnManagement = view.findViewById(R.id.btn_base_management);
        btnIntroduction = view.findViewById(R.id.btn_base_brief);
        btnCultivation = view.findViewById(R.id.btn_base_cultivation);
        btnFundManagement = view.findViewById(R.id.btn_base_fund_management);
        
        // 设置按钮点击事件
        setupButtonClickListeners();
        
        return view;
    }
    
    private void setupButtonClickListeners() {
        // 基地文化按钮
        btnCulture.setOnClickListener(v -> {
            addClickAnimation(v);
            if (mainActivity != null) {
                mainActivity.showCultureFragment();
            }
        });
        
        // 基地管理按钮
        btnManagement.setOnClickListener(v -> {
            addClickAnimation(v);
            if (mainActivity != null) {
                mainActivity.showManagementFragment();
            }
        });
        
        // 基地简介按钮
        btnIntroduction.setOnClickListener(v -> {
            addClickAnimation(v);
            if (mainActivity != null) {
                mainActivity.showIntroductionFragment();
            }
        });
        
        // 人才培养按钮
        btnCultivation.setOnClickListener(v -> {
            addClickAnimation(v);
            if (mainActivity != null) {
                mainActivity.showCultivationFragment();
            }
        });
        
        // 基地基金委员会管理办法按钮
        btnFundManagement.setOnClickListener(v -> {
            addClickAnimation(v);
            if (mainActivity != null) {
                mainActivity.showFundManagementFragment();
            }
        });
    }
    
    private void addClickAnimation(View view) {
        view.animate()
            .scaleX(0.95f)
            .scaleY(0.95f)
            .setDuration(100)
            .withEndAction(() -> {
                view.animate()
                    .scaleX(1.0f)
                    .scaleY(1.0f)
                    .setDuration(100)
                    .start();
            })
            .start();
    }
}