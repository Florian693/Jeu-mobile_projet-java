package com.example.brf.moteur;

// Choix du porteur pendant un duel.
// Les regles du choix (bonus, stat, effet) sont rangees ici,
// pour que MoteurMatch n'ait qu'a appeler choix.bonus(), choix.faitAvancer()...
public enum ChoixAttaque {
    DRIBBLE,  // DRI, pas de bonus, avance de 6 si gagne
    ESQUIVE;  // VIT, +15, reste sur place si gagne

    // Bonus des choix "surs" (esquive et blocage)
    public static final int BONUS_SUR = 15;

    // Bonus de chance donne par ce choix
    public int bonus() {
        if (this == ESQUIVE) {
            return BONUS_SUR;
        }
        return 0;
    }

    // Vrai si le porteur avance quand il gagne le duel
    public boolean faitAvancer() {
        return this == DRIBBLE;
    }

    // La stat du joueur utilisee pour ce choix
    public int statUtilisee(Joueur joueur) {
        if (this == ESQUIVE) {
            return joueur.getVit();
        }
        return joueur.getDri();
    }
}