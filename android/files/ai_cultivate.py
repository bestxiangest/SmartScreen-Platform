import requests
import time
import os
import json

# 配置API的URL
STUDY_PLAN_API = "http://sys.sharpcaterpillar.top:7878/api/study_plan"
IMAGE_GENERATION_KEY = "YOUR_DASHSCOPE_API_KEY"
IMAGE_API_BASE_URL = "https://dashscope.aliyuncs.com/api/v1"


# 1. 调用培养方案生成API
def generate_study_plan(student_data):
    """生成学生个性化培养方案"""
    headers = {"Content-Type": "application/json"}
    response = requests.post(STUDY_PLAN_API, headers=headers, json=student_data)

    if response.status_code != 200:
        error_msg = response.json().get("message", "未知错误")
        raise Exception(f"培养方案生成失败: {error_msg}")

    return response.json()


# 2. 根据培养方案生成图表提示词
def create_chart_prompt(study_plan_data):
    """根据培养方案创建图表提示词"""
    # 提取关键信息
    student_info = study_plan_data.get("student_info", {})
    development_plan = study_plan_data.get("development_plan", {})

    # 安全获取各个部分的数据
    timeline_goals = development_plan.get("timelineGoals", {})
    learning_resources = development_plan.get("learningResources", {})
    skill_development = development_plan.get("skillDevelopment", {})
    career_guidance = development_plan.get("careerGuidance", {})
    personalized_advice = development_plan.get("personalizedAdvice", {})

    # 构建提示词 - 使用Markdown格式确保信息清晰
    prompt = (
        f"请创建包含以下多个维度的学生个性化培养方案图表：\n\n"
        f"# 学生基本信息\n"
        f"- 姓名: {student_info.get('name', '未知')}\n"
        f"- 年龄: {student_info.get('age', '未知')}\n"
        f"- 学历: {student_info.get('education', '未知')}\n"
        f"- 专业: {student_info.get('major', '未知')}\n"
        f"- 兴趣方向: {student_info.get('interests', '未知')}\n"
        f"- 学习目标: {student_info.get('study_objective', '未知')}\n\n"
    )

    # 添加发展计划部分（如果存在）
    if "developmentPlan" in development_plan:
        dev_plan = development_plan["developmentPlan"]

        prompt += (
            f"# 发展计划\n"
            f"## 推荐专业方向\n"
            f"- {', '.join(dev_plan.get('recommendedMajors', ['无数据'])[:3])}\n\n"

            f"## 核心能力培养\n"
            f"- {', '.join(dev_plan.get('coreCompetencies', ['无数据'])[:3])}\n\n"

            f"## 学习路径\n"
            f"- 基础阶段: {dev_plan.get('learningPath', {}).get('foundation', '无数据')}\n"
            f"- 中级阶段: {dev_plan.get('learningPath', {}).get('intermediate', '无数据')}\n"
            f"- 高级阶段: {dev_plan.get('learningPath', {}).get('advanced', '无数据')}\n\n"
        )

    # 添加时间目标规划
    prompt += (
        f"# 时间目标规划\n"
        f"## 短期目标 ({timeline_goals.get('shortTerm', {}).get('period', '未知')})\n"
        f"- {', '.join(timeline_goals.get('shortTerm', {}).get('goals', ['无数据'])[:3])}\n\n"

        f"## 中期目标 ({timeline_goals.get('mediumTerm', {}).get('period', '未知')})\n"
        f"- {', '.join(timeline_goals.get('mediumTerm', {}).get('goals', ['无数据'])[:3])}\n\n"

        f"## 长期目标 ({timeline_goals.get('longTerm', {}).get('period', '未知')})\n"
        f"- {', '.join(timeline_goals.get('longTerm', {}).get('goals', ['无数据'])[:3])}\n\n"
    )

    # 添加学习资源推荐
    prompt += (
        f"# 学习资源推荐\n"
        f"## 在线平台\n"
        f"- {', '.join(learning_resources.get('onlinePlatforms', ['无数据'])[:3])}\n\n"

        f"## 推荐书籍\n"
        f"- {', '.join(learning_resources.get('books', ['无数据'])[:3])}\n\n"

        f"## 实践项目\n"
        f"- {', '.join(learning_resources.get('practicalProjects', ['无数据'])[:3])}\n\n"
    )

    # 添加技能发展
    prompt += (
        f"# 技能发展\n"
        f"## 技术技能\n"
        f"- {', '.join(skill_development.get('technicalSkills', ['无数据'])[:3])}\n\n"

        f"## 软技能\n"
        f"- {', '.join(skill_development.get('softSkills', ['无数据'])[:3])}\n\n"

        f"## 认证证书\n"
        f"- {', '.join(skill_development.get('certifications', ['无数据'])[:3])}\n\n"
    )

    # 添加职业发展指导
    prompt += (
        f"# 职业发展指导\n"
        f"## 行业趋势\n"
        f"- {career_guidance.get('industryTrends', '无数据')}\n\n"

        f"## 职业路径\n"
        f"- {', '.join(career_guidance.get('careerPaths', ['无数据'])[:3])}\n\n"

        f"## 准备建议\n"
        f"- {career_guidance.get('preparationAdvice', '无数据')}\n\n"
    )

    # 添加个性化建议
    prompt += (
        f"# 个性化建议\n"
        f"## 优势\n"
        f"- {personalized_advice.get('strengths', '无数据')}\n\n"

        f"## 挑战\n"
        f"- {personalized_advice.get('challenges', '无数据')}\n\n"

        f"## 策略\n"
        f"- {', '.join(personalized_advice.get('strategies', ['无数据'])[:3])}\n"
    )

    return prompt


