package com.lmxdawn.api.admin.mapper;

import com.lmxdawn.api.admin.bean.Teacher;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TeacherDao {
    /**
     * 根据teachWxId查询
     * @param teachWxId
     * @return
     */
    Teacher getTeacherInfoByOpenId(String teachWxId) throws Exception;

    /**
     * 根据Id更新教师信息
     * @return
     */
     int updateTeachInfoByStudId(Teacher teacher) throws Exception;

    /**
     * 新增教师信息
     */
    int addTeacherInfo(Teacher teacher) throws  Exception;


}
