package com.tsarskiy.view;

import com.tsarskiy.model.Note;
import com.tsarskiy.storage.Storage;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.shape.SVGPath;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.StageStyle;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Модальное окно создания и редактирования заметки.
 * <p>
 * Используется для добавления новой заметки или изменения существующей.
 * Поддерживает ввод заголовка и текста заметки,
 * а также сохраняет изменения в {@link Storage}.
 */
public class NoteModal {

    private final LocalDate date;
    private final Note note;
    private final Storage storage;
    private final Runnable onSave;
    private Stage stage;

    /**
     * Создаёт модальное окно заметки.
     *
     * @param date    дата, к которой относится заметка
     * @param note    редактируемая заметка или {@code null} при создании новой
     * @param storage хранилище заметок
     * @param onSave  действие, выполняемое после сохранения
     */
    public NoteModal(LocalDate date, Note note, Storage storage, Runnable onSave) {
        this.date = date;
        this.note = note;
        this.storage = storage;
        this.onSave = onSave;
    }

    /**
     * Отображает модальное окно.
     *
     * @param owner родительское окно
     */
    public void show(Window owner) {
        stage = new Stage();
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(owner);
        stage.initStyle(StageStyle.TRANSPARENT);

        VBox root = createContent();

        Scene scene = new Scene(root, 600, 500);
        final double[] dragDelta = new double[2];

        root.setOnMousePressed(e -> {
            dragDelta[0] = stage.getX() - e.getScreenX();
            dragDelta[1] = stage.getY() - e.getScreenY();
        });

        root.setOnMouseDragged(e -> {
            stage.setX(e.getScreenX() + dragDelta[0]);
            stage.setY(e.getScreenY() + dragDelta[1]);
        });

        scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
        scene.getStylesheets().add(
                getClass().getResource("/styles.css").toExternalForm()
        );

        stage.setScene(scene);
        stage.showAndWait();
    }

    /**
     * Создаёт основной контейнер содержимого модального окна.
     *
     * @return контейнер содержимого
     */
    private VBox createContent() {
        VBox container = new VBox();
        container.getStyleClass().add("modal-content");

        HBox header = new HBox();
        header.getStyleClass().add("modal-header");
        header.setPadding(new Insets(20));
        header.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label(note == null ? "Новая заметка" : "Редактировать заметку");
        title.getStyleClass().add("modal-title");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button closeButton = new Button();
        closeButton.getStyleClass().add("modal-close-button");
        SVGPath closeIcon = IconFactory.createX();
        closeIcon.setScaleX(1.2);
        closeIcon.setScaleY(1.2);
        closeButton.setGraphic(closeIcon);
        closeButton.setOnAction(e -> stage.close());

        header.getChildren().addAll(title, spacer, closeButton);

        VBox body = createForm();
        VBox.setVgrow(body, Priority.ALWAYS);

        container.getChildren().addAll(header, body);
        return container;
    }

    /**
     * Создаёт форму ввода данных заметки.
     *
     * @return контейнер формы
     */
    private VBox createForm() {
        VBox form = new VBox(20);
        form.getStyleClass().add("modal-body");
        form.setPadding(new Insets(20));

        VBox titleGroup = new VBox(8);
        Label titleLabel = new Label("Заголовок");
        titleLabel.getStyleClass().add("form-label");

        TextField titleField = new TextField();
        titleField.getStyleClass().add("form-input");
        titleField.setPromptText("Введите заголовок заметки");
        if (note != null) {
            titleField.setText(note.getTitle());
        }

        VBox contentGroup = new VBox(8);
        Label contentLabel = new Label("Содержание");
        contentLabel.getStyleClass().add("form-label");

        TextArea contentArea = new TextArea();
        contentArea.getStyleClass().add("form-textarea");
        contentArea.setPromptText("Введите текст заметки");
        contentArea.setPrefRowCount(8);
        contentArea.setWrapText(true);
        if (note != null && note.getContent() != null) {
            contentArea.setText(note.getContent());
        }

        VBox.setVgrow(contentArea, Priority.ALWAYS);
        VBox.setVgrow(contentGroup, Priority.ALWAYS);

        Button cancelButton = new Button("Отмена");
        cancelButton.getStyleClass().addAll("modal-button", "cancel");
        cancelButton.setOnAction(e -> stage.close());

        Button saveButton = new Button(note == null ? "Создать" : "Сохранить");
        saveButton.getStyleClass().addAll("modal-button", "submit");
        saveButton.setDefaultButton(true);
        saveButton.setOnAction(e ->
                saveNote(titleField.getText(), contentArea.getText())
        );

        HBox actions = new HBox(12, cancelButton, saveButton);
        actions.setAlignment(Pos.CENTER_RIGHT);

        titleGroup.getChildren().addAll(titleLabel, titleField);
        contentGroup.getChildren().addAll(contentLabel, contentArea);

        form.getChildren().addAll(titleGroup, contentGroup, actions);
        return form;
    }

    /**
     * Сохраняет заметку.
     * <p>
     * При редактировании обновляет существующую заметку,
     * при создании — добавляет новую.
     *
     * @param title   заголовок заметки
     * @param content текст заметки
     */
    private void saveNote(String title, String content) {
        if (title == null || title.trim().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Ошибка");
            alert.setHeaderText("Заголовок не может быть пустым");
            alert.showAndWait();
            return;
        }

        if (note != null) {
            note.setTitle(title.trim());
            note.setContent(content != null ? content.trim() : "");
            note.setDate(date);
            storage.updateNote(note);
        } else {
            Note newNote = new Note();
            newNote.setId("note-" + UUID.randomUUID());
            newNote.setDate(date);
            newNote.setTitle(title.trim());
            newNote.setContent(content != null ? content.trim() : "");
            newNote.setCreatedAt(LocalDateTime.now());
            storage.addNote(newNote);
        }

        if (onSave != null) {
            onSave.run();
        }

        stage.close();
    }
}
