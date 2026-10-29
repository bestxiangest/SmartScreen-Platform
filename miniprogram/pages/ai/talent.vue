<template>
	<view class="page-container">
		
		<!-- 自定义导航栏 -->
		<view class="custom-navbar">
			<view class="navbar-content">
				<image class="back-icon" src="http://szzgurngv.hn-bkt.clouddn.com/icon/back.png" @click="goHome" mode="aspectFit"></image>
				<view class="navbar-center">
					<text class="navbar-title">AI人才培养</text>
					<view class="navbar-subtitle">
						<text class="subtitle-text">智能培养方案</text>
						<text class="subtitle-desc">个性化学习路径</text>
					</view>
				</view>
				<view class="navbar-icon">
					<!-- <text class="icon-text">📊</text> -->
					<image class="icon-text" src="http://szzgurngv.hn-bkt.clouddn.com/icon/AI.png" mode="aspectFit"></image>
				</view>
			</view>
		</view>

		<!-- 状态指示器 -->
		<view class="status-indicators">
			<view class="indicator-item" :class="{active: currentStep >= 1}">
				<!-- <text class="indicator-icon">📊</text> -->
				<image class="section-icon" src="http://szzgurngv.hn-bkt.clouddn.com/icon/shujufenxi.png" mode="aspectFit"></image>
				<text class="indicator-text">数据分析</text>
			</view>
			<view class="indicator-item" :class="{active: currentStep >= 2}">
				<!-- <text class="indicator-icon">🎯</text> -->
				<image class="section-icon" src="http://szzgurngv.hn-bkt.clouddn.com/icon/gexingdingzhi.png" mode="aspectFit"></image>
				<text class="indicator-text">个性定制</text>
			</view>
			<view class="indicator-item" :class="{active: currentStep >= 3}">
				<!-- <text class="indicator-icon">📈</text> -->
				<image class="section-icon" src="http://szzgurngv.hn-bkt.clouddn.com/icon/chixuyouhua.png" mode="aspectFit"></image>
				<text class="indicator-text">持续优化</text>
			</view>
		</view>

		<!-- 快速信息调查 -->
		<view class="survey-section">
			<view class="section-header">
				<!-- <text class="section-icon">📋</text> -->
				<image class="section-icon" src="http://szzgurngv.hn-bkt.clouddn.com/icon/xinxidiaocha.png" mode="aspectFit"></image>
				<text class="section-title">快速信息调查</text>
			</view>

			<view class="form-container">
				<!-- 姓名、学历和年龄 -->
				<view class="form-row">
					<view class="form-item third">
						<view class="form-label">
							<!-- <text class="label-icon">👤</text> -->
							<image class="section-icon" src="http://szzgurngv.hn-bkt.clouddn.com/icon/name.png" mode="aspectFit"></image>
							<text class="label-text">姓名</text>
						</view>
						<input class="form-input" v-model="formData.name" placeholder="请输入姓名" />
					</view>
					<view class="form-item third">
						<view class="form-label">
							<!-- <text class="label-icon">🎓</text> -->
							<image class="section-icon" src="http://szzgurngv.hn-bkt.clouddn.com/icon/xueli.png" mode="aspectFit"></image>
							<text class="label-text">学历</text>
						</view>
						<input class="form-input" v-model="formData.education" placeholder="请输入学历" />
					</view>
					<view class="form-item third">
						<view class="form-label">
							<!-- <text class="label-icon">🎂</text> -->
							<image class="section-icon" src="http://szzgurngv.hn-bkt.clouddn.com/icon/age.png" mode="aspectFit"></image>
							<text class="label-text">年龄</text>
						</view>
						<input class="form-input" v-model="formData.age" placeholder="请输入年龄" type="number" />
					</view>
				</view>

				<!-- 专业背景 -->
				<view class="form-item">
					<view class="form-label">
						<!-- <text class="label-icon">🎓</text> -->
						<image class="section-icon" src="http://szzgurngv.hn-bkt.clouddn.com/icon/zhuanye.png" mode="aspectFit"></image>
						<text class="label-text">专业</text>
					</view>
					<input class="form-input" v-model="formData.major" placeholder="请输入专业名称" />
				</view>

				<!-- 兴趣方向 -->
				<view class="form-item">
					<view class="form-label">
						<!-- <text class="label-icon">🎯</text> -->
						<image class="section-icon" src="http://szzgurngv.hn-bkt.clouddn.com/icon/interests.png" mode="aspectFit"></image>
						<text class="label-text">兴趣方向 (如: 机器学习, 深度学习, 自然语言处理)</text>
					</view>
					<textarea class="form-textarea" v-model="formData.interests" placeholder="请选择您感兴趣的AI技术方向..."></textarea>
				</view>

				<!-- 学习目标 -->
				<view class="form-item">
					<view class="form-label">
						<!-- <text class="label-icon">✅</text> -->
						<image class="section-icon" src="http://szzgurngv.hn-bkt.clouddn.com/icon/mubiao.png" mode="aspectFit"></image>
						<text class="label-text">学习目标</text>
					</view>
					<textarea class="form-textarea" v-model="formData.study_objective" placeholder="请描述您的学习目标和期望达到的水平..."></textarea>
				</view>
			</view>
		</view>

		<!-- 生成AI培养方案 -->
		<view class="generate-section">
			<!-- 等待动画 -->
			<view v-if="isGenerating" class="loading-section">
				<view class="loading-container">
					<view class="loading-animation">
						<view class="loading-circle"></view>
					</view>
					<text class="loading-text">{{ loadingText }}</text>
					<view class="loading-progress">
						<view class="progress-bar">
							<view class="progress-fill" :style="{width: loadingProgress + '%'}"></view>
						</view>
						<text class="progress-text">{{ loadingProgress }}%</text>
					</view>
				</view>
			</view>
			<!-- 生成按钮 -->
			<view v-else class="generate-buttons">
				<button class="generate-btn primary" @click="generateStudyPlan">
					<text class="btn-text">方案生成</text>
				</button>
			</view>
		</view>

		<!-- AI培养方案展示 -->
		<view class="plan-section" v-if="showPlan && studyPlanData">
			<view class="section-header">
				<image class="section-icon" src="http://szzgurngv.hn-bkt.clouddn.com/icon/ai_logo.png" mode="aspectFit"></image>
				<text class="section-title">AI人才培养方案</text>
			</view>

			<!-- 学生信息概览 -->
			<view class="student-overview">
				<view class="overview-header">
					<text class="overview-title">{{ studyPlanData.student_info.name }}的个性化培养方案</text>
					<view class="overview-status">
						<text class="status-text">方案生成成功</text>
					</view>
				</view>
				<view class="overview-info">
					<text class="info-item">专业: {{ studyPlanData.student_info.major }}</text>
					<text class="info-item">兴趣: {{ studyPlanData.student_info.interests }}</text>
				</view>
			</view>

			<!-- 发展计划 -->
			<view class="development-plan" v-if="studyPlanData.development_plan.developmentPlan">
				<view class="plan-header">
					<text class="plan-title">发展计划</text>
				</view>
				<view class="plan-item">
					<text class="item-label">推荐专业方向:</text>
					<view class="item-tags">
						<text class="tag" v-for="major in studyPlanData.development_plan.developmentPlan.recommendedMajors" :key="major">{{ major }}</text>
					</view>
				</view>
				<view class="plan-item">
					<text class="item-label">核心能力培养:</text>
					<view class="item-tags">
						<text class="tag" v-for="competency in studyPlanData.development_plan.developmentPlan.coreCompetencies" :key="competency">{{ competency }}</text>
					</view>
				</view>
			</view>

			<!-- 时间目标规划 -->
			<view class="timeline-goals" v-if="studyPlanData.development_plan.timelineGoals">
				<view class="plan-header">
					<text class="plan-title">时间目标规划</text>
				</view>
				<view class="timeline-item" v-if="studyPlanData.development_plan.timelineGoals.shortTerm">
					<view class="timeline-header">
						<text class="timeline-period">短期目标</text>
						<text class="timeline-duration">{{ studyPlanData.development_plan.timelineGoals.shortTerm.period }}</text>
					</view>
					<view class="timeline-goals-list">
						<text class="goal-item" v-for="goal in studyPlanData.development_plan.timelineGoals.shortTerm.goals" :key="goal">• {{ goal }}</text>
					</view>
				</view>
				<view class="timeline-item" v-if="studyPlanData.development_plan.timelineGoals.mediumTerm">
					<view class="timeline-header">
						<text class="timeline-period">中期目标</text>
						<text class="timeline-duration">{{ studyPlanData.development_plan.timelineGoals.mediumTerm.period }}</text>
					</view>
					<view class="timeline-goals-list">
						<text class="goal-item" v-for="goal in studyPlanData.development_plan.timelineGoals.mediumTerm.goals" :key="goal">• {{ goal }}</text>
					</view>
				</view>
				<view class="timeline-item" v-if="studyPlanData.development_plan.timelineGoals.longTerm">
					<view class="timeline-header">
						<text class="timeline-period">长期目标</text>
						<text class="timeline-duration">{{ studyPlanData.development_plan.timelineGoals.longTerm.period }}</text>
					</view>
					<view class="timeline-goals-list">
						<text class="goal-item" v-for="goal in studyPlanData.development_plan.timelineGoals.longTerm.goals" :key="goal">• {{ goal }}</text>
					</view>
				</view>
			</view>

			<!-- 学习资源推荐 -->
			<view class="development-plan" v-if="studyPlanData.development_plan.learningResources">
				<view class="plan-header">
					<text class="plan-title">学习资源推荐</text>
				</view>
				<view class="plan-item" v-if="studyPlanData.development_plan.learningResources.onlinePlatforms">
					<text class="item-label">在线平台:</text>
					<view class="item-tags">
						<text class="tag" v-for="platform in studyPlanData.development_plan.learningResources.onlinePlatforms" :key="platform">{{ platform }}</text>
					</view>
				</view>
				<view class="plan-item" v-if="studyPlanData.development_plan.learningResources.books">
					<text class="item-label">推荐书籍:</text>
					<view class="item-tags">
						<text class="tag" v-for="book in studyPlanData.development_plan.learningResources.books" :key="book">{{ book }}</text>
					</view>
				</view>
				<view class="plan-item" v-if="studyPlanData.development_plan.learningResources.practicalProjects">
					<text class="item-label">实践项目:</text>
					<view class="item-tags">
						<text class="tag" v-for="project in studyPlanData.development_plan.learningResources.practicalProjects" :key="project">{{ project }}</text>
					</view>
				</view>
			</view>

			<!-- 技能发展 -->
			<view class="development-plan" v-if="studyPlanData.development_plan.skillDevelopment">
				<view class="plan-header">
					<text class="plan-title">技能发展</text>
				</view>
				<view class="plan-item" v-if="studyPlanData.development_plan.skillDevelopment.technicalSkills">
					<text class="item-label">技术技能:</text>
					<view class="item-tags">
						<text class="tag" v-for="skill in studyPlanData.development_plan.skillDevelopment.technicalSkills" :key="skill">{{ skill }}</text>
					</view>
				</view>
				<view class="plan-item" v-if="studyPlanData.development_plan.skillDevelopment.softSkills">
					<text class="item-label">软技能:</text>
					<view class="item-tags">
						<text class="tag" v-for="skill in studyPlanData.development_plan.skillDevelopment.softSkills" :key="skill">{{ skill }}</text>
					</view>
				</view>
				<view class="plan-item" v-if="studyPlanData.development_plan.skillDevelopment.certifications">
					<text class="item-label">认证证书:</text>
					<view class="item-tags">
						<text class="tag" v-for="cert in studyPlanData.development_plan.skillDevelopment.certifications" :key="cert">{{ cert }}</text>
					</view>
				</view>
			</view>

			<!-- 职业发展指导 -->
			<view class="development-plan" v-if="studyPlanData.development_plan.careerGuidance">
				<view class="plan-header">
					<text class="plan-title">职业发展指导</text>
				</view>
				<view class="plan-item" v-if="studyPlanData.development_plan.careerGuidance.industryTrends">
					<text class="item-label">行业趋势:</text>
					<view class="item-tags">
						<text class="tag">{{ studyPlanData.development_plan.careerGuidance.industryTrends }}</text>
					</view>
				</view>
				<view class="plan-item" v-if="studyPlanData.development_plan.careerGuidance.careerPaths">
					<text class="item-label">职业路径:</text>
					<view class="item-tags">
						<text class="tag" v-for="path in studyPlanData.development_plan.careerGuidance.careerPaths" :key="path">{{ path }}</text>
					</view>
				</view>
				<view class="plan-item" v-if="studyPlanData.development_plan.careerGuidance.preparationAdvice">
					<text class="item-label">准备建议:</text>
					<view class="item-tags">
						<text class="tag">{{ studyPlanData.development_plan.careerGuidance.preparationAdvice }}</text>
					</view>
				</view>
			</view>

			<!-- 个性化建议 -->
			<view class="development-plan" v-if="studyPlanData.development_plan.personalizedAdvice">
				<view class="plan-header">
					<text class="plan-title">个性化建议</text>
				</view>
				<view class="plan-item" v-if="studyPlanData.development_plan.personalizedAdvice.strengths">
					<text class="item-label">优势:</text>
					<view class="item-tags">
						<text class="tag">{{ studyPlanData.development_plan.personalizedAdvice.strengths }}</text>
					</view>
				</view>
				<view class="plan-item" v-if="studyPlanData.development_plan.personalizedAdvice.challenges">
					<text class="item-label">挑战:</text>
					<view class="item-tags">
						<text class="tag">{{ studyPlanData.development_plan.personalizedAdvice.challenges }}</text>
					</view>
				</view>
				<view class="plan-item" v-if="studyPlanData.development_plan.personalizedAdvice.strategies">
					<text class="item-label">策略:</text>
					<view class="item-tags">
						<text class="tag" v-for="strategy in studyPlanData.development_plan.personalizedAdvice.strategies" :key="strategy">{{ strategy }}</text>
					</view>
				</view>
			</view>

			<!-- 培养方案图表 -->
			<view class="chart-section">
				<view class="plan-header">
					<text class="plan-title">培养方案图表</text>
				</view>
				<view class="chart-container">
					<view v-if="isGeneratingImage" class="chart-loading">
						<text class="loading-text">正在生成培养方案图表...</text>
					</view>
					<view v-else-if="generatedImageUrl" class="chart-image-container">
						<image class="chart-image" :src="generatedImageUrl" mode="aspectFit" @click="previewImage"></image>
						<view class="image-actions">
							<button class="image-btn" @click="previewImage">查看大图</button>
							<button class="image-btn secondary" @click="regenerateChart">重新生成</button>
						</view>
					</view>
					<view v-else class="chart-placeholder">
						<text class="placeholder-text">图表生成中，请稍候...</text>
						<button class="generate-chart-btn" @click="generateChart">重新生成图表</button>
					</view>
				</view>
			</view>

		</view>
	</view>
