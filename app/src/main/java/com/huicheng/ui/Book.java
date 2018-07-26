package com.huicheng.ui;

import org.litepal.crud.DataSupport;



public class Book extends DataSupport {
    private int id;
    private String name;
    private String password;
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
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public Book(){ super();}
    public Book(int id, String name, String password) {
        super();
        this.id = id;
        this.name = name;
        this.password = password;
    }
    @Override
    public String toString() {
        return this.name + "-" + this.password;
    }
}
