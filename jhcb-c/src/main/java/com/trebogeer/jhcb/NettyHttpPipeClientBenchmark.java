package com.trebogeer.jhcb;

import com.biasedbit.http.client.DefaultHttpClientFactory;
import com.biasedbit.http.connection.PipeliningHttpConnectionFactory;

import java.util.concurrent.Executors;

/**
 * @author dimav
 *         Date: 6/6/12
 *         Time: 3:48 PM
 */
public class NettyHttpPipeClientBenchmark extends NettyHttpClientBenchmark {

    public NettyHttpPipeClientBenchmark(int threads, int requestsPerThreadPerBatch, int batches, String url) {
        super(threads, requestsPerThreadPerBatch, batches, url);
    }

    @Override
    protected void setup() {
        this.executor = Executors.newFixedThreadPool(this.threads);
        DefaultHttpClientFactory factory = new DefaultHttpClientFactory();
        factory.setMaxConnectionsPerHost(max_connections_per_host);
        factory.setRequestTimeoutInMillis(5000);
        factory.setUseNio(true);
        PipeliningHttpConnectionFactory pf = new PipeliningHttpConnectionFactory();
        //   pf.setAllowNonIdempotentPipelining(true);
        //  pf.setMaxRequestsInPipeline(10);
        //  pf.setDisconnectIfNonKeepAliveRequest(true);
        //  factory.setCleanupInactiveHostContexts(true);
        factory.setConnectionTimeoutInMillis(0);
        //  factory.setDebug(true);
        //  factory.setGatherEventHandlingStats(true);
        // factory.

        factory.setConnectionFactory(pf);
        client = factory.getClient();

        if (!this.client.init()) {
            throw new IllegalStateException("Could not initialise HttpClient");
        }
    }

}
