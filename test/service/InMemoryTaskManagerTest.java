package service;

import org.junit.jupiter.api.BeforeEach;

class InMemoryTaskManagerTest extends TaskManagerTest {

    @BeforeEach
    void createNewInMemoryTaskManager() {
        taskManager = new InMemoryTaskManager();
    }
}