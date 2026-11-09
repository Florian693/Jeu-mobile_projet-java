package com.example.brf.moteur;

public enum TypeJoueur {
    VITESSE, TECHNIQUE, PUISSANCE;

    public boolean bat(TypeJoueur autre) {
        if (this == TypeJoueur.VITESSE && autre == TypeJoueur.TECHNIQUE) {
            return true;
        }
        if (this == TypeJoueur.TECHNIQUE && autre == TypeJoueur.PUISSANCE) {
            return true;
        }
        if (this == TypeJoueur.PUISSANCE && autre == TypeJoueur.VITESSE) {
            return true;
        }
        return false;
    }
    }

