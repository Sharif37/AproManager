package com.example.AproManager.kotlinCode.retrofit;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient2 {
    private static final String BASE_URL = "https://api-inference.huggingface.co/";
    private static Retrofit retrofit = null;

    public static Retrofit getClient(String authToken) {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(120, TimeUnit.SECONDS) // Connection timeout
                .writeTimeout(120, TimeUnit.SECONDS)   // Write timeout
                .readTimeout(120, TimeUnit.SECONDS)    // Read timeout
                .addInterceptor(new AuthenticationInterceptor(authToken))
                .build();

        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(okHttpClient)
                    .build();
        }
        return retrofit;
    }
}

