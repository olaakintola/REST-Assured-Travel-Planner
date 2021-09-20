package service.flights;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;


import java.net.URI;
import java.net.URISyntaxException;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Collection;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.mashape.unirest.http.exceptions.UnirestException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException; 

import service.core.Flight; 
import service.core.FlightRequest;
import service.flights.NoSuchFlightException;
import service.core.ClientChoice;

import java.util.Iterator;

@RestController
public class FlightServiceB {
	
	private static int flightRequestReferenceNumber = 0;    // unique reference number for each flightRequest
	private static int searchedFlightReferenceNumber = 0;           // unique reference number for each flight found by FlightService that matches requirements from flightRequest
	private static int bookedFlightReferenceNumber = 0;           // unique reference number for each flight booked by a client
	final String locale = "en-GB";     // argument needed to call Skycanner API
	private Map<Integer, Flight> bookedFlights = new HashMap<>();      // Map of all flights created with new reference number as key
	private Map<Integer, Flight> searchedFlights = new HashMap<>();    // Map of all flights that FlightService searched for

	@Autowired
	private RestTemplate restTemplate;
 
	/**
	 * POST REQUEST: handles all flight requests from travel agent. TravelAgentService sends a flightRequest object specifying
	 * the user requirements and then uses this info to search for flights
	 * 
	 * @param flightRequest
	 * @return clientFlghts
	 * @throws URISyntaxException
	 */

	@RequestMapping(value="/flightservice/flightrequests",method=RequestMethod.POST)
	public ResponseEntity<Flight[]> searchFlights(@RequestBody FlightRequest flightRequest)  throws URISyntaxException {

		ArrayList<FlightQuote> flightQuotes = new ArrayList();  // FlightQuote object holds data that is needed to access JSON elements that hold
											  // data needed for Flight class

		String[] originAirportIDs = flightRequest.getOriginAirportIDs();
		String[] destAirportIDs = flightRequest.getDestAirportIDs();

		// Only uses the first origin airport and finds flights to all destination airports
		// String test = null;
		if( !(originAirportIDs[0].equals(null)) && !(destAirportIDs[0].equals(null)) ){   // search for flights only if there is an origin airport and at least one destination airport

			for (String airportID : destAirportIDs){
				System.out.println(airportID);
				flightQuotes.addAll(findFlights(flightRequest.getCountryOfOriginCode(),flightRequest.getCurrency(),locale,originAirportIDs[0],airportID,
				flightRequest.getOutboundDate(), flightRequest.getReturnDate()));
			}
	
		}
		
		/**
		 * The following code removes any duplicate flightQuotes
		 */
		ArrayList<FlightQuote> flightQuotesWithDuplicatesRemoved = new ArrayList();
		flightQuotesWithDuplicatesRemoved = removeDuplicates(flightQuotes);
		
		/**
		 * The following code converts FlightQuotes list to a Flight array (array because lists cannot be sent using REST)
		 */

		Flight[] clientFlights = new Flight[flightQuotesWithDuplicatesRemoved.size()];
		clientFlights = convertFlightQuotesToFlights(flightQuotesWithDuplicatesRemoved, flightRequest);

		/** 
		 * The following code prints all flights which were found through the Skyscanner API
		 */

		for (Flight flight : clientFlights){
			System.out.println(flight.toString());
		}

		clientFlights = addFlightsToSearchFlightsMap(clientFlights);

		flightRequestReferenceNumber++;        // give this request a unique reference number. Further functionality would have made better use of this ref number

		String path = ServletUriComponentsBuilder.fromCurrentContextPath().
			build().toUriString()+ "/flightservice/flightrequests/"+flightRequestReferenceNumber;     // Create URI for this flight request
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(new URI(path));
		return new ResponseEntity<>(clientFlights, headers, HttpStatus.CREATED);     // Returns flights to travel agent
	}
	
	/**
	 * POST REQUEST: Once the client has chosen a flight then this choice will be passed on to TravelAgentService which
	 * will then pass it on to this method. FlightService will then create a booking for this flight which can
	 * be requested via a GET request
	 * 
	 * @param clientChoice
	 * @return flight
	 */

	@RequestMapping(value="/flightservice/flights",method=RequestMethod.POST)
	public ResponseEntity<Flight> createFlight(@RequestBody ClientChoice clientChoice)  throws URISyntaxException {

		Flight flight = searchedFlights.get(clientChoice.getReferenceNumber());        // find flight the client wishes to book from searchedFlights map

		System.out.println("\nTesting /flightservice/flights\n");
		
		// Add a new flight for this client to bookedFlights map (which contains booked flights for all clients)
		bookedFlightReferenceNumber++;
		bookedFlights.put(bookedFlightReferenceNumber,flight);

		String path = ServletUriComponentsBuilder.fromCurrentContextPath().
			build().toUriString()+ "/flightservice/flights/"+bookedFlightReferenceNumber;     // Create URI for this flight
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(new URI(path));
		return new ResponseEntity<>(flight, headers, HttpStatus.CREATED);     // Returns flights to travel agent
	}

