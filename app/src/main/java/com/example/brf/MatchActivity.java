package com.example.brf;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.example.brf.moteur.Effectifs;
import com.example.brf.moteur.Equipe;
import com.example.brf.moteur.MoteurMatch;

import java.util.Random;

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
        MoteurMatch match = new MoteurMatch(creerEquipe(equipeGauche), creerEquipe(equipeDroite), new Random());

        MatchView matchView = findViewById(R.id.matchView);
        matchView.commencer(match, equipeGauche, equipeDroite);
    }

    private Equipe creerEquipe(int choix) {
        if (choix == 1) {
            return Effectifs.barcelone();
        }
        return Effectifs.realMadrid();
    }
}
