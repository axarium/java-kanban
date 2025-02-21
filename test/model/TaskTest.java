package model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    @Test
    void createTaskWithStatus() {
        Task task = new Task("Title", "Description", TaskStatus.IN_PROGRESS);

        assertEquals("Title", task.getTitle());
        assertEquals("Description", task.getDescription());
        assertEquals(TaskStatus.IN_PROGRESS, task.getStatus());
        assertEquals(0, task.getId());
    }

    @Test
    void createTaskWithoutStatus() {
        Task task = new Task("Title", "Description");

        assertEquals("Title", task.getTitle());
        assertEquals("Description", task.getDescription());
        assertEquals(TaskStatus.NEW, task.getStatus());
        assertEquals(0, task.getId());
    }

    @Test
    void copyTask() {
        Task task1 = new Task("Title", "Description", TaskStatus.DONE);
        Task task2 = new Task(task1);

        assertEquals(task1.getTitle(), task2.getTitle());
        assertEquals(task1.getDescription(), task2.getDescription());
        assertEquals(task1.getStatus(), task2.getStatus());
        assertEquals(task1.getId(), task2.getId());
    }

    @Test
    void comparisonTasks() {
        Task task1 = new Task("Title", "Description", TaskStatus.DONE);
        Task task2 = new Task(task1);

        assertEquals(task1, task2);
        assertNotEquals(null, task2);

        task2.setTitle("NewTitle");
        task2.setDescription("NewDescription");
        task2.setStatus(TaskStatus.IN_PROGRESS);

        assertEquals(task1, task2);

        task2.setId(1);

        assertNotEquals(task1, task2);
    }
}