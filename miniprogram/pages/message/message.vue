<template>
	<view class="container">
		<!-- 搜索栏 -->
		<view class="search-bar fade-in-down">
			<input class="search-input" placeholder="搜索联系人或消息" v-model="searchKeyword" @input="onSearch" />
			<view class="filter-btn card-hover" @click="showFilter">
				<image class="filter-icon icon-bounce" src="http://szzgurngv.hn-bkt.clouddn.com/icon/icon-search.png" mode="aspectFit"></image>
			</view>
		</view>

		<!-- 消息统计 -->
		<view class="message-stats">
			<view class="stat-item unread scale-in" style="animation-delay: 0.1s;">
				<text class="stat-number">{{messageStats.unread}}</text>
				<text class="stat-label">未读</text>
			</view>
			<view class="stat-item today scale-in" style="animation-delay: 0.2s;">
				<text class="stat-number">{{messageStats.today}}</text>
				<text class="stat-label">今日</text>
			</view>
			<view class="stat-item total scale-in" style="animation-delay: 0.3s;">
				<text class="stat-number">{{messageStats.total}}</text>
				<text class="stat-label">总计</text>
			</view>
		</view>



		<!-- 消息列表 -->
		<view class="message-list">
			<view class="list-header">
				<text class="header-title">消息列表</text>
				<view class="view-toggle">
					<text class="toggle-btn" :class="{active: viewMode === 'all'}" @click="viewMode = 'all'">全部</text>
					<text class="toggle-btn" :class="{active: viewMode === 'unread'}" @click="viewMode = 'unread'">未读</text>
				</view>
			</view>

			<view class="message-items">
				<view class="message-item card-hover fade-in-up" v-for="(message, index) in filteredMessages" :key="message.id" :style="{animationDelay: (0.4 + index * 0.1) + 's'}" @click="openMessage(message)">
					<view class="message-left">
						<image class="avatar floating" :src="message.avatar" mode="aspectFill"></image>
						<view class="unread-badge" v-if="message.unread > 0">
							<text class="badge-text">{{message.unread > 99 ? '99+' : message.unread}}</text>
						</view>
					</view>
					<view class="message-content">
						<view class="message-header">
							<text class="sender-name">{{message.senderName}}</text>
							<text class="message-time">{{message.time}}</text>
						</view>
						<text class="message-preview" :class="{unread: message.unread > 0}">{{message.preview}}</text>
					</view>
					<view class="message-right">
						<view class="message-type" :class="message.type">
						<image class="type-icon icon-bounce" :src="getTypeIcon(message.type)" mode="aspectFit"></image>
					</view>
					</view>
				</view>
			</view>
			
			<!-- 新建消息浮动按钮 -->
			<image class="fab-icon floating card-hover" src="http://szzgurngv.hn-bkt.clouddn.com/icon/plus-copy.png" @click="navigateToCompose" mode="aspectFit"></image>
		</view>
	</view>
</template>

