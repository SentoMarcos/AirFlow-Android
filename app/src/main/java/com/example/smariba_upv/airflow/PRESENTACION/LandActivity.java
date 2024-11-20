package com.example.smariba_upv.airflow.PRESENTACION;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.example.smariba_upv.airflow.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class LandActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_land);

        bottomNavigationView = findViewById(R.id.navtabgroup);

        // Set listener for navigation item selection
        bottomNavigationView.setOnNavigationItemSelectedListener(this::onNavigationItemSelected);

        // Initialize the first fragment (default selection)
        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
            bottomNavigationView.setSelectedItemId(R.id.item_1); // Set default selection
        }
    }

    /**
     * Handles navigation item selection and loads the corresponding fragment.
     *
     * @param item Selected menu item.
     * @return true if the selection is handled, false otherwise.
     */
    private boolean onNavigationItemSelected(@NonNull android.view.MenuItem item) {
        Fragment selectedFragment = null;

        if (item.getItemId() == R.id.item_1) {
            selectedFragment = new HomeFragment();
        } else if (item.getItemId() == R.id.item_2) {
            selectedFragment = new MapFragment();
        } else if (item.getItemId() == R.id.item_3) {
            selectedFragment = new SaludFragment();
        } else if (item.getItemId() == R.id.item_4) {
            selectedFragment = new SensorFragment();
        } else if (item.getItemId() == R.id.item_5) {
            selectedFragment = new PerfilFragment();
        }

        return loadFragment(selectedFragment);
    }

    /**
     * Loads the given fragment into the fragment container.
     *
     * @param fragment The fragment to load.
     * @return true if the fragment is loaded, false otherwise.
     */
    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }
}
