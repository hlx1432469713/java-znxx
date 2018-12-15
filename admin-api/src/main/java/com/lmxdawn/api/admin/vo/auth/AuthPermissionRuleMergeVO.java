package com.lmxdawn.api.admin.vo.auth;

import lombok.Data;

import java.util.List;

/**
 * 权限列表整合为多维数组的视图
 * @author hlx
 */
@Data
public class AuthPermissionRuleMergeVO {

    private Long id;
    private Long pid;
    private Integer isLeaf;
    private String name;
    private String title;
    private Long status;
    private String condition;
    private Long listorder;

    /*
    一次性加载所有权限规则生成 tree 树形节点时需要
    */
    private List<AuthPermissionRuleMergeVO> children;

}
