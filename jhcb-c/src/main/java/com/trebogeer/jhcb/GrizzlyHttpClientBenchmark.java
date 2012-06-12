package com.trebogeer.jhcb;

import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.AsyncHttpClientConfig;
import com.ning.http.client.Request;
import com.ning.http.client.RequestBuilder;
import com.ning.http.client.Response;
import com.ning.http.client.providers.netty.NettyAsyncHttpProvider;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author dimav
 *         Date: 6/8/12
 *         Time: 2:08 PM
 */
public class GrizzlyHttpClientBenchmark extends Benchmark {

    private AsyncHttpClient client;

    public GrizzlyHttpClientBenchmark(int threads, int requestsPerThreadPerBatch, int batches, String url) {
        super(threads, requestsPerThreadPerBatch, batches, url);
    }

    @Override
    protected void setup() {
        super.setup();
        final AsyncHttpClientConfig config = new AsyncHttpClientConfig.Builder().setAllowPoolingConnection(true).
                setConnectionTimeoutInMs(1000).setMaximumConnectionsPerHost(max_connections_per_host).
                setMaximumConnectionsTotal(max_connections_per_host).setMaxRequestRetry(5).build();

        client = new AsyncHttpClient(new NettyAsyncHttpProvider(config), config);
    }

    @Override
    protected boolean executeRequest(Random r) {
        final Request request = new RequestBuilder("GET").setUrl(url + r.nextLong()).build();
        try {
            Future<Response> responseFuture = client.executeRequest(request);
            final Response response = responseFuture.get(5, TimeUnit.SECONDS);
            String body = response.getResponseBody();
            return true;

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
        return false;
    }
}
