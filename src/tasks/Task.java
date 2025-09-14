package tasks;

import java.util.Objects;

public class Task {
    private final int id;
    private final String title;
    private final String description;
    private final TaskStatus status;

    public Task(int id, String title, String description, TaskStatus status) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
    }

    public Task(String title, String description) {
        this(0, title, description, TaskStatus.NEW);
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

    public Task withId(int newId) {
        return new Task(newId, this.title, this.description, this.status);
    }

    public Task withTitle(String newTitle) {
        return new Task(this.id, newTitle, this.description, this.status);
    }

    public Task withDescription(String newDescription) {
        return new Task(this.id, this.title, newDescription, this.status);
    }

    public Task withStatus(TaskStatus newStatus) {
        return new Task(this.id, this.title, this.description, newStatus);
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
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                '}';
    }
}
