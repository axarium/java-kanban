package util;

import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class TaskConverterTest {

    @Test
    void createTaskFromCsvString() {
        String validString = "1,TASK,Title,NEW,Description,14.01.2001 11:00:00,60,14.01.2001 12:00:00";
        String firstInvalidString = "id,TASK,Title,NEW,Description,14.01.2001 11:00:00,60,14.01.2001 12:00:00";
        String secondInvalidString = "1,TASK,NEW,Description";
        String thirdInvalidString = "1,TASK,Title,UNKNOWN,Description,14.01.2001 11:00:00,60,14.01.2001 12:00:00";
        Task task = TaskConverter.anyTaskFromCsvString(validString);

        assertEquals(1, task.getId());
        assertEquals("Title", task.getTitle());
        assertEquals("Description", task.getDescription());
        assertEquals(TaskStatus.NEW, task.getStatus());
        assertEquals(
                LocalDateTime.of(2001, 1, 14, 11, 0, 0),
                task.getStartTime()
        );
        assertEquals(
                LocalDateTime.of(2001, 1, 14, 12, 0, 0),
                task.getEndTime()
        );
        assertEquals(Duration.ofMinutes(60), task.getDuration());
        assertThrows(IllegalArgumentException.class, () -> TaskConverter.anyTaskFromCsvString(firstInvalidString));
        assertThrows(IndexOutOfBoundsException.class, () -> TaskConverter.anyTaskFromCsvString(secondInvalidString));
        assertThrows(IllegalArgumentException.class, () -> TaskConverter.anyTaskFromCsvString(thirdInvalidString));
    }

    @Test
    void createEpicCsvFromString() {
        String validString = "1,EPIC,Title,NEW,Description,14.01.2001 11:00:00,60,14.01.2001 12:00:00";
        String firstInvalidString = "id,EPIC,Title,NEW,Description,14.01.2001 11:00:00,60,14.01.2001 12:00:00";
        String secondInvalidString = "1,EPIC,NEW,Description,14.01.2001 11:00:00,60,14.01.2001 12:00:00";
        String thirdInvalidString = "1,EPIC,Title,UNKNOWN,Description,14.01.2001 11:00:00,60,14.01.2001 12:00:00";
        Epic epic = (Epic) TaskConverter.anyTaskFromCsvString(validString);

        assertEquals(1, epic.getId());
        assertEquals("Title", epic.getTitle());
        assertEquals("Description", epic.getDescription());
        assertEquals(TaskStatus.NEW, epic.getStatus());
        assertEquals(
                LocalDateTime.of(2001, 1, 14, 11, 0, 0),
                epic.getStartTime()
        );
        assertEquals(
                LocalDateTime.of(2001, 1, 14, 12, 0, 0),
                epic.getEndTime()
        );
        assertEquals(Duration.ofMinutes(60), epic.getDuration());
        assertThrows(IllegalArgumentException.class, () -> TaskConverter.anyTaskFromCsvString(firstInvalidString));
        assertThrows(IndexOutOfBoundsException.class, () -> TaskConverter.anyTaskFromCsvString(secondInvalidString));
        assertThrows(IllegalArgumentException.class, () -> TaskConverter.anyTaskFromCsvString(thirdInvalidString));
    }

    @Test
    void createSubtaskFromCsvString() {
        String validString = "1,SUBTASK,Title,NEW,Description,14.01.2001 11:00:00,60,14.01.2001 12:00:00,1";
        String firstInvalidString = "id,SUBTASK,Title,NEW,Description,14.01.2001 11:00:00,60,14.01.2001 12:00:00,1";
        String secondInvalidString = "1,SUBTASK,NEW,Description,14.01.2001 11:00:00,60,14.01.2001 12:00:00,1";
        String thirdInvalidString = "1,SUBTASK,Title,UNKNOWN,Description,14.01.2001 11:00:00,60,14.01.2001 12:00:00,1";
        String fourthInvalidString = "1,SUBTASK,Title,NEW,Description,14.01.2001 11:00:00,60,14.01.2001 12:00:00,epic";
        Subtask subtask = (Subtask) TaskConverter.anyTaskFromCsvString(validString);

        assertEquals(1, subtask.getId());
        assertEquals("Title", subtask.getTitle());
        assertEquals("Description", subtask.getDescription());
        assertEquals(TaskStatus.NEW, subtask.getStatus());
        assertEquals(1, subtask.getEpicId());
        assertEquals(
                LocalDateTime.of(2001, 1, 14, 11, 0, 0),
                subtask.getStartTime()
        );
        assertEquals(
                LocalDateTime.of(2001, 1, 14, 12, 0, 0),
                subtask.getEndTime()
        );
        assertEquals(Duration.ofMinutes(60), subtask.getDuration());
        assertThrows(IllegalArgumentException.class, () -> TaskConverter.anyTaskFromCsvString(firstInvalidString));
        assertThrows(IndexOutOfBoundsException.class, () -> TaskConverter.anyTaskFromCsvString(secondInvalidString));
        assertThrows(IllegalArgumentException.class, () -> TaskConverter.anyTaskFromCsvString(thirdInvalidString));
        assertThrows(IllegalArgumentException.class, () -> TaskConverter.anyTaskFromCsvString(fourthInvalidString));
    }
}