package com.lmxdawn.api.admin.controller;

import com.github.pagehelper.PageInfo;
import com.lmxdawn.api.admin.annotation.AuthRuleAnnotation;
import com.lmxdawn.api.admin.bean.AuthAdmin;
import com.lmxdawn.api.admin.bean.AuthRole;
import com.lmxdawn.api.admin.bean.AuthRoleAdmin;
import com.lmxdawn.api.admin.enums.ResultEnum;
import com.lmxdawn.api.admin.form.auth.AuthAdminSaveForm;
import com.lmxdawn.api.admin.form.auth.AuthAdminQueryForm;
import com.lmxdawn.api.admin.service.AuthAdminService;
import com.lmxdawn.api.admin.service.AuthRoleAdminService;
import com.lmxdawn.api.admin.service.AuthRoleService;
import com.lmxdawn.api.admin.utils.PasswordUtils;
import com.lmxdawn.api.admin.vo.auth.AuthAdminRoleVO;
import com.lmxdawn.api.common.utils.ResultVOUtils;
import com.lmxdawn.api.admin.vo.PageSimpleVO;
import com.lmxdawn.api.admin.vo.ResultVO;
import com.lmxdawn.api.admin.vo.auth.AuthAdminVo;
import org.springframework.beans.BeanUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 管理员相关
 */
@RestController
public class AuthAdminController {

    @Resource
    private AuthAdminService authAdminService;

    @Resource
    private AuthRoleService authRoleService;

    @Resource
    private AuthRoleAdminService authRoleAdminService;

    /**
     * 获取管理员列表
     */
    @AuthRuleAnnotation("admin/bean/admin/index")
    @GetMapping("/admin/auth/admin/index")
    /*
    @Valid
    用于验证注解是否符合要求，直接加在变量user之前，
    在变量中添加验证信息的要求，
    当不符合要求时就会在方法中返回message的错误提示信息。
    */
    public ResultVO index( @Valid AuthAdminQueryForm authAdminQueryForm,
                          BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return ResultVOUtils.error(ResultEnum.PARAM_VERIFY_FALL, bindingResult.getFieldError().getDefaultMessage());
        }

        if (authAdminQueryForm.getRoleId() != null) {
            List<AuthRoleAdmin> authRoleAdmins = authRoleAdminService.listByRoleId(authAdminQueryForm.getRoleId());
            List<Long> ids = new ArrayList<>();
            if (authRoleAdmins != null && !authRoleAdmins.isEmpty()) {
                ids = authRoleAdmins.stream().map(AuthRoleAdmin::getAdminId).collect(Collectors.toList());
            }
            authAdminQueryForm.setIds(ids);
        }
        List<AuthAdmin> authAdminList = authAdminService.listAdminPage(authAdminQueryForm);

        // 查询所有的权限
        List<Long> adminIds = authAdminList.stream().map(AuthAdmin::getId).collect(Collectors.toList());
        List<AuthRoleAdmin> authRoleAdminList = authRoleAdminService.listByAdminIdIn(adminIds);

        // 视图列表
        List<AuthAdminVo> authAdminVoList = authAdminList.stream().map(item -> {
            AuthAdminVo authAdminVo = new AuthAdminVo();
            BeanUtils.copyProperties(item, authAdminVo);
            List<Long> roles = authRoleAdminList.stream()
                    .filter(authRoleAdmin -> authAdminVo.getId().equals(authRoleAdmin.getAdminId()))
                    .map(AuthRoleAdmin::getRoleId)
                    .collect(Collectors.toList());
            authAdminVo.setRoles(roles);
            return authAdminVo;
        }).collect(Collectors.toList());

