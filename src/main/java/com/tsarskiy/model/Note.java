package com.tsarskiy.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Note implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private LocalDate date;
    private String title;
    private String content;
    private LocalDateTime createdAt;

    public Note() {
    }

    public Note(String id, LocalDate date, String title, String content, LocalDateTime createdAt) {
        this.id = id;
        this.date = date;
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
    }

    /* ===== GETTERS ===== */

    public String getId() {
        return id;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /* 🔥 ВАЖНО: для NotesListModal */
    public String getText() {
        return content;
    }

    /* ===== SETTERS ===== */

    public void setId(String id) {
        this.id = id;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return title != null && !title.isBlank()
                ? title
                : content;
    }
}
