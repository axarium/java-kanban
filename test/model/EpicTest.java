package model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {

    @Test
    void createEpic() {
        Epic epic = new Epic("Title", "Description");

        assertEquals("Title", epic.getTitle());
        assertEquals("Description", epic.getDescription());
        assertEquals(TaskStatus.NEW, epic.getStatus());
        assertEquals(0, epic.getId());
        assertNotNull(epic.getSubtasksIds());
        assertTrue(epic.getSubtasksIds().isEmpty());
    }

    @Test
    void copyEpic() {
        Epic firstEpic = new Epic("Title", "Description");
        firstEpic.setId(1);
        firstEpic.getSubtasksIds().add(1);
        Epic secondEpic = new Epic(firstEpic);

        assertEquals(firstEpic.getTitle(), secondEpic.getTitle());
        assertEquals(firstEpic.getDescription(), secondEpic.getDescription());
        assertEquals(firstEpic.getStatus(), secondEpic.getStatus());
        assertEquals(firstEpic.getId(), secondEpic.getId());
        assertEquals(firstEpic.getSubtasksIds(), secondEpic.getSubtasksIds());
    }

    @Test
    void compareEpics() {
        Epic firstEpic = new Epic("Title", "Description");
        firstEpic.setId(1);
        firstEpic.getSubtasksIds().add(1);
        Epic secondEpic = new Epic(firstEpic);

        assertNotEquals(null, firstEpic);
        assertEquals(firstEpic, firstEpic);
        assertEquals(firstEpic, secondEpic);

        secondEpic.setTitle("NewTitle");
        secondEpic.setDescription("NewDescription");
        secondEpic.setStatus(TaskStatus.IN_PROGRESS);
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