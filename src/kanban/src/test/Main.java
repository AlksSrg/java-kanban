package kanban.src.test;


import kanban.src.tasks.EpicTask;
import kanban.src.tasks.SubTask;
import kanban.src.tasks.Task;
import kanban.src.tools.InMemoryTaskManager;
import kanban.src.tools.TaskStatus;


import java.util.ArrayList;


public class Main { // Тесты

    public static void main(String[] args) {
        InMemoryTaskManager manager = new InMemoryTaskManager();

        // тест-комплект:
        // 3 обычные Task
        Task task1 = new Task("Погулять с собакой",
                "Погулять с собакой до 18.00",
                TaskStatus.NEW);
        manager.saveTask(task1);
        Task task2 = new Task("Заехать в магазин за продуктами",
                "Заехать после работы за продуктами по списку",
                TaskStatus.DONE);
        manager.saveTask(task2);
        Task task3 = new Task("Прибраться в доме до прихода гостей",
                "Запустить робота-пылесоса,помыть посуду,накрыть на стол",
                TaskStatus.IN_PROGRESS);
        manager.saveTask(task3);
        // Epic 1
        EpicTask epic1 = new EpicTask("Помочь ребенку с подготовкой к ОГЭ",
                "Прорешать несколько вариантов по Физике", TaskStatus.NEW);
        manager.saveEpicTask(epic1);
        // 2 SubTask Epic 1
        SubTask subtask1 = new SubTask(epic1.getTaskId(), "Посмотреть новые серии любимого сериала в оригинальной озвучке",
                "Если будет сложно включить английские субтитры", TaskStatus.NEW);
        manager.saveSubTask(subtask1);

        SubTask subtask2 = new SubTask(epic1.getTaskId(), "Выучить английский",
                "Изучить возможности изучения иностранного языка", TaskStatus.NEW);
        manager.saveSubTask(subtask2);
        // Epic 2
        EpicTask epic2 = new EpicTask("Пора обновлять компьютер!",
                "Посмотреть комплектующие для своего нового ПК", TaskStatus.NEW);
        manager.saveEpicTask(epic2);

        // 2 SubTask Epic 2
        SubTask subtask3 = new SubTask(epic2.getTaskId(), "Запланировать отпуск",
                "Изучить возможные маршруты для отпуска", TaskStatus.DONE);
        manager.saveSubTask(subtask3);
        SubTask subtask4 = new SubTask(epic2.getTaskId(), "Бросить курить",
                "Откладывать стоимость пачки сигарет в копилку <На отпуск>", TaskStatus.IN_PROGRESS);
        manager.saveSubTask(subtask4);

        // Epic 3
        EpicTask epic3 = new EpicTask("Зарядить фотоаппарат",
                "Выбраться в город для отработки навыков", TaskStatus.NEW);
        manager.saveEpicTask(epic3);

        // 3 SubTask Epic 3
        SubTask subtask5 = new SubTask(epic3.getTaskId(), "Заняться спортом",
                "Изучить условия у тренажерных залов находящихся рядом с домом",
                TaskStatus.DONE);
        manager.saveSubTask(subtask5);
        SubTask subtask6 = new SubTask(epic3.getTaskId(), "Найти тренера",
                "Поспрашивать у друзей, занимающихся в зале, контакты хороших тренеров",
                TaskStatus.DONE);
        manager.saveSubTask(subtask6);
        SubTask subtask7 = new SubTask(epic3.getTaskId(), "Выбраться с женой в кафе",
                "Выбрать место - или новое, или что то из уже посещенных ранее",
                TaskStatus.DONE);
        manager.saveSubTask(subtask7);


        //Проверка внесенных Task'ов
        System.out.println("Все Task:");
        ArrayList<Task> allTasks = manager.getAllTasks();
        for (int i = 0; i < allTasks.size(); i++) {
            System.out.println("Внесен Task № " + (i + 1) + " " + allTasks.get(i));
        }

        System.out.println("Все SubTask:");
        ArrayList<SubTask> allSubtasks = manager.getAllSubTasks();
        for (int i = 0; i < allSubtasks.size(); i++) {
            System.out.println("Внесен SubTask № " + (i + 1) + " " + allSubtasks.get(i));
        }

        System.out.println("Все EpicTask:");
        ArrayList<EpicTask> allEpics = manager.getAllEpics();
        for (int i = 0; i < allEpics.size(); i++) {
            System.out.println("Внесен EpicTask № " + (i + 1) + " " + allEpics.get(i));
        }


        // проверка на получение Tasks
        System.out.println("Получение Tasks по идентификатору " + manager.getTaskById(1));
        System.out.println("Получение SubTasks по идентификатору " + manager.getSubTaskById(5));
        System.out.println("Получение EpicTasks по идентификатору " + manager.getEpicById(4));


//        проверка удаления разных Tasks
        manager.deleteTask(task1.getTaskId());
        System.out.println("Task №1 " + manager.getTaskById(task1.getTaskId()));

        manager.deleteEpicTask(epic1.getTaskId());
        System.out.println("EpicTask №1 " + manager.getEpicById(epic1.getTaskId()));

        manager.deleteSubTask(epic1.getTaskId());
        System.out.println("SubTask № 1 " + manager.getSubTaskById(epic1.getTaskId()));

        //Проверка на изменение статуса прогресса
        System.out.println("Статус SubTask № 1 " + subtask1.getStatus());
        subtask1.setStatus(TaskStatus.DONE);
        manager.updateSubTask(subtask1);
        System.out.println("Статус SubTask № 1 " + subtask1.getStatus());


    }


}