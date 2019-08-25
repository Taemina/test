package com.example.myapplication;

import retrofit2.Call;

import retrofit2.http.GET;
import retrofit2.http.Query;

public interface OpenRate {
    @GET("daily_json.js")
    Call<RateRequest> loadWeather(@Query("Valute") String rate);
}
