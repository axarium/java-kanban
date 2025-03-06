package model;

import java.util.List;
import java.util.ArrayList;

public class Epic extends Task {
    private final List<Integer> subtasksIds;

    public Epic(String title, String description) {
        super(title, description);
        this.subtasksIds = new ArrayList<>();
    }

    public Epic(Epic epic) {
        super(epic);
        this.subtasksIds = new ArrayList<>(epic.getSubtasksIds());
    }

    public List<Integer> getSubtasksIds() {
        return subtasksIds;
    }

    @Override
    public String toString() {
        String result = "Epic{id=" + getId() + ", title='" + getTitle() + "', ";

        if (getDescription() != null) {
            result += "description.length=" + getDescription().length();
        } else {
            result += "description=null";
        }

        return (result + ", status=" + getStatus() + ", subtasksIds=" + subtasksIds + "}");
    }
}