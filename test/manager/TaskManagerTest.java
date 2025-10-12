package manager;

import org.junit.jupiter.api.BeforeEach;
import tasks.*;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public abstract class TaskManagerTest<T extends TaskManager> {
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

    void getAllSubtasksByEpicIdTest() {
        Epic epic = new Epic("Epic", "Description");
        int epicId = taskManager.createEpic(epic);
        Subtask subtask1 = new Subtask("Sub 1", "Description 1", epicId);
        Subtask subtask2 = new Subtask("Sub 2", "Description 2", epicId);
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        List<Subtask> subtasks = taskManager.getAllSubtasksByEpicId(epicId);
        assertEquals(2, subtasks.size());
    }

    void subtaskMustBeRelatedToEpic() {
        Epic epic = new Epic("Epic", "Description");
        int epicId = taskManager.createEpic(epic);
        Subtask subtask1 = new Subtask("Subtask", "Description", epicId);
        Subtask subtask2 = new Subtask("Subtask", "Description", epicId);
        int subtaskId1 = taskManager.createSubtask(subtask1);
        int subtaskId2 = taskManager.createSubtask(subtask2);
        Subtask createdSubtask1 = taskManager.getSubtaskById(subtaskId1);
        Subtask createdSubtask2 = taskManager.getSubtaskById(subtaskId2);
        assertNotNull(createdSubtask1, "Подзадача должна быть создана");
        assertNotNull(createdSubtask2, "Подзадача должна быть создана");
        assertEquals(createdSubtask1.getEpicId(), epicId,
                "ID Epic в подзадаче должен совпадать с собственным ID Epic");
        assertEquals(createdSubtask2.getEpicId(), epicId,
                "ID Epic в подзадаче должен совпадать с собственным ID Epic");
        assertTrue(epicId > 0, "ID Epic должен быть положительным числом");
    }

    void epicMustContainLinksToItsSubtasks() {
        Epic epic = new Epic("Epic", "Description");
        int epicId = taskManager.createEpic(epic);
        Subtask subtask1 = new Subtask("Subtask 1", "Description", epicId);
        Subtask subtask2 = new Subtask("Subtask 2", "Description", epicId);
        int subtaskId1 = taskManager.createSubtask(subtask1);
        int subtaskId2 = taskManager.createSubtask(subtask2);
        Epic updatedEpic = taskManager.getEpicById(epicId);
        List<Integer> subtaskIds = updatedEpic.getSubtasksAllIds();
        assertEquals(2, subtaskIds.size(), "Epic должен содержать 2 подзадачи");
        assertTrue(subtaskIds.contains(subtaskId1), "Epic должен содержать ссылку на подзадачу 1");
        assertTrue(subtaskIds.contains(subtaskId2), "Epic должен содержать ссылку на подзадачу 2");
    }

    void whenDeletingAnEpicAllSubtasksMustBeDeleted() {
        Epic epic = new Epic("Epic", "Description");
        int epicId = taskManager.createEpic(epic);
        Subtask subtask1 = new Subtask("Subtask 1", "Description", epicId);
        Subtask subtask2 = new Subtask("Subtask 2", "Description", epicId);
        int subtaskId1 = taskManager.createSubtask(subtask1);
        int subtaskId2 = taskManager.createSubtask(subtask2);
        assertNotNull(taskManager.getSubtaskById(subtaskId1));
        assertNotNull(taskManager.getSubtaskById(subtaskId2));
        taskManager.deleteEpicById(epicId);
        assertNull(taskManager.getEpicById(epicId), "Epic должен быть удален");
        assertNull(taskManager.getSubtaskById(subtaskId1), "Подзадача 1 должна быть удалена");
        assertNull(taskManager.getSubtaskById(subtaskId2), "Подзадача 2 должна быть удалена");
    }

    void tasksShouldNotOverlapInTime() {
        LocalDateTime dateTime = LocalDateTime.of(2000, 12, 30, 15, 0);
        Duration oneHour = Duration.ofHours(1);
        Task firstTask = new Task("First Task", "Description")
                .withStartTime(dateTime)
                .withDuration(oneHour.toMinutes());
        taskManager.createTask(firstTask);
        Task overlappingTask = new Task("Overlapping Task", "Description")
                .withStartTime(dateTime)
                .withDuration(oneHour.toMinutes());
        assertThrows(TimeOverlapException.class, () -> taskManager.createTask(overlappingTask),
                "Создание задачи с пересекающимся временем должно бросать TimeOverlapException");
        Task partialOverlapTask = new Task("Partial overlap", "Description")
                .withStartTime(dateTime.plusMinutes(30))
                .withDuration(oneHour.toMinutes());
        assertThrows(TimeOverlapException.class, () ->
                        taskManager.createTask(partialOverlapTask)
                , "Создание задачи с частично пересекающимся временем должно бросать TimeOverlapException");
        Task notOverlappingTask = new Task("Not overlapping", "Description")
                .withStartTime(dateTime.plusHours(2))
                .withDuration(oneHour.toMinutes());
        int taskId = taskManager.createTask(notOverlappingTask);
        assertNotNull(taskManager.getTaskById(taskId),
                "Задача без пересечения по времени должна создаваться успешно");

    }

    void shouldThrowAnExceptionIfFileIsNotFound() {
        File nonExistentFile = new File("non_existent_file.txt");
        assertThrows(ManagerSaveException.class, () -> {
            FileBackedTaskManager.loadFromFile(nonExistentFile);
        }, "Должно выбрасывать исключение при работе с несуществующим файлом");
    }

    void doNotThrowAnExceptionIfFileExists() {
        File existentFile = new File("existent_file.txt");
        assertDoesNotThrow(() -> {
            FileBackedTaskManager manager = new FileBackedTaskManager(existentFile);
            Task task = new Task("Valid Task", "Description");
            manager.createTask(task);
        }, "Не должно быть исключений при работе с существующим файлом");
    }

    void shouldNotThrowAnExceptionForNonExistentFile() {
        File nonExistentFile = new File("non_existent_file.txt");
        assertDoesNotThrow(() ->
                        new FileBackedTaskManager(nonExistentFile),
                "Не должно бросать исключение для несуществующего файла");
    }

    void anExceptionShouldBeThrownWhenWorkingWithAnInvalidDirectory() {
        FileBackedTaskManager manager = new FileBackedTaskManager(
                new File("/invalid/path/some_path/file.txt"));
        assertThrows(ManagerSaveException.class, () -> {
            manager.createTask(new Task("Test", "Description"));
        }, "Должно бросаться исключение при работе с неправильной директорией");
    }
}
