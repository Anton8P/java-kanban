package tasks;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {

    private final List<Integer> subtasksId;
    private final LocalDateTime endTime;

    public Epic(int id, TaskType type, String title, String description, TaskStatus status, List<Integer> subtasksId,
                LocalDateTime startTime, long duration, LocalDateTime endTime) {
        super(id, type, title, description, status, startTime, duration);
        this.subtasksId = new ArrayList<>(subtasksId);
        this.endTime = endTime;
    }

    public Epic(String title, String description) {
        this(0, TaskType.EPIC, title, description, TaskStatus.NEW, new ArrayList<>(),
                null, 0, null);
    }

    public Epic(String title, String description, LocalDateTime startTime, long duration) {
        this(0, TaskType.EPIC, title, description, TaskStatus.NEW, new ArrayList<>(),
                startTime, duration, null);
    }

    @Override
    public LocalDateTime getEndTime() {
        return this.endTime;
    }

    public List<Integer> getSubtasksAllIds() {
        return new ArrayList<>(subtasksId);
    }

    public Epic addSubtaskId(int subtaskId) {
        List<Integer> newSubtasks = new ArrayList<>(this.subtasksId);
        newSubtasks.add(subtaskId);
        return new Epic(this.getId(), this.getType(), this.getTitle(), this.getDescription(), this.getStatus(),
                newSubtasks, this.getStartTime(), this.getDuration().toMinutes(), this.endTime);
    }

    public boolean removeSubtaskId(int subtaskId) {
        return subtasksId.remove(Integer.valueOf(subtaskId));
    }

    public Epic withTimes(LocalDateTime startTime, long duration, LocalDateTime endTime) {
        return new Epic(this.getId(), this.getType(), this.getTitle(), this.getDescription(), this.getStatus(),
                this.getSubtasksAllIds(), startTime, duration, endTime);
    }

    public Epic withId(int newId) {
        return new Epic(newId, this.getType(), this.getTitle(), this.getDescription(), this.getStatus(),
                this.subtasksId, this.getStartTime(), this.getDuration().toMinutes(), this.endTime);
    }

    public Epic withTitle(String newTitle) {
        return new Epic(this.getId(), this.getType(), newTitle, this.getDescription(), this.getStatus(),
                this.subtasksId, this.getStartTime(), this.getDuration().toMinutes(), this.endTime);
    }

    public Epic withDescription(String newDescription) {
        return new Epic(this.getId(), this.getType(), this.getTitle(), newDescription, this.getStatus(),
                this.subtasksId, this.getStartTime(), this.getDuration().toMinutes(), this.endTime);
    }

    public Epic withStatus(TaskStatus newStatus) {
        return new Epic(this.getId(), this.getType(), this.getTitle(), this.getDescription(), newStatus,
                this.subtasksId, this.getStartTime(), this.getDuration().toMinutes(), this.endTime);
    }

    public Epic withSubtasks(List<Integer> newSubtasks) {
        return new Epic(this.getId(), this.getType(), this.getTitle(), this.getDescription(), this.getStatus(),
                newSubtasks, this.getStartTime(), this.getDuration().toMinutes(), this.endTime);
    }
}
