package service.core;

public class MongoBooking {
    // private String id;
    private String referenceId;
    private String flightDetails;
    private String hotelDetails;
    private String activitiesDetails;
    private String attractionsDetails;
    
    public MongoBooking(){}

    public MongoBooking(String referenceId, String flightDetails, String hotelDetails, String activitiesDetails, String attractionsDetails){
        this.referenceId = referenceId;
        this.flightDetails = flightDetails;
        this.hotelDetails = hotelDetails;
        this.activitiesDetails = activitiesDetails;
        this.attractionsDetails = attractionsDetails;
    }

    public void setReferenceId(String referenceId){
        this.referenceId = referenceId;
    }

    public String getReferenceId(){
        return referenceId;
    }

    public void setFlightDetails(String flightDetails){
        this.flightDetails = flightDetails;
    }

    public String getFlightDetails(){
        return flightDetails;
    }

    public void setHotelDetails(String hotelDetails){
        this.hotelDetails = hotelDetails;
    }

    public String getHotelDetails(){
        return hotelDetails;
    }
    public void setActivitiesDetails(String activitiesDetails){
        this.activitiesDetails = activitiesDetails;
    }

    public String getActivitiesDetails(){
        return activitiesDetails;
    }

    public void setAttractionsDetails(String attractionsDetails){
        this.attractionsDetails = attractionsDetails;
    }

    public String getAttractionsDetails(){
        return attractionsDetails;
    }
}
