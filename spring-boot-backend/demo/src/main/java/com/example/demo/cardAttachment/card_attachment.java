package com.example.demo.cardAttachment;

import com.example.demo.card.Cards;
import jakarta.persistence.*;

import java.sql.Date;


@Entity
public class card_attachment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long attachmentId;
    private Date upload_date ;
    private String  file_location ;
    private String filename ;
    @ManyToOne
    @JoinColumn(name = "card_id")
    private Cards card;

    public card_attachment() {
    }

    public card_attachment(long attachmentId, Date upload_date, String file_location, String filename) {
        this.attachmentId = attachmentId;
        this.upload_date = upload_date;
        this.file_location = file_location;
        this.filename = filename;
    }

    public long getAttachmentId() {
        return attachmentId;
    }


    public Date getUpload_date() {
        return upload_date;
    }

    public void setUpload_date(Date upload_date) {
        this.upload_date = upload_date;
    }

    public String getFile_location() {
        return file_location;
    }

    public void setFile_location(String file_location) {
        this.file_location = file_location;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
}
