package com.example.brf.moteur;

import java.util.ArrayList;
import java.util.List;
public class Equipe {
    public static final int TAILLE_EQUIPE = 11;

    // List<Joueur> JoueursTitulaire = new ArrayList<>();
    // D'abord on essaye juste les 11 joueurs on ajoutera après
    // florian : Plus tard : 5 remplaçants dans une liste séparée
    private String nomEquipe;

    // private Formation formation; pas utile pour le mmoment on reverra plus tard les formations

    private List<Joueur> joueurs;
    private int buts;   /* score */

    public Equipe(String nomEquipe) { // ", Formation formation" enlever des parenthèses
        this.nomEquipe = nomEquipe;
        // this.formation = formation;
        this.joueurs = new ArrayList<>();
        this.buts = 0;

    }

    public String getNomEquipe() {
        return nomEquipe;
    }

    // public Formation getFormation() {
    //    return formation;
    //}

    public List<Joueur> getJoueurs() {
        return joueurs;
    }


    public boolean ajouterJoueur(Joueur joueur) {
        if (joueurs.size() >= TAILLE_EQUIPE) {
            return false;
        }
        joueurs.add(joueur);
        return true;
    }

    public Joueur getGardien() {
        for (Joueur joueur : joueurs) {
            if (joueur.getPoste() == Poste.GARDIEN) {
                return joueur;
            }
        }
        return null;
    }
    public List<Joueur> getJoueursDuPoste(Poste poste) {
        List<Joueur> resultat = new ArrayList<>();
        for (Joueur joueur : joueurs) {
            if (joueur.getPoste() == poste) {
                resultat.add(joueur);
            }
        }
    return resultat;
    }
    public void recupererPEMiTemps(int quantite) {
        for (Joueur joueur : joueurs) {
            joueur.recupererPE(quantite);
        }
    }

    public void marquerBut() {
        buts++;
    }

    public int getButs() {
        return buts;
    }

//    public boolean estComplete() { //est ce l'équipe correspond bien à la formation
//        // Étape 1 : il faut exactement 11 joueurs
//        if (joueurs.size() != TAILLE_EQUIPE) {
//            return false;
//        }
//
//        // Étape 2 : pour chaque poste, l'équipe doit avoir le nombre que la formation demande
//        for (Poste poste : Poste.values()) {
//            int attendu = formation.nombreAuPoste(poste);
//            int reel = getJoueursDuPoste(poste).size();
//            if (reel != attendu) {
//                return false;
//            }
//        }
//
//        // Étape 3 : aucun refus, l'équipe est complète
//        return true;
//    }

}

