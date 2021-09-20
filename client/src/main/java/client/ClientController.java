package client;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.io.File;
import java.io.FileNotFoundException;
import javax.servlet.http.HttpServletResponse;

import service.core.ActivityRequest;
import service.core.ActivityItem;
import service.core.Attraction;
import service.core.AttractionRequest;
import service.core.ClientResponse;
import service.core.FlightRequest;
import service.core.HotelRequest;
import service.core.TravelPackage;
import service.core.Booking;
import client.Client;
import service.core.MongoBooking;
import service.core.ClientResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

// import org.json.simple.*;

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





/**
 * Implementation of the AuldFellas insurance quotation service.
 * 
 * @author Rem
 *
 */
@Controller
public class ClientController { 
	private HashMap<String, String> cityCodes = new HashMap<String, String>();
	private String[] attractionCities = new String[7];
    private FlightRequest flightRequest = new FlightRequest();
	private HotelRequest hotelRequest = new HotelRequest();
	private ActivityRequest activityRequest = new ActivityRequest();
	private AttractionRequest attractionRequest = new AttractionRequest();
	private TravelPackage tp = new TravelPackage();
	private ClientResponse cr = new ClientResponse();
	private MongoBooking userBooking = new MongoBooking();
	final static String locale = "en-GB";
//	@RequestMapping(value="/",method=RequestMethod.GET)
//	@ResponseBody 
	@GetMapping("/")
	public String greeting(){
		return "index.html";
	}
	
	@GetMapping("/hotels")
	public String hotelsForm(){
		return "hotels.html";
	}

	@RequestMapping(value="/home", method=RequestMethod.POST)
	public void home(String homeButton, HttpServletResponse response) throws IOException{
		response.sendRedirect("/");
	}
	
