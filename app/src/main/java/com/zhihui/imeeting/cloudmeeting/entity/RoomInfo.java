package com.zhihui.imeeting.cloudmeeting.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;

public class RoomInfo implements Parcelable {
    private int meetRoomId;
    private String meetRoomName;
    private String similar;
    private int contain;
    private String num;
    private ArrayList<String> equips;

    public RoomInfo() {
    }

    public RoomInfo(int meetRoomId, String meetRoomName, String similar, int contain, String num, ArrayList<String> equips) {
        this.meetRoomId = meetRoomId;
        this.meetRoomName = meetRoomName;
        this.similar = similar;
        this.contain = contain;
        this.num = num;
        this.equips = equips;
    }

    protected RoomInfo(Parcel in) {
        meetRoomId = in.readInt();
        meetRoomName = in.readString();
        similar = in.readString();
        contain = in.readInt();
        num = in.readString();
        equips = in.createStringArrayList();
    }

    public static final Creator<RoomInfo> CREATOR = new Creator<RoomInfo>() {
        @Override
        public RoomInfo createFromParcel(Parcel in) {
            return new RoomInfo(in);
        }

        @Override
        public RoomInfo[] newArray(int size) {
            return new RoomInfo[size];
        }
    };

    public int getMeetRoomId() {
        return meetRoomId;
    }

    public void setMeetRoomId(int meetRoomId) {
        this.meetRoomId = meetRoomId;
    }

    public String getMeetRoomName() {
        return meetRoomName;
    }

    public void setMeetRoomName(String meetRoomName) {
        this.meetRoomName = meetRoomName;
    }

    public String getSimilar() {
        return similar;
    }

    public void setSimilar(String similar) {
        this.similar = similar;
    }

    public int getContain() {
        return contain;
    }

    public void setContain(int contain) {
        this.contain = contain;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public ArrayList<String> getEquips() {
        return equips;
    }

    public void setEquips(ArrayList<String> equips) {
        this.equips = equips;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(getMeetRoomId());
        parcel.writeString(getMeetRoomName());

        parcel.writeString(getSimilar());
        parcel.writeInt(getContain());
        parcel.writeString(getNum());
        parcel.writeStringList(getEquips());
    }

    @Override
    public String toString() {
        return "RoomInfo{" +
                "meetRoomId=" + meetRoomId +
                ", meetRoomName='" + meetRoomName + '\'' +
                ", similar='" + similar + '\'' +
                ", contain=" + contain +
                ", num='" + num + '\'' +
                ", equips=" + equips +
                '}';
    }
}
