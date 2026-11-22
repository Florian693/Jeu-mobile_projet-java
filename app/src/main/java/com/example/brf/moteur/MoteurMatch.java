package com.example.brf.moteur;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MoteurMatch {
    public static final int ETAPES_PAR_TOUR = 10;   /* 1 tour = 10 petits pas */

    public static final double RAYON_DUEL = 3;      /* contact porteur / adversaire */
    public static final double AVANCE_DUEL = 6;     /* dribble ou tacle gagne : +6 */

    /* centre des buts */
    public static final Position BUT_DU_BAS = new Position(Terrain.LARGEUR / 2.0, 0);
    public static final Position BUT_DU_HAUT = new Position(Terrain.LARGEUR / 2.0, Terrain.LONGUEUR);

    private Equipe equipe1;   /* Domicile */
    private Equipe equipe2;   /* Exterieur */
    private Terrain terrain;
    private Duel duel;

    private Joueur porteur;
    private Joueur defenseurDuel;  /* SI duel a jouer */
    private Joueur joueurBattu;    /* perdant du dernier duel proteger pdnt 1 tour */
    private int tour;

    public MoteurMatch(Equipe equipe1, Equipe equipe2, Random random) {
        this.equipe1 = equipe1;
        this.equipe2 = equipe2;
        this.terrain = new Terrain();
        this.duel = new Duel(random);
        this.tour = 0;
        engagement(equipe1);
    }

    public Equipe getEquipe1() {
        return equipe1;
    }

    public Equipe getEquipe2() {
        return equipe2;
    }

    public Terrain getTerrain() {
        return terrain;
    }

    public Joueur getPorteur() {
        return porteur;
    }

    public int getTour() {
        return tour;
    }

    /* Tour */

    public List<ResultatAction> jouerTour(OrdresTour ordres1, OrdresTour ordres2) {
        List<ResultatAction> resultats = new ArrayList<>();
        tour++;
        Joueur protege = joueurBattu;
        joueurBattu = null;
        /* deplacements en 10 pas */
        for (int etape = 1; etape <= ETAPES_PAR_TOUR; etape++) {
            deplacer(equipe1, ordres1);
            deplacer(equipe2, ordres2);
            /* contact = duel, fin du tour */
            Joueur adversaire = adversaireAuContact(protege);
            if (adversaire != null) {
                defenseurDuel = adversaire;
                resultats.add(new ResultatAction(TypeResultat.DUEL_A_JOUER,
                        "Duel : " + porteur.getNom() + " contre " + adversaire.getNom(),
                        -1, (double) etape / ETAPES_PAR_TOUR, porteur, adversaire));
                return resultats;
            }
        }

        resultats.add(new ResultatAction(TypeResultat.TOUR_TERMINE, "Fin du tour " + tour));
        return resultats;
    }

    /* Duel */
    /* techniques : null si pas de technique */
    public List<ResultatAction> jouerDuel(ChoixAttaque choixAttaque, Technique techniqueAttaque,
                                          ChoixDefense choixDefense, Technique techniqueDefense) {
        List<ResultatAction> resultats = new ArrayList<>();
        Joueur attaquant = porteur;
        Joueur defenseur = defenseurDuel;
        defenseurDuel = null;

        int statAttaque = choixAttaque.statUtilisee(attaquant) + choixAttaque.bonus()
                + bonusTechnique(attaquant, techniqueAttaque);
        int statDefense = defenseur.getDef() + choixDefense.bonus()
                + bonusTechnique(defenseur, techniqueDefense);
        int chance = Duel.calculerChance(statAttaque, statDefense, attaquant.getType(), defenseur.getType());

        if (duel.tirer(chance)) {
            joueurBattu = defenseur;
            if (choixAttaque.faitAvancer()) {
                avancerVersLeBut(attaquant);
            }
            resultats.add(new ResultatAction(TypeResultat.DUEL_GAGNE_ATTAQUE,
                    attaquant.getNom() + " passe " + defenseur.getNom() + " !",
                    chance, 1.0, attaquant, defenseur));
        } else {
            porteur = defenseur;
            joueurBattu = attaquant;
            if (choixDefense.faitAvancer()) {
                avancerVersLeBut(defenseur);
            }
            resultats.add(new ResultatAction(TypeResultat.DUEL_GAGNE_DEFENSE,
                    defenseur.getNom() + " prend le ballon a " + attaquant.getNom() + " !",
                    chance, 1.0, attaquant, defenseur));
        }
        return resultats;
    }

    /* 1 petit pas pour chaque joueur qui a une fleche */
    private void deplacer(Equipe equipe, OrdresTour ordres) {
        for (Joueur joueur : equipe.getJoueurs()) {
            Position cible = ordres.getFleche(joueur);
            if (cible != null) {
                double pas = joueur.getDistanceParTour() / ETAPES_PAR_TOUR;
                Position nouvelle = terrain.getPosition(joueur).versCible(cible, pas);
                terrain.placer(joueur, terrain.borner(nouvelle));
            }
        }
    }

    /* adversaire au contact du porteur (sauf le protege), sinon null */
    private Joueur adversaireAuContact(Joueur protege) {
        List<Joueur> proches = terrain.joueursProches(terrain.getPosition(porteur), RAYON_DUEL,
                adversaireDe(porteur));
        for (Joueur joueur : proches) {
            if (joueur != protege) {
                return joueur;
            }
        }
        return null;
    }

    /* technique lancee + assez de PE : bonus */
    private int bonusTechnique(Joueur joueur, Technique technique) {
        if (technique != null && joueur.depenserPE(technique.getCoutPE())) {
            return technique.getBonus();
        }
        return 0;
    }

    private void avancerVersLeBut(Joueur joueur) {
        Position nouvelle = terrain.getPosition(joueur).versCible(butAttaque(joueur), AVANCE_DUEL);
        terrain.placer(joueur, nouvelle);
    }

    private void engagement(Equipe equipeQuiEngage) {
        terrain.placerEnFormation(equipe1, true);
        terrain.placerEnFormation(equipe2, false);
        porteur = equipeQuiEngage.getJoueursDuPoste(Poste.ATTAQUANT).get(1);
        joueurBattu = null;
    }
    /* Outils */

    private Equipe equipeDe(Joueur joueur) {
        if (equipe1.getJoueurs().contains(joueur)) {
            return equipe1;
        }
        return equipe2;
    }

    private Equipe adversaireDe(Joueur joueur) {
        if (equipeDe(joueur) == equipe1) {
            return equipe2;
        }
        return equipe1;
    }

    /* but que le joueur attaque */
    private Position butAttaque(Joueur joueur) {
        if (equipeDe(joueur) == equipe1) {
            return BUT_DU_HAUT;
        }
        return BUT_DU_BAS;
    }
}