package com.example.brf.moteur;

// Ce qui s'est passe pendant un tour (ou pendant un duel).
// MatchView lit ce type pour savoir quelle animation jouer.
public enum TypeResultat {
    TOUR_TERMINE,        // les 10 etapes sont passees sans arret
    PASSE_REUSSIE,       // le receveur a recu le ballon
    INTERCEPTION,        // un adversaire a coupe la passe
    DUEL_A_JOUER,        // contact avec le porteur : chaque camp doit choisir
    DUEL_GAGNE_ATTAQUE,  // le porteur a gagne le duel
    DUEL_GAGNE_DEFENSE,  // le defenseur a pris le ballon
    BUT,                 // le tir est rentre
    TIR_ARRETE,          // le gardien a arrete le tir
    MI_TEMPS,            // fin de la 1re mi-temps
    FIN_DU_MATCH         // fin de la 2e mi-temps
}