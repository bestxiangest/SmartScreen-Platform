package com.example.campusscreen;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ProjectCarouselAdapter extends RecyclerView.Adapter<ProjectCarouselAdapter.ProjectViewHolder> {
    
    private List<ProjectItem> projects;
    private static final int INFINITE_COUNT = 10000; // 用于实现无限循环的大数值
    
    public ProjectCarouselAdapter(List<ProjectItem> projects) {
        this.projects = projects;
    }
    
    @NonNull
    @Override
    public ProjectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_project_carousel, parent, false);
        return new ProjectViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ProjectViewHolder holder, int position) {
        // 使用模运算获取实际的项目索引，实现无限循环
        int actualPosition = position % projects.size();
        ProjectItem project = projects.get(actualPosition);
        holder.bind(project);
    }
    
    @Override
    public int getItemCount() {
        return projects.isEmpty() ? 0 : INFINITE_COUNT;
    }
    
    // 获取实际项目数量
    public int getRealItemCount() {
        return projects.size();
    }
    
    // 获取起始位置（用于初始化ViewPager2到中间位置）
    public int getStartPosition() {
        return projects.isEmpty() ? 0 : INFINITE_COUNT / 2;
    }
    
    static class ProjectViewHolder extends RecyclerView.ViewHolder {
        private ImageView projectImage;
        private TextView projectTitle;
        private TextView projectDescription;
        private TextView projectAchievement;
        
        public ProjectViewHolder(@NonNull View itemView) {
            super(itemView);
            projectImage = itemView.findViewById(R.id.project_image);
            projectTitle = itemView.findViewById(R.id.project_title);
            projectDescription = itemView.findViewById(R.id.project_description);
            projectAchievement = itemView.findViewById(R.id.project_achievement);
        }
        
        public void bind(ProjectItem project) {
            if (project.getImageResource() != 0) {
                // 如果有图片资源，显示图片并设置缩放类型
                projectImage.setImageResource(project.getImageResource());
                projectImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
                projectImage.setBackgroundColor(0); // 清除背景色
            } else {
                // 如果没有图片资源，使用背景颜色
                projectImage.setImageResource(0);
                projectImage.setBackgroundColor(project.getBackgroundColor());
            }
            projectTitle.setText(project.getTitle());
            projectDescription.setText(project.getDescription());
            projectAchievement.setText(project.getAchievement());
        }
    }
    
    public static class ProjectItem {
        private String title;
        private String description;
        private String achievement;
        private int backgroundColor;
        private int imageResource;
        
        public ProjectItem(String title, String description, String achievement, int backgroundColor) {
            this.title = title;
            this.description = description;
            this.achievement = achievement;
            this.backgroundColor = backgroundColor;
            this.imageResource = 0; // 默认无图片
        }
        
        public ProjectItem(String title, String description, String achievement, int backgroundColor, int imageResource) {
            this.title = title;
            this.description = description;
            this.achievement = achievement;
            this.backgroundColor = backgroundColor;
            this.imageResource = imageResource;
        }
        
        public String getTitle() { return title; }
        public String getDescription() { return description; }
        public String getAchievement() { return achievement; }
        public int getBackgroundColor() { return backgroundColor; }
        public int getImageResource() { return imageResource; }
    }
}