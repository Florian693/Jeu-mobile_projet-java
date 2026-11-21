package com.example.brf.moteur;

import org.junit.Test;
import static org.junit.Assert.*;

public class EquipeTest {

    // joueur pour test
    private Joueur creerJoueur(Poste poste) {
        return new Joueur("Test", poste, TypeJoueur.VITESSE, 50, 50, 50, 50, 50, 50, 50);
    }

    // Ajt "nombre" joueurs du même poste dans l'équipe
    private void ajouterJoueurs(Equipe equipe, Poste poste, int nombre) {
        for (int i = 0; i < nombre; i++) {
            equipe.ajouterJoueur(creerJoueur(poste));
        }
    }

    @Test
    public void getGardien_renvoieLeGardienAjoute() {
        Equipe equipe = new Equipe("Test");
        Joueur gardien = creerJoueur(Poste.GARDIEN);
        equipe.ajouterJoueur(creerJoueur(Poste.MILIEU));
        equipe.ajouterJoueur(gardien);

        assertEquals(gardien, equipe.getGardien());
    }

    @Test
    public void getGardien_renvoieNullSiAucunGardien() {
        Equipe equipe = new Equipe("Test");
        equipe.ajouterJoueur(creerJoueur(Poste.MILIEU));

        assertNull(equipe.getGardien());
    }

    @Test
    public void ajouterJoueur_refuseLeDouziemeJoueur() {
        Equipe equipe = new Equipe("Test");
        ajouterJoueurs(equipe, Poste.MILIEU, 11);

        boolean accepte = equipe.ajouterJoueur(creerJoueur(Poste.MILIEU));

        assertFalse(accepte);
        assertEquals(11, equipe.getJoueurs().size());
    }

    @Test
    public void getJoueursDuPoste_quatreDefenseursEnQuatreTroisTrois() {
        Equipe equipe = new Equipe("Test");
        ajouterJoueurs(equipe, Poste.GARDIEN, 1);
        ajouterJoueurs(equipe, Poste.DEFENSEUR, 4);
        ajouterJoueurs(equipe, Poste.MILIEU, 3);
        ajouterJoueurs(equipe, Poste.ATTAQUANT, 3);

        assertEquals(4, equipe.getJoueursDuPoste(Poste.DEFENSEUR).size());
    }

    @Test
    public void recupererPEMiTemps_rendLesPEAuxJoueurs() {
        Equipe equipe = new Equipe("Test");
        Joueur a = creerJoueur(Poste.MILIEU);
        Joueur b = creerJoueur(Poste.DEFENSEUR);
        equipe.ajouterJoueur(a);
        equipe.ajouterJoueur(b);
        a.depenserPE(30);   // il reste 20 PE
        b.depenserPE(30);   // il reste 20 PE

        equipe.recupererPEMiTemps(20);

        assertEquals(40, a.getPeActuel());
        assertEquals(40, b.getPeActuel());
    }
}