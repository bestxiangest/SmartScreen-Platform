<template>
	<view class="container">
		<!-- 自定义导航栏 -->
		<view class="custom-navbar">
			<view class="navbar-left">
				<image class="back-icon" src="http://szzgurngv.hn-bkt.clouddn.com/icon/back.png" @click="goBack" mode="aspectFit"></image>
			</view>
			<text class="navbar-title">消息详情</text>
			<view class="navbar-right">
			</view>
		</view>

		<!-- 消息详情内容 -->
		<view class="message-detail">
			<!-- 发送者信息 -->
			<view class="sender-info">
				<image class="sender-avatar" :src="messageDetail.avatar" mode="aspectFill"></image>
				<view class="sender-details">
					<text class="sender-name">{{messageDetail.senderName}}</text>
					<text class="send-time">{{messageDetail.fullTime}}</text>
					<view class="message-type-info">
						<text class="type-label">类型:</text>
						<text class="type-text">{{getTypeText(messageDetail.type)}}</text>
					</view>
				</view>
				<view class="message-type" :class="messageDetail.type">
					<text class="type-icon">{{getTypeIcon(messageDetail.type)}}</text>
				</view>
			</view>

			<!-- 消息主题 -->
			<view class="message-subject" v-if="messageDetail.subject">
				<text class="subject-label">主题:</text>
				<text class="subject-text">{{messageDetail.subject}}</text>
				<view class="important-badge" v-if="messageDetail.isImportant">
					<text class="important-text">重要</text>
				</view>
			</view>

			<!-- 消息内容 -->
			<view class="message-content">
				<text class="content-label">内容:</text>
				<view class="content-text">
					<text>{{messageDetail.content}}</text>
				</view>
			</view>

			<!-- 附件列表 -->
			<view class="attachments" v-if="messageDetail.attachments && messageDetail.attachments.length > 0">
				<text class="attachments-label">附件:</text>
				<view class="attachment-list">
					<view class="attachment-item" v-for="(attachment, index) in messageDetail.attachments" :key="index" @click="previewAttachment(attachment)">
						<view class="attachment-icon">
							<text class="file-icon">📎</text>
						</view>
						<view class="attachment-info">
							<text class="attachment-name">{{attachment.name}}</text>
							<text class="attachment-size">{{formatFileSize(attachment.size)}}</text>
						</view>
						<view class="download-icon">
							<text>⬇</text>
						</view>
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
				messageId: '',
				messageDetail: {
					id: '',
					senderName: '',
					avatar: '',
					subject: '',
					content: '',
					fullTime: '',
					type: '',
					attachments: []
				}
			}
		},
		onLoad(options) {
			this.messageId = options.id
			this.loadMessageDetail()
			// 标记消息为已读
			this.markAsRead()
		},
		methods: {
			// 加载消息详情
			loadMessageDetail() {
				// 首先检查是否是新发送的消息
				const storedMessage = uni.getStorageSync('messageDetail_' + this.messageId)
				if (storedMessage) {
					this.messageDetail = storedMessage
					return
				}
				
				// 检查是否是刚发送的新消息
				const newMessage = uni.getStorageSync('newMessage')
				if (newMessage && this.messageId === 'new') {
					this.messageDetail = {
						id: 'new',
						senderName: newMessage.publisherName,
						avatar: '/static/avatars/user.jpg',
						subject: newMessage.subject,
						content: newMessage.content,
						fullTime: this.formatFullTime(new Date(newMessage.timestamp)),
						type: newMessage.type,
						isImportant: newMessage.isImportant,
						attachments: newMessage.attachments || []
					}
					// 清除临时存储
					uni.removeStorageSync('newMessage')
					return
				}
				
				// 从API获取消息详情
				this.loadMessageFromAPI()
			},
			
			// 从API加载消息详情
			loadMessageFromAPI() {
				uni.showLoading({
					title: '加载中...'
				})
				
				uni.request({
					url: `http://localhost:8000/api/announcements/${this.messageId}`,
					method: 'GET',
					header: {
						'Content-Type': 'application/json'
					},
					success: (res) => {
						uni.hideLoading()
						if (res.statusCode === 200 && res.data.success) {
							const announcement = res.data.data
							
							// 转换API数据格式
							this.messageDetail = {
								id: announcement.id,
								senderName: announcement.author_name || '系统',
								avatar: this.getAvatarByType(announcement.type),
								subject: announcement.title,
								content: announcement.content,
								fullTime: this.formatFullTime(new Date(announcement.created_at)),
								type: announcement.type,
								isImportant: announcement.is_important,
								attachments: announcement.attachments || []
							}
							
							// 保存到本地存储以便下次快速加载
							uni.setStorageSync('messageDetail_' + this.messageId, this.messageDetail)
						} else {
						console.error('获取消息详情失败:', res.data.message)
						// 使用后备数据
						this.loadFallbackData()
						uni.showToast({
							title: '加载失败',
							icon: 'none'
						})
					}
				},
				fail: (err) => {
					uni.hideLoading()
					console.error('API请求失败:', err)
					// 使用后备数据
					this.loadFallbackData()
					uni.showToast({
						title: '网络连接失败',
						icon: 'none'
					})
				}
				})
			},
			
			// 加载后备数据（模拟数据）
			loadFallbackData() {
				this.messageDetail = {
					id: this.messageId,
					senderName: '系统管理员',
					avatar: '/static/avatars/system.jpg',
					subject: '消息详情',
					content: '消息内容加载失败，请检查网络连接后重试。',
					fullTime: this.formatFullTime(new Date()),
					type: '通知',
					isImportant: false,
					attachments: []
				}
			},
			
			// 加载模拟数据（保留原有逻辑）
			loadMockData() {
				const messages = [
					{
						id: '1',
						senderName: '张老师',
						avatar: '/static/avatars/teacher1.jpg',
						subject: '明天实验课安排',
						content: '明天的实验课需要提前准备材料，请同学们带好实验服、护目镜和实验记录本。实验内容是化学反应观察，需要特别注意安全操作规范。',
						fullTime: '2024年1月15日 10:30',
						type: 'teacher',
						attachments: [
							{ name: '实验指导书.pdf', size: 2048000 },
							{ name: '安全须知.docx', size: 512000 }
						]
					},
					{
						id: '2',
						senderName: '实验室管理员',
						avatar: '/static/avatars/admin.jpg',
						subject: '设备维护通知',
						content: '设备维护通知：显微镜将于明日上午9:00-12:00进行例行维护，期间暂停使用。请各位老师和同学合理安排实验时间。',
						fullTime: '2024年1月15日 09:15',
						type: 'system',
						attachments: []
					},
					{
						id: '3',
						senderName: '李同学',
						avatar: '/static/avatars/student1.jpg',
						subject: '项目资料分享',
						content: '项目资料已经整理完成，请查收。包含了实验数据、分析报告和总结文档，希望对大家有帮助。',
						fullTime: '2024年1月14日 16:45',
						type: 'student',
						attachments: [
							{ name: '实验数据.xlsx', size: 1024000 },
							{ name: '分析报告.pdf', size: 3072000 },
							{ name: '项目总结.pptx', size: 5120000 }
						]
					},
					{
						id: '4',
						senderName: '安全监控系统',
						avatar: '/static/avatars/system.jpg',
						subject: '温度异常警报',
						content: '实验室A101温度异常，当前温度28.5°C，超出正常范围。请相关人员及时检查空调系统和通风设备，确保实验环境安全。',
						fullTime: '2024年1月14日 14:20',
						type: 'alert',
						attachments: [
							{ name: '温度监控报告.pdf', size: 768000 }
						]
					}
				]

				// 从预设消息中查找
				const message = messages.find(m => m.id === this.messageId)
				if (message) {
					this.messageDetail = message
				} else {
					// 消息不存在，返回列表
					uni.showToast({
						title: '消息不存在',
						icon: 'none'
					})
					this.goBack()
				}
			},
			
			// 根据类型获取头像
			getAvatarByType(type) {
				const avatarMap = {
					'通知': '/static/avatars/system.jpg',
					'新闻': '/static/avatars/news.jpg',
					'动态': '/static/avatars/dynamic.jpg',
					'安全提醒': '/static/avatars/safety.jpg',
					'天气提醒': '/static/avatars/weather.jpg',
					'名言金句': '/static/avatars/quote.jpg',
					'teacher': '/static/avatars/teacher1.jpg',
					'student': '/static/avatars/student1.jpg',
					'system': '/static/avatars/system.jpg',
					'alert': '/static/avatars/alert.jpg'
				}
				return avatarMap[type] || '/static/avatars/default.jpg'
			},
			
			// 标记消息为已读
			markAsRead() {
				// 更新本地存储中的已读状态
				const readMessages = uni.getStorageSync('readMessages') || []
				if (!readMessages.includes(this.messageId)) {
					readMessages.push(this.messageId)
					uni.setStorageSync('readMessages', readMessages)
				}
				
				// 通知消息列表页面更新已读状态
				uni.$emit('messageRead', this.messageId)
			},
			// 返回上一页
			goBack() {
				const pages = getCurrentPages()
				if (pages.length > 1) {
					uni.navigateBack({ delta: 1 })
				} else {
					uni.switchTab({ url: '/pages/message/message' })
				}
			},
			// 显示更多选项
			showMoreOptions() {
				uni.showActionSheet({
					itemList: ['标记为未读', '删除消息', '举报'],
					success: (res) => {
						switch(res.tapIndex) {
							case 0:
								uni.showToast({ title: '已标记为未读', icon: 'success' })
								break
							case 1:
								this.deleteMessage()
								break
							case 2:
								uni.showToast({ title: '举报功能开发中', icon: 'none' })
								break
						}
					}
				})
			},
			// 删除消息
			deleteMessage() {
				uni.showModal({
					title: '确认删除',
					content: '确定要删除这条消息吗？',
					success: (res) => {
						if (res.confirm) {
							uni.showToast({ title: '删除成功', icon: 'success' })
							setTimeout(() => {
								this.goBack()
							}, 1000)
						}
					}
				})
			},
			// 预览附件
			previewAttachment(attachment) {
				uni.showModal({
					title: '附件预览',
					content: `文件名: ${attachment.name}\n大小: ${this.formatFileSize(attachment.size)}`,
					confirmText: '下载',
					cancelText: '取消',
					success: (res) => {
						if (res.confirm) {
							uni.showToast({ title: '下载功能开发中', icon: 'none' })
						}
					}
				})
			},
			// 获取类型图标
			getTypeIcon(type) {
				const icons = {
					teacher: '👨‍🏫',
					student: '👨‍🎓',
					system: '🔧',
					alert: '⚠️',
					通知: '📢',
					新闻: '📰',
					动态: '📝',
					安全提醒: '⚠️',
					天气提醒: '🌤️',
					名言金句: '💭'
				}
				return icons[type] || '💬'
			},
			// 获取类型文本
			getTypeText(type) {
				const typeMap = {
					teacher: '教师通知',
					student: '学生消息',
					system: '系统通知',
					alert: '安全提醒',
					通知: '通知',
					新闻: '新闻',
					动态: '动态',
					安全提醒: '安全提醒',
					天气提醒: '天气提醒',
					名言金句: '名言金句'
				}
				return typeMap[type] || '消息'
			},
			// 格式化文件大小
			formatFileSize(bytes) {
				if (bytes === 0) return '0 B'
				const k = 1024
				const sizes = ['B', 'KB', 'MB', 'GB']
				const i = Math.floor(Math.log(bytes) / Math.log(k))
				return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
			},
			// 格式化完整时间
			formatFullTime(date) {
				const year = date.getFullYear()
				const month = (date.getMonth() + 1).toString().padStart(2, '0')
				const day = date.getDate().toString().padStart(2, '0')
				const hour = date.getHours().toString().padStart(2, '0')
				const minute = date.getMinutes().toString().padStart(2, '0')
				return `${year}年${month}月${day}日 ${hour}:${minute}`
			}
		}
	}
