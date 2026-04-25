# AI API 接口文档

## 基础信息
- **Base URL**: `http://localhost:8080`
- **Content-Type**: `application/json`
- **统一响应格式**:
```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

---

## 一、厂商接口 (Provider)

### 1.1 创建厂商
- **URL**: `POST /api/v1/ai/providers`
- **请求体**:
```json
{
  "name": "通义千问",
  "baseUrl": "https://dashscope.aliyuncs.com/compatible-mode/v1",
  "apiKey": "sk-xxx",
  "status": "enabled"
}
```
- **响应**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "providerId": 1,
    "name": "通义千问",
    "baseUrl": "https://dashscope.aliyuncs.com/compatible-mode/v1",
    "status": "enabled",
    "createTime": "2024-01-01T12:00:00"
  }
}
```

### 1.2 更新厂商
- **URL**: `PUT /api/v1/ai/providers/{providerId}`
- **路径参数**: `providerId` - 厂商ID
- **请求体**: 同创建
- **响应**: 同创建

### 1.3 删除厂商
- **URL**: `DELETE /api/v1/ai/providers/{providerId}`
- **路径参数**: `providerId` - 厂商ID
- **响应**:
```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

### 1.4 获取厂商详情
- **URL**: `GET /api/v1/ai/providers/{providerId}`
- **路径参数**: `providerId` - 厂商ID
- **响应**: 同创建

### 1.5 分页查询厂商
- **URL**: `GET /api/v1/ai/providers/page`
- **查询参数**:
  - `page` - 页码，默认 1
  - `pageSize` - 每页条数，默认 10
  - `keyword` - 关键词搜索（名称模糊匹配），可选
- **响应**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "list": [
      {
        "providerId": 1,
        "name": "通义千问",
        "baseUrl": "https://dashscope.aliyuncs.com/compatible-mode/v1",
        "status": "enabled",
        "createTime": "2024-01-01T12:00:00"
      }
    ],
    "total": 5
  }
}
```

### 1.6 获取所有可用厂商
- **URL**: `GET /api/v1/ai/providers/list`
- **响应**: 厂商列表

---

## 二、模型接口 (Model)

### 2.1 创建模型
- **URL**: `POST /api/v1/ai/models`
- **请求体**:
```json
{
  "providerId": 1,
  "modelName": "qwen3-max",
  "displayName": "通义千问 Max",
  "contextWindow": 262144,
  "priceInput": 2.5,
  "priceOutput": 10.0,
  "capabilities": ["代码", "写作", "翻译", "推理"],
  "status": "enabled"
}
```
- **响应**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "modelId": 1,
    "providerId": 1,
    "providerName": "通义千问",
    "modelName": "qwen3-max",
    "displayName": "通义千问 Max",
    "contextWindow": 262144,
    "priceInput": 2.5,
    "priceOutput": 10.0,
    "capabilities": ["代码", "写作", "翻译", "推理"],
    "status": "enabled",
    "createTime": "2024-01-01T12:00:00"
  }
}
```

### 2.2 更新模型
- **URL**: `PUT /api/v1/ai/models/{modelId}`
- **路径参数**: `modelId` - 模型ID
- **请求体**: 同创建
- **响应**: 同创建

### 2.3 删除模型
- **URL**: `DELETE /api/v1/ai/models/{modelId}`
- **路径参数**: `modelId` - 模型ID
- **响应**:
```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

### 2.4 获取模型详情
- **URL**: `GET /api/v1/ai/models/{modelId}`
- **路径参数**: `modelId` - 模型ID
- **响应**: 同创建

### 2.5 分页查询模型
- **URL**: `GET /api/v1/ai/models/page`
- **查询参数**:
  - `page` - 页码，默认 1
  - `pageSize` - 每页条数，默认 10
  - `providerId` - 厂商ID，可选
  - `keyword` - 关键词搜索（模型名模糊匹配），可选
- **响应**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "list": [
      {
        "modelId": 1,
        "providerId": 1,
        "providerName": "通义千问",
        "modelName": "qwen3-max",
        "displayName": "通义千问 Max",
        "contextWindow": 262144,
        "priceInput": 2.5,
        "priceOutput": 10.0,
        "capabilities": ["代码", "写作", "翻译", "推理"],
        "status": "enabled",
        "createTime": "2024-01-01T12:00:00"
      }
    ],
    "total": 20
  }
}
```

### 2.6 获取所有可用模型
- **URL**: `GET /api/v1/ai/models/list`
- **查询参数**:
  - `providerId` - 厂商ID，可选
- **响应**: 模型列表

---

## 三、对话接口 (Conversation)

### 3.1 创建对话
- **URL**: `POST /api/v1/ai/conversations`
- **请求体**:
```json
{
  "modelId": 1,
  "title": "Java代码审查"
}
```
- **响应**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "conversationId": 1,
    "modelId": 1,
    "modelName": "qwen3-max",
    "providerName": "通义千问",
    "title": "Java代码审查",
    "status": "active",
    "totalInputTokens": 0,
    "totalOutputTokens": 0,
    "totalCost": 0.0,
    "createTime": "2024-01-01T12:00:00",
    "updateTime": "2024-01-01T12:00:00"
  }
}
```

### 3.2 获取对话详情
- **URL**: `GET /api/v1/ai/conversations/{conversationId}`
- **路径参数**: `conversationId` - 对话ID
- **响应**: 同创建

