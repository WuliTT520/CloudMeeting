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
    public String logout(){
        return URL+"/logout";
    }
    public String showUserinfo(){
        return URL+"/showUserinfo";
    }
    public String showUser(){
        return URL+"/group/showUser";
    }
    public String showGroup(){
        return URL+"/group/showGroup";
    }
    public String deleteGroup(){
        return URL+"/group/deleteGroup";
    }
    public String showOneGroup(){
        return URL+"/group/showOneGroup";
    }
    public String saveGroup(){
        return URL+"/group/saveGroup";
    }
    public String updateResume(){
        return URL+"/updateResume";
    }
    public String getCode(){
        return URL+"/getCode";
    }
    public String recordPhone(){
        return URL+"/recordPhone";
    }
    public String selectStatus(){
        return URL+"/face/selectStatus";
    }
    public String insert(){
        return URL+"/face/insert";
    }
    public String update(){
        return URL+"/face/update";
    }
    public String changePwd(){
        return URL+"/changePwd";
    }
    public String updateOneGroup(){
        return URL+"/group/updateOneGroup";
    }
    public String showMyReserve(){
        return URL+"/meeting/showMyReserve";
    }
    public String specifiedMyReserve(){
        return URL+"/meeting/specifiedMyReserve";
    }
    public String showOneDayReserve(){
        return URL+"/meeting/showOneDayReserve";
    }
    public String reserveIndex(){
        return URL+"/meeting/reserveIndex";
    }
    public String selectPeople(){
        return URL+"/meeting/selectPeople";
    }
    public String getGroupList(){
        return URL+"/meeting/getGroupList";
    }
    public String reserveMeeting(){
        return URL+"/meeting/reserveMeeting";
    }
    public String showOneReserveDetail(){
        return URL+"/meeting/showOneReserveDetail";
    }
    public String cancelMeeting(){
        return URL+"/meeting/cancelMeeting";
    }
    public String getMsg(){
        return URL+"/meeting/pushMessage";
    }
    public String showOne(){
        return URL+"/userInfo/showOne";
    }
}
