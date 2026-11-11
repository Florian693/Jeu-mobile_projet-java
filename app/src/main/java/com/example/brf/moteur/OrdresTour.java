package com.example.brf.moteur;

import java.util.HashMap;
import java.util.Map;

// Ce qu'un camp decide pour un tour : l'action du porteur et les fleches.
// MatchView en remplit un pour nous, IaAdverse en remplit un pour l'adversaire,
// puis MoteurMatch.jouerTour(ordresNous, ordresEux) les execute.
public class OrdresTour {
    public static final int MAX_FLECHES = 3;

    // Action du porteur (null si aucune : par exemple quand l'equipe defend)
    private ActionPorteur action;
    private Position cible;    // utilisee seulement si l'action est AVANCER
    private Joueur receveur;   // utilise seulement si l'action est PASSER

    // Fleches des autres joueurs : joueur -> point vise
    private final Map<Joueur, Position> fleches;

    // Des ordres vides : pas d'action, pas de fleche
    public OrdresTour() {
        this.action = null;
        this.cible = null;
        this.receveur = null;
        this.fleches = new HashMap<>();
    }

    // --- Action du porteur (une seule a la fois : la derniere choisie remplace l'autre) ---

    public void avancer(Position cible) {
        this.action = ActionPorteur.AVANCER;
        this.cible = cible;
        this.receveur = null;
    }

    public void passer(Joueur receveur) {
        this.action = ActionPorteur.PASSER;
        this.receveur = receveur;
        this.cible = null;
    }

    public void tirer() {
        this.action = ActionPorteur.TIRER;
        this.cible = null;
        this.receveur = null;
    }

    public ActionPorteur getAction() {
        return action;
    }

    public Position getCible() {
        return cible;
    }

    public Joueur getReceveur() {
        return receveur;
    }

    // --- Fleches ---

    // Ajoute (ou remplace) la fleche d'un joueur.
    // Refuse une 4e fleche (renvoie false), mais accepte toujours de remplacer une fleche existante.
    public boolean ajouterFleche(Joueur joueur, Position cible) {
        if (!fleches.containsKey(joueur) && fleches.size() >= MAX_FLECHES) {
            return false;
        }
        fleches.put(joueur, cible);
        return true;
    }

    public void retirerFleche(Joueur joueur) {
        fleches.remove(joueur);
    }

    // La fleche d'un joueur, ou null s'il n'en a pas
    public Position getFleche(Joueur joueur) {
        return fleches.get(joueur);
    }

    public int getNombreFleches() {
        return fleches.size();
    }

    public Map<Joueur, Position> getFleches() {
        return fleches;
    }
}