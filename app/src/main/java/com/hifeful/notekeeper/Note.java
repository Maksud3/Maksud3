package com.hifeful.notekeeper;

import java.util.Date;

public class Note {
    private long id;

    private String title;
    private String text;
    private Date date;
    private int color;

    public Note(){

    }

    public Note(long id, String title, String text, Date date, int color){
        this.id = id;
        this.title = title;
        this.text = text;
        this.date = date;
        this.color = color;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
