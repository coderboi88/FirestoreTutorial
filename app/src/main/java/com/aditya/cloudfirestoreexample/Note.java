package com.aditya.cloudfirestoreexample;

public class Note {
    private String title;
    private String description;

    public Note(){
        //public no-arg constructor needed
    }

    public Note(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }
}
