package com.example.demo.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table 
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID token;
    private String name;
    private String email;
    @Column(name = "profileuri")
    private String profileUri;
    private String password;

   
    public User() {
       
    }

    public User(String name, String email, String profileUri, String password) {
        this.name = name;
        this.email = email;
        this.profileUri = profileUri;
        this.password = password;
    }

   
    public UUID getToken() {
        return token;
    }

    public void setToken(UUID token) {
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

    public String getProfileuri() {
        return profileUri;
    }

    public void setProfileuri(String profileUri) {
        this.profileUri = profileUri;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

   
    @Override
    public String toString() {
        return "User [token=" + token + ", name=" + name + ", email=" + email + ", profileuri=" + profileUri
                + ", password=" + password + "]";
    }
}
