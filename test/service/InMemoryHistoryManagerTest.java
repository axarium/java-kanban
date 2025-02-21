package service;

import model.Task;
import model.TaskStatus;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    private InMemoryHistoryManager inMemoryHistoryManager;

    @BeforeEach
    void createNewInMemoryHistoryManager() {
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
    void addMoreTasksThanMaxHistorySize() {
        Task task1 = new Task("Title1", "Description1", TaskStatus.NEW);
        Task task2 = new Task("Title2", "Description2", TaskStatus.IN_PROGRESS);
        task1.setId(1);
        task2.setId(2);

        inMemoryHistoryManager.add(task1);
        for (int i = 1; i < 10; i++) {
            inMemoryHistoryManager.add(task2);
        }

        assertEquals(task1, inMemoryHistoryManager.getHistory().getFirst());
        assertEquals(task2, inMemoryHistoryManager.getHistory().getLast());

        inMemoryHistoryManager.add(task2);

        assertEquals(10, inMemoryHistoryManager.getHistory().size());
        assertEquals(task2, inMemoryHistoryManager.getHistory().getFirst());
        assertEquals(task2, inMemoryHistoryManager.getHistory().getLast());
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