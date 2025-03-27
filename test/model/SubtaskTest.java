package model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SubtaskTest {

    @Test
    void createSubtask() {
        Subtask subtask = new Subtask("Title", "Description", TaskStatus.NEW, 1);
        subtask.setId(1);

        assertEquals("Title", subtask.getTitle());
        assertEquals("Description", subtask.getDescription());
        assertEquals(TaskStatus.NEW, subtask.getStatus());
        assertEquals(1, subtask.getId());
        assertEquals(1, subtask.getEpicId());
    }

    @Test
    void copySubtask() {
        Subtask firstSubtask = new Subtask("Title", "Description", TaskStatus.NEW, 1);
        firstSubtask.setId(1);
        Subtask secondSubtask = new Subtask(firstSubtask);

        assertEquals(firstSubtask.getTitle(), secondSubtask.getTitle());
        assertEquals(firstSubtask.getDescription(), secondSubtask.getDescription());
        assertEquals(firstSubtask.getStatus(), secondSubtask.getStatus());
        assertEquals(firstSubtask.getId(), secondSubtask.getId());
        assertEquals(firstSubtask.getEpicId(), secondSubtask.getEpicId());
    }

    @Test
    void compareSubtasks() {
        Subtask firstSubtask = new Subtask("Title", "Description", TaskStatus.NEW, 1);
        firstSubtask.setId(1);
        Subtask secondSubtask = new Subtask(firstSubtask);

        assertNotEquals(null, firstSubtask);
        assertEquals(firstSubtask, firstSubtask);
        assertEquals(firstSubtask, secondSubtask);

        secondSubtask.setTitle("NewTitle");
        secondSubtask.setDescription("NewDescription");
        secondSubtask.setStatus(TaskStatus.IN_PROGRESS);

        assertEquals(firstSubtask, secondSubtask);

        secondSubtask.setId(2);

        assertNotEquals(firstSubtask, secondSubtask);
    }

    @Test
    void getSubtaskType() {
        Subtask subtask = new Subtask("Title", "Description", TaskStatus.NEW, 1);

        assertEquals(TaskType.SUBTASK, subtask.getType());
    }
}