package com.catapi.c4.view.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.catapi.c4.R;
import com.catapi.c4.data.AddFavouritesResponse;
import com.catapi.c4.data.InfoCatResponse;
import com.catapi.c4.data.ListCatResponse;
import com.catapi.c4.data.ListFavouritesResponse;
import com.catapi.c4.data.remote.ApiService;
import com.catapi.c4.data.remote.ApiUtils;
import com.catapi.c4.model.AddFavouritesData;
import com.catapi.c4.model.Utils;
import com.catapi.c4.view.activities.CatDescription;
import com.catapi.c4.view.adapter.AnswersAdapter;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.search.SearchBar;
import com.google.android.material.search.SearchView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainFragment extends Fragment {

    private Context context;
    private AnswersAdapter answersAdapter;
    private RecyclerView recyclerView;
    private ApiService apiService;
    private SwipeRefreshLayout swipeRefresh;
    private LinearLayout noInternetLayout;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private SearchBar searchBar;
    private SearchView searchView;
    private LinearProgressIndicator progressIndicator;
    private MaterialCardView cardView;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    public MainFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstaceState) {
        View viewLayout = inflater.inflate(R.layout.fragment_main, container, false);

        this.context = Utils.context;

        swipeRefresh = viewLayout.findViewById(R.id.swipeRefresh);
        noInternetLayout = viewLayout.findViewById(R.id.layoutNoInternet);
        drawerLayout = requireActivity().findViewById(R.id.drawer_layout);
        recyclerView = viewLayout.findViewById(R.id.layoutRecyclerView);
        searchBar = viewLayout.findViewById(R.id.search_bar);
        searchView = viewLayout.findViewById(R.id.search_view);
        progressIndicator = viewLayout.findViewById(R.id.progress_indicator);
        apiService = ApiUtils.getApiService();

        answersAdapter = new AnswersAdapter(context, new ArrayList<>(0), data -> getCatInfo(data.getId()));

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(context, 2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(answersAdapter);
        recyclerView.setHasFixedSize(true);

        progressIndicator.show();

        if (!Utils.checkInternetConnection(context) && noInternetLayout != null && recyclerView != null) {
            noInternetLayout.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            progressIndicator.setVisibility(View.GONE);
            progressIndicator.hide();
        }

        searchBar.setNavigationOnClickListener(view -> {
            if (drawerLayout != null) drawerLayout.open();
        });

        /*cardView.setOnLongClickListener(view -> {
            addFavourites();
            return false;
        });*/

        loadData();
        swipeRefresh();

        return viewLayout;
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
    }

    private void swipeRefresh() {
        swipeRefresh.setColorSchemeColors(Color.parseColor("#00B3FF"), Color.parseColor("#007DFE"));
        swipeRefresh.setOnRefreshListener(this::loadData);
    }

    private void loadData() {
        if (!Utils.checkInternetConnection(context) && noInternetLayout != null && recyclerView != null) {
            noInternetLayout.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            swipeRefresh.setRefreshing(false);
            progressIndicator.hide();
            progressIndicator.setVisibility(View.GONE);
            return;
        } else {
            if (noInternetLayout != null && recyclerView != null) {
                noInternetLayout.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }
        }

        apiService.getAnswers(ApiUtils.API_KEY).enqueue(new Callback<List<ListCatResponse>>() {
            @Override
            public void onResponse(@NonNull Call<List<ListCatResponse>> call, @NonNull Response<List<ListCatResponse>> response) {
                if (response.isSuccessful()) {
                    answersAdapter.updateAnswersCats(response.body());
                    progressIndicator.hide();
                    progressIndicator.setVisibility(View.GONE);
                    swipeRefresh.setRefreshing(false);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<ListCatResponse>> call, @NonNull Throwable t) {
                Toast.makeText(context, "Nada papa - " + t, Toast.LENGTH_SHORT).show();
                Log.e("Totiao", String.valueOf(t));
                progressIndicator.hide();
                progressIndicator.setVisibility(View.GONE);
                swipeRefresh.setRefreshing(false);
            }
        });
    }

    private void getCatInfo(String id) {
        progressIndicator.setVisibility(View.VISIBLE);
        progressIndicator.show();
        apiService.getAnswers(ApiUtils.API_KEY, id).enqueue(new Callback<InfoCatResponse>() {
            @Override
            public void onResponse(@NonNull Call<InfoCatResponse> call, @NonNull Response<InfoCatResponse> response) {
                if (response.isSuccessful()) {
                    Utils.infoCatResponse = response.body();
                    Intent intent = new Intent(context, CatDescription.class);
                    startActivity(intent);
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
