package ru.yandex.models.User;

import com.google.gson.annotations.Expose;

public class User {
    @Expose
    private String email;
    @Expose
    private String password;
    private String name;
    public User(String email,String password,String name){
        this.email=email;
        this.password=password;
        this.name=name;
    }
    public User(String email,String namePassword, boolean isName){
        this.email=email;
        if (isName){
        this.name=namePassword;
        }
        else {
            this.password = namePassword;
        }
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
