package com.huicheng.ui;

import org.litepal.crud.DataSupport;



public class Book extends DataSupport {

    private String name;
    private String password;
    private String power;

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getPower() {
        return power;
    }
    public void setPower(String power) {
        this.power = power;
    }
    public Book(){ super();}
    public Book(String name, String password,String power) {
        super();

        this.name = name;
        this.password = password;
        this.power = power;
    }
    @Override
    public String toString() {
        return this.name + "-" + this.password+"-"+this.power;
    }
}