</script>

<style scoped>
	.container {
		min-height: 100vh;
		background: #e3f2fd;
	}
	
	.custom-navbar {
		display: flex;
		align-items: center;
		justify-content: space-between;
		padding: 20rpx 30rpx;
		padding-top: 60rpx;
		background: #2196F3;
		border-bottom: none;
		position: sticky;
		top: 0;
		z-index: 100;
	}
	
	.navbar-left, .navbar-right {
		width: 80rpx;
		height: 60rpx;
		display: flex;
		align-items: center;
		justify-content: center;
	}
	
	.back-icon {
		width: 60rpx;
		height: 60rpx;
	}
	
	.more-icon {
		font-size: 32rpx;
		color: white;
		font-weight: bold;
	}
	
	.navbar-title {
		margin-top: 20rpx;
		font-size: 36rpx;
		font-weight: bold;
		color: white;
	}

	/* 消息详情样式 */
	.message-detail {
		padding: 30rpx;
		display: flex;
		flex-direction: column;
		gap: 30rpx;
	}

	.sender-info {
		display: flex;
		align-items: center;
		gap: 20rpx;
		padding: 30rpx;
		background: white;
		border-radius: 20rpx;
		box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.1);
	}

	.sender-avatar {
		width: 100rpx;
		height: 100rpx;
		border-radius: 50%;
	}

	.sender-details {
		flex: 1;
		display: flex;
		flex-direction: column;
		gap: 8rpx;
	}

	.sender-name {
		font-size: 32rpx;
		font-weight: bold;
		color: #333;
	}

	.send-time {
		font-size: 24rpx;
		color: #999;
	}

	.message-type-info {
		display: flex;
		align-items: center;
		gap: 8rpx;
		margin-top: 4rpx;
	}

	.type-label {
		font-size: 22rpx;
		color: #666;
	}

	.type-text {
		font-size: 22rpx;
		color: #2979FF;
		font-weight: 500;
		padding: 4rpx 12rpx;
		background: #e3f2fd;
		border-radius: 12rpx;
	}

	.message-type {
		width: 60rpx;
		height: 60rpx;
		display: flex;
		align-items: center;
		justify-content: center;
		border-radius: 50%;
		background: #f0f0f0;
	}

	.message-type.teacher {
		background: #e3f2fd;
	}

	.message-type.system {
		background: #fff3e0;
	}

	.message-type.alert {
		background: #ffebee;
	}

	.type-icon {
		font-size: 24rpx;
	}

	.message-subject {
		padding: 30rpx;
		background: white;
		border-radius: 20rpx;
		box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.1);
		position: relative;
	}

	.subject-label {
		font-size: 28rpx;
		color: #666;
		margin-bottom: 15rpx;
		display: block;
	}

	.subject-text {
		font-size: 32rpx;
		font-weight: bold;
		color: #333;
		line-height: 1.5;
	}

	.important-badge {
		position: absolute;
		top: 20rpx;
		right: 20rpx;
		padding: 8rpx 16rpx;
		background: #ff4444;
		border-radius: 20rpx;
	}

	.important-text {
		font-size: 20rpx;
		color: white;
		font-weight: bold;
	}

	.message-content {
		padding: 30rpx;
		background: white;
		border-radius: 20rpx;
		box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.1);
	}

	.content-label {
		font-size: 28rpx;
		color: #666;
		margin-bottom: 15rpx;
		display: block;
	}

	.content-text {
		font-size: 30rpx;
		color: #333;
		line-height: 1.6;
		white-space: pre-wrap;
		word-wrap: break-word;
		word-break: break-all;
		overflow-wrap: break-word;
		max-width: 100%;
	}

	.attachments {
		padding: 30rpx;
		background: white;
		border-radius: 20rpx;
		box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.1);
	}

	.attachments-label {
		font-size: 28rpx;
		color: #666;
		margin-bottom: 20rpx;
		display: block;
	}

	.attachment-list {
		display: flex;
		flex-direction: column;
		gap: 15rpx;
	}

	.attachment-item {
		display: flex;
		align-items: center;
		gap: 20rpx;
		padding: 20rpx;
		background: #f8f8f8;
		border-radius: 15rpx;
		transition: all 0.3s;
	}

	.attachment-item:active {
		background: #e8e8e8;
		transform: scale(0.98);
	}

	.attachment-icon {
		width: 60rpx;
		height: 60rpx;
		display: flex;
		align-items: center;
		justify-content: center;
		background: #2979FF;
		border-radius: 50%;
	}

	.file-icon {
		font-size: 24rpx;
		color: white;
	}

	.attachment-info {
		flex: 1;
		display: flex;
		flex-direction: column;
		gap: 5rpx;
	}

	.attachment-name {
		font-size: 28rpx;
		color: #333;
		font-weight: 500;
	}

	.attachment-size {
		font-size: 24rpx;
		color: #999;
	}

	.download-icon {
		width: 40rpx;
		height: 40rpx;
		display: flex;
		align-items: center;
		justify-content: center;
		color: #2979FF;
		font-size: 24rpx;
	}
</style>