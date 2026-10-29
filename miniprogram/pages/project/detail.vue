<template>
	<view class="container">
		<!-- 自定义导航栏 -->
		<view class="custom-navbar">
			<view class="navbar-left">
				<image class="back-icon" src="http://szzgurngv.hn-bkt.clouddn.com/icon/back.png" @click="goBack" mode="aspectFit"></image>
			</view>
			<text class="navbar-title">项目详情</text>
			<view class="navbar-right">
			</view>
		</view>

		<!-- 项目头部信息 -->
		<view class="project-header">
			<view class="project-basic">
				<view class="project-title-row">
					<text class="project-title">{{project.title}}</text>
					<view class="project-status" :class="project.status">
						<text class="status-text">{{getStatusText(project.status)}}</text>
					</view>
				</view>
				<text class="project-category">{{project.category}}</text>
				<text class="project-description">{{project.description}}</text>
			</view>

			<!-- 项目统计 -->
			<view class="project-stats">
				<view class="stat-item">
					<text class="stat-number">{{project.progress}}%</text>
					<text class="stat-label">完成度</text>
				</view>
				<view class="stat-item">
					<text class="stat-number">{{project.team.length}}</text>
					<text class="stat-label">团队成员</text>
				</view>
				<view class="stat-item">
					<text class="stat-number">{{project.tasks.length}}</text>
					<text class="stat-label">任务数</text>
				</view>
				<view class="stat-item">
					<text class="stat-number">{{getDaysLeft()}}</text>
					<text class="stat-label">剩余天数</text>
				</view>
			</view>

			<!-- 项目进度条 -->
			<view class="progress-section">
				<view class="progress-info">
					<text class="progress-label">项目进度</text>
					<text class="progress-percent">{{project.progress}}%</text>
				</view>
				<view class="progress-bar">
					<view class="progress-fill" :style="{width: project.progress + '%'}"></view>
				</view>
			</view>
		</view>

		<!-- 功能标签页 -->
		<view class="tab-bar">
			<view class="tab-item" 
				v-for="tab in tabs" 
				:key="tab.value"
				:class="{active: currentTab === tab.value}"
				@click="currentTab = tab.value">
				<text class="tab-text">{{tab.label}}</text>
			</view>
		</view>

		<!-- 标签页内容 -->
		<view class="tab-content">
			<!-- 任务管理 -->
			<view v-if="currentTab === 'tasks'" class="tasks-section">
				<view class="section-header">
					<button class="add-btn" @click="addTask">+ 添加任务</button>
				</view>

				<view class="task-list">
					<view class="task-item" 
						v-for="task in filteredTasks" 
						:key="task.id"
						@click="viewTask(task)">
						<view class="task-header">
							<view class="task-info">
								<text class="task-title">{{task.title}}</text>
								<text class="task-assignee">负责人：{{task.assignee}}</text>
							</view>
							<view class="task-status" :class="task.status">
								<text class="status-text">{{getTaskStatusText(task.status)}}</text>
							</view>
						</view>
						<text class="task-description">{{task.description}}</text>
						<view class="task-meta">
							<text class="meta-item">📅 {{task.deadline}}</text>
							<text class="meta-item">⏱️ {{task.priority}}</text>
						</view>
					</view>
				</view>
			</view>

			<!-- 团队成员 -->
			<view v-if="currentTab === 'team'" class="team-section">
				<view class="section-header">
					<button class="add-btn" @click="inviteMember">+ 邀请成员</button>
				</view>

				<view class="member-list">
					<view class="member-item" 
						v-for="member in project.team" 
						:key="member.id">
						<image class="member-avatar" :src="member.avatar" mode="aspectFill"></image>
						<view class="member-info">
							<text class="member-name">{{member.name}}</text>
							<text class="member-role">{{member.role}}</text>
							<text class="member-skills">{{member.skills.join('、')}}</text>
						</view>
						<view class="member-actions">
							<text class="action-btn" v-if="member.role !== '项目负责人'" @click="removeMember(member)">🗑️</text>
						</view>
					</view>
				</view>
			</view>

			<!-- 项目文档 -->
			<view v-if="currentTab === 'docs'" class="docs-section">
				<view class="section-header">
					<button class="add-btn" @click="uploadDoc">+ 上传文档</button>
				</view>

				<view class="doc-categories">
					<view class="category-item" 
						v-for="category in docCategories" 
						:key="category.value"
						:class="{active: currentDocCategory === category.value}"
						@click="currentDocCategory = category.value">
						<text class="category-text">{{category.label}}</text>
					</view>
				</view>

				<view class="doc-list">
					<view class="doc-item" 
						v-for="doc in filteredDocs" 
						:key="doc.id"
						@click="viewDoc(doc)">
						<view class="doc-icon">
							<text class="icon-text">{{getDocIcon(doc.type)}}</text>
						</view>
						<view class="doc-info">
							<text class="doc-name">{{doc.name}}</text>
							<text class="doc-meta">{{doc.size}} • {{doc.updateTime}} • {{doc.uploader}}</text>
						</view>
						<view class="doc-actions">
							<text class="action-btn" @click.stop="downloadDoc(doc)">⬇️</text>
							<text class="action-btn" @click.stop="shareDoc(doc)">📤</text>
						</view>
					</view>
				</view>
			</view>

			<!-- 项目动态 -->
			<view v-if="currentTab === 'activity'" class="activity-section">
				<!-- 发表动态 -->
				<view class="post-activity">
					<textarea class="activity-input" 
						v-model="newActivity" 
						placeholder="分享你的想法..." 
						maxlength="200"
						auto-height></textarea>
					<button class="post-btn" @click="postActivity" :disabled="!newActivity.trim()">发表</button>
				</view>

				<view class="activity-list">
					<view class="activity-item" 
						v-for="activity in project.activities" 
						:key="activity.id">
						<view class="activity-avatar">
							<image class="avatar" :src="activity.userAvatar" mode="aspectFill"></image>
						</view>
						<view class="activity-content">
							<view class="activity-header">
								<text class="activity-user">{{activity.userName}}</text>
								<text class="activity-time">{{activity.time}}</text>
							</view>
							<text class="activity-text">{{activity.content}}</text>
							<view class="activity-attachments" v-if="activity.attachments">
								<view class="attachment" 
									v-for="attachment in activity.attachments" 
									:key="attachment.id">
									<text class="attachment-name">{{attachment.name}}</text>
								</view>
							</view>
						</view>
					</view>
				</view>
			</view>
		</view>

		<!-- 添加任务模态框 -->
		<view class="task-modal" v-if="showTaskModal" @click="closeTaskModal">
			<view class="modal-content" @click.stop>
				<view class="modal-header">
					<text class="modal-title">添加任务</text>
					<text class="close-btn" @click="closeTaskModal">×</text>
				</view>
				
				<view class="modal-body">
					<view class="form-item">
						<text class="form-label">任务标题</text>
						<textarea class="form-textarea" v-model="taskForm.title" placeholder="请输入任务标题" auto-height maxlength="50"></textarea>
					</view>
					
					<view class="form-item">
						<text class="form-label">任务负责人</text>
						<picker class="form-picker" :range="teamMembers" range-key="name" @change="onAssigneeChange">
							<view class="picker-display">
								<text class="picker-value" :class="{placeholder: !taskForm.assignee}">{{taskForm.assignee || '请选择负责人'}}</text>
								<text class="picker-icon">▼</text>
							</view>
						</picker>
					</view>
					
					<view class="form-item">
						<text class="form-label">任务内容</text>
						<textarea class="form-textarea" v-model="taskForm.description" placeholder="请输入任务内容描述" maxlength="200"></textarea>
					</view>
					
					<view class="form-item">
						<text class="form-label">开始时间</text>
						<picker class="form-picker" mode="date" :value="taskForm.startDate" @change="onStartDateChange">
							<view class="picker-display">
								<text class="picker-value" :class="{placeholder: !taskForm.startDate}">{{taskForm.startDate || '请选择开始时间'}}</text>
								<text class="picker-icon">📅</text>
							</view>
						</picker>
					</view>
					
					<view class="form-item">
						<text class="form-label">截止时间</text>
						<picker class="form-picker" mode="date" :value="taskForm.deadline" @change="onDeadlineChange">
							<view class="picker-display">
								<text class="picker-value" :class="{placeholder: !taskForm.deadline}">{{taskForm.deadline || '请选择截止时间'}}</text>
								<text class="picker-icon">📅</text>
							</view>
						</picker>
					</view>
				</view>
				
				<view class="modal-footer">
					<button class="cancel-btn" @click="closeTaskModal">取消</button>
					<button class="confirm-btn" @click="confirmAddTask" :disabled="!isTaskFormValid">确认</button>
				</view>
			</view>
		</view>

		<!-- 底部编辑按钮 -->
		<view class="bottom-edit-section" v-if="isOwner">
			<button class="edit-project-btn" @click="editProject">编辑项目</button>
		</view>

	</view>
