<template>
	<view class="memo-container">
		<!-- 自定义导航栏 -->
		<view class="custom-navbar">
			<view class="navbar-left">
				<image class="back-icon" src="http://szzgurngv.hn-bkt.clouddn.com/icon/back.png" @click="goBack" mode="aspectFit"></image>
			</view>
			<text class="memo-title">{{editMode ? '编辑项目' : '新建项目'}}</text>
			<view class="navbar-right">
			</view>
		</view>

		<!-- 备忘录内容区 -->
		<view class="memo-content">
			<!-- 基本信息 -->
			<view class="memo-section">
				<view class="section-header">
					<text class="section-title">📝 基本信息</text>
				</view>
				
				<view class="input-group">
					<view class="input-item">
					<text class="input-label">项目名称</text>
					<textarea class="memo-textarea" 
						v-model="projectForm.title" 
						placeholder="输入项目名称"
						maxlength="50"
						auto-height></textarea>
				</view>
				
				<view class="input-item">
					<text class="input-label">项目类别</text>
					<textarea class="memo-textarea" 
						v-model="projectForm.category" 
						placeholder="输入项目类别"
						maxlength="20"
						auto-height></textarea>
				</view>
					
					<view class="input-item">
						<text class="input-label">项目描述</text>
						<textarea class="memo-textarea" 
							v-model="projectForm.description" 
							placeholder="详细描述项目内容和目标"
							maxlength="500"
							auto-height></textarea>
						<text class="char-counter">{{projectForm.description.length}}/500</text>
					</view>
				</view>
			</view>

			<!-- 时间安排 -->
			<view class="memo-section">
				<view class="section-header">
					<text class="section-title">📅 时间安排</text>
				</view>
				
				<view class="input-group">
					<view class="input-item">
						<text class="input-label">开始时间</text>
						<picker class="memo-picker" 
							mode="date" 
							:value="projectForm.startDate" 
							@change="onStartDateChange">
							<view class="picker-display">
								<text class="picker-value" :class="{placeholder: !projectForm.startDate}">
									{{projectForm.startDate || '选择开始日期'}}
								</text>
								<text class="picker-icon">📅</text>
							</view>
						</picker>
					</view>
					
					<view class="input-item">
						<text class="input-label">结束时间</text>
						<picker class="memo-picker" 
							mode="date" 
							:value="projectForm.endDate" 
							@change="onEndDateChange">
							<view class="picker-display">
								<text class="picker-value" :class="{placeholder: !projectForm.endDate}">
									{{projectForm.endDate || '选择结束日期'}}
								</text>
								<text class="picker-icon">📅</text>
							</view>
						</picker>
					</view>
				</view>
			</view>

			<!-- 团队规模 -->
			<view class="memo-section">
				<view class="section-header">
					<text class="section-title">👥 团队规模</text>
				</view>
				
				<view class="input-group">
					<view class="size-selector">
						<view class="size-option" 
							v-for="size in teamSizes" 
							:key="size.value"
							:class="{selected: projectForm.teamSize === size.value}"
							@click="projectForm.teamSize = size.value">
							<text class="size-label">{{size.label}}</text>
						</view>
					</view>
				</view>
			</view>

			<!-- 项目标签 -->
			<view class="memo-section">
				<view class="section-header">
					<text class="section-title">🏷️ 项目标签</text>
				</view>
				
				<view class="input-group">
					<view class="input-item" v-if="projectForm.tags.length > 0">
						<text class="input-label">已选标签</text>
						<view class="selected-tags">
							<view class="tag-chip" 
								v-for="(tag, index) in projectForm.tags" 
								:key="index">
								<text class="tag-name">{{tag}}</text>
								<text class="tag-remove" @click="removeTag(index)">×</text>
							</view>
						</view>
					</view>
					
					<view class="input-item">
						<text class="input-label">添加标签</text>
						<view class="tag-input-row">
							<textarea class="memo-textarea tag-input" 
								v-model="customTag" 
								placeholder="输入标签名称"
								maxlength="10"
								auto-height
								@confirm="addCustomTag" />
							<button class="add-btn" @click="addCustomTag">+</button>
						</view>
					</view>
					
					<view class="input-item">
						<text class="input-label">推荐标签</text>
						<view class="recommend-tags">
							<view class="recommend-tag" 
								v-for="tag in availableTags" 
								:key="tag"
								:class="{disabled: projectForm.tags.includes(tag)}"
								@click="addRecommendedTag(tag)">
								<text class="recommend-name">{{tag}}</text>
							</view>
						</view>
					</view>
				</view>
			</view>

			<!-- 项目设置 -->
			<view class="memo-section">
				<view class="section-header">
					<text class="section-title">⚙️ 项目设置</text>
				</view>
				
				<view class="input-group">
					<view class="setting-item">
						<view class="setting-content">
							<text class="setting-name">公开项目</text>
							<text class="setting-desc">其他用户可以查看项目信息</text>
						</view>
						<switch class="setting-switch" :checked="projectForm.isPublic" @change="onPublicChange" />
					</view>
					
					<view class="setting-item">
						<view class="setting-content">
							<text class="setting-name">允许申请加入</text>
							<text class="setting-desc">其他用户可以申请加入项目</text>
						</view>
						<switch class="setting-switch" :checked="projectForm.allowJoin" @change="onJoinChange" />
					</view>
				</view>
			</view>
		</view>

		<!-- 操作按钮 -->
		<view class="memo-actions">
			<button class="action-btn draft-btn" @click="saveDraft" v-if="!editMode">保存草稿</button>
			<button class="action-btn create-btn" @click="createProject">{{editMode ? '保存修改' : '创建项目'}}</button>
		</view>
	</view>
