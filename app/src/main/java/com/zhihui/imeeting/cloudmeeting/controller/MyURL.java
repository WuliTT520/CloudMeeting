package com.zhihui.imeeting.cloudmeeting.controller;

public class MyURL {
    private final static String URL="http://39.106.56.132:8080/IMeeting";

    public String login(){
        return URL+"/login";
    }
    public String pwdCode(){
        return URL+"/pwdCode";
    }
    public String forgetPwd(){
        return URL+"/forgetPwd";
    }
}
