package com.example.brf.moteur;

import org.junit.Test;
import static org.junit.Assert.*;

public class JoueurTest {
    @Test
    public void recupererPE(){
        Joueur mbappe = new Joueur("Mbappe",Poste.ATTAQUANT,TypeJoueur.VITESSE,87,78,84,45,96,0,50);
        mbappe.depenserPE(35);
        mbappe.recupererPE(20);
        assertEquals(15+20,mbappe.getPeActuel());
    }
    @Test
    public void recupererPE_neDepassePasLeMax(){
        Joueur mbappe = new Joueur("Mbappe",Poste.ATTAQUANT,TypeJoueur.VITESSE,87,78,84,45,96,0,50);
        mbappe.depenserPE(10);
        mbappe.recupererPE(20);
        assertEquals(50,mbappe.getPeActuel());
    }
    @Test
    public void depenserPE_reussi(){
        Joueur mbappe = new Joueur("Mbappe",Poste.ATTAQUANT,TypeJoueur.VITESSE,87,78,84,45,96,0,50);
        assertTrue(mbappe.depenserPE(20));
        assertEquals(30,mbappe.getPeActuel());
    }
    @Test
    public void depenserPE_refuse(){
        Joueur mbappe = new Joueur("Mbappe",Poste.ATTAQUANT,TypeJoueur.VITESSE,87,78,84,45,96,0,50);
        mbappe.depenserPE(35);
        assertFalse(mbappe.depenserPE(35));
        assertEquals(15,mbappe.getPeActuel());
    }

    /* techniques */
    @Test
    public void ajouterTechnique_refuseLaTroisieme(){
        Joueur mbappe = new Joueur("Mbappe",Poste.ATTAQUANT,TypeJoueur.VITESSE,87,78,84,45,96,0,50);
        assertTrue(mbappe.ajouterTechnique(new Technique("Tir A", TypeTechnique.TIR, 10, 20)));
        assertTrue(mbappe.ajouterTechnique(new Technique("Dribble B", TypeTechnique.DRIBBLE, 10, 20)));
        assertFalse(mbappe.ajouterTechnique(new Technique("Tir C", TypeTechnique.TIR, 10, 20)));
        assertEquals(2, mbappe.getTechniques().size());
    }
    @Test
    public void getTechniquesDuType_seulementLeBonType(){
        Joueur mbappe = new Joueur("Mbappe",Poste.ATTAQUANT,TypeJoueur.VITESSE,87,78,84,45,96,0,50);
        Technique tir = new Technique("Tir A", TypeTechnique.TIR, 10, 20);
        mbappe.ajouterTechnique(tir);
        mbappe.ajouterTechnique(new Technique("Dribble B", TypeTechnique.DRIBBLE, 10, 20));
        assertEquals(1, mbappe.getTechniquesDuType(TypeTechnique.TIR).size());
        assertEquals(tir, mbappe.getTechniquesDuType(TypeTechnique.TIR).get(0));
        assertEquals(0, mbappe.getTechniquesDuType(TypeTechnique.DEFENSE).size());
    }
    @Test
    public void peutLancer_selonLesPE(){
        Joueur mbappe = new Joueur("Mbappe",Poste.ATTAQUANT,TypeJoueur.VITESSE,87,78,84,45,96,0,50);
        Technique chere = new Technique("Tir A", TypeTechnique.TIR, 10, 30);
        assertTrue(mbappe.peutLancer(chere));
        mbappe.depenserPE(30);   /* reste 20 PE */
        assertFalse(mbappe.peutLancer(chere));
    }
}