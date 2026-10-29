<template>
	<view class="page-container">
		<!-- 自定义导航栏 -->
		<view class="custom-navbar">
			<view class="navbar-left">
				<image class="back-icon" src="http://szzgurngv.hn-bkt.clouddn.com/icon/back.png" @click="goBack" mode="aspectFit"></image>
			</view>
			<text class="navbar-title">创建通知</text>
			<view class="navbar-right">
			</view>
		</view>

		<!-- 通知编辑区域 -->
		<view class="memo-container">
			<view class="memo-card">
				<!-- 标题 -->
				<view class="form-item">
					<text class="form-label">标题</text>
					<input 
						v-model="subject" 
						placeholder="" 
						class="form-input"
						@input="checkCanSend"
					/>
				</view>

				<!-- 发布者名称 -->
				<view class="form-item">
					<text class="form-label">发布者名称</text>
					<input 
						v-model="publisherName" 
						placeholder="" 
						class="form-input"
						@input="checkCanSend"
					/>
				</view>

				<!-- 类型选择 -->
				<view class="form-item">
					<text class="form-label">类型</text>
					<picker @change="onTypeChange" :value="typeIndex" :range="typeOptions">
						<view class="picker-input">
							<text class="picker-text">{{typeOptions[typeIndex]}}</text>
							<text class="picker-arrow">▼</text>
						</view>
					</picker>
				</view>

				<!-- 内容 -->
				<view class="form-item content-item">
					<text class="form-label">内容</text>
					<textarea 
						v-model="content" 
						placeholder="" 
						class="content-textarea"
						@input="checkCanSend"
						maxlength="500"
						auto-height
					/>
					<view class="char-count">{{content.length}}/500</view>
				</view>

				<!-- 重要通知 -->
				<view class="form-item checkbox-item">
					<checkbox-group @change="onImportantChange">
						<label class="checkbox-label">
							<checkbox :checked="isImportant" color="#2979FF" />
							<text class="checkbox-text">重要通知</text>
						</label>
					</checkbox-group>
				</view>

				<!-- 附件区域 -->
				<view class="form-item" v-if="attachments.length > 0">
					<text class="form-label">附件</text>
					<view class="attachment-list">
						<view class="attachment-item" v-for="(file, index) in attachments" :key="index">
							<view class="file-info">
								<text class="file-icon">🎫</text>
								<text class="file-name">{{file.name}}</text>
							</view>
							<view class="remove-btn" @click="removeAttachment(index)">
								<text class="remove-icon">×</text>
							</view>
						</view>
					</view>
				</view>

				<!-- 工具栏 -->
				<view class="toolbar">
					<view class="tool-btn" @click="addAttachment">
						<text class="tool-icon">🎫</text>
						<text class="tool-text">附件</text>
					</view>
				</view>
			</view>
		</view>

		<!-- 底部按钮区域 -->
		<view class="bottom-actions">
			<view class="action-buttons">
				<view class="cancel-btn" @click="goBack">
					<text class="btn-text">取消</text>
				</view>
				<view class="send-btn" @click="sendMessage" :class="{disabled: !canSend}">
					<text class="btn-text">发送</text>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
