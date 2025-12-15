package com.tsarskiy.view;

import com.tsarskiy.storage.Storage;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.stage.DirectoryChooser;

import java.io.File;

/**
 * Представление стартового экрана приложения (onboarding).
 * <p>
 * Используется при первом запуске приложения и позволяет:
 * <ul>
 *     <li>ознакомить пользователя с возможностями приложения</li>
 *     <li>выбрать директорию для локального хранения данных</li>
 * </ul>
 */
public class OnboardingView {

    private final VBox root;
    private final Storage storage;
    private final Runnable onComplete;

    /**
     * Создаёт экран первоначальной настройки приложения.
     *
     * @param storage    хранилище настроек и данных
     * @param onComplete действие, выполняемое после завершения onboarding
     */
    public OnboardingView(Storage storage, Runnable onComplete) {
        this.storage = storage;
        this.onComplete = onComplete;
        this.root = createView();
    }

    /**
     * Создаёт основной интерфейс onboarding-экрана.
     *
     * @return корневой контейнер представления
     */
    private VBox createView() {
        VBox background = new VBox();
        background.setAlignment(Pos.CENTER);
        background.setStyle("-fx-background-color: transparent;");

        VBox card = new VBox(28);
        card.getStyleClass().add("onboarding-card");
        card.setAlignment(Pos.CENTER);
        card.setPrefWidth(680);
        card.setMaxWidth(680);

        Button exit = new Button("✕");
        exit.getStyleClass().add("exit-button");
        exit.setOnAction(e -> System.exit(0));

        HBox top = new HBox(exit);
        top.setAlignment(Pos.TOP_RIGHT);

        VBox header = createHeader();
        HBox features = createFeatures();
        VBox storageInfo = createStorageInfo();

        Button start = new Button("Выбрать место для царских записей");
        start.getStyleClass().add("onboarding-button");
        start.setOnAction(e -> chooseStorageDirectory());

        card.getChildren().addAll(top, header, features, storageInfo, start);
        background.getChildren().add(card);

        return background;
    }

    /**
     * Открывает диалог выбора директории хранения данных.
     * <p>
     * После выбора директории сохраняет путь,
     * отмечает onboarding как пройденный
     * и запускает дальнейшую инициализацию приложения.
     */
    private void chooseStorageDirectory() {
        DirectoryChooser chooser = new DirectoryChooser();
        File dir = chooser.showDialog(root.getScene().getWindow());
        if (dir == null) return;

        storage.setStorageDirectory(dir.toPath());
        storage.setOnboarded();
        onComplete.run();
    }

    /**
     * Создаёт заголовок onboarding-экрана.
     *
     * @return контейнер заголовка
     */
    private VBox createHeader() {
        VBox box = new VBox(16);
        box.setAlignment(Pos.CENTER);

        StackPane crownCircle = new StackPane();
        crownCircle.getStyleClass().add("crown-circle");

        SVGPath crown = IconFactory.createCrown();
        crown.setFill(Color.BLACK);
        crown.setScaleX(1.6);
        crown.setScaleY(1.6);

        crownCircle.getChildren().add(crown);

        Label title = new Label("Царский заметник");
        title.getStyleClass().add("onboarding-title");

        Label subtitle = new Label(
                "Императорский подход к организации заметок и планированию дней."
        );
        subtitle.getStyleClass().add("onboarding-subtitle");
        subtitle.setWrapText(true);
        subtitle.setMaxWidth(600);

        box.getChildren().addAll(crownCircle, title, subtitle);
        return box;
    }

    /**
     * Создаёт блок с описанием возможностей приложения.
     *
     * @return контейнер с функциональными возможностями
     */
    private HBox createFeatures() {
        HBox box = new HBox(20);
        box.setAlignment(Pos.CENTER);

        box.getChildren().addAll(
                feature(IconFactory.createCalendar(), "Царский календарь",
                        "Величественный обзор всего месяца."),
                feature(IconFactory.createFileText(), "Королевские заметки",
                        "Записывайте мысли достойные трона."),
                feature(IconFactory.createShield(), "Несокрушимая охрана",
                        "Локальное хранение ваших секретов.")
        );
        return box;
    }

    /**
     * Создаёт карточку отдельной возможности приложения.
     *
     * @param icon  иконка возможности
     * @param title заголовок
     * @param text  описание
     * @return карточка возможности
     */
    private VBox feature(SVGPath icon, String title, String text) {
        VBox card = new VBox(14);
        card.getStyleClass().add("feature-card");
        card.setAlignment(Pos.TOP_CENTER);
        card.setPrefWidth(200);

        StackPane iconCircle = new StackPane();
        iconCircle.getStyleClass().add("feature-icon-circle");
        iconCircle.setMinHeight(56);
        iconCircle.setPrefHeight(56);
        iconCircle.setMaxHeight(56);

        icon.setScaleX(1.2);
        icon.setScaleY(1.2);
        iconCircle.getChildren().add(icon);

        Label t = new Label(title);
        t.getStyleClass().add("feature-title");
        t.setWrapText(true);
        t.setMaxWidth(160);
        t.setMinHeight(44);
        t.setAlignment(Pos.TOP_CENTER);
        t.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        Label d = new Label(text);
        d.getStyleClass().add("feature-description");
        d.setWrapText(true);
        d.setMaxWidth(170);
        d.setAlignment(Pos.TOP_CENTER);
        d.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        VBox.setMargin(d, new Insets(4, 0, 0, 0));

        card.getChildren().addAll(iconCircle, t, d);
        return card;
    }

    /**
     * Создаёт информационный блок о локальном хранении данных.
     *
     * @return контейнер с информацией о хранилище
     */
    private VBox createStorageInfo() {
        VBox box = new VBox(10);
        box.getStyleClass().add("storage-info-box");
        box.setAlignment(Pos.CENTER);

        Label title = new Label("Императорское хранилище");
        title.getStyleClass().add("storage-info-title");

        Label text = new Label("Все ваши заметки сохраняются локально на вашем устройстве.");
        text.getStyleClass().add("storage-info-text");

        Label sub = new Label("Полная конфиденциальность и контроль над своими данными.");
        sub.getStyleClass().add("storage-info-subtext");

        box.getChildren().addAll(title, text, sub);
        return box;
    }

    /**
     * Возвращает корневой узел onboarding-экрана.
     *
     * @return корневой контейнер
     */
    public VBox getView() {
        return root;
    }
}
