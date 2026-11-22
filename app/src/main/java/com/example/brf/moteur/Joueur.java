package com.example.brf.moteur;
import java.util.ArrayList;
import java.util.List;

public class Joueur {

    /* distance max en un tour : vit 80 -> 20 */
    public double getDistanceParTour() {
        return vit / 4.0;
    }
    public static final int MAX_TECHNIQUES = 2;
    private String nom;
    private Poste poste;
    private TypeJoueur type;

    /* Stat joueur */
    private int att;
    private int pas;
    private int dri;
    private int def;
    private int vit;
    private int arr;
   /* PE Maximum et Actuel */
    private int peMax;
    private int peActuel;

    /* techniques speciales */
    private List<Technique> techniques;

    /* Constructeur */
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
        this.techniques = new ArrayList<>();
    }

    public String getNom() {
        return nom;
    }

    public Poste getPoste() {
        return poste;
    }

    public TypeJoueur getType() {
        return type;
    }

    public int getAtt() {
        return att;
    }

    public int getPeActuel() {
        return peActuel;
    }

    public int getPeMax() {
        return peMax;
    }

    public int getArr() {
        return arr;
    }

    public int getVit() {
        return vit;
    }

    public int getDef() {
        return def;
    }

    public int getDri() {
        return dri;
    }

    public int getPas() {
        return pas;
    }

    public boolean depenserPE(int cout){    //Valide ou non la dépense de PE
        if (peActuel >= cout){
            peActuel = peActuel - cout;
            return true;
        }
        else return false;
    }
    public void recupererPE(int quantite){    //Valide ou non la recuperation de PE
        if (peActuel+quantite <= peMax){
            peActuel = peActuel + quantite;
        }
        else {
            peActuel = peMax;
        }
    }

    /* techniques : 2 max */
    public boolean ajouterTechnique(Technique technique) {
        if (techniques.size() >= MAX_TECHNIQUES) {
            return false;
        }
        techniques.add(technique);
        return true;
    }

    public List<Technique> getTechniques() {
        return techniques;
    }

    public List<Technique> getTechniquesDuType(TypeTechnique type) {
        List<Technique> resultat = new ArrayList<>();
        for (Technique technique : techniques) {
            if (technique.getType() == type) {
                resultat.add(technique);
            }
        }
        return resultat;
    }

    /* assez de PE ? */
    public boolean peutLancer(Technique technique) {
        return peActuel >= technique.getCoutPE();
    }
}