</template>

<script>
export default {
	data() {
		return {
			currentStep: 1,
		showPlan: false,
		isGenerating: false,
		loadingProgress: 0,
		loadingText: '生成培养方案...',
		formData: {
			name: '',
			education: '',
			age: '',
			major: '',
			interests: '',
			study_objective: ''
		},
		studyPlanData: null,
		generatedImageUrl: '',
		isGeneratingImage: false,
			learningPlan: [
				{
					title: '基础理论学习',
					duration: '4-6周',
					description: '掌握AI基础概念、数学基础和编程技能',
					skills: ['Python编程', '线性代数', '概率统计', 'AI概论']
				},
				{
					title: '核心技术实践',
					duration: '8-10周',
					description: '深入学习机器学习算法和深度学习框架',
					skills: ['机器学习', 'TensorFlow', 'PyTorch', '数据处理']
				},
				{
					title: '项目实战应用',
					duration: '6-8周',
					description: '通过实际项目巩固所学知识，提升实战能力',
					skills: ['项目管理', '模型部署', '性能优化', '团队协作']
				}
			]
		}
	},
	methods: {
		goHome() {
			uni.navigateBack()
		},
		// 生成培养方案
		async generateStudyPlan() {
			// 验证表单
			if (!this.validateForm()) {
				return
			}
			
			this.isGenerating = true
			this.loadingProgress = 0
			this.showPlan = false
			
			// 启动进度动画
			this.startProgressAnimation()
			
			try {
				// 调用培养方案API
			const res = await uni.request({
				url: 'http://sys.sharpcaterpillar.top:7878/api/study_plan',
				method: 'POST',
				header: {
					'Content-Type': 'application/json'
				},
				data: {
					name: this.formData.name,
					age: this.formData.age,
					education: this.formData.education,
					major: this.formData.major,
					interests: this.formData.interests,
					study_objective: this.formData.study_objective
				}
			})
			
			console.log('培养方案API响应:', res)
			
			// 检查HTTP状态码
			if (res.statusCode !== 200) {
				const errorMsg = res.data && res.data.message ? res.data.message : '未知错误'
				throw new Error(`培养方案生成失败: ${errorMsg}`)
			}
			
			const responseData = res.data
			
			// 检查业务逻辑成功标志
			if (!responseData || !responseData.success) {
				const errorMsg = responseData && responseData.message ? responseData.message : '培养方案生成失败'
				uni.showToast({
					title: errorMsg,
					icon: 'none'
				})
				return
			}
			
			// 成功获取数据
			this.studyPlanData = responseData
			this.currentStep = 3
			this.showPlan = true
			
			// 生成图表
			await this.generateChart()
			
			uni.showToast({
				title: '培养方案生成成功',
				icon: 'success'
			})
				
			} catch (error) {
				console.error('生成培养方案失败:', error)
				uni.showToast({
					title: error.message || '网络错误，请重试',
					icon: 'none'
				})
			} finally {
				// 确保进度条完成
				this.loadingProgress = 100
				setTimeout(() => {
					this.isGenerating = false
				}, 500)
			}
		},
		// 表单验证
		validateForm() {
			if (!this.formData.name.trim()) {
				uni.showToast({
					title: '请填写姓名',
					icon: 'none'
				})
				return false
			}
			if (!this.formData.age.trim()) {
				uni.showToast({
					title: '请填写年龄',
					icon: 'none'
				})
				return false
			}
			if (!this.formData.education.trim()) {
				uni.showToast({
					title: '请填写学历',
					icon: 'none'
				})
				return false
			}
			if (!this.formData.major.trim()) {
				uni.showToast({
					title: '请填写专业',
					icon: 'none'
				})
				return false
			}
			if (!this.formData.interests.trim()) {
				uni.showToast({
					title: '请填写兴趣方向',
					icon: 'none'
				})
				return false
			}
			if (!this.formData.study_objective.trim()) {
				uni.showToast({
					title: '请填写学习目标',
					icon: 'none'
				})
				return false
			}
			return true
		},
		// 生成图表
		async generateChart() {
			if (!this.studyPlanData) {
				return
			}
			
			this.isGeneratingImage = true
			uni.showLoading({
				title: '生成图表...'
			})
			
			try {
				// 创建图表提示词
				const chartPrompt = this.createChartPrompt(this.studyPlanData)
				
				// 调用图像生成API
			const imageResponse = await uni.request({
				url: 'https://dashscope.aliyuncs.com/api/v1/services/aigc/text2image/image-synthesis',
				method: 'POST',
				header: {
					'X-DashScope-Async': 'enable',
					'Authorization': 'Bearer YOUR_DASHSCOPE_API_KEY',
					'Content-Type': 'application/json'
				},
				data: {
					model: 'wan2.2-t2i-flash',
					input: {
						prompt: chartPrompt
					},
					parameters: {
						n: 1
					}
				}
			})
			
			console.log('图片生成API响应:', imageResponse)
			
			// 检查HTTP状态码
			if (imageResponse.statusCode !== 200) {
				console.error('HTTP错误:', imageResponse.statusCode)
				throw new Error(`HTTP错误: ${imageResponse.statusCode}`)
			}
			
			// 检查响应数据
			if (!imageResponse.data) {
				console.error('响应数据为空')
				throw new Error('响应数据为空')
			}
			
			// 检查是否有错误信息
			if (imageResponse.data.code && imageResponse.data.code !== '200') {
				console.error('API错误:', imageResponse.data.message || imageResponse.data.code)
				throw new Error(imageResponse.data.message || `API错误: ${imageResponse.data.code}`)
			}
			
			// 检查输出和任务ID
			if (imageResponse.data.output && imageResponse.data.output.task_id) {
				const taskId = imageResponse.data.output.task_id
				console.log('获取到任务ID:', taskId)
				// 轮询查询图片生成结果
				await this.pollImageResult(taskId)
			} else {
				console.error('未获取到任务ID:', imageResponse.data)
				throw new Error('未获取到任务ID，请重试')
			}
			} catch (error) {
				console.error('生成图表失败:', error)
				uni.showToast({
					title: error.message || '图表生成失败',
					icon: 'none'
				})
			} finally {
				this.isGeneratingImage = false
				uni.hideLoading()
			}
		},
		// 创建图表提示词
		createChartPrompt(studyPlanData) {
			// 提取关键信息
			const studentInfo = studyPlanData.student_info || {}
			const developmentPlan = studyPlanData.development_plan || {}
			
			// 安全获取各个部分的数据
			const timelineGoals = developmentPlan.timelineGoals || {}
			const learningResources = developmentPlan.learningResources || {}
			const skillDevelopment = developmentPlan.skillDevelopment || {}
			const careerGuidance = developmentPlan.careerGuidance || {}
			const personalizedAdvice = developmentPlan.personalizedAdvice || {}
			
			// 构建提示词 - 使用Markdown格式确保信息清晰
			let prompt = `请创建包含以下多个维度的学生个性化培养方案图表：\n\n`
			prompt += `# 学生基本信息\n`
			prompt += `- 姓名: ${studentInfo.name || '未知'}\n`
			prompt += `- 年龄: ${studentInfo.age || '未知'}\n`
			prompt += `- 学历: ${studentInfo.education || '未知'}\n`
			prompt += `- 专业: ${studentInfo.major || '未知'}\n`
			prompt += `- 兴趣方向: ${studentInfo.interests || '未知'}\n`
			prompt += `- 学习目标: ${studentInfo.study_objective || '未知'}\n\n`
			
			// 添加发展计划部分（如果存在）
			if (developmentPlan.developmentPlan) {
				const devPlan = developmentPlan.developmentPlan
				
				prompt += `# 发展计划\n`
				prompt += `## 推荐专业方向\n`
				prompt += `- ${(devPlan.recommendedMajors || ['无数据']).slice(0, 3).join(', ')}\n\n`
				
				prompt += `## 核心能力培养\n`
				prompt += `- ${(devPlan.coreCompetencies || ['无数据']).slice(0, 3).join(', ')}\n\n`
				
				prompt += `## 学习路径\n`
				const learningPath = devPlan.learningPath || {}
				prompt += `- 基础阶段: ${learningPath.foundation || '无数据'}\n`
				prompt += `- 中级阶段: ${learningPath.intermediate || '无数据'}\n`
				prompt += `- 高级阶段: ${learningPath.advanced || '无数据'}\n\n`
			}
			
			// 添加时间目标规划
			prompt += `# 时间目标规划\n`
			prompt += `## 短期目标 (${timelineGoals.shortTerm?.period || '未知'})\n`
			prompt += `- ${(timelineGoals.shortTerm?.goals || ['无数据']).slice(0, 3).join(', ')}\n\n`
			prompt += `## 中期目标 (${timelineGoals.mediumTerm?.period || '未知'})\n`
			prompt += `- ${(timelineGoals.mediumTerm?.goals || ['无数据']).slice(0, 3).join(', ')}\n\n`
			prompt += `## 长期目标 (${timelineGoals.longTerm?.period || '未知'})\n`
			prompt += `- ${(timelineGoals.longTerm?.goals || ['无数据']).slice(0, 3).join(', ')}\n\n`
			
			// 添加学习资源推荐
			prompt += `# 学习资源推荐\n`
			prompt += `## 在线平台\n`
			prompt += `- ${(learningResources.onlinePlatforms || ['无数据']).slice(0, 3).join(', ')}\n\n`
			prompt += `## 推荐书籍\n`
			prompt += `- ${(learningResources.books || ['无数据']).slice(0, 3).join(', ')}\n\n`
			prompt += `## 实践项目\n`
			prompt += `- ${(learningResources.practicalProjects || ['无数据']).slice(0, 3).join(', ')}\n\n`
			
			// 添加技能发展
			prompt += `# 技能发展\n`
			prompt += `## 技术技能\n`
			prompt += `- ${(skillDevelopment.technicalSkills || ['无数据']).slice(0, 3).join(', ')}\n\n`
			prompt += `## 软技能\n`
			prompt += `- ${(skillDevelopment.softSkills || ['无数据']).slice(0, 3).join(', ')}\n\n`
			prompt += `## 认证证书\n`
			prompt += `- ${(skillDevelopment.certifications || ['无数据']).slice(0, 3).join(', ')}\n\n`
			
			// 添加职业发展指导
			prompt += `# 职业发展指导\n`
			prompt += `## 行业趋势\n`
			prompt += `- ${careerGuidance.industryTrends || '无数据'}\n\n`
			prompt += `## 职业路径\n`
			prompt += `- ${(careerGuidance.careerPaths || ['无数据']).slice(0, 3).join(', ')}\n\n`
			prompt += `## 准备建议\n`
			prompt += `- ${careerGuidance.preparationAdvice || '无数据'}\n\n`
			
			// 添加个性化建议
			prompt += `# 个性化建议\n`
			prompt += `## 优势\n`
			prompt += `- ${personalizedAdvice.strengths || '无数据'}\n\n`
			prompt += `## 挑战\n`
			prompt += `- ${personalizedAdvice.challenges || '无数据'}\n\n`
			prompt += `## 策略\n`
			prompt += `- ${(personalizedAdvice.strategies || ['无数据']).slice(0, 3).join(', ')}\n`
			
			return prompt
		},
		// 轮询查询图片生成结果
		async pollImageResult(taskId) {
			const maxAttempts = 30
			let attempt = 0
			
			while (attempt < maxAttempts) {
				try {
					const response = await uni.request({
				url: `https://dashscope.aliyuncs.com/api/v1/tasks/${taskId}`,
				method: 'GET',
				header: {
					'Authorization': 'Bearer YOUR_DASHSCOPE_API_KEY'
				}
			})
			
			console.log('轮询API响应:', response)
			
			if (response.statusCode !== 200) {
				throw new Error(`HTTP错误: ${response.statusCode}`)
			}
			
			if (!response.data) {
				throw new Error('响应数据为空')
			}
			
			if (response.data.code && response.data.code !== '200') {
				throw new Error(response.data.message || `API错误: ${response.data.code}`)
			}
			
			if (!response.data.output) {
				throw new Error('输出数据为空')
			}
			
			const status = response.data.output.task_status
			
			if (status === 'SUCCEEDED') {
				if (response.data.output.results && response.data.output.results.length > 0) {
					this.generatedImageUrl = response.data.output.results[0].url
					return
				} else {
					throw new Error('未获取到生成的图片URL')
				}
			} else if (status === 'FAILED') {
				const errorMessage = response.data.output.message || '图片生成失败'
				throw new Error(errorMessage)
			} else if (status === 'PENDING' || status === 'RUNNING') {
				// 等待5秒后继续查询
				await new Promise(resolve => setTimeout(resolve, 5000))
				attempt++
			} else {
				throw new Error(`未知状态: ${status}`)
			}
				} catch (error) {
					console.error('查询图片生成状态失败:', error)
					throw error
				}
			}
			
			throw new Error('图片生成超时')
		},
		// 预览图片
		previewImage() {
			if (this.generatedImageUrl) {
				uni.previewImage({
					urls: [this.generatedImageUrl],
					current: 0
				})
			}
		},
		// 重新生成图表
		async regenerateChart() {
			this.generatedImageUrl = ''
			await this.generateChart()
		},
		// 启动进度动画
		startProgressAnimation() {
			const steps = [
				{ progress: 20, text: '分析学生基本信息...' },
				{ progress: 40, text: '匹配专业方向...' },
				{ progress: 60, text: '制定学习计划...' },
				{ progress: 80, text: '生成培养方案...' },
				{ progress: 95, text: '优化方案内容...' }
			]
			
			let currentStep = 0
			
			const updateProgress = () => {
				if (currentStep < steps.length && this.isGenerating) {
					this.loadingProgress = steps[currentStep].progress
					this.loadingText = steps[currentStep].text
					currentStep++
					setTimeout(updateProgress, 1000 + Math.random() * 1000)
				}
			}
			
			updateProgress()
		}
	}
}
</script>

