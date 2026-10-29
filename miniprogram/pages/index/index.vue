<template>
	<view class="container">
		<!-- 自定义导航栏 -->
		<view class="custom-navbar fade-in-down" :style="{paddingTop: statusBarHeight + 'px'}">
			<view class="navbar-content">
				<text class="navbar-title">通信电子创新基地</text>
				<view class="navbar-right">
					<text class="weather">{{weather}}</text>
				</view>
			</view>
		</view>

		<!-- 欢迎横幅与天气信息 -->
		<view class="welcome-banner fade-in-up" style="animation-delay: 0.1s;">
			<view class="banner-left">
				<view class="banner-content">
					<text class="welcome-text">欢迎来到通信电子创新基地</text>
					<text class="welcome-subtitle">让实验室管理更智能</text>
				</view>
				<view class="weather-main">
					<text class="weather-desc">{{weatherData.weatherDesc}}</text>
					<text class="weather-temp">{{weatherData.temperature}}°C</text>
				</view>
			</view>
			<view class="banner-right">
				<image class="banner-icon floating" src="http://szzgurngv.hn-bkt.clouddn.com/icon/logo.png"></image>
				<view class="weather-info">
					<view class="weather-item">
						<text class="weather-label">湿度</text>
						<text class="weather-value">{{weatherData.humidity}}%</text>
					</view>
					<view class="weather-item">
						<text class="weather-label">风向</text>
						<text class="weather-value">{{weatherData.windDirection}}</text>
					</view>
					<view class="weather-item">
						<text class="weather-label">风力</text>
						<text class="weather-value">{{weatherData.windPower}}</text>
					</view>
				</view>
			</view>
		</view>

		<!-- 快速统计 -->
		<view class="stats-section fade-in-up" style="animation-delay: 0.2s;">
			<view class="stats-item scale-in" style="animation-delay: 0.3s;">
				<text class="stats-number">{{stats.availableDevices}}</text>
				<text class="stats-label">可用设备</text>
			</view>
			<view class="stats-item scale-in" style="animation-delay: 0.4s;">
				<text class="stats-number">{{stats.activeProjects}}</text>
				<text class="stats-label">进行项目</text>
			</view>
			<view class="stats-item scale-in" style="animation-delay: 0.5s;">
				<text class="stats-number">{{stats.safeDays}}</text>
				<text class="stats-label">安全天数</text>
			</view>
		</view>

		<!-- 智慧基地 -->
		<view class="modules-section fade-in-up" style="animation-delay: 0.3s;">
			<text class="section-title">智慧基地</text>
			<view class="modules-grid">
				<view class="module-item card-hover scale-in" style="animation-delay: 0.4s;" @click="navigateTo('/pages/device/device-selection')">
					<!-- <view class="module-icon project-icon">⏰</view> -->
					<image class="module-icon project-icon icon-bounce" src="http://szzgurngv.hn-bkt.clouddn.com/icon/yuyue.png" mode="aspectFit"></image>
					<text class="module-title">设备预约</text>
					<text class="module-desc">预约·管理·使用</text>
				</view>
				<view class="module-item card-hover scale-in" style="animation-delay: 0.5s;" @click="navigateTo('/pages/warehouse/warehouse')">
					<!-- <view class="module-icon device-icon">🏭</view> -->
					<image class="module-icon device-icon icon-bounce" src="http://szzgurngv.hn-bkt.clouddn.com/icon/cangku.png" mode="aspectFit"></image>
					<text class="module-title">元件仓库</text>
					<text class="module-desc">库存·申领·管理</text>
				</view>
				<view class="module-item card-hover scale-in" style="animation-delay: 0.6s;" @click="navigateTo('/pages/maintenance/maintenance')">
					<!-- <view class="module-icon safety-icon">🔧</view> -->
					<image class="module-icon safety-icon icon-bounce" src="http://szzgurngv.hn-bkt.clouddn.com/icon/weixiu.png" mode="aspectFit"></image>
					<text class="module-title">维保调度</text>
					<text class="module-desc">报修·工单·调度</text>
				</view>
				<view class="module-item card-hover scale-in" style="animation-delay: 0.7s;" @click="navigateTo('/pages/duty/duty')">
					<!-- <view class="module-icon duty-icon">👤</view> -->
					<image class="module-icon duty-icon icon-bounce" src="http://szzgurngv.hn-bkt.clouddn.com/icon/zhiban.png" mode="aspectFit"></image>
					<text class="module-title">人员值班</text>
					<text class="module-desc">排班·调度·管理</text>
				</view>

			</view>
		</view>

		<!-- AI赋能 -->
		<view class="ai-section fade-in-up" style="animation-delay: 0.4s;">
			<text class="section-title">AI赋能</text>
			<view class="ai-grid">
				<view class="ai-item card-hover scale-in" style="animation-delay: 0.5s;" @click="navigateToAIEvaluation()">
					<!-- <view class="ai-icon evaluation-icon">📊</view> -->
					<image class="ai-icon evaluation-icon icon-bounce" src="http://szzgurngv.hn-bkt.clouddn.com/icon/aipinggu.png" mode="aspectFit"></image>
					<text class="ai-title">AI项目评估</text>
					<text class="ai-desc">评价·分析·优化</text>
				</view>
				<view class="ai-item card-hover scale-in" style="animation-delay: 0.6s;" @click="navigateTo('/pages/ai/ai')">
					<!-- <view class="ai-icon assistant-icon">🤖</view> -->
					<image class="ai-icon assistant-icon icon-bounce" src="http://szzgurngv.hn-bkt.clouddn.com/icon/ai_logo.png" mode="aspectFit"></image>
					<text class="ai-title">AI智能助手</text>
					<text class="ai-desc">学习·问答·路径</text>
				</view>
				<view class="ai-item card-hover scale-in" style="animation-delay: 0.7s;" @click="navigateToAITalent()">
					<!-- <view class="ai-icon talent-icon">🎓</view> -->
					<image class="ai-icon talent-icon icon-bounce" src="http://szzgurngv.hn-bkt.clouddn.com/icon/aipeiyang.png" mode="aspectFit"></image>
					<text class="ai-title">AI人才培养</text>
					<text class="ai-desc">课程·实训·认证</text>
				</view>
			</view>
		</view>


	</view>