	/**
	 * GET REQUEST
	 * 
	 * @param reference
	 * @return flight
	 */
	@RequestMapping(value="/flightservice/flights/{reference}",method=RequestMethod.GET)
	public Flight getFlight(@PathVariable("reference") int reference) {

		Flight flight = bookedFlights.get(reference);
		if (flight == null) throw new NoSuchFlightException();
		return flight;
	}

	/**
	 * GET REQUEST (all instances)
	 * 
	 * @return bookedFlights.values()
	 */
	@RequestMapping(value="/flightservice/flights",method=RequestMethod.GET)
	public @ResponseBody Collection<Flight> listEntries() {

		if (bookedFlights.size() == 0) throw new NoSuchFlightException();
		return bookedFlights.values();
	}

	/**
	 * PUT REQUEST: replace flight with given reference number
	 * 
	 * @param referenceNumber
	 * @param clientChoice 
	 * @throws URISyntaxException
	 */

	@RequestMapping(value="/flightservice/flights/{referenceNumber}", method=RequestMethod.PUT)
    	public ResponseEntity<Flight> replaceFlight(@PathVariable int referenceNumber, @RequestBody ClientChoice clientChoice) throws URISyntaxException{

		Flight newChoiceOfFlight = searchedFlights.get(clientChoice.getReferenceNumber());        // find flight the client wishes to book

		// Replace old flight with a new flight 
		Flight previouslyBookedFlight = bookedFlights.remove(referenceNumber);
		bookedFlights.put(referenceNumber,newChoiceOfFlight);

		String path = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString()+ "/flightservice/flights/"+referenceNumber;
		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-Location", path);
		return new ResponseEntity<>(headers, HttpStatus.OK);
    }
	    
    	/**
	     * DELETE REQUEST: removes flights with given reference number
	     * 
	     * @param referenceNumber
	     */

	// @RequestMapping(value="/flights/{referenceNumber}", method=RequestMethod.DELETE)
	// @ResponseStatus(value=HttpStatus.NO_CONTENT)
	// public void deleteFlights(@PathVariable String uri) {

	// 	Flight [] clientFlights = flights.remove(referenceNumber);
	// 	if (clientFlights == null) throw new NoSuchFlightException();
	// }


	// If there is no flight listed with the given reference then throw this exception
	@ResponseStatus(value = HttpStatus.NOT_FOUND)
	public class NoSuchFlightException extends RuntimeException {
		static final long serialVersionUID = -6516152229878843037L;
	} 

	/**
	 * The following code converts a given string to a JSON object
	 * 
	 * @param response
	 * @return jsonObject
	 */

