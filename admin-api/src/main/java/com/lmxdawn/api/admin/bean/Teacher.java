package com.lmxdawn.api.admin.bean;


import lombok.Data;

import java.util.Date;

@Data
public class Teacher {

    private int teachId;

    private String teachWxId;

    private String teachName;

    private String teachSex;

    private String teachNation;

    private String teachPhone;

    private String teachNumber;

    private String teachTitle;

    private Date teachWorktime;

    private  String teachScheduleId;

    private  String userGroupId;

}
