package com.example.brf.moteur;

import org.junit.Test;
import static org.junit.Assert.*;

public class ChoixAttaqueTest {

    @Test
    public void bonus() {
        assertEquals(15, ChoixAttaque.ESQUIVE.bonus());
        assertEquals(0, ChoixAttaque.DRIBBLE.bonus());
    }

    @Test
    public void faitAvancer() {
        assertTrue(ChoixAttaque.DRIBBLE.faitAvancer());
        assertFalse(ChoixAttaque.ESQUIVE.faitAvancer());
    }

    @Test
    public void statUtilisee() {
        // att, pas, dri, def, vit, arr, peMax
        Joueur joueur = new Joueur("Test", Poste.MILIEU, TypeJoueur.TECHNIQUE, 50, 50, 80, 50, 60, 0, 50);
        assertEquals(80, ChoixAttaque.DRIBBLE.statUtilisee(joueur));
        assertEquals(60, ChoixAttaque.ESQUIVE.statUtilisee(joueur));
    }
}