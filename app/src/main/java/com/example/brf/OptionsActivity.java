package com.example.brf;

import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class OptionsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        Button btnRetour = findViewById(R.id.btnRetourOptions);
        btnRetour.setOnClickListener(v -> finish());
    }
}