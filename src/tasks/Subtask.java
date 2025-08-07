package tasks;

public class Subtask extends Task {

    private final int epicId;

    public Subtask(String title, String description, Epic epic) {
        super(title, description);
        this.epicId = epic.getId();
    }

    public int getEpicId() {
        return epicId;
    }

}