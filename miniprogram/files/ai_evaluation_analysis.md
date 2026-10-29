# AI 项目评估界面分析

## 1. 界面功能概述

“AI 项目评估” ([`pages/ai/evaluation.vue`](pages/ai/evaluation.vue)) 是一个智能化的项目可行性分析工具。用户通过表单提交项目的核心信息后，系统会调用后端 AI 模型进行多维度评估，并生成一份详尽的分析报告。为了提升用户体验，该界面还能将复杂的评估数据通过文生图技术，转换成直观、易于理解的可视化图表。

## 2. 核心业务流程

1.  **信息收集**：
    *   用户在表单中输入**项目名称、项目简介、技术领域、项目周期（月）**和**团队规模（人）**。

2.  **调用评估 API**：
    *   点击“开始评估”按钮，应用将收集到的信息整合成 JSON 对象。
    *   向后端的评估接口发起 `POST` 请求，提交项目数据。

3.  **处理并展示评估报告**：
    *   后端 AI 模型分析完成后，返回一个包含深度分析结果的 JSON 对象。
    *   前端解析返回的数据，并将其结构化地展示在多个卡片中，涵盖了以下方面：
        *   **综合评分与等级**：对项目的总体评价。
        *   **项目基本信息**：确认项目的可行性评分、复杂程度等。
        *   **团队结构**：建议的团队角色、人数及职责。
        *   **风险评估**：列出潜在的技术风险和项目管理风险。
        *   **资源需求**：预估的预算范围和所需的软硬件。

4.  **生成并展示可视化图表**：
    *   在评估报告展示后，用户可以选择生成可视化图表。
    *   前端会将评估报告中的关键数据（如评分、风险、资源等）整合成一段 Markdown 格式的文本（Prompt）。
    *   调用文生图 API，将此 Prompt 发送过去，生成一张汇总评估信息的图表。
    *   由于该过程是异步的，前端会通过**轮询**机制，定时查询图片的生成状态，成功后再将图片展示在界面上，并提供“查看大图”和“重新生成”的选项。

## 3. 调用的 API

该界面主要与两个核心 API 进行交互：

### 3.1. AI 项目评估 API

*   **URL**: `http://sys.sharpcaterpillar.top:7878/api/evaluate_ai_project`
*   **方法**: `POST`
*   **请求体 (Content-Type: application/json)**:
    ```json
    {
      "project_name": "string",
      "project_description": "string",
      "tech_area": "string",
      "project_duration": "string",
      "team_size": "string"
    }
    ```
*   **功能**: 接收项目的基础信息，通过后端 AI 模型进行深度分析，返回一份包含项目可行性、团队结构、风险评估、资源需求等详细信息的 JSON 报告。

### 3.2. 文生图 API (阿里云通义千问)

*   **功能**: 将文本格式的评估摘要转换成可视化的信息图表。
*   **生成请求 URL**: `https://dashscope.aliyuncs.com/api/v1/services/aigc/text2image/image-synthesis`
    *   **方法**: `POST`
    *   **Headers**:
        *   `Authorization`: `Bearer YOUR_DASHSCOPE_API_KEY`
        *   `X-DashScope-Async`: `enable` (表明这是一个异步任务)
    *   **请求体**:
        ```json
        {
          "model": "wan2.2-t2i-flash",
          "input": {
            "prompt": "一段由评估报告生成的、Markdown格式的详细描述文本"
          },
          "parameters": { "n": 1 }
        }
        ```
*   **任务状态查询 URL**: `https://dashscope.aliyuncs.com/api/v1/tasks/{taskId}`
    *   **方法**: `GET`
    *   **Headers**: `Authorization: Bearer YOUR_DASHSCOPE_API_KEY`
    *   **说明**: 前端通过此接口轮询查询异步任务的状态。一旦任务成功 (`SUCCEEDED`)，就从返回结果中获取图片 URL 并进行展示。

## 4. 实现总结

“AI 项目评估”界面成功地将复杂的 AI 分析能力与前端交互相结合，为用户提供了一个强大且易用的项目决策辅助工具。它通过调用后端评估服务和前沿的 AIGC 技术，不仅能生成深度的文本报告，还能将其可视化，极大地提升了信息传递的效率和用户体验。