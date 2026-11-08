package com.example.brf.moteur;

import org.junit.Test;
import static org.junit.Assert.*;

public class PositionTest {
    @Test
    public void distanceTest() {
        Position m = new Position(2, 2);
        Position e = new Position(4, 2);
        double resultat = m.distance(e);
        assertEquals(2, resultat, 0.001);
    }
    @Test
    public void pointSurTrajetTest(){
        Position m = new Position(0, 0);
        Position e = new Position(10, 20);
        Position positionTemporaireTest = Position.pointSurTrajet(m,e,0.5);
        assertEquals(5, positionTemporaireTest.getX(), 0.001);
        assertEquals(10, positionTemporaireTest.getY(), 0.001);
    }
    @Test // cas arret sur le chemin
    public void versCibleArret(){
        Position m = new Position(0, 0);
        Position cible = new Position(10, 0);
        Position arrivee = m.versCible(cible,4);
        assertEquals(4, arrivee.getX(), 0.001);
        assertEquals(0, arrivee.getY(), 0.001);
    }
    @Test // cas ne pas depassser la cible
    public void versCibleMax(){
        Position m = new Position(0, 0);
        Position cible = new Position(10, 0);
        Position arrivee = m.versCible(cible,20);
        assertEquals(10, arrivee.getX(), 0.001);
        assertEquals(0, arrivee.getY(), 0.001);
    }
    @Test // cas deja sur place
    public void versCiblePasBouger(){
        Position m = new Position(5, 5);
        Position cible = new Position(5, 5);
        Position arrivee = m.versCible(cible,4);
        assertEquals(5, arrivee.getX(), 0.001);
        assertEquals(5, arrivee.getY(), 0.001);
    }
}