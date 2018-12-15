package com.lmxdawn.api.admin.bean;

import lombok.Data;

/**
 * 权限授权表
 */
@Data
public class AuthPermission {

    private Long id;

    private Long roleId;

    private Long permissionRuleId;

    private String type;

}
