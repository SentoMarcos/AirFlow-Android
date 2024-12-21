package com.example.smariba_upv.airflow.PRESENTACION;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.smariba_upv.airflow.API.RetrofitClient;
import com.example.smariba_upv.airflow.API.ApiService;
import com.example.smariba_upv.airflow.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapFragment extends Fragment {

    private WebView webView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Inicializa el WebView
        webView = view.findViewById(R.id.webView);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);  // Habilitar JavaScript
        webSettings.setDomStorageEnabled(true);  // Habilitar almacenamiento DOM
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowContentAccess(true);
        webSettings.setAllowUniversalAccessFromFileURLs(true);
        webSettings.setAllowFileAccessFromFileURLs(true);


        // Establecer WebViewClient para que los enlaces se abran en el WebView y no en el navegador
        //webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, android.graphics.Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                Log.d("WebView", "Cargando URL: " + url);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Log.d("WebView", "Carga de página terminada: " + url);
            }
        });



        // Llamar a la API para obtener el HTML del mapa
        getMapaHtml();
    }

    private void getMapaHtml() {
        ApiService apiService = RetrofitClient.getLocalApiServiceHtml();

        // Llamada a la API para obtener el HTML
        Call<String> callHtml = apiService.getMapaHtml();
        Log.d("MapFragment", "Llamada a la API para obtener HTML del mapa");

        callHtml.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    Log.d("MapFragment", "Respuesta exitosa del servidor");

                    // Si la respuesta es exitosa, carga el HTML en el WebView
                    String html = response.body();
                    if (html != null) {
                        Log.d("MapFragment", "HTML recibido del servidor, cargando en WebView");
                        Log.d("MapFragment", "HTML recibido: " + html);
                        webView.loadData(html, "text/html", "UTF-8");
                    } else {
                        Log.e("MapFragment", "El cuerpo de la respuesta está vacío");
                        Toast.makeText(getContext(), "No se recibió HTML del mapa", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e("MapFragment", "Error en la respuesta: Código de respuesta " + response.code());
                    Toast.makeText(getContext(), "Error al obtener el mapa", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                // En caso de fallo en la solicitud
                Log.e("MapFragment", "Error en la solicitud: " + t.getMessage(), t);
                Toast.makeText(getContext(), "Error en la solicitud: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
