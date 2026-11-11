package com.example.brf.moteur;

// Choix du defenseur pendant un duel.
// La stat utilisee est toujours la DEF : seuls le bonus et l'effet changent.
public enum ChoixDefense {
    TACLE,    // pas de bonus, avance de 6 si gagne
    BLOCAGE;  // +15, reste sur place si gagne

    // Bonus de chance donne par ce choix (meme valeur que l'esquive)
    public int bonus() {
        if (this == BLOCAGE) {
            return ChoixAttaque.BONUS_SUR;
        }
        return 0;
    }

    // Vrai si le defenseur avance quand il gagne le duel
    public boolean faitAvancer() {
        return this == TACLE;
    }
}