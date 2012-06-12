package com.trebogeer.jhcb;

/**
 * @author dimav
 *         Date: 6/6/12
 *         Time: 1:58 PM
 */
public class ThreadResult {

    private final int targetRequests;
    private final int successfulRequests;
    private final long totalTime;
    private final long reties;

    public ThreadResult(int targetRequests, int successfulRequests, long totalTime) {
        this.targetRequests = targetRequests;
        this.successfulRequests = successfulRequests;
        this.totalTime = totalTime;
        this.reties = 0;
    }

    public ThreadResult(int targetRequests, int successfulRequests, long totalTime, long reties) {
        this.targetRequests = targetRequests;
        this.successfulRequests = successfulRequests;
        this.totalTime = totalTime;
        this.reties = reties;
    }

    public int getTargetRequests() {
        return targetRequests;
    }

    public int getSuccessfulRequests() {
        return successfulRequests;
    }

    public long getTotalTime() {
        return totalTime;
    }

    public long getReties() {
        return reties;
    }
}
