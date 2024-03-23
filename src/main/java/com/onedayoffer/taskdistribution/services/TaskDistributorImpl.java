package com.onedayoffer.taskdistribution.services;

import com.onedayoffer.taskdistribution.DTO.EmployeeDTO;
import com.onedayoffer.taskdistribution.DTO.TaskDTO;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Component
public class TaskDistributorImpl implements TaskDistributor {

    // эта реализация не гарантирует самого быстрого выполнения всех приоритетных задач, но потребует меньше
    // ресурсов по времени
    // т.к. для каждой новой задачи сотрудники не будут сортироваться, а просто линейно перебираться
    @Override
    public void distribute(List<EmployeeDTO> employees, List<TaskDTO> tasks) {
        // сортируем задачи по приоритету
        tasks.sort(Comparator.comparingInt(task -> task.getPriority()));
        var empIterator = employees.iterator();
        var employee = empIterator.hasNext()? empIterator.next() : null ;
        // пройдемся по всем задачам
        for (int i = 0; i < tasks.size(); i++) {
            var task = tasks.get(i);
            boolean assigned = false;
            boolean haveFreeEmployee = true;
            var firstEmployee = employee;
            // подбираем сотрудника для задачи: у него должно быть достаточно свободного времени
            // (чтобы общая загруженность была не более 7 часов)
            // пройдемся по всем сотрудникам, возможно у кого-то есть достаточно времени
            while (employee!=null && !assigned && haveFreeEmployee) {
                int timeLeft = 420 - employee.getTotalLeadTime();
                if (timeLeft > 0) {
                    if (task.getLeadTime() <= timeLeft) {
                        employee.getTasks().add(task);
                        assigned = true;
                    } else {
                        employee = empIterator.hasNext() ? empIterator.next() : null;
                    }
                } else {
                    employee = empIterator.hasNext() ? empIterator.next() : null;
                }
                if (employee == null) {
                    empIterator = employees.iterator();
                    employee = empIterator.next();
                }
                //мы проверили всех сотрудников, свободных сотрудников для этой задачи нет
                if (employee == firstEmployee) {
                    haveFreeEmployee = false;
                }
            }
        }

    }
}
