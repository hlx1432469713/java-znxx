package com.lmxdawn.api.admin.service.impl;

import com.lmxdawn.api.admin.bean.DTO.UserDTO;
import com.lmxdawn.api.admin.bean.Teacher;
import com.lmxdawn.api.admin.bean.UserGroup;
import com.lmxdawn.api.admin.enums.ResultEnum;
import com.lmxdawn.api.admin.mapper.TeacherDao;
import com.lmxdawn.api.admin.mapper.UserGroupDao;
import com.lmxdawn.api.admin.service.TeacherService;
import com.lmxdawn.api.admin.vo.ResultVO;
import com.lmxdawn.api.common.utils.JwtTokenUtil;
import com.lmxdawn.api.common.utils.ResultVOUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;


@Service

public class TeacherServiceImpl implements TeacherService {
    @Resource
    private TeacherDao teacherDao;
    @Resource
    private UserGroupDao userGroupDao;
    @Resource
    private JwtTokenUtil jwtTokenUtil;

    @Override
    public Teacher getTeachInfoByOpenId(String teachWxId) {
        Teacher teacher;
        try {
            teacher = teacherDao.getTeacherInfoByOpenId(teachWxId);
            return teacher;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public ResultVO loginTeacher(UserDTO userDTO, HttpServletResponse response)  {
        UserDTO userDTO1 = new UserDTO();
        Teacher teacher = new Teacher();
        try {
            Teacher teacher1 = teacherDao.getTeacherInfoByOpenId(userDTO.getTeacher().getTeachWxId());
            if(teacher1 == null)
            {
//                UserDTO userDTO2 = new UserDTO();
//                //当查询为空时，需要去注册，才能绑定（返回一个假user）
//                student1.setStudWxId(userDTO.getStudent().getStudWxId());
//                userDTO2.setStudent(student1);
                return ResultVOUtils.error(ResultEnum.DATA_NOT,"登录教师失败 >> 请先去绑定信息");

            }
            else
            {
                userDTO1.setTeacher(teacher1);
            }
            if (userDTO1 !=null)
            {
                UserGroup userGroup = userGroupDao.getUserGroupInfoByUserGroup(userDTO1.getUserGroup().getUserGroupId());
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
    public int updateTeachInfoByStudId(Teacher teacher)  {
        int up = 0;
        try {
            up = teacherDao.updateTeachInfoByStudId(teacher);
            return up;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  up;
    }
    @Override
    public  int addTeachInfo(Teacher teacher){
        int up = 0;
        try {
            up = teacherDao.addTeacherInfo(teacher);
            return  up;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return up;
    }
}
