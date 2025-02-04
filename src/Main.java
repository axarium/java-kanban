import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;
import service.TaskManager;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();

        Task task1 = new Task("Название задачи 1", "Описание задачи 1", TaskStatus.NEW);
        Task task2 = new Task("Название задачи 2", "Описание задачи 2", TaskStatus.IN_PROGRESS);
        taskManager.createTask(task1);
        taskManager.createTask(task2);

        Epic epic1 = new Epic("Название эпика 1", "Описание эпика 1");
        Epic epic2 = new Epic("Название эпика 2", "Описание эпика 2");
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);


        Subtask subtask1 = new Subtask(
                "Название подзадачи 1",
                "Описание подзадачи 1",
                TaskStatus.NEW,
                epic1.getId());
        Subtask subtask2 = new Subtask(
                "Название подзадачи 2",
                "Описание подзадачи 2",
                TaskStatus.IN_PROGRESS,
                epic1.getId());
        Subtask subtask3 = new Subtask(
                "Название подзадачи 3",
                "Описание подзадачи 3",
                TaskStatus.DONE,
                epic2.getId());
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        taskManager.createSubtask(subtask3);

        System.out.println(taskManager.getAllTasks());
        System.out.println(taskManager.getAllEpics());
        System.out.println(taskManager.getAllSubtasks());

        Task task3 = new Task("Название задачи 2", "Описание задачи 2", TaskStatus.DONE);
        task3.setId(task2.getId());
        taskManager.updateTask(task3);

        Epic epic3 = new Epic("Новое название эпика 2", "Новое название эпика 2");
        epic3.setId(epic2.getId());
        taskManager.updateEpic(epic3);

        Subtask subtask4 = new Subtask(
                "Название подзадачи 2",
                "Описание подзадачи 2",
                TaskStatus.NEW,
                epic1.getId());
        subtask4.setId(subtask2.getId());
        taskManager.updateSubtask(subtask4);

        System.out.println(taskManager.getAllTasks());
        System.out.println(taskManager.getAllEpics());
        System.out.println(taskManager.getAllSubtasks());

        taskManager.removeTaskById(task1.getId());
        taskManager.removeEpicById(epic3.getId());

        System.out.println(taskManager.getAllTasks());
        System.out.println(taskManager.getAllEpics());
        System.out.println(taskManager.getAllSubtasks());
    }
}