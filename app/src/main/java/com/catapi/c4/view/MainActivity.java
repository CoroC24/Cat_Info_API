package com.catapi.c4.view;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.catapi.c4.R;
import com.catapi.c4.data.InfoCatResponse;
import com.catapi.c4.data.ListCatResponse;
import com.catapi.c4.data.remote.ApiService;
import com.catapi.c4.data.remote.ApiUtils;
import com.catapi.c4.model.Utils;
import com.catapi.c4.view.adapter.AnswersAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private AnswersAdapter answersAdapter;
    private RecyclerView recyclerView;
    private ApiService apiService;
    private SwipeRefreshLayout swipeRefresh;
    private LinearLayout noInternetLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkOverlayPermission();

        swipeRefresh = findViewById(R.id.swipeRefresh);
        noInternetLayout = findViewById(R.id.layoutNoInternet);
        apiService = ApiUtils.getApiService();
        recyclerView = findViewById(R.id.layoutRecyclerView);
        answersAdapter = new AnswersAdapter(this, new ArrayList<>(0), new AnswersAdapter.OnItemClickListener() {
            @Override
            public void onPostClick(ListCatResponse data) {
                getCatInfo(data.getId());
            }
        });

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(answersAdapter);
        recyclerView.setHasFixedSize(true);

        if (!Utils.checkInternetConnection(getApplicationContext()) && noInternetLayout != null && recyclerView != null) {
            noInternetLayout.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }

        loadData();
        swipeRefresh();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1234) {
            if (Settings.canDrawOverlays(this)) {
                Toast.makeText(this, "PERMISO SUPERPOSICIÓN ACTIVADO", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "PERMISO SUPERPOSICIÓN INACTIVO", Toast.LENGTH_LONG).show();
                checkOverlayPermission();
            }
        }
    }

    private void checkOverlayPermission() {
        if (!Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, 1234);
        }
    }

    private void swipeRefresh() {
        swipeRefresh.setColorSchemeColors(Color.parseColor("#00B3FF"), Color.parseColor("#007DFE"));
        swipeRefresh.setOnRefreshListener(() -> {
            loadData();
            /*new Handler().postDelayed(() -> {
                swipeRefresh.setRefreshing(false);
            }, 3000);*/
        });
    }

    public void loadData() {
        if (!Utils.checkInternetConnection(getApplicationContext()) && noInternetLayout != null && recyclerView != null) {
            noInternetLayout.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            swipeRefresh.setRefreshing(false);
            return;
        } else {
            if (noInternetLayout != null && recyclerView != null) {
                noInternetLayout.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }
        }

        apiService.getAnswers(ApiUtils.API_KEY).enqueue(new Callback<List<ListCatResponse>>() {
            @Override
            public void onResponse(Call<List<ListCatResponse>> call, Response<List<ListCatResponse>> response) {
                if (response.isSuccessful()) {
                    answersAdapter.updateAnswersCats(response.body());
                    swipeRefresh.setRefreshing(false);
                }
            }

            @Override
            public void onFailure(Call<List<ListCatResponse>> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Nada papa - " + t, Toast.LENGTH_SHORT).show();
                Log.e("Totiao", String.valueOf(t));
                swipeRefresh.setRefreshing(false);
            }
        });
    }

    public void getCatInfo(String id) {
        apiService.getAnswers(ApiUtils.API_KEY, id).enqueue(new Callback<InfoCatResponse>() {
            @Override
            public void onResponse(Call<InfoCatResponse> call, Response<InfoCatResponse> response) {
                if (response.isSuccessful()) {
                    Utils.infoCatResponse = response.body();
                    Intent intent = new Intent(MainActivity.this, CatDescription.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onFailure(Call<InfoCatResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Nada papa - " + t, Toast.LENGTH_SHORT).show();
                Log.e("Totiao", String.valueOf(t));
            }
        });
    }
}