	@RequestMapping(value="/processFlightsForm",method=RequestMethod.POST)  
	public void processFlightsForm(String name, String cityOfOrigin, String countryOfOrigin, String cityOfDestination, String countryOfDestination, boolean oneWayTrip, String returnDate, String outboundDate, String currency, HttpServletResponse response) throws IOException {

		cityCodeGenerator();
			// ClientBooking[] clientArray = new ClientBooking[1] ;
			// ClientBooking clientBooking = new ClientBooking();
		
			String capOriginCountry = countryOfOrigin.substring(0, 1).toUpperCase() + countryOfOrigin.substring(1).toLowerCase();
			String capDestinaptionCountry = countryOfDestination.substring(0,1).toUpperCase() + countryOfDestination.substring(1).toLowerCase();
			System.out.println("caporigincountry is = "+capOriginCountry);
			System.out.println("capDestncountry is = "+capDestinaptionCountry);
			String originCountryCode = getListMarkets(capOriginCountry);
			String destinatonCountryCode = getListMarkets(capDestinaptionCountry);

			if(originCountryCode.isEmpty()){
				PrintWriter out = response.getWriter();
				out.println("<script>");
				out.println("alert('" + "invalid country of origin entered, enter again" + "');");
				out.println("window.location.replace('" + "/" + "');");
				out.println("</script>");
			}
			else{
				if(destinatonCountryCode.isEmpty()){
					PrintWriter out = response.getWriter();
					out.println("<script>");
					out.println("alert('" + "invalid country of destination entered, enter again" + "');");
					out.println("window.location.replace('" + "/" + "');");
					out.println("</script>");
				}
				else{
					if(cityCodes.get(cityOfOrigin.toLowerCase())==null){
						PrintWriter out = response.getWriter();
						out.println("<script>");
						out.println("alert('" + "invalid city of origin entered, enter again" + "');");
						out.println("window.location.replace('" + "/" + "');");
						out.println("</script>");
					}
					else{
						if(cityCodes.get(cityOfDestination.toLowerCase())==null){
							PrintWriter out = response.getWriter();
							out.println("<script>");
							out.println("alert('" + "invalid city of destination entered, enter again" + "');");
							out.println("window.location.replace('" + "/" + "');");
							out.println("</script>");
						}
						else{
							activityRequest.setCity(cityOfDestination.toLowerCase());
							if (Arrays.stream(attractionCities).anyMatch(cityOfDestination.toLowerCase()::equals)){
								System.out.println("IT MATCHEDDDD");
								attractionRequest.setCity(cityOfDestination.toLowerCase());
								attractionRequest.setCountry(capDestinaptionCountry);
							}
							else{
								System.out.println("IT DIDNT MATCHE");
							}
            				hotelRequest.setCityCode(cityCodes.get(cityOfDestination.toLowerCase()));
							activityRequest.setCountry(capDestinaptionCountry);

					ArrayList<String> originAirportIDs = new ArrayList();          // Holds all airports for the given origin city
					ArrayList<String> destinationAirportIDs = new ArrayList();      //Holds all airports for the given destination city

					flightRequest.setCountryOfOriginCode(originCountryCode);
					System.out.println(flightRequest.getCountryOfOriginCode());
					flightRequest.setCountryOfDestinationCode(destinatonCountryCode);
					System.out.println(flightRequest.getCountryOfDestinationCode());

					originAirportIDs = getListPlaces(cityOfOrigin, countryOfOrigin, originCountryCode, currency);
					String [] originAirportIDsArray = convertAirportIDsListToAirportIDsArray(originAirportIDs);    // converts list to array
					
					destinationAirportIDs = getListPlaces(cityOfDestination, countryOfDestination, destinatonCountryCode, currency); 
					String [] destAirportIDsArray = convertAirportIDsListToAirportIDsArray(destinationAirportIDs);    // converts list to array
					
					System.out.println("\n ORIGIN AIRPORT IDS: "+originAirportIDsArray+"\n");
					for(String s : originAirportIDsArray){
						System.out.println(s);
					}
					System.out.println("\n ORIGIN AIRPORT IDS: "+destAirportIDsArray+"\n");
					for(String s : destAirportIDsArray){
						System.out.println(s);
					}
					flightRequest.setOriginAirortIDs(originAirportIDsArray);
					flightRequest.setDestAirortIDs(destAirportIDsArray);
					flightRequest.setName(name);
					flightRequest.setCityOfOrigin(cityOfOrigin);
					flightRequest.setCountryOfOrigin(countryOfOrigin);
					flightRequest.setCityOfDestination(cityOfDestination);
					flightRequest.setCountryOfDestination(countryOfDestination);
					flightRequest.setOneWayTrip(oneWayTrip);
					flightRequest.setReturnDate(returnDate);
					flightRequest.setOutboundDate(outboundDate);
					flightRequest.setCurrency(currency);
            // clientArray[0] = clientBooking;
					response.sendRedirect("/hotels");
	}
}
				}
			}
		}
    
