<template>
	<view class="page-container">
		<view class="container">
		<!-- 自定义导航栏 -->
		<view class="custom-navbar">
			<view class="navbar-content">
				<image class="back-icon" src="http://szzgurngv.hn-bkt.clouddn.com/icon/back.png" @click="goHome" mode="aspectFit"></image>
				<view class="navbar-center">
					<text class="navbar-title">AI智能助手</text>
					<view class="navbar-subtitle">
						<text class="subtitle-text">已接入DeepSeek-V3</text>
						<text class="subtitle-desc">实时问答服务</text>
					</view>
				</view>
				<view class="navbar-icon">
					<!-- <text class="icon-text">📊</text> -->
					<image class="icon-text" src="http://szzgurngv.hn-bkt.clouddn.com/icon/AI.png" mode="aspectFit"></image>
				</view>
			</view>
		</view>

			<!-- 头部区域 -->
			<view class="header-section fade-in-up" style="animation-delay: 0.1s;">
				<!-- AI助手状态 -->
				<view class="ai-status">
					<view class="status-card card-hover">
						<view class="ai-avatar floating" :class="{active: isListening}">
						<image class="avatar-icon" src="http://szzgurngv.hn-bkt.clouddn.com/icon/ai_agent.png" mode="aspectFit"></image>
						<view class="pulse-ring" v-if="isListening"></view>
					</view>
						<view class="status-info">
							<text class="ai-name">小智助手</text>
							<text class="ai-status-text">{{statusText}}</text>
						</view>
						<view class="status-indicator floating" :class="statusClass"></view>
					</view>
				</view>

				<!-- 快捷功能 -->
				<view class="quick-functions fade-in-up" style="animation-delay: 0.2s;">
					<view class="function-grid">
						<view class="function-item card-hover scale-in" style="animation-delay: 0.3s;" @click="quickQuery('实验室状态')">
						<image class="function-icon icon-bounce" src="http://szzgurngv.hn-bkt.clouddn.com/icon/ai_zhuangtai.png" mode="aspectFit"></image>
						<text class="function-text">实验室状态</text>
					</view>
					<view class="function-item card-hover scale-in" style="animation-delay: 0.4s;" @click="quickQuery('设备预约')">
						<image class="function-icon icon-bounce" src="http://szzgurngv.hn-bkt.clouddn.com/icon/ai_yuyue.png" mode="aspectFit"></image>
						<text class="function-text">设备预约</text>
					</view>
					<view class="function-item card-hover scale-in" style="animation-delay: 0.5s;" @click="quickQuery('安全指导')">
						<image class="function-icon icon-bounce" src="http://szzgurngv.hn-bkt.clouddn.com/icon/ai_zhidao.png" mode="aspectFit"></image>
						<text class="function-text">安全指导</text>
					</view>
					<view class="function-item card-hover scale-in" style="animation-delay: 0.6s;" @click="quickQuery('学习资源')">
						<image class="function-icon icon-bounce" src="http://szzgurngv.hn-bkt.clouddn.com/icon/ai_ziyuan.png" mode="aspectFit"></image>
						<text class="function-text">学习资源</text>
					</view>
					</view>
				</view>
			</view>

			<!-- 聊天区域 -->
			<view class="chat-container fade-in-up" style="animation-delay: 0.3s;">
				<scroll-view class="chat-messages" 
				scroll-y="true" 
				:scroll-top="scrollTop" 
				:scroll-into-view="scrollIntoView"
				scroll-with-animation="true">
				
				<!-- 欢迎消息 -->
				<view class="message-item ai-message" v-if="messages.length === 0">
					<view class="message-avatar">
					<image class="avatar-image" src="http://szzgurngv.hn-bkt.clouddn.com/icon/ai_agent_2.png" mode="aspectFit"></image>
				</view>
					<view class="message-content">
						<view class="message-bubble">
							<text class="message-text">你好！我是小智助手，可以帮你解答实验室相关问题、查询设备状态、提供安全指导等。有什么需要帮助的吗？</text>
						</view>
						<text class="message-time">{{getCurrentTime()}}</text>
					</view>
				</view>

				<!-- 聊天消息 -->
				<view class="message-item" 
					v-for="(message, index) in messages" 
					:key="index"
					:class="message.type + '-message'">
					
					<view class="message-avatar" v-if="message.type === 'ai'">
					<image class="avatar-image" src="http://szzgurngv.hn-bkt.clouddn.com/icon/ai_agent_2.png" mode="aspectFit"></image>
				</view>
					
					<view class="message-content">
						<view class="message-bubble">
							<text class="message-text">{{message.content}}</text>
							
							<!-- AI消息的操作按钮 -->
							<view class="message-actions" v-if="message.type === 'ai' && message.actions">
								<view class="action-button" 
									v-for="action in message.actions" 
									:key="action.id"
									@click="handleAction(action)">
									<text class="action-text">{{action.text}}</text>
								</view>
							</view>
						</view>
						<text class="message-time">{{message.time}}</text>
					</view>
					
					<view class="message-avatar" v-if="message.type === 'user'">
					<image class="avatar-image" src="http://szzgurngv.hn-bkt.clouddn.com/icon/user.png" mode="aspectFit"></image>
				</view>
				</view>

				<!-- 正在输入指示器 -->
				<view class="message-item ai-message" v-if="isTyping">
					<view class="message-avatar">
					<image class="avatar-image" src="http://szzgurngv.hn-bkt.clouddn.com/icon/ai_agent_2.png" mode="aspectFit"></image>
				</view>
					<view class="message-content">
						<view class="message-bubble typing">
							<view class="typing-indicator">
								<view class="dot"></view>
								<view class="dot"></view>
								<view class="dot"></view>
							</view>
						</view>
					</view>
				</view>
				
				<!-- 滚动锚点 -->
				<view id="scroll-anchor"></view>
				</scroll-view>
			</view>

			<!-- 输入区域 -->
			<view class="input-container fade-in-up" style="animation-delay: 0.4s;">
				<!-- 录音状态指示器 -->
				<view class="recording-indicator" v-if="isListening">
					<view class="recording-info">
						<image class="recording-icon floating" src="http://szzgurngv.hn-bkt.clouddn.com/icon/ai_logo.png" mode="aspectFit"></image>
						<text class="recording-text">正在录音...</text>
						<text class="recording-time">{{ formatRecordingTime(recordingTime) }}</text>
					</view>
					<view class="recording-wave">
						<view class="wave-bar" v-for="n in 5" :key="n" :style="{animationDelay: (n-1) * 0.1 + 's'}"></view>
					</view>
				</view>
				
				<view class="input-wrapper">
					<view class="voice-btn btn-hover" 
						:class="{active: isListening}" 
						@touchstart="startVoiceInput" 
						@touchend="stopVoiceInput">
						<image class="voice-icon" src="http://szzgurngv.hn-bkt.clouddn.com/icon/maikefeng.png" mode="aspectFit"></image>
					</view>
					
					<input class="text-input" 
						v-model="inputText" 
						placeholder="输入你的问题..." 
						@confirm="sendMessage"
						confirm-type="send" />
					
					<view class="send-btn btn-hover" 
						:class="{disabled: !inputText.trim()}" 
						@click="sendMessage">
						<image class="send-icon" src="http://szzgurngv.hn-bkt.clouddn.com/icon/ai_send.png" mode="aspectFit"></image>
					</view>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				inputText: '',
				messages: [],
				isTyping: false,
				isListening: false,
				scrollTop: 0,
				scrollIntoView: '',
				statusText: 'DeepSeek-V3 在线服务',
				statusClass: 'online',
				recorderManager: null,
				voiceRecognition: null,
				recordingTime: 0,
				recordingTimer: null,
				// 腾讯云语音识别配置
				tencentConfig: {
					secretId: 'YOUR_ALIYUN_ACCESS_KEY_ID',
					secretKey: 'YOUR_ALIYUN_ACCESS_KEY_SECRET',
					region: 'ap-guangzhou',
					service: 'asr',
					host: 'asr.tencentcloudapi.com',
					version: '2019-06-14'
				}
			}
		},
		methods: {
			quickQuery(type) {
				this.inputText = this.getQuickQueryText(type)
				this.sendMessage()
			},
			
			getQuickQueryText(type) {
				const queries = {
					'实验室状态': '请查询当前实验室的使用状态',
					'设备预约': '我想预约实验设备，有什么可用的吗？',
					'安全指导': '请提供实验室安全操作指导',
					'学习资源': '推荐一些相关的学习资源'
				}
				return queries[type] || type
			},
			
			sendMessage() {
				if (!this.inputText.trim()) return
				
				// 添加用户消息
				const userMessage = {
					type: 'user',
					content: this.inputText,
					time: this.getCurrentTime()
				}
				this.messages.push(userMessage)
				
				// 清空输入框
				const query = this.inputText
				this.inputText = ''
				
				// 滚动到底部
				this.scrollToBottom()
				
				// 显示正在输入
				this.isTyping = true
				this.scrollToBottom()
				
				// 调用AI回复
				this.generateAIResponse(query)
			},
			
			generateAIResponse(query) {
				this.isTyping = false
				
				// 调用DeepSeek-V3 API
				this.callDeepSeekAPI(query)
			},
			
			// 调用DeepSeek-V3 API
			async callDeepSeekAPI(userQuery) {
				const url = 'https://api.modelarts-maas.com/v1/chat/completions'
				const apiKey = 'y-RQvrKfiPPTHvzP65GAGlraAXDI3EeXDfWFJu4PFZ16295eP7jU5-_STpx4aR4KwMD-0hdQX7yJ-Vyy_WSW2A'
				
				const headers = {
					'Content-Type': 'application/json',
					'Authorization': `Bearer ${apiKey}`
				}
				
				const data = {
					model: 'DeepSeek-V3',
					messages: [
						{
							role: 'system',
							content: '你是一个智能实验室助手，专门为实验室管理和使用提供帮助。你可以协助用户查询实验室状态、预约设备、提供安全指导、推荐学习资源等。请用简洁、专业、友好的语气回答问题。重要：请不要使用markdown格式，直接用自然段落格式回答，不要使用#、*、-等markdown符号。'
						},
						{
							role: 'user',
							content: userQuery
						}
					],
					stream: true,
					temperature: 0.6
				}
				
				try {
					// 显示正在思考状态
					this.isTyping = true
					this.scrollToBottom()
					
					// 发送请求
					const response = await uni.request({
						url: url,
						method: 'POST',
						header: headers,
						data: data,
						timeout: 30000
					})
					
					this.isTyping = false
					
					if (response.statusCode === 200) {
						// 处理流式响应
						this.handleStreamResponse(response.data)
					} else {
						console.error('API请求失败:', response)
						this.handleAPIError('API请求失败，请稍后重试')
					}
					
				} catch (error) {
					console.error('DeepSeek API调用错误:', error)
					this.isTyping = false
					
					// 网络错误时使用本地回复
					this.handleAPIError('网络连接失败，使用本地智能回复')
					this.generateLocalResponse(userQuery)
				}
			},
			
			// 处理流式响应
			handleStreamResponse(responseData) {
				let accumulatedContent = ''
				
				try {
					// 如果是流式响应，需要解析每一行
					if (typeof responseData === 'string') {
						const lines = responseData.split('\n')
						
						for (const line of lines) {
							if (line.startsWith('data:')) {
								const dataContent = line.substring(5).trim()
								
								if (dataContent === '[DONE]') {
									break
								}
								
								try {
									const dataObj = JSON.parse(dataContent)
									
									if (dataObj.choices && dataObj.choices.length > 0) {
										const delta = dataObj.choices[0].delta || {}
										const content = delta.content || ''
										if (content) {
											accumulatedContent += content
										}
									}
								} catch (parseError) {
									console.warn('JSON解析错误:', parseError)
								}
							}
						}
					} else if (responseData.choices && responseData.choices.length > 0) {
						// 非流式响应
						accumulatedContent = responseData.choices[0].message?.content || ''
					}
					
					// 添加AI回复消息
					if (accumulatedContent.trim()) {
						const aiMessage = {
							type: 'ai',
							content: accumulatedContent.trim(),
							time: this.getCurrentTime(),
							actions: this.generateActions(accumulatedContent)
						}
						
						this.messages.push(aiMessage)
						this.scrollToBottom()
					} else {
						this.handleAPIError('未收到有效回复')
						this.generateLocalResponse(this.messages[this.messages.length - 1]?.content || '')
					}
					
				} catch (error) {
					console.error('处理响应数据错误:', error)
					this.handleAPIError('响应解析失败')
					this.generateLocalResponse(this.messages[this.messages.length - 1]?.content || '')
				}
			},
			
			// 根据回复内容生成相关操作按钮
			generateActions(content) {
				// 不生成任何操作按钮
				return []
			},
			
			// 处理API错误
			handleAPIError(errorMsg) {
				console.warn('API错误:', errorMsg)
				uni.showToast({
					title: errorMsg,
					icon: 'none',
					duration: 2000
				})
			},
			
			// 生成本地回复（作为备用方案）
			generateLocalResponse(query) {
				let response = this.getAIResponse(query)
				
				const aiMessage = {
					type: 'ai',
					content: response.content,
					time: this.getCurrentTime(),
					actions: response.actions
				}
				
				this.messages.push(aiMessage)
				this.scrollToBottom()
			},
			
			getAIResponse(query) {
				const lowerQuery = query.toLowerCase()
				
				if (lowerQuery.includes('实验室状态') || lowerQuery.includes('状态')) {
					return {
						content: '当前实验室状态如下：化学实验室A使用中（3/4人），生物实验室B空闲，物理实验室C维护中，工程实验室D使用中（2/6人）。如果您需要预约特定实验室，可以通过设备预约页面进行操作。',
						actions: []
					}
				} else if (lowerQuery.includes('预约') || lowerQuery.includes('设备')) {
					return {
						content: '以下设备当前可预约：3D打印机2台（明天9:00-17:00可用），显微镜3台（今天下午可用），示波器1台（明天上午可用），离心机2台（随时可用）。您可以根据需要选择合适的时间段进行预约。',
						actions: []
					}
				} else if (lowerQuery.includes('安全') || lowerQuery.includes('指导')) {
					return {
						content: '实验室安全指导：必须佩戴防护眼镜，使用化学试剂时戴手套，穿着长袖实验服，禁止在实验室饮食，注意用电和用火安全，熟悉紧急疏散路线。如果您需要特定实验的安全指导，请告诉我具体的实验类型。',
						actions: []
					}
				} else if (lowerQuery.includes('学习') || lowerQuery.includes('资源')) {
					return {
						content: '为您推荐以下学习资源：实验操作视频教程，实验室管理手册，在线安全培训课程，实验报告模板，相关学术论文数据库。这些资源可以帮助您更好地进行实验操作和学习。',
						actions: []
					}
				} else {
					return {
						content: '我理解您的问题。作为智能实验室助手，我可以帮您查询实验室和设备状态，协助预约实验室和设备，提供安全操作指导，推荐学习资源，解答实验相关问题。请告诉我您具体需要什么帮助？',
						actions: []
					}
				}
			},
			
			handleAction(action) {
				switch(action.id) {
					// 实验室状态相关
					case 'view_lab_status':
					case 'lab_status':
						uni.switchTab({
							url: '/pages/index/index'
						})
						break
					
					// 设备预约相关
					case 'book_device':
					case 'make_booking':
					case 'book_lab':
					case 'book_3d':
					case 'book_microscope':
						uni.navigateTo({
							url: '/pages/device/booking'
						})
						break
					
					// 设备管理相关
					case 'view_detail':
					case 'view_all_devices':
						uni.switchTab({
							url: '/pages/device/device'
						})
						break
					
					// 安全指导相关
					case 'safety_guide':
					case 'chemical_safety':
					case 'bio_safety':
					case 'emergency_guide':
						uni.switchTab({
							url: '/pages/safety/safety'
						})
						break
					
					default:
						uni.showToast({
							title: '功能开发中',
							icon: 'none'
						})
				}
			},
			
			// 返回首页
			goHome() {
				uni.switchTab({
					url: '/pages/index/index'
				})
			},
			
			startVoiceInput() {
				console.log('开始语音输入')
				this.isListening = true
				this.statusText = '正在听取您的问题...'
				this.recordingTime = 0
				
				// 检查是否在微信小程序环境
				if (typeof wx !== 'undefined' && wx.startSpeechRecognition) {
					// 使用微信原生语音识别
					this.startWechatNativeSpeechRecognition()
				} else {
					// 降级到录音模式
					this.startRecording()
				}
			},
			
			stopVoiceInput() {
				console.log('停止语音输入')
				
				// 检查是否在微信小程序环境
				if (typeof wx !== 'undefined' && wx.stopSpeechRecognition) {
					// 停止微信原生语音识别
					wx.stopSpeechRecognition()
				} else if (this.recorderManager && this.isListening) {
					// 停止录音
					this.recorderManager.stop()
				} else {
					// 如果没有正在录音，直接重置状态
					this.stopRecordingTimer()
					this.recordingTime = 0
				}
				
				this.isListening = false
				this.statusText = '在线，随时为您服务'
			},
			
			setupRecorderEvents() {
				// 录音开始事件
				this.recorderManager.onStart(() => {
					console.log('录音开始')
				})
				
				// 录音结束事件
			this.recorderManager.onStop((res) => {
				console.log('录音结束', res)
				// 保存录音时长，然后停止计时器
				const recordingDuration = this.recordingTime
				console.log('保存的录音时长:', recordingDuration)
				this.stopRecordingTimer()
				this.handleVoiceRecognition(res.tempFilePath, recordingDuration)
			})
				
				// 录音错误事件
				this.recorderManager.onError((err) => {
					console.error('录音错误', err)
					this.isListening = false
					this.statusText = '在线，随时为您服务'
					this.stopRecordingTimer()
					this.recordingTime = 0 // 重置录音时长
					uni.showToast({
						title: '录音失败，请重试',
						icon: 'none'
					})
				})
			},
			
			startRecordingTimer() {
				this.recordingTime = 0
				this.recordingTimer = setInterval(() => {
					this.recordingTime++
					if (this.recordingTime >= 60) {
						// 超过60秒自动停止
						this.stopVoiceInput()
						uni.showToast({
							title: '录音时间过长，已自动停止',
							icon: 'none'
						})
					}
				}, 1000)
			},
			
			stopRecordingTimer() {
				if (this.recordingTimer) {
					clearInterval(this.recordingTimer)
					this.recordingTimer = null
				}
				// 不要立即重置recordingTime，让调用方决定何时重置
			},
			
			handleVoiceRecognition(filePath, duration) {
				// 重置录音时长显示
				this.recordingTime = 0
				
				console.log('开始处理语音识别，文件路径:', filePath, '录音时长:', duration, '秒')
				
				// 检查录音文件是否存在
				if (!filePath) {
					uni.showToast({
						title: '录音文件无效，请重新录音',
						icon: 'none'
					})
					return
				}
				
				// 降低最小录音时长要求
				if (duration === undefined || duration === null || duration < 0.5) {
					console.log('录音时长不足，duration:', duration)
					uni.showToast({
						title: `录音时间太短(${duration || 0}秒)，请长按录音`,
						icon: 'none',
						duration: 2000
					})
					return
				}
				
				// 显示识别中状态
				uni.showLoading({
					title: '正在识别...'
				})
				
				// 优先级：微信同声传译 > 微信小程序语音识别 > uni-app语音识别 > 腾讯云ASR（需服务器代理）
			this.tryWechatTranslateVoice(filePath)
				.then(result => {
					console.log('微信同声传译识别成功:', result)
					this.inputText = result
					uni.hideLoading()
					uni.showToast({
						title: '识别完成',
						icon: 'success'
					})
				})
				.catch(error => {
					console.log('微信同声传译失败，尝试其他方式:', error)
					// 如果微信同声传译失败，尝试微信小程序语音识别
					return this.tryWechatVoiceRecognition(filePath)
				})
				.then(result => {
					if (result) {
						console.log('微信小程序语音识别成功:', result)
						this.inputText = result
						uni.hideLoading()
						uni.showToast({
							title: '识别完成',
							icon: 'success'
						})
					}
				})
				.catch(error => {
					console.log('微信小程序语音识别失败，尝试uni识别:', error)
					// 如果微信API失败，尝试使用uni-app的语音识别
					return this.tryUniVoiceRecognition(filePath)
				})
				.then(result => {
					if (result) {
						console.log('uni-app语音识别成功:', result)
						this.inputText = result
						uni.hideLoading()
						uni.showToast({
							title: '识别完成',
							icon: 'success'
						})
					}
				})
				.catch(error => {
					console.log('uni-app识别失败，尝试腾讯云ASR:', error)
					// 最后尝试腾讯云ASR（需要服务器端代理）
					return this.tryTencentASR(filePath)
				})
				.catch(error => {
					console.log('所有识别方式失败，使用模拟数据:', error)
					// 如果都失败，使用模拟数据
					this.useSimulatedVoiceRecognition()
					uni.hideLoading()
				})
			},
			
			// 启动微信原生语音识别
			startWechatNativeSpeechRecognition() {
				console.log('启动微信原生语音识别')
				
				uni.showLoading({
					title: '正在识别...'
				})
				
				wx.startSpeechRecognition({
					lang: 'zh_CN',
					serviceType: 'iat',
					success: (res) => {
						console.log('微信原生语音识别成功:', res)
						this.isListening = false
						this.statusText = '在线，随时为您服务'
						
						if (res.result && res.result.trim()) {
							this.inputText = res.result.trim()
							uni.hideLoading()
							uni.showToast({
								title: '识别完成',
								icon: 'success'
							})
						} else {
							uni.hideLoading()
							uni.showToast({
								title: '未识别到内容，请重试',
								icon: 'none'
							})
						}
					},
					fail: (err) => {
						console.error('微信原生语音识别失败:', err)
						this.isListening = false
						this.statusText = '在线，随时为您服务'
						uni.hideLoading()
						
						// 降级到录音模式
						uni.showToast({
							title: '语音识别不可用，请使用录音模式',
							icon: 'none'
						})
					}
				})
			},
			
			// 启动录音模式（降级方案）
			startRecording() {
				console.log('启动录音模式')
				
				// 初始化录音管理器
				if (!this.recorderManager) {
					this.recorderManager = uni.getRecorderManager()
					this.setupRecorderEvents()
				}
				
				// 开始录音
				this.recorderManager.start({
					duration: 60000, // 最长录音60秒
					sampleRate: 16000,
					numberOfChannels: 1,
					encodeBitRate: 48000,
					format: 'wav'
				})
				
				// 开始计时
				this.startRecordingTimer()
				
				uni.showToast({
					title: '正在录音...',
					icon: 'none'
				})
			},
			
			// 尝试使用微信同声传译插件进行语音识别
			tryWechatTranslateVoice(filePath) {
				return new Promise((resolve, reject) => {
					// 检查是否在微信小程序环境
					if (typeof wx === 'undefined') {
						reject(new Error('不在微信小程序环境中'))
						return
					}
					
					console.log('尝试微信同声传译语音识别，文件路径:', filePath)
					
					// 使用微信同声传译插件
					wx.translateVoice({
						source: 'audio', // 音频来源
						filePath: filePath, // 音频文件路径
						lang: 'zh_CN', // 源语言
						targetLang: 'zh_CN', // 目标语言（同语言实现语音转文字）
						success: (res) => {
							console.log('微信同声传译成功:', res)
							if (res.translateResult && res.translateResult.trim()) {
								resolve(res.translateResult.trim())
							} else if (res.result && res.result.trim()) {
								// 有些版本可能返回result字段
								resolve(res.result.trim())
							} else {
								reject(new Error('未识别到内容'))
							}
						},
						fail: (err) => {
							console.error('微信同声传译失败:', err)
							reject(err)
						}
					})
				})
			},
			
			// 尝试使用微信小程序语音识别
			tryWechatVoiceRecognition(filePath) {
				return new Promise((resolve, reject) => {
					if (typeof wx !== 'undefined' && wx.createVoiceRecognition) {
						const voiceRecognition = wx.createVoiceRecognition()
						
						// 设置超时处理
						const timeout = setTimeout(() => {
							uni.hideLoading()
							uni.showToast({
								title: '识别超时，请重试',
								icon: 'none'
							})
							reject(new Error('识别超时'))
						}, 15000) // 15秒超时
						
						voiceRecognition.onRecognize((res) => {
							console.log('微信识别中间结果', res)
							if (res.result) {
								this.inputText = res.result
							}
						})
						
						voiceRecognition.onComplete((res) => {
							clearTimeout(timeout)
							console.log('微信识别完成', res)
							uni.hideLoading()
							
							if (res.result && res.result.trim()) {
								this.inputText = res.result.trim()
								uni.showToast({
									title: '识别完成',
									icon: 'success'
								})
								resolve(res.result.trim())
							} else {
								uni.showToast({
									title: '未识别到内容，请重新录音',
									icon: 'none'
								})
								reject(new Error('未识别到内容'))
							}
						})
						
						voiceRecognition.onError((err) => {
							clearTimeout(timeout)
							console.error('微信识别错误', err)
							uni.hideLoading()
							
							let errorMsg = '识别失败，请重试'
							if (err.errCode === 10003) {
								errorMsg = '网络异常，请检查网络连接'
							} else if (err.errCode === 10007) {
								errorMsg = '录音文件无效，请重新录音'
							} else if (err.errCode === 10008) {
								errorMsg = '录音时间过短，请重新录音'
							}
							
							uni.showToast({
								title: errorMsg,
								icon: 'none',
								duration: 2000
							})
							reject(err)
						})
						
						// 开始识别
						try {
							voiceRecognition.start({
								filePath: filePath,
								lang: 'zh_CN'
							})
						} catch (error) {
							clearTimeout(timeout)
							console.error('启动微信识别失败', error)
							uni.hideLoading()
							reject(error)
						}
					} else {
						reject(new Error('微信语音识别API不可用'))
					}
				})
			},
			
			// 尝试使用uni-app语音识别
			tryUniVoiceRecognition(filePath) {
				return new Promise((resolve, reject) => {
					// 检查是否支持uni.startSpeechRecognition
					if (uni.startSpeechRecognition) {
						uni.startSpeechRecognition({
							lang: 'zh_CN',
							success: (res) => {
								console.log('uni语音识别成功', res)
								uni.hideLoading()
								if (res.result && res.result.trim()) {
									this.inputText = res.result.trim()
									uni.showToast({
										title: '识别完成',
										icon: 'success'
									})
									resolve(res.result.trim())
								} else {
									reject(new Error('未识别到内容'))
								}
							},
							fail: (err) => {
								console.error('uni语音识别失败', err)
								uni.hideLoading()
								reject(err)
							}
						})
					} else {
						reject(new Error('uni语音识别API不可用'))
					}
				})
			},
			
			// 腾讯云语音识别API
			async tryTencentASR(filePath) {
				return new Promise(async (resolve, reject) => {
					try {
						console.log('开始腾讯云语音识别，文件路径:', filePath)
						
						// 读取音频文件并转换为base64
						const audioBase64 = await this.convertAudioToBase64(filePath)
						if (!audioBase64) {
							throw new Error('音频文件转换失败')
						}
						
						console.log('音频文件大小:', audioBase64.length, 'bytes')
						
						// 检查文件大小（腾讯云要求<5MB）
						if (audioBase64.length > 5 * 1024 * 1024) {
							throw new Error('音频文件超过5MB限制')
						}
						
						// 使用一句话识别API
						const result = await this.callTencentSentenceRecognition(audioBase64)
						resolve(result)
						
					} catch (error) {
						console.error('腾讯云ASR错误:', error)
						reject(error)
					}
				})
			},
			
			// 将音频文件转换为base64
			convertAudioToBase64(filePath) {
				return new Promise((resolve, reject) => {
					uni.getFileSystemManager().readFile({
						filePath: filePath,
						encoding: 'base64',
						success: (res) => {
							resolve(res.data)
						},
						fail: (err) => {
							console.error('读取音频文件失败:', err)
							reject(err)
						}
					})
				})
			},
			
			// 调用腾讯云一句话识别API（暂时跳过，需要服务器端代理）
			async callTencentSentenceRecognition(audioBase64) {
				return new Promise((resolve, reject) => {
					// 腾讯云API需要复杂的签名认证，小程序环境无法直接调用
					// 需要通过服务器端代理来实现
					console.log('腾讯云ASR需要服务器端代理，暂时跳过')
					reject(new Error('腾讯云ASR需要服务器端代理'))
				})
			},
			
			// 使用模拟语音识别
			useSimulatedVoiceRecognition() {
				uni.hideLoading()
				const simulatedTexts = [
					'请查询当前实验室状态',
					'我想预约实验设备',
					'请提供安全操作指导',
					'推荐一些学习资源',
					'实验室有哪些设备可以使用',
					'如何正确使用显微镜',
					'实验室安全注意事项有哪些'
				]
				const randomText = simulatedTexts[Math.floor(Math.random() * simulatedTexts.length)]
				
				setTimeout(() => {
					this.inputText = randomText
					uni.showToast({
						title: '识别完成（模拟）',
						icon: 'success',
						duration: 1500
					})
				}, 1000)
			},
			
			scrollToBottom() {
				this.$nextTick(() => {
					// 使用scroll-into-view滚动到锚点
					this.scrollIntoView = 'scroll-anchor'
					
					// 重置scrollIntoView，避免重复触发
					setTimeout(() => {
						this.scrollIntoView = ''
					}, 100)
				})
			},
			
			getCurrentTime() {
				const now = new Date()
				return `${now.getHours().toString().padStart(2, '0')}:${now.getMinutes().toString().padStart(2, '0')}`
			},
			
			formatRecordingTime(seconds) {
				const mins = Math.floor(seconds / 60)
				const secs = seconds % 60
				return `${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`
			},
			
			goHome() {
				uni.switchTab({
					url: '/pages/index/index'
				})
			}
		},
		onLoad() {
			// 初始化AI助手
			console.log('AI助手已启动')
			
			// 请求录音权限
			this.requestRecordPermission()
		},
		
		onUnload() {
			// 清理资源
			this.stopRecordingTimer()
			if (this.recorderManager) {
				this.recorderManager.stop()
			}
		},
		
		requestRecordPermission() {
			// 检查录音权限
			uni.getSetting({
				success: (res) => {
					if (!res.authSetting['scope.record']) {
						// 请求录音权限
						uni.authorize({
							scope: 'scope.record',
							success: () => {
								console.log('录音权限获取成功')
							},
							fail: () => {
								console.log('录音权限获取失败')
								uni.showModal({
									title: '权限提示',
									content: '需要录音权限才能使用语音输入功能，请在设置中开启',
									showCancel: false
								})
							}
						})
					} else {
						console.log('录音权限已获取')
					}
				}
			})
		}
	}
