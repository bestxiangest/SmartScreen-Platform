<template>
	<view class="container">
		<!-- 自定义导航栏 -->
		<view class="custom-navbar">
			<view class="navbar-content">
				<image class="back-icon" src="http://szzgurngv.hn-bkt.clouddn.com/icon/back.png" @click="goBack" mode="aspectFit"></image>
				<view class="navbar-center">
					<text class="navbar-title">AI项目评估</text>
					<view class="navbar-subtitle">
						<text class="subtitle-text">智能评估系统</text>
						<text class="subtitle-desc">深度学习分析</text>
					</view>
				</view>
				<view class="navbar-icon">
					<!-- <text class="icon-text">📊</text> -->
					<image class="icon-text" src="http://szzgurngv.hn-bkt.clouddn.com/icon/AI.png" mode="aspectFit"></image>
				</view>
			</view>
		</view>

		<!-- 项目基础信息 -->
		<view class="form-section">
			<view class="section-header">
				<!-- <view class="section-icon">📋</view> -->
				<image class="section-icon" src="http://szzgurngv.hn-bkt.clouddn.com/icon/pinggu_icon.png" mode="aspectFit"></image>
				<text class="section-title">项目基础信息</text>
			</view>
			<view class="form-content">
				<view class="form-item">
					<text class="form-label">项目名称</text>
					<view class="input-wrapper">
						<input class="form-input" v-model="projectInfo.name" />
						<text class="placeholder" v-if="!projectInfo.name">请输入项目名称</text>
					</view>
				</view>
				<view class="form-item">
					<text class="form-label">项目简介</text>
					<textarea class="form-textarea" v-model="projectInfo.description" placeholder="请简要描述项目内容和目标" />
				</view>
				<view class="form-item">
					<text class="form-label">技术领域 (例：人工智能、区块链、物联网)</text>
					<view class="input-wrapper">
						<input class="form-input" v-model="projectInfo.techField" />
						<text class="placeholder" v-if="!projectInfo.techField">请输入主要技术领域</text>
					</view>
				</view>
				<view class="form-item">
					<text class="form-label">项目周期 (月)</text>
					<view class="input-wrapper">
						<input class="form-input" v-model="projectInfo.duration" type="number" />
						<text class="placeholder" v-if="!projectInfo.duration">预计项目周期</text>
					</view>
				</view>
				<view class="form-item">
					<text class="form-label">团队规模 (人)</text>
					<view class="input-wrapper">
						<input class="form-input" v-model="projectInfo.teamSize" type="number" />
						<text class="placeholder" v-if="!projectInfo.teamSize">团队成员数量</text>
					</view>
				</view>
			</view>
			<view class="form-actions">
				<button class="btn-secondary" @click="clearForm">清空重填</button>
				<button class="btn-primary" @click="startEvaluation">开始评估</button>
			</view>
		</view>

		<!-- 评估加载动画 -->
		<view class="loading-section" v-if="isEvaluating">
			<view class="loading-container">
				<view class="loading-animation">
					<view class="loading-circle"></view>
					<view class="loading-circle"></view>
					<view class="loading-circle"></view>
				</view>
				<text class="loading-text">AI智能评估中...</text>
				<text class="loading-desc">正在分析项目可行性、技术架构、团队匹配度等多个维度</text>
				<view class="loading-progress">
					<view class="progress-bar">
						<view class="progress-fill" :style="{width: loadingProgress + '%'}"></view>
					</view>
					<text class="progress-text">{{loadingProgress}}%</text>
				</view>
			</view>
		</view>

		<!-- 智能评估结果 -->
		<view class="result-section" v-if="showResults">
			<view class="section-header">
				<view class="section-icon">📊</view>
				<text class="section-title">智能评估结果</text>
			</view>
			<view class="result-content">
				<view class="result-summary">
					<view class="summary-item">
						<text class="summary-label">综合评分</text>
						<text class="summary-score">{{evaluationResult.totalScore}}</text>
					</view>
					<view class="summary-item">
						<text class="summary-label">项目等级</text>
						<text class="summary-grade">{{evaluationResult.grade}}</text>
					</view>
				</view>
				
				<view class="evaluation-details">
					<text class="details-title">评估详情</text>
					<view class="detail-item" v-for="(item, index) in evaluationResult.details" :key="index">
						<view class="detail-header">
							<view class="detail-icon" :class="item.status">{{item.icon}}</view>
							<text class="detail-name">{{item.name}}</text>
							<text class="detail-score">{{item.score}}</text>
						</view>
						<text class="detail-desc">{{item.description}}</text>
					</view>
				</view>
				
				<!-- 详细评估信息 -->
				<view class="detailed-info" v-if="fullEvaluationData">
					<view class="info-section">
						<text class="info-title">📋 项目基本信息</text>
						<view class="info-content">
							<view class="info-item">
								<text class="info-label">项目名称:</text>
								<text class="info-value">{{fullEvaluationData.evaluation_result.projectInfo.name}}</text>
							</view>
							<view class="info-item">
								<text class="info-label">技术领域:</text>
								<text class="info-value">{{fullEvaluationData.evaluation_result.projectInfo.techArea}}</text>
							</view>
							<view class="info-item">
								<text class="info-label">项目周期:</text>
								<text class="info-value">{{fullEvaluationData.evaluation_result.projectInfo.duration}}</text>
							</view>
							<view class="info-item">
								<text class="info-label">复杂程度:</text>
								<text class="info-value">{{fullEvaluationData.evaluation_result.projectInfo.complexityLevel}}</text>
							</view>
						</view>
					</view>
					
					<view class="info-section">
						<text class="info-title">👥 团队结构</text>
						<view class="info-content">
							<view class="team-item" v-for="(role, index) in fullEvaluationData.evaluation_result.teamStructure.roles" :key="index">
								<view class="team-header">
									<text class="team-position">{{role.position}}</text>
									<text class="team-count">{{role.count}}人</text>
								</view>
								<text class="team-responsibilities">{{role.responsibilities.join('、')}}</text>
							</view>
						</view>
					</view>
					
					<view class="info-section">
						<text class="info-title">⚠️ 风险评估</text>
						<view class="info-content">
							<view class="risk-category" v-if="fullEvaluationData.evaluation_result.riskAssessment.technicalRisks.length > 0">
								<text class="risk-category-title">技术风险:</text>
								<view class="risk-item" v-for="(risk, index) in fullEvaluationData.evaluation_result.riskAssessment.technicalRisks" :key="index">
									<text class="risk-desc">{{risk.risk}}</text>
									<view class="risk-level">
										<text class="risk-probability">概率: {{risk.probability}}</text>
										<text class="risk-impact">影响: {{risk.impact}}</text>
									</view>
								</view>
							</view>
							<view class="risk-category" v-if="fullEvaluationData.evaluation_result.riskAssessment.projectRisks.length > 0">
								<text class="risk-category-title">项目风险:</text>
								<view class="risk-item" v-for="(risk, index) in fullEvaluationData.evaluation_result.riskAssessment.projectRisks" :key="index">
									<text class="risk-desc">{{risk.risk}}</text>
									<view class="risk-level">
										<text class="risk-probability">概率: {{risk.probability}}</text>
										<text class="risk-impact">影响: {{risk.impact}}</text>
									</view>
								</view>
							</view>
						</view>
					</view>
					
					<view class="info-section">
						<text class="info-title">💰 资源需求</text>
						<view class="info-content">
							<view class="info-item">
								<text class="info-label">预算范围:</text>
								<text class="info-value">{{fullEvaluationData.evaluation_result.resourceRequirements.estimatedBudget}}</text>
							</view>
							<view class="info-item">
								<text class="info-label">硬件要求:</text>
								<text class="info-value">{{fullEvaluationData.evaluation_result.resourceRequirements.hardware.join('、')}}</text>
							</view>
							<view class="info-item">
								<text class="info-label">软件要求:</text>
								<text class="info-value">{{fullEvaluationData.evaluation_result.resourceRequirements.software.join('、')}}</text>
							</view>
						</view>
					</view>
				</view>

					<!-- 实施计划 -->
					<view class="info-section" v-if="fullEvaluationData.evaluation_result.implementationPlan">
						<text class="info-title">🚀 实施计划</text>
						<view class="info-content">
							<view class="team-item" v-for="(phase, key) in fullEvaluationData.evaluation_result.implementationPlan" :key="key">
								<view class="team-header">
									<text class="team-position">{{phase.name}}</text>
									<text class="team-count">{{phase.duration}}</text>
								</view>
								<text class="team-responsibilities">{{phase.objectives.join('、')}}</text>
							</view>
						</view>
					</view>

					<!-- 质量保证 -->
					<view class="info-section" v-if="fullEvaluationData.evaluation_result.qualityAssurance">
						<text class="info-title">✅ 质量保证</text>
						<view class="info-content">
							<view class="info-item" v-if="fullEvaluationData.evaluation_result.qualityAssurance.testingStrategy">
								<text class="info-label">测试策略:</text>
								<text class="info-value">{{fullEvaluationData.evaluation_result.qualityAssurance.testingStrategy}}</text>
							</view>
							<view class="info-item" v-if="fullEvaluationData.evaluation_result.qualityAssurance.codeReview">
								<text class="info-label">代码审查:</text>
								<text class="info-value">{{fullEvaluationData.evaluation_result.qualityAssurance.codeReview}}</text>
							</view>
						</view>
					</view>

					<!-- 成功标准 -->
					<view class="info-section" v-if="fullEvaluationData.evaluation_result.successCriteria">
						<text class="info-title">🎯 成功标准</text>
						<view class="info-content">
							<view class="item-tags">
								<text class="tag" v-for="(criterion, index) in fullEvaluationData.evaluation_result.successCriteria" :key="index">{{criterion}}</text>
							</view>
						</view>
					</view>
				
				<view class="chart-section">
					<view class="chart-header">
						<view class="chart-icon">📈</view>
						<text class="chart-title">智能评估数据</text>
						<text class="chart-subtitle">基于机器学习模型的多维度分析</text>
					</view>
					<view class="chart-container">
						<view v-if="generatedImageUrl" class="chart-image-container">
							<image :src="generatedImageUrl" class="chart-image" mode="aspectFit" @click="previewImage"></image>
							<view class="image-actions">
								<button class="image-btn" @click="previewImage">查看大图</button>
								<button class="image-btn" @click="regenerateChart">重新生成</button>
							</view>
						</view>
						<view v-else class="chart-placeholder">
							<view class="chart-content">
								<text class="chart-text">AI评估图表</text>
								<text class="chart-desc">技术可行性、市场前景、团队能力等维度评估</text>
								<button class="generate-chart-btn" @click="generateChart">生成评估图表</button>
							</view>
						</view>
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
			projectInfo: {
				name: '',
				description: '',
				techField: '',
				duration: '',
				teamSize: ''
			},
			showResults: false,
			isEvaluating: false,
			loadingProgress: 0,
			fullEvaluationData: null,
			generatedImageUrl: '',
			evaluationResult: {
				totalScore: 85,
				grade: 'A',
				details: [
					{
						icon: '✅',
						name: '技术可行性分析',
						score: '90分',
						status: 'good',
						description: '技术方案成熟，实现难度适中'
					},
					{
						icon: '⚠️',
						name: '市场前景评估',
						score: '75分',
						status: 'warning',
						description: '市场需求较好，但竞争激烈'
					},
					{
						icon: '✅',
						name: '团队能力匹配',
						score: '88分',
						status: 'good',
						description: '团队技能与项目需求高度匹配'
					},
					{
						icon: '❌',
						name: '风险控制能力',
						score: '65分',
						status: 'danger',
						description: '需要加强项目风险管理'
					},
					{
						icon: '✅',
						name: '创新性评估',
						score: '92分',
						status: 'good',
						description: '项目具有较强的创新性和独特性'
					}
				]
			}
		}
	},
	methods: {
		clearForm() {
			this.projectInfo = {
				name: '',
				description: '',
				techField: '',
				duration: '',
				teamSize: ''
			}
			this.showResults = false
			this.isEvaluating = false
			this.loadingProgress = 0
			this.fullEvaluationData = null
			this.generatedImageUrl = ''
		},
		async startEvaluation() {
			// 验证必填字段
			if (!this.projectInfo.name || !this.projectInfo.description || !this.projectInfo.techField || !this.projectInfo.duration || !this.projectInfo.teamSize) {
				uni.showToast({
					title: '请填写完整的项目信息',
					icon: 'none'
				})
				return
			}
			
			// 开始评估动画
			this.isEvaluating = true
			this.loadingProgress = 0
			this.showResults = false
			
			// 启动进度条动画
			this.startProgressAnimation()
			
			try {
				// 调用AI项目评估接口
				const response = await uni.request({
					url: 'http://sys.sharpcaterpillar.top:7878/api/evaluate_ai_project',
					method: 'POST',
					header: {
						'Content-Type': 'application/json'
					},
					data: {
						project_name: this.projectInfo.name,
						project_description: this.projectInfo.description,
						tech_area: this.projectInfo.techField,
						project_duration: this.projectInfo.duration.toString(),
						team_size: this.projectInfo.teamSize.toString()
					}
				})
				
				// 确保进度条达到100%
				this.loadingProgress = 100
				
				// 延迟一下让用户看到100%
				await new Promise(resolve => setTimeout(resolve, 500))
				
				this.isEvaluating = false
				
				if (response.statusCode === 200 && response.data.success) {
					// 处理评估结果
					this.processEvaluationResult(response.data)
					this.showResults = true
					uni.showToast({
						title: '评估完成',
						icon: 'success'
					})
				} else {
					uni.showToast({
						title: response.data.message || '评估失败，请重试',
						icon: 'none'
					})
				}
			} catch (error) {
				this.isEvaluating = false
				console.error('评估接口调用失败:', error)
				uni.showToast({
					title: '网络错误，请检查网络连接',
					icon: 'none'
				})
			}
		},
		startProgressAnimation() {
			// 模拟评估进度
			const progressSteps = [
				{ progress: 20, delay: 800, text: '分析项目基础信息...' },
				{ progress: 40, delay: 1200, text: '评估技术可行性...' },
				{ progress: 60, delay: 1000, text: '分析团队匹配度...' },
				{ progress: 80, delay: 1000, text: '计算风险评估...' },
				{ progress: 95, delay: 800, text: '生成评估报告...' }
			]
			
			let currentStep = 0
			const updateProgress = () => {
				if (currentStep < progressSteps.length && this.isEvaluating) {
					const step = progressSteps[currentStep]
					this.loadingProgress = step.progress
					currentStep++
					setTimeout(updateProgress, step.delay)
				}
			}
			updateProgress()
		},
		processEvaluationResult(apiResponse) {
			// 处理API返回的评估结果
			const result = apiResponse.evaluation_result
			
			// 更新评估结果数据
			this.evaluationResult = {
				totalScore: result.projectInfo.feasibilityScore,
				grade: this.getGradeFromScore(result.projectInfo.feasibilityScore),
				projectInfo: result.projectInfo,
				technicalArchitecture: result.technicalArchitecture,
				implementationPlan: result.implementationPlan,
				timeline: result.timeline,
				teamStructure: result.teamStructure,
				riskAssessment: result.riskAssessment,
				resourceRequirements: result.resourceRequirements,
				qualityAssurance: result.qualityAssurance,
				successCriteria: result.successCriteria,
				details: [
					{
						icon: '✅',
						name: '技术可行性分析',
						score: result.projectInfo.feasibilityScore + '分',
						status: this.getStatusFromScore(result.projectInfo.feasibilityScore),
						description: `复杂程度: ${result.projectInfo.complexityLevel}, 技术领域: ${result.projectInfo.techArea}`
					},
					{
						icon: '👥',
						name: '团队结构评估',
						score: '85分',
						status: 'good',
						description: `团队规模: ${result.projectInfo.teamSize}, 协作模式: ${result.teamStructure.collaborationStructure}`
					},
					{
						icon: '⚠️',
						name: '风险控制评估',
						score: '75分',
						status: 'warning',
						description: `技术风险: ${result.riskAssessment.technicalRisks.length}项, 项目风险: ${result.riskAssessment.projectRisks.length}项`
					},
					{
						icon: '💰',
						name: '资源需求分析',
						score: '80分',
						status: 'good',
						description: `预算范围: ${result.resourceRequirements.estimatedBudget}`
					},
					{
						icon: '🎯',
						name: '项目目标评估',
						score: '88分',
						status: 'good',
						description: `项目周期: ${result.projectInfo.duration}, 质量策略: ${result.qualityAssurance.testingStrategy}`
					}
				]
			}
			
			// 存储完整的评估数据供后续使用
			this.fullEvaluationData = apiResponse
		},
		getGradeFromScore(score) {
			if (score >= 9) return 'A+'
			if (score >= 8) return 'A'
			if (score >= 7) return 'B+'
			if (score >= 6) return 'B'
			if (score >= 5) return 'C'
			return 'D'
		},
		getStatusFromScore(score) {
			if (score >= 8) return 'good'
			if (score >= 6) return 'warning'
			return 'danger'
		},
		async generateChart() {
			if (!this.fullEvaluationData) {
				uni.showToast({
					title: '请先完成项目评估',
					icon: 'none'
				})
				return
			}
			
			uni.showLoading({
				title: '生成图表中...'
			})
			
			try {
				// 创建图表提示词
				const chartPrompt = this.createChartPrompt(this.fullEvaluationData.evaluation_result)
				
				// 调用通义千问图像生成API
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
				
				if (imageResponse.statusCode === 200) {
					const taskId = imageResponse.data.output.task_id
					// 轮询查询图片生成结果
					this.pollImageResult(taskId)
				} else {
					uni.hideLoading()
					uni.showToast({
						title: '图表生成失败',
						icon: 'none'
					})
				}
			} catch (error) {
				uni.hideLoading()
				console.error('图表生成失败:', error)
				uni.showToast({
					title: '图表生成失败，请重试',
					icon: 'none'
				})
			}
		},
		createChartPrompt(evaluationResult) {
			// 根据评估结果创建图表提示词
			const projectInfo = evaluationResult.projectInfo
			const phases = evaluationResult.implementationPlan
			const team = evaluationResult.teamStructure
			const risks = evaluationResult.riskAssessment
			const resources = evaluationResult.resourceRequirements
			
			return `请创建包含以下多个维度的项目评估信息图表：\n\n` +
				`# 项目概况 ('${projectInfo.name}')\n` +
				`- 可行性评分: ${projectInfo.feasibilityScore}/10\n` +
				`- 复杂程度: ${projectInfo.complexityLevel}\n` +
				`- 技术领域: ${projectInfo.techArea}\n` +
				`- 团队规模: ${projectInfo.teamSize}\n\n` +
				`# 实施计划\n` +
				`| 阶段 | 时长 | 主要目标 |\n` +
				`|------|------|-----------|\n` +
				`| ${phases.phase1.name} | ${phases.phase1.duration} | ${phases.phase1.objectives.slice(0,2).join('; ')} |\n` +
				`| ${phases.phase2.name} | ${phases.phase2.duration} | ${phases.phase2.objectives.slice(0,2).join('; ')} |\n` +
				`| ${phases.phase3.name} | ${phases.phase3.duration} | ${phases.phase3.objectives.slice(0,2).join('; ')} |\n\n` +
				`# 团队构成\n` +
				`| 角色 | 人数 | 主要职责 |\n` +
				`|------|------|-----------|\n` +
				team.roles.map(role => `| ${role.position} | ${role.count} | ${role.responsibilities[0]} |`).join('\n') + '\n\n' +
				`# 风险评估\n` +
				(risks.technicalRisks.length > 0 ? `- 主要技术风险: ${risks.technicalRisks[0].risk} (概率: ${risks.technicalRisks[0].probability}, 影响: ${risks.technicalRisks[0].impact})\n` : '') +
				(risks.projectRisks.length > 0 ? `- 主要项目风险: ${risks.projectRisks[0].risk} (概率: ${risks.projectRisks[0].probability}, 影响: ${risks.projectRisks[0].impact})\n\n` : '') +
				`# 资源需求\n` +
				`- 预算范围: ${resources.estimatedBudget}\n` +
				`- 硬件要求: ${resources.hardware.slice(0,2).join('; ')}\n`
		},
		async pollImageResult(taskId) {
			let attempts = 0
			const maxAttempts = 30
			
			const checkResult = async () => {
				try {
					const response = await uni.request({
						url: `https://dashscope.aliyuncs.com/api/v1/tasks/${taskId}`,
						method: 'GET',
						header: {
							'Authorization': 'Bearer YOUR_DASHSCOPE_API_KEY'
						}
					})
					
					if (response.statusCode === 200) {
						const status = response.data.output.task_status
						
						if (status === 'SUCCEEDED') {
							uni.hideLoading()
							this.generatedImageUrl = response.data.output.results[0].url
							uni.showToast({
								title: '图表生成成功',
								icon: 'success'
							})
							return
						} else if (status === 'FAILED') {
							uni.hideLoading()
							uni.showToast({
								title: '图表生成失败',
								icon: 'none'
							})
							return
						} else if (status === 'PENDING' || status === 'RUNNING') {
							attempts++
							if (attempts < maxAttempts) {
								setTimeout(checkResult, 5000)
							} else {
								uni.hideLoading()
								uni.showToast({
									title: '图表生成超时',
									icon: 'none'
								})
							}
						}
					}
				} catch (error) {
					uni.hideLoading()
					uni.showToast({
						title: '查询图表状态失败',
						icon: 'none'
					})
				}
			}
			
			checkResult()
		},
		previewImage() {
			if (this.generatedImageUrl) {
				uni.previewImage({
					urls: [this.generatedImageUrl],
					current: 0
				})
			}
		},
		regenerateChart() {
			this.generatedImageUrl = ''
			this.generateChart()
		},
		goBack() {
			uni.navigateBack({
				delta: 1
			})
		}
	}
}
</script>

