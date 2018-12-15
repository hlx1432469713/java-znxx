package com.lmxdawn.api.admin.bean;

import lombok.Data;

import java.util.Date;

/**
 * 规则表
 * @author hlx
 */
@Data
public class AuthPermissionRule {

  private Long id;
  private Long pid;
  private Integer isLeaf;
  private String name;
  private String title;
  private Integer status;
  private String condition;
  private Integer listorder;
  private Date createTime;
  private Date updateTime;
}
