package com.example.campusscreen;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class CultureFragment extends Fragment {

    private RecyclerView rvCultureThemes;
    private CultureThemeAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_culture, container, false);
        
        initViews(view);
        setupRecyclerView();
        
        return view;
    }

    private void initViews(View view) {
        rvCultureThemes = view.findViewById(R.id.rv_culture_themes);
    }

    private void setupRecyclerView() {
        // 使用StaggeredGridLayoutManager实现单列布局
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        rvCultureThemes.setLayoutManager(layoutManager);
        
        // 创建文化主题数据
        List<CultureTheme> themes = createCultureThemes();
        
        // 设置适配器
        adapter = new CultureThemeAdapter(themes);
        rvCultureThemes.setAdapter(adapter);
        
        // 设置item间距
        rvCultureThemes.addItemDecoration(new GridSpacingItemDecoration(1, 16, true));
    }



    private List<CultureTheme> createCultureThemes() {
        List<CultureTheme> themes = new ArrayList<>();
        
        themes.add(new CultureTheme(
            getString(R.string.culture_theme_1_title),
            getString(R.string.culture_theme_1_content),
            R.color.culture_theme_red,
            R.drawable.ic_culture_theme
        ));
        
        themes.add(new CultureTheme(
            getString(R.string.culture_theme_2_title),
            getString(R.string.culture_theme_2_content),
            R.color.culture_theme_blue,
            R.drawable.ic_culture_theme
        ));
        
        themes.add(new CultureTheme(
            getString(R.string.culture_theme_3_title),
            getString(R.string.culture_theme_3_content),
            R.color.culture_theme_teal,
            R.drawable.ic_culture_theme
        ));
        
        themes.add(new CultureTheme(
            getString(R.string.culture_theme_4_title),
            getString(R.string.culture_theme_4_content),
            R.color.culture_theme_amber,
            R.drawable.ic_culture_theme
        ));
        
        // 链条文化默认展开
        CultureTheme chainCulture = new CultureTheme(
            getString(R.string.culture_theme_5_title),
            getString(R.string.culture_theme_5_content),
            R.color.culture_theme_indigo,
            R.drawable.ic_culture_theme
        );
        chainCulture.setExpanded(true);
        themes.add(chainCulture);
        
        return themes;
    }



    // 文化主题数据类
    public static class CultureTheme {
        private String title;
        private String content;
        private int themeColor;
        private int iconRes;
        private boolean isExpanded;

        public CultureTheme(String title, String content, int themeColor, int iconRes) {
            this.title = title;
            this.content = content;
            this.themeColor = themeColor;
            this.iconRes = iconRes;
            this.isExpanded = false;
        }

        // Getters and Setters
        public String getTitle() { return title; }
        public String getContent() { return content; }
        public int getThemeColor() { return themeColor; }
        public int getIconRes() { return iconRes; }
        public boolean isExpanded() { return isExpanded; }
        public void setExpanded(boolean expanded) { isExpanded = expanded; }
    }
}