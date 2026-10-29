<template>
	<view class="container">
		<!-- 自定义导航栏 -->
		<view class="custom-navbar">
			<view class="navbar-left">
				<image class="back-icon" src="http://szzgurngv.hn-bkt.clouddn.com/icon/back.png" @click="goBack" mode="aspectFit"></image>
			</view>
			<text class="navbar-title">故障申报</text>
			<view class="navbar-right">
			</view>
		</view>

		<!-- 申报表单 -->
		<view class="form-section">
			<view class="form-card">
				<view class="form-title">设备故障申报</view>
				
				<!-- 设备名称 -->
			<view class="form-item">
				<text class="form-label">故障设备</text>
				<input class="form-input" 
					v-model="deviceName" 
					placeholder="请输入故障设备名称" 
					maxlength="50" />
			</view>

				<!-- 紧急程度 -->
				<view class="form-item">
					<text class="form-label">紧急程度</text>
					<view class="priority-options">
						<view class="priority-item" 
							:class="{active: priority === 'low'}" 
							@click="setPriority('low')">
							<text class="priority-text">低</text>
						</view>
						<view class="priority-item" 
							:class="{active: priority === 'medium'}" 
							@click="setPriority('medium')">
							<text class="priority-text">中</text>
						</view>
						<view class="priority-item" 
							:class="{active: priority === 'high'}" 
							@click="setPriority('high')">
							<text class="priority-text">高</text>
						</view>
					</view>
				</view>

				<!-- 故障描述 -->
				<view class="form-item">
					<text class="form-label">故障描述</text>
					<textarea class="form-textarea" 
						v-model="description" 
						placeholder="请详细描述设备故障现象、发生时间等信息"
						maxlength="500"
						auto-height></textarea>
					<view class="char-count">{{description.length}}/500</view>
				</view>

				<!-- 提交按钮 -->
				<view class="submit-section">
					<button class="submit-btn" @click="submitReport" :disabled="!canSubmit">
						提交申报
					</button>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
export default {
	data() {
		return {
			statusBarHeight: 0,
			deviceName: '',
			priority: '',
			description: ''
		}
	},
	computed: {
		canSubmit() {
			return this.deviceName.trim() && 
				   this.priority && 
				   this.description.trim()
		}
	},
	onLoad() {
		const systemInfo = uni.getSystemInfoSync()
		this.statusBarHeight = systemInfo.statusBarHeight
	},
	methods: {
		goBack() {
			uni.navigateBack()
		},

		setPriority(level) {
			this.priority = level
		},
		submitReport() {
			if (!this.canSubmit) {
				uni.showToast({
					title: '请完善申报信息',
					icon: 'error'
				})
				return
			}

			// 模拟提交
			uni.showLoading({
				title: '提交中...'
			})

			setTimeout(() => {
				uni.hideLoading()
				uni.showToast({
					title: '申报成功',
					icon: 'success'
				})
				
				setTimeout(() => {
					uni.navigateBack()
				}, 1500)
			}, 1000)
		}
	}
}
</script>

<style scoped>
.container {
	background: #e3f2fd;
	min-height: 100vh;
}

/* 导航栏 */
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

/* 表单区域 */
.form-section {
	padding: 20rpx 30rpx 30rpx;
}

.form-card {
	background: white;
	border-radius: 16rpx;
	padding: 40rpx 30rpx;
	box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.1);
}

.form-title {
	font-size: 32rpx;
	font-weight: bold;
	color: #333;
	margin-bottom: 40rpx;
	text-align: center;
}

.form-item {
	margin-bottom: 30rpx;
}

.form-label {
	font-size: 28rpx;
	color: #333;
	font-weight: 500;
	margin-bottom: 15rpx;
	display: block;
}

.form-picker {
	display: flex;
	align-items: center;
	justify-content: space-between;
	padding: 20rpx 25rpx;
	border: 2rpx solid #e0e0e0;
	border-radius: 12rpx;
	background: #fafafa;
}

.picker-text {
	font-size: 26rpx;
	color: #333;
}

.picker-text.placeholder {
	color: #999;
}

.picker-arrow {
	font-size: 20rpx;
	color: #999;
}

.priority-options {
	display: flex;
	gap: 20rpx;
}

.priority-item {
	flex: 1;
	padding: 20rpx;
	border: 2rpx solid #e0e0e0;
	border-radius: 12rpx;
	text-align: center;
	background: #fafafa;
	transition: all 0.3s;
}

.priority-item.active {
	border-color: #2196F3;
	background: #e3f2fd;
}

.priority-text {
	font-size: 26rpx;
	color: #333;
}

.form-textarea {
	width: 100%;
	min-height: 200rpx;
	padding: 20rpx 25rpx;
	border: 2rpx solid #e0e0e0;
	border-radius: 12rpx;
	background: #fafafa;
	font-size: 26rpx;
	color: #333;
	box-sizing: border-box;
}

.char-count {
	font-size: 22rpx;
	color: #999;
	text-align: right;
	margin-top: 10rpx;
}

.form-input {
	width: 100%;
	padding: 0rpx 25rpx;
	min-height: 100rpx;
	border: 2rpx solid #e0e0e0;
	border-radius: 12rpx;
	background: #fafafa;
	font-size: 26rpx;
	color: #333;
	box-sizing: border-box;
}

.submit-section {
	margin-top: 50rpx;
}

.submit-btn {
	width: 100%;
	padding: 25rpx;
	background: #2196F3;
	color: white;
	border: none;
	border-radius: 12rpx;
	font-size: 30rpx;
	font-weight: bold;
	transition: background 0.3s;
}

.submit-btn:disabled {
	background: #ccc;
}

.submit-btn:not(:disabled):active {
	background: #1976D2;
}
</style>