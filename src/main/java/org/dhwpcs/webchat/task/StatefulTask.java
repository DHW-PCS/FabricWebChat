package org.dhwpcs.webchat.task;

public interface StatefulTask<T extends TaskState> extends Task {
    T getState();
}