        PageInfo<AuthAdmin> authAdminPageInfo = new PageInfo<>(authAdminList);
        PageSimpleVO<AuthAdminVo> authAdminPageSimpleVO = new PageSimpleVO<>();
        authAdminPageSimpleVO.setTotal(authAdminPageInfo.getTotal());
        authAdminPageSimpleVO.setList(authAdminVoList);
System.out.println(authAdminPageSimpleVO+"4444");
        return ResultVOUtils.success(authAdminPageSimpleVO);

    }


    /**
     * 获取角色列表
     */
    @AuthRuleAnnotation("admin/bean/admin/roleList")
    @GetMapping("/admin/auth/admin/roleList")
    public ResultVO roleList(@RequestParam(value = "page", defaultValue = "1") Integer page,
                             @RequestParam(value = "limit", defaultValue = "100") Integer limit) {

        //获取所以管理员角色
        List<AuthRole> authRoleList = authRoleService.listAuthAdminRolePage(page, limit, null);
        //PageInfo分页插件中的
        PageInfo<AuthRole> pageInfo = new PageInfo<>(authRoleList);
        //PageSimpleVO简单的分页返回对象
        PageSimpleVO<AuthAdminRoleVO> pageSimpleVO = new PageSimpleVO<>();
        pageSimpleVO.setTotal(pageInfo.getTotal());
        List<AuthAdminRoleVO> authAdminRoleVOS = authRoleList.stream().map(e -> {
            AuthAdminRoleVO authAdminRoleVO = new AuthAdminRoleVO();
            BeanUtils.copyProperties(e, authAdminRoleVO);
            return authAdminRoleVO;
        }).collect(Collectors.toList());
        pageSimpleVO.setList(authAdminRoleVOS);

        return ResultVOUtils.success(pageSimpleVO);

    }


    /**
     * 新增
     *
     * @return
     */
    @AuthRuleAnnotation("admin/bean/admin/save")
    @PostMapping("/admin/auth/admin/save")
    public ResultVO save(@RequestBody @Valid AuthAdminSaveForm authAdminSaveForm,
                         BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return ResultVOUtils.error(ResultEnum.PARAM_VERIFY_FALL, bindingResult.getFieldError().getDefaultMessage());
        }

        // 检查是否存在相同名称的管理员
        AuthAdmin byUserName = authAdminService.findByUserName(authAdminSaveForm.getUsername());
        if (byUserName != null) {
            return ResultVOUtils.error(ResultEnum.DATA_REPEAT, "当前管理员已存在");
        }

        AuthAdmin authAdmin = new AuthAdmin();
        BeanUtils.copyProperties(authAdminSaveForm, authAdmin);
        
        if (authAdmin.getPassword() != null) {
            authAdmin.setPassword(PasswordUtils.authAdminPwd(authAdmin.getPassword()));
        }

        boolean b = authAdminService.insertAuthAdmin(authAdmin);

        if (!b) {
            return ResultVOUtils.error(ResultEnum.NOT_NETWORK);
        }

        // 插入角色
        if (authAdminSaveForm.getRoles() != null) {
            authRoleAdminService.insertRolesAdminIdAll(authAdminSaveForm.getRoles(), authAdmin.getId());
        }

        AuthAdminVo authAdminVo = new AuthAdminVo();
        BeanUtils.copyProperties(authAdmin, authAdminVo);
        authAdminVo.setRoles(authAdminSaveForm.getRoles());

        return ResultVOUtils.success(authAdminVo);
    }

    /**
     * 修改
     *
     * @return
     */
    @AuthRuleAnnotation("admin/bean/admin/edit")
    @PostMapping("/admin/auth/admin/edit")
    public ResultVO edit(@RequestBody @Valid AuthAdminSaveForm authAdminSaveForm,
                         BindingResult bindingResult) {
System.out.println("dsdsdsdsd");
        if (bindingResult.hasErrors()) {
            return ResultVOUtils.error(ResultEnum.PARAM_VERIFY_FALL, bindingResult.getFieldError().getDefaultMessage());
        }

        if (authAdminSaveForm.getId() == null) {
            return ResultVOUtils.error(ResultEnum.PARAM_VERIFY_FALL, "参数错误！");
        }

        // 检查是否存在除了当前管理员的其它名称的管理员
        AuthAdmin byUserName = authAdminService.findByUserName(authAdminSaveForm.getUsername());
        System.out.println();
        if (byUserName != null && !authAdminSaveForm.getId().equals(byUserName.getId())) {
            return ResultVOUtils.error(ResultEnum.DATA_REPEAT, "当前管理员已存在");
        }

        AuthAdmin authAdmin = new AuthAdmin();
        BeanUtils.copyProperties(authAdminSaveForm, authAdmin);
        if (authAdmin.getPassword() != null) {
            authAdmin.setPassword(PasswordUtils.authAdminPwd(authAdmin.getPassword()));
        }

        boolean b = authAdminService.updateAuthAdmin(authAdmin);

        if (!b) {
            return ResultVOUtils.error(ResultEnum.NOT_NETWORK);
        }

        // 修改角色
        if (authAdminSaveForm.getRoles() != null) {
            // 先删除之前的
            authRoleAdminService.deleteByAdminId(authAdmin.getId());
            authRoleAdminService.insertRolesAdminIdAll(authAdminSaveForm.getRoles(), authAdmin.getId());
        }

        return ResultVOUtils.success();
    }

    /**
     * 删除
     *
     * @return
     */
    @AuthRuleAnnotation("admin/bean/admin/delete")
    @PostMapping("/admin/auth/admin/delete")
    public ResultVO delete(@RequestBody AuthAdminSaveForm authAdminSaveForm) {

        if (authAdminSaveForm.getId() == null) {
            return ResultVOUtils.error(ResultEnum.PARAM_VERIFY_FALL, "参数错误！");
        }

        boolean b = authAdminService.deleteById(authAdminSaveForm.getId());
        if (!b) {
            return ResultVOUtils.error(ResultEnum.NOT_NETWORK);
        }
        // 先删除之前的角色
        authRoleAdminService.deleteByAdminId(authAdminSaveForm.getId());

        return ResultVOUtils.success();
    }


}
