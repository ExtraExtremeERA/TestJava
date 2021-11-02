package io.denery;

import org.junit.jupiter.api.Test;
import org.threadly.concurrent.PriorityScheduler;
import org.threadly.concurrent.TaskPriority;
import org.threadly.concurrent.future.ListenableFuture;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

class PrioritySchedulerTest {
    private static final int tts = Runtime.getRuntime().availableProcessors() * 2;
    private final int amountOfTasks = 24;
    private ExecutorService service = Executors.newFixedThreadPool(tts);
    private static String[] strs = new String[8];

    @Test
    void test() throws ExecutionException, InterruptedException {
        setup();

        Future[] futures = new Future[amountOfTasks];
        for (int i = 0; i < amountOfTasks; i++) {
            futures[i] = service.submit(() -> {
                System.out.println(doWork()); });
        }
        for (Future future : futures) {
            future.get();
        }

        service.shutdown();
    }

    @Test
    void test1() {
        setup();

    }

    public void setup() {
        for (int i = 0; i < strs.length; i++) {
            strs[i] = String.valueOf(Math.random());
        }
    }

    public String doWork() {
        StringBuilder conc = new StringBuilder();
        for (String str : strs) {
            conc.append(str);
        }
        return conc.toString();
    }
}