</template>

<script>
	export default {
		data() {
				return {
					editMode: false,
					editProjectId: '',
					projectForm: {
						title: '',
						category: '',
						description: '',
						startDate: '',
						endDate: '',
						teamSize: 'small',
						tags: [],
						isPublic: true,
						allowJoin: false
					},
					customTag: '',
				teamSizes: [
					{value: 'small', label: '小型(2-5人)'},
					{value: 'medium', label: '中型(6-10人)'},
					{value: 'large', label: '大型(10+人)'}
				],

				availableTags: [
					'创新', '实用', '开源', '商业化', '学术研究',
					'竞赛项目', '毕业设计', '课程作业', '兴趣项目',
					'前沿技术', '跨学科', '国际合作'
				]
			}
		},
		methods: {
			goBack() {
				uni.navigateBack()
			},

			onStartDateChange(e) {
				this.projectForm.startDate = e.detail.value
			},
			onEndDateChange(e) {
				this.projectForm.endDate = e.detail.value
			},

			removeTag(index) {
				this.projectForm.tags.splice(index, 1)
			},
			addRecommendedTag(tag) {
				if (this.projectForm.tags.length >= 5) {
					uni.showToast({
						title: '最多选择5个标签',
						icon: 'none'
					})
					return
				}
				
				if (this.projectForm.tags.includes(tag)) {
					uni.showToast({
						title: '标签已存在',
						icon: 'none'
					})
					return
				}
				
				this.projectForm.tags.push(tag)
			},
			addCustomTag() {
				if (!this.customTag.trim()) return
				
				if (this.projectForm.tags.length >= 5) {
					uni.showToast({
						title: '最多选择5个标签',
						icon: 'none'
					})
					return
				}
				
				if (this.projectForm.tags.includes(this.customTag)) {
					uni.showToast({
						title: '标签已存在',
						icon: 'none'
					})
					return
				}
				
				this.projectForm.tags.push(this.customTag.trim())
				this.customTag = ''
			},
			onPublicChange(e) {
				this.projectForm.isPublic = e.detail.value
			},
			onJoinChange(e) {
				this.projectForm.allowJoin = e.detail.value
			},
			validateForm() {
				if (!this.projectForm.title.trim()) {
					uni.showToast({
						title: '请输入项目名称',
						icon: 'none'
					})
					return false
				}
				
				if (!this.projectForm.category.trim()) {
					uni.showToast({
						title: '请输入项目类别',
						icon: 'none'
					})
					return false
				}
				
				if (!this.projectForm.description.trim()) {
					uni.showToast({
						title: '请输入项目描述',
						icon: 'none'
					})
					return false
				}
				
				if (!this.projectForm.startDate) {
					uni.showToast({
						title: '请选择开始日期',
						icon: 'none'
					})
					return false
				}
				
				if (!this.projectForm.endDate) {
					uni.showToast({
						title: '请选择截止日期',
						icon: 'none'
					})
					return false
				}
				
				if (new Date(this.projectForm.startDate) >= new Date(this.projectForm.endDate)) {
					uni.showToast({
						title: '截止日期必须晚于开始日期',
						icon: 'none'
					})
					return false
				}
				
				return true
			},
			saveDraft() {
				// 保存草稿到本地存储
				uni.setStorageSync('project_draft', this.projectForm)
				uni.showToast({
					title: '草稿已保存',
					icon: 'success'
				})
				// 保存后自动退出
				setTimeout(() => {
					uni.navigateBack()
				}, 1500)
			},
			createProject() {
				if (!this.validateForm()) return
				
				uni.showLoading({
					title: this.editMode ? '保存中...' : '创建中...'
				})
				
				// 模拟API调用
				setTimeout(() => {
					uni.hideLoading()
					
					if (this.editMode) {
						// 编辑模式：更新现有项目
						const existingProject = uni.getStorageSync(`project_${this.editProjectId}`)
						const updatedProject = {
							...existingProject,
							title: this.projectForm.title,
							category: this.projectForm.category,
							description: this.projectForm.description,
							startDate: this.projectForm.startDate,
							endDate: this.projectForm.endDate,
							teamSize: this.projectForm.teamSize,
							tags: this.projectForm.tags,
							isPublic: this.projectForm.isPublic,
							allowJoin: this.projectForm.allowJoin,
							updateTime: new Date().toISOString()
						}
						
						// 保存更新的项目数据
						uni.setStorageSync(`project_${this.editProjectId}`, updatedProject)
						
						uni.showToast({
							title: '保存成功',
							icon: 'success'
						})
						
						// 返回项目详情页
						setTimeout(() => {
							uni.navigateBack()
						}, 1500)
					} else {
						// 新建模式：创建新项目
						const newProjectId = Date.now().toString()
						
						// 构建项目数据
						const projectData = {
							id: newProjectId,
							title: this.projectForm.title,
							category: this.projectForm.category,
							description: this.projectForm.description,
							startDate: this.projectForm.startDate,
							endDate: this.projectForm.endDate,
							teamSize: this.projectForm.teamSize,
							tags: this.projectForm.tags,
							isPublic: this.projectForm.isPublic,
							allowJoin: this.projectForm.allowJoin,
							status: 'active',
							progress: 0,
							createTime: new Date().toISOString()
						}
						
						// 保存项目数据到本地存储
						uni.setStorageSync(`project_${newProjectId}`, projectData)
						
						// 清除草稿
						uni.removeStorageSync('project_draft')
						
						uni.showModal({
							title: '创建成功',
							content: '项目创建成功！是否立即查看项目详情？',
							success: (res) => {
								if (res.confirm) {
									// 跳转到项目详情页，传递新项目ID
									uni.redirectTo({
										url: `/pages/project/detail?id=${newProjectId}`
									})
								} else {
									// 返回项目列表
									uni.navigateBack()
								}
							}
						})
					}
				}, 2000)
			}
		},
		onLoad(options) {
				// 检查是否为编辑模式
				if (options.mode === 'edit' && options.id) {
					// 编辑模式：加载现有项目数据
					const projectData = uni.getStorageSync(`project_${options.id}`)
					if (projectData) {
						this.projectForm = {
							title: projectData.title || '',
							category: projectData.category || '',
							description: projectData.description || '',
							startDate: projectData.startDate || '',
							endDate: projectData.endDate || '',
							teamSize: projectData.teamSize || 'small',
							tags: projectData.tags || [],
							isPublic: projectData.isPublic !== undefined ? projectData.isPublic : true,
							allowJoin: projectData.allowJoin !== undefined ? projectData.allowJoin : false
						}
						this.editMode = true
						this.editProjectId = options.id
					}
				} else {
					// 新建模式：尝试加载草稿
					const draft = uni.getStorageSync('project_draft')
					if (draft) {
						uni.showModal({
							title: '发现草稿',
							content: '检测到未完成的项目草稿，是否继续编辑？',
							success: (res) => {
								if (res.confirm) {
									this.projectForm = draft
								}
							}
						})
					}
				}
			}
	}
