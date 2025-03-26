package util;

import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TaskConverterTest {

    @Test
    void createTaskFromCsvString() {
        String validString = "1,TASK,Title,NEW,Description";
        String firstInvalidString = "id,TASK,Title,NEW,Description";
        String secondInvalidString = "1,TASK,NEW,Description";
        String thirdInvalidString = "1,TASK,Title,UNKNOWN,Description";
        Task task = TaskConverter.taskFromCsvString(validString);

        assertEquals(1, task.getId());
        assertEquals("Title", task.getTitle());
        assertEquals("Description", task.getDescription());
        assertEquals(TaskStatus.NEW, task.getStatus());
        assertThrows(IllegalArgumentException.class, () -> TaskConverter.taskFromCsvString(firstInvalidString));
        assertThrows(IndexOutOfBoundsException.class, () -> TaskConverter.taskFromCsvString(secondInvalidString));
        assertThrows(IllegalArgumentException.class, () -> TaskConverter.taskFromCsvString(thirdInvalidString));
    }

    @Test
    void createEpicCsvFromString() {
        String validString = "1,EPIC,Title,NEW,Description";
        String firstInvalidString = "id,EPIC,Title,NEW,Description";
        String secondInvalidString = "1,EPIC,NEW,Description";
        String thirdInvalidString = "1,EPIC,Title,UNKNOWN,Description";
        Epic epic = TaskConverter.epicFromCsvString(validString);

        assertEquals(1, epic.getId());
        assertEquals("Title", epic.getTitle());
        assertEquals("Description", epic.getDescription());
        assertEquals(TaskStatus.NEW, epic.getStatus());
        assertThrows(IllegalArgumentException.class, () -> TaskConverter.epicFromCsvString(firstInvalidString));
        assertThrows(IndexOutOfBoundsException.class, () -> TaskConverter.epicFromCsvString(secondInvalidString));
        assertThrows(IllegalArgumentException.class, () -> TaskConverter.epicFromCsvString(thirdInvalidString));
    }

    @Test
    void createSubtaskFromCsvString() {
        String validString = "1,SUBTASK,Title,NEW,Description,1";
        String firstInvalidString = "id,SUBTASK,Title,NEW,Description,1";
        String secondInvalidString = "1,SUBTASK,NEW,Description,1";
        String thirdInvalidString = "1,SUBTASK,Title,UNKNOWN,Description,1";
        String fourthInvalidString = "1,SUBTASK,Title,NEW,Description,epicId";
        Subtask subtask = TaskConverter.subtaskFromCsvString(validString);

        assertEquals(1, subtask.getId());
        assertEquals("Title", subtask.getTitle());
        assertEquals("Description", subtask.getDescription());
        assertEquals(TaskStatus.NEW, subtask.getStatus());
        assertEquals(1, subtask.getEpicId());
        assertThrows(IllegalArgumentException.class, () -> TaskConverter.subtaskFromCsvString(firstInvalidString));
        assertThrows(IndexOutOfBoundsException.class, () -> TaskConverter.subtaskFromCsvString(secondInvalidString));
        assertThrows(IllegalArgumentException.class, () -> TaskConverter.subtaskFromCsvString(thirdInvalidString));
        assertThrows(IllegalArgumentException.class, () -> TaskConverter.subtaskFromCsvString(fourthInvalidString));
    }
}