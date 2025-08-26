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
        Task firstTask = new Task("Task original", "Description original");
        firstTask.setId(15);
        Task secondTask = new Task("Duplicate Task", "Duplicate description");
        secondTask.setId(15);
        assertEquals(firstTask, secondTask, "Задачи с одинаковым id должны быть равны, независимо от других полей");
        assertEquals(firstTask.hashCode(), secondTask.hashCode(), "Равные объекты должны иметь равные хэш-коды");
    }

    @Test
    void comparisonShouldReturnFalseWhenComparingId() {
        Task firstTask = new Task("Task original", "Description original");
        firstTask.setId(15);
        Task secondTask = new Task("Task original", "Description original");
        secondTask.setId(10);
        assertNotEquals(firstTask, secondTask, "Задачи с разными id не должны быть равны");
        assertNotEquals(firstTask.hashCode(), secondTask.hashCode(), "Задачи с разными id должны иметь разные хэш-коды");
    }

    @Test
    void addNewTask() {
        Task task = new Task("Test addNewTask", "Test addNewTask description");
        task.setStatus(NEW);
        final int taskId = taskManager.createTask(task);
        final Task savedTask = taskManager.getTask(taskId);
        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");
        final List<Task> tasks = taskManager.getAllTasks();
        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    void add() {
        Task task = new Task("Test addNewTask", "Test addNewTask description");
        historyManager.add(task);
        final List<Task> history = historyManager.getHistory();
        assertNotNull(history, "После добавления задачи, история не должна быть пустой.");
        assertEquals(1, history.size(), "После добавления задачи, история не должна быть пустой.");
    }


}