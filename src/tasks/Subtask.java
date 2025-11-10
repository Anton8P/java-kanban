package tasks;

import java.time.LocalDateTime;

public class Subtask extends Task {

    private final int epicId;

    public Subtask(int id, TaskType type, String title, String description, TaskStatus status, int epicId,
                   LocalDateTime startTime, long duration) {
        super(id, type, title, description, status, startTime, duration);
        this.epicId = epicId;
    }

    public Subtask(String title, String description, int epicId) {
        this(0, TaskType.SUBTASK, title, description, TaskStatus.NEW, epicId, LocalDateTime.now(), 0);
    }

    public Subtask(String title, String description, int epicId, LocalDateTime startTime, long duration) {
        this(0, TaskType.SUBTASK, title, description, TaskStatus.NEW, epicId, startTime, duration);
    }

    public int getEpicId() {
        return epicId;
    }

    public Subtask withTimes(LocalDateTime startTime, long durationToMinutes) {
        return new Subtask(this.getId(), this.getType(), this.getTitle(), this.getDescription(), this.getStatus(),
                this.epicId, startTime, durationToMinutes);
    }

    public Subtask withId(int newId) {
        return new Subtask(newId, this.getType(), this.getTitle(), this.getDescription(), this.getStatus(),
                this.epicId, this.getStartTime(), this.getDuration().toMinutes());
    }

    public Subtask withTitle(String newTitle) {
        return new Subtask(this.getId(), this.getType(), newTitle, this.getDescription(), this.getStatus(),
                this.epicId, this.getStartTime(), this.getDuration().toMinutes());
    }

    public Subtask withDescription(String newDescription) {
        return new Subtask(this.getId(), this.getType(), this.getTitle(), newDescription, this.getStatus(),
                this.epicId, this.getStartTime(), this.getDuration().toMinutes());
    }

    public Subtask withStatus(TaskStatus newStatus) {
        return new Subtask(this.getId(), this.getType(), this.getTitle(), this.getDescription(), newStatus,
                this.epicId, this.getStartTime(), this.getDuration().toMinutes());
    }

    public Subtask withEpicId(int newEpicId) {
        return new Subtask(this.getId(), this.getType(), this.getTitle(), this.getDescription(), this.getStatus(),
                newEpicId, this.getStartTime(), this.getDuration().toMinutes());
    }

    public Subtask withStartTime(LocalDateTime startTime) {
        return new Subtask(this.getId(), this.getType(), this.getTitle(), this.getDescription(), this.getStatus(),
                this.epicId, startTime, this.getDuration().toMinutes());
    }

    public Subtask withDuration(long durationToMinutes) {
        return new Subtask(this.getId(), this.getType(), this.getTitle(), this.getDescription(), this.getStatus(),
                this.epicId, this.getStartTime(), durationToMinutes);
    }

}