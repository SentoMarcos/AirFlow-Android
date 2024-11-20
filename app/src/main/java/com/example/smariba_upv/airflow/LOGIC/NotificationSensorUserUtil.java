package com.example.smariba_upv.airflow.LOGIC;

import android.content.Context;
import com.example.smariba_upv.airflow.Services.NotificationService;

public class NotificationSensorUserUtil {
    private static final String TAG = "NotificationSensorUserUtil";
    private static final String CHANNEL_ID = "NotificationSensorUserUtilChannel";
    private static final int gasLimit = 100;

    public static void limitcheck(Context context, int gasValue) {
        if (gasValue > gasLimit) {
            NotificationService.sendNotification(context, "Gas Alert", "Gas value is above the limit");
        }
    }
}