package com.example.brf.moteur;

import org.junit.Test;
import static org.junit.Assert.*;

public class OrdresTourTest {

    // att, pas, dri, def, vit, arr, peMax
    private Joueur creerJoueur(String nom) {
        return new Joueur(nom, Poste.MILIEU, TypeJoueur.TECHNIQUE, 50, 50, 50, 50, 50, 0, 50);
    }

    @Test
    public void ordresVides() {
        OrdresTour ordres = new OrdresTour();
        assertNull(ordres.getAction());
        assertEquals(0, ordres.getNombreFleches());
    }

    @Test
    public void troisFlechesAccepteesQuatriemeRefusee() {
        OrdresTour ordres = new OrdresTour();
        assertTrue(ordres.ajouterFleche(creerJoueur("A"), new Position(10, 10)));
        assertTrue(ordres.ajouterFleche(creerJoueur("B"), new Position(20, 20)));
        assertTrue(ordres.ajouterFleche(creerJoueur("C"), new Position(30, 30)));
        assertFalse(ordres.ajouterFleche(creerJoueur("D"), new Position(40, 40)));
        assertEquals(3, ordres.getNombreFleches());
    }

    @Test
    public void apresRetirerFlecheOnPeutEnRajouter() {
        OrdresTour ordres = new OrdresTour();
        Joueur a = creerJoueur("A");
        ordres.ajouterFleche(a, new Position(10, 10));
        ordres.ajouterFleche(creerJoueur("B"), new Position(20, 20));
        ordres.ajouterFleche(creerJoueur("C"), new Position(30, 30));
        ordres.retirerFleche(a);
        assertTrue(ordres.ajouterFleche(creerJoueur("D"), new Position(40, 40)));
        assertEquals(3, ordres.getNombreFleches());
    }

    @Test
    public void deuxiemeFlecheMemeJoueurRemplace() {
        OrdresTour ordres = new OrdresTour();
        Joueur a = creerJoueur("A");
        ordres.ajouterFleche(a, new Position(10, 10));
        ordres.ajouterFleche(a, new Position(50, 50));
        assertEquals(1, ordres.getNombreFleches());
        assertEquals(50, ordres.getFleche(a).getX(), 0.001);
    }

    @Test
    public void actionsDuPorteur() {
        OrdresTour ordres = new OrdresTour();
        Joueur receveur = creerJoueur("R");

        ordres.avancer(new Position(30, 60));
        assertEquals(ActionPorteur.AVANCER, ordres.getAction());
        assertEquals(60, ordres.getCible().getY(), 0.001);

        ordres.passer(receveur);
        assertEquals(ActionPorteur.PASSER, ordres.getAction());
        assertEquals(receveur, ordres.getReceveur());
        assertNull(ordres.getCible());

        ordres.tirer();
        assertEquals(ActionPorteur.TIRER, ordres.getAction());
        assertNull(ordres.getReceveur());
    }
}