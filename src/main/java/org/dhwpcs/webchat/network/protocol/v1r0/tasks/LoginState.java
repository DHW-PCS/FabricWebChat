package org.dhwpcs.webchat.network.protocol.v1r0.tasks;

import org.dhwpcs.webchat.task.TaskState;

public enum LoginState implements TaskState<LoginState> {
    INITIAL,
    ACCOUNT_SET,
    SUCCESS
}