	public JSONObject parseJSONObject(String response){

		JSONObject jsonObject = new JSONObject();
		try{
			JSONParser parser = new JSONParser();
			jsonObject = (JSONObject) parser.parse(response);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return jsonObject;
	}

	/**
	 * The following method is used to call the call the GET BrowseQuotes (Skyscanner API method). This method returns the 
	 * flight info we are looking for. This info is then used to create a Flight object which is then sent back to the travel
	 * agent
	 * 
	 * @param countryOfOriginCode
	 * @param currency
	 * @param locale
	 * @param originAirportCode
	 * @param destAirportCode
	 * @param outboundDate
	 * @return flightQuotes
	 */
	
	public ArrayList<FlightQuote> findFlights(String countryOfOriginCode, String currency, String locale, String originAirportCode, String destAirportCode, 
							String outboundDate, String returnDate) {

		ArrayList<FlightQuote> flightQuotes = new ArrayList();

		try{  // The following request is used to call skyscanner API
			HttpRequest request = HttpRequest.newBuilder()
					.uri(URI.create("https://skyscanner-skyscanner-flight-search-v1.p.rapidapi.com/apiservices/browsequotes/v1.0/"+
						countryOfOriginCode+"/"+currency+"/"+locale+"/"+originAirportCode+"/"+destAirportCode+"/"+outboundDate+"?inboundpartialdate="+returnDate))
					.header("x-rapidapi-key", "91b7d3fc53mshf8b9bac5b6fd091p118e46jsn22debfe2cd83")
					.header("x-rapidapi-host", "skyscanner-skyscanner-flight-search-v1.p.rapidapi.com")
					.method("GET", HttpRequest.BodyPublishers.noBody())
					.build();
			HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

			String flightQuotations = response.body();   // Puts response into a string
			JSONObject placesJson = parseJSONObject(flightQuotations);  // Converts the string into a JSON object

			JSONArray flightQuotesArray = new JSONArray();
			flightQuotesArray = (JSONArray) placesJson.get("Quotes");
			
			JSONArray carriersArray = new JSONArray();
			carriersArray = (JSONArray) placesJson.get("Carriers");   // Find 'carriers' array in placesJson JSON object

			JSONArray placesArray = new JSONArray();
			placesArray = (JSONArray) placesJson.get("Places");   // Find 'places' array in placesJson JSON object

			// If there data is received from the skyscanner API call then extract the relevant info and use it to 
			// create a Flight object
			if (flightQuotesArray.size() > 0){

				int index = 0;
				while (index < flightQuotesArray.size() ) {

					FlightQuote flightQuote = new FlightQuote();
					JSONObject quotes = (JSONObject) flightQuotesArray.get(index);
					Long price = (Long) quotes.get("MinPrice");
					flightQuote.setPrice(Long.toString(price));

					// Finds the airline ID
					JSONObject outboundLeg = (JSONObject) quotes.get("OutboundLeg");
					JSONArray carrierIds = (JSONArray) outboundLeg.get("CarrierIds");
					Long carrierId = (Long) carrierIds.get(0);
					flightQuote.setCarrierId(Long.toString(carrierId));
					
					// Finds the airport origin ID and destination ID which are needed to find the names of the airports
					Long originId = (Long) outboundLeg.get("OriginId");
					flightQuote.setOriginId(Long.toString(originId));
					Long destId = (Long) outboundLeg.get("DestinationId");
					flightQuote.setDestinationId(Long.toString(destId));

					// Finds the name of the Airline by using IDs found in the JSON Object
					for(int j=0; j<carriersArray.size(); j++){
						JSONObject carriers = (JSONObject) carriersArray.get(j);
						Long carId = (Long) carriers.get("CarrierId");
						if (carId.equals(carrierId)){
							flightQuote.setAirline((String) carriers.get("Name"));
						}
					}

					// Finds 'Name' which is the name of the airport
					// This code finds both the origin and destination airport name
					for(int k=0; k<placesArray.size(); k++){

						JSONObject places = (JSONObject) placesArray.get(k);
						Long placeId = (Long) places.get("PlaceId");
						String placeName = (String) places.get("Name");
						
						// If IDs match then we know this is the origin airport
						if (placeId.equals(originId)){
							
							flightQuote.setOriginAirportName(placeName);
						}

						// If IDs match then we know this is the destination airport
						if (placeId.equals(destId)){
							flightQuote.setDestAirportName(placeName);
						}
					}	
					flightQuotes.add(flightQuote);
					index++;
			}	
		}
			

		} catch(IOException e) {
                  e.printStackTrace();
            }
            catch(InterruptedException e) {
                  e.printStackTrace();
		}    
		return flightQuotes;
	}

	/**
	 * The following method removes all duplicates found in the list of FlightQuotes. Duplicates are one of the limitations of the Skycanner API
	 * 
	 * @param flightQuotes
	 * @return flightQuotes
	 */
	public ArrayList<FlightQuote> removeDuplicates(ArrayList<FlightQuote> flightQuotes){

		ArrayList<FlightQuote> flightQuotesCopy = new ArrayList();
		for (FlightQuote flightQuote : flightQuotes){
			flightQuotesCopy.add(flightQuote);
		}

		for (Iterator<FlightQuote> flightQuoteIterator = flightQuotes.iterator(); flightQuoteIterator.hasNext();){

			boolean isADuplicate = false;
			FlightQuote flightQuote = flightQuoteIterator.next();
			flightQuotesCopy.remove(flightQuote);

			for(FlightQuote flightQ : flightQuotesCopy){
				if (flightQ.equals(flightQuote)){
					System.out.println("We found a copy");
					isADuplicate = true;
				}
			}
			if(isADuplicate){
				flightQuoteIterator.remove();
			}
		}
		return flightQuotes;
	}

	/**
	 * The following method converts FlightQuote list to Flight array (Flight object will contain info that is only relevant to the user).
	 * So, info is taken from the FlightRequest object and FlightQuote object and combined to create a Flight object which will then
	 * be returned to the travel agent.
	 * 
	 * @param flightQuotes
	 * @return flights
	 */

	public Flight [] convertFlightQuotesToFlights(ArrayList<FlightQuote> flightQuotes, FlightRequest flightRequest){

		Flight [] flights = new Flight[flightQuotes.size()];
		int i = 0;
		for (FlightQuote flightQuote : flightQuotes){
			Flight flight = new Flight(flightRequest.getCityOfOrigin(), flightRequest.getCityOfDestination(),
			flightRequest.getOutboundDate(), flightRequest.getReturnDate(), flightQuote.getAirline(), flightQuote.getPrice(),
			flightQuote.getOriginAirportName(), flightQuote.getDestAirportName());
			flights[i] = flight;
			i++;
		}
		return flights;
	}

	/**
	 * The following method adds all new flights found by FlightService to searchedFlights map
	 */

	 public Flight [] addFlightsToSearchFlightsMap(Flight [] flights){

		for (Flight flight : flights){

			searchedFlightReferenceNumber++; 
			flight.setReferenceNumber(searchedFlightReferenceNumber);           // set the ref number in Flight so that we can cross reference 
														  // with the client choice of flight booking
			searchedFlights.put(searchedFlightReferenceNumber,flight);          // add new flight to map with new ref number
		}
		return flights;
	 }

}
