package com.example.brf.moteur;

import org.junit.Test;
import static org.junit.Assert.*;

public class FormationTest {

    @Test
    public void nombreAuPoste_milieuEnDeuxTroisUn() {
        assertEquals(3, Formation.DEUX_TROIS_UN.nombreAuPoste(Poste.MILIEU));
    }

    @Test
    public void nombreAuPoste_defenseurEnTroisDeuxUn() {
        assertEquals(3, Formation.TROIS_DEUX_UN.nombreAuPoste(Poste.DEFENSEUR));
    }

    @Test
    public void nombreAuPoste_gardienToujoursUn() {
        // On vérifie les 3 formations : le gardien vaut 1 partout
        for (Formation formation : Formation.values()) {
            assertEquals(1, formation.nombreAuPoste(Poste.GARDIEN));
        }
    }

    @Test
    public void sommeDesPostes_vaut7PourChaqueFormation() {
        for (Formation formation : Formation.values()) {
            int total = 0;
            for (Poste poste : Poste.values()) {
                total = total + formation.nombreAuPoste(poste);
            }
            assertEquals(7, total);
        }
    }

    @Test
    public void getNom_formatAttendu() {
        assertEquals("2-3-1", Formation.DEUX_TROIS_UN.getNom());
        assertEquals("3-2-1", Formation.TROIS_DEUX_UN.getNom());
        assertEquals("2-2-2", Formation.DEUX_DEUX_DEUX.getNom());
    }
}
