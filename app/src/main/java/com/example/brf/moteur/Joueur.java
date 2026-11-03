package com.example.brf.moteur;

public class Joueur {
    private String nom;
    private Poste poste;
    private TypeJoueur type;

// Stat joueur
    private int att;
    private int pas;
    private int dri;
    private int def;
    private int vit;
    private int arr;
// PE Maximum et Actuel
    private int peMax;
    private int peActuel;
    // Constructeur
    public Joueur(String nom, Poste poste, TypeJoueur type, int att, int pas, int dri, int def, int vit, int arr, int peMax){
        this.nom = nom;
        this.poste = poste;
        this.type = type;
        this.att = att;
        this.pas = pas;
        this.dri = dri;
        this.def = def;
        this.vit = vit;
        this.arr = arr;
        this.peMax = peMax;
        this.peActuel = peMax;
    }
}
