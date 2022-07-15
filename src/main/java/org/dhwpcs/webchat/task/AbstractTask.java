package org.dhwpcs.webchat.task;

import org.dhwpcs.webchat.session.ConnectionInvoker;

public abstract class AbstractTask implements Task {

    protected boolean done = false;
    protected boolean success = false;
    protected boolean cancelled = false;
    protected TaskHandler handler;

    @Override
    public boolean isDone() {
        return done;
    }

    protected void success() {
        this.done = true;
        this.success = true;
    }

    protected void failed() {
        this.done = true;
    }

    @Override
    public void onRegister(TaskHandler handler) {
        this.handler = handler;
    }

    @Override
    public boolean isSuccess() {
        return success;
    }

    @Override
    public void cancel() {
        cancelled = true;
    }
}
