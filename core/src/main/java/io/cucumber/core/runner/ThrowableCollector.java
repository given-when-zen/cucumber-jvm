package io.cucumber.core.runner;

import static io.cucumber.core.runner.TestAbortedExceptions.isTestAbortedException;
import static io.cucumber.core.exception.UnrecoverableExceptions.rethrowIfUnrecoverable;

final class ThrowableCollector {

    private final Object monitor = new Object();
    private Throwable throwable;

    void execute(Runnable runnable) {
        try {
            runnable.run();
        } catch (Throwable t) {
            rethrowIfUnrecoverable(t);
            add(t);
        }
    }

    private void add(Throwable throwable) {
        synchronized (monitor) {
            if (this.throwable == null) {
                this.throwable = throwable;
            } else if (isTestAbortedException(throwable) && !isTestAbortedException(this.throwable)) {
                throwable.addSuppressed(this.throwable);
                this.throwable = throwable;
            } else if (this.throwable != throwable) {
                this.throwable.addSuppressed(throwable);
            }
        }
    }

    Throwable getThrowable() {
        synchronized (monitor) {
            return throwable;
        }
    }

}