<style scoped>
.page-container {
	min-height: 100vh;
	background: #e3f2fd;
}

.custom-navbar {
		background: linear-gradient(135deg, #2196F3 0%, #1976D2 100%);
		padding: 40rpx 30rpx;
		padding-top: 80rpx;
		box-shadow: 0 4rpx 20rpx rgba(33, 150, 243, 0.3);
		min-height: 120rpx;
	}

.navbar-content {
	display: flex;
	align-items: center;
	justify-content: space-between;
	position: relative;
}

.back-icon {
		width: 60rpx;
		height: 60rpx;
	}

.navbar-center {
	display: flex;
	flex-direction: column;
	align-items: center;
	gap: 5rpx;
	position: absolute;
	left: 50%;
	transform: translateX(-50%);
}

.navbar-title {
	margin-top: 58rpx;
	font-size: 48rpx;
	font-weight: bold;
	color: white;
}

.navbar-subtitle {
	display: flex;
	flex-direction: column;
	align-items: center;
	gap: 2rpx;
}

.subtitle-text {
	font-size: 24rpx;
	color: rgba(255, 255, 255, 0.9);
}

.subtitle-desc {
	font-size: 20rpx;
	color: rgba(255, 255, 255, 0.7);
}

.navbar-icon {
	width: 90rpx;
	height: 90rpx;
	border-radius: 30rpx;
	display: flex;
	align-items: center;
	justify-content: center;
}

.icon-text {
	width: 90rpx;
	height: 90rpx;
}

.status-indicators {
	display: flex;
	justify-content: space-around;
	padding: 30rpx;
	margin: 20rpx;
	background: white;
	border-radius: 20rpx;
	box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.1);
}

