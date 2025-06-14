package org.OWA;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

public class CustomThreadPool implements CustomExecutor {
    private static final Logger LOGGER = Logger.getLogger(CustomThreadPool.class.getName());
    private final int corePoolSize;
    private final int maxPoolSize;
    private final long keepAliveTime;
    private final TimeUnit timeUnit;
    private final int queueSize;
    private final int minSpareThreads;
    private final List<Worker> workers;
    private final List<TaskQueue> queues;
    private final CustomThreadFactory threadFactory;
    private final RejectedExecutionHandler rejectedHandler;
    private final AtomicInteger taskCounter = new AtomicInteger(0);
    private volatile boolean isShutdown = false;

    public CustomThreadPool(int corePoolSize, int maxPoolSize, long keepAliveTime, TimeUnit timeUnit,
                            int queueSize, int minSpareThreads, CustomThreadFactory threadFactory,
                            RejectedExecutionHandler rejectedHandler) {
        this.corePoolSize = corePoolSize;
        this.maxPoolSize = maxPoolSize;
        this.keepAliveTime = keepAliveTime;
        this.timeUnit = timeUnit;
        this.queueSize = queueSize;
        this.minSpareThreads = minSpareThreads;
        this.threadFactory = threadFactory;
        this.rejectedHandler = rejectedHandler;
        this.workers = new ArrayList<>();
        this.queues = new ArrayList<>();
        initializeWorkers();
    }

    private void initializeWorkers() {
        for (int i = 0; i < corePoolSize; i++) {
            TaskQueue queue = new TaskQueue(queueSize);
            queues.add(queue);
            Worker worker = new Worker(queue, this);
            workers.add(worker);
            threadFactory.newThread(worker).start();
        }
    }

    @Override
    public void execute(Runnable command) {
        if (isShutdown) {
            rejectedHandler.rejectedExecution(command, this);
            return;
        }
        int queueIndex = taskCounter.getAndIncrement() % queues.size();
        TaskQueue queue = queues.get(queueIndex);
        LOGGER.info("[Pool] Task accepted into queue #" + queueIndex + ": " + command.toString());
        if (!queue.offer(command)) {
            rejectedHandler.rejectedExecution(command, this);
        }
    }

    @Override
    public <T> Future<T> submit(Callable<T> callable) {
        FutureTask<T> future = new FutureTask<>(callable);
        execute(future);
        return future;
    }

    @Override
    public void shutdown() {
        isShutdown = true;
        LOGGER.info("[Pool] Initiating shutdown...");
        for (Worker worker : workers) {
            worker.stopWorker();
        }
    }

    @Override
    public void shutdownNow() {
        isShutdown = true;
        LOGGER.info("[Pool] Initiating shutdownNow...");
        for (Worker worker : workers) {
            worker.stopWorker();
            Thread thread = worker.getThread();
            if (thread != null) thread.interrupt();
        }
    }

    public int getWorkerCount() {
        return workers.size();
    }

    public long getKeepAliveTime() {
        return keepAliveTime;
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    public int getCorePoolSize() {
        return corePoolSize;
    }

    public boolean isShutdown() {
        return isShutdown;
    }
}