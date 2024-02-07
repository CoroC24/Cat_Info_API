package com.catapi.c4.view.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.catapi.c4.R;
import com.catapi.c4.data.ListFavouritesResponse;
import com.catapi.c4.data.remote.ApiService;
import com.catapi.c4.data.remote.ApiUtils;
import com.catapi.c4.databinding.FragmentFavouritesBinding;
import com.catapi.c4.model.ManageFavouritesCats;
import com.catapi.c4.model.Utils;
import com.catapi.c4.view.activities.SignInActivity;
import com.catapi.c4.view.adapter.AnswersFavouritesAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FavouritesFragment extends Fragment {

    private FragmentFavouritesBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private AnswersFavouritesAdapter answersFavouritesAdapter;
    private ApiService apiService;
    private DrawerLayout drawerLayout;
    private Context context;

    public FavouritesFragment() {

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentFavouritesBinding.inflate(inflater, container, false);

        context = Utils.context;
        mAuth = FirebaseAuth.getInstance();
        apiService = ApiUtils.getApiService();
        drawerLayout = requireActivity().findViewById(R.id.drawer_layout);

        binding.signInButtonFavourites.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), SignInActivity.class);
            startActivity(intent);
        });

        answersFavouritesAdapter = new AnswersFavouritesAdapter(new ArrayList<>(0), data -> {
            binding.progressIndicatorFavourites.setVisibility(View.VISIBLE);
            binding.progressIndicatorFavourites.show();

            Utils.getCatInfo(data.getImageId(), binding.progressIndicatorFavourites);
        });

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(context, 2);
        binding.layoutRecyclerViewFavourites.setLayoutManager(layoutManager);
        binding.layoutRecyclerViewFavourites.setAdapter(answersFavouritesAdapter);
        binding.layoutRecyclerViewFavourites.setHasFixedSize(true);

        binding.toolbarFavourites.setNavigationOnClickListener(view -> {
            if (drawerLayout != null) drawerLayout.open();
        });

        swipeRefresh();

        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();

        currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            binding.layoutNoLogged.setVisibility(View.GONE);
            binding.swipeRefreshFavourites.setVisibility(View.VISIBLE);
            loadFavouritesData();
        } else {
            binding.layoutNoLogged.setVisibility(View.VISIBLE);
            binding.swipeRefreshFavourites.setVisibility(View.GONE);
            binding.progressIndicatorFavourites.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            binding.layoutNoLogged.setVisibility(View.GONE);
            binding.swipeRefreshFavourites.setVisibility(View.VISIBLE);
            loadFavouritesData();
        } else {
            binding.layoutNoLogged.setVisibility(View.VISIBLE);
            binding.swipeRefreshFavourites.setVisibility(View.GONE);
            binding.progressIndicatorFavourites.setVisibility(View.GONE);
        }
    }

    private void swipeRefresh() {
        binding.swipeRefreshFavourites.setColorSchemeColors(Color.parseColor("#00B3FF"), Color.parseColor("#007DFE"));
        binding.swipeRefreshFavourites.setOnRefreshListener(this::loadFavouritesData);
    }

    public void loadFavouritesData() {
        currentUser = Utils.loggedUser;

        if (!Utils.checkInternetConnection(Utils.context)) {
            binding.layoutNoInternetFavourites.setVisibility(View.VISIBLE);
            binding.layoutRecyclerViewFavourites.setVisibility(View.GONE);
            binding.swipeRefreshFavourites.setRefreshing(false);
            binding.progressIndicatorFavourites.hide();
            binding.progressIndicatorFavourites.setVisibility(View.GONE);
            return;
        } else if (currentUser == null) {
            binding.layoutNoLogged.setVisibility(View.VISIBLE);
            binding.swipeRefreshFavourites.setVisibility(View.GONE);
            binding.progressIndicatorFavourites.setVisibility(View.GONE);
            return;
        } else {
            binding.layoutNoInternetFavourites.setVisibility(View.GONE);
            binding.layoutRecyclerViewFavourites.setVisibility(View.VISIBLE);
        }

        apiService.getFavourites(ApiUtils.API_KEY, 50, currentUser.getUid(), "DESC").enqueue(new Callback<List<ListFavouritesResponse>>() {
            @Override
            public void onResponse(@NonNull Call<List<ListFavouritesResponse>> call, @NonNull Response<List<ListFavouritesResponse>> response) {
                if (response.isSuccessful()) {
                    answersFavouritesAdapter.updateAnswersCats(response.body());
                    binding.progressIndicatorFavourites.hide();
                    binding.progressIndicatorFavourites.setVisibility(View.GONE);
                    binding.swipeRefreshFavourites.setRefreshing(false);

                    Utils.favouritesResponse = response.body();

                    for (ListFavouritesResponse data : Objects.requireNonNull(response.body())) {
                        String catId = data.getImageId() + ":" + data.getId();
                        ManageFavouritesCats.saveFavouritesCats(context, ManageFavouritesCats.FAV_CATS_KEY, catId);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<ListFavouritesResponse>> call, @NonNull Throwable t) {
                Toast.makeText(context, "Nada papa - " + t, Toast.LENGTH_SHORT).show();
                Log.e("Error Response", String.valueOf(t));
                binding.progressIndicatorFavourites.hide();
                binding.progressIndicatorFavourites.setVisibility(View.GONE);
                binding.swipeRefreshFavourites.setRefreshing(false);
            }
        });
    }
}
