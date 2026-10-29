#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
智慧实验室电子班牌系统 - 演示数据种子脚本

用法：
    venv/bin/python seed_demo_data.py            # 仅在空库时写入
    venv/bin/python seed_demo_data.py --force    # 清空全部业务表后重新写入

数据口径：
    - 时间统一使用服务器本地时间（Asia/Shanghai），与 models.beijing_now() 一致
    - 外键顺序：角色 -> 用户 -> 资料 -> 实验室 -> 环境 -> 公告 -> 项目 -> 设备 -> 使用/考勤
      -> 课程 -> 安全须知 -> AI 方案 -> 物料分类 -> 物料 -> 申领单 -> 出入库 -> 维修工单 -> 值班
    - 物料库存与出入库流水逐笔自洽（before_quantity / after_quantity 链式推进）
"""

import argparse
import math
import sys
from datetime import date, datetime, time, timedelta
from decimal import Decimal

from app import create_app
from app.extensions import db
from app.models import (
    AITrainingPlan, Announcement, AttendanceLog, Device, DeviceCategory, DeviceUsageLog,
    DutySchedule, EnvironmentalLog, Lab, MaintenanceOrder, Material, MaterialCategory,
    MaterialRequest, MaterialTransaction, Project, ProjectMember, Role, SafetyGuideline,
    Schedule, User, UserProfile, UserRole,
)

NOW = datetime.now().replace(second=0, microsecond=0)
TODAY = NOW.date()

PWD_STAFF = 'Campus@2026'
PWD_ADMIN = 'Admin@2026'

# 依赖顺序（写入正序，清理倒序）
TABLES = [
    Role, User, UserRole, UserProfile, Lab, EnvironmentalLog, Announcement, Project,
    ProjectMember, DeviceCategory, Device, DeviceUsageLog, AttendanceLog, Schedule,
    SafetyGuideline, AITrainingPlan, MaterialCategory, Material, MaterialRequest,
    MaterialTransaction, MaintenanceOrder, DutySchedule,
]


def weekday_dates(start: date, days: int):
    """返回从 start 起若干天内的所有工作日"""
    return [start + timedelta(days=i) for i in range(days)
            if (start + timedelta(days=i)).weekday() < 5]


def reset_all():
    # 物料分类存在自引用外键，先断开父级引用再整体删除
    MaterialCategory.query.update({MaterialCategory.parent_id: None})
    db.session.commit()
    for model in reversed(TABLES):
        model.query.delete()
        db.session.commit()


def seed():
    # ---------------- 角色 ----------------
    roles = {
        'admin': Role(role_name='admin', permissions=['all']),
        'teacher': Role(role_name='teacher', permissions=[
            'announcement:create', 'announcement:update', 'device:manage',
            'material:approve', 'maintenance:assign', 'report:view']),
        'maintainer': Role(role_name='maintainer', permissions=[
            'device:update', 'maintenance:handle', 'material:issue', 'report:view']),
        'student': Role(role_name='student', permissions=[
            'announcement:view', 'attendance:checkin', 'material:request', 'maintenance:report']),
    }
    db.session.add_all(roles.values())
    db.session.flush()

    # ---------------- 用户 ----------------
    user_specs = [
        # username, 姓名, 专业, 班级, 邮箱, 手机, 密码, 角色
        ('admin', '系统管理员', None, None, 'admin@ecjtu.edu.cn', '0791-87045612', PWD_ADMIN, 'admin'),
        ('zhanghaiyang', '张海洋', '通信工程', None, 'zhanghy@ecjtu.edu.cn', '13907091208', PWD_STAFF, 'teacher'),
        ('liwenbo', '李文博', '电子信息工程', None, 'liwb@ecjtu.edu.cn', '13879106632', PWD_STAFF, 'teacher'),
        ('sunjianguo', '孙建国', '电子信息工程', None, 'sunjg@ecjtu.edu.cn', '13707095520', PWD_STAFF, 'maintainer'),
        ('2022210101', '李明轩', '通信工程', '通信2022-1班', 'li.mingxuan@ecjtu.edu.cn', '13907091234', PWD_STAFF, 'student'),
        ('2022210102', '王雨桐', '通信工程', '通信2022-1班', 'wang.yutong@ecjtu.edu.cn', '13907092346', PWD_STAFF, 'student'),
        ('2022210103', '陈嘉豪', '电子信息工程', '电信2022-2班', 'chen.jiahao@ecjtu.edu.cn', '13907093457', PWD_STAFF, 'student'),
        ('2023210104', '刘诗雨', '通信工程', '通信2023-1班', 'liu.shiyu@ecjtu.edu.cn', '13907094568', PWD_STAFF, 'student'),
        ('2023210105', '赵子豪', '电子信息科学与技术', '电科2023-2班', 'zhao.zihao@ecjtu.edu.cn', '13907095679', PWD_STAFF, 'student'),
        ('2024210106', '周静怡', '电子信息工程', '电信2024-1班', 'zhou.jingyi@ecjtu.edu.cn', '13907096780', PWD_STAFF, 'student'),
        ('2024210107', '吴俊杰', '通信工程', '通信2024-1班', 'wu.junjie@ecjtu.edu.cn', '13907097891', PWD_STAFF, 'student'),
    ]
    users = {}
    for username, full_name, major, class_name, email, phone, pwd, role in user_specs:
        u = User(username=username, full_name=full_name, major=major, class_name=class_name,
                 email=email, phone_number=phone, created_at=NOW - timedelta(days=120))
        u.set_password(pwd)
        db.session.add(u)
        db.session.flush()
        users[username] = u
        db.session.add(UserRole(user_id=u.id, role_id=roles[role].id))
    db.session.commit()

    # ---------------- 用户资料 ----------------
    profile_specs = [
        ('zhanghaiyang', '男', date(1981, 3, 12), '基地负责人 / 指导教师', None,
         ['通信系统', '射频电路', '信号处理']),
        ('liwenbo', '男', date(1986, 7, 25), '指导教师', None,
         ['数字信号处理', 'FPGA', '嵌入式系统']),
        ('sunjianguo', '男', date(1979, 11, 3), '设备管理员', None,
         ['仪器计量', '设备维修', '电路调试']),
        ('2022210101', '男', date(2003, 5, 18), '基地学生负责人', '学8栋412室',
         ['STM32', '嵌入式C', 'LoRa']),
        ('2022210102', '女', date(2003, 9, 2), '项目组长', '学8栋305室',
         ['Altium Designer', 'PCB设计', '射频仿真']),
        ('2022210103', '男', date(2003, 2, 27), '基地成员', '学9栋217室',
         ['ESP32', 'Python', '边缘计算']),
        ('2023210104', '女', date(2004, 6, 14), '项目组长', '学6栋508室',
         ['HFSS', '天线设计', 'MATLAB']),
        ('2023210105', '男', date(2004, 3, 21), '基地成员', '学9栋312室',
         ['CST仿真', '射频测试', 'Verilog']),
        ('2024210106', '女', date(2005, 8, 9), '基地成员', '学6栋402室',
         ['Java', 'Flask', '前端开发']),
        ('2024210107', '男', date(2005, 1, 30), '基地成员', '学9栋405室',
         ['嵌入式C', 'FreeRTOS', 'Git']),
    ]
    for username, gender, birth, position, dorm, stack in profile_specs:
        db.session.add(UserProfile(
            user_id=users[username].id, gender=gender, birth_date=birth,
            position=position, dormitory=dorm, tech_stack=stack))

    # ---------------- 实验室 ----------------
    labs = [
        Lab(lab_name='通信电子创新基地（A301）',
            description='基地面向通信工程与电子信息工程专业学生开放，承担电子设计竞赛培训、'
                        '创新创业项目孵化与毕业设计实验支撑，现有工位 32 个，仪器设备 40 余台套。',
            culture_info={'motto': '以赛促学，以研促创',
                          'vision': '建成区域高校领先的通信电子实践创新平台',
                          'rules': ['设备使用需登记', '离开前关闭电源', '耗材按需申领，不得私自带离']},
            logo_url='/static/uploads/lab_a301.png'),
        Lab(lab_name='智能感知与嵌入式实验室（B208）',
            description='聚焦嵌入式系统、传感器网络与边缘计算方向，配备示波器、逻辑分析仪'
                        '与多种 MCU 开发平台，支持课程实验与学科竞赛。',
            culture_info={'motto': '从一颗芯片到一套系统',
                          'rules': ['焊接作业需在指定工位完成', '开发板借用不超过 3 天']},
            logo_url='/static/uploads/lab_b208.png'),
        Lab(lab_name='电子设计竞赛实训室（C105）',
            description='竞赛集训专用场地，配备高频仪器与快速制板设备，赛前封闭集训期间实行值班制。',
            culture_info={'motto': '四天三夜，一次成长',
                          'rules': ['集训期间 24 小时值守', '离场须交接未完成事项']},
            logo_url='/static/uploads/lab_c105.png'),
    ]
    db.session.add_all(labs)
    db.session.flush()

    # ---------------- 环境监测数据（近 24 小时，逐小时） ----------------
    for h in range(23, -1, -1):
        ts = (NOW - timedelta(hours=h)).replace(minute=0)
        hour = ts.hour
        day_wave = math.sin((hour - 6) / 24 * 2 * math.pi)
        temp = round(23.5 + 1.3 * day_wave, 2)
        hum = round(52.0 - 6.0 * day_wave, 2)
        light = round(520 + 180 * day_wave, 2) if 7 <= hour <= 18 else round(85 + 25 * abs(day_wave), 2)
        co2 = round(620 + 180 * max(0.0, math.sin((hour - 9) / 12 * math.pi)), 2)
        for sensor, value, unit in (('温度', temp, '℃'), ('湿度', hum, '%RH'),
                                    ('光照', light, 'lux'), ('CO2', co2, 'ppm')):
            db.session.add(EnvironmentalLog(sensor_type=sensor, value=Decimal(str(value)),
                                            unit=unit, timestamp=ts, lab_id=labs[0].id))

    # ---------------- 公告 ----------------
    announcements = [
        ('【重要】实验室安全用电专项检查通知',
         '定于 11 月 3 日（周二）上午 9:00 开展实验室安全用电专项检查，检查范围包括 A301、B208、'
         'C105 三个实验场地的配电箱、插座线路与大功率设备接地情况。请各工位提前整理线缆、'
         '移除私接插排，检查期间暂停焊接与高压试验。',
         '张海洋', '通知', True, NOW - timedelta(hours=3)),
        ('2026-2027 学年秋季学期实验室开放时间调整',
         '自 11 月 1 日起，A301 开放时间调整为工作日 8:00—22:00、周末 9:00—18:00；'
         'C105 竞赛集训期间凭值班表 24 小时开放。进入实验室须刷卡登记并佩戴工位牌。',
         '张海洋', '通知', True, NOW - timedelta(days=1, hours=2)),
        ('全国大学生电子设计竞赛校内选拔报名开始',
         '2027 年电赛校内选拔即日启动报名，面向通信工程、电子信息工程、电子信息科学与技术专业'
         '2024—2025 级本科生。选拔采用"理论笔试 + 48 小时作品制作"两阶段，'
         '报名截止 11 月 10 日，报名材料提交至基地邮箱。',
         '李文博', '新闻', False, NOW - timedelta(days=2, hours=5)),
        ('基地新增 RIGOL DSA815 频谱分析仪投入使用',
         '型号 DSA815（9 kHz—1.5 GHz）已完成计量校准并接入 A301 柜 5，'
         '支持天线驻波比、谐波与杂散测试。使用前请在值班登记表登记，'
         '测试功率不得超过 +20 dBm。',
         '孙建国', '动态', False, NOW - timedelta(days=4)),
        ('高频仪器操作安全提示',
         '近期发现个别同学在未断开信号源的情况下插拔射频接头，存在损坏功放模块的风险。'
         '规范要求：先降功率、后断激励、再拆接头；示波器探头严禁接入 220V 市电测量。',
         '张海洋', '安全提示', True, NOW - timedelta(days=5, hours=6)),
        ('南昌未来三天天气提示',
         '受冷空气影响，11 月 1 日至 3 日气温下降 6—8℃，最低气温 12℃ 左右并伴有小雨。'
         '实验室空调将切换至送风模式，请注意增减衣物，雨天进出请清理鞋底积水防止滑倒。',
         '基地办公室', '天气提示', False, NOW - timedelta(days=3)),
        ('行业动态：太赫兹通信关键器件研究取得阶段性进展',
         '国内团队在 220 GHz 频段完成了基于磷化铟工艺的低噪声放大器流片测试，'
         '噪声系数优于 6 dB。相关方向与基地射频电路课程内容重合，可作为拓展阅读。',
         '李文博', '新闻', False, NOW - timedelta(days=6)),
        ('纸上得来终觉浅，绝知此事要躬行。',
         '—— 陆游《冬夜读书示子聿》。做硬件尤其如此：仿真跑通只是开始，'
         '焊上板子、测得波形，才算真正入门。',
         '基地办公室', '名言金句', False, NOW - timedelta(days=7)),
    ]
    for title, content, author, atype, important, created in announcements:
        db.session.add(Announcement(title=title, content=content, author_name=author,
                                    type=atype, is_important=important, created_at=created))

    # ---------------- 项目成果 ----------------
    projects = [
        Project(project_name='基于 LoRa 与边缘计算的实验室环境监测系统',
                description='面向高校实验室的多点环境监测方案，节点采集温湿度、光照与 CO2 数据，'
                            '经 LoRa 汇聚至边缘网关后入库并推送至电子班牌展示。',
                achievement_type='获奖',
                achievement_details='2025 年全国大学生电子设计竞赛（江西赛区）一等奖；'
                                    '省级大学生创新创业训练计划结题优秀',
                start_date=date(2024, 9, 1), end_date=date(2025, 8, 20),
                image_url='/static/uploads/project_lora.png'),
        Project(project_name='一种可重构多频段天线调谐电路',
                description='通过可切换匹配网络实现 433 MHz / 915 MHz / 2.4 GHz 三频段共用天线，'
                            '兼顾小型化与效率，适用于多模物联网终端。',
                achievement_type='专利',
                achievement_details='实用新型专利 ZL 2024 2 0135792.6，2024-11-08 授权',
                start_date=date(2024, 3, 10), end_date=date(2024, 10, 28),
                image_url='/static/uploads/project_antenna.png'),
        Project(project_name='智慧实验室班牌管理平台 V1.0',
                description='面向实验室日常管理的班牌系统，覆盖公告发布、考勤签到、设备借用、'
                            '物料申领与维修工单，服务端基于 Flask，终端为 Android 电子班牌。',
                achievement_type='软著',
                achievement_details='计算机软件著作权登记号 2025SR0123456，2025-03-21 登记',
                start_date=date(2024, 11, 1), end_date=date(2025, 3, 15),
                image_url='/static/uploads/project_banpai.png'),
    ]
    db.session.add_all(projects)
    db.session.flush()
    memberships = [
        (0, '2022210101', '项目负责人'), (0, '2022210102', '硬件设计'),
        (0, '2022210103', '嵌入式软件'),
        (1, '2023210104', '第一发明人'), (1, '2023210105', '仿真与测试'),
        (2, '2024210106', '后端开发'), (2, '2024210107', '终端与联调'),
    ]
    for pidx, uname, role_in in memberships:
        db.session.add(ProjectMember(project_id=projects[pidx].id,
                                     user_id=users[uname].id,
                                     role_in_project=role_in))

    # ---------------- 设备分类与设备 ----------------
    cat_names = ['示波器', '信号发生器', '万用表', '可编程直流电源', '单片机开发板', '频谱分析仪']
    cats = {n: DeviceCategory(category_name=n) for n in cat_names}
    db.session.add_all(cats.values())
    db.session.flush()

    device_specs = [
        # 名称, 分类, 型号, 状态, 位置, 购置日期
        ('数字示波器', '示波器', 'RIGOL DS1104Z（100 MHz / 4 通道）', '可用', 'A301 柜1-01', date(2023, 9, 12)),
        ('数字示波器', '示波器', 'Tektronix TBS1102B（100 MHz / 2 通道）', '可用', 'A301 柜1-02', date(2021, 3, 5)),
        ('函数信号发生器', '信号发生器', 'RIGOL DG1062Z（60 MHz 双通道）', '使用中', 'A301 柜2-01', date(2023, 9, 12)),
        ('函数信号发生器', '信号发生器', 'RIGOL DG822（25 MHz 单通道）', '可用', 'A301 柜2-02', date(2022, 6, 18)),
        ('数字万用表', '万用表', 'FLUKE 17B+', '可用', 'A301 柜3-01', date(2022, 6, 18)),
        ('数字万用表', '万用表', '优利德 UT61E', '维修中', 'A301 维修台', date(2020, 11, 20)),
        ('可编程直流电源', '可编程直流电源', 'RIGOL DP832（三路 30V/3A）', '可用', 'A301 柜4-01', date(2023, 9, 12)),
        ('三路直流稳压电源', '可编程直流电源', '固纬 GPS-3303C', '可用', 'A301 柜4-02', date(2019, 5, 16)),
        ('STM32 开发板', '单片机开发板', '正点原子 STM32F407 探索者', '维修中', 'B208 柜1-03', date(2024, 3, 8)),
        ('ESP32 开发板', '单片机开发板', 'Espressif ESP32-S3-DevKitC-1', '可用', 'B208 柜1-04', date(2024, 3, 8)),
        ('频谱分析仪', '频谱分析仪', 'RIGOL DSA815（9 kHz—1.5 GHz）', '可用', 'A301 柜5-01', date(2025, 9, 20)),
    ]
    device_objs = []
    for name, cat, model, status, loc, purchased in device_specs:
        d = Device(device_name=name, category_id=cats[cat].id, model=model, status=status,
                   location=loc, purchase_date=purchased)
        db.session.add(d)
        db.session.flush()
        device_objs.append(d)
    # 设备 ID 与 device_specs 顺序一致：1 示波器 DS1104Z ... 11 频谱仪 DSA815
    dev_ids = {i + 1: d for i, d in enumerate(device_objs)}
    db.session.commit()

    # ---------------- 设备使用记录 ----------------
    usage_specs = [
        (2, '2022210103', NOW - timedelta(days=3, hours=5), NOW - timedelta(days=3, hours=-1),
         '电子测量技术实验：李萨如图形观测'),
        (3, '2023210105', NOW - timedelta(days=1, hours=6), None,
         '天线调谐电路扫频测试，预计 21:00 前归还'),
        (10, '2022210101', NOW - timedelta(days=5, hours=4), NOW - timedelta(days=5, hours=-3),
         '环境监测节点固件调试'),
        (5, '2024210106', NOW - timedelta(days=2, hours=7), NOW - timedelta(days=2, hours=5),
         '班牌终端电源纹波测试'),
    ]
    for dev_index, uname, checkout, checkin, notes in usage_specs:
        db.session.add(DeviceUsageLog(device_id=dev_ids[dev_index].id, user_id=users[uname].id,
                                      checkout_time=checkout, checkin_time=checkin, notes=notes))

    # ---------------- 考勤记录 ----------------
    students = ['2022210101', '2022210102', '2022210103', '2023210104', '2023210105']
    methods = ['人脸识别', '扫码', '手动']
    emotions = ['专注', '平静', '专注', '疲劳']
    for idx, day in enumerate(weekday_dates(TODAY - timedelta(days=9), 10)):
        for sidx, uname in enumerate(students):
            if (idx + sidx) % 7 == 6:
                continue  # 少量缺勤，保持数据真实
            in_t = datetime.combine(day, time(8, 20 + (sidx * 7 + idx * 3) % 30))
            out_t = datetime.combine(day, time(17, 5 + (sidx * 5 + idx * 4) % 50))
            db.session.add(AttendanceLog(
                user_id=users[uname].id, check_in_time=in_t, check_out_time=out_t,
                method=methods[(idx + sidx) % len(methods)],
                emotion_status=emotions[(idx + sidx) % len(emotions)]))

    # ---------------- 课程表（未来 10 天工作日） ----------------
    weekly_plan = {
        0: [('高频电子线路', '张海洋', time(8, 0), time(9, 40), 'A301'),
            ('通信原理', '李文博', time(10, 0), time(11, 40), 'B208')],
        1: [('数字信号处理', '李文博', time(8, 0), time(9, 40), 'C105'),
            ('单片机原理与应用', '张海洋', time(14, 0), time(15, 40), 'A301')],
        2: [('电子设计自动化（EDA）', '张海洋', time(10, 0), time(11, 40), 'C105')],
        3: [('通信原理', '李文博', time(8, 0), time(9, 40), 'B208'),
            ('电子测量技术', '孙建国', time(14, 0), time(15, 40), 'A301')],
        4: [('学术研讨与项目中期答辩', '张海洋', time(15, 0), time(17, 0), 'A301')],
    }
    for offset in range(10):
        day = TODAY + timedelta(days=offset)
        for course, teacher, start, end, loc in weekly_plan.get(day.weekday(), []):
            db.session.add(Schedule(course_name=course, teacher_name=teacher, class_date=day,
                                    start_time=start, end_time=end, location=loc))

    # ---------------- 安全须知 ----------------
    safety = [
        ('用电安全', '实验室用电与配电规范',
         '一、实验台配电容量为 2 kW，禁止使用电炉、热得快等大功率电器。'
         '二、仪器必须接入带保护接地的三孔插座，禁止使用无地线插排或多级串联插排。'
         '三、焊接台使用后须关闭烙铁电源，烙铁头冷却前不得离开工位。'
         '四、发现插座发烫、焦糊味或跳闸，立即断开工位总开关并报告值班人员。',
         'v2.1'),
        ('设备操作', '高频与精密仪器操作规范',
         '一、信号源接入功放前须确认输出功率与阻抗匹配，禁止空载满功率输出。'
         '二、示波器探头仅用于测量电路信号，严禁直接接入 220 V 市电。'
         '三、频谱分析仪输入电平不得超过 +20 dBm，前端需加限幅保护。'
         '四、仪器搬运须断电并固定探头，使用完毕按原位置归还并登记。',
         'v1.4'),
        ('应急处置', '触电与电气火灾应急处置流程',
         '一、发生触电时立即切断电源或用绝缘物挑开导线，严禁徒手拉扯触电者。'
         '二、拨打校医院急救电话并同时报告实验室负责人，记录发生时间与现场情况。'
         '三、电气火灾先断电后灭火，使用二氧化碳灭火器，严禁用水扑救。'
         '四、事故现场在调查完成前保持原状，配合填写事故报告。',
         'v2.0'),
        ('数据安全', '实验数据与账号管理要求',
         '一、班牌系统账号不得多人共用，离岗须退出登录。'
         '二、实验数据统一存放于基地共享目录，个人 U 盘接入前须查杀病毒。'
         '三、涉及专利与软著申报的数据须脱敏后外发，禁止上传至公共网盘。'
         '四、发现数据异常删除或越权访问，立即联系系统管理员并保留日志。',
         'v1.2'),
    ]
    for category, title, content, version in safety:
        db.session.add(SafetyGuideline(category=category, title=title,
                                       content=content, version=version))

    # ---------------- AI 个性化培养方案 ----------------
    plan_1 = {
        'goal': '12 周内具备独立完成 LoRa 环境监测节点软硬件设计与调试的能力',
        'mentor': '张海洋',
        'duration_weeks': 12,
        'focus': ['嵌入式C', 'STM32外设驱动', 'LoRa组网', '低功耗设计'],
        'weekly_tasks': [
            {'week': 1, 'task': 'STM32 时钟树与 GPIO 驱动，完成 LED 与按键实验', 'deliverable': '代码仓库 + 实验记录'},
            {'week': 2, 'task': 'USART/I2C 驱动，接入 SHT30 温湿度传感器', 'deliverable': '串口输出截图'},
            {'week': 3, 'task': 'ADC 采集与光照传感器标定', 'deliverable': '标定曲线'},
            {'week': 4, 'task': 'SX1276 LoRa 模块点对点通信', 'deliverable': '误码率测试表'},
        ],
        'resources': ['《STM32 嵌入式系统开发》', 'SX1276 数据手册', '基地仪器借用流程'],
        'assessment': ['周报提交率', '节点功耗实测', '网关联调通过率'],
    }
    plan_2 = {
        'goal': '10 周内完成三频段天线调谐电路的仿真、制板与实测闭环',
        'mentor': '李文博',
        'duration_weeks': 10,
        'focus': ['HFSS仿真', '阻抗匹配', 'PCB射频布局', '矢网测试'],
        'weekly_tasks': [
            {'week': 1, 'task': '学习 Smith 圆图与匹配网络拓扑', 'deliverable': '手算匹配参数'},
            {'week': 2, 'task': 'HFSS 建立单频段天线模型并仿真 S11', 'deliverable': '仿真报告'},
            {'week': 3, 'task': '设计 PIN 二极管切换电路', 'deliverable': '原理图'},
        ],
        'resources': ['《射频电路设计》', 'HFSS 官方教程', 'DSA815 使用手册'],
        'assessment': ['S11 ≤ -10 dB 达标频段数', '插损实测', '答辩表现'],
    }
    db.session.add_all([
        AITrainingPlan(user_id=users['2022210101'].id, plan_content=plan_1,
                       generated_at=NOW - timedelta(days=12), status='进行中'),
        AITrainingPlan(user_id=users['2023210104'].id, plan_content=plan_2,
                       generated_at=NOW - timedelta(days=25), status='进行中'),
    ])

    # ---------------- 物料分类 ----------------
    cat_elec = MaterialCategory(name='电子元器件', description='电阻、电容、二极管等基础器件',
                                parent_id=None, sort_order=1, created_at=NOW - timedelta(days=180))
    cat_mcu = MaterialCategory(name='芯片与模块', description='MCU、通信模块、电源模块',
                               parent_id=None, sort_order=2, created_at=NOW - timedelta(days=180))
    cat_wire = MaterialCategory(name='线材与接插件', description='杜邦线、排针、SMA 接头',
                                parent_id=None, sort_order=3, created_at=NOW - timedelta(days=180))
    cat_tool = MaterialCategory(name='工具与耗材', description='焊锡、热缩管、镊子等',
                                parent_id=None, sort_order=4, created_at=NOW - timedelta(days=180))
    cat_passive = MaterialCategory(name='电阻与电容', description='贴片/插件阻容元件',
                                   parent_id=None, sort_order=5, created_at=NOW - timedelta(days=180))
    db.session.add_all([cat_elec, cat_mcu, cat_wire, cat_tool, cat_passive])
    db.session.flush()
    cat_passive.parent_id = cat_elec.id
    db.session.flush()

    # ---------------- 物料 ----------------
    material_specs = [
        # code, 名称, 分类, 单位, 库存, 最小, 最大, 单价, 位置, 供应商
        ('RES-0805-KIT', '0805 贴片电阻包（1/4 W 1% 常用阻值）', cat_passive, '包', 45, 10, 100, '12.50', 'A301 元件柜 A-1', '立创商城'),
        ('CAP-0805-KIT', '0805 贴片电容包（50 V 常用容值）', cat_passive, '包', 32, 10, 80, '15.00', 'A301 元件柜 A-2', '立创商城'),
        ('MCU-STM32F103', 'STM32F103C8T6 最小系统板', cat_mcu, '块', 38, 10, 120, '26.00', 'A301 元件柜 B-1', '正点原子'),
        ('MCU-ESP32S3', 'ESP32-S3-DevKitC-1 开发板', cat_mcu, '块', 22, 8, 80, '45.00', 'A301 元件柜 B-2', '乐鑫官方店'),
        ('WIRE-DUPONT20', '杜邦线 20 cm 公对公（40 根）', cat_wire, '排', 6, 10, 60, '3.50', 'A301 元件柜 C-1', '深圳华强电子'),
        ('SOLDER-08-500', '焊锡丝 0.8 mm 500 g（含松香）', cat_tool, '卷', 3, 5, 20, '68.00', 'A301 耗材柜 D-1', '得力工具'),
        ('SERVO-SG90', 'SG90 微型舵机（180°）', cat_mcu, '个', 0, 5, 30, '6.50', 'A301 元件柜 B-3', '塔菲尔电子'),
        ('ADAPT-CH340', 'USB 转 TTL 模块 CH340', cat_mcu, '个', 15, 5, 40, '9.80', 'A301 元件柜 B-4', '沁恒官方店'),
    ]
    materials = []
    for code, name, cat, unit, stock, mn, mx, price, loc, supplier in material_specs:
        m = Material(code=code, name=name, category_id=cat.id, unit=unit, stock_quantity=stock,
                     min_stock=mn, max_stock=mx, unit_price=Decimal(price), location=loc,
                     supplier=supplier, created_at=NOW - timedelta(days=150))
        db.session.add(m)
        materials.append(m)
    db.session.flush()

    # ---------------- 物料申领单 ----------------
    def mat_item(index, quantity, unit):
        """按物料序号生成申领明细，ID 取真实主键"""
        material = materials[index - 1]
        return {'material_id': material.id, 'name': material.name,
                'quantity': quantity, 'unit': unit}

    req_specs = [
        ('SQ20261021001', '2022210101', '基于 LoRa 与边缘计算的实验室环境监测系统', 'issued',
         [mat_item(3, 1, '块'), mat_item(4, 2, '块')],
         date.today() + timedelta(days=20), '节点调试使用，验收后归还开发板', 'zhanghaiyang',
         NOW - timedelta(days=7, hours=2), '同意领用，开发板用完归还。'),
        ('SQ20261023002', '2022210102', '一种可重构多频段天线调谐电路', 'issued',
         [mat_item(5, 6, '排'), mat_item(3, 1, '块')],
         date.today() + timedelta(days=14), '射频切换板调试用线材', 'sunjianguo',
         NOW - timedelta(days=5, hours=4), '已按单发放。'),
        ('SQ20261026003', '2022210103', '智慧实验室班牌管理平台 V1.0', 'issued',
         [mat_item(7, 1, '个')],
         None, '班牌外壳开合演示机构使用', 'zhanghaiyang',
         NOW - timedelta(days=2, hours=6), '最后一只，领用后请登记。'),
        ('SQ20261027004', '2023210104', '一种可重构多频段天线调谐电路', 'pending',
         [mat_item(1, 2, '包')],
         None, '匹配网络调阻值试验，含 50 Ω/75 Ω 常用值', None, None, None),
        ('SQ20261028005', '2024210106', '智慧实验室班牌管理平台 V1.0', 'rejected',
         [mat_item(2, 6, '包')],
         None, '想一次领够一学期用量', None, None, None),
    ]
    requests = []
    for number, uname, proj_name, status, items, ret_date, notes, approver, approved_at, comment in req_specs:
        r = MaterialRequest(
            request_number=number, user_id=users[uname].id, project_name=proj_name,
            status=status, materials=items,
            approved_materials=items if status in ('approved', 'issued', 'returned', 'overdue') else None,
            expected_return_date=ret_date, notes=notes,
            approver_id=users[approver].id if approver else None,
            approved_at=approved_at, approval_comment=comment,
            created_at=(approved_at or NOW) - timedelta(hours=3))
        db.session.add(r)
        requests.append(r)
    db.session.flush()

    # ---------------- 出入库流水（与库存逐笔自洽） ----------------
    m = {mm.code: mm for mm in materials}
    tx = [
        (m['RES-0805-KIT'], 'in', 30, 15, 45, 'sunjianguo', None, '采购入库 PO-2026-0918', NOW - timedelta(days=40)),
        (m['CAP-0805-KIT'], 'in', 12, 20, 32, 'sunjianguo', None, '采购入库 PO-2026-0928', NOW - timedelta(days=30)),
        (m['MCU-STM32F103'], 'in', 20, 15, 35, 'sunjianguo', None, '采购入库 PO-2026-0918', NOW - timedelta(days=40)),
        (m['MCU-STM32F103'], 'in', 5, 35, 40, 'sunjianguo', None, '竞赛集训补充采购', NOW - timedelta(days=19)),
        (m['MCU-STM32F103'], 'out', 1, 40, 39, 'zhanghaiyang', requests[0], '按 SQ20261021001 发放', NOW - timedelta(days=7, hours=2)),
        (m['MCU-STM32F103'], 'out', 1, 39, 38, 'sunjianguo', requests[1], '按 SQ20261023002 发放', NOW - timedelta(days=5, hours=4)),
        (m['MCU-ESP32S3'], 'in', 4, 20, 24, 'sunjianguo', None, '采购入库 PO-2026-0925', NOW - timedelta(days=33)),
        (m['MCU-ESP32S3'], 'out', 2, 24, 22, 'zhanghaiyang', requests[0], '按 SQ20261021001 发放', NOW - timedelta(days=7, hours=2)),
        (m['WIRE-DUPONT20'], 'out', 6, 12, 6, 'sunjianguo', requests[1], '按 SQ20261023002 发放', NOW - timedelta(days=5, hours=4)),
        (m['SOLDER-08-500'], 'out', 2, 5, 3, 'sunjianguo', None, '焊接实训耗用', NOW - timedelta(days=12)),
        (m['SERVO-SG90'], 'out', 1, 1, 0, 'zhanghaiyang', requests[2], '按 SQ20261026003 发放，库存清零', NOW - timedelta(days=2, hours=6)),
        (m['ADAPT-CH340'], 'in', 5, 10, 15, 'sunjianguo', None, '采购入库 PO-2026-1015', NOW - timedelta(days=13)),
    ]
    for material, ttype, qty, before, after, uname, req, notes, ts in tx:
        db.session.add(MaterialTransaction(
            material_id=material.id, transaction_type=ttype, quantity=qty,
            before_quantity=before, after_quantity=after, user_id=users[uname].id,
            request_id=req.id if req else None, notes=notes, created_at=ts))

    # ---------------- 维修工单 ----------------
    orders = [
        MaintenanceOrder(
            order_number='WX20261020001', device_id=dev_ids[1].id, fault_type='hardware',
            fault_description='CH2 通道接入自检信号无波形输出，CH1 正常；探头与线缆已互换验证，排除外部原因。',
            images=['/static/uploads/fault_ds1104z_ch2.jpg'], priority='high', status='completed',
            reporter_id=users['2023210104'].id, assignee_id=users['sunjianguo'].id,
            reported_at=NOW - timedelta(days=8, hours=3),
            assigned_at=NOW - timedelta(days=8, hours=1),
            expected_completion=NOW - timedelta(days=6),
            actual_completion=NOW - timedelta(days=5, hours=2),
            solution_description='更换 CH2 前端信号继电器与保护电阻，重新执行垂直增益校准，自检波形恢复正常。',
            maintenance_notes='更换后连续通电 4 小时无异常，已通知使用人复测。',
            completion_images=['/static/uploads/fixed_ds1104z.jpg'],
            parts_used=[{'name': '信号继电器 HF3FA/012-ZST', 'quantity': 1},
                        {'name': '贴片电阻 100 Ω 1%', 'quantity': 2}]),
        MaintenanceOrder(
            order_number='WX20261024002', device_id=dev_ids[6].id, fault_type='hardware',
            fault_description='交流电压档读数较标准源偏低约 3.5%，超出 1.0% 精度指标，疑似分压网络元件老化。',
            images=[], priority='medium', status='in_progress',
            reporter_id=users['2022210102'].id, assignee_id=users['sunjianguo'].id,
            reported_at=NOW - timedelta(days=4, hours=5),
            assigned_at=NOW - timedelta(days=4, hours=2),
            expected_completion=NOW + timedelta(days=3),
            solution_description=None, maintenance_notes='已拆机检查分压电阻，等待 0.1% 精密电阻到货。',
            completion_images=None, parts_used=None),
        MaintenanceOrder(
            order_number='WX20261027003', device_id=dev_ids[9].id, fault_type='power',
            fault_description='上电后电源指示灯不亮，5 V 输出为 0 V，USB 供电同样无反应，怀疑电源管理芯片击穿。',
            images=['/static/uploads/fault_f407_power.jpg'], priority='urgent', status='in_progress',
            reporter_id=users['2022210101'].id, assignee_id=users['sunjianguo'].id,
            reported_at=NOW - timedelta(days=1, hours=7),
            assigned_at=NOW - timedelta(days=1, hours=5),
            expected_completion=NOW + timedelta(days=2),
            solution_description=None, maintenance_notes='已定位 U1（AMS1117-3.3）输入对地短路，待更换。',
            completion_images=None, parts_used=None),
    ]
    db.session.add_all(orders)

    # ---------------- 值班安排 ----------------
    duty_days = [TODAY - timedelta(days=2), TODAY - timedelta(days=1), TODAY] + \
                [TODAY + timedelta(days=i) for i in range(1, 7)]
    shift_def = {
        'morning': (time(8, 0), time(12, 0), 'A301 值班台'),
        'afternoon': (time(14, 0), time(18, 0), 'A301 值班台'),
        'evening': (time(19, 0), time(22, 0), 'A301 值班台'),
    }
    duty_people = ['2022210101', '2022210102', '2022210103', '2023210104', '2023210105',
                   '2024210106', '2024210107']
    responsibilities = ['仪器借用登记与归还核验', '实验室安全检查（门窗、电源、气瓶）',
                        '异常情况上报与交接班记录']
    for di, day in enumerate(duty_days):
        for si, (shift, (start, end, loc)) in enumerate(shift_def.items()):
            # 当天的班次按当前时间判断是否已结束
            shift_finished = datetime.combine(day, end) <= NOW
            if day < TODAY or shift_finished:
                status = 'completed'
            else:
                status = 'scheduled'
            person = duty_people[(di * 3 + si) % len(duty_people)]
            ds = DutySchedule(
                user_id=users[person].id, duty_date=day, shift_type=shift,
                start_time=start, end_time=end, location=loc,
                responsibilities=responsibilities, status=status,
                created_at=NOW - timedelta(days=6))
            if status == 'completed':
                ds.check_in_time = datetime.combine(day, start) + timedelta(minutes=4)
                ds.check_in_location = 'A301 门口闸机'
                ds.check_in_notes = '按排班到岗，仪器台账已核对'
                ds.check_out_time = datetime.combine(day, end) - timedelta(minutes=3)
                ds.summary = ('完成当日仪器借用登记 4 次，收回示波器探头 2 套；'
                              '协助 2 名同学完成开发板归还核验。')
                ds.issues = [] if di % 2 == 0 else [
                    {'device': '优利德 UT61E 万用表', 'issue': '交流档读数异常，已报修（WX20261024002）'}]
                ds.handover_notes = '晚班同学注意：A301 空调滤网待清洗，明日联系后勤。'
            else:
                ds.notes = '按周排班执行，如需调班提前一天在班牌端提交申请'
            db.session.add(ds)

    db.session.commit()


def summarize():
    print('%-26s %6s' % ('表名', '行数'))
    print('-' * 34)
    for model in TABLES:
        print('%-26s %6d' % (model.__tablename__, model.query.count()))


def main():
    parser = argparse.ArgumentParser(description='智慧实验室电子班牌系统 - 演示数据种子')
    parser.add_argument('--force', action='store_true', help='清空业务表后重新写入')
    args = parser.parse_args()

    app = create_app()
    with app.app_context():
        existing = {m.__tablename__: m.query.count() for m in TABLES}
        non_empty = {k: v for k, v in existing.items() if v}
        if non_empty and not args.force:
            print('检测到已有数据，未做修改。加 --force 可清空重灌：')
            for k, v in non_empty.items():
                print('  %s: %d' % (k, v))
            return 1
        if non_empty:
            print('清空已有业务数据...')
            reset_all()
        seed()
        print('演示数据写入完成。\n')
        summarize()
        return 0


if __name__ == '__main__':
    sys.exit(main())
