package service.core;

public class ActivityItem {

    private String name;
    private String description;
    private String rating;
    private String bookingLink;
    private String[] pictures;
    private String priceOfActivity;
    private Price price;
    private int referenceNumber;

    public ActivityItem() {}

    public ActivityItem(String name, String description, String rating, String bookingLink, String[] pictures, String amount, String currencyCode) {
        this.name = name;
        this.description = description;
        this.rating = rating;
        this.bookingLink = bookingLink;
        this.pictures = pictures;
        this.price = new Price(amount, currencyCode);
        this.priceOfActivity = this.price.toString();
    }

    public class Price {
        private String amount;
        private String currencyCode;

        public Price() { }
        public Price(String amount, String currencyCode) {
            this.amount = amount;
            this.currencyCode = currencyCode;
        }

        public String getAmount() {
            return amount;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }

        public String getCurrencyCode() {
            return currencyCode;
        }

        public void setCurrencyCode(String currencyCode) {
            this.currencyCode = currencyCode;
        }

        @Override
        public String toString() {
            return currencyCode + " " + amount;
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getBookingLink() {
        return bookingLink;
    }

    public void setBookingLink(String bookingLink) {
        this.bookingLink = bookingLink;
    }

    public String[] getPictures() {
        return pictures;
    }

    public void setPictures(String[] pictures) {
        this.pictures = pictures;
    }

    public Price getPrice() {
        return price;
    }

    public void setPrice(Price price) {
        this.price = price;
    }

    public int getReferenceNumber(){
        return referenceNumber;
    }

    public void setReferenceNumber(int referenceNumber){
        this.referenceNumber = referenceNumber;
    }

    public String getPriceOfActivity(){
        return priceOfActivity;
    }

    public void setPriceOfActivity(String priceOfActivity){
        this.priceOfActivity = priceOfActivity;
    }


    public String toString() {
        return "Name: " + name+"\n"+"Link: "+bookingLink+"\n";
    }
}
