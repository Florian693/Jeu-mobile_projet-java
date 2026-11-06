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
}
