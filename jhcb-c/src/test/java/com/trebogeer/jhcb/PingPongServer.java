package com.trebogeer.jhcb;

import com.sun.jersey.test.framework.JerseyTest;
import com.sun.jersey.test.framework.LowLevelAppDescriptor;
import com.sun.jersey.test.framework.spi.container.TestContainerException;
import com.sun.jersey.test.framework.spi.container.TestContainerFactory;
import com.sun.jersey.test.framework.spi.container.http.HTTPContainerFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.LinkedList;
import java.util.List;

/**
 * @author dimav
 *         Date: 6/6/12
 *         Time: 1:49 PM
 */
public class PingPongServer extends JerseyTest {

    private List<Benchmark> benchmarks = new LinkedList<Benchmark>();

    public PingPongServer() throws TestContainerException {
        super(new LowLevelAppDescriptor.Builder(JerseyPingPong.class).
                contextPath("jhcb-web-1.0").
                build());

        int threads = 10;
        int batchSize = 100;
        int batches = 4;
        String url = "http://localhost:9998/jhcb-web-1.0/a/b/c/ping/";
        benchmarks.add(new NettyHttpClientBenchmark(threads, batchSize, batches, url));
        benchmarks.add(new NettyHttpPipeClientBenchmark(threads, batchSize, batches, url));
        benchmarks.add(new JerseyClientBenchmark(threads, batchSize, batches, url));
        benchmarks.add(new HttpComponentsBenchmark(threads, batchSize, batches, url));
        benchmarks.add(new HttpComponentsAsyncBenchmark(threads, batchSize, batches, url));
        benchmarks.add(new AsyncHttpClientBenchmark(threads, batchSize, batches, url));
        benchmarks.add(new JavaPlainHttpClientBenchmark(threads, batchSize, batches, url));
    }

    @BeforeClass
    public void beforeClass() throws Exception {
        super.setUp();
        //  benchmark.setup();
        //  benchmark.warmup();
    }

    @Test
    public void rurBenchmark() {
        for (Benchmark benchmark : benchmarks) {
            BenchmarkResult result = benchmark.doBenchmark();
            System.out.println(result);
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    @AfterClass
    public void afterClass() throws Exception {
        super.tearDown();
    }

    @Override
    protected TestContainerFactory getTestContainerFactory() throws TestContainerException {
        return new HTTPContainerFactory();
    }

}
