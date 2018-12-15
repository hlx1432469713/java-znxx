package com.lmxdawn.api.admin.service;

import com.lmxdawn.api.admin.bean.DTO.UserDTO;
import com.lmxdawn.api.admin.bean.Student;
import com.lmxdawn.api.admin.vo.ResultVO;

import javax.servlet.http.HttpServletResponse;

public interface StudentService {

    Student getStudentInfoByOpenId(String studWxId);

    ResultVO loginStudent(UserDTO userDTO,HttpServletResponse response);

    int updateStudentInfoByStudId(Student student) ;

    int addStudentInfo(Student student);
}
