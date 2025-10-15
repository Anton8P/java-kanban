package tasks;

import manager.TimeOverlapException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Task {
    private final int id;
    private final TaskType type;
    private final String title;
    private final String description;
    private final TaskStatus status;
    private final LocalDateTime startTime;
    private final Duration duration;

    public Task(int id, TaskType type, String title, String description, TaskStatus status,
                LocalDateTime startTime, long duration) {
        this.id = id;
        this.type = type;
        this.title = title;
        this.description = description;
        this.status = status;
        this.startTime = startTime;
        this.duration = Duration.ofMinutes(duration);
    }

    public Task(String title, String description, LocalDateTime startTime, long duration) {
        this(0, TaskType.TASK, title, description, TaskStatus.NEW, startTime, duration);
    }

    public Task(String title, String description) {
        this(0, TaskType.TASK, title, description, TaskStatus.NEW, LocalDateTime.now(), 0);
    }

    public LocalDateTime getEndTime() {
        if (startTime == null) {
            throw new TimeOverlapException("Время старта не задано.");
        }
        return startTime.plus(duration);
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public TaskType getType() {
        return type;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public Duration getDuration() {
        return duration;
    }

    public Task withTimes(LocalDateTime startTime, long durationToMinutes) {
        return new Task(this.id, this.type, this.title, this.description, this.status,
                startTime, durationToMinutes);
    }

    public Task withStartTime(LocalDateTime startTime) {
        return new Task(this.id, this.type, this.title, this.description,
                this.status, startTime, this.duration.toMinutes());
    }

    public Task withDuration(long durationToMinutes) {
        return new Task(this.id, this.type, this.title, this.description,
                this.status, this.startTime, durationToMinutes);
    }

    public Task withId(int newId) {
        return new Task(newId, this.type, this.title, this.description, this.status,
                this.startTime, this.duration.toMinutes());
    }

    public Task withTitle(String newTitle) {
        return new Task(this.id, this.type, newTitle, this.description, this.status,
                this.startTime, this.duration.toMinutes());
    }

    public Task withDescription(String newDescription) {
        return new Task(this.id, this.type, this.title, newDescription, this.status,
                this.startTime, this.duration.toMinutes());
    }

    public Task withStatus(TaskStatus newStatus) {
        return new Task(this.id, this.type, this.title, this.description, newStatus,
                this.startTime, this.duration.toMinutes());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", type=" + type +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", startTime=" + startTime +
                ", duration=" + duration.toMinutes() +
                '}';
    }
}
