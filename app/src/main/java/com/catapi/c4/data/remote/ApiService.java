package com.catapi.c4.data.remote;

import com.catapi.c4.data.InfoCatResponse;
import com.catapi.c4.data.ListCatResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;

public interface ApiService {

    @GET("images/search")
    Call<List<ListCatResponse>> getAnswers();

    @GET("images/search?limit=50&has_breeds=1")
    Call<List<ListCatResponse>> getAnswers(@Header("x-api-key") String apiKey);

    @GET("images/{id}")
    Call<InfoCatResponse> getAnswers(@Header("x-api-key") String apiKey, @Path("id") String id);
}
