package com.example.brf;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

// Ecran 3 : le match, plein ecran.
public class MatchActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match);

        // Plein ecran : on cache la barre de statut et de navigation
        WindowInsetsControllerCompat controleur =
                WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        controleur.hide(WindowInsetsCompat.Type.systemBars());
        controleur.setSystemBarsBehavior(
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);

        // Equipes choisies a l'ecran 2
        int equipeGauche = getIntent().getIntExtra("equipeGauche", 0);
        int equipeDroite = getIntent().getIntExtra("equipeDroite", 1);

        MatchView matchView = findViewById(R.id.matchView);
        matchView.setEquipes(equipeGauche, equipeDroite);
    }
}