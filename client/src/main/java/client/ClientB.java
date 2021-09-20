// package client;

// import java.text.NumberFormat;
// import java.util.List;
// import java.util.ArrayList;
// import java.util.LinkedList;

// import org.springframework.web.client.RestTemplate; 
// import org.springframework.http.HttpEntity;
 
// import service.core.FlightRequest;
// import service.core.Flight;
// import service.core.Hotel;
// import service.core.ClientRequest;
// import service.core.ClientResponse;
// import service.core.HotelRequest;
// import service.core.TravelPackage;
// import service.core.ActivityRequest;
// import service.core.Attraction;
// import service.core.AttractionRequest;
// import service.core.ActivityItem;
// import service.core.Booking;
// import service.core.ReplaceBooking;

// import org.json.simple.JSONArray;
// import org.json.simple.JSONObject;
// import org.json.simple.parser.JSONParser;
// import org.json.simple.parser.ParseException; 
// import java.net.URI;
// import java.net.URISyntaxException;
// import java.net.http.HttpClient;
// import java.net.http.HttpRequest;
// import java.net.http.HttpResponse;
// import java.io.IOException;

// public class ClientB {
	
//       public static final String newArgs = "http://localhost:8081/travelagent/travelpackagerequests";
//       public static final String newArgs2 = "http://localhost:8081/travelagent/bookings";
// 	public static int referenceNumber = 0;
// 	final static String locale = "en-GB";     // need to call Skycanner API
	
// 	public static void main(String[] args) {
		
// 	// public static void bookingAdventure(ClientBooking[] clients) {	
// 		Flight[] flights = new Flight[10];
// 		RestTemplate restTemplate = new RestTemplate();

// 		FlightRequest flightRequest = new FlightRequest("Donald Trump", "Dublin", "Ireland", "Paris", "France", false,
// 				 "2021-01-17", "2021-01-09", "EUR");
		
// 		flightRequest.setCountryOfOriginCode(getListMarkets("Ireland"));
// 		flightRequest.setCountryOfDestinationCode(getListMarkets("France"));

// 		System.out.println("DEST CODE: "+getListMarkets("France"));

// 		HotelRequest hotelRequest = new HotelRequest();
// 		hotelRequest.setCityCode("PAR");
// 		hotelRequest.setNumberOfGuests(2);
// 		ClientRequest clientRequest = new ClientRequest();
// 		clientRequest.setFlightRequest(flightRequest);
// 		clientRequest.setHotelRequest(hotelRequest);

// 		ActivityRequest ar = new ActivityRequest();
// 		ar.setCountry("Ireland");
// 		ar.setCity("Dublin");
// 		// ar.setLatitude(53.3498);
// 		// ar.setLongitude(6.2603);
// 		clientRequest.setActivityRequest(ar);

// 		AttractionRequest attractionRequest = new AttractionRequest();
// 		attractionRequest.setCity("Paris");
// 		attractionRequest.setCountry("France");
// 		clientRequest.setAttractionRequest(attractionRequest);

// 		Attraction [] attractionsTest = new Attraction[10];
// 		System.out.println("TESTING attraction null: "+attractionsTest[0]);

		
// 		ArrayList<String> originAirportIDs = new ArrayList();          // Holds all airports for the given origin city
// 		ArrayList<String> destinationAirportIDs = new ArrayList();      //Holds all airports for the given destination city


// 		originAirportIDs = getListPlaces(flightRequest.getCityOfOrigin(), flightRequest.getCountryOfOrigin(),   // Find airport IDs for origin
// 					flightRequest.getCountryOfOriginCode(), flightRequest.getCurrency());
// 		String [] originAirportIDsArray = convertAirportIDsListToAirportIDsArray(originAirportIDs);    // converts list to array
		
// 		destinationAirportIDs = getListPlaces(flightRequest.getCityOfDestination(), flightRequest.getCountryOfDestination(),   // Find airport IDs for destination
// 					flightRequest.getCountryOfDestinationCode(), flightRequest.getCurrency()); 
// 		String [] destAirportIDsArray = convertAirportIDsListToAirportIDsArray(destinationAirportIDs);    // converts list to array
		
// 		System.out.println("\n ORIGIN AIRPORT IDS: "+originAirportIDsArray+"\n");
// 		for(String s : originAirportIDsArray){
// 			System.out.println(s);
// 		}
// 		System.out.println("\n ORIGIN AIRPORT IDS: "+destAirportIDsArray+"\n");
// 		for(String s : destAirportIDsArray){
// 			System.out.println(s);
// 		}

