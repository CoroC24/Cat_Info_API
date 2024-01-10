package com.catapi.c4.model;


import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.catapi.c4.data.InfoCatResponse;
import com.catapi.c4.data.remote.ApiService;
import com.catapi.c4.data.remote.ApiUtils;
import com.catapi.c4.view.activities.CatDescription;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.auth.FirebaseUser;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Utils {

    public static Context context;
    public static FirebaseUser loggedUser;

    public static InfoCatResponse infoCatResponse;

    public static boolean checkInternetConnection(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Network network = connectivityManager.getActiveNetwork();
                if (network != null) {
                    NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(network);
                    return capabilities != null && (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR));
                }
            } else {
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                return networkInfo != null && networkInfo.isConnected();
            }
        }

        return false;
    }

    public static void getCatInfo(String id, LinearProgressIndicator progressIndicator) {
        ApiService apiService = ApiUtils.getApiService();
        apiService.getAnswers(ApiUtils.API_KEY, id).enqueue(new Callback<InfoCatResponse>() {
            @Override
            public void onResponse(@NonNull Call<InfoCatResponse> call, @NonNull Response<InfoCatResponse> response) {
                if (response.isSuccessful()) {
                    Utils.infoCatResponse = response.body();
                    Intent intent = new Intent(context, CatDescription.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                    progressIndicator.hide();
                    progressIndicator.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<InfoCatResponse> call, @NonNull Throwable t) {
                Toast.makeText(context, "Nada papa - " + t, Toast.LENGTH_SHORT).show();
                Log.e("Totiao", String.valueOf(t));
                progressIndicator.hide();
                progressIndicator.setVisibility(View.GONE);
            }
        });
    }
}