<script>
	export default {
		onLoad() {
			this.refreshMessageData()
			// 监听消息已读事件
			uni.$on('messageRead', this.handleMessageRead)
		},
		
		onShow() {
			// 页面显示时刷新数据
			this.refreshMessageData();
			// 更新已读状态
			this.updateReadStatus()
		},
		
		onUnload() {
			// 移除事件监听
			uni.$off('messageRead', this.handleMessageRead)
		},
		
		// 下拉刷新
		onPullDownRefresh() {
			this.page = 1
			this.hasMore = true
			this.loadAnnouncementsFromAPI()
			setTimeout(() => {
				uni.stopPullDownRefresh()
			}, 1000)
		},
		
		// 上拉加载更多
		onReachBottom() {
			if (this.hasMore && !this.loading) {
				this.page++
				this.loadAnnouncementsFromAPI()
			}
		},
		data() {
			return {
				searchKeyword: '',
				viewMode: 'all', // 'all' | 'unread'
				messageStats: {
					unread: 5,
					today: 12,
					total: 48
				},
				messages: [],
			loading: false,
			page: 1,
			limit: 10,
			hasMore: true,
			apiBaseUrl: 'http://sys.sharpcaterpillar.top:5000/api/v1'
			}
		},
		computed: {
			filteredMessages() {
				let result = this.messages
				
				// 搜索过滤
				if (this.searchKeyword) {
					result = result.filter(message => 
						message.senderName.includes(this.searchKeyword) || 
						message.preview.includes(this.searchKeyword)
					)
				}
				
				// 未读过滤
				if (this.viewMode === 'unread') {
					result = result.filter(message => message.unread > 0)
				}
				
				return result
			}
		},
		methods: {
			onSearch() {
				// 搜索逻辑
			},
			showFilter() {
				uni.showActionSheet({
					itemList: ['全部消息', '未读消息', '老师消息'],
					success: (res) => {
						console.log('选择了：' + res.tapIndex)
					}
				})
			},
			navigateToCompose() {
				uni.navigateTo({
					url: '/pages/message/compose'
				})
			},
			showNotifications() {
				uni.showModal({
					title: '系统通知',
					content: '功能开发中，敬请期待',
					showCancel: false
				})
			},
			openMessage(message) {
				uni.navigateTo({
					url: `/pages/message/detail?id=${message.id}`
				})
			},
			getTypeIcon(type) {
				const icons = {
					teacher: 'icon-teacher.png',
					student: 'icon-student.png',
					system: 'icon-system.png',
					alert: 'icon-alert.png'
				}
				return `/static/${icons[type] || 'icon-message.png'}`
			},
			navigateTo(url) {
				uni.navigateTo({ url })
			},
			// 刷新消息数据
			refreshMessageData() {
				// 检查是否有新消息需要添加
				const newMessage = uni.getStorageSync('newMessage')
				if (newMessage) {
					// 添加新消息到列表顶部
					const messageId = Date.now().toString()
					this.messages.unshift({
						id: messageId,
						senderName: newMessage.publisherName || '我',
						avatar: '/static/avatars/me.jpg',
						preview: newMessage.content.substring(0, 30) + (newMessage.content.length > 30 ? '...' : ''),
						time: this.formatTime(new Date()),
						unread: 0,
						type: newMessage.type || '通知',
						isImportant: newMessage.isImportant || false
					})
					
					// 保存新消息的完整信息，供详情页面使用
					uni.setStorageSync('messageDetail_' + messageId, {
						id: messageId,
						senderName: newMessage.publisherName || '我',
						avatar: '/static/avatars/me.jpg',
						subject: newMessage.subject,
						content: newMessage.content,
						fullTime: this.formatFullTime(new Date(newMessage.timestamp)),
						type: newMessage.type || '通知',
						isImportant: newMessage.isImportant || false,
						attachments: newMessage.attachments || []
					})
					
					// 更新统计数据
					this.messageStats.today += 1
					this.messageStats.total += 1
					
					// 清除缓存的新消息
					uni.removeStorageSync('newMessage')
				}
				
				// 加载API数据
				this.loadAnnouncementsFromAPI()
			},
			
			// 从API加载公告数据
			loadAnnouncementsFromAPI() {
				if (this.loading) return
				
				this.loading = true
				uni.request({
					url: `${this.apiBaseUrl}/announcements`,
					method: 'GET',
					data: {
						page: this.page,
						limit: this.limit
					},
					header: {
						'Content-Type': 'application/json'
					},
					success: (res) => {
						if (res.statusCode === 200 && res.data.success) {
							const announcements = res.data.data.items || []
							const pagination = res.data.data.pagination || {}
							
							// 获取已读消息列表
							const readMessages = uni.getStorageSync('readMessages') || []
							
							// 转换API数据格式为消息格式
							const messages = announcements.map(item => ({
								id: item.id,
								senderName: item.author_name || '系统',
								avatar: this.getAvatarByType(item.type),
								preview: item.content.substring(0, 30) + (item.content.length > 30 ? '...' : ''),
								time: this.formatTime(new Date(item.created_at)),
								unread: readMessages.includes(item.id.toString()) ? 0 : 1, // 根据已读状态设置
								type: item.type,
								isImportant: item.is_important,
								title: item.title,
								content: item.content,
								created_at: item.created_at
							}))
							
							if (this.page === 1) {
								this.messages = messages
							} else {
								this.messages = [...this.messages, ...messages]
							}
							
							// 更新分页信息
							this.hasMore = pagination.has_next || false
							
							// 更新统计数据
							this.updateMessageStats()
							
							// 保存消息详情到本地存储
							messages.forEach(message => {
								uni.setStorageSync('messageDetail_' + message.id, {
									id: message.id,
									senderName: message.senderName,
									avatar: message.avatar,
									subject: message.title,
									content: message.content,
									fullTime: this.formatFullTime(new Date(message.created_at)),
									type: message.type,
									isImportant: message.isImportant,
									attachments: []
								})
							})
						} else {
							console.error('获取公告失败:', res.data.message)
							uni.showToast({
								title: '获取消息失败',
								icon: 'none'
							})
						}
					},
					fail: (err) => {
						console.error('API请求失败:', err)
						uni.showToast({
							title: '网络连接失败',
							icon: 'none'
						})
					},
					complete: () => {
						this.loading = false
					}
				})
			},
			
			// 根据类型获取头像
			getAvatarByType(type) {
				const avatarMap = {
					'通知': '/static/avatars/system.jpg',
					'新闻': '/static/avatars/news.jpg',
					'动态': '/static/avatars/dynamic.jpg',
					'安全提示': '/static/avatars/safety.jpg',
					'天气提示': '/static/avatars/weather.jpg',
					'名言金句': '/static/avatars/quote.jpg'
				}
				return avatarMap[type] || '/static/avatars/system.jpg'
			},
			
			// 更新消息统计
			updateMessageStats() {
				const unreadCount = this.messages.filter(msg => msg.unread > 0).length
				const today = new Date()
				const todayCount = this.messages.filter(msg => {
					const msgDate = new Date(msg.created_at || Date.now())
					return msgDate.toDateString() === today.toDateString()
				}).length
				
				this.messageStats = {
					unread: unreadCount,
					today: todayCount,
					total: this.messages.length
				}
			},
			
			// 处理消息已读事件
			handleMessageRead(messageId) {
				// 更新消息列表中对应消息的已读状态
				const messageIndex = this.messages.findIndex(msg => msg.id.toString() === messageId.toString())
				if (messageIndex !== -1) {
					this.messages[messageIndex].unread = 0
					// 更新统计数据
					this.updateMessageStats()
				}
			},
			
			// 更新已读状态
			updateReadStatus() {
				const readMessages = uni.getStorageSync('readMessages') || []
				this.messages.forEach(message => {
					if (readMessages.includes(message.id.toString())) {
						message.unread = 0
					}
				})
				// 更新统计数据
				this.updateMessageStats()
			},
			// 格式化时间
			formatTime(date) {
				const now = new Date()
				const diff = now.getTime() - date.getTime()
				
				if (diff < 60000) { // 1分钟内
					return '刚刚'
				} else if (diff < 3600000) { // 1小时内
					return Math.floor(diff / 60000) + '分钟前'
				} else if (diff < 86400000) { // 24小时内
					return date.getHours().toString().padStart(2, '0') + ':' + date.getMinutes().toString().padStart(2, '0')
				} else {
					return '昨天'
				}
			},
			// 格式化完整时间
			formatFullTime(date) {
				const year = date.getFullYear()
				const month = (date.getMonth() + 1).toString().padStart(2, '0')
				const day = date.getDate().toString().padStart(2, '0')
				const hour = date.getHours().toString().padStart(2, '0')
				const minute = date.getMinutes().toString().padStart(2, '0')
				return `${year}年${month}月${day}日 ${hour}:${minute}`
			}
		}
	}
