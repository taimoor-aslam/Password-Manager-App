package com.example.passwordmanagerapp;

import android.widget.ImageView;

class Account {

    String id;
    String accountName;
    int imageColor;
    String accountPassword;


    public Account() {
    }

    public Account(String id,String accountName,String accountPassword,int imageColor){
        this.accountName=accountName;
        this.accountPassword=accountPassword;
        this.imageColor=imageColor;
        this.id=id;
    }

    public void setName(String name){
        this.accountName=name;
    }
    public void setPassword(String password){
        this.accountPassword=password;
    }
    public void setImageColor(int imageColor){
        this.imageColor=imageColor;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return accountName;
    }

    public int getImageColor() {
        return imageColor;
    }

    public String getPassword() {
        return accountPassword;
    }

    public String getId() {
        return id;
    }
}
