<template>
	<view class="container">
		<!-- 自定义导航栏 -->
		<view class="custom-navbar fade-in-down">
			<view class="navbar-left">
				<image class="back-icon card-hover" src="http://szzgurngv.hn-bkt.clouddn.com/icon/back.png" @click="goBack" mode="aspectFit"></image>
			</view>
			<text class="navbar-title">电子元件库</text>
			<view class="navbar-right">
			</view>
		</view>

		<!-- 库存卡片组 -->
		<view class="stats-section">
			<view class="stats-item card-hover scale-in" style="animation-delay: 0.1s;">
				<!-- <view class="stats-icon">📅</view> -->
				<image class="stats-icon icon-bounce" src="http://szzgurngv.hn-bkt.clouddn.com/icon/day.png" mode="aspectFit"></image>
				<view class="stats-content">
					<text class="stats-number">15</text>
					<text class="stats-label">日出库</text>
				</view>
			</view>
			<view class="stats-item card-hover scale-in" style="animation-delay: 0.2s;">
				<!-- <view class="stats-icon">📦</view> -->
				<image class="stats-icon icon-bounce" src="http://szzgurngv.hn-bkt.clouddn.com/icon/week.png" mode="aspectFit"></image>
				<view class="stats-content">
					<text class="stats-number">243</text>
					<text class="stats-label">周出库</text>
				</view>
			</view>
			<view class="stats-item card-hover scale-in" style="animation-delay: 0.3s;">
				<!-- <view class="stats-icon">📊</view> -->
				<image class="stats-icon icon-bounce" src="http://szzgurngv.hn-bkt.clouddn.com/icon/month.png" mode="aspectFit"></image>
				<view class="stats-content">
					<text class="stats-number">1056</text>
					<text class="stats-label">月出库</text>
				</view>
			</view>
		</view>

		<!-- 功能区 -->
		<view class="function-section">
			<view class="function-item card-hover scale-in" style="animation-delay: 0.4s;" @click="navigateTo('/pages/warehouse/category')">
				<!-- <view class="function-icon">📂</view> -->
				<image class="function-icon icon-bounce" src="http://szzgurngv.hn-bkt.clouddn.com/icon/fenlei.png" mode="aspectFit"></image>
				<text class="function-title">分类</text>
			</view>
			<view class="function-item card-hover scale-in" style="animation-delay: 0.5s;" @click="showApplyProcess">
				<!-- <view class="function-icon">📥</view> -->
				<image class="function-icon icon-bounce" src="http://szzgurngv.hn-bkt.clouddn.com/icon/shenling.png" mode="aspectFit"></image>
				<text class="function-title">申领</text>
			</view>
		</view>

		<!-- 搜索栏 -->
		<view class="search-section fade-in-up" style="animation-delay: 0.6s;">
			<view class="search-bar">
				<input class="search-input" placeholder="搜索物料编码或名称" v-model="searchKeyword" @input="onSearch" />
				<view class="search-icon">🔍</view>
			</view>
		</view>

		<!-- 数据表格 -->
		<view class="table-section fade-in-up" style="animation-delay: 0.7s;">
			<view class="table-header">
				<text class="header-cell material-code">物料编码</text>
				<text class="header-cell material-name">名称</text>
				<text class="header-cell stock">库存</text>
				<text class="header-cell location">位置</text>
				<text class="header-cell actions">操作</text>
			</view>
			<view class="table-body">
				<view class="table-row card-hover fade-in-up" :class="{warning: item.stock < item.safeStock}" v-for="(item, index) in filteredMaterials" :key="index" :style="{animationDelay: (0.8 + index * 0.05) + 's'}">
					<text class="table-cell material-code">{{item.code}}</text>
					<text class="table-cell material-name">{{item.name}}</text>
					<text class="table-cell stock">{{item.stock}}</text>
					<text class="table-cell location">{{item.location}}</text>
					<view class="table-cell actions">
						<button class="action-btn apply-btn btn-hover" @click="applyMaterial(item)">申领</button>
						<button class="action-btn detail-btn btn-hover" @click="viewDetail(item)">详情</button>
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
			statusBarHeight: 0,
			searchKeyword: '',
			materials: this.getAllComponents()
		}
	},
	computed: {
		filteredMaterials() {
			if (!this.searchKeyword) {
				return this.materials
			}
			return this.materials.filter(item => 
				item.code.toLowerCase().includes(this.searchKeyword.toLowerCase()) ||
				item.name.toLowerCase().includes(this.searchKeyword.toLowerCase())
			)
		}
	},
	onLoad() {
		const systemInfo = uni.getSystemInfoSync()
		this.statusBarHeight = systemInfo.statusBarHeight
	},
	methods: {
		getAllComponents() {
			const components = {
				resistor: [
					{ model: 'R-1K-1/4W', description: '碳膜电阻 1KΩ 1/4W', stock: 500 },
					{ model: 'R-10K-1/4W', description: '碳膜电阻 10KΩ 1/4W', stock: 300 },
					{ model: 'R-100R-1/4W', description: '碳膜电阻 100Ω 1/4W', stock: 200 },
					{ model: 'R-4.7K-1/4W', description: '碳膜电阻 4.7KΩ 1/4W', stock: 250 },
					{ model: 'R-220R-1/4W', description: '碳膜电阻 220Ω 1/4W', stock: 180 },
					{ model: 'RV-10K', description: '可变电阻 10KΩ', stock: 50 },
					{ model: 'R-ARRAY-8x10K', description: '排阻 8路 10KΩ', stock: 30 },
					{ model: 'R-PRECISION-1K-1%', description: '精密电阻 1KΩ 1%', stock: 100 }
				],
				semiconductor: [
					{ model: 'LED-RED-5mm', description: 'LED红色 5mm', stock: 200 },
					{ model: 'LED-BLUE-5mm', description: 'LED蓝色 5mm', stock: 150 },
					{ model: 'LED-GREEN-5mm', description: 'LED绿色 5mm', stock: 180 },
					{ model: 'LED-WHITE-5mm', description: 'LED白色 5mm', stock: 120 },
					{ model: 'DIODE-1N4007', description: '整流二极管 1N4007', stock: 300 },
					{ model: 'DIODE-1N4148', description: '开关二极管 1N4148', stock: 250 },
					{ model: 'TRANSISTOR-2N2222', description: 'NPN三极管 2N2222', stock: 100 },
					{ model: 'TRANSISTOR-2N3906', description: 'PNP三极管 2N3906', stock: 80 },
					{ model: 'MOSFET-IRF540N', description: 'N沟道MOSFET IRF540N', stock: 40 },
					{ model: 'IC-555', description: '定时器IC 555', stock: 60 },
					{ model: 'IC-LM358', description: '运算放大器 LM358', stock: 45 },
					{ model: 'IC-74HC00', description: '四路与非门 74HC00', stock: 35 },
					{ model: 'REGULATOR-7805', description: '稳压器 7805 5V', stock: 25 },
					{ model: 'REGULATOR-LM317', description: '可调稳压器 LM317', stock: 20 },
					{ model: 'ATmega328P', description: '8位微控制器', stock: 15 },
					{ model: '74HC595', description: '8位移位寄存器', stock: 30 }
				],
				mechanical: [
					{ model: 'SW-PB-6x6', description: '轻触开关 6x6mm', stock: 200 },
					{ model: 'SW-Toggle-SPDT', description: '拨动开关 单刀双掷', stock: 50 },
					{ model: 'Relay-5V-10A', description: '继电器 5V线圈 10A触点', stock: 25 },
					{ model: 'USB-A-Female', description: 'USB-A母座', stock: 30 },
					{ model: 'HDMI-Female', description: 'HDMI母座', stock: 15 },
					{ model: 'Terminal-2P', description: '接线端子 2位', stock: 100 },
					{ model: 'Header-2.54-40P', description: '排针 2.54mm 40针', stock: 80 },
					{ model: 'Jumper-Wire-M2M', description: '杜邦线 公对公', stock: 500 },
					{ model: 'Motor-DC-3V', description: '直流电机 3V', stock: 20 },
					{ model: 'Servo-SG90', description: '舵机 SG90', stock: 15 }
				],
				control: [
					{ model: 'Arduino-Uno-R3', description: 'Arduino开发板 UNO R3', stock: 10 },
					{ model: 'ESP32-DevKit', description: 'ESP32开发板', stock: 8 },
					{ model: 'Raspberry-Pi-4B', description: '树莓派4B 4GB', stock: 5 },
					{ model: 'DHT22', description: '温湿度传感器', stock: 25 },
					{ model: 'DS18B20', description: '数字温度传感器', stock: 30 },
					{ model: 'MQ-2', description: '可燃气体传感器', stock: 20 },
					{ model: 'HC-SR04', description: '超声波距离传感器', stock: 35 },
					{ model: 'MPU6050', description: '六轴陀螺仪加速度计', stock: 15 },
					{ model: 'Photoresistor', description: '光敏电阻', stock: 50 },
					{ model: 'PIR-HC-SR501', description: '人体红外传感器', stock: 18 }
				],
				display: [
					{ model: 'LCD1602', description: 'LCD液晶显示屏 16x2', stock: 20 },
					{ model: 'OLED-0.96', description: 'OLED显示屏 0.96寸', stock: 15 },
					{ model: 'TFT-2.4', description: 'TFT彩屏 2.4寸', stock: 8 },
					{ model: '7Seg-4Digit', description: '四位数码管', stock: 25 },
					{ model: 'LED-Matrix-8x8', description: 'LED点阵 8x8', stock: 12 },
					{ model: 'Laser-Diode-650nm', description: '激光二极管 650nm', stock: 10 },
					{ model: 'Photodiode-BPW34', description: '光电二极管 BPW34', stock: 30 },
					{ model: 'Fiber-Optic-Cable', description: '光纤传感器线缆', stock: 5 }
				],
				materials: [
					{ model: 'PCB-FR4-10x10', description: 'PCB基板 FR4 10x10cm', stock: 50 },
					{ model: 'PCB-Rogers-5x5', description: '高频基板 Rogers 5x5cm', stock: 20 },
					{ model: 'Wire-AWG22', description: '导线 AWG22', stock: 1000 },
					{ model: 'Wire-AWG24', description: '导线 AWG24', stock: 800 },
					{ model: 'Breadboard-830', description: '面包板 830孔', stock: 30 },
					{ model: 'Perfboard-5x7', description: '万能板 5x7cm', stock: 40 },
					{ model: 'Solder-Wire-0.8', description: '焊锡丝 0.8mm', stock: 25 },
					{ model: 'Flux-Paste', description: '助焊膏', stock: 15 },
					{ model: 'Heat-Shrink-Tube', description: '热缩管 混合装', stock: 100 },
					{ model: 'Silicone-Wire', description: '硅胶线 混合色', stock: 200 }
				]
			}
			
			const allMaterials = []
			let codeCounter = 1
			
			Object.keys(components).forEach(category => {
				components[category].forEach(item => {
					allMaterials.push({
						code: `${category.toUpperCase().substring(0,3)}${String(codeCounter).padStart(3, '0')}`,
						name: `${item.model} - ${item.description}`,
						stock: item.stock,
						safeStock: Math.floor(item.stock * 0.3),
						location: `${category.substring(0,1).toUpperCase()}${Math.floor(codeCounter/10)+1}-${String(codeCounter%10).padStart(2, '0')}`
					})
					codeCounter++
				})
			})
			
			return allMaterials
		},
		goBack() {
			uni.navigateBack()
		},
		navigateTo(url) {
			uni.navigateTo({ url })
		},
		showApplyProcess() {
			uni.showModal({
				title: '申领流程说明',
				content: '点击物料列表中的"申领"按钮可查看详细申领流程图并提交申请',
				showCancel: false,
				confirmText: '知道了'
			})
		},
		onSearch() {
			// 搜索逻辑已在computed中实现
		},
		applyMaterial(item) {
			// 跳转到申领流程页面
			uni.navigateTo({
				url: `/pages/warehouse/apply-process?code=${item.code}&name=${encodeURIComponent(item.name)}`
			})
		},
		viewDetail(item) {
			uni.navigateTo({
				url: `/pages/warehouse/detail?code=${item.code}`
			})
		}
	}
}
</script>

