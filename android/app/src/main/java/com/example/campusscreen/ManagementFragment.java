package com.example.campusscreen;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

public class ManagementFragment extends Fragment {

    private CardView cardTeamManagement;
    private CardView cardProjectManagement;
    private CardView cardMemberManagement;
    private CardView cardResourceManagement;
    private CardView cardDailyManagement;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_management, container, false);
        
        initViews(view);
        setupClickListeners();
        
        return view;
    }

    private void initViews(View view) {
        cardTeamManagement = view.findViewById(R.id.card_team_management);
        cardProjectManagement = view.findViewById(R.id.card_project_management);
        cardMemberManagement = view.findViewById(R.id.card_member_management);
        cardResourceManagement = view.findViewById(R.id.card_resource_management);
        cardDailyManagement = view.findViewById(R.id.card_daily_management);
    }

    private void setupClickListeners() {
        // 基地管理团队与职能分配
        cardTeamManagement.setOnClickListener(v -> {
            showToast("基地管理团队与职能分配");
            // TODO: 实现具体的跳转逻辑
        });

        // 基地项目管理办法
        cardProjectManagement.setOnClickListener(v -> {
            showToast("基地项目管理办法");
            // TODO: 实现具体的跳转逻辑
        });

        // 基地成员管理办法
        cardMemberManagement.setOnClickListener(v -> {
            showToast("基地成员管理办法");
            // TODO: 实现具体的跳转逻辑
        });

        // 基地资料与材料设备管理办法
        cardResourceManagement.setOnClickListener(v -> {
            showToast("基地资料与材料设备管理办法");
            // TODO: 实现具体的跳转逻辑
        });

        // 基地日常事务管理办法
        cardDailyManagement.setOnClickListener(v -> {
            showToast("基地日常事务管理办法");
            // TODO: 实现具体的跳转逻辑
        });
    }

    private void showToast(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 创建新的ManagementFragment实例
     */
    public static ManagementFragment newInstance() {
        return new ManagementFragment();
    }
}