// 		flightRequest.setOriginAirortIDs(originAirportIDsArray);
// 		flightRequest.setDestAirortIDs(destAirportIDsArray);;


//                   HttpEntity<ClientRequest> request = new HttpEntity<>(clientRequest);
                  
// 			TravelPackage travelPackage = new TravelPackage();
// 			System.out.println("\n Country Code: "+getListMarkets("Ireland"));
//                   travelPackage = restTemplate.postForObject(newArgs,request,TravelPackage.class);
		
		

                  
//                   Hotel [] hotels2 = travelPackage.getHotels();
// 			Flight [] flights2 = travelPackage.getFlights();
// 			ActivityItem [] activities2 = travelPackage.getActivities();
// 			Attraction [] attractions2 = travelPackage.getAttractions();

// 			for (int i=0; i < 1; i++){
//                         Flight f = flights2[i];
// 				Hotel h = hotels2[i];
// 				ActivityItem a = activities2[i];
// 				Attraction at = attractions2[i];
// 				if (f != null){
// 					System.out.println("City of Destination is: " + f.getCityOfDestination());
// 					System.out.println("Price of flight is: " + f.getPrice());
//                         }	
//                         System.out.println("\n");
//                         if (h != null){
//                               System.out.println("Description of hotel is: " + h.getDescription());
// 					System.out.println("Price of hotel is: " + h.getPrice());
// 				}
// 				System.out.println("\n");
// 				if (a != null){
// 					System.out.println("Description of activity is: " + a.getDescription());
// 					System.out.println("Booking link of activity is: " + a.getBookingLink());
// 				}
// 				System.out.println("\n");
// 				if (at != null){
// 					System.out.println("Category of attraction is: " + at.getCategory());
// 					System.out.println("Name of attraction is: " + at.getName());
// 				}
//                         System.out.println("\n");
//                   }

//                   /**
//                    * Prints out the ref nums for travelPackage, each flight and each hotel
//                    */

//                    System.out.println("\nTravel package ref num: "+ travelPackage.getTravelPackageReferenceNumber()+"\n");

//                    for(Flight f : travelPackage.getFlights()){
//                          System.out.println("Flight ref num: "+f.getReferenceNumber());
//                    }

//                    System.out.println("\n");

//                    for (Hotel h : travelPackage.getHotels()){
//                          System.out.println("Hotel ref num: "+h.getReferenceNumber());
// 			 }
			 
// 			 for (int p=0; p < 3;p++){
// 				ActivityItem a = travelPackage.getActivities()[p];
// 				System.out.println("Activity ref num: "+a.getReferenceNumber());
// 			}


//             /**
//              * Testing code for POST (clientResponse)
//              */

//             ClientResponse clientResponse = new ClientResponse();
//             clientResponse.setTravelPackageReferenceNumber(travelPackage.getTravelPackageReferenceNumber());
//             clientResponse.setHotelReferenceNumber(1);
// 		clientResponse.setFlightReferenceNumber(2);
// 		int [] intArray = new int[3];
// 		intArray[0] = 1;   
// 		intArray[1] = 2;   
// 		intArray[2] = -1;   

// 		clientResponse.setActivitiesReferenceNumber(intArray);
// 		clientResponse.setAttractionsReferenceNumber(intArray);

//             HttpEntity<ClientResponse> requestClientResponse = new HttpEntity<>(clientResponse);
                  
//             Booking booking = new Booking();
//             booking = restTemplate.postForObject(newArgs2,requestClientResponse,Booking.class);

//             System.out.println("\nAirline: "+booking.getFlight().getAirline());
//             System.out.println("Hotel Address: "+booking.getHotel().getAddress());
// 		System.out.println("Booking ref Num: "+booking.getReferenceNumber());
// 		System.out.println("Activity 1: "+booking.getActivities()[0]);
//             System.out.println("Attraction 1: "+booking.getAttractions()[0]);
                  
//             /**
//              * Testing code for GET below
//              */

//              Booking getBooking = restTemplate.getForObject("http://localhost:8081/travelagent/bookings/1",Booking.class);
//              System.out.println("\nGET TEST: "+getBooking.getFlight().getAirline()+"\n");

// 		/**
// 		 * Testing code for DELETE below
// 		 */
// 		// restTemplate.delete("http://localhost:8081/travelagent/bookings/1");