.indicator-item {
	display: flex;
	flex-direction: column;
	align-items: center;
	gap: 10rpx;
	opacity: 0.5;
	transition: all 0.3s ease;
}

.indicator-item.active {
	opacity: 1;
	transform: scale(1.1);
}

.indicator-icon {
	font-size: 32rpx;
}

.indicator-text {
	font-size: 24rpx;
	color: #666;
	font-weight: 500;
}

.survey-section {
	margin: 20rpx;
	background: white;
	border-radius: 20rpx;
	padding: 30rpx;
	box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.1);
}

.section-header {
	display: flex;
	align-items: center;
	gap: 15rpx;
	margin-bottom: 30rpx;
	padding-bottom: 20rpx;
	border-bottom: 2rpx solid #f0f0f0;
}

.section-icon {
	width: 80rpx;
	height: 80rpx;
}

.section-title {
	font-size: 32rpx;
	font-weight: bold;
	color: #333;
}

.form-container {
	display: flex;
	flex-direction: column;
	gap: 25rpx;
}

.form-item {
	display: flex;
	flex-direction: column;
	gap: 15rpx;
}

.form-item.half {
		flex: 1;
	}

	.form-item.third {
		flex: 1;
	}

	.form-row {
		display: flex;
		gap: 20rpx;
	}

