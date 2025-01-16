package com.example.smariba_upv.airflow.PRESENTACION;

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.smariba_upv.airflow.PRESENTACION.Helpers.ImagePagerAdapter;
import com.example.smariba_upv.airflow.R;

import java.util.Arrays;

public class PasaralainfoAndroid extends AppCompatActivity {

    private LinearLayout dotsContainer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pasarale_android);

        ViewPager2 viewPager = findViewById(R.id.viewPager);
        dotsContainer = findViewById(R.id.dotsContainer);

        // Lista de imágenes
        Integer[] imageResources = {
                R.drawable.bienvenido,
                R.drawable.compra3,
                R.drawable.comofunciona
        };

        ImagePagerAdapter adapter = new ImagePagerAdapter(this, Arrays.asList(imageResources));
        viewPager.setAdapter(adapter);

        // Configura los puntos
        setupDots(imageResources.length);

        // Cambia el color del punto activo cuando el usuario desliza
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                updateDots(position);
            }
        });
    }

    private void setupDots(int count) {
        for (int i = 0; i < count; i++) {
            ImageView dot = new ImageView(this);
            GradientDrawable drawable = new GradientDrawable();
            drawable.setShape(GradientDrawable.OVAL);
            drawable.setSize(20, 20);
            drawable.setColor(getResources().getColor(android.R.color.darker_gray, null));
            dot.setImageDrawable(drawable);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(8, 0, 8, 0);
            dotsContainer.addView(dot, params);
        }
        updateDots(0); // Inicializa el primer punto como activo
    }

    private void updateDots(int selectedPosition) {
        for (int i = 0; i < dotsContainer.getChildCount(); i++) {
            ImageView dot = (ImageView) dotsContainer.getChildAt(i);
            GradientDrawable drawable = (GradientDrawable) dot.getDrawable();
            if (i == selectedPosition) {
                drawable.setColor(getResources().getColor(android.R.color.white, null));
            } else {
                drawable.setColor(getResources().getColor(android.R.color.darker_gray, null));
            }
        }
    }
}
