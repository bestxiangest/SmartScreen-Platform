<template>
	<view class="container">
		<!-- 自定义导航栏 -->
		<view class="custom-navbar fade-in-down">
			<view class="navbar-content">
				<text class="navbar-title">校园服务</text>
				<view class="navbar-actions">
					<view class="action-btn btn-hover" @click="showSearch">
						<text class="action-icon">🔍</text>
					</view>
					<view class="action-btn btn-hover" @click="showNotifications">
						<text class="action-icon">🔔</text>
						<view class="notification-badge floating" v-if="unreadCount > 0">
							<text class="badge-text">{{unreadCount > 99 ? '99+' : unreadCount}}</text>
						</view>
					</view>
				</view>
			</view>
		</view>

		<!-- 快捷服务 -->
		<view class="quick-services fade-in-up" style="animation-delay: 0.1s;">
			<view class="service-grid">
				<view class="service-item card-hover scale-in" style="animation-delay: 0.2s;" @click="viewLabStatus">
					<view class="service-icon lab-status">
						<text class="icon-text icon-bounce">🔬</text>
					</view>
					<text class="service-text">实验室状态</text>
					<text class="service-desc">实时查看</text>
				</view>
				
				<view class="service-item card-hover scale-in" style="animation-delay: 0.3s;" @click="openNavigation">
					<view class="service-icon navigation">
						<text class="icon-text icon-bounce">🗺️</text>
					</view>
					<text class="service-text">室内导航</text>
					<text class="service-desc">智能导航</text>
				</view>
				
				<view class="service-item card-hover scale-in" style="animation-delay: 0.4s;" @click="showQRCode">
					<view class="service-icon qr-code">
						<text class="icon-text icon-bounce">📱</text>
					</view>
					<text class="service-text">电子通行证</text>
					<text class="service-desc">扫码通行</text>
				</view>
				
				<view class="service-item card-hover scale-in" style="animation-delay: 0.5s;" @click="viewNotices">
					<view class="service-icon notices">
						<text class="icon-text icon-bounce">📢</text>
					</view>
					<text class="service-text">校园通知</text>
					<text class="service-desc">最新消息</text>
				</view>
			</view>
		</view>

		<!-- 实验室状态概览 -->
		<view class="lab-overview fade-in-up" style="animation-delay: 0.2s;">
			<view class="section-header">
				<text class="section-title">实验室状态</text>
				<text class="view-all btn-hover" @click="viewAllLabs">查看全部</text>
			</view>
			
			<view class="lab-grid">
				<view class="lab-card card-hover fade-in-up" 
					v-for="(lab, index) in labStatus" 
					:key="lab.id"
					:class="lab.status"
					:style="{animationDelay: (0.3 + index * 0.1) + 's'}"
					@click="viewLabDetail(lab)">
					<view class="lab-header">
						<text class="lab-name">{{lab.name}}</text>
						<view class="status-indicator floating" :class="lab.status"></view>
					</view>
					<view class="lab-info">
						<text class="occupancy">{{lab.occupancy}}/{{lab.capacity}}人</text>
						<text class="status-text">{{getStatusText(lab.status)}}</text>
					</view>
					<view class="lab-progress">
						<view class="progress-bar">
							<view class="progress-fill" 
								:style="{width: (lab.occupancy / lab.capacity * 100) + '%'}"></view>
						</view>
					</view>
				</view>
			</view>
		</view>

		<!-- 校园通知 -->
		<view class="campus-notices fade-in-up" style="animation-delay: 0.3s;">
			<view class="section-header">
				<text class="section-title">校园通知</text>
				<text class="view-all btn-hover" @click="viewAllNotices">查看全部</text>
			</view>
			
			<view class="notice-list">
				<view class="notice-item card-hover fade-in-up" 
					v-for="(notice, index) in notices" 
					:key="notice.id"
					:style="{animationDelay: (0.4 + index * 0.1) + 's'}"
					@click="viewNoticeDetail(notice)">
					<view class="notice-icon floating" :class="notice.type">
						<text class="icon-text">{{getNoticeIcon(notice.type)}}</text>
					</view>
					<view class="notice-content">
						<text class="notice-title">{{notice.title}}</text>
						<text class="notice-summary">{{notice.summary}}</text>
						<view class="notice-meta">
							<text class="notice-time">{{notice.time}}</text>
							<text class="notice-tag floating" :class="notice.type">{{getTypeText(notice.type)}}</text>
						</view>
					</view>
					<view class="notice-arrow">›</view>
				</view>
			</view>
		</view>

		<!-- 便民服务 -->
		<view class="convenience-services fade-in-up" style="animation-delay: 0.4s;">
			<view class="section-header">
				<text class="section-title">便民服务</text>
			</view>
			
			<view class="service-list">
				<view class="service-row">
					<view class="service-item-small card-hover scale-in" style="animation-delay: 0.5s;" @click="viewWeather">
						<text class="service-icon-small">🌤️</text>
						<text class="service-text-small">天气预报</text>
					</view>
					<view class="service-item-small card-hover scale-in" style="animation-delay: 0.6s;" @click="viewSchedule">
						<!-- <image class="service-icon-small" src="/static/icon-calendar.png" mode="aspectFit"></image> -->
						<text class="service-text-small">课程表</text>
					</view>
					<view class="service-item-small card-hover scale-in" style="animation-delay: 0.7s;" @click="viewLibrary">
						<!-- <image class="service-icon-small" src="/static/icon-book.png" mode="aspectFit"></image> -->
						<text class="service-text-small">图书馆</text>
					</view>
					<view class="service-item-small card-hover scale-in" style="animation-delay: 0.8s;" @click="viewCanteen">
						<!-- <image class="service-icon-small" src="/static/icon-restaurant.png" mode="aspectFit"></image> -->
						<text class="service-text-small">食堂菜单</text>
					</view>
				</view>
				
				<view class="service-row">
					<view class="service-item-small card-hover scale-in" style="animation-delay: 0.9s;" @click="viewTransport">
						<!-- <image class="service-icon-small" src="/static/icon-bus.png" mode="aspectFit"></image> -->
						<text class="service-text-small">校车时刻</text>
					</view>
					<view class="service-item-small card-hover scale-in" style="animation-delay: 1.0s;" @click="viewMaintenance">
						<!-- <image class="service-icon-small" src="/static/icon-wrench.png" mode="aspectFit"></image> -->
						<text class="service-text-small">报修服务</text>
					</view>
					<view class="service-item-small card-hover scale-in" style="animation-delay: 1.1s;" @click="viewLostFound">
						<!-- <image class="service-icon-small" src="/static/icon-search.png" mode="aspectFit"></image> -->
						<text class="service-text-small">失物招领</text>
					</view>
					<view class="service-item-small card-hover scale-in" style="animation-delay: 1.2s;" @click="viewContact">
						<!-- <image class="service-icon-small" src="/static/icon-phone.png" mode="aspectFit"></image> -->
						<text class="service-text-small">联系方式</text>
					</view>
				</view>
			</view>
		</view>

		<!-- 搜索弹窗 -->
		<uni-popup ref="searchPopup" type="top">
			<view class="search-popup">
				<view class="search-header">
					<view class="search-input-wrapper">
						<!-- <image class="search-icon" src="/static/icon-search.png" mode="aspectFit"></image> -->
						<input class="search-input" 
							v-model="searchKeyword" 
							placeholder="搜索服务、通知、实验室..." 
							@input="onSearchInput" />
						<view class="cancel-btn" @click="closeSearch">
							<text class="cancel-text">取消</text>
						</view>
					</view>
				</view>
				
				<view class="search-content">
					<view class="search-suggestions" v-if="!searchKeyword">
						<text class="suggestions-title">热门搜索</text>
						<view class="suggestion-tags">
							<view class="suggestion-tag" 
								v-for="tag in hotSearches" 
								:key="tag"
								@click="searchByTag(tag)">
								<text class="tag-text">{{tag}}</text>
							</view>
						</view>
					</view>
					
					<view class="search-results" v-if="searchKeyword">
						<view class="result-item" 
							v-for="result in searchResults" 
							:key="result.id"
							@click="selectResult(result)">
							<text class="result-icon">{{result.icon}}</text>
							<view class="result-content">
								<text class="result-title">{{result.title}}</text>
								<text class="result-desc">{{result.description}}</text>
							</view>
						</view>
					</view>
				</view>
			</view>
		</uni-popup>

		<!-- 电子通行证弹窗 -->
		<uni-popup ref="qrCodePopup" type="center">
			<view class="qr-popup">
				<view class="qr-header">
					<text class="qr-title">电子通行证</text>
					<view class="close-btn" @click="closeQRCode">
						<!-- <image class="close-icon" src="/static/icon-close.png" mode="aspectFit"></image> -->
					</view>
				</view>
				
				<view class="qr-content">
					<view class="user-info">
						<!-- <image class="user-avatar" src="/static/avatars/user.jpg" mode="aspectFill"></image> -->
						<view class="user-details">
							<text class="user-name">张三</text>
							<text class="user-id">学号：2021001001</text>
							<view class="user-status">
							<!-- <image class="status-icon" src="/static/icon-check.png" mode="aspectFit"></image> -->
							<text class="status-text">健康状态正常</text>
						</view>
						</view>
					</view>
					
					<view class="qr-code-container">
						<view class="qr-code">
							<!-- 这里应该是真实的二维码 -->
							<view class="qr-placeholder">
								<text class="qr-text">二维码</text>
							</view>
						</view>
						<text class="qr-desc">请将此二维码对准扫码器</text>
					</view>
					
					<view class="qr-info">
						<view class="info-item">
							<text class="info-label">有效期：</text>
							<text class="info-value">{{qrValidTime}}</text>
						</view>
						<view class="info-item">
							<text class="info-label">权限：</text>
							<text class="info-value">实验室通行</text>
						</view>
					</view>
				</view>
				
				<view class="qr-actions">
					<button class="refresh-btn" @click="refreshQRCode">刷新二维码</button>
				</view>
			</view>
		</uni-popup>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				unreadCount: 5,
				searchKeyword: '',
				qrValidTime: '',
				labStatus: [
					{
						id: 1,
						name: '化学实验室A',
						status: 'busy',
						occupancy: 8,
						capacity: 10
					},
					{
						id: 2,
						name: '生物实验室B',
						status: 'available',
						occupancy: 2,
						capacity: 12
					},
					{
						id: 3,
						name: '物理实验室C',
						status: 'maintenance',
						occupancy: 0,
						capacity: 8
					},
					{
						id: 4,
						name: '工程实验室D',
						status: 'busy',
						occupancy: 6,
						capacity: 8
					}
				],
				notices: [
					{
						id: 1,
						type: 'important',
						title: '实验室安全培训通知',
						summary: '所有学生需在本周内完成实验室安全培训，未完成者将暂停实验室使用权限',
						time: '2024-01-15 09:00'
					},
					{
						id: 2,
						type: 'maintenance',
						title: '物理实验室设备维护',
						summary: '物理实验室C将于明天进行设备维护，预计停用2天',
						time: '2024-01-14 16:30'
					},
					{
						id: 3,
						type: 'event',
						title: '科技创新大赛报名',
						summary: '第十届校园科技创新大赛开始报名，截止时间为本月底',
						time: '2024-01-13 14:20'
					},
					{
						id: 4,
						type: 'normal',
						title: '图书馆开放时间调整',
						summary: '寒假期间图书馆开放时间调整为9:00-17:00',
						time: '2024-01-12 11:45'
					}
				],
				hotSearches: ['实验室预约', '安全培训', '设备状态', '课程表', '食堂菜单', '校车时刻'],
				searchResults: []
			}
		},
		methods: {
			viewLabStatus() {
				uni.navigateTo({
					url: '/pages/campus/lab-status'
				})
			},
			
			openNavigation() {
				uni.navigateTo({
					url: '/pages/campus/navigation'
				})
			},
			
			showQRCode() {
				this.generateQRValidTime()
				this.$refs.qrCodePopup.open()
			},
			
			viewNotices() {
				uni.navigateTo({
					url: '/pages/campus/notices'
				})
			},
			
			viewAllLabs() {
				uni.navigateTo({
					url: '/pages/campus/lab-status'
				})
			},
			
			viewLabDetail(lab) {
				uni.navigateTo({
					url: `/pages/campus/lab-detail?id=${lab.id}`
				})
			},
			
			viewAllNotices() {
				uni.navigateTo({
					url: '/pages/campus/notices'
				})
			},
			
			viewNoticeDetail(notice) {
				uni.navigateTo({
					url: `/pages/campus/notice-detail?id=${notice.id}`
				})
			},
			
			getStatusText(status) {
				const statusMap = {
					available: '空闲',
					busy: '使用中',
					maintenance: '维护中',
					closed: '已关闭'
				}
				return statusMap[status] || '未知'
			},
			
			getNoticeIcon(type) {
				const iconMap = {
					important: '⚠️',
					maintenance: '🔧',
					event: '🎉',
					normal: '📢'
				}
				return iconMap[type] || '📢'
			},
			
			getTypeText(type) {
				const typeMap = {
					important: '重要',
					maintenance: '维护',
					event: '活动',
					normal: '通知'
				}
				return typeMap[type] || '通知'
			},
			
			showSearch() {
				this.$refs.searchPopup.open()
			},
			
			closeSearch() {
				this.searchKeyword = ''
				this.searchResults = []
				this.$refs.searchPopup.close()
			},
			
			onSearchInput() {
				if (this.searchKeyword.trim()) {
					this.performSearch()
				} else {
					this.searchResults = []
				}
			},
			
			performSearch() {
				// 模拟搜索结果
				const allResults = [
					{id: 1, icon: '🔬', title: '化学实验室A', description: '当前使用中，8/10人'},
					{id: 2, icon: '📋', title: '设备预约', description: '预约实验室设备'},
					{id: 3, icon: '🛡️', title: '安全培训', description: '实验室安全培训课程'},
					{id: 4, icon: '📅', title: '课程表', description: '查看个人课程安排'},
					{id: 5, icon: '🍽️', title: '食堂菜单', description: '今日菜单和营养信息'},
					{id: 6, icon: '🚌', title: '校车时刻', description: '校车运行时间表'}
				]
				
				this.searchResults = allResults.filter(item => 
					item.title.includes(this.searchKeyword) || 
					item.description.includes(this.searchKeyword)
				)
			},
			
			searchByTag(tag) {
				this.searchKeyword = tag
				this.performSearch()
			},
			
			selectResult(result) {
				this.closeSearch()
				uni.showToast({
					title: `选择了：${result.title}`,
					icon: 'none'
				})
			},
			
			showNotifications() {
				uni.navigateTo({
					url: '/pages/campus/notifications'
				})
			},
			
			closeQRCode() {
				this.$refs.qrCodePopup.close()
			},
			
			generateQRValidTime() {
				const now = new Date()
				const validUntil = new Date(now.getTime() + 30 * 60 * 1000) // 30分钟后过期
				this.qrValidTime = `${validUntil.getHours().toString().padStart(2, '0')}:${validUntil.getMinutes().toString().padStart(2, '0')}`
			},
			
			refreshQRCode() {
				this.generateQRValidTime()
				uni.showToast({
					title: '二维码已刷新',
					icon: 'success'
				})
			},
			
			// 便民服务方法
			viewWeather() {
				uni.showToast({title: '天气预报功能开发中', icon: 'none'})
			},
			viewSchedule() {
				uni.showToast({title: '课程表功能开发中', icon: 'none'})
			},
			viewLibrary() {
				uni.showToast({title: '图书馆功能开发中', icon: 'none'})
			},
			viewCanteen() {
				uni.showToast({title: '食堂菜单功能开发中', icon: 'none'})
			},
			viewTransport() {
				uni.showToast({title: '校车时刻功能开发中', icon: 'none'})
			},
			viewMaintenance() {
				uni.navigateTo({
					url: '/pages/device/repair'
				})
			},
			viewLostFound() {
				uni.showToast({title: '失物招领功能开发中', icon: 'none'})
			},
			viewContact() {
				uni.showToast({title: '联系方式功能开发中', icon: 'none'})
			}
		},
		onLoad() {
			// 初始化页面数据
			console.log('校园服务页面加载')
		}
	}
