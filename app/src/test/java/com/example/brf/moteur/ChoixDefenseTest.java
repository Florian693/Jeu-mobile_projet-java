package com.example.brf.moteur;

import org.junit.Test;
import static org.junit.Assert.*;

public class ChoixDefenseTest {

    @Test
    public void bonus() {
        assertEquals(15, ChoixDefense.BLOCAGE.bonus());
        assertEquals(0, ChoixDefense.TACLE.bonus());
    }

    @Test
    public void faitAvancer() {
        assertTrue(ChoixDefense.TACLE.faitAvancer());
        assertFalse(ChoixDefense.BLOCAGE.faitAvancer());
    }
}