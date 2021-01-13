package com.aditya.cloudfirestoreexample;

import com.google.firebase.firestore.Exclude;

import java.util.List;
import java.util.Map;

public class Book {
    private String documentId;
    private String title;
    private String description;
    private int priority;
    //private List<String> tags;
    private Map<String ,Boolean> tags;

    public Book(){ }

    public Book(String title, String description, int priority, Map<String ,Boolean> tags) {
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.tags = tags;
    }

    @Exclude
    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getPriority() {
        return priority;
    }

    public Map<String ,Boolean> getTags() {
        return tags;
    }
}
