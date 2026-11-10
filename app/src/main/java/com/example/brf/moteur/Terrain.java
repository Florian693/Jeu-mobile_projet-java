package com.example.brf.moteur;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class Terrain {
    // Taille du terrain
    private static final int LARGEUR = 60;
    private static final int LONGUEUR = 100;

    // Ou est chaque joueur en ce moment
    private Map<Joueur, Position> positions = new HashMap<>();
    // Ou chaque joueur se replace (selon la formation)
    private Map<Joueur, Position> positionsDeBase = new HashMap<>();


    public boolean estDansLeTerrain(Position p) {
        return p.getX() >= 0 && p.getX() <= LARGEUR
                && p.getY() >= 0 && p.getY() <= LONGUEUR;
    }

    // Empeche de sortir du terrain
    public Position borner(Position p) {
        double x = Math.max(0, Math.min(LARGEUR, p.getX()));
        double y = Math.max(0, Math.min(LONGUEUR, p.getY()));
        return new Position(x, y);
    }

    public boolean placer(Joueur joueur, Position position){
        if (!estDansLeTerrain(position)) {
            return false;
        }
        positions.put(joueur, position);
        return true;
    }
    public Position getPosition(Joueur joueur) {
        return positions.get(joueur);
    }
    public Position getPositionDeBase(Joueur joueur){
        return positionsDeBase.get(joueur);
    }

    // chnager la v aribale enBas pas clair je trouve
    public void placerEnFormation(Equipe equipe, boolean enBas){
        for(Poste poste : Poste.values()){
            List<Joueur> joueursDuPoste = equipe.getJoueursDuPoste(poste);
            Position[] places = positionsDuPoste(equipe.getFormation(), poste);

            for (int i=0; i< joueursDuPoste.size() && i < places.length;i++){
                Position place = places[i];
                if(!enBas){
                    place = new Position(place.getX(), LONGUEUR- place.getY());
                }
                Joueur joueur = joueursDuPoste.get(i);
                positions.put(joueur,place);
                positionsDeBase.put(joueur,place);
            }
        }
    }
    public List<Joueur> joueursProches(Position centre, double rayon, Equipe equipe){
        List<Joueur> resultat = new ArrayList<>();
        for (Joueur joueur : equipe.getJoueurs()){
            Position p = positions.get(joueur);
            if (p!= null && p.distance(centre)<= rayon){
                resultat.add(joueur);
            }
        }
        return resultat;
    }

    // Positions de base d'un poste, pour l'équipe du bas (tableau de la mission 6)
    private static Position[] positionsDuPoste(Formation formation, Poste poste) {
        switch (poste) {
            case GARDIEN:
                return new Position[] { new Position(30, 4) };
            case DEFENSEUR:
                if (formation == Formation.TROIS_DEUX_UN) {
                    return new Position[] { new Position(12, 20), new Position(30, 18), new Position(48, 20) };
                }
                return new Position[] { new Position(18, 20), new Position(42, 20) };
            case MILIEU:
                if (formation == Formation.DEUX_TROIS_UN) {
                    return new Position[] { new Position(10, 38), new Position(30, 34), new Position(50, 38) };
                }
                return new Position[] { new Position(20, 34), new Position(40, 34) };
            default: // ATTAQUANT
                if (formation == Formation.DEUX_DEUX_DEUX) {
                    return new Position[] { new Position(22, 46), new Position(38, 46) };
                }
                return new Position[] { new Position(30, 46) };
        }
    }
}
