package manager;

import tasks.Task;
import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private final List<Task> historyTask = new LinkedList<>();
    private static final int MAX_SIZE_HISTORY = 10;

    @Override
    public void add(Task task) {
        if (task == null) {
            return;
        }
        if (historyTask.size() == MAX_SIZE_HISTORY) {
            historyTask.removeFirst();
        }
        Task copyForHistoryTask = new Task(task.getTitle(), task.getDescription());
        copyForHistoryTask.setId(task.getId());
        copyForHistoryTask.setStatus(task.getStatus());
        historyTask.add(copyForHistoryTask);
    }

    @Override
    public List<Task> getHistory() {
        return new LinkedList<>(historyTask);
    }

}
