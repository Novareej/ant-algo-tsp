package optimisation;

import Driver.Driver;
import model.ville;
import utils.AtomicDouble;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.stream.IntStream;

public class fourmisOptimisation {
    private AtomicDouble[][] pheromoneLevelsMatrix = null;
    private double[][] distancesMatrix = null;
    private ArrayList<ville> villes = Driver.route_initial;
    private int villesSize = Driver.route_initial.size();

    public fourmisOptimisation() throws IOException {
        initialiser_Distances();
        initialiser_niveaux_Phermone();
    }

    //getters
    public AtomicDouble[][] getPheromoneLevelsMatrix() {
        return pheromoneLevelsMatrix;
    }

    public double[][] getDistancesMatrix() {
        return distancesMatrix;
    }

    private void initialiser_Distances() throws IOException {
        distancesMatrix = new double[villesSize][villesSize];
        IntStream.range(0, villesSize).forEach(x -> {
            ville cityY = villes.get(x);
            IntStream.range(0, villesSize).forEach(y -> distancesMatrix[x][y] = cityY.measureDistance(villes.get(y)));
        });
    }

    private void initialiser_niveaux_Phermone() {
        pheromoneLevelsMatrix = new AtomicDouble[villesSize][villesSize];
        Random random = new Random();
        IntStream.range(0, villesSize).forEach(x -> {
            IntStream.range(0, villesSize).forEach(y -> pheromoneLevelsMatrix[x][y] = new AtomicDouble(random.nextDouble()));
        });
    }


}
