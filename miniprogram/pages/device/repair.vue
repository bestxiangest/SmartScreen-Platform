<template>
	<view class="container">
		<!-- 报修类型选择 -->
		<view class="repair-types">
			<text class="section-title">报修类型</text>
			<view class="type-grid">
				<view class="type-item" 
					v-for="type in repairTypes" 
					:key="type.id"
					:class="{active: selectedType === type.id}"
					@click="selectedType = type.id">
					<text class="type-icon">{{type.icon}}</text>
					<text class="type-name">{{type.name}}</text>
				</view>
			</view>
		</view>

		<!-- 设备选择 -->
		<view class="device-selection">
			<text class="section-title">选择设备</text>
			<view class="device-search">
				<input class="search-input" 
					v-model="searchKeyword" 
					placeholder="搜索设备名称或编号"
					@input="onSearch" />
				<button class="scan-btn" @click="scanDevice">
					<text class="scan-icon">📷</text>
					<text class="scan-text">扫码</text>
				</button>
			</view>
			
			<view class="device-list" v-if="filteredDevices.length > 0">
				<view class="device-item" 
					v-for="device in filteredDevices" 
					:key="device.id"
					:class="{selected: selectedDevice?.id === device.id}"
					@click="selectDevice(device)">
					<image class="device-thumb" :src="device.image" mode="aspectFill"></image>
					<view class="device-info">
						<text class="device-name">{{device.name}}</text>
						<text class="device-type">{{device.type}}</text>
						<text class="device-location">📍 {{device.location}}</text>
					</view>
					<view class="device-status" :class="device.status">
						<text class="status-text">{{getStatusText(device.status)}}</text>
					</view>
				</view>
			</view>
		</view>

		<!-- 故障描述 -->
		<view class="fault-description" v-if="selectedDevice">
			<text class="section-title">故障描述</text>
			<view class="fault-form">
				<view class="form-item">
					<text class="form-label">故障现象</text>
					<textarea class="fault-input" 
						v-model="faultDescription" 
						placeholder="请详细描述设备故障现象，如异常声音、错误提示、功能失效等"
						maxlength="500">
					</textarea>
					<text class="char-count">{{faultDescription.length}}/500</text>
				</view>

				<view class="form-item">
					<text class="form-label">紧急程度</text>
					<view class="urgency-options">
						<view class="urgency-item" 
							v-for="urgency in urgencyLevels" 
							:key="urgency.value"
							:class="{active: selectedUrgency === urgency.value}"
							@click="selectedUrgency = urgency.value">
							<text class="urgency-icon">{{urgency.icon}}</text>
							<text class="urgency-text">{{urgency.label}}</text>
						</view>
					</view>
				</view>

				<view class="form-item">
					<text class="form-label">联系方式</text>
					<input class="contact-input" 
						v-model="contact" 
						placeholder="请输入手机号码"
						type="number">
				</view>

				<view class="form-item">
					<text class="form-label">故障图片</text>
					<view class="image-upload">
						<view class="upload-grid">
							<view class="image-item" v-for="(image, index) in uploadedImages" :key="index">
								<image class="uploaded-image" :src="image" mode="aspectFill"></image>
								<view class="delete-btn" @click="deleteImage(index)">
									<text class="delete-icon">✕</text>
								</view>
							</view>
							<view class="upload-btn" v-if="uploadedImages.length < 3" @click="uploadImage">
								<text class="upload-icon">📷</text>
								<text class="upload-text">添加图片</text>
							</view>
						</view>
						<text class="upload-tip">最多上传3张图片，有助于快速定位问题</text>
					</view>
				</view>
			</view>
		</view>

		<!-- 报修摘要 -->
		<view class="repair-summary" v-if="isFormValid">
			<text class="summary-title">报修摘要</text>
			<view class="summary-item">
				<text class="summary-label">设备：</text>
				<text class="summary-value">{{selectedDevice.name}}</text>
			</view>
			<view class="summary-item">
				<text class="summary-label">类型：</text>
				<text class="summary-value">{{getSelectedTypeText()}}</text>
			</view>
			<view class="summary-item">
				<text class="summary-label">紧急程度：</text>
				<text class="summary-value">{{getSelectedUrgencyText()}}</text>
			</view>
		</view>

		<!-- 提交按钮 -->
		<view class="submit-section">
			<button class="submit-btn" :disabled="!isFormValid" @click="submitRepair">
				提交报修
			</button>
			<text class="submit-tip">提交后将立即通知维修人员处理</text>
		</view>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				selectedType: null,
				selectedDevice: null,
				searchKeyword: '',
				faultDescription: '',
				selectedUrgency: 'medium',
				contact: '',
				uploadedImages: [],
				repairTypes: [
					{id: 1, name: '硬件故障', icon: '🔧'},
					{id: 2, name: '软件问题', icon: '💻'},
					{id: 3, name: '清洁维护', icon: '🧽'},
					{id: 4, name: '校准调试', icon: '⚙️'},
					{id: 5, name: '配件更换', icon: '🔩'},
					{id: 6, name: '其他问题', icon: '❓'}
				],
				urgencyLevels: [
					{value: 'low', label: '一般', icon: '🟢'},
					{value: 'medium', label: '紧急', icon: '🟡'},
					{value: 'high', label: '非常紧急', icon: '🔴'}
				],
				devices: [
					{
						id: 1,
						name: '光学显微镜-01',
						type: '显微镜',
						location: 'A101实验室',
						status: 'using',
						image: '/static/devices/microscope.jpg'
					},
					{
						id: 2,
						name: '高速离心机-02',
						type: '离心机',
						location: 'A102实验室',
						status: 'maintenance',
						image: '/static/devices/centrifuge.jpg'
					},
					{
						id: 3,
						name: 'CO2培养箱-03',
						type: '培养箱',
						location: 'A103实验室',
						status: 'available',
						image: '/static/devices/incubator.jpg'
					}
				]
			}
		},
		computed: {
			filteredDevices() {
				if (!this.searchKeyword) return this.devices
				return this.devices.filter(device => 
					device.name.includes(this.searchKeyword) || 
					device.type.includes(this.searchKeyword) ||
					device.location.includes(this.searchKeyword)
				)
			},
			isFormValid() {
				return this.selectedType && 
					   this.selectedDevice && 
					   this.faultDescription.trim() && 
					   this.contact.trim()
			}
		},
		methods: {
			onSearch() {
				// 搜索逻辑已在computed中处理
			},
			scanDevice() {
				uni.scanCode({
					success: (res) => {
						console.log('扫码结果:', res.result)
						// 根据扫码结果查找设备
						uni.showToast({
							title: '扫码成功',
							icon: 'success'
						})
					},
					fail: () => {
						uni.showToast({
							title: '扫码失败',
							icon: 'error'
						})
					}
				})
			},
			selectDevice(device) {
				this.selectedDevice = device
			},
			uploadImage() {
				uni.chooseImage({
					count: 3 - this.uploadedImages.length,
					sizeType: ['compressed'],
					sourceType: ['camera', 'album'],
					success: (res) => {
						this.uploadedImages.push(...res.tempFilePaths)
					}
				})
			},
			deleteImage(index) {
				this.uploadedImages.splice(index, 1)
			},
			getStatusText(status) {
				const statusMap = {
					available: '可用',
					using: '使用中',
					maintenance: '维护中'
				}
				return statusMap[status] || '未知'
			},
			getSelectedTypeText() {
				const type = this.repairTypes.find(t => t.id === this.selectedType)
				return type ? type.name : ''
			},
			getSelectedUrgencyText() {
				const urgency = this.urgencyLevels.find(u => u.value === this.selectedUrgency)
				return urgency ? urgency.label : ''
			},
			submitRepair() {
				if (!this.isFormValid) return
				
				uni.showLoading({
					title: '提交中...'
				})
				
				// 模拟API调用
				setTimeout(() => {
					uni.hideLoading()
					uni.showModal({
						title: '报修成功',
						content: '您的报修申请已提交，维修人员将尽快处理',
						showCancel: false,
						success: () => {
							uni.navigateBack()
						}
					})
				}, 1500)
			}
		}
	}
