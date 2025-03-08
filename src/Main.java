import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;
import service.Managers;
import service.TaskManager;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();
        Task firstTask = new Task("Title", "Description", TaskStatus.NEW);
        Task secondTask = new Task("NewTitle", "NewDescription", TaskStatus.IN_PROGRESS);
        Epic firstEpic = new Epic("Title", "Description");
        Epic secondEpic = new Epic("NewTitle", "NewDescription");
        taskManager.createTask(firstTask);
        taskManager.createTask(secondTask);
        taskManager.createEpic(firstEpic);
        taskManager.createEpic(secondEpic);
        Subtask firstSubtask = new Subtask("T", "D", TaskStatus.NEW, firstEpic.getId());
        Subtask secondSubtask = new Subtask("NT", "ND", TaskStatus.IN_PROGRESS, firstEpic.getId());
        Subtask thirdSubtask = new Subtask("NNT", "NND", TaskStatus.DONE, firstEpic.getId());
        taskManager.createSubtask(firstSubtask);
        taskManager.createSubtask(secondSubtask);
        taskManager.createSubtask(thirdSubtask);

        taskManager.getTaskById(firstTask.getId());
        taskManager.getTaskById(secondTask.getId());
        taskManager.getEpicById(firstEpic.getId());
        taskManager.getEpicById(secondEpic.getId());
        taskManager.getSubtaskById(firstSubtask.getId());
        taskManager.getSubtaskById(secondSubtask.getId());
        taskManager.getSubtaskById(thirdSubtask.getId());
        System.out.println(taskManager.getHistory());

        taskManager.getTaskById(secondTask.getId());
        taskManager.getEpicById(secondEpic.getId());
        taskManager.getSubtaskById(secondSubtask.getId());
        taskManager.getSubtaskById(thirdSubtask.getId());
        System.out.println(taskManager.getHistory());

        taskManager.removeTaskById(secondTask.getId());
        taskManager.removeEpicById(secondEpic.getId());
        taskManager.removeSubtaskById(thirdSubtask.getId());
        System.out.println(taskManager.getHistory());

        taskManager.removeEpicById(firstEpic.getId());
        System.out.println(taskManager.getHistory());
    }
}