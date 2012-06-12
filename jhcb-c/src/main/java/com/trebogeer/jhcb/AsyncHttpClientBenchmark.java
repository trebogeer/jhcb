package com.trebogeer.jhcb;

import com.ning.http.client.AsyncHandler;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.AsyncHttpClientConfig;
import com.ning.http.client.HttpResponseBodyPart;
import com.ning.http.client.HttpResponseHeaders;
import com.ning.http.client.HttpResponseStatus;
import com.ning.http.client.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * @author dimav
 *         Date: 6/8/12
 *         Time: 1:40 PM
 */
public class AsyncHttpClientBenchmark extends Benchmark {

    private AsyncHttpClient client;

    public AsyncHttpClientBenchmark(int threads, int requestsPerThreadPerBatch, int batches, String url) {
        super(threads, requestsPerThreadPerBatch, batches, url);
    }

    @Override
    protected void setup() {
        super.setup();

        System.setProperty("com.ning.http.client.logging.LoggerProvider.class",
                "com.ning.http.client.logging.Slf4jLoggerProvider");

        AsyncHttpClientConfig config = new AsyncHttpClientConfig.Builder()
                .setMaximumConnectionsPerHost(20).setAllowPoolingConnection(true)
                .build();
        this.client = new AsyncHttpClient(config);
    }

    @Override
    protected boolean executeRequest(Random r) {
        try {
            Future<Integer> future = client.prepareGet(url + r.nextLong()).execute(new AsyncHandler<Integer>() {
                private HttpResponseStatus httpResponseStatus = null;

                @Override
                public void onThrowable(Throwable throwable) {
                }

                @Override
                public STATE onBodyPartReceived(HttpResponseBodyPart bodyPart) throws Exception {
                    return STATE.CONTINUE;
                }

                @Override
                public STATE onStatusReceived(HttpResponseStatus status) throws Exception {
                    this.httpResponseStatus = status;
                    return STATE.CONTINUE;
                }

                @Override
                public STATE onHeadersReceived(HttpResponseHeaders headers) throws Exception {
                    return STATE.CONTINUE;
                }

                @Override
                public Integer onCompleted() throws Exception {
                    return (httpResponseStatus == null ? 500 : httpResponseStatus.getStatusCode());
                }
            });

            try {
                int result = future.get();
                if ((result >= 200) && (result <= 299)) {
                    return true;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    protected void tearDown() {
        super.tearDown();
        this.client.close();
    }
}
