package tasks;

import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

public class EpicAndSubtasksStatusTest {

    TaskManager taskManager;
    Epic epic;

    @BeforeEach
    void setUp() {
        taskManager = Managers.getDefault();
        epic = new Epic("Epic", "Epic desc");
        taskManager.createEpic(epic);
    }

    @Test
    void allSubtasksWithStatusNew() {
        int epicId = taskManager.createEpic(epic);
        Subtask subtask1 = new Subtask("Subtask 1", "Description 1",
                epicId).withStatus(TaskStatus.NEW);
        Subtask subtask2 = new Subtask("Subtask 2", "Description 2",
                epicId).withStatus(TaskStatus.NEW);
        Subtask subtask3 = new Subtask("Subtask 3", "Description 3",
                epicId).withStatus(TaskStatus.NEW);
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        taskManager.createSubtask(subtask3);
        Epic updatedEpic = taskManager.getEpicById(epicId);
        assertNotSame(subtask1, subtask2);
        assertNotSame(subtask1, subtask3);
        assertNotSame(subtask2, subtask3);
        assertEquals(TaskStatus.NEW, updatedEpic.getStatus(),
                "Epic должен иметь статус NEW, когда у всех подзадач статус NEW");
    }

    @Test
    void allSubtasksWithStatusNewAndDone() {
        int epicId = taskManager.createEpic(epic);
        Subtask subtask1 = new Subtask("Subtask 1", "Description 1",
                epicId).withStatus(TaskStatus.NEW);
        Subtask subtask2 = new Subtask("Subtask 2", "Description 2",
                epicId).withStatus(TaskStatus.DONE);
        Subtask subtask3 = new Subtask("Subtask 3", "Description 3",
                epicId).withStatus(TaskStatus.NEW);
        Subtask subtask4 = new Subtask("Subtask 3", "Description 3",
                epicId).withStatus(TaskStatus.DONE);
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        taskManager.createSubtask(subtask3);
        taskManager.createSubtask(subtask4);
        Epic updatedEpic = taskManager.getEpicById(epicId);
        assertNotSame(subtask1, subtask2);
        assertNotSame(subtask1, subtask3);
        assertNotSame(subtask2, subtask3);
        assertNotSame(subtask2, subtask4);
        assertNotSame(subtask4, subtask1);
        assertEquals(TaskStatus.IN_PROGRESS, updatedEpic.getStatus(),
                "Epic должен иметь статус IN_PROGRESS, если есть подзадачи со статусами NEW и DONE");
    }

    @Test
    void allSubtasksWithStatusDone() {
        int epicId = taskManager.createEpic(epic);
        Subtask subtask1 = new Subtask("Subtask 1", "Description 1",
                epicId).withStatus(TaskStatus.DONE);
        Subtask subtask2 = new Subtask("Subtask 2", "Description 2",
                epicId).withStatus(TaskStatus.DONE);
        Subtask subtask3 = new Subtask("Subtask 3", "Description 3",
                epicId).withStatus(TaskStatus.DONE);
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        taskManager.createSubtask(subtask3);
        Epic updatedEpic = taskManager.getEpicById(epicId);
        assertNotSame(subtask1, subtask2);
        assertNotSame(subtask1, subtask3);
        assertNotSame(subtask2, subtask3);
        assertEquals(TaskStatus.DONE, updatedEpic.getStatus(),
                "Epic должен иметь статус DONE, когда у всех подзадач статус DONE");
    }

    @Test
    void allSubtasksWithStatusInProgress() {
        int epicId = taskManager.createEpic(epic);
        Subtask subtask1 = new Subtask("Subtask 1", "Description 1",
                epicId).withStatus(TaskStatus.IN_PROGRESS);
        Subtask subtask2 = new Subtask("Subtask 2", "Description 2",
                epicId).withStatus(TaskStatus.IN_PROGRESS);
        Subtask subtask3 = new Subtask("Subtask 3", "Description 3",
                epicId).withStatus(TaskStatus.IN_PROGRESS);
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        taskManager.createSubtask(subtask3);
        Epic updatedEpic = taskManager.getEpicById(epicId);
        assertNotSame(subtask1, subtask2);
        assertNotSame(subtask1, subtask3);
        assertNotSame(subtask2, subtask3);
        assertEquals(TaskStatus.IN_PROGRESS, updatedEpic.getStatus(),
                "Epic должен иметь статус IN_PROGRESS, когда у всех подзадач статус IN_PROGRESS");
    }
}
