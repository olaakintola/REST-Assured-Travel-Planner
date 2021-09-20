package service.core;


public class ActivityRequest {

    public ActivityRequest(){}

    public ActivityRequest(String city, String country) {
        this.city = city;
        this.country = country;
    }

    private String city;
    private String country;

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
