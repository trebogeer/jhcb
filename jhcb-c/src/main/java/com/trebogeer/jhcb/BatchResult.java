package com.trebogeer.jhcb;

import java.util.Collection;

/**
 * @author dimav
 *         Date: 6/6/12
 *         Time: 1:57 PM
 */
public class BatchResult {

    private final int batchTargetRequests;
    private final int batchSuccessfulRequests;
    private final long totalTimeForAllThreads;
    private final float averageTimePerThread;
    private final float averageTimePerRequest;
    private final long totalBatchTime;
    private final long retries;

    public BatchResult(Collection<ThreadResult> threadResults, long totalTime) {
        int targetRequests = 0;
        int successfulRequests = 0;
        long totalTimeForAllThreads = 0;
        long retries = 0;

        for (ThreadResult threadResult : threadResults) {
            targetRequests += threadResult.getTargetRequests();
            successfulRequests += threadResult.getSuccessfulRequests();
            totalTimeForAllThreads += threadResult.getTotalTime();
            retries += threadResult.getReties();
        }

        this.batchTargetRequests = targetRequests;
        this.batchSuccessfulRequests = successfulRequests;
        this.totalTimeForAllThreads = totalTimeForAllThreads;

        this.averageTimePerThread = totalTimeForAllThreads / (float) threadResults.size();
        // averageTimePerThread because of the overhead of launching threads - averageTimePerThread
        // is the average time that each thread spent *after* being launched while totalTime is the time it took to
        // launch all threads and wait for them to stop.
        // This approach is utopical since it assumes that all threads will be launched and running at the same time.
        //this.averageTimePerRequest = this.averageTimePerThread / (float) successfulRequests;

        // This variation assumes that thread launching overhead is not significant. It's more realisting even though
        // it may be affected by thread launching overhead.
        this.averageTimePerRequest = totalTime / (float) successfulRequests;
        this.totalBatchTime = totalTime;
        this.retries = retries;
    }

    public int getBatchTargetRequests() {
        return batchTargetRequests;
    }

    public int getBatchSuccessfulRequests() {
        return batchSuccessfulRequests;
    }

    public long getTotalTimeForAllThreads() {
        return totalTimeForAllThreads;
    }

    public float getAverageTimePerThread() {
        return averageTimePerThread;
    }

    public float getAverageTimePerRequest() {
        return averageTimePerRequest;
    }

    public long getTotalBatchTime() {
        return totalBatchTime;
    }

    public long getRetries() {
        return retries;
    }

    @Override
    public String toString() {
        return "BatchResult{" +
                "requestsPerSecond=" + BenchmarkResult.decimal(1000000000f / this.averageTimePerRequest) +
                ", batchTargetRequests=" + batchTargetRequests +
                ", batchSuccessfulRequests=" + batchSuccessfulRequests +
                ", batchRetriedRequests=" + retries +
                ", totalTimeForAllThreads=" + BenchmarkResult.decimal(totalTimeForAllThreads / 1000000f) +
                "ms, averageTimePerThread=" + BenchmarkResult.decimal(averageTimePerThread / 1000000f) +
                "ms, averageTimePerRequest=" + BenchmarkResult.decimal(averageTimePerRequest / 1000000f) +
                "ms, totalBatchTime=" + BenchmarkResult.decimal(totalBatchTime / 1000000f) +
                "ms}";
    }
}
