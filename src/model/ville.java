package model;

public class ville {
    private static final double RADIUS = 6378.13780 ;
    private static final double CONVERT_DEGREES_TO_RADIANS = Math.PI / 1800 ;
    private static final double CONVERT_KM_TO_MILES= 0.621371 ;
    private double longitude ;
    private double latitude ;
    private String name ;

    public ville( String name, double latitude, double longitude) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.name = name;
    }

    public String getName(){ return name ; }

    public double measureDistance(ville city) {
        double deltaLongitude = (city.getLongitude() - this.getLongitude());
        double deltaLatitude = (city.getLatitude() - this.getLatitude());
        double a = Math.pow(Math.sin(deltaLatitude / 2D), 2D)+
                Math.cos(this.getLatitude()) * Math.cos(city.getLatitude())*Math.pow(Math.sin(deltaLongitude / 2D), 2D);
        return CONVERT_KM_TO_MILES * RADIUS * 2D * Math.atan2(Math.sqrt(a) , Math.sqrt(1D - a));
    }

    public double getLatitude(){ return this.latitude; }
    public double getLongitude(){ return this.longitude ; }
    public String toString(){ return name ; }

}