</script>

<style scoped>
	.container {
		padding: 20rpx;
		background-color: #f5f5f5;
		min-height: 100vh;
	}

	.section-title {
		font-size: 32rpx;
		font-weight: bold;
		color: #333;
		margin-bottom: 20rpx;
		display: block;
	}

	.repair-types {
		background: white;
		border-radius: 20rpx;
		padding: 30rpx;
		margin-bottom: 30rpx;
	}

	.type-grid {
		display: grid;
		grid-template-columns: repeat(3, 1fr);
		gap: 20rpx;
	}

	.type-item {
		padding: 25rpx;
		border: 2rpx solid #e0e0e0;
		border-radius: 15rpx;
		display: flex;
		flex-direction: column;
		align-items: center;
		gap: 10rpx;
		transition: all 0.3s;
	}

	.type-item.active {
		border-color: #2979FF;
		background: #E3F2FD;
	}

	.type-icon {
		font-size: 40rpx;
	}

	.type-name {
		font-size: 24rpx;
		color: #333;
		text-align: center;
	}

	.device-selection {
		background: white;
		border-radius: 20rpx;
		padding: 30rpx;
		margin-bottom: 30rpx;
	}

	.device-search {
		display: flex;
		gap: 20rpx;
		margin-bottom: 30rpx;
	}

	.search-input {
		flex: 1;
		padding: 20rpx 30rpx;
		border: 2rpx solid #e0e0e0;
		border-radius: 50rpx;
		font-size: 28rpx;
	}

	.scan-btn {
		padding: 20rpx 30rpx;
		background: #2979FF;
		color: white;
		border-radius: 50rpx;
		display: flex;
		align-items: center;
		gap: 10rpx;
		border: none;
		font-size: 24rpx;
	}

	.scan-icon {
		font-size: 28rpx;
	}

	.device-list {
		display: flex;
		flex-direction: column;
		gap: 15rpx;
	}

	.device-item {
		display: flex;
		align-items: center;
		padding: 20rpx;
		border: 2rpx solid #e0e0e0;
		border-radius: 15rpx;
		gap: 20rpx;
		transition: all 0.3s;
	}

	.device-item.selected {
		border-color: #2979FF;
		background: #E3F2FD;
	}

	.device-thumb {
		width: 80rpx;
		height: 80rpx;
		border-radius: 10rpx;
		background: #f0f0f0;
	}

	.device-info {
		flex: 1;
		display: flex;
		flex-direction: column;
		gap: 5rpx;
	}

	.device-name {
		font-size: 28rpx;
		font-weight: bold;
		color: #333;
	}

	.device-type {
		font-size: 24rpx;
		color: #666;
	}

	.device-location {
		font-size: 22rpx;
		color: #999;
	}

	.device-status {
		padding: 8rpx 16rpx;
		border-radius: 20rpx;
		font-size: 22rpx;
	}

	.device-status.available {
		background: #E8F5E8;
		color: #4CAF50;
	}

	.device-status.using {
		background: #FFF3E0;
		color: #FF9800;
	}

	.device-status.maintenance {
		background: #FFEBEE;
		color: #F44336;
	}

	.fault-description {
		background: white;
		border-radius: 20rpx;
		padding: 30rpx;
		margin-bottom: 30rpx;
	}

	.fault-form {
		display: flex;
		flex-direction: column;
		gap: 30rpx;
	}

	.form-item {
		display: flex;
		flex-direction: column;
		gap: 15rpx;
	}

	.form-label {
		font-size: 28rpx;
		font-weight: bold;
		color: #333;
	}

	.fault-input {
		min-height: 150rpx;
		padding: 20rpx;
		border: 2rpx solid #e0e0e0;
		border-radius: 15rpx;
		font-size: 26rpx;
		line-height: 1.5;
	}

	.char-count {
		font-size: 22rpx;
		color: #999;
		text-align: right;
	}

	.urgency-options {
		display: flex;
		gap: 15rpx;
	}

	.urgency-item {
		flex: 1;
		padding: 20rpx;
		border: 2rpx solid #e0e0e0;
		border-radius: 15rpx;
		display: flex;
		flex-direction: column;
		align-items: center;
		gap: 8rpx;
		transition: all 0.3s;
	}

	.urgency-item.active {
		border-color: #2979FF;
		background: #E3F2FD;
	}

	.urgency-icon {
		font-size: 32rpx;
	}

	.urgency-text {
		font-size: 24rpx;
		color: #333;
	}

	.contact-input {
		padding: 25rpx 30rpx;
		border: 2rpx solid #e0e0e0;
		border-radius: 15rpx;
		font-size: 28rpx;
	}

	.image-upload {
		display: flex;
		flex-direction: column;
		gap: 15rpx;
	}

	.upload-grid {
		display: grid;
		grid-template-columns: repeat(3, 1fr);
		gap: 15rpx;
	}

	.image-item {
		position: relative;
		aspect-ratio: 1;
	}

	.uploaded-image {
		width: 100%;
		height: 100%;
		border-radius: 10rpx;
	}

	.delete-btn {
		position: absolute;
		top: -10rpx;
		right: -10rpx;
		width: 40rpx;
		height: 40rpx;
		background: #F44336;
		border-radius: 50%;
		display: flex;
		align-items: center;
		justify-content: center;
	}

	.delete-icon {
		font-size: 20rpx;
		color: white;
	}

	.upload-btn {
		aspect-ratio: 1;
		border: 2rpx dashed #ccc;
		border-radius: 10rpx;
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;
		gap: 10rpx;
		background: #fafafa;
	}

	.upload-icon {
		font-size: 40rpx;
		color: #999;
	}

	.upload-text {
		font-size: 22rpx;
		color: #999;
	}

	.upload-tip {
		font-size: 22rpx;
		color: #999;
		text-align: center;
	}

	.repair-summary {
		background: white;
		border-radius: 20rpx;
		padding: 30rpx;
		margin-bottom: 30rpx;
		border-left: 8rpx solid #2979FF;
	}

	.summary-title {
		font-size: 28rpx;
		font-weight: bold;
		color: #333;
		margin-bottom: 20rpx;
		display: block;
	}

	.summary-item {
		display: flex;
		margin-bottom: 15rpx;
	}

	.summary-label {
		font-size: 26rpx;
		color: #666;
		width: 120rpx;
	}

	.summary-value {
		font-size: 26rpx;
		color: #333;
		flex: 1;
	}

	.submit-section {
		padding: 0 20rpx 40rpx;
	}

	.submit-btn {
		width: 100%;
		padding: 30rpx;
		background: #2979FF;
		color: white;
		border-radius: 15rpx;
		font-size: 32rpx;
		font-weight: bold;
		border: none;
		margin-bottom: 20rpx;
	}

	.submit-btn:disabled {
		background: #ccc;
	}

	.submit-tip {
		font-size: 24rpx;
		color: #999;
		text-align: center;
		display: block;
	}
</style>