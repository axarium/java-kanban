package model;

public class Subtask extends Task {
    private final int epicId;

    public Subtask(String title, String description, TaskStatus status, int epicId) {
        super(title, description, status);
        this.epicId = epicId;
    }

    public Subtask(Subtask subtask) {
        super(subtask);
        epicId = subtask.getEpicId();
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        String result = "Subtask{id=" + getId() + ", title='" + getTitle() + "', ";

        if (getDescription() != null) {
            result += "description.length=" + getDescription().length();
        } else {
            result += "description=null";
        }

        return (result + ", status=" + getStatus() + ", epicId=" + epicId + "}");
    }
}