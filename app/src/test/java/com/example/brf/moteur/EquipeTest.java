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
        Equipe equipe = new Equipe("Test", Formation.DEUX_TROIS_UN);
        Joueur gardien = creerJoueur(Poste.GARDIEN);
        equipe.ajouterJoueur(creerJoueur(Poste.MILIEU));
        equipe.ajouterJoueur(gardien);

        assertEquals(gardien, equipe.getGardien());
    }

    @Test
    public void getGardien_renvoieNullSiAucunGardien() {
        Equipe equipe = new Equipe("Test", Formation.DEUX_TROIS_UN);
        equipe.ajouterJoueur(creerJoueur(Poste.MILIEU));

        assertNull(equipe.getGardien());
    }

    @Test
    public void ajouterJoueur_refuseLeHuitiemeJoueur() {
        Equipe equipe = new Equipe("Test", Formation.DEUX_TROIS_UN);
        ajouterJoueurs(equipe, Poste.MILIEU, 7);

        boolean accepte = equipe.ajouterJoueur(creerJoueur(Poste.MILIEU));

        assertFalse(accepte);
        assertEquals(7, equipe.getJoueurs().size());
    }

    @Test
    public void getJoueursDuPoste_troisMilieuxEnDeuxTroisUn() {
        Equipe equipe = new Equipe("Test", Formation.DEUX_TROIS_UN);
        ajouterJoueurs(equipe, Poste.GARDIEN, 1);
        ajouterJoueurs(equipe, Poste.DEFENSEUR, 2);
        ajouterJoueurs(equipe, Poste.MILIEU, 3);
        ajouterJoueurs(equipe, Poste.ATTAQUANT, 1);

        assertEquals(3, equipe.getJoueursDuPoste(Poste.MILIEU).size());
    }

    @Test
    public void recupererPEMiTemps_rendLesPEAuxJoueurs() {
        Equipe equipe = new Equipe("Test", Formation.DEUX_TROIS_UN);
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

    @Test
    public void estComplete_vraiPourUneEquipeTroisDeuxUnBienRemplie() {
        Equipe equipe = new Equipe("Test", Formation.TROIS_DEUX_UN);
        ajouterJoueurs(equipe, Poste.GARDIEN, 1);
        ajouterJoueurs(equipe, Poste.DEFENSEUR, 3);
        ajouterJoueurs(equipe, Poste.MILIEU, 2);
        ajouterJoueurs(equipe, Poste.ATTAQUANT, 1);

        assertTrue(equipe.estComplete());
    }

    @Test
    public void estComplete_fauxAvecTroisMilieuxEnTroisDeuxUn() {
        Equipe equipe = new Equipe("Test", Formation.TROIS_DEUX_UN);
        ajouterJoueurs(equipe, Poste.GARDIEN, 1);
        ajouterJoueurs(equipe, Poste.DEFENSEUR, 2);
        ajouterJoueurs(equipe, Poste.MILIEU, 3);
        ajouterJoueurs(equipe, Poste.ATTAQUANT, 1);

        assertFalse(equipe.estComplete());
    }
}
