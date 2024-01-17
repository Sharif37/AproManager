package com.example.AproManager.JavaCode;

import java.sql.Date;

public class UserData {
    private String name;
    private String email;
    private String profileUri;
    private String password ;

    private String signUpDate ;




   
    public UserData(String name, String email, String profileUri, String password, String signUpDate) {
        this.name = name;
        this.email = email;
        this.profileUri = profileUri;
        this.password = password;
        this.signUpDate = signUpDate;
    }

    public String getSignUpDate() {
        return signUpDate;
    }

    public void setSignUpDate(String signUpDate) {
        this.signUpDate = signUpDate;
    }

    // Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfileUri() {
        return profileUri;
    }

    public void setProfileUri(String profileUri) {
        this.profileUri = profileUri;
    }
}
