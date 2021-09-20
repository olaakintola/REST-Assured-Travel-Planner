package service.hotels;

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

import service.core.FlightRequest;
import service.core.Hotel;
import service.hotels.NoSuchHotelException;
import service.core.HotelRequest;
import service.core.ClientChoice;

import java.util.Iterator;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;

@RestController
public class HotelService2 {

      @Autowired
	@LoadBalanced
	private RestTemplate restTemplate;

      private static int hotelRequestReferenceNumber = 0;    // unique reference number for each hotelRequest
	private static int searchedHotelReferenceNumber = 0;           // unique reference number for each hotel found by HotelService that matches requirements from flightRequest
	private static int bookedHotelReferenceNumber = 0;           // unique reference number for each hotel booked by a client
	private Map<Integer, Hotel> bookedHotels = new HashMap<>();      // Map of all hotels created with new reference number as key
	private Map<Integer, Hotel> searchedHotels = new HashMap<>();    // Map of all hotels that HotelService searched for

	/**
       * POST REQUEST: takes requests from TravelAgentService. Handles all hotel requests from travel agent. TravelAgentService sends a hotelRequest object specifying
	 * the user requirements and then uses this info to search for hotels
       * 
       * @param hotelRequest
       * @return
       * @throws URISyntaxException
       */

	@RequestMapping(value="/hotelservice/hotelrequests",method=RequestMethod.POST)
	public ResponseEntity<Hotel[]> getHotelInfo(@RequestBody HotelRequest hotelRequest)  throws URISyntaxException {
		
		ArrayList<Hotel> clientHotels = new ArrayList<>();
            clientHotels = findHotels(hotelRequest);
            
            /**
             * Converts Hotel ArrayList to Hotel array as it is not possible to send a list so it must be converted 
             */

            Hotel [] hotelsArray = convertHotelListToHotelArray(clientHotels);

            /**
             * The following code prints all hotels which were found through the Amadeus API
             */

            for (Hotel hotel : hotelsArray){
                  System.out.println(hotel.toString());
            }

            hotelsArray = addHotelsToSearchHotelsMap(hotelsArray);

		hotelRequestReferenceNumber++;               
		String path = ServletUriComponentsBuilder.fromCurrentContextPath().
			build().toUriString()+ "/hotelservice/hotelrequests/"+hotelRequestReferenceNumber;     // Create URI for this hotel request
		HttpHeaders headers = new HttpHeaders();
            headers.setLocation(new URI(path));    
            
		return new ResponseEntity<>(hotelsArray, headers, HttpStatus.CREATED);     // Returns hotels to travel agent
      } 

      /**
       * POST REQUEST: Once the client has chosen a hotel then this choice will be passed on to TravelAgentService which
	 * will then pass it on to this method
       * 
       * @param clientChoice
       * @return hotel
       * @throws URISyntaxException
       */
      @RequestMapping(value="/hotelservice/hotels",method=RequestMethod.POST)
	public ResponseEntity<Hotel> createHotel(@RequestBody ClientChoice clientChoice)  throws URISyntaxException {

            Hotel hotel = searchedHotels.get(clientChoice.getReferenceNumber());        // find hotel the client wishes to book
            
		// Add a new hotel for this client to bookedHotels map (which contains booked hotels for all clients)
		bookedHotelReferenceNumber++;
		bookedHotels.put(bookedHotelReferenceNumber,hotel);

		String path = ServletUriComponentsBuilder.fromCurrentContextPath().
			build().toUriString()+ "/hotelservice/hotels/"+bookedHotelReferenceNumber;     // Create URI for this hotel
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(new URI(path));
		return new ResponseEntity<>(hotel, headers, HttpStatus.CREATED);     // Returns hotels to travel agent
      }


      /**
	 * GET REQUEST
	 * 
	 * @param reference
	 * @return hotel
	 */
	@RequestMapping(value="/hotelservice/hotels/{reference}",method=RequestMethod.GET)
	public Hotel getHotel(@PathVariable("reference") int reference) {

		Hotel hotel = bookedHotels.get(reference);
		if (hotel == null) throw new NoSuchHotelException();
		return hotel;
	}

	/**
	 * GET REQUEST (all instances)
	 * 
	 * @return hotels.values()
	 */
	@RequestMapping(value="/hotelservice/hotels",method=RequestMethod.GET)
	public @ResponseBody Collection<Hotel> listEntries() {

		if (bookedHotels.size() == 0) throw new NoSuchHotelException();
		return bookedHotels.values();
	}


      
      /**
	 * PUT REQUEST: replaces hotel with given reference number. New choice of hotel is specified in clientChoice which
       * has an instance variable that holds the reference number of the new hotel
	 * 
	 * @param referenceNumber
	 * @param clientChoice
	 * @return newChoiceOfHotel
	 * @throws URISyntaxException
	 */