.form-label {
	display: flex;
	align-items: center;
	gap: 10rpx;
}

.label-icon {
	font-size: 24rpx;
}

.label-text {
	font-size: 32rpx;
	color: #333;
	font-weight: 500;
}

.form-input {
		height: 80rpx;
		padding: 0 20rpx;
		border: 2rpx solid #e0e0e0;
		border-radius: 12rpx;
		font-size: 28rpx;
		background: #fafafa;
		transition: all 0.3s ease;
		width: 100%;
		box-sizing: border-box;
	}

	.form-input:focus {
		border-color: #2196F3;
		background: white;
		box-shadow: 0 0 0 4rpx rgba(33, 150, 243, 0.1);
	}

.form-textarea {
		min-height: 120rpx;
		padding: 20rpx;
		border: 2rpx solid #e0e0e0;
		border-radius: 12rpx;
		font-size: 28rpx;
		background: #fafafa;
		transition: all 0.3s ease;
		resize: vertical;
		width: 100%;
		box-sizing: border-box;
	}

	.form-textarea:focus {
		border-color: #2196F3;
		background: white;
		box-shadow: 0 0 0 4rpx rgba(33, 150, 243, 0.1);
	}

.generate-section {
	margin: 20rpx;
	padding: 30rpx;
}

.generate-buttons {
	display: flex;
	gap: 20rpx;
}

