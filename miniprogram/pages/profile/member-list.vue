<template>
	<view class="container">
		<!-- 头部 -->
		<view class="header">
			<text class="title">基地花名册</text>
			<text class="subtitle">共{{memberList.length}}名成员</text>
		</view>

		<!-- 搜索框 -->
		<view class="search-section">
			<view class="search-box">
				<text class="search-icon">🔍</text>
				<input class="search-input" placeholder="搜索成员姓名或专业" v-model="searchKeyword" @input="filterMembers" />
			</view>
		</view>

		<!-- 成员列表 -->
		<view class="member-list">
			<view class="member-card" 
				v-for="member in filteredMembers" 
				:key="member.id"
				@click="viewMemberDetail(member)">
				<view class="card-header">
					<image class="avatar" :src="member.avatar" mode="aspectFill"></image>
					<view class="member-info">
						<text class="name">{{member.name}}</text>
						<text class="role">{{member.position}}</text>
					</view>
				</view>
				<view class="card-content">
					<view class="info-row">
						<text class="label">专业：</text>
						<text class="value">{{member.major}}</text>
					</view>
					<view class="info-row">
						<text class="label">班级：</text>
						<text class="value">{{member.class}}</text>
					</view>
					<view class="tech-preview">
						<text class="tech-label">技术栈：</text>
						<view class="tech-tags">
							<text class="tech-tag" v-for="(tech, index) in member.techStack.slice(0, 3)" :key="index">{{tech}}</text>
							<text class="more-tech" v-if="member.techStack.length > 3">+{{member.techStack.length - 3}}</text>
						</view>
					</view>
				</view>
				<view class="card-footer">
					<text class="join-date">加入时间：{{member.joinDate}}</text>
					<text class="view-detail">查看详情 ›</text>
				</view>
			</view>
		</view>

		<!-- 空状态 -->
		<view class="empty-state" v-if="filteredMembers.length === 0">
			<text class="empty-icon">👥</text>
			<text class="empty-text">暂无匹配的成员</text>
		</view>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				searchKeyword: '',
				memberList: [
					{
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
					{
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
					{
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
					{
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
				],
				filteredMembers: []
			}
		},
		methods: {
			filterMembers() {
				if (!this.searchKeyword.trim()) {
					this.filteredMembers = this.memberList
				} else {
					const keyword = this.searchKeyword.toLowerCase()
					this.filteredMembers = this.memberList.filter(member => 
						member.name.toLowerCase().includes(keyword) ||
						member.major.toLowerCase().includes(keyword) ||
						member.class.toLowerCase().includes(keyword)
					)
				}
			},
			viewMemberDetail(member) {
				uni.navigateTo({
					url: `/pages/profile/member-detail?id=${member.id}`
				})
			},

		},
		onLoad() {
			this.filteredMembers = this.memberList
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
		display: block;
		margin-bottom: 10rpx;
	}

	.subtitle {
		font-size: 26rpx;
		opacity: 0.9;
	}

	.search-section {
		margin-bottom: 30rpx;
	}

	.search-box {
		background: white;
		border-radius: 50rpx;
		padding: 20rpx 30rpx;
		display: flex;
		align-items: center;
		box-shadow: 0 2rpx 12rpx rgba(0,0,0,0.1);
	}

	.search-icon {
		font-size: 28rpx;
		margin-right: 15rpx;
		color: #999;
	}

	.search-input {
		flex: 1;
		font-size: 28rpx;
		color: #333;
	}

	.member-list {
		display: flex;
		flex-direction: column;
		gap: 20rpx;
	}

	.member-card {
		background: white;
		border-radius: 20rpx;
		padding: 30rpx;
		box-shadow: 0 2rpx 12rpx rgba(0,0,0,0.1);
		border-left: 6rpx solid #2196F3;
	}

	.card-header {
		display: flex;
		align-items: center;
		margin-bottom: 20rpx;
	}

	.avatar {
		width: 80rpx;
		height: 80rpx;
		border-radius: 50%;
		margin-right: 20rpx;
	}

	.member-info {
		flex: 1;
	}

	.name {
		font-size: 30rpx;
		font-weight: bold;
		color: #333;
		display: block;
		margin-bottom: 5rpx;
	}

	.role {
		font-size: 24rpx;
		color: #666;
	}



	.card-content {
		margin-bottom: 20rpx;
	}

	.info-row {
		display: flex;
		margin-bottom: 10rpx;
	}

	.label {
		font-size: 26rpx;
		color: #666;
		width: 100rpx;
	}

	.value {
		font-size: 26rpx;
		color: #333;
		flex: 1;
	}

	.tech-preview {
		margin-top: 15rpx;
	}

	.tech-label {
		font-size: 26rpx;
		color: #666;
		display: block;
		margin-bottom: 10rpx;
	}

	.tech-tags {
		display: flex;
		flex-wrap: wrap;
		gap: 10rpx;
	}

	.tech-tag {
		background: #e3f2fd;
		color: #2196F3;
		padding: 6rpx 12rpx;
		border-radius: 15rpx;
		font-size: 22rpx;
	}

	.more-tech {
		background: #f5f5f5;
		color: #999;
		padding: 6rpx 12rpx;
		border-radius: 15rpx;
		font-size: 22rpx;
	}

	.card-footer {
		display: flex;
		justify-content: space-between;
		align-items: center;
		padding-top: 15rpx;
		border-top: 1rpx solid #f0f0f0;
	}

	.join-date {
		font-size: 24rpx;
		color: #999;
	}

	.view-detail {
		font-size: 24rpx;
		color: #2196F3;
		font-weight: 500;
	}

	.empty-state {
		text-align: center;
		padding: 100rpx 0;
	}

	.empty-icon {
		font-size: 80rpx;
		display: block;
		margin-bottom: 20rpx;
	}

	.empty-text {
		font-size: 28rpx;
		color: #999;
	}
</style>