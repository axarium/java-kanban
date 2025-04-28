package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;

public class Epic extends Task {
    private LocalDateTime endTime;
    private List<Integer> subtasksIds;

    public Epic(String title, String description) {
        super(title, description);
        this.subtasksIds = new ArrayList<>();
    }

    public Epic(Epic epic) {
        super(epic);
        this.endTime = epic.endTime;
        this.subtasksIds = new ArrayList<>(epic.getSubtasksIds());
    }

    @Override
    public TaskType getType() {
        return TaskType.EPIC;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public List<Integer> getSubtasksIds() {
        return subtasksIds;
    }

    public void setSubtasksIds(List<Integer> subtasksIds) {
        this.subtasksIds = subtasksIds;
    }

    @Override
    public String toString() {
        String result = "Epic{id=" + getId() + ", title='" + getTitle() + "', ";

        if (getDescription() != null) {
            result += "description.length=" + getDescription().length() + ", ";
        } else {
            result += "description=null, ";
        }

        if (getStartTime() != null) {
            result += "startTime=" + getStartTime().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")) + ", ";
        } else {
            result += "startTime=null, ";
        }

        if (getDuration() != null) {
            result += "duration=" + getDuration().toMinutes() + ", ";
        } else {
            result += "duration=null, ";
        }

        if (getEndTime() != null) {
            result += "endTime=" + getEndTime().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")) + ", ";
        } else {
            result += "endTime=null, ";
        }

        return (result + "status=" + getStatus() + ", subtasksIds=" + subtasksIds + "}");
    }
}