<style scoped>
.container {
	background: #e3f2fd;
	min-height: 100vh;
}

/* 导航栏 */
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

/* 库存卡片组 */
.stats-section {
	display: flex;
	padding: 20rpx 30rpx 30rpx;
	gap: 20rpx;
}

.stats-item {
	flex: 1;
	background: white;
	border-radius: 16rpx;
	padding: 30rpx 20rpx;
	display: flex;
	align-items: center;
	gap: 15rpx;
	box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.1);
}

.stats-item.warning {
	border-left: 6rpx solid #ff9800;
}

.stats-icon {
	width: 80rpx;
	height: 80rpx;
}

.stats-content {
	display: flex;
	flex-direction: column;
	gap: 5rpx;
}

.stats-number {
	font-size: 32rpx;
	font-weight: bold;
	color: #2196F3;
}

.stats-label {
	font-size: 24rpx;
	color: #666;
}

/* 功能区 */
.function-section {
	display: flex;
	padding: 0 30rpx 30rpx;
	gap: 20rpx;
}

.function-item {
	flex: 1;
	background: white;
	border-radius: 16rpx;
	padding: 30rpx 20rpx;
	display: flex;
	flex-direction: column;
	align-items: center;
	gap: 15rpx;
	box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.1);
	transition: transform 0.2s;
}

