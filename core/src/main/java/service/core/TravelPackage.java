package service.core;

public class TravelPackage {
      
      public TravelPackage() {}

      private Flight [] flights;
      private Hotel [] hotels;
      private ActivityItem[] activities;
      private Attraction[] attractions;
      private int travelPackageReferenceNumber;

      public Flight [] getFlights(){
      return flights;
      }

      public void setFlights(Flight [] flights){
            this.flights = flights;
      }

      public Hotel [] getHotels(){
            return hotels;
      }

      public void setHotels(Hotel [] hotels){
            this.hotels = hotels;
      }

      public ActivityItem[] getActivities(){
            return activities;
      }

      public void setActivities(ActivityItem[] activities){
            this.activities = activities;
      }

      public Attraction[] getAttractions(){
            return attractions;
      }

      public void setAttractions(Attraction[] attractions){
            this.attractions = attractions;
      }

      public int getTravelPackageReferenceNumber(){
            return travelPackageReferenceNumber;
      }

      public void setTravelPackageReferenceNumber(int travelPackageReferenceNumber){
            this.travelPackageReferenceNumber = travelPackageReferenceNumber;
      }
}