// 		/**
// 		 *  Testing code for PUT below
// 		 *  */ 
// 		travelPackage = restTemplate.postForObject(newArgs,request,TravelPackage.class);
//             clientResponse.setHotelReferenceNumber(1);
// 		clientResponse.setFlightReferenceNumber(2);
// 		int [] intArray2 = new int[2];
// 		intArray2[0] = 1;    // old activity
// 		intArray2[1] = 3;	   // new activity
// 		clientResponse.setActivitiesReferenceNumber(intArray2);
// 		clientResponse.setAttractionsReferenceNumber(intArray2);

//             ReplaceBooking replaceBooking = new ReplaceBooking();
//             replaceBooking.setNewChoiceOfBooking(clientResponse);
//             replaceBooking.setPreviousBooking(booking);

//             HttpEntity<ReplaceBooking> requestClientResponsePut = new HttpEntity<>(replaceBooking);
//             restTemplate.put("http://localhost:8081/travelagent/bookings/1",requestClientResponsePut);
//             System.out.println("\nPUT TEST COMPLETE\n");

// 		 /**
// 		 *  Testing code for PATCH below
// 		 *  */ 
//             // travelPackage = restTemplate.postForObject(newArgs,request,TravelPackage.class);
//             // clientResponse.setHotelReferenceNumber(1);
//             // clientResponse.setFlightReferenceNumber(2);
//             // HttpEntity<ClientResponse> requestClientResponsePatch = new HttpEntity<>(clientResponse);
//             // Booking patchBooking = restTemplate.patchForObject("http://localhost:8081/travelagent/bookings/1",requestClientResponsePatch,Booking.class);
//             // System.out.println("\nPATCH TEST: "+patchBooking.getFlight().getAirline()+"\n");
		
// 	} 

// 	/**
// 	 * The following method converts the array list of airport IDs to an array of airport IDs as we cannot pass a list
// 	 * using REST
// 	 * 
// 	 * @param countryName
// 	 * @return
// 	 */

// 	 public static String [] convertAirportIDsListToAirportIDsArray(ArrayList<String> airportIDsList){
		 
// 		String [] airportIDsArray = new String[airportIDsList.size()];

// 		int index = 0;
// 		for (String id : airportIDsList){
			
// 			airportIDsArray[index] = airportIDsList.get(index);
// 			index++;
// 		}
// 		return airportIDsArray;
// 	 }

// 	/**
// 	 * The following code is used to retrieve the country code for the country name given as this is needed for the Skyscanner API request
// 	 * 
// 	 * @param countryName
// 	 * @return countryCode
// 	 */

// 	public static String getListMarkets(String countryName) { 
		
// 		String countryCode = "";

// 		try {
// 			HttpRequest requestCode = HttpRequest.newBuilder()
// 				.uri(URI.create("https://skyscanner-skyscanner-flight-search-v1.p.rapidapi.com/apiservices/reference/v1.0/countries/en-GB"))
// 				.header("x-rapidapi-key", "91b7d3fc53mshf8b9bac5b6fd091p118e46jsn22debfe2cd83")
// 				.header("x-rapidapi-host", "skyscanner-skyscanner-flight-search-v1.p.rapidapi.com")
// 				.method("GET", HttpRequest.BodyPublishers.noBody())
// 				.build();
// 			HttpResponse<String> response = HttpClient.newHttpClient().send(requestCode, HttpResponse.BodyHandlers.ofString());

// 			/**
// 			 * TODO may need to delete this response if examples.json isn't needed going forward
// 			 */
// 			// HttpResponse<Path> response2 = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofFile(Paths.get("countryCodes.json")));

// 			String countryCodes = response.body();
// 			JSONObject countryCodesJson = parseJSONObject(countryCodes);

// 			JSONArray countryCodesArray = new JSONArray();
// 	    		countryCodesArray = (JSONArray) countryCodesJson.get("Countries");
			
// 			//loop through array to find the country code 
// 			int index = 0;
// 			while (index < countryCodesArray.size()) {

// 				JSONObject jsonObject = (JSONObject) countryCodesArray.get(index);
// 				String name = (String) jsonObject.get("Name");
				
// 				if (name.equals(countryName)){
// 					countryCode = (String) jsonObject.get("Code");
// 				}
// 				index++;
// 			}	

// 		} catch(IOException e) {
//                   e.printStackTrace();
// 		}
// 		catch(InterruptedException e) {
//                   e.printStackTrace();
// 		}  
// 		return countryCode;
// 	}

// 	/**
// 	 * The following method will retrieve the airport IDs 
// 	 * 
// 	 * @param cityOfDestination
// 	 * @param countryOfDestination
// 	 * @param countryOfOriginCode
// 	 * @param currency
// 	 * @return
// 	 */

