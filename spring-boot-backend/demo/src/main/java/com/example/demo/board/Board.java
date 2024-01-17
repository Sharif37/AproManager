package com.example.demo.board;

import com.example.demo.list.Lists;
import com.example.demo.user.User;
import jakarta.persistence.*;

import java.sql.Date;
import java.util.Set;

@Entity
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long board_id;
    private String name ;
    private Date created_date ;
    boolean isPublic;
    @ManyToMany(mappedBy = "boards")
    private Set<User> users;
    @OneToMany(mappedBy = "board")
    private Set<Lists> lists;
    public Board(){

    }
    public Board(Long board_id, String name, Date created_date, boolean isPublic) {
        this.board_id = board_id;
        this.name = name;
        this.created_date = created_date;
        this.isPublic = isPublic;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getCreated_date() {
        return created_date;
    }

    public void setCreated_date(Date created_date) {
        this.created_date = created_date;
    }

    public boolean Is_public() {
        return isPublic;
    }

    public void setIs_public(boolean is_public) {
        this.isPublic = is_public;
    }

    public Long getBoard_id() {
        return board_id;
    }

    public void setBoard_id(Long board_id) {
        this.board_id = board_id;
    }
}
