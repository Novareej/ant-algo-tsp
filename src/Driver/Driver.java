package Driver;

import model.fourmi;
import model.route;
import model.ville;
import optimisation.fourmisOptimisation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

public class Driver{
    static final int nombre_de_fourmis = 500 ;
    static final double PROCCESSING_CYCLE_PROBABILITY = 0.8;
    public static ArrayList<ville> route_initial = new ArrayList<ville>(Arrays.asList (
            new ville( "A" , 42.3681 , -71.8589 ) ,
            new ville( "B" , 29.7684 , -95.3698 ) ,
            new ville( "C" , 30.2672 , -97.7431 ) ,
            new ville( "D" , 37.7749 , -122.4194 ) ,
            new ville( "E" , 39.7392 , -104.9903 ) ,
            new ville( "F" , 34.0522 , -118.2437 ) ,
            new ville( "G" , 41.8781 , -87.6298 ) ,
            new ville( "H" , 40.7128 , -74.0059 )
            // new City ( " Sydney " , -33.8675,151.2070 )
            // new city ( " Tokyo " , 35.6895 , 139.6917 ) ,
            // new City ( " Cape Town " , -33.9249 , 18.4241 )
    ));

    static ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    static ExecutorCompletionService<fourmi> executorCompletionService = new ExecutorCompletionService<fourmi>(executorService);

    private route shortestRoute = null;
    private int fourmisActifs = 0 ;
    public static void main(String[] args)throws IOException {
        System.out.println(">" +nombre_de_fourmis+" Artificial ants ...");
        Driver driver = new Driver();
        driver.printHeading();
        fourmisOptimisation fo = new fourmisOptimisation();
        IntStream.range(1, nombre_de_fourmis).forEach(x -> {
                    executorCompletionService.submit(new fourmi(fo, x));
                    driver.fourmisActifs++;
                    if(Math.random() > PROCCESSING_CYCLE_PROBABILITY) driver.processFourmis();
                }
        );
        driver.processFourmis();
        executorService.shutdownNow();
        System.out.println("\nOptimal route : "+Arrays.toString(driver.shortestRoute.getVilles().toArray()));
        System.out.println("w/ Distance : "+ driver.shortestRoute.getDistance());
    }

    private void processFourmis(){
        while (fourmisActifs > 0) {
            try {
                //take function calls the "call" function in class fourmi
                fourmi fm = executorCompletionService.take().get();
                route currentRoute = fm.getRoute();
                if (shortestRoute == null || currentRoute.getDistance() < shortestRoute.getDistance()) {
                    shortestRoute = currentRoute;
                    StringBuffer distance = new StringBuffer("       " + String.format("%.2f", currentRoute.getDistance()));
                    IntStream.range(0, 21 - distance.length()).forEach(k -> distance.append(" "));
                    System.out.println(Arrays.toString(shortestRoute.getVilles().toArray()) + " | " + distance + " | " + fm.getNombreFourmis());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            fourmisActifs--;
        }
    }

    private void printHeading ( ) {
        String headingColumn1 = "Route";
        String remainingHeadingColumns = "Distance ( in miles ) | ant # " ;
        int cityNamesLength = 0 ;
        for ( int x = 0 ; x < route_initial.size(); x++) cityNamesLength += route_initial.get(x).getName().length();
        int arrayLength = cityNamesLength + route_initial.size()* 2 ;
        int partialLength = ( arrayLength - headingColumn1.length())/ 2 ;
        for ( int x = 0 ; x < partialLength ; x++ ) System.out.print ( " " ) ;
        System.out.print ( headingColumn1 ) ;
        for ( int x = 0 ; x < partialLength ; x++ ) System.out.print ( " " ) ;
        if ( ( arrayLength % 2 ) == 0 ) System.out.print ( " " ) ;
        System.out.println ( " | " + remainingHeadingColumns ) ;
        cityNamesLength += remainingHeadingColumns.length() + 3 ;
        for ( int x = 0 ; x < cityNamesLength + route_initial.size() * 2 ; x ++ ) System.out.print ( " - " ) ;
        System.out.println( "" ) ;
    }




}
