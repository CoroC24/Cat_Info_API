package com.catapi.c4.data.remote;

public class ApiUtils {

    public static final String BASE_URL = "https://api.thecatapi.com/v1/";
    public static final String API_KEY = "live_i3Jcaxbj7jUWbJQpCXHOhFFEW861aq4mHiK8l5qcrYkx3SYdTqtibWDAuDJH3UkB";

    public static ApiService getApiService() {
        return RetrofitClient.getClient(BASE_URL).create(ApiService.class);
    }
}
