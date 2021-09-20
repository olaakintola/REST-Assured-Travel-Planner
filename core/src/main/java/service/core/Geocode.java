package service.core;

public class Geocode {

    private double latitude;
    private double longitude;

    public Geocode() {}

    public Geocode(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String toString() {
        return "Geocode("+ latitude + ", " + longitude +")";
    }
}
