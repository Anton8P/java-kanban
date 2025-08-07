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

    public void updateEpicStatus(HashMap<Integer, Subtask> subtasksAll) {
        if (subtasksId.isEmpty()) {
            setStatus(TaskStatus.NEW);
            return;
        }

        boolean allTasksNew = true;
        boolean allTasksDone = true;

        for (Integer subtaskId : subtasksId) {
            Subtask subtask = subtasksAll.get(subtaskId);
            if (subtask == null) {
                continue;
            }

            if (subtask.getStatus() != TaskStatus.NEW) {
                allTasksNew = false;
            }
            if (subtask.getStatus() != TaskStatus.DONE) {
                allTasksDone = false;
            }
        }

        if (allTasksNew) {
            setStatus(TaskStatus.NEW);
        } else if (allTasksDone) {
            setStatus(TaskStatus.DONE);
        } else {
            setStatus(TaskStatus.IN_PROGRESS);
        }
    }
}
