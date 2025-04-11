package model;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class SubtaskTest {
    private static final LocalDateTime currentDate = LocalDateTime.now();

    @Test
    void createSubtask() {
        Subtask subtask = new Subtask(
                "Title",
                "Description",
                TaskStatus.NEW,
                currentDate,
                Duration.ofMinutes(60),
                1
        );
        subtask.setId(1);

        assertEquals("Title", subtask.getTitle());
        assertEquals("Description", subtask.getDescription());
        assertEquals(TaskStatus.NEW, subtask.getStatus());
        assertEquals(currentDate, subtask.getStartTime());
        assertEquals(60, subtask.getDuration().toMinutes());
        assertEquals(currentDate.plusMinutes(60), subtask.getEndTime());
        assertEquals(1, subtask.getId());
        assertEquals(1, subtask.getEpicId());
    }

    @Test
    void copySubtask() {
        Subtask firstSubtask = new Subtask(
                "Title",
                "Description",
                TaskStatus.NEW,
                currentDate,
                Duration.ofMinutes(60),
                1
        );
        firstSubtask.setId(1);
        Subtask secondSubtask = new Subtask(firstSubtask);

        assertEquals(firstSubtask.getTitle(), secondSubtask.getTitle());
        assertEquals(firstSubtask.getDescription(), secondSubtask.getDescription());
        assertEquals(firstSubtask.getStatus(), secondSubtask.getStatus());
        assertEquals(firstSubtask.getStartTime(), secondSubtask.getStartTime());
        assertEquals(firstSubtask.getEndTime(), secondSubtask.getEndTime());
        assertEquals(firstSubtask.getDuration(), secondSubtask.getDuration());
        assertEquals(firstSubtask.getId(), secondSubtask.getId());
        assertEquals(firstSubtask.getEpicId(), secondSubtask.getEpicId());
    }

    @Test
    void compareSubtasks() {
        Subtask firstSubtask = new Subtask(
                "Title",
                "Description",
                TaskStatus.NEW,
                currentDate,
                Duration.ofMinutes(60),
                1
        );
        firstSubtask.setId(1);
        Subtask secondSubtask = new Subtask(firstSubtask);

        assertNotEquals(null, firstSubtask);
        assertEquals(firstSubtask, firstSubtask);
        assertEquals(firstSubtask, secondSubtask);

        secondSubtask.setTitle("NewTitle");
        secondSubtask.setDescription("NewDescription");
        secondSubtask.setStatus(TaskStatus.IN_PROGRESS);
        secondSubtask.setStartTime(currentDate.plusDays(1));
        secondSubtask.setDuration(Duration.ofMinutes(120));

        assertEquals(firstSubtask, secondSubtask);

        secondSubtask.setId(2);

        assertNotEquals(firstSubtask, secondSubtask);
    }

    @Test
    void getSubtaskType() {
        Subtask subtask = new Subtask(
                "Title",
                "Description",
                TaskStatus.NEW,
                currentDate,
                Duration.ofMinutes(60),
                1
        );

        assertEquals(TaskType.SUBTASK, subtask.getType());
    }
}