package mako3.found.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import mako3.found.entity.Task;

@Component
public class TaskDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void registerTask(Task task) {
        jdbcTemplate.update(
                "insert into found_tasks (task_id, registered_by, registered_at, task_status) values (?, ?, current_timestamp, 0)",
                task.getTaskId(), task.getRegisteredBy());
    }

    public void updateInProgress(String taskId) {
        jdbcTemplate.update("update found_tasks set task_status = 1 where task_id = ?", taskId);
    }

    public Task getTask(String taskId) {
        return jdbcTemplate.queryForObject("select * from found_tasks where task_id = ?;",
                new JdbcRowMapper(), taskId);
    }

    public void updateSuccess(String taskId) {
        jdbcTemplate.update(
                "update found_tasks set task_status = 2, finished_at = current_timestamp  where task_id = ?",
                taskId);
    }

    public void updateFailure(String taskId, String errorMessage) {
        jdbcTemplate.update(
                "update found_tasks set task_status = 9, error_message = ?, finished_at = current_timestamp where task_id = ?",
                errorMessage, taskId);
    }

    public class JdbcRowMapper implements RowMapper<Task> {

        @Override
        @Nullable
        public Task mapRow(@SuppressWarnings("null") ResultSet rs, int n) throws SQLException {
            return Task.builder()
                    .taskId(rs.getString("task_id"))
                    .taskStatus(rs.getInt("task_status"))
                    .registeredBy(rs.getString("registered_by"))
                    .registeredAt(rs.getObject("registered_at", LocalDateTime.class))
                    .finishedAt(rs.getObject("finished_at", LocalDateTime.class))
                    .errorMessage(rs.getString("error_message"))
                    .build();
        }

    }

}
