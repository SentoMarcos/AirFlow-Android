/**
 * @file QRreader.java
 * @brief Clase que permite leer un código QR
 * @details Clase que permite leer un código QR y registrar un sensor en la base de datos
 */

package com.example.smariba_upv.airflow.PRESENTACION;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.smariba_upv.airflow.LOGIC.PeticionesUserUtil;
import com.example.smariba_upv.airflow.R;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

/**
 * @class QRreader
 * @brief Clase que permite leer un código QR
 * @details Clase que permite leer un código QR y registrar un sensor en la base de datos
 */
public class QRreader extends AppCompatActivity {
    /**
     * @param cameraSource Fuente de la cámara
     * @param cameraView Vista de la cámara
     * @param MY_PERMISSIONS_REQUEST_CAMERA Permisos de la cámara
     * @param token Token
     * @param tokenanterior Token anterior
     */
    private CameraSource cameraSource;
    private SurfaceView cameraView;
    private final int MY_PERMISSIONS_REQUEST_CAMERA = 1;
    private String token = "";
    private String tokenanterior = "";

    /**
     * @param savedInstanceState Instancia guardada
     * @brief Método que se ejecuta al crear la actividad
     * @details Método que se ejecuta al crear la actividad y permite leer un código QR
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrreader);

        cameraView = findViewById(R.id.camera_view);
        Button btnCancel = findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(v -> finish());
        initQR();
    }

    /**
     * @function initQR
     * @brief Método que inicializa el lector de QR
     * @details Método que inicializa el lector de QR y registra un sensor en la base de datos
     */
    public void initQR() {

        /**
         * @param barcodeDetector Detector de códigos de barras
         * @param cameraSource Fuente de la cámara
         *
         */
        // creo el detector qr
        BarcodeDetector barcodeDetector =
                new BarcodeDetector.Builder(this)
                        .setBarcodeFormats(Barcode.ALL_FORMATS)
                        .build();

        // creo la camara
        cameraSource = new CameraSource
                .Builder(this, barcodeDetector)
                .setRequestedPreviewSize(1600, 1024)
                .setAutoFocusEnabled(true) //you should add this feature
                .build();

        // listener de ciclo de vida de la camara
        cameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {

                // verifico si el usuario dio los permisos para la camara
                if (ActivityCompat.checkSelfPermission(QRreader.this, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        // verificamos la version de ANdroid que sea al menos la M para mostrar
                        // el dialog de la solicitud de la camara
                        if (shouldShowRequestPermissionRationale(
                                Manifest.permission.CAMERA)) ;
                        requestPermissions(new String[]{Manifest.permission.CAMERA},
                                MY_PERMISSIONS_REQUEST_CAMERA);
                    }
                    return;
                } else {
                    try {
                        cameraSource.start(cameraView.getHolder());
                    } catch (IOException ie) {
                        Log.e("CAMERA SOURCE", ie.getMessage());
                    }
                }
            }

            /**
             * @param holder Holder
             * @param format Formato
             * @param width  Ancho
             * @param height Alto
             * @function surfaceChanged
             */
            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            /**
             * @param holder Holder
             * @function surfaceDestroyed
             */
            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });

        /**
         * @function setProcessor
         * @brief Método que procesa el código QR
         * @details Método que procesa el código QR y registra un sensor en la base de datos
         */
        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();

                if (barcodes.size() > 0) {
                    // Obtenemos el token
                    token = barcodes.valueAt(0).displayValue;

                    // Verificamos que el token anterior no sea igual al actual
                    if (!token.equals(tokenanterior)) {
                        // Guardamos el último token procesado
                        tokenanterior = token;
                        Log.i("token", token);

                        // Mostrar el popup del éxito del escaneo
                        runOnUiThread(() -> {
                            showPopup("Éxito", "Código QR escaneado: " + token);
                            PeticionesUserUtil.registrarSensor(token, QRreader.this);

                        });

                        new Thread(() -> {
                            try {
                                synchronized (this) {
                                    wait(5000);
                                    // Limpiamos el token
                                    tokenanterior = "";
                                }
                            } catch (InterruptedException e) {
                                Log.e("Error", "Waiting didn't work!!");
                                e.printStackTrace();
                            }
                        }).start();
                    }
                } else {
                    // Si no se detectan códigos QR, muestra un popup de error
                    //runOnUiThread(() -> showPopup("Error", "No se pudo escanear el código QR. Inténtalo de nuevo."));
                }
            }
        });
    }
    private void showPopup(String title, String message) {
        runOnUiThread(() -> {
            new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle(title)
                    .setMessage(message)
                    .setPositiveButton("OK", (dialog, which) -> {
                        dialog.dismiss();
                        finish();
                    })
                    .show();
        });
    }


}
