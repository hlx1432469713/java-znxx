package com.lmxdawn.api.admin.bean;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by zhoulin on 2018/7/7.
 * 管理人员分组信息
 */
@Data
public class UserGroup implements Serializable {

    private int userGroupId;

    private String groupName;

    private String permission;


    @Override
    public String toString() {
        return "UserGroup{" +
                "userGroupId=" + userGroupId +
                ", groupName='" + groupName + '\'' +
                ", permission='" + permission + '\'' +
                '}';
    }
}
