package com.example.AproManager.kotlinCode.retrofit;

import com.example.AproManager.kotlinCode.models.Review;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiService {
    @POST("/reviews")
    Call<ResponseBody> sendReview(@Body Review reviewData);
    @GET("/reviews")
    Call<ResponseBody> getAllReviews();
    @POST("models/ZB-Tech/Text-to-Image")
    Call<ResponseBody> generateImage(@Body ImageRequest request) ;


}
