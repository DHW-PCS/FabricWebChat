package org.dhwpcs.webchat.task;

public interface TaskState<T extends TaskState<T>> extends Comparable<T> {
    int ordinal();
}
