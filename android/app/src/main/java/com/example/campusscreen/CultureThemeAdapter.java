package com.example.campusscreen;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CultureThemeAdapter extends RecyclerView.Adapter<CultureThemeAdapter.ViewHolder> {

    private List<CultureFragment.CultureTheme> themes;

    public CultureThemeAdapter(List<CultureFragment.CultureTheme> themes) {
        this.themes = themes;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutId;
        switch (viewType) {
            case 0:
                layoutId = R.layout.culture_item_1;
                break;
            case 1:
                layoutId = R.layout.culture_item_2;
                break;
            case 2:
                layoutId = R.layout.culture_item_3;
                break;
            case 3:
                layoutId = R.layout.culture_item_4;
                break;
            case 4:
                layoutId = R.layout.culture_item_5;
                break;
            default:
                layoutId = R.layout.culture_item_1;
                break;
        }
        View view = LayoutInflater.from(parent.getContext())
                .inflate(layoutId, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CultureFragment.CultureTheme theme = themes.get(position);
        holder.bind(theme);
    }

    @Override
    public int getItemCount() {
        return themes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private CardView cardView;
        private LinearLayout headerLayout;
        private ImageView ivThemeIcon;
        private TextView tvThemeTitle;
        private ImageView ivExpandIcon;
        private LinearLayout expandableContent;
        private TextView tvThemeContent;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            
            cardView = (CardView) itemView;
            headerLayout = itemView.findViewById(R.id.header_layout);
            ivThemeIcon = itemView.findViewById(R.id.iv_theme_icon);
            tvThemeTitle = itemView.findViewById(R.id.tv_theme_title);
            ivExpandIcon = itemView.findViewById(R.id.iv_expand_icon);
            expandableContent = itemView.findViewById(R.id.expandable_content);
            tvThemeContent = itemView.findViewById(R.id.tv_theme_content);
        }

        public void bind(CultureFragment.CultureTheme theme) {
            // 设置标题和内容
            tvThemeTitle.setText(theme.getTitle());
            tvThemeContent.setText(theme.getContent());
            
            // 设置主题色
            int themeColor = ContextCompat.getColor(itemView.getContext(), theme.getThemeColor());
            tvThemeTitle.setTextColor(themeColor);
            
            // 设置图标
            ivThemeIcon.setImageResource(theme.getIconRes());
            ivThemeIcon.setColorFilter(themeColor);
            
            // 设置展开状态
            updateExpandState(theme.isExpanded(), false);
            
            // 设置点击事件
            headerLayout.setOnClickListener(v -> {
                boolean newExpandedState = !theme.isExpanded();
                theme.setExpanded(newExpandedState);
                updateExpandState(newExpandedState, true);
            });
            
            // 设置卡片悬停效果
            setupCardHoverEffect();
        }

        private void updateExpandState(boolean isExpanded, boolean animate) {
            if (animate) {
                // 展开/收起动画
                if (isExpanded) {
                    expandContent();
                } else {
                    collapseContent();
                }
                
                // 旋转箭头动画
                float targetRotation = isExpanded ? 180f : 0f;
                ObjectAnimator rotationAnimator = ObjectAnimator.ofFloat(ivExpandIcon, "rotation", targetRotation);
                rotationAnimator.setDuration(300);
                rotationAnimator.start();
            } else {
                // 直接设置状态
                expandableContent.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
                ivExpandIcon.setRotation(isExpanded ? 180f : 0f);
            }
        }

        private void expandContent() {
            expandableContent.setVisibility(View.VISIBLE);
            
            // 测量内容高度
            expandableContent.measure(
                View.MeasureSpec.makeMeasureSpec(((View) expandableContent.getParent()).getWidth(), View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            );
            
            int targetHeight = expandableContent.getMeasuredHeight();
            
            // 创建高度动画
            ValueAnimator animator = ValueAnimator.ofInt(0, targetHeight);
            animator.setDuration(300);
            animator.addUpdateListener(animation -> {
                int height = (int) animation.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = expandableContent.getLayoutParams();
                layoutParams.height = height;
                expandableContent.setLayoutParams(layoutParams);
            });
            animator.start();
        }

        private void collapseContent() {
            int initialHeight = expandableContent.getHeight();
            
            ValueAnimator animator = ValueAnimator.ofInt(initialHeight, 0);
            animator.setDuration(300);
            animator.addUpdateListener(animation -> {
                int height = (int) animation.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = expandableContent.getLayoutParams();
                layoutParams.height = height;
                expandableContent.setLayoutParams(layoutParams);
            });
            
            animator.addListener(new android.animation.AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(android.animation.Animator animation) {
                    expandableContent.setVisibility(View.GONE);
                    ViewGroup.LayoutParams layoutParams = expandableContent.getLayoutParams();
                    layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                    expandableContent.setLayoutParams(layoutParams);
                }
            });
            
            animator.start();
        }

        private void setupCardHoverEffect() {
            // 设置卡片悬停效果
            cardView.setOnTouchListener((v, event) -> {
                switch (event.getAction()) {
                    case android.view.MotionEvent.ACTION_DOWN:
                        // 按下时增加elevation
                        ObjectAnimator elevationUp = ObjectAnimator.ofFloat(cardView, "cardElevation", 8f, 12f);
                        elevationUp.setDuration(150);
                        elevationUp.start();
                        break;
                    case android.view.MotionEvent.ACTION_UP:
                    case android.view.MotionEvent.ACTION_CANCEL:
                        // 抬起时恢复elevation
                        ObjectAnimator elevationDown = ObjectAnimator.ofFloat(cardView, "cardElevation", 12f, 8f);
                        elevationDown.setDuration(150);
                        elevationDown.start();
                        break;
                }
                return false;
            });
        }
    }
}