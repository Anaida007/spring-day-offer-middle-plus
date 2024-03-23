package com.onedayoffer.taskdistribution.services;

import com.onedayoffer.taskdistribution.DTO.EmployeeDTO;
import com.onedayoffer.taskdistribution.DTO.TaskDTO;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Component
public class TaskDistributorImpl2 implements TaskDistributor {

    // эта реализация позволит выполнить более приоритетные задачи быстрее, но потребует больше ресурсов по времени
    // т.к. для каждой новой задачи будут сортироваться сотрудники по наличию свободного времени
    // алгоритм можно ускорить, если не сортировать всех сотрудников каждый раз, но перемещать сотрудника согласно
    // условию сортировки (по наименьшей занятости) внутри списка
    // (создавать отдельный список для метода либо не Immutable коллекция)
    @Override
    public void distribute(List<EmployeeDTO> employees, List<TaskDTO> tasks) {
        // сортируем задачи по приоритету
        tasks.sort(Comparator.comparingInt(task -> task.getPriority()));
        var empIterator = employees.iterator();
        var employee = empIterator.hasNext()? empIterator.next() : null ;
        // пройдемся по всем задачам
        for (int i = 0; i < tasks.size(); i++) {
            var task = tasks.get(i);
            // подбираем сотрудника для задачи: у него должно быть достаточно свободного времени
            // (чтобы общая загруженность была не более 7 часов)
            // пройдемся по всем сотрудникам, возможно у кого-то есть достаточно времени

            // нам нужен самый не загруженный работник
            employee = employees.stream()
                    .min(Comparator.comparingInt(empl->empl.getTotalLeadTime()))
                    .get();
            if (employee!=null) {
                int timeLeft = 420 - employee.getTotalLeadTime();
                // если же у самого свободного сотрудника нет времени,
                // то ни у кого нет времени, можно перейти к следующей задаче
                if (task.getLeadTime() <= timeLeft) {
                    employee.getTasks().add(task);
                }
            }
        }

    }
}
