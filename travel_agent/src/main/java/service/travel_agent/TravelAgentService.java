package service.travel_agent;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;


import java.net.URI;
import java.net.URISyntaxException;

import org.springframework.http.HttpEntity;

import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import java.util.Collection;
import java.util.LinkedList;
import java.util.HashMap;

import service.core.FlightRequest;
import service.core.Flight;
import service.core.TravelPackage;
import service.core.ClientRequest;
import service.core.HotelRequest;
import service.core.Travel;
import service.core.ActivityRequest;
import service.core.ActivityItem;
import service.core.Hotel;
import service.core.Booking;
import service.core.MongoBooking;
import service.core.ClientResponse;
import service.core.ClientChoice;
import service.core.ClientChoices;
import service.core.ReplaceBooking;
import service.core.Attraction;
import service.core.AttractionRequest;

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;

/**
 * This service is in charge of taking requests from the client for flights, hotels, activities and attractions
 *
 */
@RestController
public class TravelAgentService {

	private final MongoRepository mongoRepository;

    @Autowired
    public TravelAgentService(MongoRepository mongoRepository) {
        this.mongoRepository = mongoRepository;
    }


	@Autowired
	@LoadBalanced
	private RestTemplate restTemplate;

	public static LinkedList<String> URIs = new LinkedList();        

	private Map<Integer, Booking> clientBookings = new TreeMap();
	private static int travelPackageRequestReferenceNumber = 0;
	private static int clientBookingReferenceNumber = 0;
	private static int referenceNumber = 0;

	// private static int searchedFlightReferenceNumber = 0;
	private Map<Integer, Flight> searchedFlights = new HashMap<>(); 
	private Map<Integer, Hotel> searchedHotels = new HashMap<>(); 
	private Map<Integer, ActivityItem> searchedActivities = new HashMap<>(); 
	private Map<Integer, Attraction> searchedAttractions = new HashMap<>();  


	/**
	 * POST REQUEST: handles requests from client for Hotels, Flights, Activities and Attractions
	 * 
	 * @param clientRequest
	 * @return
	 * @throws URISyntaxException
	 */

	@RequestMapping(value="/travelagent/travelpackagerequests",method=RequestMethod.POST)
	public ResponseEntity<TravelPackage> createTravelPackageRequest(@RequestBody ClientRequest clientRequest) throws URISyntaxException {
		/**
		 * POST request to Flight service for a FlightRequest which will return a list of available flights
		 */
		Flight[] flights = new Flight[50];	
		HttpEntity<FlightRequest> request = new HttpEntity<>(clientRequest.getFlightRequest());
		flights = restTemplate.postForObject("http://flights-service/flightservice/flightrequests",request, Flight[].class);

		addFlightsToSearchFlightsMap(flights);
		/**
		 * POST request to Hotel Service for a HotelRequest which will return a list of available hotels
		 */

		HttpEntity<HotelRequest> requestForHotels = new HttpEntity<>(clientRequest.getHotelRequest());
		Hotel [] hotels = restTemplate.postForObject("http://hotels-service/hotelservice/hotelrequests",requestForHotels, Hotel[].class);
		addHotelsToSearchHotelsMap(hotels);
		/**
		 * POST request to ActivityService
		 */

		HttpEntity<ActivityRequest> activityRequest = new HttpEntity<>(clientRequest.getActivityRequest());
		ActivityItem[] activities = restTemplate.postForObject("http://activities-service/activityservice/activityrequests", activityRequest, ActivityItem[].class);
		addActivitiesToSearchActivitiesMap(activities);
		
		 /**
		 * POST request to AttractionsService
		 */

		Attraction[] attractions = new Attraction[200];  // must instantiate the array as we will be sending back a null array if there are no client requests for attractions

		if (!(clientRequest.getAttractionRequest().getCity()==null)){

			HttpEntity<AttractionRequest> attractionRequest = new HttpEntity<>(clientRequest.getAttractionRequest());
			attractions = restTemplate.postForObject("http://attractions-service/attractionservice/attractionrequests", attractionRequest, Attraction[].class);
			addAttractionsToSearchAttractionsMap(attractions);
		}
		
		/**
		 * Create a new TravelPackage for client with hotels,flight, activities and attractions found from respective services. Current implementation does not store travel packages thus
		 * the travelPackageRequestReferenceNumber is not needed. Further functionality of the system would have
		 * made use of this reference number.
		 */
		travelPackageRequestReferenceNumber++;                      // create new ref num for this travel package
		TravelPackage travelPackage = new TravelPackage();          
		travelPackage.setFlights(flights);
		travelPackage.setHotels(hotels);
		travelPackage.setActivities(activities);
		travelPackage.setAttractions(attractions);
		travelPackage.setTravelPackageReferenceNumber(travelPackageRequestReferenceNumber);
		
		/**
		 * Send response back to the client
		 */
		String path = ServletUriComponentsBuilder.fromCurrentContextPath().
			build().toUriString()+ "/travelagent/travelpackagerequests/"+travelPackageRequestReferenceNumber;  // Create new URI for this newly created travelPackage
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(new URI(path));
		return new ResponseEntity<>(travelPackage, headers, HttpStatus.CREATED);  // return the newly created travelPackage to client class
		
	} 