</script>

<style scoped>
	/* 备忘录容器 */
	.memo-container {
		min-height: 100vh;
		background: #e3f2fd;
		padding: 0;
		margin: 0;
	}

	/* 备忘录头部 */
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
	
		.memo-title {
			margin-top: 20rpx;
			font-size: 36rpx;
			font-weight: bold;
			color: white;
		}

	/* 备忘录内容区 */
	.memo-content {
		padding: 40rpx;
		max-width: 100%;
		margin: 0 auto;
	}

	/* 备忘录区块 */
	.memo-section {
		background: rgba(255, 255, 255, 0.9);
		backdrop-filter: blur(10px);
		border-radius: 20rpx;
		margin-bottom: 32rpx;
		box-shadow: 0 4rpx 20rpx rgba(0,0,0,0.1);
		border: 1rpx solid rgba(255, 255, 255, 0.2);
		overflow: hidden;
	}

	.section-header {
		padding: 32rpx 32rpx 24rpx;
		border-bottom: 1rpx solid rgba(241, 243, 244, 0.5);
		background: rgba(250, 251, 252, 0.5);
	}

	.section-title {
		font-size: 30rpx;
		font-weight: 600;
		color: #343a40;
		display: flex;
		align-items: center;
		gap: 12rpx;
	}

	/* 输入组 */
	.input-group {
		padding: 32rpx;
	}

	.input-item {
		margin-bottom: 32rpx;
	}

	.input-item:last-child {
		margin-bottom: 0;
	}

	.input-label {
		display: block;
		font-size: 28rpx;
		font-weight: 500;
		color: #495057;
		margin-bottom: 16rpx;
		line-height: 1.4;
	}

	/* 备忘录输入框 */
	.memo-input {
		width: 100%;
		padding: 32rpx 24rpx;
		height: 100rpx;
		border: 2rpx solid #dee2e6;
		border-radius: 12rpx;
		font-size: 30rpx;
		color: #495057;
		background: rgba(255, 255, 255, 0.8);
		box-sizing: border-box;
		transition: all 0.2s;
		line-height: 1.5;
	}

	.memo-input:focus {
		border-color: #667eea;
		outline: none;
		box-shadow: 0 0 0 4rpx rgba(102, 126, 234, 0.2);
		background: rgba(255, 255, 255, 0.95);
	}

	.memo-input::placeholder {
		color: #adb5bd;
	}

	/* 备忘录文本域 */
	.memo-textarea {
		width: 100%;
		min-height: 200rpx;
		padding: 32rpx 24rpx;
		border: 2rpx solid #dee2e6;
		border-radius: 12rpx;
		font-size: 30rpx;
		color: #495057;
		background: rgba(255, 255, 255, 0.8);
		box-sizing: border-box;
		transition: all 0.2s;
		line-height: 1.6;
		resize: none;
	}

	.memo-textarea:focus {
		border-color: #667eea;
		outline: none;
		box-shadow: 0 0 0 4rpx rgba(102, 126, 234, 0.2);
		background: rgba(255, 255, 255, 0.95);
	}

	.memo-textarea::placeholder {
		color: #adb5bd;
	}

	.char-counter {
		display: block;
		text-align: right;
		font-size: 24rpx;
		color: #6c757d;
		margin-top: 12rpx;
	}

	/* 选择器 */
	.memo-picker {
		width: 100%;
	}

	.picker-display {
		display: flex;
		align-items: center;
		justify-content: space-between;
		padding: 32rpx 24rpx;
		height: 100rpx;
		border: 2rpx solid #dee2e6;
		border-radius: 12rpx;
		background: rgba(255, 255, 255, 0.8);
		box-sizing: border-box;
		transition: all 0.2s;
	}

	.picker-display:active {
		border-color: #667eea;
		box-shadow: 0 0 0 4rpx rgba(102, 126, 234, 0.2);
		background: rgba(255, 255, 255, 0.95);
	}

	.picker-value {
		font-size: 30rpx;
		color: #495057;
		flex: 1;
	}

	.picker-value.placeholder {
		color: #adb5bd;
	}

	.picker-icon {
		font-size: 28rpx;
		color: #6c757d;
	}

	/* 团队规模选择器 */
	.size-selector {
		display: flex;
		gap: 16rpx;
		flex-wrap: wrap;
	}

	.size-option {
		flex: 1;
		min-width: 200rpx;
		padding: 32rpx 24rpx;
		height: 100rpx;
		border: 2rpx solid #2196F3;
		border-radius: 12rpx;
		text-align: center;
		background: rgba(255, 255, 255, 0.8);
		box-sizing: border-box;
		transition: all 0.2s;
		cursor: pointer;
	}

	.size-option.selected {
		border-color: #667eea;
		background: #2196F3;
		color: #000000;
	}

	.size-option:active {
		transform: scale(0.98);
	}

	.size-label {
		font-size: 26rpx;
		font-weight: 500;
	}

	/* 标签相关 */
	.selected-tags {
		display: flex;
		flex-wrap: wrap;
		gap: 12rpx;
	}

	.tag-chip {
		display: flex;
		align-items: center;
		gap: 8rpx;
		padding: 12rpx 16rpx;
		background: #e3f2fd;
		border: 1rpx solid #2196F3;
		border-radius: 20rpx;
		color: #2196F3;
	}

	.tag-name {
		font-size: 24rpx;
		font-weight: 500;
	}

	.tag-remove {
		font-size: 28rpx;
		font-weight: bold;
		color: #dc3545;
		cursor: pointer;
		line-height: 1;
		padding: 4rpx;
	}

	.tag-input-row {
		display: flex;
		gap: 16rpx;
		align-items: center;
	}

	.tag-input {
		flex: 1;
	}

	.add-btn {
		width: 80rpx;
		height: 80rpx;
		border-radius: 50%;
		background: #2196F3;
		color: #fff;
		border: none;
		font-size: 32rpx;
		font-weight: bold;
		display: flex;
		align-items: center;
		justify-content: center;
		transition: all 0.2s;
	}

	.add-btn:active {
		background: linear-gradient(135deg, #5a6fd8 0%, #6a4190 100%);
		transform: scale(0.95);
	}

	.recommend-tags {
		display: flex;
		flex-wrap: wrap;
		gap: 12rpx;
	}

	.recommend-tag {
		padding: 16rpx 20rpx;
		border: 1rpx solid #dee2e6;
		border-radius: 20rpx;
		background: #f8f9fa;
		transition: all 0.2s;
		cursor: pointer;
	}

	.recommend-tag:active {
		border-color: #667eea;
		background: rgba(102, 126, 234, 0.15);
		color: #667eea;
	}

	.recommend-tag.disabled {
		background: #e9ecef;
		color: #adb5bd;
		border-color: #e9ecef;
		cursor: not-allowed;
	}

	.recommend-name {
		font-size: 24rpx;
	}

	/* 设置项 */
	.setting-item {
		display: flex;
		align-items: center;
		justify-content: space-between;
		padding: 24rpx 0;
		border-bottom: 1rpx solid #f1f3f4;
	}

	.setting-item:last-child {
		border-bottom: none;
	}

	.setting-content {
		flex: 1;
	}

	.setting-name {
		font-size: 28rpx;
		font-weight: 500;
		color: #495057;
		display: block;
		margin-bottom: 8rpx;
	}

	.setting-desc {
		font-size: 24rpx;
		color: #6c757d;
		display: block;
		line-height: 1.4;
	}

	.setting-switch {
		margin-left: 20rpx;
	}

	/* 操作按钮 */
	.memo-actions {
		padding: 40rpx;
		display: flex;
		gap: 20rpx;
	}

	.action-btn {
		flex: 1;
		padding: 32rpx 24rpx;
		border-radius: 16rpx;
		font-size: 30rpx;
		font-weight: 600;
		border: none;
		transition: all 0.2s;
		text-align: center;
	}

	.draft-btn {
		background: #f8f9fa;
		color: #6c757d;
		border: 2rpx solid #dee2e6;
	}

	.draft-btn:active {
		background: #e9ecef;
		transform: translateY(2rpx);
	}

	.create-btn {
		background: #2196F3;
		color: #fff;
		box-shadow: 0 8rpx 20rpx rgba(102, 126, 234, 0.3);
	}

	.create-btn:active {
		transform: translateY(2rpx);
		box-shadow: 0 4rpx 12rpx rgba(102, 126, 234, 0.3);
	}
</style>