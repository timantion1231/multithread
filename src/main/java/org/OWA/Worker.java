package org.OWA;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class Worker implements Runnable {
    private static final Logger LOGGER = Logger.getLogger(Worker.class.getName());
    private final TaskQueue queue;  // Updated to TaskQueue
    private final CustomThreadPool pool;
    private volatile boolean isRunning = true;
    private volatile boolean idle = true;
    private Thread thread;

    public Worker(TaskQueue queue, CustomThreadPool pool) {  // Updated to accept TaskQueue
        this.queue = queue;
        this.pool = pool;
    }

    public void setThread(Thread thread) {
        this.thread = thread;
    }

    public Thread getThread() {
        return thread;
    }

    @Override
    public void run() {
        thread = Thread.currentThread();
        while (isRunning && !pool.isShutdown()) {
            try {
                Runnable task = queue.poll(pool.getKeepAliveTime(), pool.getTimeUnit());
                if (task != null) {
                    idle = false;
                    LOGGER.info("[Worker] " + thread.getName() + " executes " + task.toString());
                    task.run();
                    idle = true;
                } else if (pool.getWorkerCount() > pool.getCorePoolSize()) {
                    LOGGER.info("[Worker] " + thread.getName() + " idle timeout, stopping.");
                    isRunning = false;
                }
            } catch (InterruptedException e) {
                isRunning = false;
                LOGGER.info("[Worker] " + thread.getName() + " interrupted.");
            }
        }
        LOGGER.info("[Worker] " + thread.getName() + " terminated.");
    }

    public void stopWorker() {
        isRunning = false;
    }

    public boolean isIdle() {
        return idle;
    }

    public List<Runnable> drainQueue() {
        return queue.drain();
    }
}
