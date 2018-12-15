package com.lmxdawn.api.admin.service;


import com.lmxdawn.api.admin.bean.AuthPermissionRule;

import java.util.List;

public interface AuthPermissionRuleService {


    List<AuthPermissionRule> listByIdIn(List<Long> ids);


    List<AuthPermissionRule> listByPid(Long pid);

    List<AuthPermissionRule> listAll();

    boolean insertAuthPermissionRule(AuthPermissionRule authPermissionRule);

    boolean updateAuthPermissionRule(AuthPermissionRule authPermissionRule);

    boolean deleteById(Long id);


}
