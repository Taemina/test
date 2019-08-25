package com.example.myapplication;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Val {
    @SerializedName("Value")
    @Expose
    private double value;

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
}
