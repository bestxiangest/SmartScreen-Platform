<template>
	<view class="container">
		<!-- 头部 -->
		<view class="header">
			<view class="avatar-section">
				<image class="avatar" :src="userInfo.avatar" mode="aspectFill"></image>
				<text class="name">{{userInfo.name}}</text>
				<text class="role">{{userInfo.role}}</text>
			</view>
		</view>

		<!-- 个人信息 -->
		<view class="info-section">
			<view class="section-title">基本信息</view>
			<view class="info-list">
				<view class="info-item">
					<text class="label">姓名</text>
					<text class="value">{{userInfo.name}}</text>
				</view>
				<view class="info-item">
					<text class="label">性别</text>
					<text class="value">{{userInfo.gender}}</text>
				</view>
				<view class="info-item">
					<text class="label">出生日期</text>
					<text class="value">{{userInfo.birthday}}</text>
				</view>
				<view class="info-item">
					<text class="label">职务</text>
					<text class="value">{{userInfo.position}}</text>
				</view>
			</view>
		</view>

		<!-- 学业信息 -->
		<view class="info-section">
			<view class="section-title">学业信息</view>
			<view class="info-list">
				<view class="info-item">
					<text class="label">专业</text>
					<text class="value">{{userInfo.major}}</text>
				</view>
				<view class="info-item">
					<text class="label">班级</text>
					<text class="value">{{userInfo.class}}</text>
				</view>
				<view class="info-item">
					<text class="label">宿舍</text>
					<text class="value">{{userInfo.dormitory}}</text>
				</view>
			</view>
		</view>

		<!-- 联系方式 -->
		<view class="info-section">
			<view class="section-title">联系方式</view>
			<view class="info-list">
				<view class="info-item">
					<text class="label">联系方式</text>
					<text class="value">{{userInfo.phone}}</text>
				</view>
				<view class="info-item">
					<text class="label">邮箱</text>
					<text class="value">{{userInfo.email}}</text>
				</view>
			</view>
		</view>

		<!-- 技术栈 -->
		<view class="info-section">
			<view class="section-title">技术栈</view>
			<view class="tech-stack">
				<view class="tech-item" v-for="tech in userInfo.techStack" :key="tech">
					<text class="tech-text">{{tech}}</text>
				</view>
			</view>
		</view>

		<!-- 编辑按钮 -->
		<view class="edit-section">
			<button class="edit-btn" @click="editInfo">编辑信息</button>
		</view>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				userInfo: {
					name: '张三',
					gender: '男',
					birthday: '2001-05-15',
					position: '学生',
					major: '计算机科学与技术',
					class: '计科2101班',
					dormitory: '1号楼301室',
					phone: '138****8888',
					email: 'zhangsan@example.com',
					role: '计算机科学与技术专业学生',
					avatar: '/static/avatars/user.jpg',
					techStack: ['JavaScript', 'Vue.js', 'Python', 'Java', 'MySQL', 'Git', 'Linux']
				}
			}
		},
		methods: {
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
	.container {
		padding: 20rpx;
		background: #e3f2fd;
		min-height: 100vh;
	}

	.header {
		background: linear-gradient(135deg, #2196F3 0%, #42A5F5 100%);
		border-radius: 20rpx;
		padding: 40rpx;
		margin-bottom: 30rpx;
		color: white;
		text-align: center;
	}

	.avatar-section {
		display: flex;
		flex-direction: column;
		align-items: center;
	}

	.avatar {
		width: 120rpx;
		height: 120rpx;
		border-radius: 50%;
		margin-bottom: 20rpx;
		border: 4rpx solid rgba(255,255,255,0.3);
	}

	.name {
		font-size: 36rpx;
		font-weight: bold;
		margin-bottom: 10rpx;
	}

	.role {
		font-size: 26rpx;
		opacity: 0.9;
	}

	.info-section {
		background: white;
		border-radius: 20rpx;
		padding: 30rpx;
		margin-bottom: 30rpx;
		box-shadow: 0 2rpx 12rpx rgba(0,0,0,0.1);
	}

	.section-title {
		font-size: 32rpx;
		font-weight: bold;
		color: #333;
		margin-bottom: 30rpx;
		padding-bottom: 15rpx;
		border-bottom: 2rpx solid #f0f0f0;
	}

	.info-list {
		display: flex;
		flex-direction: column;
		gap: 25rpx;
	}

	.info-item {
		display: flex;
		justify-content: space-between;
		align-items: center;
		padding: 20rpx 0;
		border-bottom: 1rpx solid #f5f5f5;
	}

	.info-item:last-child {
		border-bottom: none;
	}

	.label {
		font-size: 28rpx;
		color: #666;
		font-weight: 500;
	}

	.value {
		font-size: 28rpx;
		color: #333;
		font-weight: 600;
	}

	.tech-stack {
		display: flex;
		flex-wrap: wrap;
		gap: 15rpx;
	}

	.tech-item {
		background: linear-gradient(135deg, #2196F3 0%, #42A5F5 100%);
		color: white;
		padding: 12rpx 24rpx;
		border-radius: 25rpx;
		font-size: 24rpx;
	}

	.tech-text {
		font-weight: 500;
	}

	.edit-section {
		padding: 30rpx 0;
	}

	.edit-btn {
		width: 100%;
		height: 88rpx;
		background: linear-gradient(135deg, #2196F3 0%, #42A5F5 100%);
		color: white;
		border: none;
		border-radius: 44rpx;
		font-size: 32rpx;
		font-weight: bold;
		display: flex;
		align-items: center;
		justify-content: center;
	}

	.edit-btn:active {
		opacity: 0.8;
	}
</style>