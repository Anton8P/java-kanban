package tasks;

import java.util.ArrayList;
import java.util.HashMap;

public class Epic extends Task {

    private final ArrayList<Integer> subtasksId = new ArrayList<>();

    public Epic(String title, String description) {
        super(title, description);
    }

    public void addSubtaskId(int subtask) {
        subtasksId.add(subtask);
    }

    public ArrayList<Integer> getSubtasksId() {
        return new ArrayList<>(subtasksId);
    }

    public boolean removeSubtaskId(int subtaskId) {
        return subtasksId.remove(Integer.valueOf(subtaskId));
    }
}
