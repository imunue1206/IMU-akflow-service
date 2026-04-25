package com.imu.akflow.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.imu.akflow.mapper.TagMapper;
import com.imu.akflow.model.entity.Tag;
import com.imu.akflow.utils.StrBitMapUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class TagService extends ServiceImpl<TagMapper, Tag> {

    private final BitService bitService;

    /**
     * 新增标签
     */
    public Tag addTag(String tagName, String tagDesc) {
        Tag tag = new Tag();
        tag.setTagName(tagName);
        tag.setTagDesc(tagDesc);
        tag.setDocBitmap("");
        tag.setDocCount(0);
        this.save(tag);
        return tag;
    }

    /**
     * 编辑标签信息（不涉及位图操作）
     */
    public void editTagInfo(Integer tagId, String tagName, String tagDesc) {
        Tag tag = this.getById(tagId);
        if (tag == null) {
            throw new IllegalArgumentException("标签不存在: " + tagId);
        }
        if (StringUtils.isNotBlank(tagName)) {
            tag.setTagName(tagName);
        }
        if (StringUtils.isNotBlank(tagDesc)) {
            tag.setTagDesc(tagDesc);
        }
        this.updateById(tag);
    }

    /**
     * 删除标签（需要清理所有文档的位图关联）
     */
    @Transactional
    public void deleteTag(Integer tagId) {
        Tag tag = this.getById(tagId);
        if (tag == null) {
            throw new IllegalArgumentException("标签不存在: " + tagId);
        }

        // 从所有关联文档中移除此标签
        Set<Integer> docIds = StrBitMapUtil.bitmapToSet(tag.getDocBitmap());
        if (CollectionUtils.isNotEmpty(docIds)) {
            bitService.removeTagFromDocs(tagId, docIds);
        }

        this.removeById(tagId);
    }

    /**
     * 批量删除标签（需要清理所有文档的位图关联）
     */
    @Transactional
    public void batchDeleteTags(Set<Integer> tagIds) {
        if (CollectionUtils.isEmpty(tagIds)) {
            return;
        }

        List<Tag> tags = this.listByIds(tagIds);
        if (CollectionUtils.isEmpty(tags)) {
            return;
        }

        // 构建 tagId -> docIds 映射
        Map<Integer, Set<Integer>> tagDocMap = new HashMap<>();
        for (Tag tag : tags) {
            Set<Integer> docIds = StrBitMapUtil.bitmapToSet(tag.getDocBitmap());
            if (CollectionUtils.isNotEmpty(docIds)) {
                tagDocMap.put(tag.getTagId(), docIds);
            }
        }

        // 一次性批量清理所有文档位图
        if (MapUtils.isNotEmpty(tagDocMap)) {
            bitService.batchRemoveTagFromDocs(tagDocMap);
        }

        this.removeByIds(tagIds);
    }
}
