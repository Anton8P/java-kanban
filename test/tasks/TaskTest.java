package tasks;

import manager.HistoryManager;
import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static tasks.TaskStatus.NEW;

class TaskTest {
    TaskManager taskManager;
    HistoryManager historyManager;

    @BeforeEach
    void setUp() {
        taskManager = Managers.getDefault();
        historyManager = Managers.getDefaultHistory();
    }

    @Test
    void comparisonShouldReturnTrueWhenComparingId() {
        Task firstTask = new Task("Task original", "Description original").withId(15);
        Task secondTask = new Task("Duplicate Task", "Duplicate description").withId(15);
        assertEquals(firstTask, secondTask, "Задачи с одинаковым id должны быть равны, независимо от других полей");
        assertEquals(firstTask.hashCode(), secondTask.hashCode(), "Равные объекты должны иметь равные хэш-коды");
    }

    @Test
    void comparisonShouldReturnFalseWhenComparingId() {
        Task firstTask = new Task("Task original", "Description original").withId(15);
        Task secondTask = new Task("Task original", "Description original").withId(10);
        assertNotEquals(firstTask, secondTask, "Задачи с разными id не должны быть равны");
        assertNotEquals(firstTask.hashCode(), secondTask.hashCode(), "Задачи с разными id должны иметь разные хэш-коды");
    }

    @Test
    void addNewTask() {
        Task task = new Task("Test addNewTask", "Test addNewTask description");
        task = task.withStatus(NEW);
        int taskId = taskManager.createTask(task);
        assertTrue(taskId > 0, "Должен вернуть положительный ID");
        Task savedTask = taskManager.getTask(taskId);
        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals("Test addNewTask", savedTask.getTitle(), "Название не совпадает");
        assertEquals("Test addNewTask description", savedTask.getDescription(), "Описание не совпадает");
        assertEquals(NEW, savedTask.getStatus(), "Статус не совпадает");
        assertEquals(taskId, savedTask.getId(), "ID не совпадает");
        List<Task> tasks = taskManager.getAllTasks();
        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        Task taskFromList = tasks.get(0);
        assertEquals("Test addNewTask", taskFromList.getTitle(), "Название в списке не совпадает");
        assertEquals(taskId, taskFromList.getId(), "ID в списке не совпадает");
    }

    @Test
    void add() {
        Task task = new Task("Test addNewTask", "Test addNewTask description");
        historyManager.add(task);
        final List<Task> history = historyManager.getHistory();
        assertNotNull(history, "После добавления задачи, история не должна быть пустой.");
        assertEquals(1, history.size(), "После добавления задачи, история не должна быть пустой.");
    }

    @Test
    void taskFieldsShouldNotBeMutable() {
        Task task = new Task(1, "Original Title", "Original Description", TaskStatus.NEW);
        Task withNewTitle = task.withTitle("New Title");
        Task withNewDesc = task.withDescription("New Description");
        Task withNewStatus = task.withStatus(TaskStatus.DONE);
        Task withNewId = task.withId(150);
        assertEquals("Original Title", task.getTitle());
        assertEquals("Original Description", task.getDescription());
        assertEquals(TaskStatus.NEW, task.getStatus());
        assertEquals(1, task.getId());
        assertNotSame(task, withNewTitle);
        assertNotSame(task, withNewDesc);
        assertNotSame(task, withNewStatus);
        assertNotSame(task, withNewId);
    }

}