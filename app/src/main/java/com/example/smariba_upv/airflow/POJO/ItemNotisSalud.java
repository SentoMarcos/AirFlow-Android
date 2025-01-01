package com.example.smariba_upv.airflow.POJO;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class ItemNotisSalud {
    private String time;
    private String msg;
    private String state;

    public ItemNotisSalud(String time, String msg, String state) {
        this.time = time;
        this.msg = msg;
        this.state = state;
    }

    public String getTime() {
        return time;
    }

    public String getMsg() {
        return msg;
    }

    public String getState() {
        return state;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getRelativeTime() {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            Date date = sdf.parse(this.time);
            long diffInMillis = new Date().getTime() - date.getTime();

            long hours = TimeUnit.MILLISECONDS.toHours(diffInMillis);
            if (hours < 24) {
                return "hace " + hours + " h";
            }

            long days = TimeUnit.MILLISECONDS.toDays(diffInMillis);
            return "hace " + days + " días";
        } catch (Exception e) {
            e.printStackTrace();
            return this.time; // En caso de error, devuelve el tiempo original.
        }
    }

    @Override
    public String toString() {
        return "ItemNotisSalud{" +
                "time='" + time + '\'' +
                ", msg='" + msg + '\'' +
                ", state='" + state + '\'' +
                '}';
    }
}
