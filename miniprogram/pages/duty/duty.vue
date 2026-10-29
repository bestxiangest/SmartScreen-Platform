<template>
	<view class="container">
		<!-- 自定义导航栏 -->
		<view class="custom-navbar fade-in-down">
			<view class="navbar-left">
				<image class="back-icon card-hover" src="http://szzgurngv.hn-bkt.clouddn.com/icon/back.png" @click="goBack" mode="aspectFit"></image>
			</view>
			<text class="navbar-title">值班调度中心</text>
			<view class="navbar-right">
			</view>
		</view>

		<!-- 日历视图 -->
		<view class="calendar-section fade-in-up" style="animation-delay: 0.2s;">
			<view class="calendar-header">
				<view class="month-nav">
					<text class="nav-btn btn-hover" @click="prevMonth">‹</text>
					<text class="month-title floating">{{currentMonth}}</text>
					<text class="nav-btn btn-hover" @click="nextMonth">›</text>
				</view>

			</view>

			<view class="calendar-grid">
				<!-- 星期标题 -->
				<view class="weekdays">
					<text class="weekday scale-in" v-for="(day, index) in weekdays" :key="day" :style="{animationDelay: (0.4 + index * 0.05) + 's'}">{{day}}</text>
				</view>

				<!-- 日期网格 -->
				<view class="days-grid">
					<view class="day-cell card-hover scale-in" v-for="(day, index) in calendarDays" :key="index" :class="{today: day.isToday, 'other-month': !day.isCurrentMonth, weekend: day.isWeekend}" :style="{animationDelay: (0.7 + (index % 7) * 0.02) + 's'}" @click="viewDayDetail(day)">
						<text class="day-number">{{day.date}}</text>
						<view class="duty-status" v-if="day.isCurrentMonth">
							<text class="status-text" v-if="day.isWeekend">休假</text>
							<text class="status-text" v-else-if="day.dutyPerson">{{day.dutyPerson}}</text>
							<text class="status-text empty" v-else>待排班</text>
						</view>
					</view>
				</view>
			</view>
		</view>

		<!-- 值班细则卡片 -->
		<view class="rules-section fade-in-up" style="animation-delay: 1.0s;">
			<view class="rules-card card-hover">
				<view class="rules-header">
					<!-- <view class="rules-icon">📋</view> -->
					<image class="rules-icon icon-bounce" src="http://szzgurngv.hn-bkt.clouddn.com/icon/zbxize.png" mode="aspectFit"></image>
					<text class="rules-title">值班细则</text>
				</view>
				<view class="rules-content">
					<view class="rule-item fade-in-up" style="animation-delay: 1.2s;">
						<text class="rule-number floating">1.</text>
						<text class="rule-text">值班时间为每日08:00-18:00，值班人员需提前10分钟到岗</text>
					</view>
					<view class="rule-item fade-in-up" style="animation-delay: 1.3s;">
						<text class="rule-number floating">2.</text>
						<text class="rule-text">值班期间需保持实验室整洁，负责设备安全和人员管理</text>
					</view>
					<view class="rule-item fade-in-up" style="animation-delay: 1.4s;">
						<text class="rule-number floating">3.</text>
						<text class="rule-text">周末为休假时间，如有特殊情况需值班请提前申请</text>
					</view>
					<view class="rule-item fade-in-up" style="animation-delay: 1.5s;">
						<text class="rule-number floating">4.</text>
						<text class="rule-text">值班人员如需调班，需提前3天申请并找到替班人员</text>
					</view>
					<view class="rule-item fade-in-up" style="animation-delay: 1.6s;">
						<text class="rule-number floating">5.</text>
						<text class="rule-text">遇到紧急情况请立即联系实验室负责人或安全管理员</text>
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
			currentDate: new Date(),
			weekdays: ['日', '一', '二', '三', '四', '五', '六'],
			dutyData: {
				// 示例数据：日期 -> 值班信息
				'2024-01-15': {
					person: '张三',
					location: '实验室A',
					timeSlot: '08:00-18:00'
				},
				'2024-01-16': {
					person: '李四',
					location: '实验室B',
					timeSlot: '08:00-18:00'
				},
				'2024-01-17': {
					person: '王五',
					location: '实验室C',
					timeSlot: '08:00-18:00'
				},
				'2024-01-18': {
					person: '赵六',
					location: '实验室A',
					timeSlot: '08:00-18:00'
				},
				'2024-01-22': {
					person: '钱七',
					location: '实验室B',
					timeSlot: '08:00-18:00'
				}
			}
		}
	},
	computed: {
		currentMonth() {
			return `${this.currentDate.getFullYear()}年${this.currentDate.getMonth() + 1}月`
		},
		calendarDays() {
				const year = this.currentDate.getFullYear()
				const month = this.currentDate.getMonth()
				const today = new Date()
				
				// 获取当月第一天和最后一天
				const firstDay = new Date(year, month, 1)
				const lastDay = new Date(year, month + 1, 0)
				
				// 获取第一天是星期几
				const firstDayWeek = firstDay.getDay()
				
				const days = []
				
				// 添加上个月的日期
				for (let i = firstDayWeek - 1; i >= 0; i--) {
					const date = new Date(year, month, -i)
					days.push({
						date: date.getDate(),
						isCurrentMonth: false,
						isToday: false,
						isWeekend: false,
						dutyPerson: null
					})
				}
				
				// 添加当月的日期
				for (let i = 1; i <= lastDay.getDate(); i++) {
					const date = new Date(year, month, i)
					const dateStr = `${year}-${String(month + 1).padStart(2, '0')}-${String(i).padStart(2, '0')}`
					const isToday = date.toDateString() === today.toDateString()
					const dayOfWeek = date.getDay()
					const isWeekend = dayOfWeek === 0 || dayOfWeek === 6 // 周日或周六
					
					days.push({
						date: i,
						isCurrentMonth: true,
						isToday,
						isWeekend,
						dateStr,
						dutyPerson: isWeekend ? null : (this.dutyData[dateStr] ? this.dutyData[dateStr].person : null),
						dutyInfo: this.dutyData[dateStr] || null
					})
				}
				
				// 补充下个月的日期，确保总共42个格子（6周）
				const remainingDays = 42 - days.length
				for (let i = 1; i <= remainingDays; i++) {
					days.push({
						date: i,
						isCurrentMonth: false,
						isToday: false,
						isWeekend: false,
						dutyPerson: null
					})
				}
				
				return days
			}
	},
	onLoad() {
		const systemInfo = uni.getSystemInfoSync()
		this.statusBarHeight = systemInfo.statusBarHeight
	},
	methods: {
			goBack() {
				uni.navigateBack()
			},
			prevMonth() {
				this.currentDate = new Date(this.currentDate.getFullYear(), this.currentDate.getMonth() - 1, 1)
			},
			nextMonth() {
				this.currentDate = new Date(this.currentDate.getFullYear(), this.currentDate.getMonth() + 1, 1)
			},
			viewDayDetail(day) {
				if (!day.isCurrentMonth) return
				
				if (day.isWeekend) {
					uni.showModal({
						title: '值班详情',
						content: `${day.date}日\n状态：休假`,
						showCancel: false
					})
				} else if (day.dutyInfo) {
					uni.showModal({
						title: '值班详情',
						content: `${day.date}日\n值班人员：${day.dutyInfo.person}\n值班地点：${day.dutyInfo.location}\n时间段：${day.dutyInfo.timeSlot}`,
						showCancel: false
					})
				} else {
					uni.showModal({
						title: '值班详情',
						content: `${day.date}日\n状态：待排班`,
						showCancel: false
					})
				}
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

/* 日历视图 */

.calendar-section {
	margin: 20rpx 30rpx 30rpx;
	background: white;
	border-radius: 16rpx;
	padding: 30rpx;
	box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.1);
}

.calendar-header {
	margin-bottom: 30rpx;
}

.month-nav {
	display: flex;
	align-items: center;
	justify-content: center;
	gap: 30rpx;
	margin-bottom: 20rpx;
}

.nav-btn {
	font-size: 32rpx;
	color: #2196F3;
	font-weight: bold;
	padding: 10rpx;
}

.month-title {
	font-size: 32rpx;
	font-weight: bold;
	color: #333;
}



.calendar-grid {
	border: 1rpx solid #eee;
	border-radius: 8rpx;
	overflow: hidden;
}

.weekdays {
	display: flex;
	background: #f5f5f5;
}

.weekday {
	flex: 1;
	padding: 20rpx 0;
	text-align: center;
	font-size: 26rpx;
	font-weight: bold;
	color: #666;
}

.days-grid {
	display: flex;
	flex-wrap: wrap;
}

.day-cell {
	width: calc(100% / 7);
	height: 100rpx;
	border-right: 1rpx solid #eee;
	border-bottom: 1rpx solid #eee;
	position: relative;
	padding: 8rpx;
	box-sizing: border-box;
	cursor: pointer;
}

.day-cell:nth-child(7n) {
	border-right: none;
}

.day-cell.today {
	background: rgba(33, 150, 243, 0.1);
}

.day-cell.other-month {
	color: #ccc;
	background: #fafafa;
}

.day-cell.weekend {
	background: rgba(158, 158, 158, 0.1);
}

.day-number {
	font-size: 22rpx;
	color: #333;
	position: absolute;
	top: 5rpx;
	left: 8rpx;
}

.duty-status {
	display: flex;
	align-items: center;
	justify-content: center;
	margin-top: 25rpx;
	height: 50rpx;
}

.status-text {
	font-size: 20rpx;
	color: #2196F3;
	font-weight: 500;
	text-align: center;
	padding: 4rpx 8rpx;
	border-radius: 8rpx;
	background: rgba(33, 150, 243, 0.1);
}

.status-text.empty {
	color: #999;
	background: rgba(153, 153, 153, 0.1);
}

/* 值班细则卡片 */
.rules-section {
	padding: 0 30rpx 30rpx;
}

.rules-card {
	background: white;
	border-radius: 16rpx;
	padding: 30rpx;
	box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.1);
}

.rules-header {
	display: flex;
	align-items: center;
	gap: 15rpx;
	margin-bottom: 25rpx;
	padding-bottom: 20rpx;
	border-bottom: 1rpx solid #eee;
}

.rules-icon {
	width: 80rpx;
	height: 80rpx;
}

.rules-title {
	font-size: 32rpx;
	font-weight: bold;
	color: #333;
}

.rules-content {
	display: flex;
	flex-direction: column;
	gap: 20rpx;
}

.rule-item {
	display: flex;
	align-items: center;
	gap: 15rpx;
	padding: 15rpx 0;
}

.rule-number {
	font-size: 26rpx;
	font-weight: bold;
	color: #2196F3;
	min-width: 30rpx;
}

.rule-text {
	font-size: 26rpx;
	color: #666;
	line-height: 1.6;
	flex: 1;
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