package mako3.found.entity;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Task {

    public enum Status {
        REGISTERED(0), IN_PROGRESS(1), SUCCEEDED(2), FAILED(9);

        private int value;

        Status(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public static Status valueOf(int value) {
            for (Status status : Status.values()) {
                if (status.getValue() == value) {
                    return status;
                }
            }
            throw new IllegalArgumentException("Invalid value: " + value);
        }

    }

    private String taskId;

    // 0: registered, 1: in progress, 2: succeeded, 9:failed
    private int taskStatus;

    private LocalDateTime registeredAt;

    private String registeredBy;

    private LocalDateTime finishedAt;

    private String errorMessage;

    public Status getStatus() {
        return Status.valueOf(taskStatus);
    }

}
