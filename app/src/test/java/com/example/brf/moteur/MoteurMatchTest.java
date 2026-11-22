package com.example.brf.moteur;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.List;
import java.util.Random;

public class MoteurMatchTest {

    private static class FauxHasard extends Random {
        private int valeur;

        FauxHasard(int valeur) {
            this.valeur = valeur;
        }

        @Override
        public int nextInt(int bound) {
            return valeur;
        }
    }

    private Equipe real;
    private Equipe barca;

    private MoteurMatch creerMatch(int hasard) {
        real = Effectifs.realMadrid();
        barca = Effectifs.barcelone();
        return new MoteurMatch(real, barca, new FauxHasard(hasard));
    }

    private Joueur trouver(Equipe equipe, String nom) {
        for (Joueur joueur : equipe.getJoueurs()) {
            if (joueur.getNom().equals(nom)) {
                return joueur;
            }
        }
        return null;
    }
    @Test
    public void debutDuMatch_mbappeAuBallon() {
        MoteurMatch match = creerMatch(0);
        assertEquals("Mbappe", match.getPorteur().getNom());
        assertEquals(0, match.getTour());
    }
    @Test
    public void sansFleche_personneNeBouge() {
        MoteurMatch match = creerMatch(0);
        Joueur mbappe = match.getPorteur();

        List<ResultatAction> resultats = match.jouerTour(new OrdresTour(), new OrdresTour());

        assertEquals(1, resultats.size());
        assertEquals(TypeResultat.TOUR_TERMINE, resultats.get(0).getType());
        assertEquals(46, match.getTerrain().getPosition(mbappe).getY(), 0.001);
        assertEquals(1, match.getTour());
    }

    @Test
    public void flecheLointaine_leJoueurAvanceDeSaDistanceParTour() {
        MoteurMatch match = creerMatch(0);
        Joueur tchouameni = trouver(real, "Tchouameni");   /* (30, 28), vitesse 72 : 18 par tour */
        OrdresTour ordres = new OrdresTour();
        ordres.ajouterFleche(tchouameni, new Position(30, 0));

        match.jouerTour(ordres, new OrdresTour());

        assertEquals(30, match.getTerrain().getPosition(tchouameni).getX(), 0.001);
        assertEquals(10, match.getTerrain().getPosition(tchouameni).getY(), 0.001);
    }

    @Test
    public void flecheProche_leJoueurSArreteDessus() {
        MoteurMatch match = creerMatch(0);
        Joueur tchouameni = trouver(real, "Tchouameni");
        OrdresTour ordres = new OrdresTour();
        ordres.ajouterFleche(tchouameni, new Position(30, 25));

        match.jouerTour(ordres, new OrdresTour());

        assertEquals(25, match.getTerrain().getPosition(tchouameni).getY(), 0.001);
    }

    private MoteurMatch creerMatchAvecDuel(int hasard) {
        MoteurMatch match = creerMatch(hasard);
        match.getTerrain().placer(trouver(barca, "Frenkie de Jong"), new Position(30, 48));
        return match;
    }

    private TypeResultat dernierType(List<ResultatAction> resultats) {
        return resultats.get(resultats.size() - 1).getType();
    }

    @Test
    public void contactAvecLePorteur_duelAJouer() {
        MoteurMatch match = creerMatchAvecDuel(0);

        List<ResultatAction> resultats = match.jouerTour(new OrdresTour(), new OrdresTour());

        ResultatAction duel = resultats.get(0);
        assertEquals(TypeResultat.DUEL_A_JOUER, duel.getType());
        assertEquals("Mbappe", duel.getAttaquant().getNom());
        assertEquals("Frenkie de Jong", duel.getDefenseur().getNom());
        assertEquals(0.1, duel.getFractionJouee(), 0.001);
    }

    @Test
    public void duelGagneAvecDribble_lAttaquantAvanceDe6() {
        MoteurMatch match = creerMatchAvecDuel(0);
        Joueur mbappe = match.getPorteur();
        match.jouerTour(new OrdresTour(), new OrdresTour());

        List<ResultatAction> resultats = match.jouerDuel(ChoixAttaque.DRIBBLE, null, ChoixDefense.TACLE, null);

        assertEquals(TypeResultat.DUEL_GAGNE_ATTAQUE, resultats.get(0).getType());
        assertEquals(mbappe, match.getPorteur());
        assertEquals(52, match.getTerrain().getPosition(mbappe).getY(), 0.001);
    }

    @Test
    public void duelPerdu_leDefenseurPrendLeBallon() {
        MoteurMatch match = creerMatchAvecDuel(99);
        match.jouerTour(new OrdresTour(), new OrdresTour());

        List<ResultatAction> resultats = match.jouerDuel(ChoixAttaque.DRIBBLE, null, ChoixDefense.BLOCAGE, null);

        assertEquals(TypeResultat.DUEL_GAGNE_DEFENSE, resultats.get(0).getType());
        assertEquals("Frenkie de Jong", match.getPorteur().getNom());
    }

    @Test
    public void leDuelTermineLeTour() {
        MoteurMatch match = creerMatchAvecDuel(0);
        match.jouerTour(new OrdresTour(), new OrdresTour());
        match.jouerDuel(ChoixAttaque.ESQUIVE, null, ChoixDefense.BLOCAGE, null);
        assertEquals(1, match.getTour());

        match.jouerTour(new OrdresTour(), new OrdresTour());
        assertEquals(2, match.getTour());
    }

    @Test
    public void lePerdantDuDuel_estProtegeUnSeulTour() {
        MoteurMatch match = creerMatchAvecDuel(0);
        match.jouerTour(new OrdresTour(), new OrdresTour());
        match.jouerDuel(ChoixAttaque.ESQUIVE, null, ChoixDefense.BLOCAGE, null);

        List<ResultatAction> tour2 = match.jouerTour(new OrdresTour(), new OrdresTour());
        assertEquals(TypeResultat.TOUR_TERMINE, dernierType(tour2));

        List<ResultatAction> tour3 = match.jouerTour(new OrdresTour(), new OrdresTour());
        assertEquals(TypeResultat.DUEL_A_JOUER, dernierType(tour3));
    }

    @Test
    public void techniqueEnDuel_depenseLesPE() {
        MoteurMatch match = creerMatchAvecDuel(0);
        Joueur dejong = trouver(barca, "Frenkie de Jong");
        Technique toile = dejong.getTechniquesDuType(TypeTechnique.DEFENSE).get(0);
        match.jouerTour(new OrdresTour(), new OrdresTour());

        match.jouerDuel(ChoixAttaque.DRIBBLE, null, ChoixDefense.TACLE, toile);

        assertEquals(55 - 20, dejong.getPeActuel());
    }
}