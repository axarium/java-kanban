package util;

import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;

public class TaskConverter {

    private TaskConverter() {

    }

    public static String taskToCsvString(Task task) {
        return String.format(
                "%d,%s,%s,%s,%s",
                task.getId(),
                task.getType(),
                task.getTitle(),
                task.getStatus(),
                task.getDescription()
        );
    }

    public static String epicToCsvString(Epic epic) {
        return String.format(
                "%d,%s,%s,%s,%s",
                epic.getId(),
                epic.getType(),
                epic.getTitle(),
                epic.getStatus(),
                epic.getDescription()
        );
    }

    public static String subtaskToCsvString(Subtask subtask) {
        return String.format(
                "%d,%s,%s,%s,%s,%d",
                subtask.getId(),
                subtask.getType(),
                subtask.getTitle(),
                subtask.getStatus(),
                subtask.getDescription(),
                subtask.getEpicId()
        );
    }

    public static Task taskFromCsvString(String value) throws IndexOutOfBoundsException, IllegalArgumentException {
        String[] taskFields = value.split(",");
        String taskId = taskFields[0];
        String taskTitle = taskFields[2];
        String taskStatus = taskFields[3];
        String taskDescription = taskFields[4];

        Task task = new Task(taskTitle, taskDescription, TaskStatus.valueOf(taskStatus));
        task.setId(Integer.parseInt(taskId));

        return task;
    }

    public static Epic epicFromCsvString(String value) throws IndexOutOfBoundsException, IllegalArgumentException {
        String[] epicFields = value.split(",");
        String epicId = epicFields[0];
        String epicTitle = epicFields[2];
        String epicStatus = epicFields[3];
        String epicDescription = epicFields[4];

        Epic epic = new Epic(epicTitle, epicDescription);
        epic.setId(Integer.parseInt(epicId));
        epic.setStatus(TaskStatus.valueOf(epicStatus));

        return epic;
    }

    public static Subtask subtaskFromCsvString(String value) throws IndexOutOfBoundsException, IllegalArgumentException {
        String[] subtaskFields = value.split(",");
        String subtaskId = subtaskFields[0];
        String subtaskTitle = subtaskFields[2];
        String subtaskStatus = subtaskFields[3];
        String subtaskDescription = subtaskFields[4];
        String subtaskEpicId = subtaskFields[5];

        Subtask subtask = new Subtask(
                subtaskTitle,
                subtaskDescription,
                TaskStatus.valueOf(subtaskStatus),
                Integer.parseInt(subtaskEpicId)
        );
        subtask.setId(Integer.parseInt(subtaskId));

        return subtask;
    }
}