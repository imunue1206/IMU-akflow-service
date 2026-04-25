package com.imu.akflow.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.imu.akflow.model.entity.Doc;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DocMapper extends BaseMapper<Doc> {

    default Doc queryByDocTitle(String docTitle) {
        return this.selectOne(
                Wrappers.lambdaQuery(Doc.class)
                        .eq(Doc::getDocTitle, docTitle)
        );
    }

    default Page<Doc> selectPageWithoutContent(Page<Doc> page, String keyword) {
        var query = Wrappers.lambdaQuery(Doc.class)
                .select(Doc::getDocId, Doc::getDocTitle, Doc::getUploadPath, Doc::getUploadPathType,
                        Doc::getTagBitmap, Doc::getTagCount, Doc::getCreateTime, Doc::getUpdateTime)
                .orderByDesc(Doc::getCreateTime);
        if (keyword != null && !keyword.trim().isEmpty()) {
            query.like(Doc::getDocTitle, keyword);
        }
        return this.selectPage(page, query);
    }

    default Page<Doc> selectPageByDocIds(Page<Doc> page, List<Integer> docIds) {
        return this.selectPage(page,
                Wrappers.lambdaQuery(Doc.class)
                        .select(Doc::getDocId, Doc::getDocTitle, Doc::getUploadPath, Doc::getUploadPathType,
                                Doc::getTagBitmap, Doc::getTagCount, Doc::getCreateTime, Doc::getUpdateTime)
                        .in(Doc::getDocId, docIds)
                        .orderByDesc(Doc::getCreateTime));
    }

    default List<Doc> listAllWithoutContent() {
        return this.selectList(
                Wrappers.lambdaQuery(Doc.class)
                        .select(Doc::getDocId, Doc::getDocTitle, Doc::getUploadPath, Doc::getUploadPathType,
                                Doc::getTagBitmap, Doc::getTagCount, Doc::getCreateTime, Doc::getUpdateTime)
        );
    }

    default String selectContentById(Integer docId) {
        Doc doc = this.selectById(docId);
        return doc != null ? doc.getDocContent() : null;
    }

    int updateBatchById(@Param("list") List<Doc> list);
}
