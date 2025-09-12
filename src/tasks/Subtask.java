package tasks;

public class Subtask extends Task {

    private final int epicId;

    public Subtask(int id, String title, String description, TaskStatus status, int epicId) {
        super(id, title, description, status);
        this.epicId = epicId;
    }

    public Subtask(String title, String description, int epicId) {
        this(0, title, description, TaskStatus.NEW, epicId);
    }

    public Subtask withId(int newId) {
        return new Subtask(newId, this.getTitle(), this.getDescription(), this.getStatus(), this.epicId);
    }

    public Subtask withTitle(String newTitle) {
        return new Subtask(this.getId(), newTitle, this.getDescription(), this.getStatus(), this.epicId);
    }

    public Subtask withDescription(String newDescription) {
        return new Subtask(this.getId(), this.getTitle(), newDescription, this.getStatus(), this.epicId);
    }

    public Subtask withStatus(TaskStatus newStatus) {
        return new Subtask(this.getId(), this.getTitle(), this.getDescription(), newStatus, this.epicId);
    }

    public Subtask withEpicId(int newEpicId) {
        return new Subtask(this.getId(), this.getTitle(), this.getDescription(), this.getStatus(), newEpicId);
    }

    public int getEpicId() {
        return epicId;
    }

}