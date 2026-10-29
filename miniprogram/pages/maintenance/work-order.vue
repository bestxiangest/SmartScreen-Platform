<template>
	<view class="container">
		<!-- 自定义导航栏 -->
		<view class="custom-navbar">
			<view class="navbar-left">
				<image class="back-icon" src="http://szzgurngv.hn-bkt.clouddn.com/icon/back.png" @click="goBack" mode="aspectFit"></image>
			</view>
			<text class="navbar-title">工单管理</text>
			<view class="navbar-right">
			</view>
		</view>

		<!-- 状态筛选 -->
		<view class="filter-section">
			<view class="filter-tabs">
				<view class="filter-tab" 
					:class="{active: currentFilter === 'all'}" 
					@click="setFilter('all')">
					<text class="tab-text">全部</text>
					<text class="tab-count">({{allOrders.length}})</text>
				</view>
				<view class="filter-tab" 
					:class="{active: currentFilter === 'pending'}" 
					@click="setFilter('pending')">
					<text class="tab-text">待处理</text>
					<text class="tab-count">({{pendingCount}})</text>
				</view>
				<view class="filter-tab" 
					:class="{active: currentFilter === 'processing'}" 
					@click="setFilter('processing')">
					<text class="tab-text">处理中</text>
					<text class="tab-count">({{processingCount}})</text>
				</view>
				<view class="filter-tab" 
					:class="{active: currentFilter === 'completed'}" 
					@click="setFilter('completed')">
					<text class="tab-text">已完成</text>
					<text class="tab-count">({{completedCount}})</text>
				</view>
			</view>
		</view>

		<!-- 工单列表 -->
		<view class="order-list">
			<view class="order-card" v-for="(order, index) in filteredOrders" :key="order.id">
				<view class="order-header">
					<view class="order-info">
						<text class="order-id">工单号: {{order.id}}</text>
						<view class="status-badge" :class="getStatusClass(order.status)">
							<text class="status-text">{{getStatusText(order.status)}}</text>
						</view>
					</view>
					<view class="priority-badge" :class="getPriorityClass(order.priority)">
						<text class="priority-text">{{getPriorityText(order.priority)}}</text>
					</view>
				</view>

				<view class="order-content">
					<view class="content-row">
						<text class="label">设备名称:</text>
						<text class="value">{{order.deviceName}}</text>
					</view>
					<view class="content-row">
						<text class="label">故障类型:</text>
						<text class="value">{{order.faultType}}</text>
					</view>
					<view class="content-row">
						<text class="label">申报时间:</text>
						<text class="value">{{order.reportTime}}</text>
					</view>
					<view class="content-row" v-if="order.assignee">
						<text class="label">负责人:</text>
						<text class="value">{{order.assignee}}</text>
					</view>
					<view class="content-row">
						<text class="label">故障描述:</text>
						<text class="value description">{{order.description}}</text>
					</view>
				</view>

				<view class="order-actions">
					<button class="action-btn urge-btn" 
						v-if="order.status !== 'completed'" 
						@click="urgeOrder(order)">
						催办
					</button>
				</view>
			</view>
		</view>

		<!-- 空状态 -->
		<view class="empty-state" v-if="filteredOrders.length === 0">
			<text class="empty-icon">📋</text>
			<text class="empty-text">暂无工单数据</text>
		</view>
	</view>
</template>

