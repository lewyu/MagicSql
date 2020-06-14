package com.bjtu.magic.entity;

import java.io.Serializable;

/**
 * @Auther: wjx
 * @Date: 2020/6/6 21:36
 * @Description:
 */
public class User implements Serializable {

    private static final long serialVersionUID=1L;

    private String userName;

    private String passwd;
    public User(){}
    public User(String userName,String passwd){
        this.userName=userName;
        this.passwd=passwd;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }
}
