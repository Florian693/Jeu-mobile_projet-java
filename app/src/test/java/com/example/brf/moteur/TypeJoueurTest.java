package com.example.brf.moteur;

import org.junit.Test;
import static org.junit.Assert.*;

public class TypeJoueurTest {

    @Test
    public void bat_lesTroisVictoires() {
        assertTrue(TypeJoueur.VITESSE.bat(TypeJoueur.TECHNIQUE));
        assertTrue(TypeJoueur.TECHNIQUE.bat(TypeJoueur.PUISSANCE));
        assertTrue(TypeJoueur.PUISSANCE.bat(TypeJoueur.VITESSE));
    }

    @Test
    public void bat_lesTroisDefaites() {
        assertFalse(TypeJoueur.TECHNIQUE.bat(TypeJoueur.VITESSE));
        assertFalse(TypeJoueur.PUISSANCE.bat(TypeJoueur.TECHNIQUE));
        assertFalse(TypeJoueur.VITESSE.bat(TypeJoueur.PUISSANCE));
    }

    @Test
    public void bat_deuxTypesIdentiquesNeSeBattentPas() {
        for (TypeJoueur type : TypeJoueur.values()) {
            assertFalse(type.bat(type));
        }
    }
}