</script>

<style scoped>
	.container {
		padding: 20rpx;
		background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
		min-height: 100vh;
	}

	.custom-navbar {
		padding: 20rpx;
		padding-top: 60rpx;
		background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
		border-radius: 0 0 30rpx 30rpx;
		margin: -20rpx -20rpx 30rpx -20rpx;
	}

	.navbar-content {
		display: flex;
		justify-content: space-between;
		align-items: center;
	}

	.navbar-title {
		font-size: 36rpx;
		font-weight: bold;
		color: white;
	}

	.navbar-actions {
		display: flex;
		gap: 15rpx;
	}

	.action-btn {
		width: 70rpx;
		height: 70rpx;
		display: flex;
		align-items: center;
		justify-content: center;
		background: rgba(255, 255, 255, 0.2);
		border-radius: 50%;
		position: relative;
		transition: all 0.3s;
	}

	.action-btn:active {
		transform: scale(0.9);
		background: rgba(255, 255, 255, 0.3);
	}

	.action-icon {
		font-size: 32rpx;
		color: white;
	}

	.notification-badge {
		position: absolute;
		top: -5rpx;
		right: -5rpx;
		min-width: 32rpx;
		height: 32rpx;
		background: #ff4444;
		border-radius: 16rpx;
		display: flex;
		align-items: center;
		justify-content: center;
		padding: 0 8rpx;
	}

	.badge-text {
		font-size: 18rpx;
		color: white;
		font-weight: bold;
	}

	.quick-services {
		margin-bottom: 30rpx;
	}

	.service-grid {
		display: grid;
		grid-template-columns: repeat(2, 1fr);
		gap: 20rpx;
	}

	.service-item {
		padding: 30rpx;
		background: white;
		border-radius: 20rpx;
		display: flex;
		flex-direction: column;
		align-items: center;
		gap: 15rpx;
		transition: all 0.3s;
		box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.05);
	}

	.service-item:active {
		transform: scale(0.98);
		box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.1);
	}

	.service-icon {
		width: 100rpx;
		height: 100rpx;
		display: flex;
		align-items: center;
		justify-content: center;
		border-radius: 50%;
	}

	.service-icon.lab-status {
		background: linear-gradient(135deg, #4CAF50, #45a049);
	}

	.service-icon.navigation {
		background: linear-gradient(135deg, #2196F3, #1976D2);
	}

	.service-icon.qr-code {
		background: linear-gradient(135deg, #FF9800, #F57C00);
	}

	.service-icon.notices {
		background: linear-gradient(135deg, #e91e63, #C2185B);
	}

	.icon-image {
		width: 48rpx;
		height: 48rpx;
	}

	.service-text {
		font-size: 28rpx;
		font-weight: bold;
		color: #333;
	}

	.service-desc {
		font-size: 22rpx;
		color: #666;
	}

	.lab-overview,
	.campus-notices,
	.convenience-services {
		background: white;
		border-radius: 20rpx;
		padding: 30rpx;
		margin-bottom: 30rpx;
	}

	.section-header {
		display: flex;
		justify-content: space-between;
		align-items: center;
		margin-bottom: 25rpx;
	}

	.section-title {
		font-size: 28rpx;
		font-weight: bold;
		color: #333;
	}

	.view-all {
		font-size: 24rpx;
		color: #2979FF;
		padding: 10rpx;
	}

	.lab-grid {
		display: grid;
		grid-template-columns: repeat(2, 1fr);
		gap: 20rpx;
	}

	.lab-card {
		padding: 25rpx;
		background: #f8f9fa;
		border-radius: 15rpx;
		border-left: 8rpx solid #ddd;
		transition: all 0.3s;
	}

	.lab-card.available {
		border-left-color: #4CAF50;
		background: #e8f5e8;
	}

	.lab-card.busy {
		border-left-color: #FF9800;
		background: #fff3e0;
	}

	.lab-card.maintenance {
		border-left-color: #f44336;
		background: #ffebee;
	}

	.lab-card:active {
		transform: scale(0.98);
	}

	.lab-header {
		display: flex;
		justify-content: space-between;
		align-items: center;
		margin-bottom: 15rpx;
	}

	.lab-name {
		font-size: 26rpx;
		font-weight: bold;
		color: #333;
	}

	.status-indicator {
		width: 20rpx;
		height: 20rpx;
		border-radius: 50%;
	}

	.status-indicator.available {
		background: #4CAF50;
	}

	.status-indicator.busy {
		background: #FF9800;
	}

	.status-indicator.maintenance {
		background: #f44336;
	}

	.lab-info {
		display: flex;
		justify-content: space-between;
		align-items: center;
		margin-bottom: 15rpx;
	}

	.occupancy {
		font-size: 24rpx;
		color: #666;
	}

	.status-text {
		font-size: 22rpx;
		color: #999;
	}

	.lab-progress {
		width: 100%;
	}

	.progress-bar {
		width: 100%;
		height: 8rpx;
		background: #e0e0e0;
		border-radius: 4rpx;
		overflow: hidden;
	}

	.progress-fill {
		height: 100%;
		background: #2979FF;
		border-radius: 4rpx;
		transition: width 0.3s;
	}

	.notice-list {
		display: flex;
		flex-direction: column;
		gap: 15rpx;
	}

	.notice-item {
		display: flex;
		align-items: center;
		gap: 20rpx;
		padding: 20rpx;
		background: #f8f9fa;
		border-radius: 15rpx;
		transition: all 0.3s;
	}

	.notice-item:active {
		background: #e3f2fd;
	}

	.notice-icon {
		width: 80rpx;
		height: 80rpx;
		display: flex;
		align-items: center;
		justify-content: center;
		border-radius: 50%;
		flex-shrink: 0;
	}

	.notice-icon.important {
		background: #ffebee;
		color: #f44336;
	}

	.notice-icon.maintenance {
		background: #fff3e0;
		color: #FF9800;
	}

	.notice-icon.event {
		background: #e8f5e8;
		color: #4CAF50;
	}

	.notice-icon.normal {
		background: #e3f2fd;
		color: #2196F3;
	}

	.notice-content {
		flex: 1;
		display: flex;
		flex-direction: column;
		gap: 8rpx;
	}

	.notice-title {
		font-size: 26rpx;
		font-weight: bold;
		color: #333;
	}

	.notice-summary {
		font-size: 24rpx;
		color: #666;
		line-height: 1.4;
		overflow: hidden;
		text-overflow: ellipsis;
		display: -webkit-box;
		-webkit-line-clamp: 2;
		-webkit-box-orient: vertical;
	}

	.notice-meta {
		display: flex;
		justify-content: space-between;
		align-items: center;
	}

	.notice-time {
		font-size: 22rpx;
		color: #999;
	}

	.notice-tag {
		padding: 4rpx 12rpx;
		border-radius: 12rpx;
		font-size: 20rpx;
		color: white;
	}

	.notice-tag.important {
		background: #f44336;
	}

	.notice-tag.maintenance {
		background: #FF9800;
	}

	.notice-tag.event {
		background: #4CAF50;
	}

	.notice-tag.normal {
		background: #2196F3;
	}

	.notice-arrow {
		font-size: 32rpx;
		color: #ccc;
		transform: rotate(90deg);
	}

	.service-list {
		display: flex;
		flex-direction: column;
		gap: 20rpx;
	}

	.service-row {
		display: grid;
		grid-template-columns: repeat(4, 1fr);
		gap: 15rpx;
	}

	.service-item-small {
		padding: 20rpx 10rpx;
		display: flex;
		flex-direction: column;
		align-items: center;
		gap: 10rpx;
		background: #f8f9fa;
		border-radius: 15rpx;
		transition: all 0.3s;
	}

	.service-item-small:active {
		transform: scale(0.95);
		background: #e3f2fd;
	}

	.service-icon-small {
		width: 36rpx;
		height: 36rpx;
	}

	.service-text-small {
		font-size: 22rpx;
		color: #333;
		text-align: center;
	}

	.search-popup {
		background: white;
		min-height: 100vh;
	}

	.search-header {
		padding: 20rpx;
		padding-top: 60rpx;
		background: #f8f9fa;
		border-bottom: 1rpx solid #e0e0e0;
	}

	.search-input-wrapper {
		display: flex;
		align-items: center;
		gap: 15rpx;
		padding: 15rpx 20rpx;
		background: white;
		border-radius: 25rpx;
	}

	.search-icon {
		width: 32rpx;
		height: 32rpx;
	}

	.search-input {
		flex: 1;
		font-size: 28rpx;
		border: none;
	}

	.cancel-btn {
		padding: 10rpx;
	}

	.cancel-text {
		font-size: 28rpx;
		color: #2979FF;
	}

	.search-content {
		padding: 30rpx 20rpx;
	}

	.suggestions-title {
		font-size: 26rpx;
		color: #666;
		margin-bottom: 20rpx;
		display: block;
	}

	.suggestion-tags {
		display: flex;
		flex-wrap: wrap;
		gap: 15rpx;
	}

	.suggestion-tag {
		padding: 15rpx 25rpx;
		background: #f0f0f0;
		border-radius: 25rpx;
		transition: all 0.3s;
	}

	.suggestion-tag:active {
		background: #e3f2fd;
	}

	.tag-text {
		font-size: 24rpx;
		color: #333;
	}

	.search-results {
		display: flex;
		flex-direction: column;
		gap: 15rpx;
	}

	.result-item {
		display: flex;
		align-items: center;
		gap: 20rpx;
		padding: 20rpx;
		background: #f8f9fa;
		border-radius: 15rpx;
		transition: all 0.3s;
	}

	.result-item:active {
		background: #e3f2fd;
	}

	.result-icon {
		width: 36rpx;
		height: 36rpx;
	}

	.result-content {
		flex: 1;
		display: flex;
		flex-direction: column;
		gap: 8rpx;
	}

	.result-title {
		font-size: 26rpx;
		font-weight: bold;
		color: #333;
	}

	.result-desc {
		font-size: 24rpx;
		color: #666;
	}

	.qr-popup {
		background: white;
		border-radius: 20rpx;
		width: 85vw;
		max-width: 600rpx;
	}

	.qr-header {
		display: flex;
		justify-content: space-between;
		align-items: center;
		padding: 30rpx;
		border-bottom: 1rpx solid #f0f0f0;
	}

	.qr-title {
		font-size: 32rpx;
		font-weight: bold;
		color: #333;
	}

	.close-btn {
		width: 60rpx;
		height: 60rpx;
		display: flex;
		align-items: center;
		justify-content: center;
		background: #f0f0f0;
		border-radius: 50%;
	}

	.close-icon {
		width: 24rpx;
		height: 24rpx;
	}

	.qr-content {
		padding: 30rpx;
		display: flex;
		flex-direction: column;
		align-items: center;
		gap: 30rpx;
	}

	.user-info {
		display: flex;
		align-items: center;
		gap: 20rpx;
		width: 100%;
		padding: 20rpx;
		background: #f8f9fa;
		border-radius: 15rpx;
	}

	.user-avatar {
		width: 100rpx;
		height: 100rpx;
		border-radius: 50%;
		background: #e0e0e0;
	}

	.user-details {
		flex: 1;
		display: flex;
		flex-direction: column;
		gap: 8rpx;
	}

	.user-name {
		font-size: 28rpx;
		font-weight: bold;
		color: #333;
	}

	.user-id {
		font-size: 24rpx;
		color: #666;
	}

	.user-status {
		font-size: 22rpx;
		color: #4CAF50;
	}

	.qr-code-container {
		display: flex;
		flex-direction: column;
		align-items: center;
		gap: 20rpx;
	}

	.qr-code {
		width: 300rpx;
		height: 300rpx;
		border: 2rpx solid #e0e0e0;
		border-radius: 15rpx;
		display: flex;
		align-items: center;
		justify-content: center;
		background: #f8f9fa;
	}

	.qr-placeholder {
		display: flex;
		align-items: center;
		justify-content: center;
		width: 100%;
		height: 100%;
	}

	.qr-text {
		font-size: 32rpx;
		color: #999;
	}

	.qr-desc {
		font-size: 24rpx;
		color: #666;
		text-align: center;
	}

	.qr-info {
		width: 100%;
		display: flex;
		flex-direction: column;
		gap: 15rpx;
	}

	.info-item {
		display: flex;
		justify-content: space-between;
		align-items: center;
		padding: 15rpx 0;
		border-bottom: 1rpx solid #f0f0f0;
	}

	.info-item:last-child {
		border-bottom: none;
	}

	.info-label {
		font-size: 26rpx;
		color: #666;
	}

	.info-value {
		font-size: 26rpx;
		color: #333;
		font-weight: bold;
	}

	.qr-actions {
		padding: 30rpx;
		border-top: 1rpx solid #f0f0f0;
	}

	.refresh-btn {
		width: 100%;
		padding: 25rpx;
		background: #2979FF;
		color: white;
		border: none;
		border-radius: 15rpx;
		font-size: 28rpx;
		font-weight: bold;
		transition: all 0.3s;
	}

	.refresh-btn:active {
		background: #1976D2;
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