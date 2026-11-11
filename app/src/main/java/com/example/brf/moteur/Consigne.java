package com.example.brf.moteur;

// Ce que fait un joueur sans fleche pendant un tour.
// La consigne reste active d'un tour a l'autre.
public enum Consigne {
    SUIVRE_LE_JEU,  // par defaut : vise sa position de base, decalee vers le ballon
    PRESSER,        // vise le porteur adverse
    RECULER,        // vise sa position de base, 15 unites vers son propre but
    MONTER,         // vise sa position de base, 20 unites vers le but adverse
    RESTER          // ne bouge pas
}
