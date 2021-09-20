package service.core;

public class Hotel{

      public Hotel() {}

      private String price;  //offers
      private String roomType; // offers (category)
      private String bedType;  // offers
      private String description; // offers
      private String address;  // hotel
      private String [] amenities;  // hotel
      private String rating;
      private String phoneNumber;
      private String name;
      private int referenceNumber;

      public String getPrice(){
            return price;
      }

      public void setPrice(String price){
            this.price = price;
      }

      public String getRoomType(){
            return roomType;
      }

      public void setRoomType(String roomType){
            this.roomType = roomType;
      }

      public String getBedType(){
            return bedType;
      }

      public void setBedType(String bedType){
            this.bedType = bedType;
      }

      public String getDescription(){
            return description;
      }

      public void setDescription(String description){
            this.description = description;
      }

      public String getAddress(){
            return address;
      }

      public void setAddress(String address){
            this.address = address;
      }

      public String [] getAmenities(){
            return amenities;
      }

      public void setAmenities(String [] amenities){
            this.amenities = amenities;
      }

      public String getRating(){
            return rating;
      }

      public void setRating(String rating){
            this.rating = rating;
      }

      public String getPhoneNumber(){
            return phoneNumber;
      }

      public void setPhoneNumber(String phoneNumber){
            this.phoneNumber = phoneNumber;
      }

      public String getName(){
            return name;
      }

      public void setName(String name){
            this.name = name;
      }

      public int getReferenceNumber(){
            return referenceNumber;
      }

      public void setReferenceNumber(int referenceNumber){
            this.referenceNumber = referenceNumber;
      }

      public String amenitiesToString(){
            
            String amenitiesString = "";
            for (String amenity : amenities){
                  amenitiesString += amenity + " ";
            }
            return amenitiesString;
      }

      public String toString(){
            String string = "";
            string += "Name: " + name +"\n";
            string += "Address: " + address +"\n";
            string += "PhoneNumber: " + phoneNumber +"\n";
            string += "Price: " + price;
            return string;
      }

}
