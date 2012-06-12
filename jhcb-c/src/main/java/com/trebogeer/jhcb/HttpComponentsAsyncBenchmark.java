package com.trebogeer.jhcb;

import org.apache.http.HttpResponse;
import org.apache.http.impl.nio.client.DefaultHttpAsyncClient;
import org.apache.http.impl.nio.conn.PoolingClientAsyncConnectionManager;
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.apache.http.nio.client.HttpAsyncClient;
import org.apache.http.nio.client.methods.HttpAsyncMethods;
import org.apache.http.nio.conn.ClientAsyncConnectionManager;
import org.apache.http.nio.conn.scheme.AsyncScheme;
import org.apache.http.nio.conn.scheme.AsyncSchemeRegistry;
import org.apache.http.nio.conn.scheme.LayeringStrategy;
import org.apache.http.nio.protocol.BasicAsyncResponseConsumer;
import org.apache.http.nio.reactor.IOSession;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * @author dimav
 *         Date: 6/8/12
 *         Time: 9:49 AM
 */
public class HttpComponentsAsyncBenchmark extends Benchmark {

    HttpAsyncClient httpclient;

    public HttpComponentsAsyncBenchmark(int threads, int requestsPerThreadPerBatch, int batches, String url) {
        super(threads, requestsPerThreadPerBatch, batches, url);
    }

    @Override
    protected void setup() {
        super.setup();
        try {
            AsyncSchemeRegistry schemeRegistry = new AsyncSchemeRegistry();
            schemeRegistry.register(new AsyncScheme("http", port, new LayeringStrategy() {
                @Override
                public boolean isSecure() {
                    return false;
                }

                @Override
                public IOSession layer(IOSession iosession) {
                    return iosession;
                }
            }));
            IOReactorConfig config = new IOReactorConfig();
            config.setIoThreadCount(200);
            config.setSoKeepalive(true);

            ClientAsyncConnectionManager cm = new PoolingClientAsyncConnectionManager(new DefaultConnectingIOReactor(), schemeRegistry);

            httpclient = new DefaultHttpAsyncClient(cm);
            httpclient.start();
        } catch (Exception o) {
            o.printStackTrace();
        }
    }

    @Override
    protected void tearDown() {
        super.tearDown();
        try {
            httpclient.getConnectionManager().shutdown();
            httpclient.shutdown();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected boolean executeRequest(Random r) {

        try {
            Future<HttpResponse> response = httpclient.execute(HttpAsyncMethods.createGet(url + r.nextLong()), new BasicAsyncResponseConsumer(), null);
            HttpResponse resp = response.get();
            EntityUtils.consume(resp.getEntity());
            return true;

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return false;
    }
}