</template>

<script>
	export default {
		data() {
			return {
				statusBarHeight: 0,
				weather: '加载中...',
				weatherData: {
					temperature: '--',
					weatherDesc: '加载中',
					humidity: '--',
					windDirection: '--',
					windPower: '--'
				},
				stats: {
					availableDevices: 12,
					activeProjects: 8,
					safeDays: 365
				},
				weatherApiKey: '06ab3ec992282fb5e6d3c5bb9604af7f',
				cityCode: '360112', // 南昌市新建区城市编码
				weatherTimer: null // 天气刷新定时器
			}
		},
		onLoad() {
			// 获取状态栏高度
			const systemInfo = uni.getSystemInfoSync()
			this.statusBarHeight = systemInfo.statusBarHeight
			
			// 加载数据
			this.loadData()
			// 加载天气数据
			this.loadWeatherData()
			// 启动天气数据定时刷新（每10分钟刷新一次）
			this.startWeatherTimer()
		},
		
		onShow() {
			// 页面显示时刷新天气数据
			this.loadWeatherData()
			// 重新启动定时器
			this.startWeatherTimer()
		},
		
		onHide() {
			// 页面隐藏时清除定时器
			this.clearWeatherTimer()
		},
		
		onUnload() {
			// 页面卸载时清除定时器
			this.clearWeatherTimer()
		},
		methods: {
			loadData() {
				// 模拟加载统计数据
				// 实际项目中这里会调用API
				console.log('加载首页数据')
			},
			
			// 加载天气数据
			loadWeatherData() {
				uni.request({
					url: 'https://restapi.amap.com/v3/weather/weatherInfo',
					method: 'GET',
					data: {
						city: this.cityCode,
						key: this.weatherApiKey
					},
					success: (res) => {
						if (res.statusCode === 200 && res.data.status === '1') {
							const weatherInfo = res.data.lives[0]
							if (weatherInfo) {
								// 更新天气数据
								this.weatherData = {
									temperature: weatherInfo.temperature,
									weatherDesc: weatherInfo.weather,
									humidity: weatherInfo.humidity,
									windDirection: weatherInfo.winddirection,
									windPower: weatherInfo.windpower
								}
								
								// 更新导航栏显示的天气信息
								this.weather = `${weatherInfo.weather} ${weatherInfo.temperature}°C`
								
								console.log('天气数据加载成功:', this.weatherData)
							} else {
								this.handleWeatherError('天气数据格式错误')
							}
						} else {
							this.handleWeatherError('天气API返回错误: ' + (res.data.info || '未知错误'))
						}
					},
					fail: (err) => {
						console.error('天气API请求失败:', err)
						this.handleWeatherError('网络连接失败')
					}
				})
			},
			
			// 处理天气数据加载错误
			handleWeatherError(errorMsg) {
				console.error('天气数据加载失败:', errorMsg)
				this.weather = '天气获取失败'
				this.weatherData = {
					temperature: '--',
					weatherDesc: '获取失败',
					humidity: '--',
					windDirection: '--',
					windPower: '--'
				}
			},
			
			// 启动天气数据定时刷新
			startWeatherTimer() {
				// 先清除已存在的定时器
				this.clearWeatherTimer()
				// 设置新的定时器，每10分钟刷新一次天气数据
				this.weatherTimer = setInterval(() => {
					console.log('定时刷新天气数据')
					this.loadWeatherData()
				}, 10 * 60 * 1000) // 10分钟 = 10 * 60 * 1000毫秒
			},
			
			// 清除天气数据定时器
			clearWeatherTimer() {
				if (this.weatherTimer) {
					clearInterval(this.weatherTimer)
					this.weatherTimer = null
					console.log('天气定时器已清除')
				}
			},
			navigateTo(url) {
				uni.navigateTo({ url })
			},
			navigateToAITalent() {
				// AI人才培养页面
				uni.navigateTo({ 
					url: '/pages/ai/talent' 
				})
			},
			navigateToAIEvaluation() {
				// AI项目评估页面
				uni.navigateTo({ 
					url: '/pages/ai/evaluation' 
				})
			},
			showQRCode() {
				uni.showModal({
					title: '电子通行证',
					content: '功能开发中，敬请期待',
					showCancel: false
				})
			}
		}
	}
