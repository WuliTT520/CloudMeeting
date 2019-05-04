package com.zhihui.imeeting.cloudmeeting.entity;

public class Equip {
    private int id;
    private String name;
    private boolean isChoose;

    public Equip(int id, String name) {
        this.id = id;
        this.name = name;
        this.isChoose=false;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isChoose() {
        return isChoose;
    }

    public void setChoose(boolean choose) {
        isChoose = choose;
    }
}