	public void storeBookingInMongo(Booking b){
		MongoBooking mb = new MongoBooking();
		mb.setReferenceId(String.valueOf(b.getReferenceNumber()));
		mb.setFlightDetails(b.getFlight().toString());
		if(b.getHotel()==null){
			mb.setHotelDetails("No hotel selected");
		}
		else{
			mb.setHotelDetails(b.getHotel().toString());
		}

		String temp="";
		boolean bool = true;
		for(ActivityItem ai:b.getActivities()){
			if(ai!=null){
				bool = false;
				temp+= ai.toString();
			}
		}
		if(bool){
			temp = "None";
		}
		mb.setActivitiesDetails(temp);

		String temp2="";
		boolean bool2 = true;
		for(Attraction at:b.getAttractions()){
			if(at!=null){
				bool2 = false;
				temp2+= at.toString();
			}
		}
		if(bool2){
			temp2 = "None";
		}
		mb.setAttractionsDetails(temp2);
		mongoRepository.insertBooking(mb);
	}


	public MongoBooking getBookingFromMongo(String referenceId){
		MongoBooking b = new MongoBooking();
		try{
			b = mongoRepository.getBookingFromMongo(referenceId);
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return b;
	}


	/**
	 * POST REQUEST: handles requests from client for a booking (after they have made their choice of flight, hotel etc.)
	 * 
	 * @param clientRequest
	 * @return
	 * @throws URISyntaxException
	 */
	
	@RequestMapping(value="/travelagent/bookings",method=RequestMethod.POST)
	public ResponseEntity<Booking> createBooking(@RequestBody ClientResponse clientResponse) throws URISyntaxException {

		/**
		 * POST request to Flight service for flight booking
		 * ClientChoice has an instance variable which holds the reference number of the flight the client wishes to book
		 */
		ClientChoice clientChoiceOfFlight = new ClientChoice();      // create ClientChoice to hold ref number
		clientChoiceOfFlight.setReferenceNumber(clientResponse.getFlightReferenceNumber());
		Flight flight = searchedFlights.get(clientChoiceOfFlight.getReferenceNumber());  

		/**
		 * POST request to Hotel Service for hotel booking
		 * ClientChoice has an instance variable which holds the reference number of the hotel the client wishes to book
		 */

		ClientChoice clientChoiceOfHotel = new ClientChoice();
		clientChoiceOfHotel.setReferenceNumber(clientResponse.getHotelReferenceNumber());
		Hotel hotel;
		if (clientChoiceOfHotel.getReferenceNumber()<0){
			hotel = new Hotel();
		}
		else{
			hotel = searchedHotels.get(clientChoiceOfHotel.getReferenceNumber()); 
		}

		/**
		 * POST request to ActivityRecommenderService for activity booking
		 * ClientChoices has an instance variable (int array) which holds each reference number for the activities the client wishes to book
		 */

		ActivityItem [] activities = new ActivityItem[clientResponse.getActivitiesReferenceNumber().length];
		
		ClientChoice clientChoiceOfActivity = new ClientChoice();
		
		if (clientResponse.getActivitiesReferenceNumber()[0] > 0){

			for(int i=0; i<clientResponse.getActivitiesReferenceNumber().length; i++){

				if(clientResponse.getActivitiesReferenceNumber()[i] > 0){
					clientChoiceOfActivity.setReferenceNumber(clientResponse.getActivitiesReferenceNumber()[i]);
					activities[i] = searchedActivities.get(clientResponse.getActivitiesReferenceNumber()[i]);
				}
			}
		}

		/**
		 * POST request to AttractionsService attraction booking
		 */

		Attraction [] attractions = new Attraction [clientResponse.getAttractionsReferenceNumber().length];      // must instantiate the array as we will be sending back a null array if client does not wish to book any attractions or if there are none available for the location

		ClientChoice clientChoiceOfAttraction = new ClientChoice(); // create ClientChoice to hold ref number of attraction
		if(clientResponse.getAttractionsReferenceNumber()[0] > 0); // if the reference number at index 0 is a negative number then we don't call AttractionsService
		{
			for (int j=0; j <clientResponse.getAttractionsReferenceNumber().length; j++){

				if (clientResponse.getAttractionsReferenceNumber()[j] > 0){

					clientChoiceOfAttraction.setReferenceNumber(clientResponse.getAttractionsReferenceNumber()[j]);
					attractions[j] = searchedAttractions.get(clientResponse.getAttractionsReferenceNumber()[j]);
				}			
			}
		}
		

		/**
		 * Create a new Booking for client
		 */
		Booking booking = new Booking();
		booking.setFlight(flight);
		booking.setHotel(hotel);
		booking.setActivities(activities);
		booking.setAttractions(attractions);
		Random r = new Random();
		clientBookingReferenceNumber = r.nextInt(1000000); // create unique ref num
		booking.setReferenceNumber(clientBookingReferenceNumber);   // give booking this unique ref num
		clientBookings.put(clientBookingReferenceNumber,booking);

		storeBookingInMongo(booking);
		/**
		 * Send response back to the client
		 */
		String path = ServletUriComponentsBuilder.fromCurrentContextPath().
			build().toUriString()+ "/travelagent/bookings/"+clientBookingReferenceNumber;  // Create new URI for this newly created travel booking
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(new URI(path));
		return new ResponseEntity<>(booking, headers, HttpStatus.CREATED);  // return the newly created booking to client 
		
	} 
	
	/**
	 * GET REQUEST (single instance)
	 * 
	 * @param referenceNumber
	 * @return booking
	 */
	@RequestMapping(value="/travelagent/bookings/{referenceNumber}",method=RequestMethod.GET)
	@ResponseStatus(value=HttpStatus.OK)
	public MongoBooking getBooking(@PathVariable String referenceNumber) throws NoSuchFieldException{
		MongoBooking mb = mongoRepository.getBookingFromMongo(referenceNumber);
		return mb;
	}


	/**
	 * GET REQUEST (all instances)
	 * 
	 * @return clientBookings
	 */
	@RequestMapping(value="/travelagent/bookings",method=RequestMethod.GET)
	public @ResponseBody Collection<Booking> listEntries() {

		if (clientBookings.size() == 0) throw new NoSuchBookingException();
		return clientBookings.values();
	}

	
	/**
	 * PUT REQUEST: This request is issued if the client wishes to replace their booking. So all 
	 * 
	 * @param referenceNumber
	 * @param clientResponse
	 * @return 
	 * @throws URISyntaxException
	 */
	@RequestMapping(value="/travelagent/bookings/{referenceNumber}", method=RequestMethod.PUT)
    	public ResponseEntity<Booking> replaceBooking(@PathVariable int referenceNumber, @RequestBody ReplaceBooking replaceBooking) throws URISyntaxException  {

		Booking booking = clientBookings.get(referenceNumber);
		if (booking == null) throw new NoSuchBookingException();

		/**
		 * PUT request to Flight service. Reference number for previous flight is specifified in URI and new flight reference number
		 * is the instance variable in clientChoice. Flight service will take this new reference number, search for the flight matching
		 * that reference number and use this flight to replace the old flight. This same approach is followed for all other services
		 */
		// Flight flight = new Flight();	
		ClientChoice clientChoiceOfFlight = new ClientChoice();      // create ClientChoice to hold ref number
		clientChoiceOfFlight.setReferenceNumber(replaceBooking.getNewChoiceOfBooking().getFlightReferenceNumber());
		HttpEntity<ClientChoice> requestFlight = new HttpEntity<>(clientChoiceOfFlight);
		restTemplate.put("http://flights-service/flightservice/flights/"+replaceBooking.getPreviousBooking().getFlight().getReferenceNumber(),requestFlight);
		Flight flight = restTemplate.getForObject("http://flights-service/flightservice/flights/"+replaceBooking.getPreviousBooking().getFlight().getReferenceNumber(),Flight.class);

		/**
		 * PUT request to Hotel Service
		 */

		// Hotel hotel = new Hotel();
		ClientChoice clientChoiceOfHotel = new ClientChoice();
		clientChoiceOfHotel.setReferenceNumber(replaceBooking.getNewChoiceOfBooking().getHotelReferenceNumber());
		HttpEntity<ClientChoice> requestHotel = new HttpEntity<>(clientChoiceOfHotel);
		restTemplate.put("http://hotels-service/hotelservice/hotels/"+replaceBooking.getPreviousBooking().getHotel().getReferenceNumber(),requestHotel);
		Hotel hotel = restTemplate.getForObject("http://hotels-service/hotelservice/hotels/"+replaceBooking.getPreviousBooking().getHotel().getReferenceNumber(), Hotel.class);

		/**
		 * PUT request to Activity Service (client is only permitted to change one activity for the current implementation)
		 * Value at index 0 of clientResponse.activitiesReferenceNumber will be the old choice & value at index 1 will be the new choice
		 */
		ActivityItem newChoiceOfActivity = new ActivityItem();
		ClientChoices clientChoicesOfActivities = new ClientChoices();
		clientChoicesOfActivities.setReferenceNumbers(replaceBooking.getNewChoiceOfBooking().getActivitiesReferenceNumber());
		HttpEntity<ClientChoices> requestActivities = new HttpEntity<>(clientChoicesOfActivities);
		restTemplate.put("http://activities-service/activityservice/activities/"+replaceBooking.getNewChoiceOfBooking().getActivitiesReferenceNumber()[0],requestActivities);
		newChoiceOfActivity = restTemplate.getForObject("http://activities-service/activityservice/activities/"+replaceBooking.getNewChoiceOfBooking().getActivitiesReferenceNumber()[0], ActivityItem.class);

		
		/**
		 * PUT request to Attraction Service (client is only permitted to change one attraction for current implementation)
		 * Value at index 0 of clientResponse.attractionsReferenceNumber will be the old choice & value at index 1 will be the new choice
		 */

		ClientChoices clientChoicesOfAttractions = new ClientChoices();
		clientChoicesOfAttractions.setReferenceNumbers(replaceBooking.getNewChoiceOfBooking().getAttractionsReferenceNumber());
		HttpEntity<ClientChoices> requestAttractions = new HttpEntity<>(clientChoicesOfAttractions);
		restTemplate.put("http://attractions-service/attractionservice/attractions/"+replaceBooking.getNewChoiceOfBooking().getAttractionsReferenceNumber()[0],requestAttractions);
		Attraction newChoiceOfAttraction = restTemplate.getForObject("http://attractions-service/attractionservice/attractions/"+replaceBooking.getNewChoiceOfBooking().getAttractionsReferenceNumber()[0], Attraction.class);

		/**
		 * Create a new Booking to replace the old booking 
		 */

		Booking previousBooking = clientBookings.remove(referenceNumber);
		Booking newBooking = new Booking();
		newBooking.setFlight(flight);
		newBooking.setHotel(hotel);

		/**
		 * Replace old activity with new one and add this set of activities to the new booking
		 */
		// ActivityItem [] newSetOfActivities = new ActivityItem[previousBooking.getActivities().length];
		// System.out.println(newSetOfActivities.length);
		// newSetOfActivities = findActivityToReplace(previousBooking.getActivities(), newChoiceOfActivity);
		// newBooking.setActivities(newSetOfActivities);

		/**
		 * Replace old attraction with new one and add this set of attractions to the new booking
		 */
		// Attraction [] newSetOfAttractions = new Attraction[previousBooking.getAttractions().length];
		// newSetOfAttractions = findAttractionToReplace(previousBooking.getAttractions(), newChoiceOfAttraction);
		// newBooking.setAttractions(newSetOfAttractions);
		
		newBooking.setReferenceNumber(referenceNumber);   // give new booking the ref num of the old booking
		clientBookings.put(referenceNumber,newBooking);

		String path = ServletUriComponentsBuilder.fromCurrentContextPath().
			build().toUriString()+ "/travelagent/bookings/"+clientBookingReferenceNumber;  // Create new URI for this newly created travel booking
		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-Location", path);
		return new ResponseEntity<>(headers, HttpStatus.OK);
    }


    /**
     * The following method replaces the old activity with the new one
     * @param activities
     * @param newChoiceOfActivity
     * @return
     */

    public ActivityItem[] findActivityToReplace(ActivityItem[] activities, ActivityItem newChoiceOfActivity){

	   for (ActivityItem activityItem : activities){

		// if(!(activityItem.equals(null))){

		// 	if (activityItem.getReferenceNumber() == newChoiceOfActivity.getReferenceNumber()){

		// 		activityItem = newChoiceOfActivity;
		// 	}
		// }
		System.out.println(activityItem.equals(null));
		
	   }

	   return activities;
    }

    /**
     * The following method replaces the old attraction with the new one
     * @param attractions
     * @param newChoiceOfAttractions
     * @return
     */

    public Attraction[] findAttractionToReplace(Attraction[] attractions, Attraction newChoiceOfAttraction){

	for (Attraction attraction : attractions){

	   if (attraction.getReferenceNumber() == newChoiceOfAttraction.getReferenceNumber()){

		   attraction = newChoiceOfAttraction;
	   }
	}

	return attractions;
 }

 public void addFlightsToSearchFlightsMap(Flight [] flights){

	for (Flight flight : flights){

		searchedFlights.put(flight.getReferenceNumber(),flight);          // add new flight to map with new ref number
	}
 }

 public void addHotelsToSearchHotelsMap(Hotel [] hotels){

	for (Hotel hotel : hotels){												  // with the client choice of hotel booking
			  searchedHotels.put(hotel.getReferenceNumber(), hotel);          // add new hotel to map with new ref number
	}
 }
 public void addActivitiesToSearchActivitiesMap(ActivityItem [] activities){

	for (ActivityItem activity : activities){        // set the ref number in Activity so that we can cross reference 
																				// with the client choice of activity booking
		searchedActivities.put(activity.getReferenceNumber(),activity);          // add new activity to map with new ref number
	}
 }
 public void addAttractionsToSearchAttractionsMap(Attraction [] attractions){

	for (Attraction attraction : attractions){
																					// with the client choice of attraction booking
		searchedAttractions.put(attraction.getReferenceNumber(),attraction);          // add new attraction to map with new ref number
	}
 }
	// /**
	//  * DELETE REQUEST
	//  * 
	//  * @param referenceNumber
	//  */
	// @RequestMapping(value="/bookings/{referenceNumber}", method=RequestMethod.DELETE)
	// public @ResponseBody String deleteBooking(@PathVariable int referenceNumber) {
	// 	Booking clientBooking = clientBookings.remove(referenceNumber);
	// 	if (clientBooking == null) throw new NoSuchBookingException();
		
	// 	return "redirect:/";
	// }


	// If there is no booking listed with the given reference then throw this exception
	@ResponseStatus(value = HttpStatus.NOT_FOUND)
	public class NoSuchBookingException extends RuntimeException {
		static final long serialVersionUID = -6516152229878843037L;
	}

}
