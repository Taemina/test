package com.example.myapplication;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RateRequest {
    @SerializedName("Valute")
    @Expose
    private Valute valutes;

    public Valute getValutes() {
        return valutes;
    }

    public void setValutes(Valute valutes) {
        this.valutes = valutes;
    }
}
