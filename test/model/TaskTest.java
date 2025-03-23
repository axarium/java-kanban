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
        Task firstTask = new Task("Title", "Description", TaskStatus.NEW);
        firstTask.setId(1);
        Task secondTask = new Task(firstTask);

        assertEquals(firstTask.getTitle(), secondTask.getTitle());
        assertEquals(firstTask.getDescription(), secondTask.getDescription());
        assertEquals(firstTask.getStatus(), secondTask.getStatus());
        assertEquals(firstTask.getId(), secondTask.getId());
    }

    @Test
    void compareTasks() {
        Task firstTask = new Task("Title", "Description", TaskStatus.NEW);
        firstTask.setId(1);
        Task secondTask = new Task(firstTask);

        assertNotEquals(null, firstTask);
        assertEquals(firstTask, firstTask);
        assertEquals(firstTask, secondTask);

        secondTask.setTitle("NewTitle");
        secondTask.setDescription("NewDescription");
        secondTask.setStatus(TaskStatus.IN_PROGRESS);

        assertEquals(firstTask, secondTask);

        secondTask.setId(2);

        assertNotEquals(firstTask, secondTask);
    }

    @Test
    void createTaskFromString() {
        String validString = "1,TASK,Title,NEW,Description";
        String firstInvalidString = "id,TASK,Title,NEW,Description";
        String secondInvalidString = "1,TASK,NEW,Description";
        String thirdInvalidString = "1,TASK,Title,UNKNOWN,Description";
        Task task = Task.fromString(validString);

        assertEquals(1, task.getId());
        assertEquals("Title", task.getTitle());
        assertEquals("Description", task.getDescription());
        assertEquals(TaskStatus.NEW, task.getStatus());
        assertThrows(IllegalArgumentException.class, () -> Task.fromString(firstInvalidString));
        assertThrows(IndexOutOfBoundsException.class, () -> Task.fromString(secondInvalidString));
        assertThrows(IllegalArgumentException.class, () -> Task.fromString(thirdInvalidString));
    }
}