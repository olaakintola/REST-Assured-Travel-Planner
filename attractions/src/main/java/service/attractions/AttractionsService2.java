package service.attractions;

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

import com.amadeus.Amadeus;
import com.amadeus.Params;
import com.amadeus.exceptions.ResponseException;
import com.amadeus.resources.PointOfInterest;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import service.core.Attraction;
import service.core.Geocode;
import service.core.AttractionRequest;
import service.core.ClientChoices;
import service.core.ClientChoice;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import service.core.ClientChoices;


@RestController
public class AttractionsService2 {

    @Autowired
	private RestTemplate restTemplate;


    private static final Amadeus amadeus= Amadeus.builder( "3qFG1Vf9IQTAvMAFQUUAXZeJbE8KAAjm",
            "f9qzap835Rv0PtCg").build();
    private static final String PAGE = "/attractions";
    private static final String QUERY_REGEX = "^([a-z\\u0080-\\u024F]+(?:. |-| |'))*[a-z\\u0080-\\u024F]*$";
    private static final Pattern QUERY_PATTERN_CHECKER = Pattern.compile(QUERY_REGEX,Pattern.CASE_INSENSITIVE);
    private static final String STATUS_CODE_ERROR = "Wrong status code: ";
    private static final String EMPTY_RECOMMENDATION = "No recommendation found / Location not supported";

    private static int attractionRequestReferenceNumber = 0;    // unique reference number for each attractionRequest
	private static int searchedAttractionReferenceNumber = 0;           // unique reference number for each array of attractions found by AttractionService that matches requirements from attractionRequest
	private static int bookedAttractionReferenceNumber = 0;           // unique reference number for each attraction list booked by a client
	private Map<Integer, Attraction> bookedAttractions = new HashMap<>();      // Map of all attractions created with new reference number as key
    private Map<Integer, Attraction> searchedAttractions = new HashMap<>();    // Map of all attractions that AttractionsService searched for


     /**
	 * POST REQUEST: handles all attraction requests from travel agent
	 * 
	 * @param flightRequest
	 * @return attractions
	 * @throws URISyntaxException
	 */

	@RequestMapping(value="/attractionservice/attractionrequests",method=RequestMethod.POST)
	public ResponseEntity<Attraction []> searchAttractions(@RequestBody AttractionRequest attractionRequest)  throws URISyntaxException {

        System.out.println("\nTesting AttractionService POST Request\n");

        Attraction[] attractions = getAttractionsWithQueries(attractionRequest.getCity(), attractionRequest.getCountry());
        addAttractionsToSearchAttractionsMap(attractions);

        /** 
		 * The following code prints all attractions which were found through the Amadeus API
		 */
        

        System.out.println("\nATTRACTIONS\n");
        //  for (int i=0; i<3; i++){
            
        //         System.out.println("\n"+attractions[i].toString()+"\n");
            
        //  }
         
         /**
          * TODO: Important
          */
    
          

		attractionRequestReferenceNumber++;

		String path = ServletUriComponentsBuilder.fromCurrentContextPath().
			build().toUriString()+ "/attractionservice/attractionrequests/"+attractionRequestReferenceNumber;     // Create URI for this attraction
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(new URI(path));
		return new ResponseEntity<>(attractions, headers, HttpStatus.CREATED);     // Returns attractions to travel agent
    }


    /**
	 * POST REQUEST: Once the client has chosen their attractions then these choices will be passed on to TravelAgentService which
	 * will then pass them on to this method
	 * 
	 * @param clientChoiceOfActivities
	 * @return attractions
	 */

