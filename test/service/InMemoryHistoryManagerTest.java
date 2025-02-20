package service;

import model.Task;
import model.TaskStatus;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    private InMemoryHistoryManager inMemoryHistoryManager;

    @BeforeEach
    void newMemoryHistoryManager() {
        inMemoryHistoryManager = new InMemoryHistoryManager();
    }

    @Test
    void createInMemoryHistoryManager() {
        assertNotNull(inMemoryHistoryManager.getHistory());
        assertTrue(inMemoryHistoryManager.getHistory().isEmpty());
    }

    @Test
    void addTaskInHistory() {
        Task task = new Task("Title", "Description", TaskStatus.NEW);
        inMemoryHistoryManager.add(task);

        assertEquals(1, inMemoryHistoryManager.getHistory().size());
    }

    @Test
    void addDuplicateInHistory() {
        Task task = new Task("Title", "Description", TaskStatus.NEW);
        inMemoryHistoryManager.add(task);
        inMemoryHistoryManager.add(task);

        assertEquals(2, inMemoryHistoryManager.getHistory().size());
    }

    @Test
    void addedCopyInHistory() {
        Task task = new Task("Title", "Description", TaskStatus.NEW);
        task.setId(1);
        inMemoryHistoryManager.add(task);
        Task taskInHistory = inMemoryHistoryManager.getHistory().getFirst();

        task.setId(2);
        task.setTitle("NewTitle");
        task.setDescription("NewDescription");
        task.setStatus(TaskStatus.IN_PROGRESS);

        assertEquals(1, taskInHistory.getId());
        assertEquals("Title", taskInHistory.getTitle());
        assertEquals("Description", taskInHistory.getDescription());
        assertEquals(TaskStatus.NEW, taskInHistory.getStatus());
    }
}