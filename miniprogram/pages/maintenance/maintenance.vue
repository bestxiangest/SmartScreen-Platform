<template>
	<view class="container">
		<!-- 自定义导航栏 -->
		<view class="custom-navbar fade-in-down">
			<view class="navbar-left">
				<image class="back-icon card-hover" src="http://szzgurngv.hn-bkt.clouddn.com/icon/back.png" @click="goBack" mode="aspectFit"></image>
			</view>
			<text class="navbar-title">维保调度中心</text>
			<view class="navbar-right">
			</view>
		</view>

		<!-- 状态卡片组 -->
		<view class="stats-section fade-in-up" style="animation-delay: 0.1s;">
			<view class="stats-item card-hover scale-in" style="animation-delay: 0.2s;">
				<image class="stats-icon floating" src="http://szzgurngv.hn-bkt.clouddn.com/icon/week.png" mode="aspectFit"></image>
				<view class="stats-content">
					<text class="stats-number">12</text>
					<text class="stats-label">本周报修</text>
				</view>
			</view>
			<view class="stats-item card-hover scale-in" style="animation-delay: 0.3s;">
				<image class="stats-icon floating" src="http://szzgurngv.hn-bkt.clouddn.com/icon/month.png" mode="aspectFit"></image>
				<view class="stats-content">
					<text class="stats-number">45</text>
					<text class="stats-label">本月报修</text>
				</view>
			</view>
			<view class="stats-item card-hover scale-in" style="animation-delay: 0.4s;">
				<image class="stats-icon floating" src="http://szzgurngv.hn-bkt.clouddn.com/icon/xiangying.png" mode="aspectFit"></image>
				<view class="stats-content">
					<text class="stats-number">2.4h</text>
					<text class="stats-label">响应时效</text>
				</view>
			</view>
		</view>

		<!-- 功能区 -->
		<view class="function-section fade-in-up" style="animation-delay: 0.2s;">
			<view class="function-item card-hover scale-in" style="animation-delay: 0.5s;" @click="navigateTo('/pages/maintenance/fault-report')">
				<image class="function-icon icon-bounce" src="http://szzgurngv.hn-bkt.clouddn.com/icon/gzshenbao.png" mode="aspectFit"></image>
				<text class="function-title">故障申报</text>
			</view>
			<view class="function-item card-hover scale-in" style="animation-delay: 0.6s;" @click="navigateTo('/pages/maintenance/work-order')">
				<image class="function-icon icon-bounce" src="http://szzgurngv.hn-bkt.clouddn.com/icon/gongdan.png" mode="aspectFit"></image>
				<text class="function-title">工单管理</text>
			</view>
		</view>

		<!-- 工单表格 -->
		<view class="table-section fade-in-up" style="animation-delay: 0.3s;">
			<view class="table-header">
				<text class="header-cell device-name">设备名称</text>
				<text class="header-cell fault-type">故障类型</text>
				<text class="header-cell report-time">申报时间</text>
				<text class="header-cell status">状态</text>
				<text class="header-cell actions">操作</text>
			</view>
			<view class="table-body">
				<view class="table-row card-hover fade-in-up" v-for="(order, index) in workOrders" :key="index" :style="{animationDelay: (0.7 + index * 0.1) + 's'}">
					<text class="table-cell device-name">{{order.deviceName}}</text>
					<text class="table-cell fault-type">{{order.faultType}}</text>
					<text class="table-cell report-time">{{order.reportTime}}</text>
					<view class="table-cell status">
						<view class="status-indicator floating" :class="getStatusClass(order.status)"></view>
					</view>
					<view class="table-cell actions">
						<button class="action-btn detail-btn btn-hover" @click="viewDetail(order)">详情</button>
						<button class="action-btn remind-btn btn-hover" 
							v-if="order.reporter === currentUser && order.status !== 'completed'" 
							@click="remindOrder(order)">催办</button>
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
			statusBarHeight: 0,
			currentUser: '张三', // 当前登录用户
			workOrders: [
				{
					deviceId: 'DEV001',
					deviceName: '激光切割机',
					faultType: '电源故障',
					reportTime: '2025-07-15 09:30',
					status: 'pending',
					reporter: '张三'
				},
				{
					deviceId: 'DEV002',
					deviceName: '3D打印机',
					faultType: '网络异常',
					reportTime: '2025-07-15 10:15',
					status: 'processing',
					reporter: '李四'
				},
				{
					deviceId: 'DEV003',
					deviceName: '数控铣床',
					faultType: '硬件损坏',
					reportTime: '2025-07-14 16:20',
					status: 'completed',
					reporter: '王五'
				},
				{
					deviceId: 'DEV004',
					deviceName: '示波器',
					faultType: '软件异常',
					reportTime: '2025-07-15 11:45',
					status: 'pending',
					reporter: '赵六'
				},
				{
					deviceId: 'DEV005',
					deviceName: '热风枪',
					faultType: '温度过高',
					reportTime: '2024-07-15 14:30',
					status: 'processing',
					reporter: '张三'
				}
			]
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
		navigateTo(url) {
			uni.navigateTo({ url })
		},
		getStatusClass(status) {
			return {
				'status-pending': status === 'pending',
				'status-processing': status === 'processing',
				'status-completed': status === 'completed'
			}
		},
		remindOrder(order) {
			uni.showToast({
				title: `已催办工单 ${order.deviceId}`,
				icon: 'success'
			})
		},
		viewDetail(order) {
			uni.navigateTo({
				url: `/pages/maintenance/detail?deviceId=${order.deviceId}`
			})
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

/* 状态卡片组 */
.stats-section {
	display: flex;
	padding: 20rpx 30rpx 30rpx;
	gap: 20rpx;
}

.stats-item {
	flex: 1;
	background: white;
	border-radius: 16rpx;
	padding: 30rpx 20rpx;
	display: flex;
	align-items: center;
	gap: 15rpx;
	box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.1);
}

