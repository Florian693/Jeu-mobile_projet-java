package com.example.brf;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.example.brf.moteur.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Ecran du match. Elle ne calcule aucune regle : elle lit le moteur.
public class MatchView extends View {

    private static final float TERRAIN_GAUCHE = 0.136f;
    private static final float TERRAIN_DROITE = 0.866f;
    private static final float TERRAIN_HAUT = 0.279f;
    private static final float TERRAIN_BAS = 0.882f;

    private static final String MBAPPE = "mbappe";
    /* 8 directions des images de course, dans le sens inverse des aiguilles d'une montre */
    private static final String[] DIRECTIONS = {
            "east", "north-east", "north", "north-west", "west", "south-west", "south", "south-east"
    };

    // Meme ordre que Effectifs : gardien, 4 defenseurs, 3 milieux, 3 attaquants
    private static final String[][] EQUIPES = {
            {
                    "real_madrid/courtois_1.png",
                    "real_madrid/cucurella_17.png", "real_madrid/huijsen_4.png",
                    "real_madrid/konate_16.png", "real_madrid/valverde_8.png",
                    "real_madrid/bellingham_5.png", "real_madrid/tchouameni_14.png",
                    "real_madrid/arda_guler_15.png",
                    "real_madrid/vinicius_jr_7.png", MBAPPE, "real_madrid/bernardo_silva_20.png"
            },
            {
                    "fc_barcelone/joan_garcia_1.png",
                    "fc_barcelone/balde_3.png", "fc_barcelone/cubarsi_5.png",
                    "fc_barcelone/kounde_23.png", "fc_barcelone/cancelo_2.png",
                    "fc_barcelone/pedri_8.png", "fc_barcelone/frenkie_de_jong_21.png",
                    "fc_barcelone/fermin_lopez_7.png",
                    "fc_barcelone/raphinha_11.png", "fc_barcelone/gabriel_jesus_9.png",
                    "fc_barcelone/lamine_yamal_10.png"
            }
    };

    private static final String[] NOMS_COURTS = {"RMA", "BAR"};
    private static final int[] COULEURS = {0xFFFFFFFF, 0xFFA50044};
    private static final int COULEUR_GARDIEN = 0xFFFFD600;
    private static final int BLEU = 0xFF2E86FF;     /* domicile (joueur 1) */
    private static final int ROUGE = 0xFFE8303A;    /* exterieur (joueur 2) */
    private static final int JAUNE = 0xFFFFC928;
    private static final int ORANGE = 0xFFFF8C1A;
    private static final int MARINE = 0xFF0B1A33;
    private static final int DUREE_ANIMATION = 1200;   /* ms */
    private static final int DUREE_TIR = 1800;         /* ms : frappe, gardien, puis filet ou rebond */
    private static final float FRAPPE = 0.25f;         /* moment du tir ou le ballon part */
    private static final float GARDIEN = 0.6f;         /* moment du tir ou le ballon arrive au gardien */

    /* etats de l'ecran */
    private static final int ORDRES = 0;
    private static final int CHANGEMENT = 1;
    private static final int RESULTAT = 2;
    private static final int DUEL_ATTAQUE = 3;
    private static final int DUEL_DEFENSE = 4;
    private static final int CHOIX_TIR = 5;
    private static final int FIN = 6;
    private static final int ANIMATION = 7;
    private static final int TIR_GARDIEN = 8;

    /* boutons du bas : 0 et 1 actions de base, 2 et 3 techniques, 9 bouton du milieu */
    private static final int MILIEU = 9;

    private final Bitmap fond;
    private final Bitmap ballon;
    private final Bitmap[] logos = new Bitmap[2];
    private final Bitmap[][] mbappeCourse = new Bitmap[8][4];   /* [direction][image] */
    private final Bitmap[][] mbappeTir = new Bitmap[2][9];      /* [0 vers la droite, 1 vers la gauche][image] */
    private final Paint paintImage = new Paint();
    private final Paint plein = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint trait = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint texte = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF zone = new RectF();
    private final Path chemin = new Path();
    private DashPathEffect pointilles;

    private MoteurMatch match;
    private int clubGauche;
    private int clubDroite;
    private Bitmap[] imagesGauche;
    private Bitmap[] imagesDroite;

    private int etat;
    private int joueurQuiJoue;
    private OrdresTour ordres1;
    private OrdresTour ordres2;
    private ResultatAction duel;
    private ChoixAttaque choixAttaque;
    private Technique techniqueAttaque;
    private boolean speciales;      /* vrai : les boutons du bas montrent les techniques */
    private final List<Technique> techniques = new ArrayList<>();
    private final List<Bouton> choix = new ArrayList<>();
    private String grandTexte;
    private String message;

    /* animation : on glisse des positions d'avant le tour vers celles d'apres */
    private final Map<Joueur, Position> avant = new HashMap<>();
    private Joueur porteurAvant;
    private Position ballonAvant;
    private Position ballonApres;
    private boolean butMarque;
    private ResultatAction tir;     /* tir joue pendant l'animation, sinon null */
    private int dureeAnimation;
    private long debutAnimation;
    private List<ResultatAction> resultatsEnAttente;

    private Joueur glisse;
    private float departX;
    private float departY;
    private float doigtX;
    private float doigtY;

    private float u;                /* 1/270 de la hauteur de l'ecran */
    private float tx, ty, tl, th;   /* terrain a l'ecran */
    private Bouton btnTirer;
    private Bouton btnEffacer;
    private Bouton btnFin;
    private Bouton btnRetour;

