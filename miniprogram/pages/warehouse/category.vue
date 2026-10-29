<template>
	<view class="container">
		<!-- 自定义导航栏 -->
		<view class="custom-navbar">
			<view class="navbar-left">
				<image class="back-icon" src="http://szzgurngv.hn-bkt.clouddn.com/icon/back.png" @click="goBack" mode="aspectFit"></image>
			</view>
			<text class="navbar-title">元件分类</text>
			<view class="navbar-right">
			</view>
		</view>

		<!-- 分类列表 -->
		<view class="category-section">
			<!-- 基础电阻元件 -->
			<view class="category-group">
				<view class="category-header" @click="toggleCategory('resistor')">
					<image class="category-icon" src="/static/ai_logo.png" mode="aspectFit"></image>
					<text class="category-title">一、基础电阻元件</text>
					<text class="expand-icon" :class="{expanded: expandedCategories.resistor}">▼</text>
				</view>
				<view class="category-content" v-if="expandedCategories.resistor">
					<view class="subcategory">
						<text class="subcategory-title">电阻器</text>
						<view class="component-grid">
							<view class="component-item" v-for="item in components.resistor" :key="item.model" @click="applyComponent(item)">
								<view class="component-info">
									<text class="component-model">{{item.model}}</text>
									<text class="component-desc">{{item.description}}</text>
									<text class="component-stock">库存: {{item.stock}}</text>
								</view>
								<view class="apply-btn">申领</view>
							</view>
						</view>
					</view>
				</view>
			</view>

			<!-- 基础电容元件 -->
			<view class="category-group">
				<view class="category-header" @click="toggleCategory('capacitor')">
					<image class="category-icon" src="/static/icons/icon-battery.png" mode="aspectFit"></image>
					<text class="category-title">二、基础电容元件</text>
					<text class="expand-icon" :class="{expanded: expandedCategories.capacitor}">▼</text>
				</view>
				<view class="category-content" v-if="expandedCategories.capacitor">
					<view class="subcategory">
						<text class="subcategory-title">电容器</text>
						<view class="component-grid">
							<view class="component-item" v-for="item in components.capacitor" :key="item.model" @click="applyComponent(item)">
								<view class="component-info">
									<text class="component-model">{{item.model}}</text>
									<text class="component-desc">{{item.description}}</text>
									<text class="component-stock">库存: {{item.stock}}</text>
								</view>
								<view class="apply-btn">申领</view>
							</view>
						</view>
					</view>
				</view>
			</view>

			<!-- 基础电感元件 -->
			<view class="category-group">
				<view class="category-header" @click="toggleCategory('inductor')">
					<image class="category-icon" src="/static/icons/icon-spiral.png" mode="aspectFit"></image>
					<text class="category-title">三、基础电感元件</text>
					<text class="expand-icon" :class="{expanded: expandedCategories.inductor}">▼</text>
				</view>
				<view class="category-content" v-if="expandedCategories.inductor">
					<view class="subcategory">
						<text class="subcategory-title">电感器</text>
						<view class="component-grid">
							<view class="component-item" v-for="item in components.inductor" :key="item.model" @click="applyComponent(item)">
								<view class="component-info">
									<text class="component-model">{{item.model}}</text>
									<text class="component-desc">{{item.description}}</text>
									<text class="component-stock">库存: {{item.stock}}</text>
								</view>
								<view class="apply-btn">申领</view>
							</view>
						</view>
					</view>
				</view>
			</view>

			<!-- 二极管三极管 -->
			<view class="category-group">
				<view class="category-header" @click="toggleCategory('diode_transistor')">
					<image class="category-icon" src="/static/icons/icon-plug.png" mode="aspectFit"></image>
					<text class="category-title">四、二极管三极管</text>
					<text class="expand-icon" :class="{expanded: expandedCategories.diode_transistor}">▼</text>
				</view>
				<view class="category-content" v-if="expandedCategories.diode_transistor">
					<view class="subcategory">
						<text class="subcategory-title">分立器件</text>
						<view class="component-grid">
							<view class="component-item" v-for="item in components.diode_transistor" :key="item.model" @click="applyComponent(item)">
								<view class="component-info">
									<text class="component-model">{{item.model}}</text>
									<text class="component-desc">{{item.description}}</text>
									<text class="component-stock">库存: {{item.stock}}</text>
								</view>
								<view class="apply-btn">申领</view>
							</view>
						</view>
					</view>
				</view>
			</view>

			<!-- 集成电路 -->
			<view class="category-group">
				<view class="category-header" @click="toggleCategory('integrated_circuit')">
					<image class="category-icon" src="/static/icons/icon-computer.png" mode="aspectFit"></image>
					<text class="category-title">五、集成电路</text>
					<text class="expand-icon" :class="{expanded: expandedCategories.integrated_circuit}">▼</text>
				</view>
				<view class="category-content" v-if="expandedCategories.integrated_circuit">
					<view class="subcategory">
						<text class="subcategory-title">微控制器</text>
						<view class="component-grid">
							<view class="component-item" v-for="item in components.microcontroller" :key="item.model" @click="applyComponent(item)">
								<view class="component-info">
									<text class="component-model">{{item.model}}</text>
									<text class="component-desc">{{item.description}}</text>
									<text class="component-stock">库存: {{item.stock}}</text>
								</view>
								<view class="apply-btn">申领</view>
							</view>
						</view>
					</view>
					<view class="subcategory">
						<text class="subcategory-title">运算放大器</text>
						<view class="component-grid">
							<view class="component-item" v-for="item in components.opamp" :key="item.model" @click="applyComponent(item)">
								<view class="component-info">
									<text class="component-model">{{item.model}}</text>
									<text class="component-desc">{{item.description}}</text>
									<text class="component-stock">库存: {{item.stock}}</text>
								</view>
								<view class="apply-btn">申领</view>
							</view>
						</view>
					</view>
					<view class="subcategory">
						<text class="subcategory-title">稳压器</text>
						<view class="component-grid">
							<view class="component-item" v-for="item in components.regulator" :key="item.model" @click="applyComponent(item)">
								<view class="component-info">
									<text class="component-model">{{item.model}}</text>
									<text class="component-desc">{{item.description}}</text>
									<text class="component-stock">库存: {{item.stock}}</text>
								</view>
								<view class="apply-btn">申领</view>
							</view>
						</view>
					</view>
					<view class="subcategory">
						<text class="subcategory-title">逻辑门</text>
						<view class="component-grid">
							<view class="component-item" v-for="item in components.logic_gate" :key="item.model" @click="applyComponent(item)">
								<view class="component-info">
									<text class="component-model">{{item.model}}</text>
									<text class="component-desc">{{item.description}}</text>
									<text class="component-stock">库存: {{item.stock}}</text>
								</view>
								<view class="apply-btn">申领</view>
							</view>
						</view>
					</view>
				</view>
			</view>

			<!-- 半导体主动器件 -->
			<view class="category-group">
				<view class="category-header" @click="toggleCategory('semiconductor')">
					<image class="category-icon" src="/static/icons/icon-diamond.png" mode="aspectFit"></image>
					<text class="category-title">六、半导体主动器件</text>
					<text class="expand-icon" :class="{expanded: expandedCategories.semiconductor}">▼</text>
				</view>
				<view class="category-content" v-if="expandedCategories.semiconductor">
					<view class="subcategory">
						<text class="subcategory-title">分立器件</text>
						<view class="component-grid">
							<view class="component-item" v-for="item in components.semiconductor" :key="item.model" @click="applyComponent(item)">
								<view class="component-info">
									<text class="component-model">{{item.model}}</text>
									<text class="component-desc">{{item.description}}</text>
									<text class="component-stock">库存: {{item.stock}}</text>
								</view>
								<view class="apply-btn">申领</view>
							</view>
						</view>
					</view>
				</view>
			</view>

			<!-- 机电与连接元件 -->
			<view class="category-group">
				<view class="category-header" @click="toggleCategory('mechanical')">
					<image class="category-icon" src="/static/icons/icon-wrench.png" mode="aspectFit"></image>
					<text class="category-title">七、机电与连接元件</text>
					<text class="expand-icon" :class="{expanded: expandedCategories.mechanical}">▼</text>
				</view>
				<view class="category-content" v-if="expandedCategories.mechanical">
					<view class="subcategory">
						<text class="subcategory-title">机电与连接元件</text>
						<view class="component-grid">
							<view class="component-item" v-for="item in components.mechanical" :key="item.model" @click="applyComponent(item)">
								<view class="component-info">
									<text class="component-model">{{item.model}}</text>
									<text class="component-desc">{{item.description}}</text>
									<text class="component-stock">库存: {{item.stock}}</text>
								</view>
								<view class="apply-btn">申领</view>
							</view>
						</view>
					</view>
				</view>
			</view>

			<!-- 学习控制与数据元件 -->
			<view class="category-group">
				<view class="category-header" @click="toggleCategory('control')">
					<image class="category-icon" src="/static/icons/icon-control.png" mode="aspectFit"></image>
					<text class="category-title">八、学习控制与数据元件</text>
					<text class="expand-icon" :class="{expanded: expandedCategories.control}">▼</text>
				</view>
				<view class="category-content" v-if="expandedCategories.control">
					<view class="subcategory">
						<text class="subcategory-title">学习控制与数据元件</text>
						<view class="component-grid">
							<view class="component-item" v-for="item in components.control" :key="item.model" @click="applyComponent(item)">
								<view class="component-info">
									<text class="component-model">{{item.model}}</text>
									<text class="component-desc">{{item.description}}</text>
									<text class="component-stock">库存: {{item.stock}}</text>
								</view>
								<view class="apply-btn">申领</view>
							</view>
						</view>
					</view>
				</view>
			</view>

			<!-- 显示与光电器件 -->
			<view class="category-group">
				<view class="category-header" @click="toggleCategory('display')">
					<image class="category-icon" src="/static/icons/icon-light.png" mode="aspectFit"></image>
					<text class="category-title">九、显示与光电器件</text>
					<text class="expand-icon" :class="{expanded: expandedCategories.display}">▼</text>
				</view>
				<view class="category-content" v-if="expandedCategories.display">
					<view class="subcategory">
						<text class="subcategory-title">显示与光电器件</text>
						<view class="component-grid">
							<view class="component-item" v-for="item in components.display" :key="item.model" @click="applyComponent(item)">
								<view class="component-info">
									<text class="component-model">{{item.model}}</text>
									<text class="component-desc">{{item.description}}</text>
									<text class="component-stock">库存: {{item.stock}}</text>
								</view>
								<view class="apply-btn">申领</view>
							</view>
						</view>
					</view>
				</view>
			</view>

			<!-- 专用材料与组件 -->
			<view class="category-group">
				<view class="category-header" @click="toggleCategory('materials')">
					<image class="category-icon" src="/static/icons/icon-test-tube.png" mode="aspectFit"></image>
					<text class="category-title">十、专用材料与组件</text>
					<text class="expand-icon" :class="{expanded: expandedCategories.materials}">▼</text>
				</view>
				<view class="category-content" v-if="expandedCategories.materials">
					<view class="subcategory">
						<text class="subcategory-title">专用材料与组件</text>
						<view class="component-grid">
							<view class="component-item" v-for="item in components.materials" :key="item.model" @click="applyComponent(item)">
								<view class="component-info">
									<text class="component-model">{{item.model}}</text>
									<text class="component-desc">{{item.description}}</text>
									<text class="component-stock">库存: {{item.stock}}</text>
								</view>
								<view class="apply-btn">申领</view>
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
			statusBarHeight: 0,
			expandedCategories: {
				resistor: false,
				capacitor: false,
				inductor: false,
				diode_transistor: false,
				integrated_circuit: false,
				semiconductor: false,
				mechanical: false,
				control: false,
				display: false,
				materials: false
			},
			components: {
				resistor: [
					{ model: 'CF 1/4W 100Ω', description: '碳膜电阻 1/4瓦 100欧', stock: 50 },
					{ model: 'CF 1/4W 1KΩ', description: '碳膜电阻 1/4瓦 1千欧', stock: 45 },
					{ model: 'CF 1/4W 10KΩ', description: '碳膜电阻 1/4瓦 10千欧', stock: 38 },
					{ model: 'MF 1/4W 100Ω', description: '金属膜电阻 1/4瓦 100欧', stock: 42 },
					{ model: 'MF 1/4W 1KΩ', description: '金属膜电阻 1/4瓦 1千欧', stock: 35 },
					{ model: 'MF 1/4W 10KΩ', description: '金属膜电阻 1/4瓦 10千欧', stock: 28 },
					{ model: 'WH5-1A 1KΩ', description: '线绕电位器 1千欧', stock: 15 },
					{ model: 'WH5-1A 10KΩ', description: '线绕电位器 10千欧', stock: 12 },
					{ model: 'RN55D 1KΩ', description: '精密电阻网络 1千欧', stock: 20 },
					{ model: 'RN55D 10KΩ', description: '精密电阻网络 10千欧', stock: 18 }
				],
				capacitor: [
					{ model: 'C0805-100nF', description: '陶瓷电容 0805 100nF', stock: 200 },
					{ model: 'C0805-1uF', description: '陶瓷电容 0805 1uF', stock: 150 },
					{ model: 'C0805-10uF', description: '陶瓷电容 0805 10uF', stock: 120 },
					{ model: 'AL-16V-100uF', description: '铝电解电容 16V 100uF', stock: 80 },
					{ model: 'AL-25V-220uF', description: '铝电解电容 25V 220uF', stock: 60 },
					{ model: 'AL-35V-470uF', description: '铝电解电容 35V 470uF', stock: 45 },
					{ model: 'FILM-250V-1uF', description: '薄膜电容 250V 1uF', stock: 30 },
					{ model: 'FILM-400V-0.47uF', description: '薄膜电容 400V 0.47uF', stock: 25 },
					{ model: 'TANT-16V-10uF', description: '钽电容 16V 10uF', stock: 40 },
					{ model: 'TANT-25V-22uF', description: '钽电容 25V 22uF', stock: 35 }
				],
				inductor: [
					{ model: 'L0805-10uH', description: '片式电感 0805 10uH', stock: 100 },
					{ model: 'L0805-22uH', description: '片式电感 0805 22uH', stock: 85 },
					{ model: 'L0805-47uH', description: '片式电感 0805 47uH', stock: 70 },
					{ model: 'L1210-100uH', description: '片式电感 1210 100uH', stock: 60 },
					{ model: 'CORE-1mH', description: '磁芯电感 1mH', stock: 40 },
					{ model: 'CORE-2.2mH', description: '磁芯电感 2.2mH', stock: 35 },
					{ model: 'PWR-10uH-3A', description: '功率电感 10uH 3A', stock: 25 },
					{ model: 'PWR-22uH-2A', description: '功率电感 22uH 2A', stock: 20 },
					{ model: 'CHOKE-1mH', description: '共模电感 1mH', stock: 30 },
					{ model: 'CHOKE-2.2mH', description: '共模电感 2.2mH', stock: 28 }
				],
				diode_transistor: [
					{ model: '1N4007', description: '整流二极管 1000V 1A', stock: 150 },
					{ model: '1N4148', description: '开关二极管 75V 150mA', stock: 120 },
					{ model: '1N5819', description: '肖特基二极管 40V 1A', stock: 100 },
					{ model: '1N5822', description: '肖特基二极管 40V 3A', stock: 80 },
					{ model: 'LED-5mm-Red', description: '发光二极管 5mm 红色', stock: 200 },
					{ model: 'LED-5mm-Blue', description: '发光二极管 5mm 蓝色', stock: 180 },
					{ model: '2N3904', description: 'NPN三极管 40V 200mA', stock: 90 },
					{ model: '2N3906', description: 'PNP三极管 40V 200mA', stock: 85 },
					{ model: 'BC547', description: 'NPN三极管 45V 100mA', stock: 75 },
					{ model: 'BC557', description: 'PNP三极管 45V 100mA', stock: 70 }
				],
				microcontroller: [
					{ model: 'STM32F103C8T6', description: 'STM32F1系列 72MHz ARM Cortex-M3', stock: 25 },
					{ model: 'STM32F407VGT6', description: 'STM32F4系列 168MHz ARM Cortex-M4', stock: 20 },
					{ model: 'STM32F429IGT6', description: 'STM32F4系列 180MHz 带LCD控制器', stock: 15 },
					{ model: 'STM32H743VIT6', description: 'STM32H7系列 480MHz 双核', stock: 10 },
					{ model: 'STM32L476RGT6', description: 'STM32L4系列 80MHz 超低功耗', stock: 18 },
					{ model: 'STM32G474RET6', description: 'STM32G4系列 170MHz 混合信号', stock: 12 },
					{ model: 'STM32F030F4P6', description: 'STM32F0系列 48MHz 入门级', stock: 30 },
					{ model: 'STM32F767ZIT6', description: 'STM32F7系列 216MHz 高性能', stock: 8 },
					{ model: 'STM32WB55RGV6', description: 'STM32WB系列 64MHz 双核无线', stock: 14 }
				],
				opamp: [
					{ model: 'LM358', description: '双运算放大器 通用型', stock: 60 },
					{ model: 'LM324', description: '四运算放大器 通用型', stock: 45 },
					{ model: 'TL072', description: '双运算放大器 JFET输入', stock: 35 },
					{ model: 'TL074', description: '四运算放大器 JFET输入', stock: 30 },
					{ model: 'LM741', description: '单运算放大器 经典型', stock: 40 },
					{ model: 'AD8066', description: '双运算放大器 高速低噪声', stock: 25 },
					{ model: 'OPA2134', description: '双运算放大器 音频专用', stock: 20 },
					{ model: 'LM833', description: '双运算放大器 低噪声音频', stock: 18 },
					{ model: 'NE5532', description: '双运算放大器 高性能音频', stock: 22 }
				],
				regulator: [
					{ model: 'LM7805', description: '线性稳压器 +5V 1A', stock: 50 },
					{ model: 'LM7812', description: '线性稳压器 +12V 1A', stock: 40 },
					{ model: 'LM7905', description: '线性稳压器 -5V 1A', stock: 30 },
					{ model: 'LM317', description: '可调线性稳压器 1.2-37V 1.5A', stock: 35 },
					{ model: 'AMS1117-3.3', description: '低压差稳压器 3.3V 1A', stock: 60 },
					{ model: 'AMS1117-5.0', description: '低压差稳压器 5V 1A', stock: 55 },
					{ model: 'LM2596', description: '开关稳压器 降压型 3A', stock: 25 },
					{ model: 'XL4015', description: '开关稳压器 降压型 5A', stock: 20 },
					{ model: 'MT3608', description: '开关稳压器 升压型 2A', stock: 28 }
				],
				logic_gate: [
					{ model: '74HC00', description: '四2输入与非门', stock: 40 },
					{ model: '74HC02', description: '四2输入或非门', stock: 35 },
					{ model: '74HC04', description: '六反相器', stock: 45 },
					{ model: '74HC08', description: '四2输入与门', stock: 38 },
					{ model: '74HC32', description: '四2输入或门', stock: 42 },
					{ model: '74HC86', description: '四2输入异或门', stock: 30 },
					{ model: '74HC138', description: '3-8线译码器', stock: 25 },
					{ model: '74HC595', description: '8位移位寄存器', stock: 35 },
					{ model: '74HC574', description: '8位D触发器', stock: 28 }
				],
				semiconductor: [
					{ model: '1N4007', description: '整流二极管 1000V 1A', stock: 100 },
					{ model: '1N4148', description: '开关二极管 75V 150mA', stock: 80 },
					{ model: '1N5819', description: '肖特基二极管 40V 1A', stock: 60 },
					{ model: 'LED-5mm-Red', description: '发光二极管 5mm 红色', stock: 150 },
					{ model: 'LED-5mm-Green', description: '发光二极管 5mm 绿色', stock: 120 },
					{ model: '2N3904', description: 'NPN三极管 40V 200mA', stock: 75 },
					{ model: '2N3906', description: 'PNP三极管 40V 200mA', stock: 70 },
					{ model: 'IRF540N', description: 'N沟道MOSFET 100V 33A', stock: 25 },
					{ model: 'LM358', description: '双运算放大器', stock: 40 },
					{ model: 'LM555', description: '定时器IC', stock: 35 },
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
		toggleCategory(category) {
			this.expandedCategories[category] = !this.expandedCategories[category]
		},
		applyComponent(item) {
			if (item.stock <= 0) {
				uni.showToast({
					title: '库存不足',
					icon: 'error'
				})
				return
			}
			
			uni.navigateTo({
				url: `/pages/warehouse/apply-process?code=${item.model}&name=${encodeURIComponent(item.description)}`
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

/* 分类区域 */
.category-section {
	padding: 20rpx 30rpx 30rpx;
}

.category-group {
	margin-bottom: 20rpx;
	background: white;
	border-radius: 16rpx;
	overflow: hidden;
	box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.1);
}

.category-header {
	display: flex;
	align-items: center;
	padding: 30rpx;
	background: linear-gradient(135deg, #2196F3, #1976D2);
	color: white;
	cursor: pointer;
}

.category-icon {
		width: 40rpx;
		height: 40rpx;
		margin-right: 20rpx;
	}

.category-title {
	flex: 1;
	font-size: 32rpx;
	font-weight: bold;
}

.expand-icon {
	font-size: 24rpx;
	transition: transform 0.3s;
}

.expand-icon.expanded {
	transform: rotate(180deg);
}

.category-content {
	padding: 0 30rpx 20rpx;
	background: white;
}

.subcategory {
	margin-bottom: 30rpx;
}

.subcategory-title {
	font-size: 28rpx;
	font-weight: bold;
	color: #2196F3;
	margin-top: 30rpx;
	margin-bottom: 15rpx;
	display: block;
}

.component-grid {
	display: grid;
	grid-template-columns: 1fr 1fr;
	gap: 20rpx;
	padding: 20rpx 0;
}

.component-item {
	background: #f8f9fa;
	border-radius: 12rpx;
	padding: 20rpx;
	border: 2rpx solid #e9ecef;
	transition: all 0.3s;
	cursor: pointer;
	position: relative;
}

.component-item:hover {
	border-color: #2196F3;
	box-shadow: 0 4rpx 12rpx rgba(33, 150, 243, 0.2);
	transform: translateY(-2rpx);
}

.component-info {
	margin-bottom: 15rpx;
}

.component-model {
	font-size: 28rpx;
	font-weight: bold;
	color: #2196F3;
	display: block;
	margin-bottom: 8rpx;
}

.component-desc {
	font-size: 24rpx;
	color: #666;
	display: block;
	margin-bottom: 8rpx;
	line-height: 1.4;
}

.component-stock {
	font-size: 22rpx;
	color: #28a745;
	display: block;
}

.apply-btn {
	background: linear-gradient(135deg, #2196F3, #1976D2);
	color: white;
	padding: 12rpx 20rpx;
	border-radius: 8rpx;
	font-size: 24rpx;
	text-align: center;
	font-weight: bold;
	transition: all 0.3s;
}

.component-item:hover .apply-btn {
	background: linear-gradient(135deg, #1976D2, #1565C0);
	transform: scale(1.05);
}
</style>