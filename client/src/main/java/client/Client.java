package client;

import java.text.NumberFormat;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;

import org.springframework.web.client.RestTemplate; 
import org.springframework.http.HttpEntity;
 
import service.core.FlightRequest;
import service.core.Flight;
import service.core.Hotel;
import service.core.Booking;
import service.core.Attraction;
import service.core.ActivityRequest;
import service.core.ActivityItem;
import service.core.AttractionRequest;
import service.core.ClientRequest;
import service.core.ClientResponse;
import service.core.HotelRequest;
import service.core.TravelPackage;
import service.core.MongoBooking;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException; 
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.io.IOException;

public class Client {
	
	// Use ip address found through "minikube ip" in format"IP:31500" to access kubernetes travel_agent
	public static final String argsRequest = "http://192.168.49.2:31500/travelagent/travelpackagerequests";
	public static final String argsResponse = "http://192.168.49.2:31500/travelagent/bookings";

	// public static final String argsRequest = "http://localhost:8081/travelagent/travelpackagerequests";
	// public static final String argsResponse = "http://localhost:8081/travelagent/bookings";

	public static int referenceNumber = 0;

	final static String locale = "en-GB";     // need to call Skycanner API
	
	// public static void main(String[] args) {

		public static TravelPackage sendBookingToTravelAgent(FlightRequest flightRequest, HotelRequest hotelRequest, ActivityRequest activityRequest, AttractionRequest attractionRequest) {	
 		
			    	Flight[] flights = new Flight[10];
			 		RestTemplate restTemplate = new RestTemplate();
			 		ClientRequest clientRequest = new ClientRequest();
			 		clientRequest.setFlightRequest(flightRequest);
					 clientRequest.setHotelRequest(hotelRequest);
					 clientRequest.setActivityRequest(activityRequest);
					 clientRequest.setAttractionRequest(attractionRequest);
			 //		Uncomment this after testing my Code to get same result as Barry
				/*
				 * referenceNumber++; clientRequest.setReferenceNumber(referenceNumber);
				 */
				System.out.println("TEST1");
			
							   HttpEntity<ClientRequest> request = new HttpEntity<>(clientRequest);
							   System.out.println("TEST2");
							  
							   TravelPackage travelPackage = new TravelPackage();
							   System.out.println("TEST3");
							   travelPackage = restTemplate.postForObject(argsRequest,request,TravelPackage.class);
							   System.out.println("TEST4");

							   ActivityItem [] activities2 = travelPackage.getActivities();
							   ActivityItem a = activities2[0];
							   System.out.println("\n");
								if (a != null){
									System.out.println("Description of activity is: " + a.getDescription());
									System.out.println("Booking link of activity is: " + a.getBookingLink());
								}
								Attraction [] attractions2 = travelPackage.getAttractions();
								Attraction at = attractions2[0];
								if (at != null){
									System.out.println("Category of attraction is: " + at.getCategory());
									System.out.println("Name of attraction is: " + at.getName());
								}
			
			                   Hotel [] hotels2 = travelPackage.getHotels();
							   Flight [] flights2 = travelPackage.getFlights();
						 return travelPackage;
						}

	//Send the clientResponse to the travel agent
	public static Booking sendBookingChoicesToTravelAgent(ClientResponse clientResponse){

		HttpEntity<ClientResponse> requestClientResponse = new HttpEntity<>(clientResponse);
		Booking booking = new Booking();
		RestTemplate restTemplate = new RestTemplate();
            booking = restTemplate.postForObject(argsResponse,requestClientResponse,Booking.class);
			return booking;
                  
	}

	public static MongoBooking getBookingFromTravelAgent(String inputBookingReference){
		RestTemplate restTemplate = new RestTemplate();

		// Use if running Spring Boot/Docker Compose:
		// MongoBooking getBooking = restTemplate.getForObject("http://localhost:8081/travelagent/bookings/"+inputBookingReference, MongoBooking.class);

		// Use for Kubernetes
		MongoBooking getBooking = restTemplate.getForObject("http://192.168.49.2:31500/travelagent/bookings/"+inputBookingReference, MongoBooking.class);
		return getBooking;
        // System.out.println("\nGET TEST: "+getBooking.getFlight().getAirline()+"\n");
	}
}
