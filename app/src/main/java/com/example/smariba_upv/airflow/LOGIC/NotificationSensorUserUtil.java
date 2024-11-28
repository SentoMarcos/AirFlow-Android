// NotificationSensorUserUtil.java
package com.example.smariba_upv.airflow.LOGIC;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.smariba_upv.airflow.POJO.SensorObject;

import java.util.HashMap;
import java.util.Map;

public class NotificationSensorUserUtil {
    private static final String TAG = "NotificationSensorUserUtil";
    private static final String CHANNEL_ID = "NotificationSensorUserUtilChannel";
    private static final int gasLimit = 100;
    private static Map<String, SensorObject> sensorMap = new HashMap<>();

    public static void TakeAllDataOfSensor(Context context, String uuid, String name, String typegas, int measure, String date, int battery) {
        //limitcheck(context, measure);
        updateSensorData(context, uuid, name, typegas, measure, date, battery);
    }

    private static void updateSensorData(Context context, String uuid, String name, String typegas, int measure, String date, int battery) {
        SensorObject sensor = sensorMap.get(uuid);
        if (sensor == null) {
            sensor = new SensorObject(uuid, name, typegas, measure, date, battery);
            sensorMap.put(uuid, sensor);
        } else {
            sensor.setNombre(name);
            sensor.setMeasure(measure);
            sensor.setTypegas(typegas);
            sensor.setDate(date);
            sensor.setBateria(battery);
        }

        // Enviar broadcast con los datos actualizados
        Intent intent = new Intent("com.example.smariba_upv.airflow.SENSOR_UPDATE");
        intent.putExtra("sensor_uuid", uuid);
        intent.putExtra("sensor_name", name);
        intent.putExtra("sensor_typegas", typegas);
        intent.putExtra("sensor_measure", measure);
        intent.putExtra("sensor_date", date);
        intent.putExtra("sensor_battery", battery);
        context.sendBroadcast(intent);
    }




}