package com.lmxdawn.api.admin.service;

import com.lmxdawn.api.admin.bean.DTO.UserDTO;
import com.lmxdawn.api.admin.bean.Teacher;
import com.lmxdawn.api.admin.vo.ResultVO;

import javax.servlet.http.HttpServletResponse;

public interface TeacherService {

    Teacher getTeachInfoByOpenId(String teachWxId);

    ResultVO loginTeacher(UserDTO userDTO, HttpServletResponse response);

    int updateTeachInfoByStudId(Teacher teacher) ;

    int addTeachInfo(Teacher teacher);
}
