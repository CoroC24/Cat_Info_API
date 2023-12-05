package com.catapi.c4.view;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.catapi.c4.R;
import com.catapi.c4.data.InfoCatResponse;
import com.catapi.c4.model.Breeds;
import com.catapi.c4.model.Utils;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CatDescription extends AppCompatActivity {

    private ImageView catImage;
    private TextView tvCatName, tvDescription, tvTemperament, tvOrigin, tvLifeSpan, tvWeight, tvWikipediaUrl;
    private RatingBar ratingBarAdaptability, ratingBarSocialNeeds, ratingBarHealthIssues, ratingBarSheddingLevel, ratingBarIntelligence,
            ratingBarAffectionate, ratingBarEnergyLevel, ratingBarDogFriendly, ratingBarStrangerFriendly, ratingBarChildFriendly, ratingBarGrooming;
    private String catName, catDesc, catTemperament, catOrigin, catLifeSpan, catWeight, wikipediaUrl;
    private int catAdaptability, catSocialNeeds, catHealthIssues, catSheddingLevel, catIntelligence, catAffectionate, catEnergyLevel,
            catDogFriendly, catStrangerFriendly, catChildFriendly, catGrooming;

    private InfoCatResponse infoCatResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cat_description);

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

        infoCatResponse = Utils.infoCatResponse;
        getBreedsInfo(infoCatResponse.getBreeds());
        setBreedsInfo();

        /*RequestOptions options = new RequestOptions().fitCenter().override(catImage.getWidth(), catImage.getHeight());
        Glide.with(getApplicationContext()).load(infoCatResponse.getUrl()).apply(options).into(catImage);*/
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
}