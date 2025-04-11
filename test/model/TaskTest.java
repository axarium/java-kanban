package model;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {
    private static final LocalDateTime currentDate = LocalDateTime.now();

    @Test
    void createTaskWithStatus() {
        Task task = new Task(
                "Title",
                "Description",
                TaskStatus.IN_PROGRESS,
                currentDate,
                Duration.ofMinutes(60)
        );

        assertEquals("Title", task.getTitle());
        assertEquals("Description", task.getDescription());
        assertEquals(TaskStatus.IN_PROGRESS, task.getStatus());
        assertEquals(currentDate, task.getStartTime());
        assertEquals(60, task.getDuration().toMinutes());
        assertEquals(currentDate.plusMinutes(60), task.getEndTime());
        assertEquals(0, task.getId());
    }

    @Test
    void createTaskWithoutStatus() {
        Task task = new Task("Title", "Description");

        assertEquals("Title", task.getTitle());
        assertEquals("Description", task.getDescription());
        assertEquals(TaskStatus.NEW, task.getStatus());
        assertEquals(0, task.getId());
        assertEquals(0, task.getDuration().toMinutes());
        assertNull(task.getStartTime());
        assertNull(task.getEndTime());
    }

    @Test
    void copyTask() {
        Task firstTask = new Task(
                "Title",
                "Description",
                TaskStatus.NEW,
                currentDate,
                Duration.ofMinutes(60)
        );
        firstTask.setId(1);
        Task secondTask = new Task(firstTask);

        assertEquals(firstTask.getTitle(), secondTask.getTitle());
        assertEquals(firstTask.getDescription(), secondTask.getDescription());
        assertEquals(firstTask.getStatus(), secondTask.getStatus());
        assertEquals(firstTask.getStartTime(), secondTask.getStartTime());
        assertEquals(firstTask.getEndTime(), secondTask.getEndTime());
        assertEquals(firstTask.getDuration(), secondTask.getDuration());
        assertEquals(firstTask.getId(), secondTask.getId());
    }

    @Test
    void compareTasks() {
        Task firstTask = new Task(
                "Title",
                "Description",
                TaskStatus.NEW,
                currentDate,
                Duration.ofMinutes(60)
        );
        firstTask.setId(1);
        Task secondTask = new Task(firstTask);

        assertNotEquals(null, firstTask);
        assertEquals(firstTask, firstTask);
        assertEquals(firstTask, secondTask);

        secondTask.setTitle("NewTitle");
        secondTask.setDescription("NewDescription");
        secondTask.setStatus(TaskStatus.IN_PROGRESS);
        secondTask.setStartTime(currentDate.plusDays(1));
        secondTask.setDuration(Duration.ofMinutes(120));

        assertEquals(firstTask, secondTask);

        secondTask.setId(2);

        assertNotEquals(firstTask, secondTask);
    }

    @Test
    void getTaskType() {
        Task task = new Task(
                "Title",
                "Description",
                TaskStatus.NEW,
                currentDate,
                Duration.ofMinutes(60)
        );

        assertEquals(TaskType.TASK, task.getType());
    }
}