	@RequestMapping(value="/hotelservice/hotels/{referenceNumber}", method=RequestMethod.PUT)
      public ResponseEntity<Hotel> replaceHotel(@PathVariable int referenceNumber, @RequestBody ClientChoice clientChoice) throws URISyntaxException{

        Hotel newChoiceOfHotel = searchedHotels.get(clientChoice.getReferenceNumber());        // find hotel the client wishes to book

        // Replace old hotel with a new hotel
        Hotel previouslyBookedHotel = bookedHotels.remove(referenceNumber);
        bookedHotels.put(referenceNumber,newChoiceOfHotel);

        String path = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString()+ "/hotelservice/hotels/"+referenceNumber;
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Location", path);
        return new ResponseEntity<>(headers, HttpStatus.OK);
      }

      /**
       * The following code converts a list of hotels to an array of hotels
       * 
       * @param hotelList
       * @return hotelsArray
       */

      public Hotel[] convertHotelListToHotelArray(ArrayList<Hotel> hotelList){

            Hotel [] hotelsArray = new Hotel[hotelList.size()];
            int index = 0;
            
            while (index < hotelList.size()){
                  hotelsArray[index] = hotelList.get(index);
                  index++;
            }
            return hotelsArray;
      }
      

      /**
       * The following method finds hotels in the given location by calling GET FindHotels from the Amadeus API collection
       * 
       * @param hotelRequest
       * @return hotelList
       */

	public ArrayList<Hotel> findHotels(HotelRequest hotelRequest){

            ArrayList<Hotel> hotelList = new ArrayList<>();
		try{   // request made to Amadeus API to get available hotel info
                  HttpRequest request = HttpRequest.newBuilder()
                  .uri(URI.create("https://test.api.amadeus.com/v2/shopping/hotel-offers?cityCode="+hotelRequest.getCityCode()+"&roomQuantity=1&adults="+hotelRequest.getNumberOfGuests()+"&radius=5&radiusUnit=KM&paymentPolicy=NONE&includeClosed=false&bestRateOnly=true&view=FULL&sort=NONE"))
                  .header("Authorization", "Bearer " + getToken())
                  .method("GET", HttpRequest.BodyPublishers.noBody())
                  .build();
                  HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
                 
                  JSONObject hotelInfo = new JSONObject();
                  hotelInfo = parseJSONObject(response.body());  // converts string to JSON object

                  JSONArray hotelInfoArray = new JSONArray();    
	    		hotelInfoArray = (JSONArray) hotelInfo.get("data");   // extracts 'data' array from JSON object

                  System.out.println(hotelInfoArray + "\n");
                  int index = 0;
                  
                  // If data is received from the Amadeus API call then extract the relevant hotel info and create a Hotel object
                  // This Hotel object is then sent back to travel agent
                  if (hotelInfoArray.size() > 0){
                        
                        while(index < hotelInfoArray.size()){

                              JSONObject jsonObject = (JSONObject) hotelInfoArray.get(index);   // create a new JSON object with the current index of the array
      
                              JSONObject hotel = (JSONObject) jsonObject.get("hotel");   // hotel holds some of the data we need to extract
                              System.out.println("hotel: "+hotel+"\n");
      
                              JSONArray offers = (JSONArray) jsonObject.get("offers"); // offers holds the rest of the data we need to extract
                              System.out.println("offers: "+offers +"\n");
                              jsonObject = (JSONObject) offers.get(0);
      
                              Hotel currentHotel = new Hotel();
                              
                              /**
                               * Finds price of room
                               */
                              JSONObject price = new JSONObject();
                              if ( !(jsonObject.get("price").equals(null)) ){

                                  price = (JSONObject) jsonObject.get("price");
                                  currentHotel.setPrice(String.valueOf(price.get("total")));
                              }
                             
                              /**
                               * Finds Room Type and Bed Type
                               */
                              JSONObject room = (JSONObject) jsonObject.get("room");
                              Object typeEstimated = room.get("typeEstimated");
                              String typeEstimatedString = String.valueOf(typeEstimated);
                              JSONObject typeEstimatedJSON = parseJSONObject(typeEstimatedString);
                              // System.out.println("Room Type: " + typeEstimatedJSON.get("category"));
                              // System.out.println("Bed Type: " + typeEstimatedJSON.get("bedType"));
                              currentHotel.setRoomType(String.valueOf(typeEstimatedJSON.get("category")));
                              currentHotel.setBedType(String.valueOf(typeEstimatedJSON.get("bedType")));
      
                              /**
                               * Finds description of room
                               */
                              Object description = room.get("description");
                              String descriptionString = String.valueOf(description);
                              JSONObject descriptionJSON = parseJSONObject(descriptionString);
                              // System.out.println("Description: " + descriptionJSON.get("text") + "\n");
                              currentHotel.setDescription(String.valueOf(descriptionJSON.get("text")));
      
                              /**
                               * Finds address of hotel
                               */
                              JSONObject address = (JSONObject) hotel.get("address");
                              JSONArray addressArray = (JSONArray) address.get("lines");
                              // System.out.println(addressArray.get(0));
                              currentHotel.setAddress(String.valueOf(addressArray.get(0)));
      
                              /**
                               * Finds hotel amenities
                               */
                              JSONArray amenities = (JSONArray) hotel.get("amenities");
                              int j = 0;
                              String [] amenitiesArray = new String[amenities.size()];
                              while(j < amenities.size()){
                                    // System.out.println(amenities.get(j));
                                    amenitiesArray[j] = String.valueOf(amenities.get(j));
                                    j++;
                              }
                              currentHotel.setAmenities(amenitiesArray);                        
      
                              /**
                               * Finds hotel rating 
                               */
                              currentHotel.setRating(String.valueOf(hotel.get("rating")));
      
                              /**
                               * Finds uri for hotel
                               */
                              // JSONArray media = (JSONArray) hotel.get("media");
                              // int k = 0;
                              // while (k < media.size()){
                              //       System.out.println(media.get(k));
                              //       k++;
                              // }
      
                              /**
                               * Finds phone number of hotel
                               */
                              JSONObject contact = (JSONObject) hotel.get("contact");
                              currentHotel.setPhoneNumber(String.valueOf(contact.get("phone")));
      
                              /**
                               * Finds name of hotel
                               */
                              currentHotel.setName(String.valueOf(hotel.get("name")));
      
                              hotelList.add(currentHotel);
                              index++;
                        }                         
                  }               
            }
            catch(IOException e) {
                  e.printStackTrace();
            }
            catch(InterruptedException e) {
                  e.printStackTrace();
            }
		return hotelList;
      }
	
