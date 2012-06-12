package com.trebogeer.jhcb;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;
import java.util.Scanner;

/**
 * @author dimav
 *         Date: 6/7/12
 *         Time: 9:38 AM
 */
public class JavaPlainHttpClientBenchmark extends Benchmark {

    public JavaPlainHttpClientBenchmark(int threads, int requestsPerThreadPerBatch, int batches, String url) {
        super(threads, requestsPerThreadPerBatch, batches, url);
    }

    private static String readFromInputStream(final InputStream is) {
        Scanner scanner = new Scanner(is);
        if (scanner.hasNext()) {
            return scanner.next();
        }
        return "";
    }

    @Override
    protected boolean executeRequest(Random r) {
        HttpURLConnection connection = null;
        try {
            URL u = new URL("http", host, port, URI_PREFIX + r.nextLong());
            connection = (HttpURLConnection) u.openConnection();
            connection.setRequestMethod("GET");
            InputStream is = connection.getInputStream();
            String s = readFromInputStream(is);
            // System.out.println(s);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }/* finally {
            if (connection != null)
                connection.disconnect();
        }*/
        return false;
    }
}
