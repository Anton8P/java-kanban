package tasks;

import manager.HistoryManager;
import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {

    TaskManager taskManager;
    HistoryManager historyManager;

    @BeforeEach
    void setUp() {
        taskManager = Managers.getDefault();
        historyManager = Managers.getDefaultHistory();
    }

    @Test
    void comparisonShouldReturnTrueWhenComparingId() {
        Epic firstEpic = new Epic("Epic original", "Description original");
        firstEpic.setId(15);
        Epic secondEpic = new Epic("Duplicate Epic", "Duplicate description");
        secondEpic.setId(15);
        assertEquals(firstEpic, secondEpic, "Эпики с одинаковым id должны быть равны, " +
                "независимо от других полей");
        assertEquals(firstEpic.hashCode(), secondEpic.hashCode(), "Равные объекты должны иметь равные хэш-коды");
    }

    @Test
    void comparisonShouldReturnFalseWhenComparingId() {
        Epic firstEpic = new Epic("Epic original", "Description original");
        firstEpic.setId(15);
        Epic secondEpic = new Epic("Epic original", "Description original");
        secondEpic.setId(20);
        assertNotEquals(firstEpic, secondEpic, "Эпики с разными id не должны быть равны");
        assertNotEquals(firstEpic.hashCode(), secondEpic.hashCode(), "Эпики с разными id " +
                "должны иметь разные хэш-коды");
    }

    @Test
    void comparisonShouldReturnTrueWhenComparingIdOfADifferentType() {
        Task firstTask = new Epic("Task original", "Description original");
        firstTask.setId(15);
        Task secondTask = new Epic("Duplicate Task", "Duplicate description");
        secondTask.setId(15);
        assertEquals(firstTask, secondTask, "Эпики с одинаковым id должны быть равны");
        assertEquals(firstTask.hashCode(), secondTask.hashCode(), "Равные объекты должны иметь равные хэш-коды");
    }

    @Test
    void epicCannotBeAddedInSubtask() {
        Epic epic = new Epic("Test Epic", "Description");
        taskManager.createEpic(epic);
        Subtask wrongSubtask = new Subtask("Wrong Subtask", "Fail test", epic.getId());
        wrongSubtask.setId(epic.getId());
        int result = taskManager.createSubtask(wrongSubtask);
        assertEquals(0, result, "Epic не должен быть subtask");
        assertNull(taskManager.getSubtask(epic.getId()), "Subtask не должна создаваться");
        assertEquals(0, epic.getSubtasksId().size(), "У Epic не должно быть subtasks");
    }

}