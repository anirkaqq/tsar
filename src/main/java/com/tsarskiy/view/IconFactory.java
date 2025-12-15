package com.tsarskiy.view;

import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;

public class IconFactory {
    
    private static final Color ICON_COLOR = Color.web("#EAB308");

    public static SVGPath createCrown() {
        SVGPath crown = new SVGPath();
        crown.setContent(
                "M11.562 3.266a.5.5 0 0 1 .876 0L15.39 8.87a1 1 0 0 0 1.516.294L21.183 5.5a.5.5 0 0 1 .798.519l-2.834 10.246a1 1 0 0 1-.956.734H5.81a1 1 0 0 1-.957-.734L2.02 6.02a.5.5 0 0 1 .798-.519l4.276 3.664a1 1 0 0 0 1.516-.294z M5 21h14"
        );

        crown.getStyleClass().add("crown-icon");
        return crown;
    }
    
    public static SVGPath createCalendar() {
        SVGPath path = new SVGPath();
        path.setContent("M8 2v4 M16 2v4 M3 10h18 M5 4h14a2 2 0 0 1 2 2v14a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V6a2 2 0 0 1 2-2z");
        path.setFill(Color.TRANSPARENT);
        path.setStroke(ICON_COLOR);
        path.setStrokeWidth(2);
        return path;
    }
    
    public static SVGPath createFileText() {
        SVGPath path = new SVGPath();
        path.setContent("M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z M14 2v6h6 M16 13H8 M16 17H8 M10 9H8");
        path.setFill(Color.TRANSPARENT);
        path.setStroke(ICON_COLOR);
        path.setStrokeWidth(2);
        return path;
    }
    
    public static SVGPath createShield() {
        SVGPath path = new SVGPath();
        path.setContent("M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z");
        path.setFill(Color.TRANSPARENT);
        path.setStroke(ICON_COLOR);
        path.setStrokeWidth(2);
        return path;
    }
    
    public static SVGPath createChevronLeft() {
        SVGPath path = new SVGPath();
        path.setContent("M15 18l-6-6 6-6");
        path.setFill(Color.TRANSPARENT);
        path.setStroke(ICON_COLOR);
        path.setStrokeWidth(2);
        return path;
    }
    
    public static SVGPath createChevronRight() {
        SVGPath path = new SVGPath();
        path.setContent("M9 18l6-6-6-6");
        path.setFill(Color.TRANSPARENT);
        path.setStroke(ICON_COLOR);
        path.setStrokeWidth(2);
        return path;
    }
    
    public static SVGPath createPlus() {
        SVGPath path = new SVGPath();
        path.setContent("M12 5v14 M5 12h14");
        path.setFill(Color.TRANSPARENT);
        path.setStroke(Color.BLACK);
        path.setStrokeWidth(2);
        return path;
    }
    
    public static SVGPath createEdit() {
        SVGPath path = new SVGPath();
        path.setContent("M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7 M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z");
        path.setFill(Color.TRANSPARENT);
        path.setStroke(ICON_COLOR);
        path.setStrokeWidth(2);
        return path;
    }
    
    public static SVGPath createTrash() {
        SVGPath path = new SVGPath();
        path.setContent("M3 6h18 M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2");
        path.setFill(Color.TRANSPARENT);
        path.setStroke(Color.web("#EF4444"));
        path.setStrokeWidth(2);
        return path;
    }
    
    public static SVGPath createX() {
        SVGPath path = new SVGPath();
        path.setContent("M18 6L6 18 M6 6l12 12");
        path.setFill(Color.TRANSPARENT);
        path.setStroke(ICON_COLOR);
        path.setStrokeWidth(2);
        return path;
    }
    
    public static SVGPath createSmallCrown() {
        SVGPath path = new SVGPath();
        path.setContent("M11.562 3.266a.5.5 0 0 1 .876 0L15.39 8.87a1 1 0 0 0 1.516.294L21.183 5.5a.5.5 0 0 1 .798.519l-2.834 10.246a1 1 0 0 1-.956.734H5.81a1 1 0 0 1-.957-.734L2.02 6.02a.5.5 0 0 1 .798-.519l4.276 3.664a1 1 0 0 0 1.516-.294z");
        path.setFill(ICON_COLOR);
        path.setStroke(ICON_COLOR);
        path.setStrokeWidth(1.5);
        return path;
    }
    
    public static SVGPath createSmallFileText() {
        SVGPath path = new SVGPath();
        path.setContent("M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z M14 2v6h6 M16 13H8 M16 17H8");
        path.setFill(Color.TRANSPARENT);
        path.setStroke(ICON_COLOR);
        path.setStrokeWidth(1.5);
        return path;
    }
}
