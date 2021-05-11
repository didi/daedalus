package com.didichuxing.daedalus.common.dto.env;

import com.didichuxing.daedalus.common.enums.ClusterEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author : jiangxinyu
 * @date : 2020/4/16
 */
@Setter
@Getter
@ToString
public class EnvGroup {


    @ApiModelProperty("环境组id")
    private String id;

    @ApiModelProperty("环境名")
    @NotBlank(message = "环境名不能为空！")
    private String name;

    private String creator;
    private String creatorCN;

    @ApiModelProperty(hidden = true)
    private Date createTime;

    @ApiModelProperty(hidden = true)
    private Date updateTime;

    @ApiModelProperty("业务线")
    @NotNull(message = "业务线不能为空")
    private Integer bizLine;

    /**
     * 存放行数据
     * 第一个是变量名 key:envVarName
     * 第二个是变量描述 key:envVarDesc
     * 后面的是 不同环境的变量值
     */
    @ApiModelProperty("第一个是变量名 key:envVarName,第二个是变量描述 key:envVarDesc,后面的是 不同环境的变量值")
    @NotNull(message = "环境变量不能为空")
    private List<LinkedHashMap<String, String>> data;


    /**
     * 环境集群
     */
    private Map<String, ClusterEnum> clusterInfo;

}
