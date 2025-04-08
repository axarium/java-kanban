package util;

import model.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TaskConverter {
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

    private TaskConverter() {

    }

    public static String anyTaskToCsvString(Task task) {
        return switch (task.getType()) {
            case EPIC -> epicToCsvString((Epic) task);
            case SUBTASK -> subtaskToCsvString((Subtask) task);
            default -> taskToCsvString(task);
        };
    }

    public static Task anyTaskFromCsvString(String value) throws IndexOutOfBoundsException, IllegalArgumentException {
        String[] taskFields = value.split(",");
        TaskType taskType = TaskType.valueOf(taskFields[1]);

        return switch (taskType) {
            case TaskType.EPIC -> epicFromCsvString(value);
            case TaskType.SUBTASK -> subtaskFromCsvString(value);
            default -> taskFromCsvString(value);
        };
    }

    private static String taskToCsvString(Task task) {
        return String.format(
                "%d,%s,%s,%s,%s,%s,%d,%s",
                task.getId(),
                task.getType(),
                task.getTitle(),
                task.getStatus(),
                task.getDescription(),
                task.getStartTime() != null ? task.getStartTime().format(dateTimeFormatter) : null,
                task.getDuration() != null ? task.getDuration().toMinutes() : null,
                task.getEndTime() != null ? task.getEndTime().format(dateTimeFormatter) : null
        );
    }

    private static String epicToCsvString(Epic epic) {
        return String.format(
                "%d,%s,%s,%s,%s,%s,%d,%s",
                epic.getId(),
                epic.getType(),
                epic.getTitle(),
                epic.getStatus(),
                epic.getDescription(),
                epic.getStartTime() != null ? epic.getStartTime().format(dateTimeFormatter) : null,
                epic.getDuration() != null ? epic.getDuration().toMinutes() : null,
                epic.getEndTime() != null ? epic.getEndTime().format(dateTimeFormatter) : null
        );
    }

    private static String subtaskToCsvString(Subtask subtask) {
        return String.format(
                "%d,%s,%s,%s,%s,%s,%d,%s,%d",
                subtask.getId(),
                subtask.getType(),
                subtask.getTitle(),
                subtask.getStatus(),
                subtask.getDescription(),
                subtask.getStartTime() != null ? subtask.getStartTime().format(dateTimeFormatter) : null,
                subtask.getDuration() != null ? subtask.getDuration().toMinutes() : null,
                subtask.getEndTime() != null ? subtask.getEndTime().format(dateTimeFormatter) : null,
                subtask.getEpicId()
        );
    }

    private static Task taskFromCsvString(String value) throws IndexOutOfBoundsException, IllegalArgumentException {
        String[] taskFields = value.split(",");
        String taskId = taskFields[0];
        String taskTitle = taskFields[2];
        String taskStatus = taskFields[3];
        String taskDescription = taskFields[4];
        String taskStartTime = taskFields[5];
        String taskDuration = taskFields[6];

        Task task = new Task(
                !taskTitle.equals("null") ? taskTitle : null,
                !taskDescription.equals("null") ? taskDescription : null,
                !taskStatus.equals("null") ? TaskStatus.valueOf(taskStatus) : null,
                !taskStartTime.equals("null") ? LocalDateTime.parse(taskStartTime, dateTimeFormatter) : null,
                !taskDuration.equals("null") ? Duration.ofMinutes(Long.parseLong(taskDuration)) : null
        );
        task.setId(Integer.parseInt(taskId));

        return task;
    }

    private static Epic epicFromCsvString(String value) throws IndexOutOfBoundsException, IllegalArgumentException {
        String[] epicFields = value.split(",");
        String epicId = epicFields[0];
        String epicTitle = epicFields[2];
        String epicStatus = epicFields[3];
        String epicDescription = epicFields[4];
        String epicStartTime = epicFields[5];
        String epicDuration = epicFields[6];
        String epicEndTime = epicFields[7];

        Epic epic = new Epic(
                !epicTitle.equals("null") ? epicTitle : null,
                !epicDescription.equals("null") ? epicDescription : null
        );
        epic.setId(Integer.parseInt(epicId));
        epic.setStatus(!epicStatus.equals("null") ? TaskStatus.valueOf(epicStatus) : null);
        epic.setStartTime(!epicStartTime.equals("null") ? LocalDateTime.parse(epicStartTime, dateTimeFormatter) : null);
        epic.setEndTime(!epicEndTime.equals("null") ? LocalDateTime.parse(epicEndTime, dateTimeFormatter) : null);
        epic.setDuration(!epicDuration.equals("null") ? Duration.ofMinutes(Long.parseLong(epicDuration)) : null);

        return epic;
    }

    private static Subtask subtaskFromCsvString(String value)
            throws IndexOutOfBoundsException, IllegalArgumentException {
        String[] subtaskFields = value.split(",");
        String subtaskId = subtaskFields[0];
        String subtaskTitle = subtaskFields[2];
        String subtaskStatus = subtaskFields[3];
        String subtaskDescription = subtaskFields[4];
        String subtaskStartTime = subtaskFields[5];
        String subtaskDuration = subtaskFields[6];
        String subtaskEpicId = subtaskFields[8];

        Subtask subtask = new Subtask(
                !subtaskTitle.equals("null") ? subtaskTitle : null,
                !subtaskDescription.equals("null") ? subtaskDescription : null,
                !subtaskStatus.equals("null") ? TaskStatus.valueOf(subtaskStatus) : null,
                !subtaskStartTime.equals("null") ? LocalDateTime.parse(subtaskStartTime, dateTimeFormatter) : null,
                !subtaskDuration.equals("null") ? Duration.ofMinutes(Long.parseLong(subtaskDuration)) : null,
                Integer.parseInt(subtaskEpicId)
        );
        subtask.setId(Integer.parseInt(subtaskId));

        return subtask;
    }
}