package com.catapi.c4.data.remote;

import com.catapi.c4.data.AddFavouritesResponse;
import com.catapi.c4.data.DeleteFavouritesResponse;
import com.catapi.c4.data.InfoCatResponse;
import com.catapi.c4.data.ListCatResponse;
import com.catapi.c4.data.ListFavouritesResponse;
import com.catapi.c4.model.AddFavouritesData;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    @GET("images/search?limit=50&has_breeds=1")
    Call<List<ListCatResponse>> getAnswers(@Header("x-api-key") String apiKey);

    @GET("images/{id}")
    Call<InfoCatResponse> getAnswers(@Header("x-api-key") String apiKey, @Path("id") String id);

    @GET("favourites")
    Call<List<ListFavouritesResponse>> getFavourites(@Header("x-api-key") String apiKey,
                                                     @Query("limit") int limit,
                                                     @Query("sub_id") String userId,
                                                     @Query("order") String order);

    @Headers("Content-Type: application/json")
    @POST("favourites")
    Call<AddFavouritesResponse> addFavourites(@Header("x-api-key") String apiKey, @Body AddFavouritesData favouritesData);

    @Headers("Content-Type: application/json")
    @DELETE("favourites/{favouriteId}")
    Call<DeleteFavouritesResponse> deleteFavourites(@Header("x-api-key") String apiKey, @Path("favouriteId") String id);

    @GET("images/search")
    Call<List<ListCatResponse>> getCatsBySearch(@Header("x-api-key") String apiKey,
                                                @Query("limit") int limit,
                                                @Query("has_breeds") int hasBreeds,
                                                @Query("breed_ids") String catName);
}
