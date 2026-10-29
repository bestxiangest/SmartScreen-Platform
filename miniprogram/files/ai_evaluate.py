import requests
import time
import os
import json

# 配置两个API的密钥和URL
PROJECT_EVALUATION_API = "http://sys.sharpcaterpillar.top:7878/api/evaluate_ai_project"
IMAGE_GENERATION_KEY = "YOUR_DASHSCOPE_API_KEY"
IMAGE_API_BASE_URL = "https://dashscope.aliyuncs.com/api/v1"


# 1. 调用项目评估API
def evaluate_project(project_data):
    """评估AI项目并返回评估结果"""
    headers = {"Content-Type": "application/json"}
    response = requests.post(PROJECT_EVALUATION_API, headers=headers, json=project_data)

    if response.status_code != 200:
        error_msg = response.json().get("message", "未知错误")
        raise Exception(f"项目评估失败: {error_msg}")

    return response.json()


# 2. 根据评估结果生成图表提示词
def create_chart_prompt(evaluation_data):
    """根据评估结果创建图表提示词"""
    # 提取关键信息
    project_info = evaluation_data["evaluation_result"]["projectInfo"]
    phases = evaluation_data["evaluation_result"]["implementationPlan"]
    timeline = evaluation_data["evaluation_result"]["timeline"]
    team = evaluation_data["evaluation_result"]["teamStructure"]
    risks = evaluation_data["evaluation_result"]["riskAssessment"]
    resources = evaluation_data["evaluation_result"]["resourceRequirements"]
    quality = evaluation_data["evaluation_result"]["qualityAssurance"]

    # 构建提示词 - 使用Markdown格式确保信息清晰
    prompt = (
        f"请创建包含以下多个维度的项目评估信息图表：\n\n"
        f"# 项目概况 ('{project_info['name']}')\n"
        f"- 可行性评分: {project_info['feasibilityScore']}/10\n"
        f"- 复杂程度: {project_info['complexityLevel']}\n"
        f"- 技术领域: {project_info['techArea']}\n"
        f"- 团队规模: {project_info['teamSize']}\n\n"

        f"# 实施计划\n"
        f"| 阶段 | 时长 | 主要目标 |\n"
        f"|------|------|-----------|\n"
        f"| {phases['phase1']['name']} | {phases['phase1']['duration']} | {'; '.join(phases['phase1']['objectives'][:2])} |\n"
        f"| {phases['phase2']['name']} | {phases['phase2']['duration']} | {'; '.join(phases['phase2']['objectives'][:2])} |\n"
        f"| {phases['phase3']['name']} | {phases['phase3']['duration']} | {'; '.join(phases['phase3']['objectives'][:2])} |\n\n"

        f"# 团队构成\n"
        f"| 角色 | 人数 | 主要职责 |\n"
        f"|------|------|-----------|\n"
    )

    # 添加团队成员信息
    for role in team["roles"]:
        prompt += f"| {role['position']} | {role['count']} | {'; '.join(role['responsibilities'][:1])} |\n"
    prompt += "\n"

    # 添加风险信息
    prompt += f"# 风险评估\n"
    if risks["technicalRisks"]:
        first_risk = risks["technicalRisks"][0]
        prompt += f"- 主要技术风险: {first_risk['risk']} (概率: {first_risk['probability']}, 影响: {first_risk['impact']})\n"
    if risks["projectRisks"]:
        first_risk = risks["projectRisks"][0]
        prompt += f"- 主要项目风险: {first_risk['risk']} (概率: {first_risk['probability']}, 影响: {first_risk['impact']})\n\n"

    # 添加资源和质量信息
    prompt += (
        f"# 资源需求\n"
        f"- 预算范围: {resources['estimatedBudget']}\n"
        f"- 硬件要求: {'; '.join(resources['hardware'][:2])}\n\n"

        f"# 质量保证\n"
        f"- 测试策略: {quality['testingStrategy']}\n"
        f"- 关键指标: {'; '.join(quality['qualityMetrics'][:2])}\n"
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
    # 准备项目数据
    project_data = {
        "project_name": "智能学习助手系统",
        "project_description": "基于自然语言处理和机器学习技术，开发一个能够理解学生学习需求，提供个性化学习建议和智能答疑的AI助手系统",
        "tech_area": "自然语言处理",
        "project_duration": "12",
        "team_size": "5"
    }

    print("🚀 开始评估项目...")
    try:
        # 1. 获取项目评估
        evaluation = evaluate_project(project_data)

        if not evaluation.get("success"):
            print("❌ 项目评估失败:", evaluation.get("message", "未知错误"))
            return

        print("✅ 项目评估成功！")

        # 2. 创建图表提示词
        chart_prompt = create_chart_prompt(evaluation)
        print("\n📝 生成的图表提示词:")
        print(chart_prompt[:500] + "...")  # 显示部分提示词

        # 3. 使用提示词生成图片
        print("\n🎨 开始生成项目评估图表...")
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