### 3.3 分页查询对话
- **URL**: `GET /api/v1/ai/conversations/page`
- **查询参数**:
  - `page` - 页码，默认 1
  - `pageSize` - 每页条数，默认 10
  - `modelId` - 模型ID，可选
  - `status` - 状态 (active/archived)，可选
- **响应**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "list": [
      {
        "conversationId": 1,
        "modelId": 1,
        "modelName": "qwen3-max",
        "providerName": "通义千问",
        "title": "Java代码审查",
        "status": "active",
        "totalInputTokens": 1500,
        "totalOutputTokens": 800,
        "totalCost": 0.01175,
        "createTime": "2024-01-01T12:00:00",
        "updateTime": "2024-01-01T12:05:00"
      }
    ],
    "total": 50
  }
}
```

### 3.4 归档对话
- **URL**: `PUT /api/v1/ai/conversations/{conversationId}/archive`
- **路径参数**: `conversationId` - 对话ID
- **响应**:
```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

### 3.5 删除对话
- **URL**: `DELETE /api/v1/ai/conversations/{conversationId}`
- **路径参数**: `conversationId` - 对话ID
- **响应**:
```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

---

## 四、消息接口 (Message)

### 4.1 发送消息
- **URL**: `POST /api/v1/ai/conversations/{conversationId}/messages`
- **路径参数**: `conversationId` - 对话ID
- **请求体**:
```json
{
  "content": "请帮我审查这段Java代码"
}
```
- **响应**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "messageId": 2,
    "conversationId": 1,
    "role": "assistant",
    "content": "这段代码存在以下问题...",
    "inputTokens": 150,
    "outputTokens": 320,
    "cost": 0.003575,
    "createTime": "2024-01-01T12:05:00"
  }
}
```

### 4.2 获取对话消息列表
- **URL**: `GET /api/v1/ai/conversations/{conversationId}/messages`
- **路径参数**: `conversationId` - 对话ID
- **查询参数**:
  - `page` - 页码，默认 1
  - `pageSize` - 每页条数，默认 20
- **响应**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "list": [
      {
        "messageId": 1,
        "conversationId": 1,
        "role": "user",
        "content": "请帮我审查这段Java代码",
        "inputTokens": 150,
        "outputTokens": 0,
        "cost": 0.0,
        "createTime": "2024-01-01T12:05:00"
      },
      {
        "messageId": 2,
        "conversationId": 1,
        "role": "assistant",
        "content": "这段代码存在以下问题...",
        "inputTokens": 150,
        "outputTokens": 320,
        "cost": 0.003575,
        "createTime": "2024-01-01T12:05:01"
      }
    ],
    "total": 2
  }
}
```

---

## 五、统计接口 (Statistics)

### 5.1 获取统计概览
- **URL**: `GET /api/v1/ai/statistics/overview`
- **查询参数**:
  - `startDate` - 开始日期 (yyyy-MM-dd)，可选，默认本月
  - `endDate` - 结束日期 (yyyy-MM-dd)，可选，默认今天
- **响应**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "totalRequests": 150,
    "totalInputTokens": 50000,
    "totalOutputTokens": 30000,
    "totalCost": 0.525,
    "modelStats": [
      {
        "modelId": 1,
        "modelName": "qwen3-max",
        "providerName": "通义千问",
        "requests": 100,
        "totalCost": 0.35
      },
      {
        "modelId": 2,
        "modelName": "glm-4",
        "providerName": "智谱AI",
        "requests": 50,
        "totalCost": 0.175
      }
    ]
  }
}
```

---

## 六、数据结构说明

### ProviderVO
| 字段 | 类型 | 说明 |
|------|------|------|
| providerId | Integer | 厂商ID |
| name | String | 厂商名称 |
| baseUrl | String | API基础URL |
| status | String | 状态 (enabled/disabled) |
| createTime | LocalDateTime | 创建时间 |

### ModelVO
| 字段 | 类型 | 说明 |
|------|------|------|
| modelId | Integer | 模型ID |
| providerId | Integer | 厂商ID |
| providerName | String | 厂商名称 |
| modelName | String | 模型标识名 |
| displayName | String | 模型显示名 |
| contextWindow | Integer | 最大上下文窗口 |
| priceInput | Double | 输入单价（元/百万token） |
| priceOutput | Double | 输出单价（元/百万token） |
| capabilities | List\<String\> | 能力标签 |
| status | String | 状态 (enabled/disabled) |
| createTime | LocalDateTime | 创建时间 |

### ConversationVO
| 字段 | 类型 | 说明 |
|------|------|------|
| conversationId | Integer | 对话ID |
| modelId | Integer | 模型ID |
| modelName | String | 模型名称 |
| providerName | String | 厂商名称 |
| title | String | 对话标题 |
| status | String | 状态 (active/archived) |
| totalInputTokens | Integer | 累计输入token数 |
| totalOutputTokens | Integer | 累计输出token数 |
| totalCost | Double | 累计花费（元） |
| createTime | LocalDateTime | 创建时间 |
| updateTime | LocalDateTime | 更新时间 |

### MessageVO
| 字段 | 类型 | 说明 |
|------|------|------|
| messageId | Integer | 消息ID |
| conversationId | Integer | 对话ID |
| role | String | 角色 (user/assistant/system) |
| content | String | 消息内容 |
| inputTokens | Integer | 输入token数 |
| outputTokens | Integer | 输出token数 |
| cost | Double | 本次花费（元） |
| createTime | LocalDateTime | 创建时间 |

### PageResult\<T\>
| 字段 | 类型 | 说明 |
|------|------|------|
| list | List\<T\> | 数据列表 |
| total | Long | 总记录数 |
