package service;

import exception.ManagerLoadException;
import exception.ManagerSaveException;
import exception.ManagerTaskTypeException;
import model.Epic;
import model.Subtask;
import model.Task;

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;

public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager {
    private final Path fileStorage;

    public FileBackedTaskManager(Path fileStorage) {
        this.fileStorage = fileStorage;
    }

    public FileBackedTaskManager(List<Task> tasks, List<Epic> epics, List<Subtask> subtasks, Path fileStorage) {
        super(tasks, epics, subtasks);
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
            List<Task> tasks = new ArrayList<>();
            List<Epic> epics = new ArrayList<>();
            List<Subtask> subtasks = new ArrayList<>();

            br.readLine();
            while (br.ready()) {
                String line = br.readLine();
                String[] lineElements = line.split(",");
                String taskType = lineElements[1];

                switch (taskType) {
                    case "TASK":
                        tasks.add(Task.fromString(line));
                        break;
                    case "EPIC":
                        epics.add(Epic.fromString(line));
                        break;
                    case "SUBTASK":
                        subtasks.add(Subtask.fromString(line));
                        break;
                    default:
                        throw new ManagerTaskTypeException(String.format("Неизвестный тип задачи - %s", taskType));
                }
            }

            return new FileBackedTaskManager(tasks, epics, subtasks, file);
        } catch (IndexOutOfBoundsException | IllegalArgumentException | ManagerTaskTypeException exception) {
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

            bw.write("id,type,title,status,description,epicId");

            for (Task task : allTasks) {
                bw.write(String.format("\n%s", task.toString()));
            }
            for (Epic epic : allEpics) {
                bw.write(String.format("\n%s", epic.toString()));
            }
            for (Subtask subtask : allSubtasks) {
                bw.write(String.format("\n%s", subtask.toString()));
            }
        } catch (IOException exception) {
            throw new ManagerSaveException(exception.getMessage());
        }
    }
}