package com.lmxdawn.api.admin.service.impl;

import com.lmxdawn.api.admin.bean.DTO.UserDTO;
import com.lmxdawn.api.admin.bean.Student;
import com.lmxdawn.api.admin.bean.UserGroup;
import com.lmxdawn.api.admin.enums.ResultEnum;
import com.lmxdawn.api.admin.mapper.StudentDao;
import com.lmxdawn.api.admin.mapper.UserGroupDao;
import com.lmxdawn.api.admin.service.StudentService;
import com.lmxdawn.api.admin.vo.ResultVO;
import com.lmxdawn.api.common.utils.JwtTokenUtil;
import com.lmxdawn.api.common.utils.ResultVOUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;


@Service

public class StudentServiceImpl implements StudentService {
    @Resource
    private StudentDao studentDao;
    @Resource
    private UserGroupDao userGroupDao;
    @Resource
    private JwtTokenUtil jwtTokenUtil;

    @Override
    public Student getStudentInfoByOpenId(String studWxId) {
        Student student;
        try {
            student = studentDao.getStudentInfoByOpenId(studWxId);
            return student;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public ResultVO loginStudent(UserDTO userDTO, HttpServletResponse response)  {
        UserDTO userDTO1 = new UserDTO();
        Student student1 = new Student();
        try {
            Student student = studentDao.getStudentInfoByOpenId(userDTO.getStudent().getStudWxId());
            if(student == null)
            {
//                UserDTO userDTO2 = new UserDTO();
//                //当查询为空时，需要去注册，才能绑定（返回一个假user）
//                student1.setStudWxId(userDTO.getStudent().getStudWxId());
//                userDTO2.setStudent(student1);
                return ResultVOUtils.error(ResultEnum.DATA_NOT,"不存在该学生");

            }
            else
            {
                userDTO1.setStudent(student);
            }
            if (userDTO1 !=null)
            {
                UserGroup userGroup = userGroupDao.getUserGroupInfoByUserGroup(userDTO1.getStudent().getUserGroupId());
                userDTO1.setUserGroup(userGroup);
                List<String> roles = new ArrayList<>();
                roles.add(userGroup.getPermission());
                userDTO1.setRoles(roles);
                String token = jwtTokenUtil.create(userDTO1);
                System.out.println(token);
                response.addHeader("refresh",token);
                return ResultVOUtils.success(userDTO1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    @Override
    public  int updateStudentInfoByStudId(Student student)  {
        int up = 0;
        try {
            up = studentDao.updateStudentInfoByStudId(student);
            return up;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  up;
    }
    @Override
    public int addStudentInfo(Student student){
        int up = 0;
        try {
            up = studentDao.addStudentInfo(student);
            return  up;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return up;
    }
}
