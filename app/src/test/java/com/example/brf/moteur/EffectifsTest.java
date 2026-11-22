package com.example.brf.moteur;

import org.junit.Test;
import static org.junit.Assert.*;
public class EffectifsTest {

    private void verifierQuatreTroisTrois(Equipe equipe) {
        assertEquals(11, equipe.getJoueurs().size());
        assertEquals(1, equipe.getJoueursDuPoste(Poste.GARDIEN).size());
        assertEquals(4, equipe.getJoueursDuPoste(Poste.DEFENSEUR).size());
        assertEquals(3, equipe.getJoueursDuPoste(Poste.MILIEU).size());
        assertEquals(3, equipe.getJoueursDuPoste(Poste.ATTAQUANT).size());
    }

    @Test
    public void realMadrid_onzeJoueursEnQuatreTroisTrois() {
        verifierQuatreTroisTrois(Effectifs.realMadrid());
    }

    @Test
    public void barcelone_onzeJoueursEnQuatreTroisTrois() {
        verifierQuatreTroisTrois(Effectifs.barcelone());
    }

    @Test
    public void chaqueJoueurDeChampADeuxTechniques() {
        for (Joueur joueur : Effectifs.realMadrid().getJoueurs()) {
            if (joueur.getPoste() != Poste.GARDIEN) {
                assertEquals(joueur.getNom(), 2, joueur.getTechniques().size());
            }
        }
        for (Joueur joueur : Effectifs.barcelone().getJoueurs()) {
            if (joueur.getPoste() != Poste.GARDIEN) {
                assertEquals(joueur.getNom(), 2, joueur.getTechniques().size());
            }
        }
    }

    @Test
    public void avantsCentres() {
        assertEquals("Mbappe", Effectifs.realMadrid().getJoueursDuPoste(Poste.ATTAQUANT).get(1).getNom());
        assertEquals("Gabriel Jesus", Effectifs.barcelone().getJoueursDuPoste(Poste.ATTAQUANT).get(1).getNom());
    }
}
