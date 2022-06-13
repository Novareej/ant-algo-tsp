package model;

import Driver.Driver;
import com.sun.org.apache.xpath.internal.operations.Bool;
import optimisation.fourmisOptimisation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

public class fourmi implements Callable<fourmi> {
    //parametre pour ajuster le pheremone
    public static final double Q= 100;
    //coefficient de la vitesse d'evaporation
    public static final double RHO=0.5;
    //importance du chemin de pheromone
    public static final double ALPHA = 1;
    //importance de la distance entre source et destination
    public static final double BETA = 5;
    private fourmisOptimisation fo ;
    private int nombreFourmis;
    private route route = null;
    static int invalidCityIndex = -1;
    static int nombre_de_villes = Driver.route_initial.size();
    public route getRoute() { return route ; }

    public fourmi(fourmisOptimisation fo, int nombreFourmis) {
        this.fo = fo;
        this.nombreFourmis = nombreFourmis;
    }

    //fonction pour instancier les fourmis dans leur ville de départ
    //la ville de départ est générer par Random
    @Override
    public fourmi call () throws Exception{
        //randomly pick up the index of the origin city
        int originalCityIndex = ThreadLocalRandom.current().nextInt(nombre_de_villes);
        ArrayList<ville> routeVilles = new ArrayList<ville>(nombre_de_villes);
        HashMap<String, Boolean> villesVisites = new HashMap<String, Boolean>(nombre_de_villes);
        //par défault tous les villes ne sont pas visités (false)
        IntStream.range(0, nombre_de_villes).forEach(x->villesVisites.put(Driver.route_initial.get(x).getName(), false));
        int nombre_villes_visites = 0;
        //mettre la ville origine comme visité (true)
        villesVisites.put(Driver.route_initial.get(originalCityIndex).getName(), true);
        double routeDistance = 0.0;
        int x = originalCityIndex;
        int y=invalidCityIndex;
        if(nombre_villes_visites != nombre_de_villes)
            y = getY(x, villesVisites);
        while(y!=invalidCityIndex){
            routeVilles.add(nombre_villes_visites++, Driver.route_initial.get(x));
            routeDistance += fo.getDistancesMatrix()[x][y];
            adjuster_niveau_pheromone(x, y, routeDistance);
            villesVisites.put(Driver.route_initial.get(y).getName(), true);
            x = y;
            if(nombre_villes_visites != nombre_de_villes)
                y = getY(x, villesVisites);
            else
                y = invalidCityIndex;
        }
        routeDistance+= fo.getDistancesMatrix()[x][originalCityIndex];
        routeVilles.add(nombre_villes_visites, Driver.route_initial.get(x));
        route = new route(routeVilles, routeDistance);
        return this ;
    }

    private void adjuster_niveau_pheromone(int x, int y, double routeDistance) {
        boolean flag = false;
        while (!flag){
            double currentPheremoneLevel = fo.getPheromoneLevelsMatrix()[x][y].doubleValue();
            double updatedPheremoneLevel = RHO * currentPheremoneLevel + Q/routeDistance;
            if(updatedPheremoneLevel<0.00) flag = fo.getPheromoneLevelsMatrix()[x][y].compareAndSet(0);
            else flag = fo.getPheromoneLevelsMatrix()[x][y].compareAndSet(updatedPheremoneLevel);
        }
    }

    //get ville de destination randomly
    private int getY(int x, HashMap<String, Boolean> visitedcities){
        int returnY = invalidCityIndex;
        double random = ThreadLocalRandom.current().nextDouble();
        ArrayList<Double> transitionProbabilites = getTransitionProbabilites(x, visitedcities);
        for(int y=0; y<nombre_de_villes; y++){
            if(transitionProbabilites.get(y) > random){
                returnY = y;
                break;
            }else random -= transitionProbabilites.get(y);
        }
        return returnY;
    }


    private ArrayList<Double> getTransitionProbabilites(int x, HashMap<String, Boolean> visitedcities) {
        ArrayList<Double> transitionProbabilites = new ArrayList<Double>(nombre_de_villes);
        //initialiser tous les probabilites de transition à 0
        IntStream.range(0, nombre_de_villes).forEach(i -> transitionProbabilites.add(0.0));
        double denominator = getTPDenominator(transitionProbabilites, x, visitedcities);
        //calcul de la probabilité de transition
        IntStream.range(0, nombre_de_villes).forEach(y -> transitionProbabilites.set(y, transitionProbabilites.get(y)/denominator));

        return transitionProbabilites;
    }

    private double getTPDenominator(ArrayList<Double> transitionProbabilites, int x, HashMap<String, Boolean> visitedCities) {
        double denominator = 0.0;
        for (int y = 0; y < nombre_de_villes; y++) {
            //si la ville testé n'est pas une ville initial
            if (!visitedCities.get(Driver.route_initial.get(y).getName())) {
                //si la ville testé est la ville courante => probabilité = 0
                if(x==y) transitionProbabilites.set(y, 0.0);
                else transitionProbabilites.set(y, getTPNumerator(x, y));
                //
                denominator += transitionProbabilites.get(y);
            }
        }
        return denominator;
    }

    private double getTPNumerator(int x, int y) {
        double numerator = 0.0;
        double pheremoneLevel = fo.getPheromoneLevelsMatrix()[y][x].doubleValue();
        if(pheremoneLevel != 0.0) numerator = Math.pow(pheremoneLevel, ALPHA) * Math.pow(1/fo.getDistancesMatrix()[x][y], BETA);
        return numerator;
    }


    public int getNombreFourmis() {
        return nombreFourmis;
    }



    }
