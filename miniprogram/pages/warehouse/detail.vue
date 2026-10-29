<template>
	<view class="container">
		<!-- 自定义导航栏 -->
		<view class="custom-navbar">
			<view class="navbar-left">
				<image class="back-icon" src="http://szzgurngv.hn-bkt.clouddn.com/icon/back.png" @click="goBack" mode="aspectFit"></image>
			</view>
			<text class="navbar-title">物料详细</text>
			<view class="navbar-right">
			</view>
		</view>

		<!-- 物料详情 -->
		<view class="detail-content">
			<view class="detail-card">
				<view class="card-title">基本信息</view>
				<view class="info-grid">
					<view class="info-item">
						<text class="label">物料编码</text>
						<text class="value">{{ materialCode }}</text>
					</view>
					<view class="info-item">
						<text class="label">物料名称</text>
						<text class="value">{{ materialInfo.name || '暂无数据' }}</text>
					</view>
					<view class="info-item">
						<text class="label">当前库存</text>
						<text class="value" :class="{ warning: materialInfo.stock < materialInfo.safeStock }">{{ materialInfo.stock || 0 }}</text>
					</view>
					<view class="info-item">
						<text class="label">安全库存</text>
						<text class="value">{{ materialInfo.safeStock || 0 }}</text>
					</view>
					<view class="info-item">
						<text class="label">存放位置</text>
						<text class="value">{{ materialInfo.location || '未知' }}</text>
					</view>
					<view class="info-item">
						<text class="label">库存状态</text>
						<text class="value" :class="stockStatusClass">{{ stockStatus }}</text>
					</view>
				</view>
			</view>

			<view class="detail-card">
				<view class="card-title">操作记录</view>
				<view class="record-list">
					<view class="record-item" v-for="(record, index) in operationRecords" :key="index">
						<view class="record-info">
							<text class="record-type">{{ record.type }}</text>
							<text class="record-desc">{{ record.description }}</text>
						</view>
						<view class="record-meta">
							<text class="record-time">{{ record.time }}</text>
							<text class="record-user">{{ record.user }}</text>
						</view>
					</view>
				</view>
			</view>
		</view>

		<!-- 操作按钮 -->
		<view class="action-buttons">
			<button class="btn btn-apply" @click="applyMaterial">申领物料</button>
		</view>
	</view>
</template>

<script>
export default {
	data() {
		return {
			statusBarHeight: 0,
			materialCode: '',
			materialInfo: {},
			operationRecords: [
				{
					type: '入库',
					description: '新采购物料入库',
					time: '2024-01-15 10:30',
					user: '库管员'
				},
				{
					type: '申领',
					description: '学生申领用于实验',
					time: '2024-01-14 14:20',
					user: '张三'
				},
				{
					type: '盘点',
					description: '定期库存盘点',
					time: '2024-01-10 09:00',
					user: '库管员'
				}
			]
		}
	},
	computed: {
		stockStatus() {
			if (!this.materialInfo.stock) return '无库存'
			if (this.materialInfo.stock < this.materialInfo.safeStock) return '库存不足'
			return '库存充足'
		},
		stockStatusClass() {
			if (!this.materialInfo.stock) return 'danger'
			if (this.materialInfo.stock < this.materialInfo.safeStock) return 'warning'
			return 'success'
		}
	},
	onLoad(options) {
		const systemInfo = uni.getSystemInfoSync()
		this.statusBarHeight = systemInfo.statusBarHeight
		
		if (options.code) {
			this.materialCode = options.code
			this.loadMaterialDetail()
		}
	},
	methods: {
		goBack() {
			uni.navigateBack()
		},
		loadMaterialDetail() {
			// 模拟加载物料详情数据
			this.materialInfo = {
				name: '示例物料 - 详细描述',
				stock: 150,
				safeStock: 100,
				location: 'A1-01'
			}
		},
		applyMaterial() {
			uni.navigateTo({
				url: `/pages/warehouse/apply-process?code=${this.materialCode}&name=${encodeURIComponent(this.materialInfo.name)}`
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

/* 详情内容 */
.detail-content {
	padding: 20rpx 30rpx 120rpx;
}

.detail-card {
	background: white;
	border-radius: 16rpx;
	padding: 30rpx;
	margin-top: 40rpx;
	margin-bottom: 20rpx;
	box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.1);
}

.card-title {
	font-size: 32rpx;
	font-weight: bold;
	color: #2196F3;
	margin-bottom: 20rpx;
}

.info-grid {
	display: grid;
	grid-template-columns: 1fr 1fr;
	gap: 20rpx;
}

.info-item {
	display: flex;
	flex-direction: column;
	gap: 8rpx;
}

.label {
	font-size: 24rpx;
	color: #666;
}

.value {
	font-size: 28rpx;
	color: #333;
	font-weight: 500;
}

.value.warning {
	color: #ff9800;
}

.value.success {
	color: #4CAF50;
}

.value.danger {
	color: #f44336;
}

/* 操作记录 */
.record-list {
	display: flex;
	flex-direction: column;
	gap: 15rpx;
}

.record-item {
	display: flex;
	justify-content: space-between;
	align-items: center;
	padding: 20rpx;
	background: #f8f9fa;
	border-radius: 8rpx;
}

.record-info {
	display: flex;
	flex-direction: column;
	gap: 5rpx;
}

.record-type {
	font-size: 26rpx;
	font-weight: bold;
	color: #2196F3;
}

.record-desc {
	font-size: 24rpx;
	color: #666;
}

.record-meta {
	display: flex;
	flex-direction: column;
	align-items: flex-end;
	gap: 5rpx;
}

.record-time {
	font-size: 22rpx;
	color: #999;
}

.record-user {
	font-size: 24rpx;
	color: #666;
}

/* 操作按钮 */
.action-buttons {
	position: fixed;
	bottom: 0;
	left: 0;
	right: 0;
	padding: 20rpx 30rpx;
	background: white;
	border-top: 1rpx solid #e0e0e0;
}

.btn {
	width: 100%;
	height: 88rpx;
	border-radius: 44rpx;
	font-size: 32rpx;
	font-weight: bold;
	border: none;
	display: flex;
	align-items: center;
	justify-content: center;
}

.btn-apply {
	background: linear-gradient(135deg, #2196F3, #1976D2);
	color: white;
}
</style>