</script>

<style scoped>
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
		transform: translate3d(0, -2px, 0);
		box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
	}

	.card-hover:active {
		transform: translate3d(0, 0, 0) scale(0.98);
		transition: all 0.1s ease;
	}

	.container {
		min-height: 100vh;
		background: #e3f2fd;
		padding: 20rpx;
	}

	.search-bar {
		display: flex;
		align-items: center;
		gap: 20rpx;
		margin-bottom: 30rpx;
	}

	.search-input {
		flex: 1;
		height: 80rpx;
		padding: 0 30rpx;
		background: white;
		border-radius: 40rpx;
		font-size: 28rpx;
		border: 2rpx solid #e0e0e0;
	}

	.filter-btn {
		width: 80rpx;
		height: 80rpx;
		display: flex;
		align-items: center;
		justify-content: center;
		background: #2196F3;
		border-radius: 40rpx;
	}

	.filter-icon {
		width: 32rpx;
		height: 32rpx;
	}

	.message-stats {
		display: flex;
		gap: 20rpx;
		margin-bottom: 30rpx;
	}

	.stat-item {
		flex: 1;
		padding: 30rpx;
		background: white;
		border-radius: 20rpx;
		display: flex;
		flex-direction: column;
		align-items: center;
		gap: 10rpx;
		box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.1);
	}

	.stat-number {
		font-size: 48rpx;
		font-weight: bold;
		color: #2196F3;
	}

	.stat-item.unread .stat-number {
		color: #2196F3;
	}

	.stat-item.today .stat-number {
		color: #2196F3;
	}

	.stat-label {
		font-size: 24rpx;
		color: #666;
	}



	.message-list {
		background: white;
		border-radius: 20rpx;
		padding: 30rpx;
		position: relative;
	}

	.list-header {
		display: flex;
		justify-content: space-between;
		align-items: center;
		margin-bottom: 30rpx;
	}

	.header-title {
		font-size: 32rpx;
		font-weight: bold;
		color: #333;
	}

	.view-toggle {
		display: flex;
		background: #f0f0f0;
		border-radius: 25rpx;
		padding: 5rpx;
	}

	.toggle-btn {
		padding: 15rpx 30rpx;
		border-radius: 20rpx;
		font-size: 24rpx;
		color: #666;
		transition: all 0.3s;
	}

	.toggle-btn.active {
		background: #2196F3;
		color: white;
	}

	.message-items {
		display: flex;
		flex-direction: column;
		gap: 20rpx;
	}

	.message-item {
		display: flex;
		align-items: center;
		gap: 20rpx;
		padding: 20rpx;
		border-radius: 15rpx;
		transition: all 0.3s;
		width: 100%; /* 确保不超出容器宽度 */
		box-sizing: border-box; /* 包含padding在宽度计算内 */
		overflow: hidden; /* 防止内容溢出 */
	}

	.message-item:active {
		background: #f8f8f8;
	}

	.message-left {
		position: relative;
	}

	.avatar {
		width: 100rpx;
		height: 100rpx;
		border-radius: 50%;
	}

	.unread-badge {
		position: absolute;
		top: -10rpx;
		right: -10rpx;
		min-width: 36rpx;
		height: 36rpx;
		background: #f44336;
		border-radius: 18rpx;
		display: flex;
		align-items: center;
		justify-content: center;
		padding: 0 8rpx;
	}

	.badge-text {
		font-size: 20rpx;
		color: white;
		font-weight: bold;
	}

	.message-content {
		flex: 1;
		display: flex;
		flex-direction: column;
		gap: 8rpx;
		min-width: 0; /* 允许flex子项收缩 */
		overflow: hidden; /* 防止内容溢出 */
	}

	.message-header {
		display: flex;
		justify-content: space-between;
		align-items: center;
		width: 100%; /* 确保占满可用宽度 */
		min-width: 0; /* 允许flex子项收缩 */
	}

	.sender-name {
		font-size: 28rpx;
		font-weight: bold;
		color: #333;
		overflow: hidden;
		text-overflow: ellipsis;
		white-space: nowrap;
		flex: 1; /* 允许名称占用剩余空间但不超出 */
		min-width: 0; /* 允许收缩 */
	}

	.message-time {
		font-size: 22rpx;
		color: #999;
	}

	.message-preview {
		font-size: 26rpx;
		color: #666;
		line-height: 1.4;
		overflow: hidden;
		text-overflow: ellipsis;
		white-space: nowrap;
		max-width: 100%; /* 确保不超出父容器 */
		width: 100%; /* 占满可用宽度 */
	}

	.message-preview.unread {
		color: #333;
		font-weight: 500;
	}

	.message-right {
		display: flex;
		flex-direction: column;
		align-items: center;
	}

	.message-type {
		width: 60rpx;
		height: 60rpx;
		display: flex;
		align-items: center;
		justify-content: center;
		border-radius: 50%;
		background: #f0f0f0;
	}

	.message-type.teacher {
		background: #e3f2fd;
	}

	.message-type.system {
		background: #fff3e0;
	}

	.message-type.alert {
		background: #ffebee;
	}

	.type-icon {
		width: 24rpx;
		height: 24rpx;
	}

	.fab-icon {
		position: fixed;
		bottom: 120rpx;
		right: 40rpx;
		width: 100rpx;
		height: 100rpx;
		z-index: 100;
	}
</style>