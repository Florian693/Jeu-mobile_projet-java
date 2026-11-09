package com.example.brf.moteur;

import java.util.Random;

public class Duel {

    // hasard
    private Random random;

    public Duel(Random random) {
        this.random = random;
    }

    // Transforme deux stats et les types en un pourcentage de réussite
    public static int calculerChance(int statAttaque, int statDefense, TypeJoueur typeAttaquant,
                                     TypeJoueur typeDefenseur) {
        int bonus = 0;
        if (typeAttaquant.bat(typeDefenseur)) {
            bonus = 10;
        }
        if (typeDefenseur.bat(typeAttaquant)) {
            bonus = -10;
        }

        int chance = 50 + (statAttaque - statDefense) + bonus;

        // jamais - de 1 et jamais + de 99
        return Math.min(99, Math.max(1, chance));
    }

    // Lance le dé et renvoie true avec une probabilité de "chance" %
    public boolean tirer(int chance) {
        return random.nextInt(100) < chance;
    }
}
