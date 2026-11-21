package com.example.brf;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

// Ecran 2 : chaque joueur choisit son equipe avec les fleches < >.
public class ChoixEquipesActivity extends AppCompatActivity {

    // Les equipes disponibles : meme index dans les deux tableaux
    private static final String[] NOMS = {"REAL MADRID", "FC BARCELONE"};
    private static final int[] LOGOS = {R.drawable.logo_real, R.drawable.logo_barca};

    // Equipe choisie par chaque joueur (index dans les tableaux)
    private int equipeGauche = 0;  // joueur 1 : Real au depart
    private int equipeDroite = 1;  // joueur 2 : PSG au depart

    private ImageView imgLogoGauche, imgLogoDroite;
    private TextView tvNomGauche, tvNomDroite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_choix_equipes);

        imgLogoGauche = findViewById(R.id.imgLogoGauche);
        imgLogoDroite = findViewById(R.id.imgLogoDroite);
        tvNomGauche = findViewById(R.id.tvNomGauche);
        tvNomDroite = findViewById(R.id.tvNomDroite);

        // Fleches du joueur 1
        findViewById(R.id.btnGauchePrec).setOnClickListener(v -> {
            equipeGauche = suivante(equipeGauche, -1);
            afficher();
        });
        findViewById(R.id.btnGaucheSuiv).setOnClickListener(v -> {
            equipeGauche = suivante(equipeGauche, +1);
            afficher();
        });

        // Fleches du joueur 2
        findViewById(R.id.btnDroitePrec).setOnClickListener(v -> {
            equipeDroite = suivante(equipeDroite, -1);
            afficher();
        });
        findViewById(R.id.btnDroiteSuiv).setOnClickListener(v -> {
            equipeDroite = suivante(equipeDroite, +1);
            afficher();
        });

        Button btnJouerMatch = findViewById(R.id.btnJouerMatch);
        btnJouerMatch.setOnClickListener(v -> {
                        Intent intent = new Intent(this, MatchActivity.class);
                        intent.putExtra("equipeGauche", equipeGauche);
                        intent.putExtra("equipeDroite", equipeDroite);
                        startActivity(intent);
                    });

        findViewById(R.id.btnQuitterChoix).setOnClickListener(v -> finish());

        afficher();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    // Passe a l'equipe suivante (+1) ou precedente (-1), en boucle
    private int suivante(int index, int pas) {
        return (index + pas + NOMS.length) % NOMS.length;
    }

    // Met a jour logos et noms selon les choix
    private void afficher() {
        imgLogoGauche.setImageResource(LOGOS[equipeGauche]);
        tvNomGauche.setText(NOMS[equipeGauche]);
        imgLogoDroite.setImageResource(LOGOS[equipeDroite]);
        tvNomDroite.setText(NOMS[equipeDroite]);
    }
}