    private void cityCodeGenerator(){
		File file = new File("city_codes.txt");
		File file2 = new File("attraction_cities.txt");
        try{
            Scanner sc = new Scanner(file);
            while(sc.hasNextLine()) 
            {
                String line = sc.nextLine();
                int index = line.lastIndexOf(" ")+1;
                String cityCode = line.substring(index);
                String cityName = line.substring(0,index);
                cityCodes.put(cityName.toLowerCase().trim() , cityCode.trim() );
            }
        }   
        catch (FileNotFoundException e) {
            e.printStackTrace();
		}
		try{
			int i = 0;
            Scanner sc = new Scanner(file2);
            while(sc.hasNextLine()) 
            { 
                String line = sc.nextLine();
				line = line.trim().toLowerCase();
				attractionCities[i] = line;
				i++;
            }
        }   
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @RequestMapping(value="/processHotelsForm",method=RequestMethod.POST)
    public void processHotelsForm(String guests, String checkIn, String checkOut, String minRatings, HttpServletResponse response) throws IOException
    {

        System.out.println("minrating is "+minRatings);
            hotelRequest.setNumberOfGuests(Integer.parseInt(guests));
            
            int minNumOfStars = 0;
            switch (minRatings){
                case "oneStar":
                    minNumOfStars = 1;
                    break;
                case "twoStar":
                    minNumOfStars = 2;
                    break;
                case "threeStar":
                    minNumOfStars = 3;
                    break;
                case "fourStar":
                    minNumOfStars = 4;
                    break;
                case "fiveStar":
                    minNumOfStars = 5;
                    break;
            }
            hotelRequest.setMinNumberOfStarsRequiredForHotel(minNumOfStars);
            tp = Client.sendBookingToTravelAgent(flightRequest, hotelRequest, activityRequest, attractionRequest);
			response.sendRedirect("/displayFlights");
    }

	@RequestMapping(value="/userFlightSelection",method=RequestMethod.POST)
	public void userFlightSelection(String inputFlightIndex, HttpServletResponse response) throws IOException
	{
		boolean isNumeric = inputFlightIndex.chars().allMatch( Character::isDigit );
		if(!isNumeric){
			PrintWriter out = response.getWriter();
            out.println("<script>");
            out.println("alert('" + "invalid index entered, enter again" + "');");
            out.println("window.location.replace('" + "/displayFlights" + "');");
            out.println("</script>");
		}
		else{
			int i = Integer.parseInt(inputFlightIndex);
			if(i<0 || (i == 0 && tp.getFlights().length!=0) || (i!=0 && i>tp.getFlights().length)){
				PrintWriter out = response.getWriter();
            out.println("<script>");
            out.println("alert('" + "invalid index entered, enter again" + "');");
            out.println("window.location.replace('" + "/displayFlights" + "');");
            out.println("</script>");
			}
			else{
				System.out.println("chosen index is = "+i);
				if (i==0){
					response.sendRedirect("/");
				}
				else{
					cr.setFlightReferenceNumber(tp.getFlights()[i-1].getReferenceNumber());
					response.sendRedirect("/displayHotels");
				}
			}
		}
	}

	@RequestMapping(value="/userHotelSelection",method=RequestMethod.POST)
	public void userHotelSelection(String inputHotelIndex, HttpServletResponse response) throws IOException
	{
		boolean isNumeric = inputHotelIndex.chars().allMatch( Character::isDigit );
		if(!isNumeric){
			PrintWriter out = response.getWriter();
            out.println("<script>");
            out.println("alert('" + "invalid index entered, enter again" + "');");
            out.println("window.location.replace('" + "/displayHotels" + "');");
            out.println("</script>");
		}
		else{
			int i = Integer.parseInt(inputHotelIndex);
			if(i<0 || (i == 0 && tp.getHotels().length!=0) || (i!=0 && i>tp.getHotels().length)){
				PrintWriter out = response.getWriter();
            out.println("<script>");
            out.println("alert('" + "invalid index entered, enter again" + "');");
            out.println("window.location.replace('" + "/displayHotels" + "');");
            out.println("</script>");
			}
			else{
				System.out.println("chosen index is = "+i);
				if (i!=0){
					cr.setHotelReferenceNumber(tp.getHotels()[i-1].getReferenceNumber());
				}
				else{
					cr.setHotelReferenceNumber(-1);
				}
				response.sendRedirect("/displayActivities");
				// response.sendRedirect("/");
			}
		}
	}

	@RequestMapping(value="/userActivitiesSelection",method=RequestMethod.POST)
	public void userActivitiesSelection(String inputActivitiesIndex, HttpServletResponse response) throws IOException
	{
		System.out.println("CHOSEN STRING = "+inputActivitiesIndex);
		ArrayList<Integer> chosenActivities = new ArrayList<Integer>();
		boolean isNumeric=true;
		String[] splited = inputActivitiesIndex.split("\\s+");
		for(String s: splited){
			isNumeric = s.chars().allMatch( Character::isDigit );
			if (!isNumeric){
				break;
			}
		}
		if(!isNumeric){
			PrintWriter out = response.getWriter();
            out.println("<script>");
            out.println("alert('" + "invalid indexes entered, enter again" + "');");
            out.println("window.location.replace('" + "/displayActivities" + "');");
            out.println("</script>");
		}
		else{
			boolean checkerB=true;
			for(String s: splited){
			int i = Integer.parseInt(s);
			chosenActivities.add(i);
			if(i<0 || (i == 0 && tp.getActivities().length!=0) || (i!=0 && i>tp.getActivities().length)){
				checkerB = false;
				break;
			}
			}
			int [] chosenActivitiesRefs = new int[chosenActivities.size()];
			int index0=0;
			if(checkerB){
				if (chosenActivities.get(0)!=0){
					for(int in: chosenActivities){
						chosenActivitiesRefs[index0]=tp.getActivities()[in-1].getReferenceNumber();
						index0++;
					}
					
					cr.setActivitiesReferenceNumber(chosenActivitiesRefs);
				}
				else{
					chosenActivitiesRefs[0] = -1;
					cr.setActivitiesReferenceNumber(chosenActivitiesRefs);
				}
				response.sendRedirect("/displayAttractions");
			}
			else{
				PrintWriter out = response.getWriter();
            out.println("<script>");
            out.println("alert('" + "invalid indexes entered, enter again" + "');");
            out.println("window.location.replace('" + "/displayActivities" + "');");
            out.println("</script>");
			}
		}
	}

	@RequestMapping(value="/userAttractionsSelection",method=RequestMethod.POST)
	public void userAttractionsSelection(String inputAttractionsIndex, HttpServletResponse response) throws IOException
	{
		System.out.println("CHOSEN STRING = "+inputAttractionsIndex);
		ArrayList<Integer> chosenAttractions = new ArrayList<Integer>();
		boolean isNumeric=true;
		String[] splited = inputAttractionsIndex.split("\\s+");
		for(String s: splited){
			isNumeric = s.chars().allMatch( Character::isDigit );
			if (!isNumeric){
				break;
			}
		}
		if(!isNumeric){
			PrintWriter out = response.getWriter();
            out.println("<script>");
            out.println("alert('" + "invalid indexes entered, enter again" + "');");
            out.println("window.location.replace('" + "/displayAttractions" + "');");
            out.println("</script>");
		}
		else{
			boolean checkerB=true;
			for(String s: splited){
			int i = Integer.parseInt(s);
			chosenAttractions.add(i);
			if(i<0 || (i == 0 && tp.getAttractions()[0]!=null) || (i!=0 && i>tp.getAttractions().length)){
				checkerB = false;
				break;
			}
			}
			int [] chosenAttractionsRefs = new int[chosenAttractions.size()];
			int index0=0;
			if(checkerB){
				if (chosenAttractions.get(0)!=0){
					for(int in: chosenAttractions){
						chosenAttractionsRefs[index0]=tp.getAttractions()[in-1].getReferenceNumber();
						index0++;
					}
					cr.setAttractionsReferenceNumber(chosenAttractionsRefs);
				}
				else{
					chosenAttractionsRefs[0] = -1;
					cr.setAttractionsReferenceNumber(chosenAttractionsRefs);
				}
				cr.setTravelPackageReferenceNumber(tp.getTravelPackageReferenceNumber());
				PrintWriter out = response.getWriter();
				Booking bookingObj = Client.sendBookingChoicesToTravelAgent(cr);
				userBooking = convertBookingToMongoBooking(bookingObj);
            	out.println("<script>");
            	out.println("alert('" + "Booking Successful!! your Booking Reference ID is - " +bookingObj.getReferenceNumber()+ "');");
            	out.println("window.location.replace('" + "/reservation" + "');");
            	out.println("</script>");
			}
			else{
				PrintWriter out = response.getWriter();
            out.println("<script>");
            out.println("alert('" + "invalid indexes entered, enter again" + "');");
            out.println("window.location.replace('" + "/reservation" + "');");
            out.println("</script>");
			}
		}
	}

	public MongoBooking convertBookingToMongoBooking(Booking b){
		userBooking.setReferenceId(String.valueOf(b.getReferenceNumber()));
		userBooking.setFlightDetails(b.getFlight().toString());
		userBooking.setHotelDetails(b.getHotel().toString());

		String temp="";
		boolean bool = true;
		for(ActivityItem ai:b.getActivities()){
			if(ai!=null){
				bool = false;
				temp+= ai.toString();
				temp+="\n";
			}
		}
		if(bool){
			temp = "None";
		}
		userBooking.setActivitiesDetails(temp);

		String temp2="";
		boolean bool2 = true;
		for(Attraction at:b.getAttractions()){
			if(at!=null){
				bool2 = false;
				temp2+= at.toString();
				temp2+="\n";
			}
		}
		if(bool2){
			temp2 = "None";
		}
		userBooking.setAttractionsDetails(temp2);
		return userBooking;
	}

	@RequestMapping(value="/showBooking",method=RequestMethod.POST)
	public void showBooking(String inputBookingReference, HttpServletResponse response) throws IOException{
		inputBookingReference = inputBookingReference.trim();
		boolean isNumeric = inputBookingReference.chars().allMatch( Character::isDigit );
		if(!isNumeric){
			PrintWriter out = response.getWriter();
            out.println("<script>");
            out.println("alert('" + "invalid booking reference id entered, enter again" + "');");
            out.println("window.location.replace('" + "/" + "');");
            out.println("</script>");
		}
		else{
		userBooking = Client.getBookingFromTravelAgent(inputBookingReference);
		if (userBooking.getReferenceId()==null){
			PrintWriter out = response.getWriter();
            out.println("<script>");
            out.println("alert('" + "No booking found with provided booking id, enter again" + "');");
            out.println("window.location.replace('" + "/" + "');");
            out.println("</script>");
		}
		else{
			response.sendRedirect("/reservation");
		}
	}
	}

@GetMapping("/reservation")
    public String reservation(Model model){
		DisplayBooking db= new DisplayBooking(userBooking);
        model.addAttribute("userBooking", db);
        return "reservation.html";
    }

    @GetMapping("/displayFlights")
    public String displayflights(Model model){
        model.addAttribute("flightDetails", tp.getFlights());
        return "displayFlights.html";
    }
    
    @GetMapping("/displayHotels")
    public String displayhotels(Model model){
        model.addAttribute("hotelDetails", tp.getHotels());
        return "displayHotels.html";
	}
	
    @GetMapping("/displayActivities")
    public String displayactivities(Model model){
        model.addAttribute("activitiesDetails", tp.getActivities());
        return "displayActivities.html";
	}

	@GetMapping("/displayAttractions")
    public String displayAttraction(Model model){
        model.addAttribute("attractionsDetails", tp.getAttractions());
        return "displayAttractions.html";
    }
    

    /**
	 * The following method converts the array list of airport IDs to an array of airport IDs as we cannot pass a list
	 * using REST
	 * 
	 * @param countryName
	 * @return
	 */

	 public static String [] convertAirportIDsListToAirportIDsArray(ArrayList<String> airportIDsList){
		 
		String [] airportIDsArray = new String[airportIDsList.size()];

		int index = 0;
		for (String id : airportIDsList){
			
			airportIDsArray[index] = airportIDsList.get(index);
			index++;
		}
		return airportIDsArray;
	 }

	/**
	 * The following code is used to retrieve the country code for the country name given as this is needed for the Skyscanner API request
	 * 
	 * @param countryName
	 * @return countryCode
	 */

	public static String getListMarkets(String countryName) { 
		
		String countryCode = "";

		try {
			HttpRequest requestCode = HttpRequest.newBuilder()
				.uri(URI.create("https://skyscanner-skyscanner-flight-search-v1.p.rapidapi.com/apiservices/reference/v1.0/countries/en-GB"))
				.header("x-rapidapi-key", "91b7d3fc53mshf8b9bac5b6fd091p118e46jsn22debfe2cd83")
				.header("x-rapidapi-host", "skyscanner-skyscanner-flight-search-v1.p.rapidapi.com")
				.method("GET", HttpRequest.BodyPublishers.noBody())
				.build();
			HttpResponse<String> response = HttpClient.newHttpClient().send(requestCode, HttpResponse.BodyHandlers.ofString());

			/**
			 * TODO may need to delete this response if examples.json isn't needed going forward
			 */
			// HttpResponse<Path> response2 = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofFile(Paths.get("countryCodes.json")));

			String countryCodes = response.body();
			JSONObject countryCodesJson = parseJSONObject(countryCodes);

			JSONArray countryCodesArray = new JSONArray();
	    		countryCodesArray = (JSONArray) countryCodesJson.get("Countries");
			
			//loop through array to find the country code 
			int index = 0;
			while (index < countryCodesArray.size()) {

				JSONObject jsonObject = (JSONObject) countryCodesArray.get(index);
				String name = (String) jsonObject.get("Name");
				
				if (name.equals(countryName)){
					countryCode = (String) jsonObject.get("Code");
				}
				index++;
			}	

		} catch(IOException e) {
                  e.printStackTrace();
		}
		catch(InterruptedException e) {
                  e.printStackTrace();
		}  
		return countryCode;
	}

	/**
	 * The following method will retrieve the airport IDs 
	 * 
	 * @param cityOfDestination
	 * @param countryOfDestination
	 * @param countryOfOriginCode
	 * @param currency
	 * @return
	 */

	// GET ListPlaces (Skyscanner API)
	public static ArrayList<String> getListPlaces(String cityOfDestination, String countryOfDestination, String countryOfOriginCode, String currency) { 
		
		ArrayList<String> airportIDs = new ArrayList();

		try {
			HttpRequest request = HttpRequest.newBuilder()
					.uri(URI.create("https://skyscanner-skyscanner-flight-search-v1.p.rapidapi.com/apiservices/autosuggest/v1.0/"+
						countryOfOriginCode+"/"+currency+"/"+locale+"/?query="+cityOfDestination+"%20"+countryOfDestination))
					.header("x-rapidapi-key", "91b7d3fc53mshf8b9bac5b6fd091p118e46jsn22debfe2cd83")
					.header("x-rapidapi-host", "skyscanner-skyscanner-flight-search-v1.p.rapidapi.com")
					.method("GET", HttpRequest.BodyPublishers.noBody())
					.build();
			HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
			/**
			 * TODO may need to delete this response if airports.json isn't needed going forward
			 */
			// HttpResponse<Path> response2 = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofFile(Paths.get("airports.json")));
			
			System.out.println("Get ListPlaces: "+response.body());

			String places = response.body();
			JSONObject placesJson = parseJSONObject(places);

			JSONArray placesArray = new JSONArray();
		      placesArray = (JSONArray) placesJson.get("Places");
			System.out.println("Places array: "+placesArray);
			
			int index = 0;
			while (index < placesArray.size()) {
				JSONObject jsonObject = (JSONObject) placesArray.get(index);
				airportIDs.add((String) jsonObject.get("PlaceId"));
				index++;
			}
		} catch(IOException e) {
                  e.printStackTrace();
		}
		catch(InterruptedException e) {
                  e.printStackTrace();
		}  
		return airportIDs;
	}

	/**
	 * The following code converts a given string to a JSON object
	 * 
	 * @param response
	 * @return jsonObject
	 */

	public static JSONObject parseJSONObject(String response){

		JSONObject jsonObject = new JSONObject();
		try{
			JSONParser parser = new JSONParser();
			jsonObject = (JSONObject) parser.parse(response);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return jsonObject;
	}
}





