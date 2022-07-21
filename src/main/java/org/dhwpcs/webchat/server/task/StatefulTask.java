package org.dhwpcs.webchat.server.task;

public interface StatefulTask<T extends TaskState> extends Task {
    T getState();
}
