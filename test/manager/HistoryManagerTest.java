package manager;

import org.junit.jupiter.api.BeforeEach;
import tasks.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public abstract class HistoryManagerTest<T extends HistoryManager> {
    protected T historyManager;

    protected abstract T createHistoryManager();

    @BeforeEach
    void setUp() {
        historyManager = createHistoryManager();
    }

    void historyShouldBeEmptyIfNoTasksHaveBeenViewed() {
        List<Task> history = historyManager.getHistory();
        assertNotNull(history, "История не должна быть null");
        assertTrue(history.isEmpty(), "История должна быть пустой если не было просмотров");
        assertEquals(0, history.size(), "Размер пустой истории должен равнятся == 0");
    }

    void shouldAddTaskToHistory() {
        Task task = new Task("Task", "Description").withId(1);
        historyManager.add(task);
        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size(), "История должна содержать 1 задачу");
        assertEquals(task.getId(), history.get(0).getId(), "ID задачи в истории должен совпадать");
        assertEquals(task.getTitle(), history.get(0).getTitle(), "Название задачи должно сохраниться");
    }

    void historyMustNotContainDuplicates() {
        Task task = new Task("Task", "Description").withId(1);
        historyManager.add(task);
        historyManager.add(task);
        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size(), "История не должна содержать дубликатов");
        assertEquals(task.getId(), history.get(0).getId(), "В истории должна быть одна задача");
    }

    void historyShouldBeEmptyAfterDeletingTask() {
        Task task = new Task("Test Task", "Description").withId(1);
        historyManager.add(task);
        historyManager.remove(1);
        List<Task> history = historyManager.getHistory();
        assertTrue(history.isEmpty(), "История должна быть пустой после удаления задачи");
    }

    void shouldRemoveTaskFromBeginning() {
        Task task1 = new Task("Task 1", "Description").withId(1);
        Task task2 = new Task("Task 2", "Description").withId(2);
        Task task3 = new Task("Task 3", "Description").withId(3);
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.remove(1);
        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size(), "История должна содержать 2 задачи после удаления");
        assertEquals(2, history.get(0).getId(), "Первой должна быть Task 2");
        assertEquals(3, history.get(1).getId(), "Второй должна быть Task 3");
        assertFalse(history.contains(task1), "История не должна содержать удаленную Task 1");
    }

    void shouldRemoveTaskFromMiddle() {
        Task task1 = new Task("Task 1", "Description").withId(1);
        Task task2 = new Task("Task 2", "Description").withId(2);
        Task task3 = new Task("Task 3", "Description").withId(3);
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.remove(2);
        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size(), "История должна содержать 2 задачи после удаления");
        assertEquals(1, history.get(0).getId(), "Первой должна быть Task 1");
        assertEquals(3, history.get(1).getId(), "Второй должна быть Task 3");
        assertFalse(history.contains(task2), "История не должна содержать удаленную Task 2");
    }

    void shouldRemoveTaskFromEnd() {
        Task task1 = new Task("Task 1", "Description").withId(1);
        Task task2 = new Task("Task 2", "Description").withId(2);
        Task task3 = new Task("Task 3", "Description").withId(3);
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.remove(3);
        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size(), "История должна содержать 2 задачи после удаления");
        assertEquals(1, history.get(0).getId(), "Первой должна быть Task 1");
        assertEquals(2, history.get(1).getId(), "Второй должна быть Task 2");
        assertFalse(history.contains(task3), "История не должна содержать удаленную Task 3");
    }


}