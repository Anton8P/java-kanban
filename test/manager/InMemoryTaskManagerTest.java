package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {
    InMemoryTaskManager inMemoryTaskManager;
    TaskManager taskManager;

    @BeforeEach
    void setUp() {
        taskManager = Managers.getDefault();
        inMemoryTaskManager = new InMemoryTaskManager();
    }

    @Test
    void createTask() {
        Task task = new Task("Test NewTask", "Test NewTask description", taskManager.getGeneratedId());
        int taskId = inMemoryTaskManager.createTask(task);
        Task twoTask = inMemoryTaskManager.getTask(taskId);
        Task threeTask = inMemoryTaskManager.getTaskById(taskId);
        assertNotNull(task, "Созданная Задача не может быть Null");
        assertNotNull(twoTask, "Вернувшаяся по id Задача не может быть Null");
        assertNotNull(threeTask, "Вернувшаяся по id Задача не может быть Null");
        assertNotNull(inMemoryTaskManager.getTask(taskId), "Задача должна быть в хранилище с правильным типом данных");
        assertNull(inMemoryTaskManager.getEpic(taskId), "Задача не должна находиться в хранилище не с тем типом данных");
    }

    @Test
    void createEpic() {
        Epic epic = new Epic("Test NewEpic", "Test NewEpic description", taskManager.getGeneratedId());
        int epicId = inMemoryTaskManager.createEpic(epic);
        Task twoEpic = inMemoryTaskManager.getEpic(epicId);
        Task threeEpic = inMemoryTaskManager.getEpicById(epicId);
        assertNotNull(epic, "Созданный Epic не может быть Null");
        assertNotNull(twoEpic, "Вернувшийся по id Epic не может быть Null");
        assertNotNull(threeEpic, "Вернувшийся по id Epic не может быть Null");
        assertNotNull(inMemoryTaskManager.getEpic(epicId), "Epic должн быть в хранилище с правильным типом данных");
        assertNull(inMemoryTaskManager.getSubtask(epicId), "Epic не должен находиться в хранилище не с тем типом данных");
    }

    @Test
    void createSubtask() {
        Epic epic = new Epic("Test NewEpic", "Test NewEpic description", taskManager.getGeneratedId());
        int epicId = inMemoryTaskManager.createEpic(epic);
        Subtask subtask = new Subtask("Test NewSubtask", "Test NewSubtask description", taskManager.getGeneratedId(), epicId);
        int subtaskId = inMemoryTaskManager.createSubtask(subtask);
        Subtask twoSubtask = inMemoryTaskManager.getSubtask(subtaskId);
        Subtask threeSubtask = inMemoryTaskManager.getSubtaskById(subtaskId);
        assertNotNull(epic, "Созданный Epic не может быть Null");
        assertNotNull(subtask, "Созданный Subtask не может быть Null");
        assertNotNull(twoSubtask, "Вернувшийся по id Subtask не может быть Null");
        assertNotNull(threeSubtask, "Вернувшийся по id Subtask не может быть Null");
        assertNotNull(inMemoryTaskManager.getSubtask(subtaskId), "Subtask должна быть в хранилище с правильным типом данных");
        assertNull(inMemoryTaskManager.getTask(subtaskId), "Subtask не должна находиться в хранилище не с тем типом данных");
    }

    @Test
    void notConflictIdInTask() {
        taskManager.getGeneratedId();
        taskManager.getGeneratedId();
        Task taskWithGeneratedId = new Task("Test WithGeneratedIdToTask", "Task description", taskManager.getGeneratedId());
        int generatedId = taskManager.createTask(taskWithGeneratedId);
        Task taskWithGivenId = new Task("Test WithGivenIdToTask", "Task description", 3);
        int givenId = taskManager.createTask(taskWithGivenId);
        assertNotEquals(generatedId, givenId, "id не должны совпадать");
    }

    @Test
    void notConflictIdInEpic() {
        taskManager.getGeneratedId();
        Epic epicWithGeneratedId = new Epic("Test WithGeneratedIdToEpic", "Epic description", taskManager.getGeneratedId());
        int generatedId = taskManager.createEpic(epicWithGeneratedId);
        Epic epicWithGivenId = new Epic("Test WithGivenIdToEpic", "Epic description", 2);
        int givenId = taskManager.createEpic(epicWithGivenId);
        assertNotEquals(generatedId, givenId, "id не должны совпадать");
    }

    @Test
    void notConflictIdInSubtask() {
        Epic epic = new Epic("Test WithGeneratedIdToEpic", "Epic description", taskManager.getGeneratedId());
        int epicId = taskManager.createEpic(epic);
        Subtask subtaskWithGeneratedId = new Subtask("Test WithGeneratedIdToSubtask", "Subtask description", taskManager.getGeneratedId(), epicId);
        int generatedId = taskManager.createSubtask(subtaskWithGeneratedId);
        Subtask subtaskWithGivenId = new Subtask("Test WithGivenIdToSubtask", "Subtask description", 1, epicId);
        int givenId = taskManager.createSubtask(subtaskWithGivenId);
        assertNotEquals(generatedId, givenId, "id не должны совпадать");
    }

    @Test
    void fieldsShouldNotBeChangedInManager() {
        Task task = new Task("Test Task", "Description", taskManager.getGeneratedId());
        int id = taskManager.createTask(task);
        Task taskFromManager = taskManager.getTask(id);
        assertNotNull(task);
        assertNotNull(taskFromManager);
        assertEquals(task.getId(), taskFromManager.getId(), "id не должен изменяться");
        assertEquals("Test Task", taskFromManager.getTitle(), "Title не должно изменяться");
        assertEquals("Description", taskFromManager.getDescription(), "Description не должно изменяться");
    }
}