<style scoped>
.container {
	min-height: 100vh;
	background: #e3f2fd;
	padding-bottom: 200rpx;
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

.form-section, .result-section {
	margin: 30rpx 20rpx;
	background: white;
	border-radius: 20rpx;
	overflow: hidden;
}

.section-header {
	background: linear-gradient(135deg, #2196F3, #42A5F5);
	padding: 30rpx;
	display: flex;
	align-items: center;
	gap: 15rpx;
}

.section-icon {
	font-size: 40rpx;
	width: 100rpx;
	height: 100rpx;
	border-radius: 30rpx;
	display: flex;
	align-items: center;
	justify-content: center;
}

.section-title {
	font-size: 32rpx;
	font-weight: bold;
	color: white;
}

.form-content {
	padding: 30rpx;
}

.form-item {
	margin-bottom: 30rpx;
}

.input-wrapper {
	position: relative;
}

.placeholder {
	position: absolute;
	left: 20rpx;
	top: 50%;
	transform: translateY(-50%);
	color: #999;
	font-size: 28rpx;
	pointer-events: none;
}

.form-label {
	display: block;
	font-size: 28rpx;
	color: #333;
	margin-bottom: 15rpx;
	font-weight: 500;
}

.form-input, .form-textarea {
	width: 100%;
	padding: 20rpx;
	border: 2rpx solid #e0e0e0;
	border-radius: 12rpx;
	font-size: 28rpx;
	background: #f8f9fa;
	box-sizing: border-box;
	height: 80rpx;
}

.form-input:focus, .form-textarea:focus {
	border-color: #2196F3;
	background: white;
}

.form-textarea {
	height: 120rpx;
	resize: none;
}

.form-actions {
	display: flex;
	gap: 20rpx;
	padding: 30rpx;
	background: #f8f9fa;
}

.btn-secondary, .btn-primary {
	flex: 1;
	padding: 25rpx;
	border-radius: 12rpx;
	font-size: 28rpx;
	font-weight: bold;
	border: none;
}

.btn-secondary {
	background: #e0e0e0;
	color: #666;
}

.btn-primary {
	background: #2196F3;
	color: white;
}

.result-content {
	padding: 30rpx;
}

.result-summary {
	display: flex;
	gap: 30rpx;
	margin-bottom: 40rpx;
}

.summary-item {
	flex: 1;
	text-align: center;
	padding: 30rpx;
	background: linear-gradient(135deg, #2196F3, #42A5F5);
	border-radius: 15rpx;
}

.summary-label {
	display: block;
	font-size: 24rpx;
	color: rgba(255, 255, 255, 0.8);
	margin-bottom: 10rpx;
}

.summary-score {
	display: block;
	font-size: 48rpx;
	font-weight: bold;
	color: white;
}

.summary-grade {
	display: block;
	font-size: 36rpx;
	font-weight: bold;
	color: white;
}

.evaluation-details {
	margin-bottom: 40rpx;
}

.details-title {
	display: block;
	font-size: 32rpx;
	font-weight: bold;
	color: #333;
	margin-bottom: 25rpx;
}

.detail-item {
	padding: 25rpx;
	background: #f8f9fa;
	border-radius: 12rpx;
	margin-bottom: 15rpx;
}

.detail-header {
	display: flex;
	align-items: center;
	gap: 15rpx;
	margin-bottom: 10rpx;
}

.detail-icon {
	font-size: 32rpx;
	width: 50rpx;
	height: 50rpx;
	border-radius: 25rpx;
	display: flex;
	align-items: center;
	justify-content: center;
}

.detail-icon.good {
	background: #e8f5e8;
}

.detail-icon.warning {
	background: #fff3e0;
}

.detail-icon.danger {
	background: #ffebee;
}

.detail-name {
	flex: 1;
	font-size: 28rpx;
	font-weight: 500;
	color: #333;
}

.detail-score {
	font-size: 28rpx;
	font-weight: bold;
	color: #2196F3;
}

.detail-desc {
	font-size: 24rpx;
	color: #666;
	line-height: 1.5;
}

.chart-section {
	background: #f8f9fa;
	border-radius: 15rpx;
	padding: 30rpx;
}

.chart-header {
	display: flex;
	flex-direction: column;
	align-items: center;
	margin-bottom: 30rpx;
}

.chart-icon {
	font-size: 50rpx;
	margin-bottom: 15rpx;
}

.chart-title {
	font-size: 32rpx;
	font-weight: bold;
	color: #333;
	margin-bottom: 8rpx;
}

.chart-subtitle {
	font-size: 24rpx;
	color: #666;
}

.chart-container {
	min-height: 300rpx;
}

.chart-placeholder {
	height: 300rpx;
	background: linear-gradient(135deg, #e3f2fd, #bbdefb);
	border-radius: 12rpx;
	display: flex;
	align-items: center;
	justify-content: center;
}

.chart-content {
	text-align: center;
}

.chart-text {
	display: block;
	font-size: 32rpx;
	font-weight: bold;
	color: #2196F3;
	margin-bottom: 10rpx;
}

.chart-desc {
	font-size: 24rpx;
	color: #666;
	margin-bottom: 20rpx;
}

.generate-chart-btn {
	padding: 20rpx 40rpx;
	background: #2196F3;
	color: white;
	border-radius: 25rpx;
	font-size: 26rpx;
	font-weight: bold;
	border: none;
	margin-top: 15rpx;
}

.chart-image-container {
	text-align: center;
}

.chart-image {
	width: 100%;
	max-height: 400rpx;
	border-radius: 12rpx;
	margin-bottom: 20rpx;
	box-shadow: 0 4rpx 12rpx rgba(0,0,0,0.1);
}

.image-actions {
	display: flex;
	gap: 15rpx;
	justify-content: center;
}

.image-btn {
	padding: 15rpx 30rpx;
	background: #f5f5f5;
	color: #333;
	border-radius: 20rpx;
	font-size: 24rpx;
	border: none;
}

.image-btn:first-child {
	background: #2196F3;
	color: white;
}

/* 加载动画样式 */
.loading-section {
	margin: 30rpx 20rpx;
	background: white;
	border-radius: 20rpx;
	padding: 60rpx 30rpx;
	text-align: center;
	box-shadow: 0 4rpx 20rpx rgba(33, 150, 243, 0.1);
}

.loading-container {
	display: flex;
	flex-direction: column;
	align-items: center;
	gap: 30rpx;
}

.loading-animation {
	display: flex;
	gap: 15rpx;
	margin-bottom: 20rpx;
}

.loading-circle {
	width: 20rpx;
	height: 20rpx;
	background: #2196F3;
	border-radius: 50%;
	animation: loading-bounce 1.4s ease-in-out infinite both;
}

.loading-circle:nth-child(1) {
	animation-delay: -0.32s;
}

.loading-circle:nth-child(2) {
	animation-delay: -0.16s;
}

.loading-circle:nth-child(3) {
	animation-delay: 0s;
}

@keyframes loading-bounce {
	0%, 80%, 100% {
		transform: scale(0);
	}
	40% {
		transform: scale(1);
	}
}

.loading-text {
	font-size: 32rpx;
	font-weight: bold;
	color: #2196F3;
	margin-bottom: 10rpx;
}

.loading-desc {
	font-size: 26rpx;
	color: #666;
	line-height: 1.5;
	margin-bottom: 30rpx;
}

.loading-progress {
	width: 100%;
	display: flex;
	flex-direction: column;
	align-items: center;
	gap: 15rpx;
}

.progress-bar {
	width: 80%;
	height: 8rpx;
	background: #e0e0e0;
	border-radius: 4rpx;
	overflow: hidden;
}

.progress-fill {
	height: 100%;
	background: linear-gradient(90deg, #2196F3, #42A5F5);
	border-radius: 4rpx;
	transition: width 0.3s ease;
}

.progress-text {
	font-size: 24rpx;
	color: #2196F3;
	font-weight: bold;
}

/* 详细评估信息样式 */
.detailed-info {
	margin-top: 40rpx;
}

.info-section {
	margin-bottom: 30rpx;
	padding: 25rpx;
	background: #f8f9fa;
	border-radius: 12rpx;
	border-left: 4rpx solid #2196F3;
}

.info-title {
	font-size: 30rpx;
	font-weight: bold;
	color: #333;
	margin-bottom: 20rpx;
	display: block;
}

.info-content {
	display: flex;
	flex-direction: column;
	gap: 15rpx;
}

.info-item {
	display: flex;
	align-items: center;
	gap: 15rpx;
}

.info-label {
	font-size: 26rpx;
	color: #666;
	font-weight: 500;
	min-width: 140rpx;
}

.info-value {
	flex: 1;
	font-size: 26rpx;
	color: #333;
	line-height: 1.4;
}

.team-item {
	padding: 20rpx;
	background: white;
	border-radius: 8rpx;
	margin-bottom: 15rpx;
}

.team-header {
	display: flex;
	justify-content: space-between;
	align-items: center;
	margin-bottom: 10rpx;
}

.team-position {
	font-size: 28rpx;
	font-weight: bold;
	color: #2196F3;
}

.team-count {
	font-size: 24rpx;
	color: #666;
	background: #e3f2fd;
	padding: 4rpx 12rpx;
	border-radius: 12rpx;
}

.team-responsibilities {
	font-size: 24rpx;
	color: #666;
	line-height: 1.4;
}

.risk-category {
	margin-bottom: 25rpx;
}

.risk-category-title {
	font-size: 26rpx;
	font-weight: bold;
	color: #ff9800;
	margin-bottom: 15rpx;
	display: block;
}

.risk-item {
	padding: 15rpx;
	background: white;
	border-radius: 8rpx;
	margin-bottom: 10rpx;
	border-left: 3rpx solid #ff9800;
}

.risk-desc {
	font-size: 26rpx;
	color: #333;
	margin-bottom: 8rpx;
	display: block;
}

.risk-level {
	display: flex;
	gap: 20rpx;
}

.risk-probability,
.risk-impact {
	font-size: 22rpx;
	color: #666;
	background: #fff3e0;
	padding: 4rpx 8rpx;
	border-radius: 8rpx;
}

.bottom-actions {
	position: fixed;
	bottom: 0;
	left: 0;
	right: 0;
	background: white;
	padding: 15rpx 10rpx;
	border-top: 1rpx solid #e0e0e0;
	display: grid;
	grid-template-columns: 1fr 1fr 1fr 1fr;
	gap: 8rpx;
}

.action-btn {
	padding: 20rpx 8rpx;
	background: #2196F3;
	color: white;
	border-radius: 12rpx;
	font-size: 22rpx;
	font-weight: bold;
	border: none;
	text-align: center;
}
</style>