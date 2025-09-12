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
        Epic epic = new Epic("NewEpic", "NewEpic description");
        taskManager.createEpic(epic);
        Subtask firstSubtask = new Subtask("Subtask original", "Description original", epic.getId()).withId(15);
        Subtask secondSubtask = new Subtask("Duplicate Subtask", "Duplicate description", epic.getId()).withId(15);
        assertEquals(firstSubtask, secondSubtask, "Subtask с одинаковым id должны быть равны, " +
                "независимо от других полей");
        assertEquals(firstSubtask.hashCode(), secondSubtask.hashCode(), "Равные объекты должны иметь равные хэш-коды");
    }

    @Test
    void comparisonShouldReturnFalseWhenComparingId() {
        Epic epic = new Epic("NewEpic", "NewEpic description");
        taskManager.createEpic(epic);
        Subtask firstSubtask = new Subtask("Subtask original", "Description original", epic.getId()).withId(15);
        Subtask secondSubtask = new Subtask("Duplicate Subtask", "Duplicate description", epic.getId()).withId(10);
        assertNotEquals(firstSubtask, secondSubtask, "Subtask с разными id не должны быть равны");
        assertNotEquals(firstSubtask.hashCode(), secondSubtask.hashCode(), "Subtask с разными id " +
                "должны иметь разные хэш-коды");
    }

    @Test
    void methodReturnsSubtaskById() {
        Epic epic = new Epic("Test addNewEpic", "Test addNewEpic description");
        int epicId = taskManager.createEpic(epic);
        Subtask subtask = new Subtask("Test addNewSubtask", "Test addNewSubtask description", epicId);
        int subtaskId = taskManager.createSubtask(subtask);
        Subtask savedSubtask = taskManager.getSubtaskById(subtaskId);
        assertNotNull(savedSubtask, "Возвращаемый объект не может быть Null");
    }

    @Test
    void subtaskCannotBeAddedToEpic() {
        Epic epic = new Epic("Test addNewEpic", "Test addNewEpic description");
        taskManager.createEpic(epic);
        Subtask wrongSubtask = new Subtask("Wrong Subtask", "Fail test", epic.getId());
        int subtaskId = taskManager.createSubtask(wrongSubtask);
        Subtask wrongSubtaskWrong = new Subtask("Wrong Subtask", "Fail test", subtaskId);
        int subtaskIdWrong = taskManager.createSubtask(wrongSubtaskWrong);
        assertEquals(0, subtaskIdWrong, "Subtask не должна быть Epic");
        assertNull(taskManager.getSubtask(subtaskIdWrong), "Subtask не должна создаваться");
    }

    @Test
    void subtaskFieldsShouldNotBeMutable() {
        Subtask subtask = new Subtask(1, "Subtask Title", "Subtask Description", TaskStatus.NEW, 150);
        Subtask withNewEpicId = subtask.withEpicId(200);
        Subtask withNewTitle = subtask.withTitle("New Title");
        assertEquals(150, subtask.getEpicId());
        assertEquals("Subtask Title", subtask.getTitle());
        assertEquals(1, subtask.getId());
        assertNotSame(subtask, withNewEpicId);
        assertNotSame(subtask, withNewTitle);
    }
}