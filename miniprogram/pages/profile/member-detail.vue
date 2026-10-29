<template>
	<view class="container">
		<!-- 头部 -->
		<view class="header">
			<view class="avatar-section">
				<image class="avatar" :src="memberInfo.avatar" mode="aspectFill"></image>
				<text class="name">{{memberInfo.name}}</text>
				<text class="role">{{memberInfo.position}} · {{memberInfo.major}}</text>
			</view>
		</view>

		<!-- 基本信息 -->
		<view class="info-section">
			<view class="section-title">基本信息</view>
			<view class="info-list">
				<view class="info-item">
					<text class="label">姓名</text>
					<text class="value">{{memberInfo.name}}</text>
				</view>
				<view class="info-item">
					<text class="label">性别</text>
					<text class="value">{{memberInfo.gender}}</text>
				</view>
				<view class="info-item">
					<text class="label">出生日期</text>
					<text class="value">{{memberInfo.birthday}}</text>
				</view>
				<view class="info-item">
					<text class="label">职务</text>
					<text class="value">{{memberInfo.position}}</text>
				</view>
				<view class="info-item">
					<text class="label">加入时间</text>
					<text class="value">{{memberInfo.joinDate}}</text>
				</view>
			</view>
		</view>

		<!-- 学业信息 -->
		<view class="info-section">
			<view class="section-title">学业信息</view>
			<view class="info-list">
				<view class="info-item">
					<text class="label">专业</text>
					<text class="value">{{memberInfo.major}}</text>
				</view>
				<view class="info-item">
					<text class="label">班级</text>
					<text class="value">{{memberInfo.class}}</text>
				</view>
				<view class="info-item">
					<text class="label">宿舍</text>
					<text class="value">{{memberInfo.dormitory}}</text>
				</view>
			</view>
		</view>

		<!-- 联系方式 -->
		<view class="info-section">
			<view class="section-title">联系方式</view>
			<view class="info-list">
				<view class="info-item">
					<text class="label">联系方式</text>
					<text class="value">{{memberInfo.phone}}</text>
				</view>
				<view class="info-item">
					<text class="label">邮箱</text>
					<text class="value">{{memberInfo.email}}</text>
				</view>
			</view>
		</view>

		<!-- 技术栈 -->
		<view class="info-section">
			<view class="section-title">技术栈</view>
			<view class="tech-stack">
				<view class="tech-item" v-for="tech in memberInfo.techStack" :key="tech">
					<text class="tech-text">{{tech}}</text>
				</view>
			</view>
		</view>


	</view>
</template>

<script>
	export default {
		data() {
			return {
				memberId: '',
				memberInfo: {}
			}
		},
		methods: {


			loadMemberInfo() {
				// 模拟成员数据
				const memberData = {
					'1': {
						id: 1,
						name: '张三',
						gender: '男',
						birthday: '2001-05-15',
						position: '学生',
						major: '计算机科学与技术',
						class: '计科2101班',
						dormitory: '1号楼301室',
						phone: '138****8888',
						email: 'zhangsan@example.com',
						avatar: '/static/avatars/user1.jpg',
						status: 'online',
						joinDate: '2021-09-01',
						techStack: ['JavaScript', 'Vue.js', 'Python', 'Java', 'MySQL', 'Git', 'Linux']
					},
					'2': {
						id: 2,
						name: '李四',
						gender: '女',
						birthday: '2002-03-20',
						position: '学生',
						major: '软件工程',
						class: '软工2102班',
						dormitory: '2号楼205室',
						phone: '139****9999',
						email: 'lisi@example.com',
						avatar: '/static/avatars/user2.jpg',
						status: 'busy',
						joinDate: '2021-09-01',
						techStack: ['React', 'Node.js', 'MongoDB', 'TypeScript', 'Docker']
					},
					'3': {
						id: 3,
						name: '王五',
						gender: '男',
						birthday: '2001-11-08',
						position: '研究生',
						major: '人工智能',
						class: '人工智能2001班',
						dormitory: '研究生楼A101室',
						phone: '137****7777',
						email: 'wangwu@example.com',
						avatar: '/static/avatars/user3.jpg',
						status: 'offline',
						joinDate: '2020-09-01',
						techStack: ['Python', 'TensorFlow', 'PyTorch', 'OpenCV', 'Pandas', 'Scikit-learn']
					},
					'4': {
						id: 4,
						name: '赵六',
						gender: '女',
						birthday: '2002-07-12',
						position: '学生',
						major: '数据科学与大数据技术',
						class: '数据2101班',
						dormitory: '2号楼308室',
						phone: '136****6666',
						email: 'zhaoliu@example.com',
						avatar: '/static/avatars/user4.jpg',
						status: 'online',
						joinDate: '2021-09-01',
						techStack: ['R', 'Python', 'Spark', 'Hadoop', 'Tableau', 'SQL']
					}
				}
				
				this.memberInfo = memberData[this.memberId] || {}
			}
		},
		onLoad(options) {
			this.memberId = options.id || '1'
			this.loadMemberInfo()
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
		margin-bottom: 15rpx;
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
		justify-content: center;
		align-items: center;
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


</style>