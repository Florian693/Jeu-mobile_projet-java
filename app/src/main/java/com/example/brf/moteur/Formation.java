package com.example.brf.moteur;

public enum Formation {
    DEUX_TROIS_UN(2,3,1),
    TROIS_DEUX_UN(3,2,1),
    DEUX_DEUX_DEUX(2,2,2);

    private final int defenseur;
    private final int milieu;
    private final int attaquant;

    Formation(int defenseur, int milieu, int attaquant) {
        this.defenseur = defenseur;
        this.milieu = milieu;
        this.attaquant = attaquant;
    }
    public int nombreAuPoste(Poste poste) {
        switch (poste) {
            case GARDIEN:
                return 1;
            case DEFENSEUR:
                return defenseur;
            case MILIEU:
                return milieu;
            case ATTAQUANT:
                return attaquant;
            default:
                return 0;
        }
    }
    public String getNom() {
        return defenseur + "-" + milieu + "-" + attaquant;
    }
}
