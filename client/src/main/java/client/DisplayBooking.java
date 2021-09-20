package client;

import service.core.MongoBooking;

public class DisplayBooking {
    
    private String refId;
    private String[] hotel;
    private String[] flight;
    private String[] activities;
    private String[] attractions;

    public DisplayBooking(MongoBooking mb){
        refId = mb.getReferenceId();
        hotel = mb.getHotelDetails().split("\\r?\\n");
        flight = mb.getFlightDetails().split("\\r?\\n");
        activities = mb.getActivitiesDetails().split("\\r?\\n");
        attractions = mb.getAttractionsDetails().split("\\r?\\n");
    }

    public String[] getHotel(){
        return this.hotel;
    }
    public String getRefId(){
        return this.refId;
    }
    public String[] getFlight(){
        return this.flight;
    }
    public String[] getActivities(){
        return this.activities;
    }
    public String[] getAttractions(){
        return this.attractions;
    }
}
