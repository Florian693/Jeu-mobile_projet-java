package com.example.brf.moteur;

/* les 2 equipes du jeu (joueurs 2026-2027, memes noms que joueurs.json) */
/* chaque poste de gauche a droite, 2e attaquant = avant-centre (coup d'envoi) */
/* stats : nom, poste, type, att, pas, dri, def, vit, arr, peMax */
public class Effectifs {

    public static Equipe realMadrid() {
        Equipe equipe = new Equipe("Real Madrid");

        equipe.ajouterJoueur(new Joueur("Courtois", Poste.GARDIEN, TypeJoueur.PUISSANCE, 20, 64, 30, 42, 58, 90, 60));

        Joueur cucurella = new Joueur("Cucurella", Poste.DEFENSEUR, TypeJoueur.PUISSANCE, 55, 76, 72, 80, 80, 0, 50);
        cucurella.ajouterTechnique(new Technique("Tacle Tornade", TypeTechnique.DEFENSE, 18, 25));
        cucurella.ajouterTechnique(new Technique("Crinière Rebelle", TypeTechnique.DEFENSE, 14, 20));
        equipe.ajouterJoueur(cucurella);

        Joueur huijsen = new Joueur("Huijsen", Poste.DEFENSEUR, TypeJoueur.TECHNIQUE, 50, 82, 68, 85, 74, 0, 50);
        huijsen.ajouterTechnique(new Technique("Muraille Royale", TypeTechnique.DEFENSE, 20, 30));
        huijsen.ajouterTechnique(new Technique("Relance Laser", TypeTechnique.DRIBBLE, 14, 20));
        equipe.ajouterJoueur(huijsen);

        Joueur konate = new Joueur("Konate", Poste.DEFENSEUR, TypeJoueur.PUISSANCE, 48, 68, 60, 88, 84, 0, 55);
        konate.ajouterTechnique(new Technique("Tour de Fer", TypeTechnique.DEFENSE, 20, 30));
        konate.ajouterTechnique(new Technique("Tacle Rouleau", TypeTechnique.DEFENSE, 16, 20));
        equipe.ajouterJoueur(konate);

        Joueur valverde = new Joueur("Valverde", Poste.DEFENSEUR, TypeJoueur.VITESSE, 82, 84, 78, 80, 88, 0, 60);
        valverde.ajouterTechnique(new Technique("Sprint Charrúa", TypeTechnique.DRIBBLE, 16, 20));
        valverde.ajouterTechnique(new Technique("Retour Express", TypeTechnique.DEFENSE, 14, 20));
        equipe.ajouterJoueur(valverde);

        Joueur bellingham = new Joueur("Bellingham", Poste.MILIEU, TypeJoueur.PUISSANCE, 86, 84, 86, 72, 78, 0, 55);
        bellingham.ajouterTechnique(new Technique("Frappe du Roi", TypeTechnique.TIR, 18, 25));
        bellingham.ajouterTechnique(new Technique("Percée Anglaise", TypeTechnique.DRIBBLE, 16, 20));
        equipe.ajouterJoueur(bellingham);

        Joueur tchouameni = new Joueur("Tchouameni", Poste.MILIEU, TypeJoueur.PUISSANCE, 72, 82, 72, 88, 72, 0, 55);
        tchouameni.ajouterTechnique(new Technique("Mur du Milieu", TypeTechnique.DEFENSE, 18, 25));
        tchouameni.ajouterTechnique(new Technique("Tacle Ciseau", TypeTechnique.DEFENSE, 14, 20));
        equipe.ajouterJoueur(tchouameni);

        Joueur guler = new Joueur("Arda Guler", Poste.MILIEU, TypeJoueur.TECHNIQUE, 84, 88, 88, 50, 72, 0, 50);
        guler.ajouterTechnique(new Technique("Gauche Magique", TypeTechnique.TIR, 16, 20));
        guler.ajouterTechnique(new Technique("Roulette du Bosphore", TypeTechnique.DRIBBLE, 14, 20));
        equipe.ajouterJoueur(guler);

        Joueur vinicius = new Joueur("Vinicius Jr", Poste.ATTAQUANT, TypeJoueur.VITESSE, 86, 80, 95, 30, 95, 0, 60);
        vinicius.ajouterTechnique(new Technique("Samba Fulgurante", TypeTechnique.DRIBBLE, 20, 30));
        vinicius.ajouterTechnique(new Technique("Frappe Enroulée", TypeTechnique.TIR, 16, 20));
        equipe.ajouterJoueur(vinicius);

        Joueur mbappe = new Joueur("Mbappe", Poste.ATTAQUANT, TypeJoueur.VITESSE, 95, 82, 90, 35, 97, 0, 60);
        mbappe.ajouterTechnique(new Technique("Frappe Tonnerre", TypeTechnique.TIR, 20, 30));
        mbappe.ajouterTechnique(new Technique("Accélération Éclair", TypeTechnique.DRIBBLE, 18, 25));
        equipe.ajouterJoueur(mbappe);

        Joueur bernardo = new Joueur("Bernardo Silva", Poste.ATTAQUANT, TypeJoueur.TECHNIQUE, 80, 90, 90, 60, 76, 0, 55);
        bernardo.ajouterTechnique(new Technique("Feinte de Lisbonne", TypeTechnique.DRIBBLE, 16, 20));
        bernardo.ajouterTechnique(new Technique("Lob Malicieux", TypeTechnique.TIR, 14, 20));
        equipe.ajouterJoueur(bernardo);

        return equipe;
    }