	@RequestMapping(value="/attractionservice/attractions",method=RequestMethod.POST)
	public ResponseEntity<Attraction> createAttraction(@RequestBody ClientChoices clientChoicesOfAttractions)  throws URISyntaxException {

        System.out.println("\nTESTING ATTraction POST BOOKING)");
		Attraction attraction = searchedAttractions.get(clientChoicesOfAttractions.getReferenceNumbers()[0]);        // find attraction the client wishes to book
        Attraction[] attractions = new Attraction[1];
        attractions[0] = attraction;
		System.out.println("\nTesting /attractionservice/attractions\n");
		// System.out.println(attraction.toString());
		
		// Add a new attraction for this client to bookedAttractions map (which contains booked attractions for all clients)
		bookedAttractionReferenceNumber++;
		bookedAttractions.put(bookedAttractionReferenceNumber,attraction);

		String path = ServletUriComponentsBuilder.fromCurrentContextPath().
			build().toUriString()+ "/attractionservice/attractions/"+bookedAttractionReferenceNumber;     // Create URI for this attraction
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(new URI(path));
		return new ResponseEntity<>(attraction, headers, HttpStatus.CREATED);     // Returns attraction to travel agent
    }
    
    
    /**
	 * GET REQUEST
	 * 
	 * @param reference
	 * @return attraction
	 */
	@RequestMapping(value="/attractionservice/attractions/{reference}",method=RequestMethod.GET)
	public Attraction getAttraction(@PathVariable("reference") int reference) {

		Attraction attraction = bookedAttractions.get(reference);
		if (attraction == null) throw new NoSuchAttractionException();
		return attraction;
	}

	/**
	 * GET REQUEST (all instances)
	 * 
	 * @return bookedAttractions.values()
	 */
	@RequestMapping(value="/activityservice/activities",method=RequestMethod.GET)
	public @ResponseBody Collection<Attraction> listEntries() {

		if (bookedAttractions.size() == 0) throw new NoSuchAttractionException();
		return bookedAttractions.values();
	}

	/**
	 * PUT REQUEST: replace attraction with given reference number
     * A limitiation of this method is that we can only change one attraction per call
	 * 
	 * @param referenceNumber
	 * @param clientChoices
	 * @throws URISyntaxException
	 */

	@RequestMapping(value="/attractionservice/attractions/{referenceNumber}", method=RequestMethod.PUT)
    	public ResponseEntity<Attraction> replaceAttraction(@PathVariable int referenceNumber, @RequestBody ClientChoices clientChoices) throws URISyntaxException{

		Attraction newChoiceOfAttraction = searchedAttractions.get(clientChoices.getReferenceNumbers()[1]);        // find attraction the client wishes to book

		System.out.println("\nTesting PUT /attractionservice/attractions\n");
		System.out.println(newChoiceOfAttraction.toString());
		
		// Replace old attraction with a new attraction
		Attraction previouslyBookedAttractioin = bookedAttractions.remove(referenceNumber);
        bookedAttractions.put(referenceNumber,newChoiceOfAttraction);

		String path = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString()+ "/attractionservice/attractions"+referenceNumber;
		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-Location", path);
		return new ResponseEntity<>(headers, HttpStatus.OK);
    }


    /**
     * Method to validate the user string query, to prevent unwanted characters used<br/>
     * Reference : https://stackoverflow.com/questions/11757013/regular-expressions-for-city-name
     * @param query user query in string
     * @return boolean value shows if the given user query valid
     */
    public boolean isValidLocationName(String query) {
        if(query.isBlank()) return false;
        Matcher matcher = QUERY_PATTERN_CHECKER.matcher(query);
        return matcher.find();
    }

    /**
     * Method to get the geo-coordinate of a given city and country query with Nominatim REST API<br/>
     * Reference : https://nominatim.org/
     * @param city      Full city name in string, eg. Dublin instead of DUB
     * @param country   Full country name in string, eg. Ireland instead of IRE
     * @return A Geocode object, and null if the parameter(s) are invalid or processing issue
     */
    public Geocode getDestinationGeocode(String city, String country) {

        System.out.println("LINE 134");
        if(!isValidLocationName(city) || !isValidLocationName(country)) {
            System.out.println("Invalid City or Country Queries (City : '" + city + "', Country : '" + country + "')");
            return null;
        }

        Scanner sc = null;

        try {
            URL url = new URL("https://nominatim.openstreetmap.org/" +
                    "search?city=" + city + "&country=" + country + "&format=json");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();
            int responseCode = conn.getResponseCode();

            if (responseCode != 200) throw new RuntimeException("Unexpected HTTP Response Code: " + responseCode);
            else {
                sc = new Scanner(url.openStream());
                StringBuilder raw = new StringBuilder();
                while (sc.hasNext()) {
                    raw.append(sc.nextLine());
                }
                JSONParser parser = new JSONParser();
                JSONArray jsonObject = (JSONArray) parser.parse(String.valueOf(raw));

                JSONObject first = (JSONObject) jsonObject.get(0);
                double longitude = Double.parseDouble((String) first.get("lon"));
                double latitude = Double.parseDouble((String) first.get("lat"));

                return new Geocode(latitude, longitude);
            }
        } catch(IOException e) {
            System.out.println(e.toString());
        } catch (IndexOutOfBoundsException e) {
            System.out.println("Invalid location query given (City : '" + city + "', Country : '" + country + "')");
        } catch (ParseException e) {
            System.out.println("Unrecognized JSON pattern found in JSONString from the API");
            System.out.println(e.toString());
        } finally {
            assert sc != null;
            sc.close();
        }
        return null;
    }

