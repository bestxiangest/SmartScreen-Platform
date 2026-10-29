<template>
	<view class="container">
		<!-- 头部 -->
		<view class="header">
			<text class="title">编辑个人信息</text>
		</view>

		<!-- 头像编辑 -->
		<view class="avatar-section">
			<view class="avatar-container" @click="changeAvatar">
				<image class="avatar" :src="userInfo.avatar" mode="aspectFill"></image>
				<view class="avatar-overlay">
				<image class="camera-icon" src="http://szzgurngv.hn-bkt.clouddn.com/icon/touxiang.png" mode="aspectFit"></image>
			</view>
			</view>
			<text class="avatar-tip">点击更换头像</text>
		</view>

		<!-- 基本信息编辑 -->
		<view class="form-section">
			<view class="section-title">基本信息</view>
			<view class="form-list">
				<view class="form-item">
					<text class="label">姓名</text>
					<input class="input" v-model="userInfo.name" placeholder="请输入姓名" />
				</view>
				<view class="form-item">
					<text class="label">性别</text>
					<picker mode="selector" :value="genderIndex" :range="genderOptions" @change="onGenderChange">
						<view class="picker-value">{{genderOptions[genderIndex]}}</view>
					</picker>
				</view>
				<view class="form-item">
					<text class="label">出生日期</text>
					<picker mode="date" :value="userInfo.birthday" @change="onBirthdayChange">
						<view class="picker-value">{{userInfo.birthday}}</view>
					</picker>
				</view>
				<view class="form-item">
					<text class="label">职务</text>
					<input class="input" v-model="userInfo.position" placeholder="请输入职务" />
				</view>
			</view>
		</view>

		<!-- 学业信息编辑 -->
		<view class="form-section">
			<view class="section-title">学业信息</view>
			<view class="form-list">
				<view class="form-item">
					<text class="label">学历</text>
					<picker mode="selector" :value="educationIndex" :range="educationOptions" @change="onEducationChange">
						<view class="picker-value">{{educationOptions[educationIndex]}}</view>
					</picker>
				</view>
				<view class="form-item">
					<text class="label">专业</text>
					<input class="input" v-model="userInfo.major" placeholder="请输入专业" />
				</view>
				<view class="form-item">
					<text class="label">班级</text>
					<input class="input" v-model="userInfo.class" placeholder="请输入班级" />
				</view>
				<view class="form-item">
					<text class="label">宿舍</text>
					<input class="input" v-model="userInfo.dormitory" placeholder="请输入宿舍" />
				</view>
			</view>
		</view>

		<!-- 联系方式编辑 -->
		<view class="form-section">
			<view class="section-title">联系方式</view>
			<view class="form-list">
				<view class="form-item">
					<text class="label">联系方式</text>
					<input class="input" v-model="userInfo.phone" placeholder="请输入联系方式" />
				</view>
				<view class="form-item">
					<text class="label">邮箱</text>
					<input class="input" v-model="userInfo.email" placeholder="请输入邮箱" />
				</view>
			</view>
		</view>

		<!-- 技术栈编辑 -->
		<view class="form-section">
			<view class="section-title">技术栈</view>
			<view class="tech-edit">
				<view class="tech-list">
					<view class="tech-item" v-for="(tech, index) in userInfo.techStack" :key="index">
						<text class="tech-text">{{tech}}</text>
						<text class="remove-btn" @click="removeTech(index)">×</text>
					</view>
				</view>
				<view class="add-tech">
					<input class="tech-input" v-model="newTech" placeholder="添加技术栈" @confirm="addTech" />
					<button class="add-btn" @click="addTech">添加</button>
				</view>
			</view>
		</view>

		<!-- 操作按钮 -->
		<view class="action-section">
			<button class="save-btn" @click="saveInfo">保存修改</button>
			<button class="cancel-btn" @click="cancel">取消</button>
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
				education: '本科',
				major: '计算机科学与技术',
				class: '计科2101班',
				dormitory: '1号楼301室',
				phone: '138****8888',
				email: 'zhangsan@example.com',
				avatar: '/static/avatars/user.jpg',
				techStack: ['JavaScript', 'Vue.js', 'Python', 'Java', 'MySQL', 'Git', 'Linux']
			},
			genderOptions: ['男', '女'],
			genderIndex: 0,
			educationOptions: ['专科', '本科', '硕士', '博士'],
			educationIndex: 1,
			newTech: ''
			}
		},
		methods: {
			changeAvatar() {
				uni.chooseImage({
					count: 1,
					sizeType: ['compressed'],
					sourceType: ['album', 'camera'],
					success: (res) => {
						this.userInfo.avatar = res.tempFilePaths[0]
						uni.showToast({
							title: '头像已更新',
							icon: 'success'
						})
					}
				})
			},
			onGenderChange(e) {
				this.genderIndex = e.detail.value
				this.userInfo.gender = this.genderOptions[e.detail.value]
			},
			onEducationChange(e) {
				this.educationIndex = e.detail.value
				this.userInfo.education = this.educationOptions[e.detail.value]
			},
			onBirthdayChange(e) {
				this.userInfo.birthday = e.detail.value
			},
			addTech() {
				if (this.newTech.trim()) {
					if (!this.userInfo.techStack.includes(this.newTech.trim())) {
						this.userInfo.techStack.push(this.newTech.trim())
						this.newTech = ''
					} else {
						uni.showToast({
							title: '技术栈已存在',
							icon: 'none'
						})
					}
				}
			},
			removeTech(index) {
				this.userInfo.techStack.splice(index, 1)
			},
			saveInfo() {
				// 验证必填字段
				if (!this.userInfo.name.trim()) {
					uni.showToast({
						title: '请输入姓名',
						icon: 'none'
					})
					return
				}
				
				if (!this.userInfo.email.trim() || !this.validateEmail(this.userInfo.email)) {
					uni.showToast({
						title: '请输入正确的邮箱',
						icon: 'none'
					})
					return
				}
				
				// 模拟保存到服务器
				uni.showLoading({
					title: '保存中...'
				})
				
				setTimeout(() => {
					// 保存到本地存储
					uni.setStorageSync('userInfo', this.userInfo)
					
					uni.hideLoading()
					uni.showToast({
						title: '保存成功',
						icon: 'success'
					})
					
					// 返回上一页
					setTimeout(() => {
						uni.navigateBack()
					}, 1500)
				}, 1000)
			},
			cancel() {
				uni.showModal({
					title: '确认取消',
					content: '确定要取消编辑吗？未保存的修改将丢失',
					success: (res) => {
						if (res.confirm) {
							uni.navigateBack()
						}
					}
				})
			},
			validateEmail(email) {
				const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
				return emailRegex.test(email)
			},
			loadUserInfo() {
				// 从本地存储加载用户信息
				const savedUserInfo = uni.getStorageSync('userInfo')
				if (savedUserInfo) {
					this.userInfo = { ...this.userInfo, ...savedUserInfo }
				}
				// 设置性别选择器的初始值
				this.genderIndex = this.genderOptions.indexOf(this.userInfo.gender)
			}
		},
		onLoad() {
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

	.title {
		font-size: 36rpx;
		font-weight: bold;
	}

	.avatar-section {
		background: white;
		border-radius: 20rpx;
		padding: 40rpx;
		margin-bottom: 30rpx;
		text-align: center;
		box-shadow: 0 2rpx 12rpx rgba(0,0,0,0.1);
	}

	.avatar-container {
		position: relative;
		display: inline-block;
		margin-bottom: 20rpx;
	}

	.avatar {
		width: 120rpx;
		height: 120rpx;
		border-radius: 50%;
	}

	.avatar-overlay {
		position: absolute;
		top: 0;
		left: 0;
		right: 0;
		bottom: 0;
		background: #2196F3;
		border-radius: 50%;
		display: flex;
		align-items: center;
		justify-content: center;
		opacity: 1;
		transition: opacity 0.3s;
	}

	.avatar-container:active .avatar-overlay {
		opacity: 1;
	}

	.camera-icon {
		width: 80rpx;
		height: 80rpx;
	}

	.avatar-tip {
		font-size: 28rpx;
		color: #999;
		position: relative;
		top: -40rpx; /* 负值上移元素 */
		left: 25rpx;
	}

	.form-section {
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

	.form-list {
		display: flex;
		flex-direction: column;
		gap: 25rpx;
	}

	.form-item {
		display: flex;
		align-items: center;
		padding: 20rpx 0;
		border-bottom: 1rpx solid #f5f5f5;
	}

	.form-item:last-child {
		border-bottom: none;
	}

	.label {
		font-size: 28rpx;
		color: #666;
		font-weight: 500;
		width: 120rpx;
		flex-shrink: 0;
	}

	.input {
		flex: 1;
		font-size: 28rpx;
		color: #333;
		padding: 10rpx 20rpx;
		background: #f8f9fa;
		border-radius: 10rpx;
		border: 1rpx solid #e9ecef;
	}

	.picker-value {
		flex: 1;
		font-size: 28rpx;
		color: #333;
		padding: 10rpx 20rpx;
		background: #f8f9fa;
		border-radius: 10rpx;
		border: 1rpx solid #e9ecef;
		text-align: left;
	}

	.tech-edit {
		display: flex;
		flex-direction: column;
		gap: 20rpx;
	}

	.tech-list {
		display: flex;
		flex-wrap: wrap;
		gap: 15rpx;
	}

	.tech-item {
		background: #e3f2fd;
		color: #2196F3;
		padding: 8rpx 16rpx;
		border-radius: 20rpx;
		font-size: 22rpx;
		display: flex;
		align-items: center;
		gap: 8rpx;
	}

	.tech-text {
		font-weight: 500;
	}

	.remove-btn {
		font-size: 24rpx;
		font-weight: bold;
		width: 28rpx;
		height: 28rpx;
		background: rgba(33, 150, 243, 0.2);
		color: #2196F3;
		border-radius: 50%;
		display: flex;
		align-items: center;
		justify-content: center;
		line-height: 1;
	}

	.add-tech {
		display: flex;
		gap: 15rpx;
		align-items: center;
	}

	.tech-input {
		flex: 1;
		font-size: 26rpx;
		color: #333;
		padding: 15rpx 20rpx;
		background: #f8f9fa;
		border-radius: 25rpx;
		border: 1rpx solid #e9ecef;
	}

	.add-btn {
		padding: 15rpx 30rpx;
		background: linear-gradient(135deg, #2196F3 0%, #42A5F5 100%);
		color: white;
		border: none;
		border-radius: 25rpx;
		font-size: 26rpx;
		font-weight: 500;
	}

	.action-section {
		display: flex;
		gap: 20rpx;
		padding: 30rpx 0;
	}

	.save-btn {
		flex: 1;
		height: 88rpx;
		background: linear-gradient(135deg, #2196F3 0%, #42A5F5 100%);
		color: white;
		border: none;
		border-radius: 44rpx;
		font-size: 32rpx;
		font-weight: bold;
	}

	.cancel-btn {
		flex: 1;
		height: 88rpx;
		background: white;
		color: #2196F3;
		border: 2rpx solid #2196F3;
		border-radius: 44rpx;
		font-size: 32rpx;
		font-weight: bold;
	}

	.save-btn:active,
	.cancel-btn:active {
		opacity: 0.8;
	}
</style>