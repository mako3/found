package mako3.found.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import mako3.found.dao.TaskDao;
import mako3.found.entity.Task;

@Component
public class TaskService {

    @Autowired
    private TaskDao taskDao;

    public String registerTask(String registeredBy) {
        String taskId = UUID.randomUUID().toString();
        Task task = Task.builder()
                .taskId(taskId)
                .registeredBy(registeredBy)
                .build();
        taskDao.registerTask(task);
        return taskId;
    }

    public void updateInProgress(String taskId) {
        taskDao.updateInProgress(taskId);
    }

    public Task getStatus(String taskId) {
        return taskDao.getTask(taskId);
    }

    public void updateSuccess(String taskId) {
        taskDao.updateSuccess(taskId);
    }

    public void updateFailure(String taskId, String errorMessage) {
        taskDao.updateFailure(taskId, errorMessage);
    }

}
