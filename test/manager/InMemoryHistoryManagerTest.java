package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    TaskManager taskManager;
    HistoryManager historyManager;

    @BeforeEach
    void setUp() {
        taskManager = Managers.getDefault();
        historyManager = Managers.getDefaultHistory();
    }

    @Test
    void shouldPreservePreviousStateOfObject() {
        Task initialTask = new Task(155, "Initial Task", "Preview description"
                , TaskStatus.IN_PROGRESS);
        historyManager.add(initialTask);
        Task modifiedTask = initialTask
                .withTitle("Changed title")
                .withDescription("Modified description")
                .withStatus(TaskStatus.DONE);
        List<Task> history = historyManager.getHistory();
        Task fromHistory = history.get(0);
        assertNotNull(fromHistory);
        assertNotEquals(modifiedTask.getTitle(), fromHistory.getTitle());
        assertEquals("Initial Task", fromHistory.getTitle());
        assertEquals("Preview description", fromHistory.getDescription());
        assertEquals(TaskStatus.IN_PROGRESS, fromHistory.getStatus());
        assertEquals("Initial Task", initialTask.getTitle());
        assertEquals("Preview description", initialTask.getDescription());
        assertEquals(TaskStatus.IN_PROGRESS, initialTask.getStatus());
        assertEquals("Changed title", modifiedTask.getTitle());
        assertEquals("Modified description", modifiedTask.getDescription());
        assertEquals(TaskStatus.DONE, modifiedTask.getStatus());
    }

    @Test
    void shouldCheckLinkedListWorkCorrectly() {
        Task task1 = new Task("Task 1", "Description 1");
        Task task2 = new Task("Task 2", "Description 2");
        Task task3 = new Task("Task 3", "Description 3");
        Task task4 = new Task("Task 4", "Description 4");

        int id1 = taskManager.createTask(task1);
        int id2 = taskManager.createTask(task2);
        int id3 = taskManager.createTask(task3);
        int id4 = taskManager.createTask(task4);

        historyManager.add(taskManager.getTaskById(id1));
        historyManager.add(taskManager.getTaskById(id2));
        historyManager.add(taskManager.getTaskById(id3));
        historyManager.add(taskManager.getTaskById(id4));

        List<Task> history = historyManager.getHistory();
        assertEquals(4, history.size(), "История должна содержать 4 задачи");
        assertEquals(id1, history.get(0).getId(), "Задача 1 не совпадает");
        assertEquals(id2, history.get(1).getId(), "Задача 2 не совпадает");
        assertEquals(id3, history.get(2).getId(), "Задача 3 не совпадает");
        assertEquals(id4, history.get(3).getId(), "Задача 4 не совпадает");

        historyManager.add(taskManager.getTaskById(id2));

        List<Task> updatedHistory = historyManager.getHistory();
        assertEquals(4, updatedHistory.size(), "Размер истории не должен измениться");
        assertEquals(id1, updatedHistory.get(0).getId(), "Задача 1 должна остаться первой");
        assertEquals(id3, updatedHistory.get(1).getId(), "Задача 3 должна стать второй");
        assertEquals(id4, updatedHistory.get(2).getId(), "Задача 4 должна стать третьей");
        assertEquals(id2, updatedHistory.get(3).getId(), "Задача 2 должна переместиться в конец");
    }

    @Test
    void taskShouldBeDeletedFromHistory() {
        Task task1 = new Task("Task 1", "Description 1");
        Task task2 = new Task("Task 2", "Description 2");
        Task task3 = new Task("Task 3", "Description 3");

        int taskId1 = taskManager.createTask(task1);
        int taskId2 = taskManager.createTask(task2);
        int taskId3 = taskManager.createTask(task3);

        historyManager.add(taskManager.getTaskById(taskId1));
        historyManager.add(taskManager.getTaskById(taskId2));
        historyManager.add(taskManager.getTaskById(taskId3));

        List<Task> initialHistory = historyManager.getHistory();
        assertEquals(3, initialHistory.size(), "Должно быть 3 задачи в истории");

        historyManager.remove(taskId2);

        List<Task> historyAfterRemove = historyManager.getHistory();
        assertEquals(2, historyAfterRemove.size(), "Должно остаться 2 задачи");
        assertEquals(taskId1, historyAfterRemove.get(0).getId(), "Задача 1 должна быть первой");
        assertEquals(taskId3, historyAfterRemove.get(1).getId(), "Задача 3 должна быть второй");

        historyManager.remove(taskId1);

        List<Task> historyAfterFirstRemove = historyManager.getHistory();
        assertEquals(1, historyAfterFirstRemove.size(), "Должна остаться 1 задача");
        assertEquals(taskId3, historyAfterFirstRemove.get(0).getId(), "Задача 3 должна остаться");

        historyManager.remove(taskId3);

        List<Task> emptyHistory = historyManager.getHistory();
        assertTrue(emptyHistory.isEmpty(), "История должна быть пустой");

        List<Task> finalHistory = historyManager.getHistory();
        assertTrue(finalHistory.isEmpty(), "История должна остаться пустой после удаления несуществующей задачи");
    }

    @Test
    void methodAddShouldAcceptAllTaskTypes() {
        Task task = new Task(1, "Task", "Description to task", TaskStatus.NEW);
        Epic epic = new Epic(2, "Epic", "Description to epic", TaskStatus.NEW, new ArrayList<>());
        Subtask subtask = new Subtask(3, "Subtask", "Description to subtask", TaskStatus.NEW, 2);

        historyManager.add(task);
        historyManager.add(epic);
        historyManager.add(subtask);

        List<Task> history = historyManager.getHistory();
        assertEquals(3, history.size(), "Должно быть 3 задачи в истории");

        assertInstanceOf(Task.class, history.get(0), "Первая задача должна быть Task");
        assertInstanceOf(Epic.class, history.get(1), "Вторая задача должна быть Epic");
        assertInstanceOf(Subtask.class, history.get(2), "Третья задача должна быть Subtask");

        assertEquals(1, history.get(0).getId(), "У первой задачи должен быть ID = 1");
        assertEquals(2, history.get(1).getId(), "У первой задачи должен быть ID = 2");
        assertEquals(3, history.get(2).getId(), "У первой задачи должен быть ID = 3");
    }
}