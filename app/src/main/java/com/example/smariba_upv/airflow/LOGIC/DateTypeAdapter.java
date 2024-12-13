package com.example.smariba_upv.airflow.LOGIC;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateTypeAdapter extends TypeAdapter<Date> {

    private final SimpleDateFormat sdf;

    public DateTypeAdapter() {
        sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    @Override
    public void write(JsonWriter out, Date value) throws IOException {
        out.value(sdf.format(value));
    }

    @Override
    public Date read(JsonReader in) throws IOException {
        try {
            return sdf.parse(in.nextString());
        } catch (Exception e) {
            throw new IOException("Error al parsear la fecha", e);
        }
    }
}
