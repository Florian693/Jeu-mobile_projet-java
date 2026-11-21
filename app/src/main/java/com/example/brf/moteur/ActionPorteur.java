package com.example.brf.moteur;

// Ce que fait le porteur du ballon pendant un tour.
/* sans action : il garde le ballon et suit sa fleche */
public enum ActionPorteur {
    // AVANCER,  // avance avec le ballon vers une cible
    PASSER,   // passe au sol vers un coequipier
    TIRER     // tire au but (seulement dans la zone de tir)
}