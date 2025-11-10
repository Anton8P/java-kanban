package manager;

import org.junit.jupiter.api.Test;
import tasks.*;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InMemoryHistoryManagerTest extends HistoryManagerTest<InMemoryHistoryManager> {

    @Override
    protected InMemoryHistoryManager createHistoryManager() {
        return new InMemoryHistoryManager();
    }

    @Test
    @Override
    void historyShouldBeEmptyIfNoTasksHaveBeenViewed() {
        super.historyShouldBeEmptyIfNoTasksHaveBeenViewed();
    }

    @Test
    @Override
    void shouldAddTaskToHistory() {
        super.shouldAddTaskToHistory();
    }

    @Test
    @Override
    void historyMustNotContainDuplicates() {
        super.historyMustNotContainDuplicates();
    }

    @Test
    @Override
    void historyShouldBeEmptyAfterDeletingTask() {
        super.historyShouldBeEmptyAfterDeletingTask();
    }

    @Test
    @Override
    void shouldRemoveTaskFromBeginning() {
        super.shouldRemoveTaskFromBeginning();
    }

    @Test
    @Override
    void shouldRemoveTaskFromMiddle() {
        super.shouldRemoveTaskFromMiddle();
    }

    @Test
    @Override
    void shouldRemoveTaskFromEnd() {
        super.shouldRemoveTaskFromEnd();
    }

    @Test
    void shouldPreservePreviousStateOfObject() {
        LocalDateTime dateTime1 = LocalDateTime.of(2000, 12, 30, 12, 0);
        LocalDateTime dateTime2 = LocalDateTime.of(2000, 12, 31, 23, 0);
        Task initialTask = new Task(155, TaskType.TASK, "Initial Task", "Preview description"
                , TaskStatus.IN_PROGRESS, dateTime1, 15);
        historyManager.add(initialTask);
        Task modifiedTask = initialTask
                .withTitle("Changed title")
                .withDescription("Modified description")
                .withStatus(TaskStatus.DONE)
                .withTimes(dateTime2, 20);
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
        assertEquals(initialTask.getStartTime(), LocalDateTime.of(2000, 12, 30,
                12, 0));
        assertEquals(modifiedTask.getStartTime(), LocalDateTime.of(2000, 12, 31,
                23, 0));
        assertEquals(15, initialTask.getDuration().toMinutes());
        assertEquals(20, modifiedTask.getDuration().toMinutes());
    }

    @Test
    void methodAddShouldAcceptAllTaskTypes() {
        Task task = new Task("Task", "Description to task").withId(1);
        Epic epic = new Epic("Epic", "Description to epic").withId(2);
        Subtask subtask = new Subtask("Subtask", "Description to subtask", 2).withId(3);
        historyManager.add(task);
        historyManager.add(epic);
        historyManager.add(subtask);
        List<Task> history = historyManager.getHistory();
        assertEquals(3, history.size(), "Должно быть 3 задачи в истории");
        assertTrue(history.get(0) instanceof Task, "Первая задача должна быть Task");
        assertTrue(history.get(1) instanceof Epic, "Вторая задача должна быть Epic");
        assertTrue(history.get(2) instanceof Subtask, "Третья задача должна быть Subtask");
        assertEquals(1, history.get(0).getId(), "У первой задачи должен быть ID = 1");
        assertEquals(2, history.get(1).getId(), "У первой задачи должен быть ID = 2");
        assertEquals(3, history.get(2).getId(), "У первой задачи должен быть ID = 3");
    }
}