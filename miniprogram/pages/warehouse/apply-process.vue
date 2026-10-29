<template>
	<view class="container">
		<!-- 自定义导航栏 -->
		<view class="custom-navbar">
			<view class="navbar-left">
				<image class="back-icon" src="http://szzgurngv.hn-bkt.clouddn.com/icon/back.png" @click="goBack" mode="aspectFit"></image>
			</view>
			<text class="navbar-title">申领流程</text>
			<view class="navbar-right">
			</view>
		</view>

		<!-- 申领信息 -->
		<view class="apply-info">
			<view class="info-card">
				<view class="info-title">申领物料信息</view>
				<view class="info-content">
					<view class="info-item">
						<text class="label">物料编码：</text>
						<text class="value">{{ materialCode }}</text>
					</view>
					<view class="info-item">
						<text class="label">物料名称：</text>
						<text class="value">{{ materialName }}</text>
					</view>
				</view>
			</view>
		</view>

		<!-- 流程图 -->
		<view class="process-flow">
			<view class="flow-title">申领流程图</view>
			
			<!-- 流程步骤 -->
			<view class="flow-container">
				<!-- 步骤1 -->
				<view class="flow-step" :class="{ active: currentStep >= 1, completed: currentStep > 1 }">
					<view class="step-circle">
						<text class="step-number">1</text>
					</view>
					<view class="step-content">
						<view class="step-title">提交申请</view>
						<view class="step-desc">填写申领信息并提交申请</view>
					</view>
				</view>

				<!-- 连接线 -->
				<view class="flow-line" :class="{ active: currentStep > 1 }"></view>

				<!-- 步骤2 -->
				<view class="flow-step" :class="{ active: currentStep >= 2, completed: currentStep > 2 }">
					<view class="step-circle">
						<text class="step-number">2</text>
					</view>
					<view class="step-content">
						<view class="step-title">管理员审核</view>
						<view class="step-desc">管理员审核申领申请</view>
					</view>
				</view>

				<!-- 连接线 -->
				<view class="flow-line" :class="{ active: currentStep > 2 }"></view>

				<!-- 步骤3 -->
				<view class="flow-step" :class="{ active: currentStep >= 3, completed: currentStep > 3 }">
					<view class="step-circle">
						<text class="step-number">3</text>
					</view>
					<view class="step-content">
						<view class="step-title">领取确认</view>
						<view class="step-desc">学生确认领取物料</view>
					</view>
				</view>
			</view>
		</view>

		<!-- 申领表单 -->
		<view class="apply-form">
			<view class="form-title">申领信息填写</view>
			
			<view class="form-item">
				<view class="form-label">申领数量</view>
				<input class="form-input-large" type="number" v-model="applyQuantity" placeholder="请输入申领数量" />
			</view>
			
			<view class="form-item">
				<view class="form-label">申领用途</view>
				<textarea class="form-textarea" v-model="applyPurpose" placeholder="请描述申领用途" />
			</view>
		</view>

		<!-- 操作按钮 -->
		<view class="action-buttons">
			<button class="btn btn-cancel" @click="goBack">取消</button>
			<button class="btn btn-submit" @click="submitApply">提交申请</button>
		</view>
	</view>
</template>

<script>
export default {
	data() {
		return {
			statusBarHeight: 0,
			materialCode: '',
			materialName: '',
			currentStep: 1,
			applyQuantity: '',
			applyPurpose: ''
		}
	},
	onLoad(options) {
		const systemInfo = uni.getSystemInfoSync()
		this.statusBarHeight = systemInfo.statusBarHeight
		
		if (options.code) {
			this.materialCode = options.code
		}
		if (options.name) {
			this.materialName = decodeURIComponent(options.name)
		}
	},
	methods: {
		goBack() {
			uni.navigateBack()
		},
		submitApply() {
			if (!this.applyQuantity || this.applyQuantity === '' || Number(this.applyQuantity) <= 0) {
				uni.showToast({
					title: '请输入有效的申领数量',
					icon: 'none'
				})
				return
			}
			
			if (!this.applyPurpose.trim()) {
				uni.showToast({
					title: '请填写申领用途',
					icon: 'none'
				})
				return
			}
			
			// 模拟提交申请
			uni.showLoading({
				title: '提交中...'
			})
			
			setTimeout(() => {
				uni.hideLoading()
				this.currentStep = 2
				
				uni.showModal({
					title: '申请提交成功',
					content: '您的申领申请已提交，请等待管理员审核',
					showCancel: false,
					success: () => {
						// 模拟流程进度
						this.simulateProcess()
					}
				})
			}, 1500)
		},
		simulateProcess() {
			// 模拟审核流程
			setTimeout(() => {
				this.currentStep = 3
				uni.showToast({
					title: '管理员审核通过，请及时领取',
					icon: 'success'
				})
			}, 2000)
		}
	}
}
</script>