.stats-item.urgent {
	border-left: 6rpx solid #f44336;
}

.stats-icon {
	width: 60rpx;
	height: 60rpx;
}

.stats-content {
	display: flex;
	flex-direction: column;
	gap: 5rpx;
}

.stats-number {
	font-size: 32rpx;
	font-weight: bold;
	color: #2196F3;
}

.stats-label {
	font-size: 24rpx;
	color: #666;
}

/* 功能区 */
.function-section {
	display: flex;
	padding: 0 30rpx 30rpx;
	gap: 20rpx;
}

.function-item {
	flex: 1;
	background: white;
	border-radius: 16rpx;
	padding: 30rpx 20rpx;
	display: flex;
	flex-direction: column;
	align-items: center;
	gap: 15rpx;
	box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.1);
	transition: transform 0.2s;
}

.function-item:active {
	transform: scale(0.95);
}

.function-icon {
	width: 100rpx;
	height: 100rpx;
}

.function-title {
	font-size: 26rpx;
	color: #333;
	font-weight: 500;
}

/* 表格 */
.table-section {
	margin: 0 30rpx;
	background: white;
	border-radius: 16rpx;
	overflow: hidden;
	box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.1);
}

.table-header {
	display: flex;
	background: #f5f5f5;
	padding: 20rpx 0;
	border-bottom: 2rpx solid #eee;
}

.header-cell {
	font-size: 28rpx;
	font-weight: bold;
	color: #333;
	text-align: center;
}

.table-row {
	display: flex;
	padding: 25rpx 0;
	border-bottom: 1rpx solid #f0f0f0;
	align-items: center;
}

.table-row:last-child {
	border-bottom: none;
}

.table-cell {
	font-size: 26rpx;
	color: #666;
	text-align: center;
	display: flex;
	align-items: center;
	justify-content: center;
}

.device-name {
	width: 20%;
}

.fault-type {
	width: 25%;
}

.report-time {
	width: 25%;
}

.status {
	width: 15%;
}

.actions {
	width: 15%;
	gap: 10rpx;
}

.status-indicator {
	width: 20rpx;
	height: 20rpx;
	border-radius: 50%;
}

.status-pending {
	background: #f44336;
}

.status-processing {
	background: #ff9800;
}

.status-completed {
	background: #4caf50;
}

.action-btn {
	padding: 8rpx 16rpx;
	border-radius: 8rpx;
	font-size: 22rpx;
	border: none;
	cursor: pointer;
}

.remind-btn {
	background: #ff9800;
	color: white;
}

.detail-btn {
	background: #2196F3;
	color: white;
}

/* 动画效果 */
@keyframes fadeInDown {
	from {
		opacity: 0;
		transform: translate3d(0, -30px, 0);
	}
	to {
		opacity: 1;
		transform: translate3d(0, 0, 0);
	}
}

@keyframes fadeInUp {
	from {
		opacity: 0;
		transform: translate3d(0, 30px, 0);
	}
	to {
		opacity: 1;
		transform: translate3d(0, 0, 0);
	}
}

@keyframes scaleIn {
	from {
		opacity: 0;
		transform: scale3d(0.8, 0.8, 1);
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
	animation: fadeInDown 0.6s ease-out;
	will-change: transform, opacity;
}

.fade-in-up {
	animation: fadeInUp 0.6s ease-out;
	will-change: transform, opacity;
}

.scale-in {
	animation: scaleIn 0.5s ease-out;
	will-change: transform, opacity;
}

.floating {
	animation: floating 2s ease-in-out infinite;
	will-change: transform;
}

.icon-bounce {
	animation: iconBounce 1.5s ease-in-out infinite;
	will-change: transform;
}

.card-hover {
	transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
	will-change: transform, box-shadow;
}

.card-hover:hover {
	transform: translate3d(0, -2px, 0);
	box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
}

.card-hover:active {
	transform: translate3d(0, 0, 0);
	box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.btn-hover {
	transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
	will-change: transform, background-color;
}

.btn-hover:hover {
	transform: translate3d(0, -1px, 0);
}

.btn-hover:active {
	transform: scale3d(0.98, 0.98, 1);
}
</style>