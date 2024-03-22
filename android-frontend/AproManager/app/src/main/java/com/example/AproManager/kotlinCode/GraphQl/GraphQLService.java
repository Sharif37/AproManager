package com.example.AproManager.kotlinCode.GraphQl;


import com.example.AproManager.kotlinCode.models.GraphQLRequest;
import com.google.gson.JsonObject;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface GraphQLService {
    @POST("/")
    Call<JsonObject> postQuery(@Body GraphQLRequest body);
    @POST("/")
    Call<JsonObject> addReview(@Body GraphQLRequest body);
}




