package org.OWA;

public interface RejectedExecutionHandler {
    void rejectedExecution(Runnable r, CustomThreadPool executor);
}