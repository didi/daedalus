package com.didichuxing.daedalus.service;

import com.didichuxing.daedalus.common.dto.directory.Directory;
import com.didichuxing.daedalus.dal.DirectoryDal;
import com.didichuxing.daedalus.dal.DirectoryShareDal;
import com.didichuxing.daedalus.entity.directory.DirectoryEntity;
import com.didichuxing.daedalus.entity.directory.DirectoryShareEntity;
import com.didichuxing.daedalus.entity.directory.SubNodeEntity;
import com.didichuxing.daedalus.handler.DirectoryConverter;
import com.didichuxing.daedalus.pojo.BizException;
import com.didichuxing.daedalus.util.Context;
import com.didichuxing.daedalus.util.IdGenerator;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author : jiangxinyu
 * @date : 2020/11/25
 */
@Service
public class DirectoryService {
    @Autowired
    private DirectoryDal directoryDal;
    @Autowired
    private DirectoryShareDal directoryShareDal;

    public Directory detail() {
        DirectoryEntity directoryEntity = directoryDal.findByUserName(Context.getUser().getUsername());

        directoryEntity = Optional.ofNullable(directoryEntity)
                .orElseGet(() -> {
                    DirectoryEntity entity = new DirectoryEntity();
                    entity.setUsername(Context.getUser().getUsername());
                    entity.setUsernameCN(Context.getUser().getUsernameCN());
                    return entity;
                });

        if (CollectionUtils.isEmpty(directoryEntity.getDirectories())) {
            SubNodeEntity child = new SubNodeEntity();
            child.setName("默认目录");
            child.setType("directory");
            child.setId(System.currentTimeMillis() + "");
            child.setChildren(Collections.EMPTY_LIST);
            directoryEntity.setDirectories(Lists.newArrayList(child));
        }

        return DirectoryConverter.entityToDto(directoryEntity);
    }

    public void save(Directory directory) {
        DirectoryEntity directoryEntity = DirectoryConverter.dtoToEntity(directory);
        directoryEntity.setUsername(Context.getUser().getUsername());
        directoryEntity.setUsernameCN(Context.getUser().getUsernameCN());
        DirectoryEntity entity = directoryDal.findByUserName(Context.getUser().getUsername());
        if (entity != null) {
            directoryEntity.setId(entity.getId());
        }
        directoryDal.save(directoryEntity);
    }

    /**
     * 获取分享的linkid
     *
     * @param nodeId
     * @return
     */
    public String getLinkId(String nodeId) {
        DirectoryEntity directoryEntity = directoryDal.findByUserName(Context.getUser().getUsername());
        List<SubNodeEntity> directories = Optional.ofNullable(directoryEntity)
                .orElseThrow(() -> new BizException("目录信息不存在！")).getDirectories();
        SubNodeEntity node = findNode(nodeId, directories);

        if (node == null) {
            throw new BizException("无效节点！");
        }

        DirectoryShareEntity directoryShareEntity = new DirectoryShareEntity();
        String linkId = IdGenerator.id();
        directoryShareEntity.setLinkId(linkId);
        directoryShareEntity.setSharer(Context.getUser().getUsername());
        directoryShareEntity.setShareNodes(Lists.newArrayList(node));

        directoryShareDal.save(directoryShareEntity);

        return linkId;
    }

    public void importShare(String linkId) {
        DirectoryShareEntity directoryShareEntity = Optional.ofNullable(directoryShareDal.findByLinkId(linkId))
                .orElseThrow(() -> new BizException("分享链接不存在！"));

        String username = Context.getUser().getUsername();
        if (username.equals(directoryShareEntity.getSharer())) {
            throw new BizException("不能使用自己的分享链接！");
        }

        DirectoryEntity directoryEntity = Optional.ofNullable(directoryDal.findByUserName(username))
                .orElseGet(() -> {
                    DirectoryEntity entity = new DirectoryEntity();
                    entity.setUsername(Context.getUser().getUsername());
                    entity.setUsernameCN(Context.getUser().getUsernameCN());
                    entity.setDirectories(new ArrayList<>());
                    return entity;
                });
        directoryShareEntity.getShareNodes().forEach(node->node.setParentId(null));
        directoryEntity.getDirectories().addAll(directoryShareEntity.getShareNodes());
        directoryDal.save(directoryEntity);
    }

    private SubNodeEntity findNode(String id, List<SubNodeEntity> nodes) {
        if (CollectionUtils.isEmpty(nodes)) {
            return null;
        }
        for (SubNodeEntity node : nodes) {
            if (Objects.equals(node.getId(), id)) {
                return node;
            }
            SubNodeEntity childrenNode = findNode(id, node.getChildren());
            if (childrenNode != null) {
                return childrenNode;
            }
        }
        return null;
    }
}
