package com.trebogeer.jhcb;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.DefaultClientConfig;

import javax.ws.rs.core.MediaType;
import java.util.Random;

/**
 * @author dimav
 *         Date: 6/7/12
 *         Time: 9:26 AM
 */
public class JerseyClientBenchmark extends Benchmark {

    private Client client;

    public JerseyClientBenchmark(int threads, int requestsPerThreadPerBatch, int batches, String url) {
        super(threads, requestsPerThreadPerBatch, batches, url);
    }


    @Override
    protected void setup() {
        super.setup();
        DefaultClientConfig cfg = new DefaultClientConfig();
        cfg.getProperties().put(DefaultClientConfig.PROPERTY_READ_TIMEOUT, Integer.valueOf("3000"));
        cfg.getProperties().put(DefaultClientConfig.PROPERTY_CONNECT_TIMEOUT, Integer.valueOf("3000"));
        cfg.getProperties().put(DefaultClientConfig.PROPERTY_THREADPOOL_SIZE, Integer.valueOf("1"));
        client = Client.create(cfg);
    }

    @Override
    protected boolean executeRequest(Random r) {
        WebResource res = client.resource(url + r.nextLong());
        String response = res.accept(
                MediaType.TEXT_PLAIN).
                get(String.class);
        return response != null && response.length() != 0;
    }
}
