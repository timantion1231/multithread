package org.OWA;


import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

public class CustomThreadFactory implements ThreadFactory {
    private static final Logger LOGGER = Logger.getLogger(CustomThreadFactory.class.getName());
    private final AtomicInteger threadNumber = new AtomicInteger(1);
    private final String namePrefix = "MyPool-worker-";

    @Override
    public Thread newThread(Runnable r) {
        String threadName = namePrefix + threadNumber.getAndIncrement();
        LOGGER.info("[ThreadFactory] Creating new thread: " + threadName);
        Thread thread = new Thread(r, threadName);
        if (r instanceof Worker) {
            ((Worker) r).setThread(thread);
        }
        return thread;
    }
}