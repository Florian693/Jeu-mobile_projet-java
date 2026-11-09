package com.example.brf.moteur;

import org.junit.Test;
import java.util.Random;
import static org.junit.Assert.*;

public class DuelTest {

    @Test
    public void calculerChance_statsEgalesMemeType_vaut50() {
        int chance = Duel.calculerChance(50, 50, TypeJoueur.VITESSE, TypeJoueur.VITESSE);
        assertEquals(50, chance);
    }

    @Test
    public void calculerChance_ecartDeStats_vaut70() {
        int chance = Duel.calculerChance(80, 60, TypeJoueur.VITESSE, TypeJoueur.VITESSE);
        assertEquals(70, chance);
    }

    @Test
    public void calculerChance_avantageDeType_ajoute10() {
        int sansAvantage = Duel.calculerChance(50, 50, TypeJoueur.VITESSE, TypeJoueur.VITESSE);
        int avecAvantage = Duel.calculerChance(50, 50, TypeJoueur.VITESSE, TypeJoueur.TECHNIQUE);
        assertEquals(sansAvantage + 10, avecAvantage);
    }

    @Test
    public void calculerChance_desavantageDeType_retire10() {
        int sansDesavantage = Duel.calculerChance(50, 50, TypeJoueur.VITESSE, TypeJoueur.VITESSE);
        int avecDesavantage = Duel.calculerChance(50, 50, TypeJoueur.TECHNIQUE, TypeJoueur.VITESSE);
        assertEquals(sansDesavantage - 10, avecDesavantage);
    }

    @Test
    public void calculerChance_estBorneeA99() {
        int chance = Duel.calculerChance(99, 1, TypeJoueur.VITESSE, TypeJoueur.TECHNIQUE);
        assertEquals(99, chance);
    }

    @Test
    public void calculerChance_estBorneeA1() {
        int chance = Duel.calculerChance(1, 99, TypeJoueur.TECHNIQUE, TypeJoueur.VITESSE);
        assertEquals(1, chance);
    }

    @Test
    public void tirer_sur10000TiragesAvecChance70() {
        Duel duel = new Duel(new Random(42));
        int reussites = 0;
        for (int i = 0; i < 10000; i++) {
            if (duel.tirer(70)) {
                reussites++;
            }
        }
        assertTrue(reussites >= 6800);
        assertTrue(reussites <= 7200);
    }

    @Test
    public void tirer_chanceZeroNeReussitJamais() {
        Duel duel = new Duel(new Random(42));
        for (int i = 0; i < 1000; i++) {
            assertFalse(duel.tirer(0));
        }
    }
}
