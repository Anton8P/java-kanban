package tasks;

import manager.HistoryManager;
import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;


class SubtaskTest {

    TaskManager taskManager;
    HistoryManager historyManager;

    @BeforeEach
    void setUp() {
        taskManager = Managers.getDefault();
        historyManager = Managers.getDefaultHistory();
    }

    @Test
    void comparisonShouldReturnTrueWhenComparingId() {
        Epic epic = new Epic("NewEpic", "NewEpic description", taskManager.getGeneratedId());
        taskManager.createEpic(epic);
        Subtask firstSubtask = new Subtask("Subtask original", "Description original", 150, epic.getId());
        Subtask secondSubtask = new Subtask("Duplicate Subtask", "Duplicate description", 150, epic.getId());
        assertEquals(firstSubtask, secondSubtask, "Subtask с одинаковым id должны быть равны, " +
                "независимо от других полей");
        assertEquals(firstSubtask.hashCode(), secondSubtask.hashCode(), "Равные объекты должны иметь равные хэш-коды");
    }

    @Test
    void comparisonShouldReturnFalseWhenComparingId() {
        Epic epic = new Epic("NewEpic", "NewEpic description", taskManager.getGeneratedId());
        taskManager.createEpic(epic);
        Subtask firstSubtask = new Subtask("Subtask original", "Description original", 150, epic.getId());
        Subtask secondSubtask = new Subtask("Duplicate Subtask", "Duplicate description", 250, epic.getId());
        assertNotEquals(firstSubtask, secondSubtask, "Subtask с разными id не должны быть равны");
        assertNotEquals(firstSubtask.hashCode(), secondSubtask.hashCode(), "Subtask с разными id " +
                "должны иметь разные хэш-коды");
    }

    @Test
    void methodReturnsSubtaskById() {
        Epic epic = new Epic("Test addNewEpic", "Test addNewEpic description", taskManager.getGeneratedId());
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask("Test addNewSubtask", "Test addNewSubtask description", taskManager.getGeneratedId(), epic.getId());
        int subtaskId = taskManager.createSubtask(subtask);
        Subtask savedSubtask = taskManager.getSubtaskById(subtaskId);
        assertNotNull(savedSubtask, "Возвращаемый объект не может быть Null");
    }

    @Test
    void subtaskCannotBeAddedToEpic() {
        int subtaskId = taskManager.getGeneratedId();
        Subtask wrongSubtask = new Subtask("Wrong Subtask", "Fail test", subtaskId, subtaskId);
        int result = taskManager.createSubtask(wrongSubtask);
        assertEquals(0, result, "Subtask не должна быть Epic");
        assertNull(taskManager.getSubtask(subtaskId), "Subtask не должна создаваться");
    }

}