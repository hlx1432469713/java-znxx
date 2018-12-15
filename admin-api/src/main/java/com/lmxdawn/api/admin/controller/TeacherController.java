package com.lmxdawn.api.admin.controller;


import com.lmxdawn.api.admin.bean.DTO.UserDTO;
import com.lmxdawn.api.admin.bean.Decode;
import com.lmxdawn.api.admin.bean.Student;
import com.lmxdawn.api.admin.bean.Teacher;
import com.lmxdawn.api.admin.enums.ResultEnum;
import com.lmxdawn.api.admin.service.StudentService;
import com.lmxdawn.api.admin.service.TeacherService;
import com.lmxdawn.api.admin.utils.HttpRequest;
import com.lmxdawn.api.admin.vo.ResultVO;
import com.lmxdawn.api.common.utils.ResultVOUtils;
import net.sf.json.JSONObject;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.UUID;

/**
 * 教师信息相关
 */
@RestController
public class TeacherController {
    @Resource
    private TeacherService teacherService;

    /**
     * 教师登录
     */
    @PostMapping("/api/teacher/loginTeacher")
    public ResultVO loginTeacher(@RequestBody Decode decode, HttpSession session , HttpServletResponse response){
        String s = this.decodeDriverInfo(decode);
        UserDTO userDTO = new UserDTO();
        if(s!="")
        {
            Teacher teacher = new Teacher();
            teacher.setTeachWxId(s);
            userDTO.setTeacher(teacher);
        }
        else
            return ResultVOUtils.error(ResultEnum.LOGIN_VERIFY_FALL,"登录信息为空登录失败");
        ResultVO resultVO = null;
        try {
            resultVO = teacherService.loginTeacher(userDTO,response);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(resultVO != null)
        {
                if (resultVO.getCode() ==0) {
                    session.setAttribute("user", resultVO.getData());
                } else if (resultVO.getCode() == 5) {
                    session.setAttribute("user1", resultVO.getData());
                }
        }
        return resultVO;
    }


    /***
     * 根据openId获取教师全部信息
     */
    @PostMapping("/api/teacher/getTeachInfoByOpenId")
    public ResultVO getTeachInfoByOpenId(@RequestBody Teacher teacher) {
        if(teacher.getTeachWxId()!=null)
        {
            Teacher teacher1 = teacherService.getTeachInfoByOpenId(teacher.getTeachWxId());
            if(teacher1 != null)
            {
                return ResultVOUtils.success(teacher1);
            }

        }
        return ResultVOUtils.error(ResultEnum.PARAM_VERIFY_FALL,"教师信息为空，请重新查询！");
    }

    /***
     * 根据Id更新教师信息
     */
    @PostMapping("/api/student/updateTeachInfoByStudId")
    public ResultVO updateTeachInfoByStudId(@RequestBody Teacher teacher)  {
        UserDTO userInfo = (UserDTO) SecurityContextHolder.getContext().getAuthentication().getDetails();
        teacher.setTeachId(userInfo.getUserId());
        int updatestu = 0;
        updatestu = teacherService.updateTeachInfoByStudId(teacher);
        if(updatestu == 1)
        {
            return  ResultVOUtils.success("更新教师信息成功");
        }
        else
            return  ResultVOUtils.error(ResultEnum.DATA_CHANGE,"更新教师信息失败");

    }

    /**
     * 新增教师信息
     * @param
     * @return
     */
    @PostMapping("/api/student/insertTeacherInfo")
    public  ResultVO insertTeacherInfo(@RequestBody Teacher teacher){
//        String s = this.decodeDriverInfo(d);
//        teacher.setTeachWxId(s);
        UUID uuid =UUID.randomUUID();
        String str = uuid.toString();
        // 去掉"-"符号
        String temp = str.substring(0, 8) +str.substring(9, 13) + str.substring(14, 18) + str.substring(19, 23) +str.substring(24);
        teacher.setTeachScheduleId(temp);
        int status = 0;
        status = teacherService.addTeachInfo(teacher);
        if (status == 1)
        {
            return  ResultVOUtils.success("新增教师信息成功");
        }
        else
            return ResultVOUtils.error(ResultEnum.DATA_NOT,"新增教师信息失败");

    }

    public String decodeDriverInfo(@RequestBody Decode decodeUserInfo ) {
        String code = decodeUserInfo.getCode();
        if (code == null || code.length() == 0) {
            return null;
        }
        String wxspAppid = "";
        String wxspSecret = "";
        String grant_type = "authorization_code";

        String params = "appid=" + wxspAppid + "&secret=" + wxspSecret + "&js_code=" + code + "&grant_type=" + grant_type;
        //发送请求
        String sr = HttpRequest.sendGet("https://api.weixin.qq.com/sns/jscode2session", params);
        //解析相应内容（转换成json对象）
        JSONObject json = JSONObject.fromObject(sr);
        // 用户的唯一标识（openid）
        String openid = (String) json.get("openid");
        return openid;

    }

}
