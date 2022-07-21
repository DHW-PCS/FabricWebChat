package org.dhwpcs.webchat.server.task;

public interface Task {
    boolean isDone();
    void cancel();
    boolean isSuccess();
    void onRegister(TaskHandler handler);
}
