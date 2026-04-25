# Doc & Tag API 接口文档

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

## 一、文档接口 (Doc)

### 1.1 上传文档
- **URL**: `POST /api/v1/docs/upload`
- **请求体**:
```json
{
  "path": "/path/to/file.md",
  "tagIds": [1, 2, 3]
}
```
- **响应**:
```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

### 1.2 更新文档标签
- **URL**: `PUT /api/v1/docs/tags/{docId}`
- **路径参数**: `docId` - 文档ID
- **请求体**: 标签ID数组
```json
[1, 2, 4]
```
- **响应**:
```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

### 1.3 删除文档
- **URL**: `DELETE /api/v1/docs/{docId}`
- **路径参数**: `docId` - 文档ID
- **响应**:
```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

### 1.4 批量删除文档
- **URL**: `DELETE /api/v1/docs/batch`
- **请求体**:
```json
[1, 2, 3, 4, 5]
```
- **响应**:
```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

### 1.5 获取文档详情
- **URL**: `GET /api/v1/docs/{docId}`
- **路径参数**: `docId` - 文档ID
- **响应**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "docId": 1,
    "docTitle": "文档标题",
    "docContent": "文档内容",
    "uploadPath": "/path/to/file.md",
    "uploadPathType": "LOCAL",
    "tagCount": 2,
    "createTime": "2024-01-01T12:00:00",
    "updateTime": "2024-01-01T12:00:00",
    "tags": [
      {
        "tagId": 1,
        "tagName": "技术",
        "tagDesc": "技术类文档"
      },
      {
        "tagId": 2,
        "tagName": "Java",
        "tagDesc": "Java相关"
      }
    ]
  }
}
```

### 1.6 分页查询文档
- **URL**: `GET /api/v1/docs/page`
- **查询参数**:
  - `page` - 页码，默认 1
  - `pageSize` - 每页条数，默认 10
  - `keyword` - 关键词搜索（标题模糊匹配），可选
- **请求示例**: `GET /api/v1/docs/page?page=1&pageSize=10&keyword=Java`
- **响应**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "list": [
      {
        "docId": 1,
        "docTitle": "Java入门教程",
        "docContent": "...",
        "uploadPath": "/path/to/file.md",
        "uploadPathType": "LOCAL",
        "tagCount": 2,
        "createTime": "2024-01-01T12:00:00",
        "updateTime": "2024-01-01T12:00:00",
        "tags": [
          {
            "tagId": 1,
            "tagName": "技术",
            "tagDesc": "技术类文档"
          }
        ]
      }
    ],
    "total": 100
  }
}
```

---

## 二、标签接口 (Tag)

### 2.1 创建标签
- **URL**: `POST /api/v1/tags`
- **请求体**:
```json
{
  "tagName": "技术",
  "tagDesc": "技术类文档"
}
```
- **响应**:
```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

### 2.2 更新标签
- **URL**: `PUT /api/v1/tags/{tagId}`
- **路径参数**: `tagId` - 标签ID
- **请求体**:
```json
{
  "tagName": "新技术",
  "tagDesc": "新技术类文档"
}
```
- **响应**:
```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

### 2.3 删除标签
- **URL**: `DELETE /api/v1/tags/{tagId}`
- **路径参数**: `tagId` - 标签ID
- **响应**:
```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

### 2.4 批量删除标签
- **URL**: `DELETE /api/v1/tags/batch`
- **请求体**:
```json
[1, 2, 3, 4, 5]
```
- **响应**:
```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

### 2.5 获取标签详情
- **URL**: `GET /api/v1/tags/{tagId}`
- **路径参数**: `tagId` - 标签ID
- **响应**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "tagId": 1,
    "tagName": "技术",
    "tagDesc": "技术类文档",
    "docCount": 10,
    "createTime": "2024-01-01T12:00:00",
    "updateTime": "2024-01-01T12:00:00"
  }
}
```

### 2.6 分页查询标签
- **URL**: `GET /api/v1/tags/page`
- **查询参数**:
  - `page` - 页码，默认 1
  - `pageSize` - 每页条数，默认 10
  - `keyword` - 关键词搜索（标签名模糊匹配），可选
- **请求示例**: `GET /api/v1/tags/page?page=1&pageSize=10&keyword=技术`
- **响应**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "list": [
      {
        "tagId": 1,
        "tagName": "技术",
        "tagDesc": "技术类文档",
        "docCount": 10,
        "createTime": "2024-01-01T12:00:00",
        "updateTime": "2024-01-01T12:00:00"
      }
    ],
    "total": 50
  }
}
```

### 2.7 获取标签关联的文档列表
- **URL**: `GET /api/v1/tags/{tagId}/docs`
- **路径参数**: `tagId` - 标签ID
- **查询参数**:
  - `page` - 页码，默认 1
  - `pageSize` - 每页条数，默认 10
- **请求示例**: `GET /api/v1/tags/1/docs?page=1&pageSize=10`
- **响应**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "list": [
      {
        "docId": 1,
        "docTitle": "Java入门教程",
        "docContent": "...",
        "uploadPath": "/path/to/file.md",
        "uploadPathType": "LOCAL",
        "tagCount": 2,
        "createTime": "2024-01-01T12:00:00",
        "updateTime": "2024-01-01T12:00:00",
        "tags": [
          {
            "tagId": 1,
            "tagName": "技术",
            "tagDesc": "技术类文档"
          }
        ]
      }
    ],
    "total": 10
  }
}
```

---

## 三、数据结构说明

### DocVO
| 字段 | 类型 | 说明 |
|------|------|------|
| docId | Integer | 文档ID |
| docTitle | String | 文档标题 |
| docContent | String | 文档内容 |
| uploadPath | String | 上传路径 |
| uploadPathType | String | 上传路径类型 |
| tagCount | Integer | 标签数量 |
| createTime | LocalDateTime | 创建时间 |
| updateTime | LocalDateTime | 更新时间 |
| tags | List\<TagSimpleVO\> | 关联的标签列表 |

### TagSimpleVO
| 字段 | 类型 | 说明 |
|------|------|------|
| tagId | Integer | 标签ID |
| tagName | String | 标签名称 |
| tagDesc | String | 标签描述 |

### TagVO
| 字段 | 类型 | 说明 |
|------|------|------|
| tagId | Integer | 标签ID |
| tagName | String | 标签名称 |
| tagDesc | String | 标签描述 |
| docCount | Integer | 关联文档数量 |
| createTime | LocalDateTime | 创建时间 |
| updateTime | LocalDateTime | 更新时间 |

### PageResult\<T\>
| 字段 | 类型 | 说明 |
|------|------|------|
| list | List\<T\> | 数据列表 |
| total | Long | 总记录数 |
