package com.example.AproManager.JavaCode;

import android.util.Log;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiService {
    private static ApiService instance;
    private final MyApi myApi;
    //connect to server

    private ApiService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://61df-103-134-127-180.ngrok-free.app/") // backend URL
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        myApi = retrofit.create(MyApi.class);
    }

    public static synchronized ApiService getInstance() {
        if (instance == null) {
            instance = new ApiService();
        }
        return instance;
    }

    public void signUp(UserData userData, Callback<Void> callback) {
        Call<Void> call = myApi.storeUserData(userData);
        call.enqueue(callback);
        //Log.e("ApiService", userData.getProfileUri()+"");
    }

    public void checkUser(String email, Callback<UserData> callback) {
        Call<UserData> call = myApi.checkUser(email);
        call.enqueue(callback);
    }
    public void checkPasswordMatch(String email, String password, Callback<UserData> callback) {
        Call<UserData> call = myApi.checkPasswordMatch(email, password);
        call.enqueue(callback);
    }
}
