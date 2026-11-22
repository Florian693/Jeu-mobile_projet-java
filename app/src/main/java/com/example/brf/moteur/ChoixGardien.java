package com.example.brf.moteur;

// Choix du gardien face a un tir (meme idee que ChoixDefense).
public enum ChoixGardien {
    ARRET,     // pas de bonus, le gardien garde le ballon
    PLONGEON;  // +15, mais le ballon est relache au joueur le plus proche

    // Bonus de chance donne par ce choix
    public int bonus() {
        if (this == PLONGEON) {
            return ChoixAttaque.BONUS_SUR;
        }
        return 0;
    }

    // Vrai si le gardien garde le ballon quand il arrete le tir
    public boolean garderLeBallon() {
        return this == ARRET;
    }
}