export default {
	data() {
		return {
			subject: '',
			publisherName: '',
			content: '',
			attachments: [],
			canSend: false,
			typeIndex: 0,
			typeOptions: ['通知', '新闻', '动态', '安全提醒', '天气提醒', '名言金句'],
			isImportant: false
		}
	},
	methods: {
		// 返回上一页
		goBack() {
			// 获取页面栈
			const pages = getCurrentPages()
			if (pages.length > 1) {
				// 有上一页，正常返回
				uni.navigateBack({
					delta: 1
				})
			} else {
				// 没有上一页，跳转到消息页面
				uni.switchTab({
					url: '/pages/message/message'
				})
			}
		},

		// 检查是否可以发送
		checkCanSend() {
			this.canSend = this.subject.trim() && this.publisherName.trim() && this.content.trim()
		},

		// 类型选择变化
		onTypeChange(e) {
			this.typeIndex = e.detail.value
		},

		// 重要通知变化
		onImportantChange(e) {
			this.isImportant = e.detail.value.length > 0
		},

		// 发送消息
		sendMessage() {
			if (!this.canSend) {
				uni.showToast({
					title: '请填写完整信息',
					icon: 'none'
				})
				return
			}

			// 显示发送中状态
			uni.showLoading({
				title: '发送中...'
			})

			// 构造API请求数据
			const requestData = {
				title: this.subject,
				content: this.content,
				type: this.typeOptions[this.typeIndex],
				is_important: this.isImportant,
				author_name: this.publisherName
			}

			// 调用API创建公告
			uni.request({
				url: 'http://localhost:8000/api/announcements',
				method: 'POST',
				data: requestData,
				header: {
					'Content-Type': 'application/json'
				},
				success: (res) => {
					uni.hideLoading()
					if (res.statusCode === 201 && res.data.success) {
						uni.showToast({
							title: '发送成功',
							icon: 'success'
						})

						// 返回上一页并刷新消息列表
						setTimeout(() => {
							const pages = getCurrentPages()
							if (pages.length > 1) {
								// 有上一页，正常返回
								uni.navigateBack({
									delta: 1
								})
							} else {
								// 没有上一页，跳转到消息页面
								uni.switchTab({
									url: '/pages/message/message'
								})
							}
						}, 1500)
					} else {
						console.error('发送失败:', res.data.message)
						uni.showToast({
							title: res.data.message || '发送失败',
							icon: 'none'
						})
					}
				},
				fail: (err) => {
					uni.hideLoading()
					console.error('API请求失败:', err)
					uni.showToast({
						title: '网络连接失败',
						icon: 'none'
					})
				}
			})
		},

		// 添加附件
		addAttachment() {
			// 在小程序环境中使用chooseMessageFile
			if (typeof uni.chooseMessageFile === 'function') {
				uni.chooseMessageFile({
					count: 1,
					type: 'all',
					success: (res) => {
						const file = res.tempFiles[0]
						this.attachments.push({
							name: file.name,
							path: file.path,
							size: file.size
						})
					},
					fail: () => {
						uni.showToast({
							title: '选择文件失败',
							icon: 'none'
						})
					}
				})
			} else {
				// 模拟添加附件
				this.attachments.push({
					name: '示例文件.pdf',
					path: '/temp/example.pdf',
					size: 1024
				})
				uni.showToast({
					title: '附件添加成功',
					icon: 'success'
				})
			}
		},

		// 移除附件
		removeAttachment(index) {
			this.attachments.splice(index, 1)
		}
	}
}
</script>

<style scoped>
.page-container {
	min-height: 100vh;
	background: #e3f2fd;
	display: flex;
	flex-direction: column;
}

