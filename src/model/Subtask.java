package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Subtask extends Task {
    private final int epicId;

    public Subtask(
            String title,
            String description,
            TaskStatus status,
            LocalDateTime startTime,
            Duration duration,
            int epicId
    ) {
        super(title, description, status, startTime, duration);
        this.epicId = epicId;
    }

    public Subtask(Subtask subtask) {
        super(subtask);
        epicId = subtask.getEpicId();
    }

    @Override
    public TaskType getType() {
        return TaskType.SUBTASK;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        String result = "Subtask{id=" + getId() + ", title='" + getTitle() + "', ";

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

        return (result + "status=" + getStatus() + ", epicId=" + epicId + "}");
    }
}