package org.dhwpcs.webchat.server.task;

public abstract class AbstractStatefulTask<T extends TaskState<T>>
        extends AbstractTask implements StatefulTask<T> {

    private T state;

    protected AbstractStatefulTask( T initial) {
        state = initial;
    }

    @Override
    public T getState() {
        return state;
    }

    protected void setState(T state) {
        this.state = state;
    }
}
