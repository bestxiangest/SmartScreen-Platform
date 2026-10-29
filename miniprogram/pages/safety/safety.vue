<template>
	<view class="container">
		<!-- 安全提醒 -->
		<view class="safety-tips fade-in-down">
			<view class="section-header">
				<text class="section-title">安全提醒</text>
			</view>
			<view class="tips-list">
				<view class="tip-item card-hover fade-in-up" v-for="(tip, index) in safetyTips" :key="tip.id" :style="{animationDelay: (0.1 + index * 0.1) + 's'}">
			<image class="tip-icon icon-bounce" :src="tip.icon" mode="aspectFit"></image>
			<text class="tip-text">{{tip.content}}</text>
		</view>
			</view>
		</view>

		<!-- 监控区域 -->
		<view class="monitoring-section fade-in-up" style="animation-delay: 0.3s;">
			<view class="section-header">
				<text class="section-title">监控区域</text>
				<view class="view-mode">
					<image class="mode-btn btn-hover" 
					:class="{active: viewMode === 'grid'}"
					@click="viewMode = 'grid'"
					src="http://szzgurngv.hn-bkt.clouddn.com/icon/wangge.png" mode="aspectFit"></image>
				<image class="mode-btn btn-hover" 
					:class="{active: viewMode === 'list'}"
					@click="viewMode = 'list'"
					src="http://szzgurngv.hn-bkt.clouddn.com/icon/mode_liemiao.png" mode="aspectFit"></image>
				</view>
			</view>

			<!-- 网格视图 -->
			<view v-if="viewMode === 'grid'" class="grid-view">
				<view class="area-card card-hover scale-in" 
					v-for="(area, index) in monitoringAreas" 
					:key="area.id"
					:style="{animationDelay: (0.5 + index * 0.1) + 's'}"
					@click="viewAreaDetail(area)">
					<view class="area-header">
						<text class="area-name">{{area.name}}</text>
						<view class="area-status" :class="area.status">
							<text class="status-dot"></text>
						</view>
					</view>
					<view class="area-preview">
						<!-- 主试验室显示视频流 -->
						<image v-if="area.id === 1" 
							:src="videoStreamUrl" 
							mode="widthFix" 
							style="width: 100%;"
							@load="onImageLoad"
							@error="onImageError">
						</image>
						<!-- 焊接室显示静态图片 -->
						<image v-else 
							src="http://szzgurngv.hn-bkt.clouddn.com/icon/name.png" 
							mode="aspectFill"
							style="width: 100%; height: 100%; border-radius: 8px;">
						</image>
					</view>
					<view class="area-info">
						<view class="info-item">
						<image class="info-icon" src="http://szzgurngv.hn-bkt.clouddn.com/icon/onlinepp.png" mode="aspectFit"></image>
						<text>{{area.peopleCount}}人在线</text>
					</view>
					<view class="info-item">
						<image class="info-icon" src="http://szzgurngv.hn-bkt.clouddn.com/icon/temp.png" mode="aspectFit"></image>
						<text>{{area.temperature}}°C</text>
					</view>
					</view>
				</view>
			</view>

			<!-- 列表视图 -->
			<view v-else class="list-view">
				<view class="area-item card-hover fade-in-up" 
					v-for="(area, index) in monitoringAreas" 
					:key="area.id"
					:style="{animationDelay: (0.5 + index * 0.1) + 's'}"
					@click="viewAreaDetail(area)">
					<!-- 主试验室显示视频流 -->
					<image v-if="area.id === 1" 
						:src="videoStreamUrl" 
						mode="widthFix" 
						style="width: 100%;"
						@load="onImageLoad"
						@error="onImageError">
					</image>
					<!-- 焊接室显示静态图片 -->
					<image v-else 
						src="http://szzgurngv.hn-bkt.clouddn.com/icon/name.png" 
						mode="aspectFill"
						style="width: 100%; height: 100%; border-radius: 8px;">
					</image>
					<view class="area-details">
						<view class="detail-header">
							<text class="area-name">{{area.name}}</text>
						</view>
						<view class="detail-stats">
							<view class="stat-text">
							<image class="stat-icon-small" src="http://szzgurngv.hn-bkt.clouddn.com/icon/user.png" mode="aspectFit"></image>
						</view>
						<view class="stat-text">
							<image class="stat-icon-small" src="http://szzgurngv.hn-bkt.clouddn.com/icon/onlinepp.png" mode="aspectFit"></image>
							<text>{{area.peopleCount}}人在线</text>
						</view>
						<view class="stat-text">
							<image class="stat-icon-small" src="http://szzgurngv.hn-bkt.clouddn.com/icon/temp.png" mode="aspectFit"></image>
							<text>{{area.temperature}}°C</text>
						</view>
						</view>
						<text class="area-description">{{area.description}}</text>
					</view>
				</view>
			</view>
		</view>



		<!-- 环境监测 -->
		<view class="environment-section fade-in-up" style="animation-delay: 0.7s;">
			<view class="section-header">
				<text class="section-title">环境监测</text>
				<image class="refresh-btn icon-bounce card-hover" src="http://szzgurngv.hn-bkt.clouddn.com/icon/shuaxin.png" mode="aspectFit" @click="refreshEnvironment"></image>
			</view>

			<!-- 区域切换按钮 -->
			<view class="area-switch">
				<view class="switch-btn btn-hover" 
					:class="{active: currentEnvArea === 'main'}"
					@click="switchEnvArea('main')">
					<text class="switch-text">主试验室</text>
				</view>
				<view class="switch-btn btn-hover" 
					:class="{active: currentEnvArea === 'welding'}"
					@click="switchEnvArea('welding')">
					<text class="switch-text">焊接室</text>
				</view>
			</view>

			<view class="environment-grid">
				<view class="env-card card-hover scale-in" 
				v-for="(env, index) in currentEnvironmentData" 
				:key="env.type"
				:class="env.status"
				:style="{animationDelay: (0.9 + index * 0.1) + 's'}">
				<view class="env-header">
			<image class="env-icon icon-bounce" :src="env.icon" mode="aspectFit"></image>
			<text class="env-name">{{env.name}}</text>
		</view>
				<view class="env-value-unit">
					<text class="env-value floating">{{env.value}}</text>
					<text class="env-unit">{{env.unit}}</text>
				</view>
				<view class="env-range">
					<text class="range-text">正常范围：{{env.normalRange}}</text>
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
					viewMode: 'grid',
					lastUpdateTime: '刚刚',
					currentEnvArea: 'main', // 当前选中的环境监测区域
					videoStreamUrl: 'http://192.168.43.95:81/stream', // 视频流地址
				monitoringAreas: [
					{
						id: 1,
						name: '主试验室',
						status: 'safe',
						cameraCount: 4,
						peopleCount: 12,
						temperature: 22,
						description: '主要实验区域，配备完整的安全监控设备'
					},
					{
						id: 2,
						name: '焊接室',
						status: 'safe',
						cameraCount: 3,
						peopleCount: 6,
						temperature: 24,
						description: '焊接作业专用区域，配备完整的安全防护设备'
					}
				],
				
				environmentData: {
				main: [
					{
						type: 'temperature',
						name: '温度',
						icon: 'http://szzgurngv.hn-bkt.clouddn.com/icon/temp.png',
						value: 23.5,
						unit: '°C',
						normalRange: '18-26°C',
						status: 'normal'
					},
					{
						type: 'humidity',
						name: '湿度',
						icon: 'http://szzgurngv.hn-bkt.clouddn.com/icon/humidity.png',
						value: 45,
						unit: '%',
						normalRange: '40-60%',
						status: 'normal'
					},
					{
						type: 'air_quality',
						name: '空气质量',
						icon: 'http://szzgurngv.hn-bkt.clouddn.com/icon/kongqi.png',
						value: 35,
						unit: 'AQI',
						normalRange: '0-50',
						status: 'normal'
					},
					{
						type: 'noise',
						name: '噪音',
						icon: 'http://szzgurngv.hn-bkt.clouddn.com/icon/zaoyin.png',
						value: 42,
						unit: 'dB',
						normalRange: '<50dB',
						status: 'normal'
					},
					{
						type: 'light',
						name: '光照',
						icon: 'http://szzgurngv.hn-bkt.clouddn.com/icon/guang.png',
						value: 450,
						unit: 'lux',
						normalRange: '300-500lux',
						status: 'normal'
					},
					{
						type: 'gas',
						name: '有害气体',
						icon: 'http://szzgurngv.hn-bkt.clouddn.com/icon/qiti.png',
						value: 0.02,
						unit: 'ppm',
						normalRange: '<0.1ppm',
						status: 'normal'
					}
				],
				welding: [
					{
						type: 'temperature',
						name: '温度',
						icon: 'http://szzgurngv.hn-bkt.clouddn.com/icon/temp.png',
						value: 28.2,
						unit: '°C',
						normalRange: '18-30°C',
						status: 'normal'
					},
					{
						type: 'humidity',
						name: '湿度',
						icon: 'http://szzgurngv.hn-bkt.clouddn.com/icon/humidity.png',
						value: 38,
						unit: '%',
						normalRange: '30-50%',
						status: 'normal'
					},
					{
						type: 'air_quality',
						name: '空气质量',
						icon: 'http://szzgurngv.hn-bkt.clouddn.com/icon/kongqi.png',
						value: 42,
						unit: 'AQI',
						normalRange: '0-60',
						status: 'normal'
					},
					{
						type: 'noise',
						name: '噪音',
						icon: 'http://szzgurngv.hn-bkt.clouddn.com/icon/zaoyin.png',
						value: 65,
						unit: 'dB',
						normalRange: '<70dB',
						status: 'normal'
					},
					{
						type: 'light',
						name: '光照',
						icon: 'http://szzgurngv.hn-bkt.clouddn.com/icon/guang.png',
						value: 380,
						unit: 'lux',
						normalRange: '200-400lux',
						status: 'normal'
					},
					{
						type: 'gas',
						name: '有害气体',
						icon: 'http://szzgurngv.hn-bkt.clouddn.com/icon/qiti.png',
						value: 0.08,
						unit: 'ppm',
						normalRange: '<0.15ppm',
						status: 'normal'
					}
				]
			},
				safetyTips: [
				{
					id: 1,
					icon: 'http://szzgurngv.hn-bkt.clouddn.com/icon/qiti.png',
					content: '进入实验室前请佩戴好防护用品'
				},
				{
					id: 2,
					icon: 'http://szzgurngv.hn-bkt.clouddn.com/icon/qiti.png',
					content: '化学实验室禁止明火，注意通风'
				},
				{
					id: 3,
					icon: 'http://szzgurngv.hn-bkt.clouddn.com/icon/AI.png',
					content: '离开实验室时请确保门窗关闭'
				}
			]
			}
		},
		computed: {
			currentEnvironmentData() {
				return this.environmentData[this.currentEnvArea] || this.environmentData.main
			}
		},
		methods: {
			getStatusText(status) {
				const statusMap = {
					safe: '安全',
					warning: '警告',
					danger: '危险'
				}
				return statusMap[status] || '未知'
			},
			switchEnvArea(area) {
				this.currentEnvArea = area
			},
			
			viewAreaDetail(area) {
				uni.showModal({
					title: '区域详情',
					content: `查看${area.name}的详细监控信息\n\n人员在线：${area.peopleCount}人\n当前温度：${area.temperature}°C\n区域描述：${area.description}`,
					showCancel: false,
					confirmText: '确定'
				})
			},
			
			refreshEnvironment() {
				uni.showLoading({
					title: '刷新中...'
				})
				
				setTimeout(() => {
					uni.hideLoading()
					this.lastUpdateTime = '刚刚'
					uni.showToast({
						title: '数据已更新',
						icon: 'success'
					})
				}, 1500)
			},
			
			// 图片/视频流调试事件处理函数
			onImageLoad(e) {
				console.log('✅ 视频流图片加载成功:', {
					timestamp: new Date().toLocaleTimeString(),
					streamUrl: this.videoStreamUrl,
					event: e
				})
			},
			
			onImageError(e) {
				console.error('❌ 视频流图片加载错误:', {
					timestamp: new Date().toLocaleTimeString(),
					streamUrl: this.videoStreamUrl,
					error: e.detail,
					event: e
				})
				
				// 测试网络连接
				this.testNetworkConnection()
			},
			
			// 测试网络连接
			testNetworkConnection() {
				console.log('🌐 开始测试网络连接:', this.videoStreamUrl)
				
				uni.request({
					url: this.videoStreamUrl,
					method: 'GET',
					timeout: 10000,
					success: (res) => {
						console.log('✅ 网络连接测试成功:', {
							statusCode: res.statusCode,
							headers: res.header,
							url: this.videoStreamUrl
						})
					},
					fail: (err) => {
						console.error('❌ 网络连接测试失败:', {
							error: err,
							url: this.videoStreamUrl
						})
					}
				})
			}
		},
		onLoad() {
			// 打印初始化信息
			console.log('🚀 安全监控页面初始化:', {
				timestamp: new Date().toLocaleTimeString(),
				streamUrl: this.videoStreamUrl,
				monitoringAreas: this.monitoringAreas,
				note: '主实验室使用image组件显示视频流'
			})
			
			// 测试视频流URL可访问性
			this.testNetworkConnection()
			
			// 模拟实时更新
			this.updateTimer = setInterval(() => {
				const now = new Date()
				const minutes = now.getMinutes()
				this.lastUpdateTime = `${minutes}分钟前`
			}, 60000)
		},
		onUnload() {
			if (this.updateTimer) {
				clearInterval(this.updateTimer)
			}
		}
	}
