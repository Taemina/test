package com.example.myapplication;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Valute {
    @SerializedName("EUR")
    @Expose
    private Val valueEUR;
    @SerializedName("USD")
    @Expose
    private Val valueUSD;

    public Val getValueEUR() {
        return valueEUR;
    }

    public Val getValueUSD() {
        return valueUSD;
    }

    public void setValueEUR(Val valueEUR) {
        this.valueEUR = valueEUR;
    }

    public void setValueUSD(Val valueUSD) {
        this.valueUSD = valueUSD;
    }
}
