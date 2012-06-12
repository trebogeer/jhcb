package com.trebogeer.jhcb;


import com.biasedbit.http.client.AbstractHttpClient;
import com.biasedbit.http.client.HttpClient;
import com.biasedbit.http.future.HttpRequestFuture;
import com.biasedbit.http.processor.BodyAsStringProcessor;
import org.jboss.netty.handler.codec.http.DefaultHttpRequest;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpVersion;

import java.util.Random;

/**
 * @author dimav
 *         Date: 6/6/12
 *         Time: 2:33 PM
 */
public class NettyHttpClientBenchmark extends Benchmark {

    protected HttpClient client;

    protected static final String HOST_HEADER_VALUE = host + ":" + port;

    public NettyHttpClientBenchmark(int threads, int requestsPerThreadPerBatch, int batches, String url) {
        super(threads, requestsPerThreadPerBatch, batches, url);
    }

    @Override
    protected void setup() {
        super.setup();
        this.client = new BenchmarkHttpClient();
        this.client.init();
    }

    @Override
    protected void tearDown() {
        super.tearDown();
        this.client.terminate();
    }

    private static final class BenchmarkHttpClient extends AbstractHttpClient {
        private BenchmarkHttpClient() {
            super();
            super.useNio = true;
            super.maxConnectionsPerHost = max_connections_per_host;
        }
    }


    @Override
    protected boolean executeRequest(Random r) {

        final HttpRequest request = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, URI_PREFIX + r.nextLong());
        request.setHeader(HttpHeaders.Names.HOST, HOST_HEADER_VALUE);
        HttpHeaders.setKeepAlive(request, true);
        HttpRequestFuture<String> future = client.execute(host, port, request, new BodyAsStringProcessor());

        future.awaitUninterruptibly();
//                        if (future.getCause() == HttpRequestFuture.CONNECTION_LOST) {
//                            System.out.println("Connection lost.");
//                        }
//                        if (future.getCause() == HttpRequestFuture.EXECUTION_REJECTED) {
//                            System.out.println("Rejected by connection request.");
//                        }
        if (future.getCause() != null) {
            System.out.println(future.getCause());
        }
        if (future.isSuccessfulResponse()) {
            future.getProcessedResult();
            return true;

        } else {
            // retrying
            System.out.println();
            future = client.execute(host, port, request, new BodyAsStringProcessor());
            // retries++;
            future.awaitUninterruptibly();
            if (future.getCause() != null) {
                System.out.println(future.getCause());
            }
            if (future.isSuccessfulResponse()) {
                future.getProcessedResult();
                return true;
            }
        }

        return false;
    }
}
