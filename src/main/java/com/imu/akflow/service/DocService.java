package com.imu.akflow.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.imu.akflow.mapper.DocMapper;
import com.imu.akflow.model.entity.Doc;
import com.imu.akflow.model.param.UploadParam;
import com.imu.akflow.utils.FileUtil;
import com.imu.akflow.utils.StrBitMapUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@Service
public class DocService extends ServiceImpl<DocMapper, Doc> {

    private final DocMapper docMapper;
    private final BitService bitService;

    /**
     * 根据本地上传文件进行文档存档
     */
    @Transactional
    public void uploadMdFile(UploadParam param) throws Exception {
        // 读取文件
        File mdFile = FileUtil.getMdFile(param.getPath());

        Doc doc = Doc.init(mdFile, param.getTagIds());
        Doc hisDoc = docMapper.queryByDocTitle(doc.getDocTitle());
        if (hisDoc != null) {
            doc.setVersion(1 + hisDoc.getVersion());
            deleteDoc(hisDoc);
        }
        this.save(doc);

        // 同时更新doc和tag的位图信息
        bitService.addDocTags(param.getTagIds(), doc.getDocId());
    }

    @Transactional
    public void deleteDoc(Doc doc) {
        this.removeById(doc);
        Set<Integer> tagIds = StrBitMapUtil.bitmapToSet(doc.getTagBitmap());
        bitService.deleteDocTags(tagIds, doc.getDocId());
    }

    @Transactional
    public void batchDeleteDocs(Set<Integer> docIds) {
        if (CollectionUtils.isEmpty(docIds)) {
            return;
        }

        List<Doc> docs = this.listByIds(docIds);
        if (CollectionUtils.isEmpty(docs)) {
            return;
        }

        // 构建 docId -> tagIds 映射
        Map<Integer, Set<Integer>> docTagMap = new HashMap<>();
        for (Doc doc : docs) {
            Set<Integer> tagIds = StrBitMapUtil.bitmapToSet(doc.getTagBitmap());
            if (CollectionUtils.isNotEmpty(tagIds)) {
                docTagMap.put(doc.getDocId(), tagIds);
            }
        }

        // 一次性批量清理所有标签位图
        if (MapUtils.isNotEmpty(docTagMap)) {
            bitService.batchDeleteDocTags(docTagMap);
        }

        this.removeByIds(docIds);
    }
}
