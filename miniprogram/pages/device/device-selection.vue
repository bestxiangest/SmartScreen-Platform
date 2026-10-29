<template>
	<view class="container">
	<!-- 自定义导航栏 -->
	<view class="custom-navbar">
		<view class="navbar-left">
			<image class="back-icon" src="http://szzgurngv.hn-bkt.clouddn.com/icon/back.png" @click="goBack" mode="aspectFit"></image>
		</view>
		<text class="navbar-title">选择设备</text>
		<view class="navbar-right">
		</view>
	</view>

		<!-- 设备统计 -->
		<view class="stats-section">
			<view class="stat-card total">
				<text class="stat-number">24</text>
				<text class="stat-label">设备总数</text>
			</view>
			<view class="stat-card available">
				<text class="stat-number">18</text>
				<text class="stat-label">可用</text>
			</view>
			<view class="stat-card in-use">
				<text class="stat-number">4</text>
				<text class="stat-label">使用中</text>
			</view>
		</view>

		<!-- 设备分类 -->
		<view class="device-categories">
			<!-- 测量仪器 -->
			<view class="category-section">
				<text class="category-title">测量仪器</text>
				<view class="device-grid">
					<view class="device-card" 
						v-for="device in measurementDevices" 
						:key="device.id"
						@click="selectDevice(device)">
						<image class="device-image" :src="device.image" mode="aspectFit"></image>
						<text class="device-name">{{device.name}}</text>
						<text class="device-location">{{device.location}}</text>
						<view class="device-status" :class="device.status">
							<text class="status-dot"></text>
						</view>
					</view>
				</view>
			</view>

			<!-- 开发平台 -->
			<view class="category-section">
				<text class="category-title">开发平台</text>
				<view class="device-grid">
					<view class="device-card" 
						v-for="device in developmentDevices" 
						:key="device.id"
						@click="selectDevice(device)">
						<image class="device-image" :src="device.image" mode="aspectFit"></image>
						<text class="device-name">{{device.name}}</text>
						<text class="device-location">{{device.location}}</text>
						<view class="device-status" :class="device.status">
							<text class="status-dot"></text>
						</view>
					</view>
				</view>
			</view>

			<!-- 科学实验 -->
			<view class="category-section">
				<text class="category-title">科学实验</text>
				<view class="device-grid">
					<view class="device-card" 
						v-for="device in experimentDevices" 
						:key="device.id"
						@click="selectDevice(device)">
						<image class="device-image" :src="device.image" mode="aspectFit"></image>
						<text class="device-name">{{device.name}}</text>
						<text class="device-location">{{device.location}}</text>
						<view class="device-status" :class="device.status">
							<text class="status-dot"></text>
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
				measurementDevices: [
				{
					id: 1,
					name: '网络分析仪',
					location: 'A101',
					image: '/static/devices/network-analyzer.png',
					status: 'available'
				},
				{
					id: 2,
					name: '无线网络分析仪',
					location: 'A102',
					image: '/static/devices/wireless-analyzer.png',
					status: 'in_use'
				},
				{
					id: 3,
					name: '示波器',
					location: 'A103',
					image: '/static/devices/oscilloscope.png',
					status: 'available'
				},
				{
					id: 4,
					name: '信号发生器',
					location: 'A104',
					image: '/static/devices/signal-generator.png',
					status: 'available'
				}
			],
				developmentDevices: [
				{
					id: 5,
					name: 'Linux开发板',
					location: 'B101',
					image: '/static/devices/linux-board.png',
					status: 'in_use'
				},
				{
					id: 6,
					name: 'FPGA开发板',
					location: 'B102',
					image: '/static/devices/fpga-board.png',
					status: 'available'
				},
				{
					id: 7,
					name: 'ARM开发板',
					location: 'B103',
					image: '/static/devices/arm-board.png',
					status: 'available'
				},
				{
					id: 8,
					name: 'DSP开发板',
					location: 'B104',
					image: '/static/devices/dsp_objects.jpg',
					status: 'available'
				}
			],
				experimentDevices: [
				{
					id: 9,
					name: '电烙铁',
					location: 'C101',
					image: '/static/devices/soldering-iron.png',
					status: 'available'
				},
				{
					id: 10,
					name: '热风枪',
					location: 'C102',
					image: '/static/devices/heat-gun.png',
					status: 'in_use'
				},
				{
					id: 11,
					name: '加热台',
					location: 'C103',
					image: '/static/devices/heating-plate.png',
					status: 'available'
				},
				{
					id: 12,
					name: '3D打印机',
					location: 'C104',
					image: '/static/devices/3d-printer.png',
					status: 'available'
				}
			]
			}
		},
		methods: {
			goBack() {
				uni.navigateBack()
			},
			selectDevice(device) {
				// 只有可用状态的设备才能选择
				if (device.status !== 'available') {
					uni.showToast({
						title: '该设备当前不可用',
						icon: 'none'
					})
					return
				}
				
				// 跳转到预约页面并传递设备信息
				uni.navigateTo({
					url: `/pages/device/booking?deviceId=${device.id}&deviceName=${encodeURIComponent(device.name)}&deviceLocation=${encodeURIComponent(device.location)}&deviceStatus=${device.status}`
				})
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

	/* 设备统计 */
	.stats-section {
		padding: 30rpx;
		background: #e3f2fd;
		border-radius: 30rpx 30rpx 0 0;
		margin-top: 20rpx;
		display: flex;
		gap: 20rpx;
		margin-bottom: 0;
	}

	.stat-card {
		flex: 1;
		padding: 30rpx 20rpx;
		border-radius: 20rpx;
		text-align: center;
		color: white;
	}

	.stat-card.total {
		background: linear-gradient(135deg, #1976D2 0%, #1565C0 100%);
	}

	.stat-card.available {
		background: linear-gradient(135deg, #4CAF50 0%, #388E3C 100%);
	}

	.stat-card.in-use {
		background: linear-gradient(135deg, #FF9800 0%, #F57C00 100%);
	}

	.stat-number {
		font-size: 48rpx;
		font-weight: bold;
		display: block;
		margin-bottom: 8rpx;
	}

	.stat-label {
		font-size: 24rpx;
		opacity: 0.9;
	}

	/* 设备分类 */
	.device-categories {
		padding: 30rpx 30rpx 30rpx;
		background: #e3f2fd;
	}

	.category-section {
		margin-bottom: 80rpx;
	}

	.category-title {
		font-size: 32rpx;
		font-weight: bold;
		color: #333;
		margin-bottom: 20rpx;
		display: block;
	}

	.device-grid {
		display: grid;
		grid-template-columns: 1fr 1fr 1fr 1fr;
		gap: 20rpx;
	}

	.device-card {
		background: white;
		border-radius: 20rpx;
		padding: 30rpx 20rpx;
		text-align: center;
		position: relative;
		border: 2rpx solid transparent;
		transition: all 0.3s ease;
	}

	.device-card:active {
		transform: scale(0.95);
		box-shadow: 0 4rpx 15rpx rgba(33, 150, 243, 0.2);
	}

	.device-image {
		width: 80rpx;
		height: 80rpx;
		margin-bottom: 15rpx;
	}

	.device-name {
		font-size: 24rpx;
		font-weight: bold;
		color: #333;
		display: block;
		margin-bottom: 8rpx;
	}

	.device-location {
		font-size: 20rpx;
		color: #666;
		display: block;
		margin-bottom: 15rpx;
	}

	.device-status {
		position: absolute;
		top: 15rpx;
		right: 15rpx;
		width: 20rpx;
		height: 20rpx;
		border-radius: 50%;
	}

	.device-status.available {
		background: #4CAF50;
	}

	.device-status.in_use {
		background: #FF9800;
	}

	.device-status.maintenance {
		background: #F44336;
	}

	.status-dot {
		display: block;
		width: 100%;
		height: 100%;
		border-radius: 50%;
		background: inherit;
	}
</style>