# 3. 使用提示词生成图片
def generate_image(prompt_text):
    """生成图片并返回图片URL"""
    url = f"{IMAGE_API_BASE_URL}/services/aigc/text2image/image-synthesis"
    headers = {
        "X-DashScope-Async": "enable",
        "Authorization": f"Bearer {IMAGE_GENERATION_KEY}",
        "Content-Type": "application/json"
    }
    payload = {
        "model": "wan2.2-t2i-flash",
        "input": {"prompt": prompt_text},
        "parameters": {"n": 1}
    }

    response = requests.post(url, headers=headers, json=payload)
    if response.status_code != 200:
        error_msg = response.json().get("message", "未知错误")
        raise Exception(f"生成图片失败: {error_msg}")

    return response.json()


# 4. 查询图片生成结果
def get_image_task_result(task_id):
    """查询图片生成任务状态"""
    url = f"{IMAGE_API_BASE_URL}/tasks/{task_id}"
    headers = {"Authorization": f"Bearer {IMAGE_GENERATION_KEY}"}

    response = requests.get(url, headers=headers)
    if response.status_code != 200:
        error_msg = response.json().get("message", "未知错误")
        raise Exception(f"查询任务失败: {error_msg}")

    return response.json()


# 主流程
def main():
    # 准备学生数据
    student_data = {
        "name": "陈都",
        "age": "20",
        "education": "本科",
        "major": "通信工程",
        "interests": "主要聚焦5G/6G无线通信、物联网（IoT）、人工智能与通信融合、光通信技术、网络安全及量子通信等前沿领域，同时关注通信协议设计、信号处理及芯片开发等实践方向。",
        "study_objective": "掌握扎实的通信原理、信号处理及网络技术基础，熟练运用5G/6G、物联网、人工智能等前沿技术，培养创新能力和工程实践能力，成为具备系统设计、优化及研发能力的高素质通信人才。"
    }

    print("🚀 开始生成培养方案...")
    try:
        # 1. 获取培养方案
        study_plan = generate_study_plan(student_data)

        if not study_plan.get("success", False):
            print("❌ 培养方案生成失败:", study_plan.get("message", "未知错误"))
            return

        print("✅ 培养方案生成成功！")

        # 调试输出（可选）
        # print("API响应:", json.dumps(study_plan, indent=2, ensure_ascii=False))

        # 2. 创建图表提示词
        chart_prompt = create_chart_prompt(study_plan)
        print("\n📝 生成的图表提示词:")
        print(chart_prompt[:500] + "...")  # 显示部分提示词

        # 3. 使用提示词生成图片
        print("\n🎨 开始生成培养方案图表...")
        image_response = generate_image(chart_prompt)
        task_id = image_response["output"]["task_id"]
        print(f"🆔 图表生成任务ID: {task_id}")

        # 4. 等待图片生成
        print("⏳ 等待图表生成完成...", end="", flush=True)
        max_attempts = 30  # 最大尝试次数（约2.5分钟）
        attempt = 0

        while attempt < max_attempts:
            try:
                task_result = get_image_task_result(task_id)
                status = task_result["output"]["task_status"]

                if status == "SUCCEEDED":
                    image_url = task_result["output"]["results"][0]["url"]
                    print(f"\n🖼️ 图表生成成功！图片URL: {image_url}")
                    return
                elif status == "FAILED":
                    error_msg = task_result["output"].get("message", "未知错误")
                    print(f"\n❌ 图表生成失败: {error_msg}")
                    return
                elif status in ["PENDING", "RUNNING"]:
                    print(".", end="", flush=True)
                    time.sleep(5)
                    attempt += 1
                else:
                    print(f"\n⚠️ 未知状态: {status}")
                    return
            except Exception as e:
                print(f"\n⚠️ 查询任务时发生错误: {str(e)}")
                return

        print("\n⏱️ 等待超时，图表生成时间过长")

    except Exception as e:
        print(f"\n⚠️ 发生错误: {str(e)}")
    finally:
        if os.name == 'nt':
            input("\n按Enter键退出...")


if __name__ == "__main__":
    main()