// 	// GET ListPlaces (Skyscanner API)
// 	public static ArrayList<String> getListPlaces(String cityOfDestination, String countryOfDestination, String countryOfOriginCode, String currency) { 
		
// 		ArrayList<String> airportIDs = new ArrayList();

// 		try {
// 			HttpRequest request = HttpRequest.newBuilder()
// 					.uri(URI.create("https://skyscanner-skyscanner-flight-search-v1.p.rapidapi.com/apiservices/autosuggest/v1.0/"+
// 						countryOfOriginCode+"/"+currency+"/"+locale+"/?query="+cityOfDestination+"%20"+countryOfDestination))
// 					.header("x-rapidapi-key", "91b7d3fc53mshf8b9bac5b6fd091p118e46jsn22debfe2cd83")
// 					.header("x-rapidapi-host", "skyscanner-skyscanner-flight-search-v1.p.rapidapi.com")
// 					.method("GET", HttpRequest.BodyPublishers.noBody())
// 					.build();
// 			HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
// 			/**
// 			 * TODO may need to delete this response if airports.json isn't needed going forward
// 			 */
// 			// HttpResponse<Path> response2 = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofFile(Paths.get("airports.json")));
			
// 			System.out.println("Get ListPlaces: "+response.body());

// 			String places = response.body();
// 			JSONObject placesJson = parseJSONObject(places);

// 			JSONArray placesArray = new JSONArray();
// 		      placesArray = (JSONArray) placesJson.get("Places");
// 			System.out.println("Places array: "+placesArray);
			
// 			int index = 0;
// 			while (index < placesArray.size()) {
// 				JSONObject jsonObject = (JSONObject) placesArray.get(index);
// 				airportIDs.add((String) jsonObject.get("PlaceId"));
// 				index++;
// 			}	

// 		} catch(IOException e) {
//                   e.printStackTrace();
// 		}
// 		catch(InterruptedException e) {
//                   e.printStackTrace();
// 		}  
// 		return airportIDs;
// 	}

// 	/**
// 	 * The following code converts a given string to a JSON object
// 	 * 
// 	 * @param response
// 	 * @return jsonObject
// 	 */

// 	public static JSONObject parseJSONObject(String response){

// 		JSONObject jsonObject = new JSONObject();
// 		try{
// 			JSONParser parser = new JSONParser();
// 			jsonObject = (JSONObject) parser.parse(response);
// 		} catch (ParseException e) {
// 			e.printStackTrace();
// 		}
// 		return jsonObject;
// 	}

   
// 	// /**
// 	//  * Display the client info nicely.
// 	//  * 
// 	//  * @param info
// 	//  */
// 	// public static void displayProfile(ClientInfo info) {
// 	// 	System.out.println("|=================================================================================================================|");
// 	// 	System.out.println("|                                     |                                     |                                     |");
// 	// 	System.out.println(
// 	// 			"| Name: " + String.format("%1$-29s", info.getName()) + 
// 	// 			" | Gender: " + String.format("%1$-27s", (info.getGender()==ClientInfo.MALE?"Male":"Female")) +
// 	// 			" | Age: " + String.format("%1$-30s", info.getAge())+" |");
// 	// 	System.out.println(
// 	// 			"| License Number: " + String.format("%1$-19s", info.getLicenseNumber()) + 
// 	// 			" | No Claims: " + String.format("%1$-24s", info.getNoClaims() +" years") +
// 	// 			" | Penalty Points: " + String.format("%1$-19s", info.getPoints())+" |");
// 	// 	System.out.println("|                                     |                                     |                                     |");
// 	// 	System.out.println("|=================================================================================================================|");
// 	// }

// 	// /**
// 	//  * Display a quotation nicely - note that the assumption is that the quotation will follow
// 	//  * immediately after the profile (so the top of the quotation box is missing).
// 	//  * 
// 	//  * @param quotation
// 	//  */
// 	// public static void displayQuotation(Quotation quotation) {
// 	// 	System.out.println(
// 	// 			"| Company: " + String.format("%1$-26s", quotation.getCompany()) + 
// 	// 			" | Reference: " + String.format("%1$-24s", quotation.getReference()) +
// 	// 			" | Price: " + String.format("%1$-28s", NumberFormat.getCurrencyInstance().format(quotation.getPrice()))+" |");
// 	// 	System.out.println("|=================================================================================================================|");
// 	// }
	
// 	/**
// 	 * Test Data
// 	 */
	
// }
