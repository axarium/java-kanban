package service;

import exception.ManagerLoadException;
import exception.ManagerSaveException;
import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskType;
import util.TaskConverter;

import java.io.*;
import java.nio.file.Path;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;

public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager {
    private final Path fileStorage;
    private static final String FILE_FORMAT = "id,type,title,status,description,epicId";

    public FileBackedTaskManager(Path fileStorage) {
        this.fileStorage = fileStorage;
    }

    @Override
    public void removeAllTasks() {
        super.removeAllTasks();
        save();
    }

    @Override
    public void removeAllEpics() {
        super.removeAllEpics();
        save();
    }

    @Override
    public void removeAllSubtasks() {
        super.removeAllSubtasks();
        save();
    }

    @Override
    public void createTask(Task task) {
        super.createTask(task);
        save();
    }

    @Override
    public void createEpic(Epic epic) {
        super.createEpic(epic);
        save();
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        Subtask createdSubtask = super.createSubtask(subtask);
        save();
        return createdSubtask;
    }

    @Override
    public Task updateTask(Task task) {
        Task updatedTask = super.updateTask(task);
        save();
        return updatedTask;
    }

    @Override
    public Epic updateEpic(Epic epic) {
        Epic updatedEpic = super.updateEpic(epic);
        save();
        return updatedEpic;
    }

    @Override
    public Subtask updateSubtask(Subtask subtask) {
        Subtask updatedSubtask = super.updateSubtask(subtask);
        save();
        return updatedSubtask;
    }

    @Override
    public Task removeTaskById(int id) {
        Task task = super.removeTaskById(id);
        save();
        return task;
    }

    @Override
    public Epic removeEpicById(int id) {
        Epic epic = super.removeEpicById(id);
        save();
        return epic;
    }

    @Override
    public Subtask removeSubtaskById(int id) {
        Subtask subtask = super.removeSubtaskById(id);
        save();
        return subtask;
    }

    public static FileBackedTaskManager loadFromFile(Path file) throws ManagerLoadException {
        try (BufferedReader br = new BufferedReader(new FileReader(file.toString(), UTF_8))) {
            FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);
            int maxId = 0;

            br.readLine();
            while (br.ready()) {
                String line = br.readLine();
                String[] lineElements = line.split(",");

                int taskId = Integer.parseInt(lineElements[0]);
                TaskType taskType = TaskType.valueOf(lineElements[1]);

                if (taskId > maxId) {
                    maxId = taskId;
                }

                switch (taskType) {
                    case TaskType.TASK:
                        fileBackedTaskManager.tasks.put(taskId, TaskConverter.taskFromCsvString(line));
                        break;
                    case TaskType.EPIC:
                        fileBackedTaskManager.epics.put(taskId, TaskConverter.epicFromCsvString(line));
                        break;
                    case TaskType.SUBTASK:
                        int epicId = Integer.parseInt(lineElements[5]);
                        fileBackedTaskManager.epics.get(epicId).getSubtasksIds().add(taskId);
                        fileBackedTaskManager.subtasks.put(taskId, TaskConverter.subtaskFromCsvString(line));
                        break;
                }
            }

            if (maxId > FileBackedTaskManager.tasksCount) {
                FileBackedTaskManager.tasksCount = maxId;
            }

            return fileBackedTaskManager;
        } catch (IndexOutOfBoundsException | IllegalArgumentException exception) {
            System.out.printf("[ERROR] Неверный формат файла.\nТекст ошибки: %s.\n", exception.getMessage());
            return new FileBackedTaskManager(file);
        } catch (IOException exception) {
            throw new ManagerLoadException(exception.getMessage());
        }
    }

    private void save() throws ManagerSaveException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileStorage.toString(), UTF_8))) {
            List<Task> allTasks = getAllTasks();
            List<Epic> allEpics = getAllEpics();
            List<Subtask> allSubtasks = getAllSubtasks();

            bw.write(FILE_FORMAT);

            for (Task task : allTasks) {
                bw.write(String.format("\n%s", TaskConverter.taskToCsvString(task)));
            }
            for (Epic epic : allEpics) {
                bw.write(String.format("\n%s", TaskConverter.epicToCsvString(epic)));
            }
            for (Subtask subtask : allSubtasks) {
                bw.write(String.format("\n%s", TaskConverter.subtaskToCsvString(subtask)));
            }
        } catch (IOException exception) {
            throw new ManagerSaveException(exception.getMessage());
        }
    }
}