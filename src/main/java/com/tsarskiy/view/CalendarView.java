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

/**
 * Основное представление календаря приложения.
 * <p>
 * Класс отвечает за:
 * <ul>
 *     <li>Отображение календарной сетки месяца</li>
 *     <li>Навигацию между месяцами</li>
 *     <li>Отображение праздников</li>
 *     <li>Работу с заметками (просмотр, добавление, редактирование)</li>
 * </ul>
 * <p>
 * Использует {@link Storage} для хранения заметок и {@link HolidayService}
 * для получения государственных праздников.
 */
public class CalendarView {

    /** Размер одной ячейки дня календаря. */
    private static final double CELL = 110;

    /** Отступ между ячейками календаря. */
    private static final double GAP = 10;

    /** Масштаб всего календаря. */
    private static final double SCALE = 0.92;

    /** Корневой контейнер представления. */
    private final StackPane root;

    /** Хранилище заметок. */
    private final Storage storage;

    /** Сервис получения праздников. */
    private final HolidayService holidayService = new HolidayService();

    /** Текущий отображаемый месяц. */
    private YearMonth currentMonth = YearMonth.now();

    /** Выбранная пользователем дата. */
    private LocalDate selectedDate;

    /** Текущая дата (сегодня). */
    private final LocalDate today = LocalDate.now();

    /** Карта праздников текущего месяца. */
    private Map<LocalDate, String> holidays;

    /** Сетка календаря. */
    private final GridPane calendarGrid = new GridPane();

    /** Метка с названием месяца. */
    private final Label monthLabel = new Label();

    /** Информационная строка (праздники, количество заметок). */
    private final Label infoLabel = new Label();

    /** Кнопка добавления заметки. */
    private final Button addNoteButton = new Button("Добавить заметку");

    /** Хедер календаря. */
    private HBox header;

    /**
     * Создаёт представление календаря.
     *
     * @param storage хранилище заметок
     */
    public CalendarView(Storage storage) {
        this.storage = storage;
        this.holidays = holidayService.getHolidaysForMonth(currentMonth);
        this.root = build();
        updateMonthLabel();
        updateGrid();
    }

    /**
     * Строит интерфейс календаря.
     *
     * @return корневой контейнер представления
     */
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

        /* ================= HEADER ================= */
        header = new HBox(12);
        header.setAlignment(Pos.CENTER_LEFT);

        SVGPath crown = IconFactory.createCrown();
        crown.getStyleClass().add("calendar-crown-icon");

        Label title = new Label("Царский Заметник");
        title.getStyleClass().add("calendar-title");

        HBox titleBox = new HBox(8, crown, title);
        titleBox.setAlignment(Pos.CENTER_LEFT);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button closeButton = new Button();
        closeButton.getStyleClass().add("close-button");
        closeButton.setGraphic(IconFactory.createX());
        closeButton.setOnAction(e ->
                closeButton.getScene().getWindow().hide()
        );

        header.getChildren().addAll(titleBox, spacer, closeButton);

        /* ================= INFO ================= */
        infoLabel.getStyleClass().add("calendar-info");
        infoLabel.setVisible(false);
        infoLabel.managedProperty().bind(infoLabel.visibleProperty());

        StackPane infoWrapper = new StackPane(infoLabel);
        infoWrapper.setAlignment(Pos.CENTER);
        infoWrapper.setMinHeight(26);

        /* ================= NAVIGATION ================= */
        Button prev = navButton(IconFactory.createChevronLeft(), this::prevMonth);
        Button next = navButton(IconFactory.createChevronRight(), this::nextMonth);

        monthLabel.getStyleClass().add("calendar-month");

        Region l = new Region();
        Region r = new Region();
        HBox.setHgrow(l, Priority.ALWAYS);
        HBox.setHgrow(r, Priority.ALWAYS);

        HBox nav = new HBox(14, prev, l, monthLabel, r, next);
        nav.setAlignment(Pos.CENTER);

        /* ================= WEEKDAYS ================= */
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

        /* ================= GRID ================= */
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

        HBox gridWrapper = new HBox(calendarGrid);
        gridWrapper.setAlignment(Pos.CENTER);

        /* ================= ADD NOTE BUTTON ================= */
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

    /**
     * Возвращает хедер календаря.
     *
     * @return контейнер хедера
     */
    public HBox getHeader() {
        return header;
    }

    /**
     * Создаёт кнопку навигации по месяцам.
     *
     * @param icon иконка кнопки
     * @param action действие при нажатии
     * @return кнопка навигации
     */
    private Button navButton(SVGPath icon, Runnable action) {
        icon.getStyleClass().add("calendar-nav-icon");
        Button b = new Button();
        b.getStyleClass().add("calendar-nav");
        b.setGraphic(icon);
        b.setOnAction(e -> action.run());
        return b;
    }

    /**
     * Обновляет сетку календаря в соответствии с текущим месяцем.
     */
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

    /**
     * Создаёт кнопку одного дня календаря.
     *
     * @param date дата, соответствующая ячейке
     * @return кнопка дня
     */
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

    /**
     * Отображает информацию о дне (праздник и количество заметок).
     *
     * @param date дата, для которой показывается информация
     */
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

    /** Переход к предыдущему месяцу. */
    private void prevMonth() {
        currentMonth = currentMonth.minusMonths(1);
        holidays = holidayService.getHolidaysForMonth(currentMonth);
        selectedDate = null;
        updateMonthLabel();
        updateGrid();
    }

    /** Переход к следующему месяцу. */
    private void nextMonth() {
        currentMonth = currentMonth.plusMonths(1);
        holidays = holidayService.getHolidaysForMonth(currentMonth);
        selectedDate = null;
        updateMonthLabel();
        updateGrid();
    }

    /** Обновляет текст заголовка текущего месяца. */
    private void updateMonthLabel() {
        String m = currentMonth.getMonth()
                .getDisplayName(TextStyle.FULL_STANDALONE, new Locale("ru"));
        monthLabel.setText(
                m.substring(0, 1).toUpperCase() + m.substring(1)
                        + " " + currentMonth.getYear()
        );
    }

    /**
     * Возвращает корневой узел представления календаря.
     *
     * @return корневой контейнер
     */
    public StackPane getView() {
        return root;
    }
}
