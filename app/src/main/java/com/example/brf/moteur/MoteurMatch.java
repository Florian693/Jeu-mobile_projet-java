package com.example.brf.moteur;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MoteurMatch {
    public static final int TOURS_PAR_MI_TEMPS = 20;
    public static final int PE_MI_TEMPS = 20;       /* PE rendus a la mi-temps */
    public static final int ETAPES_PAR_TOUR = 10;   /* 1 tour = 10 petits pas */

    public static final double RAYON_DUEL = 3;      /* contact porteur / adversaire */
    public static final double AVANCE_DUEL = 6;     /* dribble ou tacle gagne : +6 */
    public static final double RAYON_INTERCEPTION = 3;   /* adversaire pres de la passe */
    public static final double DISTANCE_TIR = 25;        /* tir possible a 25 du but */

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
    private Technique techniqueTir;   /* SI tir a jouer */
    private int tour;
    private boolean termine;

    public MoteurMatch(Equipe equipe1, Equipe equipe2, Random random) {
        this.equipe1 = equipe1;
        this.equipe2 = equipe2;
        this.terrain = new Terrain();
        this.duel = new Duel(random);
        this.tour = 0;
        this.termine = false;
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

    public boolean estTermine() {
        return termine;
    }

    /* perdant du dernier duel : ne bouge pas au prochain tour */
    public Joueur getJoueurBattu() {
        return joueurBattu;
    }

    /* assez pres du but adverse ? */
    public boolean peutTirer() {
        return terrain.getPosition(porteur).distance(butAttaque(porteur)) <= DISTANCE_TIR;
    }

    /* Tour */

    public List<ResultatAction> jouerTour(OrdresTour ordres1, OrdresTour ordres2) {
        List<ResultatAction> resultats = new ArrayList<>();
        tour++;
        Joueur protege = joueurBattu;
        joueurBattu = null;

        /* ordres de l'equipe qui a le ballon */
        OrdresTour ordresPorteur = ordres2;
        if (equipeDe(porteur) == equipe1) {
            ordresPorteur = ordres1;
        }

        /* tir : le gardien doit choisir (jouerTir) */
        if (ordresPorteur.getAction() == ActionPorteur.TIRER && peutTirer()) {
            techniqueTir = ordresPorteur.getTechnique();
            Joueur gardien = adversaireDe(porteur).getGardien();
            resultats.add(new ResultatAction(TypeResultat.TIR_A_JOUER,
                    "Tir de " + porteur.getNom() + " !", -1, 0, porteur, gardien));
            return resultats;
        }

        /* passe : coupee = fin du tour */
        if (ordresPorteur.getAction() == ActionPorteur.PASSER && ordresPorteur.getReceveur() != null) {
            ResultatAction passe = passer(ordresPorteur.getReceveur());
            resultats.add(passe);
            if (passe.getType() == TypeResultat.INTERCEPTION) {
                finirLeTour(resultats);
                return resultats;
            }
        }

        /* deplacements en 10 pas */
        for (int etape = 1; etape <= ETAPES_PAR_TOUR; etape++) {
            deplacer(equipe1, ordres1, protege);
            deplacer(equipe2, ordres2, protege);
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
        finirLeTour(resultats);
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
        finirLeTour(resultats);
        return resultats;
    }

    /* Tir */
    /* technique du gardien : null si pas de technique */
    public List<ResultatAction> jouerTir(ChoixGardien choixGardien, Technique techniqueGardien) {
        List<ResultatAction> resultats = new ArrayList<>();
        resultats.add(tirer(choixGardien, techniqueGardien));
        finirLeTour(resultats);
        return resultats;
    }

    /* Actions */

    /* tir contre le gardien : plus c'est loin, plus c'est dur */
    private ResultatAction tirer(ChoixGardien choixGardien, Technique techniqueGardien) {
        Joueur tireur = porteur;
        Equipe defense = adversaireDe(tireur);
        Joueur gardien = defense.getGardien();

        int distance = (int) terrain.getPosition(tireur).distance(butAttaque(tireur));
        int statTir = tireur.getAtt() + bonusTechnique(tireur, techniqueTir) - distance;
        int statGardien = gardien.getArr() + choixGardien.bonus() + bonusTechnique(gardien, techniqueGardien);
        int chance = Duel.calculerChance(statTir, statGardien, tireur.getType(), gardien.getType());
        techniqueTir = null;

        if (duel.tirer(chance)) {
            equipeDe(tireur).marquerBut();
            engagement(defense);   /* l'equipe qui a pris le but engage */
            return new ResultatAction(TypeResultat.BUT, "But de " + tireur.getNom() + " !",
                    chance, 1.0, tireur, gardien);
        }
        if (choixGardien.garderLeBallon()) {
            porteur = gardien;
            return new ResultatAction(TypeResultat.TIR_ARRETE, "Arret de " + gardien.getNom() + " !",
                    chance, 1.0, tireur, gardien);
        }
        porteur = plusProcheDe(gardien);   /* plongeon : ballon relache */
        return new ResultatAction(TypeResultat.TIR_ARRETE,
                gardien.getNom() + " repousse, ballon pour " + porteur.getNom() + " !",
                chance, 1.0, tireur, gardien);
    }

    /* 10 points sur la passe : le 1er adversaire trop pres tente de la couper */
    private ResultatAction passer(Joueur receveur) {
        Joueur passeur = porteur;
        Position depart = terrain.getPosition(passeur);
        Position arrivee = terrain.getPosition(receveur);
        int chance = -1;

        for (int i = 1; i <= ETAPES_PAR_TOUR; i++) {
            double fraction = (double) i / ETAPES_PAR_TOUR;
            Position point = Position.pointSurTrajet(depart, arrivee, fraction);
            List<Joueur> proches = terrain.joueursProches(point, RAYON_INTERCEPTION, adversaireDe(passeur));
            if (!proches.isEmpty()) {
                Joueur adversaire = proches.get(0);
                chance = Duel.calculerChance(passeur.getPas(), adversaire.getDef(),
                        passeur.getType(), adversaire.getType());
                if (!duel.tirer(chance)) {
                    porteur = adversaire;
                    return new ResultatAction(TypeResultat.INTERCEPTION,
                            "Interception de " + adversaire.getNom() + " !",
                            chance, fraction, passeur, adversaire);
                }
                break;   /* rate : la passe arrive */
            }
        }
        porteur = receveur;
        return new ResultatAction(TypeResultat.PASSE_REUSSIE,
                "Passe de " + passeur.getNom() + " pour " + receveur.getNom(),
                chance, 0, passeur, receveur);
    }

    /* 1 petit pas pour chaque joueur qui a une fleche (sauf le joueur battu) */
    private void deplacer(Equipe equipe, OrdresTour ordres, Joueur immobile) {
        for (Joueur joueur : equipe.getJoueurs()) {
            Position cible = ordres.getFleche(joueur);
            if (cible != null && joueur != immobile) {
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

    /* Mi-temps : tour 20, fin du match : tour 40 */

    private void finirLeTour(List<ResultatAction> resultats) {
        if (tour == TOURS_PAR_MI_TEMPS) {
            equipe1.recupererPEMiTemps(PE_MI_TEMPS);
            equipe2.recupererPEMiTemps(PE_MI_TEMPS);
            engagement(equipe2);
            resultats.add(new ResultatAction(TypeResultat.MI_TEMPS, "Mi-temps"));
        }
        if (tour == 2 * TOURS_PAR_MI_TEMPS) {
            termine = true;
            resultats.add(new ResultatAction(TypeResultat.FIN_DU_MATCH, "Fin du match"));
        }
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

    /* joueur le plus proche (des 2 equipes), sauf lui-meme */
    private Joueur plusProcheDe(Joueur joueur) {
        Position centre = terrain.getPosition(joueur);
        List<Joueur> tous = new ArrayList<>(equipe1.getJoueurs());
        tous.addAll(equipe2.getJoueurs());
        Joueur plusProche = null;
        for (Joueur autre : tous) {
            if (autre != joueur && (plusProche == null
                    || terrain.getPosition(autre).distance(centre) < terrain.getPosition(plusProche).distance(centre))) {
                plusProche = autre;
            }
        }
        return plusProche;
    }

    /* but que le joueur attaque */
    private Position butAttaque(Joueur joueur) {
        if (equipeDe(joueur) == equipe1) {
            return BUT_DU_HAUT;
        }
        return BUT_DU_BAS;
    }
}