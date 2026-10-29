<template>
	<view class="container">
		<!-- 搜索栏 -->
		<view class="search-bar fade-in-down">
			<input class="search-input" placeholder="搜索设备名称或编号" v-model="searchKeyword" @input="onSearch" />
			<view class="filter-btn card-hover" @click="showFilter">
				<image class="filter-icon" src="http://szzgurngv.hn-bkt.clouddn.com/icon/cangku.png" mode="aspectFit"></image>
			</view>
		</view>

		<!-- 设备统计 -->
		<view class="device-stats">
			<view class="stat-item available scale-in" style="animation-delay: 0.1s;">
				<text class="stat-number">{{deviceStats.available}}</text>
				<text class="stat-label">可用</text>
			</view>
			<view class="stat-item using scale-in" style="animation-delay: 0.2s;">
				<text class="stat-number">{{deviceStats.using}}</text>
				<text class="stat-label">使用中</text>
			</view>
			<view class="stat-item maintenance scale-in" style="animation-delay: 0.3s;">
				<text class="stat-number">{{deviceStats.maintenance}}</text>
				<text class="stat-label">维护中</text>
			</view>
		</view>

		<!-- 快捷操作 -->
		<view class="quick-actions">
			<view class="action-item card-hover scale-in" style="animation-delay: 0.4s;" @click="navigateTo('/pages/device/booking')">
				<image class="action-icon icon-bounce" src="/static/icon-calendar.png" mode="aspectFit"></image>
				<text class="action-text">设备预约</text>
			</view>
			<view class="action-item card-hover scale-in" style="animation-delay: 0.5s;" @click="navigateTo('/pages/device/repair')">
				<image class="action-icon icon-bounce" src="/static/icon-wrench.png" mode="aspectFit"></image>
				<text class="action-text">故障报修</text>
			</view>
			<view class="action-item card-hover scale-in" style="animation-delay: 0.6s;" @click="showMyBookings">
				<image class="action-icon icon-bounce" src="/static/icon-clipboard.png" mode="aspectFit"></image>
				<text class="action-text">我的预约</text>
			</view>
		</view>

		<!-- 设备列表 -->
		<view class="device-list">
			<view class="list-header">
				<text class="header-title">设备列表</text>
				<view class="view-toggle">
					<text class="toggle-btn" :class="{active: viewMode === 'list'}" @click="viewMode = 'list'">列表</text>
					<text class="toggle-btn" :class="{active: viewMode === 'grid'}" @click="viewMode = 'grid'">网格</text>
				</view>
			</view>

			<view class="device-grid" v-if="viewMode === 'grid'">
				<view class="device-card card-hover fade-in-up" v-for="(device, index) in filteredDevices" :key="device.id" :style="{animationDelay: (0.7 + index * 0.1) + 's'}" @click="showDeviceDetail(device)">
					<view class="device-status" :class="device.status"></view>
					<image class="device-image" :src="device.image" mode="aspectFill"></image>
					<view class="device-info">
						<text class="device-name">{{device.name}}</text>
						<text class="device-type">{{device.type}}</text>
						<view class="device-location">
							<image class="location-icon" src="/static/icon-location.png" mode="aspectFit"></image>
							<text>{{device.location}}</text>
						</view>
					</view>
					<view class="device-actions">
						<button class="action-btn primary btn-hover" v-if="device.status === 'available'" @click.stop="bookDevice(device)">预约</button>
						<button class="action-btn secondary" v-else-if="device.status === 'using'" disabled>使用中</button>
						<button class="action-btn warning" v-else disabled>维护中</button>
					</view>
				</view>
			</view>

			<view class="device-list-view" v-else>
				<view class="device-item card-hover fade-in-up" v-for="(device, index) in filteredDevices" :key="device.id" :style="{animationDelay: (0.7 + index * 0.1) + 's'}" @click="showDeviceDetail(device)">
					<view class="device-left">
						<image class="device-thumb" :src="device.image" mode="aspectFill"></image>
						<view class="device-info">
							<text class="device-name">{{device.name}}</text>
							<text class="device-type">{{device.type}}</text>
							<view class="device-location">
							<image class="location-icon" src="/static/icon-location.png" mode="aspectFit"></image>
							<text>{{device.location}}</text>
						</view>
						</view>
					</view>
					<view class="device-right">
						<view class="status-badge" :class="device.status">
							<text class="status-text">{{getStatusText(device.status)}}</text>
						</view>
						<button class="book-btn btn-hover" v-if="device.status === 'available'" @click.stop="bookDevice(device)">预约</button>
					</view>
				</view>
			</view>
		</view>

		<!-- 筛选弹窗 -->
		<view class="filter-popup" v-if="showFilterPopup" @click="showFilterPopup = false">
			<view class="filter-content" @click.stop>
				<view class="filter-header">
					<text class="filter-title">筛选条件</text>
					<image class="filter-close" src="/static/icon-close.png" mode="aspectFit" @click="showFilterPopup = false"></image>
				</view>
				<view class="filter-section">
					<text class="filter-label">设备类型</text>
					<view class="filter-options">
						<text class="filter-option" :class="{active: filterType === 'all'}" @click="filterType = 'all'">全部</text>
						<text class="filter-option" :class="{active: filterType === '显微镜'}" @click="filterType = '显微镜'">显微镜</text>
						<text class="filter-option" :class="{active: filterType === '离心机'}" @click="filterType = '离心机'">离心机</text>
						<text class="filter-option" :class="{active: filterType === '培养箱'}" @click="filterType = '培养箱'">培养箱</text>
					</view>
				</view>
				<view class="filter-section">
					<text class="filter-label">设备状态</text>
					<view class="filter-options">
						<text class="filter-option" :class="{active: filterStatus === 'all'}" @click="filterStatus = 'all'">全部</text>
						<text class="filter-option" :class="{active: filterStatus === 'available'}" @click="filterStatus = 'available'">可用</text>
						<text class="filter-option" :class="{active: filterStatus === 'using'}" @click="filterStatus = 'using'">使用中</text>
						<text class="filter-option" :class="{active: filterStatus === 'maintenance'}" @click="filterStatus = 'maintenance'">维护中</text>
					</view>
				</view>
				<view class="filter-actions">
					<button class="filter-reset" @click="resetFilter">重置</button>
					<button class="filter-confirm" @click="applyFilter">确定</button>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				searchKeyword: '',
				viewMode: 'grid', // 'grid' | 'list'
				showFilterPopup: false,
				filterType: 'all',
				filterStatus: 'all',
				deviceStats: {
					available: 12,
					using: 8,
					maintenance: 3
				},
				devices: [
					{
						id: 1,
						name: '光学显微镜-01',
						type: '显微镜',
						location: 'A101实验室',
						status: 'available',
						image: '/static/devices/microscope.jpg'
					},
					{
						id: 2,
						name: '高速离心机-02',
						type: '离心机',
						location: 'A102实验室',
						status: 'using',
						image: '/static/devices/centrifuge.jpg'
					},
					{
						id: 3,
						name: 'CO2培养箱-03',
						type: '培养箱',
						location: 'A103实验室',
						status: 'available',
						image: '/static/devices/incubator.jpg'
					},
					{
						id: 4,
						name: '电子显微镜-04',
						type: '显微镜',
						location: 'A104实验室',
						status: 'maintenance',
						image: '/static/devices/electron-microscope.jpg'
					}
				]
			}
		},
		computed: {
			filteredDevices() {
				let result = this.devices
				
				// 搜索过滤
				if (this.searchKeyword) {
					result = result.filter(device => 
						device.name.includes(this.searchKeyword) || 
						device.type.includes(this.searchKeyword) ||
						device.location.includes(this.searchKeyword)
					)
				}
				
				// 类型过滤
				if (this.filterType !== 'all') {
					result = result.filter(device => device.type === this.filterType)
				}
				
				// 状态过滤
				if (this.filterStatus !== 'all') {
					result = result.filter(device => device.status === this.filterStatus)
				}
				
				return result
			}
		},
		methods: {
			onSearch() {
				// 搜索逻辑已在computed中处理
			},
			showFilter() {
				this.showFilterPopup = true
			},
			resetFilter() {
				this.filterType = 'all'
				this.filterStatus = 'all'
			},
			applyFilter() {
				this.showFilterPopup = false
			},
			navigateTo(url) {
				uni.navigateTo({ url })
			},
			showMyBookings() {
				uni.showModal({
					title: '我的预约',
					content: '功能开发中，敬请期待',
					showCancel: false
				})
			},
			showDeviceDetail(device) {
				uni.showModal({
					title: device.name,
					content: `设备类型：${device.type}\n位置：${device.location}\n状态：${this.getStatusText(device.status)}`,
					showCancel: false
				})
			},
			bookDevice(device) {
				uni.navigateTo({
					url: `/pages/device/booking?deviceId=${device.id}&deviceName=${device.name}`
				})
			},
			getStatusText(status) {
				const statusMap = {
					available: '可用',
					using: '使用中',
					maintenance: '维护中'
				}
				return statusMap[status] || '未知'
			}
		}
	}
