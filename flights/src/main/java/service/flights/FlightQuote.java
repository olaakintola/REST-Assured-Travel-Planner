package service.flights;

public class FlightQuote {

      public FlightQuote() {}

      private String carrierId;
      private String originId;
      private String destinationId;
      private String price;
      private String airline;
      private String originAirportName;
      private String destAirportName;

      public String getCarrierId(){
            return carrierId;
      }

      public void setCarrierId(String carrierId){
            this.carrierId = carrierId;
      }

      public String getOriginId(){
            return originId;
      }

      public void setOriginId(String originId){
            this.originId = originId;
      }

      public String getDestinationId(){
            return destinationId;
      }

      public void setDestinationId(String destinationId){
            this.destinationId = destinationId;
      }

      public String getPrice(){
            return price;
      }

      public void setPrice(String price){
            this.price = price;
      }

      public String getAirline(){
            return airline;
      }

      public void setAirline(String airline){
            this.airline = airline;
      }

      public String getOriginAirportName(){
            return originAirportName;
      }

      public void setOriginAirportName(String originAirportName){
            this.originAirportName = originAirportName;
      }

      public String getDestAirportName(){
            return destAirportName;
      }

      public void setDestAirportName(String destAirportName){
            this.destAirportName = destAirportName;
      }

      public boolean equals(FlightQuote flightQuote){
            if (this.price.equals(flightQuote.price) && this.airline.equals(flightQuote.airline) && 
                  this.originAirportName.equals(flightQuote.getOriginAirportName()) && this.destAirportName.equals(
                        flightQuote.getDestAirportName()) ) {
                              return true;
                        }
            return false;
      }
}
