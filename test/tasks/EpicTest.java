package tasks;

import manager.HistoryManager;
import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
        Epic firstEpic = new Epic("Epic original", "Description original").withId(15);
        Epic secondEpic = new Epic("Duplicate Epic", "Duplicate description").withId(15);
        assertEquals(firstEpic, secondEpic, "Эпики с одинаковым id должны быть равны, " +
                "независимо от других полей");
        assertEquals(firstEpic.hashCode(), secondEpic.hashCode(), "Равные объекты должны иметь равные хэш-коды");
    }

    @Test
    void comparisonShouldReturnFalseWhenComparingId() {
        Epic firstEpic = new Epic("Epic original", "Description original").withId(15);
        Epic secondEpic = new Epic("Epic original", "Description original").withId(20);
        assertNotEquals(firstEpic, secondEpic, "Эпики с разными id не должны быть равны");
        assertNotEquals(firstEpic.hashCode(), secondEpic.hashCode(), "Эпики с разными id " +
                "должны иметь разные хэш-коды");
    }

    @Test
    void comparisonShouldReturnTrueWhenComparingIdOfADifferentType() {
        Task firstTask = new Epic("Task original", "Description original").withId(15);
        Task secondTask = new Epic("Duplicate Task", "Duplicate description").withId(15);
        assertEquals(firstTask, secondTask, "Эпики с одинаковым id должны быть равны");
        assertEquals(firstTask.hashCode(), secondTask.hashCode(), "Равные объекты должны иметь равные хэш-коды");
    }

    @Test
    void epicCannotBeAddedInSubtask() {
        Epic epic = new Epic("Test Epic", "Description");
        taskManager.createEpic(epic);
        Subtask wrongSubtask = new Subtask("Wrong Subtask", "Fail test", epic.getId()).withId(epic.getId());
        int result = taskManager.createSubtask(wrongSubtask);
        assertEquals(0, result, "Epic не должен быть subtask");
        assertNull(taskManager.getSubtask(epic.getId()), "Subtask не должна создаваться");
        assertEquals(0, epic.getSubtasksId().size(), "У Epic не должно быть subtasks");
    }

    @Test
    void shouldRemoveSubtaskIdFromEpicSubtaskIsDeleted() {
        Epic epic = new Epic("Test Epic", "Description");
        int epicId = taskManager.createEpic(epic);
        Subtask subtask1 = new Subtask("Subtask 1", "Description", epicId);
        Subtask subtask2 = new Subtask("Subtask 2", "Description", epicId);
        int subtaskId1 = taskManager.createSubtask(subtask1);
        int subtaskId2 = taskManager.createSubtask(subtask2);
        Epic epicWithSubtasks = taskManager.getEpicById(epicId);
        List<Integer> subtaskIds = epicWithSubtasks.getSubtasksId();
        assertEquals(2, subtaskIds.size(), "Эпик должен содержать 2 подзадачи");
        assertTrue(subtaskIds.contains(subtaskId1), "Эпик должен содержать подзадачу 1");
        assertTrue(subtaskIds.contains(subtaskId2), "Эпик должен содержать подзадачу 2");
        taskManager.deleteSubtaskById(subtaskId1);
        Epic epicAfterDeletion = taskManager.getEpicById(epicId);
        List<Integer> updatedSubtaskIds = epicAfterDeletion.getSubtasksId();
        assertEquals(1, updatedSubtaskIds.size(), "Эпик должен содержать 1 подзадачу после удаления");
        assertFalse(updatedSubtaskIds.contains(subtaskId1), "Эпик НЕ должен содержать ID удаленной подзадачи");
        assertTrue(updatedSubtaskIds.contains(subtaskId2), "Эпик должен содержать ID оставшейся подзадачи");
        assertTrue(subtaskId1 != 1, "ID подзадачи не должно быть равным своему прежнему ID");
        assertNull(taskManager.getSubtaskById(subtaskId1), "Подзадача должна быть удалена из менеджера");
    }

    @Test
    void getSubtasksIdShouldReturnCopy() {
        List<Integer> initialSubtasks = Arrays.asList(1, 2, 3);
        Epic epic = new Epic(1, "Test", "Description", TaskStatus.NEW, initialSubtasks);
        List<Integer> subtasks = epic.getSubtasksId();
        assertNotSame(initialSubtasks, subtasks, "Должна вернуться копия");
        assertEquals(initialSubtasks, subtasks, "Содержимое должно совпадать");
        subtasks.add(4);
        subtasks.remove(0);
        List<Integer> originalSubtasks = epic.getSubtasksId();
        assertEquals(3, originalSubtasks.size(), "Оригинальный список не должен измениться");
        assertEquals(Arrays.asList(1, 2, 3), originalSubtasks, "Оригинальное содержимое не должно измениться");
    }

    @Test
    void shouldRemoveSubtaskById() {
        List<Integer> initialSubtasks = new ArrayList<>(Arrays.asList(101, 102, 103));
        Epic epic = new Epic(1, "Test Epic", "Description", TaskStatus.NEW, initialSubtasks);
        boolean result = epic.removeSubtaskId(102);
        assertTrue(result, "Метод должен вернуть true при успешном удалении");
        List<Integer> othersSubtasks = epic.getSubtasksId();
        assertEquals(2, othersSubtasks.size(), "Должно остаться 2 подзадачи");
        assertTrue(othersSubtasks.contains(101), "Должна остаться подзадача 101");
        assertTrue(othersSubtasks.contains(103), "Должна остаться подзадача 103");
        assertFalse(othersSubtasks.contains(102), "Подзадача 102 должна быть удалена");
    }

    @Test
    void epicFieldsShouldNotBeMutable() {
        List<Integer> subtasks = List.of(101, 102);
        Epic epic = new Epic(1, "Epic Title", "Epic Description", TaskStatus.NEW, subtasks);
        Epic withNewSubtasks = epic.withSubtasks(List.of(201, 202));
        Epic withAddedSubtask = epic.addSubtaskId(103);
        assertEquals(List.of(101, 102), epic.getSubtasksId());
        assertEquals("Epic Title", epic.getTitle());
        assertEquals(1, epic.getId());
        assertNotSame(epic, withNewSubtasks);
        assertNotSame(epic, withAddedSubtask);
    }
}























