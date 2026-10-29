<template>
	<view class="container">
		<!-- 项目统计 -->
		<view class="project-stats fade-in-down">
			<view class="stat-item card-hover scale-in" style="animation-delay: 0.1s;">
				<text class="stat-number floating">{{projectStats.total}}</text>
				<text class="stat-label">总项目</text>
			</view>
			<view class="stat-item card-hover scale-in" style="animation-delay: 0.2s;">
				<text class="stat-number floating">{{projectStats.active}}</text>
				<text class="stat-label">进行中</text>
			</view>
			<view class="stat-item card-hover scale-in" style="animation-delay: 0.3s;">
				<text class="stat-number floating">{{projectStats.completed}}</text>
				<text class="stat-label">已完成</text>
			</view>
			<view class="stat-item card-hover scale-in" style="animation-delay: 0.4s;">
				<text class="stat-number floating">{{projectStats.awards}}</text>
				<text class="stat-label">获奖项目</text>
			</view>
		</view>



		<!-- 项目筛选 -->
		<view class="project-filter fade-in-up" style="animation-delay: 0.5s;">
			<view class="filter-tabs">
				<text class="filter-tab btn-hover" 
					v-for="filter in filterTabs" 
					:key="filter.value"
					:class="{active: currentFilter === filter.value}"
					@click="currentFilter = filter.value">
					{{filter.label}}
				</text>
			</view>
			<view class="search-bar">
				<input class="search-input" 
					v-model="searchKeyword" 
					placeholder="搜索项目名称或关键词"
					@input="onSearch" />
			</view>
		</view>

		<!-- 项目列表 -->
		<view class="project-list">
			<view class="project-card card-hover fade-in-up" 
				v-for="(project, index) in filteredProjects" 
				:key="project.id"
				:style="{animationDelay: (0.6 + index * 0.1) + 's'}"
				@click="navigateTo(`/pages/project/detail?id=${project.id}`)">
				
				<!-- 项目头部 -->
				<view class="project-header">
					<view class="project-info">
						<text class="project-title">{{project.title}}</text>
						<text class="project-category">{{project.category}}</text>
					</view>
					<view class="project-status" :class="project.status">
						<text class="status-text">{{getStatusText(project.status)}}</text>
					</view>
				</view>

				<!-- 项目描述 -->
				<text class="project-description">{{project.description}}</text>

				<!-- 项目进度 -->
				<view class="project-progress">
					<view class="progress-info">
						<text class="progress-label">项目进度</text>
						<text class="progress-percent">{{project.progress}}%</text>
					</view>
					<view class="progress-bar">
						<view class="progress-fill" :style="{width: project.progress + '%'}"></view>
					</view>
				</view>

				<!-- 团队信息 -->
				<view class="project-team">
					<view class="team-avatars">
						<image class="avatar floating" 
							v-for="(member, index) in project.team.slice(0, 3)" 
							:key="index"
							:src="member.avatar" 
							mode="aspectFill">
						</image>
						<view class="more-members" v-if="project.team.length > 3">
							<text class="more-text">+{{project.team.length - 3}}</text>
						</view>
					</view>
					<view class="project-meta">
						<view class="meta-item">
							<image class="meta-icon icon-bounce" src="/static/icon-team.png" mode="aspectFit"></image>
							<text>{{project.team.length}}人</text>
						</view>
						<view class="meta-item">
							<image class="meta-icon icon-bounce" src="/static/icon-calendar.png" mode="aspectFit"></image>
							<text>{{project.deadline}}</text>
						</view>
						<view class="meta-item" v-if="project.award">
							<image class="meta-icon icon-bounce" src="/static/icon-trophy.png" mode="aspectFit"></image>
							<text>{{project.award}}</text>
						</view>
					</view>
				</view>

				<!-- 项目标签 -->
				<view class="project-tags">
					<text class="tag" v-for="tag in project.tags" :key="tag">{{tag}}</text>
				</view>
			</view>
		</view>

		<!-- 空状态 -->
		<view class="empty-state" v-if="filteredProjects.length === 0">
			<image class="empty-icon" src="/static/icon-clipboard.png" mode="aspectFit"></image>
			<text class="empty-text">暂无项目</text>
			<text class="empty-tip">点击上方按钮创建或加入项目</text>
		</view>

		<!-- 浮动创建按钮 -->
		<image class="floating-icon floating card-hover" src="http://szzgurngv.hn-bkt.clouddn.com/icon/plus-copy.png" @click="navigateTo('/pages/project/create')" mode="aspectFit"></image>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				currentFilter: 'all',
				searchKeyword: '',
				projectStats: {
					total: 15,
					active: 8,
					completed: 6,
					awards: 3
				},
				filterTabs: [
					{value: 'all', label: '全部'},
					{value: 'active', label: '进行中'},
					{value: 'completed', label: '已完成'},
					{value: 'my', label: '我的项目'}
				],
				projects: [
					{
						id: 1,
						title: '智能实验室管理系统',
						category: '软件开发',
						description: '基于物联网技术的智能实验室环境监控与设备管理系统，实现实验室的数字化转型。',
						status: 'active',
						progress: 75,
						deadline: '2025-06-30',
						award: null,
						tags: ['物联网', 'Vue.js', '数据分析'],
						team: [
							{name: '张三', avatar: '/static/avatars/avatar1.jpg'},
							{name: '李四', avatar: '/static/avatars/avatar2.jpg'},
							{name: '王五', avatar: '/static/avatars/avatar3.jpg'},
							{name: '赵六', avatar: '/static/avatars/avatar4.jpg'}
						]
					},
					{
						id: 2,
						title: '新型材料性能测试',
						category: '材料科学',
						description: '研究新型复合材料的力学性能和耐久性，为工程应用提供数据支撑。',
						status: 'completed',
						progress: 100,
						deadline: '2025-03-15',
						award: '省级一等奖',
						tags: ['材料测试', '数据分析', '实验设计'],
						team: [
							{name: '陈七', avatar: '/static/avatars/avatar5.jpg'},
							{name: '周八', avatar: '/static/avatars/avatar6.jpg'}
						]
					},
					{
						id: 3,
						title: '机器学习算法优化',
						category: '人工智能',
						description: '针对图像识别任务的深度学习算法优化研究，提升识别准确率和处理速度。',
						status: 'active',
						progress: 45,
						deadline: '2024-08-20',
						award: null,
						tags: ['机器学习', 'Python', '图像处理'],
						team: [
							{name: '吴九', avatar: '/static/avatars/avatar7.jpg'},
							{name: '郑十', avatar: '/static/avatars/avatar8.jpg'},
							{name: '孙一', avatar: '/static/avatars/avatar9.jpg'}
						]
					}
				]
			}
		},
		computed: {
			filteredProjects() {
				let result = this.projects
				
				// 状态筛选
				if (this.currentFilter !== 'all') {
					if (this.currentFilter === 'my') {
						// 这里应该根据用户ID筛选
						result = result.filter(project => true) // 模拟筛选我的项目
					} else {
						result = result.filter(project => project.status === this.currentFilter)
					}
				}
				
				// 搜索筛选
				if (this.searchKeyword) {
					result = result.filter(project => 
						project.title.includes(this.searchKeyword) || 
						project.description.includes(this.searchKeyword) ||
						project.category.includes(this.searchKeyword) ||
						project.tags.some(tag => tag.includes(this.searchKeyword))
					)
				}
				
				return result
			}
		},
		methods: {
			onSearch() {
				// 搜索逻辑已在computed中处理
			},
			navigateTo(url) {
				uni.navigateTo({ url })
			},

			getStatusText(status) {
				const statusMap = {
					active: '进行中',
					completed: '已完成',
					paused: '已暂停'
				}
				return statusMap[status] || '未知'
			},

			loadProjectsFromStorage() {
				// 获取所有本地存储的项目
				const storageKeys = uni.getStorageInfoSync().keys
				const projectKeys = storageKeys.filter(key => key.startsWith('project_') && key !== 'project_draft')
				
				const storedProjects = []
				projectKeys.forEach(key => {
					const projectData = uni.getStorageSync(key)
					if (projectData) {
						// 转换数据格式以匹配现有结构
						const formattedProject = {
							id: projectData.id,
							title: projectData.title,
							category: projectData.category,
							description: projectData.description,
							status: projectData.status || 'active',
							progress: projectData.progress || 0,
							deadline: projectData.endDate,
							award: null,
							tags: projectData.tags || [],
							team: projectData.team || [
								{name: '项目创建者', avatar: '/static/avatars/avatar1.jpg'}
							]
						}
						storedProjects.push(formattedProject)
					}
				})
				
				// 将新创建的项目添加到现有项目列表前面
				this.projects = [...storedProjects, ...this.projects]
				
				// 更新统计数据
				this.updateProjectStats()
			},

			updateProjectStats() {
				const total = this.projects.length
				const active = this.projects.filter(p => p.status === 'active').length
				const completed = this.projects.filter(p => p.status === 'completed').length
				const awards = this.projects.filter(p => p.award).length
				
				this.projectStats = {
					total,
					active,
					completed,
					awards
				}
			}
		},

		onLoad() {
			// 页面加载时获取项目数据
			this.loadProjectsFromStorage()
		},

		onShow() {
			// 页面显示时重新加载项目数据（处理从创建页面返回的情况）
			this.loadProjectsFromStorage()
		}
	}
