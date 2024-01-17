package com.example.demo.card;

import com.example.demo.cardAttachment.card_attachment;
import com.example.demo.list.Lists;
import com.example.demo.user.User;
import jakarta.persistence.*;

import java.sql.Date;
import java.util.Set;

@Entity
public class Cards {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long cardId ;
    private String card_name ;
    private String card_description ;
    private Date create_date ;
    private Date due_date ;
    private Date reminder_date ;
    private boolean isActive ;

    @ManyToOne
    @JoinColumn(name = "listId")
    private Lists list;

    @ManyToMany(mappedBy = "cards")
    private Set<User> users;

    @OneToMany(mappedBy = "card")
    private Set<card_attachment> attachments;


    public Cards(long cardId, String card_name, String card_description, Date create_date, Date due_date, Date reminder_date, boolean isActive) {
        this.cardId = cardId;
        this.card_name = card_name;
        this.card_description = card_description;
        this.create_date = create_date;
        this.due_date = due_date;
        this.reminder_date = reminder_date;
        this.isActive = isActive;
    }

    public Cards() {
    }

    public long getCardId() {
        return cardId;
    }



    public String getCard_name() {
        return card_name;
    }

    public void setCard_name(String card_name) {
        this.card_name = card_name;
    }

    public String getCard_description() {
        return card_description;
    }

    public void setCard_description(String card_description) {
        this.card_description = card_description;
    }

    public Date getCreate_date() {
        return create_date;
    }

    public void setCreate_date(Date create_date) {
        this.create_date = create_date;
    }

    public Date getDue_date() {
        return due_date;
    }

    public void setDue_date(Date due_date) {
        this.due_date = due_date;
    }

    public Date getReminder_date() {
        return reminder_date;
    }

    public void setReminder_date(Date reminder_date) {
        this.reminder_date = reminder_date;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
