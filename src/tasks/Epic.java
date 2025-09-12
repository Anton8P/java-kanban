package tasks;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {

    private final List<Integer> subtasksId;

    public Epic(int id, String title, String description, TaskStatus status, List<Integer> subtasksId) {
        super(id, title, description, status);
        this.subtasksId = new ArrayList<>(subtasksId);
    }

    public Epic(String title, String description) {
        this(0, title, description, TaskStatus.NEW, new ArrayList<>());
    }

    public List<Integer> getSubtasksId() {
        return new ArrayList<>(subtasksId);
    }

    public Epic withId(int newId) {
        return new Epic(newId, this.getTitle(), this.getDescription(), this.getStatus(), this.subtasksId);
    }

    public Epic withTitle(String newTitle) {
        return new Epic(this.getId(), newTitle, this.getDescription(), this.getStatus(), this.subtasksId);
    }

    public Epic withDescription(String newDescription) {
        return new Epic(this.getId(), this.getTitle(), newDescription, this.getStatus(), this.subtasksId);
    }

    public Epic withStatus(TaskStatus newStatus) {
        return new Epic(this.getId(), this.getTitle(), this.getDescription(), newStatus, this.subtasksId);
    }

    public Epic withSubtasks(List<Integer> newSubtasks) {
        return new Epic(this.getId(), this.getTitle(), this.getDescription(), this.getStatus(), newSubtasks);
    }

    public Epic addSubtaskId(int subtaskId) {
        List<Integer> newSubtasks = new ArrayList<>(this.subtasksId);
        newSubtasks.add(subtaskId);
        return new Epic(this.getId(), this.getTitle(), this.getDescription(), this.getStatus(), newSubtasks);
    }

    public boolean removeSubtaskId(int subtaskId) {
        return subtasksId.remove(Integer.valueOf(subtaskId));
    }
}
