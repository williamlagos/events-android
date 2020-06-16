package br.com.danceapp.android.service;

import android.os.Build;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import br.com.danceapp.android.BuildConfig;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Cria um cliente Retrofit2 configurado para acessar a API do Backend do DanceApp
 */
public class ServiceBuilder {
    private static final String BASE_URL = "http://api2.danceapp.com.br/api/";
    private static final String AUTH_HEADER = "Basic YXBwLWFuZHJvaWQtdjE6dDVdKXY6YlhbL3ZwJUBNNTlNOV59ZC5hWQ==";

    private static ServiceInterface sApiClient;
    private static String sUserAgent;

    public static ServiceInterface getClient() {
        if (sApiClient == null) {
            Interceptor interceptor = new Interceptor() {
                @Override
                public okhttp3.Response intercept(Chain chain) throws IOException {
                    Request newRequest = chain.request().newBuilder()
                            .addHeader("User-Agent", getUserAgent())
                            .addHeader("Authorization", AUTH_HEADER)
                            .build();
                    return chain.proceed(newRequest);
                }
            };

            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.interceptors().add(interceptor);
            OkHttpClient client = builder.build();

            Gson gson = new GsonBuilder()
                    .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                    .create();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(client)
                    .build();

            sApiClient = retrofit.create(ServiceInterface.class);
        }

        return sApiClient;
    }

    private static String getUserAgent() {
        if (sUserAgent == null) {
            String versionName = BuildConfig.VERSION_NAME;
            sUserAgent = "DanceApp/" + versionName +
                    "(Android/" + Build.VERSION.RELEASE + "; " +
                    Build.MANUFACTURER + "; " + Build.MODEL + "/" + Build.PRODUCT + ")";

            Log.d("ServiceBuilder", "App user agent: " + sUserAgent);
        }

        return sUserAgent;
    }
}
