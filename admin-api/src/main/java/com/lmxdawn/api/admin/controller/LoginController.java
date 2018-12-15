package com.lmxdawn.api.admin.controller;

import com.lmxdawn.api.admin.annotation.AuthRuleAnnotation;
import com.lmxdawn.api.admin.bean.AuthAdmin;
import com.lmxdawn.api.admin.enums.ResultEnum;
import com.lmxdawn.api.admin.exception.JsonException;
import com.lmxdawn.api.admin.form.auth.LoginForm;
import com.lmxdawn.api.admin.form.auth.UpdatePasswordForm;
import com.lmxdawn.api.admin.service.AuthAdminService;
import com.lmxdawn.api.admin.service.AuthLoginService;
import com.lmxdawn.api.admin.utils.PasswordUtils;
import com.lmxdawn.api.admin.vo.auth.LoginUserInfoVO;
import com.lmxdawn.api.common.utils.IpUtils;
import com.lmxdawn.api.common.utils.JwtUtils;
import com.lmxdawn.api.common.utils.ResultVOUtils;
import com.lmxdawn.api.admin.vo.ResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.*;

/**
 * 登录相关
 */
@RestController
@Slf4j
public class LoginController {

    @Autowired
    private AuthLoginService authLoginService;

    @Autowired
    private AuthAdminService authAdminService;

    /**
     * 用户登录
     * @return
     */
    @PostMapping(value = "/admin/auth/login/index")
    public ResultVO index(@RequestBody @Valid LoginForm loginForm,
                          BindingResult bindingResult,
                          HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            return ResultVOUtils.error(ResultEnum.PARAM_VERIFY_FALL, bindingResult.getFieldError().getDefaultMessage());
        }

        //通过用户名查找
        AuthAdmin authAdmin = authAdminService.findByUserName(loginForm.getUserName());
        if (authAdmin == null) {
            throw new JsonException(ResultEnum.DATA_NOT, "该用户不存在");
        }

        if (!PasswordUtils.authAdminPwd(loginForm.getPwd()).equals(authAdmin.getPassword())) {
            throw new JsonException(ResultEnum.DATA_NOT, "用户名或密码错误");
        }
        
        // 更新登录状态
        AuthAdmin authAdminUp = new AuthAdmin();
        authAdminUp.setId(authAdmin.getId());
        //最后登录时间
        authAdminUp.setLastLoginTime(new Date());
        //最后登录IP
        authAdminUp.setLastLoginIp(IpUtils.getIpAddr(request));
        authAdminService.updateAuthAdmin(authAdminUp);

        // 登录成功后获取权限，这里面会设置到缓存
        authLoginService.listRuleByAdminId(authAdmin.getId());

        Map<String, Object> claims = new HashMap<>();
        claims.put("admin_id", authAdmin.getId());
        String token = JwtUtils.createToken(claims, 86400L); // 一天后过期

        Map<String, Object> map = new HashMap<>();
        map.put("id", authAdmin.getId());
        map.put("token", token);

        return ResultVOUtils.success(map);
    }

    /**
     * 获取登录用户信息
     * @return
     */
    @AuthRuleAnnotation("")
    @GetMapping("/admin/auth/login/userInfo")
    public ResultVO userInfo(HttpServletRequest request) {
        String adminId = request.getHeader("X-Adminid");
        Long id = Long.valueOf(adminId);

        AuthAdmin authAdmin = authAdminService.findById(id);

        List<String> authRules = authLoginService.listRuleByAdminId(authAdmin.getId());

        LoginUserInfoVO loginUserInfoVO = new LoginUserInfoVO();
        BeanUtils.copyProperties(authAdmin, loginUserInfoVO);
        loginUserInfoVO.setAuthRules(authRules);

        return ResultVOUtils.success(loginUserInfoVO);
    }

    /**
     * 登出
     * @return
     */
    @PostMapping("/admin/auth/login/out")
    public ResultVO out(){
        return ResultVOUtils.success();
    }

    /**
     * 修改密码
     * @return
     */
    @AuthRuleAnnotation("") // 需要登录验证,但是不需要权限验证时,value 值填空字符串
    @PostMapping("/admin/auth/login/password")
    public ResultVO password(@RequestBody @Valid UpdatePasswordForm updatePasswordForm,
                             BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResultVOUtils.error(ResultEnum.PARAM_VERIFY_FALL.getCode(),
                    bindingResult.getFieldError().getDefaultMessage());
        }

        AuthAdmin authAdmin = authAdminService.findPwdById(updatePasswordForm.getAdminId());
        if (authAdmin == null) {
            throw new JsonException(ResultEnum.DATA_NOT);
        }
        String newPwd = PasswordUtils.authAdminPwd(updatePasswordForm.getOldPassword());
        // 旧密码不对
        if (authAdmin.getPassword() != null
                && !authAdmin.getPassword().equals(newPwd)) {
            throw new JsonException(ResultEnum.DATA_NOT, "旧密码匹配失败");
        }

        AuthAdmin authAdminUp = new AuthAdmin();
        authAdminUp.setId(updatePasswordForm.getAdminId());
        authAdminUp.setPassword(newPwd);

        boolean b = authAdminService.updateAuthAdmin(authAdminUp);
        if (b) {
            return ResultVOUtils.success();
        }

        return ResultVOUtils.error(ResultEnum.DATA_CHANGE);
    }

}
