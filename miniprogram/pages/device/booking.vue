<template>
	<view class="container">
		<!-- 自定义导航栏 -->
		<view class="custom-navbar">
			<view class="navbar-left">
				<image class="back-icon" src="http://szzgurngv.hn-bkt.clouddn.com/icon/back.png" @click="goBack" mode="aspectFit"></image>
			</view>
			<text class="navbar-title">设备预约</text>
			<view class="navbar-right">
			</view>
		</view>

		<!-- 预约表单 -->
		<view class="booking-form" v-if="selectedDevice">
			<view class="form-section">
				<text class="section-title">选择日期</text>
				<view class="date-picker">
					<picker mode="date" :value="bookingDate" @change="onDateChange" :start="today">
						<view class="picker-input">
							<text class="picker-text">{{bookingDate || '请选择日期'}}</text>
							<text class="picker-icon">📅</text>
						</view>
					</picker>
				</view>
			</view>

			<view class="form-section">
				<text class="section-title">选择时间段</text>
				<view class="time-slots">
					<view class="time-slot" 
						v-for="slot in timeSlots" 
						:key="slot.id"
						:class="{active: selectedTimeSlot === slot.id, disabled: slot.booked}"
						@click="selectTimeSlot(slot)">
						<text class="slot-time">{{slot.time}}</text>
						<text class="slot-status" v-if="slot.booked">已预约</text>
					</view>
				</view>
			</view>

			<view class="form-section">
				<text class="section-title">使用时长</text>
				<view class="duration-options">
					<view class="duration-option" 
						v-for="duration in durations" 
						:key="duration.value"
						:class="{active: selectedDuration === duration.value}"
						@click="selectedDuration = duration.value">
						<text class="duration-text">{{duration.label}}</text>
					</view>
				</view>
			</view>

			<view class="form-section">
				<text class="section-title">使用目的</text>
				<textarea class="purpose-input" 
					v-model="purpose" 
					placeholder="请简要描述设备使用目的和实验内容"
					maxlength="200">
				</textarea>
				<text class="char-count">{{purpose.length}}/200</text>
			</view>


		</view>

		<!-- 预约摘要 -->
		<view class="booking-summary" v-if="isFormValid">
			<text class="summary-title">预约摘要</text>
			<view class="summary-item">
				<text class="summary-label">设备：</text>
				<text class="summary-value">{{selectedDevice.name}}</text>
			</view>
			<view class="summary-item">
				<text class="summary-label">时间：</text>
				<text class="summary-value">{{bookingDate}} {{getSelectedTimeSlotText()}}</text>
			</view>
			<view class="summary-item">
				<text class="summary-label">时长：</text>
				<text class="summary-value">{{getDurationText()}}</text>
			</view>
		</view>

		<!-- 提交按钮 -->
		<view class="submit-section">
			<button class="submit-btn" :disabled="!isFormValid" @click="submitBooking">
				确认预约
			</button>
			<text class="submit-tip">预约成功后将发送微信通知</text>
		</view>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				selectedDevice: null,
				bookingDate: '',
				selectedTimeSlot: null,
				selectedDuration: 1,
				purpose: '',
				today: '',

				timeSlots: [
					{id: 1, time: '08:00-10:00', booked: false},
					{id: 2, time: '10:00-12:00', booked: true},
					{id: 3, time: '14:00-16:00', booked: false},
					{id: 4, time: '16:00-18:00', booked: false},
					{id: 5, time: '19:00-21:00', booked: false}
				],
				durations: [
					{value: 1, label: '1小时'},
					{value: 2, label: '2小时'},
					{value: 3, label: '3小时'},
					{value: 4, label: '4小时'}
				]
			}
		},
		computed: {
			isFormValid() {
				return this.selectedDevice && 
					   this.selectedDevice.status === 'available' &&
					   this.bookingDate && 
					   this.selectedTimeSlot && 
					   this.purpose.trim()
			}
		},
		onLoad(options) {
			// 设置今天的日期
			const today = new Date()
			this.today = today.toISOString().split('T')[0]
			
			// 如果有传入设备信息，设置选中的设备
			if (options.deviceId && options.deviceName) {
				this.selectedDevice = {
					id: options.deviceId,
					name: decodeURIComponent(options.deviceName),
					type: options.deviceType ? decodeURIComponent(options.deviceType) : '',
					location: options.deviceLocation ? decodeURIComponent(options.deviceLocation) : '',
					status: options.deviceStatus || 'available'
				}
			}
		},
		methods: {
			onDateChange(e) {
				this.bookingDate = e.detail.value
				// 重置时间段选择
				this.selectedTimeSlot = null
				// 这里可以根据日期获取可用时间段
				this.loadTimeSlots()
			},
			loadTimeSlots() {
				// 模拟加载时间段数据
				// 实际应该调用API获取指定日期的可用时间段
				console.log('加载时间段数据')
			},
			selectTimeSlot(slot) {
				if (slot.booked) return
				this.selectedTimeSlot = slot.id
			},

			goBack() {
				uni.navigateBack()
			},
			getSelectedTimeSlotText() {
				const slot = this.timeSlots.find(s => s.id === this.selectedTimeSlot)
				return slot ? slot.time : ''
			},
			getDurationText() {
				const duration = this.durations.find(d => d.value === this.selectedDuration)
				return duration ? duration.label : ''
			},
			goToDeviceSelection() {
				uni.navigateTo({
					url: '/pages/device/device-selection'
				})
			},
			submitBooking() {
				if (!this.isFormValid) return
				
				uni.showLoading({
					title: '提交中...'
				})
				
				// 模拟API调用
				setTimeout(() => {
					uni.hideLoading()
					uni.showModal({
						title: '预约成功',
						content: '您的设备预约已提交，请按时使用设备',
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
		padding: 0;
		background: #e3f2fd;
		min-height: 100vh;
	}

	/* 自定义导航栏 */
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

	/* 设备信息显示 */
	.device-info-section {
		padding: 30rpx;
		background: #f5f5f5;
		border-radius: 30rpx 30rpx 0 0;
		margin-top: -30rpx;
	}

	.device-selector {
		background: white;
		border-radius: 20rpx;
		padding: 30rpx;
		margin-bottom: 30rpx;
		border: 2rpx solid #e0e0e0;
		transition: all 0.3s ease;
	}

	.device-selector:active {
		background: #f8f8f8;
	}

	.selector-content {
		display: flex;
		justify-content: space-between;
		align-items: center;
	}

	.selector-left {
		flex: 1;
	}

	.selector-title {
		font-size: 28rpx;
		font-weight: bold;
		color: #333;
		display: block;
		margin-bottom: 8rpx;
	}

	.selector-subtitle {
		font-size: 24rpx;
		color: #666;
		display: block;
	}

	.selector-arrow {
		font-size: 32rpx;
		color: #999;
		font-weight: bold;
	}

	.device-image {
		width: 120rpx;
		height: 120rpx;
		border-radius: 15rpx;
		background: #f0f0f0;
	}

	.device-details {
		flex: 1;
		display: flex;
		flex-direction: column;
		gap: 8rpx;
	}

	.device-name {
		font-size: 32rpx;
		font-weight: bold;
		color: #333;
	}

	.device-type {
		font-size: 26rpx;
		color: #666;
	}

	.device-location {
		font-size: 24rpx;
		color: #999;
	}

	.device-status {
		padding: 8rpx 16rpx;
		border-radius: 20rpx;
		font-size: 22rpx;
		align-self: flex-start;
		margin-top: 10rpx;
	}

	.device-status.available {
		background: #E8F5E8;
		color: #4CAF50;
	}



	.booking-form {
		background: white;
		border-radius: 20rpx;
		padding: 30rpx;
		margin: 30rpx 30rpx 30rpx;
	}

	.form-section {
		margin-bottom: 40rpx;
	}

	.form-section:last-child {
		margin-bottom: 0;
	}

	.section-title {
		font-size: 28rpx;
		font-weight: bold;
		color: #333;
		margin-bottom: 20rpx;
		display: block;
	}

	.date-picker {
		border: 2rpx solid #e0e0e0;
		border-radius: 15rpx;
		overflow: hidden;
	}

	.picker-input {
		display: flex;
		justify-content: space-between;
		align-items: center;
		padding: 25rpx 30rpx;
		background: white;
	}

	.picker-text {
		font-size: 28rpx;
		color: #333;
	}

	.picker-icon {
		font-size: 28rpx;
		color: #666;
	}

	.time-slots {
		display: grid;
		grid-template-columns: 1fr 1fr;
		gap: 15rpx;
	}

	.time-slot {
		padding: 25rpx;
		border: 2rpx solid #e0e0e0;
		border-radius: 15rpx;
		text-align: center;
		position: relative;
	}

	.time-slot.active {
		border-color: #2196F3;
		background: #E3F2FD;
	}

	.time-slot.disabled {
		background: #f5f5f5;
		color: #ccc;
	}

	.slot-time {
		font-size: 26rpx;
		color: #333;
		display: block;
	}

	.slot-status {
		font-size: 20rpx;
		color: #999;
		margin-top: 5rpx;
		display: block;
	}

	.duration-options {
		display: flex;
		gap: 15rpx;
	}

	.duration-option {
		flex: 1;
		padding: 20rpx;
		border: 2rpx solid #e0e0e0;
		border-radius: 15rpx;
		text-align: center;
	}

	.duration-option.active {
		border-color: #2196F3;
		background: #E3F2FD;
	}

	.duration-text {
		font-size: 26rpx;
		color: #333;
	}

	.purpose-input {
		width: 100%;
		min-height: 150rpx;
		padding: 20rpx;
		margin: 0;
		border: 2rpx solid #e0e0e0;
		border-radius: 15rpx;
		font-size: 26rpx;
		line-height: 1.5;
		resize: none;
		box-sizing: border-box;
	}

	.char-count {
		font-size: 22rpx;
		color: #999;
		text-align: right;
		margin-top: 10rpx;
		display: block;
	}



	.booking-summary {
		background: white;
		border-radius: 20rpx;
		padding: 30rpx;
		margin: 0 30rpx 30rpx;
		border-left: 8rpx solid #2196F3;
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
		padding: 0 30rpx 40rpx;
	}

	.submit-btn {
		width: 100%;
		padding: 30rpx;
		background: #2196F3;
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