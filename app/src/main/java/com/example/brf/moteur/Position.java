package com.example.brf.moteur;

public class Position {
    private final double x;
    private final double y;

    public Position(double x, double y){
        this.x = x;
        this.y = y;
    }
    public double getY() { return y;}
    public double getX() {
        return x;
    }

    public double distance(Position autre){
        double dx = this.x - autre.x;
        double dy = this.y - autre.y;
        return Math.hypot(dx, dy);
    }

    public static Position pointSurTrajet(Position a,Position b, double fraction){
        double nouveauX = a.x + (b.x-a.x) * fraction;
        double nouveauY = a.y + (b.y-a.y) * fraction;
        return new Position(nouveauX, nouveauY);
    }

    public Position versCible(Position cible, double pasMax){
        double distanceCible = distance(cible);
        if (distanceCible <= pasMax) {
            return cible;
        }
        return pointSurTrajet(this,cible,pasMax/distanceCible);
    }
}