</script>

<style scoped>
	.page-container {
		position: fixed;
		top: 0;
		left: 0;
		right: 0;
		bottom: 0;
		background: linear-gradient(135deg, #2196F3 0%, #1976D2 100%);
		z-index: 1;
	}

	.container {
		display: flex;
		flex-direction: column;
		height: 100vh;
		overflow: hidden;
		padding-bottom: env(safe-area-inset-bottom);
	}

	.header-section {
		flex-shrink: 0;
	}

	.custom-navbar {
		padding: 40rpx 30rpx;
		padding-top: 80rpx;
		background: linear-gradient(135deg, #2196F3 0%, #1976D2 100%);
		box-shadow: 0 4rpx 20rpx rgba(33, 150, 243, 0.3);
		min-height: 120rpx;
	}

	.navbar-content {
		display: flex;
		justify-content: space-between;
		align-items: center;
		position: relative;
	}

	.back-icon {
		width: 60rpx;
		height: 60rpx;
	}

	.navbar-center {
		display: flex;
		flex-direction: column;
		align-items: center;
		gap: 5rpx;
		position: absolute;
		left: 50%;
		transform: translateX(-50%);
	}

	.navbar-title {
		margin-top: 58rpx;
		font-size: 48rpx;
		font-weight: bold;
		color: white;
	}

	.navbar-subtitle {
		display: flex;
		flex-direction: column;
		align-items: center;
		gap: 2rpx;
	}

	.subtitle-text {
		font-size: 24rpx;
		color: rgba(255, 255, 255, 0.9);
	}

	.subtitle-desc {
		font-size: 20rpx;
		color: rgba(255, 255, 255, 0.7);
	}

	.navbar-icon {
		width: 90rpx;
		height: 90rpx;
		display: flex;
		align-items: center;
		justify-content: center;
		background: rgba(255, 255, 255, 0.2);
		border-radius: 50%;
	}

	.icon-text {
		font-size: 32rpx;
	}

	.navbar-actions {
		display: flex;
		gap: 15rpx;
	}

	.action-btn {
		width: 70rpx;
		height: 70rpx;
		display: flex;
		align-items: center;
		justify-content: center;
		background: rgba(255, 255, 255, 0.2);
		border-radius: 50%;
		transition: all 0.3s;
	}

	.action-btn:active {
		transform: scale(0.9);
		background: rgba(255, 255, 255, 0.3);
	}

	.action-icon {
		font-size: 32rpx;
		color: white;
	}

	.ai-status {
		padding: 30rpx 20rpx;
	}

	.status-card {
		display: flex;
		align-items: center;
		gap: 25rpx;
		padding: 30rpx;
		background: #f3811c;
		border-radius: 25rpx;
		backdrop-filter: blur(10rpx);
	}

	.ai-avatar {
		width: 100rpx;
		height: 100rpx;
		display: flex;
		align-items: center;
		justify-content: center;
		background: rgba(255, 255, 255, 0.2);
		border-radius: 50%;
		position: relative;
		transition: all 0.3s;
	}

	.ai-avatar.active {
		animation: pulse 2s infinite;
	}

	.avatar-icon {
		font-size: 48rpx;
		border-radius: 80rpx;
	}

	.pulse-ring {
		position: absolute;
		top: -10rpx;
		left: -10rpx;
		right: -10rpx;
		bottom: -10rpx;
		border: 4rpx solid rgba(255, 255, 255, 0.3);
		border-radius: 50%;
		animation: pulse-ring 2s infinite;
	}

	@keyframes pulse {
		0%, 100% { transform: scale(1); }
		50% { transform: scale(1.1); }
	}

	@keyframes pulse-ring {
		0% { transform: scale(1); opacity: 1; }
		100% { transform: scale(1.3); opacity: 0; }
	}

	.status-info {
		flex: 1;
		display: flex;
		flex-direction: column;
		gap: 8rpx;
	}

	.ai-name {
		font-size: 32rpx;
		font-weight: bold;
		color: white;
	}

	.ai-status-text {
		font-size: 24rpx;
		color: #e3f2fd;
	}

	.status-indicator {
		width: 20rpx;
		height: 20rpx;
		border-radius: 50%;
		background: #4CAF50;
	}

	.status-indicator.online {
		background: #4CAF50;
		box-shadow: 0 0 20rpx rgba(76, 175, 80, 0.5);
	}

	.status-indicator.busy {
		background: #FF9800;
		box-shadow: 0 0 20rpx rgba(255, 152, 0, 0.5);
	}

	.status-indicator.offline {
		background: #f44336;
		box-shadow: 0 0 20rpx rgba(244, 67, 54, 0.5);
	}

	.quick-functions {
		padding: 0 20rpx 20rpx;
	}

	.function-grid {
		display: grid;
		grid-template-columns: repeat(4, 1fr);
		gap: 20rpx;
	}

	.function-item {
		padding: 25rpx 15rpx;
		display: flex;
		flex-direction: column;
		align-items: center;
		gap: 12rpx;
		background: rgba(255, 255, 255, 0.15);
		border-radius: 20rpx;
		backdrop-filter: blur(10rpx);
		transition: all 0.3s;
	}

	.function-item:active {
		transform: scale(0.95);
		background: rgba(255, 255, 255, 0.25);
	}

	.function-icon {
		width: 60rpx;
		height: 60rpx;
	}

	.function-text {
		font-size: 22rpx;
		color: white;
		text-align: center;
	}

	.chat-container {
		flex: 1;
		background: white;
		border-radius: 30rpx 30rpx 0 0;
		margin-top: 20rpx;
		display: flex;
		flex-direction: column;
		min-height: 0;
		position: relative;
		overflow: hidden;
		margin-bottom: 0;
	}

	.chat-messages {
		flex: 1;
		padding: 30rpx 20rpx;
		padding-bottom: 20rpx;
		height: 100%;
		box-sizing: border-box;
	}

	.message-item {
		display: flex;
		gap: 20rpx;
		margin-bottom: 30rpx;
		align-items: flex-start;
	}

	.ai-message {
		justify-content: flex-start;
	}

	.user-message {
		flex-direction: row;
		justify-content: flex-end;
	}

	.message-avatar {
		width: 80rpx;
		height: 80rpx;
		display: flex;
		align-items: center;
		justify-content: center;
		border-radius: 50%;
		background: #ffffff;
		flex-shrink: 0;
	}

	.user-message .message-avatar {
		background: #2979FF;
		color: white;
	}

	.avatar-image {
		width: 90rpx;
		height: 90rpx;
	}

	.message-content {
		flex: 1;
		display: flex;
		flex-direction: column;
		gap: 8rpx;
	}

	.user-message .message-content {
		align-items: flex-end;
		order: 1;
	}

	.user-message .message-avatar {
		order: 2;
		margin-left: 20rpx;
		margin-right: 0;
	}

	.message-bubble {
		padding: 20rpx 25rpx;
		border-radius: 25rpx;
		background: #f0f0f0;
		max-width: 80%;
		word-wrap: break-word;
	}

	.user-message .message-bubble {
		background: #2979FF;
		color: white;
		border-radius: 25rpx 25rpx 8rpx 25rpx;
	}

	.ai-message .message-bubble {
		border-radius: 25rpx 25rpx 25rpx 8rpx;
	}

	.message-bubble.typing {
		padding: 25rpx;
	}

	.message-text {
		font-size: 28rpx;
		line-height: 1.5;
		white-space: pre-line;
	}

	.message-actions {
		display: flex;
		flex-wrap: wrap;
		gap: 15rpx;
		margin-top: 20rpx;
	}

	.action-button {
		padding: 12rpx 20rpx;
		background: #e3f2fd;
		color: #2979FF;
		border-radius: 20rpx;
		font-size: 24rpx;
		transition: all 0.3s;
	}

	.action-button:active {
		background: #2979FF;
		color: white;
	}

	.action-text {
		font-size: 24rpx;
	}

	.message-time {
		font-size: 22rpx;
		color: #999;
		margin-top: 8rpx;
	}

	.user-message .message-time {
		text-align: right;
	}

	.typing-indicator {
		display: flex;
		gap: 8rpx;
		align-items: center;
	}

	.dot {
		width: 12rpx;
		height: 12rpx;
		background: #ccc;
		border-radius: 50%;
		animation: typing 1.4s infinite;
	}

	.dot:nth-child(2) {
		animation-delay: 0.2s;
	}

	.dot:nth-child(3) {
		animation-delay: 0.4s;
	}

	@keyframes typing {
		0%, 60%, 100% {
			transform: translateY(0);
			opacity: 0.4;
		}
		30% {
			transform: translateY(-10rpx);
			opacity: 1;
		}
	}

	.input-container {
		padding: 20rpx;
		padding-bottom: 20rpx;
		background: white;
		border-top: 1rpx solid #f0f0f0;
		flex-shrink: 0;
		position: relative;
		z-index: 10;
		min-height: 120rpx;
	}

	.recording-indicator {
		padding: 20rpx;
		margin-bottom: 15rpx;
		background: linear-gradient(135deg, #ff6b6b, #ee5a52);
		border-radius: 20rpx;
		display: flex;
		justify-content: space-between;
		align-items: center;
		animation: recording-glow 2s infinite;
	}

	@keyframes recording-glow {
		0%, 100% {
			box-shadow: 0 0 10rpx rgba(255, 107, 107, 0.3);
		}
		50% {
			box-shadow: 0 0 20rpx rgba(255, 107, 107, 0.6);
		}
	}

	.recording-info {
		display: flex;
		align-items: center;
		gap: 15rpx;
	}

	.recording-icon {
		width: 32rpx;
		height: 32rpx;
		animation: recording-bounce 1s infinite;
	}

	@keyframes recording-bounce {
		0%, 100% {
			transform: scale(1);
		}
		50% {
			transform: scale(1.2);
		}
	}

	.recording-text {
		color: white;
		font-size: 28rpx;
		font-weight: bold;
	}

	.recording-time {
		color: rgba(255, 255, 255, 0.9);
		font-size: 24rpx;
		font-family: 'Courier New', monospace;
		background: rgba(255, 255, 255, 0.2);
		padding: 8rpx 15rpx;
		border-radius: 15rpx;
	}

	.recording-wave {
		display: flex;
		align-items: center;
		gap: 6rpx;
	}

	.wave-bar {
		width: 6rpx;
		height: 20rpx;
		background: rgba(255, 255, 255, 0.8);
		border-radius: 3rpx;
		animation: wave-animation 1.2s infinite ease-in-out;
	}

	@keyframes wave-animation {
		0%, 40%, 100% {
			transform: scaleY(0.4);
		}
		20% {
			transform: scaleY(1);
		}
	}

	.input-wrapper {
		display: flex;
		align-items: center;
		gap: 15rpx;
		padding: 15rpx;
		background: #f8f9fa;
		border-radius: 30rpx;
		min-height: 100rpx;
		box-sizing: border-box;
	}

	.voice-btn {
		width: 60rpx;
		height: 60rpx;
		display: flex;
		align-items: center;
		justify-content: center;
		background: #2979FF;
		border-radius: 50%;
		transition: all 0.3s;
		flex-shrink: 0;
		position: relative;
		overflow: hidden;
	}

	.voice-btn:active {
		transform: scale(0.95);
	}

	.voice-btn.active {
		background: #f44336;
		animation: recording-pulse 1.5s infinite;
		box-shadow: 0 0 0 0 rgba(244, 67, 54, 0.7);
	}

	.voice-btn.active::before {
		content: '';
		position: absolute;
		top: -10rpx;
		left: -10rpx;
		right: -10rpx;
		bottom: -10rpx;
		border: 3rpx solid rgba(244, 67, 54, 0.3);
		border-radius: 50%;
		animation: recording-ring 1.5s infinite;
	}

	@keyframes recording-pulse {
		0% {
			transform: scale(1);
			box-shadow: 0 0 0 0 rgba(244, 67, 54, 0.7);
		}
		70% {
			transform: scale(1.05);
			box-shadow: 0 0 0 10rpx rgba(244, 67, 54, 0);
		}
		100% {
			transform: scale(1);
			box-shadow: 0 0 0 0 rgba(244, 67, 54, 0);
		}
	}

	@keyframes recording-ring {
		0% {
			transform: scale(1);
			opacity: 1;
		}
		100% {
			transform: scale(1.4);
			opacity: 0;
		}
	}

	.voice-icon {
		width: 28rpx;
		height: 28rpx;
		z-index: 1;
		position: relative;
	}

	.text-input {
		flex: 1;
		padding: 0 25rpx;
		background: white;
		border-radius: 25rpx;
		border: none;
		font-size: 28rpx;
		outline: none;
		box-sizing: border-box;
		height: 70rpx;
		line-height: 70rpx;
		text-align: center;
		width: 100%;
		overflow: visible;
		white-space: nowrap;
		text-overflow: ellipsis;
	}

	.send-btn {
		width: 60rpx;
		height: 60rpx;
		display: flex;
		align-items: center;
		justify-content: center;
		border-radius: 50%;
		transition: all 0.3s;
		flex-shrink: 0;
	}

	.send-btn.disabled {
		pointer-events: none;
	}

	.send-icon {
		width: 60rpx;
		height: 60rpx;
	}

	.history-popup {
		background: white;
		border-radius: 30rpx 30rpx 0 0;
		max-height: 80vh;
		display: flex;
		flex-direction: column;
	}

	.settings-popup {
		background: white;
		border-radius: 20rpx;
		width: 80vw;
		max-width: 600rpx;
	}

	.popup-header {
		display: flex;
		justify-content: space-between;
		align-items: center;
		padding: 30rpx;
		border-bottom: 1rpx solid #f0f0f0;
	}

	.popup-title {
		font-size: 32rpx;
		font-weight: bold;
		color: #333;
	}

	.close-btn {
		width: 60rpx;
		height: 60rpx;
		display: flex;
		align-items: center;
		justify-content: center;
		background: #f0f0f0;
		border-radius: 50%;
	}

	.close-icon {
		font-size: 24rpx;
		color: #666;
	}

	.history-list {
		flex: 1;
		padding: 20rpx;
		max-height: 60vh;
	}

	.history-item {
		display: flex;
		align-items: center;
		padding: 25rpx;
		margin-bottom: 15rpx;
		background: #f8f9fa;
		border-radius: 15rpx;
		transition: all 0.3s;
	}

	.history-item:active {
		background: #e3f2fd;
	}

	.history-content {
		flex: 1;
		display: flex;
		flex-direction: column;
		gap: 8rpx;
	}

	.history-title {
		font-size: 28rpx;
		font-weight: bold;
		color: #333;
	}

	.history-preview {
		font-size: 24rpx;
		color: #666;
		overflow: hidden;
		text-overflow: ellipsis;
		white-space: nowrap;
	}

	.history-time {
		font-size: 22rpx;
		color: #999;
	}

	.delete-btn {
		width: 60rpx;
		height: 60rpx;
		display: flex;
		align-items: center;
		justify-content: center;
		background: #ffebee;
		border-radius: 50%;
		margin-left: 15rpx;
	}

	.delete-icon {
		font-size: 24rpx;
		color: #f44336;
	}

	.settings-content {
		padding: 30rpx;
	}

	.setting-item {
		display: flex;
		justify-content: space-between;
		align-items: center;
		padding: 25rpx 0;
		border-bottom: 1rpx solid #f0f0f0;
	}

	.setting-item:last-child {
		border-bottom: none;
	}

	.setting-label {
		font-size: 28rpx;
		color: #333;
	}

	.settings-actions {
		padding: 30rpx;
		display: flex;
		gap: 20rpx;
		border-top: 1rpx solid #f0f0f0;
	}

	.clear-btn,
	.reset-btn {
		flex: 1;
		padding: 25rpx;
		border-radius: 15rpx;
		font-size: 26rpx;
		border: none;
	}

	.clear-btn {
		background: #ffebee;
		color: #f44336;
	}

	.reset-btn {
		background: #e3f2fd;
		color: #2979FF;
	}

	/* 优化的动画效果 - 减少卡顿 */
	@keyframes fadeInDown {
		from {
			opacity: 0;
			transform: translate3d(0, -20px, 0);
		}
		to {
			opacity: 1;
			transform: translate3d(0, 0, 0);
		}
	}

	@keyframes fadeInUp {
		from {
			opacity: 0;
			transform: translate3d(0, 20px, 0);
		}
		to {
			opacity: 1;
			transform: translate3d(0, 0, 0);
		}
	}

	@keyframes scaleIn {
		from {
			opacity: 0;
			transform: scale3d(0.9, 0.9, 1);
		}
		to {
			opacity: 1;
			transform: scale3d(1, 1, 1);
		}
	}

	@keyframes floating {
		0%, 100% {
			transform: translate3d(0, 0, 0);
		}
		50% {
			transform: translate3d(0, -6px, 0);
		}
	}

	@keyframes iconBounce {
		0%, 20%, 50%, 80%, 100% {
			transform: translate3d(0, 0, 0);
		}
		40% {
			transform: translate3d(0, -8px, 0);
		}
		60% {
			transform: translate3d(0, -4px, 0);
		}
	}

	.fade-in-down {
		animation: fadeInDown 0.4s ease-out;
		will-change: transform, opacity;
	}

	.fade-in-up {
		animation: fadeInUp 0.4s ease-out;
		will-change: transform, opacity;
	}

	.scale-in {
		animation: scaleIn 0.3s ease-out;
		will-change: transform, opacity;
	}

	.floating {
		animation: floating 2.5s ease-in-out infinite;
		will-change: transform;
	}

	.icon-bounce {
		animation: iconBounce 1.8s infinite;
		will-change: transform;
	}

	.card-hover {
		transition: transform 0.2s ease, box-shadow 0.2s ease;
		will-change: transform;
	}

	.card-hover:hover {
		transform: translate3d(0, -1px, 0);
		box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
	}

	.card-hover:active {
		transform: translate3d(0, 0, 0);
		box-shadow: 0 1px 3px rgba(0, 0, 0, 0.08);
	}

	.btn-hover {
		transition: transform 0.15s ease;
		will-change: transform;
	}

	.btn-hover:hover {
		transform: scale3d(1.03, 1.03, 1);
	}

	.btn-hover:active {
		transform: scale3d(0.97, 0.97, 1);
	}
	
	/* 状态指示器样式 */
	.status-indicator {
		display: flex;
		align-items: center;
		gap: 8rpx;
		padding: 8rpx 15rpx;
		border-radius: 20rpx;
		background: rgba(255, 255, 255, 0.1);
	}
	
	.status-dot {
		width: 12rpx;
		height: 12rpx;
		border-radius: 50%;
		background: #4caf50;
		animation: pulse 2s infinite;
	}
	
	.status-text {
		font-size: 24rpx;
		color: #fff;
		font-weight: 500;
	}
	
	@keyframes pulse {
		0% {
			box-shadow: 0 0 0 0 rgba(76, 175, 80, 0.7);
		}
		70% {
			box-shadow: 0 0 0 10rpx rgba(76, 175, 80, 0);
		}
		100% {
			box-shadow: 0 0 0 0 rgba(76, 175, 80, 0);
		}
	}
</style>