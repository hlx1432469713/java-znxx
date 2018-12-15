package com.lmxdawn.api.common.utils;



import com.lmxdawn.api.admin.bean.DTO.UserDTO;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Spring Security中存放的认证用户
 * @author Niu Li
 * @since 2017/6/28
 */
public class TokenUserAuthentication implements Authentication {
    private static final long serialVersionUID = 3730332217518791533L;
    private UserDTO userDTO;
    private Boolean authentication = false;
    private String newToken;
    public TokenUserAuthentication(UserDTO userDTO, Boolean authentication) {
        this.userDTO = userDTO;
        this.authentication = authentication;
    }
    //这里的权限是FilterSecurityInterceptor做权限验证使用
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return userDTO.getRoles().stream()
                .map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }
    @Override
    public Object getCredentials() {
        return "";
    }
    @Override
    public Object getDetails() {
        return userDTO;
    }
    @Override
    public Object getPrincipal() {
        if (userDTO.getStudent()!=null){
            return userDTO.getStudent().getStudWxId();
        }
        else if (userDTO.getTeacher()!=null){
            return userDTO.getTeacher().getTeachWxId();
        }
// else if (userDTO.getAdminInfo()!=null){
//            return userDTO.getAdminInfo().getUsername();
//        }
        else {
            return userDTO.getUserName();
        }
    }
    @Override
    public boolean isAuthenticated() {
        return authentication;
    }
    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        this.authentication = isAuthenticated;
    }
    @Override
    public String getName() {
        if (userDTO.getStudent()!=null){
            return userDTO.getStudent().getStudWxId();
        }
        else if (userDTO.getTeacher()!=null){
            return userDTO.getTeacher().getTeachWxId();
        }
// else if (userDTO.getAdminInfo()!=null){
//            return userDTO.getAdminInfo().getUsername();
//        }
        else {
            return userDTO.getUserName();
        }
    }
}