</script>

<style scoped>
	/* 优化的动画效果 - 减少卡顿 */
	@keyframes fadeInDown {
		from {
			opacity: 0;
			transform: translate3d(0, -20px, 0);
		}
		to {
			opacity: 1;
			transform: translate3d(0, 0, 0);
		}
	}

	@keyframes fadeInUp {
		from {
			opacity: 0;
			transform: translate3d(0, 20px, 0);
		}
		to {
			opacity: 1;
			transform: translate3d(0, 0, 0);
		}
	}

	@keyframes scaleIn {
		from {
			opacity: 0;
			transform: scale3d(0.9, 0.9, 1);
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
			transform: translate3d(0, -4px, 0);
		}
		60% {
			transform: translate3d(0, -2px, 0);
		}
	}

	.fade-in-down {
		animation: fadeInDown 0.4s ease-out;
		will-change: transform, opacity;
	}

	.fade-in-up {
		animation: fadeInUp 0.4s ease-out;
		animation-fill-mode: both;
		will-change: transform, opacity;
	}

	.scale-in {
		animation: scaleIn 0.3s ease-out;
		animation-fill-mode: both;
		will-change: transform, opacity;
	}

	.floating {
		animation: floating 2.5s ease-in-out infinite;
		will-change: transform;
	}

	.icon-bounce:hover {
		animation: iconBounce 0.5s ease-in-out;
		will-change: transform;
	}

	.card-hover {
		transition: transform 0.2s ease, box-shadow 0.2s ease;
		will-change: transform;
	}

	.card-hover:hover {
		transform: translate3d(0, -2px, 0);
		box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
	}

	.card-hover:active {
		transform: translate3d(0, 0, 0) scale3d(0.98, 0.98, 1);
		transition: all 0.1s ease;
	}

	.btn-hover {
		transition: transform 0.15s ease;
		will-change: transform;
	}

	.btn-hover:hover {
		transform: scale3d(1.03, 1.03, 1);
	}

	.btn-hover:active {
		transform: scale3d(0.97, 0.97, 1);
	}

	.container {
		padding: 20rpx;
		background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
		min-height: 100vh;
	}

	.search-bar {
		display: flex;
		gap: 20rpx;
		margin-bottom: 30rpx;
	}

	.search-input {
		flex: 1;
		padding: 20rpx 30rpx;
		background: white;
		border-radius: 50rpx;
		font-size: 28rpx;
		border: 2rpx solid #e0e0e0;
	}

	.filter-btn {
		width: 80rpx;
		height: 80rpx;
		background: #2979FF;
		border-radius: 40rpx;
		display: flex;
		align-items: center;
		justify-content: center;
	}

	.filter-icon {
		width: 32rpx;
		height: 32rpx;
	}

	.device-stats {
		display: flex;
		gap: 20rpx;
		margin-bottom: 30rpx;
	}

	.stat-item {
		flex: 1;
		padding: 30rpx;
		background: white;
		border-radius: 20rpx;
		text-align: center;
		border-left: 8rpx solid #ddd;
	}

	.stat-item.available { border-left-color: #4CAF50; }
	.stat-item.using { border-left-color: #FF9800; }
	.stat-item.maintenance { border-left-color: #F44336; }

	.stat-number {
		font-size: 48rpx;
		font-weight: bold;
		color: #333;
		display: block;
		margin-bottom: 10rpx;
	}

	.stat-label {
		font-size: 24rpx;
		color: #666;
	}

	.quick-actions {
		display: flex;
		gap: 20rpx;
		margin-bottom: 30rpx;
	}

	.action-item {
		flex: 1;
		padding: 25rpx;
		background: white;
		border-radius: 15rpx;
		display: flex;
		flex-direction: column;
		align-items: center;
		gap: 10rpx;
	}

	.action-icon {
		width: 40rpx;
		height: 40rpx;
	}

	.action-text {
		font-size: 24rpx;
		color: #333;
	}

	.device-list {
		background: white;
		border-radius: 20rpx;
		padding: 30rpx;
	}

	.list-header {
		display: flex;
		justify-content: space-between;
		align-items: center;
		margin-bottom: 30rpx;
	}

	.header-title {
		font-size: 32rpx;
		font-weight: bold;
		color: #333;
	}

	.view-toggle {
		display: flex;
		background: #f0f0f0;
		border-radius: 25rpx;
		padding: 4rpx;
	}

	.toggle-btn {
		padding: 12rpx 24rpx;
		font-size: 24rpx;
		border-radius: 20rpx;
		color: #666;
		transition: all 0.3s;
	}

	.toggle-btn.active {
		background: #2979FF;
		color: white;
	}

	.device-grid {
		display: grid;
		grid-template-columns: 1fr 1fr;
		gap: 20rpx;
	}

	.device-card {
		border: 2rpx solid #e0e0e0;
		border-radius: 15rpx;
		overflow: hidden;
		position: relative;
	}

	.device-status {
		position: absolute;
		top: 15rpx;
		right: 15rpx;
		width: 20rpx;
		height: 20rpx;
		border-radius: 50%;
		z-index: 2;
	}

	.device-status.available { background: #4CAF50; }
	.device-status.using { background: #FF9800; }
	.device-status.maintenance { background: #F44336; }

	.device-image {
		width: 100%;
		height: 200rpx;
		background: #f0f0f0;
	}

	.device-info {
		padding: 20rpx;
	}

	.device-name {
		font-size: 28rpx;
		font-weight: bold;
		color: #333;
		display: block;
		margin-bottom: 8rpx;
	}

	.device-type {
		font-size: 24rpx;
		color: #666;
		display: block;
		margin-bottom: 8rpx;
	}

	.device-location {
		display: flex;
		align-items: center;
		gap: 8rpx;
		font-size: 22rpx;
		color: #999;
	}

	.location-icon {
		width: 22rpx;
		height: 22rpx;
	}

	.device-actions {
		padding: 0 20rpx 20rpx;
	}

	.action-btn {
		width: 100%;
		padding: 15rpx;
		border-radius: 8rpx;
		font-size: 24rpx;
		border: none;
	}

	.action-btn.primary {
		background: #2979FF;
		color: white;
	}

	.action-btn.secondary {
		background: #FF9800;
		color: white;
	}

	.action-btn.warning {
		background: #F44336;
		color: white;
	}

	.device-list-view {
		display: flex;
		flex-direction: column;
		gap: 20rpx;
	}

	.device-item {
		display: flex;
		justify-content: space-between;
		align-items: center;
		padding: 20rpx;
		border: 2rpx solid #e0e0e0;
		border-radius: 15rpx;
	}

	.device-left {
		display: flex;
		align-items: center;
		gap: 20rpx;
	}

	.device-thumb {
		width: 100rpx;
		height: 100rpx;
		border-radius: 10rpx;
		background: #f0f0f0;
	}

	.device-right {
		display: flex;
		flex-direction: column;
		align-items: flex-end;
		gap: 15rpx;
	}

	.status-badge {
		padding: 8rpx 16rpx;
		border-radius: 20rpx;
		font-size: 22rpx;
	}

	.status-badge.available {
		background: #E8F5E8;
		color: #4CAF50;
	}

	.status-badge.using {
		background: #FFF3E0;
		color: #FF9800;
	}

	.status-badge.maintenance {
		background: #FFEBEE;
		color: #F44336;
	}

	.book-btn {
		padding: 12rpx 24rpx;
		background: #2979FF;
		color: white;
		border-radius: 20rpx;
		font-size: 22rpx;
		border: none;
	}

	.filter-popup {
		position: fixed;
		top: 0;
		left: 0;
		right: 0;
		bottom: 0;
		background: rgba(0, 0, 0, 0.5);
		z-index: 1000;
		display: flex;
		align-items: flex-end;
	}

	.filter-content {
		width: 100%;
		background: white;
		border-radius: 30rpx 30rpx 0 0;
		padding: 40rpx;
		max-height: 80vh;
	}

	.filter-header {
		display: flex;
		justify-content: space-between;
		align-items: center;
		margin-bottom: 40rpx;
	}

	.filter-title {
		font-size: 32rpx;
		font-weight: bold;
		color: #333;
	}

	.filter-close {
		width: 32rpx;
		height: 32rpx;
	}

	.filter-section {
		margin-bottom: 40rpx;
	}

	.filter-label {
		font-size: 28rpx;
		font-weight: bold;
		color: #333;
		margin-bottom: 20rpx;
		display: block;
	}

	.filter-options {
		display: flex;
		flex-wrap: wrap;
		gap: 15rpx;
	}

	.filter-option {
		padding: 15rpx 30rpx;
		background: #f0f0f0;
		border-radius: 25rpx;
		font-size: 24rpx;
		color: #666;
	}

	.filter-option.active {
		background: #2979FF;
		color: white;
	}

	.filter-actions {
		display: flex;
		gap: 20rpx;
	}

	.filter-reset, .filter-confirm {
		flex: 1;
		padding: 25rpx;
		border-radius: 15rpx;
		font-size: 28rpx;
		border: none;
	}

	.filter-reset {
		background: #f0f0f0;
		color: #666;
	}

	.filter-confirm {
		background: #2979FF;
		color: white;
	}
</style>