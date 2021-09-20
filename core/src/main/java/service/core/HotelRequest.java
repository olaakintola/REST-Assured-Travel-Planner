package service.core;

public class HotelRequest {
      
      public HotelRequest() {}

      private String cityCode;
      private int numberOfGuests;
      private int minNumberOfStarsRequiredForHotel; 

      public String getCityCode(){
            return cityCode;
      }

      public void setCityCode(String cityCode){
            this.cityCode = cityCode;
      }

      public int getNumberOfGuests(){
            return numberOfGuests;
      }

      public void setNumberOfGuests(int numberOfGuests){
            this.numberOfGuests = numberOfGuests;
      }

      public int getMinNumberOfStarsRequiredForHotel(){
            return minNumberOfStarsRequiredForHotel;
      }
      
      public void setMinNumberOfStarsRequiredForHotel(int minNumberOfStarsRequiredForHotel){
            this.minNumberOfStarsRequiredForHotel = minNumberOfStarsRequiredForHotel;
      }

      /**
	 * TODO (Barry): Must update equals method with ref num
	 */

      public Boolean equals(HotelRequest hotelRequest){
            if (this.getNumberOfGuests() == hotelRequest.getNumberOfGuests() &&
                  this.getCityCode().equals(hotelRequest.getCityCode()) &&
                  this.getMinNumberOfStarsRequiredForHotel() == hotelRequest.getMinNumberOfStarsRequiredForHotel()) {
                         return true;
                   }
            return false;
      }
}
