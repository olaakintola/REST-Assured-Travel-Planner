package service.core;

import service.core.ClientResponse;

public class ReplaceBooking {
      
      public ReplaceBooking() {}

      private ClientResponse newChoiceOfBooking;
      private Booking previousBooking;

      public ClientResponse getNewChoiceOfBooking(){
            return newChoiceOfBooking;
      }

      public void setNewChoiceOfBooking(ClientResponse newChoiceOfBooking){
            this.newChoiceOfBooking = newChoiceOfBooking;
      }

      public Booking getPreviousBooking(){
            return previousBooking;
      }

      public void setPreviousBooking(Booking previousBooking){
            this.previousBooking = previousBooking;
      }
}
