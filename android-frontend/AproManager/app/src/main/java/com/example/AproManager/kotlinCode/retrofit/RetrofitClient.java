package com.example.AproManager.kotlinCode.retrofit;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static final String baseUrl="http://84f2-103-134-127-183.ngrok-free.app/";
    private static final Retrofit retrofit=new Retrofit.
            Builder().
            baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create()).
            build();
    public static ApiService getApiService(){
        return retrofit.create(ApiService.class);
    }

}
