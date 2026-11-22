package com.example.brf;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;

// Bouton dessine sur l'ecran du match
public class Bouton {
    public static final int BLEU = 0xFF1E6FD9;
    public static final int ORANGE = 0xFFF08A00;
    public static final int ROUGE = 0xFFD32F2F;
    public static final int GRIS = 0xFF5F6B78;
    private static final int MARINE = 0xFF0B1A33;

    private static final Paint PLEIN = new Paint(Paint.ANTI_ALIAS_FLAG);
    private static final Paint TEXTE = new Paint(Paint.ANTI_ALIAS_FLAG);
    private static final RectF INTERIEUR = new RectF();

    static {
        TEXTE.setTypeface(Typeface.DEFAULT_BOLD);
        TEXTE.setStrokeJoin(Paint.Join.ROUND);
    }

    public final RectF zone;
    public final String texte;
    public String detail;          /* ex : "30 PE", ecrit a droite */
    public int couleur = BLEU;
    public boolean rond;
    public boolean actif = true;
    public int code;

    public Bouton(String texte, float gauche, float haut, float droite, float bas) {
        this.texte = texte;
        this.zone = new RectF(gauche, haut, droite, bas);
    }

    public boolean contient(float x, float y) {
        return actif && zone.contains(x, y);
    }

    public void dessiner(Canvas canvas) {
        float h = zone.height();
        float rayon = rond ? h / 2 : h / 4;
        float e = h / 12;

        /* bord sombre */
        PLEIN.setColor(MARINE);
        canvas.drawRoundRect(zone, rayon, rayon, PLEIN);

        /* couleur + reflet en haut */
        INTERIEUR.set(zone.left + e, zone.top + e, zone.right - e, zone.bottom - e);
        PLEIN.setColor(actif ? couleur : GRIS);
        canvas.drawRoundRect(INTERIEUR, rayon - e, rayon - e, PLEIN);
        INTERIEUR.bottom = INTERIEUR.top + INTERIEUR.height() / 2;
        PLEIN.setColor(0x30FFFFFF);
        canvas.drawRoundRect(INTERIEUR, rayon - e, rayon - e, PLEIN);

        if (texte.isEmpty()) {
            return;
        }
        float taille = h * 0.38f;
        float marge = h * 0.35f;
        int couleurTexte = actif ? 0xFFFFFFFF : 0xFFB8C0C8;

        if (detail == null) {
            TEXTE.setTextAlign(Paint.Align.CENTER);
            TEXTE.setTextSize(ajuster(texte, taille, zone.width() - 2 * marge));
            ecrire(canvas, texte, zone.centerX(), couleurTexte);
            return;
        }
        TEXTE.setTextSize(taille);
        float largeurDetail = TEXTE.measureText(detail);
        TEXTE.setTextAlign(Paint.Align.RIGHT);
        ecrire(canvas, detail, zone.right - marge, actif ? 0xFFFFE066 : couleurTexte);
        TEXTE.setTextAlign(Paint.Align.LEFT);
        TEXTE.setTextSize(ajuster(texte, taille, zone.width() - 3 * marge - largeurDetail));
        ecrire(canvas, texte, zone.left + marge, couleurTexte);
    }

    /* texte entoure de sombre */
    private void ecrire(Canvas canvas, String s, float x, int couleur) {
        float y = zone.centerY() - (TEXTE.ascent() + TEXTE.descent()) / 2;
        TEXTE.setStyle(Paint.Style.STROKE);
        TEXTE.setStrokeWidth(TEXTE.getTextSize() * 0.2f);
        TEXTE.setColor(MARINE);
        canvas.drawText(s, x, y, TEXTE);
        TEXTE.setStyle(Paint.Style.FILL);
        TEXTE.setColor(couleur);
        canvas.drawText(s, x, y, TEXTE);
    }

    private float ajuster(String s, float taille, float place) {
        TEXTE.setTextSize(taille);
        float largeur = TEXTE.measureText(s);
        if (largeur > place) {
            return taille * place / largeur;
        }
        return taille;
    }
}
