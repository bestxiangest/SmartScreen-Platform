package com.example.campusscreen;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    
    private LinearLayout navHome, navSchedule, navEquipment, navProjects, navSafety, navAiCultivation, navIntroduction, navAiEvaluation, navAiAssistant;
    private TextView timeDisplay, dateDisplay, weekdayDisplay;
    private FrameLayout contentContainer;
    private Handler timeHandler;
    private Runnable timeRunnable;
    
    // 轮播图自动播放相关
    private Handler carouselHandler;
    private Runnable carouselRunnable;
    private ViewPager2 currentCarousel;
    
    // 当前选中的导航项
    private LinearLayout currentSelectedNav;
    
    // API服务
    private AnnouncementApiService announcementApiService;
    
    // 消息数据
    private List<Announcement> currentAnnouncements = new ArrayList<>();
    
    // 分页相关
    private int currentPage = 1;
    private int messagesPerPage = 5;
    private int totalPages = 1;
    
    // 网络请求相关
    private OkHttpClient httpClient;
    private ExecutorService executorService;
    
    // 天气API配置
    private static final String WEATHER_API_KEY = "06ab3ec992282fb5e6d3c5bb9604af7f";
    private static final String WEATHER_CITY_CODE = "360112"; // 南昌市新建区
    private static final String WEATHER_API_URL = "https://restapi.amap.com/v3/weather/weatherInfo?city=" + WEATHER_CITY_CODE + "&key=" + WEATHER_API_KEY;
    
    // 名言金句相关
    private Handler quoteHandler;
    private Runnable quoteRunnable;
    private TextView quoteTextView;
    private static final String QUOTE_API_URL = "https://api.nmxc.ltd/yiyan";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // 初始化网络请求组件
        httpClient = new OkHttpClient();
        executorService = Executors.newFixedThreadPool(2);
        
        // 初始化API服务
        announcementApiService = new AnnouncementApiService();
        
        initViews();
        setupNavigation();
        
        // 启动时间更新
        startTimeUpdate();
        
        // 默认显示基地动态
        selectNavItem(navHome);
        showHomeFragment();
    }
    
    private void initViews() {
        // 初始化导航项
        navHome = findViewById(R.id.nav_home);
        navSchedule = findViewById(R.id.nav_schedule);
        navEquipment = findViewById(R.id.nav_equipment);
        navProjects = findViewById(R.id.nav_projects);
        navSafety = findViewById(R.id.nav_safety);
        navAiCultivation = findViewById(R.id.nav_ai_cultivation);
        navIntroduction = findViewById(R.id.nav_introduction);
        navAiEvaluation = findViewById(R.id.nav_ai_evaluation);
        navAiAssistant = findViewById(R.id.nav_ai_assistant);
        
        // 初始化时间显示
        timeDisplay = findViewById(R.id.tv_time);
        dateDisplay = findViewById(R.id.tv_date);
        weekdayDisplay = findViewById(R.id.tv_weekday);
        
        // 初始化内容容器
        contentContainer = findViewById(R.id.content_container);
        
        // 设置标题字体
        TextView labTitle = findViewById(R.id.tv_lab_title);
        if (labTitle != null) {
            try {
                Typeface customFont = Typeface.createFromAsset(getAssets(), "maobizi.ttf");
                labTitle.setTypeface(customFont);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    private void setupNavigation() {
        navHome.setOnClickListener(v -> {
            selectNavItem(navHome);
            showHomeFragment();
        });
        
        navSchedule.setOnClickListener(v -> {
            selectNavItem(navSchedule);
            showScheduleFragment();
        });
        
        navEquipment.setOnClickListener(v -> {
            selectNavItem(navEquipment);
            showEquipmentFragment();
        });
        
        navProjects.setOnClickListener(v -> {
            selectNavItem(navProjects);
            showProjectsFragment();
        });
        
        navSafety.setOnClickListener(v -> {
            selectNavItem(navSafety);
            showSafetyFragment();
        });
        
        navAiCultivation.setOnClickListener(v -> {
            selectNavItem(navAiCultivation);
            showAiCultivationModernFragment();
        });
        
        navIntroduction.setOnClickListener(v -> {
            selectNavItem(navIntroduction);
            showBaseIntroductionFragment();
        });
        
        navAiEvaluation.setOnClickListener(v -> {
            selectNavItem(navAiEvaluation);
            showAiEvaluationModernFragment();
        });
        
        navAiAssistant.setOnClickListener(v -> {
            selectNavItem(navAiAssistant);
            showAiAssistantFragment();
        });
        
        // 默认选中首页
        selectNavItem(navHome);
    }
    
    private void selectNavItem(LinearLayout selectedNav) {
        // 重置所有导航项的选中状态和文字颜色
        if (currentSelectedNav != null) {
            currentSelectedNav.setSelected(false);
            TextView currentTextView = (TextView) currentSelectedNav.getChildAt(1);
            if (currentTextView != null) {
                currentTextView.setTextColor(ContextCompat.getColor(this, R.color.nav_text_color));
            }
        }
        
        // 设置新选中的导航项和文字颜色
        selectedNav.setSelected(true);
        TextView selectedTextView = (TextView) selectedNav.getChildAt(1);
        if (selectedTextView != null) {
            selectedTextView.setTextColor(ContextCompat.getColor(this, android.R.color.white));
        }
        currentSelectedNav = selectedNav;
    }
    
    private void showHomeFragment() {
        // 停止之前的轮播图自动播放
        stopCarouselAutoPlay();
        
        contentContainer.removeAllViews();
        getLayoutInflater().inflate(R.layout.fragment_home, contentContainer, true);
        
        // 初始化轮播图
        setupProjectCarousel();
        
        // 设置重要通知点击事件
        LinearLayout importantNoticesContainer = contentContainer.findViewById(R.id.important_notices_container);
        if (importantNoticesContainer != null) {
            importantNoticesContainer.setOnClickListener(v -> {
                // 添加点击效果
                v.setAlpha(0.7f);
                v.postDelayed(() -> v.setAlpha(1.0f), 150);
                
                // 跳转到消息通知界面
                selectNavItem(navSchedule);
                showScheduleFragment();
            });
        }
        
        // 设置基地动态界面重要通知中每条消息的点击事件
        setupHomeNoticeClickListeners();
        
        // 显示首页重要通知加载状态
        showHomeNoticesLoadingState();
        
        // 更新首页重要通知数据（独立更新，不依赖消息通知界面）
        updateHomeImportantNoticesIndependently();
        
        // 更新天气数据
        updateWeatherData();
        
        // 初始化名言金句
        setupQuoteDisplay();
        
        // 设置快速入口按钮点击事件
        setupQuickAccessButtons();
    }
    
    private void setupProjectCarousel() {
        ViewPager2 projectsCarousel = contentContainer.findViewById(R.id.projects_carousel);
        TextView carouselIndicator = contentContainer.findViewById(R.id.carousel_indicator);
        LinearLayout dotsIndicator = contentContainer.findViewById(R.id.dots_indicator);
        ImageView btnPrev = contentContainer.findViewById(R.id.btn_prev);
        ImageView btnNext = contentContainer.findViewById(R.id.btn_next);
        
        if (projectsCarousel == null) return;
        
        // 创建项目数据
        List<ProjectCarouselAdapter.ProjectItem> projects = createProjectData();
        
        // 设置适配器
        ProjectCarouselAdapter adapter = new ProjectCarouselAdapter(projects);
        projectsCarousel.setAdapter(adapter);
        
        // 设置初始位置到中间，实现无限循环
        int startPosition = adapter.getStartPosition();
        projectsCarousel.setCurrentItem(startPosition, false);
        
        // 创建指示器点
        createDotIndicators(dotsIndicator, adapter.getRealItemCount());
        
        // 设置页面变化监听器
        projectsCarousel.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                // 计算实际位置用于指示器显示
                int actualPosition = position % adapter.getRealItemCount();
                updateCarouselIndicator(carouselIndicator, actualPosition + 1, adapter.getRealItemCount());
                updateDotIndicators(dotsIndicator, actualPosition);
            }
        });
        
        // 设置按钮点击事件
        if (btnPrev != null) {
            btnPrev.setOnClickListener(v -> {
                int currentItem = projectsCarousel.getCurrentItem();
                projectsCarousel.setCurrentItem(currentItem - 1, true);
                // 重置自动播放计时器
                startCarouselAutoPlay();
            });
        }
        
        if (btnNext != null) {
            btnNext.setOnClickListener(v -> {
                int currentItem = projectsCarousel.getCurrentItem();
                projectsCarousel.setCurrentItem(currentItem + 1, true);
                // 重置自动播放计时器
                startCarouselAutoPlay();
            });
        }
        
        // 初始化指示器
        int actualPosition = startPosition % adapter.getRealItemCount();
        updateCarouselIndicator(carouselIndicator, actualPosition + 1, adapter.getRealItemCount());
        updateDotIndicators(dotsIndicator, actualPosition);
        
        // 保存当前轮播图引用并启动自动轮播
        currentCarousel = projectsCarousel;
        startCarouselAutoPlay();
    }
    
    private List<ProjectCarouselAdapter.ProjectItem> createProjectData() {
        List<ProjectCarouselAdapter.ProjectItem> projects = new ArrayList<>();
        
        projects.add(new ProjectCarouselAdapter.ProjectItem(
            "基于物联网技术的智慧蔬菜大棚监测系统",
            "该系统通过部署各类环境传感器，结合LoRa无线通信技术，实现对大棚环境的实时精准监测与数据采集以及枯叶监测。支持远程可视化监控、智能预警及自动化调控，有效提升蔬菜种植的精准化与智能化程度。",
            "第16届中国大学生计算机设计大赛江西省一等奖",
            ContextCompat.getColor(this, R.color.blue_500),
            R.drawable.project_presentation_1
        ));
        
        projects.add(new ProjectCarouselAdapter.ProjectItem(
            "点滴卫士——智慧输液报警系统",
            "该系统基于物联网和智能传感技术，实时监测输液进度，在药液即将滴完或出现异常（如输液管堵塞、滴速异常）时，自动触发声光报警并推送提醒至医护人员手持终端，有效避免输液事故的发生。",
            "第18届iCAN大学生创新创业大赛江西赛区二等奖",
            ContextCompat.getColor(this, R.color.green_500),
            R.drawable.project_presentation_2
        ));
        
        projects.add(new ProjectCarouselAdapter.ProjectItem(
            "术影无踪——OpenCV双目视觉智能手术调光系统",
            "该系统基于OpenCV计算机视觉框架与双目立体成像技术，通过实时分析手术场景的深度信息和组织反射特性，动态调节无影灯照明参数（色温、亮度及照射角度）。创新性地采用深度学习算法识别手术器械与组织边界，实现毫米级精度的自适应补光，有效解决传统手术照明中阴影遮挡和反光过曝问题。",
            "第17届中国大学生计算机设计大赛江西省二等奖",
            ContextCompat.getColor(this, R.color.purple_500),
            R.drawable.project_presentation_3
        ));
        
        projects.add(new ProjectCarouselAdapter.ProjectItem(
            "光明之路——一种”开源节流“型路灯",
            "本系统创新性地融合无线传感网络(WSN)、风光互补发电与NB-IoT通信技术，构建新一代绿色智慧路灯解决方案。通过太阳能-风能混合供电系统实现能源\"开源\"，配合基于运动检测的PWM调光算法达成\"节流\"，使整体能耗降低60%以上。",
            "第18届iCAN大学生创新创业大赛江西赛区二等奖",
            ContextCompat.getColor(this, R.color.orange_500),
            R.drawable.project_presentation_4
        ));
        
        projects.add(new ProjectCarouselAdapter.ProjectItem(
            "声纹哨兵——城市噪声污染AI监测与治理系统",
            "本系统创新性地将TensorFlow深度学习框架与物联网感知技术相结合，构建了\"监测-识别-取证-治理\"全链条噪声管控体系。通过部署智能声纹采集终端，实现城市噪声的7×24小时精准溯源与自动化执法，噪声识别准确率达93.72%。",
            "第17届中国大学生计算机设计大赛江西省二等奖",
            ContextCompat.getColor(this, R.color.teal_500),
            R.drawable.project_presentation_5
        ));
        
        return projects;
    }
    
    private void createDotIndicators(LinearLayout dotsIndicator, int count) {
        if (dotsIndicator == null) return;
        
        dotsIndicator.removeAllViews();
        for (int i = 0; i < count; i++) {
            View dot = new View(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(16, 16);
            params.setMargins(8, 0, 8, 0);
            dot.setLayoutParams(params);
            dot.setBackgroundResource(R.drawable.bg_project_card);
            dot.setAlpha(0.3f);
            dotsIndicator.addView(dot);
        }
    }
    
    private void updateCarouselIndicator(TextView indicator, int current, int total) {
        if (indicator != null) {
            indicator.setText(current + "/" + total);
        }
    }
    
    private void updateDotIndicators(LinearLayout dotsIndicator, int selectedPosition) {
        if (dotsIndicator == null) return;
        
        for (int i = 0; i < dotsIndicator.getChildCount(); i++) {
            View dot = dotsIndicator.getChildAt(i);
            dot.setAlpha(i == selectedPosition ? 1.0f : 0.3f);
        }
    }
    
    private void startCarouselAutoPlay() {
        stopCarouselAutoPlay(); // 先停止之前的自动播放
        
        carouselHandler = new Handler();
        carouselRunnable = new Runnable() {
            @Override
            public void run() {
                if (currentCarousel != null) {
                    int currentItem = currentCarousel.getCurrentItem();
                    // 直接向右移动到下一项，实现连续向右循环
                    currentCarousel.setCurrentItem(currentItem + 1, true);
                    carouselHandler.postDelayed(this, 3000); // 3秒后执行下一次
                }
            }
        };
        carouselHandler.postDelayed(carouselRunnable, 3000); // 3秒后开始第一次自动切换
    }
    
    private void stopCarouselAutoPlay() {
        if (carouselHandler != null && carouselRunnable != null) {
            carouselHandler.removeCallbacks(carouselRunnable);
        }
    }
    
    private void showHomeNoticesLoadingState() {
        Log.d("MainActivity", "显示首页重要通知加载状态");
        
        // 隐藏重要通知列表，显示加载动画
        ScrollView homeNoticesScrollView = contentContainer.findViewById(R.id.home_notices_scroll_view);
        LinearLayout homeNoticesLoadingLayout = contentContainer.findViewById(R.id.home_notices_loading_layout);
        
        if (homeNoticesScrollView != null) {
            homeNoticesScrollView.setVisibility(View.GONE);
        }
        
        if (homeNoticesLoadingLayout != null) {
            homeNoticesLoadingLayout.setVisibility(View.VISIBLE);
        }
        
        Log.d("MainActivity", "首页重要通知加载状态显示完成");
    }
    
    private void showScheduleFragment() {
        // 显示消息通知界面
        contentContainer.removeAllViews();
        getLayoutInflater().inflate(R.layout.fragment_schedule, contentContainer, true);
        
        Log.d("MainActivity", "开始加载消息通知界面");
        
        // 设置消息项点击事件
        setupMessageClickListeners();
        
        // 优化数据加载：先检查缓存，再异步加载最新数据
        if (currentAnnouncements != null && !currentAnnouncements.isEmpty()) {
            Log.d("MainActivity", "使用缓存数据立即更新界面，共 " + currentAnnouncements.size() + " 条公告");
            // 立即使用缓存数据更新界面
            updateScheduleUI(currentAnnouncements);
            // 异步加载最新数据
            loadAnnouncementDataAsync();
        } else {
            Log.d("MainActivity", "无缓存数据，显示加载状态并调用API");
            // 显示加载状态
            showLoadingState();
            // 加载数据
            loadAnnouncementData();
        }
    }
    
    private void showLoadingState() {
        Log.d("MainActivity", "显示加载状态");
        
        // 隐藏消息列表，显示加载动画
        LinearLayout messagesContainer = contentContainer.findViewById(R.id.messages_container);
        LinearLayout loadingLayout = contentContainer.findViewById(R.id.loading_layout);
        
        if (messagesContainer != null) {
            messagesContainer.setVisibility(View.GONE);
        }
        
        if (loadingLayout != null) {
            loadingLayout.setVisibility(View.VISIBLE);
        }
        
        // 更新统计数据为加载状态
        TextView todayMessageCount = contentContainer.findViewById(R.id.today_message_count);
        if (todayMessageCount != null) {
            todayMessageCount.setText("...");
        }
        
        TextView importantMessageCount = contentContainer.findViewById(R.id.important_message_count);
        if (importantMessageCount != null) {
            importantMessageCount.setText("...");
        }
        
        Log.d("MainActivity", "加载状态显示完成");
    }
    
    private void loadAnnouncementDataAsync() {
        Log.d("MainActivity", "=== 异步加载最新公告数据 ===");
        
        if (announcementApiService == null) {
            Log.e("MainActivity", "AnnouncementApiService未初始化！");
            return;
        }
        
        // 调用API获取最新公告列表
        announcementApiService.getAnnouncements(1, 10, null, null, new AnnouncementApiService.AnnouncementCallback() {
            @Override
            public void onSuccess(List<Announcement> announcements) {
                Log.d("MainActivity", "异步API调用成功，获取到 " + (announcements != null ? announcements.size() : 0) + " 条公告");
                
                // 在主线程更新UI
                runOnUiThread(() -> {
                    currentAnnouncements = announcements;
                    updateScheduleUI(announcements);
                    // 同时更新首页重要通知数据，确保数据同步
                    updateHomeImportantNoticesFromData(announcements);
                });
            }
            
            @Override
            public void onError(String error) {
                Log.e("MainActivity", "异步API调用失败: " + error);
                // 异步加载失败时不做处理，保持当前缓存数据显示
            }
        });
    }
    
    private void loadAnnouncementData() {
        Log.d("MainActivity", "=== 开始加载公告数据 ===");
        Log.d("MainActivity", "AnnouncementApiService实例: " + (announcementApiService != null ? "已创建" : "未创建"));
        
        if (announcementApiService == null) {
            Log.e("MainActivity", "AnnouncementApiService未初始化！");
            return;
        }
        
        Log.d("MainActivity", "准备调用API: getAnnouncements(1, 10, null, null)");
        
        // 先测试网络连接
        testNetworkConnection();
        
        // 调用API获取公告列表
        announcementApiService.getAnnouncements(1, 10, null, null, new AnnouncementApiService.AnnouncementCallback() {
            @Override
            public void onSuccess(List<Announcement> announcements) {
                Log.d("MainActivity", "API调用成功，获取到 " + (announcements != null ? announcements.size() : 0) + " 条公告");
                
                // 在主线程更新UI
                runOnUiThread(() -> {
                    currentAnnouncements = announcements;
                    updateScheduleUI(announcements);
                    // 同时更新首页重要通知数据，确保数据同步
                    updateHomeImportantNoticesFromData(announcements);
                });
            }
            
            @Override
            public void onError(String error) {
                Log.e("MainActivity", "API调用失败: " + error);
                
                // 在主线程显示错误信息
                runOnUiThread(() -> {
                    // 使用测试数据作为备用
                    List<Announcement> testData = createTestAnnouncements();
                    Log.d("MainActivity", "使用测试数据，共 " + testData.size() + " 条");
                    currentAnnouncements = testData;
                    updateScheduleUI(testData);
                     // 同时更新首页重要通知数据
                     updateHomeImportantNoticesFromData(testData);
                 });
             }
         });
    }
    
    private List<Announcement> createTestAnnouncements() {
        List<Announcement> testAnnouncements = new ArrayList<>();
        
        Announcement announcement1 = new Announcement();
        announcement1.setId(1);
        announcement1.setTitle("实验室大扫除");
        announcement1.setContent("为了保持实验室的整洁和安全，定于本周五下午进行实验室大扫除活动。");
        announcement1.setType("通知");
        announcement1.setImportant(true);
        announcement1.setCreatedAt("2024-12-20T10:00:00Z");
        testAnnouncements.add(announcement1);
        
        Announcement announcement2 = new Announcement();
        announcement2.setId(2);
        announcement2.setTitle("实验室安全培训通知");
        announcement2.setContent("为了提高实验室安全意识，将于下周一举行安全培训讲座。");
        announcement2.setType("培训");
        announcement2.setImportant(true);
        announcement2.setCreatedAt("2024-12-19T14:30:00Z");
        testAnnouncements.add(announcement2);
        
        Announcement announcement3 = new Announcement();
        announcement3.setId(3);
        announcement3.setTitle("设备维护通知");
        announcement3.setContent("实验室部分设备将进行定期维护，请提前做好实验安排。");
        announcement3.setType("维护");
        announcement3.setImportant(false);
        announcement3.setCreatedAt("2024-12-18T09:15:00Z");
        testAnnouncements.add(announcement3);
        
        return testAnnouncements;
    }
    
    private void testNetworkConnection() {
        Log.d("MainActivity", "=== 测试网络连接 ===");
        
        // 使用线程池执行网络测试
        executorService.execute(() -> {
            try {
                // 测试连接到后端服务器
                String testUrl = "http://sys.sharpcaterpillar.top:5000/health";
                Log.d("MainActivity", "测试连接: " + testUrl);
                
                Request request = new Request.Builder()
                        .url(testUrl)
                        .get()
                        .build();
                
                Response response = httpClient.newCall(request).execute();
                String responseBody = response.body().string();
                
                Log.d("MainActivity", "网络测试响应码: " + response.code());
                Log.d("MainActivity", "网络测试响应: " + responseBody);
                
                if (response.isSuccessful()) {
                    Log.d("MainActivity", "✓ 网络连接正常");
                } else {
                    Log.w("MainActivity", "✗ 服务器响应异常: " + response.code());
                }
                
            } catch (Exception e) {
                Log.e("MainActivity", "✗ 网络连接失败: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }
    
    private void updateScheduleUI(List<Announcement> announcements) {
        Log.d("MainActivity", "开始更新UI，公告数量: " + (announcements != null ? announcements.size() : 0));
        
        // 隐藏加载动画，显示消息列表
        LinearLayout messagesContainer = contentContainer.findViewById(R.id.messages_container);
        LinearLayout loadingLayout = contentContainer.findViewById(R.id.loading_layout);
        
        if (loadingLayout != null) {
            loadingLayout.setVisibility(View.GONE);
        }
        
        if (messagesContainer != null) {
            messagesContainer.setVisibility(View.VISIBLE);
        }
        
        if (announcements == null || announcements.isEmpty()) {
            Log.w("MainActivity", "公告列表为空，无法更新UI");
            return;
        }
        
        // 保存当前公告数据
        currentAnnouncements = announcements;
        
        // 计算总页数
        totalPages = (int) Math.ceil((double) announcements.size() / messagesPerPage);
        if (totalPages == 0) totalPages = 1;
        
        // 更新消息统计
        updateMessageStatistics(announcements);
        
        // 更新当前页的消息
        updateCurrentPageMessages();
        
        // 更新分页控件
        updatePaginationControls();
        
        Log.d("MainActivity", "日程界面更新完成，总页数: " + totalPages + "，当前页: " + currentPage);
    }
    
    private void updateMessageStatistics(List<Announcement> announcements) {
        try {
            int todayCount = 0;
            int importantCount = 0;
            
            // 统计今日消息和重要消息数量
            for (Announcement announcement : announcements) {
                if (announcement.isTodayMessage()) {
                    todayCount++;
                }
                if (announcement.isImportant()) {
                    importantCount++;
                }
            }
            
            // 更新今日消息数量
            TextView todayMessageCount = contentContainer.findViewById(R.id.today_message_count);
            if (todayMessageCount != null) {
                todayMessageCount.setText(String.valueOf(todayCount));
                Log.d("MainActivity", "更新今日消息数量: " + todayCount);
            } else {
                Log.w("MainActivity", "未找到今日消息数量TextView");
            }
            
            // 更新重要消息数量
            TextView importantMessageCount = contentContainer.findViewById(R.id.important_message_count);
            if (importantMessageCount != null) {
                importantMessageCount.setText(String.valueOf(importantCount));
                Log.d("MainActivity", "更新重要消息数量: " + importantCount);
            } else {
                Log.w("MainActivity", "未找到重要消息数量TextView");
            }
            
        } catch (Exception e) {
            Log.e("MainActivity", "更新消息统计失败: " + e.getMessage());
        }
    }
    
    private void updateMessageItemByIndex(Announcement announcement, int index) {
        try {
            Log.d("MainActivity", "尝试更新消息项 " + index + ": " + announcement.getTitle());
            
            // 根据索引找到对应的消息项容器
            String messageItemId = "message_item_" + index;
            int messageItemResId = getResources().getIdentifier(messageItemId, "id", getPackageName());
            
            if (messageItemResId != 0) {
                LinearLayout messageItem = contentContainer.findViewById(messageItemResId);
                if (messageItem != null) {
                    Log.d("MainActivity", "找到消息项容器: " + messageItemId);
                    
                    // 直接更新标题和时间
                    updateTitleAndTime(messageItem, announcement, index);
                    
                    // 更新消息类型标签
                    updateMessageTypeTag(messageItem, announcement, index);
                    
                    // 更新今日消息标签
                    updateTodayTag(messageItem, announcement, index);
                    
                    // 更新重要消息标签
                    updateImportantTag(messageItem, announcement, index);
                    
                    // 更新详细内容
                    updateDetailContent(messageItem, announcement, index);
                    
                } else {
                    Log.w("MainActivity", "未找到消息项容器: " + messageItemId);
                }
            } else {
                Log.w("MainActivity", "无法获取资源ID: " + messageItemId);
            }
        } catch (Exception e) {
            Log.e("MainActivity", "更新消息项失败: " + e.getMessage());
        }
    }
    
    private void updateTextViewsInMessageItem(LinearLayout messageItem, Announcement announcement) {
        Log.d("MainActivity", "开始在消息项中查找TextView");
        
        // 先列出所有TextView来了解布局结构
        listAllTextViews(messageItem, "");
        
        // 递归查找并更新TextView
        updateTextViewsRecursively(messageItem, announcement, 0);
    }
    
    private void listAllTextViews(View view, String indent) {
        if (view instanceof TextView) {
            TextView textView = (TextView) view;
            String text = textView.getText().toString();
            Log.d("MainActivity", indent + "TextView: \"" + text + "\" (字体大小: " + textView.getTextSize() + ")");
        } else if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            Log.d("MainActivity", indent + viewGroup.getClass().getSimpleName() + " (子视图数: " + viewGroup.getChildCount() + ")");
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                listAllTextViews(viewGroup.getChildAt(i), indent + "  ");
            }
        }
    }
    
    private void updateTextViewsRecursively(View view, Announcement announcement, int textViewIndex) {
        if (view instanceof TextView) {
            TextView textView = (TextView) view;
            String currentText = textView.getText().toString();
            
            Log.d("MainActivity", "找到TextView: " + currentText + ", 字体大小: " + textView.getTextSize());
            
            // 更简单的判断逻辑：根据当前文本内容来判断应该更新什么
            if (currentText.contains("实验室") || currentText.contains("通知") || currentText.contains("消息")) {
                // 这可能是标题
                Log.d("MainActivity", "更新标题: " + announcement.getTitle());
                textView.setText(announcement.getTitle());
            }
            else if (currentText.contains("2024") || currentText.matches(".*\\d{4}-\\d{2}-\\d{2}.*")) {
                // 这可能是时间
                Log.d("MainActivity", "更新时间: " + announcement.getFormattedCreatedAt());
                textView.setText(announcement.getFormattedCreatedAt());
            }
            else if (currentText.length() > 20) {
                // 这可能是详细内容
                Log.d("MainActivity", "更新内容: " + announcement.getContent());
                textView.setText(announcement.getContent());
            }
        } else if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                updateTextViewsRecursively(viewGroup.getChildAt(i), announcement, textViewIndex);
            }
        }
    }
    
    private void updateMessageTypeTag(LinearLayout messageItem, Announcement announcement, int index) {
        try {
            String tagTypeId = "tag_type_" + index;
            int tagTypeResId = getResources().getIdentifier(tagTypeId, "id", getPackageName());
            
            if (tagTypeResId != 0) {
                TextView tagType = messageItem.findViewById(tagTypeResId);
                if (tagType != null) {
                    tagType.setText(announcement.getType());
                    tagType.setVisibility(View.VISIBLE);
                    Log.d("MainActivity", "更新消息类型标签 " + index + ": " + announcement.getType());
                }
            }
        } catch (Exception e) {
            Log.e("MainActivity", "更新消息类型标签失败: " + e.getMessage());
        }
    }
    
    private void updateTodayTag(LinearLayout messageItem, Announcement announcement, int index) {
        try {
            String tagTodayId = "tag_today_" + index;
            int tagTodayResId = getResources().getIdentifier(tagTodayId, "id", getPackageName());
            
            Log.d("MainActivity", "检查今日消息标签 " + index + ": 标题=" + announcement.getTitle() + ", 创建时间=" + announcement.getCreatedAt() + ", isTodayMessage=" + announcement.isTodayMessage());
            
            if (tagTodayResId != 0) {
                TextView tagToday = messageItem.findViewById(tagTodayResId);
                if (tagToday != null) {
                    if (announcement.isTodayMessage()) {
                        tagToday.setVisibility(View.VISIBLE);
                        Log.d("MainActivity", "显示今日消息标签 " + index + " (" + announcement.getTitle() + ")");
                    } else {
                        tagToday.setVisibility(View.GONE);
                        Log.d("MainActivity", "隐藏今日消息标签 " + index + " (" + announcement.getTitle() + ")");
                    }
                } else {
                    Log.w("MainActivity", "未找到今日消息标签控件: " + tagTodayId);
                }
            } else {
                Log.w("MainActivity", "无法获取今日消息标签资源ID: " + tagTodayId);
            }
        } catch (Exception e) {
            Log.e("MainActivity", "更新今日消息标签失败: " + e.getMessage());
        }
    }
    
    private void updateImportantTag(LinearLayout messageItem, Announcement announcement, int index) {
        try {
            String tagImportantId = "tag_important_" + index;
            int tagImportantResId = getResources().getIdentifier(tagImportantId, "id", getPackageName());
            
            Log.d("MainActivity", "检查重要消息标签 " + index + ": 标题=" + announcement.getTitle() + ", isImportant=" + announcement.isImportant());
            
            if (tagImportantResId != 0) {
                TextView tagImportant = messageItem.findViewById(tagImportantResId);
                if (tagImportant != null) {
                    if (announcement.isImportant()) {
                        tagImportant.setVisibility(View.VISIBLE);
                        Log.d("MainActivity", "显示重要消息标签 " + index + " (" + announcement.getTitle() + ")");
                    } else {
                        tagImportant.setVisibility(View.GONE);
                        Log.d("MainActivity", "隐藏重要消息标签 " + index + " (" + announcement.getTitle() + ")");
                    }
                } else {
                    Log.w("MainActivity", "未找到重要消息标签控件: " + tagImportantId);
                }
            } else {
                Log.w("MainActivity", "无法获取重要消息标签资源ID: " + tagImportantId);
            }
        } catch (Exception e) {
            Log.e("MainActivity", "更新重要消息标签失败: " + e.getMessage());
        }
    }
    
    private void updateDetailContent(LinearLayout messageItem, Announcement announcement, int index) {
        try {
            String contentId = "content_" + index;
            int contentResId = getResources().getIdentifier(contentId, "id", getPackageName());
            
            if (contentResId != 0) {
                TextView contentView = messageItem.findViewById(contentResId);
                if (contentView != null) {
                    contentView.setText(announcement.getContent());
                    Log.d("MainActivity", "更新详细内容 " + index + ": " + announcement.getContent());
                }
            }
        } catch (Exception e) {
            Log.e("MainActivity", "更新详细内容失败: " + e.getMessage());
        }
    }
    
    private void updateTitleAndTime(LinearLayout messageItem, Announcement announcement, int index) {
        try {
            // 更新标题
            String titleId = "title_" + index;
            int titleResId = getResources().getIdentifier(titleId, "id", getPackageName());
            if (titleResId != 0) {
                TextView titleView = messageItem.findViewById(titleResId);
                if (titleView != null) {
                    titleView.setText(announcement.getTitle());
                    Log.d("MainActivity", "更新标题 " + index + ": " + announcement.getTitle());
                }
            }
            
            // 更新时间
            String timeId = "time_" + index;
            int timeResId = getResources().getIdentifier(timeId, "id", getPackageName());
            if (timeResId != 0) {
                TextView timeView = messageItem.findViewById(timeResId);
                if (timeView != null) {
                    timeView.setText(announcement.getFormattedCreatedAt());
                    Log.d("MainActivity", "更新时间 " + index + ": " + announcement.getFormattedCreatedAt());
                }
            }
        } catch (Exception e) {
            Log.e("MainActivity", "更新标题和时间失败: " + e.getMessage());
        }
    }
    
    private void updateCurrentPageMessages() {
        try {
            // 计算当前页的起始索引
            int startIndex = (currentPage - 1) * messagesPerPage;
            
            // 更新每个消息项
            for (int i = 1; i <= messagesPerPage; i++) {
                int dataIndex = startIndex + i - 1;
                
                if (dataIndex < currentAnnouncements.size()) {
                    // 有数据，显示消息项并更新内容
                    Announcement announcement = currentAnnouncements.get(dataIndex);
                    updateMessageItemByIndex(announcement, i);
                    showMessageItem(i);
                } else {
                    // 没有数据，隐藏消息项
                    hideMessageItem(i);
                }
            }
        } catch (Exception e) {
            Log.e("MainActivity", "更新当前页消息失败: " + e.getMessage());
        }
    }
    
    private void updatePaginationControls() {
        try {
            TextView pageInfo = contentContainer.findViewById(R.id.tv_page_info);
            Button prevButton = contentContainer.findViewById(R.id.btn_prev_page);
            Button nextButton = contentContainer.findViewById(R.id.btn_next_page);
            
            if (pageInfo != null) {
                pageInfo.setText("第 " + currentPage + " 页 / 共 " + totalPages + " 页");
            }
            
            if (prevButton != null) {
                prevButton.setEnabled(currentPage > 1);
                prevButton.setOnClickListener(v -> {
                    if (currentPage > 1) {
                        currentPage--;
                        updateCurrentPageMessages();
                        updatePaginationControls();
                    }
                });
            }
            
            if (nextButton != null) {
                nextButton.setEnabled(currentPage < totalPages);
                nextButton.setOnClickListener(v -> {
                    if (currentPage < totalPages) {
                        currentPage++;
                        updateCurrentPageMessages();
                        updatePaginationControls();
                    }
                });
            }
        } catch (Exception e) {
            Log.e("MainActivity", "更新分页控件失败: " + e.getMessage());
        }
    }
    
    private void showMessageItem(int index) {
        try {
            String messageItemId = "message_item_" + index;
            int messageItemResId = getResources().getIdentifier(messageItemId, "id", getPackageName());
            
            if (messageItemResId != 0) {
                View messageItem = contentContainer.findViewById(messageItemResId);
                if (messageItem != null) {
                    messageItem.setVisibility(View.VISIBLE);
                }
            }
        } catch (Exception e) {
            Log.e("MainActivity", "显示消息项失败: " + e.getMessage());
        }
    }
    
    private void hideMessageItem(int index) {
        try {
            String messageItemId = "message_item_" + index;
            int messageItemResId = getResources().getIdentifier(messageItemId, "id", getPackageName());
            
            if (messageItemResId != 0) {
                View messageItem = contentContainer.findViewById(messageItemResId);
                if (messageItem != null) {
                    messageItem.setVisibility(View.GONE);
                }
            }
        } catch (Exception e) {
            Log.e("MainActivity", "隐藏消息项失败: " + e.getMessage());
        }
    }
    
    private void setupHomeNoticeClickListeners() {
        // 为基地动态界面重要通知中的每条消息设置点击事件
        LinearLayout homeNoticeItem1 = contentContainer.findViewById(R.id.home_notice_item_1);
        LinearLayout homeNoticeItem2 = contentContainer.findViewById(R.id.home_notice_item_2);
        LinearLayout homeNoticeItem3 = contentContainer.findViewById(R.id.home_notice_item_3);
        LinearLayout homeNoticeItem4 = contentContainer.findViewById(R.id.home_notice_item_4);
        LinearLayout homeNoticeItem5 = contentContainer.findViewById(R.id.home_notice_item_5);
        
        // 为每条通知设置点击事件
        setHomeNoticeItemClickListener(homeNoticeItem1);
        setHomeNoticeItemClickListener(homeNoticeItem2);
        setHomeNoticeItemClickListener(homeNoticeItem3);
        setHomeNoticeItemClickListener(homeNoticeItem4);
        setHomeNoticeItemClickListener(homeNoticeItem5);
    }
    
    private void setHomeNoticeItemClickListener(LinearLayout noticeItem) {
        if (noticeItem != null) {
            noticeItem.setOnClickListener(v -> {
                // 添加点击高亮效果
                v.setAlpha(0.7f);
                v.postDelayed(() -> v.setAlpha(1.0f), 150);
                
                // 跳转到消息通知界面
                selectNavItem(navSchedule);
                showScheduleFragment();
            });
        }
    }
    
    private void updateHomeImportantNoticesIndependently() {
        // 首页重要通知完全独立更新，不依赖于消息通知界面的缓存
        Log.d("MainActivity", "=== 首页重要通知独立更新开始 ===");
        
        if (announcementApiService != null) {
            announcementApiService.getAnnouncements(1, 10, null, null, new AnnouncementApiService.AnnouncementCallback() {
                @Override
                public void onSuccess(List<Announcement> announcements) {
                    runOnUiThread(() -> {
                        // 只更新首页重要通知，不影响缓存数据
                        updateHomeImportantNoticesFromData(announcements);
                        Log.d("MainActivity", "首页重要通知独立更新成功");
                    });
                }
                
                @Override
                public void onError(String error) {
                    Log.e("MainActivity", "首页重要通知独立更新失败: " + error);
                    // 如果API调用失败，使用测试数据
                    List<Announcement> testData = createTestAnnouncements();
                    updateHomeImportantNoticesFromData(testData);
                }
            });
        } else {
            // 如果API服务不可用，使用测试数据
            List<Announcement> testData = createTestAnnouncements();
            updateHomeImportantNoticesFromData(testData);
        }
    }
    
    private void updateHomeImportantNotices() {
        // 首页重要通知更新（保留原方法供其他地方调用）
        if (announcementApiService != null) {
            announcementApiService.getAnnouncements(1, 10, null, null, new AnnouncementApiService.AnnouncementCallback() {
                @Override
                public void onSuccess(List<Announcement> announcements) {
                    runOnUiThread(() -> {
                        // 同时更新缓存数据和首页重要通知
                        currentAnnouncements = announcements;
                        updateHomeImportantNoticesFromData(announcements);
                        Log.d("MainActivity", "首页重要通知已更新");
                    });
                }
                
                @Override
                public void onError(String error) {
                    Log.e("MainActivity", "获取首页重要通知失败: " + error);
                    // 如果API调用失败，使用测试数据
                    List<Announcement> testData = createTestAnnouncements();
                    // 只有在没有缓存数据时才更新缓存
                    if (currentAnnouncements == null || currentAnnouncements.isEmpty()) {
                        currentAnnouncements = testData;
                    }
                    updateHomeImportantNoticesFromData(testData);
                }
            });
        } else {
            // 如果API服务不可用，使用测试数据
            List<Announcement> testData = createTestAnnouncements();
            if (currentAnnouncements == null || currentAnnouncements.isEmpty()) {
                currentAnnouncements = testData;
            }
            updateHomeImportantNoticesFromData(testData);
        }
    }
    
    private void updateHomeNoticeItems(List<Announcement> announcements) {
        if (announcements == null || announcements.isEmpty()) {
            return;
        }
        
        // 获取首页通知项的ID数组
        int[] noticeItemIds = {
            R.id.home_notice_item_1,
            R.id.home_notice_item_2,
            R.id.home_notice_item_3,
            R.id.home_notice_item_4,
            R.id.home_notice_item_5
        };
        
        // 更新每个通知项
        for (int i = 0; i < Math.min(announcements.size(), noticeItemIds.length); i++) {
            LinearLayout noticeItem = contentContainer.findViewById(noticeItemIds[i]);
            if (noticeItem != null) {
                Announcement announcement = announcements.get(i);
                updateHomeNoticeItem(noticeItem, announcement);
            }
        }
    }
    
    private void updateHomeNoticeItem(LinearLayout noticeItem, Announcement announcement) {
        if (noticeItem == null || announcement == null) {
            return;
        }
        
        // 获取通知项中的TextView
        TextView titleView = (TextView) noticeItem.getChildAt(0);
        TextView contentView = (TextView) noticeItem.getChildAt(1);
        TextView timeView = (TextView) noticeItem.getChildAt(2);
        
        if (titleView != null) {
            titleView.setText(announcement.getTitle());
        }
        
        if (contentView != null) {
            contentView.setText(announcement.getContent());
        }
        
        if (timeView != null) {
            String formattedTime = "发布时间：" + formatAnnouncementTime(announcement.getCreatedAt());
            timeView.setText(formattedTime);
        }
    }
    
    private String formatAnnouncementTime(String createdAt) {
        if (createdAt == null || createdAt.isEmpty()) {
            return "未知时间";
        }
        
        try {
            // 假设API返回的时间格式为ISO 8601格式，如"2023-12-15T09:30:00Z"
            // 这里简化处理，直接截取前19个字符并替换T为空格
            if (createdAt.length() >= 19) {
                return createdAt.substring(0, 19).replace("T", " ");
            } else {
                return createdAt;
            }
        } catch (Exception e) {
            Log.e("MainActivity", "时间格式化失败: " + e.getMessage());
            return createdAt;
        }
     }
     
     private void updateHomeImportantNoticesFromData(List<Announcement> allAnnouncements) {
         // 隐藏加载动画，显示重要通知列表
         ScrollView homeNoticesScrollView = contentContainer.findViewById(R.id.home_notices_scroll_view);
         LinearLayout homeNoticesLoadingLayout = contentContainer.findViewById(R.id.home_notices_loading_layout);
         
         if (homeNoticesLoadingLayout != null) {
             homeNoticesLoadingLayout.setVisibility(View.GONE);
         }
         
         if (homeNoticesScrollView != null) {
             homeNoticesScrollView.setVisibility(View.VISIBLE);
         }
         
         if (allAnnouncements == null || allAnnouncements.isEmpty()) {
             return;
         }
         
         // 从所有公告中筛选出重要通知
         List<Announcement> importantAnnouncements = new ArrayList<>();
         for (Announcement announcement : allAnnouncements) {
             if (announcement.isImportant()) {
                 importantAnnouncements.add(announcement);
             }
         }
         
         // 如果没有重要通知，则不更新首页
         if (importantAnnouncements.isEmpty()) {
             Log.d("MainActivity", "没有重要通知，保持首页静态数据");
             return;
         }
         
         // 更新首页重要通知项
         updateHomeNoticeItems(importantAnnouncements);
         Log.d("MainActivity", "首页重要通知已更新，共 " + importantAnnouncements.size() + " 条");
     }
     
     private void setupMessageClickListeners() {
        // 为每个消息项设置点击事件，点击后保持在消息通知界面
        LinearLayout messageItem1 = contentContainer.findViewById(R.id.message_item_1);
        LinearLayout messageItem2 = contentContainer.findViewById(R.id.message_item_2);
        LinearLayout messageItem3 = contentContainer.findViewById(R.id.message_item_3);
        LinearLayout messageItem4 = contentContainer.findViewById(R.id.message_item_4);
        LinearLayout messageItem5 = contentContainer.findViewById(R.id.message_item_5);
        
        // 设置展开/收起按钮的点击事件
        setupExpandButtons();
        
        if (messageItem1 != null) {
            messageItem1.setOnClickListener(v -> {
                // 点击消息项，确保导航栏选中消息通知
                selectNavItem(navSchedule);
                // 可以在这里添加消息详情显示逻辑
            });
        }
        
        if (messageItem2 != null) {
            messageItem2.setOnClickListener(v -> {
                selectNavItem(navSchedule);
                // 可以在这里添加消息详情显示逻辑
            });
        }
        
        if (messageItem3 != null) {
            messageItem3.setOnClickListener(v -> {
                selectNavItem(navSchedule);
                // 可以在这里添加消息详情显示逻辑
            });
        }
        
        if (messageItem4 != null) {
            messageItem4.setOnClickListener(v -> {
                selectNavItem(navSchedule);
                // 可以在这里添加消息详情显示逻辑
            });
        }
        
        if (messageItem5 != null) {
            messageItem5.setOnClickListener(v -> {
                selectNavItem(navSchedule);
                // 可以在这里添加消息详情显示逻辑
            });
        }
    }
    
    private void setupExpandButtons() {
        // 为每个展开/收起按钮设置点击事件
        Button btnExpand1 = contentContainer.findViewById(R.id.btn_expand_1);
        Button btnExpand2 = contentContainer.findViewById(R.id.btn_expand_2);
        Button btnExpand3 = contentContainer.findViewById(R.id.btn_expand_3);
        Button btnExpand4 = contentContainer.findViewById(R.id.btn_expand_4);
        Button btnExpand5 = contentContainer.findViewById(R.id.btn_expand_5);
        
        LinearLayout messageDetail1 = contentContainer.findViewById(R.id.message_detail_1);
        LinearLayout messageDetail2 = contentContainer.findViewById(R.id.message_detail_2);
        LinearLayout messageDetail3 = contentContainer.findViewById(R.id.message_detail_3);
        LinearLayout messageDetail4 = contentContainer.findViewById(R.id.message_detail_4);
        LinearLayout messageDetail5 = contentContainer.findViewById(R.id.message_detail_5);
        
        setupExpandButton(btnExpand1, messageDetail1);
        setupExpandButton(btnExpand2, messageDetail2);
        setupExpandButton(btnExpand3, messageDetail3);
        setupExpandButton(btnExpand4, messageDetail4);
        setupExpandButton(btnExpand5, messageDetail5);
    }
    
    private void setupExpandButton(Button expandButton, LinearLayout messageDetail) {
        if (expandButton != null && messageDetail != null) {
            // 初始状态：详细内容隐藏
            messageDetail.setVisibility(View.GONE);
            
            expandButton.setOnClickListener(v -> {
                if (messageDetail.getVisibility() == View.GONE) {
                    // 展开详细内容
                    messageDetail.setVisibility(View.VISIBLE);
                    expandButton.setText("收起");
                } else {
                    // 收起详细内容
                    messageDetail.setVisibility(View.GONE);
                    expandButton.setText("展开详细内容");
                }
            });
        }
    }
    
    private void showEquipmentFragment() {
        contentContainer.removeAllViews();
        getLayoutInflater().inflate(R.layout.fragment_equipment, contentContainer, true);
        
        // 设置预约设备按钮的点击事件
        setupEquipmentReservationButton();
    }
    
    private void setupEquipmentReservationButton() {
        Button btnReserveEquipment = contentContainer.findViewById(R.id.btn_reserve_equipment);
        if (btnReserveEquipment != null) {
            btnReserveEquipment.setOnClickListener(v -> {
                // 跳转到预约设备链接
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(android.net.Uri.parse("https://www.bilibili.com/"));
                try {
                    startActivity(intent);
                } catch (Exception e) {
                    showToast("无法打开链接，请检查网络连接");
                }
            });
        }
    }
    
    private void showProjectsFragment() {
        contentContainer.removeAllViews();
        getLayoutInflater().inflate(R.layout.fragment_projects, contentContainer, true);
        
        // 设置查看详细按钮的点击事件
        setupProjectDetailButtons();
    }
    
    private void setupProjectDetailButtons() {
        // 项目详细信息数据
        String[] projectDetails = {
            "基于物联网技术的智慧蔬菜大棚监测系统\n\n项目简介：\n本项目开发了一套基于深度学习技术的智能图像识别系统，采用卷积神经网络(CNN)架构，能够准确识别和分类各种图像内容。系统具有高精度、快速响应的特点，在多个应用场景中表现优异。\n\n技术特点：\n• 采用ResNet-50深度网络架构\n• 支持实时图像处理和识别\n• 识别准确率达到95%以上\n• 支持多种图像格式输入\n\n应用价值：\n该系统可广泛应用于安防监控、医疗诊断、工业检测等领域，为相关行业提供智能化解决方案。",
            "点滴卫士——智慧输液报警系统\n\n项目简介：\n构建了一个高效的物联网边缘计算平台，通过在网络边缘部署计算资源，实现数据的就近处理和分析，大幅降低网络延迟，提高系统响应速度。\n\n核心功能：\n• 边缘设备管理和监控\n• 实时数据处理和分析\n• 智能任务调度和负载均衡\n• 安全的数据传输和存储\n\n技术优势：\n• 延迟降低80%以上\n• 带宽使用效率提升60%\n• 支持多种IoT设备接入\n• 具备故障自愈能力\n\n市场前景：\n随着5G和IoT技术的快速发展，边缘计算将成为未来智能化系统的重要基础设施。",
            "术影无踪——OpenCV双目视觉智能手术调光系统\n\n项目简介：\n基于区块链技术开发的供应链溯源系统，通过分布式账本技术确保供应链信息的透明性和不可篡改性，为消费者提供可信的产品溯源服务。\n\n系统特色：\n• 全链条信息记录和追踪\n• 智能合约自动执行\n• 多方参与的信任机制\n• 防伪验证和质量保证\n\n技术架构：\n• 基于以太坊区块链平台\n• 采用IPFS分布式存储\n• 支持移动端和Web端访问\n• 集成二维码和NFC技术\n\n社会价值：\n有效解决食品安全、药品监管等社会关注问题，提升消费者信心，促进诚信商业环境建设。",
            "光明之路——一种“开源节流”型路灯\n\n项目简介：\n针对5G网络的复杂性和高性能要求，研究开发了一套智能网络优化算法，能够动态调整网络参数，优化资源分配，提升网络性能和用户体验。\n\n研究内容：\n• 基于机器学习的网络预测模型\n• 动态资源分配优化算法\n• 网络切片技术应用\n• 边缘计算与5G融合\n\n技术创新：\n• 网络吞吐量提升40%\n• 延迟降低50%\n• 能耗优化30%\n• 支持大规模设备连接\n\n应用前景：\n为5G网络运营商提供智能化网络管理解决方案，推动5G技术在各行业的深度应用。",
            "声纹哨兵——城市噪声污染AI监测与治理系统\n\n项目简介：\n开发了基于人工智能技术的医疗辅助诊断系统，通过深度学习算法分析医学影像和临床数据，为医生提供准确的诊断建议和治疗方案推荐。\n\n核心功能：\n• 医学影像智能分析\n• 疾病风险评估\n• 个性化治疗方案推荐\n• 药物相互作用检测\n\n技术特点：\n• 诊断准确率达到专家水平\n• 支持多种医学影像格式\n• 实时处理和快速响应\n• 符合医疗数据安全标准\n\n临床价值：\n显著提高诊断效率和准确性，减轻医生工作负担，为偏远地区提供高质量医疗服务支持。",
            "聆途睿行一一基于多模态人机交互的智慧导盲系统\n\n项目简介：\n构建了综合性的智慧农业监测平台，集成物联网传感器、无人机遥感、气象监测等技术，实现农业生产的精准化、智能化管理。\n\n系统功能：\n• 土壤环境实时监测\n• 作物生长状态分析\n• 病虫害预警和防治\n• 智能灌溉控制\n• 产量预测和优化\n\n技术集成：\n• 多源数据融合分析\n• 机器学习预测模型\n• 自动化控制系统\n• 移动端管理应用\n\n经济效益：\n帮助农户提高产量20%以上，降低成本15%，实现农业可持续发展，推动乡村振兴战略实施。"
        };
        
        // 为每个查看详细按钮设置点击事件
        for (int i = 1; i <= 6; i++) {
            int buttonId = getResources().getIdentifier("btn_detail_" + i, "id", getPackageName());
            ImageButton detailButton = contentContainer.findViewById(buttonId);
            if (detailButton != null) {
                final int projectIndex = i - 1;
                detailButton.setOnClickListener(v -> showProjectDetail(projectIndex));
            }
        }
    }
    
    private void showProjectDetailDialog(String projectDetail) {
        // 不再使用对话框，而是启动新的Activity
        // 这个方法现在被重命名为showProjectDetail并接受项目索引
    }
    
    private void showProjectDetail(int projectIndex) {
        Intent intent = new Intent(this, ProjectDetailActivity.class);
        intent.putExtra("project_index", projectIndex);
        startActivity(intent);
    }
    
    private void showSafetyFragment() {
        contentContainer.removeAllViews();
        getLayoutInflater().inflate(R.layout.fragment_safety, contentContainer, true);
    }
    

    
    public void showCultureFragment() {
        CultureFragment fragment = new CultureFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_container, fragment)
                .commit();
    }

    public void showManagementFragment() {
        ManagementFragment fragment = new ManagementFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_container, fragment)
                .commit();
    }
    
    public void showCultivationFragment() {
        contentContainer.removeAllViews();
        
        // 使用Fragment来支持Glide图片加载和交互功能
        CultivationFragment cultivationFragment = new CultivationFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_container, cultivationFragment)
                .commit();
    }
    
    private void showAiCultivationFragment() {
        contentContainer.removeAllViews();
        getLayoutInflater().inflate(R.layout.fragment_ai_cultivation, contentContainer, true);
        
        // 设置AI人才培养界面的交互功能
        setupAiCultivationInteractions();
    }
    
    /**
     * 显示现代化的AI人才培养界面
     * 这是UI改进后的新界面，具有现代化设计和微信小程序入口
     */
    private void showAiCultivationModernFragment() {
        contentContainer.removeAllViews();
        
        // 使用Fragment方式加载现代化界面
        AiCultivationModernFragment modernFragment = new AiCultivationModernFragment();
        getSupportFragmentManager().beginTransaction()
            .replace(R.id.content_container, modernFragment)
            .commit();
    }
    
    private void showAiEvaluationFragment() {
        contentContainer.removeAllViews();
        getLayoutInflater().inflate(R.layout.fragment_ai_evaluation, contentContainer, true);
        
        // 设置AI项目评估界面的交互功能
        setupAiEvaluationInteractions();
    }
    
    private void setupAiCultivationInteractions() {
        // 获取界面元素
        Button btnGeneratePlan = contentContainer.findViewById(R.id.btn_generate_plan);
        Button btnSavePlan = contentContainer.findViewById(R.id.btn_save_plan);
        Button btnSharePlan = contentContainer.findViewById(R.id.btn_share_plan);
        LinearLayout layoutPlanDisplay = contentContainer.findViewById(R.id.layout_plan_display);
        TextView tvPlanContent = contentContainer.findViewById(R.id.tv_plan_content);
        
        // 获取微信小程序按钮
        ImageButton btnWechatMiniprogram = contentContainer.findViewById(R.id.btn_wechat_miniprogram);
        
        // 获取输入框
        EditText etName = contentContainer.findViewById(R.id.et_name);
        EditText etAge = contentContainer.findViewById(R.id.et_age);
        EditText etEducation = contentContainer.findViewById(R.id.et_education);
        EditText etMajor = contentContainer.findViewById(R.id.et_major);
        EditText etExperience = contentContainer.findViewById(R.id.et_experience);
        EditText etSkillLevel = contentContainer.findViewById(R.id.et_skill_level);
        EditText etInterest = contentContainer.findViewById(R.id.et_interest);
        EditText etGoal = contentContainer.findViewById(R.id.et_goal);
        
        // 生成方案按钮点击事件
        if (btnGeneratePlan != null) {
            btnGeneratePlan.setOnClickListener(v -> {
                // 收集用户输入信息
                String name = etName != null ? etName.getText().toString().trim() : "";
                String age = etAge != null ? etAge.getText().toString().trim() : "";
                String education = etEducation != null ? etEducation.getText().toString().trim() : "";
                String major = etMajor != null ? etMajor.getText().toString().trim() : "";
                String experience = etExperience != null ? etExperience.getText().toString().trim() : "";
                String skillLevel = etSkillLevel != null ? etSkillLevel.getText().toString().trim() : "";
                String interest = etInterest != null ? etInterest.getText().toString().trim() : "";
                String goal = etGoal != null ? etGoal.getText().toString().trim() : "";
                
                // 验证必填字段
                if (name.isEmpty() || education.isEmpty() || major.isEmpty()) {
                    showToast("请填写姓名、学历和专业等基本信息");
                    return;
                }
                
                // 生成AI培养方案
                String plan = generateAiCultivationPlan(name, age, education, major, experience, skillLevel, interest, goal);
                
                // 显示方案
                if (tvPlanContent != null) {
                    tvPlanContent.setText(plan);
                    tvPlanContent.setGravity(android.view.Gravity.START);
                }
                
                if (layoutPlanDisplay != null) {
                    layoutPlanDisplay.setVisibility(View.VISIBLE);
                }
                
                showToast("AI培养方案生成成功！");
            });
        }
        
        // 保存方案按钮点击事件
        if (btnSavePlan != null) {
            btnSavePlan.setOnClickListener(v -> {
                showToast("方案已保存到本地");
            });
        }
        
        // 分享方案按钮点击事件
        if (btnSharePlan != null) {
            btnSharePlan.setOnClickListener(v -> {
                if (tvPlanContent != null) {
                    String planText = tvPlanContent.getText().toString();
                    if (!planText.isEmpty() && !planText.contains("点击上方按钮")) {
                        shareAiPlan(planText);
                    } else {
                        showToast("请先生成培养方案");
                    }
                }
            });
        }
        
        // 微信小程序按钮点击事件
        if (btnWechatMiniprogram != null) {
            btnWechatMiniprogram.setOnClickListener(v -> {
                // 添加点击动画效果
                v.setAlpha(0.7f);
                v.postDelayed(() -> v.setAlpha(1.0f), 150);
                
                // 跳转到微信小程序界面
                Intent intent = new Intent(MainActivity.this, WechatMiniprogramActivity.class);
                startActivity(intent);
            });
        }
    }
    
    private String generateAiCultivationPlan(String name, String age, String education, String major, 
                                            String experience, String skillLevel, String interest, String goal) {
        StringBuilder plan = new StringBuilder();
        
        plan.append("🎯 ").append(name).append("的AI人才培养方案\n\n");
        
        plan.append("📋 基本信息分析\n");
        plan.append("• 姓名：").append(name).append("\n");
        if (!age.isEmpty()) plan.append("• 年龄：").append(age).append("岁\n");
        plan.append("• 学历：").append(education).append("\n");
        plan.append("• 专业：").append(major).append("\n");
        if (!experience.isEmpty()) plan.append("• 工作经验：").append(experience).append("年\n");
        if (!skillLevel.isEmpty()) plan.append("• 技能水平：").append(skillLevel).append("\n");
        plan.append("\n");
        
        plan.append("🎯 培养目标设定\n");
        if (!goal.isEmpty()) {
            plan.append("• 个人目标：").append(goal).append("\n");
        }
        plan.append("• 核心能力：机器学习算法理解与应用\n");
        plan.append("• 技术栈：Python、TensorFlow/PyTorch、数据分析\n");
        plan.append("• 项目经验：完成2-3个AI实战项目\n\n");
        
        plan.append("📚 学习路径规划\n");
        plan.append("阶段一：基础理论学习（1-2个月）\n");
        plan.append("• 数学基础：线性代数、概率统计、微积分\n");
        plan.append("• 编程基础：Python语言深入学习\n");
        plan.append("• AI概论：人工智能发展史与基本概念\n\n");
        
        plan.append("阶段二：核心技术掌握（2-3个月）\n");
        plan.append("• 机器学习：监督学习、无监督学习、强化学习\n");
        plan.append("• 深度学习：神经网络、CNN、RNN、Transformer\n");
        plan.append("• 数据处理：数据清洗、特征工程、可视化\n\n");
        
        plan.append("阶段三：实战项目训练（2-3个月）\n");
        if (!interest.isEmpty()) {
            plan.append("• 专业方向项目：基于").append(interest).append("的应用开发\n");
        }
        plan.append("• 计算机视觉：图像识别与处理项目\n");
        plan.append("• 自然语言处理：文本分析与生成项目\n");
        plan.append("• 推荐系统：个性化推荐算法实现\n\n");
        
        plan.append("🛠️ 推荐学习资源\n");
        plan.append("• 在线课程：吴恩达机器学习课程、CS231n\n");
        plan.append("• 实践平台：Kaggle竞赛、GitHub开源项目\n");
        plan.append("• 技术社区：CSDN、知乎AI专栏、Stack Overflow\n");
        plan.append("• 论文阅读：arXiv最新AI论文跟踪\n\n");
        
        plan.append("📈 能力评估与进阶\n");
        plan.append("• 月度评估：理论知识测试 + 项目代码审查\n");
        plan.append("• 技能认证：获得相关AI技术认证证书\n");
        plan.append("• 行业实习：寻找AI相关实习或工作机会\n");
        plan.append("• 持续学习：关注AI前沿技术发展趋势\n\n");
        
        plan.append("💡 个性化建议\n");
        if (major.contains("计算机") || major.contains("软件")) {
            plan.append("• 基于您的计算机专业背景，建议重点关注算法优化和系统架构\n");
        } else if (major.contains("数学") || major.contains("统计")) {
            plan.append("• 基于您的数学背景，建议深入学习机器学习理论和算法原理\n");
        } else {
            plan.append("• 建议先补强数学和编程基础，再进入AI核心技术学习\n");
        }
        
        if (!experience.isEmpty() && !experience.equals("0")) {
            plan.append("• 利用您的工作经验，寻找AI技术在现有领域的应用场景\n");
        }
        
        plan.append("• 建议加入AI学习社群，与同行交流学习心得\n");
        plan.append("• 定期参加AI技术会议和workshop，拓展视野\n\n");
        
        plan.append("🎉 预期成果\n");
        plan.append("完成本培养方案后，您将具备：\n");
        plan.append("✅ 扎实的AI理论基础和实践能力\n");
        plan.append("✅ 独立完成AI项目的技术实力\n");
        plan.append("✅ 在AI领域的职业发展竞争力\n");
        plan.append("✅ 持续学习和技术创新的能力\n");
        
        return plan.toString();
    }
    
    private void shareAiPlan(String planText) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, planText);
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "我的AI人才培养方案");
        startActivity(Intent.createChooser(shareIntent, "分享培养方案"));
    }
    
    private void setupAiEvaluationInteractions() {
        // 获取启动评估按钮
        Button btnStartEvaluation = contentContainer.findViewById(R.id.btn_start_evaluation);
        Button btnExportResult = contentContainer.findViewById(R.id.btn_export_result);
        
        // 启动评估按钮点击事件
        if (btnStartEvaluation != null) {
            btnStartEvaluation.setOnClickListener(v -> {
                // 添加点击动画效果
                v.setAlpha(0.7f);
                v.postDelayed(() -> v.setAlpha(1.0f), 150);
                
                showToast("AI项目评估功能正在开发中...");
            });
        }
        
        // 导出结果按钮点击事件
        if (btnExportResult != null) {
            btnExportResult.setOnClickListener(v -> {
                // 添加点击动画效果
                v.setAlpha(0.7f);
                v.postDelayed(() -> v.setAlpha(1.0f), 150);
                
                showToast("结果导出功能正在开发中...");
            });
        }
    }
    
    private void showToast(String message) {
        android.widget.Toast.makeText(this, message, android.widget.Toast.LENGTH_SHORT).show();
    }
    
    private void showBaseIntroductionFragment() {
        BaseIntroductionFragment fragment = new BaseIntroductionFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_container, fragment)
                .commit();
    }
    
    private void showAiAssistantFragment() {
        AiAssistantFragment fragment = new AiAssistantFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_container, fragment)
                .commit();
    }
    
    private void showAiEvaluationModernFragment() {
        AiEvaluationModernFragment fragment = new AiEvaluationModernFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_container, fragment)
                .commit();
    }
    
    public void showIntroductionFragment() {
        IntroductionFragment fragment = new IntroductionFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_container, fragment)
                .commit();
    }
    
    public void showFundManagementFragment() {
        FundManagementFragment fragment = new FundManagementFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_container, fragment)
                .commit();
    }
    
    private void startTimeUpdate() {
        timeHandler = new Handler();
        timeRunnable = new Runnable() {
            @Override
            public void run() {
                updateTime();
                timeHandler.postDelayed(this, 1000); // 每秒更新一次
            }
        };
        timeHandler.post(timeRunnable);
    }
    
    private void updateTime() {
        Date now = new Date();
        
        // 更新时间显示 (HH:mm:ss)
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        timeDisplay.setText(timeFormat.format(now));
        
        // 更新日期显示 (yyyy年MM月dd日)
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日", Locale.getDefault());
        dateDisplay.setText(dateFormat.format(now));
        
        // 更新星期显示 (EEEE)
        SimpleDateFormat weekdayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
        weekdayDisplay.setText(weekdayFormat.format(now));
    }
    
    // 天气数据更新方法
    private void updateWeatherData() {
        executorService.execute(() -> {
            try {
                Request request = new Request.Builder()
                    .url(WEATHER_API_URL)
                    .build();
                
                Response response = httpClient.newCall(request).execute();
                if (response.isSuccessful() && response.body() != null) {
                    String responseBody = response.body().string();
                    parseAndUpdateWeather(responseBody);
                } else {
                    runOnUiThread(this::setDefaultWeatherData);
                }
            } catch (IOException e) {
                e.printStackTrace();
                // 网络请求失败时在主线程显示默认数据
                runOnUiThread(this::setDefaultWeatherData);
            }
        });
    }
    
    private void parseAndUpdateWeather(String jsonResponse) {
        try {
            JSONObject jsonObject = new JSONObject(jsonResponse);
            String status = jsonObject.getString("status");
            
            if ("1".equals(status)) {
                JSONArray lives = jsonObject.getJSONArray("lives");
                if (lives.length() > 0) {
                    JSONObject weatherData = lives.getJSONObject(0);
                    
                    String temperature = weatherData.getString("temperature");
                    String weather = weatherData.getString("weather");
                    String humidity = weatherData.getString("humidity");
                    String windDirection = weatherData.getString("winddirection");
                    String windPower = weatherData.getString("windpower");
                    String reportTime = weatherData.getString("reporttime");
                    
                    // 在主线程更新UI
                    runOnUiThread(() -> updateWeatherUI(temperature, weather, humidity, windDirection, windPower, reportTime));
                } else {
                    runOnUiThread(this::setDefaultWeatherData);
                }
            } else {
                runOnUiThread(this::setDefaultWeatherData);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            runOnUiThread(this::setDefaultWeatherData);
        }
    }
    
    private void updateWeatherUI(String temperature, String weather, String humidity, String windDirection, String windPower, String reportTime) {
        TextView temperatureView = contentContainer.findViewById(R.id.temperature);
        TextView weatherDescView = contentContainer.findViewById(R.id.weather_desc);
        ImageView weatherIconView = contentContainer.findViewById(R.id.weather_icon);
        TextView humidityView = contentContainer.findViewById(R.id.humidity);
        TextView windDirectionView = contentContainer.findViewById(R.id.wind_direction);
        TextView windPowerView = contentContainer.findViewById(R.id.wind_power);
        TextView updateTimeView = contentContainer.findViewById(R.id.update_time);
        
        if (temperatureView != null) {
            temperatureView.setText(temperature + "°C");
        }
        if (weatherDescView != null) {
            weatherDescView.setText(weather);
        }
        
        // 根据天气状况设置天气图标
        if (weatherIconView != null) {
            int weatherIconRes = getWeatherIcon(weather);
            weatherIconView.setImageResource(weatherIconRes);
        }
        
        if (humidityView != null) {
            humidityView.setText(humidity + "%");
        }
        if (windDirectionView != null) {
            windDirectionView.setText(windDirection + "风");
        }
        if (windPowerView != null) {
            windPowerView.setText(windPower + "级");
        }
        if (updateTimeView != null) {
            // 格式化更新时间显示
            try {
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                SimpleDateFormat outputFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
                Date date = inputFormat.parse(reportTime);
                if (date != null) {
                    updateTimeView.setText("更新时间: " + outputFormat.format(date));
                } else {
                    updateTimeView.setText("更新时间: 刚刚");
                }
            } catch (Exception e) {
                updateTimeView.setText("更新时间: 刚刚");
            }
        }
    }
    
    private void setDefaultWeatherData() {
        TextView temperatureView = contentContainer.findViewById(R.id.temperature);
        TextView weatherDescView = contentContainer.findViewById(R.id.weather_desc);
        ImageView weatherIconView = contentContainer.findViewById(R.id.weather_icon);
        TextView humidityView = contentContainer.findViewById(R.id.humidity);
        TextView windDirectionView = contentContainer.findViewById(R.id.wind_direction);
        TextView windPowerView = contentContainer.findViewById(R.id.wind_power);
        TextView updateTimeView = contentContainer.findViewById(R.id.update_time);
        
        if (temperatureView != null) {
            temperatureView.setText("0°C");
        }
        if (weatherDescView != null) {
            weatherDescView.setText("晴");
        }
        
        // 设置默认天气图标
        if (weatherIconView != null) {
            weatherIconView.setImageResource(R.drawable.sunday);
        }
        
        if (humidityView != null) {
            humidityView.setText("65%");
        }
        if (windDirectionView != null) {
            windDirectionView.setText("东南风");
        }
        if (windPowerView != null) {
            windPowerView.setText("3级");
        }
        if (updateTimeView != null) {
            updateTimeView.setText("更新时间: 刚刚");
        }
    }
    
    // 根据天气状况返回对应的图标资源ID
    private int getWeatherIcon(String weather) {
        if (weather.contains("阴")) {
            return R.drawable.overcast;
        } else if (weather.contains("晴")) {
            return R.drawable.sunday;
        } else if (weather.contains("雨")) {
            return R.drawable.rainday;
        } else if (weather.contains("多云")) {
            return R.drawable.clouday;
        } else {
            // 默认返回晴天图标
            return R.drawable.sunday;
        }
    }
    
    // 名言金句相关方法
    private void setupQuoteDisplay() {
        quoteTextView = contentContainer.findViewById(R.id.tv_quote);
        if (quoteTextView != null) {
            // 立即更新一次名言金句
            updateQuoteData();
            // 启动定时更新
            startQuoteUpdate();
        }
    }
    
    private void startQuoteUpdate() {
        stopQuoteUpdate(); // 先停止之前的定时器
        
        quoteHandler = new Handler();
        quoteRunnable = new Runnable() {
            @Override
            public void run() {
                updateQuoteData();
                quoteHandler.postDelayed(this, 5 * 60 * 1000); // 5分钟后执行下一次
            }
        };
        quoteHandler.postDelayed(quoteRunnable, 5 * 60 * 1000); // 5分钟后开始第一次自动更新
    }
    
    private void stopQuoteUpdate() {
        if (quoteHandler != null && quoteRunnable != null) {
            quoteHandler.removeCallbacks(quoteRunnable);
        }
    }
    
    private void updateQuoteData() {
        executorService.execute(() -> {
            try {
                Request request = new Request.Builder()
                    .url(QUOTE_API_URL)
                    .build();
                
                Response response = httpClient.newCall(request).execute();
                if (response.isSuccessful() && response.body() != null) {
                    String responseBody = response.body().string();
                    parseAndUpdateQuote(responseBody);
                } else {
                    // API请求失败时保持当前名言不变
                }
            } catch (IOException e) {
                e.printStackTrace();
                // 网络请求失败时保持当前名言不变
            }
        });
    }
    
    private void parseAndUpdateQuote(String jsonResponse) {
        try {
            JSONObject jsonObject = new JSONObject(jsonResponse);
            String hitokoto = jsonObject.getString("hitokoto");
            String from = jsonObject.optString("from", "");
            String fromWho = jsonObject.optString("from_who", "");
            
            // 构建显示文本
            StringBuilder quoteText = new StringBuilder(hitokoto);
            if (!from.isEmpty()) {
                quoteText.append(" —— ");
                if (!fromWho.isEmpty()) {
                    quoteText.append(fromWho).append("《").append(from).append("》");
                } else {
                    quoteText.append("《").append(from).append("》");
                }
            }
            
            // 在主线程更新UI
            runOnUiThread(() -> {
                if (quoteTextView != null) {
                    quoteTextView.setText(quoteText.toString());
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
            // JSON解析失败时保持当前名言不变
        }
    }
    
    // 设置快速入口按钮点击事件
    private void setupQuickAccessButtons() {
        LinearLayout btnGradeSearch = contentContainer.findViewById(R.id.btn_grade_search);
        if (btnGradeSearch != null) {
            btnGradeSearch.setOnClickListener(v -> {
                // 使用默认浏览器打开网址
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("http://sys.sharpcaterpillar.top:7878/single"));
                startActivity(intent);
            });
        }
        
        LinearLayout btnScanCheckin = contentContainer.findViewById(R.id.btn_scan_checkin);
        if (btnScanCheckin != null) {
            btnScanCheckin.setOnClickListener(v -> {
                // 跳转到扫码签到网页
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("http://sys.sharpcaterpillar.top:5000/qr-display"));
                startActivity(intent);
            });
        }
        
        LinearLayout btnResourceCenter = contentContainer.findViewById(R.id.btn_resource_center);
        if (btnResourceCenter != null) {
            btnResourceCenter.setOnClickListener(v -> {
                // 使用默认浏览器打开资源中心网址
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("http://101.132.166.117:9898/"));
                startActivity(intent);
            });
        }
        
        LinearLayout btnEmergencyHelp = contentContainer.findViewById(R.id.btn_emergency_help);
        if (btnEmergencyHelp != null) {
            btnEmergencyHelp.setOnClickListener(v -> {
                // 跳转到紧急求助界面
                Intent intent = new Intent(MainActivity.this, EmergencyHelpActivity.class);
                startActivity(intent);
            });
        }
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timeHandler != null && timeRunnable != null) {
            timeHandler.removeCallbacks(timeRunnable);
        }
        // 停止轮播图自动播放
        stopCarouselAutoPlay();
        
        // 停止名言金句定时更新
        stopQuoteUpdate();
        
        // 关闭网络请求线程池
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}