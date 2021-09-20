package service.core;

import service.core.FlightRequest;
import service.core.HotelRequest;
import service.core.ActivityRequest;
import service.core.AttractionRequest;

public class ClientRequest {

      public ClientRequest() {
            flightRequest = new FlightRequest();
            hotelRequest = new HotelRequest();
            activityRequest = new ActivityRequest();
      }

      private FlightRequest flightRequest;
      private HotelRequest hotelRequest;
      private ActivityRequest activityRequest;
      private AttractionRequest attractionRequest;

      public FlightRequest getFlightRequest(){
            return flightRequest;
      }

      public void setFlightRequest(FlightRequest flightRequest){
            this.flightRequest = flightRequest;
      }

      public HotelRequest getHotelRequest(){
            return hotelRequest;
      }

      public void setHotelRequest(HotelRequest hotelRequest){
            this.hotelRequest = hotelRequest;
      }

      public ActivityRequest getActivityRequest() {
            return activityRequest;
      }

      public void setActivityRequest(ActivityRequest activityRequest) {
            this.activityRequest = activityRequest;
      }

      public AttractionRequest getAttractionRequest() {
            return attractionRequest;
      }

      public void setAttractionRequest(AttractionRequest attractionRequest) {
            this.attractionRequest = attractionRequest;
      }
      
}
