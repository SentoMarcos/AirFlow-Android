package com.example.smariba_upv.airflow.API.MODELS;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class BoundsResponse {

    @SerializedName("status")
    private String status;

    @SerializedName("data")
    private List<Data> data;

    public String getStatus() {
        return status;
    }

    public List<Data> getData() {
        return data;
    }

    public static class Data {

        @SerializedName("lat")
        private double lat;

        @SerializedName("lon")
        private double lon;

        @SerializedName("aqi")
        private int aqi;

        public double getLat() {
            return lat;
        }

        public double getLon() {
            return lon;
        }

        public int getAqi() {
            return aqi;
        }
    }
}
