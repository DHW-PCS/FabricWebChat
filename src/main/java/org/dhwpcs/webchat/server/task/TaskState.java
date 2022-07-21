package org.dhwpcs.webchat.server.task;

public interface TaskState<T extends TaskState<T>> extends Comparable<T> {
    int ordinal();
}
