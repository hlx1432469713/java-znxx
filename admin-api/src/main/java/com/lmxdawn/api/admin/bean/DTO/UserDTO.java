package com.lmxdawn.api.admin.bean.DTO;

import com.lmxdawn.api.admin.bean.Student;
import com.lmxdawn.api.admin.bean.Teacher;
import com.lmxdawn.api.admin.bean.UserGroup;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

//持久化
@Data
public class UserDTO implements Serializable {
    private  int userId;

    private String userName;

    private Student student;

    private Teacher teacher;

    private List<String> roles;

    private int authorityId;

    private UserGroup userGroup;

    private String token;
}
