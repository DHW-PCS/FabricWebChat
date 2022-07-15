package org.dhwpcs.webchat.task;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface TaskHandler {
    boolean registerSingleton(Task task);
    <T extends Task> Optional<T> querySingleton(Class<T> tsk);

    boolean register(UUID uid, Task tsk);
    UUID register(Task tsk);
    Optional<Task> query(UUID uid);
    <T extends Task> Set<T> queryAll(Class<T> tsk);

    void shutdown();
}
