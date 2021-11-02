package io.denery;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.threadly.concurrent.*;
import org.threadly.concurrent.future.ListenableFuture;

import java.util.concurrent.*;

/**
 *
 */
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 3)
@Measurement(iterations = 3)
@State(Scope.Benchmark)
public class ThreadlySamples {
    @Param({"1", "24", "128"})
    int amountOfTasks;
    private static final int tts = Runtime.getRuntime().availableProcessors() * 2;

    public static void doWork() {
        Blackhole.consumeCPU(4096);
    }

    public static void runInThreadPool(int amountOfTasks, Blackhole bh, ExecutorService threadPool)
            throws InterruptedException, ExecutionException {
        Future<?>[] futures = new Future[amountOfTasks];
        for (int i = 0; i < amountOfTasks; i++) {
            futures[i] = threadPool.submit(ThreadlySamples::doWork);
        }
        for (Future<?> future : futures) {
            bh.consume(future.get());
        }

        threadPool.shutdownNow();
        threadPool.awaitTermination(10, TimeUnit.SECONDS);
    }

    public static void runInPriorityScheduler(int amountOfTasks, Blackhole bh, PriorityScheduler threadPool, TaskPriority pr)
            throws InterruptedException, ExecutionException {
        ListenableFuture<?>[] futures = new ListenableFuture[amountOfTasks];
        for (int i = 0; i < amountOfTasks; i++) {
            futures[i] = threadPool.submit(ThreadlySamples::doWork, pr);
        }
        for (ListenableFuture<?> future : futures) {
            bh.consume(future.get());
        }

        threadPool.shutdownNow();
        threadPool.awaitTermination(10000);
    }

    @Benchmark
    public void noThreading() {
        for (int i = 0; i < amountOfTasks; i++) {
            doWork();
        }
    }

    @Benchmark
    public void fixedThreadPool(Blackhole bh) throws ExecutionException, InterruptedException {
        runInThreadPool(amountOfTasks, bh, Executors.newFixedThreadPool(tts));
    }

    @Benchmark
    public void prioritySchedulerLow(Blackhole bh) throws ExecutionException, InterruptedException {
        runInPriorityScheduler(amountOfTasks, bh, new PriorityScheduler(tts), TaskPriority.Low);
    }

    @Benchmark
    public void prioritySchedulerHigh(Blackhole bh) throws ExecutionException, InterruptedException {
        runInPriorityScheduler(amountOfTasks, bh, new PriorityScheduler(tts), TaskPriority.High);
    }
}
