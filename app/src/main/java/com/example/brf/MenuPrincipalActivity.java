package com.example.brf;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MenuPrincipalActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_principal);

        Button btnPresse     = findViewById(R.id.btnPresse);
        Button btnTrophees   = findViewById(R.id.btnTrophees);
        Button btnBureau     = findViewById(R.id.btnBureau);
        Button btnClassement = findViewById(R.id.btnClassement);
        Button btnMatchs     = findViewById(R.id.btnMatchs);
        Button btnRetour     = findViewById(R.id.btnRetour);

        // Retour : ferme cet écran, on revient au précédent
        btnRetour.setOnClickListener(v -> finish());

        // Pour l'instant : un simple message pour tester
        btnPresse.setOnClickListener(v -> message("Presse"));
        btnTrophees.setOnClickListener(v -> message("Trophées"));
        btnBureau.setOnClickListener(v -> message("Bureau"));
        btnClassement.setOnClickListener(v -> message("Classement"));
        btnMatchs.setOnClickListener(v -> message("Matchs"));
    }

    private void message(String nom) {
        Toast.makeText(this, nom + " : à venir", Toast.LENGTH_SHORT).show();
    }
}