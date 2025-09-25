package manager;

import org.junit.jupiter.api.BeforeEach;
import tasks.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public abstract class AbstractTaskManagerTest<T extends TaskManager> {
    T taskManager;

    protected abstract T createTaskManager();

    @BeforeEach
    void setUp() {
        taskManager = createTaskManager();
    }

    void createTaskTest() {
        Task task = new Task("Task", "Description Task");
        int taskId = taskManager.createTask(task);

        assertNotNull(taskManager.getTaskById(taskId));
        assertEquals("Task", taskManager.getTaskById(taskId).getTitle());
        assertEquals("Description Task", taskManager.getTaskById(taskId).getDescription());
        assertEquals(TaskStatus.NEW, taskManager.getTaskById(taskId).getStatus());
        assertEquals(TaskType.TASK, taskManager.getTaskById(taskId).getType());
    }

    void createEpicTest() {
        Epic epic = new Epic("Epic", "Description Epic");
        int epicId = taskManager.createEpic(epic);

        assertNotNull(taskManager.getEpicById(epicId));
        assertEquals("Epic", taskManager.getEpicById(epicId).getTitle());
        assertEquals("Description Epic", taskManager.getEpicById(epicId).getDescription());
        assertEquals(TaskStatus.NEW, taskManager.getEpicById(epicId).getStatus());
        assertEquals(TaskType.EPIC, taskManager.getEpicById(epicId).getType());
    }

    void createSubtaskTest() {
        Epic epic = new Epic("Epic", "Description Epic");
        int epicId = taskManager.createEpic(epic);

        Subtask subtask = new Subtask("Subtask", "Description Subtask", epicId);
        int subtaskId = taskManager.createSubtask(subtask);

        assertNotNull(taskManager.getSubtaskById(subtaskId));
        assertEquals("Subtask", taskManager.getSubtaskById(subtaskId).getTitle());
        assertEquals(epicId, taskManager.getSubtaskById(subtaskId).getEpicId());
        assertEquals("Description Subtask", taskManager.getSubtaskById(subtaskId).getDescription());
        assertEquals(TaskStatus.NEW, taskManager.getSubtaskById(subtaskId).getStatus());
        assertEquals(TaskType.SUBTASK, taskManager.getSubtaskById(subtaskId).getType());
    }

    void getAllTasksTest() {
        Task task1 = new Task("Task 1", "Description 1");
        Task task2 = new Task("Task 2", "Description 2");

        taskManager.createTask(task1);
        taskManager.createTask(task2);

        List<Task> tasks = taskManager.getAllTasks();
        assertEquals(2, tasks.size());
    }

    void updateTaskTest() {
        Task task = new Task("Original", "Description original");
        int taskId = taskManager.createTask(task);

        Task updatedTask = task.withId(taskId).withTitle("Updated").withStatus(TaskStatus.IN_PROGRESS);
        assertTrue(taskManager.updateTask(updatedTask));

        Task taskById = taskManager.getTaskById(taskId);
        assertEquals("Updated", taskById.getTitle());
        assertEquals(TaskStatus.IN_PROGRESS, taskById.getStatus());
    }

    void deleteTaskTest() {
        Task task = new Task("Task", "Description");
        int taskId = taskManager.createTask(task);

        assertTrue(taskManager.deleteTaskById(taskId));
        assertNull(taskManager.getTaskById(taskId));
    }

    void getAllSubtasksFromEpicTest() {
        Epic epic = new Epic("Epic", "Description");
        int epicId = taskManager.createEpic(epic);

        Subtask subtask1 = new Subtask("Sub 1", "Description 1", epicId);
        Subtask subtask2 = new Subtask("Sub 2", "Description 2", epicId);

        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        List<Subtask> subtasks = taskManager.getAllSubtasksFromEpic(epicId);
        assertEquals(2, subtasks.size());
    }


}
