-- AI 模块数据库迁移脚本
-- 适用于已有数据库，新增 AI 相关表

-- AI 厂商表
CREATE TABLE IF NOT EXISTS ai_provider (
    provider_id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    base_url TEXT NOT NULL,
    api_key TEXT NOT NULL,
    status TEXT DEFAULT 'enabled',
    create_by TEXT DEFAULT '',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_by TEXT DEFAULT '',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_deleted INTEGER DEFAULT 0
);

-- AI 模型表
CREATE TABLE IF NOT EXISTS ai_model (
    model_id INTEGER PRIMARY KEY AUTOINCREMENT,
    provider_id INTEGER NOT NULL,
    model_name TEXT NOT NULL,
    display_name TEXT NOT NULL,
    context_window INTEGER DEFAULT 0,
    price_input REAL DEFAULT 0,
    price_output REAL DEFAULT 0,
    capabilities TEXT DEFAULT '[]',
    status TEXT DEFAULT 'enabled',
    create_by TEXT DEFAULT '',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_by TEXT DEFAULT '',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_deleted INTEGER DEFAULT 0
);

-- AI 对话表
CREATE TABLE IF NOT EXISTS ai_conversation (
    conversation_id INTEGER PRIMARY KEY AUTOINCREMENT,
    model_id INTEGER NOT NULL,
    title TEXT DEFAULT '',
    status TEXT DEFAULT 'active',
    total_input_tokens INTEGER DEFAULT 0,
    total_output_tokens INTEGER DEFAULT 0,
    total_cost REAL DEFAULT 0,
    create_by TEXT DEFAULT '',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_by TEXT DEFAULT '',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_deleted INTEGER DEFAULT 0
);

-- AI 消息表
CREATE TABLE IF NOT EXISTS ai_message (
    message_id INTEGER PRIMARY KEY AUTOINCREMENT,
    conversation_id INTEGER NOT NULL,
    role TEXT NOT NULL,
    content TEXT NOT NULL,
    input_tokens INTEGER DEFAULT 0,
    output_tokens INTEGER DEFAULT 0,
    cost REAL DEFAULT 0,
    create_by TEXT DEFAULT '',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_by TEXT DEFAULT '',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_deleted INTEGER DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_ai_model_provider ON ai_model(provider_id);
CREATE INDEX IF NOT EXISTS idx_ai_conversation_model ON ai_conversation(model_id);
CREATE INDEX IF NOT EXISTS idx_ai_message_conversation ON ai_message(conversation_id);
