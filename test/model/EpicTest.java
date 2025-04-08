package model;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {

    @Test
    void createEpic() {
        Epic epic = new Epic("Title", "Description");

        assertEquals("Title", epic.getTitle());
        assertEquals("Description", epic.getDescription());
        assertEquals(TaskStatus.NEW, epic.getStatus());
        assertEquals(0, epic.getId());
        assertEquals(0, epic.getDuration().toMinutes());
        assertNull(epic.getStartTime());
        assertNull(epic.getEndTime());
        assertNotNull(epic.getSubtasksIds());
        assertTrue(epic.getSubtasksIds().isEmpty());
    }

    @Test
    void copyEpic() {
        Epic firstEpic = new Epic("Title", "Description");
        firstEpic.setId(1);
        firstEpic.setStartTime(LocalDateTime.now());
        firstEpic.setDuration(Duration.ofMinutes(60));
        firstEpic.setEndTime(firstEpic.getStartTime().plus(firstEpic.getDuration()));
        firstEpic.getSubtasksIds().add(1);
        Epic secondEpic = new Epic(firstEpic);

        assertEquals(firstEpic.getTitle(), secondEpic.getTitle());
        assertEquals(firstEpic.getDescription(), secondEpic.getDescription());
        assertEquals(firstEpic.getStatus(), secondEpic.getStatus());
        assertEquals(firstEpic.getId(), secondEpic.getId());
        assertEquals(firstEpic.getStartTime(), secondEpic.getStartTime());
        assertEquals(firstEpic.getEndTime(), secondEpic.getEndTime());
        assertEquals(firstEpic.getDuration(), secondEpic.getDuration());
        assertEquals(firstEpic.getSubtasksIds(), secondEpic.getSubtasksIds());
    }

    @Test
    void compareEpics() {
        Epic firstEpic = new Epic("Title", "Description");
        firstEpic.setId(1);
        firstEpic.setStartTime(LocalDateTime.now());
        firstEpic.setDuration(Duration.ofMinutes(60));
        firstEpic.setEndTime(firstEpic.getStartTime().plus(firstEpic.getDuration()));
        firstEpic.getSubtasksIds().add(1);
        Epic secondEpic = new Epic(firstEpic);

        assertNotEquals(null, firstEpic);
        assertEquals(firstEpic, firstEpic);
        assertEquals(firstEpic, secondEpic);

        secondEpic.setTitle("NewTitle");
        secondEpic.setDescription("NewDescription");
        secondEpic.setStatus(TaskStatus.IN_PROGRESS);
        secondEpic.setStartTime(LocalDateTime.now().plusDays(1));
        secondEpic.setDuration(Duration.ofMinutes(120));
        secondEpic.setEndTime(firstEpic.getStartTime().plus(firstEpic.getDuration()));
        secondEpic.getSubtasksIds().add(2);

        assertEquals(firstEpic, secondEpic);

        secondEpic.setId(2);

        assertNotEquals(firstEpic, secondEpic);
    }

    @Test
    void getEpicType() {
        Epic epic = new Epic("Title", "Description");

        assertEquals(TaskType.EPIC, epic.getType());
    }
}