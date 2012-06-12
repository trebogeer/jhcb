package com.trebogeer.jhcb;

import com.sun.jersey.test.framework.spi.container.TestContainerException;
import org.testng.annotations.Test;

import java.util.LinkedList;
import java.util.List;

/**
 * @author dimav
 *         Date: 6/7/12
 *         Time: 12:29 PM
 */
public class PingPongServerRemote {

    private List<Benchmark> benchmarks = new LinkedList<Benchmark>();

    public PingPongServerRemote() throws TestContainerException {

        int threads =400;
        int batchSize = 10;
        int batches = 20000;
        String url = "http://" + Benchmark.host + ":" + Benchmark.port + "/jhcb-web-1.0/a/b/c/ping/";
      //  benchmarks.add(new NettyHttpClientBenchmark(threads, batchSize, batches, url));
      //  benchmarks.add(new NettyHttpPipeClientBenchmark(threads, batchSize, batches, url));
        benchmarks.add(new GrizzlyHttpClientBenchmark(threads, batchSize, batches, url));
        //sucks
        // benchmarks.add(new JerseyClientBenchmark(threads, batchSize, batches, url));
        //sucks
      //  benchmarks.add(new HttpComponentsBenchmark(threads, batchSize, batches, url));
        //sucks
        // benchmarks.add(new JavaPlainHttpClientBenchmark(threads, batchSize, batches, url));
        //sucks
        // benchmarks.add(new AsyncHttpClientBenchmark(threads, batchSize, batches, url));
        // benchmarks.add(new HttpComponentsAsyncBenchmark(threads, batchSize, batches, url));
    }


    @Test
    public void rurBenchmark() {
        for (Benchmark benchmark : benchmarks) {
            BenchmarkResult result = benchmark.doBenchmark();
            System.out.println(result);
//            try {
//                Thread.sleep(10000);
//            } catch (InterruptedException e) {
//                Thread.currentThread().interrupt();
//            }
        }
    }

}
