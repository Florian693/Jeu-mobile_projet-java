package com.example.brf.moteur;

/* technique speciale : coute des PE, donne un bonus */
public class Technique {
    private String nom;
    private TypeTechnique type;
    private int bonus;
    private int coutPE;

    public Technique(String nom, TypeTechnique type, int bonus, int coutPE) {
        this.nom = nom;
        this.type = type;
        this.bonus = bonus;
        this.coutPE = coutPE;
    }

    public String getNom() {
        return nom;
    }

    public TypeTechnique getType() {
        return type;
    }

    public int getBonus() {
        return bonus;
    }

    public int getCoutPE() {
        return coutPE;
    }
}