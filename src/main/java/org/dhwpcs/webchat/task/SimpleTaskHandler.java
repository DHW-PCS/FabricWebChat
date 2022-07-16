package org.dhwpcs.webchat.task;

import java.util.*;
import java.util.stream.Collectors;

public class SimpleTaskHandler implements TaskHandler {

    private Map<Object, Task> entries = new HashMap<>();

    @Override
    public boolean registerSingleton(Task task) {
        if(entries.containsKey(task.getClass())) {
            return false;
        }
        entries.put(task.getClass(), task);
        return true;
    }

    @Override
    public <T extends Task> Optional<T> querySingleton(Class<T> tsk) {
        return Optional.ofNullable((T)entries.get(tsk));
    }

    @Override
    public boolean register(UUID uid, Task tsk) {
        return entries.putIfAbsent(uid, tsk) == null;
    }

    @Override
    public UUID register(Task tsk) {
        UUID uid;
        do {
            uid = UUID.randomUUID();
        } while(!register(uid, tsk));
        return uid;
    }

    @Override
    public Optional<Task> query(UUID uid) {
        return Optional.ofNullable(entries.get(uid));
    }

    @Override
    public <T extends Task> Set<T> queryAll(Class<T> tsk) {
        return entries.values().stream().filter(tsk::isInstance).map(tsk::cast).collect(Collectors.toSet());
    }

    @Override
    public void shutdown() {
        entries.values().forEach(Task::cancel);
    }

    @Override
    public void tick() {
        for(Object key : entries.keySet()) {
            if(entries.get(key).isDone()) {
                entries.remove(key);
            }
        }
    }
}
