package com.sample;


public class User {

  private long userId;
  private String userName;
  private String userMobile;
  private String userPwd;
  private java.sql.Timestamp userCreateTime;


  public long getUserId() {
    return userId;
  }

  public void setUserId(long userId) {
    this.userId = userId;
  }


  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }


  public String getUserMobile() {
    return userMobile;
  }

  public void setUserMobile(String userMobile) {
    this.userMobile = userMobile;
  }


  public String getUserPwd() {
    return userPwd;
  }

  public void setUserPwd(String userPwd) {
    this.userPwd = userPwd;
  }


  public java.sql.Timestamp getUserCreateTime() {
    return userCreateTime;
  }

  public void setUserCreateTime(java.sql.Timestamp userCreateTime) {
    this.userCreateTime = userCreateTime;
  }

}