.generate-btn {
	width: 100%;
	height: 80rpx;
	border-radius: 12rpx;
	border: none;
	font-size: 28rpx;
	font-weight: bold;
	transition: all 0.3s ease;
}

.generate-btn.primary {
	background: linear-gradient(135deg, #2196F3 0%, #1976D2 100%);
	color: white;
	box-shadow: 0 4rpx 20rpx rgba(33, 150, 243, 0.3);
}

.generate-btn.primary:active {
	transform: translateY(2rpx);
	box-shadow: 0 2rpx 10rpx rgba(33, 150, 243, 0.3);
}

.generate-btn.secondary {
	background: white;
	color: #2196F3;
	border: 2rpx solid #2196F3;
}

.generate-btn.secondary:active {
	background: #f5f5f5;
}

.btn-text {
	font-size: 28rpx;
	font-weight: bold;
}

.plan-section {
	margin: 20rpx;
	background: white;
	border-radius: 20rpx;
	padding: 30rpx;
	box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.1);
}

.plan-content {
	display: flex;
	flex-direction: column;
	gap: 20rpx;
}

.plan-card {
	background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
	border-radius: 16rpx;
	padding: 30rpx;
	border: 2rpx solid #e0e0e0;
}

.plan-header {
	display: flex;
	justify-content: space-between;
	align-items: center;
	margin-bottom: 25rpx;
	padding-bottom: 20rpx;
	border-bottom: 2rpx solid #e0e0e0;
}

