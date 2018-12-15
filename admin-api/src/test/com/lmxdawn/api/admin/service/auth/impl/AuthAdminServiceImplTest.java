package com.lmxdawn.api.admin.service.auth.impl;

import com.github.pagehelper.PageInfo;
import com.lmxdawn.api.BaseAdminApplicationTest;
import com.lmxdawn.api.admin.entity.bean.AuthAdmin;
import com.lmxdawn.api.admin.form.auth.AuthAdminSaveForm;
import com.lmxdawn.api.admin.form.auth.AuthAdminQueryForm;
import com.lmxdawn.api.admin.service.auth.AuthAdminService;
import org.junit.Test;

import javax.annotation.Resource;

import java.util.*;

import static org.junit.Assert.*;

public class AuthAdminServiceImplTest  extends BaseAdminApplicationTest {

    @Resource
    private AuthAdminService authAdminService;

    @Test
    public void listAdminPage() {

        Integer page = 1;
        Integer limit = 20;
        Integer status = 1;
        String username = "api";
        Long roleId = 1L;

        AuthAdminQueryForm authAdminQueryForm = new AuthAdminQueryForm();
        authAdminQueryForm.setPage(page);
        authAdminQueryForm.setLimit(limit);
        authAdminQueryForm.setStatus(status);
        authAdminQueryForm.setUsername(username);

        List<AuthAdmin> authAdminList = authAdminService.listAdminPage(authAdminQueryForm);
        PageInfo<AuthAdmin> authAdminPageInfo = new PageInfo<>(authAdminList);
        System.out.println(authAdminPageInfo.getList());
        assertTrue(authAdminPageInfo.getList().size() > 0);
    }

    @Test
    public void findByUserName() {
    }

    @Test
    public void findById() {
    }

    @Test
    public void findPwdById() {
    }

    @Test
    public void insertAuthAdmin() {

        AuthAdmin authAdmin = new AuthAdmin();
        authAdmin.setUsername("sssfff4");
        authAdmin.setCreateTime(new Date());
        authAdmin.setStatus(1);

        boolean b = authAdminService.insertAuthAdmin(authAdmin);
        assertTrue(b);
    }

    @Test
    public void updateAuthAdmin() {
    }

}