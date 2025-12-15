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

public class Main extends Application {

    private Stage stage;
    private Storage storage;

    private double dragOffsetX;
    private double dragOffsetY;

    @Override
    public void start(Stage primaryStage) {
        this.stage = primaryStage;
        this.storage = Storage.getInstance(); //

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

    private void showOnboarding() {
        OnboardingView view = new OnboardingView(storage, this::showCalendar);

        Scene scene = new Scene(view.getView());
        scene.setFill(Color.TRANSPARENT);
        applyStyles(scene);

        stage.setScene(scene);
        stage.sizeToScene();
    }

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

    private void applyStyles(Scene scene) {
        URL css = getClass().getResource("/styles.css");
        if (css != null) {
            scene.getStylesheets().add(css.toExternalForm());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