    public MatchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        fond = BitmapFactory.decodeResource(getResources(), R.drawable.fond_match);
        BitmapFactory.Options petit = new BitmapFactory.Options();
        petit.inSampleSize = 8;
        logos[0] = BitmapFactory.decodeResource(getResources(), R.drawable.logo_real, petit);
        logos[1] = BitmapFactory.decodeResource(getResources(), R.drawable.logo_barca, petit);
        ballon = lireAsset("ballon.webp");
        for (int d = 0; d < 8; d++) {
            for (int i = 0; i < 4; i++) {
                mbappeCourse[d][i] = lireAsset("mbappe/course/" + DIRECTIONS[d] + "/frame_00" + i + ".png");
            }
        }
        for (int i = 0; i < 9; i++) {
            mbappeTir[0][i] = lireAsset("mbappe/tir/east/frame_00" + i + ".png");
            mbappeTir[1][i] = lireAsset("mbappe/tir/west/frame_00" + i + ".png");
        }
        paintImage.setFilterBitmap(true);
        trait.setStyle(Paint.Style.STROKE);
        trait.setStrokeCap(Paint.Cap.ROUND);
        trait.setStrokeJoin(Paint.Join.ROUND);
        texte.setTypeface(Typeface.DEFAULT_BOLD);
        texte.setTextAlign(Paint.Align.CENTER);
        texte.setStrokeJoin(Paint.Join.ROUND);
    }

    public void commencer(MoteurMatch match, int gauche, int droite) {
        this.match = match;
        this.clubGauche = gauche;
        this.clubDroite = droite;
        this.imagesGauche = chargerEquipe(gauche, true);
        this.imagesDroite = chargerEquipe(droite, false);
        nouveauTour();
    }

    @Override
    protected void onSizeChanged(int w, int h, int ancienneLargeur, int ancienneHauteur) {
        super.onSizeChanged(w, h, ancienneLargeur, ancienneHauteur);
        u = h / 270f;
        pointilles = new DashPathEffect(new float[]{5 * u, 4 * u}, 0);
        btnTirer = new Bouton("TIRER", 6 * u, 5 * u, 76 * u, 31 * u);
        btnTirer.couleur = Bouton.ORANGE;
        btnEffacer = new Bouton("EFFACER", 82 * u, 5 * u, 152 * u, 31 * u);
        btnFin = new Bouton("FIN DU TOUR", w - 126 * u, 5 * u, w - 38 * u, 31 * u);
        btnRetour = new Bouton("", w - 32 * u, 5 * u, w - 6 * u, 31 * u);
        btnRetour.couleur = Bouton.ROUGE;
        if (enTrainDeChoisir()) {
            preparerChoix();
        }
    }

    /* Deroulement du tour */

    private void nouveauTour() {
        ordres1 = new OrdresTour();
        ordres2 = new OrdresTour();
        joueurQuiJoue = 1;
        grandTexte = null;
        message = null;
        duel = null;
        etat = ORDRES;
        invalidate();
    }

    private void finDesOrdres() {
        if (joueurQuiJoue == 1) {
            joueurQuiJoue = 2;
            etat = CHANGEMENT;
            return;
        }
        retenirPositions();
        animer(match.jouerTour(ordres1, ordres2));
    }

    /* photo des positions avant que le moteur joue */
    private void retenirPositions() {
        avant.clear();
        for (Joueur joueur : match.getEquipe1().getJoueurs()) {
            avant.put(joueur, position(joueur));
        }
        for (Joueur joueur : match.getEquipe2().getJoueurs()) {
            avant.put(joueur, position(joueur));
        }
        porteurAvant = match.getPorteur();
        ballonAvant = position(porteurAvant);
    }

    private void animer(List<ResultatAction> resultats) {
        butMarque = false;
        tir = null;
        dureeAnimation = DUREE_ANIMATION;
        ballonApres = position(match.getPorteur());
        for (ResultatAction resultat : resultats) {
            if (resultat.getType() == TypeResultat.BUT || resultat.getType() == TypeResultat.TIR_ARRETE) {
                tir = resultat;
                dureeAnimation = DUREE_TIR;
            }
            if (resultat.getType() == TypeResultat.BUT) {
                butMarque = true;   /* les joueurs restent en place, le ballon finit dans le filet */
                ballonApres = new Position(Terrain.LARGEUR / 2.0, -4);
                if (aGauche(resultat.getAttaquant())) {
                    ballonApres = new Position(Terrain.LARGEUR / 2.0, Terrain.LONGUEUR + 4);
                }
            }
        }
        if (!quelqueChoseBouge()) {
            traiter(resultats);
            return;
        }
        resultatsEnAttente = resultats;
        debutAnimation = SystemClock.uptimeMillis();
        etat = ANIMATION;
        postDelayed(() -> finAnimation(), dureeAnimation);
        invalidate();
    }

    private void finAnimation() {
        if (etat == ANIMATION) {
            traiter(resultatsEnAttente);
            invalidate();
        }
    }

    private boolean quelqueChoseBouge() {
        if (ballonAvant.distance(ballonApres) > 0.1) {
            return true;
        }
        for (Joueur joueur : avant.keySet()) {
            if (avant.get(joueur).distance(position(joueur)) > 0.1) {
                return true;
            }
        }
        return false;
    }

    /* 0 au debut de l'animation, 1 a la fin */
    private float avancement() {
        float t = (SystemClock.uptimeMillis() - debutAnimation) / (float) dureeAnimation;
        return Math.min(1, t);
    }

    /* position dessinee : pendant l'animation, entre avant et apres */
    private Position positionAffichee(Joueur joueur) {
        if (etat != ANIMATION) {
            return position(joueur);
        }
        if (butMarque) {
            return avant.get(joueur);
        }
        return Position.pointSurTrajet(avant.get(joueur), position(joueur), avancement());
    }

    private void traiter(List<ResultatAction> resultats) {
        grandTexte = null;
        message = "";
        for (ResultatAction resultat : resultats) {
            String titre = titre(resultat.getType());
            if (titre != null && grandTexte == null) {
                grandTexte = titre;
                message = resultat.getMessage();
            } else if (titre != null) {
                message = message + "   " + resultat.getMessage();
            }
        }
        ResultatAction dernier = resultats.get(resultats.size() - 1);
        if (dernier.getType() == TypeResultat.DUEL_A_JOUER) {
            duel = dernier;
            etat = DUEL_ATTAQUE;
            speciales = false;
            preparerChoix();
            return;
        }
        if (dernier.getType() == TypeResultat.TIR_A_JOUER) {
            duel = dernier;     /* tireur contre gardien */
            etat = TIR_GARDIEN;
            speciales = false;
            preparerChoix();
            return;
        }
        duel = null;
        if (grandTexte == null) {
            continuer();    /* rien a montrer : tour suivant */
        } else {
            etat = RESULTAT;
        }
    }

    private String titre(TypeResultat type) {
        if (type == TypeResultat.BUT) {
            return "BUT !";
        }
        if (type == TypeResultat.TIR_ARRETE) {
            return "ARRÊT !";
        }
        if (type == TypeResultat.PASSE_REUSSIE) {
            return "PASSE RÉUSSIE";
        }
        if (type == TypeResultat.INTERCEPTION) {
            return "INTERCEPTION !";
        }
        if (type == TypeResultat.DUEL_GAGNE_ATTAQUE) {
            return "DUEL GAGNÉ !";
        }
        if (type == TypeResultat.DUEL_GAGNE_DEFENSE) {
            return "BALLON RÉCUPÉRÉ !";
        }
        if (type == TypeResultat.MI_TEMPS) {
            return "MI-TEMPS";
        }
        if (type == TypeResultat.FIN_DU_MATCH) {
            return "FIN DU MATCH";
        }
        return null;
    }

    private void continuer() {
        if (match.estTermine()) {
            etat = FIN;
            return;
        }
        nouveauTour();
    }

    private void effacer() {
        if (joueurQuiJoue == 2) {
            ordres2 = new OrdresTour();
        } else {
            ordres1 = new OrdresTour();
        }
    }

    /* Boutons du bas : 2 actions de base (orange) ou 2 techniques (bleu), bouton du milieu pour changer */
    private void preparerChoix() {
        choix.clear();
        techniques.clear();
        Joueur joueur = joueurDuChoix();
        String[] bases = {"DRIBBLE", "ESQUIVE"};
        TypeTechnique type = TypeTechnique.DRIBBLE;
        if (etat == DUEL_DEFENSE) {
            bases = new String[]{"TACLE", "BLOCAGE"};
            type = TypeTechnique.DEFENSE;
        }
        if (etat == CHOIX_TIR) {
            bases = new String[]{"TIR", "ANNULER"};
            type = TypeTechnique.TIR;
        }
        if (etat == TIR_GARDIEN) {
            bases = new String[]{"ARRÊT", "PLONGEON"};
            type = TypeTechnique.ARRET;
        }
        techniques.addAll(joueur.getTechniquesDuType(type));

        float cx = getWidth() / 2f;
        float bas = getHeight() - 6 * u;
        float haut = bas - 32 * u;
        float largeur = Math.min(185 * u, getWidth() / 2f - 60 * u);
        for (int i = 0; i < 2; i++) {
            float g = cx + 26 * u;
            if (i == 0) {
                g = cx - 26 * u - largeur;
            }
            Bouton b;
            if (!speciales) {
                b = new Bouton(bases[i], g, haut, g + largeur, bas);
                b.couleur = Bouton.ORANGE;
                if (etat == CHOIX_TIR && i == 1) {
                    b.couleur = Bouton.GRIS;
                }
                b.code = i;
            } else if (i < techniques.size()) {
                Technique technique = techniques.get(i);
                b = new Bouton(technique.getNom(), g, haut, g + largeur, bas);
                b.detail = technique.getCoutPE() + " PE";
                b.actif = joueur.peutLancer(technique);
                b.code = 2 + i;
            } else {
                b = new Bouton("AUCUNE", g, haut, g + largeur, bas);
                b.actif = false;
            }
            choix.add(b);
        }
        Bouton milieu = new Bouton("", cx - 20 * u, haut - 4 * u, cx + 20 * u, bas + 4 * u);
        milieu.rond = true;
        milieu.code = MILIEU;
        choix.add(milieu);
    }

    private void choisir(int code) {
        if (code == MILIEU) {
            speciales = !speciales;
            preparerChoix();
            return;
        }
        Technique technique = null;
        if (code >= 2) {
            technique = techniques.get(code - 2);
        }
        speciales = false;
        if (etat == DUEL_ATTAQUE) {
            choixAttaque = ChoixAttaque.DRIBBLE;
            if (code == 1) {
                choixAttaque = ChoixAttaque.ESQUIVE;
            }
            techniqueAttaque = technique;
            etat = DUEL_DEFENSE;
            preparerChoix();
        } else if (etat == DUEL_DEFENSE) {
            ChoixDefense choixDefense = ChoixDefense.TACLE;
            if (code == 1) {
                choixDefense = ChoixDefense.BLOCAGE;
            }
            retenirPositions();
            animer(match.jouerDuel(choixAttaque, techniqueAttaque, choixDefense, technique));
        } else if (etat == TIR_GARDIEN) {
            ChoixGardien choixGardien = ChoixGardien.ARRET;
            if (code == 1) {
                choixGardien = ChoixGardien.PLONGEON;
            }
            retenirPositions();
            animer(match.jouerTir(choixGardien, technique));
        } else if (etat == CHOIX_TIR) {
            if (code != 1) {
                ordresQuiJouent().tirer(technique);
            }
            etat = ORDRES;
        }
    }

    private void retourAccueil() {
        Intent intent = new Intent(getContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        getContext().startActivity(intent);
        ((Activity) getContext()).finish();
    }

    /* Outils */

    private Equipe equipeQuiJoue() {
        if (joueurQuiJoue == 2) {
            return match.getEquipe2();
        }
        return match.getEquipe1();
    }

    private OrdresTour ordresQuiJouent() {
        if (joueurQuiJoue == 2) {
            return ordres2;
        }
        return ordres1;
    }

    private Joueur joueurDuChoix() {
        if (etat == DUEL_ATTAQUE) {
            return duel.getAttaquant();
        }
        if (etat == DUEL_DEFENSE || etat == TIR_GARDIEN) {
            return duel.getDefenseur();
        }
        return match.getPorteur();
    }

    private boolean enTrainDeChoisir() {
        return etat == DUEL_ATTAQUE || etat == DUEL_DEFENSE || etat == CHOIX_TIR || etat == TIR_GARDIEN;
    }

    /* le porteur glisse sur un coequipier : c'est une passe */
    private Joueur passeVisee() {
        if (glisse == null || glisse != match.getPorteur() || !aLeBallon()) {
            return null;
        }
        Joueur arrivee = joueurSousLeDoigt(versTerrain(doigtX, doigtY));
        if (arrivee == glisse) {
            return null;
        }
        return arrivee;
    }

    private boolean peutBouger(Joueur joueur) {
        return joueur != match.getJoueurBattu();
    }

    /* mouvement a l'ecran -> une des 8 directions des images */
    private int direction(float dx, float dy) {
        int secteur = (int) Math.round(Math.atan2(-dy, dx) / (Math.PI / 4));
        return (secteur + 8) % 8;
    }

    /* pendant l'animation : ce joueur vient de tirer ? */
    private boolean vientDeTirer(Joueur joueur) {
        return tir != null && tir.getAttaquant() == joueur;
    }

    private boolean aLeBallon() {
        return equipeQuiJoue().getJoueurs().contains(match.getPorteur());
    }

    private boolean aGauche(Joueur joueur) {
        return match.getEquipe1().getJoueurs().contains(joueur);
    }

    private Position butAdverse() {
        if (joueurQuiJoue == 2) {
            return MoteurMatch.BUT_DU_BAS;
        }
        return MoteurMatch.BUT_DU_HAUT;
    }

    private Position position(Joueur joueur) {
        return match.getTerrain().getPosition(joueur);
    }

    /* la fleche s'arrete au cercle de course et au bord du terrain */
    private Position limiter(Joueur joueur, Position cible) {
        Position bout = position(joueur).versCible(cible, joueur.getDistanceParTour());
        return match.getTerrain().borner(bout);
    }

    private String score() {
        return match.getEquipe1().getNomEquipe() + "  " + match.getEquipe1().getButs() + " - "
                + match.getEquipe2().getButs() + "  " + match.getEquipe2().getNomEquipe();
    }

    private Bitmap[] chargerEquipe(int equipe, boolean aGauche) {
        String[] noms = EQUIPES[equipe];
        Bitmap[] images = new Bitmap[noms.length];
        for (int i = 0; i < noms.length; i++) {
            String chemin;
            if (noms[i].equals(MBAPPE)) {
                chemin = "mbappe/" + (aGauche ? "east" : "west") + ".png";
            } else {
                chemin = "portraits/" + noms[i];
            }
            images[i] = lireAsset(chemin);
        }
        return images;
    }

    private Bitmap lireAsset(String chemin) {
        try (InputStream in = getContext().getAssets().open(chemin)) {
            return BitmapFactory.decodeStream(in);
        } catch (IOException e) {
            return null;
        }
    }

    /* moteur : x = largeur (0-60), y = longueur (0-100) -> ecran couche */
    private float ecranX(Position p) {
        return tx + (float) (p.getY() / Terrain.LONGUEUR) * tl;
    }

    private float ecranY(Position p) {
        return ty + (float) (p.getX() / Terrain.LARGEUR) * th;
    }

    private Position versTerrain(float x, float y) {
        return new Position((y - ty) / th * Terrain.LARGEUR, (x - tx) / tl * Terrain.LONGUEUR);
    }

    private float diametre() {
        return th * 0.12f;
    }

    /* Dessin */

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        dessinerFond(canvas);
        if (match == null) {
            return;
        }
        if (etat == ORDRES) {
            dessinerSol(canvas);
        }
        List<Joueur> tous = new ArrayList<>(match.getEquipe1().getJoueurs());
        tous.addAll(match.getEquipe2().getJoueurs());
        for (Joueur joueur : tous) {
            float x = ecranX(positionAffichee(joueur));
            float y = ecranY(positionAffichee(joueur));
            float d = diametre();
            /* joueur battu : il clignote */
            boolean clignote = joueur == match.getJoueurBattu() && (SystemClock.uptimeMillis() / 250) % 2 == 0;
            if (clignote) {
                canvas.saveLayerAlpha(x - d, y - d, x + d, y + d, 60);
            }
            dessinerJoueur(canvas, joueur, x, y, d);
            if (clignote) {
                canvas.restore();
            }
        }
        if (etat == ORDRES) {
            dessinerOrdres(canvas);
        }
        dessinerBallon(canvas);
        dessinerNoms(canvas);
        dessinerScore(canvas);
        if (etat == ORDRES) {
            btnTirer.actif = aLeBallon() && match.peutTirer();
            btnTirer.dessiner(canvas);
            btnEffacer.dessiner(canvas);
            btnFin.dessiner(canvas);
        }
        if (enTrainDeChoisir()) {
            dessinerBarre(canvas);
        }
        if (etat == CHANGEMENT) {
            dessinerBandeau(canvas, "JOUEUR 2", "Passe le téléphone, puis touche l'écran");
        }
        if (etat == RESULTAT) {
            dessinerBandeau(canvas, grandTexte, message);
        }
        if (etat == FIN) {
            dessinerBandeau(canvas, "FIN DU MATCH", score());
        }
        btnRetour.dessiner(canvas);
        iconeRetour(canvas, btnRetour.zone.centerX(), btnRetour.zone.centerY(), btnRetour.zone.height());

        /* on redessine tant que ca bouge ou que ca clignote */
        if (etat == ANIMATION) {
            postInvalidateOnAnimation();
        } else if (match.getJoueurBattu() != null) {
            postInvalidateDelayed(125);
        }
    }

    private void dessinerFond(Canvas canvas) {
        float bw = fond.getWidth();
        float bh = fond.getHeight();
        float echelle = Math.max(getWidth() / bw, getHeight() / bh);
        float largeurFond = bw * echelle;
        float hauteurFond = bh * echelle;
        float decalX = (getWidth() - largeurFond) / 2f;
        float decalY = (getHeight() - hauteurFond) / 2f;
        zone.set(decalX, decalY, decalX + largeurFond, decalY + hauteurFond);
        canvas.drawBitmap(fond, null, zone, paintImage);

        tx = decalX + TERRAIN_GAUCHE * largeurFond;
        ty = decalY + TERRAIN_HAUT * hauteurFond;
        tl = (TERRAIN_DROITE - TERRAIN_GAUCHE) * largeurFond;
        th = (TERRAIN_BAS - TERRAIN_HAUT) * hauteurFond;
    }

    /* sous les joueurs : zone de tir et cercle de course */
    private void dessinerSol(Canvas canvas) {
        if (aLeBallon()) {
            canvas.save();
            canvas.clipRect(tx, ty, tx + tl, ty + th);
            trait.setPathEffect(pointilles);
            ovale(canvas, butAdverse(), MoteurMatch.DISTANCE_TIR, 0x18FFEB3B, 0xB0FFEB3B);
            trait.setPathEffect(null);
            canvas.restore();
        }
        if (glisse != null && peutBouger(glisse)) {
            ovale(canvas, position(glisse), glisse.getDistanceParTour(), 0x30FFFFFF, 0xC0FFFFFF);
        }
    }

    /* au-dessus des joueurs : passe, fleches, tir */
    private void dessinerOrdres(Canvas canvas) {
        OrdresTour ordres = ordresQuiJouent();
        int couleur = joueurQuiJoue == 2 ? ROUGE : BLEU;
        Position porteur = position(match.getPorteur());

        Joueur visee = passeVisee();
        if (visee != null) {
            passe(canvas, porteur, position(visee));
        } else if (ordres.getAction() == ActionPorteur.PASSER && ordres.getReceveur() != null) {
            passe(canvas, porteur, position(ordres.getReceveur()));
        }
        for (Joueur joueur : ordres.getFleches().keySet()) {
            if (joueur != glisse) {
                Position cible = ordres.getFleche(joueur);
                fleche(canvas, ecranX(position(joueur)), ecranY(position(joueur)), ecranX(cible), ecranY(cible), couleur);
            }
        }
        if (glisse != null && visee == null && peutBouger(glisse)) {
            Position depart = position(glisse);
            Position cible = limiter(glisse, versTerrain(doigtX, doigtY));
            fleche(canvas, ecranX(depart), ecranY(depart), ecranX(cible), ecranY(cible), couleur);
        }
        if (ordres.getAction() == ActionPorteur.TIRER) {
            fleche(canvas, ecranX(porteur), ecranY(porteur), ecranX(butAdverse()), ecranY(butAdverse()), JAUNE);
        }
    }

    /* passe : pointilles orange + cercle orange autour du receveur */
    private void passe(Canvas canvas, Position depart, Position receveur) {
        trait.setColor(ORANGE);
        trait.setStrokeWidth(2.4f * u);
        trait.setPathEffect(pointilles);
        canvas.drawLine(ecranX(depart), ecranY(depart), ecranX(receveur), ecranY(receveur), trait);
        trait.setPathEffect(null);
        trait.setColor(MARINE);
        trait.setStrokeWidth(4.6f * u);
        canvas.drawCircle(ecranX(receveur), ecranY(receveur), diametre() * 0.66f, trait);
        trait.setColor(ORANGE);
        trait.setStrokeWidth(3 * u);
        canvas.drawCircle(ecranX(receveur), ecranY(receveur), diametre() * 0.66f, trait);
    }

    private void dessinerJoueur(Canvas canvas, Joueur joueur, float cx, float cy, float d) {
        boolean gauche = aGauche(joueur);
        int i = gauche ? match.getEquipe1().getJoueurs().indexOf(joueur) : match.getEquipe2().getJoueurs().indexOf(joueur);
        int club = gauche ? clubGauche : clubDroite;
        Bitmap image = gauche ? imagesGauche[i] : imagesDroite[i];

        if (duel != null && (joueur == duel.getAttaquant() || joueur == duel.getDefenseur())) {
            trait.setColor(0xFFFF5252);
            trait.setStrokeWidth(2 * u);
            canvas.drawCircle(cx, cy, d * 0.7f, trait);
        }

        if (EQUIPES[club][i].equals(MBAPPE)) {
            dessinerMbappe(canvas, joueur, image, gauche, cx, cy, d);
            return;
        }

        float r = d / 2f;
        plein.setColor(COULEURS[club]);
        canvas.drawCircle(cx, cy, r, plein);
        if (image != null) {
            chemin.reset();
            chemin.addCircle(cx, cy, r, Path.Direction.CW);
            canvas.save();
            canvas.clipPath(chemin);
            zone.set(cx - r, cy - r, cx + r, cy + r);
            canvas.drawBitmap(image, null, zone, paintImage);
            canvas.restore();
        }
        trait.setColor(i == 0 ? COULEUR_GARDIEN : 0xFF000000);
        trait.setStrokeWidth(d / 25f);
        canvas.drawCircle(cx, cy, r, trait);
    }

    /* Mbappe : il court pendant l'animation, il frappe quand il tire */
    private void dessinerMbappe(Canvas canvas, Joueur joueur, Bitmap image, boolean gauche, float cx, float cy, float d) {
        if (etat == ANIMATION) {
            long temps = SystemClock.uptimeMillis() - debutAnimation;
            Position debut = avant.get(joueur);
            Position fin = position(joueur);
            if (vientDeTirer(joueur)) {
                /* 9 images de frappe sur le debut du tir : le pied touche le ballon vers la 6e */
                int numero = (int) Math.min(8, avancement() / FRAPPE * 6);
                image = mbappeTir[gauche ? 0 : 1][numero];
            } else if (!butMarque && debut.distance(fin) > 0.1) {
                int sens = direction(ecranX(fin) - ecranX(debut), ecranY(fin) - ecranY(debut));
                image = mbappeCourse[sens][(int) (temps / 110) % 4];
            }
        }
        if (image == null) {
            return;
        }
        /* les images de course sont plus grandes (176 px) que celle au repos (128 px) */
        float t = d * 1.7f * image.getWidth() / 128f;
        zone.set(cx - t / 2f, cy - t / 2f, cx + t / 2f, cy + t / 2f);
        canvas.drawBitmap(image, null, zone, paintImage);
    }

    private void dessinerBallon(Canvas canvas) {
        Joueur porteur = match.getPorteur();
        float d = diametre();
        float x = ecranX(position(porteur)) + decalageBallon(porteur);
        float y = ecranY(position(porteur)) + d * 0.35f;

        if (etat == ANIMATION && tir != null) {
            dessinerBallonTir(canvas, d);
            return;
        }
        if (etat == ANIMATION) {
            /* le ballon roule de l'ancien porteur vers le nouveau */
            float t = avancement();
            float x0 = ecranX(ballonAvant) + decalageBallon(porteurAvant);
            float y0 = ecranY(ballonAvant) + d * 0.35f;
            float tourne = t * Math.max(Math.abs(x - x0), Math.abs(y - y0)) / d * 180;
            x = x0 + (x - x0) * t;
            y = y0 + (y - y0) * t;
            canvas.save();
            canvas.rotate(tourne, x, y);
            dessinerImageBallon(canvas, x, y, d * 0.25f);
            canvas.restore();
            return;
        }

        float px = ecranX(position(porteur));
        float py = ecranY(position(porteur));
        trait.setColor(JAUNE);
        trait.setStrokeWidth(1.6f * u);
        canvas.drawCircle(px, py, d * 0.62f, trait);
        dessinerImageBallon(canvas, x, y, d * 0.25f);
    }

    /* tir : le ballon part a la frappe et va au gardien,
       puis il reste dans ses mains, finit dans le filet ou est repousse vers un joueur */
    private void dessinerBallonTir(Canvas canvas, float d) {
        float t = avancement();
        Joueur gardien = tir.getDefenseur();
        Position positionGardien = avant.get(gardien);
        float x0 = ecranX(ballonAvant) + decalageBallon(tir.getAttaquant());
        float y0 = ecranY(ballonAvant) + d * 0.35f;
        float x1 = ecranX(positionGardien) + decalageBallon(gardien);
        float y1 = ecranY(positionGardien) + d * 0.35f;
        float x2 = x1;      /* arret : il garde le ballon */
        float y2 = y1;
        boolean garde = true;
        if (tir.getType() == TypeResultat.BUT) {
            x2 = ecranX(ballonApres);
            y2 = ecranY(ballonApres);
            garde = false;
        } else if (match.getPorteur() != gardien) {
            Joueur rebond = match.getPorteur();
            x2 = ecranX(position(rebond)) + decalageBallon(rebond);
            y2 = ecranY(position(rebond)) + d * 0.35f;
            garde = false;
        }

        float x = x0;
        float y = y0;
        if (t > GARDIEN) {
            float f = (t - GARDIEN) / (1 - GARDIEN);
            x = x1 + (x2 - x1) * f;
            y = y1 + (y2 - y1) * f;
        } else if (t > FRAPPE) {
            float f = (t - FRAPPE) / (GARDIEN - FRAPPE);
            x = x0 + (x1 - x0) * f;
            y = y0 + (y1 - y0) * f;
        }

        /* le ballon tourne seulement quand il vole */
        float vol = garde ? Math.min(t, GARDIEN) : t;
        canvas.save();
        canvas.rotate(Math.max(0, vol - FRAPPE) * 900, x, y);
        dessinerImageBallon(canvas, x, y, d * 0.25f);
        canvas.restore();
    }

    private float decalageBallon(Joueur porteur) {
        if (aGauche(porteur)) {
            return diametre() * 0.55f;
        }
        return -diametre() * 0.55f;
    }

    private void dessinerImageBallon(Canvas canvas, float x, float y, float r) {
        if (ballon != null) {
            zone.set(x - r, y - r, x + r, y + r);
            canvas.drawBitmap(ballon, null, zone, paintImage);
        } else {
            plein.setColor(0xFFFFFFFF);
            canvas.drawCircle(x, y, r, plein);
        }
    }

    private void dessinerNoms(Canvas canvas) {
        if (duel != null) {
            nom(canvas, duel.getAttaquant(), true);
            nom(canvas, duel.getDefenseur(), true);
            return;
        }
        Joueur porteur = match.getPorteur();
        nom(canvas, porteur, etat == CHOIX_TIR || (etat == ORDRES && aLeBallon()));
        if (glisse != null && glisse != porteur) {
            nom(canvas, glisse, false);
        }
    }

    private void nom(Canvas canvas, Joueur joueur, boolean avecPE) {
        String s = joueur.getNom();
        if (avecPE) {
            s = s + "  " + joueur.getPeActuel() + " PE";
        }
        Position p = positionAffichee(joueur);
        contour(canvas, s, ecranX(p), ecranY(p) - diametre() * 0.85f, 8 * u, 0xFFFFFFFF);
    }

    private void dessinerScore(Canvas canvas) {
        float cx = getWidth() / 2f;
        cadre(canvas, cx - 84 * u, 3 * u, cx + 84 * u, 35 * u, 6 * u, 0xE00B1A33, 0xFFFFFFFF, u);
        dessinerLogo(canvas, logos[clubGauche], cx - 72 * u, 14 * u);
        dessinerLogo(canvas, logos[clubDroite], cx + 72 * u, 14 * u);
        contour(canvas, NOMS_COURTS[clubGauche], cx - 42 * u, 14 * u, 10 * u, 0xFFFFFFFF);
        contour(canvas, NOMS_COURTS[clubDroite], cx + 42 * u, 14 * u, 10 * u, 0xFFFFFFFF);
        contour(canvas, match.getEquipe1().getButs() + " - " + match.getEquipe2().getButs(), cx, 14 * u, 16 * u, JAUNE);

        int tour = match.getTour();
        if (etat == ORDRES || etat == CHANGEMENT) {
            tour++;
        }
        tour = Math.max(1, Math.min(tour, 2 * MoteurMatch.TOURS_PAR_MI_TEMPS));
        String periode = (tour <= MoteurMatch.TOURS_PAR_MI_TEMPS ? "1re" : "2e") + " mi-temps   Tour "
                + ((tour - 1) % MoteurMatch.TOURS_PAR_MI_TEMPS + 1) + "/" + MoteurMatch.TOURS_PAR_MI_TEMPS;
        ecrire(canvas, periode, cx, 28 * u, 7 * u, 0xFFE0E0E0);

        /* qui a le telephone */
        if (etat == ORDRES || enTrainDeChoisir()) {
            int numero = joueurQuiJoue;
            if (enTrainDeChoisir()) {
                numero = aGauche(joueurDuChoix()) ? 1 : 2;
            }
            cadre(canvas, cx - 36 * u, 39 * u, cx + 36 * u, 53 * u, 7 * u, numero == 1 ? BLEU : ROUGE, 0xFFFFFFFF, u);
            contour(canvas, "JOUEUR " + numero, cx, 46 * u, 9 * u, 0xFFFFFFFF);
        }
    }

    private void dessinerLogo(Canvas canvas, Bitmap logo, float cx, float cy) {
        if (logo == null) {
            return;
        }
        float r = 9 * u;
        zone.set(cx - r, cy - r, cx + r, cy + r);
        canvas.drawBitmap(logo, null, zone, paintImage);
    }

    /* barre du bas pendant un duel ou un tir */
    private void dessinerBarre(Canvas canvas) {
        Bouton milieu = choix.get(2);
        float haut = milieu.zone.top - 4 * u;
        plein.setColor(0xC00B1A33);
        canvas.drawRect(0, haut, getWidth(), getHeight(), plein);
        trait.setColor(0xFF6FB3FF);
        trait.setStrokeWidth(u);
        canvas.drawLine(0, haut, getWidth(), haut, trait);

        Joueur joueur = joueurDuChoix();
        String action = " ATTAQUE";
        if (etat == DUEL_DEFENSE) {
            action = " DÉFEND";
        }
        if (etat == CHOIX_TIR) {
            action = " TIRE";
        }
        if (etat == TIR_GARDIEN) {
            action = " PROTÈGE SON BUT";
        }
        contour(canvas, joueur.getNom().toUpperCase() + action, getWidth() / 2f, haut - 11 * u, 12 * u, 0xFFFFFFFF);

        for (Bouton b : choix) {
            b.dessiner(canvas);
        }
        float cx = milieu.zone.centerX();
        float cy = milieu.zone.centerY();
        if (speciales) {
            iconeRetour(canvas, cx, cy, milieu.zone.height());
        } else if (ballon != null) {
            float r = milieu.zone.height() * 0.28f;
            zone.set(cx - r, cy - r, cx + r, cy + r);
            canvas.drawBitmap(ballon, null, zone, paintImage);
        }
    }

    private void dessinerBandeau(Canvas canvas, String titre, String sousTitre) {
        float cx = getWidth() / 2f;
        float cy = getHeight() / 2f;
        plein.setColor(0x60000000);
        canvas.drawRect(0, 0, getWidth(), getHeight(), plein);
        plein.setColor(0xE00B1A33);
        canvas.drawRect(0, cy - 36 * u, getWidth(), cy + 36 * u, plein);
        trait.setColor(JAUNE);
        trait.setStrokeWidth(1.5f * u);
        canvas.drawLine(0, cy - 36 * u, getWidth(), cy - 36 * u, trait);
        canvas.drawLine(0, cy + 36 * u, getWidth(), cy + 36 * u, trait);
        contour(canvas, titre, cx, cy - 9 * u, ajuster(titre, 30 * u, getWidth() - 40 * u), JAUNE);
        contour(canvas, sousTitre, cx, cy + 20 * u, ajuster(sousTitre, 11 * u, getWidth() - 40 * u), 0xFFFFFFFF);
    }

    /* grosse fleche pleine, du bord du joueur jusqu'a la cible */
    private void fleche(Canvas canvas, float x1, float y1, float x2, float y2, int couleur) {
        float dx = x2 - x1;
        float dy = y2 - y1;
        float longueur = (float) Math.hypot(dx, dy);
        float debut = diametre() / 2;
        if (longueur < debut + 4 * u) {
            return;
        }
        float ux = dx / longueur;
        float uy = dy / longueur;
        float px = -uy;     /* perpendiculaire */
        float py = ux;
        float queue = 2 * u;
        float corps = 3.5f * u;
        float tete = 8 * u;
        float pointe = Math.min(13 * u, longueur - debut);
        float ax = x1 + ux * debut;
        float ay = y1 + uy * debut;
        float bx = x2 - ux * pointe;
        float by = y2 - uy * pointe;

        chemin.reset();
        chemin.moveTo(ax + px * queue, ay + py * queue);
        chemin.lineTo(bx + px * corps, by + py * corps);
        chemin.lineTo(bx + px * tete, by + py * tete);
        chemin.lineTo(x2, y2);
        chemin.lineTo(bx - px * tete, by - py * tete);
        chemin.lineTo(bx - px * corps, by - py * corps);
        chemin.lineTo(ax - px * queue, ay - py * queue);
        chemin.close();
        plein.setColor(couleur);
        canvas.drawPath(chemin, plein);
        trait.setColor(MARINE);
        trait.setStrokeWidth(1.2f * u);
        canvas.drawPath(chemin, trait);
    }

    /* fleche en U vers la gauche (retour) */
    private void iconeRetour(Canvas canvas, float cx, float cy, float taille) {
        float r = taille * 0.2f;
        float x = cx;
        float y = cy - r * 0.3f;
        chemin.reset();
        chemin.moveTo(x - r * 0.3f, y + r);
        chemin.lineTo(x, y + r);
        zone.set(x - r, y - r, x + r, y + r);
        chemin.arcTo(zone, 90, -180);
        chemin.lineTo(x - r * 0.8f, y - r);
        trait.setColor(0xFFFFFFFF);
        trait.setStrokeWidth(r * 0.55f);
        trait.setStrokeCap(Paint.Cap.BUTT);
        canvas.drawPath(chemin, trait);
        trait.setStrokeCap(Paint.Cap.ROUND);

        float p = r * 0.85f;
        chemin.reset();
        chemin.moveTo(x - r * 0.3f - p, y + r);
        chemin.lineTo(x - r * 0.3f, y + r - p * 0.8f);
        chemin.lineTo(x - r * 0.3f, y + r + p * 0.8f);
        chemin.close();
        plein.setColor(0xFFFFFFFF);
        canvas.drawPath(chemin, plein);
    }

    /* cercle du moteur (en unites du terrain) dessine sur le terrain couche */
    private void ovale(Canvas canvas, Position centre, double rayon, int fond, int bord) {
        float rx = (float) (rayon / Terrain.LONGUEUR) * tl;
        float ry = (float) (rayon / Terrain.LARGEUR) * th;
        float cx = ecranX(centre);
        float cy = ecranY(centre);
        zone.set(cx - rx, cy - ry, cx + rx, cy + ry);
        plein.setColor(fond);
        canvas.drawOval(zone, plein);
        trait.setColor(bord);
        trait.setStrokeWidth(1.2f * u);
        canvas.drawOval(zone, trait);
    }

    private void cadre(Canvas canvas, float g, float h, float d, float b, float rayon, int fond, int bord, float epaisseur) {
        zone.set(g, h, d, b);
        plein.setColor(fond);
        canvas.drawRoundRect(zone, rayon, rayon, plein);
        if (epaisseur > 0) {
            trait.setColor(bord);
            trait.setStrokeWidth(epaisseur);
            canvas.drawRoundRect(zone, rayon, rayon, trait);
        }
    }

    private void ecrire(Canvas canvas, String s, float x, float y, float taille, int couleur) {
        texte.setTextSize(taille);
        texte.setColor(couleur);
        canvas.drawText(s, x, y - (texte.ascent() + texte.descent()) / 2, texte);
    }

    /* texte entoure de sombre, comme dans les jeux */
    private void contour(Canvas canvas, String s, float x, float y, float taille, int couleur) {
        texte.setTextSize(taille);
        float base = y - (texte.ascent() + texte.descent()) / 2;
        texte.setStyle(Paint.Style.STROKE);
        texte.setStrokeWidth(taille * 0.18f);
        texte.setColor(MARINE);
        canvas.drawText(s, x, base, texte);
        texte.setStyle(Paint.Style.FILL);
        texte.setColor(couleur);
        canvas.drawText(s, x, base, texte);
    }

    private float ajuster(String s, float taille, float place) {
        texte.setTextSize(taille);
        float largeur = texte.measureText(s);
        if (largeur > place) {
            return taille * place / largeur;
        }
        return taille;
    }

    /* Doigt */

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (match == null) {
            return false;
        }
        float x = event.getX();
        float y = event.getY();
        int action = event.getAction();
        if (action == MotionEvent.ACTION_UP && glisse == null && btnRetour.contient(x, y)) {
            retourAccueil();
            return true;
        }
        if (etat == ANIMATION) {
            return true;    /* on attend la fin de l'animation */
        }
        if (etat == ORDRES) {
            toucherOrdres(action, x, y);
        } else if (action == MotionEvent.ACTION_UP) {
            if (etat == CHANGEMENT) {
                etat = ORDRES;
            } else if (etat == RESULTAT) {
                continuer();
            } else if (etat == FIN) {
                retourAccueil();
                return true;
            } else {
                for (Bouton b : choix) {
                    if (b.contient(x, y)) {
                        choisir(b.code);
                        break;
                    }
                }
            }
        }
        invalidate();
        return true;
    }

    private void toucherOrdres(int action, float x, float y) {
        if (action == MotionEvent.ACTION_DOWN) {
            departX = x;
            departY = y;
            glisse = null;
            if (!surUnBouton(x, y)) {
                glisse = joueurSousLeDoigt(versTerrain(x, y));
            }
        }
        doigtX = x;
        doigtY = y;
        if (action != MotionEvent.ACTION_UP) {
            return;
        }
        if (glisse == null) {
            if (btnTirer.contient(x, y)) {
                etat = CHOIX_TIR;
                speciales = false;
                preparerChoix();
            } else if (btnEffacer.contient(x, y)) {
                effacer();
            } else if (btnFin.contient(x, y)) {
                finDesOrdres();
            }
            return;
        }
        boolean aGlisse = Math.hypot(x - departX, y - departY) > 10 * u;
        Joueur visee = passeVisee();
        Joueur porteur = match.getPorteur();
        if (aGlisse && visee != null) {
            ordresQuiJouent().passer(visee);        /* porteur glisse sur un coequipier = passe */
        } else if (aGlisse && peutBouger(glisse)) {
            ordresQuiJouent().ajouterFleche(glisse, limiter(glisse, versTerrain(x, y)));
        } else if (aGlisse) {
            ordresQuiJouent().retirerFleche(glisse);   /* joueur battu : il ne bouge pas */
        } else if (aLeBallon() && glisse != porteur) {
            ordresQuiJouent().passer(glisse);       /* touche un coequipier = passe */
        } else {
            ordresQuiJouent().retirerFleche(glisse);
        }
        glisse = null;
    }

    private boolean surUnBouton(float x, float y) {
        return btnTirer.zone.contains(x, y) || btnEffacer.zone.contains(x, y)
                || btnFin.zone.contains(x, y) || btnRetour.zone.contains(x, y);
    }

    private Joueur joueurSousLeDoigt(Position point) {
        List<Joueur> proches = match.getTerrain().joueursProches(point, 4, equipeQuiJoue());
        if (proches.isEmpty()) {
            return null;
        }
        return proches.get(0);
    }
}
