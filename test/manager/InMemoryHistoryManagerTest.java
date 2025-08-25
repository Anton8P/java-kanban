package manager;

import org.junit.jupiter.api.Test;
import tasks.Task;
import tasks.TaskStatus;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    @Test
    void shouldPreservePreviousStateOfObject() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        Task initialTask = new Task("Initial Task", "Preview description", 155);
        initialTask.setStatus(TaskStatus.IN_PROGRESS);
        historyManager.add(initialTask);
        initialTask.setTitle("Changed title");
        initialTask.setDescription("Modified description");
        initialTask.setStatus(TaskStatus.DONE);
        List<Task> history = historyManager.getHistory();
        Task fromHistory = history.get(0);
        assertNotNull(fromHistory);
        assertNotEquals(initialTask.getTitle(), fromHistory.getTitle());
        assertEquals("Initial Task", fromHistory.getTitle());
        assertEquals("Preview description", fromHistory.getDescription());
        assertEquals(TaskStatus.IN_PROGRESS, fromHistory.getStatus());
    }


}