</template>

<script>
	export default {
		data() {
			return {
				projectId: '',
			currentTab: 'tasks',
			currentDocCategory: 'all',
			newActivity: '',
			showTaskModal: false,
			taskForm: {
				title: '',
				assignee: '',
				description: '',
				startDate: '',
				deadline: ''
			},
			tabs: [
				{value: 'tasks', label: '任务'},
				{value: 'team', label: '团队'},
				{value: 'docs', label: '文档'},
				{value: 'activity', label: '动态'}
			],
			docCategories: [
					{value: 'all', label: '全部'},
					{value: 'design', label: '设计文档'},
					{value: 'code', label: '代码文档'},
					{value: 'test', label: '测试文档'},
					{value: 'other', label: '其他'}
				],
				project: {
					id: 1,
					title: '智能实验室管理系统',
					category: '软件开发',
					description: '基于物联网技术的智能实验室环境监控与设备管理系统，实现实验室的数字化转型。通过传感器网络实时监控实验室环境参数，智能调节设备状态，提升实验效率和安全性。',
					status: 'active',
					progress: 75,
					startDate: '2024-01-15',
					endDate: '2024-06-30',
					isFavorite: false,
					team: [
						{
							id: 1,
							name: '张三',
							avatar: '/static/avatars/avatar1.jpg',
							role: '项目负责人',
							skills: ['Vue.js', '项目管理', 'IoT']
						},
						{
							id: 2,
							name: '李四',
							avatar: '/static/avatars/avatar2.jpg',
							role: '前端开发',
							skills: ['Vue.js', 'JavaScript', 'CSS']
						},
						{
							id: 3,
							name: '王五',
							avatar: '/static/avatars/avatar3.jpg',
							role: '后端开发',
							skills: ['Node.js', 'Python', '数据库']
						},
						{
							id: 4,
							name: '赵六',
							avatar: '/static/avatars/avatar4.jpg',
							role: '硬件工程师',
							skills: ['Arduino', '传感器', '电路设计']
						}
					],
					tasks: [
						{
							id: 1,
							title: '前端界面设计',
							description: '设计并实现用户管理界面，包括设备监控面板和数据可视化图表',
							assignee: '李四',
							status: 'doing',
							priority: '高',
							deadline: '2024-04-15'
						},
						{
							id: 2,
							title: '数据库设计',
							description: '设计设备数据存储结构，优化查询性能',
							assignee: '王五',
							status: 'done',
							priority: '中',
							deadline: '2024-03-30'
						},
						{
							id: 3,
							title: '传感器集成',
							description: '集成温湿度、光照、空气质量等传感器',
							assignee: '赵六',
							status: 'todo',
							priority: '高',
							deadline: '2024-05-01'
						}
					],
					docs: [
						{
							id: 1,
							name: '项目需求文档.docx',
							type: 'docx',
							category: 'design',
							size: '2.5MB',
							updateTime: '2024-03-15',
							uploader: '张三'
						},
						{
							id: 2,
							name: 'API接口文档.pdf',
							type: 'pdf',
							category: 'code',
							size: '1.8MB',
							updateTime: '2024-03-20',
							uploader: '王五'
						},
						{
							id: 3,
							name: '测试用例.xlsx',
							type: 'xlsx',
							category: 'test',
							size: '856KB',
							updateTime: '2024-03-25',
							uploader: '李四'
						}
					],
					activities: [
						{
							id: 1,
							userName: '张三',
							userAvatar: '/static/avatars/avatar1.jpg',
							content: '更新了项目进度，完成了数据库设计阶段',
							time: '2小时前'
						},
						{
							id: 2,
							userName: '李四',
							userAvatar: '/static/avatars/avatar2.jpg',
							content: '上传了新的界面设计稿',
							time: '5小时前',
							attachments: [
								{id: 1, name: '界面设计v2.0.fig'}
							]
						},
						{
							id: 3,
							userName: '王五',
							userAvatar: '/static/avatars/avatar3.jpg',
							content: '完成了API接口开发，已提交代码',
							time: '1天前'
						}
					]
				}
			}
		},
		computed: {
			filteredTasks() {
				return this.project.tasks
			},
			filteredDocs() {
				if (this.currentDocCategory === 'all') {
					return this.project.docs
				}
				return this.project.docs.filter(doc => doc.category === this.currentDocCategory)
			},
			isMember() {
				// 模拟判断当前用户是否为项目成员
				return true
			},
			isOwner() {
				// 模拟判断当前用户是否为项目负责人
				return true
			},
			teamMembers() {
				return this.project.team || []
			},
			isTaskFormValid() {
				return this.taskForm.title.trim() && 
					   this.taskForm.assignee && 
					   this.taskForm.description.trim() && 
					   this.taskForm.startDate && 
					   this.taskForm.deadline
			}
		},
		methods: {
			goBack() {
				uni.navigateBack()
			},
			getDaysLeft() {
				const today = new Date()
				const endDate = new Date(this.project.endDate)
				const diffTime = endDate - today
				const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24))
				return diffDays > 0 ? diffDays : 0
			},
			getStatusText(status) {
				const statusMap = {
					active: '进行中',
					completed: '已完成',
					paused: '已暂停'
				}
				return statusMap[status] || '未知'
			},
			getTaskStatusText(status) {
				const statusMap = {
					todo: '待办',
					doing: '进行中',
					done: '已完成'
				}
				return statusMap[status] || '未知'
			},
			getDocIcon(type) {
				const iconMap = {
					docx: '📄',
					pdf: '📕',
					xlsx: '📊',
					pptx: '📋',
					zip: '📦',
					img: '🖼️'
				}
				return iconMap[type] || '📄'
			},

			addTask() {
				this.showTaskModal = true
			},
			closeTaskModal() {
				this.showTaskModal = false
				// 重置表单
				this.taskForm = {
					title: '',
					assignee: '',
					description: '',
					startDate: '',
					deadline: ''
				}
			},
			onAssigneeChange(e) {
				const index = e.detail.value
				this.taskForm.assignee = this.teamMembers[index].name
			},
			onStartDateChange(e) {
				this.taskForm.startDate = e.detail.value
			},
			onDeadlineChange(e) {
				this.taskForm.deadline = e.detail.value
			},
			confirmAddTask() {
				if (!this.isTaskFormValid) {
					uni.showToast({
						title: '请填写完整信息',
						icon: 'none'
					})
					return
				}
				
				// 创建新任务
				const newTask = {
					id: Date.now(),
					title: this.taskForm.title.trim(),
					description: this.taskForm.description.trim(),
					assignee: this.taskForm.assignee,
					status: 'todo',
					priority: '中',
					startDate: this.taskForm.startDate,
					deadline: this.taskForm.deadline
				}
				
				// 添加到任务列表
				this.project.tasks.push(newTask)
				
				// 保存到本地存储
				uni.setStorageSync(`project_${this.projectId}`, this.project)
				
				// 显示成功提示
				uni.showToast({
					title: '任务添加成功',
					icon: 'success'
				})
				
				// 关闭模态框
				this.closeTaskModal()
			},
			viewTask(task) {
				uni.showModal({
					title: task.title,
					content: task.description,
					showCancel: false
				})
			},
			inviteMember() {
				uni.showModal({
					title: '邀请成员',
					content: '功能开发中，敬请期待',
					showCancel: false
				})
			},
			removeMember(member) {
				uni.showModal({
					title: '移除成员',
					content: `确定要移除 ${member.name} 吗？`,
					success: (res) => {
						if (res.confirm) {
							uni.showToast({
								title: '功能开发中',
								icon: 'none'
							})
						}
					}
				})
			},
			uploadDoc() {
				uni.chooseFile({
					count: 1,
					success: (res) => {
						uni.showToast({
							title: '功能开发中',
							icon: 'none'
						})
					}
				})
			},
			viewDoc(doc) {
				uni.showModal({
					title: '查看文档',
					content: `打开文档：${doc.name}`,
					showCancel: false
				})
			},
			downloadDoc(doc) {
				uni.showToast({
					title: '下载中...',
					icon: 'loading'
				})
			},
			shareDoc(doc) {
				uni.showActionSheet({
					itemList: ['微信好友', '朋友圈', '复制链接'],
					success: (res) => {
						uni.showToast({
							title: '功能开发中',
							icon: 'none'
						})
					}
				})
			},
			editProject() {
				uni.navigateTo({
					url: `/pages/project/create?id=${this.projectId}&mode=edit`
				})
			},
			postActivity() {
				if (!this.newActivity.trim()) return
				
				// 创建新动态
				const newActivityItem = {
					id: Date.now(),
					userName: '当前用户', // 实际应用中应该从用户信息获取
					userAvatar: '/static/avatars/avatar1.jpg', // 实际应用中应该从用户信息获取
					content: this.newActivity.trim(),
					time: '刚刚'
				}
				
				// 添加到动态列表开头
				this.project.activities.unshift(newActivityItem)
				
				// 清空输入框
				this.newActivity = ''
				
				// 显示成功提示
				uni.showToast({
					title: '发表成功',
					icon: 'success'
				})
				
				// 保存到本地存储
				uni.setStorageSync(`project_${this.projectId}`, this.project)
			},

			loadProjectData() {
			// 尝试从本地存储加载项目数据
			const storedProject = uni.getStorageSync(`project_${this.projectId}`)
			if (storedProject) {
				// 使用存储的项目数据，并补充缺失的字段
				this.project = {
					...this.project, // 保留默认结构
					...storedProject, // 覆盖存储的数据
					isFavorite: false,
					// 如果存储数据中没有team，使用默认team
					team: storedProject.team || [
						{
							id: 1,
							name: '项目创建者',
							role: '项目负责人',
							avatar: '/static/avatars/avatar1.jpg',
							skills: ['项目管理', '技术架构']
						}
					],
					// 如果存储数据中没有tasks，使用空数组
					tasks: storedProject.tasks || [],
					// 如果存储数据中没有docs，使用空数组
					docs: storedProject.docs || [],
					// 如果存储数据中没有activities，创建默认活动
					activities: storedProject.activities || [
						{
							id: 1,
							userName: '项目创建者',
							userAvatar: '/static/avatars/avatar1.jpg',
							content: `创建了项目「${storedProject.title}」`,
							time: this.formatTime(storedProject.createTime)
						}
					]
				}
				console.log('加载新创建的项目数据:', this.project)
			} else {
				// 如果没有找到存储的数据，使用默认的模拟数据
				console.log('使用默认项目数据:', this.projectId)
			}
		},
		formatTime(timeString) {
			if (!timeString) return '刚刚'
			const now = new Date()
			const time = new Date(timeString)
			const diff = now - time
			const minutes = Math.floor(diff / (1000 * 60))
			const hours = Math.floor(diff / (1000 * 60 * 60))
			const days = Math.floor(diff / (1000 * 60 * 60 * 24))
			
			if (minutes < 1) return '刚刚'
			if (minutes < 60) return `${minutes}分钟前`
			if (hours < 24) return `${hours}小时前`
			if (days < 7) return `${days}天前`
			return time.toLocaleDateString()
		}
	},
	onLoad(options) {
		this.projectId = options.id || '1'
		// 根据ID加载项目数据
		this.loadProjectData()
	},
	onShow() {
		// 页面显示时重新加载项目数据，确保编辑后能及时更新
		if (this.projectId) {
			this.loadProjectData()
		}
	}
}
</script>

