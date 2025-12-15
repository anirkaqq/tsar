package com.tsarskiy.storage;

import com.tsarskiy.model.Note;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;
import java.util.stream.Collectors;

public class Storage {

    private static Storage instance;

    private static final String ONBOARDED_KEY = "calendar_onboarded";
    private static final String STORAGE_DIR_KEY = "storage_dir";
    private static final String NOTES_FILE_NAME = "notes.dat";

    private final Preferences preferences;

    private Storage() {
        this.preferences = Preferences.userNodeForPackage(Storage.class);
    }

    /* ===== SINGLETON ===== */
    public static Storage getInstance() {
        if (instance == null) {
            instance = new Storage();
        }
        return instance;
    }

    /* ===== ONBOARDING ===== */
    public boolean isOnboarded() {
        return preferences.getBoolean(ONBOARDED_KEY, false);
    }

    public void setOnboarded() {
        preferences.putBoolean(ONBOARDED_KEY, true);
    }

    /* ===== STORAGE DIR ===== */
    public boolean hasStorageDirectory() {
        String dir = preferences.get(STORAGE_DIR_KEY, null);
        if (dir == null) {
            return false;
        }
        return Files.exists(Path.of(dir));
    }


    public void setStorageDirectory(Path dir) {
        preferences.put(STORAGE_DIR_KEY, dir.toAbsolutePath().toString());
        try {
            Files.createDirectories(dir);
        } catch (IOException e) {
            throw new RuntimeException("Не удалось создать директорию хранения", e);
        }
    }

    private Path getNotesFile() {
        String dir = preferences.get(STORAGE_DIR_KEY, null);

        if (dir == null) {
            throw new IllegalStateException("Папка хранения не выбрана");
        }

        Path storageDir = Path.of(dir);
        if (!Files.exists(storageDir)) {
            throw new IllegalStateException("Папка хранения была удалена");
        }

        return storageDir.resolve(NOTES_FILE_NAME);
    }


    /* ===== LOAD / SAVE ===== */
    public List<Note> getNotes() {
        Path file = getNotesFile();
        if (!Files.exists(file)) return new ArrayList<>();

        try (ObjectInputStream in =
                     new ObjectInputStream(new FileInputStream(file.toFile()))) {

            Object obj = in.readObject();
            if (obj instanceof List<?>) {
                @SuppressWarnings("unchecked")
                List<Note> notes = (List<Note>) obj;
                return notes;
            }

        } catch (Exception ignored) {}

        return new ArrayList<>();
    }

    public void saveNotes(List<Note> notes) {
        Path file = getNotesFile();

        try (ObjectOutputStream out =
                     new ObjectOutputStream(new FileOutputStream(file.toFile()))) {
            out.writeObject(notes);
        } catch (IOException ignored) {}
    }

    /* ===== CRUD ===== */
    public void addNote(Note note) {
        List<Note> notes = getNotes();
        notes.add(note);
        saveNotes(notes);
    }

    public void updateNote(Note updated) {
        List<Note> notes = getNotes();
        for (int i = 0; i < notes.size(); i++) {
            if (notes.get(i).getId().equals(updated.getId())) {
                notes.set(i, updated);
                break;
            }
        }
        saveNotes(notes);
    }

    public void deleteNote(Note note) {
        if (note == null || note.getId() == null) return;

        List<Note> notes = getNotes();
        boolean removed = notes.removeIf(n ->
                n.getId() != null && n.getId().equals(note.getId())
        );

        if (removed) {
            saveNotes(notes);
        }
    }

    /* ===== QUERIES ===== */
    public List<Note> getNotesForDate(LocalDate date) {
        return getNotes()
                .stream()
                .filter(n -> date.equals(n.getDate()))
                .collect(Collectors.toList());
    }
}
