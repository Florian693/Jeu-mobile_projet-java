package com.example.brf.moteur;

// Ce que le moteur renvoie a MatchView apres un tour ou un duel.
// MatchView ne calcule rien : elle lit ce resultat et l'anime.
// Pas de setter : une fois cree, un resultat ne change plus.
public class ResultatAction {
    private final TypeResultat type;    // ce qui s'est passe
    private final String message;       // texte du bandeau, ex : "But de Mbappe !"
    private final int chance;           // pourcentage de reussite, ou -1 s'il n'y en a pas
    private final double fractionJouee; // part du tour jouee avant l'arret (de 0 a 1)
    private final Joueur attaquant;     // joueur du duel / de l'interception, sinon null
    private final Joueur defenseur;     // joueur du duel / de l'interception, sinon null

    // Constructeur complet
    public ResultatAction(TypeResultat type, String message, int chance,
                          double fractionJouee, Joueur attaquant, Joueur defenseur) {
        this.type = type;
        this.message = message;
        this.chance = chance;
        this.fractionJouee = fractionJouee;
        this.attaquant = attaquant;
        this.defenseur = defenseur;
    }

    // Constructeur court, pour un resultat simple (tour termine, mi-temps...) :
    // pas de chance, tour joue en entier, pas de joueurs concernes.
    public ResultatAction(TypeResultat type, String message) {
        this(type, message, -1, 1.0, null, null);
    }

    public TypeResultat getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }

    public int getChance() {
        return chance;
    }

    public double getFractionJouee() {
        return fractionJouee;
    }

    public Joueur getAttaquant() {
        return attaquant;
    }

    public Joueur getDefenseur() {
        return defenseur;
    }
}