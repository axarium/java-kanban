package model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SubtaskTest {
    @Test
    void createSubtask() {
        Subtask subtask = new Subtask("Title", "Description", TaskStatus.NEW, 1);

        assertEquals("Title", subtask.getTitle());
        assertEquals("Description", subtask.getDescription());
        assertEquals(TaskStatus.NEW, subtask.getStatus());
        assertEquals(0, subtask.getId());
        assertEquals(1, subtask.getEpicId());
    }

    @Test
    void comparisonSubtasks() {
        Subtask subtask1 = new Subtask("Title", "Description", TaskStatus.NEW, 1);
        Subtask subtask2 = new Subtask("Title", "Description", TaskStatus.NEW, 1);

        assertEquals(subtask1, subtask2);
        assertNotEquals(null, subtask2);

        subtask2.setTitle("NewTitle");
        subtask2.setDescription("NewDescription");
        subtask2.setStatus(TaskStatus.NEW);

        assertEquals(subtask1, subtask2);

        subtask2.setId(1);

        assertNotEquals(subtask1, subtask2);
    }
}