package com.catapi.c4.view.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.catapi.c4.R;
import com.catapi.c4.data.AddFavouritesResponse;
import com.catapi.c4.data.DeleteFavouritesResponse;
import com.catapi.c4.data.InfoCatResponse;
import com.catapi.c4.data.ListFavouritesResponse;
import com.catapi.c4.data.remote.ApiService;
import com.catapi.c4.data.remote.ApiUtils;
import com.catapi.c4.model.AddFavouritesData;
import com.catapi.c4.model.Breeds;
import com.catapi.c4.model.ManageFavouritesCats;
import com.catapi.c4.model.Utils;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CatDescription extends AppCompatActivity {

    private ImageView catImage;
    private TextView tvCatName, tvDescription, tvTemperament, tvOrigin, tvLifeSpan, tvWeight, tvWikipediaUrl;
    private RatingBar ratingBarAdaptability, ratingBarSocialNeeds, ratingBarHealthIssues, ratingBarSheddingLevel, ratingBarIntelligence,
            ratingBarAffectionate, ratingBarEnergyLevel, ratingBarDogFriendly, ratingBarStrangerFriendly, ratingBarChildFriendly, ratingBarGrooming;
    private String catName, catDesc, catTemperament, catOrigin, catLifeSpan, catWeight, wikipediaUrl;
    private int catAdaptability, catSocialNeeds, catHealthIssues, catSheddingLevel, catIntelligence, catAffectionate, catEnergyLevel,
            catDogFriendly, catStrangerFriendly, catChildFriendly, catGrooming;
    private ImageButton favouritesButton;
    private ApiService apiService;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    private InfoCatResponse infoCatResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cat_description);

        mAuth = FirebaseAuth.getInstance();

        catImage = findViewById(R.id.imgCat);
        tvCatName = findViewById(R.id.tvCatName);
        tvDescription = findViewById(R.id.tvDescription);
        tvTemperament = findViewById(R.id.tvTemperament);
        tvOrigin = findViewById(R.id.tvOrigin);
        tvLifeSpan = findViewById(R.id.tvLifeSpan);
        tvWeight = findViewById(R.id.tvWeight);
        tvWikipediaUrl = findViewById(R.id.tvWikipediaUrl);
        ratingBarAdaptability = findViewById(R.id.ratingAdaptability);
        ratingBarSocialNeeds = findViewById(R.id.ratingSocialNeeds);
        ratingBarHealthIssues = findViewById(R.id.ratingHealthIssues);
        ratingBarSheddingLevel = findViewById(R.id.ratingSheddingLevel);
        ratingBarIntelligence = findViewById(R.id.ratingIntelligence);
        ratingBarAffectionate = findViewById(R.id.ratingAffectionate);
        ratingBarEnergyLevel = findViewById(R.id.ratingEnergyLevel);
        ratingBarDogFriendly = findViewById(R.id.ratingDogFriendly);
        ratingBarStrangerFriendly = findViewById(R.id.ratingStrangerFriendly);
        ratingBarChildFriendly = findViewById(R.id.ratingChildFriendly);
        ratingBarGrooming = findViewById(R.id.ratingGrooming);
        favouritesButton = findViewById(R.id.add_favourites_button);
        apiService = ApiUtils.getApiService();

        infoCatResponse = Utils.infoCatResponse;
        getBreedsInfo(infoCatResponse.getBreeds());
        setBreedsInfo();

        favouritesButton.setOnClickListener(view -> addFavourites());
    }

    @Override
    protected void onStart() {
        super.onStart();
        currentUser = mAuth.getCurrentUser();

        String[] getFavCats = ManageFavouritesCats.getFavouritesCatsValues(getApplicationContext(), ManageFavouritesCats.FAV_CATS_KEY);

        for (String catsIds : (getFavCats != null ? getFavCats : new String[0])) {
            String[] favCatsIds = catsIds.split(":");

            if (infoCatResponse.getId().equals(favCatsIds[0])) {
                if (Utils.isNightModeActive()) {
                    favouritesButton.setImageResource(R.drawable.ic_favourite_white_filled);
                } else {
                    favouritesButton.setImageResource(R.drawable.ic_favourite_black_filled);
                }
            }
        }
    }

    public void getBreedsInfo(List<Breeds> breedsInfo) {
        for (Breeds breeds : breedsInfo) {
            catName = breeds.getName();
            catDesc = breeds.getDescription();
            catTemperament = breeds.getTemperament();
            catOrigin = breeds.getOrigin();
            catLifeSpan = breeds.getLifeSpan();
            catWeight = breeds.getWeight().getMetric();
            wikipediaUrl = breeds.getWikipediaUrl();
            catAdaptability = breeds.getAdaptability();
            catSocialNeeds = breeds.getSocialNeeds();
            catHealthIssues = breeds.getHealthIssues();
            catSheddingLevel = breeds.getSheddingLevel();
            catIntelligence = breeds.getIntelligence();
            catAffectionate = breeds.getAffectionLevel();
            catEnergyLevel = breeds.getEnergyLevel();
            catDogFriendly = breeds.getDogFriendly();
            catStrangerFriendly = breeds.getStrangerFriendly();
            catChildFriendly = breeds.getChildFriendly();
            catGrooming = breeds.getGrooming();
        }
    }

    public void setBreedsInfo() {
        Picasso.get().load(infoCatResponse.getUrl()).centerCrop().resize(1080, 720).into(catImage);

        tvCatName.setText(catName);
        tvDescription.setText(catDesc);
        tvTemperament.setText(catTemperament);
        tvOrigin.setText(catOrigin);
        tvLifeSpan.setText(catLifeSpan);
        tvWeight.setText(catWeight);
        tvWikipediaUrl.setText(wikipediaUrl);

        ratingBarAdaptability.setRating(catAdaptability);
        ratingBarSocialNeeds.setRating(catSocialNeeds);
        ratingBarHealthIssues.setRating(catHealthIssues);
        ratingBarSheddingLevel.setRating(catSheddingLevel);
        ratingBarIntelligence.setRating(catIntelligence);
        ratingBarAffectionate.setRating(catAffectionate);
        ratingBarEnergyLevel.setRating(catEnergyLevel);
        ratingBarDogFriendly.setRating(catDogFriendly);
        ratingBarStrangerFriendly.setRating(catStrangerFriendly);
        ratingBarChildFriendly.setRating(catChildFriendly);
        ratingBarGrooming.setRating(catGrooming);
    }

    private void addFavourites() {
        if (currentUser == null) {
            Snackbar.make(findViewById(android.R.id.content), "Log in to your account to add to favourites", Snackbar.LENGTH_SHORT).show();
            return;
        }

        AddFavouritesData favouritesData = new AddFavouritesData();
        favouritesData.setImageId(Utils.infoCatResponse.getId());
        favouritesData.setSubId(currentUser.getUid());
        apiService.addFavourites(ApiUtils.API_KEY, favouritesData).enqueue(new Callback<AddFavouritesResponse>() {
            @Override
            public void onResponse(@NonNull Call<AddFavouritesResponse> call, @NonNull Response<AddFavouritesResponse> response) {
                if (response.isSuccessful()) {
                    Snackbar.make(findViewById(android.R.id.content), "Cat added to favourites", Snackbar.LENGTH_SHORT).show();

                    for (ListFavouritesResponse idsFav : Utils.favouritesResponse) {
                        if (idsFav.getImageId().equals(favouritesData.getImageId())) {
                            String catId = favouritesData.getImageId() + ":" + idsFav.getId();
                            ManageFavouritesCats.saveFavouritesCats(getApplicationContext(), ManageFavouritesCats.FAV_CATS_KEY, catId);
                        }
                    }

                    if (Utils.isNightModeActive()) {
                        favouritesButton.setImageResource(R.drawable.ic_favourite_white_filled);
                    } else {
                        favouritesButton.setImageResource(R.drawable.ic_favourite_black_filled);
                    }
                }

                if (response.code() == 400) {
                    for (ListFavouritesResponse idsFav : Utils.favouritesResponse) {
                        if (idsFav.getImageId().equals(favouritesData.getImageId())) {
                            deleteFavourites(favouritesData.getImageId(), String.valueOf(idsFav.getId()));
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<AddFavouritesResponse> call, @NonNull Throwable t) {
                Toast.makeText(getApplicationContext(), "Nada papa - " + t, Toast.LENGTH_SHORT).show();
                Log.e("Error Response", String.valueOf(t));
            }
        });
    }

    private void deleteFavourites(String catImageId, String catFavouriteId) {
        if (currentUser == null) {
            Snackbar.make(findViewById(android.R.id.content), "Log in to your account to delete cats favourites", Snackbar.LENGTH_SHORT).show();
            return;
        }

        apiService.deleteFavourites(ApiUtils.API_KEY, catFavouriteId).enqueue(new Callback<DeleteFavouritesResponse>() {
            @Override
            public void onResponse(@NonNull Call<DeleteFavouritesResponse> call, @NonNull Response<DeleteFavouritesResponse> response) {
                if (response.isSuccessful()) {
                    Snackbar.make(findViewById(android.R.id.content), "Cat deleted to favourites", Snackbar.LENGTH_SHORT).show();

                    if (Utils.isNightModeActive()) {
                        favouritesButton.setImageResource(R.drawable.ic_favourite_white);
                    } else {
                        favouritesButton.setImageResource(R.drawable.ic_favourite_black);
                    }

                    ManageFavouritesCats.deleteFavouritesCats(getApplicationContext(), ManageFavouritesCats.FAV_CATS_KEY, catImageId + ":" + catFavouriteId);
                }
            }

            @Override
            public void onFailure(@NonNull Call<DeleteFavouritesResponse> call, @NonNull Throwable t) {
                Toast.makeText(getApplicationContext(), "Nada papa - " + t, Toast.LENGTH_SHORT).show();
                Log.e("Error Response", String.valueOf(t));
            }
        });
    }
}