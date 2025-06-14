package org.OWA;

import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        CustomThreadFactory threadFactory = new CustomThreadFactory();
        RejectedExecutionHandler rejectedHandler = new DefaultRejectedExecutionHandler();
        CustomThreadPool pool = new CustomThreadPool(2, 4, 5, TimeUnit.SECONDS, 5, 1, threadFactory, rejectedHandler);

        // Отправка задач
        for (int i = 0; i < 15; i++) { // Отправляем больше задач, чем может обработать очередь
            final int taskId = i;
            pool.execute(() -> {
                System.out.println("[Task] Starting task " + taskId + " in " + Thread.currentThread().getName());
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                System.out.println("[Task] Completed task " + taskId);
            });
        }

        // Даем время для выполнения задач
        Thread.sleep(3000);

        // Вызов shutdown
        pool.shutdown();

        // Ожидание завершения всех задач
        Thread.sleep(5000);
        System.out.println("[Main] All tasks completed, pool shut down.");
    }
}