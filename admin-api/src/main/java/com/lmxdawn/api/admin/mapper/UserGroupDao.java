package com.lmxdawn.api.admin.mapper;

import com.lmxdawn.api.admin.bean.UserGroup;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserGroupDao {
    /**
     * 根据userGroupId查询
     * @param userGroupId
     * @return
     */
    UserGroup getUserGroupInfoByUserGroup(int userGroupId) throws Exception;




}
