package com.imu.akflow.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.imu.akflow.mapper.TagMapper;
import com.imu.akflow.model.common.PageResult;
import com.imu.akflow.model.entity.Tag;
import com.imu.akflow.model.vo.TagVO;
import com.imu.akflow.utils.StrBitMapUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class TagService extends ServiceImpl<TagMapper, Tag> {

    private final BitService bitService;

    /**
     * 新增标签
     */
    @CacheEvict(value = "tags", allEntries = true)
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
    @CacheEvict(value = "tags", allEntries = true)
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
    @CacheEvict(value = "tags", allEntries = true)
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
    @CacheEvict(value = "tags", allEntries = true)
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

    @Cacheable(value = "tags", key = "'all'")
    public List<Tag> listAllTags() {
        return this.list();
    }

    public TagVO getTagById(Integer tagId) {
        Tag tag = this.getById(tagId);
        if (tag == null) {
            throw new IllegalArgumentException("标签不存在: " + tagId);
        }
        return TagVO.from(tag);
    }

    public PageResult<TagVO> pageTags(Integer page, Integer pageSize, String keyword) {
        Page<Tag> pageParam = new Page<>(page, pageSize);

        var query = Wrappers.lambdaQuery(Tag.class);
        if (StringUtils.isNotBlank(keyword)) {
            query.like(Tag::getTagName, keyword);
        }
        query.orderByDesc(Tag::getCreateTime);

        Page<Tag> result = this.page(pageParam, query);

        return PageResult.of(
                result.getRecords().stream()
                        .map(TagVO::from)
                        .collect(Collectors.toList()),
                result.getTotal()
        );
    }
}
