package com.didichuxing.daedalus.handler;

import com.didichuxing.daedalus.common.dto.directory.Directory;
import com.didichuxing.daedalus.common.dto.directory.SubNode;
import com.didichuxing.daedalus.entity.directory.DirectoryEntity;
import com.didichuxing.daedalus.entity.directory.SubNodeEntity;
import lombok.experimental.UtilityClass;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author : jiangxinyu
 * @date : 2020/11/25
 */
@UtilityClass
public class DirectoryConverter {

    public static DirectoryEntity dtoToEntity(Directory directory) {
        if (directory == null) {
            return null;
        }

        DirectoryEntity directoryEntity = SimpleConverter.convert(directory, DirectoryEntity.class);
        if (CollectionUtils.isNotEmpty(directory.getDirectories())) {
            directoryEntity.setDirectories(directory.getDirectories().stream().map(DirectoryConverter::dtoToEntity).collect(Collectors.toList()));
        } else {
            directoryEntity.setDirectories(Collections.EMPTY_LIST);
        }

        return directoryEntity;
    }

    private static SubNodeEntity dtoToEntity(SubNode subNode) {
        if (subNode == null) {
            return null;
        }

        SubNodeEntity subNodeEntity = SimpleConverter.convert(subNode, SubNodeEntity.class);

        Optional.ofNullable(subNode.getChildren())
                .ifPresent(subNodes -> {
                    subNodeEntity.setChildren(subNodes.stream().map(DirectoryConverter::dtoToEntity).collect(Collectors.toList()));
                });

        return subNodeEntity;

    }


    public static Directory entityToDto(DirectoryEntity directory) {
        if (directory == null) {
            return null;
        }

        Directory directoryEntity = SimpleConverter.convert(directory, Directory.class);
        if (CollectionUtils.isNotEmpty(directory.getDirectories())) {
            directoryEntity.setDirectories(directory.getDirectories().stream().map(DirectoryConverter::entityToDto).collect(Collectors.toList()));
        } else {
            directoryEntity.setDirectories(Collections.EMPTY_LIST);
        }

        if (directoryEntity.getUnfoldNodes() == null) {
            directoryEntity.setUnfoldNodes(new ArrayList<>());
        }
        return directoryEntity;
    }

    private static SubNode entityToDto(SubNodeEntity subNode) {
        if (subNode == null) {
            return null;
        }

        SubNode subNodeEntity = SimpleConverter.convert(subNode, SubNode.class);

        Optional.ofNullable(subNode.getChildren())
                .ifPresent(subNodes -> {
                    subNodeEntity.setChildren(subNodes.stream().map(DirectoryConverter::entityToDto).collect(Collectors.toList()));
                });

        return subNodeEntity;

    }
}
