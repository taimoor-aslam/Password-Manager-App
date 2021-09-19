package com.example.passwordmanagerapp;

public class LoginInfo {

    public String userID;
    public String name;
    public String email;
    public String password;

    LoginInfo(){}

    LoginInfo(String userID,String name,String email,String password){

        this.name=name;
        this.email=email;
        this.password=password;
        this.userID=userID;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public String getUserID() {
        return userID;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
}
