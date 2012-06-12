package com.trebogeer.jhcb;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author dimav
 *         Date: 6/6/12
 *         Time: 1:54 PM
 */
public abstract class Benchmark {


    public static final int port = Integer.getInteger("jhcb.port", 9998);
    public static final String host = System.getProperty("jhcb.host", "localhost");
    public static final int max_connections_per_host = Integer.getInteger("jhcb.max.connections", 100);

    protected static final int WARMUP_REQUESTS = 1000;

    protected static final String URI_PREFIX = "/jhcb-web-1.0/a/b/c/ping/";

    protected final int threads;
    protected final int requestsPerThreadPerBatch;
    protected final int batches;
    protected final String url;
    protected int warmupRequests;

    protected ExecutorService executor;

    public Benchmark(int threads, int requestsPerThreadPerBatch, int batches, String url) {
        if (threads < 1) {
            throw new IllegalArgumentException("Thread count must be > 1");
        }

        this.threads = threads;
        this.requestsPerThreadPerBatch = requestsPerThreadPerBatch;
        this.batches = batches;
        this.url = url;

        this.warmupRequests = WARMUP_REQUESTS;
    }

    public BenchmarkResult doBenchmark() {
        System.err.println("Setting up " + this.getClass().getSimpleName() + "...");
        this.setup();
        System.err.println("Beginning warmup stage...");
        this.warmup();
        System.err.println("Warmup complete, running " + this.batches + " batches...");

        List<BatchResult> results = new ArrayList<BatchResult>(this.batches);
        for (int i = 0; i < this.batches; i++) {
            BatchResult result = this.runBatch();
            results.add(result);
            System.err.println("Batch " + i + " finished: " + result);
        }

        System.err.println("Test finished, shutting down and calculating results...");
        this.tearDown();
        return new BenchmarkResult(this.threads, this.batches, results, getName());
    }

    protected void setup() {
        this.executor = Executors.newFixedThreadPool(this.threads);
    }

    protected void tearDown() {
        this.executor.shutdown();
    }

    private String getName() {
        return this.getClass().getSimpleName();
    }

    private void warmup() {
        Random r = new Random();
        for (int i = 0; i < warmupRequests; i++) {
            executeRequest(r);
        }
    }


    private BatchResult runBatch() {
        final CountDownLatch latch = new CountDownLatch(this.threads);
        final Vector<ThreadResult> threadResults = new Vector<ThreadResult>(this.threads);

        long batchStart = System.nanoTime();
        for (int i = 0; i < this.threads; i++) {
            this.executor.submit(new Runnable() {

                @Override
                public void run() {
                    Random p = new Random();
                    int successful = 0;
                    long start = System.nanoTime();
                    for (int i = 0; i < requestsPerThreadPerBatch; i++) {
                        if (executeRequest(p)) {
                            successful++;
                        }
                    }

                    long totalTime = System.nanoTime() - start;
                    threadResults.add(new ThreadResult(requestsPerThreadPerBatch, successful, totalTime));
                    latch.countDown();
                }
            });
        }

        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.interrupted();
        }
        long batchTotalTime = System.nanoTime() - batchStart;

        return new BatchResult(threadResults, batchTotalTime);
    }

    protected abstract boolean executeRequest(final Random r);
}