</script>

<style scoped>
	.container {
		min-height: 100vh;
		background: #e3f2fd;
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
		transform: translate3d(0, -3px, 0);
		box-shadow: 0 6px 15px rgba(0, 0, 0, 0.12);
	}

	.card-hover:active {
		transform: translate3d(0, -1px, 0) scale3d(0.98, 0.98, 1);
		transition: all 0.1s ease;
	}

	.custom-navbar {
		background: #2196F3;
		backdrop-filter: blur(10px);
	}

	.navbar-content {
		display: flex;
		justify-content: space-between;
		align-items: center;
		padding: 20rpx 30rpx;
	}

	.navbar-title {
		font-size: 36rpx;
		font-weight: bold;
		color: white;
	}

	.navbar-right {
		display: flex;
		flex-direction: column;
		align-items: flex-end;
	}

	.weather {
		color: white;
		font-size: 24rpx;
	}

	.welcome-banner {
		display: flex;
		justify-content: space-between;
		align-items: flex-start;
		padding: 40rpx 30rpx;
		margin: 20rpx;
		background: #2196F3;
		border-radius: 20rpx;
		backdrop-filter: blur(10px);
	}

	.banner-left {
		display: flex;
		flex-direction: column;
		flex: 1;
	}

	.banner-content {
		display: flex;
		flex-direction: column;
		margin-bottom: 20rpx;
	}

	.welcome-text {
		font-size: 36rpx;
		font-weight: bold;
		color: white;
		margin-bottom: 10rpx;
	}

	.welcome-subtitle {
		font-size: 32rpx;
		color: rgba(255, 255, 255, 0.8);
	}

	.weather-main {
		display: flex;
		align-items: center;
		gap: 15rpx;
	}

	.weather-desc {
		font-size: 48rpx;
		font-weight: bold;
		color: white;
	}

	.weather-temp {
		font-size: 48rpx;
		font-weight: bold;
		color: white;
	}

	.banner-right {
		display: flex;
		flex-direction: column;
		align-items: center;
		gap: 20rpx;
	}

	.banner-icon {
		width: 160rpx;
		height: 100rpx;
	}
	
	.weather-info {
		display: flex;
		gap: 20rpx;
	}
	
	.weather-item {
		display: flex;
		flex-direction: column;
		align-items: center;
		text-align: center;
	}
	
	.weather-label {
		font-size: 28rpx;
		color: rgba(255, 255, 255, 0.7);
		margin-bottom: 5rpx;
	}
	
	.weather-value {
		font-size: 25rpx;
		font-weight: bold;
		color: white;
	}

	.stats-section {
		display: flex;
		justify-content: space-around;
		padding: 30rpx;
		margin: 0 20rpx 20rpx;
		background: rgba(255, 255, 255, 0.9);
		border-radius: 20rpx;
	}

	.stats-item {
		display: flex;
		flex-direction: column;
		align-items: center;
	}

	.stats-number {
		font-size: 48rpx;
		font-weight: bold;
		color: #2196F3;
		margin-bottom: 10rpx;
	}

	.stats-label {
		font-size: 24rpx;
		color: #666;
	}

	.modules-section, .campus-section, .ai-section {
		padding: 0 20rpx 30rpx;
	}

	.section-title {
		font-size: 32rpx;
		font-weight: bold;
		color: black;
		margin-bottom: 20rpx;
		display: block;
	}

	.modules-grid {
		display: grid;
		grid-template-columns: 1fr 1fr;
		gap: 20rpx;
	}

	.ai-grid {
		display: grid;
		grid-template-columns: repeat(3, 1fr);
		gap: 15rpx;
	}

	.module-item, .ai-item {
		padding: 30rpx;
		background: rgba(255, 255, 255, 0.9);
		border-radius: 20rpx;
		display: flex;
		flex-direction: column;
		align-items: center;
		text-align: center;
	}

	.ai-item {
		padding: 25rpx 15rpx;
	}

	.module-icon, .ai-icon {
		font-size: 60rpx;
		margin-bottom: 15rpx;
		width: 80rpx;
		height: 80rpx;
		display: flex;
		align-items: center;
		justify-content: center;
	}

	.ai-icon {
		font-size: 45rpx;
		width: 80rpx;
		height: 80rpx;
		margin-bottom: 10rpx;
	}

	.device-icon { background: #ffffff; }
	.project-icon { background: #ffffff; }
	.safety-icon { background: #ffffff; }
	.ai-icon { background: #ffffff; }
	.campus-icon { background: #ffffff; }
	.evaluation-icon { background: #ffffff; }
	.assistant-icon { background: #ffffff; }
	.talent-icon { background: #ffffff; }

	.module-title, .ai-title {
		font-size: 28rpx;
		font-weight: bold;
		color: #333;
		margin-bottom: 8rpx;
	}

	.ai-title {
		font-size: 24rpx;
		margin-bottom: 5rpx;
	}

	.module-desc, .ai-desc {
		font-size: 22rpx;
		color: #666;
	}

	.ai-desc {
		font-size: 20rpx;
	}

	.campus-grid {
		display: grid;
		grid-template-columns: repeat(4, 1fr);
		gap: 15rpx;
	}

	.campus-item {
		padding: 20rpx;
		background: rgba(255, 255, 255, 0.9);
		border-radius: 15rpx;
		display: flex;
		flex-direction: column;
		align-items: center;
		text-align: center;
	}

	.campus-icon {
		font-size: 40rpx;
		margin-bottom: 10rpx;
	}

	.campus-title {
		font-size: 22rpx;
		color: #333;
	}


</style>
