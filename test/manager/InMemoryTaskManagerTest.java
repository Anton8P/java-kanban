package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

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
        Task task = new Task("Test NewTask", "Test NewTask description");
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
        Epic epic = new Epic("Test NewEpic", "Test NewEpic description");
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
    void notConflictIdInTask() {
        Task taskWithGeneratedId = new Task("Test WithGeneratedIdToTask", "Task description");
        int generatedId = taskManager.createTask(taskWithGeneratedId);
        Task taskWithGivenId = new Task("Test WithGivenIdToTask", "Task description").withId(1);
        int givenId = taskManager.createTask(taskWithGivenId);
        assertNotEquals(0, generatedId, "id должен быть положительным");
        assertNotEquals(0, givenId, "id должен быть положительным");
        assertNotEquals(generatedId, givenId, "id не должны совпадать");
    }

    @Test
    void notConflictIdInEpic() {
        Epic epicWithGeneratedIdOne = new Epic("Test WithGeneratedIdToEpicOne", "Epic description One");
        int generatedIdOne = taskManager.createEpic(epicWithGeneratedIdOne);
        Epic epicWithGeneratedIdTwo = new Epic("Test WithGeneratedIdToEpicTwo", "Epic description Two");
        int generatedIdTwo = taskManager.createEpic(epicWithGeneratedIdTwo);
        Epic epicWithGivenId = new Epic("Test WithGivenIdToEpic", "Epic description").withId(2);
        int givenId = taskManager.createEpic(epicWithGivenId);
        assertNotEquals(0, generatedIdOne, "id должен быть положительным");
        assertNotEquals(0, generatedIdTwo, "id должен быть положительным");
        assertNotEquals(0, epicWithGivenId.getId(), "id должен быть положительным");
        assertNotEquals(generatedIdOne, givenId, "id не должны совпадать");
        assertNotEquals(generatedIdTwo, givenId, "id не должны совпадать");
    }

    @Test
    void notConflictIdInSubtask() {
        Epic epic = new Epic("Test WithGeneratedIdToEpic", "Epic description");
        int epicId = taskManager.createEpic(epic);
        Subtask subtaskWithGeneratedId = new Subtask("Test WithGeneratedIdToSubtask", "Subtask description", epicId);
        int generatedId = taskManager.createSubtask(subtaskWithGeneratedId);
        Subtask subtaskWithGivenId = new Subtask("Test WithGivenIdToSubtask", "Subtask description", epicId).withId(2);
        int givenId = taskManager.createSubtask(subtaskWithGivenId);
        assertNotEquals(0, epicId, "id должен быть положительным");
        assertNotEquals(0, generatedId, "id должен быть положительным");
        assertNotEquals(0, givenId, "id должен быть положительным и быть равным id Epic");
        assertNotEquals(generatedId, givenId, "id не должны совпадать");
    }

    @Test
    void fieldsShouldNotBeChangedInManager() {
        Task task = new Task("Test Task", "Description");
        int id = taskManager.createTask(task);
        Task taskTemp = task.withId(id);
        task = taskTemp;
        Task taskFromManager = taskManager.getTask(id);
        assertEquals(task.getTitle(), taskFromManager.getTitle());
        assertNotNull(task);
        assertNotNull(taskFromManager);
        assertEquals(task.getId(), taskFromManager.getId(), "id не должен изменяться");
        assertEquals("Test Task", taskFromManager.getTitle(), "Title не должно изменяться");
        assertEquals("Description", taskFromManager.getDescription(), "Description не должно изменяться");
    }

    @Test
    void epicUpdateShouldUpdateStatusCorrectly() {
        Epic epic = new Epic("Test Epic", "Description");
        int epicId = taskManager.createEpic(epic);
        Epic initialEpic = taskManager.getEpicById(epicId);
        assertEquals(TaskStatus.NEW, initialEpic.getStatus(), "Пустой Эпик должен иметь статус NEW");

        Subtask subtask1 = new Subtask("Subtask 1", "Description", epicId);
        int subtaskId1 = taskManager.createSubtask(subtask1);
        Epic epicAfterFirstSubtask = taskManager.getEpicById(epicId);
        assertEquals(TaskStatus.NEW, epicAfterFirstSubtask.getStatus(),
                "Эпик с подзадачами NEW должен иметь статус NEW");

        Subtask subtask2 = new Subtask("Subtask 2", "Description", epicId);
        int subtaskId2 = taskManager.createSubtask(subtask2);
        Subtask currentSubtask2 = taskManager.getSubtaskById(subtaskId2);
        Subtask updatedSubtask2Dto = new Subtask(
                currentSubtask2.getTitle(),
                currentSubtask2.getDescription(),
                currentSubtask2.getEpicId()
        ).withId(currentSubtask2.getId()).withStatus(TaskStatus.DONE);
        taskManager.updateSubtask(updatedSubtask2Dto);

        Epic epicWithMixedStatus = taskManager.getEpicById(epicId);
        assertEquals(TaskStatus.IN_PROGRESS, epicWithMixedStatus.getStatus(),
                "Эпик с подзадачами разных статусов должен иметь статус IN_PROGRESS");
        Subtask currentSubtask1 = taskManager.getSubtaskById(subtaskId1);
        Subtask updatedSubtask1Dto = new Subtask(
                currentSubtask1.getTitle(),
                currentSubtask1.getDescription(),
                currentSubtask1.getEpicId()
        ).withId(currentSubtask1.getId()).withStatus(TaskStatus.DONE);
        taskManager.updateSubtask(updatedSubtask1Dto);

        Epic epicAllDone = taskManager.getEpicById(epicId);
        assertEquals(TaskStatus.DONE, epicAllDone.getStatus(),
                "Эпик со всеми подзадачами DONE должен иметь статус DONE");
        Subtask subtask3 = new Subtask("Subtask 3", "Description", epicId);
        int subtaskId3 = taskManager.createSubtask(subtask3);

        Subtask currentSubtask3 = taskManager.getSubtaskById(subtaskId3);
        Subtask updatedSubtask3Dto = new Subtask(
                currentSubtask3.getTitle(),
                currentSubtask3.getDescription(),
                currentSubtask3.getEpicId()
        ).withId(currentSubtask3.getId()).withStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateSubtask(updatedSubtask3Dto);

        Epic epicWithInProgress = taskManager.getEpicById(epicId);
        assertEquals(TaskStatus.IN_PROGRESS, epicWithInProgress.getStatus(),
                "Эпик с подзадачами IN_PROGRESS должен иметь статус IN_PROGRESS");

        taskManager.deleteSubtaskById(subtaskId1);
        taskManager.deleteSubtaskById(subtaskId2);
        taskManager.deleteSubtaskById(subtaskId3);

        Epic epicEmpty = taskManager.getEpicById(epicId);
        assertEquals(TaskStatus.NEW, epicEmpty.getStatus(), "Пустой Эпик должен иметь статус NEW");
    }
}