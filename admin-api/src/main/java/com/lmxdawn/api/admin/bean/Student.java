package com.lmxdawn.api.admin.bean;

import lombok.Data;

import java.util.Date;

@Data
public class Student {
    private  int studId;

    private String studWxId;

    private String studName;

    private String studSex;

    private Date studBirth ;

    private String studNation;

    private String studPhone;

    private Date studEnterTime;

    private String studSchool;

    private  String  studEduSystem;

    private String studNumber;

    private String studClassroom;

    private double studCredits;

    private String studScheduleId;

    private int userGroupId;


}