<script>
export default {
	data() {
		return {
			statusBarHeight: 0,
			currentFilter: 'all',
			currentUser: '张三', // 当前登录用户
			allOrders: [
				{
					id: 'WO202401001',
					deviceName: '激光切割机',
					faultType: '电源故障',
					status: 'pending',
					priority: 'high',
					reportTime: '2024-01-15 09:30',
					description: '设备无法正常启动，电源指示灯不亮，疑似电源模块故障',
					assignee: '',
					reporter: '张三'
				},
				{
					id: 'WO202401002',
					deviceName: '3D打印机',
					faultType: '网络异常',
					status: 'processing',
					priority: 'medium',
					reportTime: '2024-01-15 10:15',
					description: '打印机无法连接到网络，WiFi连接异常',
					assignee: '李四',
					reporter: '王五'
				},
				{
					id: 'WO202401003',
					deviceName: '数控铣床',
					faultType: '硬件损坏',
					status: 'completed',
					priority: 'medium',
					reportTime: '2024-01-14 16:20',
					description: '主轴轴承磨损严重，需要更换',
					assignee: '赵六',
					reporter: '钱七',
					completedTime: '2024-01-15 14:30'
				},
				{
					id: 'WO202401004',
					deviceName: '示波器',
					faultType: '软件异常',
					status: 'pending',
					priority: 'low',
					reportTime: '2024-01-15 11:45',
					description: '软件界面卡死，无法正常操作',
					assignee: '',
					reporter: '孙八'
				},
				{
					id: 'WO202401005',
					deviceName: '热风枪',
					faultType: '温度过高',
					status: 'processing',
					priority: 'high',
					reportTime: '2024-01-15 14:30',
					description: '设备温度控制异常，温度过高无法调节',
					assignee: '周九',
					reporter: '吴十'
				}
			]
		}
	},
	computed: {
		filteredOrders() {
			// 只显示当前用户提交的工单
			const userOrders = this.allOrders.filter(order => order.reporter === this.currentUser)
			if (this.currentFilter === 'all') {
				return userOrders
			}
			return userOrders.filter(order => order.status === this.currentFilter)
		},
		pendingCount() {
			return this.allOrders.filter(order => order.reporter === this.currentUser && order.status === 'pending').length
		},
		processingCount() {
			return this.allOrders.filter(order => order.reporter === this.currentUser && order.status === 'processing').length
		},
		completedCount() {
			return this.allOrders.filter(order => order.reporter === this.currentUser && order.status === 'completed').length
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
		setFilter(filter) {
			this.currentFilter = filter
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
		getPriorityClass(priority) {
			return {
				'priority-low': priority === 'low',
				'priority-medium': priority === 'medium',
				'priority-high': priority === 'high'
			}
		},
		getPriorityText(priority) {
			const priorityMap = {
				low: '低',
				medium: '中',
				high: '高'
			}
			return priorityMap[priority] || '未知'
		},
		urgeOrder(order) {
			uni.showModal({
				title: '催办工单',
				content: `确定要催办工单 ${order.id} 吗？`,
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

/* 筛选区域 */
.filter-section {
	padding: 20rpx 30rpx 20rpx;
}

.filter-tabs {
	display: flex;
	background: white;
	border-radius: 16rpx;
	overflow: hidden;
	box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.1);
}

.filter-tab {
	flex: 1;
	padding: 25rpx 15rpx;
	text-align: center;
	background: white;
	border-right: 1rpx solid #f0f0f0;
	transition: all 0.3s;
}

.filter-tab:last-child {
	border-right: none;
}

.filter-tab.active {
	background: #e3f2fd;
	border-bottom: 4rpx solid #2196F3;
}

.tab-text {
	font-size: 26rpx;
	color: #333;
	font-weight: 500;
	display: block;
	margin-bottom: 5rpx;
}

.tab-count {
	font-size: 22rpx;
	color: #666;
}

/* 工单列表 */
.order-list {
	padding: 0 30rpx 30rpx;
}

.order-card {
	background: white;
	border-radius: 16rpx;
	margin-bottom: 20rpx;
	padding: 30rpx;
	box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.1);
}

.order-header {
	display: flex;
	justify-content: space-between;
	align-items: flex-start;
	margin-bottom: 20rpx;
}

.order-info {
	display: flex;
	align-items: center;
	gap: 15rpx;
}

.order-id {
	font-size: 28rpx;
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

.priority-badge {
	padding: 8rpx 16rpx;
	border-radius: 20rpx;
	font-size: 22rpx;
}

.priority-low {
	background: #e8f5e8;
	color: #4caf50;
}

.priority-medium {
	background: #fff3e0;
	color: #ff9800;
}

.priority-high {
	background: #ffebee;
	color: #f44336;
}

.order-content {
	margin-bottom: 25rpx;
}

.content-row {
	display: flex;
	margin-bottom: 15rpx;
	align-items: flex-start;
}

.label {
	font-size: 26rpx;
	color: #666;
	width: 160rpx;
	flex-shrink: 0;
}

.value {
	font-size: 26rpx;
	color: #333;
	flex: 1;
}

.description {
	line-height: 1.5;
}

.order-actions {
	display: flex;
	gap: 15rpx;
	flex-wrap: wrap;
}

.action-btn {
	padding: 12rpx 24rpx;
	border-radius: 8rpx;
	font-size: 24rpx;
	border: none;
	cursor: pointer;
	transition: all 0.3s;
}

.urge-btn {
	background: #ff9800;
	color: white;
}

/* 空状态 */
.empty-state {
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;
	padding: 100rpx 30rpx;
}

.empty-icon {
	font-size: 120rpx;
	margin-bottom: 30rpx;
}

.empty-text {
	font-size: 28rpx;
	color: #999;
}
</style>