      // If there is no hotel listed with the given reference then throw this exception
      @ResponseStatus(value = HttpStatus.NOT_FOUND)
      public class NoSuchHotelException extends RuntimeException {
            static final long serialVersionUID = -6516152229878843037L;
      } 

      /**
       * The following method converts a string to a JSON Object
       * 
       * @param response
       * @return
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
	 * The following method acquires an access token. An access token is always needed to make a call to Amadeus hotel api 
	 */

	public String getToken(){

		String token = "";  // holds our token to gain access to subsequent api calls
		try {

                  List<String> tokenCommand = new ArrayList<String>();         // list to hold CURL command which call API for token

                  tokenCommand.add("curl");
                  tokenCommand.add("https://test.api.amadeus.com/v1/security/oauth2/token");
                  tokenCommand.add("-H");
                  tokenCommand.add("Content-Type: application/x-www-form-urlencoded");
                  tokenCommand.add("-d");
                  tokenCommand.add("grant_type=client_credentials&client_id=u7gTwvqxHbRyEUbKASMPfdTaHfVFPY7k&client_secret=y1NpUer8LSzWvwNc");

                  ProcessBuilder processBuilder = new ProcessBuilder(tokenCommand);
                  processBuilder.redirectErrorStream(true);
                  Process process = processBuilder.start();  

                  BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));

                  String line = "";
                  List<String> response = new ArrayList();   // holds response of curl command taken through bufferedReader
                  int i = 0;      // tells us when we can start adding lines to response as the first few lines are meaningless
                  while ((line = bufferedReader.readLine()) != null) {
                  i++;
                        if(i>8 && i<15 ){
                              response.add(line.trim());
                        }
                  }

                  for(String s : response){
                        if (s.substring(1,7).equals("access")){   // when we find "access" then we know our token will follow
                              token = s.substring(17,s.length()-2);  // remove unnecessary characters to leave only the required token
                        }
                  }
                  System.out.println("Final token: " + token);
                  // token = "YyfL69mLQ0pmdtwOmFRwMmA9jAcn";
            } catch(IOException e) {
                  e.printStackTrace();
		}
		return token;
      }
      
      /**
	 * The following method adds all new hotels found by HotelService to searchedHotels map. Each hotel object is given 
       * a unique reference number which is also the key for the object itself. The client can choose the ref number associated 
       * with the hotel he/she would like to book and HotelService will cross reference this number with the one stored in
       * searchedHotels map.
	 */

	 public Hotel [] addHotelsToSearchHotelsMap(Hotel [] hotels){

		for (Hotel hotel : hotels){

			searchedHotelReferenceNumber++; 
			hotel.setReferenceNumber(searchedHotelReferenceNumber);           // set the ref number in hotel so that we can cross reference 
                                                                                      // with the client choice of hotel booking
                  searchedHotels.put(searchedHotelReferenceNumber,hotel);          // add new hotel to map with new ref number
		}
		return hotels;
	 }
	

}
