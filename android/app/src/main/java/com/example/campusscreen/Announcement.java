package com.example.campusscreen;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Announcement {
    private int id;
    private String title;
    private String content;
    private String authorName;
    private String type;
    private boolean isImportant;
    private String createdAt;
    
    // 构造函数
    public Announcement() {}
    
    public Announcement(int id, String title, String content, String authorName, String type, boolean isImportant, String createdAt) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.authorName = authorName;
        this.type = type;
        this.isImportant = isImportant;
        this.createdAt = createdAt;
    }
    
    // Getter和Setter方法
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public String getAuthorName() {
        return authorName;
    }
    
    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public boolean isImportant() {
        return isImportant;
    }
    
    public void setImportant(boolean important) {
        isImportant = important;
    }
    
    public String getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
    
    /**
     * 获取格式化的创建时间
     * @return 格式化后的时间字符串
     */
    public String getFormattedCreatedAt() {
        try {
            // 解析ISO 8601格式的时间（不带Z后缀）
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm", Locale.getDefault());
            
            Date date = inputFormat.parse(createdAt);
            return outputFormat.format(date);
        } catch (ParseException e) {
            // 如果解析失败，尝试其他格式
            try {
                SimpleDateFormat inputFormat2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
                SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm", Locale.getDefault());
                Date date = inputFormat2.parse(createdAt);
                return outputFormat.format(date);
            } catch (ParseException e2) {
                // 如果都解析失败，返回原始时间字符串
                return createdAt;
            }
        }
    }
    
    /**
     * 判断是否为今日消息
     * @return 如果是今日消息返回true，否则返回false
     */
    public boolean isTodayMessage() {
        try {
            // 解析ISO 8601格式的时间（不带Z后缀）
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            Date messageDate = inputFormat.parse(createdAt);
            
            // 获取当前系统时间
            Date currentDate = new Date();
            
            // 比较日期部分（忽略时间）
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String messageDateStr = dateFormat.format(messageDate);
            String currentDateStr = dateFormat.format(currentDate);
            
            // 比较消息日期与当前日期
            return messageDateStr.equals(currentDateStr);
        } catch (ParseException e) {
            // 如果解析失败，尝试其他格式
            try {
                SimpleDateFormat inputFormat2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
                Date messageDate = inputFormat2.parse(createdAt);
                
                // 获取当前系统时间
                Date currentDate = new Date();
                
                // 比较日期部分（忽略时间）
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                String messageDateStr = dateFormat.format(messageDate);
                String currentDateStr = dateFormat.format(currentDate);
                
                // 比较消息日期与当前日期
                return messageDateStr.equals(currentDateStr);
            } catch (ParseException e2) {
                // 如果都解析失败，返回false
                return false;
            }
        }
    }
    
    /**
     * 获取类型标签的背景资源
     * @return 背景资源ID
     */
    public String getTypeTagBackground() {
        switch (type) {
            case "通知":
                return "@drawable/bg_tag_orange";
            case "新闻":
                return "@drawable/bg_tag_blue";
            case "动态":
                return "@drawable/bg_tag_green";
            case "安全提示":
                return "@drawable/bg_tag_red";
            case "天气提示":
                return "@drawable/bg_tag_blue";
            case "名言金句":
                return "@drawable/bg_tag_purple";
            default:
                return "@drawable/bg_tag_gray";
        }
    }
    
    /**
     * 获取消息边框背景资源
     * @return 背景资源ID
     */
    public String getMessageBorderBackground() {
        if (isImportant) {
            return "@drawable/bg_message_border_orange";
        } else {
            return "@drawable/bg_message_border_blue";
        }
    }
    
    /**
     * 获取内容摘要（用于列表显示）
     * @param maxLength 最大长度
     * @return 摘要内容
     */
    public String getContentSummary(int maxLength) {
        if (content == null || content.length() <= maxLength) {
            return content;
        }
        return content.substring(0, maxLength) + "...";
    }
    
    @Override
    public String toString() {
        return "Announcement{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", authorName='" + authorName + '\'' +
                ", type='" + type + '\'' +
                ", isImportant=" + isImportant +
                ", createdAt='" + createdAt + '\'' +
                '}';
    }
}