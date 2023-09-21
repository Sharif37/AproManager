package com.example.AproManager;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface MyApi {
    @POST("storeUserData")
    Call<Void> storeUserData(@Body UserData userData);

    @GET("check-user")
    Call<Boolean> checkUser(@Query("email") String email);

        @GET("checkPasswordMatch")
        Call<Boolean> checkPasswordMatch(
                @Query("email") String email,
                @Query("password") String password
        );

       //use retrofit
}
