package com.example.demo.user;

import com.example.demo.board.Board;
import com.example.demo.card.Cards;
import jakarta.persistence.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long user_id;
    private String name;
    private String email;

    @Column(name = "profileUri")
    private String profileUri;

    private String password;
    private String signUpDate ;


    @Autowired
    private transient PasswordEncoder passwordEncoder;

    @ManyToMany
    @JoinTable(
            name = "board_member",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "board_id")
    )
    private Set<Board> boards;

    @ManyToMany
    @JoinTable(
            name = "card_member",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "cardId")
    )
    private Set<Cards>cards ;

    public User() {

    }



    public User(String name, String email, String profileUri, String password, String signUpDate) {
        this.name = name;
        this.email = email;
        this.profileUri = profileUri;
        this.password = password;
        this.signUpDate=signUpDate ;
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

    public void setProfileUri(String profileUri) {

        this.profileUri = profileUri;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password =password;
    }




    public String getProfileUri() {
        return profileUri;
    }

    public String getSignUpDate() {
        return signUpDate;
    }

    public void setSignUpDate(String signUpDate) {
        this.signUpDate = signUpDate;
    }
    @Override
    public String toString() {
        return "User [user_id=" + user_id + ", name=" + name + ", email=" + email + ", profileuri=" + profileUri
                + ", password=" + password + "]";
    }

    public long getUser_id() {
        return user_id;
    }

    public void setUser_id(long user_id) {
        this.user_id = user_id;
    }


    public boolean checkPassword(String rawPassword) {
        return passwordEncoder.matches(rawPassword, this.password);
    }

   
}
