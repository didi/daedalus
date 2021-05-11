package com.didichuxing.daedalus.common.dto.directory;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : jiangxinyu
 * @date : 2020/11/24
 */
@Data
public class Directory {

    private List<SubNode> directories;

    private String username;

    private String usernameCN;

    private List<String> unfoldNodes;
}
