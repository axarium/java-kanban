package service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {

    @Test
    void getNotNullDefaultTaskManager() {
        assertNotNull(Managers.getDefault());
    }

    @Test
    void getNotNullDefaultHistoryManager() {
        assertNotNull(Managers.getDefaultHistory());
    }
}