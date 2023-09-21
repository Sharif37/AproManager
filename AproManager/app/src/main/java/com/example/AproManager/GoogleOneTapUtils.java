package com.example.AproManager;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;

import java.io.IOException;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GoogleOneTapUtils {

    public static void startOneTapLogin(Activity activity, SignInClient oneTapClient,
                                        BeginSignInRequest signUpRequest, int requestCode) {
        oneTapClient.beginSignIn(signUpRequest)
                .addOnSuccessListener(activity, result -> {
                    try {
                        activity.startIntentSenderForResult(
                                result.getPendingIntent().getIntentSender(), requestCode,
                                null, 0, 0, 0);
                    } catch (IntentSender.SendIntentException e) {
                        Log.e(TAG, "Couldn't start One Tap UI: " + e.getLocalizedMessage());
                    }
                })
                .addOnFailureListener(activity, e -> {
                    Log.d(TAG, e.getLocalizedMessage());
                    // Handle failure, e.g., show an error message
                });
    }


    public static void checkUser(Activity activity, ApiService apiService,String email,String password,SignInCredential credential) {

        apiService.checkUser(email, new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.isSuccessful()) {
                    boolean isUserPresent = response.body();
                    if (isUserPresent) {
                        // Toast.makeText(signUpActivity.this, "User is already present", Toast.LENGTH_SHORT).show();
                        showAlertDialog(activity);

                    } else {
                        storeData(activity,apiService,email,password,credential);

                    }
                } else {

                    Log.e(TAG, "Response not successful. Response code: " + response.code());
                    try {
                        Log.e(TAG, "Response error body: " + response.errorBody().string());
                    } catch (IOException e) {
                        Log.e(TAG, "Error reading error response: " + e.getMessage());
                    }
                    Toast.makeText(activity, "Server error", Toast.LENGTH_SHORT).show();


                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Log.e(ContentValues.TAG, "Network error: " + t.getMessage());
                 Toast.makeText(activity, "Network error", Toast.LENGTH_SHORT).show();
            }

        });

    }

    public static void checkUser_login(Activity activity, ApiService apiService,String email) {

        apiService.checkUser(email, new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.isSuccessful()) {
                    boolean isUserPresent = response.body();
                    if (isUserPresent) {
                        //user already present

                        Intent intent = new Intent(activity, Home.class);
                        activity.startActivity(intent);



                    } else {
                        Toast.makeText(activity, "please sign up first.", Toast.LENGTH_SHORT).show();

                    }
                } else {

                    Log.e(TAG, "Response not successful. Response code: " + response.code());
                    try {
                        Log.e(TAG, "Response error body: " + response.errorBody().string());
                    } catch (IOException e) {
                        Log.e(TAG, "Error reading error response: " + e.getMessage());
                    }
                    Toast.makeText(activity, "Server error", Toast.LENGTH_SHORT).show();


                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Log.e(ContentValues.TAG, "Network error: " + t.getMessage());
                 Toast.makeText(activity, "Network error", Toast.LENGTH_SHORT).show();
            }

        });

    }

    static void isPasswordOk(String email, String password, Activity activity,ApiService apiService) {
        apiService.checkPasswordMatch(email, password, new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.isSuccessful()) {
                    boolean passwordMatch = response.body() != null && response.body();
                    if (passwordMatch) {

                        Intent intent = new Intent(activity, Home.class);
                        activity.startActivity(intent);

                    } else {
                        Toast.makeText(activity, "give correct password", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(activity, "failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                // Handle failure
            }
        });
    }



    public static void checkLogin(Activity activity, ApiService apiService,String email,String password) {

        apiService.checkUser(email, new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.isSuccessful()) {
                    boolean validLogin = response.body();
                    if (validLogin) {
                        //user already present
                       //now check password with email
                        isPasswordOk(email,password,activity,apiService);
                        Toast.makeText(activity, "user present", Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(activity, "please sign up first.", Toast.LENGTH_SHORT).show();

                    }
                } else {

                    Log.e(TAG, "Response not successful. Response code: " + response.code());
                    try {
                        Log.e(TAG, "Response error body: " + response.errorBody().string());
                    } catch (IOException e) {
                        Log.e(TAG, "Error reading error response: " + e.getMessage());
                    }
                    Toast.makeText(activity, "Server error", Toast.LENGTH_SHORT).show();


                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Log.e(ContentValues.TAG, "Network error: " + t.getMessage());
                 Toast.makeText(activity, "Network error", Toast.LENGTH_SHORT).show();
            }

        });

    }

    public static void showAlertDialog(Activity activity) {

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(" ")
                .setMessage(R.string.dialog_start)
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(activity, LogInActivity.class);
                        activity.startActivity(intent);
                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }


    public static void storeData(Activity activity, ApiService apiService, String email,String password, SignInCredential credential) {
        //user not present in database

        UserData userData ;
        if(credential!=null) {
            String name = credential.getDisplayName();
            Uri profileLink = credential.getProfilePictureUri();
            userData = new UserData(name, email, profileLink.toString(),null);
           // Toast.makeText(activity, profileLink.toString()+"", Toast.LENGTH_SHORT).show();
        }else
        {
            int atIndex = email.indexOf('@');
            Toast.makeText(activity, password, Toast.LENGTH_SHORT).show();
            userData = new UserData( email.substring(0,atIndex), email, null,password);
        }

        apiService.storeUserData(userData, new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(activity, "sign up successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(activity, Home.class);
                    activity.startActivity(intent);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(ContentValues.TAG, "Network error: " + t.getMessage());
                Toast.makeText(activity, "Network request failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static boolean isValidEmail(String email) {
        String regex = "^[A-Za-z0-9+_.-]+@(.+)$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);

        return matcher.matches();

    }





}










