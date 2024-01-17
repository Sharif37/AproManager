package com.example.AproManager.JavaCode;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface MyApi {
    @POST("Signup")
    Call<Void> storeUserData(@Body UserData userData);

    @GET("checkUser")
    Call<UserData> checkUser(@Query("email") String email);

        @GET("login")
        Call<UserData> checkPasswordMatch(
                @Query("email") String email,
                @Query("password") String password
        );


}
