package com.tsarskiy.view;

import com.tsarskiy.model.Note;
import com.tsarskiy.storage.Storage;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Consumer;

/**
 * Модальное окно отображения списка заметок за выбранный день.
 * <p>
 * Позволяет:
 * <ul>
 *     <li>просматривать список заметок</li>
 *     <li>удалять заметки</li>
 *     <li>выбирать заметку для редактирования</li>
 * </ul>
 */
public class NotesListModal {

    private final List<Note> notes;
    private final Consumer<Note> onSelect;

    private double dragOffsetX;
    private double dragOffsetY;

    private static final DateTimeFormatter DF =
            DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm:ss");

    /**
     * Создаёт модальное окно списка заметок.
     *
     * @param notes    список заметок за день
     * @param onSelect обработчик выбора заметки
     */
    public NotesListModal(List<Note> notes, Consumer<Note> onSelect) {
        this.notes = notes;
        this.onSelect = onSelect;
    }

    /**
     * Отображает модальное окно.
     *
     * @param owner родительское окно
     */
    public void show(Window owner) {
        Stage stage = new Stage();
        stage.initOwner(owner);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setResizable(false);

        VBox root = new VBox(14);
        root.getStyleClass().add("modal-card");
        root.setPadding(new Insets(18));

        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Заметки за день");
        title.getStyleClass().add("modal-title");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        SVGPath closeIcon = IconFactory.createX();
        closeIcon.getStyleClass().add("icon-gold");

        StackPane close = new StackPane(closeIcon);
        close.setPadding(new Insets(6));
        close.setOnMouseClicked(e -> stage.close());

        header.getChildren().addAll(title, spacer, close);

        header.setOnMousePressed(e -> {
            dragOffsetX = e.getScreenX() - stage.getX();
            dragOffsetY = e.getScreenY() - stage.getY();
        });
        header.setOnMouseDragged(e -> {
            stage.setX(e.getScreenX() - dragOffsetX);
            stage.setY(e.getScreenY() - dragOffsetY);
        });

        VBox list = new VBox(14);
        list.setPadding(new Insets(4));

        if (notes.isEmpty()) {
            Label empty = new Label("Заметок за этот день нет");
            empty.getStyleClass().add("modal-empty");
            list.getChildren().add(empty);
        } else {
            for (Note note : notes) {
                list.getChildren().add(createCard(note, stage, list));
            }
        }

        ScrollPane scroll = new ScrollPane(list);
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.getStyleClass().add("modal-scroll");

        root.getChildren().addAll(header, scroll);

        Scene scene = new Scene(root, 620, 420);
        scene.setFill(Color.TRANSPARENT);
        scene.getStylesheets().add(
                getClass().getResource("/styles.css").toExternalForm()
        );

        stage.setScene(scene);
        stage.showAndWait();
    }

    /**
     * Создаёт карточку отдельной заметки.
     *
     * @param note  заметка
     * @param stage окно списка
     * @param list  контейнер списка заметок
     * @return карточка заметки
     */
    private VBox createCard(Note note, Stage stage, VBox list) {
        VBox card = new VBox(6);
        card.getStyleClass().add("note-item");
        card.setPadding(new Insets(14));

        HBox top = new HBox(8);
        top.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label(
                note.getTitle() == null || note.getTitle().isBlank()
                        ? "Без заголовка"
                        : note.getTitle()
        );
        title.getStyleClass().add("note-title");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        SVGPath trashIcon = IconFactory.createTrash();
        StackPane delete = new StackPane(trashIcon);
        delete.setPadding(new Insets(6));
        delete.getStyleClass().add("note-delete-button");

        delete.setOnMouseClicked(e -> {
            e.consume();

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Удаление заметки");
            alert.setHeaderText("Удалить заметку?");
            alert.setContentText("Это действие нельзя отменить");

            alert.showAndWait().ifPresent(btn -> {
                if (btn == ButtonType.OK) {
                    Storage.getInstance().deleteNote(note);
                    list.getChildren().remove(card);
                }
            });
        });

        top.getChildren().addAll(title, spacer, delete);

        Label content = new Label(
                note.getContent() == null || note.getContent().isBlank()
                        ? "Без содержимого"
                        : note.getContent()
        );
        content.getStyleClass().add("note-content");
        content.setWrapText(true);

        Label created = new Label(
                "Создано: " + (note.getCreatedAt() != null
                        ? DF.format(note.getCreatedAt())
                        : "—")
        );
        created.getStyleClass().add("note-content");
        created.setOpacity(0.7);

        card.getChildren().addAll(top, content, created);

        card.setOnMouseClicked(e -> {
            e.consume();
            stage.close();
            javafx.application.Platform.runLater(() -> onSelect.accept(note));
        });

        return card;
    }
}
