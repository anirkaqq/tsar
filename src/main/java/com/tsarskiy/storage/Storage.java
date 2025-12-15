package com.tsarskiy.storage;

import com.tsarskiy.model.Note;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;
import java.util.stream.Collectors;

/**
 * Локальное хранилище заметок приложения.
 * <p>
 * Хранение реализовано через сериализацию списка {@link Note} в файл {@code notes.dat}
 * в выбранной пользователем директории. Путь к директории сохраняется в {@link Preferences}.
 * <p>
 * Класс реализован как Singleton — для использования единого экземпляра хранилища
 * во всём приложении.
 */
public class Storage {

    /** Логгер для фиксации действий и ошибок работы хранилища. */
    private static final Logger logger = LogManager.getLogger(Storage.class);

    /** Единственный экземпляр {@link Storage}. */
    private static Storage instance;

    /** Ключ признака прохождения стартового экрана (onboarding). */
    private static final String ONBOARDED_KEY = "calendar_onboarded";

    /** Ключ пути к директории, выбранной пользователем для хранения данных. */
    private static final String STORAGE_DIR_KEY = "storage_dir";

    /** Имя файла, в который сохраняются заметки. */
    private static final String NOTES_FILE_NAME = "notes.dat";

    /** Хранилище пользовательских настроек. */
    private final Preferences preferences;

    /**
     * Приватный конструктор для реализации Singleton.
     * Инициализирует {@link Preferences}.
     */
    private Storage() {
        this.preferences = Preferences.userNodeForPackage(Storage.class);
        logger.info("Storage инициализирован");
    }

    /**
     * Возвращает единственный экземпляр хранилища.
     *
     * @return экземпляр {@link Storage}
     */
    public static Storage getInstance() {
        if (instance == null) {
            instance = new Storage();
        }
        return instance;
    }

    /**
     * Проверяет, был ли пройден стартовый экран (onboarding).
     *
     * @return {@code true}, если onboarding пройден, иначе {@code false}
     */
    public boolean isOnboarded() {
        boolean onboarded = preferences.getBoolean(ONBOARDED_KEY, false);
        logger.debug("Проверка onboarding: {}", onboarded);
        return onboarded;
    }

    /**
     * Отмечает стартовый экран (onboarding) как пройденный.
     */
    public void setOnboarded() {
        preferences.putBoolean(ONBOARDED_KEY, true);
        logger.info("Onboarding отмечен как пройденный");
    }

    /**
     * Проверяет, указана ли директория хранения и существует ли она на диске.
     *
     * @return {@code true}, если директория хранения существует, иначе {@code false}
     */
    public boolean hasStorageDirectory() {
        String dir = preferences.get(STORAGE_DIR_KEY, null);
        boolean exists = dir != null && Files.exists(Path.of(dir));
        logger.info("Проверка папки хранения: {}", exists);
        return exists;
    }

    /**
     * Устанавливает директорию для хранения данных приложения.
     * <p>
     * Если директория не существует — она будет создана.
     *
     * @param dir путь к директории хранения
     * @throws RuntimeException если не удалось создать директорию
     */
    public void setStorageDirectory(Path dir) {
        preferences.put(STORAGE_DIR_KEY, dir.toAbsolutePath().toString());
        try {
            Files.createDirectories(dir);
            logger.info("Установлена папка хранения: {}", dir);
        } catch (IOException e) {
            logger.error("Ошибка создания папки хранения", e);
            throw new RuntimeException("Не удалось создать директорию хранения", e);
        }
    }

    /**
     * Возвращает путь к файлу заметок {@code notes.dat} в выбранной директории хранения.
     *
     * @return путь к файлу заметок
     * @throws IllegalStateException если директория хранения не выбрана или была удалена
     */
    private Path getNotesFile() {
        String dir = preferences.get(STORAGE_DIR_KEY, null);

        if (dir == null) {
            logger.error("Папка хранения не выбрана");
            throw new IllegalStateException("Папка хранения не выбрана");
        }

        Path storageDir = Path.of(dir);
        if (!Files.exists(storageDir)) {
            logger.error("Папка хранения была удалена: {}", storageDir);
            throw new IllegalStateException("Папка хранения была удалена");
        }

        return storageDir.resolve(NOTES_FILE_NAME);
    }

    /**
     * Загружает все заметки из файла хранения.
     * <p>
     * Если файл не существует — возвращается пустой список.
     *
     * @return список заметок
     */
    public List<Note> getNotes() {
        Path file = getNotesFile();

        if (!Files.exists(file)) {
            logger.warn("Файл заметок не найден, возвращён пустой список");
            return new ArrayList<>();
        }

        try (ObjectInputStream in =
                     new ObjectInputStream(new FileInputStream(file.toFile()))) {

            Object obj = in.readObject();
            if (obj instanceof List<?>) {
                logger.debug("Заметки загружены из файла");
                return (List<Note>) obj;
            }

        } catch (Exception e) {
            logger.error("Ошибка чтения заметок", e);
        }

        return new ArrayList<>();
    }

    /**
     * Сохраняет список заметок в файл хранения.
     *
     * @param notes список заметок для сохранения
     */
    public void saveNotes(List<Note> notes) {
        Path file = getNotesFile();

        try (ObjectOutputStream out =
                     new ObjectOutputStream(new FileOutputStream(file.toFile()))) {
            out.writeObject(notes);
            logger.info("Заметки сохранены, количество: {}", notes.size());
        } catch (IOException e) {
            logger.error("Ошибка сохранения заметок", e);
        }
    }

    /**
     * Добавляет заметку в хранилище.
     *
     * @param note заметка для добавления
     */
    public void addNote(Note note) {
        List<Note> notes = getNotes();
        notes.add(note);
        saveNotes(notes);
        logger.info("Добавлена заметка id={}", note.getId());
    }

    /**
     * Обновляет заметку в хранилище по её идентификатору.
     * Если заметка не найдена — данные не изменяются.
     *
     * @param updated обновлённая заметка
     */
    public void updateNote(Note updated) {
        List<Note> notes = getNotes();

        for (int i = 0; i < notes.size(); i++) {
            if (notes.get(i).getId().equals(updated.getId())) {
                notes.set(i, updated);
                saveNotes(notes);
                logger.info("Обновлена заметка id={}", updated.getId());
                return;
            }
        }

        logger.warn("Заметка для обновления не найдена id={}", updated.getId());
    }

    /**
     * Удаляет заметку из хранилища по её идентификатору.
     *
     * @param note заметка для удаления
     */
    public void deleteNote(Note note) {
        if (note == null || note.getId() == null) {
            logger.warn("Попытка удалить некорректную заметку");
            return;
        }

        List<Note> notes = getNotes();
        boolean removed = notes.removeIf(n ->
                n.getId() != null && n.getId().equals(note.getId())
        );

        if (removed) {
            saveNotes(notes);
            logger.info("Удалена заметка id={}", note.getId());
        } else {
            logger.warn("Заметка для удаления не найдена id={}", note.getId());
        }
    }

    /**
     * Возвращает список заметок за указанную дату.
     *
     * @param date дата, по которой выполняется поиск
     * @return список заметок за выбранную дату
     */
    public List<Note> getNotesForDate(LocalDate date) {
        List<Note> result = getNotes()
                .stream()
                .filter(n -> date.equals(n.getDate()))
                .collect(Collectors.toList());

        logger.debug("Запрошены заметки за {}: {}", date, result.size());
        return result;
    }
}
