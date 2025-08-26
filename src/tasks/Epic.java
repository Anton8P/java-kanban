package tasks;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {

    private final List<Integer> subtasksId;

    public Epic(String title, String description) {
        super(title, description);
        subtasksId = new ArrayList<>();
    }

    public boolean addSubtaskId(Subtask subtask) {
        return subtasksId.add(subtask.getId());
    }

    public List<Integer> getSubtasksId() {
        return new ArrayList<>(subtasksId);
    }

    public boolean removeSubtaskId(int subtaskId) {
        return subtasksId.remove(Integer.valueOf(subtaskId));
    }
}