.plan-title {
	font-size: 32rpx;
	font-weight: bold;
	color: #333;
}

.plan-status {
	background: linear-gradient(135deg, #4CAF50 0%, #45a049 100%);
	padding: 8rpx 16rpx;
	border-radius: 20rpx;
}

.status-text {
	font-size: 22rpx;
	color: white;
	font-weight: 500;
}

.plan-phases {
	display: flex;
	flex-direction: column;
	gap: 20rpx;
	margin-bottom: 30rpx;
}

.phase-item {
	background: white;
	border-radius: 12rpx;
	padding: 25rpx;
	border-left: 6rpx solid #2196F3;
	box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
}

.phase-header {
	display: flex;
	align-items: center;
	gap: 15rpx;
	margin-bottom: 15rpx;
}

.phase-number {
	width: 40rpx;
	height: 40rpx;
	border-radius: 20rpx;
	background: #2196F3;
	color: white;
	font-size: 24rpx;
	font-weight: bold;
	display: flex;
	align-items: center;
	justify-content: center;
}

.phase-title {
	flex: 1;
	font-size: 28rpx;
	font-weight: bold;
	color: #333;
}

.phase-duration {
	font-size: 24rpx;
	color: #666;
	background: #f0f0f0;
	padding: 6rpx 12rpx;
	border-radius: 12rpx;
}

.phase-content {
	display: flex;
	flex-direction: column;
	gap: 15rpx;
}

.phase-description {
	font-size: 26rpx;
	color: #666;
	line-height: 1.5;
}

.phase-skills {
	display: flex;
	flex-wrap: wrap;
	gap: 10rpx;
}

.skill-tag {
	background: linear-gradient(135deg, #e3f2fd 0%, #bbdefb 100%);
	color: #1976D2;
	padding: 8rpx 16rpx;
	border-radius: 20rpx;
	font-size: 22rpx;
	font-weight: 500;
	border: 1rpx solid #90caf9;
}

.plan-actions {
	display: flex;
	gap: 15rpx;
}

.action-btn {
	flex: 1;
	height: 70rpx;
	border-radius: 12rpx;
	border: none;
	font-size: 26rpx;
	font-weight: bold;
	background: linear-gradient(135deg, #2196F3 0%, #1976D2 100%);
	color: white;
	box-shadow: 0 4rpx 15rpx rgba(33, 150, 243, 0.3);
	transition: all 0.3s ease;
}

.action-btn:active {
	transform: translateY(2rpx);
	box-shadow: 0 2rpx 8rpx rgba(33, 150, 243, 0.3);
}

.action-btn.secondary {
	background: white;
	color: #2196F3;
	border: 2rpx solid #2196F3;
	box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.1);
}

.action-btn.secondary:active {
	background: #f5f5f5;
}

/* 学生信息概览样式 */
.student-overview {
	margin-bottom: 30rpx;
	padding: 30rpx;
	background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
	border-radius: 16rpx;
	border: 2rpx solid #e0e0e0;
}

.overview-header {
	display: flex;
	justify-content: space-between;
	align-items: center;
	margin-bottom: 20rpx;
}

.overview-title {
	font-size: 32rpx;
	font-weight: bold;
	color: #333;
}

.overview-status {
	background: linear-gradient(135deg, #4CAF50 0%, #45a049 100%);
	padding: 8rpx 16rpx;
	border-radius: 20rpx;
}

.overview-info {
	display: flex;
	gap: 30rpx;
}

.info-item {
	font-size: 26rpx;
	color: #666;
}

/* 发展计划样式 */
.development-plan {
	margin-bottom: 30rpx;
	padding: 30rpx;
	background: white;
	border-radius: 16rpx;
	box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.1);
}

.plan-header {
	margin-bottom: 25rpx;
	padding-bottom: 20rpx;
	border-bottom: 2rpx solid #f0f0f0;
}

.plan-title {
	font-size: 30rpx;
	font-weight: bold;
	color: #333;
}

.plan-item {
	margin-bottom: 20rpx;
}

.item-label {
	font-size: 26rpx;
	color: #666;
	margin-bottom: 15rpx;
	display: block;
}

.item-tags {
	display: flex;
	flex-wrap: wrap;
	gap: 10rpx;
}

.tag {
	background: linear-gradient(135deg, #e3f2fd 0%, #bbdefb 100%);
	color: #1976D2;
	padding: 8rpx 16rpx;
	border-radius: 20rpx;
	font-size: 24rpx;
	font-weight: 500;
	border: 1rpx solid #90caf9;
}

/* 时间目标规划样式 */
.timeline-goals {
	margin-bottom: 30rpx;
	padding: 30rpx;
	background: white;
	border-radius: 16rpx;
	box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.1);
}

.timeline-item {
	margin-bottom: 25rpx;
	padding: 20rpx;
	background: #f8f9fa;
	border-radius: 12rpx;
	border-left: 6rpx solid #2196F3;
}

.timeline-header {
	display: flex;
	justify-content: space-between;
	align-items: center;
	margin-bottom: 15rpx;
}

.timeline-period {
	font-size: 28rpx;
	font-weight: bold;
	color: #2196F3;
}

.timeline-duration {
	font-size: 24rpx;
	color: #666;
	background: #e3f2fd;
	padding: 6rpx 12rpx;
	border-radius: 12rpx;
}

.timeline-goals-list {
	display: flex;
	flex-direction: column;
	gap: 8rpx;
}

.goal-item {
	font-size: 26rpx;
	color: #333;
	line-height: 1.4;
}

/* 图表部分样式 */
.chart-section {
	margin-bottom: 30rpx;
	padding: 30rpx;
	background: white;
	border-radius: 16rpx;
	box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.1);
}

.chart-container {
	min-height: 300rpx;
	display: flex;
	align-items: center;
	justify-content: center;
}

.chart-loading {
	display: flex;
	align-items: center;
	justify-content: center;
	height: 200rpx;
}

.loading-text {
	font-size: 28rpx;
	color: #666;
}

.chart-image-container {
	width: 100%;
	display: flex;
	flex-direction: column;
	align-items: center;
	gap: 20rpx;
}

.chart-image {
	width: 100%;
	max-height: 400rpx;
	border-radius: 12rpx;
	box-shadow: 0 4rpx 15rpx rgba(0, 0, 0, 0.1);
}

.image-actions {
	display: flex;
	gap: 15rpx;
}

.image-btn {
	padding: 12rpx 24rpx;
	border-radius: 8rpx;
	border: none;
	font-size: 24rpx;
	font-weight: 500;
	background: #2196F3;
	color: white;
}

.image-btn.secondary {
	background: white;
	color: #2196F3;
	border: 2rpx solid #2196F3;
}

.chart-placeholder {
	display: flex;
	flex-direction: column;
	align-items: center;
	gap: 20rpx;
	height: 200rpx;
	justify-content: center;
}

.placeholder-text {
	font-size: 26rpx;
	color: #999;
}

.generate-chart-btn {
	padding: 15rpx 30rpx;
	background: #2196F3;
	color: white;
	border: none;
	border-radius: 8rpx;
	font-size: 26rpx;
	font-weight: 500;
}

/* 等待动画样式 */
.loading-section {
	padding: 40rpx;
	background: white;
	border-radius: 20rpx;
	margin: 20rpx;
	box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.1);
}

