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
    public String advanceOver(){
        return URL+"/meeting/advanceOver";
    }
    public String selectMyJoinMeetingByDate(){
        return URL+"/meeting/selectMyJoinMeetingByDate";
    }
    public String sendLeaveInformation(){
        return URL+"/meeting/sendLeaveInformation";
    }
    public String CountLeaveInformation(){
        return URL+"/meeting/CountLeaveInformation";
    }
    public String showOneMeetingLeaveInfo(){
        return URL+"/meeting/showOneMeetingLeaveInfo";
    }
    public String agreeLeave(){
        return URL+"/meeting/agreeLeave";
    }
    public String disagreeLeave(){
        return URL+"/meeting/disagreeLeave";
    }
    public String oneRoomReserver(){
        return URL+"/meeting/oneRoomReserver";
    }
    public String robMeeting(){
        return URL+"/meeting/robMeeting";
    }
    public String coordinateMeeting(){
        return URL+"/meeting/coordinateMeeting";
    }
    public String toJoinPersonIndex(){
        return URL+"/joinPerson/toJoinPersonIndex";
    }
    public String showOneMeeting(){
        return URL+"/joinPerson/showOneMeeting";
    }
    public String remindOne(){
        return URL+"/joinPerson/remindOne";
    }
    public String getOneRoomEquip(){
        return URL+"/meetRoom/getOneRoomEquip";
    }
}
