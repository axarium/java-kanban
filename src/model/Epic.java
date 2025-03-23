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

    public static Epic fromString(String value) throws IndexOutOfBoundsException, IllegalArgumentException {
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

    public List<Integer> getSubtasksIds() {
        return subtasksIds;
    }

    @Override
    public String toString() {
        return String.format(
                "%d,EPIC,%s,%s,%s",
                getId(),
                getTitle(),
                getStatus(),
                getDescription()
        );
    }
}