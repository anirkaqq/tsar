package com.tsarskiy;

import com.tsarskiy.storage.Storage;
import com.tsarskiy.view.CalendarView;
import com.tsarskiy.view.OnboardingView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.net.URL;

/**
 * Главный класс приложения «Царский заметник».
 * <p>
 * Является точкой входа в приложение и отвечает за:
 * <ul>
 *     <li>инициализацию JavaFX</li>
 *     <li>выбор начального экрана</li>
 *     <li>переключение между onboarding и календарём</li>
 *     <li>настройку внешнего вида окна</li>
 * </ul>
 */
public class Main extends Application {

    /**
     * Главное окно приложения.
     */
    private Stage stage;

    /**
     * Хранилище заметок и настроек приложения.
     */
    private Storage storage;

    /**
     * Смещение окна по оси X при перетаскивании.
     */
    private double dragOffsetX;

    /**
     * Смещение окна по оси Y при перетаскивании.
     */
    private double dragOffsetY;

    /**
     * Точка входа JavaFX-приложения.
     * <p>
     * Выполняет инициализацию окна и определяет,
     * какой экран необходимо показать при запуске.
     *
     * @param primaryStage главное окно JavaFX
     */
    @Override
    public void start(Stage primaryStage) {
        this.stage = primaryStage;
        this.storage = Storage.getInstance();

        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setTitle("Царский заметник");
        stage.setResizable(false);

        if (storage.isOnboarded() && storage.hasStorageDirectory()) {
            showCalendar();
        } else {
            showOnboarding();
        }

        stage.show();
        stage.centerOnScreen();
    }

    /**
     * Отображает экран первоначальной настройки приложения.
     * <p>
     * Используется при первом запуске или при отсутствии папки хранения.
     */
    private void showOnboarding() {
        OnboardingView view = new OnboardingView(storage, this::showCalendar);

        Scene scene = new Scene(view.getView());
        scene.setFill(Color.TRANSPARENT);
        applyStyles(scene);

        stage.setScene(scene);
        stage.sizeToScene();
    }

    /**
     * Отображает основной экран календаря с заметками.
     */
    private void showCalendar() {
        CalendarView view = new CalendarView(storage);

        Scene scene = new Scene(view.getView());
        scene.setFill(Color.TRANSPARENT);
        applyStyles(scene);

        enableWindowDrag(view.getHeader());

        stage.setScene(scene);
        stage.sizeToScene();
        stage.centerOnScreen();
    }

    /**
     * Включает возможность перетаскивания окна мышью
     * за указанный элемент интерфейса.
     *
     * @param dragNode элемент, за который можно перетаскивать окно
     */
    private void enableWindowDrag(javafx.scene.Node dragNode) {
        dragNode.setOnMousePressed(e -> {
            dragOffsetX = e.getScreenX() - stage.getX();
            dragOffsetY = e.getScreenY() - stage.getY();
        });

        dragNode.setOnMouseDragged(e -> {
            stage.setX(e.getScreenX() - dragOffsetX);
            stage.setY(e.getScreenY() - dragOffsetY);
        });
    }

    /**
     * Применяет CSS-стили к сцене приложения.
     *
     * @param scene сцена, к которой применяются стили
     */
    private void applyStyles(Scene scene) {
        URL css = getClass().getResource("/styles.css");
        if (css != null) {
            scene.getStylesheets().add(css.toExternalForm());
        }
    }

    /**
     * Точка входа в приложение.
     *
     * @param args аргументы командной строки
     */
    public static void main(String[] args) {
        launch(args);
    }
}
