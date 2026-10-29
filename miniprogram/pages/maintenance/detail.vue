<template>
	<view class="container">
		<!-- 自定义导航栏 -->
		<view class="custom-navbar">
			<view class="navbar-left">
				<image class="back-icon" src="http://szzgurngv.hn-bkt.clouddn.com/icon/back.png" @click="goBack" mode="aspectFit"></image>
			</view>
			<text class="navbar-title">工单详情</text>
			<view class="navbar-right">
			</view>
		</view>

		<!-- 工单详情 -->
		<view class="detail-section">
			<view class="detail-card">
				<view class="detail-header">
					<view class="order-info">
						<text class="order-id">工单号: {{orderDetail.deviceId}}</text>
						<view class="status-badge" :class="getStatusClass(orderDetail.status)">
							<text class="status-text">{{getStatusText(orderDetail.status)}}</text>
						</view>
					</view>
					<text class="report-time">{{orderDetail.reportTime}}</text>
				</view>

				<view class="detail-content">
					<view class="content-row">
						<text class="label">故障设备:</text>
						<text class="value">{{orderDetail.deviceName}}</text>
					</view>
					<view class="content-row">
						<text class="label">故障类型:</text>
						<text class="value">{{orderDetail.faultType}}</text>
					</view>
					<view class="content-row">
						<text class="label">申报人:</text>
						<text class="value">{{orderDetail.reporter}}</text>
					</view>
					<view class="content-row" v-if="orderDetail.assignee">
						<text class="label">负责人:</text>
						<text class="value">{{orderDetail.assignee}}</text>
					</view>
					<view class="content-row">
						<text class="label">故障描述:</text>
						<text class="value description">{{orderDetail.description}}</text>
					</view>
					<view class="content-row" v-if="orderDetail.completedTime">
						<text class="label">完成时间:</text>
						<text class="value">{{orderDetail.completedTime}}</text>
					</view>
				</view>

				<!-- 操作按钮 -->
				<view class="action-section" v-if="orderDetail.reporter === currentUser && orderDetail.status !== 'completed'">
					<button class="action-btn urge-btn" @click="urgeOrder">
						催办工单
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
			currentUser: '张三', // 当前登录用户
			orderDetail: {}
		}
	},
	onLoad(options) {
		const systemInfo = uni.getSystemInfoSync()
		this.statusBarHeight = systemInfo.statusBarHeight
		
		// 根据传入的deviceId获取工单详情
		if (options.deviceId) {
			this.loadOrderDetail(options.deviceId)
		}
	},
	methods: {
		goBack() {
			uni.navigateBack()
		},
		loadOrderDetail(deviceId) {
			// 模拟从服务器获取工单详情
			const mockOrders = [
				{
					deviceId: 'DEV001',
					deviceName: '激光切割机',
					faultType: '电源故障',
					reportTime: '2024-01-15 09:30',
					status: 'pending',
					reporter: '张三',
					assignee: '',
					description: '设备无法正常启动，电源指示灯不亮，疑似电源模块故障。已检查电源线连接正常，问题可能出现在内部电源模块。'
				},
				{
					deviceId: 'DEV002',
					deviceName: '3D打印机',
					faultType: '网络异常',
					reportTime: '2024-01-15 10:15',
					status: 'processing',
					reporter: '李四',
					assignee: '王工',
					description: '打印机无法连接到实验室WiFi网络，尝试重启设备和路由器后问题依然存在。'
				},
				{
					deviceId: 'DEV003',
					deviceName: '数控铣床',
					faultType: '硬件损坏',
					reportTime: '2024-01-14 16:20',
					status: 'completed',
					reporter: '王五',
					assignee: '李工',
					description: '主轴轴承磨损严重，运行时有异响，加工精度下降。',
					completedTime: '2024-01-15 14:30'
				},
				{
					deviceId: 'DEV004',
					deviceName: '示波器',
					faultType: '软件异常',
					reportTime: '2024-01-15 11:45',
					status: 'pending',
					reporter: '赵六',
					assignee: '',
					description: '设备开机后软件界面卡死，无法进行任何操作，重启后问题依然存在。'
				},
				{
					deviceId: 'DEV005',
					deviceName: '热风枪',
					faultType: '温度过高',
					reportTime: '2024-01-15 14:30',
					status: 'processing',
					reporter: '张三',
					assignee: '赵工',
					description: '设备温度控制异常，无法调节到指定温度，温度显示异常。'
				}
			]
			
			const order = mockOrders.find(item => item.deviceId === deviceId)
			if (order) {
				this.orderDetail = order
			} else {
				uni.showToast({
					title: '工单不存在',
					icon: 'error'
				})
				setTimeout(() => {
					uni.navigateBack()
				}, 1500)
			}
		},
		getStatusClass(status) {
			return {
				'status-pending': status === 'pending',
				'status-processing': status === 'processing',
				'status-completed': status === 'completed'
			}
		},
		getStatusText(status) {
			const statusMap = {
				pending: '待处理',
				processing: '处理中',
				completed: '已完成'
			}
			return statusMap[status] || '未知'
		},
		urgeOrder() {
			uni.showModal({
				title: '催办工单',
				content: `确定要催办工单 ${this.orderDetail.deviceId} 吗？`,
				success: (res) => {
					if (res.confirm) {
						uni.showToast({
							title: '催办成功',
							icon: 'success'
						})
					}
				}
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

/* 详情区域 */
.detail-section {
	padding: 20rpx 30rpx 30rpx;
}

.detail-card {
	background: white;
	border-radius: 16rpx;
	padding: 40rpx 30rpx;
	box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.1);
}

.detail-header {
	display: flex;
	justify-content: space-between;
	align-items: flex-start;
	margin-bottom: 30rpx;
	padding-bottom: 20rpx;
	border-bottom: 2rpx solid #f0f0f0;
}

.order-info {
	display: flex;
	align-items: center;
	gap: 15rpx;
}

.order-id {
	font-size: 32rpx;
	font-weight: bold;
	color: #333;
}

.status-badge {
	padding: 8rpx 16rpx;
	border-radius: 20rpx;
	font-size: 22rpx;
}

.status-pending {
	background: #ffebee;
	color: #f44336;
}

.status-processing {
	background: #fff3e0;
	color: #ff9800;
}

.status-completed {
	background: #e8f5e8;
	color: #4caf50;
}

.report-time {
	font-size: 24rpx;
	color: #999;
}

.detail-content {
	margin-bottom: 30rpx;
}

.content-row {
	display: flex;
	margin-bottom: 20rpx;
	align-items: flex-start;
}

.label {
	font-size: 28rpx;
	color: #666;
	width: 160rpx;
	flex-shrink: 0;
	font-weight: 500;
}

.value {
	font-size: 28rpx;
	color: #333;
	flex: 1;
	line-height: 1.5;
}

.description {
	line-height: 1.6;
	color: #555;
}

.action-section {
	padding-top: 20rpx;
	border-top: 2rpx solid #f0f0f0;
	display: flex;
	justify-content: center;
}

.action-btn {
	padding: 20rpx 40rpx;
	border-radius: 12rpx;
	font-size: 28rpx;
	border: none;
	cursor: pointer;
	transition: all 0.3s;
}

.urge-btn {
	background: #ff9800;
	color: white;
}

.urge-btn:active {
	background: #f57c00;
}
</style>