    public static Equipe barcelone() {
        Equipe equipe = new Equipe("FC Barcelone");

        equipe.ajouterJoueur(new Joueur("Joan Garcia", Poste.GARDIEN, TypeJoueur.VITESSE, 20, 70, 35, 40, 62, 86, 60));

        Joueur balde = new Joueur("Balde", Poste.DEFENSEUR, TypeJoueur.VITESSE, 58, 74, 80, 74, 92, 0, 50);
        balde.ajouterTechnique(new Technique("Flèche Catalane", TypeTechnique.DRIBBLE, 16, 20));
        balde.ajouterTechnique(new Technique("Retour Supersonique", TypeTechnique.DEFENSE, 12, 15));
        equipe.ajouterJoueur(balde);

        Joueur cubarsi = new Joueur("Cubarsi", Poste.DEFENSEUR, TypeJoueur.TECHNIQUE, 45, 84, 66, 86, 72, 0, 55);
        cubarsi.ajouterTechnique(new Technique("Tour de Contrôle", TypeTechnique.DEFENSE, 18, 25));
        cubarsi.ajouterTechnique(new Technique("Mur Catalan", TypeTechnique.DEFENSE, 22, 30));
        equipe.ajouterJoueur(cubarsi);

        Joueur kounde = new Joueur("Kounde", Poste.DEFENSEUR, TypeJoueur.VITESSE, 52, 76, 72, 86, 86, 0, 50);
        kounde.ajouterTechnique(new Technique("Tacle Cadenas", TypeTechnique.DEFENSE, 14, 20));
        kounde.ajouterTechnique(new Technique("Repli Éclair", TypeTechnique.DEFENSE, 16, 20));
        equipe.ajouterJoueur(kounde);

        Joueur cancelo = new Joueur("Cancelo", Poste.DEFENSEUR, TypeJoueur.TECHNIQUE, 70, 84, 84, 76, 80, 0, 50);
        cancelo.ajouterTechnique(new Technique("Roulette Lusitanienne", TypeTechnique.DRIBBLE, 16, 20));
        cancelo.ajouterTechnique(new Technique("Couverture Express", TypeTechnique.DEFENSE, 14, 20));
        equipe.ajouterJoueur(cancelo);

        Joueur pedri = new Joueur("Pedri", Poste.MILIEU, TypeJoueur.TECHNIQUE, 78, 92, 92, 66, 74, 0, 55);
        pedri.ajouterTechnique(new Technique("La Croqueta", TypeTechnique.DRIBBLE, 18, 25));
        pedri.ajouterTechnique(new Technique("Frappe Sang-Froid", TypeTechnique.TIR, 14, 20));
        equipe.ajouterJoueur(pedri);

        Joueur dejong = new Joueur("Frenkie de Jong", Poste.MILIEU, TypeJoueur.TECHNIQUE, 70, 90, 88, 80, 78, 0, 55);
        dejong.ajouterTechnique(new Technique("Toile d'Araignée", TypeTechnique.DEFENSE, 16, 20));
        dejong.ajouterTechnique(new Technique("Pivot Fantôme", TypeTechnique.DRIBBLE, 14, 20));
        equipe.ajouterJoueur(dejong);

        Joueur fermin = new Joueur("Fermin Lopez", Poste.MILIEU, TypeJoueur.PUISSANCE, 82, 80, 80, 64, 78, 0, 50);
        fermin.ajouterTechnique(new Technique("Missile Catalan", TypeTechnique.TIR, 16, 20));
        fermin.ajouterTechnique(new Technique("Pressing Infatigable", TypeTechnique.DEFENSE, 12, 15));
        equipe.ajouterJoueur(fermin);

        Joueur raphinha = new Joueur("Raphinha", Poste.ATTAQUANT, TypeJoueur.VITESSE, 88, 82, 86, 45, 88, 0, 55);
        raphinha.ajouterTechnique(new Technique("Enroulé Carioca", TypeTechnique.TIR, 16, 20));
        raphinha.ajouterTechnique(new Technique("Feinte Brésilienne", TypeTechnique.DRIBBLE, 14, 20));
        equipe.ajouterJoueur(raphinha);

        Joueur jesus = new Joueur("Gabriel Jesus", Poste.ATTAQUANT, TypeJoueur.PUISSANCE, 84, 78, 84, 50, 82, 0, 50);
        jesus.ajouterTechnique(new Technique("Volée de São Paulo", TypeTechnique.TIR, 16, 20));
        jesus.ajouterTechnique(new Technique("Pivot Brésilien", TypeTechnique.DRIBBLE, 14, 20));
        equipe.ajouterJoueur(jesus);

        Joueur yamal = new Joueur("Lamine Yamal", Poste.ATTAQUANT, TypeJoueur.TECHNIQUE, 88, 86, 96, 35, 90, 0, 60);
        yamal.ajouterTechnique(new Technique("Slalom Fantôme", TypeTechnique.DRIBBLE, 20, 30));
        yamal.ajouterTechnique(new Technique("Lucarne Céleste", TypeTechnique.TIR, 18, 25));
        equipe.ajouterJoueur(yamal);

        return equipe;
    }
}