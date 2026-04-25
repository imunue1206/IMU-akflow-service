-- 文档表
CREATE TABLE doc (
    -- 主键
    doc_id INTEGER PRIMARY KEY AUTOINCREMENT,

    -- 基础字段
    doc_title TEXT NOT NULL,
    doc_content TEXT,

    upload_path TEXT,
    upload_path_type TEXT,

    -- 位图字段
    tag_bitmap TEXT DEFAULT '0',

    -- 统计字段（推荐添加，便于查询）
    tag_count INTEGER DEFAULT 0,

    -- 基础实体字段
    create_by TEXT DEFAULT '',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_by TEXT DEFAULT '',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    version INTEGER DEFAULT 0,
    is_deleted INTEGER DEFAULT 0,  -- 0-未删除，1-已删除

);

-- 标签表
CREATE TABLE tag (
    -- 主键
    tag_id INTEGER PRIMARY KEY AUTOINCREMENT,

    -- 基础字段
    tag_name TEXT NOT NULL,
    tag_desc TEXT DEFAULT '',

    -- 位图字段
    doc_bitmap TEXT DEFAULT '0',

    -- 统计字段
    doc_count INTEGER DEFAULT 0,

    -- 基础实体字段
    create_by TEXT DEFAULT '',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_by TEXT DEFAULT '',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    version INTEGER DEFAULT 0,
    is_deleted INTEGER DEFAULT 0,  -- 0-未删除，1-已删除
);

-- 创建索引
CREATE INDEX idx_doc_create_time ON doc(create_time);
CREATE INDEX idx_doc_is_deleted ON doc(is_deleted);

CREATE INDEX idx_tag_create_time ON tag(create_time);
CREATE INDEX idx_tag_is_deleted ON tag(is_deleted);