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
        task.setId(1);
        inMemoryHistoryManager.add(task);
        Task taskInHistory = inMemoryHistoryManager.getHistory().getFirst();

        assertEquals(1, inMemoryHistoryManager.getHistory().size());
        assertEquals(task, taskInHistory);

        task.setId(2);
        task.setTitle("NewTitle");
        task.setDescription("NewDescription");
        task.setStatus(TaskStatus.IN_PROGRESS);

        assertEquals(1, taskInHistory.getId());
        assertEquals("Title", taskInHistory.getTitle());
        assertEquals("Description", taskInHistory.getDescription());
        assertEquals(TaskStatus.NEW, taskInHistory.getStatus());
    }

    @Test
    void taskInHistoryCannotBeChanged() {
        Task task = new Task("Title", "Description", TaskStatus.NEW);
        task.setId(1);
        inMemoryHistoryManager.add(task);
        Task copyTaskFromHistory = inMemoryHistoryManager.getHistory().getFirst();
        copyTaskFromHistory.setId(2);
        copyTaskFromHistory.setTitle("NewTitle");
        copyTaskFromHistory.setDescription("NewDescription");
        copyTaskFromHistory.setStatus(TaskStatus.IN_PROGRESS);

        assertEquals(1, inMemoryHistoryManager.getHistory().getFirst().getId());
        assertEquals("Title", inMemoryHistoryManager.getHistory().getFirst().getTitle());
        assertEquals("Description", inMemoryHistoryManager.getHistory().getFirst().getDescription());
        assertEquals(TaskStatus.NEW, inMemoryHistoryManager.getHistory().getFirst().getStatus());
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
        Task firstTask = new Task("Title", "Description", TaskStatus.NEW);
        Task secondTask = new Task("Title", "Description", TaskStatus.NEW);
        firstTask.setId(1);
        secondTask.setId(2);
        inMemoryHistoryManager.add(firstTask);
        for (int i = 1; i < 10; i++) {
            inMemoryHistoryManager.add(secondTask);
        }

        assertEquals(firstTask, inMemoryHistoryManager.getHistory().getFirst());
        assertEquals(secondTask, inMemoryHistoryManager.getHistory().getLast());

        inMemoryHistoryManager.add(secondTask);

        assertEquals(10, inMemoryHistoryManager.getHistory().size());
        assertEquals(secondTask, inMemoryHistoryManager.getHistory().getFirst());
        assertEquals(secondTask, inMemoryHistoryManager.getHistory().getLast());
    }
}