package com.example.AproManager.JavaCode;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.example.AproManager.R;
import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GoogleOneTapUtils {
    public static void openOneTap(Activity activity, int requestCode) {

        SignInClient oneTapClient = Identity.getSignInClient(activity);
        BeginSignInRequest signInRequest = createSignInRequest(activity);
        startOneTap(activity, oneTapClient, signInRequest, requestCode);
    }

    private static BeginSignInRequest createSignInRequest(Activity activity) {
        return BeginSignInRequest.builder()
                .setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                        .setSupported(true)
                        .setServerClientId(activity.getString(R.string.clientID))
                        .setFilterByAuthorizedAccounts(false)
                        .build())
                .build();
    }


    public static void startOneTap(Activity activity, SignInClient oneTapClient,
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
                    Log.d(TAG, Objects.requireNonNull(e.getLocalizedMessage()));

                });
    }

    //signUp using google
    public static void signup_usingGoogle(Activity activity, String name, String email, String s, String profileLink, String formattedDate) {

        ApiService.getInstance().checkUser(email, new Callback<UserData>() {
            @Override
            public void onResponse(Call<UserData> call, Response<UserData> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null && response.body().getEmail() !=null) {
                        showAlertDialog(activity);
                    } else {
                      storeData(activity,name,email,s,profileLink,formattedDate);
                    }

                } else {
                    String errorMessage = "Sign up failed. Error: " + response.message();
                   // Log.e("API Error", "Failed to make API call. " + errorMessage);
                }

            }

            @Override
            public void onFailure(Call<UserData> call, Throwable t) {

                Toast.makeText(activity, "Failed to make API call. Please check your network connection.", Toast.LENGTH_SHORT).show();
              //  Log.e("API Error", "Failed to make API call", t);
            }
        });
    }



    public static void signup_usingPass(Activity activity, String email, String password) {
        ApiService.getInstance().checkPasswordMatch(email, password, new Callback<UserData>() {
            @Override
            public void onResponse(Call<UserData> call, Response<UserData> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null && response.body().getEmail() !=null) {
                        showAlertDialog(activity,"user already exits");
                    } else {

                        long currentTimeMillis = System.currentTimeMillis();
                        Date currentDate = new Date(currentTimeMillis);
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX", Locale.getDefault());
                        String formattedDate = dateFormat.format(currentDate);
                        int atIndex = email.indexOf('@');

                        storeData(activity, email.substring(0, atIndex), email, password, "profileLink", formattedDate);
                    }

                } else {
                    String errorMessage = "Sign up failed. Error: " + response.message();
                    Toast.makeText(activity, errorMessage, Toast.LENGTH_SHORT).show();
                    Log.e("API Error", "Failed to make API call. " + errorMessage);
                }

            }

            @Override
            public void onFailure(Call<UserData> call, Throwable t) {
                // Network or other failure
                //Toast.makeText(activity, "Failed to make API call. Please check your network connection.", Toast.LENGTH_SHORT).show();
                Log.e("API Error", "Failed to make API call", t);
            }
        });
    }

    private static void showAlertDialog(Activity activity, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Authentication Failed")
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }




   /* public static void storeData(Activity activity, String name, String email, String password, String profileLink, String currentDate) {
        //user not present in database

        UserData userData = new UserData(name, email, profileLink, password, currentDate);

        ApiService.getInstance().signUp(userData, new Callback<Void>() {

            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {

                        Toast.makeText(activity, "Sign up successful", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(activity, Home.class);
                        activity.startActivity(intent);

                } else {
                    Toast.makeText(activity, "Sign up failed. Error: " + response.message(), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(activity, "Network request failed", Toast.LENGTH_SHORT).show();
            }
        });

    }*/

    public static void storeData(Activity activity, String name, String email, String password, String profileLink, String currentDate) {
        UserData userData = new UserData(name, email, profileLink, password, currentDate);

        ApiService.getInstance().signUp(userData, new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    // Store user session information
                    saveUserSession(activity, email);

                    Toast.makeText(activity, "Sign up successful", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(activity, Home.class);
                    activity.startActivity(intent);
                } else {
                    Toast.makeText(activity, "Sign up failed. Error: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(activity, "Network request failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // save user session
    private static void saveUserSession(Activity activity, String userEmail) {
        String sessionKey = "user_session";

        SharedPreferences sharedPreferences = activity.getSharedPreferences(sessionKey, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("user_email", userEmail);
        editor.apply();
    }

    // Method to retrieve user session information using SharedPreferences
    public static String getUserSession(Activity activity) {
        String sessionKey = "user_session";
        SharedPreferences sharedPreferences = activity.getSharedPreferences(sessionKey, Context.MODE_PRIVATE);
        return sharedPreferences.getString("user_email", null);
    }

    public static boolean isValidEmail(String email) {
        String regex = "^[A-Za-z0-9+_.-]+@(.+)$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);

        return matcher.matches();

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


    public static void login_usingGoogle(Activity activity, String email) {

        ApiService.getInstance().checkUser(email, new Callback<UserData>() {
            @Override
            public void onResponse(Call<UserData> call, Response<UserData> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null && response.body().getEmail()==null) {
                        showAlertDialog(activity,"user not exists");
                        } else {
                        Toast.makeText(activity, "Login Successful.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(activity, Home.class);
                        activity.startActivity(intent);

                    }
                } else {
                    Toast.makeText(activity, "Login  failed. Error: " + response.message(), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<UserData> call, Throwable t) {

            }
        });

    }

    public static void login_usingPass(Activity activity, String email, String password) {

        ApiService.getInstance().checkPasswordMatch(email,password, new Callback<UserData>() {
            @Override
            public void onResponse(Call<UserData> call, Response<UserData> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null && response.body().getEmail()==null) {
                       showAlertDialog(activity,"user or password not match.");
                    } else {
                        Toast.makeText(activity, "Login Successful.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(activity, Home.class);
                        activity.startActivity(intent);
                    }
                } else {
                    Toast.makeText(activity, "login failed. Error: " + response.message(), Toast.LENGTH_SHORT).show();
                }


            }

            @Override
            public void onFailure(Call<UserData> call, Throwable t) {

            }
        });
    }


}










