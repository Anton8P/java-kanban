package manager;

import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.*;


class FileBackedTaskManagerTest {

    @Test
    void shouldSaveAndLoadEmptyFile() throws IOException {
        File testFile = File.createTempFile("test_temp", ".txt");
        testFile.deleteOnExit();
        FileBackedTaskManager manager = new FileBackedTaskManager(testFile);

        assertTrue(testFile.exists());
        assertTrue(manager.getAllTasks().isEmpty());
        assertTrue(manager.getAllEpics().isEmpty());
        assertTrue(manager.getAllSubtasks().isEmpty());

        manager.save();
        String content = Files.readString(testFile.toPath());
        assertTrue(content.contains("id,type,name,status,description,epic"));

        FileBackedTaskManager loadManager = FileBackedTaskManager.loadFromFile(testFile);
        assertTrue(loadManager.getAllTasks().isEmpty());
        assertTrue(loadManager.getAllEpics().isEmpty());
        assertTrue(loadManager.getAllSubtasks().isEmpty());
    }

    @Test
    void shouldSaveSeveralTasks() throws IOException {
        File testFile = File.createTempFile("test_save", ".txt");
        testFile.deleteOnExit();

        FileBackedTaskManager manager = new FileBackedTaskManager(testFile);

        Task task1 = new Task("Task 1", "Task 1 Description");
        Task task2 = new Task("Task 2", "Task 2 Description");
        Epic epic1 = new Epic("Epic", "Epic Description");
        int epicId = manager.createEpic(epic1);
        Subtask subtask = new Subtask("Subtask", "Subtask Description", epicId);
        manager.createTask(task1);
        manager.createTask(task2);
        manager.createSubtask(subtask);

        manager.save();

        String content = Files.readString(testFile.toPath());
        assertTrue(content.contains("Task 1"));
        assertTrue(content.contains("Task 2"));
        assertTrue(content.contains("Epic"));
        assertTrue(content.contains("Subtask"));
        assertTrue(content.contains("TASK"));
        assertTrue(content.contains("EPIC"));
        assertTrue(content.contains("SUBTASK"));
    }

    @Test
    void shouldLoadSeveralTasks() throws IOException {
        File testFile = File.createTempFile("test_load", ".txt");
        testFile.deleteOnExit();

        FileBackedTaskManager saveManager = new FileBackedTaskManager(testFile);

        Task task1 = new Task("Task 1", "Task 1 Description");
        Task task2 = new Task("Task 2", "Task 2 Description");

        Epic epic = new Epic("Epic", "Epic Description");
        int epicId = saveManager.createEpic(epic);

        Subtask subtask1 = new Subtask("Subtask 1", "Subtask 1 Description", epicId);
        Subtask subtask2 = new Subtask("Subtask 2", "Subtask 2 Description", epicId);

        saveManager.createTask(task1);
        saveManager.createTask(task2);
        saveManager.createSubtask(subtask1);
        saveManager.createSubtask(subtask2);

        saveManager.save();

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(testFile);

        assertEquals(2, loadedManager.getAllTasks().size());
        assertEquals(1, loadedManager.getAllEpics().size());
        assertEquals(2, loadedManager.getAllSubtasks().size());

        Task loadTask = loadedManager.getAllTasks().get(0);
        assertNotNull(loadTask);
        assertEquals("Task 1", loadTask.getTitle());
        assertEquals("Task 1 Description", loadTask.getDescription());

        Epic loadEpic = loadedManager.getAllEpics().get(0);
        assertEquals(2, loadedManager.getAllSubtasksFromEpic(loadEpic.getId()).size());
    }

    @Test
    void idMustBePreservedAfterSavingAndLoading() throws IOException {
        File testFile = File.createTempFile("test_ids", ".txt");
        testFile.deleteOnExit();

        FileBackedTaskManager manager = new FileBackedTaskManager(testFile);

        Task task = new Task("Task", "Task Description");
        int taskId = manager.createTask(task);

        Epic epic = new Epic("Epic", "Epic Description");
        int epicId = manager.createEpic(epic);

        Subtask subtask = new Subtask("Subtask", "Subtask Description", epicId);
        int subtaskId = manager.createSubtask(subtask);

        manager.save();

        FileBackedTaskManager loadManager = FileBackedTaskManager.loadFromFile(testFile);

        assertNotNull(loadManager.getTaskById(taskId));
        assertNotNull(loadManager.getEpicById(epicId));
        assertNotNull(loadManager.getSubtaskById(subtaskId));

        assertEquals("Task", loadManager.getTaskById(taskId).getTitle());
        assertEquals("Epic", loadManager.getEpicById(epicId).getTitle());
        assertEquals("Subtask", loadManager.getSubtaskById(subtaskId).getTitle());
    }

}