.loading-container {
	display: flex;
	flex-direction: column;
	align-items: center;
	gap: 30rpx;
}

.loading-animation {
	display: flex;
	align-items: center;
	justify-content: center;
}

.loading-circle {
	width: 80rpx;
	height: 80rpx;
	border: 6rpx solid #f0f0f0;
	border-top: 6rpx solid #2196F3;
	border-radius: 50%;
	animation: loading-bounce 1s linear infinite;
}

@keyframes loading-bounce {
	0% {
		transform: rotate(0deg);
	}
	100% {
		transform: rotate(360deg);
	}
}

.loading-text {
	font-size: 28rpx;
	color: #666;
	font-weight: 500;
	text-align: center;
}

.loading-progress {
	width: 100%;
	display: flex;
	flex-direction: column;
	align-items: center;
	gap: 15rpx;
}

.progress-bar {
	width: 100%;
	height: 12rpx;
	background: #f0f0f0;
	border-radius: 6rpx;
	overflow: hidden;
}

.progress-fill {
	height: 100%;
	background: linear-gradient(135deg, #2196F3 0%, #1976D2 100%);
	border-radius: 6rpx;
	transition: width 0.3s ease;
}

.progress-text {
	font-size: 24rpx;
	color: #2196F3;
	font-weight: bold;
}
</style>