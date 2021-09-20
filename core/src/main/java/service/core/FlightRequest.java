package service.core;

public class FlightRequest {

	
	public FlightRequest(String name, String cityOfOrigin, String countryOfOrigin, String cityOfDestination,
	String countryOfDestination, boolean oneWayTrip, String returnDate, String outboundDate, String currency) {
		this.name = name;
		this.cityOfOrigin = cityOfOrigin; 
		this.countryOfOrigin = countryOfOrigin;
		this.cityOfDestinaton = cityOfDestination;
		this.countryOfDestination = countryOfDestination;
		this.oneWayTrip = oneWayTrip;
		this.outboundDate = outboundDate;
		this.returnDate = returnDate;
		this.currency = currency;
	}
	
	public FlightRequest() {}

	private String name;
	private String cityOfOrigin;
	private String countryOfOrigin;
	private String cityOfDestinaton;
	private String countryOfDestination;
	private boolean oneWayTrip;
	private String returnDate;  //format: yyyy-mm-dd
	private String outboundDate; //format: yyyy-mm-dd or null
	private String currency;
	private int referenceNumber;
	private String countryOfOriginCode;
	private String countryOfDestinationCode;
	private String [] originAirportIDs;
	private String [] destAirportIDs;

	public String getName(){
		return name;
	}

	public void setName(String name){
		this.name = name;
	}

	public String getCityOfOrigin(){
		return cityOfOrigin;
	}

	public void setCityOfOrigin(String city){
		this.cityOfOrigin = city;
	}

	public String getCountryOfOrigin(){
		return countryOfOrigin;
	}

	public void setCountryOfOrigin(String country){
		this.countryOfOrigin = country;
	}


	public String getCityOfDestination(){
		return cityOfDestinaton;
	}

	public void setCityOfDestination(String city){
		this.cityOfDestinaton = city;
	}

	public String getCountryOfDestination(){
		return countryOfDestination;
	}

	public void setCountryOfDestination(String country){
		this.countryOfDestination = country;
	}

	public boolean getOneWayTrip(){
		return oneWayTrip;
	}

	public void setOneWayTrip(boolean oneWayTrip){
		this.oneWayTrip = oneWayTrip;
	}

	public String getReturnDate(){
		return returnDate;
	}

	public void setReturnDate(String returnDate){
		this.returnDate = returnDate;
	}

	public String getOutboundDate(){
		return outboundDate;
	}

	public void setOutboundDate(String outboundDate){
		this.outboundDate = outboundDate;
	}

	public String getCurrency(){
		return currency;
	}

	public void setCurrency(String currency){
		this.currency = currency;
	}

	public int getReferenceNumber(){
            return referenceNumber;
      }

      public void setReferenceNumber(int referenceNumber){
            this.referenceNumber = referenceNumber;
	}
	
	public String getCountryOfOriginCode(){
		return countryOfOriginCode;
	}

	public void setCountryOfOriginCode(String countryOfOriginCode){
		this.countryOfOriginCode = countryOfOriginCode;
	}

	public String getCountryOfDestinationCode(){
		return countryOfDestinationCode;
	}

	public void setCountryOfDestinationCode(String countryOfDestinationCode){
		this.countryOfDestinationCode = countryOfDestinationCode;
	}

	public String[] getOriginAirportIDs(){
		return originAirportIDs;
	}

	public void setOriginAirortIDs(String[] originAirportIDs){
		this.originAirportIDs = originAirportIDs;
	}

	public String[] getDestAirportIDs(){
		return destAirportIDs;
	}

	public void setDestAirortIDs(String[] destAirportIDs){
		this.destAirportIDs = destAirportIDs;
	}

	/**
	 * TODO (Barry): Must update equals method with ref num
	 */
	public Boolean equals(FlightRequest flightRequest){
		if (this.getName().equals(flightRequest.getName()) &&
			this.getCityOfOrigin().equals(flightRequest.getCityOfOrigin()) &&
			this.getCountryOfOrigin().equals(flightRequest.getCountryOfOrigin()) && 
			this.getCityOfDestination().equals(flightRequest.getCityOfDestination()) &&  
			this.getCountryOfDestination().equals(flightRequest.getCountryOfDestination()) &&    
			(this.getOneWayTrip() == flightRequest.getOneWayTrip()) &&    
			this.getReturnDate().equals(flightRequest.getReturnDate()) &&
			this.getOutboundDate().equals(flightRequest.getOutboundDate()) &&
			this.getCurrency().equals(flightRequest.getCurrency()) ) {
				return true;
			}
		return false;
	}

}
