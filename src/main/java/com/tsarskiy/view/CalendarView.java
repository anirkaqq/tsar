package com.tsarskiy.view;

import com.tsarskiy.model.Note;
import com.tsarskiy.service.HolidayService;
import com.tsarskiy.storage.Storage;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.shape.SVGPath;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CalendarView {

    private static final double CELL = 110;
    private static final double GAP = 10;
    private static final double SCALE = 0.92;

    private final StackPane root;
    private final Storage storage;
    private final HolidayService holidayService = new HolidayService();

    private YearMonth currentMonth = YearMonth.now();
    private LocalDate selectedDate;
    private final LocalDate today = LocalDate.now();

    private Map<LocalDate, String> holidays;

    private final GridPane calendarGrid = new GridPane();
    private final Label monthLabel = new Label();
    private final Label infoLabel = new Label();
    private final Button addNoteButton = new Button("Добавить заметку");

    private HBox header;

    public CalendarView(Storage storage) {
        this.storage = storage;
        this.holidays = holidayService.getHolidaysForMonth(currentMonth);
        this.root = build();
        updateMonthLabel();
        updateGrid();
    }

    private StackPane build() {
        StackPane screen = new StackPane();
        screen.getStyleClass().add("calendar-screen");

        VBox card = new VBox(18);
        card.getStyleClass().add("calendar-card");
        card.setAlignment(Pos.TOP_CENTER);
        card.setPadding(new Insets(28));
        card.setMaxWidth(1000);

        card.setScaleX(SCALE);
        card.setScaleY(SCALE);

        /* ===== HEADER ===== */
        /* ===== HEADER ===== */
        header = new HBox(12);
        header.setAlignment(Pos.CENTER_LEFT);

        /* crown */
        SVGPath crown = IconFactory.createCrown();
        crown.getStyleClass().add("calendar-crown-icon");

        /* title */
        Label title = new Label("Царский Заметник");
        title.getStyleClass().add("calendar-title");

        /* box with crown + text */
        HBox titleBox = new HBox(8, crown, title);
        titleBox.setAlignment(Pos.CENTER_LEFT);
        /* spacer */
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        /* close */
        Button closeButton = new Button();
        closeButton.getStyleClass().add("close-button");
        closeButton.setGraphic(IconFactory.createX());
        closeButton.setOnAction(e ->
                closeButton.getScene().getWindow().hide()
        );

        header.getChildren().addAll(titleBox, spacer, closeButton);



        /* INFO */
        infoLabel.getStyleClass().add("calendar-info");
        infoLabel.setVisible(false);
        infoLabel.managedProperty().bind(infoLabel.visibleProperty());

        StackPane infoWrapper = new StackPane(infoLabel);
        infoWrapper.setAlignment(Pos.CENTER);
        infoWrapper.setMinHeight(26);

        /* NAV */
        Button prev = navButton(IconFactory.createChevronLeft(), this::prevMonth);
        Button next = navButton(IconFactory.createChevronRight(), this::nextMonth);

        monthLabel.getStyleClass().add("calendar-month");

        Region l = new Region();
        Region r = new Region();
        HBox.setHgrow(l, Priority.ALWAYS);
        HBox.setHgrow(r, Priority.ALWAYS);

        HBox nav = new HBox(14, prev, l, monthLabel, r, next);
        nav.setAlignment(Pos.CENTER);

        /* WEEKDAYS */
        GridPane weekdays = new GridPane();
        weekdays.setHgap(GAP);
        weekdays.setAlignment(Pos.CENTER);

        String[] days = {"Пн","Вт","Ср","Чт","Пт","Сб","Вс"};
        for (int i = 0; i < 7; i++) {
            Label wd = new Label(days[i]);
            wd.getStyleClass().add("calendar-weekday");
            wd.setPrefWidth(CELL);
            wd.setAlignment(Pos.CENTER);
            weekdays.add(wd, i, 0);
        }

        /* GRID */
        calendarGrid.setHgap(GAP);
        calendarGrid.setVgap(GAP);

        calendarGrid.getColumnConstraints().clear();
        calendarGrid.getRowConstraints().clear();

        for (int i = 0; i < 7; i++) {
            calendarGrid.getColumnConstraints().add(new ColumnConstraints(CELL));
        }
        for (int i = 0; i < 6; i++) {
            calendarGrid.getRowConstraints().add(new RowConstraints(CELL));
        }

        calendarGrid.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);

        HBox gridWrapper = new HBox(calendarGrid);
        gridWrapper.setAlignment(Pos.CENTER);

        /* BUTTON */
        addNoteButton.getStyleClass().add("onboarding-button");
        addNoteButton.setPrefWidth(300);
        addNoteButton.setDisable(true);
        addNoteButton.setOnAction(e ->
                new NoteModal(selectedDate, null, storage, this::updateGrid)
                        .show(root.getScene().getWindow())
        );

        card.getChildren().addAll(
                header,
                infoWrapper,
                nav,
                weekdays,
                gridWrapper,
                addNoteButton
        );

        screen.getChildren().add(card);
        return screen;
    }

    public HBox getHeader() {
        return header;
    }

    private Button navButton(SVGPath icon, Runnable action) {
        icon.getStyleClass().add("calendar-nav-icon");
        Button b = new Button();
        b.getStyleClass().add("calendar-nav");
        b.setGraphic(icon);
        b.setOnAction(e -> action.run());
        return b;
    }

    private void updateGrid() {
        calendarGrid.getChildren().clear();

        LocalDate firstOfMonth = currentMonth.atDay(1);
        int startIndex = firstOfMonth.getDayOfWeek().getValue() - 1;
        int daysInMonth = currentMonth.lengthOfMonth();

        int cellIndex = 0;

        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < 7; col++) {
                int dayNumber = cellIndex - startIndex + 1;
                if (dayNumber >= 1 && dayNumber <= daysInMonth) {
                    calendarGrid.add(dayCell(currentMonth.atDay(dayNumber)), col, row);
                }
                cellIndex++;
            }
        }

        addNoteButton.setDisable(selectedDate == null);
    }

    private Button dayCell(LocalDate date) {
        Button b = new Button();
        b.getStyleClass().add("calendar-day");
        b.setPrefSize(CELL, CELL);

        List<Note> notes = storage.getNotesForDate(date);
        String holiday = holidays.get(date);

        VBox content = new VBox(6);
        content.setAlignment(Pos.TOP_CENTER);
        content.setPadding(new Insets(8, 6, 8, 6));

        StackPane top = new StackPane();
        top.setMinHeight(20);

        if (holiday != null) {
            SVGPath crown = IconFactory.createCrown();
            crown.getStyleClass().add("calendar-holiday-icon");
            crown.setScaleX(0.65);
            crown.setScaleY(0.65);
            top.getChildren().add(crown);
        }

        Label num = new Label(String.valueOf(date.getDayOfMonth()));
        num.getStyleClass().add("calendar-day-number");

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        StackPane bottom = new StackPane();
        bottom.setMinHeight(20);

        if (!notes.isEmpty()) {
            SVGPath noteIcon = IconFactory.createSmallFileText();
            noteIcon.getStyleClass().add("calendar-note-icon");
            bottom.getChildren().add(noteIcon);
        }

        content.getChildren().addAll(top, num, spacer, bottom);
        b.setGraphic(content);

        if (date.equals(today)) b.getStyleClass().add("today");
        if (date.equals(selectedDate)) b.getStyleClass().add("selected");

        b.setOnMouseEntered(e -> showInfo(date));
        b.setOnMouseExited(e -> infoLabel.setVisible(false));

        b.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2 && !notes.isEmpty()) {
                new NotesListModal(notes, noteToEdit -> {
                    new NoteModal(
                            noteToEdit.getDate(),
                            noteToEdit,
                            storage,
                            this::updateGrid
                    ).show(root.getScene().getWindow());
                }).show(root.getScene().getWindow());


                updateGrid();
                return;
            }

            selectedDate = date;
            showInfo(date);
            updateGrid();
        });

        return b;
    }

    private void showInfo(LocalDate date) {
        String holiday = holidays.get(date);
        List<Note> notes = storage.getNotesForDate(date);

        StringBuilder sb = new StringBuilder();
        if (holiday != null) sb.append(holiday);
        if (!notes.isEmpty()) {
            if (sb.length() > 0) sb.append(" | ");
            sb.append("Заметок: ").append(notes.size());
        }

        if (sb.length() > 0) {
            infoLabel.setText(sb.toString());
            infoLabel.setVisible(true);
        } else {
            infoLabel.setVisible(false);
        }
    }

    private void prevMonth() {
        currentMonth = currentMonth.minusMonths(1);
        holidays = holidayService.getHolidaysForMonth(currentMonth);
        selectedDate = null;
        updateMonthLabel();
        updateGrid();
    }

    private void nextMonth() {
        currentMonth = currentMonth.plusMonths(1);
        holidays = holidayService.getHolidaysForMonth(currentMonth);
        selectedDate = null;
        updateMonthLabel();
        updateGrid();
    }

    private void updateMonthLabel() {
        String m = currentMonth.getMonth()
                .getDisplayName(TextStyle.FULL_STANDALONE, new Locale("ru"));
        monthLabel.setText(
                m.substring(0, 1).toUpperCase() + m.substring(1)
                        + " " + currentMonth.getYear()
        );
    }

    public StackPane getView() {
        return root;
    }
}
