package org.dhwpcs.webchat.task;

public interface Task {
    boolean isDone();
    void cancel();
    boolean isSuccess();
    void onRegister(TaskHandler handler);
}
