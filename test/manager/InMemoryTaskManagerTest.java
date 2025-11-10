package manager;

import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @Override
    protected InMemoryTaskManager createTaskManager() {
        return new InMemoryTaskManager();
    }

    @Test
    @Override
    void createTaskTest() {
        super.createTaskTest();
    }

    @Test
    @Override
    void createEpicTest() {
        super.createEpicTest();
    }

    @Test
    @Override
    void createSubtaskTest() {
        super.createSubtaskTest();
    }

    @Test
    @Override
    void getAllTasksTest() {
        super.getAllTasksTest();
    }

    @Test
    @Override
    void updateTaskTest() {
        super.updateTaskTest();
    }

    @Test
    @Override
    void deleteTaskTest() {
        super.deleteTaskTest();
    }

    @Test
    void getAllSubtasksByEpicIdTest() {
        super.getAllSubtasksByEpicIdTest();
    }

    @Test
    @Override
    void subtaskMustBeRelatedToEpic() {
        super.subtaskMustBeRelatedToEpic();
    }

    @Test
    @Override
    void epicMustContainLinksToItsSubtasks() {
        super.epicMustContainLinksToItsSubtasks();
    }

    @Test
    @Override
    void whenDeletingAnEpicAllSubtasksMustBeDeleted() {
        super.whenDeletingAnEpicAllSubtasksMustBeDeleted();
    }

    @Test
    @Override
    void tasksShouldNotOverlapInTime() {
        super.tasksShouldNotOverlapInTime();
    }

    @Test
    @Override
    void epicMustBeDeletedAlongWithSubtasks() {
        super.epicMustBeDeletedAlongWithSubtasks();
    }

    ;

    @Test
    void shouldRestoreMaximumId() {
        File file = new File("test.txt");
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        int maxId = 10;
        if (maxId >= manager.generatedId) {
            manager.generatedId = maxId + 1;
        }
        assertEquals(11, manager.generatedId);
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
        Subtask subtaskWithGeneratedId = new Subtask("Test WithGeneratedIdToSubtask",
                "Subtask description", epicId);
        int generatedId = taskManager.createSubtask(subtaskWithGeneratedId);
        Subtask subtaskWithGivenId = new Subtask("Test WithGivenIdToSubtask", "Subtask description",
                epicId).withId(2);
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
        Task taskFromManager = taskManager.getTaskById(id);
        assertEquals(task.getTitle(), taskFromManager.getTitle());
        assertNotNull(task);
        assertNotNull(taskFromManager);
        assertEquals(task.getId(), taskFromManager.getId(), "id не должен изменяться");
        assertEquals("Test Task", taskFromManager.getTitle(), "Title не должно изменяться");
        assertEquals("Description", taskFromManager.getDescription(),
                "Description не должно изменяться");
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