    /**
     * Method to retrieve a list of attractions available in given destination with its latitude and longitude
     * @param latitude Double value in range of -90 to 90
     * @param longitude Double value in range of -90 to 90
     * @return list of attractions in a given destination, empty if the given location is unavailable or no attractions can be found
     */
    // @GetMapping(value = PAGE)
    public PointOfInterest[] getAttractions(double latitude, double longitude) {
        if(Math.abs(latitude) > 90 || Math.abs(longitude) > 180) {
            System.out.println("Invalid coordinate(s) given (Lat=" + latitude + ", Lon=" + longitude + ")");
            return new PointOfInterest[0];
        }

        try {
            PointOfInterest[] pois = amadeus.referenceData.locations.pointsOfInterest.get(Params
                    .with("latitude", Double.toString(latitude))
                    .and("longitude", Double.toString(longitude)));

            if(pois[0]!=null) {
                if(pois[0].getResponse().getStatusCode() != 200) {
                    System.out.println(STATUS_CODE_ERROR + pois[0].getResponse().getStatusCode());
                }
            } else {
                System.out.println(EMPTY_RECOMMENDATION);
            }

            return pois;


        } catch(ResponseException e) {
            System.out.println("Error " + e.getCode() + " : " + e.getDescription());
        }

        return new PointOfInterest[0];
    }

    /**
     * Method to retrieve a list of attraction available in given destination with the city and country full name
     * @param city Full city name in string, eg. Dublin instead of DUB
     * @param country Full country name in string, eg. Ireland instead of IRE
     * @return list of attractions in given destination, empty if the given location is unavailable or no attraction can be found
     */
    public Attraction[] getAttractionsWithQueries(String city, String country) {
        System.out.println("LINE 222");
        System.out.println("CITY = "+city);
        System.out.println("COUNTRY = "+country);
        Geocode destination = getDestinationGeocode(city, country);
        if(destination == null) {
            System.out.println("Invalid destination (" + city + ", " + country + ")");
            return new Attraction[0];
        }
        PointOfInterest[] activities = getAttractions(destination.getLatitude(), destination.getLongitude());
        System.out.println("LINE 228");
        System.out.println("POI size = "+activities.length);
        if(activities == null) {
            System.out.println("No activity found in (" + city + ", " + country + ")");
            return new Attraction[0];
        }
        LinkedList<Attraction> translated = new LinkedList<>();
        for(PointOfInterest pointOfInterest : activities) {
            translated.add(toCoreAttraction(pointOfInterest));
        }
        return listToArray(translated);
    }

    public Attraction toCoreAttraction(PointOfInterest poi) {
        return new Attraction(poi.getName(), poi.getCategory(), poi.getType(), poi.getSubType());
    }

    public Attraction[] listToArray(List<Attraction> activities) {

        Attraction[] attractionsArray = new Attraction[activities.size()];
        int index = 0;
        while (index < activities.size()){
            attractionsArray[index] = activities.get(index);
            index++;
        }

        return attractionsArray;
    }

      /**
	 * The following method adds all attractions found by AttractionsService to searchedAttractions map
	 */

	 public Attraction [] addAttractionsToSearchAttractionsMap(Attraction [] attractions){

		for (Attraction attraction : attractions){

			searchedAttractionReferenceNumber++; 
			attraction.setReferenceNumber(searchedAttractionReferenceNumber);           // set the ref number in Attraction so that we can cross reference 
                                                                                        // with the client choice of attraction booking
            searchedAttractions.put(searchedAttractionReferenceNumber,attraction);          // add new attraction to map with new ref number
		}
		return attractions;
	 }


}