</script>

<style scoped>
	.container {
		padding: 20rpx;
		background: #e3f2fd;
		min-height: 100vh;
	}

	

	.monitoring-section, .environment-section, .safety-tips {
		margin-bottom: 30rpx;
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

	.safety-tips .section-title {
		color: white;
		font-size: 36rpx;
		font-weight: bold;
		text-shadow: 0 2rpx 4rpx rgba(0, 0, 0, 0.3);
		text-align: center;
	}

	.safety-tips .section-header {
		margin-bottom: 25rpx;
		text-align: center;
	}

	.view-mode {
		display: flex;
		gap: 10rpx;
	}

	.mode-btn {
		width: 60rpx;
		height: 60rpx;
		display: flex;
		align-items: center;
		justify-content: center;
		background: #f0f0f0;
		border-radius: 10rpx;
		color: #666;
		transition: all 0.3s;
	}

	.mode-btn.active {
		background: #2196F3;
		color: white;
	}

	.view-all {
		font-size: 26rpx;
		color: #2196F3;
		padding: 10rpx;
	}

	.refresh-btn {
		width: 45rpx;
		height: 45rpx;
		padding: 10rpx;
	}

	.area-switch {
		display: flex;
		gap: 15rpx;
		margin-bottom: 20rpx;
		justify-content: center;
	}

	.switch-btn {
		padding: 15rpx 30rpx;
		background: #f0f0f0;
		border-radius: 25rpx;
		transition: all 0.3s;
		flex: 1;
		max-width: 200rpx;
	}

	.switch-btn.active {
		background: #2196F3;
	}

	.switch-text {
		font-size: 26rpx;
		color: #666;
		text-align: center;
		display: block;
	}

	.switch-btn.active .switch-text {
		color: white;
	}

	.grid-view {
		display: grid;
		grid-template-columns: repeat(2, 1fr);
		gap: 15rpx;
	}

	.area-card {
		background: white;
		border-radius: 15rpx;
		padding: 20rpx;
		overflow: hidden;
	}

	.area-header {
		display: flex;
		justify-content: space-between;
		align-items: center;
		margin-bottom: 15rpx;
	}

	.area-name {
		font-size: 26rpx;
		font-weight: bold;
		color: #333;
	}





	.area-preview {
		position: relative;
		margin-bottom: 15rpx;
	}



	.camera-count {
		position: absolute;
		top: 10rpx;
		right: 10rpx;
		padding: 8rpx 12rpx;
		background: rgba(0, 0, 0, 0.7);
		border-radius: 15rpx;
	}

	.count-text {
		font-size: 20rpx;
		color: white;
	}

	.area-info {
		display: flex;
		justify-content: space-between;
	}

	.info-item {
		font-size: 22rpx;
		color: #666;
		display: flex;
		align-items: center;
		gap: 8rpx;
	}

	.info-icon {
		width: 22rpx;
		height: 22rpx;
	}

	.list-view {
		display: flex;
		flex-direction: column;
		gap: 15rpx;
	}

	.area-item {
		background: white;
		border-radius: 15rpx;
		padding: 20rpx;
		display: flex;
		align-items: center;
		gap: 20rpx;
	}

	.area-thumbnail {
		width: 120rpx;
		height: 120rpx;
		border-radius: 10rpx;
		background: #f0f0f0;
	}

	.area-details {
		flex: 1;
		display: flex;
		flex-direction: column;
		gap: 10rpx;
	}

	.detail-header {
		display: flex;
		justify-content: space-between;
		align-items: center;
	}

	.detail-stats {
		display: flex;
		gap: 20rpx;
	}

	.stat-text {
		font-size: 22rpx;
		color: #666;
		display: flex;
		align-items: center;
		gap: 8rpx;
	}

	.stat-icon-small {
		width: 20rpx;
		height: 20rpx;
	}

	.area-description {
		font-size: 24rpx;
		color: #999;
		line-height: 1.4;
		display: block;
	}



	

	

	.environment-grid {
		display: grid;
		grid-template-columns: repeat(2, 1fr);
		gap: 15rpx;
	}

	.env-card {
		padding: 25rpx;
		background: white;
		border-radius: 15rpx;
		border-left: 6rpx solid #4CAF50;
	}

	.env-card.warning {
		border-left-color: #FF9800;
	}

	.env-card.danger {
		border-left-color: #f44336;
	}

	.env-header {
		display: flex;
		align-items: center;
		gap: 15rpx;
		margin-bottom: 15rpx;
	}

	.env-icon {
		width: 45rpx;
		height: 45rpx;
	}

	.env-name {
		font-size: 24rpx;
		color: #666;
	}

	.env-value-unit {
		display: flex;
		align-items: baseline;
		gap: 5rpx;
		margin-bottom: 10rpx;
	}

	.env-value {
		font-size: 36rpx;
		font-weight: bold;
		color: #333;
	}

	.env-unit {
		font-size: 22rpx;
		color: #999;
	}

	.env-range {
		padding: 8rpx 12rpx;
		background: #f8f9fa;
		border-radius: 8rpx;
	}

	.range-text {
		font-size: 20rpx;
		color: #666;
	}

	.safety-tips {
		background: linear-gradient(135deg, #ff6b6b, #ee5a24);
		border-radius: 20rpx;
		padding: 30rpx;
		box-shadow: 0 8rpx 25rpx rgba(255, 107, 107, 0.3);
		border: 3rpx solid #ff4757;
	}

	.tips-list {
		display: flex;
		flex-direction: column;
		gap: 20rpx;
	}

	.tip-item {
		display: flex;
		flex-direction: column;
		align-items: center;
		text-align: center;
		height: 40rpx;
		gap: 15rpx;
		padding: 25rpx 20rpx;
		background: rgba(255, 255, 255, 0.95);
		border-radius: 15rpx;
		border: 2rpx solid #ff6b6b;
		box-shadow: 0 4rpx 15rpx rgba(0, 0, 0, 0.1);
	}

	.tip-icon {
		width: 40rpx;
		height: 40rpx;
		filter: drop-shadow(0 2rpx 4rpx rgba(255, 107, 107, 0.3));
	}

	.tip-text {
		position: relative;
		top: -15rpx;
		font-size: 30rpx;
		color: #2c3e50;
		line-height: 1.5;
		font-weight: 500;
		text-align: center;
		width: 100%;
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