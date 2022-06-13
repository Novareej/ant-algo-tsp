package model;

import java.util.ArrayList;
import java.util.Arrays;

public class route {
    private ArrayList<ville> villes;
    private double distance;
    public route(ArrayList<ville> villes, double distance) {
        this.villes = villes;
        this.distance = distance ;
    }
    public ArrayList<ville> getVilles() { return villes ; }
    public double getDistance(){ return distance ;}
    public String toString(){ return Arrays.toString(villes.toArray())+" | " +distance ; }
}
