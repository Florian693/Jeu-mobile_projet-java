package com.example.brf;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import java.io.IOException;
import java.io.InputStream;

// Dessine le stade et les 22 joueurs. Elle ne calcule aucune regle de jeu.
public class MatchView extends View {

    // Position du terrain DANS l'image bg_match (fraction de sa taille)
    private static final float TERRAIN_GAUCHE = 0.136f;
    private static final float TERRAIN_DROITE = 0.866f;
    private static final float TERRAIN_HAUT = 0.279f;
    private static final float TERRAIN_BAS = 0.882f;

    // Formation 4-4-2 de l'equipe de gauche : {x, y} en fraction du terrain
    private static final float[][] FORMATION = {
            {0.04f, 0.50f},                                                // gardien
            {0.18f, 0.15f}, {0.18f, 0.38f}, {0.18f, 0.62f}, {0.18f, 0.85f}, // defenseurs
            {0.32f, 0.15f}, {0.32f, 0.38f}, {0.32f, 0.62f}, {0.32f, 0.85f}, // milieux
            {0.44f, 0.35f}, {0.44f, 0.65f}                                 // attaquants
    };

    // Repere pour Mbappe : il a un vrai sprite, pas une tete
    private static final String MBAPPE = "mbappe";

    // Les 11 joueurs de chaque equipe, dans l'ordre de FORMATION
    // (gardien, 4 defenseurs, 4 milieux, 2 attaquants).
    // Index 0 = Real Madrid, index 1 = FC Barcelone (comme dans ChoixEquipesActivity).
    private static final String[][] EQUIPES = {
            {
                    "real_madrid/courtois_1.png",
                    "real_madrid/cucurella_17.png", "real_madrid/konate_16.png",
                    "real_madrid/huijsen_4.png", "real_madrid/valverde_8.png",
                    "real_madrid/arda_guler_15.png", "real_madrid/tchouameni_14.png",
                    "real_madrid/bellingham_5.png", "real_madrid/bernardo_silva_20.png",
                    "real_madrid/vinicius_jr_7.png", MBAPPE
            },
            {
                    "fc_barcelone/joan_garcia_1.png",
                    "fc_barcelone/balde_3.png", "fc_barcelone/cubarsi_5.png",
                    "fc_barcelone/kounde_23.png", "fc_barcelone/cancelo_2.png",
                    "fc_barcelone/raphinha_11.png", "fc_barcelone/pedri_8.png",
                    "fc_barcelone/frenkie_de_jong_21.png", "fc_barcelone/lamine_yamal_10.png",
                    "fc_barcelone/gabriel_jesus_9.png", "fc_barcelone/fermin_lopez_7.png"
            }
    };

    // Couleur du fond de chaque pastille : Real blanc, Barca grenat
    private static final int[] COULEURS = {0xFFFFFFFF, 0xFFA50044};
    private static final int COULEUR_GARDIEN = 0xFFFFD600;

    private final Bitmap fond;
    private final Paint paintFond = new Paint();
    private final Paint paintSprite = new Paint();
    private final Paint paintJoueur = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint paintContour = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF zone = new RectF();
    private final Path masque = new Path();

    private int equipeGauche = 0;
    private int equipeDroite = 1;
    private Bitmap[] imagesGauche = new Bitmap[FORMATION.length];
    private Bitmap[] imagesDroite = new Bitmap[FORMATION.length];

    public MatchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        fond = BitmapFactory.decodeResource(getResources(), R.drawable.fond_match);
        paintFond.setFilterBitmap(false);
        paintSprite.setFilterBitmap(true);  // les images sont reduites : on les lisse un peu
        paintContour.setStyle(Paint.Style.STROKE);
        paintContour.setStrokeWidth(4f);
    }

    // Appelee par MatchActivity : quelle equipe a gauche, laquelle a droite
    public void setEquipes(int gauche, int droite) {
        this.equipeGauche = gauche;
        this.equipeDroite = droite;
        this.imagesGauche = chargerEquipe(gauche, true);
        this.imagesDroite = chargerEquipe(droite, false);
        invalidate();
    }

    // Charge les 11 images d'une equipe. Mbappe regarde vers le but adverse :
    // a gauche on attaque vers la droite (east), a droite vers la gauche (west).
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

    // Lit une image dans le dossier assets (null si introuvable)
    private Bitmap lireAsset(String chemin) {
        try (InputStream in = getContext().getAssets().open(chemin)) {
            return BitmapFactory.decodeStream(in);
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int w = getWidth();
        int h = getHeight();
        float bw = fond.getWidth();
        float bh = fond.getHeight();

        // Le fond remplit l'ecran (comme centerCrop)
        float echelle = Math.max(w / bw, h / bh);
        float largeurFond = bw * echelle;
        float hauteurFond = bh * echelle;
        float decalX = (w - largeurFond) / 2f;
        float decalY = (h - hauteurFond) / 2f;
        zone.set(decalX, decalY, decalX + largeurFond, decalY + hauteurFond);
        canvas.drawBitmap(fond, null, zone, paintFond);

        // Rectangle du terrain, en coordonnees d'ecran
        float tx = decalX + TERRAIN_GAUCHE * largeurFond;
        float ty = decalY + TERRAIN_HAUT * hauteurFond;
        float tl = (TERRAIN_DROITE - TERRAIN_GAUCHE) * largeurFond;
        float th = (TERRAIN_BAS - TERRAIN_HAUT) * hauteurFond;
        float diametre = th * 0.12f;

        for (int i = 0; i < FORMATION.length; i++) {
            float fx = FORMATION[i][0];
            float fy = FORMATION[i][1];

            // Equipe de gauche : positions normales
            dessinerJoueur(canvas, tx + fx * tl, ty + fy * th, diametre,
                    imagesGauche[i], COULEURS[equipeGauche], i == 0,
                    EQUIPES[equipeGauche][i].equals(MBAPPE));
            // Equipe de droite : positions en miroir
            dessinerJoueur(canvas, tx + (1f - fx) * tl, ty + fy * th, diametre,
                    imagesDroite[i], COULEURS[equipeDroite], i == 0,
                    EQUIPES[equipeDroite][i].equals(MBAPPE));
        }
    }

    // Mbappe : son sprite en grand. Les autres : une pastille ronde avec la tete.
    private void dessinerJoueur(Canvas canvas, float cx, float cy, float diametre,
                                Bitmap image, int couleur, boolean gardien, boolean mbappe) {
        if (mbappe && image != null) {
            float t = diametre * 1.7f;
            zone.set(cx - t / 2f, cy - t / 2f, cx + t / 2f, cy + t / 2f);
            canvas.drawBitmap(image, null, zone, paintSprite);
            return;
        }

        float r = diametre / 2f;
        paintJoueur.setColor(couleur);
        canvas.drawCircle(cx, cy, r, paintJoueur);

        if (image != null) {
            masque.reset();
            masque.addCircle(cx, cy, r, Path.Direction.CW);
            canvas.save();
            canvas.clipPath(masque);
            zone.set(cx - r, cy - r, cx + r, cy + r);
            canvas.drawBitmap(image, null, zone, paintSprite);
            canvas.restore();
        }

        paintContour.setColor(gardien ? COULEUR_GARDIEN : 0xFF000000);
        canvas.drawCircle(cx, cy, r, paintContour);
    }
}