package com.catapi.c4.view.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.catapi.c4.R;
import com.catapi.c4.data.ListCatResponse;
import com.catapi.c4.data.remote.ApiService;
import com.catapi.c4.data.remote.ApiUtils;
import com.catapi.c4.databinding.FragmentMainBinding;
import com.catapi.c4.model.Utils;
import com.catapi.c4.view.adapter.AnswersAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainFragment extends Fragment {

    private FragmentMainBinding binding;
    private Context context;
    private AnswersAdapter answersAdapter;
    private ApiService apiService;
    private DrawerLayout drawerLayout;

    public MainFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMainBinding.inflate(inflater, container, false);

        this.context = Utils.context;

        drawerLayout = requireActivity().findViewById(R.id.drawer_layout);
        apiService = ApiUtils.getApiService();

        answersAdapter = new AnswersAdapter(new ArrayList<>(0), data -> {
            binding.progressIndicator.setVisibility(View.VISIBLE);
            binding.progressIndicator.show();

            Utils.getCatInfo(data.getId(), binding.progressIndicator);
        });

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(context, 2);
        binding.layoutRecyclerView.setLayoutManager(layoutManager);
        binding.layoutRecyclerView.setAdapter(answersAdapter);
        binding.layoutRecyclerView.setHasFixedSize(true);

        binding.progressIndicator.show();

        if (!Utils.checkInternetConnection(context)) {
            binding.layoutNoInternet.setVisibility(View.VISIBLE);
            binding.layoutRecyclerView.setVisibility(View.GONE);
            binding.progressIndicator.setVisibility(View.GONE);
            binding.progressIndicator.hide();
        }

        binding.searchBar.setNavigationOnClickListener(view -> {
            if (drawerLayout != null) drawerLayout.open();
        });

        requireActivity().getOnBackPressedDispatcher().addCallback(requireActivity(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (binding.searchView.isShowing()) {
                    binding.searchView.hide();
                } else {
                    requireActivity().finish();
                }
            }
        });

        loadData();
        swipeRefresh();

        return binding.getRoot();
    }

    private void swipeRefresh() {
        binding.swipeRefresh.setColorSchemeColors(Color.parseColor("#00B3FF"), Color.parseColor("#007DFE"));
        binding.swipeRefresh.setOnRefreshListener(this::loadData);
    }

    private void loadData() {
        if (!Utils.checkInternetConnection(context)) {
            binding.layoutNoInternet.setVisibility(View.VISIBLE);
            binding.layoutRecyclerView.setVisibility(View.GONE);
            binding.swipeRefresh.setRefreshing(false);
            binding.progressIndicator.hide();
            binding.progressIndicator.setVisibility(View.GONE);
            return;
        } else {
            binding.layoutNoInternet.setVisibility(View.GONE);
            binding.layoutRecyclerView.setVisibility(View.VISIBLE);
        }

        apiService.getAnswers(ApiUtils.API_KEY).enqueue(new Callback<List<ListCatResponse>>() {
            @Override
            public void onResponse(@NonNull Call<List<ListCatResponse>> call, @NonNull Response<List<ListCatResponse>> response) {
                if (response.isSuccessful()) {
                    answersAdapter.updateAnswersCats(response.body());
                    binding.progressIndicator.hide();
                    binding.progressIndicator.setVisibility(View.GONE);
                    binding.swipeRefresh.setRefreshing(false);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<ListCatResponse>> call, @NonNull Throwable t) {
                Toast.makeText(context, "Nada papa - " + t, Toast.LENGTH_SHORT).show();
                Log.e("Error Response", String.valueOf(t));
                binding.progressIndicator.hide();
                binding.progressIndicator.setVisibility(View.GONE);
                binding.swipeRefresh.setRefreshing(false);
            }
        });
    }
}
