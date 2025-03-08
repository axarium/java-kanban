package service;

import model.Task;
import model.TaskStatus;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

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
        Task firstTask = new Task("Title", "Description", TaskStatus.NEW);
        firstTask.setId(1);
        Task secondTask = new Task("NewTitle", "NewDescription", TaskStatus.IN_PROGRESS);
        secondTask.setId(1);
        inMemoryHistoryManager.add(firstTask);
        inMemoryHistoryManager.add(secondTask);
        Task taskInHistory = inMemoryHistoryManager.getHistory().getFirst();

        assertEquals(1, inMemoryHistoryManager.getHistory().size());
        assertEquals(1, taskInHistory.getId());
        assertEquals("NewTitle", taskInHistory.getTitle());
        assertEquals("NewDescription", taskInHistory.getDescription());
        assertEquals(TaskStatus.IN_PROGRESS, taskInHistory.getStatus());
    }

    @Test
    void removeTasksFromHistory() {
        Task firstTask = new Task("Title", "Description", TaskStatus.NEW);
        Task secondTask = new Task("Title", "Description", TaskStatus.NEW);
        Task thirdTask = new Task("Title", "Description", TaskStatus.NEW);
        Task fourthTask = new Task("Title", "Description", TaskStatus.NEW);
        firstTask.setId(1);
        secondTask.setId(2);
        thirdTask.setId(3);
        fourthTask.setId(4);
        inMemoryHistoryManager.add(firstTask);
        inMemoryHistoryManager.add(secondTask);
        inMemoryHistoryManager.add(thirdTask);
        inMemoryHistoryManager.add(fourthTask);
        inMemoryHistoryManager.remove(3);
        List<Task> tasksInHistory = inMemoryHistoryManager.getHistory();

        assertEquals(3, tasksInHistory.size());
        assertEquals(firstTask, tasksInHistory.getFirst());
        assertEquals(secondTask, tasksInHistory.get(1));
        assertEquals(fourthTask, tasksInHistory.get(2));

        inMemoryHistoryManager.remove(4);
        tasksInHistory = inMemoryHistoryManager.getHistory();

        assertEquals(2, tasksInHistory.size());
        assertEquals(firstTask, tasksInHistory.getFirst());
        assertEquals(secondTask, tasksInHistory.get(1));

        inMemoryHistoryManager.remove(1);
        tasksInHistory = inMemoryHistoryManager.getHistory();

        assertEquals(1, tasksInHistory.size());
        assertEquals(secondTask, tasksInHistory.getFirst());

        inMemoryHistoryManager.remove(2);
        tasksInHistory = inMemoryHistoryManager.getHistory();

        assertTrue(tasksInHistory.isEmpty());
    }
}