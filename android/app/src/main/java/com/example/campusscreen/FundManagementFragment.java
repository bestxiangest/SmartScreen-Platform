package com.example.campusscreen;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;

public class FundManagementFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fund_management, container, false);
        
        // 新的布局采用静态展示方式，不需要按钮交互
        // 所有管理办法内容直接在布局中展示
        
        return view;
    }
}