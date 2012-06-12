package com.trebogeer.jhcb;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.Random;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;

/**
 * @author dimav
 *         Date: 6/6/12
 *         Time: 2:02 PM
 */
public class HttpComponentsBenchmark extends Benchmark {

    // internal vars --------------------------------------------------------------------------------------------------

    private HttpClient client;

    // constructors ---------------------------------------------------------------------------------------------------

    public HttpComponentsBenchmark(int threads, int requestsPerThreadPerBatch, int batches, String uri) {
        super(threads, requestsPerThreadPerBatch, batches, uri);
    }

    // AbstractBenchmark ----------------------------------------------------------------------------------------------

    @Override
    protected void setup() {
        super.setup();
        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(new Scheme("http", port, PlainSocketFactory.getSocketFactory()));
        PoolingClientConnectionManager cm = new PoolingClientConnectionManager(schemeRegistry);
        // Increase max total connection to 200
        cm.setMaxTotal(max_connections_per_host);
        // Increase default max connection per route to 200
        cm.setDefaultMaxPerRoute(max_connections_per_host);
        // HttpParams
        this.client = new DefaultHttpClient(cm);
    }

    @Override
    protected void tearDown() {
        super.tearDown();
        this.client.getConnectionManager().shutdown();
    }

    @Override
    protected boolean executeRequest(Random r) {

        HttpGet get = new HttpGet(url + r.nextLong());
        try {
            HttpResponse response = client.execute(get);
            EntityUtils.consume(response.getEntity());
            return true;
        } catch (IOException e) {
            get.abort();
            e.printStackTrace();
        }
        return false;
    }
}
