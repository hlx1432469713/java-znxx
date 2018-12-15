package com.lmxdawn.api.admin.controller;


import com.lmxdawn.api.admin.bean.DTO.UserDTO;
import com.lmxdawn.api.admin.bean.Decode;
import com.lmxdawn.api.admin.bean.Student;
import com.lmxdawn.api.admin.enums.ResultEnum;
import com.lmxdawn.api.admin.service.StudentService;
import com.lmxdawn.api.admin.utils.HttpRequest;
import com.lmxdawn.api.admin.vo.ResultVO;
import net.sf.json.JSONObject;
import com.lmxdawn.api.common.utils.ResultVOUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.UUID;

/**
 * 学生信息相关
 */
@RestController
public class StudentController {
    @Resource
    private StudentService studentService;

    /**
     * 学生登录
     */
    @PostMapping("/api/student/loginStudent")
    public ResultVO loginStudent(@RequestBody Decode decode, HttpSession session , HttpServletResponse response){
        String s = this.decodeDriverInfo(decode);
        UserDTO userDTO = new UserDTO();
        if(s!="")
        {
            Student student = new Student();
            student.setStudWxId(s);
            userDTO.setStudent(student);
        }
        else
            return ResultVOUtils.error(ResultEnum.LOGIN_VERIFY_FALL,"登录信息为空登录失败");
        ResultVO resultVO = null;
        try {
            resultVO = studentService.loginStudent(userDTO,response);
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
     * 根据openId获取学生全部信息
     */
    @PostMapping("/api/student/getStudentInfoByOpenId")
    public ResultVO getStudentInfoByOpenId(@RequestBody Student student) {
        if(student.getStudWxId()!=null)
        {
            Student student1 = studentService.getStudentInfoByOpenId(student.getStudWxId());
            if(student1 != null)
            {
                return ResultVOUtils.success(student1);
            }

        }
        return ResultVOUtils.error(ResultEnum.PARAM_VERIFY_FALL,"学生输入信息为空，请重新查询！");
    }

    /***
     * 根据Id更新学生信息
     */
    @PostMapping("/api/student/updateStudentInfoByStudId")
    public ResultVO updateStudentInfoByStudId(@RequestBody Student student)  {
        UserDTO userInfo = (UserDTO) SecurityContextHolder.getContext().getAuthentication().getDetails();
        int updatestu = 0;
        student.setStudId(userInfo.getUserId());//获取缓存中的studId
        updatestu = studentService.updateStudentInfoByStudId(student);
        if(updatestu == 1)
        {
            return  ResultVOUtils.success("更新学生信息成功");
        }
        else
            return  ResultVOUtils.error(ResultEnum.DATA_CHANGE,"更新学生信息失败");

    }

    /**
     * 新增学生信息
     * @param
     * @return
     */
    @PostMapping("/api/student/insertStudentInfo")
    public  ResultVO insertStudentInfo(@RequestBody Student student ,@RequestBody Decode d){
        //获取studWxId
        String s = this.decodeDriverInfo(d);
        student.setStudWxId(s);
        UUID uuid =UUID.randomUUID();
        String str = uuid.toString();
        // 去掉"-"符号
        String temp = str.substring(0, 8) +str.substring(9, 13) + str.substring(14, 18) + str.substring(19, 23) +str.substring(24);
        System.out.println(student.getStudEnterTime());
        student.setStudScheduleId(temp);
        int status = 0;
        status = studentService.addStudentInfo(student);
        if (status == 1)
        {
            return  ResultVOUtils.success("新增学生信息成功");
        }
        else
            return ResultVOUtils.error(ResultEnum.DATA_NOT,"新增学生信息失败");

    }

    public String decodeDriverInfo(@RequestBody Decode decodeUserInfo ) {
        String code = decodeUserInfo.getCode();
        if (code == null || code.length() == 0) {
            return null;
        }
        String wxspAppid = "wx34a09890619ba03b";
        String wxspSecret = "ab5dc839f1abc02b98f2b3d71f679bc5";
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
