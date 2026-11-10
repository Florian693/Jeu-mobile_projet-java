package com.example.brf.moteur;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.List;

public class TerrainTest {

    // Crée une équipe complète qui respecte la formation donnée
    private Equipe creerEquipe(String nom, Formation formation) {
        Equipe equipe = new Equipe(nom, formation);
        for (Poste poste : Poste.values()) {
            for (int i = 0; i < formation.nombreAuPoste(poste); i++) {
                equipe.ajouterJoueur(new Joueur(nom + " " + poste + " " + i, poste,
                        TypeJoueur.VITESSE, 50, 50, 50, 50, 50, 50, 50));
            }
        }
        return equipe;
    }

    private Joueur creerMbappe() {
        return new Joueur("Mbappe", Poste.ATTAQUANT, TypeJoueur.VITESSE, 87, 78, 84, 45, 96, 0, 50);
    }

    // --- estDansLeTerrain et borner ---

    @Test
    public void estDansLeTerrain_dehors() {
        Terrain terrain = new Terrain();
        assertFalse(terrain.estDansLeTerrain(new Position(61, 10)));
    }

    @Test
    public void estDansLeTerrain_coinCompris() {
        Terrain terrain = new Terrain();
        assertTrue(terrain.estDansLeTerrain(new Position(60, 100)));
    }

    @Test
    public void estDansLeTerrain_negatif() {
        Terrain terrain = new Terrain();
        assertFalse(terrain.estDansLeTerrain(new Position(-5, 10)));
    }

    @Test
    public void borner_ramene_dans_le_terrain() {
        Terrain terrain = new Terrain();
        Position resultat = terrain.borner(new Position(-5, 120));
        assertEquals(0, resultat.getX(), 0.001);
        assertEquals(100, resultat.getY(), 0.001);
    }

    @Test
    public void borner_ne_change_rien_si_deja_dedans() {
        Terrain terrain = new Terrain();
        Position resultat = terrain.borner(new Position(30, 50));
        assertEquals(30, resultat.getX(), 0.001);
        assertEquals(50, resultat.getY(), 0.001);
    }

    // --- placer et getPosition ---

    @Test
    public void placer_puis_getPosition() {
        Terrain terrain = new Terrain();
        Joueur mbappe = creerMbappe();
        assertTrue(terrain.placer(mbappe, new Position(30, 50)));
        assertEquals(30, terrain.getPosition(mbappe).getX(), 0.001);
        assertEquals(50, terrain.getPosition(mbappe).getY(), 0.001);
    }

    @Test
    public void placer_hors_du_terrain_refuse() {
        Terrain terrain = new Terrain();
        Joueur mbappe = creerMbappe();
        assertFalse(terrain.placer(mbappe, new Position(70, 50)));
        assertNull(terrain.getPosition(mbappe));
    }

    // --- placerEnFormation ---

    @Test
    public void placerEnFormation_gardienDuHaut() {
        Terrain terrain = new Terrain();
        Equipe eux = creerEquipe("Eux", Formation.DEUX_TROIS_UN);
        terrain.placerEnFormation(eux, false);
        Position p = terrain.getPosition(eux.getGardien());
        assertEquals(30, p.getX(), 0.001);
        assertEquals(96, p.getY(), 0.001);
    }

    @Test
    public void placerEnFormation_troisDefenseursEnBas() {
        Terrain terrain = new Terrain();
        Equipe nous = creerEquipe("Nous", Formation.TROIS_DEUX_UN);
        terrain.placerEnFormation(nous, true);
        List<Joueur> defenseurs = nous.getJoueursDuPoste(Poste.DEFENSEUR);
        assertEquals(12, terrain.getPosition(defenseurs.get(0)).getX(), 0.001);
        assertEquals(20, terrain.getPosition(defenseurs.get(0)).getY(), 0.001);
        assertEquals(30, terrain.getPosition(defenseurs.get(1)).getX(), 0.001);
        assertEquals(18, terrain.getPosition(defenseurs.get(1)).getY(), 0.001);
        assertEquals(48, terrain.getPosition(defenseurs.get(2)).getX(), 0.001);
        assertEquals(20, terrain.getPosition(defenseurs.get(2)).getY(), 0.001);
    }

    @Test
    public void placerEnFormation_remplitAussiLesPositionsDeBase() {
        Terrain terrain = new Terrain();
        Equipe nous = creerEquipe("Nous", Formation.DEUX_DEUX_DEUX);
        terrain.placerEnFormation(nous, true);
        for (Joueur joueur : nous.getJoueurs()) {
            assertNotNull(terrain.getPosition(joueur));
            assertNotNull(terrain.getPositionDeBase(joueur));
        }
    }

    // --- joueursProches ---

    @Test
    public void joueursProches_trouveLeProcheEtIgnoreLeLoin() {
        Terrain terrain = new Terrain();
        Equipe nous = creerEquipe("Nous", Formation.DEUX_TROIS_UN);
        List<Joueur> joueurs = nous.getJoueurs();
        Joueur proche = joueurs.get(0);
        Joueur loin = joueurs.get(1);
        terrain.placer(proche, new Position(33, 50));   // à 3 unités du centre
        terrain.placer(loin, new Position(40, 50));     // à 10 unités du centre

        List<Joueur> resultat = terrain.joueursProches(new Position(30, 50), 4, nous);

        assertTrue(resultat.contains(proche));
        assertFalse(resultat.contains(loin));
    }
}