package com.example.campusscreen;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Locale;

public class AiAssistantFragment extends Fragment {
    
    private static final int PERMISSION_REQUEST_RECORD_AUDIO = 1;
   // UI组件
    private LinearLayout messagesContainer;
    private ScrollView messagesScrollView;
    private EditText messageInput;
    private ImageButton sendButton;
    private ImageButton microphoneButton;
    private ImageButton clearChatButton;
    private TextView conversationStatusText;
    private LinearLayout btnWechatMiniprogram;
    
    // AI服务
    private QwenApiService qwenApiService;
    
    // 语音识别相关
    private SpeechRecognizer speechRecognizer;
    private Intent speechRecognizerIntent;
    private boolean isListening = false;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ai_assistant, container, false);
        
        initViews(view);
        setupListeners();
        initSpeechRecognizer();
        
        // 确保预设对话保持可见
        preserveExampleMessages();
        
        // 强制刷新布局，确保内容显示
        view.post(() -> {
            if (messagesScrollView != null) {
                messagesScrollView.requestLayout();
                messagesScrollView.invalidate();
                messagesScrollView.setVisibility(View.VISIBLE);
            }
            if (messagesContainer != null) {
                messagesContainer.requestLayout();
                messagesContainer.invalidate();
                messagesContainer.setVisibility(View.VISIBLE);
            }
        });
        
        // 设置软键盘监听
        setupKeyboardListener(view);
        
        return view;
    }
    
    private void initViews(View view) {
        messagesContainer = view.findViewById(R.id.messages_container);
        messagesScrollView = view.findViewById(R.id.messages_scroll_view);
        messageInput = view.findViewById(R.id.et_message_input);
        sendButton = view.findViewById(R.id.btn_send_message);
        microphoneButton = view.findViewById(R.id.btn_microphone);
        clearChatButton = view.findViewById(R.id.btn_clear_chat);
        conversationStatusText = view.findViewById(R.id.tv_conversation_status);
        btnWechatMiniprogram = view.findViewById(R.id.btn_wechat_miniprogram);
        
        // 初始化AI服务
        qwenApiService = new QwenApiService();
        
        // 更新对话状态显示
        updateConversationStatus();
        
        // 确保ScrollView和消息容器初始可见
        if (messagesScrollView != null) {
            messagesScrollView.setVisibility(View.VISIBLE);
        }
        if (messagesContainer != null) {
            messagesContainer.setVisibility(View.VISIBLE);
        }
    }
    
    private void preserveExampleMessages() {
        // 保留布局文件中的预设示例对话，不做任何清空操作
        // 新消息将添加到现有内容之后
    }
    
    private void setupListeners() {
        // 发送消息按钮点击事件
        if (sendButton != null) {
            sendButton.setOnClickListener(v -> sendMessage());
        }
        
        // 麦克风按钮点击事件
        if (microphoneButton != null) {
            microphoneButton.setOnClickListener(v -> {
                startVoiceInput();
            });
        }
        
        // 输入框回车发送
        if (messageInput != null) {
            messageInput.setOnEditorActionListener((v, actionId, event) -> {
                sendMessage();
                return true;
            });
        }
        
        // 输入框焦点变化监听
        if (messageInput != null) {
            messageInput.setOnFocusChangeListener((v, hasFocus) -> {
                // 无论获得还是失去焦点，都确保消息内容可见
                ensureMessagesVisible();
            });
        }
        
        // 清空对话按钮点击事件
        if (clearChatButton != null) {
            clearChatButton.setOnClickListener(v -> clearConversation());
        }
        
        // 微信小程序按钮点击事件
        if (btnWechatMiniprogram != null) {
            btnWechatMiniprogram.setOnClickListener(v -> openWechatMiniprogram());
        }
    }
    
    private void sendMessage() {
        String message = messageInput.getText().toString().trim();
        if (!message.isEmpty()) {
            // 添加用户消息到界面
            addUserMessage(message);
            
            // 清空输入框
            messageInput.setText("");
            
            // 模拟AI回复
            simulateAiResponse(message);
        }
    }
    
    private void addUserMessage(String message) {
        // 创建用户消息气泡
        LinearLayout messageLayout = new LinearLayout(getContext());
        messageLayout.setLayoutParams(new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        messageLayout.setGravity(android.view.Gravity.END);
        messageLayout.setPadding(0, 0, 0, 12);
        
        TextView messageText = new TextView(getContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(60, 0, 0, 0);
        messageText.setLayoutParams(params);
        messageText.setText(message);
        messageText.setTextColor(getResources().getColor(R.color.white, null));
        messageText.setTextSize(14);
        messageText.setPadding(12, 12, 12, 12);
        messageText.setBackgroundResource(R.drawable.bg_user_message_bubble);
        messageText.setElevation(2);
        
        messageLayout.addView(messageText);
        messagesContainer.addView(messageLayout);
        
        // 滚动到底部显示新消息
        scrollToBottom();
    }
    
    private void addAiMessage(String message) {
        // 创建AI消息气泡
        LinearLayout messageLayout = new LinearLayout(getContext());
        messageLayout.setLayoutParams(new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        messageLayout.setGravity(android.view.Gravity.START);
        messageLayout.setPadding(0, 0, 0, 16);
        
        TextView messageText = new TextView(getContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 60, 0);
        messageText.setLayoutParams(params);
        messageText.setText(message);
        messageText.setTextColor(getResources().getColor(R.color.text_primary, null));
        messageText.setTextSize(14);
        messageText.setPadding(12, 12, 12, 12);
        messageText.setBackgroundResource(R.drawable.bg_assistant_message_bubble);
        messageText.setElevation(2);
        messageText.setLineSpacing(2, 1.0f);
        
        messageLayout.addView(messageText);
        messagesContainer.addView(messageLayout);
        
        // 滚动到底部显示新消息
        scrollToBottom();
    }
    
    private void simulateAiResponse(String userMessage) {
        // 显示思考中的提示
        addAiMessage("🤔 正在思考中...");
        
        // 调用通义千问API
        qwenApiService.sendMessage(userMessage, new QwenApiService.ApiCallback() {
            @Override
            public void onSuccess(String response) {
                // 移除"思考中"消息并添加真实回复
                removeLastMessage();
                addAiMessage(response);
                // 更新对话状态
                updateConversationStatus();
            }
            
            @Override
            public void onError(String error) {
                // 移除"思考中"消息并显示错误信息
                removeLastMessage();
                String fallbackResponse = "抱歉，AI服务暂时不可用 😔\n\n" + 
                                        "错误信息：" + error + "\n\n" +
                                        "您可以尝试：\n" +
                                        "• 检查网络连接\n" +
                                        "• 稍后重试\n" +
                                        "• 或直接咨询工作人员";
                addAiMessage(fallbackResponse);
            }
        });
    }
    
    private void clearConversation() {
        // 显示确认对话框
        new android.app.AlertDialog.Builder(getContext())
                .setTitle("清空对话")
                .setMessage("确定要清空所有对话记录吗？此操作不可撤销。")
                .setPositiveButton("确定", (dialog, which) -> {
                    // 清空UI中的消息（保留示例消息）
                    clearMessagesFromUI();
                    
                    // 清空AI服务中的对话历史
                    qwenApiService.clearConversationHistory();
                    
                    // 更新对话状态
                     updateConversationStatus();
                     
                     // 显示成功提示
                     android.widget.Toast.makeText(getContext(), "对话已清空，可以开始新的对话了 ✨", android.widget.Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("取消", null)
                .show();
    }
    
    private void clearMessagesFromUI() {
        if (messagesContainer != null) {
            // 移除所有动态添加的消息，保留原始的示例消息
            int childCount = messagesContainer.getChildCount();
            // 保留前4个示例消息（根据布局文件中的示例对话数量）
            int exampleMessageCount = 4;
            
            for (int i = childCount - 1; i >= exampleMessageCount; i--) {
                messagesContainer.removeViewAt(i);
            }
            
            // 滚动到顶部显示示例消息
            messagesScrollView.post(() -> messagesScrollView.fullScroll(ScrollView.FOCUS_UP));
        }
     }
     
     private void updateConversationStatus() {
         if (conversationStatusText != null && qwenApiService != null) {
             int conversationSize = qwenApiService.getConversationSize();
             String statusText;
             
             if (conversationSize == 0) {
                 statusText = "💬 支持上下文对话";
             } else {
                 int rounds = conversationSize / 2; // 每轮对话包含用户消息和AI回复
                 statusText = String.format("💬 已进行 %d 轮对话", rounds);
             }
             
             conversationStatusText.setText(statusText);
         }
     }
     
     private String generateAiResponse(String userMessage) {
        // 简单的关键词匹配回复
        String message = userMessage.toLowerCase();
        
        if (message.contains("你好") || message.contains("hello")) {
            return "您好！我是AI智能助手，很高兴为您服务！🤖\n\n我可以帮助您：\n💬 智能对话交流\n📚 知识问答解答\n🎯 基地信息查询\n\n请告诉我您需要什么帮助？";
        } else if (message.contains("实验室") || message.contains("基地")) {
            return "通信电子创新基地是我们的核心实验室！🏢\n\n主要功能包括：\n📡 5G通信技术研发\n🔬 电子创新实验平台\n👥 专业人才培养\n🎯 科研项目孵化\n\n您想了解哪个方面的详细信息？";
        } else if (message.contains("项目") || message.contains("培养")) {
            return "我们的人才培养项目非常丰富！🎓\n\n包括：\n🌟 理论与实践结合课程\n💻 前沿技术专题培训\n🤝 导师一对一指导\n🏆 实战项目参与机会\n📈 职业发展规划\n\n如需了解申请流程，请直接询问我！";
        } else if (message.contains("帮助") || message.contains("功能")) {
            return "我的主要功能如下：\n\n🤖 智能对话交流\n📚 基地信息查询\n🎓 人才培养咨询\n🔬 实验室介绍\n📊 项目信息展示\n\n您可以直接向我提问，我会尽力为您解答！";
        } else {
            return "感谢您的提问！🤔\n\n我正在学习中，如果我的回答不够准确，您可以：\n\n💬 换个方式重新提问\n📚 询问基地相关信息\n🎓 咨询人才培养项目\n\n有什么其他问题我可以帮您解答吗？";
        }
    }
    
    private void startVoiceInput() {
        // 检查设备是否支持语音识别
        if (!SpeechRecognizer.isRecognitionAvailable(getContext())) {
            // 聚焦到输入框
            if (messageInput != null) {
                messageInput.requestFocus();
            }
            return;
        }
        
        if (speechRecognizer == null) {
            initSpeechRecognizer();
            return;
        }
        
        if (checkAudioPermission()) {
            if (!isListening) {
                startListening();
            } else {
                stopListening();
            }
        } else {
            requestAudioPermission();
        }
    }
    
    private void scrollToBottom() {
        // 延迟滚动，确保新消息已经添加到布局中
        messagesContainer.post(() -> {
            if (messagesScrollView != null) {
                messagesScrollView.fullScroll(ScrollView.FOCUS_DOWN);
                messagesScrollView.invalidate();
            }
        });
    }
    
    private void removeLastMessage() {
        if (messagesContainer != null && messagesContainer.getChildCount() > 0) {
            int lastIndex = messagesContainer.getChildCount() - 1;
            messagesContainer.removeViewAt(lastIndex);
        }
    }
    
    private void setupKeyboardListener(View rootView) {
        // 监听整个布局的大小变化，这通常表示键盘显示/隐藏
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            ensureMessagesVisible();
        });
    }
    
    private void ensureMessagesVisible() {
        // 确保消息内容可见
        if (messagesScrollView != null) {
            messagesScrollView.setVisibility(View.VISIBLE);
            messagesScrollView.requestLayout();
            messagesScrollView.invalidate();
        }
        if (messagesContainer != null) {
            messagesContainer.setVisibility(View.VISIBLE);
            messagesContainer.requestLayout();
            messagesContainer.invalidate();
        }
    }
    
    // 语音识别相关方法
    private void initSpeechRecognizer() {
        try {
            if (SpeechRecognizer.isRecognitionAvailable(getContext())) {
                speechRecognizer = SpeechRecognizer.createSpeechRecognizer(getContext());
                if (speechRecognizer == null) {
                    disableVoiceInput();
                    return;
                }
                
                speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
                speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
            
            speechRecognizer.setRecognitionListener(new RecognitionListener() {
                @Override
                public void onReadyForSpeech(Bundle params) {
                    isListening = true;
                    updateMicrophoneButton();
                }
                
                @Override
                public void onBeginningOfSpeech() {}
                
                @Override
                public void onRmsChanged(float rmsdB) {}
                
                @Override
                public void onBufferReceived(byte[] buffer) {}
                
                @Override
                public void onEndOfSpeech() {
                    isListening = false;
                    updateMicrophoneButton();
                }
                
                @Override
                public void onError(int error) {
                    isListening = false;
                    updateMicrophoneButton();
                }
                
                @Override
                public void onResults(Bundle results) {
                    isListening = false;
                    updateMicrophoneButton();
                    ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                    if (matches != null && !matches.isEmpty()) {
                        String recognizedText = matches.get(0);
                        if (messageInput != null) {
                            messageInput.setText(recognizedText);
                            messageInput.setSelection(recognizedText.length());
                        }
                    }
                }
                
                @Override
                public void onPartialResults(Bundle partialResults) {
                    ArrayList<String> matches = partialResults.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                    if (matches != null && !matches.isEmpty() && messageInput != null) {
                        String partialText = matches.get(0);
                        messageInput.setText(partialText);
                        messageInput.setSelection(partialText.length());
                    }
                }
                
                @Override
                public void onEvent(int eventType, Bundle params) {}
            });
        } else {
            disableVoiceInput();
        }
        } catch (Exception e) {
            disableVoiceInput();
            e.printStackTrace();
        }
    }
    
    private void disableVoiceInput() {
        if (microphoneButton != null) {
            microphoneButton.setEnabled(false);
            microphoneButton.setAlpha(0.5f);
            microphoneButton.setOnClickListener(v -> {
                // 语音功能不可用时，聚焦到输入框
                if (messageInput != null) {
                    messageInput.requestFocus();
                }
            });
        }
    }
    
    private boolean checkAudioPermission() {
        return ContextCompat.checkSelfPermission(getContext(), Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;
    }
    
    private void requestAudioPermission() {
        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.RECORD_AUDIO}, PERMISSION_REQUEST_RECORD_AUDIO);
    }
    
    private void startListening() {
        try {
            if (speechRecognizer != null && speechRecognizerIntent != null) {
                speechRecognizer.startListening(speechRecognizerIntent);
            } else {
                initSpeechRecognizer();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void stopListening() {
        if (speechRecognizer != null) {
            speechRecognizer.stopListening();
            isListening = false;
            updateMicrophoneButton();
        }
    }
    
    private void updateMicrophoneButton() {
        if (microphoneButton != null) {
            if (isListening) {
                microphoneButton.setImageResource(R.drawable.ic_mic_on);
                microphoneButton.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.red));
            } else {
                microphoneButton.setImageResource(R.drawable.ic_mic);
                microphoneButton.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.primary_blue));
            }
        }
    }
    
    private String getErrorMessage(int errorCode) {
        switch (errorCode) {
            case SpeechRecognizer.ERROR_AUDIO:
                return "音频录制错误";
            case SpeechRecognizer.ERROR_CLIENT:
                return "客户端错误";
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                return "权限不足";
            case SpeechRecognizer.ERROR_NETWORK:
                return "网络错误";
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                return "网络超时";
            case SpeechRecognizer.ERROR_NO_MATCH:
                return "无法识别语音";
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                return "识别服务忙";
            case SpeechRecognizer.ERROR_SERVER:
                return "服务器错误";
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                return "语音输入超时";
            default:
                return "未知错误";
        }
    }
    
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_RECORD_AUDIO) {
            // 权限处理逻辑，无需显示提醒
        }
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (speechRecognizer != null) {
            speechRecognizer.destroy();
        }
    }
    
    // 打开微信小程序
    private void openWechatMiniprogram() {
        try {
            // 跳转到微信小程序界面
            Intent intent = new Intent(getActivity(), WechatMiniprogramActivity.class);
            startActivity(intent);
            Toast.makeText(getContext(), "正在进入微信小程序界面...", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(getContext(), "界面跳转失败，请重试", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

}