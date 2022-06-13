package Driver;

import model.fourmi;
import model.route;
import model.ville;
import optimisation.fourmisOptimisation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

public class Driver{
    static final int nombre_de_fourmis = 30 ;
    static final double PROCCESSING_CYCLE_PROBABILITY = 0.8;
    public static ArrayList<ville> route_initial = new ArrayList<ville>(Arrays.asList (
            new ville( "Oujda" , 34.73939479311721, -1.9542187952271033) ,
            new ville( "Nador" , 35.15076917871777, -3.070151780937334 ) ,
            new ville( "Taza" , 34.2585310420493, -3.950295409536495),
            new ville( "Tanger" , 35.76341591988914, -5.8491724973591674) ,
            new ville( "Casablanca" , 33.595809998697035, -7.63943968385629 ) ,
            new ville( "Marrakech" , 31.60416232077552 , -8.024541818015313 ) ,
            new ville( "Essaouira" , 31.514744081341927, -9.76168521197997 ),
            new ville( "Agadir" , 30.426695780322653, -9.57567339695713 ),
            new ville("Fes", 34.01475961519363, -5.0347126515202305),
            new ville("Taroudant", 30.50110880973873, -8.852328256212717),
            new ville("Beni Mellal", 32.3601397812047, -6.370719124342611),
            new ville("El Jadida", 33.221963006447105, -8.46070099156245),
            new ville("Rabat", 33.917934664827584, -6.877753284808482),
            new ville("Mohammedia", 33.64841192802041, -7.377317169019304),
            new ville("Settat", 32.96355577532341, -7.616143740355304),
            new ville("Kouribga", 32.87677203731297, -6.8677077086109035),
            new ville("Larache", 35.152151019134635, -6.138207719215849),
            new ville("Asila", 35.46103774546476, -6.005592620091785),
            new ville("Tetouan", 35.55886798535154, -5.368763888026954),
            new ville("Chefchaouen", 35.15063888684921, -5.27041751137265),
            new ville("Al Hoceima", 35.18745678224889, -4.091766652365846),
            new ville("Tata", 29.7916783480341, -7.907652948811761),
            new ville("Tiznit", 29.740774123578635, -9.66689729764945),
            new ville("Guelmim", 29.028416907776844, -10.050585851039855),
            new ville("Tarfaya", 27.927123413592554, -12.882276521585938),
            new ville("Laayoune", 27.148383810365164, -13.164708978118888),
            new ville("La Güera", 20.861040194340827, -17.07773386436336),
            new ville("Sidi Bennour", 32.62972996580253, -8.419669639688474),
            new ville("Safi", 32.269169262324105, -9.224496969518908),
            new ville("El Kelaa des Sraghna", 32.050561110845734, -7.359128387772718)

            // new City ( " Sydney " , -33.8675,151.2070 )
            // new city ( " Tokyo " , 35.6895 , 139.6917 ) ,
            // new City ( " Cape Town " , -33.9249 , 18.4241 )
    ));

    static ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    static ExecutorCompletionService<fourmi> executorCompletionService = new ExecutorCompletionService<fourmi>(executorService);

    private route shortestRoute = null;
    private int fourmisActifs = 0;
    public static void main(String[] args)throws IOException {
        System.out.println(">" +nombre_de_fourmis+" Artificial ants ...");
        Driver driver = new Driver();
        driver.printHeading();
        fourmisOptimisation fo = new fourmisOptimisation();
        //instanciation des fourmis
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
        String remainingHeadingColumns = "Distance | fourmi # " ;
        int cityNamesLength = 0 ;
        for(int x = 0 ; x < route_initial.size(); x++) cityNamesLength += route_initial.get(x).getName().length();
        int arrayLength = cityNamesLength + route_initial.size()* 2 ;
        int partialLength = ( arrayLength - headingColumn1.length())/ 2 ;
        for( int x = 0 ; x < partialLength ; x++ ) System.out.print ( " " ) ;
        System.out.print ( headingColumn1 ) ;
        for( int x = 0 ; x < partialLength ; x++ ) System.out.print ( " " ) ;
        if( ( arrayLength % 2 ) == 0 ) System.out.print ( " " ) ;
        System.out.println ( " | " + remainingHeadingColumns ) ;
        cityNamesLength += remainingHeadingColumns.length() + 3 ;
        for( int x = 0 ; x < cityNamesLength + route_initial.size() * 2 ; x ++ ) System.out.print ( " - " ) ;
        System.out.println( "" ) ;
    }




}
