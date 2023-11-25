package com.example.AproManager;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;

public class loadingSpinner {

    private static AlertDialog loadingDialog;

    public static void showLoadingSpinner(Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.loading_spinner, null);
        builder.setView(dialogView);
        builder.setCancelable(true);
        loadingDialog = builder.create();
        loadingDialog.show();
    }

    public static void hideLoadingSpinner() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
            loadingDialog = null;
        }
    }
}
