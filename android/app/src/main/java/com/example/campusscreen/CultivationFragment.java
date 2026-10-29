package com.example.campusscreen;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

public class CultivationFragment extends Fragment {
    
    private ImageView imgCultivationGoal;
    private ImageView imgCultivationDirection1;
    private ImageView imgCultivationDirection2;
    private CardView cardCultivationGoal;
    private CardView cardCultivationDirection1;
    private CardView cardCultivationDirection2;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cultivation, container, false);
        
        initViews(view);
        loadImages();
        setupInteractions();
        
        return view;
    }
    
    private void initViews(View view) {
        imgCultivationGoal = view.findViewById(R.id.img_cultivation_goal);
        imgCultivationDirection1 = view.findViewById(R.id.img_cultivation_direction_1);
        imgCultivationDirection2 = view.findViewById(R.id.img_cultivation_direction_2);
        
        cardCultivationGoal = view.findViewById(R.id.card_cultivation_goal);
        cardCultivationDirection1 = view.findViewById(R.id.card_cultivation_direction_1);
        cardCultivationDirection2 = view.findViewById(R.id.card_cultivation_direction_2);
    }
    
    private void loadImages() {
        // 配置Glide请求选项
        RequestOptions requestOptions = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerInside();
        
        // 使用Glide异步加载图片
        if (getContext() != null) {
            // 加载培养目标图片
            Glide.with(this)
                    .load(R.drawable.cultivation_1)
                    .apply(requestOptions)
                    .into(imgCultivationGoal);
            
            // 加载培养方向图片1
            Glide.with(this)
                    .load(R.drawable.cultivation_2)
                    .apply(requestOptions)
                    .into(imgCultivationDirection1);
            
            // 加载培养方向图片2
            Glide.with(this)
                    .load(R.drawable.cultivation_3)
                    .apply(requestOptions)
                    .into(imgCultivationDirection2);
        }
    }
    
    private void setupInteractions() {
        // 设置图片悬停缩放效果
        setupImageHoverEffect(imgCultivationGoal);
        setupImageHoverEffect(imgCultivationDirection1);
        setupImageHoverEffect(imgCultivationDirection2);
        
        // 设置卡片点击涟漪反馈
        setupCardClickEffect(cardCultivationGoal);
        setupCardClickEffect(cardCultivationDirection1);
        setupCardClickEffect(cardCultivationDirection2);
    }
    
    private void setupImageHoverEffect(ImageView imageView) {
        if (imageView != null) {
            imageView.setOnTouchListener((v, event) -> {
                switch (event.getAction()) {
                    case android.view.MotionEvent.ACTION_DOWN:
                        // 按下时缩放到1.03
                        ObjectAnimator scaleUpX = ObjectAnimator.ofFloat(v, "scaleX", 1.0f, 1.03f);
                        ObjectAnimator scaleUpY = ObjectAnimator.ofFloat(v, "scaleY", 1.0f, 1.03f);
                        scaleUpX.setDuration(150);
                        scaleUpY.setDuration(150);
                        scaleUpX.start();
                        scaleUpY.start();
                        break;
                    case android.view.MotionEvent.ACTION_UP:
                    case android.view.MotionEvent.ACTION_CANCEL:
                        // 释放时恢复原始大小
                        ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(v, "scaleX", 1.03f, 1.0f);
                        ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(v, "scaleY", 1.03f, 1.0f);
                        scaleDownX.setDuration(150);
                        scaleDownY.setDuration(150);
                        scaleDownX.start();
                        scaleDownY.start();
                        break;
                }
                return false; // 返回false以允许其他事件处理
            });
        }
    }
    
    private void setupCardClickEffect(CardView cardView) {
        if (cardView != null) {
            cardView.setOnClickListener(v -> {
                // 添加点击反馈动画
                ObjectAnimator scaleX = ObjectAnimator.ofFloat(v, "scaleX", 1.0f, 0.95f, 1.0f);
                ObjectAnimator scaleY = ObjectAnimator.ofFloat(v, "scaleY", 1.0f, 0.95f, 1.0f);
                scaleX.setDuration(200);
                scaleY.setDuration(200);
                scaleX.start();
                scaleY.start();
                
                // 这里可以添加具体的点击处理逻辑
                // 例如：显示详细信息、跳转到其他页面等
            });
        }
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // 清理Glide请求，避免内存泄漏
        if (getContext() != null) {
            Glide.with(this).clear(imgCultivationGoal);
            Glide.with(this).clear(imgCultivationDirection1);
            Glide.with(this).clear(imgCultivationDirection2);
        }
    }
}