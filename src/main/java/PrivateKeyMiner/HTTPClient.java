package PrivateKeyMiner;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.logging.*;

import okhttp3.*;

public class HTTPClient {

    public static OkHttpClient getOkHttpClient() {

        Logger.getLogger(OkHttpClient.class.getName()).setLevel(Level.FINE);

        return new OkHttpClient.Builder()
                .protocols(Arrays.asList(Protocol.HTTP_1_1))
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build();
    }

    public static OkHttpClient getOkHttpClient(String key, String secret) {

        Logger.getLogger(OkHttpClient.class.getName()).setLevel(Level.FINE);

        return new OkHttpClient.Builder()
                .protocols(Arrays.asList(Protocol.HTTP_1_1))
                .connectTimeout(90, TimeUnit.SECONDS)
                .writeTimeout(90, TimeUnit.SECONDS)
                .readTimeout(90, TimeUnit.SECONDS)
                .authenticator(new okhttp3.Authenticator() {
                    @Override
                    public Request authenticate(Route route, Response response) throws IOException {
                        String credential = Credentials.basic(key, secret);
                        return response.request().newBuilder().header("Authorization", credential).build();
                    }
                })
                .build();
    }
}