<style scoped>
	.container {
		padding: 0;
		background: #e3f2fd;
		min-height: 100vh;
		padding-bottom: 20rpx;
	}

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

	.project-header {
		margin: 20rpx;
		background: white;
		border-radius: 20rpx;
		padding: 30rpx;
	}

	.project-basic {
		margin-bottom: 30rpx;
	}

	.project-title-row {
		display: flex;
		justify-content: space-between;
		align-items: flex-start;
		margin-bottom: 15rpx;
	}

	.project-title {
		flex: 1;
		font-size: 36rpx;
		font-weight: bold;
		color: #333;
		margin-right: 20rpx;
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

	.project-category {
		font-size: 26rpx;
		color: #666;
		margin-bottom: 15rpx;
		display: block;
	}

	.project-description {
		font-size: 28rpx;
		color: #333;
		line-height: 1.6;
		display: block;
	}

	.project-stats {
		display: grid;
		grid-template-columns: repeat(4, 1fr);
		gap: 15rpx;
		margin-bottom: 30rpx;
	}

	.stat-item {
		padding: 20rpx;
		background: #f8f9fa;
		border-radius: 15rpx;
		text-align: center;
		display: flex;
		flex-direction: column;
		gap: 8rpx;
	}

	.stat-number {
		font-size: 32rpx;
		font-weight: bold;
		color: #2979FF;
	}

	.stat-label {
		font-size: 22rpx;
		color: #666;
	}

	.progress-section {
		padding: 20rpx;
		background: #f8f9fa;
		border-radius: 15rpx;
	}

	.progress-info {
		display: flex;
		justify-content: space-between;
		align-items: center;
		margin-bottom: 15rpx;
	}

	.progress-label {
		font-size: 26rpx;
		color: #333;
	}

	.progress-percent {
		font-size: 26rpx;
		font-weight: bold;
		color: #2979FF;
	}

	.progress-bar {
		height: 12rpx;
		background: #e0e0e0;
		border-radius: 6rpx;
		overflow: hidden;
	}

	.progress-fill {
		height: 100%;
		background: linear-gradient(90deg, #2979FF, #42A5F5);
		transition: width 0.3s;
	}

	.tab-bar {
		display: flex;
		background: white;
		margin: 0 20rpx;
		border-radius: 20rpx;
		padding: 10rpx;
	}

	.tab-item {
		flex: 1;
		padding: 20rpx;
		text-align: center;
		border-radius: 15rpx;
		transition: all 0.3s;
	}

	.tab-item.active {
		background: #2979FF;
	}

	.tab-text {
		font-size: 28rpx;
		color: #666;
	}

	.tab-item.active .tab-text {
		color: white;
		font-weight: bold;
	}

	.tab-content {
		margin: 20rpx;
	}

	.section-header {
		display: flex;
		justify-content: space-between;
		align-items: center;
		margin-bottom: 20rpx;
	}

	.section-title {
		font-size: 32rpx;
		font-weight: bold;
		color: #333;
	}

	.add-btn {
		padding: 15rpx 25rpx;
		background: #2979FF;
		color: white;
		border: none;
		border-radius: 20rpx;
		font-size: 24rpx;
	}

	.task-filters {
		display: flex;
		gap: 15rpx;
		margin-bottom: 20rpx;
	}

	.doc-categories {
		display: flex;
		gap: 20rpx;
		margin-bottom: 30rpx;
		justify-content: space-around;
		padding: 0 10rpx;
	}

	.filter-item {
		padding: 15rpx 25rpx;
		background: #f0f0f0;
		border-radius: 20rpx;
		transition: all 0.3s;
	}

	.category-item {
		flex: 1;
		padding: 15rpx 8rpx;
		background: #f0f0f0;
		border-radius: 20rpx;
		transition: all 0.3s;
		text-align: center;
		margin: 0 5rpx;
	}

	.filter-item.active, .category-item.active {
		background: #2979FF;
	}

	.filter-text, .category-text {
		font-size: 24rpx;
		color: #666;
	}

	.filter-item.active .filter-text,
	.category-item.active .category-text {
		color: white;
	}

	.task-list, .member-list, .doc-list, .activity-list {
		display: flex;
		flex-direction: column;
		gap: 15rpx;
	}

	.task-item, .member-item, .doc-item, .activity-item {
		background: white;
		border-radius: 15rpx;
		padding: 25rpx;
	}

	.task-header {
		display: flex;
		justify-content: space-between;
		align-items: flex-start;
		margin-bottom: 15rpx;
	}

	.task-info {
		flex: 1;
		display: flex;
		flex-direction: column;
		gap: 8rpx;
	}

	.task-title {
		font-size: 28rpx;
		font-weight: bold;
		color: #333;
	}

	.task-assignee {
		font-size: 24rpx;
		color: #666;
	}

	.task-status {
		padding: 6rpx 12rpx;
		border-radius: 15rpx;
		font-size: 20rpx;
	}

	.task-status.todo {
		background: #FFF3E0;
		color: #FF9800;
	}

	.task-status.doing {
		background: #E3F2FD;
		color: #2196F3;
	}

	.task-status.done {
		background: #E8F5E8;
		color: #4CAF50;
	}

	.task-description {
		font-size: 26rpx;
		color: #666;
		line-height: 1.5;
		margin-bottom: 15rpx;
		display: block;
	}

	.task-meta {
		display: flex;
		gap: 20rpx;
	}

	.meta-item {
		font-size: 22rpx;
		color: #999;
	}

	.member-item {
		display: flex;
		align-items: center;
		gap: 20rpx;
	}

	.member-avatar {
		width: 80rpx;
		height: 80rpx;
		border-radius: 50%;
		background: #f0f0f0;
	}

	.member-info {
		flex: 1;
		display: flex;
		flex-direction: column;
		gap: 8rpx;
	}

	.member-name {
		font-size: 28rpx;
		font-weight: bold;
		color: #333;
	}

	.member-role {
		font-size: 24rpx;
		color: #2979FF;
	}

	.member-skills {
		font-size: 22rpx;
		color: #666;
	}

	.member-actions {
		display: flex;
		gap: 15rpx;
	}

	.action-btn {
		width: 60rpx;
		height: 60rpx;
		display: flex;
		align-items: center;
		justify-content: center;
		background: #f0f0f0;
		border-radius: 50%;
		font-size: 32rpx;
	}

	.doc-item {
		display: flex;
		align-items: center;
		gap: 20rpx;
	}

	.doc-icon {
		width: 80rpx;
		height: 80rpx;
		display: flex;
		align-items: center;
		justify-content: center;
		background: #f0f0f0;
		border-radius: 15rpx;
	}

	.icon-text {
		font-size: 40rpx;
	}

	.doc-info {
		flex: 1;
		display: flex;
		flex-direction: column;
		gap: 8rpx;
	}

	.doc-name {
		font-size: 28rpx;
		font-weight: bold;
		color: #333;
	}

	.doc-meta {
		font-size: 22rpx;
		color: #666;
	}

	.doc-actions {
		display: flex;
		gap: 15rpx;
	}

	.activity-item {
		display: flex;
		gap: 20rpx;
	}

	.activity-avatar {
		width: 80rpx;
		height: 80rpx;
	}

	.avatar {
		width: 100%;
		height: 100%;
		border-radius: 50%;
		background: #f0f0f0;
	}

	.activity-content {
		flex: 1;
		display: flex;
		flex-direction: column;
		gap: 10rpx;
	}

	.activity-header {
		display: flex;
		justify-content: space-between;
		align-items: center;
	}

	.activity-user {
		font-size: 26rpx;
		font-weight: bold;
		color: #333;
	}

	.activity-time {
		font-size: 22rpx;
		color: #999;
	}

	.activity-text {
		font-size: 26rpx;
		color: #333;
		line-height: 1.5;
	}

	.activity-attachments {
		margin-top: 15rpx;
		display: flex;
		flex-wrap: wrap;
		gap: 10rpx;
	}

	.attachment {
		padding: 10rpx 15rpx;
		background: #f0f0f0;
		border-radius: 10rpx;
	}

	.attachment-name {
		font-size: 22rpx;
		color: #666;
	}

	.bottom-edit-section {
		padding: 30rpx 20rpx;
		background: white;
		margin: 20rpx;
		border-radius: 15rpx;
		text-align: center;
	}

	.edit-project-btn {
		width: 100%;
		padding: 25rpx;
		background: #2979FF;
		color: white;
		border: none;
		border-radius: 15rpx;
		font-size: 28rpx;
		font-weight: bold;
	}

	.post-activity {
		padding: 20rpx;
		background: white;
		border-radius: 15rpx;
		margin-bottom: 20rpx;
		display: flex;
		flex-direction: column;
		gap: 15rpx;
	}

	.activity-input {
		min-height: 120rpx;
		padding: 20rpx 20rpx;
		border: 2rpx solid #e0e0e0;
		border-radius: 10rpx;
		font-size: 26rpx;
		line-height: 1.5;
		resize: none;
		background: #fafafa;
		box-sizing: border-box;
		width: 100%;
	}

	.post-btn {
		align-self: flex-end;
		padding: 15rpx 30rpx;
		background: #2979FF;
		color: white;
		border: none;
		border-radius: 25rpx;
		font-size: 26rpx;
		font-weight: bold;
	}

	.post-btn:disabled {
		background: #ccc;
		color: #999;
	}

	/* 任务模态框样式 */
	.task-modal {
		position: fixed;
		top: 0;
		left: 0;
		width: 100%;
		height: 100%;
		background: rgba(0, 0, 0, 0.5);
		display: flex;
		align-items: center;
		justify-content: center;
		z-index: 1000;
	}

	.modal-content {
		width: 90%;
		max-width: 600rpx;
		background: white;
		border-radius: 20rpx;
		overflow: hidden;
		box-shadow: 0 10rpx 30rpx rgba(0, 0, 0, 0.3);
	}

	.modal-header {
		display: flex;
		justify-content: space-between;
		align-items: center;
		padding: 30rpx;
		border-bottom: 1rpx solid #e3f2fd;
		background: #e3f2fd;
	}

	.modal-title {
		font-size: 32rpx;
		font-weight: bold;
		color: #1976D2;
	}

	.close-btn {
		font-size: 40rpx;
		color: #1976D2;
		width: 50rpx;
		height: 50rpx;
		display: flex;
		align-items: center;
		justify-content: center;
		border-radius: 50%;
		transition: all 0.2s;
	}

	.close-btn:active {
		background: #bbdefb;
	}

	.modal-body {
		padding: 30rpx;
		max-height: 60vh;
		overflow-y: auto;
	}

	.form-item {
		margin-bottom: 30rpx;
	}

	.form-item:last-child {
		margin-bottom: 0;
	}

	.form-label {
		display: block;
		font-size: 28rpx;
		font-weight: 500;
		color: #333;
		margin-bottom: 15rpx;
	}

	.form-input {
		width: 100%;
		padding: 25rpx 20rpx;
		border: 2rpx solid #e0e0e0;
		border-radius: 10rpx;
		font-size: 28rpx;
		color: #333;
		background: #fafafa;
		box-sizing: border-box;
		transition: all 0.2s;
	}

	.form-input:focus {
		border-color: #2196F3;
		background: white;
		outline: none;
	}

	.form-textarea {
		width: 100%;
		min-height: 120rpx;
		padding: 25rpx 20rpx;
		border: 2rpx solid #e0e0e0;
		border-radius: 10rpx;
		font-size: 28rpx;
		color: #333;
		background: #fafafa;
		box-sizing: border-box;
		resize: none;
		line-height: 1.5;
		transition: all 0.2s;
	}

	.form-textarea:focus {
		border-color: #2196F3;
		background: white;
		outline: none;
	}

	.form-picker {
		width: 100%;
	}

	.picker-display {
		display: flex;
		align-items: center;
		justify-content: space-between;
		padding: 25rpx 20rpx;
		border: 2rpx solid #e0e0e0;
		border-radius: 10rpx;
		background: #fafafa;
		box-sizing: border-box;
		transition: all 0.2s;
	}

	.picker-display:active {
		border-color: #2196F3;
		background: white;
	}

	.picker-value {
		font-size: 28rpx;
		color: #333;
		flex: 1;
	}

	.picker-value.placeholder {
		color: #999;
	}

	.picker-icon {
		font-size: 24rpx;
		color: #666;
	}

	.modal-footer {
		display: flex;
		gap: 20rpx;
		padding: 30rpx;
		border-top: 1rpx solid #e3f2fd;
		background: #e3f2fd;
	}

	.cancel-btn {
		flex: 1;
		padding: 25rpx;
		background: #ccc;
		color: #999;
		border: none;
		border-radius: 10rpx;
		font-size: 28rpx;
		font-weight: bold;
		transition: all 0.2s;
	}

	.cancel-btn:active {
		background: #64B5F6;
	}

	.confirm-btn {
		flex: 1;
		padding: 25rpx;
		background: #2196F3;
		color: white;
		border: none;
		border-radius: 10rpx;
		font-size: 28rpx;
		font-weight: bold;
		transition: all 0.2s;
	}

	.confirm-btn:disabled {
		background: #ccc;
		color: #999;
	}

	.confirm-btn:active:not(:disabled) {
		background: #1976D2;
	}

</style>