</script>

<style scoped>
	.container {
		padding: 20rpx;
		background: #e3f2fd;
		min-height: 100vh;
		padding-bottom: 120rpx;
	}

	.project-stats {
		display: grid;
		grid-template-columns: repeat(4, 1fr);
		gap: 15rpx;
		margin-bottom: 30rpx;
	}

	.stat-item {
		padding: 25rpx;
		background: white;
		border-radius: 15rpx;
		text-align: center;
		display: flex;
		flex-direction: column;
		gap: 8rpx;
	}

	.stat-number {
		font-size: 36rpx;
		font-weight: bold;
		color: #2979FF;
	}

	.stat-label {
		font-size: 22rpx;
		color: #666;
	}



	.project-filter {
		background: white;
		border-radius: 20rpx;
		padding: 30rpx;
		margin-bottom: 30rpx;
	}

	.filter-tabs {
		display: flex;
		background: #f0f0f0;
		border-radius: 25rpx;
		padding: 6rpx;
		margin-bottom: 20rpx;
	}

	.filter-tab {
		flex: 1;
		padding: 15rpx;
		text-align: center;
		font-size: 26rpx;
		border-radius: 20rpx;
		color: #666;
		transition: all 0.3s;
	}

	.filter-tab.active {
		background: #2979FF;
		color: white;
	}

	.search-bar {
		position: relative;
	}

	.search-input {
		width: 100%;
		padding: 20rpx 30rpx;
		border: 2rpx solid #e0e0e0;
		border-radius: 50rpx;
		font-size: 28rpx;
	}

	.project-list {
		display: flex;
		flex-direction: column;
		gap: 20rpx;
	}

	.project-card {
		background: white;
		border-radius: 20rpx;
		padding: 30rpx;
		border-left: 8rpx solid #2979FF;
	}

	.project-header {
		display: flex;
		justify-content: space-between;
		align-items: flex-start;
		margin-bottom: 15rpx;
	}

	.project-info {
		flex: 1;
		display: flex;
		flex-direction: column;
		gap: 8rpx;
	}

	.project-title {
		font-size: 32rpx;
		font-weight: bold;
		color: #333;
	}

	.project-category {
		font-size: 24rpx;
		color: #666;
	}

	.project-status {
		padding: 8rpx 16rpx;
		border-radius: 20rpx;
		font-size: 22rpx;
	}

	.project-status.active {
		background: #E8F5E8;
		color: #4CAF50;
	}

	.project-status.completed {
		background: #E3F2FD;
		color: #2196F3;
	}

	.project-status.paused {
		background: #FFF3E0;
		color: #FF9800;
	}

	.project-description {
		font-size: 26rpx;
		color: #666;
		line-height: 1.5;
		margin-bottom: 20rpx;
		display: block;
	}

	.project-progress {
		margin-bottom: 20rpx;
	}

	.progress-info {
		display: flex;
		justify-content: space-between;
		align-items: center;
		margin-bottom: 10rpx;
	}

	.progress-label {
		font-size: 24rpx;
		color: #666;
	}

	.progress-percent {
		font-size: 24rpx;
		font-weight: bold;
		color: #2979FF;
	}

	.progress-bar {
		height: 8rpx;
		background: #e0e0e0;
		border-radius: 4rpx;
		overflow: hidden;
	}

	.progress-fill {
		height: 100%;
		background: linear-gradient(90deg, #2979FF, #42A5F5);
		transition: width 0.3s;
	}

	.project-team {
		display: flex;
		justify-content: space-between;
		align-items: center;
		margin-bottom: 20rpx;
	}

	.team-avatars {
		display: flex;
		align-items: center;
	}

	.avatar {
		width: 60rpx;
		height: 60rpx;
		border-radius: 50%;
		border: 4rpx solid white;
		margin-left: -15rpx;
		background: #f0f0f0;
	}

	.avatar:first-child {
		margin-left: 0;
	}

	.more-members {
		width: 60rpx;
		height: 60rpx;
		border-radius: 50%;
		background: #e0e0e0;
		border: 4rpx solid white;
		margin-left: -15rpx;
		display: flex;
		align-items: center;
		justify-content: center;
	}

	.more-text {
		font-size: 20rpx;
		color: #666;
	}

	.project-meta {
		display: flex;
		flex-direction: column;
		gap: 5rpx;
		align-items: flex-end;
	}

	.meta-item {
		display: flex;
		align-items: center;
		gap: 8rpx;
		font-size: 22rpx;
		color: #999;
	}

	.meta-icon {
		width: 22rpx;
		height: 22rpx;
	}

	.project-tags {
		display: flex;
		flex-wrap: wrap;
		gap: 10rpx;
	}

	.tag {
		padding: 8rpx 16rpx;
		background: #f0f0f0;
		border-radius: 20rpx;
		font-size: 22rpx;
		color: #666;
	}

	.empty-state {
		padding: 100rpx 40rpx;
		text-align: center;
		display: flex;
		flex-direction: column;
		align-items: center;
		gap: 20rpx;
	}

	.empty-icon {
		width: 120rpx;
		height: 120rpx;
		opacity: 0.3;
	}

	.empty-text {
		font-size: 32rpx;
		color: #666;
		font-weight: bold;
	}

	.empty-tip {
		font-size: 26rpx;
		color: #999;
	}

	.floating-icon {
		position: fixed;
		bottom: 120rpx;
		right: 40rpx;
		width: 100rpx;
		height: 100rpx;
		z-index: 100;
	}

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
		transform: translate3d(0, -8px, 0);
	}
	60% {
		transform: translate3d(0, -4px, 0);
	}
}

.fade-in-down {
	animation: fadeInDown 0.4s ease-out;
	will-change: transform, opacity;
}

.fade-in-up {
	animation: fadeInUp 0.4s ease-out;
	will-change: transform, opacity;
}

.scale-in {
	animation: scaleIn 0.3s ease-out;
	will-change: transform, opacity;
}

.floating {
	animation: floating 2.5s ease-in-out infinite;
	will-change: transform;
}

.icon-bounce {
	animation: iconBounce 1.8s infinite;
	will-change: transform;
}

.card-hover {
	transition: transform 0.2s ease, box-shadow 0.2s ease;
	will-change: transform;
}

.card-hover:hover {
	transform: translate3d(0, -1px, 0);
	box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.card-hover:active {
	transform: translate3d(0, 0, 0);
	box-shadow: 0 1px 3px rgba(0, 0, 0, 0.08);
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

</style>