<template>
	<view class="container">
		<!-- 用户信息卡片 -->
		<view class="user-card fade-in-down">
			<view class="user-header">
				<image class="user-avatar floating" :src="userInfo.avatar" mode="aspectFill"></image>
				<view class="user-info">
					<text class="user-name">{{userInfo.name}}</text>
					<text class="user-role">{{userInfo.role}}</text>
					<text class="user-id">学号：{{userInfo.studentId}}</text>
				</view>
				<view class="edit-btn card-hover" @click="editProfile">
					<image class="edit-icon icon-bounce" src="http://szzgurngv.hn-bkt.clouddn.com/icon/edit.png" mode="aspectFit"></image>
				</view>
			</view>
			
			<view class="user-stats">
				<view class="stat-item card-hover scale-in" style="animation-delay: 0.1s;" @click="viewMyProjects">
					<text class="stat-number">{{userStats.projects}}</text>
					<text class="stat-label">参与项目</text>
				</view>
				<view class="stat-item card-hover scale-in" style="animation-delay: 0.2s;" @click="viewMyBookings">
					<text class="stat-number">{{userStats.bookings}}</text>
					<text class="stat-label">设备预约</text>
				</view>
				<view class="stat-item card-hover scale-in" style="animation-delay: 0.3s;" @click="viewMyReports">
					<text class="stat-number">{{userStats.reports}}</text>
					<text class="stat-label">安全上报</text>
				</view>
				<view class="stat-item card-hover scale-in" style="animation-delay: 0.4s;" @click="viewMyCredits">
					<text class="stat-number">{{userStats.credits}}</text>
					<text class="stat-label">积分余额</text>
				</view>
			</view>
		</view>

		<!-- 个人信息 -->
		<view class="info-section highlight-section fade-in-up" style="animation-delay: 0.5s;">
			<view class="section-title"><image class="section-icon icon-bounce" src="/static/icons/icon-person.png" mode="aspectFit"></image> 个人信息</view>
			<view class="info-grid">
				<view class="info-item card-hover">
					<text class="info-label">姓名：</text>
					<text class="info-value">{{userInfo.name}}</text>
				</view>
				<view class="info-item card-hover">
					<text class="info-label">性别：</text>
					<text class="info-value">{{userInfo.gender}}</text>
				</view>
				<view class="info-item card-hover">
					<text class="info-label">出生日期：</text>
					<text class="info-value">{{userInfo.birthday}}</text>
				</view>
				<view class="info-item card-hover">
					<text class="info-label">职务：</text>
					<text class="info-value">{{userInfo.position}}</text>
				</view>
				<view class="info-item card-hover">
					<text class="info-label">学历：</text>
					<text class="info-value">{{userInfo.education}}</text>
				</view>
				<view class="info-item card-hover">
					<text class="info-label">专业：</text>
					<text class="info-value">{{userInfo.major}}</text>
				</view>
				<view class="info-item card-hover">
					<text class="info-label">班级：</text>
					<text class="info-value">{{userInfo.class}}</text>
				</view>
				<view class="info-item card-hover">
					<text class="info-label">宿舍：</text>
					<text class="info-value">{{userInfo.dormitory}}</text>
				</view>
				<view class="info-item card-hover">
					<text class="info-label">联系方式：</text>
					<text class="info-value">{{userInfo.phone}}</text>
				</view>
				<view class="info-item card-hover">
					<text class="info-label">邮箱：</text>
					<text class="info-value">{{userInfo.email}}</text>
				</view>
			</view>
		</view>

		<!-- 技术栈 -->
		<view class="info-section highlight-section fade-in-up" style="animation-delay: 0.6s;">
			<view class="section-title"><image class="section-icon icon-bounce" src="/static/icons/icon-computer.png" mode="aspectFit"></image> 技术栈</view>
			<view class="tech-stack">
				<view class="tech-item highlight-tech card-hover" v-for="(tech, index) in userInfo.techStack" :key="tech" :style="{animationDelay: (0.7 + index * 0.1) + 's'}">
					<text class="tech-text">{{tech}}</text>
				</view>
			</view>
		</view>

		<!-- 操作按钮 -->
		<view class="action-section action-row fade-in-up" style="animation-delay: 1.4s;">
			<button class="action-btn btn-hover" @click="viewMemberList">基地花名册</button>
			<button class="action-btn btn-hover" @click="editInfo">编辑资料</button>
		</view>

		<!-- 退出登录 -->
		<view class="logout-section fade-in-up" style="animation-delay: 1.5s;">
			<button class="logout-btn btn-hover" @click="logout">退出登录</button>
		</view>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				userInfo: {
				name: '张三',
				role: '计算机科学与技术专业',
				studentId: '2021001001',
				avatar: '/static/avatars/user.jpg',
				gender: '男',
				birthday: '2001-05-15',
				position: '学生',
				education: '本科',
				major: '计算机科学与技术',
				class: '计科2101班',
				dormitory: '1号楼301室',
				phone: '138****8888',
				email: 'zhangsan@example.com',
				techStack: ['JavaScript', 'Vue.js', 'Python', 'Java', 'MySQL', 'Git', 'Linux']
			},
				userStats: {
					projects: 5,
					bookings: 12,
					reports: 3,
					credits: 1250
				}
			}
		},
		methods: {
			editProfile() {
				uni.navigateTo({
					url: '/pages/profile/edit'
				})
			},
			viewMyProjects() {
				uni.navigateTo({
					url: '/pages/profile/my-projects'
				})
			},
			viewMyBookings() {
				uni.navigateTo({
					url: '/pages/profile/my-bookings'
				})
			},
			viewMyReports() {
				uni.navigateTo({
					url: '/pages/profile/my-reports'
				})
			},
			viewMyCredits() {
				uni.navigateTo({
					url: '/pages/profile/credits'
				})
			},
			viewMemberList() {
				uni.navigateTo({
					url: '/pages/profile/member-list'
				})
			},
			editInfo() {
				uni.navigateTo({
					url: '/pages/profile/edit-info'
				})
			},
			loadUserInfo() {
				// 从本地存储加载用户信息
				const savedUserInfo = uni.getStorageSync('userInfo')
				if (savedUserInfo) {
					this.userInfo = { ...this.userInfo, ...savedUserInfo }
					// 根据专业和班级信息更新role显示
					if (savedUserInfo.major && savedUserInfo.class) {
						this.userInfo.role = `${savedUserInfo.major} ${savedUserInfo.class}`
					} else if (savedUserInfo.major) {
						this.userInfo.role = `${savedUserInfo.major}专业学生`
					}
				}
				console.log('加载用户详细信息', this.userInfo)
			},
			logout() {
				uni.showModal({
					title: '退出登录',
					content: '确定要退出登录吗？',
					success: (res) => {
						if (res.confirm) {
							// 清除用户数据
							uni.clearStorageSync()
							
							// 跳转到登录页
							uni.reLaunch({
								url: '/pages/login/login'
							})
							
							uni.showToast({
								title: '已退出登录',
								icon: 'success'
							})
						}
					}
				})
			}
		},
		onLoad() {
			// 加载用户信息
			this.loadUserInfo()
		},
		onShow() {
			// 页面显示时重新加载用户信息（从编辑页面返回时会触发）
			this.loadUserInfo()
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
		background: #e3f2fd;
		min-height: 100vh;
	}

	.user-card {
		background: linear-gradient(135deg, #2196F3 0%, #42A5F5 100%);
		border-radius: 20rpx;
		padding: 40rpx;
		margin-bottom: 30rpx;
		color: white;
	}

	.user-header {
		display: flex;
		align-items: center;
		gap: 25rpx;
		margin-bottom: 30rpx;
	}

	.user-avatar {
		width: 120rpx;
		height: 120rpx;
		border-radius: 50%;
		border: 6rpx solid rgba(255, 255, 255, 0.3);
		background: rgba(255, 255, 255, 0.1);
	}

	.user-info {
		flex: 1;
		display: flex;
		flex-direction: column;
		gap: 8rpx;
	}

	.user-name {
		font-size: 36rpx;
		font-weight: bold;
	}

	.user-role {
		font-size: 26rpx;
		opacity: 0.9;
	}

	.user-id {
		font-size: 24rpx;
		opacity: 0.8;
	}

	.edit-btn {
		width: 100rpx;
		height: 100rpx;
		display: flex;
		align-items: center;
		justify-content: center;
		background: linear-gradient(135deg, #e3f2fd 0%, #f3e5f5 100%);
		border-radius: 50%;
	}

	.edit-icon {
		width: 80rpx;
		height: 80rpx;
	}

	.user-stats {
		display: grid;
		grid-template-columns: repeat(4, 1fr);
		gap: 20rpx;
	}

	.stat-item {
		padding: 20rpx;
		background: rgba(255, 255, 255, 0.15);
		border-radius: 15rpx;
		text-align: center;
		display: flex;
		flex-direction: column;
		gap: 8rpx;
		transition: all 0.3s;
	}

	.stat-item:active {
		transform: scale(0.95);
		background: rgba(255, 255, 255, 0.25);
	}

	.stat-number {
		font-size: 32rpx;
		font-weight: bold;
	}

	.stat-label {
		font-size: 22rpx;
		opacity: 0.9;
	}

	.quick-functions {
		background: white;
		border-radius: 20rpx;
		padding: 30rpx;
		margin-bottom: 30rpx;
	}

	.function-row {
		display: grid;
		grid-template-columns: repeat(4, 1fr);
		gap: 20rpx;
	}

	.function-item {
		padding: 25rpx 15rpx;
		display: flex;
		flex-direction: column;
		align-items: center;
		gap: 12rpx;
		position: relative;
		transition: all 0.3s;
	}

	.function-item:active {
		transform: scale(0.95);
		background: #f8f9fa;
		border-radius: 15rpx;
	}

	.function-icon {
		font-size: 48rpx;
	}

	.function-text {
		font-size: 24rpx;
		color: #333;
		text-align: center;
	}

	.notification-badge {
		position: absolute;
		top: 15rpx;
		right: 15rpx;
		min-width: 32rpx;
		height: 32rpx;
		background: #2196F3;
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

	.menu-section {
		display: flex;
		flex-direction: column;
		gap: 30rpx;
		margin-bottom: 30rpx;
	}

	.menu-group {
		background: white;
		border-radius: 20rpx;
		padding: 30rpx;
	}

	.group-title {
		font-size: 28rpx;
		font-weight: bold;
		color: #333;
		margin-bottom: 20rpx;
		display: block;
	}

	.menu-item {
		display: flex;
		align-items: center;
		justify-content: space-between;
		padding: 25rpx 0;
		border-bottom: 1rpx solid #f0f0f0;
		transition: all 0.3s;
	}

	.menu-item:last-child {
		border-bottom: none;
	}

	.menu-item:active {
		background: #f8f9fa;
		margin: 0 -30rpx;
		padding: 25rpx 30rpx;
		border-radius: 15rpx;
	}

	.menu-left {
		display: flex;
		align-items: center;
		gap: 20rpx;
	}

	.menu-icon {
		font-size: 36rpx;
	}

	.menu-text {
		font-size: 28rpx;
		color: #333;
	}

	.menu-arrow {
		font-size: 32rpx;
		color: #ccc;
		transform: rotate(90deg);
	}



	.info-section {
		background: white;
		border-radius: 20rpx;
		padding: 30rpx;
		margin-bottom: 30rpx;
		box-shadow: 0 4rpx 20rpx rgba(33, 150, 243, 0.1);
	}

	.highlight-section {
		background: linear-gradient(135deg, #e3f2fd 0%, #f3e5f5 100%);
		border: 2rpx solid #2196F3;
		box-shadow: 0 6rpx 25rpx rgba(33, 150, 243, 0.2);
	}

	.section-title {
		font-size: 32rpx;
		font-weight: bold;
		color: #2196F3;
		margin-bottom: 25rpx;
		display: flex;
		align-items: center;
		justify-content: center;
		gap: 10rpx;
	}
	
	.section-icon {
		width: 32rpx;
		height: 32rpx;
	}

	.info-grid {
		display: grid;
		grid-template-columns: 1fr 1fr;
		gap: 20rpx;
	}

	.info-grid .info-item {
		display: flex;
		flex-direction: row;
		align-items: center;
		padding: 15rpx;
		border: none;
		border-bottom: none;
		background: rgba(255, 255, 255, 0.8);
		border-radius: 10rpx;
		width: 100%;
		box-sizing: border-box;
		overflow: hidden;
	}

	.info-grid .info-item .info-label {
		font-size: 22rpx;
		color: #666;
		font-weight: 500;
		margin-right: 8rpx;
		flex-shrink: 0;
		width: auto;
		white-space: nowrap;
	}

	.info-grid .info-item .info-value {
		font-size: 24rpx;
		color: #333;
		font-weight: 600;
		flex: 1;
		word-wrap: break-word;
		word-break: break-all;
		overflow: hidden;
		text-overflow: ellipsis;
	}

	.info-card {
		background: #f8f9fa;
		border-radius: 15rpx;
		padding: 20rpx;
		text-align: center;
		border: 1rpx solid #e9ecef;
		transition: all 0.3s;
	}

	.info-card:active {
		transform: scale(0.98);
		box-shadow: 0 2rpx 10rpx rgba(33, 150, 243, 0.1);
	}

	.info-card .info-label {
		font-size: 22rpx;
		color: #666;
		margin-bottom: 8rpx;
		display: block;
		font-weight: 500;
	}

	.info-card .info-value {
		font-size: 26rpx;
		color: #333;
		font-weight: 600;
		display: block;
	}

	.info-item {
		display: flex;
		align-items: center;
		padding: 20rpx 0;
		border-bottom: 1rpx solid #f0f0f0;
	}

	.info-item:last-child {
		border-bottom: none;
	}

	.highlight-item {
		background: rgba(33, 150, 243, 0.05);
		border-radius: 10rpx;
		padding: 25rpx 20rpx;
		margin: 10rpx 0;
		border: 2rpx solid #2196F3;
	}

	.info-label {
		width: 120rpx;
		font-size: 24rpx;
		color: #666;
		flex-shrink: 0;
		font-weight: 500;
	}

	.info-value {
		flex: 1;
		font-size: 26rpx;
		color: #333;
	}

	.highlight-value {
		font-size: 30rpx;
		font-weight: bold;
		color: #2196F3;
	}

	.tech-stack {
		display: flex;
		flex-wrap: wrap;
		gap: 15rpx;
		margin-top: 10rpx;
		justify-content: center;
	}

	.tech-tag {
		padding: 8rpx 16rpx;
		background: #e3f2fd;
		color: #2196F3;
		border-radius: 20rpx;
		font-size: 22rpx;
	}

	.highlight-tech {
		padding: 12rpx 20rpx;
		background: linear-gradient(135deg, #2196F3 0%, #42A5F5 100%);
		color: white;
		border-radius: 25rpx;
		font-size: 24rpx;
		font-weight: 600;
		box-shadow: 0 4rpx 15rpx rgba(33, 150, 243, 0.3);
		transition: all 0.3s;
		transform: scale(1.05);
	}

	.highlight-tech:active {
		transform: scale(1.02);
	}

	.action-section {
		margin-bottom: 30rpx;
	}

	.action-row {
		display: flex;
		gap: 20rpx;
	}

	.action-buttons {
		display: flex;
		gap: 20rpx;
		margin-bottom: 30rpx;
	}

	.action-btn {
		flex: 1;
		padding: 25rpx;
		background: white;
		color: #2196F3;
		border: 2rpx solid #2196F3;
		border-radius: 15rpx;
		font-size: 26rpx;
		font-weight: bold;
		text-align: center;
		transition: all 0.3s;
	}

	.action-btn:active {
		background: #2196F3;
		color: white;
	}

	.logout-section {
		padding: 20rpx 0;
	}

	.logout-btn {
		width: 100%;
		padding: 30rpx;
		background: white;
		color: #ff5722;
		border: 2rpx solid #ff5722;
		border-radius: 15rpx;
		font-size: 28rpx;
		font-weight: bold;
		transition: all 0.3s;
	}

	.logout-btn:active {
		background: #ff5722;
		color: white;
	}
</style>