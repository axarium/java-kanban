package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Task {
    private int id;
    private String title;
    private String description;
    private TaskStatus status;
    private LocalDateTime startTime;
    private Duration duration;

    public Task(String title, String description, TaskStatus status, LocalDateTime startTime, Duration duration) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.startTime = startTime;
        this.duration = duration;
    }

    public Task(String title, String description) {
        this.title = title;
        this.description = description;
        this.status = TaskStatus.NEW;
        this.duration = Duration.ZERO;
    }

    public Task(Task task) {
        this.id = task.id;
        this.title = task.title;
        this.description = task.description;
        this.status = task.status;
        this.startTime = task.startTime;
        this.duration = task.duration;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public TaskType getType() {
        return TaskType.TASK;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocalDateTime getEndTime() {
        return (startTime != null && duration != null) ? startTime.plus(duration) : null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Task task = (Task) o;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        String result = "Task{id=" + id + ", title='" + title + "', ";

        if (description != null) {
            result += "description.length=" + description.length() + ", ";
        } else {
            result += "description=null, ";
        }

        if (startTime != null) {
            result += "startTime=" + startTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"));
        } else {
            result += "startTime=null";
        }

        if (duration != null) {
            result += "duration=" + duration.toMinutes();
        } else {
            result += "duration=null";
        }

        if (getEndTime() != null) {
            result += "endTime=" + getEndTime().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"));
        } else {
            result += "endTime=null";
        }

        return (result + ", status=" + status + "}");
    }
}