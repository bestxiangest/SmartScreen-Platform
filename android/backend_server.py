#!/usr/bin/env python3
# -*- coding: utf-8 -*-

from flask import Flask, jsonify, request
from flask_cors import CORS
from datetime import datetime
import json

app = Flask(__name__)
CORS(app)  # 允许跨域请求

# 模拟公告数据
announcements_data = [
    {
        "id": 1,
        "title": "【重要】实验室安全检查通知",
        "content": "为确保实验室安全，将于本周五进行全面安全检查，请各位同学配合。",
        "author_name": "实验室管理员",
        "type": "通知",
        "is_important": True,
        "created_at": "2024-12-20T10:00:00Z"
    },
    {
        "id": 2,
        "title": "实验室设备维护公告",
        "content": "部分实验设备将于下周一进行定期维护，预计维护时间2小时。",
        "author_name": "技术部",
        "type": "维护",
        "is_important": False,
        "created_at": "2024-12-19T14:30:00Z"
    },
    {
        "id": 3,
        "title": "新设备到货通知",
        "content": "实验室新采购的高精度测量设备已到货，将于本周内完成安装调试。",
        "author_name": "采购部",
        "type": "新闻",
        "is_important": False,
        "created_at": "2024-12-18T09:15:00Z"
    },
    {
        "id": 4,
        "title": "实验室开放时间调整",
        "content": "由于节假日安排，实验室开放时间临时调整为上午9:00-下午5:00。",
        "author_name": "管理办公室",
        "type": "通知",
        "is_important": True,
        "created_at": "2024-12-17T16:45:00Z"
    },
    {
        "id": 5,
        "title": "学术讲座预告",
        "content": "著名学者将于下周三在实验室举办学术讲座，欢迎大家参加。",
        "author_name": "学术委员会",
        "type": "讲座",
        "is_important": False,
        "created_at": "2024-12-16T11:20:00Z"
    },
    {
        "id": 6,
        "title": "实验室清洁日安排",
        "content": "每月例行的实验室清洁日定于本月最后一个周五，请大家积极参与。",
        "author_name": "后勤部",
        "type": "活动",
        "is_important": False,
        "created_at": "2024-12-15T08:30:00Z"
    }
]

@app.route('/', methods=['GET'])
def home():
    return jsonify({
        "name": "智慧实验室电子班牌系统",
        "version": "1.0.0",
        "description": "智慧实验室电子班牌管理系统"
    })

@app.route('/health', methods=['GET'])
def health_check():
    return jsonify({
        "status": "healthy",
        "timestamp": datetime.now().isoformat()
    })

@app.route('/api/v1/test', methods=['GET'])
def api_test():
    return jsonify({
        "success": True,
        "message": "API测试成功",
        "data": {
            "timestamp": datetime.now().isoformat(),
            "server": "Flask Development Server"
        }
    })

@app.route('/api/v1/announcements', methods=['GET'])
def get_announcements():
    try:
        # 获取查询参数
        page = int(request.args.get('page', 1))
        limit = int(request.args.get('limit', 10))
        announcement_type = request.args.get('type')
        is_important = request.args.get('is_important')
        
        print(f"收到公告列表请求: page={page}, limit={limit}, type={announcement_type}, is_important={is_important}")
        
        # 过滤数据
        filtered_data = announcements_data.copy()
        
        if announcement_type:
            filtered_data = [a for a in filtered_data if a['type'] == announcement_type]
        
        if is_important is not None:
            is_important_bool = is_important.lower() == 'true'
            filtered_data = [a for a in filtered_data if a['is_important'] == is_important_bool]
        
        # 分页
        start_index = (page - 1) * limit
        end_index = start_index + limit
        paginated_data = filtered_data[start_index:end_index]
        
        total = len(filtered_data)
        total_pages = (total + limit - 1) // limit
        
        response_data = {
            "code": 200,
            "success": True,
            "message": "获取通知公告列表成功",
            "data": {
                "items": paginated_data,
                "pagination": {
                    "page": page,
                    "limit": limit,
                    "total": total,
                    "total_pages": total_pages,
                    "has_next": page < total_pages,
                    "has_prev": page > 1
                }
            }
        }
        
        print(f"返回 {len(paginated_data)} 条公告数据")
        return jsonify(response_data)
        
    except Exception as e:
        print(f"获取公告列表时发生错误: {str(e)}")
        return jsonify({
            "code": 500,
            "success": False,
            "message": f"服务器内部错误: {str(e)}"
        }), 500

@app.route('/api/v1/announcements/<int:announcement_id>', methods=['GET'])
def get_announcement_detail(announcement_id):
    try:
        print(f"收到公告详情请求: ID={announcement_id}")
        
        # 查找指定ID的公告
        announcement = next((a for a in announcements_data if a['id'] == announcement_id), None)
        
        if not announcement:
            return jsonify({
                "code": 404,
                "success": False,
                "message": "公告不存在"
            }), 404
        
        return jsonify({
            "code": 200,
            "success": True,
            "message": "获取通知公告详情成功",
            "data": announcement
        })
        
    except Exception as e:
        print(f"获取公告详情时发生错误: {str(e)}")
        return jsonify({
            "code": 500,
            "success": False,
            "message": f"服务器内部错误: {str(e)}"
        }), 500

if __name__ == '__main__':
    print("启动智慧实验室电子班牌系统后端服务器...")
    print("API地址: http://localhost:5000")
    print("健康检查: http://localhost:5000/health")
    print("API测试: http://localhost:5000/api/v1/test")
    print("公告列表: http://localhost:5000/api/v1/announcements")
    app.run(host='0.0.0.0', port=5000, debug=True)