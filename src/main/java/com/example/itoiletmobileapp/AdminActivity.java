package com.example.itoiletmobileapp;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import com.google.android.material.bottomnavigation.BottomNavigationView;
public class AdminActivity extends AppCompatActivity{

    @Override
    protected  void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        BottomNavigationView bottomNavigationView = findViewById(R.id.adminBottomNav);
        //Bypass Navigation library due to errors
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment1);
        // Get the controller from NavHostFragment
        NavController navController = navHostFragment.getNavController();
        // Connect view to the controller
        NavigationUI.setupWithNavController(bottomNavigationView, navController);

    }

}
