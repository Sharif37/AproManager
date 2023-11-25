package com.example.AproManager;

public class UserData {
    private String token;
    private String name;
    private String email;
    private String profileUri;
    private String password ;

    public UserData( String name, String email, String profileUri,String password) {

        this.name = name;
        this.email = email;
        this.profileUri = profileUri;
        this.password=password ;
    }

    // Getters and setters
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

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
