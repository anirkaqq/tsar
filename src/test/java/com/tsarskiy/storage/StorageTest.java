package com.tsarskiy.storage;

import com.tsarskiy.model.Note;
import org.junit.jupiter.api.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StorageTest {

    private Storage storage;
    private Path tempDir;

    @BeforeEach
    void setUp() throws Exception {
        storage = Storage.getInstance();
        tempDir = Files.createTempDirectory("storage-test");
        storage.setStorageDirectory(tempDir);
    }

    @AfterEach
    void tearDown() throws Exception {
        Files.walk(tempDir)
                .map(Path::toFile)
                .forEach(file -> {
                    if (!file.delete()) {
                        file.deleteOnExit();
                    }
                });
    }

    @Test
    void addNote_shouldStoreNote() {
        Note note = new Note(
                LocalDate.now(),
                "Test",
                "Content"
        );

        storage.addNote(note);

        List<Note> notes = storage.getNotes();
        assertEquals(1, notes.size());
    }


    @Test
    void deleteNote_shouldRemoveNote() {
        Note note = new Note(
                "Test",
                "Content",
                LocalDate.now()
        );

        storage.addNote(note);
        storage.deleteNote(note);

        assertTrue(storage.getNotes().isEmpty());
    }

    @Test
    void getNotesForDate_shouldReturnOnlyMatchingDate() {
        LocalDate today = LocalDate.now();

        storage.addNote(new Note("A", "1", today));
        storage.addNote(new Note("B", "2", today.minusDays(1)));

        List<Note> result = storage.getNotesForDate(today);

        assertEquals(1, result.size());
        assertEquals("A", result.get(0).getTitle());
    }
}
