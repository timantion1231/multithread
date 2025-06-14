package org.OWA;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class TaskQueue {
    private final BlockingQueue<Runnable> queue;

    public TaskQueue(int queueSize) {
        this.queue = new LinkedBlockingQueue<>(queueSize);
    }

    public boolean offer(Runnable task) {
        return queue.offer(task);
    }

    public Runnable poll(long timeout, TimeUnit unit) throws InterruptedException {
        return queue.poll(timeout, unit);
    }

    public List<Runnable> drain() {
        List<Runnable> tasks = new ArrayList<>();
        queue.drainTo(tasks);
        return tasks;
    }
}
