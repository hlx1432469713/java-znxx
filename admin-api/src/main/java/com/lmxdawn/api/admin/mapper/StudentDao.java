package com.lmxdawn.api.admin.mapper;


import com.lmxdawn.api.admin.bean.Student;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface StudentDao {
    /**
     * 根据studWxId查询
     * @param studWxId
     * @return
     */
    Student getStudentInfoByOpenId(String studWxId) throws Exception;

    /**
     * 根据Id更新学生信息
     * @return
     */
     int updateStudentInfoByStudId(Student student) throws Exception;

    /**
     * 新增学生信息
     */
    int addStudentInfo(Student student) throws  Exception;


}
