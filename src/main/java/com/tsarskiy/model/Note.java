package com.tsarskiy.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Класс {@code Note} представляет собой модель заметки календаря.
 * <p>
 * Заметка содержит уникальный идентификатор, дату,
 * заголовок, текст заметки и дату создания.
 * Используется для хранения и отображения пользовательских заметок.
 */
public class Note implements Serializable {

    /**
     * Идентификатор версии сериализации.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Уникальный идентификатор заметки.
     */
    private String id;

    /**
     * Дата, к которой привязана заметка.
     */
    private LocalDate date;

    /**
     * Заголовок заметки.
     */
    private String title;

    /**
     * Текстовое содержимое заметки.
     */
    private String content;

    /**
     * Дата и время создания заметки.
     */
    private LocalDateTime createdAt;

    /**
     * Конструктор по умолчанию.
     * Используется при создании новой заметки и при десериализации.
     */
    public Note() {
    }

    /**
     * Конструктор для создания заметки со всеми параметрами.
     *
     * @param id        уникальный идентификатор заметки
     * @param date      дата заметки
     * @param title     заголовок заметки
     * @param content   текст заметки
     * @param createdAt дата и время создания
     */
    public Note(String id,
                LocalDate date,
                String title,
                String content,
                LocalDateTime createdAt) {
        this.id = id;
        this.date = date;
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
    }

    /* ===== GETTERS ===== */

    /**
     * Возвращает уникальный идентификатор заметки.
     *
     * @return идентификатор заметки
     */
    public String getId() {
        return id;
    }

    /**
     * Возвращает дату, к которой относится заметка.
     *
     * @return дата заметки
     */
    public LocalDate getDate() {
        return date;
    }

    /**
     * Возвращает заголовок заметки.
     *
     * @return заголовок заметки
     */
    public String getTitle() {
        return title;
    }

    /**
     * Возвращает текстовое содержимое заметки.
     *
     * @return текст заметки
     */
    public String getContent() {
        return content;
    }

    /**
     * Возвращает дату и время создания заметки.
     *
     * @return дата и время создания
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * Возвращает текст заметки.
     * Используется для отображения в списке заметок.
     *
     * @return текст заметки
     */
    public String getText() {
        return content;
    }

    /* ===== SETTERS ===== */

    /**
     * Устанавливает уникальный идентификатор заметки.
     *
     * @param id идентификатор заметки
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Устанавливает дату заметки.
     *
     * @param date дата заметки
     */
    public void setDate(LocalDate date) {
        this.date = date;
    }

    /**
     * Устанавливает заголовок заметки.
     *
     * @param title заголовок заметки
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Устанавливает текст заметки.
     *
     * @param content текст заметки
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * Устанавливает дату и время создания заметки.
     *
     * @param createdAt дата и время создания
     */
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Возвращает строковое представление заметки.
     * Если заголовок отсутствует, возвращается текст заметки.
     *
     * @return строковое представление заметки
     */
    @Override
    public String toString() {
        return title != null && !title.isBlank()
                ? title
                : content;
    }
}