<style scoped>
.container {
	background: #e3f2fd;
	min-height: 100vh;
	padding-bottom: 30rpx;
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

/* 申领信息 */
.apply-info {
	padding: 20rpx 30rpx 30rpx;
}

.info-card {
	background: white;
	border-radius: 16rpx;
	padding: 30rpx;
	box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.1);
}

.info-title {
	font-size: 32rpx;
	font-weight: bold;
	color: #2196F3;
	margin-bottom: 20rpx;
}

.info-item {
	display: flex;
	margin-bottom: 15rpx;
}

.label {
	color: #666;
	width: 160rpx;
}

.value {
	color: #333;
	flex: 1;
	font-weight: 500;
}

/* 流程图 */
.process-flow {
	padding: 0 30rpx 30rpx;
}

.flow-title {
	font-size: 32rpx;
	font-weight: bold;
	color: #333;
	margin-bottom: 30rpx;
	text-align: center;
}

.flow-container {
	background: white;
	border-radius: 16rpx;
	padding: 40rpx 30rpx;
	box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.1);
}

.flow-step {
	display: flex;
	align-items: center;
	margin-bottom: 30rpx;
	opacity: 0.4;
	transition: all 0.3s;
}

.flow-step.active {
	opacity: 1;
}

.flow-step.completed {
	opacity: 0.8;
}

.step-circle {
	width: 60rpx;
	height: 60rpx;
	border-radius: 50%;
	background: #ddd;
	display: flex;
	align-items: center;
	justify-content: center;
	margin-right: 20rpx;
	transition: all 0.3s;
}

.flow-step.active .step-circle {
	background: #2196F3;
	color: white;
}

.flow-step.completed .step-circle {
	background: #4CAF50;
	color: white;
}

.step-number {
	font-size: 24rpx;
	font-weight: bold;
}

.step-content {
	flex: 1;
}

.step-title {
	font-size: 28rpx;
	font-weight: bold;
	color: #333;
	margin-bottom: 5rpx;
}

.step-desc {
	font-size: 24rpx;
	color: #666;
}

.flow-line {
	width: 4rpx;
	height: 40rpx;
	background: #ddd;
	margin: 10rpx 0 10rpx 28rpx;
	transition: all 0.3s;
}

.flow-line.active {
	background: #2196F3;
}

/* 申领表单 */
.apply-form {
	padding: 0 30rpx 30rpx;
}

.form-title {
	font-size: 32rpx;
	font-weight: bold;
	color: #333;
	margin-bottom: 30rpx;
	text-align: center;
}

.form-item {
	background: white;
	border-radius: 16rpx;
	padding: 30rpx;
	margin-bottom: 20rpx;
	box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.1);
}

.form-label {
	font-size: 28rpx;
	font-weight: bold;
	color: #333;
	margin-bottom: 15rpx;
}

.form-input, .form-textarea {
	width: 100%;
	padding: 20rpx;
	border: 2rpx solid #e0e0e0;
	border-radius: 8rpx;
	font-size: 26rpx;
	box-sizing: border-box;
}

.form-input-large {
	width: 100%;
	padding: 20rpx;
	border: 2rpx solid #e0e0e0;
	border-radius: 8rpx;
	font-size: 26rpx;
	height: 100rpx;
	box-sizing: border-box;
}

.form-input-large:focus {
	border-color: #2196F3;
	outline: none;
}

.form-textarea {
	height: 120rpx;
	resize: none;
}

.picker-input {
	padding: 20rpx;
	border: 2rpx solid #e0e0e0;
	border-radius: 8rpx;
	font-size: 26rpx;
	color: #333;
}

/* 操作按钮 */
.action-buttons {
	display: flex;
	gap: 20rpx;
	padding: 0 30rpx;
}

.btn {
	flex: 1;
	height: 88rpx;
	border-radius: 44rpx;
	font-size: 32rpx;
	font-weight: bold;
	border: none;
	display: flex;
	align-items: center;
	justify-content: center;
}

.btn-cancel {
	background: #f5f5f5;
	color: #666;
}

.btn-submit {
	background: linear-gradient(135deg, #2196F3, #1976D2);
	color: white;
}
</style>