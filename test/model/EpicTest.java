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
    void comparisonEpics() {
        Epic epic1 = new Epic("Title", "Description");
        Epic epic2 = new Epic("Title", "Description");

        assertEquals(epic1, epic2);
        assertNotEquals(null, epic2);

        epic2.setTitle("NewTitle");
        epic2.setDescription("NewDescription");
        epic2.setStatus(TaskStatus.IN_PROGRESS);

        assertEquals(epic1, epic2);

        epic2.setId(1);

        assertNotEquals(epic1, epic2);
    }

    @Test
    void epicToString() {
        Epic epic = new Epic("Title", "Description");
        String expectedString = "Epic{id=0, title='Title', description.length=11, status=NEW, subtasksIds=[]}";

        assertEquals(expectedString, epic.toString());

        epic.setDescription(null);
        expectedString = "Epic{id=0, title='Title', description=null, status=NEW, subtasksIds=[]}";

        assertEquals(expectedString, epic.toString());
    }
}