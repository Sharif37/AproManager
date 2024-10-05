package com.example.AproManager.kotlinCode.GraphQl;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClientForGraphQl {

    private static final String baseUrl="https://b542-103-134-127-176.ngrok-free.app/";
    private static final Retrofit retrofit=new Retrofit.
            Builder().
            baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create()).
            build();
    public static GraphQLService getApiService(){
        return retrofit.create(GraphQLService.class);
    }

}