/* 导航栏样式 */
	.custom-navbar {
		display: flex;
		align-items: center;
		justify-content: space-between;
		padding: 20rpx 30rpx;
		padding-top: 60rpx;
		min-height: 100rpx;
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

/* 备忘录容器 */
.memo-container {
	flex: 1;
	padding: 20rpx 20rpx 140rpx;
	display: flex;
	flex-direction: column;
	gap: 20rpx;
}

.memo-card {
	background: #fff;
	border-radius: 20rpx;
	padding: 40rpx 30rpx;
	margin: 0;
	box-shadow: 0 8rpx 32rpx rgba(0, 0, 0, 0.12);
	border: none;
	display: flex;
	flex-direction: column;
	gap: 30rpx;
	min-height: auto;
	position: relative;
	transform: translateY(0);
	transition: all 0.3s ease;
}

/* 表单项样式 */
.form-item {
	margin-bottom: 0;
	padding: 0;
}

.form-label {
	display: block;
	font-size: 28rpx;
	font-weight: 600;
	color: #2c3e50;
	margin-bottom: 16rpx;
	position: relative;
}

.form-label::before {
	content: '';
	position: absolute;
	left: -12rpx;
	top: 50%;
	transform: translateY(-50%);
	width: 6rpx;
	height: 20rpx;
	background: linear-gradient(135deg, #667eea, #764ba2);
	border-radius: 3rpx;
}

.form-input {
	width: 100%;
	height: 80rpx;
	font-size: 30rpx;
	color: #2c3e50;
	border: 2rpx solid #e8ecf4;
	border-radius: 16rpx;
	padding: 0 24rpx;
	background: #f8f9ff;
	box-sizing: border-box;
	transition: all 0.3s ease;
}

.form-input:focus {
	border-color: #667eea;
	box-shadow: 0 0 0 4rpx rgba(102, 126, 234, 0.15);
	background: #fff;
}

.form-input::placeholder {
	color: #a0a9c0;
	font-size: 28rpx;
}

/* 选择器样式 */
.picker-input {
	display: flex;
	align-items: center;
	justify-content: space-between;
	width: 100%;
	height: 80rpx;
	font-size: 30rpx;
	color: #2c3e50;
	border: 2rpx solid #e8ecf4;
	border-radius: 16rpx;
	padding: 0 24rpx;
	background: #f8f9ff;
	box-sizing: border-box;
	transition: all 0.3s ease;
}

.picker-input:active {
	border-color: #667eea;
	box-shadow: 0 0 0 4rpx rgba(102, 126, 234, 0.15);
	background: #fff;
}

.picker-text {
	flex: 1;
	color: #2c3e50;
	font-weight: 500;
}

.picker-arrow {
	font-size: 24rpx;
	color: #667eea;
	font-weight: bold;
}

/* 内容区域 */
.content-item {
	flex: 1;
}

.content-textarea {
	width: 100%;
	min-height: 200rpx;
	font-size: 30rpx;
	color: #2c3e50;
	border: 2rpx solid #e8ecf4;
	border-radius: 16rpx;
	padding: 24rpx;
	background: #f8f9ff;
	box-sizing: border-box;
	line-height: 1.6;
	resize: none;
	transition: all 0.3s ease;
}

.content-textarea:focus {
	border-color: #667eea;
	box-shadow: 0 0 0 4rpx rgba(102, 126, 234, 0.15);
	background: #fff;
}

.content-textarea::placeholder {
	color: #a0a9c0;
	font-size: 28rpx;
}

.char-count {
	text-align: right;
	font-size: 26rpx;
	color: #8892b0;
	margin-top: 12rpx;
	font-weight: 500;
}

/* 复选框样式 */
.checkbox-item {
	padding: 20rpx 24rpx;
	background: linear-gradient(135deg, #fff5f5 0%, #fff0f6 100%);
	border-radius: 16rpx;
	border: 2rpx solid #fce4ec;
	position: relative;
	overflow: hidden;
}

.checkbox-item::before {
	content: '';
	position: absolute;
	top: 0;
	left: 0;
	width: 6rpx;
	height: 100%;
	background: linear-gradient(135deg, #ff6b9d, #c44569);
}

.checkbox-label {
	display: flex;
	align-items: center;
	gap: 16rpx;
	padding: 0;
}

.checkbox-text {
	font-size: 30rpx;
	color: #2c3e50;
	font-weight: 600;
}

/* 工具栏 */
.toolbar {
	display: flex;
	align-items: center;
	gap: 20rpx;
	padding: 24rpx 0 0;
	border-top: 2rpx solid #f0f2f8;
	margin-top: 20rpx;
}

.tool-btn {
	display: flex;
	align-items: center;
	gap: 12rpx;
	padding: 16rpx 28rpx;
	background: #2196F3;
	color: white;
	border-radius: 20rpx;
	transition: all 0.3s ease;
	box-shadow: 0 6rpx 20rpx rgba(102, 126, 234, 0.4);
	position: relative;
	overflow: hidden;
}

.tool-btn::before {
	content: '';
	position: absolute;
	top: 0;
	left: -100%;
	width: 100%;
	height: 100%;
	background: linear-gradient(90deg, transparent, rgba(255,255,255,0.2), transparent);
	transition: left 0.5s;
}

.tool-btn:active {
	transform: scale(0.95);
	box-shadow: 0 4rpx 12rpx rgba(102, 126, 234, 0.5);
}

.tool-btn:active::before {
	left: 100%;
}

.tool-icon {
	font-size: 28rpx;
}

.tool-text {
	font-size: 26rpx;
	color: white;
	font-weight: 600;
}

/* 附件区域 */
.attachment-list {
	display: flex;
	flex-direction: column;
	gap: 16rpx;
	padding: 0;
}

.attachment-item {
	display: flex;
	align-items: center;
	justify-content: space-between;
	padding: 20rpx 24rpx;
	background: linear-gradient(135deg, #f8f9ff 0%, #f0f4ff 100%);
	border-radius: 16rpx;
	border: 2rpx solid #e8ecf4;
	position: relative;
	overflow: hidden;
}

.attachment-item::before {
	content: '';
	position: absolute;
	top: 0;
	left: 0;
	width: 6rpx;
	height: 100%;
	background: linear-gradient(135deg, #667eea, #764ba2);
}

.file-info {
	display: flex;
	align-items: center;
	gap: 16rpx;
	flex: 1;
}

.file-icon {
	font-size: 32rpx;
	color: #667eea;
}

.file-name {
	font-size: 28rpx;
	color: #2c3e50;
	flex: 1;
	overflow: hidden;
	text-overflow: ellipsis;
	white-space: nowrap;
	font-weight: 500;
}

.remove-btn {
	width: 48rpx;
	height: 48rpx;
	display: flex;
	align-items: center;
	justify-content: center;
	background: linear-gradient(135deg, #ff6b9d, #c44569);
	border-radius: 50%;
	transition: all 0.3s ease;
	box-shadow: 0 4rpx 12rpx rgba(255, 107, 157, 0.3);
}

.remove-btn:active {
	transform: scale(0.9);
	box-shadow: 0 2rpx 6rpx rgba(255, 107, 157, 0.4);
}

.remove-icon {
	font-size: 26rpx;
	color: white;
	font-weight: bold;
}



/* 底部按钮区域 */
.bottom-actions {
	position: fixed;
	bottom: 0;
	left: 0;
	right: 0;
	background: linear-gradient(135deg, rgba(255,255,255,0.95) 0%, rgba(248,250,255,0.95) 100%);
	padding: 24rpx 30rpx;
	box-shadow: 0 -8rpx 32rpx rgba(0, 0, 0, 0.12);
	border-top: 2rpx solid rgba(232, 236, 244, 0.8);
	z-index: 100;
	backdrop-filter: blur(10rpx);
}

.action-buttons {
	display: flex;
	gap: 24rpx;
	justify-content: center;
}

.cancel-btn,
.send-btn {
	flex: 1;
	height: 88rpx;
	display: flex;
	align-items: center;
	justify-content: center;
	border-radius: 20rpx;
	transition: all 0.3s ease;
	max-width: 280rpx;
	position: relative;
	overflow: hidden;
}

.cancel-btn {
	background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
	border: 2rpx solid #dee2e6;
	color: #6c757d;
	box-shadow: 0 4rpx 16rpx rgba(108, 117, 125, 0.2);
}

.cancel-btn:active {
	transform: scale(0.96);
	box-shadow: 0 2rpx 8rpx rgba(108, 117, 125, 0.3);
}

.send-btn {
	background: #2196F3;
	box-shadow: 0 6rpx 24rpx rgba(102, 126, 234, 0.4);
	color: white;
	border: none;
}

.send-btn::before {
	content: '';
	position: absolute;
	top: 0;
	left: -100%;
	width: 100%;
	height: 100%;
	background: linear-gradient(90deg, transparent, rgba(255,255,255,0.2), transparent);
	transition: left 0.5s;
}

.send-btn.disabled {
	background: linear-gradient(135deg, #adb5bd 0%, #868e96 100%);
	box-shadow: 0 2rpx 8rpx rgba(173, 181, 189, 0.3);
}

.send-btn:active:not(.disabled) {
	transform: scale(0.96);
	box-shadow: 0 4rpx 16rpx rgba(102, 126, 234, 0.5);
}

.send-btn:active:not(.disabled)::before {
	left: 100%;
}

.btn-text {
	font-size: 30rpx;
	font-weight: 600;
}

.cancel-btn .btn-text {
	color: #6c757d;
}

.send-btn .btn-text {
	color: white;
}

.send-btn.disabled .btn-text {
	color: #fff;
}
</style>