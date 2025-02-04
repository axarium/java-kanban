package model;

import java.util.ArrayList;

public class Epic extends Task {
    private final ArrayList<Integer> subtasksIds;

    public Epic(String title, String description) {
        super(title, description);
        this.subtasksIds = new ArrayList<>();
    }

    public ArrayList<Integer> getSubtasksIds() {
        return subtasksIds;
    }

    @Override
    public String toString() {
        String result = "Task{id=" + getId() + ", title='" + getTitle() + "', ";

        if (getDescription() != null) {
            result += "description.length=" + getDescription().length();
        } else {
            result += "description=null";
        }

        return (result + ",  status=" + getStatus() + ", subtasksIds=" + subtasksIds + "}");
    }
}