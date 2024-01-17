package com.example.demo.list;

import com.example.demo.board.Board;
import com.example.demo.card.Cards;
import jakarta.persistence.*;

import java.util.Set;

@Entity
public class Lists {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long listId;

    private String name;
    private int position;

    @ManyToOne
    @JoinColumn(name = "board_id")
    private Board board;

    @OneToMany(mappedBy = "list")
    private Set<Cards> cards;



    public Lists() {
    }

    public Lists(Long listId, String name, int position, Board board) {
        this.listId = listId;
        this.name = name;
        this.position = position;
        this.board = board;
    }


    public Long getListId() {
        return listId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    // Other methods, if needed

    @Override
    public String toString() {
        return "Lists{" +
                "listId=" + listId +
                ", name='" + name + '\'' +
                ", position=" + position +
                ", board=" + board +
                '}';
    }
}
