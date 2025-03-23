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

    public static Subtask fromString(String value) throws IndexOutOfBoundsException, IllegalArgumentException {
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

    public int getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return String.format(
                "%d,SUBTASK,%s,%s,%s,%d",
                getId(),
                getTitle(),
                getStatus(),
                getDescription(),
                getEpicId()
        );
    }
}