package com.example.brf.moteur;

import org.junit.Test;
import static org.junit.Assert.*;

public class ResultatActionTest {

    @Test
    public void renvoieCeQuOnLuiADonne() {
        // att, pas, dri, def, vit, arr, peMax
        Joueur attaquant = new Joueur("A", Poste.ATTAQUANT, TypeJoueur.VITESSE, 80, 50, 70, 30, 90, 0, 50);
        Joueur defenseur = new Joueur("D", Poste.DEFENSEUR, TypeJoueur.PUISSANCE, 30, 50, 40, 80, 50, 0, 50);

        ResultatAction resultat = new ResultatAction(TypeResultat.DUEL_A_JOUER, "Duel !", 62, 0.4, attaquant, defenseur);

        assertEquals(TypeResultat.DUEL_A_JOUER, resultat.getType());
        assertEquals("Duel !", resultat.getMessage());
        assertEquals(62, resultat.getChance());
        assertEquals(0.4, resultat.getFractionJouee(), 0.001);
        assertEquals(attaquant, resultat.getAttaquant());
        assertEquals(defenseur, resultat.getDefenseur());
    }

    @Test
    public void constructeurCourt() {
        ResultatAction resultat = new ResultatAction(TypeResultat.TOUR_TERMINE, "Fin du tour");
        assertEquals(-1, resultat.getChance());
        assertEquals(1.0, resultat.getFractionJouee(), 0.001);
        assertNull(resultat.getAttaquant());
        assertNull(resultat.getDefenseur());
    }
}