.function-item:active {
	transform: scale(0.95);
}

.function-icon {
	width: 80rpx;
	height: 80rpx;
}

.function-title {
	font-size: 26rpx;
	color: #333;
	font-weight: 500;
}

/* 搜索栏 */
.search-section {
	padding: 0 30rpx 30rpx;
}

.search-bar {
	position: relative;
	background: white;
	border-radius: 50rpx;
	padding: 0 50rpx 0 30rpx;
	box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.1);
}

.search-input {
	height: 80rpx;
	font-size: 28rpx;
	color: #333;
	width: 100%;
}

.search-icon {
	position: absolute;
	right: 20rpx;
	top: 50%;
	transform: translateY(-50%);
	font-size: 32rpx;
	color: #999;
}

/* 表格 */
.table-section {
	margin: 0 30rpx;
	background: white;
	border-radius: 16rpx;
	overflow: hidden;
	box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.1);
}

.table-header {
	display: flex;
	background: #f5f5f5;
	padding: 20rpx 0;
	border-bottom: 2rpx solid #eee;
}

.header-cell {
	font-size: 28rpx;
	font-weight: bold;
	color: #333;
	text-align: center;
}

.table-row {
	display: flex;
	padding: 25rpx 0;
	border-bottom: 1rpx solid #f0f0f0;
	align-items: center;
}

.table-row:last-child {
	border-bottom: none;
}

.table-row.warning {
	background: rgba(255, 152, 0, 0.1);
}

.table-cell {
	font-size: 26rpx;
	color: #666;
	text-align: center;
	display: flex;
	align-items: center;
	justify-content: center;
}

.material-code {
	width: 20%;
}

.material-name {
	width: 25%;
}

.stock {
	width: 15%;
}

.location {
	width: 20%;
}

.actions {
	width: 20%;
	gap: 10rpx;
}

.action-btn {
	padding: 8rpx 16rpx;
	border-radius: 8rpx;
	font-size: 22rpx;
	border: none;
	cursor: pointer;
}

.apply-btn {
	background: #4caf50;
	color: white;
}

.detail-btn {
	background: #2196F3;
	color: white;
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